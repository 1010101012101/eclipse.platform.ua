package org.eclipse.help.internal.util;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */

import org.eclipse.core.runtime.*;

/**
 * Generic help system exception.
 * NOTE: we should better start using it...
 */
public class HelpException extends CoreException {

	/**
	 * HelpException constructor comment.
	 * @param plugin com.ibm.itp.core.api.plugins.IPluginDescriptor
	 * @param status com.ibm.itp.core.api.resources.IStatus
	 */
	public HelpException(IStatus status) {
		super(status);
	}
}
