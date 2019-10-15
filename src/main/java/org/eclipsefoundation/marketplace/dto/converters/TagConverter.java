/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.converters;

import org.bson.Document;
import org.eclipsefoundation.marketplace.dto.Tag;

/**
 * Converter implementation for the {@link Tag} object.
 * 
 * @author Martin Lowe
 */
public class TagConverter implements Converter<Tag> {

	@Override
	public Tag convert(Document src) {
		Tag tag = new Tag();
		tag.setId(src.getString("id"));
		tag.setName(src.getString("name"));
		return tag;
	}

	@Override
	public Document convert(Tag src) {
		Document doc = new Document();
		doc.put("name", src.getName());
		doc.put("id", src.getId());
		return doc;
	}

}
