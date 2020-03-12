/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.persistence.model;

import java.util.Objects;

/**
 * Enumeration representing the different possible sort orders available when
 * retrieving data.
 * 
 * @author Martin Lowe
 */
public enum SortOrder {
	RANDOM("RAND", 0), ASCENDING("ASC", 1), DESCENDING("DESC", -1), NONE(null, 0);

	private String shortName;
	private int order;

	private SortOrder(String shortName, int sortOrder) {
		this.shortName = shortName;
		this.order = sortOrder;
	}

	/**
	 * @return the numeric representation of the order
	 */
	public int getOrder() {
		return this.order;
	}

	/**
	 * Iterates over the different possible sort orders, returning a specific
	 * SortOrder if the name value matches either the fullname of one of the
	 * enumerations, or the designated short names. Otherwise, NONE is returned.
	 * 
	 * @param name the value to compare to SortOrder names.
	 * @return the matching sort order, or none.
	 */
	public static SortOrder getOrderByName(String name) {
		Objects.requireNonNull(name);
		String casedName = name.toUpperCase();
		for (SortOrder order : values()) {
			if (order.equals(NONE)) {
				continue;
			}
			if (order.name().equals(casedName) || casedName.equals(order.shortName)) {
				return order;
			}
		}
		return NONE;
	}

	/**
	 * Gets the SortOrder value associated with a sort parameter value if one
	 * exists.
	 * 
	 * @param value the value of the sort parameter
	 * @return the SortOrder associated with the request, or
	 *         {@linkplain SortOrder.NONE}
	 */
	public static SortOrder getOrderFromValue(String value) {
		// get the index of the space separator 
		int idx = value.indexOf(' ');
		// check if the sort string matches the RANDOM sort order
		if (SortOrder.RANDOM.equals(SortOrder.getOrderByName(value))) {
			return SortOrder.RANDOM;
		} else if (idx > 0) {
			return SortOrder.getOrderByName(value.substring(idx + 1));
		}
		return SortOrder.NONE;
	}
}