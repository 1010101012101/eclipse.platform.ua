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

package org.eclipse.ui.intro.config;

/**
 * An Intro url. An intro URL is a valid http url, with org.eclipse.ui.intro as
 * a host. It is inteded to only be used in conjunction with the pre-supplied
 * CustomizableIntroPart. See the <code>org.eclipse.ui.intro.config</code>
 * extension point for more details.
 * <p>
 * An intro url instance is created by parsing the url and retrieving the
 * embedded "command" and parametrs. For example, the following urls are valid
 * intro urls:
 * http://org.eclipse.ui.intro/close
 * http://org.eclipse.ui.intro/runAction?pluginId=x.y.z&class=x.y.z.someClass
 * </p>
 * <p>
 * When parsed, the first url has "close" as a command, and no parameters. While
 * the second "runAction" as a command and "pluginId" and "class" as parameters.
 * </p>
 * <p>
 * There is a number of supported Intro commands. Check docs for more details.
 * Calling execute runs the command if it happens to be one of the supported
 * commands.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IntroURLFactory
 * @see IIntroAction
 * @since 3.0
 */
public interface IIntroURL {

    /**
     * Executes whatever valid Intro command is embedded in this Intro URL.
     *  
     */
    public void execute();

    /**
     * @return Returns the command imbedded in this URL.
     */
    public String getAction();

    /**
     * Return a parameter defined in the Intro URL. Returns null if the
     * parameter is not defined.
     * 
     * @param parameterId
     * @return
     */
    public String getParameter(String parameterId);
}