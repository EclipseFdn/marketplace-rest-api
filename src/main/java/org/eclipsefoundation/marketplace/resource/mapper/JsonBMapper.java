/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.resource.mapper;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipsefoundation.marketplace.model.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 */
@Provider
public class JsonBMapper implements ExceptionMapper<ProcessingException> {
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonBMapper.class);

	@Override
	public Response toResponse(ProcessingException exception) {
		LOGGER.error(exception.getMessage(), exception);
		return new Error(Status.BAD_REQUEST,
				"Error while processing JSON input. Please check input format to ensure it matches input specification.")
						.asResponse();
	}

}
