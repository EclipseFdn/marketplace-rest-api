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
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipsefoundation.core.model.RequestWrapper;
import org.eclipsefoundation.marketplace.dto.ListingVersion;
import org.eclipsefoundation.marketplace.namespace.DtoTableNames;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.eclipsefoundation.persistence.dto.filter.DtoFilter;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatement;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatementBuilder;

/**
 * Filter implementation for the {@linkplain ListingVersion} class.
 * 
 * @author Martin Lowe
 *
 */
@ApplicationScoped
public class ListingVersionFilter implements DtoFilter<ListingVersion> {

	@Inject
	ParameterizedSQLStatementBuilder builder;

	@Override
	public ParameterizedSQLStatement getFilters(RequestWrapper wrap, boolean isRoot) {
		ParameterizedSQLStatement stmt = builder.build(DtoTableNames.LISTING_VERSION.getTable());
		if (isRoot) {
			// ID check
			Optional<String> id = wrap.getFirstParam(UrlParameterNames.ID);
			if (id.isPresent()) {
				stmt.addClause(
						new ParameterizedSQLStatement.Clause(DtoTableNames.LISTING_VERSION.getAlias() + ".id = ?",
								new Object[] { UUID.fromString(id.get()) }));
			}
		}

		// solution version - OS filter
		Optional<String> os = wrap.getFirstParam(UrlParameterNames.OS);
		if (os.isPresent()) {
			stmt.addClause(new ParameterizedSQLStatement.Clause(
					"? IN elements(" + DtoTableNames.LISTING_VERSION.getAlias() + ".platforms)",
					new Object[] { os.get() }));
		}
		// solution version - eclipse version
		Optional<String> eclipseVersion = wrap.getFirstParam(UrlParameterNames.ECLIPSE_VERSION);
		if (eclipseVersion.isPresent()) {
			stmt.addClause(new ParameterizedSQLStatement.Clause(
					"? IN elements(" + DtoTableNames.LISTING_VERSION.getAlias() + ".eclipseVersions)",
					new Object[] { eclipseVersion.get() }));
		}
		// solution version - Java version
		Optional<String> javaVersion = wrap.getFirstParam(UrlParameterNames.JAVA_VERSION);
		if (javaVersion.isPresent() && StringUtils.isNumeric(javaVersion.get())) {
			stmt.addClause(new ParameterizedSQLStatement.Clause(
					DtoTableNames.LISTING_VERSION.getAlias() + ".minJavaVersion >= ?",
					new Object[] { Integer.valueOf(javaVersion.get()) }));
		}
		return stmt;
	}

	@Override
	public Class<ListingVersion> getType() {
		return ListingVersion.class;
	}

}
