/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.intro.impl.model;

import java.io.*;
import java.net.*;
import java.util.*;

import org.eclipse.jface.action.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.help.*;
import org.eclipse.ui.internal.intro.impl.*;
import org.eclipse.ui.internal.intro.impl.model.loader.*;
import org.eclipse.ui.internal.intro.impl.parts.*;
import org.eclipse.ui.internal.intro.impl.util.*;
import org.eclipse.ui.intro.*;
import org.eclipse.ui.intro.config.*;

/**
 * An intro url. An intro URL is a valid http url, with org.eclipse.ui.intro as
 * a host. This class holds all logic to execute Intro URL commands, ie: an
 * Intro URL knows how to execute itself.
 */
public class IntroURL implements IIntroURL {


    /**
     * Intro URL constants.
     */
    public static final String INTRO_PROTOCOL = "http"; //$NON-NLS-1$
    public static final String INTRO_HOST_ID = "org.eclipse.ui.intro"; //$NON-NLS-1$

    /**
     * Constants that represent Intro URL actions.
     */
    public static final String SET_STANDBY_MODE = "setStandbyMode"; //$NON-NLS-1$
    public static final String SHOW_STANDBY = "showStandby"; //$NON-NLS-1$
    public static final String CLOSE = "close"; //$NON-NLS-1$
    public static final String SHOW_HELP_TOPIC = "showHelpTopic"; //$NON-NLS-1$
    public static final String SHOW_HELP = "showHelp"; //$NON-NLS-1$
    public static final String OPEN_BROWSER = "openBrowser"; //$NON-NLS-1$
    public static final String RUN_ACTION = "runAction"; //$NON-NLS-1$
    public static final String SHOW_PAGE = "showPage"; //$NON-NLS-1$
    public static final String SHOW_MESSAGE = "showMessage"; //$NON-NLS-1$

    /**
     * Constants that represent valid action keys.
     */
    public static final String KEY_ID = "id"; //$NON-NLS-1$
    public static final String KEY_PLUGIN_ID = "pluginId"; //$NON-NLS-1$
    public static final String KEY_CLASS = "class"; //$NON-NLS-1$
    public static final String KEY_STANDBY = "standby"; //$NON-NLS-1$
    public static final String KEY_PART_ID = "partId"; //$NON-NLS-1$
    public static final String KEY_INPUT = "input"; //$NON-NLS-1$
    public static final String KEY_MESSAGE = "message"; //$NON-NLS-1$
    public static final String KEY_URL = "url"; //$NON-NLS-1$

    private String action = null;
    private Properties parameters = null;

    /**
     * Prevent creation. Must be created through an IntroURLParser. This
     * constructor assumed we have a valid intro url.
     * 
     * @param url
     */
    IntroURL(String action, Properties parameters) {
        this.action = action;
        this.parameters = parameters;
    }

    /**
     * Executes whatever valid Intro action is embedded in this Intro URL.
     *  
     */
    public boolean execute() {
        final boolean[] result = new boolean[1];
        Display display = Display.getCurrent();
        BusyIndicator.showWhile(display, new Runnable() {

            public void run() {
                // Avoid flicker when we can. If intro is not opened, we dont
                // want to force an open on it. Also, be careful if the execute
                // command closes the Intro, then the control is disposed and
                // there is no need to redraw.
                CustomizableIntroPart currentIntroPart = (CustomizableIntroPart) IntroPlugin
                        .getIntroPart();
                if (currentIntroPart == null)
                    result[0] = doExecute();
                else {
                    currentIntroPart.getControl().setRedraw(false);
                    result[0] = doExecute();
                    currentIntroPart = (CustomizableIntroPart) IntroPlugin
                            .getIntroPart();
                    if (currentIntroPart != null)
                        // no one closed it.
                        currentIntroPart.getControl().setRedraw(true);
                }
            }
        });
        return result[0];
    }

    private boolean doExecute() {

        // check for all supported Intro actions first.
        if (action.equals(CLOSE))
            return closeIntro();

        else if (action.equals(SET_STANDBY_MODE))
            // Sets the state of the intro part. Does not care about passing
            // input to the part.
            return setStandbyState(getParameter(KEY_STANDBY));

        else if (action.equals(SHOW_STANDBY))
            return handleStandbyStateChanged(getParameter(KEY_PART_ID),
                    getParameter(KEY_INPUT));

        else if (action.equals(SHOW_HELP))
            // display the full Help System.
            return showHelp();

        else if (action.equals(SHOW_HELP_TOPIC))
            // display a Help System Topic.
            return showHelpTopic(getParameter(KEY_ID));

        else if (action.equals(OPEN_BROWSER))
            // display url in external browser
            return openBrowser(getParameter(KEY_URL),
                    getParameter(KEY_PLUGIN_ID));

        else if (action.equals(RUN_ACTION))
            // run an Intro action. Get the pluginId and the class keys.
            return runAction(getParameter(KEY_PLUGIN_ID),
                    getParameter(KEY_CLASS));

        else if (action.equals(SHOW_PAGE)) {
            // display an Intro Page.
            return showPage(getParameter(KEY_ID));
        } else if (action.equals(SHOW_MESSAGE))
            return showMessage(getParameter(KEY_MESSAGE));

        else
            return handleCustomAction();
    }


    private boolean closeIntro() {
        // Relies on Workbench.
        return PlatformUI.getWorkbench().getIntroManager().closeIntro(
                PlatformUI.getWorkbench().getIntroManager().getIntro());
    }

    /**
     * Sets the into part to standby, and shows the passed standby part, with
     * the given input.
     * 
     * @param partId
     * @param input
     */
    private boolean handleStandbyStateChanged(String partId, String input) {
        // set intro to standby mode. we know we have a customizable part.
        CustomizableIntroPart introPart = getCustomizableIntroPart(true);
        PlatformUI.getWorkbench().getIntroManager().setIntroStandby(introPart,
                true);
        StandbyPart standbyPart = (StandbyPart) introPart
                .getAdapter(StandbyPart.class);

        boolean success = standbyPart.showContentPart(partId, input);
        if (success)
            return true;

        // we do not have a valid partId or we failed to instantiate part or
        // create the part content, show empty part and signal failure.
        standbyPart.setTopControl(IIntroConstants.EMPTY_STANDBY_CONTENT_PART);
        return false;
    }

    /**
     * Set the Workbench Intro Part state.
     * 
     * @param state
     */
    private boolean setStandbyState(String state) {
        boolean standby = state.equals("true") ? true : false; //$NON-NLS-1$
        CustomizableIntroPart introPart = getCustomizableIntroPart(standby);
        if (introPart == null)
            return false;
        // should rely on Workbench api. If the Intro part was not open when
        // this method was called, the following line simply resets the part
        // into standby.
        PlatformUI.getWorkbench().getIntroManager().setIntroStandby(introPart,
                standby);
        return true;
    }

    /**
     * Utility method to return the Intro part, if it is open. If it is not, then opens the 
     * Intro part with the given state. This is needed to avoid flicker if states need to be changed.
     * @param standby
     * @return
     * @todo Generated comment
     */
    private CustomizableIntroPart getCustomizableIntroPart(boolean standby) {
        // do not rely on model presentation to get part because Intro may be
        // closed.
        CustomizableIntroPart intro = (CustomizableIntroPart) IntroPlugin
                .getIntroPart();
        if (intro == null)
            intro = (CustomizableIntroPart) IntroPlugin.showIntroPart(standby);
        return intro;
    }

    /**
     * Run an action
     */
    private boolean runAction(String pluginId, String className) {

        Object actionObject = ModelLoaderUtil.createClassInstance(pluginId,
                className);
        try {
            if (actionObject instanceof IIntroAction) {
                IIntroAction introAction = (IIntroAction) actionObject;
                IIntroSite site = IntroPlugin.getDefault().getIntroModelRoot()
                        .getPresentation().getIntroPart().getIntroSite();
                introAction.run(site, parameters);
            } else if (actionObject instanceof IAction) {
                IAction action = (IAction) actionObject;
                action.run();
            } else if (actionObject instanceof IActionDelegate) {
                final IActionDelegate delegate = (IActionDelegate) actionObject;
                if (delegate instanceof IWorkbenchWindowActionDelegate)
                    ((IWorkbenchWindowActionDelegate) delegate).init(PlatformUI
                            .getWorkbench().getActiveWorkbenchWindow());
                Action proxy = new Action(this.action) {

                    public void run() {
                        delegate.run(this);
                    }
                };
                proxy.run();
                return true;
            }
            // we could not create the class.
            return false;
        } catch (Exception e) {
            Log.error("Could not run action: " + className, e); //$NON-NLS-1$
            return false;
        }
    }

    /**
     * Open a help topic.
     */
    private boolean showHelpTopic(String href) {
        // WorkbenchHelp takes care of error handling.
        WorkbenchHelp.displayHelpResource(href);
        return true;
    }

    /**
     * Open the help system.
     */
    private boolean showHelp() {
        WorkbenchHelp.displayHelp();
        return true;
    }

    /**
     * Launch external browser
     */
    private boolean openBrowser(String url, String pluginId) {
        // no need to decode url because we will create another url from this
        // url anyway. Resolve the url just in case we are trying to load a
        // plugin relative file.
        url = IntroModelRoot.resolveURL(url, pluginId);
        return Util.openBrowser(url);
    }

    private boolean showMessage(String message) {

        // REVISIT: some of the actions run UI code yet they are in
        // model package.
        if (message == null)
            return false;
        else {
            try {
                message = URLDecoder.decode(message, "UTF-8"); //$NON-NLS-1$
                DialogUtil.displayInfoMessage(null, message);
                return true;
            } catch (UnsupportedEncodingException e) {
                DialogUtil.displayInfoMessage(null, "IntroURL.failedToDecode", //$NON-NLS-1$
                        new Object[] { message });
                return false;
            }
        }
    }

    /**
     * Display an Intro Page.
     */
    private boolean showPage(String pageId) {
        // set the current page id in the model. This will triger appropriate
        // listener event to the UI. If setting the page in the model fails (ie:
        // the page was not found in the model), return false.
        IntroModelRoot modelRoot = IntroPlugin.getDefault().getIntroModelRoot();
        return modelRoot.setCurrentPageId(pageId);
    }

    /**
     * @return Returns the action imbedded in this URL.
     */
    public String getAction() {
        return action;
    }

    /**
     * Return a parameter defined in the Intro URL. Returns null if the
     * parameter is not defined.
     * 
     * @param parameterId
     * @return
     */
    public String getParameter(String parameterId) {
        return parameters.getProperty(parameterId);
    }

    private boolean handleCustomAction() {
        IntroURLAction command = ExtensionPointManager.getInst()
                .getSharedConfigExtensionsManager().getCommand(action);
        if (command == null) {
            DialogUtil.displayInfoMessage(null, "IntroURL.badCommand", //$NON-NLS-1$
                    new Object[] { action });
            return false;
        }

        // custom command. execute it.
        StringBuffer url = new StringBuffer();
        url.append("http://org.eclipse.ui.intro/"); //$NON-NLS-1$
        url.append(command.getReplaceValue().trim());
        if (command.getReplaceValue().indexOf("?") == -1) //$NON-NLS-1$
            // command does not have parameters.
            url.append("?"); //$NON-NLS-1$
        else
            // command already has parameters.
            url.append("&"); //$NON-NLS-1$
        url.append(retrieveInitialQuery());
        IIntroURL introURL = IntroURLFactory.createIntroURL(url.toString());
        if (introURL != null)
            return introURL.execute();
        else
            return false;
    }


    /**
     * Recreate the initial query passed to this URL.
     * 
     * @return
     */
    private String retrieveInitialQuery() {
        StringBuffer query = new StringBuffer();
        Enumeration keys = parameters.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            query.append(key);
            query.append("="); //$NON-NLS-1$
            query.append(parameters.get(key));
            if (keys.hasMoreElements())
                query.append("&"); //$NON-NLS-1$
        }
        return query.toString();
    }

}

