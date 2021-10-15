/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eclipsefoundation.persistence.dto.BareNode;

/**
 * Holds a set of install metrics for the last year for a given listing.
 * 
 * @author Martin Lowe
 *
 */
@Entity
@Table
public class InstallMetrics extends BareNode {

	@OneToOne
	private Listing listing;
	@OneToMany(cascade = CascadeType.REMOVE)
	private Set<MetricPeriod> periods;
	private int total;

	/**
	 * @return the periods
	 */
	public Set<MetricPeriod> getPeriods() {
		return new HashSet<>(periods);
	}

	/**
	 * @param periods the periods to set
	 */
	public void setPeriods(Set<MetricPeriod> periods) {
		this.periods = new HashSet<>(periods);
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
		return Objects.hash(periods, total);
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
		return  Objects.equals(periods, other.periods)
				&& Objects.equals(total, other.total);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InstallMetrics [periods=");
		builder.append(periods);
		builder.append(", total=");
		builder.append(total);
		builder.append("]");
		return builder.toString();
	}

}
