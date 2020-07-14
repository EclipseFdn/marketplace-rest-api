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
import java.util.UUID;

import javax.annotation.security.PermitAll;
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

import org.eclipsefoundation.core.helper.ResponseHelper;
import org.eclipsefoundation.core.model.Error;
import org.eclipsefoundation.core.model.RequestWrapper;
import org.eclipsefoundation.core.namespace.DefaultUrlParameterNames;
import org.eclipsefoundation.core.service.CachingService;
import org.eclipsefoundation.marketplace.dto.Install;
import org.eclipsefoundation.marketplace.dto.InstallMetrics;
import org.eclipsefoundation.marketplace.dto.MetricPeriod;
import org.eclipsefoundation.marketplace.helper.StreamHelper;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.eclipsefoundation.persistence.dao.PersistenceDao;
import org.eclipsefoundation.persistence.dto.filter.DtoFilter;
import org.eclipsefoundation.persistence.model.RDBMSQuery;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource for retrieving installs + statistics from the MongoDB instance.
 * 
 * @author Martin Lowe
 */
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/installs")
public class InstallResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(InstallResource.class);

	@Inject
	PersistenceDao dao;
	@Inject
	RequestWrapper wrapper;
	@Inject
	ResponseHelper responseBuider;

	// insert required filters for different objects + states
	@Inject
	DtoFilter<Install> dtoFilter;
	
	@Inject
	DtoFilter<MetricPeriod> periodFilter;
	@Inject
	DtoFilter<InstallMetrics> metricFilter;


	// Inject 2 caching service references, as we want to cache count results.
	@Inject
	CachingService<Long> countCache;
	@Inject
	CachingService<List<InstallMetrics>> installCache;

	/**
	 * Endpoint for /installs/${listingId} to retrieve install counts for a specific
	 * listing from the database with given filters.
	 * 
	 * @param listingId the listing ID
	 * @return response for the browser
	 */
	@GET
	@PermitAll
	@Path("/{listingId}")
	public Response selectInstallCount(@PathParam("listingId") String listingId) {
		wrapper.addParam(DefaultUrlParameterNames.ID, listingId);
		RDBMSQuery<Install> q = new RDBMSQuery<>(wrapper, dtoFilter);
		Optional<Long> cachedResults = countCache.get(listingId, wrapper,
				() -> StreamHelper.awaitCompletionStage(dao.count(q)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached install metrics for ID {}", listingId);
			return Response.serverError().build();
		}

		// return the results as a response
		return responseBuider.build(listingId, wrapper, cachedResults.get());
	}

	/**
	 * 
	 * Endpoint for /installs/${listingId}/${version} to retrieve install counts for
	 * a specific listing version from the database.
	 * 
	 * @param listingId the listing ID
	 * @param version   the listing version number
	 * @return response for the browser
	 */
	@GET
	@PermitAll
	@Path("/{listingId}/{version}")
	public Response selectInstallCount(@PathParam("listingId") String listingId, @PathParam("version") String version) {
		wrapper.addParam(DefaultUrlParameterNames.ID, listingId);
		wrapper.addParam(UrlParameterNames.VERSION, version);
		RDBMSQuery<Install> q = new RDBMSQuery<>(wrapper, dtoFilter);
		Optional<Long> cachedResults = countCache.get(getCompositeKey(listingId, version), wrapper,
				() -> StreamHelper.awaitCompletionStage(dao.count(q)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached listing for ID {}", listingId);
			return Response.serverError().build();
		}

		// return the results as a response
		return responseBuider.build(getCompositeKey(listingId, version), wrapper, cachedResults.get());
	}

	/**
	 * Endpoint for /installs/${listingId}/metrics to retrieve install metrics for a
	 * specific listing from the database.
	 * 
	 * @param listingId the listing ID
	 * @return response for the browser
	 */
	@GET
	@PermitAll
	@Path("/{listingId}/metrics")
	public Response selectInstallMetrics(@PathParam("listingId") String listingId) {
		wrapper.addParam(DefaultUrlParameterNames.ID, listingId);
		RDBMSQuery<InstallMetrics> q = new RDBMSQuery<>(wrapper, metricFilter);
		Optional<List<InstallMetrics>> cachedResults = installCache.get(listingId, wrapper,
				() -> dao.get(q));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached install metrics for ID {}", listingId);
			return Response.serverError().build();
		}

		// return the results as a response
		return Response.ok(cachedResults.get()).build();
	}

	/**
	 * Endpoint for /installs/${listingId}/${version} to post install metrics for a
	 * specific listing version to a database.
	 * 
	 * @param listingId the listing ID
	 * @param version   the listing version number
	 * @return response for the browser
	 */
	@POST
	@PermitAll
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
		record.setListingId(UUID.fromString(listingId));
		record.setVersion(version);

		// create the query wrapper to pass to DB dao
		RDBMSQuery<Install> q = new RDBMSQuery<>(wrapper, dtoFilter);

		// add the object, and await the result
		dao.add(q, Arrays.asList(record));

		// return the results as a response
		return Response.ok().build();
	}

	private String getCompositeKey(String listingId, String version) {
		return listingId + ':' + version;
	}
}
