/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
package org.eclipse.help.internal;
import java.util.*;

import org.eclipse.core.boot.*;
import org.eclipse.core.runtime.*;
import org.eclipse.help.*;
import org.eclipse.help.browser.*;
import org.eclipse.help.internal.appserver.WebappManager;
import org.eclipse.help.internal.browser.*;
import org.eclipse.help.internal.context.*;
import org.eclipse.help.internal.search.*;
import org.eclipse.help.internal.toc.*;
import org.eclipse.help.internal.util.*;
import org.eclipse.help.internal.workingset.*;

/**
 * The actual implementation of the help system plugin.
 */
public final class HelpSystem {
	protected static final HelpSystem instance = new HelpSystem();

	private final static String WEBAPP_EXTENSION_ID = "org.eclipse.help.webapp";
	private static final String WEBAPP_DEFAULT_ATTRIBUTE = "default";

	private static final String HELP_SUPPORT_EXTENSION_ID =
		"org.eclipse.help.support";
	private static final String HELP_SUPPORT_CLASS_ATTRIBUTE = "class";

	public final static String LOG_LEVEL_KEY = "log_level";
	public final static String BANNER_KEY = "banner";
	public final static String BANNER_HEIGHT_KEY = "banner_height";
	public final static String LINKS_VIEW_KEY = "linksView";
	public final static String BASE_TOCS_KEY = "baseTOCS";
	public final static String BOOKMARKS = "bookmarks";
	public final static String WORKING_SETS = "workingSets";
	public final static String WORKING_SET = "workingSet";
	public final static int MODE_WORKBENCH = 0;
	public final static int MODE_INFOCENTER = 1;
	public final static int MODE_STANDALONE = 2;

	protected TocManager tocManager;
	protected ContextManager contextManager;
	protected SearchManager searchManager;
	protected HashMap workingSetManagers;
	private int mode = MODE_WORKBENCH;
	private boolean webappStarted = false;
	private IErrorUtil defaultErrorMessenger;
	private IBrowser browser;
	private IHelp helpSupport = null;
	private boolean webappRunning = false;

	/**
	 * HelpSystem constructor comment.
	 */
	private HelpSystem() {
		super();
	}
	/**
	 * Used to obtain Context Manager
	 * returns an instance of ContextManager
	 */
	public static ContextManager getContextManager() {
		if (getInstance().contextManager == null)
			getInstance().contextManager = new ContextManager();
		return getInstance().contextManager;
	}

	public static HelpSystem getInstance() {
		return instance;
	}
	/**
	 * Used to obtain Toc Naviagiont Manager
	 * @return instance of TocManager
	 */
	public static TocManager getTocManager() {
		if (getInstance().tocManager == null) {
			getInstance().tocManager = new TocManager();
		}
		return getInstance().tocManager;
	}
	/**
	 * Used to obtain Search Manager
	 * @return instance of SearchManager
	 */
	public static SearchManager getSearchManager() {
		if (getInstance().searchManager == null) {
			getInstance().searchManager = new SearchManager();
		}
		return getInstance().searchManager;
	}
	/**
	 * Used to obtain Working Set Manager
	 * @return instance of WorkingSetManager
	 */
	public static WorkingSetManager getWorkingSetManager() {
		return getWorkingSetManager(BootLoader.getNL());
	}
	
	public static WorkingSetManager getWorkingSetManager(String locale) {
		if (getInstance().workingSetManagers == null) {
			getInstance().workingSetManagers = new HashMap();
		}
		WorkingSetManager wsmgr =
			(WorkingSetManager) getInstance().workingSetManagers.get(locale);
		if (wsmgr == null) {
			wsmgr = new WorkingSetManager(locale);
			getInstance().workingSetManagers.put(locale, wsmgr);
		}
		return wsmgr;
	}

	public static synchronized IBrowser getHelpBrowser() {
		if (getInstance().browser == null)
			getInstance().browser =
				BrowserManager.getInstance().createBrowser();
		return getInstance().browser;
	}

	public static synchronized IHelp getHelpSupport() {
		if (getInstance().helpSupport == null)
			getInstance().helpSupport = getInstance().initHelpSupport();
		return getInstance().helpSupport;
	}
	/**
	 */
	public HelpSystem newInstance() {
		return null;
	}

	/**
	 * Shuts down the Help System.
	 * @exception CoreException if this method fails to shut down
	 *   this plug-in 
	 */
	public static void shutdown() throws CoreException {
		Logger.logInfo("Help System shutting down");
		if (getInstance().searchManager != null) {
			getInstance().searchManager.close();
		}
		// stop the web apps
		WebappManager.stop("help");
		if (getMode() != MODE_WORKBENCH)
			WebappManager.stop("helpControl");

		// close any browsers created
		BrowserManager.getInstance().closeAll();

		Logger.shutdown();
	}
	/**
	 * Called by Platform after loading the plugin
	 */
	public static void startup() {
		try {
			setDefaultErrorUtil(new IErrorUtil() {
				public void displayError(String msg) {
					System.out.println(msg);
				}

				public void displayError(String msg, Thread uiThread) {
					System.out.println(msg);
				}

			});
			Preferences prefs = HelpPlugin.getDefault().getPluginPreferences();
			Logger.setDebugLevel(prefs.getInt(LOG_LEVEL_KEY));
		} catch (Exception e) {
			HelpPlugin.getDefault().getLog().log(
				new Status(
					Status.ERROR,
					HelpPlugin
						.getDefault()
						.getDescriptor()
						.getUniqueIdentifier(),
					0,
					Resources.getString("E005"),
					e));
		}
		Logger.logInfo("Help System started.");
	}
	public static boolean ensureWebappRunning() {
		if (!getInstance().webappStarted) {
			getInstance().webappStarted = true;

			String webappPlugin = getWebappPlugin();

			if (getMode() != MODE_WORKBENCH) {
				// start the help control web app
				try {
					WebappManager.start(
						"helpControl",
						webappPlugin,
						Path.EMPTY);
				} catch (CoreException e) {
					Logger.logError("ensureWebappRunning()", e);
					return false;
				}
			}
			// start the help web app
			try {
				WebappManager.start("help", webappPlugin, Path.EMPTY);
			} catch (CoreException e) {
				Logger.logError("ensureWebappRunning()", e);
				return false;
			}
			getInstance().webappRunning = true;

		}
		return getInstance().webappRunning;
	}

	/**
	 * Returns the mode.
	 * @return int
	 */
	public static int getMode() {
		return getInstance().mode;
	}

	/**
	 * Sets the mode.
	 * @param mode The mode to set
	 */
	public static void setMode(int mode) {
		getInstance().mode = mode;
	}

	/**
	 * Sets the error messenger
	 */
	public static void setDefaultErrorUtil(IErrorUtil em) {
		getInstance().defaultErrorMessenger = em;
	}

	/**
	 * Returns the default error messenger. When no UI is present, all
	 * errors are sent to System.out.
	 * @return IErrorMessenger
	 */
	public static IErrorUtil getDefaultErrorUtil() {
		return getInstance().defaultErrorMessenger;
	}

	/**
	 * Returns the plugin id that defines the help webapp
	 */
	private static String getWebappPlugin() {

		// get the webapp extension from the system plugin registry
		IPluginRegistry pluginRegistry = Platform.getPluginRegistry();
		IExtensionPoint point =
			pluginRegistry.getExtensionPoint(WEBAPP_EXTENSION_ID);
		if (point != null) {
			IExtension[] extensions = point.getExtensions();
			if (extensions.length != 0) {
				// We need to pick up the non-default configuration
				IConfigurationElement[] elements =
					extensions[0].getConfigurationElements();

				for (int i = 0; i < elements.length; i++) {
					String defaultValue =
						elements[i].getAttribute(WEBAPP_DEFAULT_ATTRIBUTE);
					if (defaultValue == null || defaultValue.equals("false")) {
						return elements[i]
							.getDeclaringExtension()
							.getDeclaringPluginDescriptor()
							.getUniqueIdentifier();
					}
				}
				// if reached this point, then then pick the first (default) webapp
				if (elements.length > 0)
					return elements[0]
						.getDeclaringExtension()
						.getDeclaringPluginDescriptor()
						.getUniqueIdentifier();
			}
		}

		// if all fails
		return "org.eclipse.help.webapp";
	}

	/**
	 * Instantiate the help support
	 */
	private IHelp initHelpSupport() {
		if (helpSupport == null) {
			IPluginRegistry pluginRegistry = Platform.getPluginRegistry();
			IExtensionPoint point =
				pluginRegistry.getExtensionPoint(HELP_SUPPORT_EXTENSION_ID);
			if (point != null) {
				IExtension[] extensions = point.getExtensions();
				if (extensions.length != 0) {
					// There should only be one extension/config element so we just take the first
					IConfigurationElement[] elements =
						extensions[0].getConfigurationElements();
					if (elements.length != 0) { // Instantiate the help support
						try {
							helpSupport =
								(IHelp) elements[0].createExecutableExtension(
									HELP_SUPPORT_CLASS_ATTRIBUTE);
						} catch (CoreException e) {
							// may need to change this
							HelpPlugin.getDefault().getLog().log(e.getStatus());
						}
					} 
				}
			}
		}
		
		// if no extension point found or instantiated, use default impl
		if (helpSupport == null)
			helpSupport = new DefaultHelpSupport();
			
		return helpSupport;
	}
}
