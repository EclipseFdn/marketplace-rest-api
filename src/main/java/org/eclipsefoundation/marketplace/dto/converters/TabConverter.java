/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.converters;

import org.bson.Document;
import org.eclipsefoundation.marketplace.dto.Tab;

/**
 * Converter implementation for the {@link Tab} object.
 * 
 * @author Martin Lowe
 */
public class TabConverter implements Converter<Tab> {

	@Override
	public Tab convert(Document src) {
		Tab org = new Tab();

		org.setTitle(src.getString("title"));
		org.setType(src.getString("type"));
		org.setUrl(src.getString("url"));

		return org;
	}

	@Override
	public Document convert(Tab src) {
		Document doc = new Document();
		doc.put("title", src.getTitle());
		doc.put("type", src.getType());
		doc.put("url", src.getUrl());
		return doc;
	}

}
