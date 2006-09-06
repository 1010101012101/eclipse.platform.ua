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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.help.AbstractIndexProvider;
import org.eclipse.help.IIndexContribution;
import org.eclipse.help.internal.base.HelpBasePlugin;

/*
 * Provides the TOC data that is located on the remote infocenter, if the system
 * is configured for remote help. If not, returns no contributions.
 */
public class RemoteIndexProvider extends AbstractIndexProvider {

	private static final String PATH_INDEX = "/index"; //$NON-NLS-1$
	private static RemoteIndexProvider instance;
	
	/*
	 * Returns the class's singleton instance.
	 */
	public static RemoteIndexProvider getInstance() {
		return instance;
	}
	
	/*
	 * Called by the platform, but we need a way to get the singleton
	 * instance.
	 */
	public RemoteIndexProvider() {
		instance = this;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.help.AbstractIndexProvider#getIndexContributions(java.lang.String)
	 */
	public IIndexContribution[] getIndexContributions(String locale) {
		if (RemoteHelp.isEnabled()) {
			InputStream in = null;
			try {
				URL url = RemoteHelp.getURL(PATH_INDEX);
				in = url.openStream();
				RemoteIndexParser parser = new RemoteIndexParser();
				return parser.parse(in);
			}
			catch (IOException e) {
				String msg = "I/O error while trying to contact the remote help server"; //$NON-NLS-1$
				HelpBasePlugin.logError(msg, e);
			}
			catch (Throwable t) {
				String msg = "Internal error while reading index contents from remote server"; //$NON-NLS-1$
				HelpBasePlugin.logError(msg, t);
			}
			finally {
				if (in != null) {
					try {
						in.close();
					}
					catch (IOException e) {
						// nothing more we can do
					}
				}
			}
		}
		return new IIndexContribution[0];
	}
	
	/*
	 * Called by the preference page whenever the user changed preferences.
	 */
	public void notifyPreferencesChanged() {
		// forward the notification on to the platform
		contentChanged();
	}
}
