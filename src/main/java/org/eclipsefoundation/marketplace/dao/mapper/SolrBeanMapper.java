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

/**
 * Interface for transformation vector of domain beans and Solr documents.
 * 
 * @author Martin Lowe
 *
 * @param <T> the type of domain bean this mapper supports
 */
public interface SolrBeanMapper<T> {

	/**
	 * 
	 * @param doc
	 * @return
	 */
	T toBean(SolrDocument doc);

	/**
	 * Transforms the bean of type T into a SolrInputDocument, which is the form
	 * that Solr accepts for adding documents back to its index.
	 * 
	 * @param bean the domain bean to transform into an input document.
	 * @return an input document for Solr consumption
	 */
	SolrInputDocument toDocument(T bean);

}
