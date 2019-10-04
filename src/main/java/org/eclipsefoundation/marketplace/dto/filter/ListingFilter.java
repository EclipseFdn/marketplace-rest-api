/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.bson.conversions.Bson;
import org.eclipsefoundation.marketplace.dto.Listing;
import org.eclipsefoundation.marketplace.model.RequestWrapper;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;
import org.eclipsefoundation.marketplace.namespace.DtoTableNames;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

/**
 * Filter implementation for the Listing class. Checks the following fields:
 * 
 * <ul>
 * <li>platform_version
 * <li>java_version
 * <li>os
 * <li>license_type
 * <li>q
 * <li>ids
 * <li>tags
 * </ul>
 * 
 * <p>
 * Injects categories into the results by way of aggregate pipeline.
 * </p>
 * 
 * @author Martin Lowe
 */
@ApplicationScoped
public class ListingFilter implements DtoFilter<Listing> {

	@Override
	public List<Bson> getFilters(RequestWrapper wrap) {
		List<Bson> filters = new ArrayList<>();

		// Listing ID check
		Optional<String> id = wrap.getFirstParam(UrlParameterNames.ID);
		if (id.isPresent()) {
			filters.add(Filters.eq(DatabaseFieldNames.DOCID, id.get()));
		}

		// select by multiple IDs
		List<String> ids = wrap.getParams(UrlParameterNames.IDS);
		if (!ids.isEmpty()) {
			filters.add(Filters.in(DatabaseFieldNames.DOCID, ids));
		}

		// Listing license type check
		Optional<String> licType = wrap.getFirstParam(DatabaseFieldNames.LICENSE_TYPE);
		if (licType.isPresent()) {
			filters.add(Filters.eq(DatabaseFieldNames.LICENSE_TYPE, licType.get()));
		}

		// handle version sub document selection
		List<Bson> versionFilters = new ArrayList<>();
		// solution version - OS filter
		Optional<String> os = wrap.getFirstParam(UrlParameterNames.OS);
		if (os.isPresent()) {
			versionFilters.add(Filters.eq("platforms", os.get()));
		}
		// solution version - eclipse version
		Optional<String> eclipseVersion = wrap.getFirstParam(UrlParameterNames.ECLIPSE_VERSION);
		if (eclipseVersion.isPresent()) {
			versionFilters.add(Filters.eq("compatible_versions", eclipseVersion.get()));
		}
		// TODO this sorts by naturally by character rather than by actual number (e.g.
		// 1.9 is technically greater than 1.10)
		// solution version - Java version
		Optional<String> javaVersion = wrap.getFirstParam(UrlParameterNames.JAVA_VERSION);
		if (javaVersion.isPresent()) {
			versionFilters.add(Filters.gte("min_java_version", javaVersion.get()));
		}
		if (!versionFilters.isEmpty()) {
			filters.add(Filters.elemMatch("versions", Filters.and(versionFilters)));
		}

		// select by multiple tags
		List<String> tags = wrap.getParams(UrlParameterNames.TAGS);
		if (!tags.isEmpty()) {
			filters.add(Filters.in(DatabaseFieldNames.LISTING_TAGS + ".title", tags));
		}

		// text search
		Optional<String> text = wrap.getFirstParam(UrlParameterNames.QUERY_STRING);
		if (text.isPresent()) {
			filters.add(Filters.text(text.get()));
		}
		return filters;
	}

	@Override
	public List<Bson> getAggregates(RequestWrapper wrap) {
		List<Bson> aggs = new ArrayList<>();
		// adds a $lookup aggregate, joining categories on categoryIDS as "categories"
		aggs.add(Aggregates.lookup(DtoTableNames.CATEGORY.getTableName(), DatabaseFieldNames.CATEGORY_IDS, DatabaseFieldNames.DOCID,
				DatabaseFieldNames.LISTING_CATEGORIES));
		List<String> marketIds = wrap.getParams(UrlParameterNames.MARKET_IDS);
		if (!marketIds.isEmpty()) {
			aggs.add(Aggregates.match(Filters.in("categories.market_ids", marketIds)));
		}
		return aggs;
	}

	@Override
	public Class<Listing> getType() {
		return Listing.class;
	}
}
