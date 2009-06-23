/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ua.tests.help.toc;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.help.internal.validation.TocValidator;
import org.eclipse.help.internal.validation.TocValidator.BrokenLink;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TocLinkChecker extends TestCase {
	
	private final class ReferenceFilter extends TocValidator.Filter {
		public boolean isIncluded(String href) {
			return href.startsWith("reference");
		}
	}
	
	private final class NonReferenceFilter extends TocValidator.Filter {
		public boolean isIncluded(String href) {
			return !href.startsWith("reference");
		}
	}
	
	private final class NonReferenceNonSampleFilter extends TocValidator.Filter {
		public boolean isIncluded(String href) {
			return !href.startsWith("reference") && !href.startsWith("samples");
		}
	}
	
	private final class ReferenceOrSampleFilter extends TocValidator.Filter {
		public boolean isIncluded(String href) {
			return href.startsWith("reference") || href.startsWith("samples");
		}
	}

	private static final String[] PLATFORM_USER = {"/org.eclipse.platform.doc.user/toc.xml"};
	private static final String[] PLATFORM_ISV = {"/org.eclipse.platform.doc.isv/toc.xml"};
	private static final String[] PDE_USER = {"/org.eclipse.pde.doc.user/toc.xml"};
	private static final String[] JDT_USER = {"/org.eclipse.jdt.doc.user/toc.xml"};
	private static final String[] JDT_ISV = {"/org.eclipse.jdt.doc.isv/toc.xml"};
	
	public static Test suite() {
		return new TestSuite(TocLinkChecker.class);
	}
	
	public void testPlatformUser() throws Exception {
		ArrayList failures = TocValidator.validate(PLATFORM_USER);
		doAssert(failures);
	}

	public void testPlatformIsvStatic() throws Exception {
		ArrayList failures = TocValidator.filteredValidate(PLATFORM_ISV, new NonReferenceNonSampleFilter());
		doAssert(failures);
	}
	
	public void testPlatformIsvGenerated() throws Exception {
		ArrayList failures = TocValidator.filteredValidate(PLATFORM_ISV, new ReferenceOrSampleFilter());
		doAssert(failures);
	}

	public void testPdeUserStatic() throws Exception {
		ArrayList failures = TocValidator.filteredValidate(PDE_USER, new NonReferenceFilter());
		doAssert(failures);
	}
	
	public void testPdeUserGenerated() throws Exception {
		ArrayList failures = TocValidator.filteredValidate(PDE_USER, new ReferenceFilter());
		doAssert(failures);
	}
	
	public void testJdtUser() throws Exception {
		ArrayList failures = TocValidator.validate(JDT_USER);
		doAssert(failures);
	}
	
	public void testJdtIsvStatic() throws Exception {
		ArrayList failures = TocValidator.filteredValidate(JDT_ISV, new NonReferenceFilter());
		doAssert(failures);
	}
	
	public void testJdtIsvGenerated() throws Exception {
		ArrayList failures = TocValidator.filteredValidate(JDT_ISV, new ReferenceFilter());
		doAssert(failures);
	}
	
	private void doAssert(List failures) {
		StringBuffer message = new StringBuffer();
		for (int i = 0; i < failures.size(); i++) {
			BrokenLink link = (BrokenLink)failures.get(i);
			message.append("Invalid link in \"" + link.getTocID() + "\": " + link.getHref());
		}
		Assert.assertTrue(message.toString(), failures.isEmpty());
	}
}
