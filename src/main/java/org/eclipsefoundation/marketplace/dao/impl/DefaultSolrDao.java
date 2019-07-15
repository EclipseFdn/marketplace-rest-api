/*
 * Copyright (C) 2019 Eclipse Foundation and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*/
package org.eclipsefoundation.marketplace.dao.impl;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipsefoundation.marketplace.dao.SolrDao;
import org.eclipsefoundation.marketplace.dao.mapper.SolrBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the Solr DAO interface. Creates a reusable client
 * object to maintain sessions and latches to a single Solr core.
 * 
 * @author Martin Lowe
 */
@ApplicationScoped
public class DefaultSolrDao implements SolrDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSolrDao.class);

	@ConfigProperty(name = "solr.url.base")
	String baseUrl;

	@ConfigProperty(name = "solr.core")
	String core;

	private HttpSolrClient client;

	/**
	 * Initializes the Solr client object with the injected properties from
	 * micro-profile. The parser is set manually as by default there is none set.
	 * This doesn't affect the data returned, only how it is serialized.
	 */
	@PostConstruct
	public void init() {
		LOGGER.debug("Creating Solr client for {}/{}", baseUrl, core);

		this.client = new HttpSolrClient.Builder(baseUrl + '/' + core).build();
		this.client.setParser(new XMLResponseParser());
	}

	@Override
	public <T, M extends SolrBeanMapper<T>> List<T> get(SolrQuery q, M mapper) {
		try {
			// query solr for documents using the below query
			QueryResponse r = client.query(q);
			List<SolrDocument> docs = r.getResults();

			// log the data to trace, to not burden the logs
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Found {} results for query {}", docs.size(), q.toQueryString());
			}

			// convert the solr documents using the bean mappers and return
			return docs.stream().map(mapper::toBean).collect(Collectors.toList());
		} catch (SolrServerException e) {
			throw new SolrException(SolrException.ErrorCode.SERVER_ERROR, "Error communicating with Solr ", e);
		} catch (IOException e) {
			throw new SolrException(SolrException.ErrorCode.SERVER_ERROR,
					"Error while streaming results for current query: " + q.toString(), e);
		}
	}

	@Override
	public <T, M extends SolrBeanMapper<T>> void add(List<T> beans, M mapper) {
		// create a list of documents to upload to Solr
		List<SolrInputDocument> solrDocuments = beans.stream().map(mapper::toDocument).filter(Objects::nonNull)
				.collect(Collectors.toList());
		try {
			// update or add the given records
			UpdateResponse response = client.add(solrDocuments);
			NamedList<Object> responseFields = response.getResponse();
			
			// if there was an error that didn't cause an exception, throw one
			if (responseFields.get("error") != null) {
				throw new SolrException(SolrException.ErrorCode.SERVER_ERROR,
						"Error encountered while adding documents: " + responseFields.get("message"));
			}

			// commit the documents to be indexed
			client.commit();
			
			// trace time taken + number of entries updated
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Operation took {}ms to add/update {} record(s)", response.getElapsedTime(), beans.size());
			}
		} catch (SolrServerException e) {
			throw new SolrException(SolrException.ErrorCode.SERVER_ERROR, "Error communicating with Solr ", e);
		} catch (IOException e) {
			throw new SolrException(SolrException.ErrorCode.SERVER_ERROR, "Error while streaming data to Solr", e);
		}
	}

	@Override
	public long count(SolrQuery q) {
		try {
			// override the row count to 0. Result counts are included as metadata, and shrinks the payload
			q.setRows(0);
			
			// query solr for documents using the below query
			QueryResponse r = client.query(q);
			// return the count of 
			return r.getResults().getNumFound();
		} catch (SolrServerException e) {
			throw new SolrException(SolrException.ErrorCode.SERVER_ERROR, "Error communicating with Solr ", e);
		} catch (IOException e) {
			throw new SolrException(SolrException.ErrorCode.SERVER_ERROR,
					"Error while streaming results for current query: " + q.toString(), e);
		}
	}

	@Override
	public int delete(SolrQuery q) {
		throw new UnsupportedOperationException("Deleting is not yet supported");
	}

	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @param baseUrl the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * @return the core
	 */
	public String getCore() {
		return core;
	}

	/**
	 * @param core the core to set
	 */
	public void setCore(String core) {
		this.core = core;
	}
}
