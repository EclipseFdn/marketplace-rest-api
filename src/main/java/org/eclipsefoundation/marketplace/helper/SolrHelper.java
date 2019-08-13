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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.common.SolrInputField;
import org.eclipsefoundation.marketplace.model.QueryParams;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for interacting with SolrJ, wrapping commonly used functionality
 * like generating base queries from parameter maps for more convenient usage.
 * 
 * @author Martin Lowe
 */
public final class SolrHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(SolrHelper.class);

	/**
	 * Default number on entries to include in a response
	 */
	public static final int DEFAULT_PAGE_SIZE = 10;

	/**
	 * Creates a simple SolrQuery object with default values set to be overridden
	 * later if needed.
	 * 
	 * @return a basic SolrQuery object with no query string that retrieves the
	 *         default number of documents ({@link SolrHelper#DEFAULT_PAGE_SIZE}).
	 */
	public static SolrQuery createQuery() {
		return createQuery(null, 1, DEFAULT_PAGE_SIZE);
	}

	/**
	 * Returns a query object with the given basic parameters.
	 * 
	 * @param queryString the Solr query string to use to retrieve data
	 * @param page        the page to retrieve documents for
	 * @param pageSize    the number of documents per page of results
	 * @return a query populated with the given values
	 */
	public static SolrQuery createQuery(String queryString, int page, int pageSize) {
		SolrQuery q = new SolrQuery(queryString);
		q.setStart(getDocumentStart(page, pageSize));
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
	public static SolrQuery createQuery(QueryParams params) {
		// retrieve the necessary values from the param map
		List<String> queryStrings = params.getParams(UrlParameterNames.SOLR_QUERY_STRING);
		Optional<String> pageVal = params.getFirstParam(UrlParameterNames.SOLR_CURRENT_PAGE);
		Optional<String> pageSizeVal = params.getFirstParam(UrlParameterNames.SOLR_PAGE_SIZE);
		Optional<String> sortVal = params.getFirstParam(UrlParameterNames.SOLR_SORT);

		// convert the values if they are numeric, or else use defaults
		Integer page = 0;
		if (pageVal.isPresent() && StringUtils.isNumeric(pageVal.get())) {
			page = Integer.valueOf(pageVal.get());
		}
		Integer pageSize = DEFAULT_PAGE_SIZE;
		if (pageSizeVal.isPresent() && StringUtils.isNumeric(pageSizeVal.get())) {
			pageSize = Integer.valueOf(pageSizeVal.get());
		}

		// create the query object based on the querystring
		SolrQuery q = new SolrQuery(StringUtils.join(queryStrings, " and "));

		// add params for page size and count
		q.setStart(getDocumentStart(page, pageSize));
		q.setRows(pageSize);
		
		// set sort clauses if present
		if (sortVal.isPresent() && StringUtils.isNotBlank(sortVal.get())) {
			q.setSorts(getSortClauses(sortVal.get()));
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Generated query: {}", q.toQueryString());
		}

		// return the complete solr query object
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
	 * Calculates the document start index given a positive, non-zero page and page
	 * size.
	 * 
	 * @param page     the page to start from
	 * @param pageSize the size of result pages
	 * @return the start index for the given page and page size.
	 */
	public static int getDocumentStart(int page, int pageSize) {
		if (page < 0) {
			throw new IllegalArgumentException("Cannot generate a document start point with a page less than 0");
		}
		if (pageSize < 1) {
			throw new IllegalArgumentException("Cannot generate a document start point with a pageSize less than 1");
		}

		return Math.max(0, page - 1) * pageSize;
	}

	/**
	 * <p>
	 * Converts a comma-delimited list of string sort clauses into a form usable by
	 * Solr. This method takes a string in the following form:
	 * </p>
	 * <code>
	 * [indexed_field_name] [asc|desc], ...
	 * </code>
	 * 
	 * <p>
	 * e.g. entity_id asc
	 * </p>
	 * <p>
	 * The field name passed into the sort clause must be indexed by Solr, or the
	 * query will fail. Additionally, if the sort order is not set to either asc or
	 * desc, the clause will be skipped, allowing the query to continue.
	 * </p>
	 * 
	 * @param sortVal the comma-delimited list of sort clause values
	 * @return a list of Solr sort clauses
	 */
	private static List<SortClause> getSortClauses(String sortVal) {
		// if empty, return immediately
		if (StringUtils.isBlank(sortVal)) {
			return Collections.emptyList();
		}

		// separate multiple sort clauses by comma
		String[] sorts = sortVal.split(",");
		List<SortClause> clauses = new ArrayList<>(sorts.length);
		for (String sort : sorts) {
			// split the sort string into its parts, splitting on whitespace
			String[] sortParts = sort.split(" ");

			// validate the sort parts
			if (sortParts.length != 2) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Following sort string is malformed: '{}'", sort);
				}
			} else if (!"asc".equalsIgnoreCase(sortParts[1]) && !"desc".equalsIgnoreCase(sortParts[1])) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Order part in sort clause must be asc or desc, case sensitive, found: {}",
							sortParts[1]);
				}
			} else {
				// add the clause after checking it
				clauses.add(new SortClause(sortParts[0], sortParts[1].toLowerCase()));
			}
		}
		return clauses;
	}

	/**
	 * Hide the constructor as helpers should not be instantiated.
	 */
	private SolrHelper() {

	}
}
