package org.eclipse.help.ui.internal.search;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
import java.net.URLEncoder;
import java.util.*;
import org.eclipse.help.internal.toc.Toc;
import org.eclipse.help.internal.util.URLCoder;
/**
 * Help Search Query.
 */
public class SearchQueryData {
	/** 
	 * Default maximum number of hits that a search engine
	 * will search stop
	 */
	private static int MAX_HITS = 500;
	/** maximum number of hits that a search engine
	 * will search stop
	 */
	private int maxHits;
	private boolean fieldSearch;
	private boolean bookFiltering;
	private List selectedBooks;
	/** search keyword(s) */
	private String expression;
	/** locale to be used for search */
	private String locale;
	/** fields that will be searched */
	private Collection fieldNames;
	/**
	 * HelpSearchQuery constructor.
	 * @param key java.lang.String
	 * @param maxH int
	 */
	public SearchQueryData() {
		expression = "";
		bookFiltering = false;
		locale = Locale.getDefault().toString();
		maxHits = MAX_HITS;
		fieldSearch = false;
		fieldNames = new ArrayList();
		fieldNames.add("h1");
		fieldNames.add("h2");
		fieldNames.add("h3");
		fieldNames.add("keyword");
		fieldNames.add("role");
		fieldNames.add("solution");
		fieldNames.add("technology");
		selectedBooks = new ArrayList(0);
	}
	/**
	 * Returns the list of books to be excluded from search.
	 * (A category is a top level topic in an info view)
	 * When the list is null (note, empty list is not the same as null)
	 * no filtering is performed.
	 */
	public List getSelectedBooks() {
		return selectedBooks;
	}
	/**
	 * Returns the locale in which the search will be performed.
	 */
	public String getLocale() {
		return locale;
	}
	/**
	 * Returns true if books filtering is enabled.
	 */
	public boolean isBookFiltering() {
		return bookFiltering;
	}
	/**
	 * Returns true if search is to be performed on the fields only.
	 */
	public boolean isFieldsSearch() {
		return fieldSearch;
	}
	/**
	 * Enables book filtering.
	 * @param enable true if book filtering is turned on
	 */
	public void setBookFiltering(boolean enable) {
		this.bookFiltering = enable;
	}
	/**
	 * Sets the list of books to be included in search.
	 */
	public void setSelecteBooks(List selected) {
		this.selectedBooks = selected;
	}
	/**
	 * Sets search to be performed on the fields only.
	 * @param fieldSearch true if field only search
	 */
	public void setFieldsSearch(boolean fieldSearch) {
		this.fieldSearch = fieldSearch;
	}
	/**
	 * Sets locale in which the search will be performed.
	 * @param newLocale java.lang.String
	 */
	public void setLocale(String newLocale) {
		locale = newLocale;
	}
	/**
	 * Changes a limit on number of hits returned by the search engine
	 * @param newMaxHits int
	 */
	public void setMaxHits(int newMaxHits) {
		maxHits = newMaxHits;
	}
	public String toURLQuery() {
		String q =
			"keyword="
				+ URLCoder.encode(expression)
				+ "&maxHits="
				+ maxHits
				+ "&lang="
				+ (locale != null ? locale : Locale.getDefault().toString());
		if (fieldNames != null && !fieldNames.isEmpty())
			for (Iterator iterator = fieldNames.iterator(); iterator.hasNext();) {
				String field = (String) iterator.next();
				q += "&field=" + URLEncoder.encode(field);
			}
		if (fieldSearch)
			q += "&fieldSearch=true";
		else
			q += "&fieldSearch=false";
		if (bookFiltering && selectedBooks != null)
			for (Iterator iterator = selectedBooks.iterator(); iterator.hasNext();) {
				Toc toc = (Toc) iterator.next();
				q += "&scope=" + URLEncoder.encode(toc.getHref());
			}
		return q;
	} /**
	 * Gets the expression
	 * @return Returns a String
	 */
	public String getExpression() {
		return expression;
	}
	/**
	 * Sets the expression
	 * @param expression The expression to set
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}
}