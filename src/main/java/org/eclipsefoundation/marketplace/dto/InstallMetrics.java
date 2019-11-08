/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto;

import java.util.List;
import java.util.Objects;

/**
 * Holds a set of install metrics for the last year for a given listing.
 * 
 * @author Martin Lowe
 *
 */
public class InstallMetrics {

	private String listingId;
	private List<MetricPeriod> periods;
	private int total;

	public InstallMetrics() {
	}

	/**
	 * @param listingId
	 * @param periods
	 */
	public InstallMetrics(String listingId, List<MetricPeriod> periods, int total) {
		this.listingId = listingId;
		this.periods = periods;
		this.total = total;
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

	/**
	 * @return the periods
	 */
	public List<MetricPeriod> getPeriods() {
		return periods;
	}

	/**
	 * @param periods the periods to set
	 */
	public void setPeriods(List<MetricPeriod> periods) {
		this.periods = periods;
	}

	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(int total) {
		this.total = total;
	}

	@Override
	public int hashCode() {
		return Objects.hash(listingId, periods, total);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		InstallMetrics other = (InstallMetrics) obj;
		return Objects.equals(listingId, other.listingId) && Objects.equals(periods, other.periods)
				&& Objects.equals(total, other.total);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InstallMetrics [listingId=");
		builder.append(listingId);
		builder.append(", periods=");
		builder.append(periods);
		builder.append(", total=");
		builder.append(total);
		builder.append("]");
		return builder.toString();
	}

}
