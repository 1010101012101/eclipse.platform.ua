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
import org.eclipse.help.internal.webapp.servlet.*;

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
			String title =
				UrlUtil.isIE(request)
					? UrlUtil.unescape(
						UrlUtil.getRawRequestParameter(request, "title"))
					: request.getParameter("title");
			Preferences prefs = HelpPlugin.getDefault().getPluginPreferences();
			String bookmarks = prefs.getString(HelpSystem.BOOKMARKS);

			// separate the url and title by vertical bar

			// check for duplicates
			if (bookmarks.indexOf("," + bookmarkURL + "|") != -1)
				return;
			bookmarks = bookmarks + "," + bookmarkURL + "|" + title;
			prefs.setValue(HelpSystem.BOOKMARKS, bookmarks);
			HelpPlugin.getDefault().savePluginPreferences();
		}
	}

	public void removeBookmark() {
		String bookmarkURL = request.getParameter("bookmark");
		if (bookmarkURL != null
			&& bookmarkURL.length() > 0
			&& !bookmarkURL.equals("about:blank")) {
			String title =
				UrlUtil.isIE(request)
					? UrlUtil.unescape(
						UrlUtil.getRawRequestParameter(request, "title"))
					: request.getParameter("title");
			Preferences prefs = HelpPlugin.getDefault().getPluginPreferences();
			String bookmarks = prefs.getString(HelpSystem.BOOKMARKS);
			String removeString = "," + bookmarkURL + "|" + title;
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

				String label = bookmark.substring(separator + 1);
				String href =
					separator < 0 ? "" : bookmark.substring(0, separator);
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
}
