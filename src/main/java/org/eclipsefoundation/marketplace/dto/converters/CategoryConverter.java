/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.converters;

import org.bson.Document;
import org.eclipsefoundation.marketplace.dto.Category;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;

/**
 * Converter implementation for the {@link Category} object.
 * 
 * @author Martin Lowe
 */
public class CategoryConverter implements Converter<Category> {

	@Override
	public Category convert(Document src) {
		Category out = new Category();
		out.setId(src.getString(DatabaseFieldNames.DOCID));
		out.setName(src.getString(DatabaseFieldNames.NAME));
		out.setUrl(src.getString(DatabaseFieldNames.URL));
		return out;
	}

	@Override
	public Document convert(Category src) {
		Document doc = new Document();
		doc.put(DatabaseFieldNames.DOCID, src.getId());
		doc.put(DatabaseFieldNames.NAME, src.getName());
		doc.put(DatabaseFieldNames.URL, src.getUrl());
		return doc;
	}

}
