/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.filter;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.bson.conversions.Bson;
import org.eclipsefoundation.marketplace.dto.Category;
import org.eclipsefoundation.marketplace.model.RequestWrapper;

/**
 * @author martin
 *
 */
@ApplicationScoped
public class CategoryFilter implements DtoFilter<Category> {

	@Override
	public List<Bson> getFilters(RequestWrapper qps) {
		return Collections.emptyList();
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
