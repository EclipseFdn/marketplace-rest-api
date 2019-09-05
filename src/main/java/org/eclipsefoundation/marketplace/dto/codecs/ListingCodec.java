/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.codecs;

import java.util.Date;
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
import org.eclipsefoundation.marketplace.dto.Listing;
import org.eclipsefoundation.marketplace.dto.converters.AuthorConverter;
import org.eclipsefoundation.marketplace.dto.converters.OrganizationConverter;
import org.eclipsefoundation.marketplace.dto.converters.SolutionVersionConverter;
import org.eclipsefoundation.marketplace.dto.converters.TagConverter;
import org.eclipsefoundation.marketplace.namespace.MongoFieldNames;

import com.mongodb.MongoClient;

/**
 * MongoDB codec for transcoding of {@link Listing} and {@link Document}
 * objects. Used when writing or retrieving objects of given type from the
 * database.
 * 
 * @author Martin Lowe
 */
public class ListingCodec implements CollectibleCodec<Listing> {
	private final Codec<Document> documentCodec;

	// converter objects for handling internal objects
	private final AuthorConverter authorConverter;
	private final OrganizationConverter organizationConverter;
	private final TagConverter tagConverter;
	private final SolutionVersionConverter versionConverter;

	/**
	 * Creates the codec and initializes the codecs and converters needed to create
	 * a listing from end to end.
	 */
	public ListingCodec() {
		this.documentCodec = MongoClient.getDefaultCodecRegistry().get(Document.class);
		this.authorConverter = new AuthorConverter();
		this.organizationConverter = new OrganizationConverter();
		this.tagConverter = new TagConverter();
		this.versionConverter = new SolutionVersionConverter();
	}

	@Override
	public void encode(BsonWriter writer, Listing value, EncoderContext encoderContext) {
		Document doc = new Document();

		// for each of the fields, get the value from the unencoded object and set it
		doc.put(MongoFieldNames.DOCID, value.getId());
		doc.put(MongoFieldNames.LISTING_ID, value.getListingId());
		doc.put(MongoFieldNames.LISTING_TITLE, value.getTitle());
		doc.put(MongoFieldNames.LISTING_URL, value.getUrl());
		doc.put(MongoFieldNames.SUPPORT_PAGE_URL, value.getSupportUrl());
		doc.put(MongoFieldNames.HOME_PAGE_URL, value.getHomepageUrl());
		doc.put(MongoFieldNames.LISTING_BODY, value.getTeaser());
		doc.put(MongoFieldNames.LISTING_TEASER, value.getBody());
		doc.put(MongoFieldNames.MARKETPLACE_FAVORITES, value.getFavoriteCount());
		doc.put(MongoFieldNames.RECENT_NSTALLS, value.getInstallsRecent());
		doc.put(MongoFieldNames.TOTAL_NSTALLS, value.getInstallsTotal());
		doc.put(MongoFieldNames.LICENSE_TYPE, value.getLicense());
		doc.put(MongoFieldNames.LISTING_STATUS, value.getStatus());
		doc.put(MongoFieldNames.UPDATE_DATE, new Date(value.getUpdateDate()));
		doc.put(MongoFieldNames.CREATION_DATE, new Date(value.getCreationDate()));
		doc.put(MongoFieldNames.FOUNDATION_MEMBER_FLAG, value.isFoundationMember());

		// for nested document types, use the converters to safely transform into BSON
		// documents
		doc.put(MongoFieldNames.LISTING_ORGANIZATIONS,
				value.getOrganizations().stream().map(organizationConverter::convert).collect(Collectors.toList()));
		doc.put(MongoFieldNames.LISTING_AUTHORS,
				value.getAuthors().stream().map(authorConverter::convert).collect(Collectors.toList()));
		doc.put(MongoFieldNames.LISTING_TAGS,
				value.getTags().stream().map(tagConverter::convert).collect(Collectors.toList()));
		doc.put(MongoFieldNames.LISTING_VERSIONS,
				value.getVersions().stream().map(versionConverter::convert).collect(Collectors.toList()));
		documentCodec.encode(writer, doc, encoderContext);
	}

	@Override
	public Class<Listing> getEncoderClass() {
		return Listing.class;
	}

	@Override
	public Listing decode(BsonReader reader, DecoderContext decoderContext) {
		Document document = documentCodec.decode(reader, decoderContext);
		Listing out = new Listing();

		// for each field, get the value from the encoded object and set it in POJO
		out.setId(document.getString(MongoFieldNames.DOCID));
		out.setListingId(document.getLong(MongoFieldNames.LISTING_ID));
		out.setTitle(document.getString(MongoFieldNames.LISTING_TITLE));
		out.setUrl(document.getString(MongoFieldNames.LISTING_URL));
		out.setSupportUrl(document.getString(MongoFieldNames.SUPPORT_PAGE_URL));
		out.setHomepageUrl(document.getString(MongoFieldNames.HOME_PAGE_URL));
		out.setTeaser(document.getString(MongoFieldNames.LISTING_TEASER));
		out.setBody(document.getString(MongoFieldNames.LISTING_BODY));
		out.setStatus(document.getString(MongoFieldNames.LISTING_STATUS));
		out.setInstallsRecent(document.getLong(MongoFieldNames.RECENT_NSTALLS));
		out.setInstallsTotal(document.getLong(MongoFieldNames.TOTAL_NSTALLS));
		out.setLicense(document.getString(MongoFieldNames.LICENSE_TYPE));
		out.setFavoriteCount(document.getLong(MongoFieldNames.MARKETPLACE_FAVORITES));
		out.setFoundationMember(document.getBoolean(MongoFieldNames.FOUNDATION_MEMBER_FLAG));

		// for nested document types, use the converters to safely transform into POJO
		out.setAuthors(document.getList(MongoFieldNames.LISTING_AUTHORS, Document.class).stream()
				.map(authorConverter::convert).collect(Collectors.toList()));
		out.setOrganizations(document.getList(MongoFieldNames.LISTING_ORGANIZATIONS, Document.class).stream()
				.map(organizationConverter::convert).collect(Collectors.toList()));
		out.setTags(document.getList(MongoFieldNames.LISTING_TAGS, Document.class).stream().map(tagConverter::convert)
				.collect(Collectors.toList()));
		out.setVersions(document.getList(MongoFieldNames.LISTING_VERSIONS, Document.class).stream()
				.map(versionConverter::convert).collect(Collectors.toList()));

		// convert date to epoch milli
		out.setCreationDate(document.getDate(MongoFieldNames.CREATION_DATE).toInstant().toEpochMilli());
		out.setUpdateDate(document.getDate(MongoFieldNames.UPDATE_DATE).toInstant().toEpochMilli());

		return out;
	}

	@Override
	public Listing generateIdIfAbsentFromDocument(Listing document) {
		if (!documentHasId(document)) {
			document.setId(UUID.randomUUID().toString());
		}
		return document;
	}

	@Override
	public boolean documentHasId(Listing document) {
		return document.getId() != null;
	}

	@Override
	public BsonValue getDocumentId(Listing document) {
		return new BsonString(document.getId());
	}
}
