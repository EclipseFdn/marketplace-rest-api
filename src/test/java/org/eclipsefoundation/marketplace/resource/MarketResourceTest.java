/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.resource;

import static io.restassured.RestAssured.given;

import javax.inject.Inject;

import org.eclipsefoundation.marketplace.dao.impl.MockHibernateDao;
import org.eclipsefoundation.marketplace.helper.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

/**
 * Test the market resource endpoint, using fake data points to test solely the
 * responsiveness of the endpoint.
 * 
 * @author Martin Lowe
 */
@QuarkusTest
public class MarketResourceTest {

	// explicitly use the mock DAO to avoid potential issues with standard DAO
	@Inject
	private MockHibernateDao dao;

	@BeforeEach
	public void cleanDao() {
		dao.init();
	}

	@Test
	public void testMarkets() {
		given()
			.when().get("/markets")
				.then()
					.statusCode(200);
	}
	
	@Test
	public void testMarketIdEndpoint() {
		given()
			.when().get("/markets/" + TestHelper.SAMPLE_UUID)
				.then()
					.statusCode(200);
	}

	@Test
	public void testMarketIdEndpointNoResults() {
		given()
			.param(TestHelper.DATA_EXISTS_PARAM_NAME, false)
			.when().get("/markets/" + TestHelper.SAMPLE_UUID)
				.then()
					.statusCode(404);
	}
	
	@Test
	public void testMarketIdEndpointInvalidUUID() {
		given()
			.when().get("/markets/invalid-uuid-string")
				.then()
					.statusCode(500);
	}
	
	@Test
	public void testDeletion() {
		given()
			.when().delete("/markets/" + TestHelper.SAMPLE_UUID)
				.then()
					.statusCode(200);
	}
	
	@Test
	public void testDeletionNotExists() {
		// pass param that tells the mock service that no data should be returned for
		// this call
		given()
			.param(TestHelper.DATA_EXISTS_PARAM_NAME, false)
			.when().delete("/markets/" + TestHelper.SAMPLE_UUID)
				.then()
					.statusCode(404);
	}
	
	@Test
	public void testDeletionInvalidUUID() {
		given()
			.when().delete("/markets/invalid-uuid-string")
				.then()
					.statusCode(500);
	}
	
	@Test
	public void testPutJSON() {
		// JSON string for a market
		String json = 
			"{" + 
			"	\"title\": \"Sample\",\n" + 
			"	\"url\": \"https://www.eclipse.org\",\n" + 
			"	\"listing_ids\": [{\"id\":\"7478a5e3-b76d-4463-894d-bb6547ea1333\"}]\n" + 
			"}";
		given()
			.body(json)
			.contentType(ContentType.JSON)
			.when().put("/markets")
				.then()
					.statusCode(200);
	}
	
	@Test
	public void testPutInvalidJSON() {
		// expect bad request response as whole object needs to be posted
		given()
			.body("{'id':'" + TestHelper.SAMPLE_UUID + "'}")
			.contentType(ContentType.JSON)
			.when().put("/markets")
				.then()
					.statusCode(400);
	}
}
