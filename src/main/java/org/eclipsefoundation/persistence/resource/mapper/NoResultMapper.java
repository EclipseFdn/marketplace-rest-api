/*
 * Copyright (C) 2019 Eclipse Foundation and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*/
package org.eclipsefoundation.persistence.resource.mapper;

import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipsefoundation.core.model.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates human legible error responses in the case of no result exceptions. By
 * catching the NoResultException, we handle exceptions thrown when there are no
 * results when one is required.
 * 
 * @author Martin Lowe
 */
@Provider
public class NoResultMapper implements ExceptionMapper<NoResultException> {
	private static final Logger LOGGER = LoggerFactory.getLogger(NoResultMapper.class);

	@Override
	public Response toResponse(NoResultException exception) {
		LOGGER.error(exception.getMessage(), exception);
		return new Error(Status.NOT_FOUND, exception.getMessage()).asResponse();
	}
}
