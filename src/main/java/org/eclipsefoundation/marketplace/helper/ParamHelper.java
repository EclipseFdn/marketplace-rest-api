/*
 * Copyright (C) 2019 Eclipse Foundation and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*/
package org.eclipsefoundation.marketplace.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ParamHelper {

	/**
	 * Retrieves the first value set in a list from the map for a given key.
	 * 
	 * @param params the parameter map containing the value
	 * @param key    the key to retrieve the value for
	 * @return the first value set in the parameter map for the given key, or null
	 *         if absent.
	 */
	public static String getFirstParam(Map<String, List<String>> params, String key) {
		List<String> vals = params.get(key);
		if (vals == null || vals.isEmpty()) {
			return null;
		}
		return vals.get(0);
	}

	/**
	 * Sets the given value for the given key, overriding existing values. Uses a
	 * small load factor for the array to keep the memory footprint smaller, but the
	 * object churn reasonable should more items need to be added.
	 * 
	 * @param params map containing parameters to update
	 * @param key    string key to set the value for
	 * @param value  the value to set for the key
	 */
	public static void setParam(Map<String, List<String>> params, String key, String value) {
		List<String> param = new ArrayList<>(4);
		param.add(value);
		params.put(key, param);
	}

	/**
	 * Adds the given value for the given key, preserving previous values if they
	 * exist.
	 * 
	 * @param params map containing parameters to update
	 * @param key    string key to add the value to
	 * @param value  the value to add to the key
	 */
	public static void addParam(Map<String, List<String>> params, String key, String value) {
		List<String> param = params.get(key);
		if (param == null) {
			setParam(params, key, value);
		} else {
			param.add(value);
		}
	}

	/**
	 * Hide the constructor as its not needed for helper classes
	 */
	private ParamHelper() {

	}
}
