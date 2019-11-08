/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.converters;

import org.bson.Document;
import org.eclipsefoundation.marketplace.dto.MetricPeriod;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;

/**
 * Converter implementation for the {@link MetricPeriod} object.
 * 
 * @author Martin Lowe
 */
public class MetricPeriodConverter implements Converter<MetricPeriod> {

	@Override
	public MetricPeriod convert(Document src) {
		MetricPeriod out = new MetricPeriod();
		out.setListingId(src.getString(DatabaseFieldNames.DOCID));
		out.setStart(src.getDate(DatabaseFieldNames.PERIOD_START));
		out.setEnd(src.getDate(DatabaseFieldNames.PERIOD_END));
		out.setCount(src.getInteger(DatabaseFieldNames.PERIOD_COUNT));
		return out;
	}

	@Override
	public Document convert(MetricPeriod src) {
		Document doc = new Document();
		doc.put(DatabaseFieldNames.DOCID, src.getListingId());
		doc.put(DatabaseFieldNames.PERIOD_START, src.getStart());
		doc.put(DatabaseFieldNames.PERIOD_END, src.getEnd());
		doc.put(DatabaseFieldNames.PERIOD_COUNT, src.getCount());
		return doc;
	}

}
