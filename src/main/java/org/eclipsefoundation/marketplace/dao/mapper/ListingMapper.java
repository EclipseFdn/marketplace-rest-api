/*
 * Copyright (C) 2019 Eclipse Foundation and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*/
package org.eclipsefoundation.marketplace.dao.mapper;

import static org.eclipsefoundation.marketplace.helper.SolrHelper.setListField;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.eclipsefoundation.marketplace.helper.SolrHelper;
import org.eclipsefoundation.marketplace.model.Listing;
import org.eclipsefoundation.marketplace.namespace.SolrFieldNames;

/**
 * Mapping class for handling the transform between the Listing class and
 * SolrInputDocuments.
 * 
 * @author Martin Lowe
 */
public class ListingMapper implements SolrBeanMapper<Listing> {
	public static final ListingMapper INSTANCE = new ListingMapper();

	/**
	 * Hide the constructor as it shouldn't be used.
	 */
	private ListingMapper() {
	}

	@Override
	public Listing toBean(SolrDocument doc) {
		Listing out = new Listing();

		out.setDocid((String) doc.getFieldValue(SolrFieldNames.DOCID));
		out.setListingId((long) doc.getFieldValue(SolrFieldNames.LISTING_ID));
		out.setTitle((String) doc.getFieldValue(SolrFieldNames.LISTING_TITLE));
		out.setUrl((String) doc.getFieldValue(SolrFieldNames.LISTING_URL));
		out.setSupportUrl((String) doc.getFieldValue(SolrFieldNames.SUPPORT_PAGE_URL));
		out.setHomePageUrl((String) doc.getFieldValue(SolrFieldNames.HOME_PAGE_URL));
		out.setVersion((String) doc.getFieldValue(SolrFieldNames.LISTING_VERSION));
		out.setEclipseVersion((String) doc.getFieldValue(SolrFieldNames.LISTING_ECLIPSE_VERSION));
		out.setTeaser((String) doc.getFieldValue(SolrFieldNames.LISTING_TEASER));
		out.setBody((String) doc.getFieldValue(SolrFieldNames.LISTING_BODY));
		out.setOwner((String) doc.getFieldValue(SolrFieldNames.LISTING_OWNER));
		out.setStatus((String) doc.getFieldValue(SolrFieldNames.LISTING_STATUS));
		out.setCompanyName((String) doc.getFieldValue(SolrFieldNames.LISTING_COMPANY_NAME));
		out.setInstallsRecent((long) doc.getFieldValue(SolrFieldNames.RECENT_NSTALLS));
		out.setInstallsTotal((long) doc.getFieldValue(SolrFieldNames.TOTAL_NSTALLS));

		// check that it exists first, as non-existant fields throw exceptions
		if (doc.containsKey(SolrFieldNames.PLATFORMS)) {
			setListField(doc.getFieldValue(SolrFieldNames.PLATFORMS), out::setPlatforms);
		}
		if (doc.containsKey(SolrFieldNames.LICENSE_TYPE)) {
			setListField(doc.getFieldValue(SolrFieldNames.LICENSE_TYPE), out::setLicenseType);
		}
		if (doc.containsKey(SolrFieldNames.INSTALLABLE_UNITS)) {
			setListField(doc.getFieldValue(SolrFieldNames.INSTALLABLE_UNITS), out::setInstallableUnits);
		}
		if (doc.containsKey(SolrFieldNames.MARKETPLACE_FAVORITES)) {
			out.setFavoriteCount((long) doc.getFieldValue(SolrFieldNames.MARKETPLACE_FAVORITES));
		}

		// convert date to epoch milli
		Date creationDate = (Date) doc.getFieldValue(SolrFieldNames.CREATION_DATE);
		out.setCreationDate(creationDate.toInstant().toEpochMilli());

		Date updateDate = (Date) doc.getFieldValue(SolrFieldNames.UPDATE_DATE);
		out.setUpdateDate(updateDate.toInstant().toEpochMilli());

		// handle int to boolean conversion, where 1 is true
		boolean isFoundationMember = (long) doc.getFieldValue(SolrFieldNames.FOUNDATION_MEMBER_FLAG) == 1;
		out.setFoundationMember(isFoundationMember);

		return out;
	}

	@Override
	public SolrInputDocument toDocument(Listing document) {
		Map<String, SolrInputField> fields = new HashMap<>();
		SolrHelper.addInputField(SolrFieldNames.DOCID, document.getDocid(), fields);
		SolrHelper.addInputField(SolrFieldNames.LISTING_ID, document.getListingId(), fields);
		SolrHelper.addInputField(SolrFieldNames.LISTING_TITLE, document.getTitle(), fields);
		SolrHelper.addInputField(SolrFieldNames.LISTING_URL, document.getUrl(), fields);
		SolrHelper.addInputField(SolrFieldNames.SUPPORT_PAGE_URL, document.getSupportUrl(), fields);
		SolrHelper.addInputField(SolrFieldNames.HOME_PAGE_URL, document.getHomePageUrl(), fields);
		SolrHelper.addInputField(SolrFieldNames.LISTING_VERSION, document.getVersion(), fields);
		SolrHelper.addInputField(SolrFieldNames.LISTING_ECLIPSE_VERSION, document.getEclipseVersion(), fields);
		SolrHelper.addInputField(SolrFieldNames.LISTING_BODY, document.getTeaser(), fields);
		SolrHelper.addInputField(SolrFieldNames.LISTING_TEASER, document.getBody(), fields);
		SolrHelper.addInputField(SolrFieldNames.MARKETPLACE_FAVORITES, document.getFavoriteCount(), fields);
		SolrHelper.addInputField(SolrFieldNames.RECENT_NSTALLS, document.getInstallsRecent(), fields);
		SolrHelper.addInputField(SolrFieldNames.TOTAL_NSTALLS, document.getInstallsTotal(), fields);
		SolrHelper.addInputField(SolrFieldNames.LICENSE_TYPE, document.getLicenseType(), fields);
		SolrHelper.addInputField(SolrFieldNames.PLATFORMS, document.getPlatforms(), fields);
		SolrHelper.addInputField(SolrFieldNames.INSTALLABLE_UNITS, document.getInstallableUnits(), fields);
		SolrHelper.addInputField(SolrFieldNames.LISTING_OWNER, document.getOwner(), fields);
		SolrHelper.addInputField(SolrFieldNames.LISTING_STATUS, document.getStatus(), fields);
		SolrHelper.addInputField(SolrFieldNames.LISTING_COMPANY_NAME, document.getCompanyName(), fields);

		// convert epoch milli to date
		SolrHelper.addInputField(SolrFieldNames.UPDATE_DATE, new Date(document.getUpdateDate()), fields);
		SolrHelper.addInputField(SolrFieldNames.CREATION_DATE, new Date(document.getCreationDate()), fields);

		// handle boolean to int conversion, where 1 is true
		SolrHelper.addInputField(SolrFieldNames.FOUNDATION_MEMBER_FLAG, document.isFoundationMember() ? 1 : 0, fields);

		return new SolrInputDocument(fields);
	}
}
