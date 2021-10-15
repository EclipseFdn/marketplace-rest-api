/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.core.resource.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipsefoundation.core.exception.MaintenanceException;
import org.eclipsefoundation.core.model.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Catches and prints out human legible exceptions when the service throws a
 * MaintenanceException;
 * 
 * @author Martin Lowe
 */
@Provider
public class MaintenanceMapper implements ExceptionMapper<MaintenanceException> {
	private static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceMapper.class);

	@Override
	public Response toResponse(MaintenanceException exception) {
		LOGGER.error(exception.getMessage(), exception);
		return new Error(Status.SERVICE_UNAVAILABLE,
				"Called service is currently set to maintenance and is not currently available").asResponse();
	}
}
