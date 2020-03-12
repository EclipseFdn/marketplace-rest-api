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
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.eclipsefoundation.persistence.dto.BareNode;

/**
 * Domain object representing a marketplace listing version. EAGER loading is
 * required as the entity is nested, and gets called by hash sets.
 * 
 * @author Martin Lowe
 */
@Entity
@Table(
	indexes = {
		@Index(columnList="minJavaVersion"),
		@Index(columnList="listingId")
	}
)
public class ListingVersion extends BareNode {
	private String version;
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<String> eclipseVersions;
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<String> platforms;
	private int minJavaVersion;
	private String updateSiteUrl;
	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Set<FeatureId> featureIds;
	@Column(columnDefinition = "BINARY(16)")
	private UUID listingId;

	public ListingVersion() {
		this.eclipseVersions = new HashSet<>();
		this.platforms = new HashSet<>();
		this.featureIds = new HashSet<>();
	}
	
	@Override
	public void initializeLazyFields() {
		this.getEclipseVersions();
		this.getFeatureIds();
		this.getPlatforms();
	}

	/**
	 * @return the versionString
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
	 * @return the eclipseVersions
	 */
	public Set<String> getEclipseVersions() {
		return new HashSet<>(eclipseVersions);
	}

	/**
	 * @param eclipseVersions the eclipseVersions to set
	 */
	public void setEclipseVersions(Set<String> eclipseVersions) {
		Objects.requireNonNull(eclipseVersions);
		this.eclipseVersions = new HashSet<>(eclipseVersions);
	}

	/**
	 * @return the platforms
	 */
	public Set<String> getPlatforms() {
		return new HashSet<>(platforms);
	}

	/**
	 * @param platforms the platforms to set
	 */
	public void setPlatforms(Set<String> platforms) {
		Objects.requireNonNull(platforms);
		this.platforms = new HashSet<>(platforms);
	}

	/**
	 * @return the minimumJavaVersion
	 */
	public int getMinJavaVersion() {
		return minJavaVersion;
	}

	/**
	 * @param minJavaVersion the minJavaVersion to set
	 */
	public void setMinJavaVersion(int minJavaVersion) {
		this.minJavaVersion = minJavaVersion;
	}

	/**
	 * @return the updateSiteUrl
	 */
	public String getUpdateSiteUrl() {
		return updateSiteUrl;
	}

	/**
	 * @param updateSiteUrl the updateSiteUrl to set
	 */
	public void setUpdateSiteUrl(String updateSiteUrl) {
		this.updateSiteUrl = updateSiteUrl;
	}

	/**
	 * @return the featureIds
	 */
	public Set<FeatureId> getFeatureIds() {
		return new HashSet<>(featureIds);
	}

	/**
	 * @param featureIds the featureIds to set
	 */
	public void setFeatureIds(Set<FeatureId> featureIds) {
		Objects.requireNonNull(featureIds);
		this.featureIds = new HashSet<>(featureIds);
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

	public boolean validate() {
		return version != null && listingId != null && minJavaVersion < 0 && platforms.isEmpty()
				&& !eclipseVersions.isEmpty();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(getEclipseVersions(), getFeatureIds(), listingId, minJavaVersion,
				getPlatforms(), updateSiteUrl, version);
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
		ListingVersion other = (ListingVersion) obj;
		return Objects.equals(getEclipseVersions(), other.getEclipseVersions())
				&& Objects.equals(getFeatureIds(), other.getFeatureIds()) && Objects.equals(listingId, other.listingId)
				&& minJavaVersion == other.minJavaVersion && Objects.equals(getPlatforms(), other.getPlatforms())
				&& Objects.equals(updateSiteUrl, other.updateSiteUrl) && Objects.equals(version, other.version);
	}

}
