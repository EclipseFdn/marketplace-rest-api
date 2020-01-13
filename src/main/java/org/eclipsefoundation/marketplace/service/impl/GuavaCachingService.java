/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipsefoundation.marketplace.model.RequestWrapper;
import org.eclipsefoundation.marketplace.namespace.MicroprofilePropertyNames;
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

	@ConfigProperty(name = MicroprofilePropertyNames.CACHE_SIZE_MAX, defaultValue = "10000")
	long maxSize;
	@ConfigProperty(name = MicroprofilePropertyNames.CACHE_TTL_MAX_SECONDS, defaultValue = "900")
	long ttlWrite;

	// actual cache object
	Cache<String, T> cache = null;
	Map<String, Long> ttl;

	@PostConstruct
	public void init() {
		this.ttl = new HashMap<>();
		// create cache with configured settings that maintains a TTL map
		cache = CacheBuilder
					.newBuilder()
					.maximumSize(maxSize)
					.expireAfterWrite(ttlWrite, TimeUnit.SECONDS)
					.removalListener(not -> ttl.remove(not.getKey()))
					.build();

	}

	@Override
	public Optional<T> get(String id, RequestWrapper wrapper, Map<String, List<String>> params,
			Callable<? extends T> callable) {
		Objects.requireNonNull(id);
		Objects.requireNonNull(wrapper);
		Objects.requireNonNull(callable);

		String cacheKey = getCacheKey(id, wrapper, params);
		LOGGER.debug("Retrieving cache value for '{}'", cacheKey);
		try {
			// check if the cache is bypassed for the request
			if (wrapper.isCacheBypass()) {
				T result = callable.call();
				// if the cache has a value for key, update it
				if (cache.asMap().containsKey(cacheKey)) {
					cache.put(cacheKey, result);
				}
				return Optional.of(result);
			}
			
			// get entry, and enter a ttl as soon as it returns
			T data = cache.get(cacheKey, callable);
			if (data != null) {
				ttl.putIfAbsent(cacheKey, System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(ttlWrite, TimeUnit.SECONDS));
			}
			return Optional.of(cache.get(cacheKey, callable));
		} catch (InvalidCacheLoadException | UncheckedExecutionException e) {
			LOGGER.error("Error while retrieving fresh value for cachekey: {}", cacheKey, e);
		} catch (Exception e) {
			LOGGER.error("Error while retrieving value of callback", e);
		}
		return Optional.empty();
	}

	@Override
	public Optional<Long> getExpiration(String id, RequestWrapper params) {
		return Optional.ofNullable(ttl.get(getCacheKey(Objects.requireNonNull(id), Objects.requireNonNull(params), null)));
	}
	
	@Override
	public Set<String> getCacheKeys() {
		return cache.asMap().keySet();
	}

	@Override
	public void remove(String key) {
		cache.invalidate(key);
	}

	@Override
	public void removeAll() {
		cache.invalidateAll();
	}

	@Override
	public long getMaxAge() {
		return ttlWrite;
	}

}
