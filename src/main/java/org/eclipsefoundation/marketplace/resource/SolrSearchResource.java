/*
 * Copyright (C) 2019 Eclipse Foundation and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*/
package org.eclipsefoundation.marketplace.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipsefoundation.marketplace.dao.impl.DefaultSolrDao;
import org.eclipsefoundation.marketplace.dao.mapper.RawSolrResultMapper;
import org.eclipsefoundation.marketplace.helper.SolrHelper;
import org.eclipsefoundation.marketplace.model.RawSolrResult;

@Path("/solr")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolrSearchResource {

	@Inject
	private DefaultSolrDao dao;

	@Context
	private UriInfo uriInfo;

	@GET
	public Response select() {
		Map<String, List<String>> params = new HashMap<>(uriInfo.getQueryParameters());

		List<RawSolrResult> results = dao.get(SolrHelper.createQuery(params), RawSolrResultMapper.INSTANCE);
		return Response.ok(results).build();
	}
}