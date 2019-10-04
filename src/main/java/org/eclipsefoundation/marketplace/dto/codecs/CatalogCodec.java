/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.codecs;

import java.util.UUID;
import java.util.stream.Collectors;

import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.eclipsefoundation.marketplace.dto.Catalog;
import org.eclipsefoundation.marketplace.dto.converters.TabConverter;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;

import com.mongodb.MongoClient;

/**
 * MongoDB codec for transcoding of {@link Catalog} and {@link Document}
 * objects. Used when writing or retrieving objects of given type from the
 * database.
 * 
 * @author Martin Lowe
 */
public class CatalogCodec implements CollectibleCodec<Catalog> {
	private final Codec<Document> documentCodec;

	// converter objects for handling internal objects
	private final TabConverter tabConverter;

	/**
	 * Creates the codec and initializes the codecs and converters needed to create
	 * a listing from end to end.
	 */
	public CatalogCodec() {
		this.documentCodec = MongoClient.getDefaultCodecRegistry().get(Document.class);
		this.tabConverter = new TabConverter();
	}

	@Override
	public void encode(BsonWriter writer, Catalog value, EncoderContext encoderContext) {
		Document doc = new Document();

		doc.put(DatabaseFieldNames.DOCID, value.getId());
		doc.put(DatabaseFieldNames.CATALOG_TITLE, value.getTitle());
		doc.put(DatabaseFieldNames.CATALOG_URL, value.getUrl());
		doc.put(DatabaseFieldNames.CATALOG_ICON, value.getIcon());
		doc.put(DatabaseFieldNames.CATALOG_SELF_CONTAINED, value.isSelfContained());
		doc.put(DatabaseFieldNames.CATALOG_SEARCH_ENABLED, value.isSearchEnabled());
		doc.put(DatabaseFieldNames.CATALOG_DEPENDENCIES_REPOSITORY, value.getDependenciesRepository());
		doc.put(DatabaseFieldNames.CATALOG_TABS,
				value.getTabs().stream().map(tabConverter::convert).collect(Collectors.toList()));
		documentCodec.encode(writer, doc, encoderContext);
	}

	@Override
	public Class<Catalog> getEncoderClass() {
		return Catalog.class;
	}

	@Override
	public Catalog decode(BsonReader reader, DecoderContext decoderContext) {
		Document document = documentCodec.decode(reader, decoderContext);
		Catalog out = new Catalog();
		out.setId(document.getString(DatabaseFieldNames.DOCID));
		out.setUrl(document.getString(DatabaseFieldNames.CATALOG_URL));
		out.setTitle(document.getString(DatabaseFieldNames.CATALOG_TITLE));
		out.setIcon(document.getString(DatabaseFieldNames.CATALOG_ICON));
		out.setSelfContained(document.getBoolean(DatabaseFieldNames.CATALOG_SELF_CONTAINED));
		out.setSearchEnabled(document.getBoolean(DatabaseFieldNames.CATALOG_SEARCH_ENABLED));
		out.setDependenciesRepository(document.getString(DatabaseFieldNames.CATALOG_DEPENDENCIES_REPOSITORY));
		out.setTabs(document.getList(DatabaseFieldNames.CATALOG_TABS, Document.class).stream().map(tabConverter::convert)
				.collect(Collectors.toList()));
		return out;
	}

	@Override
	public Catalog generateIdIfAbsentFromDocument(Catalog document) {
		if (!documentHasId(document)) {
			document.setId(UUID.randomUUID().toString());
		}
		return document;
	}

	@Override
	public boolean documentHasId(Catalog document) {
		return document.getId() != null;
	}

	@Override
	public BsonValue getDocumentId(Catalog document) {
		return new BsonString(document.getId());
	}

}
