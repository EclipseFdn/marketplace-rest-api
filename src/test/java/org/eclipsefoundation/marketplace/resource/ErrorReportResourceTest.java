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
 * Test the error report resource endpoint, using fake data points to test solely the
 * responsiveness of the endpoint.
 * 
 * @author Martin Lowe
 */
@QuarkusTest
public class ErrorReportResourceTest {

	// explicitly use the mock DAO to avoid potential issues with standard DAO
	@Inject
	private MockHibernateDao dao;

	@BeforeEach
	public void cleanDao() {
		dao.init();
	}

	@Test
	public void testErrorReports() {
		given()
			.when().get("/error_reports")
				.then()
					.statusCode(200);
	}
	
	@Test
	public void testErrorReportIdEndpoint() {
		given()
			.when().get("/error_reports/" + TestHelper.SAMPLE_UUID)
				.then()
					.statusCode(200);
	}

	@Test
	public void testErrorReportIdEndpointNoResults() {
		given()
			.param(TestHelper.DATA_EXISTS_PARAM_NAME, false)
			.when().get("/error_reports/" + TestHelper.SAMPLE_UUID)
				.then()
					.statusCode(404);
	}
	
	@Test
	public void testErrorReportIdEndpointInvalidUUID() {
		given()
			.when().get("/error_reports/invalid-uuid-string")
				.then()
					.statusCode(500);
	}
	
	@Test
	public void testPostJSON() {
		// JSON string for an error report
		String json = 
			"{\n" + 
			"	\"id\": \"c8014575-a2a2-4a5f-ae1b-14b9c1746ead\",\n" + 
			"	\"title\": \"Goodbye\",\n" + 
			"	\"body\": \"friend.\",\n" + 
			"	\"detailed_message\": \"Hello world you suck!\",\n" + 
			"	\"ip_address\": \"192.168.0.1\",\n" + 
			"	\"is_read\": true,\n" + 
			"	\"listing_id\": \"7478a5e3-b76d-4463-894d-bb6547ea1333\",\n" + 
			"	\"feature_ids\": null,\n" + 
			"	\"status\": \"0\"\n" + 
			"}";
		given()
			.body(json)
			.contentType(ContentType.JSON)
			.when().post("/error_reports")
				.then()
					.statusCode(200);
	}
	
	@Test
	public void testPostInvalidJSON() {
		// expect bad request response as whole object needs to be posted
		given()
			.body("{'id':'" + TestHelper.SAMPLE_UUID + "'}")
			.contentType(ContentType.JSON)
			.when().post("/error_reports")
				.then()
					.statusCode(400);
	}
}
