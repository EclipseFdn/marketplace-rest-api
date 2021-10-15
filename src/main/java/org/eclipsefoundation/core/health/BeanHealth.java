/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.core.health;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponse.State;

/**
 * Interface for indicating health of a bean in a response that fits in the
 * Microprofile spec.
 * 
 * @author Martin Lowe
 */
public interface BeanHealth {

	/**
	 * Provides a response indicating current health of the bean
	 * 
	 * @return a Microprofile HealthCheckResponse indicating state and passing
	 *         errors if they exist
	 */
	public HealthCheckResponse health();

	/**
	 * Provides health of the application as a boolean rather than a Response
	 * object.
	 * 
	 * @return true if state is UP, otherwise false.
	 */
	default boolean isHealthy() {
		return health().getState().equals(State.UP);
	}
}
