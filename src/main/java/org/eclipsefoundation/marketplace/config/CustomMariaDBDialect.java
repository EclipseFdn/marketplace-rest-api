package org.eclipsefoundation.marketplace.config;

import org.hibernate.dialect.MariaDB103Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;

/**
 * Custom dialect extension to register missing RAND functions.
 * 
 * @author Martin Lowe
 *
 */
public class CustomMariaDBDialect extends MariaDB103Dialect {

	/**
	 * Register random as a function within the dialect. OOTB, rand exists without
	 * an argument, and will fail if an arg is passed. This is a valid state within
	 * MariaDB as the arg acts as a seed.
	 */
	public CustomMariaDBDialect() {
		super();
		registerFunction("rand", new StandardSQLFunction("rand"));
	}
}
