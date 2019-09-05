/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Represents an author for listings.
 * 
 * @author Martin Lowe
 */
@RegisterForReflection
public class Author {
	private String username;
	private String fullName;

	public Author(String username, String fullName) {
		this.username = username;
		this.fullName = fullName;
	}

	public Author() {
		// purposefully empty
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
