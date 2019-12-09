/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Used to handle the conversion of Java versions to and from storage mediums.
 * 
 * @author Martin Lowe
 *
 */
public class JavaVersionHelper {
	// checks retrieves both possible major versions in Java-version strings
	private static final Pattern MAJOR_VERSION_CHECK = Pattern.compile("^(\\d+)\\.(\\d+)(?:\\.[\\d_]+){0,}");

	/**
	 * Converts value to strip out parts of Java version string that cause issues
	 * with natural number sorting in databases.
	 * 
	 * @param val the java version string to convert (e.g. 1.7, 10.0.1)
	 * @return modified and more sortable version string, or null if it doesn't
	 *         match Java version strings
	 */
	public static final String convertToDBSafe(String val) {
		String out = val;
		if (StringUtils.isBlank(out)) {
			return null;
		}
		// strip out the major Java version as it isn't useful.
		Matcher m = MAJOR_VERSION_CHECK.matcher(out);
		if (m.matches()) {
			String majorVersion = m.group(1);
			// if we have a major version of 1, this indicates old school java and we should
			// use the second group as the product version
			if ("1".equals(majorVersion)) {
				out = m.group(2);
			} else {
				out = majorVersion;
			}
			return out;
		}
		return null;
	}

	/**
	 * Converts the sortable DB value for the Java version to something more in line
	 * with what is expected by the user.
	 * 
	 * @param val the sortable stored version string
	 * @return display value of the sortable Java version
	 */
	public static final String convertToDisplayValue(String val) {
		String out = val;
		if (StringUtils.isBlank(out) || !StringUtils.isNumeric(val)) {
			return null;
		}
		// check that the is 5,6,7,8 before adding a "1." in front
		if (val.equals("5") || val.equals("6") || val.equals("7") || val.equals("8")) {
			// add major version of 1 to string
			out = "1." + val;
		}
		return out;
	}

	private JavaVersionHelper() {
	}
}
