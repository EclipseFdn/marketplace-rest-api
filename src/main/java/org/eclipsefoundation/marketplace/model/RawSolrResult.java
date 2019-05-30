/*
 * Copyright (C) 2019 Eclipse Foundation and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*/
package org.eclipsefoundation.marketplace.model;

import java.util.HashMap;
import java.util.Map;

public class RawSolrResult {
	private Map<String, Object> fields;

	/**
	 * Default constructor, creates the fields map that will hold the search results
	 * in a way that is easily serializable by JAXB.
	 */
	public RawSolrResult() {
		this.fields = new HashMap<>();
	}

	/**
	 * Set the fields to the passed map
	 * 
	 * @param fields the search result fields to set internally
	 */
	public void setFields(Map<String, Object> fields) {
		this.fields = new HashMap<>(fields);
	}

	/**
	 * Updates a single field in the stored data with the given value.
	 * 
	 * @param key   the key of the value to insert/update
	 * @param value the value to insert
	 */
	public void setField(String key, Object value) {
		this.fields.put(key, value);
	}

	/**
	 * Return the map of search results.
	 * 
	 * @return map containing search results
	 */
	public Map<String, Object> getFields() {
		return new HashMap<>(fields);
	}

	/**
	 * Get a single field value from the search result.
	 * 
	 * @param key the key of the value to retrieve
	 * @return the value for the given key, or null if absent.
	 */
	public Object getField(String key) {
		return fields.get(key);
	}
}
