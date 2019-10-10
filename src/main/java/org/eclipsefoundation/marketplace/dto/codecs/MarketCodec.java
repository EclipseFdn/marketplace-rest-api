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
import org.eclipsefoundation.marketplace.dto.Market;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;

import com.mongodb.MongoClient;

/**
 * MongoDB codec for transcoding of {@link Market} and {@link Document}
 * objects. Used when writing or retrieving objects of given type from the
 * database.
 * 
 * @author Martin Lowe
 */
public class MarketCodec implements CollectibleCodec<Market> {
	private final Codec<Document> documentCodec;

	/**
	 * Creates the codec and initializes the codecs and converters needed to create
	 * a listing from end to end.
	 */
	public MarketCodec() {
		this.documentCodec = MongoClient.getDefaultCodecRegistry().get(Document.class);
	}
	
	@Override
	public void encode(BsonWriter writer, Market value, EncoderContext encoderContext) {
		Document doc = new Document();

		doc.put(DatabaseFieldNames.DOCID, value.getId());
		doc.put(DatabaseFieldNames.URL, value.getUrl());
		doc.put(DatabaseFieldNames.NAME, value.getName());
		doc.put(DatabaseFieldNames.CATEGORY_IDS, value.getCategoryIds());

		documentCodec.encode(writer, doc, encoderContext);
	}

	@Override
	public Class<Market> getEncoderClass() {
		return Market.class;
	}

	@Override
	public Market decode(BsonReader reader, DecoderContext decoderContext) {
		Document document = documentCodec.decode(reader, decoderContext);
		Market out = new Market();
		out.setId(document.getString(DatabaseFieldNames.DOCID));
		out.setUrl(document.getString(DatabaseFieldNames.URL));
		out.setName(document.getString(DatabaseFieldNames.NAME));
		
		return out;
	}

	@Override
	public Market generateIdIfAbsentFromDocument(Market document) {
		if (!documentHasId(document)) {
			document.setId(UUID.randomUUID().toString());
		}
		return document;
	}

	@Override
	public boolean documentHasId(Market document) {
		return StringUtils.isNotBlank(document.getId());
	}

	@Override
	public BsonValue getDocumentId(Market document) {
		return new BsonString(document.getId());
	}

}
