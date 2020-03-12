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
import org.eclipsefoundation.marketplace.dto.Market;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;
import org.eclipsefoundation.marketplace.namespace.DtoTableNames;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.eclipsefoundation.persistence.dto.filter.DtoFilter;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatement;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatementBuilder;

/**
 * Filter implementation for the {@linkplain Market} class.
 * 
 * @author Martin Lowe
 */
@ApplicationScoped
public class MarketFilter implements DtoFilter<Market> {

	@Inject
	ParameterizedSQLStatementBuilder builder;

	@Override
	public ParameterizedSQLStatement getFilters(RequestWrapper wrap, boolean isRoot) {
		ParameterizedSQLStatement stmt = builder.build(DtoTableNames.MARKET.getTable());
		if (isRoot) {
			// ID check
			Optional<String> id = wrap.getFirstParam(UrlParameterNames.ID);
			if (id.isPresent()) {
				stmt.addClause(new ParameterizedSQLStatement.Clause(
						DtoTableNames.MARKET.getAlias() + "." + DatabaseFieldNames.DOCID + " = ?",
						new Object[] { UUID.fromString(id.get()) }));
			}
		}
		// check for markets that contain a given listing
		Optional<String> listingId = wrap.getFirstParam(UrlParameterNames.LISTING_ID);
		if (listingId.isPresent()) {
			stmt.addClause(new ParameterizedSQLStatement.Clause(
					"? IN elements(" + DtoTableNames.MARKET.getAlias() + ".listingId)",
					new Object[] { listingId.get() }));
		}

		return stmt;
	}

	@Override
	public Class<Market> getType() {
		return Market.class;
	}

}
