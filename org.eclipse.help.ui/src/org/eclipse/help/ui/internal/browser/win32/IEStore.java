package org.eclipse.help.ui.internal.browser.win32;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
import java.io.*;
import java.util.Properties;
/**
 * Stored properties of wrapped IE browser.
 */
public class IEStore extends Properties {
	private File file = null;
	/**
	 * @param name the file;
	 */
	public IEStore(String name) {
		super();
		file = new File(name);
	}
	/**
	 * Restores contents of the Properties from a file.
	 * @return true if persistant data was read in
	 */
	public boolean restore() {
		InputStream in = null;
		boolean loaded = false;
		clear();
		// Test if we have a contribution file to start with
		// If this is a clean start, then we will not have a 
		// contribution file. return false.
		if (!file.exists())
			return loaded;
		try {
			in = new FileInputStream(file);
			super.load(in);
			loaded = true;
		} catch (IOException ioe) {
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException ioe10) {
				}
		}
		return loaded;
	}
	/**
	 * Saves contents to a file.
	 * @return true if operation was successful
	 */
	public boolean save() {
		OutputStream out = null;
		boolean ret = false;
		try {
			out = new FileOutputStream(file);
			super.store(out, "This is a generated file; do not edit.");
			ret = true;
		} catch (IOException ioe00) {
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ioe01) {
			}
		}
		return ret;
	}
	/**
	 * Helper function: gets boolean for a given name.
	 */
	public boolean getBoolean(String name) {
		String value = getProperty(name);
		if (value == null)
			return false;
		return Boolean.valueOf(value).booleanValue();
	}
	/**
	 * Helper function: gets int for a given name.
	 */
	public int getInt(String name) {
		String value = getProperty(name);
		if (value == null)
			return 0;
		int ival = 0;
		try {
			ival = Integer.parseInt(value);
		} catch (NumberFormatException e) {
		}
		return ival;
	}
	public String getString(String name) {
		String value = getProperty(name);
		if (value == null)
			return "";
		return value;
	}
}