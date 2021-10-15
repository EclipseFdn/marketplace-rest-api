package org.eclipsefoundation.persistence.model;

import java.util.Objects;

/**
 * Represents an object in the database. This should be deprecated to instead
 * read persistence annotations from the javax namespace.
 * 
 * @author Martin Lowe
 *
 */
public class DtoTable {

	private Class<?> baseClass;
	private String alias;

	public DtoTable(Class<?> baseClass, String alias) {
		this.baseClass = Objects.requireNonNull(baseClass);
		this.alias = Objects.requireNonNull(alias);
	}

	public Class<?> getType() {
		return this.baseClass;
	}

	public String getAlias() {
		return this.alias;
	}
}
