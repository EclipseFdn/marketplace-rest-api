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
import org.eclipsefoundation.marketplace.dto.Market;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.eclipsefoundation.persistence.dao.PersistenceDao;
import org.eclipsefoundation.persistence.dto.filter.DtoFilter;
import org.eclipsefoundation.persistence.model.RDBMSQuery;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 */
@Path("/markets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class MarketResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(MarketResource.class);

	@Inject
	PersistenceDao dao;
	@Inject
	CachingService<List<Market>> cachingService;
	@Inject
	RequestWrapper params;
	@Inject
	DtoFilter<Market> dtoFilter;
	@Inject
	ResponseHelper responseBuider;

	@GET
	public Response select() {
		RDBMSQuery<Market> q = new RDBMSQuery<>(params, dtoFilter);
		// retrieve the possible cached object
		Optional<List<Market>> cachedResults = cachingService.get("all", params, () -> dao.get(q));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached markets");
			return Response.serverError().build();
		}

		// return the results as a response
		return responseBuider.build("all", params, cachedResults.get());
	}

	/**
	 * Endpoint for /markets/ to post a new Market to the persistence layer.
	 * 
	 * @param market the market object to insert into the database.
	 * @return response for the browser
	 */
	@PUT
	@RolesAllowed({ "marketplace_market_put", "marketplace_admin_access" })
	public Response putMarket(Market market) {
		// add the object, and await the result
		dao.add(new RDBMSQuery<>(params, dtoFilter), Arrays.asList(market));

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

		// retrieve a cached version of the value for the current listing
		Optional<List<Market>> cachedResults = cachingService.get(marketId, params,
				() -> dao.get(new RDBMSQuery<>(params, dtoFilter)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached market for ID {}", marketId);
			return Response.serverError().build();
		}
		if (cachedResults.get().isEmpty()) {
			throw new NoResultException("Could not find any documents with ID " + marketId);
		}

		// return the results as a response
		return responseBuider.build(marketId, params, cachedResults.get());
	}

	/**
	 * Endpoint for /markets/\<marketId\> to retrieve a specific Market from the
	 * database.
	 * 
	 * @param marketId the market ID
	 * @return response for the browser
	 */
	@DELETE
	@RolesAllowed({ "marketplace_market_delete", "marketplace_admin_access" })
	@Path("/{marketId}")
	public Response delete(@PathParam("marketId") String marketId) {
		params.addParam(UrlParameterNames.ID, marketId);
		// delete the currently selected asset
		dao.delete(new RDBMSQuery<>(params, dtoFilter));
		// return the results as a response
		return Response.ok().build();
	}
}
