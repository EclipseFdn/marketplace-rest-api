/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.core.helper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import org.eclipsefoundation.core.model.RequestWrapper;
import org.eclipsefoundation.core.service.CachingService;

/**
 * Helper class that transforms data into a response usable for the RESTeasy
 * container. Uses injected JSON-B serializer and caching service to get current
 * information on cache data.
 * 
 * @author Martin Lowe
 *
 */
@ApplicationScoped
public class ResponseHelper {

	private static final MessageDigest DIGEST;
	static {
		try {
			DIGEST = MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Could not create an MD5 hash digest");
		}
	}
	
	@Inject
	Jsonb jsonb;
	@Inject
	CachingService<?> cachingService;

	/**
	 * Builds a response using passed data. Uses references to the caching service
	 * and the current request to add information about ETags and Cache-Control
	 * headers.
	 * 
	 * @param id      the ID of the object to be stored in cache
	 * @param wrapper the query parameters for the current request
	 * @param data    the data to attach to the response
	 * @return a complete response object for the given data and request.
	 */
	public Response build(String id, RequestWrapper wrapper, Object data) {
		// set default cache control flags for API responses
		CacheControl cc = new CacheControl();
		cc.setNoStore(wrapper.isCacheBypass());

		if (!cc.isNoStore()) {
			cc.setMaxAge((int) cachingService.getMaxAge());
			// get the TTL for the current entry
			Optional<Long> ttl = cachingService.getExpiration(id, wrapper);
			if (!ttl.isPresent()) {
				return Response.serverError().build();
			}

			// serialize the data to get an etag
			String content = jsonb.toJson(Objects.requireNonNull(data));
			// ingest the content and hash to create an etag for current content
			String hash;
			synchronized (this) {
				DIGEST.update(content.getBytes(StandardCharsets.UTF_8));
				hash = DatatypeConverter.printHexBinary(DIGEST.digest());
				DIGEST.reset();
			}
			
			// check if etag matches
			String etag = wrapper.getHeader("Etag");
			if (hash.equals(etag)) {
				return Response.notModified(etag).cacheControl(cc).expires(new Date(ttl.get())).build();
			}
			// return a response w/ the generated etag
			return Response.ok(data).tag(hash).cacheControl(cc).expires(new Date(ttl.get())).build();
		}
		return Response.ok(data).cacheControl(cc).build();
	}
}
