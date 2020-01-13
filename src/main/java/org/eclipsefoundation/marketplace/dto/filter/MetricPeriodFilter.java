/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.conversions.Bson;
import org.eclipsefoundation.marketplace.dto.MetricPeriod;
import org.eclipsefoundation.marketplace.model.QueryParameters;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

/**
 * Filter implementation for the {@linkplain MetricPeriod} class.
 * 
 * @author Martin Lowe
 *
 */
@ApplicationScoped
public class MetricPeriodFilter implements DtoFilter<MetricPeriod> {

	@Override
	public List<Bson> getFilters(QueryParameters params, String root) {
		return Collections.emptyList();
	}

	@Override
	public List<Bson> getAggregates(QueryParameters params) {
		// check that we have required fields first
		Optional<String> startDate = params.getFirstIfPresent(UrlParameterNames.START.getParameterName());
		Optional<String> endDate = params.getFirstIfPresent(UrlParameterNames.END.getParameterName());
		List<Bson> aggregates = new ArrayList<>();
		if (startDate.isPresent() && endDate.isPresent()) {
			// check for all listings that are after the start date
			BsonArray startDateComparison = new BsonArray();
			startDateComparison.add(new BsonString("$" + DatabaseFieldNames.INSTALL_DATE));
			BsonDocument startDoc = new BsonDocument();
			startDoc.append("dateString", new BsonString(startDate.get()));
			startDoc.append("format", new BsonString("%Y-%m-%dT%H:%M:%SZ"));
			// build doc to convert string to date to be used in query
			BsonDocument startDateConversion = new BsonDocument("$dateFromString", startDoc);
			startDateComparison.add(startDateConversion);

			// check for all listings that are before the end date
			BsonArray endDateComparison = new BsonArray();
			endDateComparison.add(new BsonString("$" + DatabaseFieldNames.INSTALL_DATE));
			BsonDocument endDoc = new BsonDocument();
			endDoc.append("dateString", new BsonString(endDate.get()));
			endDoc.append("format", new BsonString("%Y-%m-%dT%H:%M:%SZ"));
			// build doc to convert string to date to be used in query
			BsonDocument endDateConversion = new BsonDocument("$dateFromString", endDoc);
			endDateComparison.add(endDateConversion);
			
			// add the 2 date comparisons to the pipeline
			aggregates.add(Aggregates.match(Filters
					.expr(Filters.eq("$and", new BsonArray(Arrays.asList(new BsonDocument("$gte", startDateComparison),
							new BsonDocument("$lte", endDateComparison)))))));
			// group the results by listing ID
			aggregates.add(Aggregates.group("$listing_id", new BsonField(DatabaseFieldNames.PERIOD_COUNT, Filters.eq("$sum", 1))));
			// project the start + end date into the end result
			aggregates.add(Aggregates.project(Projections.fields(Projections.include(DatabaseFieldNames.PERIOD_COUNT),
					Projections.computed(DatabaseFieldNames.PERIOD_START, startDateConversion),
					Projections.computed(DatabaseFieldNames.PERIOD_END, endDateConversion))));
		} else {
			// count all existing installs and group them by listing ID
			aggregates.add(Aggregates.group("$listing_id", new BsonField(DatabaseFieldNames.PERIOD_COUNT, Filters.eq("$sum", 1))));
		}
		
		return aggregates;
	}

	@Override
	public Class<MetricPeriod> getType() {
		return MetricPeriod.class;
	}

	@Override
	public boolean useLimit() {
		return false;
	}
}
