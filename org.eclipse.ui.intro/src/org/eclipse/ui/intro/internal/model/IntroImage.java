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
 * An intro image element.
 */
public class IntroImage extends IntroElement {

	protected static final String IMAGE_ELEMENT = "img";

	private static final String SRC_ATTRIBUTE = "src";
	private static final String ALT_ATTRIBUTE = "alt";

	private String src;
	private String alt;

	IntroImage(IConfigurationElement element) {
		super(element);
		src = element.getAttribute(SRC_ATTRIBUTE);
		alt = element.getAttribute(ALT_ATTRIBUTE);

		// Resolve.
		src = IntroModelRoot.getPluginLocation(src, element);
	}

	/**
	 * @return Returns the alt.
	 */
	public String getAlt() {
		return alt;
	}

	/**
	 * @return Returns the src.
	 */
	public String getSrc() {
		return src;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.intro.internal.model.IntroElement#getType()
	 */
	public int getType() {
		return IntroElement.IMAGE;
	}

}
