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

import java.util.*;

import org.eclipse.core.runtime.*;

/**
 * Base class for all Intro pages., inlcuding HomePage.
 */
public abstract class AbstractIntroPage extends AbstractIntroContainer {

    protected static final String PAGE_ELEMENT = "page";

    private static final String TITLE_ATTRIBUTE = "title";
    private static final String STYLE_ATTRIBUTE = "style";
    private static final String ALT_STYLE_ATTRIBUTE = "alt-style";

    private String title;
    private String style;
    private String altStyle;

    /**
     * The vectors to hold all inhertied styles and alt styles from included
     * elements. They are lazily created when children are resolved (ie:
     * includes are resolved) OR when extensions are resolved and styles need
     * to be added to the target page.
     * <p>
     * Style Rules:
     * <ul>
     * <li>For includes, merge-style controls wether or not the enclosing page
     * inherits the styles of the target.
     * <li>If a page is including a shared div, merging target styles into
     * this page is ignored. Shared divs do not have styles.</li>
     * <li>For extensions, if the style or alt-style is not defined, that
     * means that no style inheritence is needed, and the style of the target
     * page are not updated.
     * <li>If an extension is extending a shared div, merging the styles of
     * this extension into the target is ignored. Shared divs do not have
     * styles.</li>
     * </ul>
     */
    private Vector styles;
    private Vector altStyles;

    /**
     *  
     */
    AbstractIntroPage(IConfigurationElement element) {
        super(element);
        title = element.getAttribute(TITLE_ATTRIBUTE);
        style = element.getAttribute(STYLE_ATTRIBUTE);
        altStyle = element.getAttribute(ALT_STYLE_ATTRIBUTE);

        // Resolve style. The ALT style need not be resolved.
        style = IntroModelRoot.getPluginLocation(style, element);
    }

    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return Returns the style.
     */
    public String getStyle() {
        return style;
    }

    /**
     * @return Returns the alt_style.
     */
    public String getAltStyle() {
        return altStyle;
    }

    /**
     * Gets all the inherited styles of this page. Styles can be inherited from
     * includes or from configExtensions.
     * <p>
     * Note: this call needs to get all the children of this page, and so it
     * will resolve this page. might be expensive.
     * 
     * @return Returns all the inherited styles of this page. Returns an empty
     *         array if page is not expandable or does not have inherited
     *         styles.
     */
    public String[] getStyles() {
        // call get children first to resolve includes and populate styles
        // vector. Resolving children will initialize the style vestors.
        getChildren();
        String[] stylesArray = new String[styles.size()];
        styles.copyInto(stylesArray);
        return stylesArray;
    }

    /**
     * Gets all the inherited alt-styles of this page (ie: styles for the SWT
     * presentation). Note: this call needs to get all the children of this
     * page, and so its will resolve this page. might be expensive.
     * 
     * @return Returns all the inherited styles of this page. Returns an empty
     *         array if page is not expandable, does not have any includes, or
     *         has includes that do not merge styles.
     */
    public String[] getAltStyles() {
        // call get children first to resolve includes and populate styles
        // vector. Resolving children will initialize the style vestors.
        getChildren();
        String[] altStylesArray = new String[altStyles.size()];
        altStyles.copyInto(altStylesArray);
        return altStylesArray;
    }

    /**
     * Adds the given style to the list. Style is not added if it already
     * exists in the list.
     * 
     * @param style
     */
    protected void addStyle(String style) {
        initStylesVectors();
        if (styles.contains(style))
            return;
        styles.add(style);
    }

    /**
     * Adds the given style to the list.Style is not added if it already exists
     * in the list.
     * 
     * @param altStyle
     */
    protected void addAltStyle(String altStyle) {
        initStylesVectors();
        if (altStyles.contains(altStyle))
            return;
        altStyles.add(altStyle);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.intro.internal.model.IntroElement#getType()
     */
    public int getType() {
        return IntroElement.ABSTRACT_PAGE;
    }

    /*
     * Override parent behavior to lazily initialize styles vectors. This will
     * only be called once, if resolved == false.
     * 
     * @see org.eclipse.ui.intro.internal.model.AbstractIntroContainer#resolveChildren()
     */
    protected void resolveChildren() {
        initStylesVectors();
        super.resolveChildren();
    }

    private void initStylesVectors() {
        if (styles == null)
            // delay creation until needed.
            styles = new Vector();
        if (altStyles == null)
            // delay creation until needed.
            altStyles = new Vector();
    }

    /**
     * Override parent behavior to add support for HEAD element in pages only,
     * and not in divs.
     * 
     * @see org.eclipse.ui.intro.internal.model.AbstractIntroContainer#getModelChild(org.eclipse.core.runtime.IConfigurationElement)
     */
    protected IntroElement getModelChild(IConfigurationElement childElement) {
        IntroElement child = null;
        if (childElement.getName().equalsIgnoreCase(IntroHead.HEAD_ELEMENT)) {
            child = new IntroHead(childElement);
            return child;
        }
        return super.getModelChild(childElement);
    }

    /**
     * Returns all head contributions in this page. There can be more than one
     * head contribution in the page;
     * 
     * @return
     */
    public IntroHead[] getHTMLHeads() {
        return (IntroHead[]) getChildrenOfType(IntroElement.HEAD);
    }

    // THESE METHODS MIGHT BE REMOVED. ADDED HERE FOR BACKWARD COMPATIBILITY.
    public IntroLink[] getLinks() {
        return (IntroLink[]) getChildrenOfType(IntroElement.LINK);
    }

    public IntroDiv[] getDivs() {
        return (IntroDiv[]) getChildrenOfType(IntroElement.DIV);
    }

    public String getText() {
        IntroText[] texts = (IntroText[]) getChildrenOfType(IntroElement.TEXT);
        if (texts.length == 0)
            return null;
        return texts[0].getText();
    }

}
