/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
package org.eclipse.help.internal.context;
import java.util.*;
import org.eclipse.help.IHelpResource;
import org.eclipse.help.internal.util.ContextResources;
public class ContextsBuilder {
	/**
	 * Map of contexts indexed by short ID
	 */
	protected Map contexts;
	private String definingPluginID;
	private String pluginID;
	/**
	 * Contexts Builder Constructor.
	 */
	public ContextsBuilder() {
		this.contexts = new HashMap();
	}
	public void build(RelatedTopic relatedTopic) {
		relatedTopic.setPlugin(definingPluginID);
		// set the href on the related topic   
		String href = relatedTopic.getHref();
		if (href == null)
			relatedTopic.setHref("");
		else {
			if (!href.equals("") // no empty link
				&& !href.startsWith("/") // no help url
				&& href.indexOf(':') == -1) // no other protocols
				{
				relatedTopic.setHref("/" + definingPluginID + "/" + href);
			}
		}
	}
	public void build(Context context) {
		context.setPluginID(pluginID);
		context.setText(getNLText(definingPluginID, context.getText()));
		// if context with same Id exists, merge them
		Context existingContext = (Context) contexts.get(context.getShortId());
		if (existingContext != null) {
			mergeContexts(existingContext, context);
		} else {
			contexts.put(context.getShortId(), context);
		}
	}
	public void build(ContextsFile contextsFile) {
		this.pluginID = contextsFile.getPluginID();
		this.definingPluginID = contextsFile.getDefiningPluginID();
		ContextsFileParser parser = new ContextsFileParser(this);
		parser.parse(contextsFile);
	}
	public void build(List pluginContextsFiles) {
		for (Iterator contextFilesIt = pluginContextsFiles.iterator();
			contextFilesIt.hasNext();
			) {
			ContextsFile contextsFile = (ContextsFile) contextFilesIt.next();
			contextsFile.build(this);
		}
	}
	public Map getBuiltContexts() {
		return contexts;
	}
	/**
	 * Merges Text and Links from new Context into
	 * an existing Context
	 */
	private void mergeContexts(Context existingContext, Context newContext) {
		// Merge Text
		if (newContext.getText() != null) {
			if (existingContext.getText() != null) {
				existingContext.setText(
					existingContext.getText() + "\n" + newContext.getText());
			} else {
				existingContext.setText(newContext.getText());
			}
		}
		// Merge Related Links
		existingContext.getChildren().addAll(newContext.getChildren());
		removeDuplicateLinks(existingContext);
	}
	/**
	 * Filters out the duplicate related topics in a Context
	 */
	private void removeDuplicateLinks(Context context) {
		List links = context.getChildren();
		if (links == null || links.size() <= 0)
			return;
		List filtered = new ArrayList();
		for (Iterator it = links.iterator(); it.hasNext();) {
			IHelpResource topic1 = (IHelpResource) it.next();
			if (!isValidTopic(topic1))
				continue;
			boolean dup = false;
			for (int j = 0; j < filtered.size(); j++) {
				IHelpResource topic2 = (IHelpResource) filtered.get(j);
				if (!isValidTopic(topic2))
					continue;
				if (equalTopics(topic1, topic2)) {
					dup = true;
					break;
				}
			}
			if (!dup)
				filtered.add(topic1);
		}
		context.setChildren(filtered);
	}
	/**
	 * Checks if topic labels and href are not null and not empty strings
	 */
	private boolean isValidTopic(IHelpResource topic) {
		return topic != null
			&& topic.getHref() != null
			&& !"".equals(topic.getHref())
			&& topic.getLabel() != null
			&& !"".equals(topic.getLabel());
	}
	/**
	 * Check if two context topic are the same.
	 * They are considered the same if both labels and href are equal
	 */
	private boolean equalTopics(IHelpResource topic1, IHelpResource topic2) {
		return topic1.getHref().equals(topic2.getHref())
			&& topic1.getLabel().equals(topic2.getLabel());
	}
	private String getNLText(String pluginID, String text) {
		if (text == null)
			return text;
		// if description starts with %, need to translate.
		if (text.indexOf('%') == 0) {
			// strip off the leading %
			text = text.substring(1);
			// now translate
			text = ContextResources.getPluginString(pluginID, text);
		}
		return text;
	}
}