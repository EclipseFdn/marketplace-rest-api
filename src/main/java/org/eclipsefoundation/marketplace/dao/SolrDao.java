/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dao;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.eclipsefoundation.marketplace.dao.mapper.SolrBeanMapper;

/**
 * Interface for DAO objects that interact with Solr instances.
 * 
 * @author Martin Lowe
 */
public interface SolrDao {

	/**
	 * Retrieves data from the current Solr instance using a SolrQuery object.
	 * 
	 * @param <T>    the type of data that should be generated from the Solr results
	 * @param <T1>   a mapper object that generates objects of type {@link T}
	 * @param q      the query object
	 * @param mapper an instance of the mapper that will be used to generate results
	 *               of type {@link T}
	 * @return a list of objects that map 1:1 for results of the Solr query.
	 */
	<T, T1 extends SolrBeanMapper<T>> List<T> get(SolrQuery q, T1 mapper);

	/**
	 * Persists documents to the Solr instance, using the mapper to convert
	 * documents back into a form consumable by Solr.
	 * 
	 * @param <T>       the type of data that should be persisted to Solr
	 * @param <T1>      a mapper object that generates objects of type {@link T}
	 * @param documents the objects to be persisted to Solr
	 * @param mapper    an instance of the mapper that will be used to generate
	 *                  results of type {@link T}
	 */
	<T, T1 extends SolrBeanMapper<T>> void add(List<T> documents, T1 mapper);

	/**
	 * Removes documents from Solr by query, removing any documents that match the
	 * below query.
	 * 
	 * @param q the query identifying documents to be deleted from Solr
	 * @return the number of documents affected by the deletion request
	 */
	int delete(SolrQuery q);
}
