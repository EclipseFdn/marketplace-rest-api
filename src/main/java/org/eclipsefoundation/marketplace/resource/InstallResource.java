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

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipsefoundation.marketplace.dao.MongoDao;
import org.eclipsefoundation.marketplace.dto.Install;
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
	RequestWrapper wrapper;
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
	@PermitAll
	@Path("/{listingId}")
	public Response selectInstallMetrics(@PathParam("listingId") String listingId) {
		wrapper.addParam(UrlParameterNames.ID, listingId);
		MongoQuery<Install> q = new MongoQuery<>(wrapper, dtoFilter, installCache);
		Optional<Long> cachedResults = countCache.get(listingId, wrapper,
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
	@PermitAll
	@Path("/{listingId}/{version}")
	public Response selectInstallMetrics(@PathParam("listingId") String listingId,
			@PathParam("version") String version) {
		wrapper.addParam(UrlParameterNames.ID, listingId);
		wrapper.addParam(UrlParameterNames.VERSION, version);
		MongoQuery<Install> q = new MongoQuery<>(wrapper, dtoFilter, installCache);
		Optional<Long> cachedResults = countCache.get(getCompositeKey(listingId, version), wrapper,
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
	@RolesAllowed({ "marketplace_install_put", "marketplace_admin_access" })
	@Path("/{listingId}/{version}")
	public Response postInstallMetrics(@PathParam("listingId") String listingId, @PathParam("version") String version,
			Install installDetails) {
		Install record = null;

		// check that connection was opened by MPC, and check for install information
		// from user agent
		if (wrapper.getUserAgent().isValid()) {
			record = wrapper.getUserAgent().generateInstallRecord();
		} else if (wrapper.getUserAgent().isFromMPC()) {
			if (installDetails == null) {
				return new Error(Status.BAD_REQUEST, "Install data could not be read from request body").asResponse();
			}
			record = installDetails;
		} else {
			LOGGER.warn("Rebuffed request to post install from request: {}", wrapper);
			return new Error(Status.FORBIDDEN, "Installs cannot be posted directly from consumer applications")
					.asResponse();
		}

		// update the install details to reflect the current request
		record.setInstallDate(new Date(System.currentTimeMillis()));
		record.setListingId(listingId);
		record.setVersion(version);

		// create the query wrapper to pass to DB dao
		MongoQuery<Install> q = new MongoQuery<>(wrapper, dtoFilter, installCache);

		// add the object, and await the result
		StreamHelper.awaitCompletionStage(dao.add(q, Arrays.asList(record)));

		// return the results as a response
		return Response.ok().build();
	}

	private String getCompositeKey(String listingId, String version) {
		return listingId + ':' + version;
	}
}
