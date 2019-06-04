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

import java.util.List;

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

import org.apache.commons.lang3.StringUtils;
import org.eclipsefoundation.marketplace.dao.impl.DefaultSolrDao;
import org.eclipsefoundation.marketplace.dao.mapper.ListingMapper;
import org.eclipsefoundation.marketplace.helper.SolrHelper;
import org.eclipsefoundation.marketplace.model.Error;
import org.eclipsefoundation.marketplace.model.Listing;
import org.eclipsefoundation.marketplace.model.QueryParams;
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

	/**
	 * Endpoint for /listing/ to retrieve all listings from the database along with
	 * the given query string parameters.
	 * 
	 * @param listingId string version of the listing ID. This is used as a string
	 *                  as using an integer with bad input is handled before the
	 *                  resource, meaning we have no input to its look and feel.
	 * @return response for the browser
	 */
	@GET
	public Response select() {
		// retrieve the query parameters, and add to a modifiable map
		QueryParams params = new QueryParams(uriInfo.getQueryParameters());
		// TODO we should add this in a way that doesn't override existing q params
		params.addParam(UrlParameterNames.SOLR_QUERY_STRING, "entity_type:node");

		// retrieve the results for the parameters
		List<Listing> results = dao.get(SolrHelper.createQuery(params), ListingMapper.INSTANCE);

		return Response.ok(results).build();
	}

	/**
	 * Endpoint for /listing/\<listingId\> to retrieve a specific listing from the
	 * database.
	 * 
	 * @param listingId string version of the listing ID. This is used as a string
	 *                  as using an integer with bad input is handled before the
	 *                  resource, meaning we have no input to its look and feel.
	 * @return response for the browser
	 */
	@GET
	@Path("/{listingId}")
	public Response select(@PathParam("listingId") String listingId) {
		// Numeric check used instead of int param as it blocks unfriendly error screens
		if (!StringUtils.isNumeric(listingId)) {
			return new Error(Status.BAD_REQUEST, "Passed parameter was not a number").asResponse();
		}

		// retrieve the query parameters, and add to a modifiable map
		QueryParams params = new QueryParams(uriInfo.getQueryParameters());
		// TODO we should add this in a way that doesn't override existing q params
		params.addParam(UrlParameterNames.SOLR_QUERY_STRING, "entity_id:" + listingId);

		// retrieve the results for the parameters, and check that a listing was found
		List<Listing> results = dao.get(SolrHelper.createQuery(params), ListingMapper.INSTANCE);
		if (results.isEmpty()) {
			return new Error(Status.NOT_FOUND, "No listing was found for ID '" + listingId + '\'').asResponse();
		}

		// return the results as a response
		return Response.ok(results.get(0)).build();
	}
}