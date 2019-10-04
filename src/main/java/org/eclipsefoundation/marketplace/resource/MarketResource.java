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
import org.eclipsefoundation.marketplace.dto.Market;
import org.eclipsefoundation.marketplace.dto.filter.DtoFilter;
import org.eclipsefoundation.marketplace.helper.StreamHelper;
import org.eclipsefoundation.marketplace.model.MongoQuery;
import org.eclipsefoundation.marketplace.model.RequestWrapper;
import org.eclipsefoundation.marketplace.model.ResourceDataType;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.eclipsefoundation.marketplace.service.CachingService;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 */
@ResourceDataType(Market.class)
@Path("/markets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class MarketResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(MarketResource.class);

	@Inject
	MongoDao dao;
	@Inject
	CachingService<List<Market>> cachingService;
	@Inject
	RequestWrapper params;
	@Inject
	DtoFilter<Market> dtoFilter;

	@GET
	public Response select() {
		MongoQuery<Market> q = new MongoQuery<>(params, dtoFilter, cachingService);
		// retrieve the possible cached object
		Optional<List<Market>> cachedResults = cachingService.get("all", params,
				() -> StreamHelper.awaitCompletionStage(dao.get(q)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached Categorys");
			return Response.serverError().build();
		}

		// return the results as a response
		return Response.ok(cachedResults.get()).build();
	}

	/**
	 * Endpoint for /markets/ to post a new Market to the persistence layer.
	 * 
	 * @param market the Category object to insert into the database.
	 * @return response for the browser
	 */
	@POST
	public Response postMarket(Market market) {
		MongoQuery<Market> q = new MongoQuery<>(params, dtoFilter, cachingService);

		// add the object, and await the result
		StreamHelper.awaitCompletionStage(dao.add(q, Arrays.asList(market)));

		// return the results as a response
		return Response.ok().build();
	}

	/**
	 * Endpoint for /markets/\<marketId\> to retrieve a specific Market from the
	 * database.
	 * 
	 * @param marketId int version of the listing ID
	 * @return response for the browser
	 */
	@GET
	@Path("/{marketId}")
	public Response select(@PathParam("marketId") String marketId) {
		params.addParam(UrlParameterNames.ID, marketId);

		MongoQuery<Market> q = new MongoQuery<>(params, dtoFilter, cachingService);
		// retrieve a cached version of the value for the current listing
		Optional<List<Market>> cachedResults = cachingService.get(marketId, params,
				() -> StreamHelper.awaitCompletionStage(dao.get(q)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached listing for ID {}", marketId);
			return Response.serverError().build();
		}

		// return the results as a response
		return Response.ok(cachedResults.get()).build();
	}
}
