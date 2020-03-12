/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipsefoundation.persistence.dto.BareNode;

/**
 * Domain object representing the data stored for installs.
 * 
 * @author Martin Lowe
 */
@Entity
@Table
public class Install extends BareNode {

	private Date installDate;
	private String os;
	private String version;
	@Column(columnDefinition = "BINARY(16)")
	private UUID listingId;
	private String javaVersion;
	private String eclipseVersion;
	private String locale;

	/**
	 * @return the installDate
	 */
	public Date getInstallDate() {
		return installDate;
	}

	/**
	 * @param installDate the installDate to set
	 */
	public void setInstallDate(Date installDate) {
		this.installDate = installDate;
	}

	/**
	 * @return the os
	 */
	public String getOs() {
		return os;
	}

	/**
	 * @param os the os to set
	 */
	public void setOs(String os) {
		this.os = os;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the listingId
	 */
	public UUID getListingId() {
		return listingId;
	}

	/**
	 * @param listingId the listingId to set
	 */
	public void setListingId(UUID listingId) {
		this.listingId = listingId;
	}

	/**
	 * @return the javaVersion
	 */
	public String getJavaVersion() {
		return javaVersion;
	}

	/**
	 * @param javaVersion the javaVersion to set
	 */
	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}

	/**
	 * @return the eclipseVersion
	 */
	public String getEclipseVersion() {
		return eclipseVersion;
	}

	/**
	 * @param eclipseVersion the eclipseVersion to set
	 */
	public void setEclipseVersion(String eclipseVersion) {
		this.eclipseVersion = eclipseVersion;
	}

	/**
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(String locale) {
		this.locale = locale;
	}

	public boolean validate() {
		return version != null && listingId != null && os != null && eclipseVersion.isEmpty() && javaVersion != null
				&& installDate != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash(eclipseVersion, installDate, javaVersion, listingId, locale, os, version);
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
		Install other = (Install) obj;
		return Objects.equals(eclipseVersion, other.eclipseVersion) && Objects.equals(installDate, other.installDate)
				&& Objects.equals(javaVersion, other.javaVersion) && Objects.equals(listingId, other.listingId)
				&& Objects.equals(locale, other.locale) && Objects.equals(os, other.os)
				&& Objects.equals(version, other.version);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Install [id=");
		builder.append(getId());
		builder.append(", installDate=");
		builder.append(installDate);
		builder.append(", os=");
		builder.append(os);
		builder.append(", version=");
		builder.append(version);
		builder.append(", listingId=");
		builder.append(listingId);
		builder.append(", javaVersion=");
		builder.append(javaVersion);
		builder.append(", eclipseVersion=");
		builder.append(eclipseVersion);
		builder.append(", locale=");
		builder.append(locale);
		builder.append("]");
		return builder.toString();
	}

}
