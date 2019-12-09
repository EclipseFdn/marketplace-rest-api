/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.codecs;

import java.util.UUID;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.eclipsefoundation.marketplace.dto.Install;
import org.eclipsefoundation.marketplace.helper.JavaVersionHelper;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;

import com.mongodb.MongoClient;

/**
 * MongoDB codec for transcoding of {@link Install} and {@link Document}
 * objects. Used when writing or retrieving objects of given type from the
 * database.
 * 
 * @author Martin Lowe
 */
public class InstallCodec implements CollectibleCodec<Install> {
	private final Codec<Document> documentCodec;

	/**
	 * Creates the codec and initializes the codecs and converters needed to create
	 * an install from end to end.
	 */
	public InstallCodec() {
		this.documentCodec = MongoClient.getDefaultCodecRegistry().get(Document.class);
	}

	@Override
	public void encode(BsonWriter writer, Install value, EncoderContext encoderContext) {
		Document doc = new Document();

		doc.put(DatabaseFieldNames.DOCID, value.getId());
		doc.put(DatabaseFieldNames.INSTALL_JAVA_VERSION, JavaVersionHelper.convertToDBSafe(value.getJavaVersion()));
		doc.put(DatabaseFieldNames.INSTALL_VERSION, value.getVersion());
		doc.put(DatabaseFieldNames.INSTALL_LISTING_ID, value.getListingId());
		doc.put(DatabaseFieldNames.INSTALL_DATE, value.getInstallDate());
		doc.put(DatabaseFieldNames.ECLIPSE_VERSION, value.getEclipseVersion());
		doc.put(DatabaseFieldNames.OS, value.getOs());
		doc.put(DatabaseFieldNames.LOCALE, value.getLocale());
		documentCodec.encode(writer, doc, encoderContext);
	}

	@Override
	public Class<Install> getEncoderClass() {
		return Install.class;
	}

	@Override
	public Install decode(BsonReader reader, DecoderContext decoderContext) {
		Document document = documentCodec.decode(reader, decoderContext);
		Install out = new Install();
		out.setId(document.getString(DatabaseFieldNames.DOCID));
		out.setJavaVersion(JavaVersionHelper.convertToDisplayValue(document.getString(DatabaseFieldNames.INSTALL_JAVA_VERSION)));
		out.setVersion(document.getString(DatabaseFieldNames.INSTALL_VERSION));
		out.setListingId(document.getString(DatabaseFieldNames.INSTALL_LISTING_ID));
		out.setInstallDate(document.getDate(DatabaseFieldNames.INSTALL_DATE));
		out.setEclipseVersion(document.getString(DatabaseFieldNames.ECLIPSE_VERSION));
		out.setLocale(document.getString(DatabaseFieldNames.LOCALE));
		out.setOs(document.getString(DatabaseFieldNames.OS));
		return out;
	}

	@Override
	public Install generateIdIfAbsentFromDocument(Install document) {
		if (!documentHasId(document)) {
			document.setId(UUID.randomUUID().toString());
		}
		return document;
	}

	@Override
	public boolean documentHasId(Install document) {
		return document.getId() != null;
	}

	@Override
	public BsonValue getDocumentId(Install document) {
		return new BsonString(document.getId());
	}

}
