/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.converters;

import org.bson.Document;
import org.eclipsefoundation.marketplace.dto.Organization;

/**
 * Converter implementation for the {@link Organization} object.
 * 
 * @author Martin Lowe
 */
public class OrganizationConverter implements Converter<Organization> {

	@Override
	public Organization convert(Document src) {
		Organization org = new Organization();

		org.setId(src.getString("id"));
		org.setName(src.getString("name"));

		return org;
	}

	@Override
	public Document convert(Organization src) {
		Document doc = new Document();
		doc.put("name", src.getName());
		doc.put("id", src.getId());
		return doc;
	}

}
