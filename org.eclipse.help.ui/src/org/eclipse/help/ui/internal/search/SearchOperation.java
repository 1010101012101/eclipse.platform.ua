package org.eclipse.help.ui.internal.search;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import org.apache.xerces.parsers.DOMParser;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.help.*;
import org.eclipse.help.internal.ui.WorkbenchHelpPlugin;
import org.eclipse.help.internal.ui.util.WorkbenchResources;
import org.eclipse.help.internal.util.*;
import org.eclipse.help.ui.browser.IBrowser;
import org.eclipse.jface.resource.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.search.ui.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
/**
 * Help Search Query.
 */
public class SearchOperation extends WorkspaceModifyOperation {
	// Images
	private static final ImageDescriptor IMAGE_DSCR_SEARCH =
		ImageDescriptor.createFromURL(
			WorkbenchResources.getImagePath(SearchUIConstants.IMAGE_KEY_SEARCH));
	private static final ImageDescriptor IMAGE_DSCR_TOPIC =
		ImageDescriptor.createFromURL(
			WorkbenchResources.getImagePath(SearchUIConstants.IMAGE_KEY_TOPIC));
	// Resource we will be using
	private static final IResource resource =
		ResourcesPlugin.getWorkspace().getRoot();
	private static ImageRegistry imgRegistry = null;
	private SearchQueryData queryData = null;
	/**
	 * HelpSearchQuery constructor.
	 * @param key java.lang.String
	 */
	public SearchOperation(SearchQueryData data) {
		if (imgRegistry == null) {
			imgRegistry = WorkbenchHelpPlugin.getDefault().getImageRegistry();
			imgRegistry.put(SearchUIConstants.IMAGE_KEY_SEARCH, IMAGE_DSCR_SEARCH);
			imgRegistry.put(SearchUIConstants.IMAGE_KEY_TOPIC, IMAGE_DSCR_TOPIC);
		}
		queryData = data;
	}
	/**
	 * @see WorkspaceModifyOperation#execute(IProgressMonitor)
	 */
	protected void execute(IProgressMonitor monitor)
		throws CoreException, InvocationTargetException, InterruptedException {
		try {
			//convert qurey to a URL format
			String queryURL = queryData.toURLQuery();
			URL surl = new URL("search:/?" + queryURL);
			InputStream resultsStream = surl.openStream();
			if (resultsStream != null)
				displayResults(resultsStream);
		} catch (Exception e) {
			Logger.logError(WorkbenchResources.getString("WE021"), e);
		}
		monitor.done();
	}
	private void displayResults(InputStream resultsStream) {
		ISearchResultView sView = SearchUI.getSearchResultView();
		if (sView != null)
			sView
				.searchStarted(
					SearchUIConstants.RESULTS_PAGE_ID,
					WorkbenchResources.getString("singleSearchResult", queryData.getExpression()),
					WorkbenchResources.getString(
						"multipleSearchResult",
						queryData.getExpression(),
						"{0}"),
					IMAGE_DSCR_SEARCH,
					null,
					new LabelProvider() {
			public String getText(Object element) {
				if (element instanceof ISearchResultViewEntry)
					try {
						ISearchResultViewEntry entry = (ISearchResultViewEntry) element;
						return (String) entry.getSelectedMarker().getAttribute(
							SearchUIConstants.HIT_MARKER_ATTR_LABEL);
					} catch (CoreException ce) {
					}
				return "";
			}
			public Image getImage(Object element) {
				if (element instanceof ISearchResultViewEntry)
					return imgRegistry.get(SearchUIConstants.IMAGE_KEY_TOPIC);
				return null;
			}
		}, new org.eclipse.jface.action.Action() {
			public void run() {
				ISearchResultView view = SearchUI.getSearchResultView();
				view.getSelection();
				ISelection selection = view.getSelection();
				Object element = null;
				if (selection instanceof IStructuredSelection)
					element = ((IStructuredSelection) selection).getFirstElement();
				if (element instanceof ISearchResultViewEntry) {
					ISearchResultViewEntry entry = (ISearchResultViewEntry) element;
					try {
						if (!AppServer.isRunning())
							return; // may want to display an error message
						String url =
							"http://"
								+ AppServer.getHost()
								+ ":"
								+ AppServer.getPort()
								+ "/help?tab=search&query="
								+ URLCoder.encode(queryData.getExpression())
								/*+ "&topic=http://"
								+ AppServer.getHost()
								+ ":"
								+ AppServer.getPort()
								+ "/help/content/help:"
								+ (String) entry.getSelectedMarker().getAttribute(
									SearchUIConstants.HIT_MARKER_ATTR_HREF)*/;
						IBrowser browser = WorkbenchHelpPlugin.getDefault().getHelpBrowser();
						browser.displayURL(url);
					} catch (Exception e) {
					}
				}
			}
		}, new IGroupByKeyComputer() {
			public Object computeGroupByKey(IMarker marker) {
				try {
					if (marker.getAttribute(SearchUIConstants.HIT_MARKER_ATTR_HREF) != null)
						return marker.getAttribute(SearchUIConstants.HIT_MARKER_ATTR_HREF);
				} catch (CoreException ce) {
				}
				return "UNKNOWN";
			}
		}, this);
		// Delete all previous results
		try {
			resource.deleteMarkers(
				SearchUIConstants.HIT_MARKER_ID,
				true,
				IResource.DEPTH_INFINITE);
		} catch (CoreException ex) {
		}
		createResultsMarkers(resultsStream, sView);
		sView.searchFinished();
	}
	private void createResultsMarkers(
		InputStream resultsStream,
		ISearchResultView sView) {
		DOMParser parser = new DOMParser();
		InputSource input = new InputSource(resultsStream);
		try {
			parser.parse(input);
		} catch (Exception e) {
			// this should work, as we created the xml, but ...
			Logger.logError("", e);
		}
		Document doc = parser.getDocument();
		if (doc == null)
			return;
		Element searchRoot = doc.getDocumentElement();
		if (searchRoot == null)
			return;
		NodeList topics = searchRoot.getElementsByTagName(ITopic.TOPIC);
		for (int i = 0; i < topics.getLength(); i++) {
			Element topic = (Element) topics.item(i);
			try {
				IMarker marker = null;
				marker = resource.createMarker(SearchUIConstants.HIT_MARKER_ID);
				marker.setAttribute(
					SearchUIConstants.HIT_MARKER_ATTR_HREF,
					topic.getAttribute(ITopic.HREF));
				marker.setAttribute(
					SearchUIConstants.HIT_MARKER_ATTR_RESULTOF,
					queryData.toURLQuery());
				marker.setAttribute(
					SearchUIConstants.HIT_MARKER_ATTR_LABEL,
					topic.getAttribute(ITopic.LABEL));
				marker.setAttribute(
					SearchUIConstants.HIT_MARKER_ATTR_ORDER,
					new Integer(i).toString());
				sView.addMatch(
					topic.getAttribute(ITopic.LABEL),
					marker.getAttribute(SearchUIConstants.HIT_MARKER_ATTR_HREF),
					resource,
					marker);
			} catch (CoreException ce) {
			}
		}
	}
	/**
	 * Gets the queryData
	 * @return Returns a HelpSearchQuery
	 */
	public SearchQueryData getQueryData() {
		return queryData;
	}
}