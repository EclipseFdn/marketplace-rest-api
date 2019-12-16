/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipsefoundation.marketplace.dao.MongoDao;
import org.eclipsefoundation.marketplace.exception.MaintenanceException;
import org.eclipsefoundation.marketplace.model.MongoQuery;
import org.eclipsefoundation.marketplace.namespace.DtoTableNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.DeleteResult;

import io.quarkus.mongodb.ReactiveMongoClient;
import io.quarkus.mongodb.ReactiveMongoCollection;

/**
 * Default implementation of the MongoDB DAO.
 * 
 * @author Martin Lowe
 */
@ApplicationScoped
public class DefaultMongoDao implements MongoDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMongoDao.class);

	@ConfigProperty(name = "mongodb.database")
	String databaseName;

	@ConfigProperty(name = "mongodb.default.limit")
	int defaultLimit;

	@ConfigProperty(name = "mongodb.default.limit.max")
	int defaultMax;

	@ConfigProperty(name = "mongodb.maintenance", defaultValue = "false")
	boolean maintenanceFlag;

	@Inject
	Instance<CodecProvider> providers;

	@Inject
	ReactiveMongoClient mongoClient;

	@Override
	public <T> CompletionStage<List<T>> get(MongoQuery<T> q) {
		if (maintenanceFlag) {
			throw new MaintenanceException();
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Querying MongoDB using the following query: {}", q);
		}

		LOGGER.debug("Getting aggregate results");
		// build base query
		PublisherBuilder<T> builder = getCollection(q.getDocType()).aggregate(q.getPipeline(getLimit(q)),
				q.getDocType());
		// check if result set should be limited
		if (q.getDTOFilter().useLimit()) {
			builder = builder.limit(getLimit(q));
		}
		// run the query
		return builder.distinct().toList().run();
	}

	@Override
	public <T> CompletionStage<Void> add(MongoQuery<T> q, List<T> documents) {
		if (maintenanceFlag) {
			throw new MaintenanceException();
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Adding {} documents to MongoDB of type {}", documents.size(), q.getDocType().getSimpleName());
		}

		// set up upserting to not fail on updates
		ReplaceOptions ro = new ReplaceOptions().upsert(true).bypassDocumentValidation(true);

		// maintain a list of updates
		List<CompletionStage<?>> stages = new ArrayList<>(documents.size());
		Bson filter = q.getFilter();
		for (T doc : documents) {
			if (filter == null) {
				stages.add(getCollection(q.getDocType()).insertOne(doc));
			} else {
				stages.add(getCollection(q.getDocType()).replaceOne(filter, doc, ro));
			}
		}

		// convert the stages to futures, and wrap them in a completable future
		List<CompletableFuture<?>> all = stages.stream().map(CompletionStage::toCompletableFuture)
				.collect(Collectors.toList());
		return CompletableFuture.allOf(all.toArray(new CompletableFuture[all.size()]));
	}

	@Override
	public <T> CompletionStage<DeleteResult> delete(MongoQuery<T> q) {
		if (maintenanceFlag) {
			throw new MaintenanceException();
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Removing documents from MongoDB using the following query: {}", q);
		}
		return getCollection(q.getDocType()).deleteMany(q.getFilter());
	}

	@Override
	public <T> CompletionStage<Long> count(MongoQuery<T> q) {
		if (maintenanceFlag) {
			throw new MaintenanceException();
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Counting documents in MongoDB that match the following query: {}", q);
		}
		return getCollection(q.getDocType()).countDocuments(q.getFilter());
	}

	@Override
	public HealthCheckResponse health() {
		HealthCheckResponseBuilder b = HealthCheckResponse.named("MongoDB readiness");
		if (maintenanceFlag) {
			return b.down().withData("error", "Maintenance flag is set").build();
		}
		return b.up().build();
	}

	private <T> int getLimit(MongoQuery<T> q) {
		return q.getLimit() > 0 ? Math.min(q.getLimit(), defaultMax) : defaultLimit;
	}

	private <T> ReactiveMongoCollection<T> getCollection(Class<T> type) {
		return mongoClient.getDatabase(databaseName).getCollection(DtoTableNames.getTableName(type), type);
	}
}
