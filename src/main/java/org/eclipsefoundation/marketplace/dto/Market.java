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
import java.util.LinkedList;
import java.util.List;

import javax.json.bind.annotation.JsonbTransient;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Domain object representing a Market within the marketplace.
 * 
 * @author Martin Lowe
 * @since 05/2019
 */
@RegisterForReflection
public class Market {
	private String id;
	private String name;
	private String url;
	private List<String> categoryIds;
	private List<Category> categories;

	/**
	 * Default constructor. Creates an empty linkedlist for categories, as its
	 * unknown how many categories the market will reference.
	 */
	public Market() {
		this.categories = new LinkedList<>();
		this.categoryIds = new LinkedList<>();
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the categoryIds
	 */
	@JsonbTransient
	public List<String> getCategoryIds() {
		return new ArrayList<>(categoryIds);
	}

	/**
	 * @param categoryIds the categoryIds to set
	 */
	public void setCategoryIds(List<String> categoryIds) {
		this.categoryIds = new ArrayList<>(categoryIds);
	}
}
