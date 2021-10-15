/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.persistence.health;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipsefoundation.persistence.dao.PersistenceDao;

/**
 * Liveness check implementation for the DB DAO layer.
 * 
 * @author Martin Lowe
 */
@Liveness
@ApplicationScoped
public class PersistenceDaoHealthCheck implements HealthCheck {

	@Inject
	PersistenceDao dao;

	@Override
	public HealthCheckResponse call() {
		return dao.health();
	}
}
