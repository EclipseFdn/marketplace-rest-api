/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.persistence.dao;

import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.enterprise.event.Observes;

import org.eclipsefoundation.core.health.BeanHealth;
import org.eclipsefoundation.persistence.dto.BareNode;
import org.eclipsefoundation.persistence.model.RDBMSQuery;

import io.quarkus.runtime.StartupEvent;

/**
 * Interface for classes communicating with MongoDB. Assumes that reactive
 * stream asynchronous calls are used rather than blocking methods.
 * 
 * @author Martin Lowe
 */
public interface PersistenceDao extends BeanHealth {

	/**
	 * Retrieves a list of typed results given the query passed.
	 * 
	 * @param q the query object for the current operation
	 * @return a future result set of objects of type set in query
	 */
	<T extends BareNode> List<T> get(RDBMSQuery<T> q);

	/**
	 * Adds a list of typed documents to the currently active database and schema,
	 * using the query object to access the document type.
	 * 
	 * @param <T>       the type of document to post
	 * @param q         the query object for the current operation
	 * @param documents the list of typed documents to add to the database instance.
	 * @return a future Void result indicating success on return.
	 */
	<T extends BareNode> void add(RDBMSQuery<T> q, List<T> documents);

	/**
	 * Deletes documents that match the given query.
	 * 
	 * @param <T> the type of document that is being deleted
	 * @param q   the query object for the current operation
	 * @return a future deletion result indicating whether the operation was
	 *         successful
	 */
	<T extends BareNode> void delete(RDBMSQuery<T> q);

	/**
	 * Counts the number of filtered results of the given document type present.
	 * 
	 * @param <T> the type of documents beign counted
	 * @param q   the query object for the current operation
	 * @return a future long result representing the number of results available for
	 *         the given query and docuement type.
	 */
	<T extends BareNode> CompletionStage<Long> count(RDBMSQuery<T> q);

	default void startup(@Observes StartupEvent event) {
		// intentionally empty
	}
}
