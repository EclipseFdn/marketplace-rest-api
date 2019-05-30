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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eclipsefoundation.marketplace.dao.impl.DefaultSolrDao;
import org.eclipsefoundation.marketplace.dao.mapper.ListingMapper;
import org.eclipsefoundation.marketplace.helper.ParamHelper;
import org.eclipsefoundation.marketplace.helper.SolrHelper;
import org.eclipsefoundation.marketplace.model.EnhancedResponse;
import org.eclipsefoundation.marketplace.model.Error;
import org.eclipsefoundation.marketplace.model.Listing;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

/**
 * Resource for retrieving listings from the Solr instance.
 * 
 * @author Martin Lowe
 *
 */
@Path("/listings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ListingResource {

	@Inject
	private DefaultSolrDao dao;

	@Context
	private UriInfo uriInfo;

	@GET
	public Response select() {
		// retrieve the query parameters, and add to a modifiable map
		Map<String, List<String>> params = new HashMap<>(uriInfo.getQueryParameters());

		// retrieve the results for the parameters
		List<Listing> results = dao.get(SolrHelper.createQuery(params), ListingMapper.INSTANCE);
		
		// build an enhanced response object to help contain data
		EnhancedResponse resp = new EnhancedResponse();
		resp.setData(results);
		resp.setSize(results.size());
		
		return Response.ok(resp).build();
	}

	@GET
	@Path("/{listingId}")
	public Response select(@PathParam("listingId") String listingId) {
		// retrieve the query parameters, and add to a modifiable map
		Map<String, List<String>> params = new HashMap<>(uriInfo.getQueryParameters());
		ParamHelper.setParam(params, UrlParameterNames.SOLR_QUERY_STRING, "entity_id:" + listingId);

		// retrieve the results for the parameters, and check that a listing was found
		List<Listing> results = dao.get(SolrHelper.createQuery(params), ListingMapper.INSTANCE);
		if (results.isEmpty()) {
			return new Error(Status.NOT_FOUND, "No listing was found for ID '" + listingId + '\'').asResponse();
		}

		// build an enhanced response object to help contain data
		EnhancedResponse resp = new EnhancedResponse();
		resp.setData(results.get(0));
		resp.setSize(results.size());
		
		// return the results as a response
		return Response.ok(resp).build();
	}
}