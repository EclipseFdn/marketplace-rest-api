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
import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipsefoundation.core.helper.ResponseHelper;
import org.eclipsefoundation.core.model.RequestWrapper;
import org.eclipsefoundation.core.namespace.DefaultUrlParameterNames;
import org.eclipsefoundation.core.service.CachingService;
import org.eclipsefoundation.marketplace.dto.Listing;
import org.eclipsefoundation.marketplace.service.PromotionService;
import org.eclipsefoundation.persistence.dao.PersistenceDao;
import org.eclipsefoundation.persistence.dto.filter.DtoFilter;
import org.eclipsefoundation.persistence.model.RDBMSQuery;
import org.eclipsefoundation.search.service.PersistenceBackedSearchService;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource for retrieving listings from the MongoDB instance.
 * 
 * @author Martin Lowe
 */
@Path("/listings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class ListingResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(ListingResource.class);

	// service/access layers
	@Inject
	PersistenceDao dao;
	@Inject
	PersistenceBackedSearchService searchService;
	@Inject
	CachingService<List<Listing>> cachingService;
	
	@Inject
	PromotionService promoService;

	@Inject
	RequestWrapper params;
	@Inject
	DtoFilter<Listing> dtoFilter;
	@Inject
	ResponseHelper responseBuider;

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
		// retrieve the possible cached object
		Optional<String> searchTerm = params.getFirstParam(DefaultUrlParameterNames.QUERY_STRING);
		Optional<List<Listing>> cachedResults;
		// if there is a search term set, use it
		if (searchTerm.isPresent()) {
			cachedResults = cachingService.get("all", params,
					() -> searchService.find(params, dtoFilter));
		} else {
			cachedResults = cachingService.get("all", params,
					() -> dao.get(new RDBMSQuery<>(params, dtoFilter)));
		}
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached listings");
			return Response.serverError().build();
		}
		// return the results as a response
		return responseBuider.build("all", params, cachedResults.get());
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
		// add the object, and await the result
		dao.add(new RDBMSQuery<>(params, dtoFilter), Arrays.asList(listing));

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
		params.addParam(DefaultUrlParameterNames.ID.getParameterName(), listingId);

		// retrieve a cached version of the value for the current listing
		Optional<List<Listing>> cachedResults = cachingService.get(listingId, params,
				() -> dao.get(new RDBMSQuery<>(params, dtoFilter)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached listing for ID {}", listingId);
			return Response.serverError().build();
		}
		if (cachedResults.get().isEmpty()) {
			throw new NoResultException("Could not find any documents with ID " + listingId);
		}
		// return the results as a response
		return responseBuider.build(listingId, params, cachedResults.get());
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
		params.addParam(DefaultUrlParameterNames.ID.getParameterName(), listingId);
		// delete the currently selected asset
		dao.delete(new RDBMSQuery<>(params, dtoFilter));
		// return the results as a response
		return Response.ok().build();
	}
}
