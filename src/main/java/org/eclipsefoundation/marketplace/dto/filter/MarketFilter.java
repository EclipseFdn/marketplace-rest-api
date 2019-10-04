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

import javax.enterprise.context.ApplicationScoped;

import org.bson.conversions.Bson;
import org.eclipsefoundation.marketplace.dto.Market;
import org.eclipsefoundation.marketplace.model.RequestWrapper;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;
import org.eclipsefoundation.marketplace.namespace.DtoTableNames;

import com.mongodb.client.model.Aggregates;

/**
 * Filter implementation for the {@linkplain Market} class.
 * 
 * @author Martin Lowe
 */
@ApplicationScoped
public class MarketFilter implements DtoFilter<Market> {

	@Override
	public List<Bson> getFilters(RequestWrapper wrap) {
		return Collections.emptyList();
	}

	@Override
	public List<Bson> getAggregates(RequestWrapper wrap) {
		List<Bson> aggs = new ArrayList<>();
		// adds a $lookup aggregate, joining categories on categoryIDS as "categories"
		aggs.add(Aggregates.lookup(DtoTableNames.CATEGORY.getTableName(), DatabaseFieldNames.CATEGORY_IDS, DatabaseFieldNames.DOCID,
				"categories"));
		
		return aggs;
	}

	@Override
	public Class<Market> getType() {
		return Market.class;
	}

}
