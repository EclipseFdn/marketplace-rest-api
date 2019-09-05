/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dao.impl;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipsefoundation.marketplace.dao.MongoDao;
import org.eclipsefoundation.marketplace.model.MongoQuery;

import com.mongodb.MongoException;
import com.mongodb.client.result.DeleteResult;

import io.quarkus.test.Mock;

/**
 * Dummy service for MongoDB that spoofs all returned results.
 * 
 * @author Martin Lowe
 */
@Mock
@ApplicationScoped
public class MockMongoDao implements MongoDao {

	private boolean completeExceptionally = false;
	private Throwable exception = new MongoException("");

	@Override
	public <T> CompletionStage<List<T>> get(MongoQuery<T> q) {
		try {
			// use reflection to create an object
			Constructor<T> con = q.getDocType().getConstructor();
			T out = con.newInstance();

			// return the object in a list after a short wait.
			CompletableFuture<List<T>> cf = CompletableFuture.supplyAsync(() -> {
				return Arrays.asList(out);
			});
			// if flag is set, complete exceptionally
			if (completeExceptionally) {
				cf.completeExceptionally(exception);
			}
			return cf;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> CompletionStage<Void> add(MongoQuery<T> q, List<T> documents) {
		try {
			// return the object in a list after a short wait.
			CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> {
				return null;
			});
			// if flag is set, complete exceptionally
			if (completeExceptionally) {
				cf.completeExceptionally(exception);
			}
			return cf;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> CompletionStage<DeleteResult> delete(MongoQuery<T> q) {
		try {
			// return the object in a list after a short wait.
			CompletableFuture<DeleteResult> cf = CompletableFuture.supplyAsync(() -> {
				return DeleteResult.acknowledged(1);
			});
			// if flag is set, complete exceptionally
			if (completeExceptionally) {
				cf.completeExceptionally(exception);
			}
			return cf;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> CompletionStage<Long> count(MongoQuery<T> q) {
		try {
			// return the object in a list after a short wait.
			CompletableFuture<Long> cf = CompletableFuture.supplyAsync(() -> {
				return Math.round(Math.random() * 100);
			});
			// if flag is set, complete exceptionally
			if (completeExceptionally) {
				cf.completeExceptionally(exception);
			}
			return cf;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setCompleteExceptionally(boolean completeExceptionally) {
		this.completeExceptionally = completeExceptionally;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}

	@Override
	public HealthCheckResponse health() {
		return HealthCheckResponse.named("MOCK MongoDB readiness").up().build();
	}

}
