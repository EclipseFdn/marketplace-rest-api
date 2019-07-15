/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.service.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipsefoundation.marketplace.model.QueryParams;
import org.eclipsefoundation.marketplace.service.CachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * <p>
 * Simple caching service for caching objects in an in-memory cache, implemented
 * using the Google Guava cache mechanism. Cache size and time to live are
 * configured within the MicroProfile configuration.
 * </p>
 * 
 * <p>
 * Guava cache is inherently thread safe, so no synchronization needs to be done
 * on access.
 * </p>
 * 
 * @author Martin Lowe
 * @param <T> the type of object cached by this instance of the service
 *
 */
@ApplicationScoped
public class GuavaCachingService<T> implements CachingService<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GuavaCachingService.class);

	@ConfigProperty(name = "cache.max", defaultValue = "2500")
	long maxSize;
	@ConfigProperty(name = "cache.ttl.access", defaultValue = "21600")
	long ttlAccess;
	@ConfigProperty(name = "cache.ttl.write", defaultValue = "86400")
	long ttlWrite;

	// actual cache object
	Cache<String, T> cache = null;

	@PostConstruct
	public void init() {
		cache = CacheBuilder
				.newBuilder()
				.maximumSize(maxSize)
				.expireAfterAccess(ttlAccess, TimeUnit.SECONDS)
				.expireAfterWrite(ttlWrite, TimeUnit.SECONDS)
				.build();
	}

	@Override
	public Optional<T> get(String id, QueryParams params, Callable<? extends T> callable) {
		Objects.requireNonNull(id);
		Objects.requireNonNull(callable);
		
		String cacheKey = getCacheKey(id, Optional.ofNullable(params));		
		try {
			return Optional.of(cache.get(cacheKey, callable));
		} catch (ExecutionException e) {
			LOGGER.error("Error while retrieving value of callback", e);
		} catch (InvalidCacheLoadException | UncheckedExecutionException e) {
			LOGGER.error("Error while retrieving fresh value for cachekey: {}", cacheKey, e);
		}
		return Optional.empty();
	}

	/**
	 * Used to clear out results in cache.
	 */
	void clear() {
		cache.invalidateAll();
	}
}
