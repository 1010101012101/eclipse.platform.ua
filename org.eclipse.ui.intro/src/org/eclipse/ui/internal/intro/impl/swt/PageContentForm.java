/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.intro.impl.swt;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.widgets.*;
import org.eclipse.ui.internal.intro.impl.*;
import org.eclipse.ui.internal.intro.impl.model.*;

/**
 * A Composite that represents the content of an Intro Page. It is swapped in
 * the categories page book in the PageForm class.
 */
public class PageContentForm implements IIntroConstants {

    private FormToolkit toolkit;
    private IntroModelRoot model;
    private PageStyleManager styleManager;

    /**
     *  
     */
    public PageContentForm(FormToolkit toolkit, IntroModelRoot modelRoot) {
        this.toolkit = toolkit;
        this.model = modelRoot;
    }

    /**
     * Create the form for the root page. Number of columns there is equal to
     * the number of links. Every image link does not cache a model object for
     * data retrieval..
     * 
     * @param pageBook
     */
    public void createPartControl(ScrolledPageBook contentPageBook,
            SharedStyleManager sharedStyleManager) {
        // create a page style manager.
        AbstractIntroPage page = model.getCurrentPage();
        styleManager = new PageStyleManager(page, sharedStyleManager
                .getProperties());

        String pageId = model.getCurrentPageId();

        // categoriesComposite has Table Layout with one col. Holds page
        // description and composite with all other children.
        Composite contentComposite = contentPageBook.createPage(pageId);
        //Util.highlight(contentComposite, SWT.COLOR_GREEN);
        TableWrapLayout layout = new TableWrapLayout();
        layout.topMargin = 15;
        layout.leftMargin = 15;
        layout.rightMargin = 15;
        layout.bottomMargin = 15;
        layout.verticalSpacing = 15;
        contentComposite.setLayout(layout);

        if (styleManager.getPageDescription() != null) {
            Label label = toolkit.createLabel(contentComposite, styleManager
                    .getPageDescription(), SWT.WRAP);
            label.setFont(PageStyleManager.getDefaultFont());
            TableWrapData td = new TableWrapData();
            td.align = TableWrapData.FILL;
            label.setLayoutData(td);
        }

        // Store the sub-title data for this composite from this page's
        // subtitle. Make sure you do this before creating the page content to
        // filter out page sub-title from content area.
        contentComposite.setData(PAGE_SUBTITLE, styleManager.getPageSubTitle());

        createPageChildren(page, contentComposite);

        // now we can clear all styleManagers, for memory performance.
        styleManager = null;
        sharedStyleManager = null;
    }

    private void createPageChildren(AbstractIntroPage page, Composite parent) {
        // setup page composite/layout
        PageWidgetFactory factory = new PageWidgetFactory(toolkit, styleManager);
        Composite pageComposite = createPageTableComposite(factory, parent);
        // now add all children
        AbstractIntroElement[] children = page.getChildren();
        for (int i = 0; i < children.length; i++)
            factory.createIntroElement(pageComposite, children[i]);

    }

    /**
     * Creates a composite with TableWrapLayout to hold all page children. The
     * default number of columns is 1.
     * 
     * @param parent
     * @return
     */
    private Composite createPageTableComposite(PageWidgetFactory factory,
            Composite parent) {
        Composite client = toolkit.createComposite(parent);
        TableWrapLayout layout = new TableWrapLayout();
        layout.topMargin = 0;
        layout.bottomMargin = 0;
        layout.leftMargin = 0;
        layout.rightMargin = 0;
        int numColumns = styleManager.getPageNumberOfColumns();
        layout.numColumns = numColumns == 0 ? 1 : numColumns;
        client.setLayout(layout);

        // parent has TableWrapLayout, and so update layout of this child.
        TableWrapData td = new TableWrapData();
        td.align = TableWrapData.FILL;
        td.grabHorizontal = true;
        client.setLayoutData(td);
        return client;
    }

}