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

public class Wizard {
	private String title;
	private String icon;
	private boolean searchEnabled;
	private boolean popularEnabled;
	private boolean recentEnabled;
	private boolean newsEnabled;

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