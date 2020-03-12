/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.core.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipsefoundation.core.namespace.RequestHeaderNames;
import org.eclipsefoundation.core.service.CachingService;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

/**
 * Resource that gives quick access to caching layer to see and clear data.
 * Requires a preset secret token to be passed via request headers.
 * 
 * @author Martin Lowe
 */
@Path("/cache")
@RolesAllowed("admin")
@Produces(MediaType.APPLICATION_JSON)
public class CacheResource {

	@ConfigProperty(name = "eclipse.secret.token")
	String token;

	@Inject
	Instance<CachingService<?>> cacheServices;

	@GET
	public Response getActiveCacheEntries() {
		List<Set<String>> cacheEntries = new ArrayList<>();
		for (CachingService<?> cs : cacheServices) {
			cacheEntries.add(cs.getCacheKeys());
		}
		return Response.ok(cacheEntries).build();
	}

	@DELETE
	@Path("/{key}")
	public Response removeCacheEntry(@PathParam("key") String key,
			@HeaderParam(RequestHeaderNames.ACCESS_TOKEN) String token) {
		if (!this.token.equals(token)) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		cacheServices.forEach(cs -> cs.remove(key));
		return Response.ok().build();
	}

	@DELETE
	@Path("/all")
	public Response clearCaches(@HeaderParam(RequestHeaderNames.ACCESS_TOKEN) String token) {
		if (!this.token.equals(token)) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		cacheServices.forEach(CachingService::removeAll);
		return Response.ok().build();
	}
}
