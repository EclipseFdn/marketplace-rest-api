/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.core.request;

import java.io.IOException;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

/**
 * Checks passed parameters and if any match one of the criteria for bypassing
 * caching, an attribute will be set to the request to skip cache requests and
 * instead directly return results.
 * 
 * @author Martin Lowe
 *
 */
@Provider
public class CacheBypassFilter implements ContainerRequestFilter {
	public static final String ATTRIBUTE_NAME = "bypass-cache";

	@Inject
	Instance<BypassCondition> conditions;

	@Context
	HttpServletRequest request;

	@Context
	HttpServletResponse response;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// check for random sort order, which always bypasses cache
		for (BypassCondition cond : conditions) {
			if (cond.matches(requestContext, request)) {
				setBypass();
				return;
			}
		}
		request.setAttribute(ATTRIBUTE_NAME, Boolean.FALSE);
	}

	private void setBypass() {
		request.setAttribute(ATTRIBUTE_NAME, Boolean.TRUE);
		// no-store should be used as cache bypass should not return
		response.setHeader("Cache-Control", "no-store");
	}

	/**
	 * Interface for adding a bypass condition to requests made against a given
	 * server.
	 * 
	 * @author Martin Lowe
	 *
	 */
	public interface BypassCondition {
		/**
		 * Tests the request context to check whether any data fetched for this request
		 * should bypass the cache layer.
		 * 
		 * @param requestContext the current requests container context
		 * @param request        raw servlet request containing more information about
		 *                       the request
		 * @return true if the request should bypass the cache, false otherwise.
		 */
		boolean matches(ContainerRequestContext requestContext, HttpServletRequest request);
	}
}
