package org.eclipsefoundation.persistence.model;

public interface SQLGenerator {

	String getSelectSQL(ParameterizedSQLStatement src);
	
	String getDeleteSQL(ParameterizedSQLStatement src);
	
	String getCountSQL(ParameterizedSQLStatement src);
}
