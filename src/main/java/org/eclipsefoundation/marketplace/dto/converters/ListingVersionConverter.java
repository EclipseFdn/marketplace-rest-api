/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.converters;

import java.util.stream.Collectors;

import org.bson.Document;
import org.eclipsefoundation.marketplace.dto.ListingVersion;
import org.eclipsefoundation.marketplace.helper.JavaVersionHelper;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;

/**
 * Converter implementation for the {@link ListingVersion} object.
 * 
 * @author Martin Lowe
 */
public class ListingVersionConverter implements Converter<ListingVersion> {

	private final FeatureIdConverter featureIdConverter = new FeatureIdConverter();

	@Override
	public ListingVersion convert(Document src) {
		ListingVersion version = new ListingVersion();
		version.setId(src.getString(DatabaseFieldNames.DOCID));
		version.setListingId(src.getString(DatabaseFieldNames.LISTING_ID));
		version.setEclipseVersions(src.getList("compatible_versions", String.class));
		version.setPlatforms(src.getList("platforms", String.class));
		version.setMinJavaVersion(JavaVersionHelper.convertToDisplayValue(Integer.toString(src.getInteger("min_java_version"))));
		version.setUpdateSiteUrl(src.getString("update_site_url"));
		version.setVersion(src.getString("version"));
		version.setFeatureIds(src.getList(DatabaseFieldNames.FEATURE_IDS, Document.class).stream()
				.map(featureIdConverter::convert).collect(Collectors.toList()));
		return version;
	}

	@Override
	public Document convert(ListingVersion src) {
		Document doc = new Document();
		doc.put(DatabaseFieldNames.DOCID, src.getId());
		doc.put(DatabaseFieldNames.LISTING_ID, src.getListingId());
		doc.put("compatible_versions", src.getEclipseVersions());
		doc.put("platforms", src.getPlatforms());
		doc.put("min_java_version", Integer.valueOf(JavaVersionHelper.convertToDBSafe(src.getMinJavaVersion())));
		doc.put("update_site_url", src.getUpdateSiteUrl());
		doc.put("version", src.getVersion());
		doc.put(DatabaseFieldNames.FEATURE_IDS,
				src.getFeatureIds().stream().map(featureIdConverter::convert).collect(Collectors.toList()));
		return doc;
	}
}
