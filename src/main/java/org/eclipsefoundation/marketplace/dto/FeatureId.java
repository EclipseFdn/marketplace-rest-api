/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto;

import java.util.Objects;

import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipsefoundation.persistence.dto.BareNode;

/**
 * Represents a feature in a listing solution version.
 * 
 * @author Martin Lowe
 */
@Entity
@Table
public class FeatureId extends BareNode {
	@JsonbProperty("feature_id")
	private String name;
	private String installState;
	private String versionId;
	
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

	/**
	 * @return the listingId
	 */
	public String getVersionId() {
		return versionId;
	}

	/**
	 * @param versionId the versionId to set
	 */
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(installState, name, versionId);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeatureId other = (FeatureId) obj;
		return Objects.equals(installState, other.installState) && Objects.equals(name, other.name)
				&& Objects.equals(versionId, other.versionId);
	}

}
