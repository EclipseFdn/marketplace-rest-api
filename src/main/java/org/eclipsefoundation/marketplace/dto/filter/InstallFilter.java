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

import org.apache.commons.lang3.StringUtils;
import org.eclipsefoundation.core.model.RequestWrapper;
import org.eclipsefoundation.core.namespace.DefaultUrlParameterNames;
import org.eclipsefoundation.marketplace.dto.Install;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;
import org.eclipsefoundation.marketplace.namespace.DtoTableNames;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.eclipsefoundation.persistence.dto.filter.DtoFilter;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatement;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatementBuilder;

/**
 * Filter implementation for the {@linkplain Install} class.
 * 
 * @author Martin Lowe
 */
@ApplicationScoped
public class InstallFilter implements DtoFilter<Install> {

	@Inject
	ParameterizedSQLStatementBuilder builder;
	

	@Override
	public ParameterizedSQLStatement getFilters(RequestWrapper wrap, boolean isRoot) {
		ParameterizedSQLStatement stmt = builder.build(DtoTableNames.INSTALL.getTable());

		if (isRoot) {
			// ID check
			Optional<String> id = wrap.getFirstParam(DefaultUrlParameterNames.ID);
			if (id.isPresent()) {
				stmt.addClause(new ParameterizedSQLStatement.Clause(
						DtoTableNames.INSTALL.getAlias() + "." + DatabaseFieldNames.LISTING_ID + " = ?",
						new Object[] { UUID.fromString(id.get()) }));
			}
		}
		// version check
		Optional<String> version = wrap.getFirstParam(UrlParameterNames.VERSION);
		if (version.isPresent()) {
			stmt.addClause(new ParameterizedSQLStatement.Clause(
					DtoTableNames.INSTALL.getAlias() + "." + DatabaseFieldNames.INSTALL_VERSION + " = ?",
					new Object[] { version.get() }));
		}
		// OS filter
		Optional<String> os = wrap.getFirstParam(UrlParameterNames.OS);
		if (os.isPresent()) {
			stmt.addClause(new ParameterizedSQLStatement.Clause(
					DtoTableNames.INSTALL.getAlias() + "." + DatabaseFieldNames.OS + " = ?",
					new Object[] { os.get() }));
		}
		// eclipse version
		Optional<String> eclipseVersion = wrap.getFirstParam(UrlParameterNames.ECLIPSE_VERSION);
		if (eclipseVersion.isPresent()) {
			stmt.addClause(new ParameterizedSQLStatement.Clause(
					DtoTableNames.INSTALL.getAlias() + "." + DatabaseFieldNames.ECLIPSE_VERSION + " = ?",
					new Object[] { eclipseVersion.get() }));
		}
		// Java version
		Optional<String> javaVersion = wrap.getFirstParam(UrlParameterNames.JAVA_VERSION);
		if (javaVersion.isPresent() && StringUtils.isNumeric(javaVersion.get())) {
			stmt.addClause(new ParameterizedSQLStatement.Clause(
					DtoTableNames.INSTALL.getAlias() + "." + DatabaseFieldNames.INSTALL_JAVA_VERSION + " >= ?",
					new Object[] { Integer.valueOf(javaVersion.get()) }));
		}
		// solution version - Java version
		Optional<String> date = wrap.getFirstParam(UrlParameterNames.DATE_FROM);
		if (date.isPresent() && StringUtils.isNumeric(date.get())) {
			stmt.addClause(new ParameterizedSQLStatement.Clause(
					DtoTableNames.INSTALL.getAlias() + "." + DatabaseFieldNames.INSTALL_DATE + " >= ?",
					new Object[] { Integer.valueOf(date.get()) }));
		}
		return stmt;
	}

	@Override
	public Class<Install> getType() {
		return Install.class;
	}

}
