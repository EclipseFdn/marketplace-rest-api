/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.helper;

import java.util.HashMap;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.eclipsefoundation.marketplace.model.QueryParams;
import org.eclipsefoundation.marketplace.namespace.SolrFieldNames;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Tests for the SolrHelper class
 * 
 * @author Martin Lowe
 *
 */
@QuarkusTest
public class SolrHelperTest {

	@Test
	public void testCreateQuery() {
		// set up
		int expectedPageSize = SolrHelper.DEFAULT_PAGE_SIZE;
		int expectedStart = 0;

		// run method
		SolrQuery q = SolrHelper.createQuery();

		// test values are what we expect
		List<SortClause> sc = q.getSorts();
		Assertions.assertEquals(expectedStart, q.getStart());
		Assertions.assertEquals(expectedPageSize, q.getRows());
		Assertions.assertEquals(null, q.getQuery());
		Assertions.assertEquals(0, sc.size());
	}

	@Test
	public void testCreateQueryParams() {
		// set up
		int expectedPageSize = SolrHelper.DEFAULT_PAGE_SIZE;
		int expectedPage = 2;
		Integer expectedStart = Integer.valueOf(expectedPageSize) * (Integer.valueOf(expectedPage) - 1);
		String queryString = "sample:query";

		// run method
		SolrQuery q = SolrHelper.createQuery(queryString, expectedPage, expectedPageSize);

		// test values are what we expect
		List<SortClause> sc = q.getSorts();
		Assertions.assertEquals(expectedStart, q.getStart());
		Assertions.assertEquals(expectedPageSize, q.getRows());
		Assertions.assertEquals(queryString, q.getQuery());
		Assertions.assertEquals(0, sc.size());
	}
	
	@Test
	public void testCreateQueryParamsBadPage() {
		// set up
		int expectedPageSize = SolrHelper.DEFAULT_PAGE_SIZE;
		int expectedPage = -1;
		String queryString = "sample:query";

		// run method
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			SolrHelper.createQuery(queryString, expectedPage, expectedPageSize);
		});
	}
	
	@Test
	public void testCreateQueryParamsBadPageSize() {
		// set up
		int expectedPageSize = -10;
		int expectedPage = 1;
		String queryString = "sample:query";

		// run method
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			SolrHelper.createQuery(queryString, expectedPage, expectedPageSize);
		});
	}

	@Test
	public void testCreateQueryParamMap() {
		// set up
		String pageSizeVal = "20";
		String pageVal = "2";
		Integer expectedStart = Integer.valueOf(pageSizeVal) * (Integer.valueOf(pageVal) - 1);

		QueryParams params = new QueryParams(new HashMap<>());
		params.addParam(UrlParameterNames.SOLR_CURRENT_PAGE, pageVal);
		params.addParam(UrlParameterNames.SOLR_PAGE_SIZE, pageSizeVal);
		params.addParam(UrlParameterNames.SOLR_SORT, SolrFieldNames.LISTING_ID + " asc");

		// run method
		SolrQuery q = SolrHelper.createQuery(params);

		// test values are what we expect
		List<SortClause> sc = q.getSorts();
		Assertions.assertEquals(expectedStart, q.getStart());
		Assertions.assertEquals(Integer.valueOf(pageSizeVal), q.getRows());
		Assertions.assertEquals("", q.getQuery());
		Assertions.assertEquals(1, sc.size());
		Assertions.assertEquals(SolrFieldNames.LISTING_ID, sc.get(0).getItem());
		Assertions.assertEquals(ORDER.asc, sc.get(0).getOrder());
	}

	@Test
	public void testGetSortClausesBadOrder() {
		// Test scenario - 1 bad sort clause, 1 good. Good clause should still be
		// attached, bad clause (bad order) skipped
		StringBuilder sortSB = new StringBuilder();
		sortSB.append(SolrFieldNames.LISTING_ID).append(" bad,");
		sortSB.append(SolrFieldNames.CREATION_DATE).append(" desc");

		QueryParams params = new QueryParams(new HashMap<>());
		params.addParam(UrlParameterNames.SOLR_SORT, sortSB.toString());

		// run method that calls and makes results accessible for sort clauses
		SolrQuery q = SolrHelper.createQuery(params);

		// test values are what we expect
		List<SortClause> sc = q.getSorts();
		Assertions.assertEquals(1, sc.size());
		Assertions.assertEquals(SolrFieldNames.CREATION_DATE, sc.get(0).getItem());
		Assertions.assertEquals(ORDER.desc, sc.get(0).getOrder());
	}

	@Test
	public void testGetSortClausesMalformed() {
		// Test scenario - 1 bad sort clause, 1 good. Good clause should still be
		// attached, bad clause (malformed) skipped
		StringBuilder sortSB = new StringBuilder();
		sortSB.append(SolrFieldNames.LISTING_ID).append(" asc asc,");
		sortSB.append(SolrFieldNames.CREATION_DATE).append(" desc");

		QueryParams params = new QueryParams(new HashMap<>());
		params.addParam(UrlParameterNames.SOLR_SORT, sortSB.toString());

		// run method that calls and makes results accessible for sort clauses
		SolrQuery q = SolrHelper.createQuery(params);

		// test values are what we expect
		List<SortClause> sc = q.getSorts();
		Assertions.assertEquals(1, sc.size());
		Assertions.assertEquals(SolrFieldNames.CREATION_DATE, sc.get(0).getItem());
		Assertions.assertEquals(ORDER.desc, sc.get(0).getOrder());
	}
}
