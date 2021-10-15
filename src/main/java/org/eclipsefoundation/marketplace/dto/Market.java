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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipsefoundation.persistence.dto.NodeBase;

/**
 * Domain object representing a Market within the marketplace.
 * 
 * @author Martin Lowe
 * @since 05/2019
 */
@Entity
@Table
public class Market extends NodeBase {
	@ManyToMany
	private Set<Listing> listingIds;
	@Transient
	private Set<Category> categories;

	/**
	 * Default constructor. Creates an empty linkedlist for categories, as its
	 * unknown how many categories the market will reference.
	 */
	public Market() {
		this.listingIds = new HashSet<>();
		this.categories = new HashSet<>();
	}

	/**
	 * Copies unique categories from all listings associated w/ Market into transient categories field
	 */
	@PostLoad
	private void post() {
		listingIds.stream().map(Listing::getCategories).flatMap(Set::stream).collect(Collectors.toList())
				.forEach(c -> {
					if (!categories.contains((Category) c)) {
						this.categories.add(c);
					}
				});
	}

	/**
	 * @return the listingIds
	 */
	public List<Listing> getListingIds() {
		return new ArrayList<>(listingIds);
	}

	/**
	 * @param listingIds the listingIds to set
	 */
	public void setListingIds(Set<Listing> listingIds) {
		this.listingIds = new HashSet<>(listingIds);
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
	public void setCategories(Set<Category> categories) {
		this.categories = new HashSet<>(categories);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(categories, listingIds);
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
		Market other = (Market) obj;
		return Objects.equals(categories, other.categories) && Objects.equals(listingIds, other.listingIds);
	}
}
