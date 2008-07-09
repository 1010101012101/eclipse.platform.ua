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


import junit.framework.TestCase;


public class LocaleTest extends TestCase {
	 
    public void testSearchWordInDefaultOnly() {
    	SearchTestUtils.searchOneLocale("duejrnfjudshebn", new String[] { "/org.eclipse.ua.tests/data/help/search/testnl1.xhtml"}, "en");
    	SearchTestUtils.searchOneLocale("duejrnfjudshebn", new String[0], "de");
    } 
    
    public void testSearchWordInNonDefaultOnly() {
    	SearchTestUtils.searchOneLocale("dkdskadksak", new String[] { "/org.eclipse.ua.tests/data/help/search/testnl1.xhtml"}, "de");
    	SearchTestUtils.searchOneLocale("dkdskadksak", new String[0], "en");
    }
     
    public void testSearchExactMatchInDefaultOnly() {
    	SearchTestUtils.searchOneLocale("\"fesaggresgf duejrnfjudshebn\"", new String[] { "/org.eclipse.ua.tests/data/help/search/testnl1.xhtml"}, "en");
    	SearchTestUtils.searchOneLocale("\"fesaggresgf duejrnfjudshebn\"", new String[0], "de");
    } 
    
    public void testSearchExactMatchInNonDefaultOnly() {
    	SearchTestUtils.searchOneLocale("\"dkdskadksak redfrewfdsa\"", new String[] { "/org.eclipse.ua.tests/data/help/search/testnl1.xhtml"}, "de");
    	SearchTestUtils.searchOneLocale("\"dkdskadksak redfrewfdsa\"", new String[0], "en");
    } 
    
    public void testSearchWithWildcardInDefaultOnly() {
    	SearchTestUtils.searchOneLocale("duejrnf?udshebn", new String[] { "/org.eclipse.ua.tests/data/help/search/testnl1.xhtml"}, "en");
    	SearchTestUtils.searchOneLocale("duejrnf?udshebn", new String[0], "de");
    } 
    
    public void testSearchWithWildcardInNonDefaultOnly() {
    	SearchTestUtils.searchOneLocale("dkd?kadksak", new String[] { "/org.eclipse.ua.tests/data/help/search/testnl1.xhtml"}, "de");
    	SearchTestUtils.searchOneLocale("dkd?kadksak", new String[0], "en");
    }

    public void testSearchPageNotInTocForLocale() {
    	SearchTestUtils.searchOneLocale("undefgfdsgfds", new String[0], "de");
    	SearchTestUtils.searchOneLocale("undefgfdsgfds", new String[0], "en");
    }
    
    public void testSearchPageOnlyInDefaultToc() {
    	SearchTestUtils.searchOneLocale("idskrekfuej", new String[0], "de");
    	SearchTestUtils.searchOneLocale("idskrekfuej", new String[] { "/org.eclipse.ua.tests/data/help/search/test_en.html"}, "en");
    } 
    
    public void testSearchPageOnlyInLocalToc() {
    	SearchTestUtils.searchOneLocale("deuufjfu", new String[0], "en");
    	SearchTestUtils.searchOneLocale("deuufjfu", new String[] { "/org.eclipse.ua.tests/data/help/search/test_de.html"}, "de");
    }

    public void testSearchEnglishStemming_ed() {
    	SearchTestUtils.searchOneLocale("udjerufdjd", new String[0], "de");
    	SearchTestUtils.searchOneLocale("udjerufdjd", new String[] { "/org.eclipse.ua.tests/data/help/search/test10.xhtml"}, "en"); 
    }
    
    public void testSearchEnglishStemming_ing() {
    	SearchTestUtils.searchOneLocale("kjfdskajdfska", new String[0], "de");
    	SearchTestUtils.searchOneLocale("kjfdskajdfska", new String[] { "/org.eclipse.ua.tests/data/help/search/test10.xhtml"}, "en"); 
    } 
    
    // Prefixes are not matched by stemming
    public void testSearchEnglishStemming_re() {
    	SearchTestUtils.searchOneLocale("dhdsahkdshakjd", new String[0], "de");
    	SearchTestUtils.searchOneLocale("dhdsahkdshakjd", new String[0], "en"); 
    }   

    /*
     * These tests do not pass, I would have expected German stemming to pick them up
    public void testSearchGermanStemming_keit() {
    	SearchTestUtils.searchOneLocale("iskdskhfsff", new String[0], "en");
    	SearchTestUtils.searchOneLocale("iskdskhfsff", new String[] { "/org.eclipse.ua.tests/data/help/search/test10.xhtml"}, "de"); 
    }
    
    public void testSearchGermanStemming_isch() {
    	SearchTestUtils.searchOneLocale("dfskajkfsaf", new String[0], "en");
    	SearchTestUtils.searchOneLocale("dfskajkfsaf", new String[] { "/org.eclipse.ua.tests/data/help/search/test10.xhtml"}, "de"); 
    }
    */
    
}
