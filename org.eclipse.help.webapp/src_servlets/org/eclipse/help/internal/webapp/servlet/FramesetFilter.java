/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */

package org.eclipse.help.internal.webapp.servlet;

import java.io.*;

import javax.servlet.http.*;

/**
 * This class inserts a script for showing the page inside the appropriate frameset
 * when bookmarked.
 */
public class FramesetFilter implements IFilter {
	private static final String scriptPart1 =
		"<script>if( self == top ){ window.location.replace( \"";
	private static final String scriptPart3 = "\");}</script>";

	/*
	 * @see IFilter#filter(HttpServletRequest, OutputStream)
	 */
	public OutputStream filter(HttpServletRequest req, OutputStream out) {
		String uri = req.getRequestURI();
		if (uri == null || !uri.endsWith("html") && !uri.endsWith("htm")) {
			return out;
		}
		
		String noframes = req.getParameter("noframes");
		if ("true".equals(noframes)){
			return out;
		}

		String path = req.getPathInfo();
		if (path == null) {
			return out;
		}
		StringBuffer script = new StringBuffer(scriptPart1);
		for (int i;
			0 <= (i = path.indexOf('/'));
			path = path.substring(i + 1)) {
			script.append("../");
		}
		script.append("?topic=");
		script.append(req.getPathInfo().substring("/help:".length()));
		script.append(scriptPart3);
		return new FilterHTMLHeadOutputStream(
			out,
			script.toString().getBytes());
	}
}
