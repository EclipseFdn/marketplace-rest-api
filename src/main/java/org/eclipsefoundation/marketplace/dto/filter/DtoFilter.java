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

/**
 * Filter interface for usage when querying data.
 * 
 * @author Martin Lowe
 */
public interface DtoFilter<T> {

	/**
	 * Retrieve filter objects for the current arguments.
	 * 
	 * @param wrap wrapper for the current request
	 * @return list of filters for the current request, or empty if there are no applicable filters.
	 */
	List<Bson> getFilters(RequestWrapper wrap);

	/**
	 * Retrieve aggregate filter operations for the current arguments.
	 * 
	 * @param wrap wrapper for the current request
	 * @return list of aggregates for the current request, or empty if there are no applicable aggregates.
	 */
	List<Bson> getAggregates(RequestWrapper wrap);

	/**
	 * Returns the type of data this object will filter for.
	 * 
	 * @return class of object to filter
	 */
	Class<T> getType();
}
