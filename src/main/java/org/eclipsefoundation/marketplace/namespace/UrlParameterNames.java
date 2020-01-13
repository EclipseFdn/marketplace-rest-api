/*
 * Copyright (C) 2019 Eclipse Foundation and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*/
package org.eclipsefoundation.marketplace.namespace;

/**
 * Namespace containing URL parameters used throughout the API.
 * 
 * @author Martin Lowe
 */
public enum UrlParameterNames {

	QUERY_STRING("q"),
	PAGE("page"),
	LIMIT("limit"),
	SORT("sort"),
	OS("os"),
	ECLIPSE_VERSION("eclipse_version"),
	JAVA_VERSION("min_java_version"),
	IDS("ids"),
	TAGS("tags"),
	MARKET_IDS("market_ids"),
	ID("id"),
	LISTING_ID("listing_id"),
	READ("read"),
	FEATURE_ID("feature_id"),
	VERSION("version"),
	DATE_FROM("from"),
	END("end"),
	START("start");

	private String parameterName;
	private UrlParameterNames(String parameterName) {
		this.parameterName = parameterName;
	}
	
	/**
	 * @return the URL parameters name
	 */
	public String getParameterName() {
		return parameterName;
	}
	
	/**
	 * Retrieves the UrlParameterName for the given name.
	 * 
	 * @param name the name to retrieve a URL parameter for
	 * @return the URL parameter name if it exists, or null if no match is found
	 */
	public static UrlParameterNames getByParameterName(String name) {
		for (UrlParameterNames param: values()) {
			if (param.getParameterName().equalsIgnoreCase(name)) {
				return param;
			}
		}
		return null;
	}
}
