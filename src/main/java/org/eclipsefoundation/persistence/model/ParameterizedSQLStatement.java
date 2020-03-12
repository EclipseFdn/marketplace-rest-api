package org.eclipsefoundation.persistence.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * In-context wrapper for prepared statement
 * 
 * @author Martin Lowe
 *
 */
public class ParameterizedSQLStatement {
	private static final Random RND = new Random();
	private DtoTable base;

	private List<Clause> clauses;
	private List<Join> joins;

	private String sortField;
	private SortOrder order;
	private SQLGenerator gen;
	private float seed;

	/**
	 * Builds a loaded parameterized statement to be used in querying dataset.
	 */
	ParameterizedSQLStatement(DtoTable base, SQLGenerator gen) {
		this.base = Objects.requireNonNull(base);
		this.clauses = new ArrayList<>();
		this.joins = new ArrayList<>();
		this.gen = Objects.requireNonNull(gen);
		this.seed = RND.nextFloat();
	}

	public ParameterizedSQLStatement combine(ParameterizedSQLStatement other) {
		this.clauses.addAll(other.clauses);
		this.joins.addAll(other.joins);

		return this;
	}

	/**
	 * @return the sql
	 */
	public String getSelectSql() {
		return gen.getSelectSQL(this);
	}

	/**
	 * @return the base
	 */
	public DtoTable getBase() {
		return base;
	}

	/**
	 * @param base the base to set
	 */
	public void setBase(DtoTable base) {
		this.base = base;
	}

	/**
	 * @return the sortField
	 */
	public String getSortField() {
		return sortField;
	}

	/**
	 * @param sortField the sortField to set
	 */
	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	/**
	 * @return the order
	 */
	public SortOrder getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(SortOrder order) {
		this.order = order;
	}

	/**
	 * @return the params
	 */
	public Object[] getParams() {
		return clauses.stream().map(Clause::getParams).flatMap(Arrays::stream).collect(Collectors.toList()).toArray();
	}

	public void addClause(Clause c) {
		this.clauses.add(c);
	}

	public void addJoin(Join j) {
		this.joins.add(j);
	}

	public List<Clause> getClauses() {
		return new ArrayList<>(clauses);
	}

	public List<Join> getJoins() {
		return new ArrayList<>(joins);
	}
	
	public float getSeed() {
		return this.seed;
	}

	/**
	 * Represents a clause for an SQL query
	 * 
	 * @author Martin Lowe
	 *
	 */
	public static class Clause {
		private String sql;
		private Object[] params;

		public Clause(String sql, Object[] params) {
			this.sql = sql;
			this.params = params;
		}

		/**
		 * @return the sql
		 */
		public String getSql() {
			return sql;
		}

		/**
		 * @param sql the sql to set
		 */
		public void setSql(String sql) {
			this.sql = sql;
		}

		/**
		 * @return the params
		 */
		public Object[] getParams() {
			return params;
		}

		/**
		 * @param params the params to set
		 */
		public void setParams(Object[] params) {
			this.params = params;
		}
	}

	public static class Join {
		private DtoTable localTable;
		private DtoTable foreignTable;
		private String localField;
		private String foreignField;

		public Join(DtoTable localTable, DtoTable foreignTable, String localField) {
			this(localTable, foreignTable, localField, null);
		}

		public Join(DtoTable localTable, DtoTable foreignTable, String localField, String foreignField) {
			this.localTable = localTable;
			this.foreignTable = foreignTable;
			this.localField = localField;
			this.foreignField = foreignField;
		}

		/**
		 * @return the localTable
		 */
		public DtoTable getLocalTable() {
			return localTable;
		}

		/**
		 * @param localTable the localTable to set
		 */
		public void setLocalTable(DtoTable localTable) {
			this.localTable = localTable;
		}

		/**
		 * @return the foreignTable
		 */
		public DtoTable getForeignTable() {
			return foreignTable;
		}

		/**
		 * @param foreignTable the foreignTable to set
		 */
		public void setForeignTable(DtoTable foreignTable) {
			this.foreignTable = foreignTable;
		}

		/**
		 * @return the localField
		 */
		public String getLocalField() {
			return localField;
		}

		/**
		 * @param localField the localField to set
		 */
		public void setLocalField(String localField) {
			this.localField = localField;
		}

		/**
		 * @return the foreignField
		 */
		public String getForeignField() {
			return foreignField;
		}

		/**
		 * @param foreignField the foreignField to set
		 */
		public void setForeignField(String foreignField) {
			this.foreignField = foreignField;
		}
	}
}