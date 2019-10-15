/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Domain object representing a marketplace listing solution version
 * 
 * @author Martin Lowe
 */
public class SolutionVersion {

	private String version;
	private List<String> eclipseVersions;
	private List<String> platforms;
	private String minJavaVersion;
	private String updateSiteUrl;
	private List<FeatureId> featureIds;
	
	public SolutionVersion() {
		this.eclipseVersions = new ArrayList<>();
		this.platforms = new ArrayList<>();
		this.featureIds = new ArrayList<>();
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
	public List<String> getEclipseVersions() {
		return new ArrayList<>(eclipseVersions);
	}

	/**
	 * @param eclipseVersions the eclipseVersions to set
	 */
	public void setEclipseVersions(List<String> eclipseVersions) {
		Objects.requireNonNull(eclipseVersions);
		this.eclipseVersions = new ArrayList<>(eclipseVersions);
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
	 * @return the minimumJavaVersion
	 */
	public String getMinJavaVersion() {
		return minJavaVersion;
	}

	/**
	 * @param minJavaVersion the minJavaVersion to set
	 */
	public void setMinJavaVersion(String minJavaVersion) {
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
	public List<FeatureId> getFeatureIds() {
		return featureIds;
	}

	/**
	 * @param featureIds the featureIds to set
	 */
	public void setFeatureIds(List<FeatureId> featureIds) {
		this.featureIds = featureIds;
	}

}
