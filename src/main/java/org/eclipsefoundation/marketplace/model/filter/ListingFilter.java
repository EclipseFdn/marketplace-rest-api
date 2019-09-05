/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.model.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.conversions.Bson;
import org.eclipsefoundation.marketplace.dto.Listing;
import org.eclipsefoundation.marketplace.model.QueryParams;
import org.eclipsefoundation.marketplace.namespace.MongoFieldNames;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;

import com.mongodb.client.model.Filters;

/**
 * Filter implementation for the Listing class. Checks the following fields:
 * 
 * 	<ul>
 * 		<li>platform_version
 * 		<li>java_version
 * 		<li>os
 * 		<li>license_type
 * 		<li>q
 * 		<li>ids
 * 		<li>tags
 * 	</ul>
 * 
 * @author Martin Lowe
 */
public class ListingFilter implements DtoFilter<Listing> {

	@Override
	public List<Bson> getFilters(QueryParams qps) {
		List<Bson> filters = new ArrayList<>();

		// Listing ID check
		Optional<String> id = qps.getFirstParam(MongoFieldNames.LISTING_ID);
		if (id.isPresent()) {
			filters.add(Filters.eq(MongoFieldNames.LISTING_ID, Long.valueOf(id.get())));
		}
		
		// select by multiple IDs
		List<String> ids = qps.getParams(UrlParameterNames.IDS);
		if (!ids.isEmpty()) {
			filters.add(Filters.in(MongoFieldNames.LISTING_ID, ids));
		}

		// Listing license type check
		Optional<String> licType = qps.getFirstParam(MongoFieldNames.LICENSE_TYPE);
		if (licType.isPresent()) {
			filters.add(Filters.eq(MongoFieldNames.LICENSE_TYPE, licType.get()));
		}
		
		// handle version sub document selection
		List<Bson> versionFilters = new ArrayList<>();
		// solution version - OS filter
		Optional<String> os = qps.getFirstParam(UrlParameterNames.OS);
		if (os.isPresent()) {
			versionFilters.add(Filters.eq("platforms", os.get()));
		}
		// solution version - eclipse version 
		Optional<String> eclipseVersion = qps.getFirstParam(UrlParameterNames.ECLIPSE_VERSION);
		if (eclipseVersion.isPresent()) {
			versionFilters.add(Filters.eq("compatible_versions", eclipseVersion.get()));
		}
		// TODO this sorts by naturally by character rather than by actual number (e.g. 1.9 is technically greater than 1.10)
		// solution version - Java version 
		Optional<String> javaVersion = qps.getFirstParam(UrlParameterNames.JAVA_VERSION);
		if (javaVersion.isPresent()) {
			versionFilters.add(Filters.gte("min_java_version", javaVersion.get()));
		}
		if (!versionFilters.isEmpty()) {
			filters.add(Filters.elemMatch("versions", Filters.and(versionFilters)));
		}
		
		// select by multiple tags
		List<String> tags = qps.getParams(UrlParameterNames.TAGS);
		if (!tags.isEmpty()) {
			filters.add(Filters.in(MongoFieldNames.LISTING_TAGS + ".title", tags));
		}

		// text search
		Optional<String> text = qps.getFirstParam(UrlParameterNames.QUERY_STRING);
		if (text.isPresent()) {
			filters.add(Filters.text(text.get()));
		}
		
		return filters;
	}

	@Override
	public Class<Listing> getType() {
		return Listing.class;
	}
}
