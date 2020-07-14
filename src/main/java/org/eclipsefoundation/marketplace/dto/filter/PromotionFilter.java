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
import org.eclipsefoundation.core.namespace.DefaultUrlParameterNames;
import org.eclipsefoundation.marketplace.dto.Promotion;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;
import org.eclipsefoundation.marketplace.namespace.DtoTableNames;
import org.eclipsefoundation.persistence.dto.filter.DtoFilter;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatement;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatementBuilder;

/**
 * Filter implementation for the {@link Promotion} class.
 * 
 * @author Martin Lowe
 * 
 */
@ApplicationScoped
public class PromotionFilter implements DtoFilter<Promotion> {

	@Inject
	ParameterizedSQLStatementBuilder builder;

	@Override
	public ParameterizedSQLStatement getFilters(RequestWrapper wrap, boolean isRoot) {
		ParameterizedSQLStatement stmt = builder.build(DtoTableNames.LISTING.getTable());
		if (isRoot) {
			// ID check
			Optional<String> id = wrap.getFirstParam(DefaultUrlParameterNames.ID);
			if (id.isPresent()) {
				stmt.addClause(new ParameterizedSQLStatement.Clause(
						DtoTableNames.LISTING.getAlias() + "." + DatabaseFieldNames.DOCID + " = ?",
						new Object[] { UUID.fromString(id.get()) }));
			}
		}
		return stmt;
	}

	@Override
	public Class<Promotion> getType() {
		return Promotion.class;
	}
}
