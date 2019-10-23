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
import org.eclipsefoundation.marketplace.dto.Catalog;
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
 * @author martin
 *
 */
@ResourceDataType(Catalog.class)
@Path("/catalogs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class CatalogResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(CatalogResource.class);

	@Inject
	MongoDao dao;
	@Inject
	CachingService<List<Catalog>> cachingService;
	@Inject
	RequestWrapper params;
	@Inject
	DtoFilter<Catalog> dtoFilter;

	@GET
	public Response select() {
		MongoQuery<Catalog> q = new MongoQuery<>(params, dtoFilter, cachingService);
		// retrieve the possible cached object
		Optional<List<Catalog>> cachedResults = cachingService.get("all", params,
				() -> StreamHelper.awaitCompletionStage(dao.get(q)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached Catalogs");
			return Response.serverError().build();
		}

		// return the results as a response
		return Response.ok(cachedResults.get()).build();
	}

	/**
	 * Endpoint for /Catalog/ to post a new Catalog to the persistence layer.
	 * 
	 * @param catalog the Catalog object to insert into the database.
	 * @return response for the browser
	 */
	@PUT
	public Response putCatalog(Catalog catalog) {
		MongoQuery<Catalog> q = new MongoQuery<>(params, dtoFilter, cachingService);
		// add the object, and await the result
		StreamHelper.awaitCompletionStage(dao.add(q, Arrays.asList(catalog)));

		// return the results as a response
		return Response.ok().build();
	}

	/**
	 * Endpoint for /catalogs/\<catalogId\> to retrieve a specific Catalog from
	 * the database.
	 * 
	 * @param catalogId the Catalog ID
	 * @return response for the browser
	 */
	@GET
	@Path("/{catalogId}")
	public Response select(@PathParam("catalogId") String catalogId) {
		params.addParam(UrlParameterNames.ID, catalogId);

		MongoQuery<Catalog> q = new MongoQuery<>(params, dtoFilter, cachingService);
		// retrieve a cached version of the value for the current listing
		Optional<List<Catalog>> cachedResults = cachingService.get(catalogId, params,
				() -> StreamHelper.awaitCompletionStage(dao.get(q)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached listing for ID {}", catalogId);
			return Response.serverError().build();
		}

		// return the results as a response
		return Response.ok(cachedResults.get()).build();
	}

	/**
	 * Endpoint for /catalogs/\<catalogId\> to retrieve a specific Catalog from
	 * the database.
	 * 
	 * @param catalogId the catalog ID
	 * @return response for the browser
	 */
	@DELETE
	@Path("/{catalogId}")
	public Response delete(@PathParam("catalogId") String catalogId) {
		params.addParam(UrlParameterNames.ID, catalogId);

		MongoQuery<Catalog> q = new MongoQuery<>(params, dtoFilter, cachingService);
		// delete the currently selected asset
		DeleteResult result = StreamHelper.awaitCompletionStage(dao.delete(q));
		if (result.getDeletedCount() == 0 || !result.wasAcknowledged()) {
			return new Error(Status.NOT_FOUND, "Did not find an asset to delete for current call").asResponse();
		}
		// return the results as a response
		return Response.ok().build();
	}
}
