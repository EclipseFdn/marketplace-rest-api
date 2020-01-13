/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.bson.conversions.Bson;
import org.eclipsefoundation.marketplace.model.QueryParameters;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

/**
 * Filter interface for usage when querying data.
 * 
 * @author Martin Lowe
 */
public interface DtoFilter<T> {

	/**
	 * Retrieve filter objects for the current arguments.
	 * 
	 * @param params     parameters to use in filter construction
	 * @param nestedPath current path for nesting of filters
	 * @return list of filters for the current request, or empty if there are no
	 *         applicable filters.
	 */
	default List<Bson> getFilters(QueryParameters params, String root) {
		List<Bson> filters = new ArrayList<>();
		// perform following checks only if there is no doc root
		if (root == null) {
			// ID check
			Optional<String> id = params.getFirstIfPresent(UrlParameterNames.ID.getParameterName());
			if (id.isPresent()) {
				filters.add(Filters.eq(DatabaseFieldNames.DOCID, id.get()));
			}
		}
		return filters;

	}

	/**
	 * Retrieve aggregate filter operations for the current arguments.
	 * 
	 * @param params parameters to use in aggregate construction
	 * @return list of aggregates for the current request, or empty if there are no
	 *         applicable aggregates.
	 */
	default List<Bson> getAggregates(QueryParameters params) {
		return Collections.emptyList();
	}

	/**
	 * Returns the type of data this object will filter for.
	 * 
	 * @return class of object to filter
	 */
	Class<T> getType();

	/**
	 * Wraps each of the filters present for a given filter type in an aggregate
	 * match operation to port filter operations into an aggregate pipeline. This is
	 * handy when importing nested types and enabling filters.
	 * 
	 * @param params     parameters for the current call
	 * @param nestedPath current path for nesting of filters
	 * @return a list of aggregate pipeline operations representing the filters for
	 *         the current request.
	 */
	default Bson wrapFiltersToAggregate(QueryParameters params, String nestedPath) {
		List<Bson> filters = getFilters(params, nestedPath);
		if (!filters.isEmpty()) {
			return Aggregates.match(Filters.elemMatch(nestedPath, Filters.and(filters)));
		}
		return null;
	}

	/**
	 * Whether this type of data should be restrained to a limited set, or return
	 * all data that is found.
	 * 
	 * @return true if limit should be used, false otherwise.
	 */
	default boolean useLimit() {
		return true;
	}

}
