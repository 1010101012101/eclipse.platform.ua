package org.eclipse.help.internal.util;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import org.eclipse.core.runtime.*;
import org.eclipse.help.internal.HelpPlugin;

/**
 * Help Log Listener is a sink for text messages
 * coming from Logger.
 */
class HelpLogListener implements ILogListener {
	PrintWriter log = null;
	public HelpLogListener() {
		try {
			// Initialize log file location here. Check to see
			// if we need to log to console. If not set, then it is
			// assumed that we are logging to file.

			String logToConsole =
				Platform.getDebugOption("org.eclipse.help/debug/consolelog");

			if ( (logToConsole != null)
				&& (logToConsole.equalsIgnoreCase("true"))) {
					System.out.println("Help System logging to console...");
					log = new PrintWriter(System.out, true);

			} else {

				IPath path =
					HelpPlugin.getDefault().getStateLocation().addTrailingSeparator().append(".log");
				File outputFile = path.toFile();
				log =
					new PrintWriter(
						new BufferedWriter(new FileWriter(outputFile.toString(), false)),
						true);

			}
		} catch (Exception e) {
			//  can not log anything.
			log = null;
		}

	}
	public void logging(IStatus status) {
		if (log == null)
			return;
		else {
			String date = new Date().toString();
			log.println(date);
			int severity = status.getSeverity();
			if (severity == IStatus.ERROR) {
				log.print("ERROR");
			} else
				if (severity == IStatus.WARNING) {
					log.print("WARNING");
				} else
					if (severity == IStatus.INFO) {
						log.print("INFO");
					} else
						if (severity == IStatus.OK) {
							log.print("DEBUG");
						}

			log.print("  ");
			log.print(status.getPlugin());
			// removed for now because we do not use Error codes.
			//log.print("  ");
			//log.print(status.getCode());
			log.print("  ");
			log.println(status.getMessage());
			if (status.getException() != null)
				status.getException().printStackTrace(log);
			if (status.isMultiStatus()) {
				IStatus[] children = status.getChildren();
				for (int i = 0; i < children.length; i++)
					loggingChild(children[i]);
			}
			log.println("---------------------------------------------------------------");
		}

	}
	public void logging(IStatus status, String plugin) {
		logging(status);
	}
	/**
	 * @param tmp org.eclipse.core.runtime.IStatus
	 */
	private void loggingChild(IStatus status) {
		if (log == null)
			return;
		else {
			int severity = status.getSeverity();
			log.print("\t");
			log.println(status.getMessage());
			if (status.getException() != null)
				status.getException().printStackTrace(log);
			if (status.isMultiStatus()) {
				IStatus[] children = status.getChildren();
				for (int i = 0; i < children.length; i++)
					logging(children[i]);
			}
		}

	}
	public void shutdown() {
		if (log == null)
			return;
		log.flush();
		log.close();
		log = null;
	}
}