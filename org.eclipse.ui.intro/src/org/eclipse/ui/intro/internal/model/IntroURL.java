/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.intro.internal.model;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.eclipse.ui.*;
import org.eclipse.ui.help.*;
import org.eclipse.ui.intro.*;
import org.eclipse.ui.intro.internal.*;
import org.eclipse.ui.intro.internal.presentations.*;
import org.eclipse.ui.intro.internal.util.*;

/**
 * An intro url. An intro URL is a valid http url, with org.eclipse.ui.intro as
 * a host. This class holds all logic to execute Intro URL commands, ie: an
 * Intro URL knows how to execute itself.
 */
public class IntroURL {

	/**
	 * Intro URL constants.
	 */
	public static final String INTRO_PROTOCOL = "http";
	public static final String INTRO_HOST_ID = "org.eclipse.ui.intro";

	/**
	 * Constants that represent Intro URL actions.
	 */
	public static final String SET_STANDBY = "setStandbyMode";
	public static final String CLOSE = "close";
	public static final String SHOW_HELP_TOPIC = "showHelpTopic";
	public static final String SHOW_HELP = "showHelp";
	public static final String OPEN_BROWSER = "openBrowser";
	public static final String OPEN_CHEAT_SHEET = "openCheatSheet";
	public static final String RUN_ACTION = "runAction";
	public static final String SHOW_PAGE = "showPage";

	/**
	 * Constants that represent valid action keys.
	 */
	public static final String KEY_ID = "id";
	public static final String KEY_PLUGIN_ID = "pluginId";
	public static final String KEY_CLASS = "class";
	public static final String KEY_STANDBY = "standby";

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
	public void execute() {
		// check for all Intro actions.
		if (action.equals(CLOSE))
			closeIntro();

		else if (action.equals(SET_STANDBY))
			setStandbyState();

		else if (action.equals(SHOW_HELP))
			// display the Help System.
			showHelp();

		else if (action.equals(SHOW_HELP_TOPIC))
			// display a Help System Topic.
			showHelpTopic(getParameter(KEY_ID));

		else if (action.equals(RUN_ACTION))
			// run an Intro action. Get the pluginId and the class keys.
			runAction(getParameter(KEY_PLUGIN_ID), getParameter(KEY_CLASS));

		else if (action.equals(SHOW_PAGE))
			// display an Intro Page.
			showPage(getParameter(KEY_ID));
		else if (action.equals(OPEN_CHEAT_SHEET))
			openCheatSheet(getParameter(KEY_ID));
	}

	private void closeIntro() {
		// Relies on Workbench.
		PlatformUI.getWorkbench().closeIntro(
			PlatformUI.getWorkbench().findIntro());
	}

	private void setStandbyState() {
		boolean state = getParameter(KEY_STANDBY).equals("true") ? true : false;

		// Relies on Workbench.
		PlatformUI.getWorkbench().setIntroStandby(
			PlatformUI.getWorkbench().findIntro(),
			state);
	}

	/**
	 * Run an action
	 */
	private void runAction(String pluginId, String className) {
		if (pluginId.equals("") | className.equals(""))
			// quick exits.
			return;

		IPluginDescriptor desc =
			Platform.getPluginRegistry().getPluginDescriptor(pluginId);
		if (desc == null)
			// quick exit.
			return;

		Class actionClass;
		Object actionObject;
		try {
			actionClass = desc.getPluginClassLoader().loadClass(className);
			actionObject = actionClass.newInstance();
			if (actionObject instanceof IIntroAction) {
				IIntroAction introAction = (IIntroAction) actionObject;
				IIntroSite site =
					IntroPlugin
						.getDefault()
						.getIntroModelRoot()
						.getPresentation()
						.getIntroPart()
						.getIntroSite();
				introAction.initialize(site, parameters);
				introAction.run();
			} else if (actionObject instanceof IAction) {
				IAction action = (IAction) actionObject;
				action.run();
			} else if (actionObject instanceof IActionDelegate) {
				final IActionDelegate delegate = (IActionDelegate)actionObject;
				if (delegate instanceof IWorkbenchWindowActionDelegate)
					((IWorkbenchWindowActionDelegate)delegate).init(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
				Action proxy = new Action(this.action) {
					public void run() {
						delegate.run(this);
					}
				};
				proxy.run();
			}
		} catch (Exception e) {
			Logger.logError("Could not run action: " + className, e);
			return;
		}
	}

	/**
	 * Open a help topic.
	 */
	private void showHelpTopic(String href) {
		// WorkbenchHelp takes care of error handling.
		WorkbenchHelp.displayHelpResource(href);
	}

	/**
	 * Open the help system.
	 */
	private void showHelp() {
		WorkbenchHelp.displayHelp();
	}

	/**
	 * Display an Intro Page.
	 */
	private void showPage(String pageId) {
		// set the current page id in the model. This will triger a listener
		// event to the UI.
		IntroModelRoot modelRoot = IntroPlugin.getDefault().getIntroModelRoot();
		modelRoot.setCurrentPageId(pageId);
	}

	private void openCheatSheet(String sheetId) {
		IIntroPart introPart = PlatformUI.getWorkbench().findIntro();
		if (introPart == null)
			introPart =
				PlatformUI.getWorkbench().showIntro(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		PlatformUI.getWorkbench().setIntroStandby(
			PlatformUI.getWorkbench().findIntro(),
			true);
		((CustomizableIntroPart) introPart).setStandbyInput(sheetId);
	}

	/**
	 * @return Returns the action imbedded in this URL.
	 */
	public String getAction() {
		return action;
	}

	public String getParameter(String parameterId) {
		return parameters.getProperty(parameterId, "");
	}

}
