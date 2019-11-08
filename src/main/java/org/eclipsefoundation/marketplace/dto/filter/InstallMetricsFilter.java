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
import org.eclipsefoundation.marketplace.dto.InstallMetrics;
import org.eclipsefoundation.marketplace.model.RequestWrapper;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;

import com.mongodb.client.model.Filters;

/**
 * Filter implementation for the {@linkplain InstallMetrics} class.
 * 
 * @author Martin Lowe
 *
 */
@ApplicationScoped
public class InstallMetricsFilter implements DtoFilter<InstallMetrics> {

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
		return Collections.emptyList();
	}

	@Override
	public Class<InstallMetrics> getType() {
		return InstallMetrics.class;
	}

}
