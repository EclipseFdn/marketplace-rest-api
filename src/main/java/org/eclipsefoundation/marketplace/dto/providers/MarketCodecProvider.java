/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.providers;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.eclipsefoundation.marketplace.dto.Category;
import org.eclipsefoundation.marketplace.dto.Market;
import org.eclipsefoundation.marketplace.dto.codecs.CategoryCodec;
import org.eclipsefoundation.marketplace.dto.codecs.MarketCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the {@link CategoryCodec} to MongoDB for conversions of
 * {@link Category} objects.
 * 
 * @author Martin Lowe
 */
public class MarketCodecProvider implements CodecProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(MarketCodecProvider.class);

	@SuppressWarnings("unchecked")
	@Override
	public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
		if (clazz == Market.class) {
			LOGGER.debug("Registering custom Category class MongoDB codec");
			return (Codec<T>) new MarketCodec();
		}
		return null;
	}
}
