/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.core.namespace;

import java.util.Date;
import java.util.Objects;

/**
 * Centralized location for creating the "Deprecated" header to be used when
 * warning clients. This is designed to be used when endpoints/functionality are
 * updated such that they diverge from previous versions.
 * 
 * @author Martin Lowe
 *
 */
public class DeprecatedHeader {

	public static final String NAME = "Deprecated";

	private Date date;
	private String message;

	/**
	 * Creates a valid deprecation header with the given values set.
	 * 
	 * @param date    the date that the endpoint/functionality was deprecated
	 * @param message information about the deprecation
	 */
	public DeprecatedHeader(Date date, String message) {
		this.date = Objects.requireNonNull(date);
		this.message = Objects.requireNonNull(message);
	}

	/**
	 * Get the value of a header that would contain the given values.
	 * 
	 * @param date    the date that the endpoint was deprecated
	 * @param message information about the deprecation
	 * @return the value for the header with given values.
	 */
	public static String getValue(Date date, String message) {
		Objects.requireNonNull(date);
		Objects.requireNonNull(message);

		return date.toString() + ';' + message;
	}

	/**
	 * Get the value of the header for the current header.
	 * 
	 * @return the value for this header.
	 */
	public String getValue() {
		return DeprecatedHeader.getValue(this.date, this.message);
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeprecatedHeader [date=");
		builder.append(date);
		builder.append(", message=");
		builder.append(message);
		builder.append("]");
		return builder.toString();
	}

}
