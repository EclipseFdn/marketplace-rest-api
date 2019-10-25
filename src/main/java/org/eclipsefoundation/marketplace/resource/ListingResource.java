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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipsefoundation.marketplace.dao.MongoDao;
import org.eclipsefoundation.marketplace.dto.Listing;
import org.eclipsefoundation.marketplace.dto.filter.DtoFilter;
import org.eclipsefoundation.marketplace.helper.StreamHelper;
import org.eclipsefoundation.marketplace.model.Error;
import org.eclipsefoundation.marketplace.model.MongoQuery;
import org.eclipsefoundation.marketplace.model.RequestWrapper;
import org.eclipsefoundation.marketplace.model.ResourceDataType;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.eclipsefoundation.marketplace.service.CachingService;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.result.DeleteResult;

/**
 * Resource for retrieving listings from the MongoDB instance.
 * 
 * @author Martin Lowe
 */
@ResourceDataType(Listing.class)
@Path("/listings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class ListingResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(ListingResource.class);

	@Inject
	MongoDao dao;
	@Inject
	CachingService<List<Listing>> cachingService;
	@Inject
	RequestWrapper params;
	@Inject
	DtoFilter<Listing> dtoFilter;

	/**
	 * Endpoint for /listing/ to retrieve all listings from the database along with
	 * the given query string parameters.
	 * 
	 * @param listingId int version of the listing ID
	 * @return response for the browser
	 */
	@GET
	@PermitAll
	public Response select() {
		MongoQuery<Listing> q = new MongoQuery<>(params, dtoFilter, cachingService);
		// retrieve the possible cached object
		Optional<List<Listing>> cachedResults = cachingService.get("all", params,
				() -> StreamHelper.awaitCompletionStage(dao.get(q)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached listings");
			return Response.serverError().build();
		}

		// return the results as a response
		return Response.ok(cachedResults.get()).build();
	}

	/**
	 * Endpoint for /listing/ to post a new listing to the persistence layer.
	 * 
	 * @param listing the listing object to insert into the database.
	 * @return response for the browser
	 */
	@PUT
	@RolesAllowed({ "marketplace_listing_put", "marketplace_admin_access" })
	public Response putListing(Listing listing) {
		MongoQuery<Listing> q = new MongoQuery<>(params, dtoFilter, cachingService);

		// add the object, and await the result
		StreamHelper.awaitCompletionStage(dao.add(q, Arrays.asList(listing)));

		// return the results as a response
		return Response.ok().build();
	}

	/**
	 * Endpoint for /listing/\<listingId\> to retrieve a specific listing from the
	 * database.
	 * 
	 * @param listingId the listing ID
	 * @return response for the browser
	 */
	@GET
	@PermitAll
	@Path("/{listingId}")
	public Response select(@PathParam("listingId") String listingId) {
		params.addParam(UrlParameterNames.ID, listingId);

		MongoQuery<Listing> q = new MongoQuery<>(params, dtoFilter, cachingService);
		// retrieve a cached version of the value for the current listing
		Optional<List<Listing>> cachedResults = cachingService.get(listingId, params,
				() -> StreamHelper.awaitCompletionStage(dao.get(q)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached listing for ID {}", listingId);
			return Response.serverError().build();
		}

		// return the results as a response
		return Response.ok(cachedResults.get()).build();
	}

	/**
	 * Endpoint for /listing/\<listingId\> to delete a specific listing from the
	 * database.
	 * 
	 * @param listingId the listing ID
	 * @return response for the browser
	 */
	@DELETE
	@RolesAllowed({ "marketplace_listing_delete", "marketplace_admin_access" })
	@Path("/{listingId}")
	public Response delete(@PathParam("listingId") String listingId) {
		params.addParam(UrlParameterNames.ID, listingId);
		MongoQuery<Listing> q = new MongoQuery<>(params, dtoFilter, cachingService);
		// delete the currently selected asset
		DeleteResult result = StreamHelper.awaitCompletionStage(dao.delete(q));
		if (result.getDeletedCount() == 0 || !result.wasAcknowledged()) {
			return new Error(Status.NOT_FOUND, "Did not find an asset to delete for current call").asResponse();
		}
		// return the results as a response
		return Response.ok().build();
	}
}