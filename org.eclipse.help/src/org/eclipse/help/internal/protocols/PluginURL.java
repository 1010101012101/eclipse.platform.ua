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
package org.eclipse.help.internal.protocols;
import java.io.*;

import org.eclipse.core.runtime.*;
import org.eclipse.help.internal.appserver.*;
import org.eclipse.help.internal.util.*;
/**
 * URL for documentation coming from a plugin. This is part of the help:/ protocol (help:/pluginid/path/to/file).
 */
public class PluginURL extends HelpURL {
	protected IPluginDescriptor plugin;
	protected String file;
	/**
	 * document caching - dissabled if running in dev mode
	 */
	private static boolean cachingEnabled = true;
	static {
		String[] args = Platform.getCommandLineArgs();
		for (int i = 0; i < args.length; i++) {
			if ("-dev".equals(args[i])) {
				cachingEnabled = false;
				break;
			}
		}
	}
	/**
	 * FileURL constructor comment.
	 * @param url pluginid/file
	 */
	public PluginURL(String url, String query) {
		super(url, query);
	}
	private String getFile() {
		if (file == null) {
			// Strip the plugin id
			int start = url.indexOf("/") + 1;
			// Strip query string or anchor bookmark
			int end = url.indexOf("?");
			if (end == -1)
				end = url.indexOf("#");
			if (end == -1)
				end = url.length();
			file = url.substring(start, end);
			file = URLCoder.decode(file);
		}
		return file;
	}
	private IPluginDescriptor getPlugin() {
		if (plugin == null) {
			// Assume the url is pluginID/path_to_topic.html
			int i = url.indexOf('/');
			String pluginId = i == -1 ? "" : url.substring(0, i);
			pluginId = URLCoder.decode(pluginId);
			plugin = Platform.getPluginRegistry().getPluginDescriptor(pluginId);
		}
		return plugin;
	}
	public boolean isCacheable() {
		if (getValue("resultof") != null)
			return false;
		else
			return cachingEnabled;
	}
	/**
	 * Opens a stream for reading.
	 * 
	 * @return java.io.InputStream
	 */
	public InputStream openStream() {
		IPluginDescriptor plugin = getPlugin();
		if (plugin == null) {
			return null;
		}
		if (plugin
			.equals(
				AppserverPlugin.getDefault().getContributingServerPlugin())) {
			return null;
		}
		if (getFile() == null || "".equals(getFile())) {
			return null;
		}
		// When the platform supports find() with a locale specified, use this
		//Locale locale = getLocale();
		InputStream inputStream = null;
		// first try finding the file inside nl tree in doc.zip,
		// and then, in the file system
		inputStream =
			ResourceLocator.openFromZip(
				plugin,
				"doc.zip",
				getFile(),
				getLocale());
		if (inputStream == null)
			inputStream =
				ResourceLocator.openFromPlugin(plugin, getFile(), getLocale());
		return inputStream;
	}
}
