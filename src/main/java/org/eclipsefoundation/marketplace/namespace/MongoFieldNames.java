/*
 * Copyright (C) 2019 Eclipse Foundation and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*/
package org.eclipsefoundation.marketplace.namespace;

/**
 * Contains names of MongoDB fields for use when working with MongoDB documents.
 * 
 * @author Martin Lowe
 *
 */
public final class MongoFieldNames {

	public static final String DOCID = "id";
	public static final String LISTING_ID = "listing_id";
	public static final String LISTING_TITLE = "title";
	public static final String LISTING_URL = "url";
	public static final String LISTING_TEASER = "teaser";
	public static final String LISTING_BODY = "body";
	public static final String LISTING_AUTHORS = "authors";
	public static final String LISTING_STATUS = "status";
	public static final String LISTING_ORGANIZATIONS = "organization";
	public static final String RECENT_NSTALLS = "installs_recent";
	public static final String TOTAL_NSTALLS = "installs_total";
	public static final String MARKETPLACE_FAVORITES = "favorite_count";
	public static final String LICENSE_TYPE = "license_type";
	public static final String PLATFORMS = "platforms";
	public static final String INSTALLABLE_UNITS = "installable_units";
	public static final String CREATION_DATE = "created";
	public static final String UPDATE_DATE = "changed";
	public static final String FOUNDATION_MEMBER_FLAG = "member_company";
	public static final String HOME_PAGE_URL = "homepage_url";
	public static final String SUPPORT_PAGE_URL = "support_url";
	public static final String LISTING_VERSIONS = "versions";
	public static final String LISTING_TAGS = "tags";

	private MongoFieldNames() {
	}
}
