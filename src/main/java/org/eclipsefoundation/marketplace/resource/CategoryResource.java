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
import org.eclipsefoundation.marketplace.dto.Category;
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
 * @author martin
 *
 */
@Path("/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class CategoryResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryResource.class);

	@Inject
	MongoDao dao;
	@Inject
	CachingService<List<Category>> cachingService;
	@Inject
	RequestWrapper params;
	@Inject
	DtoFilter<Category> dtoFilter;
	@Inject
	ResponseHelper responseBuider;

	@GET
	@PermitAll
	public Response select() {
		MongoQuery<Category> q = new MongoQuery<>(params, dtoFilter);
		// retrieve the possible cached object
		Optional<List<Category>> cachedResults = cachingService.get("all", params,
				() -> StreamHelper.awaitCompletionStage(dao.get(q)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached Categorys");
			return Response.serverError().build();
		}

		// return the results as a response
		return responseBuider.build("all", params, cachedResults.get());
	}

	/**
	 * Endpoint for /Category/ to post a new Category to the persistence layer.
	 * 
	 * @param category the Category object to insert into the database.
	 * @return response for the browser
	 */
	@PUT
	@RolesAllowed({"marketplace_category_put", "marketplace_admin_access"})
	public Response putCategory(Category category) {
		MongoQuery<Category> q = new MongoQuery<>(params, dtoFilter);
		// add the object, and await the result
		StreamHelper.awaitCompletionStage(dao.add(q, Arrays.asList(category)));

		// return the results as a response
		return Response.ok().build();
	}

	/**
	 * Endpoint for /categories/\<categoryId\> to retrieve a specific Category from
	 * the database.
	 * 
	 * @param categoryId the Category ID
	 * @return response for the browser
	 */
	@GET
	@Path("/{categoryId}")
	public Response select(@PathParam("categoryId") String categoryId) {
		params.addParam(UrlParameterNames.ID, categoryId);

		MongoQuery<Category> q = new MongoQuery<>(params, dtoFilter);
		// retrieve a cached version of the value for the current listing
		Optional<List<Category>> cachedResults = cachingService.get(categoryId, params,
				() -> StreamHelper.awaitCompletionStage(dao.get(q)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached listing for ID {}", categoryId);
			return Response.serverError().build();
		}

		// return the results as a response
		return responseBuider.build(categoryId, params, cachedResults.get());
	}

	/**
	 * Endpoint for /categories/\<categoryId\> to retrieve a specific Category from
	 * the database.
	 * 
	 * @param categoryId the category ID
	 * @return response for the browser
	 */
	@DELETE
	@RolesAllowed({ "marketplace_category_delete", "marketplace_admin_access" })
	@Path("/{categoryId}")
	public Response delete(@PathParam("categoryId") String categoryId) {
		params.addParam(UrlParameterNames.ID, categoryId);

		MongoQuery<Category> q = new MongoQuery<>(params, dtoFilter);
		// delete the currently selected asset
		DeleteResult result = StreamHelper.awaitCompletionStage(dao.delete(q));
		if (result.getDeletedCount() == 0 || !result.wasAcknowledged()) {
			return new Error(Status.NOT_FOUND, "Did not find an asset to delete for current call").asResponse();
		}
		// return the results as a response
		return Response.ok().build();
	}
}
