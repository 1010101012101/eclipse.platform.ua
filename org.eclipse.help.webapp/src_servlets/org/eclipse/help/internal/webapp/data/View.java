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
package org.eclipse.help.internal.webapp.data;

public class View {
	private String name;
	private String url;
	private String imageURL;

	public View(String name, String url, String imageURL) {
		this.name = name;
		this.url = url;
		this.imageURL = imageURL;
	}
	
	public String getName() {
		return name;
	}
	
	public String getURL() {
		return url;
	}
	
	/**
	 * Returns the enabled gray image
	 * @return String
	 */
	public String getImage() {
		int i = imageURL.lastIndexOf('/');
		return imageURL.substring(0, i) + "/e_"+ imageURL.substring(i+1);
	}
	
	/**
	 * Returns the image when selected
	 * @return String
	 */
	public String getOnImage() {
		return getImage();
	}
}
