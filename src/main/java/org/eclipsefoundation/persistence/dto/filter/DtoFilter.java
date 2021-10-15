/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.persistence.dto.filter;

import org.eclipsefoundation.core.model.RequestWrapper;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatement;

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
	ParameterizedSQLStatement getFilters(RequestWrapper wrap, boolean isRoot);

	/**
	 * Returns the type of data this object will filter for.
	 * 
	 * @return class of object to filter
	 */
	Class<T> getType();

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
