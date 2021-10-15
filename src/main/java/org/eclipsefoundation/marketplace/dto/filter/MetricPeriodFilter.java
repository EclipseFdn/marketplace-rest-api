/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.filter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipsefoundation.core.model.RequestWrapper;
import org.eclipsefoundation.marketplace.dto.MetricPeriod;
import org.eclipsefoundation.marketplace.namespace.DtoTableNames;
import org.eclipsefoundation.persistence.dto.filter.DtoFilter;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatement;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatementBuilder;

/**
 * Filter implementation for the {@linkplain MetricPeriod} class.
 * 
 * @author Martin Lowe
 *
 */
@ApplicationScoped
public class MetricPeriodFilter implements DtoFilter<MetricPeriod> {

	@Inject
	ParameterizedSQLStatementBuilder builder;

	@Override
	public ParameterizedSQLStatement getFilters(RequestWrapper wrap, boolean isRoot) {
		ParameterizedSQLStatement stmt = builder.build(DtoTableNames.METRIC_PERIOD.getTable());

		// TODO we could do 'popular' filter by looking for metrics that have X installs
		// in a period

		return stmt;
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
