/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.internal.base.remote;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.help.internal.base.BaseHelpSystem;
import org.eclipse.help.internal.base.HelpBasePlugin;
import org.eclipse.help.internal.base.IHelpBaseConstants;

/*
 * Convenience methods for querying the remote help settings.
 */
public class RemoteHelp {

	private static final String PROTOCOL_HTTP = "http"; //$NON-NLS-1$

	/*
	 * Returns the hostname of the remote help server to use, or empty string
	 * if not configured.
	 */
	public static String getHost() {
		return HelpBasePlugin.getDefault().getPluginPreferences().getString(IHelpBaseConstants.P_KEY_REMOTE_HELP_HOST);
	}

	/*
	 * Returns the path of the remote help server to use. Ensures that there
	 * is a leading slash and no trailing slash, e.g. "/myPath"
	 */
	public static String getPath() {
		String path = HelpBasePlugin.getDefault().getPluginPreferences().getString(IHelpBaseConstants.P_KEY_REMOTE_HELP_PATH);
		if (!path.startsWith("/")) { //$NON-NLS-1$
			path = '/' + path;
		}
		if (path.endsWith("/")) { //$NON-NLS-1$
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	/*
	 * Returns the port to use for connecting to the remote help server.
	 */
	public static int getPort() {
		Preferences prefs = HelpBasePlugin.getDefault().getPluginPreferences();
		if (prefs.getBoolean(IHelpBaseConstants.P_KEY_REMOTE_HELP_DEFAULT_PORT) == true) {
			prefs.getDefaultInt(IHelpBaseConstants.P_KEY_REMOTE_HELP_PORT);
		}
		return prefs.getInt(IHelpBaseConstants.P_KEY_REMOTE_HELP_PORT);
	}

	public static URL getURL(String pathSuffix) throws MalformedURLException {
		String host = RemoteHelp.getHost();
		String path = RemoteHelp.getPath() + pathSuffix;
		int port = RemoteHelp.getPort();
		return new URL(PROTOCOL_HTTP, host, port, path);
	}
	
	/*
	 * Returns whether or not the help system is allowed to be enabled in the
	 * current mode.
	 */
	public static boolean isAllowed() {
		return (BaseHelpSystem.getMode() != BaseHelpSystem.MODE_INFOCENTER);
	}
	
	/*
	 * Returns whether or not the help system is currently configured for remote
	 * help content.
	 */
	public static boolean isEnabled() {
		if (isAllowed()) {
			Preferences prefs = HelpBasePlugin.getDefault().getPluginPreferences();
			return prefs.getBoolean(IHelpBaseConstants.P_KEY_REMOTE_HELP_ON);
		}
		return false;
	}
}
