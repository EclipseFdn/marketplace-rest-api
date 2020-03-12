package org.eclipsefoundation.persistence.model;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ParameterizedSQLStatementBuilder {

	@Inject
	SQLGenerator generator;
	
	public ParameterizedSQLStatement build(DtoTable base) {
		return new ParameterizedSQLStatement(base, generator);
	}
	
}
