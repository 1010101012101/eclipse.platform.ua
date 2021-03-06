/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help;

/**
 * An entry that represents a see also node in the index. A see
 * also node must always reference another node.
 * 
 * @since 3.5
 */
public interface IIndexSee extends IUAElement {

	public String getKeyword(); 
	
	/**
	 * Determines whether the text to be displayed is "see" or "see also"
	 * @return true if this is a "see also", false if this is a "see"
	 */
	public boolean isSeeAlso();
	
	/**
	 * 
	 * @return an array of length zero if this see element references an element
	 * at the top level of the index, otherwise a list of entries which represent keywords
	 * at the second and lower levels of the index tree.
	 */
	public IIndexSubpath[] getSubpathElements();
}
