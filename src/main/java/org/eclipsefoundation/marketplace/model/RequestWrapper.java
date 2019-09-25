/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.core.ResteasyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class for query parameter functionality, wrapping a Map of String to
 * collection of String values. This class should be used anywhere where query
 * parameters are used to control access and prevent bad data from being
 * entered.
 * 
 * @author Martin Lowe
 */
@RequestScoped
public class RequestWrapper {
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestWrapper.class);
	private static final String EMPTY_KEY_MESSAGE = "Key must not be null or blank";

	private Map<String, List<String>> params;
	
	private UriInfo uriInfo;
	private HttpServletRequest request;

	/**
	 * Generates a wrapper around the 
	 * @param uriInfo
	 */
	RequestWrapper() {
		this.uriInfo = ResteasyContext.getContextData(UriInfo.class);
		this.request = ResteasyContext.getContextData(HttpServletRequest.class);
	}
	
	/**
	 * Retrieves the first value set in a list from the map for a given key.
	 * 
	 * @param params the parameter map containing the value
	 * @param key    the key to retrieve the value for
	 * @return the first value set in the parameter map for the given key, or null
	 *         if absent.
	 */
	public Optional<String> getFirstParam(String key) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException(EMPTY_KEY_MESSAGE);
		}

		List<String> vals = getParams().get(key);
		if (vals == null || vals.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(vals.get(0));
	}

	/**
	 * Retrieves the value list from the map for a given key.
	 * 
	 * @param params the parameter map containing the values
	 * @param key    the key to retrieve the values for
	 * @return the value list for the given key if it exists, or an empty collection
	 *         if none exists.
	 */
	public List<String> getParams(String key) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException(EMPTY_KEY_MESSAGE);
		}

		List<String> vals = getParams().get(key);
		if (vals == null || vals.isEmpty()) {
			return Collections.emptyList();
		}
		return vals;
	}
	
	/**
	 * Adds the given value for the given key, preserving previous values if they
	 * exist.
	 * 
	 * @param params map containing parameters to update
	 * @param key    string key to add the value to, must not be null
	 * @param value  the value to add to the key
	 */
	public void addParam(String key, String value) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException(EMPTY_KEY_MESSAGE);
		}
		Objects.requireNonNull(value);
		getParams().computeIfAbsent(key, k -> new ArrayList<>()).add(value);
	}

	/**
	 * Returns this QueryParams object as a Map of param values indexed by the param name.
	 * 
	 * @return a copy of the internal param map
	 */
	public Map<String, List<String>> asMap() {
		return new HashMap<>(getParams());
	}
	
	private Map<String, List<String>> getParams() {
		if (params == null) {
			params = new HashMap<>();
			if (uriInfo != null) {
				params.putAll(uriInfo.getQueryParameters());
			}
		}
		return this.params;
	}
	
	/**
	 * Returns the endpoint for the current call
	 * @return
	 */
	public String getEndpoint() {
		return uriInfo.getPath();
	}
	
	public Object getAttribute(String key) {
		return request.getAttribute(key);
	}
}
