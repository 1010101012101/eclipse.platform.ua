/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
package org.eclipse.help.internal.toc;
import java.util.*;
import org.eclipse.help.*;
import org.xml.sax.Attributes;
/** 
 * Root of navigation TocFile
 * Can be linked with other Toc objects.
 */
public class Toc extends TocNode implements IToc {
	private String link_to;
	private String href;
	private String label;
	private TocFile tocFile;
	private ITopic[] topicArray;
	/**
	 * Map of all topics in a TOC for fast lookup by href
	 */
	private Map topicMap;
	/**
	 * Constructor.  Used when parsing help contributions.
	 */
	protected Toc(TocFile tocFile, Attributes attrs) {
		if (attrs == null)
			return;
		this.tocFile = tocFile;
		this.label = attrs.getValue("label");
		this.link_to = attrs.getValue("link_to");
		this.link_to = HrefUtil.normalizeHref(tocFile.getPluginID(), link_to);
		this.href = HrefUtil.normalizeHref(tocFile.getPluginID(), tocFile.getHref());
	}
	/**
	 * Implements abstract method.
	 */
	public void build(TocBuilder builder) {
		builder.buildToc(this);
	}
	/**
	 * Returns the toc file. 
	 * Returns null when the topic is read from a temp file.
	 */
	public TocFile getTocFile() {
		return tocFile;
	}
	/**
	 * Gets the link_to
	 * @return Returns a String
	 */
	protected String getLink_to() {
		return link_to;
	}
	/**
	 * Gets the href
	 * @return Returns a String
	 */
	public String getHref() {
		return href;
	}
	public String getLabel() {
		return label;
	}
	/**
	 * Returns a topic with the specified href.
	 * <br> It is possible that multiple tocs have 
	 * the same href, in which case there is no guarantee 
	 * which one is returned.
	 * @param href The topic's href value.
	 */
	public ITopic getTopic(String href) {
		if (topicMap == null) {
			// traverse TOC and fill in the topicMap
			topicMap = new HashMap();
			Stack stack = new Stack();
			ITopic[] topics = getTopics();
			for (int i = 0; i < topics.length; i++)
				stack.push(topics[i]);
			while (!stack.isEmpty()) {
				ITopic topic = (ITopic) stack.pop();
				if (topic != null) {
					String topicHref = topic.getHref();
					if (topicHref != null) {
						topicMap.put(topicHref, topic);
					}
					ITopic[] subtopics = topic.getSubtopics();
					for (int i = 0; i < subtopics.length; i++)
						stack.push(subtopics[i]);
				}
			}
		}
		return (ITopic) topicMap.get(href);
	}
	/**
	 * Note: assumes the toc has been built....
	 * @return ITopic list
	 */
	public ITopic[] getTopics() {
		if (topicArray == null) {
			List topics = getChildTopics();
			topicArray = new ITopic[topics.size()];
			topics.toArray(topicArray);
		}
		return topicArray;
	}
	/**
	 * Used by debugger
	 */
	public String toString() {
		return href != null ? href : super.toString();
	}
}