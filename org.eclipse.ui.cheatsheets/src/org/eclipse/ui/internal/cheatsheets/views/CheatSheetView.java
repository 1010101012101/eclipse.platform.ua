package org.eclipse.ui.internal.cheatsheets.views;

import java.net.*;
import java.net.URL;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.internal.cheatsheets.*;
import org.eclipse.ui.internal.cheatsheets.actions.CheatSheetMenu;
import org.eclipse.ui.internal.cheatsheets.registry.*;
import org.eclipse.ui.internal.cheatsheets.registry.CheatSheetElement;
import org.eclipse.ui.part.ViewPart;

public class CheatSheetView extends ViewPart {

	private boolean actionBarContributed = false;
	private CheatSheetExpandRestoreAction expandRestoreAction;
	private CheatSheetViewer viewer;
	private IMemento memento;

	private void contributeToActionBars() {
		//here you have to assemble the same list as the list added to the help menu bar.
		//so an external class should do it so it can be shared with something that
		//both these classes can use.  I will call it CheatSheetActionGetter.
		//	System.out.println("Inside of contribute to action bars!!!!");
	
		IActionBars bars = getViewSite().getActionBars();
		IMenuManager menuManager = bars.getMenuManager();
		IToolBarManager tbmanager = bars.getToolBarManager();
	
		// fields
		String collapseExpandFile = "icons/full/elcl16/collapse_expand_all.gif"; //$NON-NLS-1$
		URL collapseExpandURL = CheatSheetPlugin.getPlugin().find(new Path(collapseExpandFile));
		ImageDescriptor collapseExpandImage = ImageDescriptor.createFromURL(collapseExpandURL);
	
		expandRestoreAction = new CheatSheetExpandRestoreAction(CheatSheetPlugin.getResourceString(ICheatSheetResource.COLLAPSE_ALL_BUT_CURRENT_TOOLTIP), false, viewer);
		expandRestoreAction.setToolTipText(CheatSheetPlugin.getResourceString(ICheatSheetResource.COLLAPSE_ALL_BUT_CURRENT_TOOLTIP));
		expandRestoreAction.setImageDescriptor(collapseExpandImage);
		tbmanager.add(expandRestoreAction);

		viewer.setExpandRestoreAction(expandRestoreAction);
	
		CheatSheetMenu cheatsheetMenuMenuItem = new CheatSheetMenu();
		menuManager.add(cheatsheetMenuMenuItem);
	}


	/**
	 * Creates the SWT controls for this workbench part.
	 * <p>
	 * Clients should not call this method (the workbench calls this method at
	 * appropriate times).
	 * </p>
	 * <p>
	 * For implementors this is a multi-step process:
	 * <ol>
	 *   <li>Create one or more controls within the parent.</li>
	 *   <li>Set the parent layout as needed.</li>
	 *   <li>Register any global actions with the <code>IActionService</code>.</li>
	 *   <li>Register any popup menus with the <code>IActionService</code>.</li>
	 *   <li>Register a selection provider with the <code>ISelectionService</code>
	 *     (optional). </li>
	 * </ol>
	 * </p>
	 *
	 * @param parent the parent control
	 */
	public void createPartControl(Composite parent) {
		viewer = new CheatSheetViewer();
		viewer.createPartControl(parent);
	
		if (!actionBarContributed) {
			contributeToActionBars();
			actionBarContributed = true;
		}
		if (memento != null) {
			restoreState(memento);
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		super.dispose();

		// TODO: remove this if the dispose api is removed
		if(viewer != null)
			viewer.dispose();
	}
	
	public CheatSheetElement getContent() {
		if(viewer != null) {
			return viewer.getContent();
		}
		return null;
	}

	public String getgetCheatSheetID() {
		if(viewer != null) {
			return viewer.getCheatSheetID();
		}
		return null;
	}

	/* (non-Javadoc)
	 * Initializes this view with the given view site.  A memento is passed to
	 * the view which contains a snapshot of the views state from a previous
	 * session.  Where possible, the view should try to recreate that state
	 * within the part controls.
	 * <p>
	 * This implementation will ignore the memento and initialize the view in
	 * a fresh state.  Subclasses may override the implementation to perform any
	 * state restoration as needed.
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		init(site);
		this.memento = memento;
	}

	/**
	 * Restore the view state
	 */
	private void restoreState(IMemento memento) {
		IMemento contentMemento = memento.getChild(ICheatSheetResource.MEMENTO);
		if (contentMemento != null) {
			String id = contentMemento.getString(ICheatSheetResource.MEMENTO_ID);
			String name = contentMemento.getString(ICheatSheetResource.MEMENTO_NAME);
			
			// Using an if/else if here because at a point in time there was a different
			// attribute used. As a result an if/else could cause setInput(null) to be
			// invoked but this would throw an IllegalArgumentException. 
			if(name != null) {
				try {
				URL fileURL = new URL(contentMemento.getString(ICheatSheetResource.MEMENTO_URL));
				setInput(id, name, fileURL);
				} catch (MalformedURLException mue) {
				}
			} else if (id != null) {
				setInput(id);
			}

		}
	}

	/* (non-Javadoc)
	 * Method declared on IViewPart.
	 */
	public void saveState(IMemento memento) {
		if(viewer != null) {
			CheatSheetElement element = viewer.getContent();

			if(element == null) {
				// Currently no cheat sheet is being displayed so just return 
				return;
			}

			IMemento contentMemento = memento.createChild(ICheatSheetResource.MEMENTO);

			CheatSheetElement tempElement = CheatSheetRegistryReader.getInstance().findCheatSheet(element.getID());
			if(tempElement != null) {
				contentMemento.putString(ICheatSheetResource.MEMENTO_ID, element.getID());
			} else {
				contentMemento.putString(ICheatSheetResource.MEMENTO_ID, element.getID());
				contentMemento.putString(ICheatSheetResource.MEMENTO_NAME, element.getLabel(null));
				contentMemento.putString(ICheatSheetResource.MEMENTO_URL, element.getContentFile());
			}

			// Make sure the current cheat sheet is saved
			viewer.saveCurrentSheet();
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {		
		if(viewer != null) {
			viewer.setFocus();
		}
	}
	
	public void setInput(String id) {
		if(viewer != null) {
			viewer.setInput(id);
		}
	}

	public void setInput(String id, String name, URL url) {
		if(viewer != null) {
			viewer.setInput(id, name, url);
		}
	}
}
