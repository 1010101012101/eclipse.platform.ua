/*
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2000, 2003.
 * All Rights Reserved. 
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP  schedule Contract with IBM Corp. 
*/

package org.eclipse.ui.internal.cheatsheets.data;

/**
 * Interface containing the constants used by the cheatsheet parser
 * to identify the tags used in the cheatsheet file.
 */
public interface IParserTags {
	public static final String copyright = "Licensed Material - Property of IBM <<PART NUMBER - 5724-D15>> (C) Copyright IBM Corp. 2003 - All Rights Reserved. US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.".intern(); //$NON-NLS-1$
	public static final String TITLE = "title"; //$NON-NLS-1$
	public static final String INTRO = "intro"; //$NON-NLS-1$
	public static final String HREF = "href"; //$NON-NLS-1$
	public static final String ITEM = "item"; //$NON-NLS-1$
	public static final String SUBITEM = "subitem"; //$NON-NLS-1$
	public static final String SUPERITEM = "superitem"; //$NON-NLS-1$
	public static final String LABEL = "label"; //$NON-NLS-1$
	public static final String PLUGINID = "pluginId"; //$NON-NLS-1$
	public static final String CHEATSHEETID = "cheatsheetID"; //$NON-NLS-1$
	public static final String CHEATSHEET = "Cheatsheet"; //$NON-NLS-1$
	public static final String CLASS = "class"; //$NON-NLS-1$
	public static final String ACTIONPHRASE = "actionphrase"; //$NON-NLS-1$
	public static final String ACTIONPARAM = "actionparam"; //$NON-NLS-1$
	public static final String ACTION = "action"; //$NON-NLS-1$
	public static final String VERSION = "version";//$NON-NLS-1$
	public static final String ID = "id";//$NON-NLS-1$
	public static final String DYNAMIC = "dynamic";//$NON-NLS-1$
	public static final String CHEATSHEETMANAGER = "CheatsheetManager";//$NON-NLS-1$
	public static final String MANAGERDATA = "CSMData";//$NON-NLS-1$
	public static final String MANAGERDATAKEY = "key"; //$NON-NLS-1$
	public static final String DYNAMICDATA = "dynamicData"; //$NON-NLS-1$
	public static final String DYNAMICSUBITEMDATA = "dynamicSubItemData"; //$NON-NLS-1$
	public static final String SUBITEMLABEL ="subitemlabel"; //$NON-NLS-1$
	
	public static final String COMPLETED ="completed"; //$NON-NLS-1$
	public static final String SUBITEMCOMPLETED ="subitemcompleted"; //$NON-NLS-1$
	public static final String SUBITEMSKIPPED ="subitemskipped"; //$NON-NLS-1$
	public static final String CURRENT = "current"; //$NON-NLS-1$
	public static final String EXPANDED = "expanded"; //$NON-NLS-1$
	public static final String EXPANDRESTORE = "expandRestore"; //$NON-NLS-1$
	public static final String BUTTON = "button"; //$NON-NLS-1$
	public static final String BUTTONSTATE = "buttonstate"; //$NON-NLS-1$
	public static final String URL = "url"; //$NON-NLS-1$
	
}
