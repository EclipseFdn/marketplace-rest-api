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
 * Contains names of Solr fields for use when working with Solr data sets.
 * 
 * @author Martin Lowe
 *
 */
public final class SolrFieldNames {

	public static final String DOCID = "id";
	public static final String LISTING_ID = "entity_id";
	public static final String LISTING_TITLE = "label";
	public static final String LISTING_URL = "url";
	public static final String LISTING_TEASER = "teaser";
	public static final String LISTING_BODY = "content";
	public static final String LISTING_OWNER = "ss_Name";
	public static final String LISTING_STATUS = "sm_field_status";
	public static final String LISTING_COMPANY_NAME = "sm_field_companyname";
	public static final String RECENT_NSTALLS = "itm_field_installs_recent";
	public static final String TOTAL_NSTALLS = "itm_field_installs_total";
	public static final String MARKETPLACE_FAVORITES = "its_field_mpc_favorite_count";
	public static final String LICENSE_TYPE = "sm_field_licensetype";
	public static final String PLATFORMS = "sm_field_platform";
	public static final String INSTALLABLE_UNITS = "sm_field_installable_units";
	public static final String CREATION_DATE = "ds_created";
	public static final String UPDATE_DATE = "ds_changed";
	public static final String FOUNDATION_MEMBER_FLAG = "im_field_membercompany";
	public static final String HOME_PAGE_URL = "sm_field_url";
	public static final String SUPPORT_PAGE_URL = "sm_field_supporturl";
	public static final String LISTING_VERSION = "sm_field_version";
	public static final String LISTING_ECLIPSE_VERSION = "sm_field_eclipseversion";

	private SolrFieldNames() {
	}
}
