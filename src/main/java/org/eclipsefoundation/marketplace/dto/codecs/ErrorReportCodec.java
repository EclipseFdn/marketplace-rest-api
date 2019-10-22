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
import org.eclipsefoundation.marketplace.dto.ErrorReport;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;

import com.mongodb.MongoClient;


/**
 * MongoDB codec for transcoding of {@link ErrorReport} and {@link Document}
 * objects. Used when writing or retrieving objects of given type from the
 * database.
 * 
 * @author Martin Lowe
 */
public class ErrorReportCodec implements CollectibleCodec<ErrorReport> {
	private final Codec<Document> documentCodec;

	public ErrorReportCodec() {
		this.documentCodec = MongoClient.getDefaultCodecRegistry().get(Document.class);
	}
	
	@Override
	public void encode(BsonWriter writer, ErrorReport value, EncoderContext encoderContext) {
		Document doc = new Document();

		doc.put(DatabaseFieldNames.DOCID, value.getId());
		doc.put(DatabaseFieldNames.TITLE, value.getTitle());
		doc.put(DatabaseFieldNames.ERROR_BODY, value.getBody());
		doc.put(DatabaseFieldNames.ERROR_DETAILED_MESSAGE, value.getDetailedMessage());
		doc.put(DatabaseFieldNames.ERROR_READ, value.isRead());
		doc.put(DatabaseFieldNames.ERROR_FEATURE_IDS, value.getFeatureIDs());
		doc.put(DatabaseFieldNames.ERROR_STATUS_CODE, value.getStatus());
		doc.put(DatabaseFieldNames.ERROR_STATUS_MESSAGE, value.getStatusMessage());
		documentCodec.encode(writer, doc, encoderContext);
	}

	@Override
	public Class<ErrorReport> getEncoderClass() {
		return ErrorReport.class;
	}

	@Override
	public ErrorReport decode(BsonReader reader, DecoderContext decoderContext) {
		Document document = documentCodec.decode(reader, decoderContext);
		ErrorReport out = new ErrorReport();
		out.setId(document.getString(DatabaseFieldNames.DOCID));
		out.setTitle(document.getString(DatabaseFieldNames.ERROR_TITLE));
		out.setBody(document.getString(DatabaseFieldNames.ERROR_BODY));
		out.setDetailedMessage(document.getString(DatabaseFieldNames.ERROR_DETAILED_MESSAGE));
		out.setStatusMessage(document.getString(DatabaseFieldNames.ERROR_STATUS_MESSAGE));
		out.setStatus(document.getString(DatabaseFieldNames.ERROR_STATUS_CODE));
		out.setFeatureIds(document.getList(DatabaseFieldNames.ERROR_FEATURE_IDS, String.class));
		out.setRead(document.getBoolean(DatabaseFieldNames.ERROR_READ));
		
		return out;
	}

	@Override
	public ErrorReport generateIdIfAbsentFromDocument(ErrorReport document) {
		if (!documentHasId(document)) {
			document.setId(UUID.randomUUID().toString());
		}
		return document;
	}

	@Override
	public boolean documentHasId(ErrorReport document) {
		return !StringUtils.isBlank(document.getId());
	}

	@Override
	public BsonValue getDocumentId(ErrorReport document) {
		return new BsonString(document.getId());
	}

}
