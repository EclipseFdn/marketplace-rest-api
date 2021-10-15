package org.eclipsefoundation.marketplace.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.NotImplementedException;
import org.eclipsefoundation.persistence.model.DtoTable;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatement;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatement.Clause;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatement.Join;
import org.eclipsefoundation.persistence.model.SQLGenerator;
import org.eclipsefoundation.persistence.model.SortOrder;

@ApplicationScoped
public class HQLGenerator implements SQLGenerator {
	private static final Pattern ORDINAL_PARAMETER_PATTERN = Pattern.compile("\\?(?!\\d)");

	@Override
	public String getSelectSQL(ParameterizedSQLStatement src) {
		DtoTable base = src.getBase();
		// retrieve once to reduce obj churn
		List<Join> joins = src.getJoins();
		List<Clause> clauses = src.getClauses();

		StringBuilder sb = new StringBuilder(64);
		sb.append("SELECT ").append(base.getAlias());
		sb.append(" FROM");
		// handle selection of table data
		sb.append(' ').append(base.getType().getSimpleName());
		sb.append(' ').append(base.getAlias());
		List<DtoTable> selectedTables = new ArrayList<>();
		for (Join j : joins) {
			if (base != j.getForeignTable() && !selectedTables.contains(j.getForeignTable())) {
				selectedTables.add(j.getLocalTable());
				sb.append(" LEFT JOIN ").append(j.getLocalTable().getAlias());
				sb.append('.').append(j.getLocalField());
				sb.append(" AS ").append(j.getForeignTable().getAlias());
			}
		}

		if (!clauses.isEmpty()) {
			sb.append(" WHERE");
		}
		// handle clauses
		int ordinal = 1;
		for (int cIdx = 0; cIdx < clauses.size(); cIdx++) {
			if (cIdx != 0) {
				sb.append(" AND");
			}

			// create matcher on sql clause to replace legacy parameter placeholders with
			// ordinals
			String sql = clauses.get(cIdx).getSql();
			Matcher m = ORDINAL_PARAMETER_PATTERN.matcher(sql);
			while (m.find()) {
				sql = sql.substring(0, m.start()) + '?' + ordinal++ + sql.substring(m.end());
			}
			sb.append(' ').append(sql);
		}

		// add sort if set
		if (src.getSortField() != null && !SortOrder.RANDOM.equals(src.getOrder())) {
			sb.append("ORDER BY ").append(src.getSortField());
			if (SortOrder.ASCENDING.equals(src.getOrder())) {
				sb.append(" asc");
			} else {
				sb.append(" desc");
			}
		} else if (SortOrder.RANDOM.equals(src.getOrder())) {
			sb.append(" order by RAND()");
		}
		return sb.toString();
	}

	@Override
	public String getDeleteSQL(ParameterizedSQLStatement src) {
		throw new NotImplementedException("HQL does not utilize deletion SQL logic");
	}

	@Override
	public String getCountSQL(ParameterizedSQLStatement src) {
		return getSelectSQL(src);
	}

}
