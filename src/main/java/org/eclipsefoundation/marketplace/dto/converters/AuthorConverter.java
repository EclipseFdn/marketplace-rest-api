/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.converters;

import org.bson.Document;
import org.eclipsefoundation.marketplace.dto.Author;

/**
 * Converter implementation for the {@link Author} object.
 * 
 * @author Martin Lowe
 */
public class AuthorConverter implements Converter<Author> {

	@Override
	public Author convert(Document src) {
		Author auth = new Author();
		auth.setFullName(src.getString("full_name"));
		auth.setUsername(src.getString("username"));

		return auth;
	}

	@Override
	public Document convert(Author src) {
		Document doc = new Document();
		doc.put("full_name", src.getFullName());
		doc.put("username", src.getUsername());

		return doc;
	}
}
