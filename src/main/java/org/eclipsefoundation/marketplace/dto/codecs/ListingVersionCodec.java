/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.codecs;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.eclipsefoundation.marketplace.dto.ListingVersion;
import org.eclipsefoundation.marketplace.dto.converters.ListingVersionConverter;

import com.mongodb.MongoClient;


/**
 * MongoDB codec for transcoding of {@link ListingVersion} and {@link Document}
 * objects. Used when writing or retrieving objects of given type from the
 * database.
 * 
 * @author Martin Lowe
 */
public class ListingVersionCodec implements CollectibleCodec<ListingVersion> {
	private final Codec<Document> documentCodec;

	private ListingVersionConverter cc;

	/**
	 * Creates the codec and initializes the codecs and converters needed to create
	 * a listing from end to end.
	 */
	public ListingVersionCodec() {
		this.documentCodec = MongoClient.getDefaultCodecRegistry().get(Document.class);
		this.cc = new ListingVersionConverter();
	}
	
	@Override
	public void encode(BsonWriter writer, ListingVersion value, EncoderContext encoderContext) {
		documentCodec.encode(writer, cc.convert(value), encoderContext);
	}

	@Override
	public Class<ListingVersion> getEncoderClass() {
		return ListingVersion.class;
	}

	@Override
	public ListingVersion decode(BsonReader reader, DecoderContext decoderContext) {
		return cc.convert(documentCodec.decode(reader, decoderContext));
	}

	@Override
	public ListingVersion generateIdIfAbsentFromDocument(ListingVersion document) {
		if (!documentHasId(document)) {
			document.setId(UUID.randomUUID().toString());
		}
		return document;
	}

	@Override
	public boolean documentHasId(ListingVersion document) {
		return !StringUtils.isBlank(document.getId());
	}

	@Override
	public BsonValue getDocumentId(ListingVersion document) {
		return new BsonString(document.getId());
	}

}
