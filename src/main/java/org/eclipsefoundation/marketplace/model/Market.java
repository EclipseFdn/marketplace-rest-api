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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipsefoundation.marketplace.exceptions.InvalidParameterException;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Domain object representing a Market within the marketplace.
 * 
 * @author Martin Lowe
 * @since 05/2019
 */
@RegisterForReflection
public class Market {
	private int id;
	private String name;
	private String url;
	private List<Category> categories;

	/**
	 * Default constructor. Creates an empty linkedlist for categories, as its
	 * unknown how many categories the market will reference.
	 */
	public Market() {
		this.categories = new LinkedList<>();
	}

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
	public void setCategories(List<Category> categories) {
		this.categories = new ArrayList<>(categories);
	}

	/**
	 * Remove a category from the market definition.
	 * 
	 * @param c the category to remove if present
	 * @return the category removed if it was present, or null if it was missing.
	 * @throws InvalidParameterException if the passed category is an illegal value
	 *                                   (i.e. null)
	 */
	public Category removeCategory(Category c) {
		int idx = categories.indexOf(c);
		if (idx > 0) {
			return this.categories.remove(idx);
		} else {
			return null;
		}
	}

	/**
	 * Add a category to the market definition.
	 * 
	 * @param c the category to add
	 * @throws InvalidParameterException if the passed category is an illegal value
	 *                                   (i.e. null)
	 */
	public void addCategory(Category c) {
		if (c == null) {
			throw new InvalidParameterException("Cannot add a null category to list");
		}
		this.categories.add(c);
	}
}
