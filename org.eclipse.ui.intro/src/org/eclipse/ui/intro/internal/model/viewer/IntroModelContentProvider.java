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

package org.eclipse.ui.intro.internal.model.viewer;
import org.eclipse.jface.viewers.*;
import org.eclipse.ui.intro.internal.model.*;

public class IntroModelContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object element) {

		IntroElement introElement = null;
		if (element instanceof IntroElement)
			// synch the resource first.
			introElement = (IntroElement) element;

		if (introElement != null
			&& introElement.isOfType(IntroElement.ABSTRACT_CONTAINER))
			return ((AbstractIntroContainer) introElement).getChildren();

		return new Object[0];
	}

	public Object getParent(Object element) {
		IntroElement introElement = null;
		if (element instanceof IntroElement) {
			// synch the resource first.
			introElement = (IntroElement) element;
			return introElement.getParent();
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		IntroElement introElement = null;
		if (element instanceof IntroElement)
			// synch the resource first.
			introElement = (IntroElement) element;
		if (introElement != null
			&& introElement.isOfType(IntroElement.ABSTRACT_CONTAINER))
			return true;
		return false;
	}

	/**
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object element) {
		return getChildren(element);
	}

	/**
	 * @see IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

}
