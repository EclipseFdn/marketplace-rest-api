/*
 * Copyright (C) 2019 Eclipse Foundation and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*/
package org.eclipsefoundation.core.resource.mapper;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipsefoundation.core.model.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates human legible error responses in the case of not found exceptions. By
 * catching the NotFoundException, we handle exceptions caused by bad path
 * casting.
 * 
 * @author Martin Lowe
 */
@Provider
public class NotFoundMapper implements ExceptionMapper<NotFoundException> {
	private static final Logger LOGGER = LoggerFactory.getLogger(NotFoundMapper.class);

	@Override
	public Response toResponse(NotFoundException exception) {
		LOGGER.error(exception.getMessage(), exception);
		return new Error(Status.BAD_REQUEST, exception.getMessage()).asResponse();
	}
}
