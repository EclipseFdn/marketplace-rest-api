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

/**
 * Represents a listing catalog.
 * 
 * @author Martin Lowe
 */
public class Catalog {
	private String id;
	private String title;
	private String url;
	private boolean selfContained;
	private boolean searchEnabled;
	private String icon;
	private String description;
	private String dependenciesRepository;
	private List<Tab> tabs;

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
	 * @return the tabs
	 */
	public List<Tab> getTabs() {
		return new ArrayList<>(tabs);
	}

	/**
	 * @param tabs the tabs to set
	 */
	public void setTabs(List<Tab> tabs) {
		this.tabs = new ArrayList<>(tabs);
	}

}