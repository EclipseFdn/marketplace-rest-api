/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.converters;

import org.bson.Document;
import org.eclipsefoundation.marketplace.dto.FeatureId;
import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;

/**
 * Converter implementation for the {@link FeatureId} object.
 * 
 * @author Martin Lowe
 */
public class FeatureIdConverter implements Converter<FeatureId> {

	@Override
	public FeatureId convert(Document src) {
		FeatureId featureId = new FeatureId();

		featureId.setName(src.getString(DatabaseFieldNames.FEATURE_ID));
		featureId.setInstallState(src.getString(DatabaseFieldNames.INSTALL_STATE));

		return featureId;
	}

	@Override
	public Document convert(FeatureId src) {
		Document doc = new Document();
		doc.put(DatabaseFieldNames.FEATURE_ID, src.getName());
		doc.put(DatabaseFieldNames.INSTALL_STATE, src.getInstallState());
		return doc;
	}

}
