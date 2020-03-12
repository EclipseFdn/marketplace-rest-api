/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dao.impl;

import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipsefoundation.core.exception.MaintenanceException;
import org.eclipsefoundation.persistence.dao.PersistenceDao;
import org.eclipsefoundation.persistence.model.RDBMSQuery;
import org.eclipsefoundation.persistence.model.ParameterizedSQLStatement.Clause;
import org.eclipsefoundation.persistence.dto.BareNode;
import org.eclipsefoundation.search.dao.SearchIndexDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the DB DAO, persisting via the Hibernate framework.
 * 
 * @author Martin Lowe
 */
@ApplicationScoped
public class DefaultHibernateDao implements PersistenceDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHibernateDao.class);

	@Inject
	EntityManager em;
	@Inject
	SearchIndexDAO indexDAO;

	@ConfigProperty(name = "eclipse.db.default.limit")
	int defaultLimit;

	@ConfigProperty(name = "eclipse.db.default.limit.max")
	int defaultMax;

	@ConfigProperty(name = "eclipse.db.maintenance", defaultValue = "false")
	boolean maintenanceFlag;

	@Override
	public <T extends BareNode> List<T> get(RDBMSQuery<T> q) {
		if (maintenanceFlag) {
			throw new MaintenanceException();
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Querying DB using the following query: {}", q);
		}
		LOGGER.error("SQL: {}\nParams: {}", q.getFilter().getSelectSql(), q.getFilter().getParams());

		// build base query
		TypedQuery<T> query = em.createQuery(q.getFilter().getSelectSql(), q.getDocType());

		// add ordinal parameters
		int ord = 1;
		for (Clause c : q.getFilter().getClauses()) {
			for (Object param : c.getParams()) {
				query.setParameter(ord++, param);
			}
		}

		// check if result set should be limited
		if (q.getDTOFilter().useLimit()) {
			query = query.setFirstResult(getOffset(q)).setMaxResults(getLimit(q));
		}
		// run the query
		return query.getResultList();
	}

	@Transactional
	@Override
	public <T extends BareNode> void add(RDBMSQuery<T> q, List<T> documents) {
		if (maintenanceFlag) {
			throw new MaintenanceException();
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Adding {} documents to DB of type {}", documents.size(), q.getDocType().getSimpleName());
		}
		// for each doc, check if update or create
		for (T doc : documents) {
			if (doc.getId() != null) {
				// ensure this object exists before merging on it
				if (em.find(q.getDocType(), doc.getId()) != null) {
					LOGGER.debug("Merging document with existing document with id '{}'", doc.getId());
					em.merge(doc);
				} else {
					LOGGER.debug("Persisting new document with id '{}'", doc.getId());
					em.persist(doc);
				}
			} else {
				LOGGER.debug("Persisting new document with generated UUID ID");
				em.persist(doc);
			}
		}
		// indexDAO.createOrUpdate(q, documents);
	}

	@Transactional
	@Override
	public <T extends BareNode> void delete(RDBMSQuery<T> q) {
		if (maintenanceFlag) {
			throw new MaintenanceException();
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Removing documents from DB using the following query: {}", q);
		}
		// retrieve results for the given deletion query to delete using entity manager
		List<T> results = get(q);
		if (results.isEmpty()) {
			throw new NoResultException("Could not find any documents with given filters");
		}
		// remove all matched documents
		results.forEach(em::remove);
		indexDAO.remove(results);
	}

	@Transactional
	@Override
	public <T extends BareNode> CompletionStage<Long> count(RDBMSQuery<T> q) {
		if (maintenanceFlag) {
			throw new MaintenanceException();
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Counting documents in DB that match the following query: {}", q);
		}
		throw new RuntimeException();
	}

	@Override
	public HealthCheckResponse health() {
		HealthCheckResponseBuilder b = HealthCheckResponse.named("DB readiness");
		if (maintenanceFlag) {
			return b.down().withData("error", "Maintenance flag is set").build();
		}
		return b.up().build();
	}

	private int getLimit(RDBMSQuery<?> q) {
		return q.getLimit() > 0 ? Math.min(q.getLimit(), defaultMax) : defaultLimit;
	}

	private int getOffset(RDBMSQuery<?> q) {
		// if first page, no offset
		if (q.getPage() <= 1) {
			return 0;
		}
		int limit = getLimit(q);
		return (limit * q.getPage()) - limit;
	}
}
