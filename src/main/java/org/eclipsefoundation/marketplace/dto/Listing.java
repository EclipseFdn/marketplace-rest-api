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

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

import org.eclipsefoundation.marketplace.model.SortableField;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Domain object representing a marketplace listing.
 * 
 * @author Martin Lowe
 */
@RegisterForReflection
public class Listing extends NodeBase {

	private String supportUrl;
	private String homepageUrl;
	private String teaser;
	private String body;
	private String status;
	private String logo;
	private boolean foundationMember;

	@SortableField(name = "installs_count")
	@JsonbProperty("installs_count")
	private long installsTotal;

	@SortableField(name = "installs_count_recent")
	@JsonbProperty("installs_count_recent")
	private long installsRecent;

	@SortableField
	private long favoriteCount;

	@SortableField(name = DatabaseFieldNames.CREATION_DATE)
	@JsonbProperty(DatabaseFieldNames.CREATION_DATE)
	private long creationDate;

	@SortableField(name = DatabaseFieldNames.UPDATE_DATE)
	@JsonbProperty(DatabaseFieldNames.UPDATE_DATE)
	private long updateDate;
	@JsonbProperty(DatabaseFieldNames.LICENSE_TYPE)
	private String license;
	private List<String> categoryIds;
	private List<Category> categories;
	private Organization organization;
	private List<Author> authors;
	private List<Tag> tags;
	private List<SolutionVersion> versions;

	/**
	 * Default constructor, sets lists to empty lists to stop null pointers
	 */
	public Listing() {
		this.authors = new ArrayList<>();
		this.tags = new ArrayList<>();
		this.versions = new ArrayList<>();
		this.categoryIds = new ArrayList<>();
		this.categories = new ArrayList<>();
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
	 * @return the homepageUrl
	 */
	public String getHomepageUrl() {
		return homepageUrl;
	}

	/**
	 * @param homepageUrl the homepageUrl to set
	 */
	public void setHomepageUrl(String homepageUrl) {
		this.homepageUrl = homepageUrl;
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
	 * @return the logo
	 */
	public String getLogo() {
		return logo;
	}

	/**
	 * @param logo the logo to set
	 */
	public void setLogo(String logo) {
		this.logo = logo;
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
	@JsonbTransient
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
	 * @return the license
	 */
	public String getLicense() {
		return license;
	}

	/**
	 * @param license the license to set
	 */
	public void setLicense(String license) {
		this.license = license;
	}

	/**
	 * @return the categoryIds
	 */
	@JsonbTransient
	public List<String> getCategoryIds() {
		return categoryIds;
	}

	/**
	 * @param categoryIds the categoryIds to set
	 */
	public void setCategoryIds(List<String> categoryIds) {
		this.categoryIds = new ArrayList<>(categoryIds);
	}

	/**
	 * @return the categories
	 */
	public List<Category> getCategories() {
		return new ArrayList<>(categories);
	}

	/**
	 * @param categories the categories to set
	 */
	@JsonbTransient
	public void setCategories(List<Category> categories) {
		this.categories = new ArrayList<>(categories);
	}

	/**
	 * @return the organization
	 */
	public Organization getOrganization() {
		return organization;
	}

	/**
	 * @param organization the organization to set
	 */
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	/**
	 * @return the authors
	 */
	public List<Author> getAuthors() {
		return new ArrayList<>(authors);
	}

	/**
	 * @param authors the authors to set
	 */
	public void setAuthors(List<Author> authors) {
		Objects.requireNonNull(authors);
		this.authors = new ArrayList<>(authors);
	}

	/**
	 * @return the tags
	 */
	public List<Tag> getTags() {
		return new ArrayList<>(tags);
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<Tag> tags) {
		Objects.requireNonNull(tags);
		this.tags = new ArrayList<>(tags);
	}

	/**
	 * @return the versions
	 */
	public List<SolutionVersion> getVersions() {
		return new ArrayList<>(versions);
	}

	/**
	 * @param versions the versions to set
	 */
	public void setVersions(List<SolutionVersion> versions) {
		Objects.requireNonNull(versions);
		this.versions = new ArrayList<>(versions);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(authors, body, categories, categoryIds, creationDate, favoriteCount,
				foundationMember, homepageUrl, installsRecent, installsTotal, license, logo, organization, status,
				supportUrl, tags, teaser, updateDate, versions);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Listing other = (Listing) obj;
		return Objects.equals(authors, other.authors) && Objects.equals(body, other.body)
				&& Objects.equals(categories, other.categories) && Objects.equals(categoryIds, other.categoryIds)
				&& creationDate == other.creationDate && favoriteCount == other.favoriteCount
				&& foundationMember == other.foundationMember && Objects.equals(homepageUrl, other.homepageUrl)
				&& installsRecent == other.installsRecent && installsTotal == other.installsTotal
				&& Objects.equals(logo, other.logo) && Objects.equals(organization, other.organization)
				&& Objects.equals(status, other.status) && Objects.equals(supportUrl, other.supportUrl)
				&& Objects.equals(tags, other.tags) && Objects.equals(teaser, other.teaser)
				&& updateDate == other.updateDate && Objects.equals(versions, other.versions);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Listing [");
		sb.append(", id=").append(getId());
		sb.append(", name=").append(getTitle());
		sb.append(", url=").append(getUrl());
		sb.append(", supportUrl=").append(supportUrl);
		sb.append(", homepageUrl=").append(homepageUrl);
		sb.append(", teaser=").append(teaser);
		sb.append(", body=").append(body);
		sb.append(", status=").append(status);
		sb.append(", logo=").append(logo);
		sb.append(", foundationMember=").append(foundationMember);
		sb.append(", installsTotal=").append(installsTotal);
		sb.append(", installsRecent=").append(installsRecent);
		sb.append(", favoriteCount=").append(favoriteCount);
		sb.append(", creationDate=").append(creationDate);
		sb.append(", updateDate=").append(updateDate);
		sb.append(", license=").append(license);
		sb.append(", organization=").append(organization);
		sb.append(", authors=").append(authors);
		sb.append(", tags=").append(tags);
		sb.append(", versions=").append(versions);
		sb.append(']');
		return sb.toString();
	}
}