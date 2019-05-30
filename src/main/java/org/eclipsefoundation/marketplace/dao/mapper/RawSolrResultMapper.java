/*
 * Copyright (C) 2019 Eclipse Foundation and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*/
package org.eclipsefoundation.marketplace.dao.mapper;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.eclipsefoundation.marketplace.model.RawSolrResult;

/**
 * Mapping class for handling the transform between the RawSolrResult class and
 * SolrInputDocuments.
 * 
 * @author Martin Lowe
 */
public class RawSolrResultMapper implements SolrBeanMapper<RawSolrResult> {
	public static final RawSolrResultMapper INSTANCE = new RawSolrResultMapper();

	private RawSolrResultMapper() {
	}

	@Override
	public RawSolrResult toBean(SolrDocument doc) {
		RawSolrResult result = new RawSolrResult();
		if (doc.getFieldValueMap().isEmpty()) {
			return result;
		}

		// due to Solr implementation of getFieldValuesMap, the values must be iterated
		// over using keyset to pick up all fields, otherwise unsupported operation
		// exception is thrown.
		for (String key : doc.getFieldValuesMap().keySet()) {
			result.setField(key, doc.getFieldValue(key));
		}
		return result;
	}

	@Override
	public SolrInputDocument toDocument(RawSolrResult document) {
		throw new UnsupportedOperationException("Solr documents should never be directly inserted into the index");
	}
}
