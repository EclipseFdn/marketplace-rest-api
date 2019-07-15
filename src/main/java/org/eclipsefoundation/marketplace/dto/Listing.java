/*
 * Copyright (C) 2019 Eclipse Foundation and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*/
package org.eclipsefoundation.marketplace.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Listing {

	private String id;
	private long listingId;
	private String title;
	private String url;
	private String supportUrl;
	private String homePageUrl;
	private String updateUrl;
	private String version;
	private String eclipseVersion;
	private String teaser;
	private String body;
	private String owner;
	private String status;
	private String companyName;
	private boolean foundationMember;
	private long installsTotal;
	private long installsRecent;
	private long favoriteCount;
	private long creationDate;
	private long updateDate;
	private List<String> licenseType;
	private List<String> platforms;
	private List<String> installableUnits;

	/**
	 * Default constructor, sets lists to empty lists to stop null pointers
	 */
	public Listing() {
		this.platforms = new ArrayList<>();
		this.licenseType = new ArrayList<>();
		this.installableUnits = new ArrayList<>();
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
	 * @return the listingId
	 */
	public long getListingId() {
		return listingId;
	}

	/**
	 * @param listingId the id to set
	 */
	public void setListingId(long listingId) {
		this.listingId = listingId;
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
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the supportUrl
	 */
	public String getSupportUrl() {
		return supportUrl;
	}

	/**
	 * @param supportUrl the supportUrl to set
	 */
	public void setSupportUrl(String supportUrl) {
		this.supportUrl = supportUrl;
	}

	/**
	 * @return the homePageUrl
	 */
	public String getHomePageUrl() {
		return homePageUrl;
	}

	/**
	 * @param homePageUrl the homePageUrl to set
	 */
	public void setHomePageUrl(String homePageUrl) {
		this.homePageUrl = homePageUrl;
	}

	/**
	 * @return the updateUrl
	 */
	public String getUpdateUrl() {
		return updateUrl;
	}

	/**
	 * @param updateUrl the updateUrl to set
	 */
	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
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
	 * @return the teaser
	 */
	public String getTeaser() {
		return teaser;
	}

	/**
	 * @param teaser the teaser to set
	 */
	public void setTeaser(String teaser) {
		this.teaser = teaser;
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
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
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
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the foundationMember
	 */
	public boolean isFoundationMember() {
		return foundationMember;
	}

	/**
	 * @param foundationMember the foundationMember to set
	 */
	public void setFoundationMember(boolean foundationMember) {
		this.foundationMember = foundationMember;
	}

	/**
	 * @return the installsTotal
	 */
	public long getInstallsTotal() {
		return installsTotal;
	}

	/**
	 * @param installsTotal the installsTotal to set
	 */
	public void setInstallsTotal(long installsTotal) {
		this.installsTotal = installsTotal;
	}

	/**
	 * @return the installsRecent
	 */
	public long getInstallsRecent() {
		return installsRecent;
	}

	/**
	 * @param installsRecent the installsRecent to set
	 */
	public void setInstallsRecent(long installsRecent) {
		this.installsRecent = installsRecent;
	}

	/**
	 * @return the favoriteCount
	 */
	public long getFavoriteCount() {
		return favoriteCount;
	}

	/**
	 * @param favoriteCount the favoriteCount to set
	 */
	public void setFavoriteCount(long favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	/**
	 * @return the creationDate
	 */
	public long getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the updateDate
	 */
	public long getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(long updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * @return the licenseType
	 */
	public List<String> getLicenseType() {
		return new ArrayList<>(licenseType);
	}

	/**
	 * @param licenseType the licenseType to set
	 */
	public void setLicenseType(List<String> licenseType) {
		Objects.requireNonNull(licenseType);
		this.licenseType = new ArrayList<>(licenseType);
	}

	/**
	 * @return the platforms
	 */
	public List<String> getPlatforms() {
		return new ArrayList<>(platforms);
	}

	/**
	 * @param platforms the platforms to set
	 */
	public void setPlatforms(List<String> platforms) {
		Objects.requireNonNull(platforms);
		this.platforms = new ArrayList<>(platforms);
	}

	/**
	 * @return the installableUnits
	 */
	public List<String> getInstallableUnits() {
		return new ArrayList<>(installableUnits);
	}

	/**
	 * @param installableUnits the installableUnits to set
	 */
	public void setInstallableUnits(List<String> installableUnits) {
		Objects.requireNonNull(installableUnits);
		this.installableUnits = new ArrayList<>(installableUnits);
	}

}