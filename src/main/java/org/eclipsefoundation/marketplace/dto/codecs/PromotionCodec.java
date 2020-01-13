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
import org.eclipsefoundation.marketplace.dto.Promotion;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;

import com.mongodb.MongoClient;

/**
 * Codec for reading and writing {@linkplain Promotion} objectss to database objects.
 * 
 * @author Martin Lowe
 *
 */
public class PromotionCodec implements CollectibleCodec<Promotion> {
	
	private final Codec<Document> documentCodec;

	/**
	 * Creates the codec and initializes the codecs and converters needed to create
	 * a listing from end to end.
	 */
	public PromotionCodec() {
		this.documentCodec = MongoClient.getDefaultCodecRegistry().get(Document.class);
	}

	@Override
	public void encode(BsonWriter writer, Promotion value, EncoderContext encoderContext) {
		Document doc = new Document();
		doc.put(DatabaseFieldNames.DOCID, value.getId());
		doc.put(DatabaseFieldNames.LISTING_ID, value.getListingId());
		documentCodec.encode(writer, doc, encoderContext);
	}

	@Override
	public Class<Promotion> getEncoderClass() {
		return Promotion.class;
	}

	@Override
	public Promotion decode(BsonReader reader, DecoderContext decoderContext) {
		Document value = documentCodec.decode(reader, decoderContext);

		Promotion out = new Promotion();
		out.setId(value.getString(DatabaseFieldNames.DOCID));
		out.setListingId(value.getString(DatabaseFieldNames.LISTING_ID));
		out.setWeight(value.getInteger(DatabaseFieldNames.PROMOTION_WEIGHTING, 1));
		return out;
	}

	@Override
	public Promotion generateIdIfAbsentFromDocument(Promotion document) {
		if (!documentHasId(document)) {
			document.setId(UUID.randomUUID().toString());
		}
		return document;
	}

	@Override
	public boolean documentHasId(Promotion document) {
		return !StringUtils.isBlank(document.getId());
	}

	@Override
	public BsonValue getDocumentId(Promotion document) {
		return new BsonString(document.getId());
	}

}
