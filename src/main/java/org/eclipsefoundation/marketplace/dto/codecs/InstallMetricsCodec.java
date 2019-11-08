/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.codecs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;

/**
 * Codec for getting and translating {@linkplain InstallMetrics} objects.
 * 
 * @author Martin Lowe
 *
 */
public class InstallMetricsCodec implements CollectibleCodec<InstallMetrics> {
	private static final Logger LOGGER = LoggerFactory.getLogger(InstallMetricsCodec.class);
	private final Codec<Document> documentCodec;

	private MetricPeriodConverter periodConverter;

	/**
	 * Creates the codec and initializes the codecs and converters needed to create
	 * a listing from end to end.
	 */
	public InstallMetricsCodec() {
		this.documentCodec = MongoClient.getDefaultCodecRegistry().get(Document.class);
		this.periodConverter = new MetricPeriodConverter();
	}

	@Override
	public void encode(BsonWriter writer, InstallMetrics value, EncoderContext encoderContext) {
		Document doc = new Document();

		doc.put(DatabaseFieldNames.DOCID, value.getListingId());
		doc.put(DatabaseFieldNames.PERIOD_COUNT, value.getTotal());

		// get the periods and sort them
		List<MetricPeriod> mps = value.getPeriods();
		mps.sort((o1, o2) -> o2.getEnd().compareTo(o1.getEnd()));

		LOGGER.debug("Parsing periods for {}", value.getListingId());
		// get calendar to check months
		Calendar c = Calendar.getInstance();
		int curr = 0;
		for (int i = 0; i < 12; i++) {
			// get the next period
			MetricPeriod period;
			if (curr < mps.size()) {
				period = mps.get(curr);
				LOGGER.debug("Got period: {}", period);
				// check that the retrieved period is the same month
				Calendar periodCalendar = Calendar.getInstance();
				periodCalendar.setTime(period.getEnd());
				if (periodCalendar.get(Calendar.MONTH) != c.get(Calendar.MONTH)) {
					LOGGER.debug("Regenerating period, {}:{}", periodCalendar.get(Calendar.MONTH),
							c.get(Calendar.MONTH));
					// if the month doesn't match, get a new month
					period = generatePeriod(value.getListingId(), c);
					LOGGER.debug("Regenerated period, {}", period);
				} else {
					// increment the array index pointer once its used
					curr++;
					// increment after we get a period
					c.add(Calendar.MONTH, -1);
				}
			} else {
				period = generatePeriod(value.getListingId(), c);
				LOGGER.debug("Generated period: {}", period);
			}
			// put the period into the document
			doc.put(DatabaseFieldNames.MONTH_OFFSET_PREFIX + i, periodConverter.convert(period));
		}
		documentCodec.encode(writer, doc, encoderContext);
	}

	@Override
	public InstallMetrics decode(BsonReader reader, DecoderContext decoderContext) {
		Document document = documentCodec.decode(reader, decoderContext);

		InstallMetrics out = new InstallMetrics();
		out.setListingId(document.getString(DatabaseFieldNames.DOCID));
		out.setTotal(document.getInteger(DatabaseFieldNames.PERIOD_COUNT));

		// get the base calendar for the documents
		Calendar c = getBaseCalendar(document);
		// create a list of periods
		List<MetricPeriod> periods = new ArrayList<>(12);
		for (int i = 0; i < 12; i++) {
			MetricPeriod period = periodConverter.convert(document.get(DatabaseFieldNames.MONTH_OFFSET_PREFIX + i, Document.class));
			// if there is no period, generate one. otherwise, increment c and continue
			if (period == null) {
				period = generatePeriod(out.getListingId(), c);
			} else {
				c.add(Calendar.MONTH, -1);
			}
			periods.add(period);
		}
		out.setPeriods(periods);

		return out;
	}

	@Override
	public Class<InstallMetrics> getEncoderClass() {
		return InstallMetrics.class;
	}

	@Override
	public InstallMetrics generateIdIfAbsentFromDocument(InstallMetrics document) {
		if (!documentHasId(document)) {
			throw new IllegalArgumentException(
					"A listing ID must be set to InstallMetrics objects before writing or they are invalid");
		}
		return document;
	}

	@Override
	public boolean documentHasId(InstallMetrics document) {
		return !StringUtils.isBlank(document.getListingId());
	}

	@Override
	public BsonValue getDocumentId(InstallMetrics document) {
		return new BsonString(document.getListingId());
	}

	private Calendar getBaseCalendar(Document d) {
		for (int i = 0; i < 12; i++) {
			MetricPeriod period = periodConverter.convert(d.get(DatabaseFieldNames.MONTH_OFFSET_PREFIX + i, Document.class));
			// if we have a period set, get its date
			if (period != null) {
				Calendar out = Calendar.getInstance();
				out.setTime(period.getEnd());
				// adjust the calendar to the base date
				out.add(Calendar.MONTH, i);
				return out;
			}
		}
		// fall back to now as the base time as there is no date to compare to
		return Calendar.getInstance();
	}

	private MetricPeriod generatePeriod(String listingId, Calendar c) {
		MetricPeriod period = new MetricPeriod();
		period.setListingId(listingId);
		period.setCount(0);
		period.setEnd(c.getTime());
		c.add(Calendar.MONTH, -1);
		period.setStart(c.getTime());

		return period;
	}
}
