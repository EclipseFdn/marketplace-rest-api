/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto;

import javax.json.bind.annotation.JsonbProperty;

/**
 * Represents a feature in a listing solution version.
 * 
 * @author Martin Lowe
 */
public class FeatureId {
	@JsonbProperty("feature_id")
	private String name;
	private String installState;
	
	/**
	 * @return the featureId
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the featureId to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the installState
	 */
	public String getInstallState() {
		return installState;
	}
	/**
	 * @param installState the installState to set
	 */
	public void setInstallState(String installState) {
		this.installState = installState;
	}
	
}
