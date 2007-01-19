/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.internal.webapp.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.help.ITopic;
import org.eclipse.help.internal.HelpPlugin;
import org.eclipse.help.internal.webapp.WebappResources;
import org.eclipse.help.internal.webapp.data.UrlUtil;

public class NavServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	private static final String XHTML_1 = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n<head>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"../content/PLUGINS_ROOT/PRODUCT_PLUGIN/book.css\"/>\n<title>"; //$NON-NLS-1$
	private static final String XHTML_2 = "</title>\n</head>\n<body>\n"; //$NON-NLS-1$
	private static final String XHTML_3 = "</body>\n</html>"; //$NON-NLS-1$

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Locale locale = req.getLocale();
		req.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
		resp.setContentType("text/html; charset=UTF-8"); //$NON-NLS-1$
		
		String path = req.getPathInfo().substring(1);
		ITopic topic = getTopic(path, locale);
		writeContent(topic, path, locale, resp.getWriter());
	}
	
	private ITopic getTopic(String topicPath, Locale locale) {
		StringTokenizer tok = new StringTokenizer(topicPath, "_"); //$NON-NLS-1$
		int index = Integer.parseInt(tok.nextToken());
		ITopic topic = HelpPlugin.getTocManager().getTocs(locale.toString())[index].getTopic(null);
		while (tok.hasMoreTokens()) {
			index = Integer.parseInt(tok.nextToken());
			topic = topic.getSubtopics()[index];
		}
		return topic;
	}
	
	private void writeContent(ITopic topic, String path, Locale locale, PrintWriter writer) {
		writer.write(XHTML_1);
		writer.write(topic.getLabel());
		writer.write(XHTML_2);
		writer.write("<h1 id=\"nav-title\">" + topic.getLabel() + "</h1>\n"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.write("<h3 id=\"nav-list-title\">" + WebappResources.getString("TocHeading", locale) + "</h3>\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		writer.write("<ul id=\"nav-list\">\n"); //$NON-NLS-1$
		ITopic[] subtopics = topic.getSubtopics();
		for (int i=0;i<subtopics.length;++i) {
			writer.write("<li><a href=\""); //$NON-NLS-1$
			String href = subtopics[i].getHref();
			if (href == null) {
				href = path + '_' + i;
			}
			else {
				href = XMLGenerator.xmlEscape(UrlUtil.getHelpURL(href));
			}
			writer.write(href);
			writer.write("\">" + subtopics[i].getLabel() + "</a></li>\n");  //$NON-NLS-1$//$NON-NLS-2$
		}
		writer.write("</ul>\n"); //$NON-NLS-1$
		writer.write(XHTML_3);
	}
}
