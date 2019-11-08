/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.codecs;

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
import org.eclipsefoundation.marketplace.dto.InstallMetrics;
import org.eclipsefoundation.marketplace.dto.MetricPeriod;
import org.eclipsefoundation.marketplace.dto.converters.MetricPeriodConverter;

import com.mongodb.MongoClient;

/**
 * Codec for getting and translating {@linkplain MetricPeriod} objects. These do
 * not represent a table but a section of the {@linkplain InstallMetrics} that
 * are generated from the install table.
 * 
 * @author Martin Lowe
 *
 */
public class MetricPeriodCodec implements CollectibleCodec<MetricPeriod> {
	private final Codec<Document> documentCodec;

	private MetricPeriodConverter periodConverter;

	/**
	 * Creates the codec and initializes the codecs and converters needed to create
	 * a listing from end to end.
	 */
	public MetricPeriodCodec() {
		this.documentCodec = MongoClient.getDefaultCodecRegistry().get(Document.class);
		this.periodConverter = new MetricPeriodConverter();
	}

	@Override
	public void encode(BsonWriter writer, MetricPeriod value, EncoderContext encoderContext) {
		documentCodec.encode(writer, periodConverter.convert(value), encoderContext);
	}

	@Override
	public Class<MetricPeriod> getEncoderClass() {
		return MetricPeriod.class;
	}

	@Override
	public MetricPeriod decode(BsonReader reader, DecoderContext decoderContext) {
		return periodConverter.convert(documentCodec.decode(reader, decoderContext));
	}

	@Override
	public MetricPeriod generateIdIfAbsentFromDocument(MetricPeriod document) {
		if (!documentHasId(document)) {
			throw new IllegalArgumentException(
					"A listing ID must be set to MetricPeriod objects before writing or they are invalid");
		}
		return document;
	}

	@Override
	public boolean documentHasId(MetricPeriod document) {
		return !StringUtils.isBlank(document.getListingId());
	}

	@Override
	public BsonValue getDocumentId(MetricPeriod document) {
		return new BsonString(document.getListingId());
	}

}
