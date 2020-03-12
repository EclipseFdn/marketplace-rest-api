/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.filter;

import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipsefoundation.core.model.RequestWrapper;
import org.eclipsefoundation.marketplace.dto.InstallMetrics;
import org.eclipsefoundation.marketplace.dto.MetricPeriod;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;
import org.eclipsefoundation.marketplace.namespace.DtoTableNames;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.eclipsefoundation.persistence.dto.filter.DtoFilter;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatement;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatementBuilder;

/**
 * Filter implementation for the Category class.
 * 
 * @author Martin Lowe
 */
@ApplicationScoped
public class InstallMetricFilter implements DtoFilter<InstallMetrics> {

	@Inject
	ParameterizedSQLStatementBuilder builder;
	

	@Inject
	DtoFilter<MetricPeriod> metricPeriodFilter;

	@Override
	public ParameterizedSQLStatement getFilters(RequestWrapper wrap, boolean isRoot) {
		ParameterizedSQLStatement stmt = builder.build(DtoTableNames.INSTALL_METRIC.getTable());

		// listing ID filter
		Optional<String> listingId = wrap.getFirstParam(UrlParameterNames.LISTING_ID);
		if (listingId.isPresent()) {
			stmt.addClause(new ParameterizedSQLStatement.Clause(
					DtoTableNames.INSTALL_METRIC.getAlias() + "." + DatabaseFieldNames.LISTING_ID + " = ?",
					new Object[] { UUID.fromString(listingId.get()) }));
		}
		
		// retrieve the metric period filters.
		stmt.addJoin(new ParameterizedSQLStatement.Join(DtoTableNames.INSTALL_METRIC.getTable(), DtoTableNames.METRIC_PERIOD.getTable(),
				"periods"));
		stmt.combine(metricPeriodFilter.getFilters(wrap, false));

		return stmt;
	}

	@Override
	public Class<InstallMetrics> getType() {
		return InstallMetrics.class;
	}

}
