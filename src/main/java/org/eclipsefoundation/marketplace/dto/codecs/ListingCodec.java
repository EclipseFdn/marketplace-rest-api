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
import org.eclipsefoundation.marketplace.dto.Listing;
import org.eclipsefoundation.marketplace.dto.converters.AuthorConverter;
import org.eclipsefoundation.marketplace.dto.converters.CategoryConverter;
import org.eclipsefoundation.marketplace.dto.converters.OrganizationConverter;
import org.eclipsefoundation.marketplace.dto.converters.ListingVersionConverter;
import org.eclipsefoundation.marketplace.dto.converters.TagConverter;
import org.eclipsefoundation.marketplace.helper.DateTimeHelper;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;

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
	private final ListingVersionConverter versionConverter;
	private final CategoryConverter categoryConverter;

	/**
	 * Creates the codec and initializes the codecs and converters needed to create
	 * a listing from end to end.
	 */
	public ListingCodec() {
		this.documentCodec = MongoClient.getDefaultCodecRegistry().get(Document.class);
		this.authorConverter = new AuthorConverter();
		this.organizationConverter = new OrganizationConverter();
		this.tagConverter = new TagConverter();
		this.versionConverter = new ListingVersionConverter();
		this.categoryConverter = new CategoryConverter();
	}

	@Override
	public void encode(BsonWriter writer, Listing value, EncoderContext encoderContext) {
		Document doc = new Document();

		// for each of the fields, get the value from the unencoded object and set it
		doc.put(DatabaseFieldNames.DOCID, value.getId());
		doc.put(DatabaseFieldNames.TITLE, value.getTitle());
		doc.put(DatabaseFieldNames.URL, value.getUrl());
		doc.put(DatabaseFieldNames.SUPPORT_PAGE_URL, value.getSupportUrl());
		doc.put(DatabaseFieldNames.HOME_PAGE_URL, value.getHomepageUrl());
		doc.put(DatabaseFieldNames.LISTING_BODY, value.getTeaser());
		doc.put(DatabaseFieldNames.LISTING_TEASER, value.getBody());
		doc.put(DatabaseFieldNames.MARKETPLACE_FAVORITES, value.getFavoriteCount());
		doc.put(DatabaseFieldNames.LICENSE_TYPE, value.getLicense());
		doc.put(DatabaseFieldNames.LISTING_STATUS, value.getStatus());
		doc.put(DatabaseFieldNames.UPDATE_DATE, DateTimeHelper.toRFC3339(value.getUpdateDate()));
		doc.put(DatabaseFieldNames.CREATION_DATE, DateTimeHelper.toRFC3339(value.getCreationDate()));
		doc.put(DatabaseFieldNames.FOUNDATION_MEMBER_FLAG, value.isFoundationMember());
		doc.put(DatabaseFieldNames.CATEGORY_IDS, value.getCategoryIds());
		doc.put(DatabaseFieldNames.SCREENSHOTS, value.getScreenshots());
		doc.put(DatabaseFieldNames.MARKET_IDS, value.getMarketIds());
		doc.put(DatabaseFieldNames.PUBLISH_STATUS, value.getPublishStatus());
		doc.put(DatabaseFieldNames.MODERATION_STATUS, value.getModerationStatus());
		
		// for nested document types, use the converters to safely transform into BSON
		// documents
		doc.put(DatabaseFieldNames.LISTING_ORGANIZATIONS, organizationConverter.convert(value.getOrganization()));
		doc.put(DatabaseFieldNames.LISTING_AUTHORS,
				value.getAuthors().stream().map(authorConverter::convert).collect(Collectors.toList()));
		doc.put(DatabaseFieldNames.LISTING_TAGS,
				value.getTags().stream().map(tagConverter::convert).collect(Collectors.toList()));
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
		out.setId(document.getString(DatabaseFieldNames.DOCID));
		out.setTitle(document.getString(DatabaseFieldNames.TITLE));
		out.setUrl(document.getString(DatabaseFieldNames.URL));
		out.setSupportUrl(document.getString(DatabaseFieldNames.SUPPORT_PAGE_URL));
		out.setHomepageUrl(document.getString(DatabaseFieldNames.HOME_PAGE_URL));
		out.setTeaser(document.getString(DatabaseFieldNames.LISTING_TEASER));
		out.setBody(document.getString(DatabaseFieldNames.LISTING_BODY));
		out.setStatus(document.getString(DatabaseFieldNames.LISTING_STATUS));
		out.setInstallsRecent(document.getInteger(DatabaseFieldNames.RECENT_INSTALLS,0));
		out.setInstallsTotal(document.getInteger(DatabaseFieldNames.TOTAL_INSTALLS,0));
		out.setLicense(document.getString(DatabaseFieldNames.LICENSE_TYPE));
		out.setFavoriteCount(document.getLong(DatabaseFieldNames.MARKETPLACE_FAVORITES));
		out.setFoundationMember(document.getBoolean(DatabaseFieldNames.FOUNDATION_MEMBER_FLAG));
		out.setCategoryIds(document.getList(DatabaseFieldNames.CATEGORY_IDS, String.class));
		out.setMarketIds(document.getList(DatabaseFieldNames.MARKET_IDS, String.class));
		out.setScreenshots(document.getList(DatabaseFieldNames.SCREENSHOTS, String.class));
		out.setPublishStatus(document.getString(DatabaseFieldNames.PUBLISH_STATUS));
		out.setModerationStatus(document.getString(DatabaseFieldNames.MODERATION_STATUS));

		// for nested document types, use the converters to safely transform into POJO
		out.setAuthors(document.getList(DatabaseFieldNames.LISTING_AUTHORS, Document.class).stream()
				.map(authorConverter::convert).collect(Collectors.toList()));
		out.setOrganization(
				organizationConverter.convert(document.get(DatabaseFieldNames.LISTING_ORGANIZATIONS, Document.class)));
		out.setTags(document.getList(DatabaseFieldNames.LISTING_TAGS, Document.class).stream()
				.map(tagConverter::convert).collect(Collectors.toList()));
		out.setVersions(document.getList(DatabaseFieldNames.LISTING_VERSIONS, Document.class).stream()
				.map(versionConverter::convert).collect(Collectors.toList()));
		out.setCategories(document.getList(DatabaseFieldNames.LISTING_CATEGORIES, Document.class).stream()
				.map(categoryConverter::convert).collect(Collectors.toList()));

		// convert date to date string
		out.setCreationDate(DateTimeHelper.toRFC3339(document.getDate(DatabaseFieldNames.CREATION_DATE)));
		out.setUpdateDate(DateTimeHelper.toRFC3339(document.getDate(DatabaseFieldNames.UPDATE_DATE)));

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
