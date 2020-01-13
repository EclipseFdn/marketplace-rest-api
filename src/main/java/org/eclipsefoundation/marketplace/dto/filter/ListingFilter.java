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
import javax.inject.Inject;

import org.bson.conversions.Bson;
import org.eclipsefoundation.marketplace.dto.Listing;
import org.eclipsefoundation.marketplace.dto.ListingVersion;
import org.eclipsefoundation.marketplace.model.QueryParameters;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;
import org.eclipsefoundation.marketplace.namespace.DtoTableNames;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UnwindOptions;

/**
 * Filter implementation for the {@linkplain Listing} class.
 * 
 * @author Martin Lowe
 */
@ApplicationScoped
public class ListingFilter implements DtoFilter<Listing> {

	@Inject
	DtoFilter<ListingVersion> listingVersionFilter;
	
	@Override
	public List<Bson> getFilters(QueryParameters params, String root) {
		List<Bson> filters = new ArrayList<>();
		// perform following checks only if there is no doc root
		if (root == null) {
			// ID check
			Optional<String> id = params.getFirstIfPresent(UrlParameterNames.ID.getParameterName());
			if (id.isPresent()) {
				filters.add(Filters.eq(DatabaseFieldNames.DOCID, id.get()));
			}
		}

		// select by multiple IDs
		List<String> ids = params.getValues(UrlParameterNames.IDS.getParameterName());
		if (!ids.isEmpty()) {
			filters.add(Filters.in(DatabaseFieldNames.DOCID, ids));
		}

		// Listing license type check
		Optional<String> licType = params.getFirstIfPresent(DatabaseFieldNames.LICENSE_TYPE);
		if (licType.isPresent()) {
			filters.add(Filters.eq(DatabaseFieldNames.LICENSE_TYPE, licType.get()));
		}

		// select by multiple tags
		List<String> tags = params.getValues(UrlParameterNames.TAGS.getParameterName());
		if (!tags.isEmpty()) {
			filters.add(Filters.in(DatabaseFieldNames.LISTING_TAGS + ".title", tags));
		}

		// text search
		Optional<String> text = params.getFirstIfPresent(UrlParameterNames.QUERY_STRING.getParameterName());
		if (text.isPresent()) {
			filters.add(Filters.text(text.get()));
		}
		return filters;
	}

	@Override
	public List<Bson> getAggregates(QueryParameters params) {
		List<Bson> aggs = new ArrayList<>();
		// adds a $lookup aggregate, joining categories on categoryIDS as "categories"
		aggs.add(Aggregates.lookup(DtoTableNames.LISTING_VERSION.getTableName(), DatabaseFieldNames.DOCID, DatabaseFieldNames.LISTING_ID,
				DatabaseFieldNames.LISTING_VERSIONS));
		Bson filters = listingVersionFilter.wrapFiltersToAggregate(params, DatabaseFieldNames.LISTING_VERSIONS);
		if (filters != null) {
			aggs.add(filters);
		}
		aggs.add(Aggregates.lookup(DtoTableNames.CATEGORY.getTableName(), DatabaseFieldNames.CATEGORY_IDS,
				DatabaseFieldNames.DOCID, DatabaseFieldNames.LISTING_CATEGORIES));
		List<String> marketIds = params.getValues(UrlParameterNames.MARKET_IDS.getParameterName());
		if (!marketIds.isEmpty()) {
			aggs.add(Aggregates.match(Filters.in("categories.market_ids", marketIds)));
		}
		// adds a $lookup aggregate, joining install metrics on ids as "installs"
		aggs.add(Aggregates.lookup(DtoTableNames.INSTALL_METRIC.getTableName(), DatabaseFieldNames.DOCID,
				DatabaseFieldNames.DOCID, "installs"));
		// unwinds the installs out of arrays
		aggs.add(Aggregates.unwind("$installs", new UnwindOptions().preserveNullAndEmptyArrays(true)));
		// push the installs counts to the listing, and remove the installs merged in
		aggs.add(Aggregates.addFields(new Field<String>(DatabaseFieldNames.RECENT_INSTALLS, "$installs.offset_0.count"),
				new Field<String>(DatabaseFieldNames.TOTAL_INSTALLS, "$installs.count")));
		aggs.add(Aggregates.project(Projections.exclude("installs")));
		return aggs;
	}

	@Override
	public Class<Listing> getType() {
		return Listing.class;
	}
}
