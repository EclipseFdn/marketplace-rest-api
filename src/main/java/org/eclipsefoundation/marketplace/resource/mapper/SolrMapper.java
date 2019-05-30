/*
 * Copyright (C) 2019 Eclipse Foundation and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*/
package org.eclipsefoundation.marketplace.resource.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.solr.common.SolrException;
import org.eclipsefoundation.marketplace.model.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates human legible error responses in the case of Solr exceptions.
 * 
 * @author Martin Lowe
 *
 */
@Provider
public class SolrMapper implements ExceptionMapper<SolrException> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SolrMapper.class);

	@Override
	public Response toResponse(SolrException exception) {
		LOGGER.error(exception.getMessage(), exception);
		return new Error(Status.INTERNAL_SERVER_ERROR, "Passed parameter was not a number").asResponse();
	}
}
