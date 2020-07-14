/*
 * Copyright (C) 2019 Eclipse Foundation and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*/
package org.eclipsefoundation.core.namespace;

/**
 * Namespace containing common URL parameters used throughout the API.
 * 
 * @author Martin Lowe
 */
public enum DefaultUrlParameterNames implements UrlParameterName {

	QUERY_STRING("q"),
	PAGE("page"),
	LIMIT("limit"),
	IDS("ids"),
	ID("id");

	private String parameterName;
	private DefaultUrlParameterNames(String parameterName) {
		this.parameterName = parameterName;
	}
	
	/**
	 * @return the URL parameters name
	 */
	@Override
	public String getParameterName() {
		return parameterName;
	}
	
	/**
	 * Retrieves the UrlParameterName for the given name.
	 * 
	 * @param name the name to retrieve a URL parameter for
	 * @return the URL parameter name if it exists, or null if no match is found
	 */
	public static DefaultUrlParameterNames getByParameterName(String name) {
		for (DefaultUrlParameterNames param: values()) {
			if (param.getParameterName().equalsIgnoreCase(name)) {
				return param;
			}
		}
		return null;
	}
}
