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
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrInputField;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for interacting with SolrJ, wrapping commonly used functionality
 * like generating base queries from parameter maps for more convenient usage.
 * 
 * @author Martin Lowe
 *
 */
public final class SolrHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(SolrHelper.class);

	public static final int DEFAULT_PAGE_SIZE = 10;

	public static SolrQuery createQuery() {
		return createQuery(null, 0, DEFAULT_PAGE_SIZE);
	}

	public static SolrQuery createQuery(int page, int pageSize) {
		return createQuery(null, page, pageSize);
	}

	public static SolrQuery createQuery(String queryString) {
		return createQuery(queryString, 0, DEFAULT_PAGE_SIZE);
	}

	public static SolrQuery createQuery(String queryString, int page, int pageSize) {
		SolrQuery q = new SolrQuery(queryString);
		q.setStart((page - 1) * pageSize);
		q.setRows(pageSize);

		return q;
	}

	/**
	 * Builds a Solr query based off of a map of parameters retrieved from an HTTP
	 * request.
	 * 
	 * @param params map containing parameter values for the Solr query
	 * @return a Solr query populated by values from the parameter map
	 */
	public static SolrQuery createQuery(Map<String, List<String>> params) {
		// retrieve the necessary values from the param map

		String queryString = ParamHelper.getFirstParam(params, UrlParameterNames.SOLR_QUERY_STRING);
		String pageVal = ParamHelper.getFirstParam(params, UrlParameterNames.SOLR_CURRENT_PAGE);
		String pageSizeVal = ParamHelper.getFirstParam(params, UrlParameterNames.SOLR_PAGE_SIZE);

		// convert the values if they are numeric, or else use defaults
		Integer page = StringUtils.isNumeric(pageVal) ? Integer.valueOf(pageVal) : null;
		Integer pageSize = StringUtils.isNumeric(pageSizeVal) ? Integer.valueOf(pageSizeVal) : DEFAULT_PAGE_SIZE;

		// create the query object based on the querystring
		SolrQuery q = new SolrQuery(queryString);

		// add params for page size and count
		if (page != null) {
			q.setStart((page - 1) * pageSize);
		}
		q.setRows(pageSize);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Generated query: {}", q.toQueryString());
		}

		// return the complete solrquery object
		return q;
	}

	/**
	 * Ensures that the object passed in is set as a list to the consumer, as Solr
	 * will interchangeably use lists for keys that can have multiple values at
	 * once.
	 * 
	 * @param <T>   the type of object contained in the list
	 * @param value the value to ensure is a list, or to convert if is singular
	 * @param c     consumer for setting the value once it is ensured to be a list
	 * @throws ClassCastException if the type of value consumed is not of type T
	 */
	@SuppressWarnings("unchecked")
	public static <T> void setListField(Object value, Consumer<List<T>> c) {
		// if value is already a list, set it back immediately and return
		if (value instanceof List) {
			c.accept((List<T>) value);
			return;
		}

		// creates a new list, adds the object value, and sets it back
		List<T> l = new ArrayList<>();
		l.add((T) value);
		c.accept(l);
	}

	/**
	 * Handles repetitive operation of generating a SolrInputField with the given
	 * key and value, mapping to the passed key in the map.
	 * 
	 * @param key    the key for the SolrInputField
	 * @param value  value for the SolrInputField
	 * @param fields the map containing the SolrInputFields already generated
	 */
	public static void addInputField(String key, Object value, Map<String, SolrInputField> fields) {
		// Generate the new input field with the set name
		SolrInputField field = new SolrInputField(key);
		// boost is used in promoting new content. It is left as default in this call
		// (1) to not affect ordering of output
		field.setValue(value, field.getBoost());

		// add the new input value to the map of fields
		fields.put(key, field);
	}

	/**
	 * Hide the constructor as helpers should not be instantiated.
	 */
	private SolrHelper() {

	}
}
