/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.resource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import org.eclipsefoundation.core.service.CachingService;
import org.eclipsefoundation.marketplace.dto.ListingVersion;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.eclipsefoundation.persistence.dao.PersistenceDao;
import org.eclipsefoundation.persistence.dto.filter.DtoFilter;
import org.eclipsefoundation.persistence.model.RDBMSQuery;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource for retrieving {@linkplain ListingVersion}s from the MongoDB
 * instance.
 * 
 * @author Martin Lowe
 */
@RequestScoped
@Path("/listing_versions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ListingVersionResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(ListingVersionResource.class);

	@Inject
	PersistenceDao dao;
	@Inject
	CachingService<List<ListingVersion>> cachingService;
	@Inject
	RequestWrapper params;
	@Inject
	DtoFilter<ListingVersion> dtoFilter;
	@Inject
	ResponseHelper responseBuider;

	@GET
	public Response select() {
		RDBMSQuery<ListingVersion> q = new RDBMSQuery<>(params, dtoFilter);
		// retrieve the possible cached object
		Optional<List<ListingVersion>> cachedResults = cachingService.get("all", params, () -> dao.get(q));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached ListingVersions");
			return Response.serverError().build();
		}

		// return the results as a response
		return Response.ok(cachedResults.get()).build();
	}

	/**
	 * Endpoint for /listing_versions/ to post a new ListingVersion to the persistence
	 * layer.
	 * 
	 * @param listingVersion the ListingVersion object to insert into the database.
	 * @return response for the browser
	 */
	@PUT
	public Response putListingVersion(ListingVersion listingVersion) {
		// add the object, and await the result
		dao.add(new RDBMSQuery<>(params, dtoFilter), Arrays.asList(listingVersion));

		// return the results as a response
		return Response.ok().build();
	}

	/**
	 * Endpoint for /listing_versions/\<listingVersionId\> to retrieve a specific
	 * ListingVersion from the database.
	 * 
	 * @param listingVersionId the ListingVersion ID
	 * @return response for the browser
	 */
	@GET
	@Path("/{listingVersionId}")
	public Response select(@PathParam("listingVersionId") String listingVersionId) {
		params.addParam(UrlParameterNames.ID, listingVersionId);

		// retrieve a cached version of the value for the current listing
		Optional<List<ListingVersion>> cachedResults = cachingService.get(listingVersionId, params,
				() -> dao.get(new RDBMSQuery<>(params, dtoFilter)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached listing for ID {}", listingVersionId);
			return Response.serverError().build();
		}
		if (cachedResults.get().isEmpty()) {
			throw new NoResultException("Could not find any documents with ID " + listingVersionId);
		}

		// return the results as a response
		return Response.ok(cachedResults.get()).build();
	}

	/**
	 * Endpoint for /listing_versions/\<listingVersionId\> to remove a specific
	 * ListingVersion from the database.
	 * 
	 * @param listingVersionId the listingVersion ID
	 * @return response for the browser
	 */
	@DELETE
	@RolesAllowed({ "marketplace_version_delete", "marketplace_admin_access" })
	@Path("/{listingVersionId}")
	public Response delete(@PathParam("listingVersionId") String listingVersionId) {
		params.addParam(UrlParameterNames.ID, listingVersionId);
		// delete the currently selected asset
		dao.delete(new RDBMSQuery<>(params, dtoFilter));
		// return the results as a response
		return Response.ok().build();
	}
}
