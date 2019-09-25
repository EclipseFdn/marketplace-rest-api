/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.eclipsefoundation.marketplace.model.ResourceDataType;

/**
 * Pre-processes the request to inject the datatype class name into the request.
 * This is later used in reflection to circumvent type erasure within Query
 * objects.
 * 
 * @author Martin Lowe
 */
@Provider
public class AnnotationClassInjectionFilter implements ContainerRequestFilter {
	public static final String ATTRIBUTE_NAME = "enclosed-data-type";

    @Context
    HttpServletRequest request;
    
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		List<Object> resources = requestContext.getUriInfo().getMatchedResources();
		if (!resources.isEmpty()) {
			// Quarkus compiles wrapper classes around beans, needs superclass call to get original class
			Class<?> clazz = resources.get(0).getClass().getSuperclass();
			// get the resource data type
			ResourceDataType[] dataTypes = clazz.getAnnotationsByType(ResourceDataType.class);
			if (dataTypes.length > 0) {
				ResourceDataType firstType = dataTypes[0];
				request.setAttribute(ATTRIBUTE_NAME, firstType.value());
			}
		}
	}
}
