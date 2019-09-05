/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.converters;

import org.bson.Document;
import org.eclipsefoundation.marketplace.dto.SolutionVersion;

/**
 * Converter implementation for the {@link SolutionVersion} object.
 * 
 * @author Martin Lowe
 */
public class SolutionVersionConverter implements Converter<SolutionVersion> {

	@Override
	public SolutionVersion convert(Document src) {
		SolutionVersion version = new SolutionVersion();
		version.setEclipseVersions(src.getList("compatible_versions", String.class));
		version.setPlatforms(src.getList("platforms", String.class));
		version.setMinJavaVersion(src.getString("min_java_version"));
		version.setUpdateSiteUrl(src.getString("update_site_url"));
		version.setVersion(src.getString("version"));

		return version;
	}

	@Override
	public Document convert(SolutionVersion src) {
		Document doc = new Document();
		doc.put("compatible_versions", src.getEclipseVersions());
		doc.put("platforms", src.getPlatforms());
		doc.put("min_java_version", src.getMinJavaVersion());
		doc.put("update_site_url", src.getUpdateSiteUrl());
		doc.put("version", src.getVersion());

		return doc;
	}
}
