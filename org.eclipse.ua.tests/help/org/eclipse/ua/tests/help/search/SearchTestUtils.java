/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ua.tests.help.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.help.internal.base.BaseHelpSystem;
import org.eclipse.help.internal.search.ISearchQuery;
import org.eclipse.help.internal.search.SearchHit;
import org.eclipse.help.internal.search.SearchQuery;
import org.eclipse.help.internal.search.SearchResults;

public class SearchTestUtils {

	public static void searchAllLocales(String searchWord, String[] hrefs) {
		searchOneLocale(searchWord, hrefs, "en");	
		searchOneLocale(searchWord, hrefs, "de");
	}

	public static void searchOneLocale(String searchWord, String[] hrefs, String nl) {
		final Set hrefsToFind = new HashSet();
		final Set unexpectedHrefs = new HashSet();		
		hrefsToFind.addAll(Arrays.asList(hrefs));
		
		ISearchQuery query = new SearchQuery(searchWord, false, new ArrayList(), nl);
		SearchResults collector = new SearchResults(null, 500, nl);
		BaseHelpSystem.getSearchManager().search(query, collector, new NullProgressMonitor());
		SearchHit[] hits = collector.getSearchHits();
		for (int j=0;j<hits.length;++j) {
			String href = hits[j].getHref();
			// ignore query params
			int index = href.indexOf('?');
			if (index != -1) {
				href = href.substring(0, index);
			}
			if (hrefsToFind.contains(href)) {
				hrefsToFind.remove(href);
			}
			else {
				unexpectedHrefs.add(href);
			}
		}
		
		if (!hrefsToFind.isEmpty() || !unexpectedHrefs.isEmpty()) {
			StringBuffer buf = new StringBuffer();
			buf.append("While searching for: " + searchWord + ",\n");
			if (!hrefsToFind.isEmpty()) {
				buf.append("Some of the expected results were not found:\n");
				Iterator iter = hrefsToFind.iterator();
				while (iter.hasNext()) {
					String missedHref = (String)iter.next();
					buf.append(missedHref + "\n");
				}
			}
			if (!unexpectedHrefs.isEmpty()) {
				if (!hrefsToFind.isEmpty()) {
					buf.append("\nAlso,\n");
				}
				buf.append("Found some unexpected search results:\n");
				Iterator iter = unexpectedHrefs.iterator();
				while (iter.hasNext()) {
					String unexpectedHref = (String)iter.next();
					buf.append(unexpectedHref + "\n");
				}
			}
			Assert.fail(buf.toString());
		}
	}

}
