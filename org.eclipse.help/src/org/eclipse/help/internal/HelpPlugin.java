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
package org.eclipse.help.internal;
import org.eclipse.core.runtime.*;
import org.eclipse.help.internal.context.*;
import org.eclipse.help.internal.toc.*;
import org.osgi.framework.*;
/**
 * Help System Core plug-in
 */
public class HelpPlugin extends Plugin {
	public final static String PLUGIN_ID = "org.eclipse.help";
	// debug options
	public static boolean DEBUG = false;
	public static boolean DEBUG_CONTEXT = false;
	public static boolean DEBUG_PROTOCOLS = false;
	protected static HelpPlugin plugin;
	private static BundleContext bundleContext;

	public final static String BASE_TOCS_KEY = "baseTOCS";

	protected TocManager tocManager;
	protected static Object tocManagerCreateLock = new Object();
	protected ContextManager contextManager;

	/**
	 * Logs an Error message with an exception. Note that the message should
	 * already be localized to proper locale. ie: Resources.getString() should
	 * already have been called
	 */
	public static synchronized void logError(String message, Throwable ex) {
		if (message == null)
			message = "";
		Status errorStatus =
			new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, message, ex);
		HelpPlugin.getDefault().getLog().log(errorStatus);
	}
	/**
	 * Logs a Warning message with an exception. Note that the message should
	 * already be localized to proper local. ie: Resources.getString() should
	 * already have been called
	 */
	public static synchronized void logWarning(String message) {
		if (HelpPlugin.DEBUG) {
			if (message == null)
				message = "";
			Status warningStatus =
				new Status(
					IStatus.WARNING,
					PLUGIN_ID,
					IStatus.OK,
					message,
					null);
			HelpPlugin.getDefault().getLog().log(warningStatus);
		}
	}

	/**
	 * @return the singleton instance of the plugin
	 */
	public static HelpPlugin getDefault() {
		return plugin;
	}
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		bundleContext = null;
		super.stop(context);
	}
	
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		bundleContext = context;
		// Setup debugging options
		DEBUG = isDebugging();
		if (DEBUG) {
			DEBUG_CONTEXT = "true".equalsIgnoreCase(Platform.getDebugOption(PLUGIN_ID + "/debug/context")); //$NON-NLS-1$
			DEBUG_PROTOCOLS = "true".equalsIgnoreCase(Platform.getDebugOption(PLUGIN_ID + "/debug/protocols")); //$NON-NLS-1$
		}
	}
	/**
	 * Used to obtain Toc Naviagiont Manager
	 * 
	 * @return instance of TocManager
	 */
	public static TocManager getTocManager() {
		if (getDefault().tocManager == null) {
			synchronized (tocManagerCreateLock) {
				if (getDefault().tocManager == null) {
					getDefault().tocManager = new TocManager();
				}
			}
		}
		return getDefault().tocManager;
	}
	/**
	 * Used to obtain Context Manager returns an instance of ContextManager
	 */
	public static ContextManager getContextManager() {
		if (getDefault().contextManager == null)
			getDefault().contextManager = new ContextManager();
		return getDefault().contextManager;
	}

}
