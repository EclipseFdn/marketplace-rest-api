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
import org.eclipsefoundation.marketplace.dto.ErrorReport;
import org.eclipsefoundation.marketplace.model.QueryParameters;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;

import com.mongodb.client.model.Filters;

/**
 * Filter implementation for the ErrorReport class.
 * 
 * @author Martin Lowe
 */
@ApplicationScoped
public class ErrorReportFilter implements DtoFilter<ErrorReport> {

	@Override
	public List<Bson> getFilters(QueryParameters params, String root) {
		List<Bson> filters = new ArrayList<>();

		// ErrorReport ID check
		Optional<String> id = params.getFirstIfPresent(UrlParameterNames.ID.getParameterName());
		if (id.isPresent()) {
			filters.add(Filters.eq(DatabaseFieldNames.DOCID, id.get()));
		}

		// select by multiple IDs
		List<String> ids = params.getValues(UrlParameterNames.IDS.getParameterName());
		if (!ids.isEmpty()) {
			filters.add(Filters.in(DatabaseFieldNames.DOCID, ids));
		}

		// listing ID check
		Optional<String> listingId = params.getFirstIfPresent(UrlParameterNames.LISTING_ID.getParameterName());
		if (listingId.isPresent()) {
			filters.add(Filters.eq(DatabaseFieldNames.LISTING_ID, listingId.get()));
		}

		// listing ID check
		Optional<String> isRead = params.getFirstIfPresent(UrlParameterNames.READ.getParameterName());
		if (isRead.isPresent()) {
			filters.add(Filters.eq(DatabaseFieldNames.ERROR_READ, Boolean.valueOf(isRead.get())));
		}
		
		// select by feature ID
		List<String> featureId = params.getValues(UrlParameterNames.FEATURE_ID.getParameterName());
		if (!featureId.isEmpty()) {
			filters.add(Filters.in(DatabaseFieldNames.ERROR_FEATURE_IDS, featureId));
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
		return Collections.emptyList();
	}

	@Override
	public Class<ErrorReport> getType() {
		return ErrorReport.class;
	}
}
