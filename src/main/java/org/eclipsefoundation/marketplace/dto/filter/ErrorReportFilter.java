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
import org.eclipsefoundation.marketplace.dto.ErrorReport;
import org.eclipsefoundation.marketplace.model.RequestWrapper;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;

import com.mongodb.client.model.Filters;

/**
 * Filter implementation for the ErrorReport class.
 * 
 * @author Martin Lowe
 */
@ApplicationScoped
public class ErrorReportFilter implements DtoFilter<ErrorReport> {

	@Inject
	ParameterizedSQLStatementBuilder builder;
	

	@Override
	public ParameterizedSQLStatement getFilters(RequestWrapper wrap, boolean isRoot) {
		ParameterizedSQLStatement stmt = builder.build(DtoTableNames.ERRORREPORT.getTable());
		if (isRoot) {
			// ID check
			Optional<String> id = wrap.getFirstParam(UrlParameterNames.ID);
			if (id.isPresent()) {
				stmt.addClause(new ParameterizedSQLStatement.Clause(
						DtoTableNames.ERRORREPORT.getAlias() + "." + DatabaseFieldNames.DOCID + " = ?",
						new Object[] { UUID.fromString(id.get()) }));
			}
		}
		// IDS
		List<String> ids = wrap.getParams(UrlParameterNames.IDS);
		if (!ids.isEmpty()) {
			stmt.addClause(new ParameterizedSQLStatement.Clause(
					DtoTableNames.ERRORREPORT.getAlias() + "." + DatabaseFieldNames.DOCID + " = ?",
					new Object[] { ids.stream().map(UUID::fromString).collect(Collectors.toList()) }));
		}
		// listing ID filter
		Optional<String> listingId = wrap.getFirstParam(UrlParameterNames.LISTING_ID);
		if (listingId.isPresent()) {
			stmt.addClause(new ParameterizedSQLStatement.Clause(
					DtoTableNames.ERRORREPORT.getAlias() + "." + DatabaseFieldNames.LISTING_ID + " = ?",
					new Object[] { UUID.fromString(listingId.get()) }));
		}
		// read filter
		Optional<String> isRead = wrap.getFirstParam(UrlParameterNames.READ);
		if (isRead.isPresent()) {
			stmt.addClause(new ParameterizedSQLStatement.Clause(
					DtoTableNames.ERRORREPORT.getAlias() + "." + DatabaseFieldNames.ERROR_READ + " = ?",
					new Object[] { Boolean.valueOf(isRead.get()) }));
		}
		// feature IDs
		Optional<String> featureId = wrap.getFirstParam(UrlParameterNames.FEATURE_ID);
		if (featureId.isPresent()) {
			stmt.addClause(new ParameterizedSQLStatement.Clause(
					DtoTableNames.ERRORREPORT.getAlias() + "." + DatabaseFieldNames.ERROR_FEATURE_IDS + " = ?",
					new Object[] { featureId.get() }));
		}
		return stmt;
	}

	@Override
	public Class<ErrorReport> getType() {
		return ErrorReport.class;
	}
}
