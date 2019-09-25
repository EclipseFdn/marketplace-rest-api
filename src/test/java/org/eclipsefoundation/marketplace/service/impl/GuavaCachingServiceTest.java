/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.service.impl;

import java.util.Optional;

import javax.inject.Inject;

import org.eclipsefoundation.marketplace.model.RequestWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.DisabledOnSubstrate;
import io.quarkus.test.junit.QuarkusTest;

/**
 * @author martin
 *
 */
@DisabledOnSubstrate
@QuarkusTest
public class GuavaCachingServiceTest {

	@Inject
	GuavaCachingService<Object> gcs;
	@Inject
	RequestWrapper sample;

	/**
	 * Clear the cache before every test
	 */
	@BeforeEach
	public void pre() {
		// expire all active key values
		gcs.removeAll();
	}

	/**
	 * Test the manual creation of cache services, which is not the normal use case,
	 * and using javax injection, which is the expected use case.
	 */
	@Test
	public void testCacheInstantiation() {
		// create a manual object of cache to test instantiation of manual cache object
		GuavaCachingService<Object> gcsManual = new GuavaCachingService<>();

		// without post construct init via javax management, cache will not be properly
		// set
		Assertions.assertThrows(NullPointerException.class, () -> {
			gcsManual.get("sampleKey", sample, Object::new);
		});

		// initialize the cache w/ configs
		gcsManual.init();

		// run a command to interact with cache
		gcsManual.get("sampleKey", sample, Object::new);

		// test the injected cache service (which is the normal use case)
		gcs.get("sampleKey", sample, Object::new);
	}

	@Test
	public void testGet() {
		Object cachableObject = new Object();
		String key = "k";

		// get the cached obj from a fresh cache
		Optional<Object> cachedObj = gcs.get(key, sample, () -> cachableObject);

		Assertions.assertTrue(cachedObj.isPresent());
		Assertions.assertEquals(cachableObject, cachedObj.get());
	}

	@Test
	public void testGetNullCallable() {
		Assertions.assertThrows(NullPointerException.class, () -> {
			gcs.get("key", sample, null);
		});
	}

	@Test
	public void testGetNullCallableResult() {
		Optional<Object> emptyObj = gcs.get("failure key", sample, () -> null);
		Assertions.assertFalse(emptyObj.isPresent());
	}
	
	@Test
	public void testGetExceptionalCallable() {
		Optional<Object> emptyObj = gcs.get("k", sample, () -> {
			throw new IllegalStateException();
		});
		Assertions.assertFalse(emptyObj.isPresent());
	}
}
