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
package org.eclipse.help.internal.toc;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

import org.eclipse.core.runtime.*;
import org.eclipse.help.*;
import org.eclipse.help.internal.*;
/** 
 * Toc created from files in a extra directory in a plugin.
 */
public class DirectoryToc {
	private String dir;
	/**
	 * Map of ITopic by href String;
	 */
	private Map extraTopics;
	private String locale;
	/**
	 * Constructor.
	 */
	protected DirectoryToc(TocFile tocFile) {
		this(tocFile.getPluginID(), tocFile.getLocale(), tocFile.getExtraDir());
	}
	private DirectoryToc(String pluginID, String locale, String directory) {
		this.locale = locale;
		// Obtain extra search directory if provided
		this.dir = HrefUtil.normalizeDirectoryHref(pluginID, directory);

	}
	/**
	 * This public method is to be used after the build of TOCs
	 * is finished.
	 * With assumption that TOC model is not modifiable
	 * after the build, this method caches topics in an array
	 * and releases objects used only during build.
	 * @return Map of ITopic
	 */
	public Map getExtraTopics() {
		if (extraTopics == null) {
			extraTopics = createExtraTopics();
			// for memory foot print, release TocFile and dir
			dir = null;
		}

		return extraTopics;
	}
	/**
	 * Obtains URLs of all documents inside given directory.
	 * @param extraDir directory for the topics in the form
	 *  <em>/pluginID/directory/containing/docs<em>
	 *  or
	 *  <em>/pluginID<em> for all documents in a given plugin
	 * @return Map of ITopic by href
	 */
	private Map createExtraTopics() {
		Map ret = new HashMap();
		String pluginID = HrefUtil.getPluginIDFromHref(dir);
		if (pluginID == null) {
			return ret;
		}
		IPluginDescriptor pluginDesc =
			Platform.getPluginRegistry().getPluginDescriptor(pluginID);
		if (pluginDesc == null)
			return ret;
		String directory = HrefUtil.getResourcePathFromHref(dir);
		if (directory == null) {
			// the root - all files in a zip should be indexed
			directory = "";
		}
		// Find doc.zip file
		IPath iPath = new Path("$nl$/doc.zip");
		Map override = new HashMap(1);
		override.put("$nl$", locale);
		URL url = null;
		try {
			url = pluginDesc.getPlugin().find(iPath, override);
			if (url == null) {
				url = pluginDesc.getPlugin().find(new Path("doc.zip"));
			}
		} catch (CoreException ce) {
			HelpPlugin.logError(
				HelpResources.getString("E034", "/" + pluginID + "/doc.zip"),
				ce);
		}
		if (url != null) {
			// collect topics from doc.zip file
			ret.putAll(createExtraTopicsFromZip(pluginID, directory, url));
		}
		// Find directory on the filesystem
		iPath = new Path("$nl$/" + directory);
		url = null;
		try {
			url = pluginDesc.getPlugin().find(iPath, override);
			if (url == null) {
				if (directory.length() == 0) {
					// work around NPE in plugin.find()
					url = pluginDesc.getInstallURL();
				} else {
					url = pluginDesc.getPlugin().find(new Path(directory));
				}
			}
		} catch (CoreException ce) {
			HelpPlugin.logError(
				HelpResources.getString("E035", "/" + pluginID + "/" + directory),
				ce);
		}
		if (url != null) {
			// collect topics from doc.zip file
			ret.putAll(
				createExtraTopicsFromDirectory(pluginID, directory, url));
		}
		return ret;

	}
	/**
	 * @param directory path in the form "segment1/segment2...",
	 *  "" will return names of all files in a zip
	 * @return Map of ITopic by href String
	 */
	private Map createExtraTopicsFromZip(
		String pluginID,
		String directory,
		URL url) {
		Map ret = new HashMap(0);
		URL realZipURL;
		try {
			realZipURL = Platform.asLocalURL(Platform.resolve(url));
		} catch (IOException ioe) {
			HelpPlugin.logError(
				HelpResources.getString("E036", url.toString()),
				ioe);
			return new HashMap(0);
		}
		ZipFile zipFile;
		try {
			zipFile = new ZipFile(realZipURL.getFile());
			ret = createExtraTopicsFromZipFile(pluginID, zipFile, directory);
			zipFile.close();
		} catch (IOException ioe) {
			HelpPlugin.logError(
				HelpResources.getString("E037", realZipURL.getFile()),
				ioe);
			return new HashMap(0);
		}

		return ret;

	}

	/**
	* Obtains names of files in a zip file that given directory in their path.
	* Files in subdirectories are included as well.
	* @param directory path in the form "segment1/segment2...",
	*  "" will return names of all files in a zip
	* @return Map of ITopic by href String
	*/
	private Map createExtraTopicsFromZipFile(
		String pluginID,
		ZipFile zipFile,
		String directory) {
		String constantHrefSegment = "/" + pluginID + "/";
		Map ret = new HashMap();
		for (Enumeration enum = zipFile.entries(); enum.hasMoreElements();) {
			ZipEntry zEntry = (ZipEntry) enum.nextElement();
			if (zEntry.isDirectory()) {
				continue;
			}
			String docName = zEntry.getName();
			int l = directory.length();
			if (l == 0
				|| docName.length() > l
				&& docName.charAt(l) == '/'
				&& directory.equals(docName.substring(0, l))) {
				String href=constantHrefSegment + docName;
				ret.put(href, new ExtraTopic(href));
			}
		}
		return ret;
	}
	/**
	 * @param directory path in the form "segment1/segment2...",
	 *  "" will return names of all files in a directory
	 * @return Map of ITopic by href String
	 */
	private Map createExtraTopicsFromDirectory(
		String pluginID,
		String directory,
		URL url) {
		Map m = new HashMap();
		URL realURL;
		try {
			realURL = Platform.asLocalURL(Platform.resolve(url));
		} catch (IOException ioe) {
			HelpPlugin.logError(
				HelpResources.getString("E038", url.toString()),
				ioe);
			return m;
		}
		File dirFile = new File(realURL.getFile());
		if (dirFile.exists() && dirFile.isDirectory()) {
			String prefix;
			if (directory.length() > 0) {
				prefix = "/" + pluginID + "/" + directory;
			} else {
				prefix = "/" + pluginID;
			}
			createExtraTopicsFromDirectoryFile(prefix, dirFile, m);
		}
		return m;

	}
	/**
	 * @prefix /pluginID/segment1/segment2
	 * @return Map of ITopic by href String
	 */
	private Map createExtraTopicsFromDirectoryFile(
		String prefix,
		File dir,
		Map m) {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			String href = prefix + "/" + files[i].getName();
			if (files[i].isDirectory()) {
				createExtraTopicsFromDirectoryFile(href, files[i], m);
			} else {
				m.put(href, new ExtraTopic(href));
			}
		}
		return m;
	}
	class ExtraTopic implements ITopic {
		private String topicHref;
		public ExtraTopic(String href) {
			this.topicHref = href;
		}

		public String getHref() {
			return topicHref;
		}
		public String getLabel() {
			return topicHref;
		}
		public ITopic[] getSubtopics() {
			return new ITopic[0];
		}
	}
}
