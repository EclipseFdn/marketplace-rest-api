/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto;

import java.util.Date;

import javax.json.bind.annotation.JsonbTransient;

/**
 * Represents a set of install metrics for a given listing in a time period.
 * This is represented without filters and cannot be filtered down.
 * 
 * @author Martin Lowe
 *
 */
public class MetricPeriod {

	private String listingId;
	private Integer count;
	private Date start;
	private Date end;

	/**
	 * @return the listingId
	 */
	@JsonbTransient
	public String getListingId() {
		return listingId;
	}

	/**
	 * @param listingId the listingId to set
	 */
	public void setListingId(String listingId) {
		this.listingId = listingId;
	}

	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}

	/**
	 * @return the start
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(Date start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public Date getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(Date end) {
		this.end = end;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InstallMetric [listingId=");
		builder.append(listingId);
		builder.append(", count=");
		builder.append(count);
		builder.append(", start=");
		builder.append(start);
		builder.append(", end=");
		builder.append(end);
		builder.append("]");
		return builder.toString();
	}
}
