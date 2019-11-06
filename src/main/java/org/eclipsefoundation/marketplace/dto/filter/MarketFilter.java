/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.filter;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.expr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.bson.BsonArray;
import org.bson.conversions.Bson;
import org.eclipsefoundation.marketplace.dto.Market;
import org.eclipsefoundation.marketplace.model.RequestWrapper;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;
import org.eclipsefoundation.marketplace.namespace.DtoTableNames;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Variable;

/**
 * Filter implementation for the {@linkplain Market} class.
 * 
 * @author Martin Lowe
 */
@ApplicationScoped
public class MarketFilter implements DtoFilter<Market> {

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
		return filters;
	}

	@Override
	public List<Bson> getAggregates(RequestWrapper wrap) {
		List<Bson> aggs = new ArrayList<>();

		String tempFieldName = "tmp";
		List<Bson> pipeline = new ArrayList<>();
		// match the listings on the given market_id
		pipeline.add(
				Aggregates.match(expr(eq("$in", Arrays.asList("$$market_id", "$" + DatabaseFieldNames.MARKET_IDS)))));
		// suppress all fields except category_ids
		pipeline.add(Aggregates.project(
				Projections.fields(Projections.excludeId(), Projections.include(DatabaseFieldNames.CATEGORY_IDS))));

		// set up a var reference for the _id
		Variable<String> id = new Variable<>("market_id", "$" + DatabaseFieldNames.DOCID);
		// lookup all category IDS from listings with the given market ID
		aggs.add(Aggregates.lookup(DtoTableNames.LISTING.getTableName(), Arrays.asList(id), pipeline, tempFieldName));
		// explode all category IDS for collection
		aggs.add(Aggregates.unwind("$" + tempFieldName));

		// flatten categories using projection, and retain original data through data
		// field
		aggs.add(Aggregates.group("$_id", Accumulators.first("data", "$$ROOT"),
				Accumulators.push(tempFieldName, "$" + tempFieldName + "." + DatabaseFieldNames.CATEGORY_IDS)));

		// no reduction shortcuts in driver, build documents from scratch
		// in operation merges multiple lists using sets to deduplicate
		Bson inOperation = eq("$setUnion", Arrays.asList("$$value", "$$this"));
		Bson reductionOptions = eq(tempFieldName, eq("$reduce",
				and(eq("input", "$" + tempFieldName), eq("initialValue", new BsonArray()), eq("in", inOperation))));

		// using projections, retain data-root + tmp category IDS and reduce them
		aggs.add(Aggregates.project(Projections.fields(Projections.include("data"), reductionOptions)));

		// create custom array as mergeObjects uses non-standard syntax
		BsonArray ba = BsonArray.parse("[ '$data', {'" + tempFieldName + "': '$" + tempFieldName + "'}]");
		// replaceRoot to restore original root data + set data for category IDs
		aggs.add(Aggregates.replaceRoot(eq("$mergeObjects", ba)));

		// adds a $lookup aggregate, joining categories on categoryIDS as "categories"
		aggs.add(Aggregates.lookup(DtoTableNames.CATEGORY.getTableName(), tempFieldName, DatabaseFieldNames.DOCID,
				"categories"));

		// remove the unneeded temporary field
		aggs.add(Aggregates.project(Projections.exclude(tempFieldName)));
		return aggs;
	}

	@Override
	public Class<Market> getType() {
		return Market.class;
	}

}
