/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
package org.eclipse.help.internal.protocols;
import java.io.*;
import java.net.*;
import java.util.Date;

import org.eclipse.help.internal.util.Logger;
public class HelpURLConnection extends URLConnection {
	private HelpURL helpURL = null;
	/**
	 * Constructor for HelpURLConnection
	 */
	public HelpURLConnection(URL url) {
		super(url);
		helpURL = HelpURLFactory.createHelpURL(url.getFile());
		setDefaultUseCaches(helpURL.isCacheable());
		if (Logger.DEBUG)
			Logger.logDebugMessage("HelpURLConnection", "url= " + url);
	}
	/**
	 * @see URLConnection#connect()
	 */
	public void connect() throws IOException {
		return;
	}
	public InputStream getInputStream() throws IOException {
		// must override parent implementation, since it does nothing.
		InputStream is = helpURL.openStream();
		if (is == null) {
			throw new IOException("Resource not found.");
		}
		return is;
	}
	public String getContentType() {
		return helpURL.getContentType();
	}
	public long getExpiration() {
		Date now = new Date();
		return now.getTime() + 10000;
	}
}