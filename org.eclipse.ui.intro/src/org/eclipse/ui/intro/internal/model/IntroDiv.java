/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.intro.internal.model;

import org.eclipse.core.runtime.*;

/**
 * An intro div.
 */
public class IntroDiv extends AbstractIntroContainer {

	protected static final String DIV_ELEMENT = "div";

	private static final String LABEL_ATTRIBUTE = "label";

	private String label;

	/**
	 * @param element
	 */
	IntroDiv(IConfigurationElement element) {
		super(element);
		label = element.getAttribute(LABEL_ATTRIBUTE);
	}

	/**
	 * @return Returns the label.
	 */
	public String getLabel() {
		return label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.intro.internal.model.IntroElement#getType()
	 */
	public int getType() {
		return IntroElement.DIV;
	}

	// THESE METHODS MIGHT BE REMOVED. ADDED HERE FOR BACKWARD COMPATIBILITY.
	public IntroLink[] getLinks() {
		return (IntroLink[]) getChildrenOfType(IntroElement.LINK);
	}

	public String getText() {
		IntroText[] texts = (IntroText[]) getChildrenOfType(IntroElement.TEXT);
		if (texts.length == 0)
			return null;
		return texts[0].getText();
	}

}
