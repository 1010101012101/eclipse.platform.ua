/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
package org.eclipse.help.servlet;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;
/**
 * Servlet to interface client with remote Eclipse
 */
public class ContentServlet extends HttpServlet {
	private static final String RESOURCE_BUNDLE = ContentServlet.class.getName();
	private ResourceBundle resBundle;
	private EclipseConnector connector;

	/**
	 */
	public void init() throws ServletException {
		try {
			resBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE);
			connector = new EclipseConnector(getServletContext());
		} catch (Throwable e) {
			//log(resBundle.getString("problemInit"), e); 
			throw new ServletException(e);
		}
	}

	/**
	 * Called by the server (via the <code>service</code> method) to
	 * allow a servlet to handle a GET request. 
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

			if (connector != null)
				connector.transfer(req, resp);
	}
	/**
	 *
	 * Called by the server (via the <code>service</code> method)
	 * to allow a servlet to handle a POST request.
	 *
	 * Handle the search requests,
	 *
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		if (connector != null)
				connector.transfer(req, resp);
	}
}