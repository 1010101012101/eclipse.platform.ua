/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.help.internal.base.BaseHelpSystem;
import org.eclipse.help.internal.search.ISearchHitCollector;
import org.eclipse.help.internal.search.ISearchQuery;
import org.eclipse.help.internal.search.SearchQuery;
import org.eclipse.help.internal.webapp.data.UrlUtil;

/*
 * Returns the search hits for the query provided in the phrase parameter.
 * 
 * This is called on infocenters by client workbenches configured for remote
 * help in order to retrieve search hits from the remote help server.
 */
public class SearchServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final String PARAMETER_PHRASE = "phrase"; //$NON-NLS-1$
	private Collection results = new ArrayList();
	private ISearchHitCollector collector = new ISearchHitCollector() {
		public void addHits(List hits, String wordsSearched) {
			if (results != null) {
				results.addAll(hits);
			}
		}
	};
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String locale = UrlUtil.getLocale(req, resp);
		req.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
		resp.setContentType("application/xml; charset=UTF-8"); //$NON-NLS-1$
		String phrase = req.getParameter(PARAMETER_PHRASE);
		if (phrase != null) {
			ISearchQuery query = new SearchQuery(phrase, false, Collections.EMPTY_LIST, locale);
			results.clear();
			BaseHelpSystem.getSearchManager().search(query, collector, new NullProgressMonitor());
			String response = SearchSerializer.serialize(results);
			resp.getWriter().write(response);
		}
		else {
			resp.sendError(400); // bad request; missing parameter
		}
	}
}
