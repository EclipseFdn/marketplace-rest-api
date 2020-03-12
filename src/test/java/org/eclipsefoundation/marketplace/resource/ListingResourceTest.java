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
 * Test the listing resource endpoint, using fake data points to test solely the
 * responsiveness of the endpoint.
 * 
 * @author Martin Lowe
 */
@QuarkusTest
public class ListingResourceTest {

	// explicitly use the mock DAO to avoid potential issues with standard DAO
	@Inject
	private MockHibernateDao dao;

	@BeforeEach
	public void cleanDao() {
		dao.init();
	}

	@Test
	public void testListings() {
		given()
			.when().get("/listings")
				.then()
					.statusCode(200);
	}
	
	@Test
	public void testListingIdEndpoint() {
		given()
			.when().get("/listings/" + TestHelper.SAMPLE_UUID)
				.then()
					.statusCode(200);
	}

	@Test
	public void testListingIdEndpointNoResults() {
		given()
			.param(TestHelper.DATA_EXISTS_PARAM_NAME, false)
			.when().get("/listings/" + TestHelper.SAMPLE_UUID)
				.then()
					.statusCode(404);
	}
	
	@Test
	public void testListingIdEndpointInvalidUUID() {
		given()
			.when().get("/listings/invalid-uuid-string")
				.then()
					.statusCode(500);
	}
	
	@Test
	public void testDeletion() {
		given()
			.when().delete("/listings/" + TestHelper.SAMPLE_UUID)
				.then()
					.statusCode(200);
	}
	
	@Test
	public void testDeletionNotExists() {
		// pass param that tells the mock service that no data should be returned for
		// this call
		given()
			.param(TestHelper.DATA_EXISTS_PARAM_NAME, false)
			.when().delete("/listings/" + TestHelper.SAMPLE_UUID)
				.then()
					.statusCode(404);
	}
	
	@Test
	public void testDeletionInvalidUUID() {
		given()
			.when().delete("/listings/invalid-uuid-string")
				.then()
					.statusCode(500);
	}
	
	@Test
	public void testPutJSON() {
		// JSON string for a listing
		String json = 
			"{" + 
			"   \"id\":\"d348733d-c7ab-4867-83b0-4a2f601a6843\"," + 
			"   \"title\":\"Sample\"," + 
			"   \"url\":\"https://jakarta.ee\"," + 
			"   \"teaser\":\"birth refused iron replace stretch exciting anyone extra stop toy vowel land group zero voyage hair tell taste thumb own son full hour growth how handle product blew example ranch present affect original let captured older require environment spread ranch wing tiny aware mostly independent market nobody sharp frighten vegetable bring liquid must\"," + 
			"   \"body\":\"flag sheet attention drive boy dear copper two stop driver orange garage broad stream blue again alphabet composed fruit hole possibly consider purple catch steel front according pan selection upper model scared pull doll magic usually character studied use company worry over announced pond handsome recall thought flow term settle practice plenty bone union familiar horn well gold direct brought paid itself consider hunt someone wheat station occur brave respect heart ago thousand house voyage simplest train welcome stop other like colony swimming hospital doll fed central steady wait farm community wore shine settle proper movement product plenty nervous teach total guide trip after audience rising toward honor place hollow wall doubt bare desk branch entirely wave tell hello bat law birth ill biggest dish oil till sense difficult make easier gasoline pig bowl whenever state plus seldom widely writer explore bill understanding sold limited would solid horse common shallow men saved corner pull blue officer heard had provide sick sold noted situation silver official hunt adjective sort son though pattern twice none triangle fresh master quite meat cream end walk factor remarkable him themselves tribe string\"," + 
			"   \"status\":\"draft\"," + 
			"   \"support_url\":\"https://jakarta.ee/about/faq\"," + 
			"   \"license_type\":\"EPL-2.0\"," + 
			"   \"created\":\"2020-03-04T14:46:52-05:00\"," + 
			"   \"changed\":\"2020-03-04T14:46:52-05:00\"," + 
			"   \"authors\":[" + 
			"      {" + 
			"         \"full_name\":\"Martin Lowe\"," + 
			"         \"username\":\"autumnfound\"" + 
			"      }" + 
			"   ]," + 
			"   \"organization\":{" + 
			"      \"name\":\"Eclipse Foundation\"" + 
			"   }," + 
			"   \"tags\":[" + 
			"      {" + 
			"         \"name\":\"Build tools\"," + 
			"         \"url\":\"\"" + 
			"      }" + 
			"   ]," + 
			"   \"categories\":[" + 
			"      {" + 
			"         \"id\":\"e763f904-c51d-462f-bd94-a1594f4367d4\"" + 
			"      }," + 
			"      {" + 
			"         \"id\":\"14059954-8ca9-4d04-8d85-cbb7f4da60ed\"" + 
			"      }," + 
			"      {" + 
			"         \"id\":\"2d9b20a0-6c90-4708-9b82-0ed2100a995c\"" + 
			"      }," + 
			"      {" + 
			"         \"id\":\"0dcebdda-85b7-43b5-b985-fe54bc5967db\"" + 
			"      }," + 
			"      {" + 
			"         \"id\":\"93e475a8-0c2e-41c0-86a7-dbc8b8dda33d\"" + 
			"      }," + 
			"      {" + 
			"         \"id\":\"d8dd18d0-9d2c-4223-9b25-d7fefee4b4af\"" + 
			"      }" + 
			"   ]," + 
			"   \"screenshots\":[" + 
			"      \"http://www.example.com/img/sample.png\"" + 
			"   ]" + 
			"}";
		given()
			.body(json)
			.contentType(ContentType.JSON)
			.when().put("/listings")
				.then()
					.statusCode(200);
	}
	
	@Test
	public void testPutInvalidJSON() {
		// expect bad request response as whole object needs to be posted
		given()
			.body("{'id':'" + TestHelper.SAMPLE_UUID + "'}")
			.contentType(ContentType.JSON)
			.when().put("/listings")
				.then()
					.statusCode(400);
	}
}
