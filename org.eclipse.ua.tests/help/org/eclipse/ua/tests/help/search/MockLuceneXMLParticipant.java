/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ua.tests.help.search;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.help.search.XMLSearchParticipant;
import org.xml.sax.Attributes;

public class MockLuceneXMLParticipant extends XMLSearchParticipant {

	public static final String DOC_1 = "/org.eclipse.ua.tests/data/help/search/lucene_xml_1.xml";
	public static final String DOC_2 = "/org.eclipse.ua.tests/data/help/search/lucene_xml_2.xml";
	
	public Set getAllDocuments(String locale) {
		HashSet set = new HashSet();
		set.add(DOC_1 + "?id=xml1" + "?id=xml2");
		set.add(DOC_2);
		return set;
	}
	
	public Set getContributingPlugins() {
		Set result = new HashSet();
		result.add("org.eclipse.ua.tests");
		return result;
	}

	protected void handleStartElement(String name, Attributes attributes,
			IParsedXMLContent data) {
		if ("searchable".equalsIgnoreCase(name)) {
			data.setTitle(attributes.getValue("title"));			
		}
		String summary = attributes.getValue("summary");
		if (summary != null) {
		    data.addToSummary(summary);
		}
	}

	protected void handleEndElement(String name, IParsedXMLContent data) {
	}

	protected void handleText(String text, IParsedXMLContent data) {
		String top = getTopElement();
		if ("text".equalsIgnoreCase(top)) {
			data.addText(text);
		}
	}
	
	public boolean open(String id) {
		System.out.println("Open " + id);
		return true;
	}

}
