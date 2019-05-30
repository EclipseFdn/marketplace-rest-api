/*
 * Copyright (C) 2019 Eclipse Foundation and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*/
package org.eclipsefoundation.marketplace.model;

public class Catalog {
	private int id;
	private String title;
	private String url;
	private boolean selfContained;
	private String icon;
	private String description;
	private String dependenciesRepository;
	private Wizard wizard;

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
	 * @return the wizard
	 */
	public Wizard getWizard() {
		return wizard;
	}

	/**
	 * @param wizard the wizard to set
	 */
	public void setWizard(Wizard wizard) {
		this.wizard = wizard;
	}

}