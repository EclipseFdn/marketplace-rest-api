/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.codecs;

import org.bson.BsonInt32;
import org.bson.BsonReader;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.eclipsefoundation.marketplace.dto.Category;
import org.eclipsefoundation.marketplace.dto.converters.CategoryConverter;

import com.mongodb.MongoClient;

/**
 * @author martin
 *
 */
public class CategoryCodec implements CollectibleCodec<Category> {
	private final Codec<Document> documentCodec;

	private CategoryConverter cc;

	/**
	 * Creates the codec and initializes the codecs and converters needed to create
	 * a listing from end to end.
	 */
	public CategoryCodec() {
		this.documentCodec = MongoClient.getDefaultCodecRegistry().get(Document.class);
		this.cc = new CategoryConverter();
	}
	
	@Override
	public void encode(BsonWriter writer, Category value, EncoderContext encoderContext) {
		documentCodec.encode(writer, cc.convert(value), encoderContext);
	}

	@Override
	public Class<Category> getEncoderClass() {
		return Category.class;
	}

	@Override
	public Category decode(BsonReader reader, DecoderContext decoderContext) {
		return cc.convert(documentCodec.decode(reader, decoderContext));
	}

	@Override
	public Category generateIdIfAbsentFromDocument(Category document) {
		// TODO Auto-generated method stub
		return document;
	}

	@Override
	public boolean documentHasId(Category document) {
		return document.getId() > 0;
	}

	@Override
	public BsonValue getDocumentId(Category document) {
		return new BsonInt32(document.getId());
	}

}
