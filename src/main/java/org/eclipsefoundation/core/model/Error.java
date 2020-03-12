/*
 * Copyright (C) 2019 Eclipse Foundation and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*/
package org.eclipsefoundation.core.model;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class Error {
	private int statusCode;
	private String message;
	private String url;

	public Error(int statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}

	/**
	 * Creates an error object using the JAX-RS status to fetch the status code.
	 * 
	 * @param status  the JAX-RS status for the error.
	 * @param message the message to return
	 */
	public Error(Status status, String message) {
		this.statusCode = status.getStatusCode();
		this.message = message;
	}

	/**
	 * Returns a response object given the fields within as the contents.
	 * 
	 * @return a JAX-RS Response with the given status code, with this object as the
	 *         message.
	 */
	public Response asResponse() {
		return Response.status(statusCode).entity(this).build();
	}

	/**
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
}
