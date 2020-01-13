/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.request;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.eclipsefoundation.marketplace.model.SortOrder;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;

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

	@Context
	HttpServletRequest request;

	@Context
	HttpServletResponse response;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// check for random sort order, which always bypasses cache
		String[] sortVals = request.getParameterValues(UrlParameterNames.SORT.getParameterName());
		if (sortVals != null) {
			for (String sortVal : sortVals) {
				// check if the sort order for request matches RANDOM
				if (SortOrder.RANDOM.equals(SortOrder.getOrderFromValue(sortVal))) {
					setBypass();
					return;
				}
			}
		}
		request.setAttribute(ATTRIBUTE_NAME, Boolean.FALSE);
	}

	private void setBypass() {
		request.setAttribute(ATTRIBUTE_NAME, Boolean.TRUE);
		// no-store should be used as cache bypass should not return
		response.setHeader("Cache-Control", "no-store");
	}
}
