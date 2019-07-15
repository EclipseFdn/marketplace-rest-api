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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

/**
 * Mapping class for handling the transform between the RawSolrResult class and
 * SolrInputDocuments.
 * 
 * @author Martin Lowe
 */
public class RawSolrResultMapper implements SolrBeanMapper<Map<String, Collection<Object>>> {

	@Override
	public Map<String, Collection<Object>> toBean(SolrDocument doc) {
		if (doc.getFieldValuesMap().isEmpty()) {
			return Collections.emptyMap();
		}

		// due to Solr implementation of getFieldValuesMap, the values must be iterated
		// over using keyset to pick up all fields, otherwise unsupported operation
		// exception is thrown.
		return doc.getFieldValuesMap().keySet().stream().collect(Collectors.toMap(Function.identity(), doc::getFieldValues));
	}

	@Override
	public SolrInputDocument toDocument(Map<String, Collection<Object>> doc) {
		throw new UnsupportedOperationException("Solr documents should never be directly inserted into the index");
	}
}
