/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an error report in the database.
 * 
 * @author Martin Lowe
 */
public class ErrorReport {

	private String id;
	private String title;
	private String body;
	private String status;
	private String statusMessage;
	private List<String> featureIDs;
	private String detailedMessage;
	private boolean read;
	private String listingId;
	
	/**
	 * 
	 */
	public ErrorReport() {
		this.featureIDs = new ArrayList<>();
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
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
	public List<String> getFeatureIDs() {
		return new ArrayList<>(featureIDs);
	}
	/**
	 * @param featureIDs the featureIDs to set
	 */
	public void setFeatureIds(List<String> featureIDs) {
		this.featureIDs = new ArrayList<>(featureIDs);
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
		return read;
	}
	/**
	 * @param read the read to set
	 */
	public void setRead(boolean read) {
		this.read = read;
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
	
}
