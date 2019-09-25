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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipsefoundation.marketplace.dao.MongoDao;
import org.eclipsefoundation.marketplace.dto.Category;
import org.eclipsefoundation.marketplace.dto.filter.DtoFilter;
import org.eclipsefoundation.marketplace.helper.StreamHelper;
import org.eclipsefoundation.marketplace.model.MongoQuery;
import org.eclipsefoundation.marketplace.model.RequestWrapper;
import org.eclipsefoundation.marketplace.model.ResourceDataType;
import org.eclipsefoundation.marketplace.service.CachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 */
@ResourceDataType(Category.class)
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


	@GET
	public Response select() {
		MongoQuery<Category> q = new MongoQuery<>(params, dtoFilter, cachingService);
		// retrieve the possible cached object
		Optional<List<Category>> cachedResults = cachingService.get("all", params,
				() -> StreamHelper.awaitCompletionStage(dao.get(q)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached Categorys");
			return Response.serverError().build();
		}

		// return the results as a response
		return Response.ok(cachedResults.get()).build();
	}

	/**
	 * Endpoint for /Category/ to post a new Category to the persistence layer.
	 * 
	 * @param category the Category object to insert into the database.
	 * @return response for the browser
	 */
	@POST
	public Response postCategory(Category category) {
		MongoQuery<Category> q = new MongoQuery<>(params, dtoFilter, cachingService);
		// add the object, and await the result
		StreamHelper.awaitCompletionStage(dao.add(q, Arrays.asList(category)));

		// return the results as a response
		return Response.ok().build();
	}
}
