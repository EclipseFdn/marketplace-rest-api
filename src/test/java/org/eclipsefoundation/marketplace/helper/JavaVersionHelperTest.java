/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Test class for {@linkplain JavaVersionHelper}
 * 
 * @author Martin Lowe
 */
@QuarkusTest
public class JavaVersionHelperTest {

	@Test
	public void testConvertToDBSafe() {
		Assertions.assertEquals("7", JavaVersionHelper.convertToDBSafe("1.7"));
		Assertions.assertEquals("7", JavaVersionHelper.convertToDBSafe("1.7.0_4"));
		Assertions.assertEquals("8", JavaVersionHelper.convertToDBSafe("1.8.45"));
		Assertions.assertEquals("8", JavaVersionHelper.convertToDBSafe("8.1.45"));
		Assertions.assertEquals("11", JavaVersionHelper.convertToDBSafe("11.1.2"));
		Assertions.assertEquals(null, JavaVersionHelper.convertToDBSafe("';DROP TABLES;"));
		Assertions.assertEquals(null, JavaVersionHelper.convertToDBSafe(" "));
		Assertions.assertEquals(null, JavaVersionHelper.convertToDBSafe(null));
	}
	@Test
	public void testConvertToDisplayValue() {
		Assertions.assertEquals("1.7", JavaVersionHelper.convertToDisplayValue("7"));
		Assertions.assertEquals("1.8", JavaVersionHelper.convertToDisplayValue("8"));
		Assertions.assertEquals("9", JavaVersionHelper.convertToDisplayValue("9"));
		Assertions.assertEquals("11", JavaVersionHelper.convertToDisplayValue("11"));
		Assertions.assertEquals(null, JavaVersionHelper.convertToDisplayValue("11.1"));
		Assertions.assertEquals(null, JavaVersionHelper.convertToDisplayValue("';DROP TABLES;"));
		Assertions.assertEquals(null, JavaVersionHelper.convertToDisplayValue(" "));
		Assertions.assertEquals(null, JavaVersionHelper.convertToDisplayValue(null));
	}
}
