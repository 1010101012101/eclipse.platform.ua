/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
package org.eclipse.help.ui.internal.search;

import java.net.*;
import java.util.*;

import org.eclipse.help.internal.search.*;
import org.eclipse.help.internal.util.*;
import org.eclipse.ui.*;

/**
 * Help Search Query Data.
 */
public class SearchQueryData {
	private SearchQuery searchQuery;
	/** 
	 * Default maximum number of hits that a search engine
	 * will search stop
	 */
	private static int MAX_HITS = 500;
	/**
	 * maximum number of hits that a search engine
	 * will search
	 */
	private int maxHits;
	private boolean bookFiltering;
	private IWorkingSet[] workingSets;
	
	/**
	 * HelpSearchQuery constructor.
	 * @param key java.lang.String
	 * @param maxH int
	 */
	public SearchQueryData() {
		searchQuery = new SearchQuery();
		bookFiltering = false;
		maxHits = MAX_HITS;
		workingSets = new IWorkingSet[0];
	}
	public ISearchQuery getSearchQuery() {
		return searchQuery;
	}
	/**
	 * Returns the list of books to be included in search,
	 * or null (if bookFiltering is off).
	 */
	public IWorkingSet[] getSelectedWorkingSets() {
		if (bookFiltering) {
			return workingSets;
		}
		return null;
	}
	/**
	 * Returns the locale in which the search will be performed.
	 */
	public String getLocale() {
		return searchQuery.getLocale();
	}
	/**
	 * Returns true if books filtering is enabled.
	 */
	public boolean isBookFiltering() {
		return bookFiltering;
	}
	/**
	 * Enables book filtering.
	 * @param enable true if book filtering is turned on
	 */
	public void setBookFiltering(boolean enable) {
		this.bookFiltering = enable;
		if (enable && workingSets.length > 0) {
			/*
			selectedBooks = new ArrayList();
			IToc tocs[] =
				HelpSystem.getTocManager().getTocs(searchQuery.getLocale());
			for (int i = 0; i < tocs.length; i++)
				selectedBooks.add(tocs[i]);
			*/
		}
	}

	/**
	 * Sets the working sets to be included in search.
	 * @param workingSets	 */
	public void setSelectedWorkingSets(IWorkingSet[] workingSets) {
		this.workingSets = workingSets;
	}
	
	/**
	 * Sets search to be performed on the fields only.
	 * @param fieldSearch true if field only search
	 */
	public void setFieldsSearch(boolean fieldSearch) {
		searchQuery.setFieldSearch(fieldSearch);
	}
	/**
	* Sets the maxHits.
	* @param maxHits The maxHits to set
	*/
	public void setMaxHits(int maxHits) {
		this.maxHits = maxHits;
	}

	public String toURLQuery() {
		String q =
			"searchWord="
				+ URLCoder.encode(searchQuery.getSearchWord())
				+ "&maxHits="
				+ maxHits
				+ "&lang="
				+ (searchQuery.getLocale());
		if (!searchQuery.getFieldNames().isEmpty())
			for (Iterator iterator = searchQuery.getFieldNames().iterator();
				iterator.hasNext();
				) {
				String field = (String) iterator.next();
				q += "&field=" + URLEncoder.encode(field);
			}
		if (searchQuery.isFieldSearch())
			q += "&fieldSearch=true";
		else
			q += "&fieldSearch=false";
		if (bookFiltering) {
			for (int i=0; i<workingSets.length; i++ ) {
				q += "&scope=" + URLEncoder.encode(workingSets[i].getName());
			}
		}
		return q;
	}
	/**
	 * Gets the searchWord
	 * @return Returns a String
	 */
	public String getSearchWord() {
		return searchQuery.getSearchWord();
	}
	/**
	 * Sets the searchWord
	 * @param searchWord The search word to set
	 */
	public void setSearchWord(String searchWord) {
		searchQuery.setSearchWord(searchWord);
	}
	/**
	 * Gets the maxHits.
	 * @return Returns a int
	 */
	public int getMaxHits() {
		return maxHits;
	}

}