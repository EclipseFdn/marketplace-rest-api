/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.filter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipsefoundation.core.model.RequestWrapper;
import org.eclipsefoundation.core.namespace.DefaultUrlParameterNames;
import org.eclipsefoundation.marketplace.dto.Listing;
import org.eclipsefoundation.marketplace.dto.ListingVersion;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;
import org.eclipsefoundation.marketplace.namespace.DtoTableNames;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.eclipsefoundation.persistence.dto.filter.DtoFilter;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatement;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatementBuilder;

/**
 * Filter implementation for the {@linkplain Listing} class.
 * 
 * @author Martin Lowe
 */
@ApplicationScoped
public class ListingFilter implements DtoFilter<Listing> {

	@Inject
	ParameterizedSQLStatementBuilder builder;
	@Inject
	DtoFilter<ListingVersion> listingVersionFilter;

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

		// select by multiple IDs
		List<String> ids = wrap.getParams(DefaultUrlParameterNames.IDS);
		if (!ids.isEmpty()) {
			// convert the IDs to UUID objects
			List<UUID> actualIDs = ids.stream().map(UUID::fromString).collect(Collectors.toList());
			stmt.addClause(new ParameterizedSQLStatement.Clause(
					DtoTableNames.LISTING.getAlias() + "." + DatabaseFieldNames.DOCID + " in (?)", new Object[] { actualIDs }));
		}

		// Listing license type check
		Optional<String> licType = wrap.getFirstParam(UrlParameterNames.LICENSE_TYPE);
		if (licType.isPresent()) {
			stmt.addClause(new ParameterizedSQLStatement.Clause(DtoTableNames.LISTING.getAlias() + ".licenseType = ?",
					new Object[] { licType.get() }));
		}

		// retrieve the listing version filters.
		stmt.addJoin(
				new ParameterizedSQLStatement.Join(DtoTableNames.LISTING.getTable(), DtoTableNames.LISTING_VERSION.getTable(), "versions"));
		stmt.combine(listingVersionFilter.getFilters(wrap, false));
		// TODO lookup install metric values from install_metrics table

		return stmt;
	}

	@Override
	public Class<Listing> getType() {
		return Listing.class;
	}
}
