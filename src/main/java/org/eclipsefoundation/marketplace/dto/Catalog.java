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

public class Catalog {
	private int id;
	private String title;
	private String url;
	private boolean selfContained;
	private String icon;
	private String description;
	private String dependenciesRepository;
	private boolean searchEnabled;
	private boolean popularEnabled;
	private boolean recentEnabled;
	private boolean newsEnabled;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
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
	 * @return the selfContained
	 */
	public boolean isSelfContained() {
		return selfContained;
	}

	/**
	 * @param selfContained the selfContained to set
	 */
	public void setSelfContained(boolean selfContained) {
		this.selfContained = selfContained;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the dependenciesRepository
	 */
	public String getDependenciesRepository() {
		return dependenciesRepository;
	}

	/**
	 * @param dependenciesRepository the dependenciesRepository to set
	 */
	public void setDependenciesRepository(String dependenciesRepository) {
		this.dependenciesRepository = dependenciesRepository;
	}

	/**
	 * @return the searchEnabled
	 */
	public boolean isSearchEnabled() {
		return searchEnabled;
	}

	/**
	 * @param searchEnabled the searchEnabled to set
	 */
	public void setSearchEnabled(boolean searchEnabled) {
		this.searchEnabled = searchEnabled;
	}

	/**
	 * @return the popularEnabled
	 */
	public boolean isPopularEnabled() {
		return popularEnabled;
	}

	/**
	 * @param popularEnabled the popularEnabled to set
	 */
	public void setPopularEnabled(boolean popularEnabled) {
		this.popularEnabled = popularEnabled;
	}

	/**
	 * @return the recentEnabled
	 */
	public boolean isRecentEnabled() {
		return recentEnabled;
	}

	/**
	 * @param recentEnabled the recentEnabled to set
	 */
	public void setRecentEnabled(boolean recentEnabled) {
		this.recentEnabled = recentEnabled;
	}

	/**
	 * @return the newsEnabled
	 */
	public boolean isNewsEnabled() {
		return newsEnabled;
	}

	/**
	 * @param newsEnabled the newsEnabled to set
	 */
	public void setNewsEnabled(boolean newsEnabled) {
		this.newsEnabled = newsEnabled;
	}

}