/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.filter;

import java.util.List;

import org.bson.conversions.Bson;
import org.eclipsefoundation.marketplace.model.RequestWrapper;

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
	 * @param wrap       wrapper for the current request
	 * @param nestedPath current path for nesting of filters
	 * @return list of filters for the current request, or empty if there are no
	 *         applicable filters.
	 */
	List<Bson> getFilters(RequestWrapper wrap, String nestedPath);

	/**
	 * Retrieve aggregate filter operations for the current arguments.
	 * 
	 * @param wrap wrapper for the current request
	 * @return list of aggregates for the current request, or empty if there are no
	 *         applicable aggregates.
	 */
	List<Bson> getAggregates(RequestWrapper wrap);

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
	 * @param wrap       wrapper for the current request
	 * @param nestedPath current path for nesting of filters
	 * @return a list of aggregate pipeline operations representing the filters for
	 *         the current request.
	 */
	default Bson wrapFiltersToAggregate(RequestWrapper wrap, String nestedPath) {
		List<Bson> filters = getFilters(wrap, nestedPath);
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
