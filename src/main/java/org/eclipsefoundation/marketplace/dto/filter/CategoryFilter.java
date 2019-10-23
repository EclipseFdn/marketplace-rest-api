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
import org.eclipsefoundation.marketplace.dto.Category;
import org.eclipsefoundation.marketplace.model.RequestWrapper;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;

import com.mongodb.client.model.Filters;

/**
 * @author martin
 *
 */
@ApplicationScoped
public class CategoryFilter implements DtoFilter<Category> {

	@Override
	public List<Bson> getFilters(RequestWrapper wrap) {
		List<Bson> filters = new ArrayList<>();
		// ID check
		Optional<String> id = wrap.getFirstParam(UrlParameterNames.ID);
		if (id.isPresent()) {
			filters.add(Filters.eq(DatabaseFieldNames.DOCID, id.get()));
		}
		return filters;
	}

	@Override
	public List<Bson> getAggregates(RequestWrapper wrap) {
		return Collections.emptyList();
	}

	@Override
	public Class<Category> getType() {
		return Category.class;
	}

}
