/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.resource;

import java.sql.Date;
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
import org.eclipsefoundation.marketplace.dto.Install;
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
 * Resource for retrieving installs + statistics from the MongoDB instance.
 * 
 * @author Martin Lowe
 */
@RequestScoped
@ResourceDataType(Install.class)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/installs")
public class InstallResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(ListingResource.class);

	@Inject
	MongoDao dao;
	@Inject
	RequestWrapper params;
	@Inject
	DtoFilter<Install> dtoFilter;

	// Inject 2 caching service references, as we want to cache count results.
	@Inject
	CachingService<Long> countCache;
	@Inject
	CachingService<List<Install>> installCache;
	
	/**
	 * Endpoint for /listing/\<listingId\>/installs to retrieve install metrics for
	 * a specific listing from the database.
	 * 
	 * @param listingId int version of the listing ID
	 * @return response for the browser
	 */
	@GET
	@Path("/{listingId}")
	public Response selectInstallMetrics(@PathParam("listingId") String listingId) {
		params.addParam(UrlParameterNames.ID, listingId);
		MongoQuery<Install> q = new MongoQuery<>(params, dtoFilter, installCache);
		Optional<Long> cachedResults = countCache.get(listingId, params,
				() -> StreamHelper.awaitCompletionStage(dao.count(q)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached listing for ID {}", listingId);
			return Response.serverError().build();
		}

		// return the results as a response
		return Response.ok(cachedResults.get()).build();
	}

	/**
	 * 
	 * Endpoint for /listing/\<listingId\>/installs/\<version\> to retrieve install
	 * metrics for a specific listing version from the database.
	 * 
	 * @param listingId int version of the listing ID
	 * @param version   int version of the listing version number
	 * @return response for the browser
	 */
	@GET
	@Path("/{listingId}/{version}")
	public Response selectInstallMetrics(@PathParam("listingId") String listingId, @PathParam("version") String version) {
		params.addParam(UrlParameterNames.ID, listingId);
		params.addParam(UrlParameterNames.VERSION, version);
		MongoQuery<Install> q = new MongoQuery<>(params, dtoFilter, installCache);
		Optional<Long> cachedResults = countCache.get(getCompositeKey(listingId, version), params,
				() -> StreamHelper.awaitCompletionStage(dao.count(q)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached listing for ID {}", listingId);
			return Response.serverError().build();
		}

		// return the results as a response
		return Response.ok(cachedResults.get()).build();
	}

	/**
	 * Endpoint for /listing/\<listingId\>/installs/\<version\> to post install
	 * metrics for a specific listing version to a database.
	 * 
	 * @param listingId int version of the listing ID
	 * @param version   int version of the listing version number
	 * @return response for the browser
	 */
	@POST
	@Path("/{listingId}/{version}")
	public Response postInstallMetrics(@PathParam("listingId") String listingId, @PathParam("version") String version,
			Install installDetails) {
		// update the install details to reflect the current request
		installDetails.setInstallDate(new Date(System.currentTimeMillis()));
		installDetails.setListingId(listingId);
		installDetails.setVersion(version);
		
		// create the query wrapper to pass to DB dao
		MongoQuery<Install> q = new MongoQuery<>(params, dtoFilter, installCache);

		// add the object, and await the result
		StreamHelper.awaitCompletionStage(dao.add(q, Arrays.asList(installDetails)));

		// return the results as a response
		return Response.ok().build();
	}
	
	private String getCompositeKey(String listingId, String version) {
		return listingId + ':' + version;
	}
}
