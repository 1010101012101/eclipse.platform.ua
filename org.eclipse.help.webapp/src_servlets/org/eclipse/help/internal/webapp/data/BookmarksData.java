package org.eclipse.help.internal.webapp.data;

/*
 * (c) Copyright IBM Corp. 2002.
 * All Rights Reserved.
 */

import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.help.internal.*;
import org.eclipse.help.internal.util.*;

/**
 * This class manages bookmarks.
 */
public class BookmarksData extends RequestData {
	public final static int NONE = 0;
	public final static int ADD = 1;
	public final static int REMOVE = 2;

	public BookmarksData(ServletContext context, HttpServletRequest request) {
		super(context, request);

		switch (getOperation()) {
			case ADD :
				addBookmark();
				break;
			case REMOVE :
				removeBookmark();
				break;
			default :
				break;
		}
	}

	public void addBookmark() {
		String bookmarkURL = request.getParameter("bookmark");
		if (bookmarkURL != null
			&& bookmarkURL.length() > 0
			&& !bookmarkURL.equals("about:blank")) {
			String title = getDBCSParameter("title");
			Preferences prefs = HelpPlugin.getDefault().getPluginPreferences();
			String bookmarks = prefs.getString(HelpSystem.BOOKMARKS);

			// separate the url and title by vertical bar

			// check for duplicates
			if (bookmarks.indexOf("," + encode(bookmarkURL) + "|") != -1)
				return;
			bookmarks =
				bookmarks + "," + encode(bookmarkURL) + "|" + encode(title);
			prefs.setValue(HelpSystem.BOOKMARKS, bookmarks);
			HelpPlugin.getDefault().savePluginPreferences();
		}
	}

	public void removeBookmark() {
		String bookmarkURL = request.getParameter("bookmark");
		if (bookmarkURL != null
			&& bookmarkURL.length() > 0
			&& !bookmarkURL.equals("about:blank")) {
			String title = getDBCSParameter("title");
			Preferences prefs = HelpPlugin.getDefault().getPluginPreferences();
			String bookmarks = prefs.getString(HelpSystem.BOOKMARKS);
			String removeString =
				"," + encode(bookmarkURL) + "|" + encode(title);
			int i = bookmarks.indexOf(removeString);
			if (i == -1)
				return;
			bookmarks =
				bookmarks.substring(0, i)
					+ bookmarks.substring(i + removeString.length());
			prefs.setValue(HelpSystem.BOOKMARKS, bookmarks);
			HelpPlugin.getDefault().savePluginPreferences();
		}
	}

	public Topic[] getBookmarks() {
		// sanity test for infocenter, but this could not work anyway...
		if (HelpSystem.getMode() != HelpSystem.MODE_INFOCENTER) {
			// this is workbench
			Preferences prefs = HelpPlugin.getDefault().getPluginPreferences();
			String bookmarks = prefs.getString(HelpSystem.BOOKMARKS);
			StringTokenizer tokenizer = new StringTokenizer(bookmarks, ",");
			Topic[] topics = new Topic[tokenizer.countTokens()];
			for (int i = 0; tokenizer.hasMoreTokens(); i++) {
				String bookmark = tokenizer.nextToken();
				// url and title are separated by vertical bar
				int separator = bookmark.indexOf('|');

				String label = decode(bookmark.substring(separator + 1));
				String href =
					separator < 0
						? ""
						: decode(bookmark.substring(0, separator));
				topics[i] = new Topic(label, href);
			}
			return topics;
		}
		return new Topic[0];
	}

	private int getOperation() {
		String op = request.getParameter("operation");
		if ("add".equals(op))
			return ADD;
		else if ("remove".equals(op))
			return REMOVE;
		else
			return NONE;
	}
	/**
	 * Ensures that string does not contains
	 * ',' or '|' characters.
	 * @param s
	 * @return String
	 */
	private static String encode(String s) {
		s = TString.change(s, "\\", "\\escape");
		s = TString.change(s, ",", "\\coma");
		return TString.change(s, "|", "\\pipe");
	}
	private static String decode(String s) {
		s = TString.change(s, "\\pipe", "|");
		s = TString.change(s, "\\coma", ",");
		return TString.change(s, "\\escape", "\\");
	}
}
