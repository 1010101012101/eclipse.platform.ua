/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.ui.internal.browser;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.help.internal.base.HelpBasePlugin;
import org.eclipse.help.internal.browser.*;
import org.eclipse.help.ui.internal.*;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
/**
 * Preference page for selecting default web browser.
 */
public class BrowsersPreferencePage extends PreferencePage
		implements
			IWorkbenchPreferencePage {
	private Button alwaysExternal;
	private Button[] externalBrowsers;
	private Button customBrowserRadio;
	private Label customBrowserPathLabel;
	private Text customBrowserPath;
	private Button customBrowserBrowse;
	/**
	 * Creates preference page controls on demand.
	 * 
	 * @param parent
	 *            the parent for the preference page
	 */
	protected Control createContents(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IHelpUIConstants.PREF_PAGE_BROWSERS);
		Composite mainComposite = new Composite(parent, SWT.NULL);
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		mainComposite.setLayoutData(data);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		mainComposite.setLayout(layout);
		Label description = new Label(mainComposite, SWT.NULL);
		description.setText(HelpUIResources.getString("select_browser")); //$NON-NLS-1$
		createSpacer(mainComposite);
		if (BrowserManager.getInstance().isEmbeddedBrowserPresent()) {
			alwaysExternal = new Button(mainComposite, SWT.CHECK);
			alwaysExternal
					.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL));
			alwaysExternal.setText(HelpUIResources
					.getString("use_only_external_browser")); //$NON-NLS-1$
			alwaysExternal.setSelection(HelpBasePlugin.getDefault()
					.getPluginPreferences().getBoolean(
							BrowserManager.ALWAYS_EXTERNAL_BROWSER_KEY));
			createSpacer(mainComposite);
		}
		Label tableDescription = new Label(mainComposite, SWT.NULL);
		tableDescription.setText(HelpUIResources.getString("current_browser")); //$NON-NLS-1$
		//data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		//description.setLayoutData(data);
		Color bgColor = parent.getDisplay().getSystemColor(
				SWT.COLOR_LIST_BACKGROUND);
		Color fgColor = parent.getDisplay().getSystemColor(
				SWT.COLOR_LIST_FOREGROUND);
		final ScrolledComposite externalBrowsersScrollable = new ScrolledComposite(
				mainComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = convertHeightInCharsToPixels(2);
		externalBrowsersScrollable.setLayoutData(gd);
		externalBrowsersScrollable.setBackground(bgColor);
		externalBrowsersScrollable.setForeground(fgColor);
		Composite externalBrowsersComposite = new Composite(
				externalBrowsersScrollable, SWT.NONE);
		externalBrowsersScrollable.setContent(externalBrowsersComposite);
		GridLayout layout2 = new GridLayout();
		externalBrowsersComposite.setLayout(layout2);
		externalBrowsersComposite.setBackground(bgColor);
		externalBrowsersComposite.setForeground(fgColor);
		BrowserDescriptor[] descriptors = BrowserManager.getInstance()
				.getBrowserDescriptors();
		externalBrowsers = new Button[descriptors.length];
		for (int i = 0; i < descriptors.length; i++) {
			Button radio = new Button(externalBrowsersComposite, SWT.RADIO);
			org.eclipse.jface.dialogs.Dialog.applyDialogFont(radio);
			radio.setBackground(bgColor);
			radio.setForeground(fgColor);
			radio.setText(descriptors[i].getLabel());
			if (BrowserManager.getInstance().getCurrentBrowserID().equals(
					descriptors[i].getID()))
				radio.setSelection(true);
			else
				radio.setSelection(false);
			radio.setData(descriptors[i]);
			externalBrowsers[i] = radio;
			if (BrowserManager.BROWSER_ID_CUSTOM.equals(descriptors[i].getID())) {
				customBrowserRadio = radio;
				radio.addSelectionListener(new SelectionListener() {
					public void widgetSelected(SelectionEvent selEvent) {
						setCustomBrowserPathEnabled();
					}
					public void widgetDefaultSelected(SelectionEvent selEvent) {
						widgetSelected(selEvent);
					}
				});
			}
		}
		externalBrowsersComposite.setSize(externalBrowsersComposite
				.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		createCustomBrowserPathPart(mainComposite);
		org.eclipse.jface.dialogs.Dialog.applyDialogFont(mainComposite);
		createSpacer(mainComposite);
		return mainComposite;
	}
	private void createCustomBrowserPathPart(Composite mainComposite) {
		Font font = mainComposite.getFont();
		// vertical space
		new Label(mainComposite, SWT.NULL);
		Composite bPathComposite = new Composite(mainComposite, SWT.NULL);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(bPathComposite,
				IHelpUIConstants.PREF_PAGE_CUSTOM_BROWSER_PATH);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 3;
		bPathComposite.setLayout(layout);
		bPathComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		customBrowserPathLabel = new Label(bPathComposite, SWT.LEFT);
		customBrowserPathLabel.setFont(font);
		customBrowserPathLabel.setText(HelpUIResources
				.getString("CustomBrowserPreferencePage.Program")); //$NON-NLS-1$
		customBrowserPath = new Text(bPathComposite, SWT.BORDER);
		customBrowserPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		customBrowserPath.setFont(font);
		customBrowserPath.setText(HelpBasePlugin.getDefault()
				.getPluginPreferences().getString(
						CustomBrowser.CUSTOM_BROWSER_PATH_KEY));
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalAlignment = GridData.FILL;
		data.widthHint = convertWidthInCharsToPixels(10);
		customBrowserPath.setLayoutData(data);
		customBrowserBrowse = new Button(bPathComposite, SWT.NONE);
		customBrowserBrowse.setFont(font);
		customBrowserBrowse.setText(HelpUIResources
				.getString("CustomBrowserPreferencePage.Browse")); //$NON-NLS-1$
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, customBrowserBrowse.computeSize(
				SWT.DEFAULT, SWT.DEFAULT, true).x);
		customBrowserBrowse.setLayoutData(data);
		customBrowserBrowse.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent event) {
			}
			public void widgetSelected(SelectionEvent event) {
				FileDialog d = new FileDialog(getShell());
				d.setText(HelpUIResources
						.getString("CustomBrowserPreferencePage.Details")); //$NON-NLS-1$
				String file = d.open();
				if (file != null) {
					customBrowserPath.setText("\"" + file + "\" %1"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		});
		setCustomBrowserPathEnabled();
	}
	/**
	 * @see IWorkbenchPreferencePage
	 */
	public void init(IWorkbench workbench) {
	}
	/**
	 * Performs special processing when this page's Defaults button has been
	 * pressed.
	 * <p>
	 * This is a framework hook method for sublcasses to do special things when
	 * the Defaults button has been pressed. Subclasses may override, but should
	 * call <code>super.performDefaults</code>.
	 * </p>
	 */
	protected void performDefaults() {
		String defaultBrowserID = BrowserManager.getInstance()
				.getDefaultBrowserID();
		for (int i = 0; i < externalBrowsers.length; i++) {
			BrowserDescriptor descriptor = (BrowserDescriptor) externalBrowsers[i]
					.getData();
			externalBrowsers[i]
					.setSelection(descriptor.getID() == defaultBrowserID);
		}
		customBrowserPath.setText(HelpBasePlugin.getDefault()
				.getPluginPreferences().getDefaultString(
						CustomBrowser.CUSTOM_BROWSER_PATH_KEY));
		setCustomBrowserPathEnabled();
		if (alwaysExternal != null) {
			alwaysExternal.setSelection(HelpBasePlugin.getDefault()
					.getPluginPreferences().getDefaultBoolean(
							BrowserManager.ALWAYS_EXTERNAL_BROWSER_KEY));
		}
		super.performDefaults();
	}
	/**
	 * @see IPreferencePage
	 */
	public boolean performOk() {
		Preferences pref = HelpBasePlugin.getDefault().getPluginPreferences();
		for (int i = 0; i < externalBrowsers.length; i++) {
			if (externalBrowsers[i].getSelection()) {
				// set new current browser
				String browserID = ((BrowserDescriptor) externalBrowsers[i]
						.getData()).getID();
				BrowserManager.getInstance().setCurrentBrowserID(browserID);
				// save id in help preferences
				pref.setValue(BrowserManager.DEFAULT_BROWSER_ID_KEY, browserID);
				break;
			}
		}
		pref.setValue(CustomBrowser.CUSTOM_BROWSER_PATH_KEY, customBrowserPath
				.getText());
		if (alwaysExternal != null) {
			pref.setValue(BrowserManager.ALWAYS_EXTERNAL_BROWSER_KEY,
					alwaysExternal.getSelection());
			BrowserManager.getInstance().setAlwaysUseExternal(
					alwaysExternal.getSelection());
		}
		HelpBasePlugin.getDefault().savePluginPreferences();
		return true;
	}
	/**
	 * Creates a horizontal spacer line that fills the width of its container.
	 * 
	 * @param parent
	 *            the parent control
	 */
	private void createSpacer(Composite parent) {
		Label spacer = new Label(parent, SWT.NONE);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.BEGINNING;
		spacer.setLayoutData(data);
	}
	private void setCustomBrowserPathEnabled() {
		boolean enabled = customBrowserRadio.getSelection();
		customBrowserPathLabel.setEnabled(enabled);
		customBrowserPath.setEnabled(enabled);
		customBrowserBrowse.setEnabled(enabled);
	}
}
