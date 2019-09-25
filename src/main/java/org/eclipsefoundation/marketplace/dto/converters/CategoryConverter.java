/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.converters;

import org.bson.Document;
import org.eclipsefoundation.marketplace.dto.Category;
import org.eclipsefoundation.marketplace.namespace.MongoFieldNames;

/**
 * @author martin
 *
 */
public class CategoryConverter implements Converter<Category> {

	@Override
	public Category convert(Document src) {
		Category out = new Category();
		out.setId(src.getInteger(MongoFieldNames.DOCID));
		out.setName(src.getString(MongoFieldNames.CATEGORY_NAME));
		out.setUrl(src.getString(MongoFieldNames.CATEGORY_URL));
		out.setMarketIds(src.getList(MongoFieldNames.MARKET_IDS, Integer.class));
		return out;
	}

	@Override
	public Document convert(Category src) {
		Document doc = new Document();
		doc.put(MongoFieldNames.DOCID, src.getId());
		doc.put(MongoFieldNames.CATEGORY_NAME, src.getName());
		doc.put(MongoFieldNames.CATEGORY_URL, src.getUrl());
		doc.put(MongoFieldNames.MARKET_IDS, src.getMarketIds());
		return doc;
	}

}
