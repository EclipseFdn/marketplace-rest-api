/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.converters;

import org.bson.Document;

/**
 * Converters are used for POJOs nested in other POJOs to be written to the
 * MongoDB. In current state, nested document types aren't able to detect custom
 * codecs defined in registries.
 * 
 * @author Martin Lowe
 */
public interface Converter<T> {

	/**
	 * Converts a BSON document into a POJO.
	 * 
	 * @param src BSON document to convert
	 * @return converted POJO object
	 */
	T convert(Document src);

	/**
	 * Converts a POJO into a BSON document.
	 * 
	 * @param src object to conert
	 * @return converted BSON document
	 */
	Document convert(T src);
}
