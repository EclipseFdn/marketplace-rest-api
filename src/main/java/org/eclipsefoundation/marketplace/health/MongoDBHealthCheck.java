/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.health;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipsefoundation.marketplace.dao.MongoDao;

/**
 * Liveness check implementation for the MongoDB DAO layer.
 * 
 * @author Martin Lowe
 */
@Liveness
@ApplicationScoped
public class MongoDBHealthCheck implements HealthCheck {

	@Inject
	MongoDao dao;

	@Override
	public HealthCheckResponse call() {
		return dao.health();
	}
}
