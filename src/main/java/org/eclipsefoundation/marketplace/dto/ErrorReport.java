/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipsefoundation.persistence.dto.BareNode;

/**
 * Represents an error report in the database.
 * 
 * @author Martin Lowe
 */
@Entity
@Table
public class ErrorReport extends BareNode {
	private String title;
	private String body;
	private String status;
	private String statusMessage;
	@OneToOne(targetEntity = FeatureId.class)
	private FeatureId featureID;
	private String detailedMessage;
	private boolean isRead;
	private String listingId;
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the statusMessage
	 */
	public String getStatusMessage() {
		return statusMessage;
	}
	/**
	 * @param statusMessage the statusMessage to set
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	/**
	 * @return the featureIDs
	 */
	public FeatureId getFeatureID() {
		return featureID;
	}
	/**
	 * @param featureIDs the featureIDs to set
	 */
	public void setFeatureIds(FeatureId featureId) {
		this.featureID = featureId;
	}
	/**
	 * @return the detailedMessage
	 */
	public String getDetailedMessage() {
		return detailedMessage;
	}
	/**
	 * @param detailedMessage the detailedMessage to set
	 */
	public void setDetailedMessage(String detailedMessage) {
		this.detailedMessage = detailedMessage;
	}
	/**
	 * @return the read
	 */
	public boolean isRead() {
		return isRead;
	}
	/**
	 * @param isRead the read to set
	 */
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	/**
	 * @return the listingId
	 */
	public String getListingId() {
		return listingId;
	}
	/**
	 * @param listingId the listingId to set
	 */
	public void setListingId(String listingId) {
		this.listingId = listingId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash(body, detailedMessage, featureID, isRead, listingId, status, statusMessage, title);
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
		ErrorReport other = (ErrorReport) obj;
		return Objects.equals(body, other.body) && Objects.equals(detailedMessage, other.detailedMessage)
				&& Objects.equals(featureID, other.featureID) && isRead == other.isRead
				&& Objects.equals(listingId, other.listingId) && Objects.equals(status, other.status)
				&& Objects.equals(statusMessage, other.statusMessage) && Objects.equals(title, other.title);
	}
}
