/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
import org.eclipsefoundation.marketplace.dto.InstallMetrics;
import org.eclipsefoundation.marketplace.dto.MetricPeriod;
import org.eclipsefoundation.marketplace.dto.filter.DtoFilter;
import org.eclipsefoundation.marketplace.helper.DateTimeHelper;
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
	private static final Logger LOGGER = LoggerFactory.getLogger(ListingResource.class);

	@Inject
	MongoDao dao;
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
		wrapper.addParam(UrlParameterNames.ID.getParameterName(), listingId);
		MongoQuery<Install> q = new MongoQuery<>(wrapper, dtoFilter);
		Optional<Long> cachedResults = countCache.get(listingId, wrapper,
				null, () -> StreamHelper.awaitCompletionStage(dao.count(q)));
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
		wrapper.addParam(UrlParameterNames.ID.getParameterName(), listingId);
		wrapper.addParam(UrlParameterNames.VERSION.getParameterName(), version);
		MongoQuery<Install> q = new MongoQuery<>(wrapper, dtoFilter);
		Optional<Long> cachedResults = countCache.get(getCompositeKey(listingId, version), wrapper,
				null, () -> StreamHelper.awaitCompletionStage(dao.count(q)));
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
		wrapper.addParam(UrlParameterNames.ID.getParameterName(), listingId);
		MongoQuery<InstallMetrics> q = new MongoQuery<>(wrapper, metricFilter);
		Optional<List<InstallMetrics>> cachedResults = installCache.get(listingId, wrapper,
				null, () -> StreamHelper.awaitCompletionStage(dao.get(q)));
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
		record.setListingId(listingId);
		record.setVersion(version);

		// create the query wrapper to pass to DB dao
		MongoQuery<Install> q = new MongoQuery<>(wrapper, dtoFilter);

		// add the object, and await the result
		StreamHelper.awaitCompletionStage(dao.add(q, Arrays.asList(record)));

		// return the results as a response
		return Response.ok().build();
	}

	/**
	 * Regenerates the install_metrics table, using the install table as the base.
	 * Creates 12 columns for metric periods, to provide users with a count of
	 * installs over the past year. For months with no installs, an empty metric
	 * period is generated to avoid gaps in the stats.
	 * 
	 * TODO: This should be moved to a separate job resource and be callable through
	 * a service that tracks last run time. https://github.com/EclipseFdn/marketplace-rest-api/issues/54
	 * 
	 * @return an OK response when finished
	 */
	@GET
	@RolesAllowed("marketplace_admin_access")
	@Path("/generate_metrics")
	public Response generateInstallStats() {
		List<CompletionStage<List<MetricPeriod>>> stages = new ArrayList<>();
		// get total install count for all listings available
		Map<String, Integer> overallCounts = new HashMap<>();
		CompletionStage<List<MetricPeriod>> stage = dao.get(new MongoQuery<>(wrapper, periodFilter));
		stage.whenComplete((metrics, e) -> {
			// if theres an error, immediately stop processing
			if (e != null) {
				throw new RuntimeException(e);
			}
			// for each metric, insert total count into the map
			for (MetricPeriod metric : metrics) {
				overallCounts.put(metric.getListingId(), metric.getCount());
			}
		});
		stages.add(stage);

		// use thread safe map impl for storing metrics
		Map<String, List<MetricPeriod>> r = new ConcurrentHashMap<>();
		// get the last 12 months of stats for installs asynchronously
		Calendar c = Calendar.getInstance();
		for (int m = 0; m < 12; m++) {
			// set up the date ranges for the current call
			String end = DateTimeHelper.toRFC3339(c.getTime());
			c.add(Calendar.MONTH, -1);
			String start = DateTimeHelper.toRFC3339(c.getTime());
			wrapper.setParam(UrlParameterNames.END.getParameterName(), end);
			wrapper.setParam(UrlParameterNames.START.getParameterName(), start);

			// create the query wrapper to pass to DB dao. No cache needed as this info
			// won't be cached
			MongoQuery<MetricPeriod> q = new MongoQuery<>(wrapper, periodFilter);
			// run query, and set up a completion activity to record data
			CompletionStage<List<MetricPeriod>> statStage = dao.get(q);
			statStage.whenComplete((metrics, e) -> {
				// if theres an error, immediately stop processing
				if (e != null) {
					throw new RuntimeException(e);
				}
				// for each metric, insert into the map
				for (MetricPeriod metric : metrics) {
					r.computeIfAbsent(metric.getListingId(), k -> new ArrayList<MetricPeriod>()).add(metric);
				}
			});
			// keep stage reference to check when complete
			stages.add(statStage);
		}
		// wrap futures and await all calls to finish
		StreamHelper.awaitCompletionStage(CompletableFuture.allOf(stages.toArray(new CompletableFuture[] {})));

		// convert the map to a list of install metric objects, adding in total count
		List<InstallMetrics> installMetrics = r.entrySet().stream().map(entry -> new InstallMetrics(entry.getKey(),
				entry.getValue(), overallCounts.getOrDefault(entry.getKey(), 0))).collect(Collectors.toList());

		// push the content to the database, and await for it to finish
		StreamHelper.awaitCompletionStage(dao.add(new MongoQuery<>(wrapper, metricFilter), installMetrics));
		// return the results as a response
		return Response.ok().build();
	}

	private String getCompositeKey(String listingId, String version) {
		return listingId + ':' + version;
	}
}
