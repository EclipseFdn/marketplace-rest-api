package org.eclipsefoundation.search.dao.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipsefoundation.persistence.dto.BareNode;
import org.eclipsefoundation.search.dao.SearchIndexDao;
import org.eclipsefoundation.search.model.IndexerResponse;
import org.eclipsefoundation.search.model.SolrDocumentConverter;
import org.eclipsefoundation.search.namespace.IndexerResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class SolrIndexDAO implements SearchIndexDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(SolrIndexDAO.class);

	// DAO settings
	@ConfigProperty(name = "eclipse.solr.enabled", defaultValue = "false")
	boolean solrEnabled;
	@ConfigProperty(name = "eclipse.solr.maintenance", defaultValue = "false")
	boolean solrMaintenance;

	// Solr settings
	@ConfigProperty(name = "eclipse.solr.host", defaultValue = "")
	String solrURL;
	@ConfigProperty(name = "eclipse.solr.core", defaultValue = "")
	String core;
	@ConfigProperty(name = "eclipse.solr.timeout", defaultValue = "10000")
	int solrTimeout;
	@ConfigProperty(name = "eclipse.solr.queue", defaultValue = "100")
	int queueSize;
	@ConfigProperty(name = "eclipse.solr.threads", defaultValue = "25")
	int threadCount;

	// internal state members
	private ConcurrentUpdateSolrClient solrServer;
	private Map<Class, SolrDocumentConverter> converters;

	@PostConstruct
	public void init() {
		if (!solrEnabled) {
			LOGGER.info("Eclipse Solr server not started, it is not currently enabled");
		} else {
			// create solr server
			ConcurrentUpdateSolrClient.Builder b = new ConcurrentUpdateSolrClient.Builder(solrURL + '/' + core)
					.withConnectionTimeout(solrTimeout).withQueueSize(queueSize).withThreadCount(threadCount);
			this.solrServer = b.build();
			this.converters = new HashMap<>();
			LOGGER.debug("Started Solr server for index processing");
		}
	}

	@Override
	public void close() {
		// shut down threads to solr server
		this.solrServer.close();
		LOGGER.error("Closed Solr server for index processing");
	}

	@Override
	public <T extends BareNode> List<SolrDocument> get(String searchTerm, Class<T> docType) {
		// check whether call should proceed
		if (!stateCheck() || StringUtils.isBlank(searchTerm)) {
			return Collections.emptyList();
		}
		
		// get the current doctype converter
		SolrDocumentConverter<T> converter = getConverter(docType);
		SolrParams query = converter.getBaseQuery(searchTerm);
		try {
			QueryResponse response = solrServer.query(query);
			return response.getResults();
		} catch (SolrServerException | IOException e) {
			LOGGER.error("Error while retrieving search results",e);
			return null;
		}
	}

	@Override
	public <T extends BareNode> IndexerResponse createOrUpdate(List<T> entities, Class<T> docType) {
		// check whether call should proceed
		if (!stateCheck()) {
			return IndexerResponse.getMaintenanceResponse();
		}

		// get the current doctype converter
		SolrDocumentConverter<T> converter = getConverter(docType);

		// convert the documents
		List<SolrInputDocument> docs = entities.stream().map(converter::convert).filter(Objects::nonNull)
				.collect(Collectors.toList());
		// attempt to update + commit the changes
		long now = System.currentTimeMillis();
		if (docs.isEmpty()) {
			LOGGER.debug("No documents to be indexed for current call (recieved {} entities)", docs.size());

			// TODO: should return some empty response base line rather than maint
			return IndexerResponse.getMaintenanceResponse();
		}
		try {
			// attempting to add documents to solr core
			solrServer.add(docs);
			return generateResponse(solrServer.commit(false, false, true),
					"Success! Indexed " + entities.size() + " documents",
					"Non-fatal error encountered while indexing documents");
		} catch (SolrServerException | IOException e) {
			LOGGER.error("Error while adding indexed documents to Solr server", e);
			return new IndexerResponse("Error while creating/updating index documents", IndexerResponseStatus.FAILED,
					System.currentTimeMillis() - now, e);
		}
	}

	@Override
	public <T extends BareNode> IndexerResponse remove(List<T> entities) {
		// check whether call should proceed
		if (!stateCheck()) {
			return IndexerResponse.getMaintenanceResponse();
		}

		if (entities == null || entities.isEmpty()) {
			return new IndexerResponse();
		}
		// retrieve the list of IDs of indexed documents to delete
		List<String> ids = entities.stream().map(BareNode::getId).map(UUID::toString).collect(Collectors.toList());

		// attempt to update + commit the changes
		long now = System.currentTimeMillis();
		try {
			// run the update to remove responses
			solrServer.deleteById(ids);
			return generateResponse(solrServer.commit(false, false, true),
					"Success! Removed " + entities.size() + " documents",
					"Non-fatal error encountered while removing documents");
		} catch (SolrServerException | IOException e) {
			LOGGER.error("Error while removing indexed documents from Solr server", e);
			return new IndexerResponse("Error while removing indexed documents from Solr server",
					IndexerResponseStatus.FAILED, System.currentTimeMillis() - now, e);
		}
	}

	@SuppressWarnings("unchecked")
	private <T extends BareNode> SolrDocumentConverter<T> getConverter(Class<T> docType) {
		return converters.computeIfAbsent(docType, SolrDocumentConverter::new);
	}

	/**
	 * Generate a generic indexer response object based on the Solr update response
	 * document and a set of messages that will be set into the response depending
	 * on operation success or failure..
	 * 
	 * @param solrResponse   the response object from Solr on commit
	 * @param successMessage the message to include for successful calls
	 * @param failureMessage the message to include for failed calls
	 * @return a populated generic indexer response object
	 */
	private IndexerResponse generateResponse(UpdateResponse solrResponse, String successMessage,
			String failureMessage) {
		// create output response
		IndexerResponse r = new IndexerResponse();
		r.setElapsedTimeMS(solrResponse.getElapsedTime());

		// check for an error, and update the response
		if (solrResponse.getException() != null) {
			r.setException(solrResponse.getException());
			r.setStatus(IndexerResponseStatus.FAILED);
			r.setMessage(failureMessage);
		} else {
			r.setStatus(IndexerResponseStatus.SUCCESSFUL);
			r.setMessage(successMessage);
		}
		return r;
	}

	private boolean stateCheck() {
		// check state to ensure call should run
		if (!solrEnabled) {
			return false;
		} else if (solrMaintenance) {
			LOGGER.warn("Solr DAO set to maintenance, not indexing content");
			return false;
		}
		return true;
	}
}
