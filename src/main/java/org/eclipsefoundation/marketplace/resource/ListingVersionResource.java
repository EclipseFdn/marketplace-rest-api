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
import org.eclipsefoundation.marketplace.dto.ListingVersion;
import org.eclipsefoundation.marketplace.dto.filter.DtoFilter;
import org.eclipsefoundation.marketplace.helper.ResponseHelper;
import org.eclipsefoundation.marketplace.helper.StreamHelper;
import org.eclipsefoundation.marketplace.model.Error;
import org.eclipsefoundation.marketplace.model.MongoQuery;
import org.eclipsefoundation.marketplace.model.RequestWrapper;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.eclipsefoundation.marketplace.service.CachingService;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.result.DeleteResult;

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
	MongoDao dao;
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
		MongoQuery<ListingVersion> q = new MongoQuery<>(params, dtoFilter);
		// retrieve the possible cached object
		Optional<List<ListingVersion>> cachedResults = cachingService.get("all", params,
				() -> StreamHelper.awaitCompletionStage(dao.get(q)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached ListingVersions");
			return Response.serverError().build();
		}

		// return the results as a response
		return Response.ok(cachedResults.get()).build();
	}

	/**
	 * Endpoint for /ListingVersion/ to post a new ListingVersion to the persistence layer.
	 * 
	 * @param listingVersion the ListingVersion object to insert into the database.
	 * @return response for the browser
	 */
	@PUT
	public Response putListingVersion(ListingVersion listingVersion) {
		if (listingVersion.getId() != null) {
			params.addParam(UrlParameterNames.ID, listingVersion.getId());
		}
		MongoQuery<ListingVersion> q = new MongoQuery<>(params, dtoFilter);
		// add the object, and await the result
		StreamHelper.awaitCompletionStage(dao.add(q, Arrays.asList(listingVersion)));

		// return the results as a response
		return Response.ok().build();
	}

	/**
	 * Endpoint for /listingVersions/\<listingVersionId\> to retrieve a specific ListingVersion from the
	 * database.
	 * 
	 * @param listingVersionId the ListingVersion ID
	 * @return response for the browser
	 */
	@GET
	@Path("/{listingVersionId}")
	public Response select(@PathParam("listingVersionId") String listingVersionId) {
		params.addParam(UrlParameterNames.ID, listingVersionId);

		MongoQuery<ListingVersion> q = new MongoQuery<>(params, dtoFilter);
		// retrieve a cached version of the value for the current listing
		Optional<List<ListingVersion>> cachedResults = cachingService.get(listingVersionId, params,
				() -> StreamHelper.awaitCompletionStage(dao.get(q)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached listing for ID {}", listingVersionId);
			return Response.serverError().build();
		}

		// return the results as a response
		return Response.ok(cachedResults.get()).build();
	}

	/**
	 * Endpoint for /listingVersions/\<listingVersionId\> to retrieve a specific ListingVersion from the
	 * database.
	 * 
	 * @param listingVersionId the listingVersion ID
	 * @return response for the browser
	 */
	@DELETE
	@RolesAllowed({ "marketplace_version_delete", "marketplace_admin_access" })
	@Path("/{listingVersionId}")
	public Response delete(@PathParam("listingVersionId") String listingVersionId) {
		params.addParam(UrlParameterNames.ID, listingVersionId);

		MongoQuery<ListingVersion> q = new MongoQuery<>(params, dtoFilter);
		// delete the currently selected asset
		DeleteResult result = StreamHelper.awaitCompletionStage(dao.delete(q));
		if (result.getDeletedCount() == 0 || !result.wasAcknowledged()) {
			return new Error(Status.NOT_FOUND, "Did not find an asset to delete for current call").asResponse();
		}
		// return the results as a response
		return Response.ok().build();
	}
}
