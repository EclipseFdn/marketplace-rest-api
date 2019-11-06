/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.bson.conversions.Bson;
import org.eclipsefoundation.marketplace.dto.ListingVersion;
import org.eclipsefoundation.marketplace.model.RequestWrapper;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;

import com.mongodb.client.model.Filters;

/**
 * Filter implementation for the {@linkplain ListingVersion} class.
 * 
 * @author Martin Lowe
 *
 */
@ApplicationScoped
public class ListingVersionFilter implements DtoFilter<ListingVersion> {

	@Override
	public List<Bson> getFilters(RequestWrapper wrap, String root) {
		List<Bson> filters = new ArrayList<>();
		// perform following checks only if there is no doc root
		if (root == null) {
			// ID check
			Optional<String> id = wrap.getFirstParam(UrlParameterNames.ID);
			if (id.isPresent()) {
				filters.add(Filters.eq(DatabaseFieldNames.DOCID, id.get()));
			}
		}

		// solution version - OS filter
		Optional<String> os = wrap.getFirstParam(UrlParameterNames.OS);
		if (os.isPresent()) {
			filters.add(Filters.eq(getPath(root, "platforms"), os.get()));
		}
		// solution version - eclipse version
		Optional<String> eclipseVersion = wrap.getFirstParam(UrlParameterNames.ECLIPSE_VERSION);
		if (eclipseVersion.isPresent()) {
			filters.add(Filters.eq(getPath(root, "compatible_versions"), eclipseVersion.get()));
		}
		// TODO this sorts by naturally by character rather than by actual number (e.g.
		// 1.9 is technically greater than 1.10)
		// solution version - Java version
		Optional<String> javaVersion = wrap.getFirstParam(UrlParameterNames.JAVA_VERSION);
		if (javaVersion.isPresent()) {
			filters.add(Filters.gte(getPath(root, "min_java_version"), javaVersion.get()));
		}
		
		return filters;
	}

	@Override
	public List<Bson> getAggregates(RequestWrapper wrap) {
		return Collections.emptyList();
	}

	@Override
	public Class<ListingVersion> getType() {
		return ListingVersion.class;
	}

}
