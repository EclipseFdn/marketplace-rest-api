/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto;

import java.util.Date;

/**
 * Domain object representing the data stored for installs.
 * 
 * @author Martin Lowe
 */
public class Install {

	private String id;
	private Date installDate;
	private String os;
	private String version;
	private String listingId;
	private String javaVersion;
	private String eclipseVersion;
	private String locale;
	
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Install [id=");
		builder.append(id);
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
