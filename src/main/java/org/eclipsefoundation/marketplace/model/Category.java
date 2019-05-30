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

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Category {
	private int id;
	private int marketId;
	private String name;
	private String url;
	private Listing node;

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
	 * @return the marketId
	 */
	public int getMarketId() {
		return marketId;
	}

	/**
	 * @param marketId the marketId to set
	 */
	public void setMarketId(int marketId) {
		this.marketId = marketId;
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
	 * @return the node
	 */
	public Listing getNode() {
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(Listing node) {
		this.node = node;
	}
}
