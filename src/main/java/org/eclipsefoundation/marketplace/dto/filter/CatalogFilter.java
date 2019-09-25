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
import org.eclipsefoundation.marketplace.dto.Catalog;
import org.eclipsefoundation.marketplace.model.RequestWrapper;

/**
 * Filter implementation for the Listing class. Checks the following fields:
 * 
 * 	<ul>
 
 * 	</ul>
 * 
 * @author Martin Lowe
 */
@ApplicationScoped
public class CatalogFilter implements DtoFilter<Catalog> {

	@Override
	public List<Bson> getFilters(RequestWrapper qps) {
		List<Bson> filters = new ArrayList<>();

		
		return filters;
	}
	
	@Override
	public List<Bson> getAggregates(RequestWrapper wrap) {
		return Collections.emptyList();
	}

	@Override
	public Class<Catalog> getType() {
		return Catalog.class;
	}

}
