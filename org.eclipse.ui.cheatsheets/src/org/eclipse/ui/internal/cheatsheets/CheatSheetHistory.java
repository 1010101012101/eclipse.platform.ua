/*
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2000, 2003.
 * All Rights Reserved. 
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP  schedule Contract with IBM Corp. 
*/

package org.eclipse.ui.internal.cheatsheets;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.ui.*;

import org.eclipse.ui.internal.cheatsheets.registry.*;

/**
 * This is used to store the most recently used (MRU) list
 * of cheatsheet for the entire workbench.
 */
public class CheatSheetHistory {

	private static final int DEFAULT_DEPTH = 5;
	
	private ArrayList history;
	private CheatSheetRegistryReader reg; 
	private ListenerList listeners = new ListenerList();

	public CheatSheetHistory(CheatSheetRegistryReader reg) {
		this.history = new ArrayList(DEFAULT_DEPTH);
		this.reg = reg;
	}

	public void addListener(IPropertyListener l) {
		listeners.add(l);
	}	
	
	public void removeListener(IPropertyListener l) {
		listeners.remove(l);
	}	
	
	private void fireChange() {
		Object[] array = listeners.getListeners();
		for (int i = 0; i < array.length; i++) {
			IPropertyListener element = (IPropertyListener)array[i];
			element.propertyChanged(this, 0);
		}
	}
	
	public IStatus restoreState(IMemento memento) {
		IMemento [] children = memento.getChildren("element"); //$NON-NLS-1$
		for (int i = 0; i < children.length && i < DEFAULT_DEPTH; i++) {
			CheatSheetElement element =
				reg.findCheatSheet(children[i].getID());
			if (element != null) 
				history.add(element);
		}
		return new Status(IStatus.OK,ICheatSheetResource.CHEAT_SHEET_PLUGIN_ID,0,"",null); //$NON-NLS-1$
	}
	
	public IStatus saveState(IMemento memento) {
		Iterator iter = history.iterator();
		while (iter.hasNext()) {
			CheatSheetElement element = (CheatSheetElement)iter.next();
			memento.createChild("element", element.getID()); //$NON-NLS-1$
		}
		return new Status(IStatus.OK,ICheatSheetResource.CHEAT_SHEET_PLUGIN_ID,0,"",null); //$NON-NLS-1$
	}

	public void add(String id) {
		CheatSheetElement element = reg.findCheatSheet(id);
		if (element != null) 
			add(element);
	}
	
	public void add(CheatSheetElement element) {
		// Avoid duplicates
		if (history.contains(element))
			return;

		// If the shortcut list will be too long, remove oldest ones			
		int size = history.size();
		int preferredSize = DEFAULT_DEPTH;
		while (size >= preferredSize) {
			size--;
			history.remove(size);
		}
		
		// Insert at top as most recent
		history.add(0, element);
		fireChange();
	}
	
	public void refreshFromRegistry() {
		boolean change = false;
		
		Iterator iter = history.iterator();
		while (iter.hasNext()) {
			CheatSheetElement element = (CheatSheetElement)iter.next();
			if (reg.findCheatSheet(element.getID()) == null) {
				iter.remove();
				change = true;
			}
		}
		
		if (change)
			fireChange();
	}

	/**
	 * Copy the requested number of items from the history into
	 * the destination list at the given index.
	 * 
	 * @param dest destination list to contain the items
	 * @param destStart index in destination list to start copying items at
	 * @param count number of items to copy from history
	 * @return the number of items actually copied
	 */
	public int copyItems(List dest, int destStart, int count) {
		int itemCount = count;
		if (itemCount > history.size())
			itemCount = history.size();
			
		for (int i = 0; i < itemCount; i++)
			dest.add(destStart + i, history.get(i));
			
		return itemCount;
	} 
}

