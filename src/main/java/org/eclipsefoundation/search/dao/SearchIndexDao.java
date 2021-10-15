package org.eclipsefoundation.search.dao;

import java.io.Closeable;
import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.eclipsefoundation.persistence.dto.BareNode;
import org.eclipsefoundation.search.model.IndexerResponse;

/**
 * Interface for interacting with a search indexing engine. This DAO should be
 * able to write new entries, as well as retrieve, update or delete existing
 * entries.
 * 
 * @author Martin Lowe
 *
 */
public interface SearchIndexDao extends Closeable {

	/**
	 * Retrieves indexed and ranked information for the given query. This
	 * information
	 * 
	 * @param <T> the type of entity to be searched
	 * @param q   the current RDBMS query to get ranked indexed documents for
	 * @return an ordered list of bare documents
	 */
	<T extends BareNode> List<SolrDocument> get(String searchTerm, Class<T> docType);

	/**
	 * Update or create entries in the search indexer for the given entities.
	 * 
	 * @param <T>      the type of entities that will be persisted to the service.
	 * @param entities the list of new or updated database entities to be indexed.
	 * @return 
	 */
	<T extends BareNode> IndexerResponse createOrUpdate(List<T> entities, Class<T> docType);

	/**
	 * Remove the given list of entities from the search index.
	 * 
	 * @param <T>      the type of entities that will be removed from the index.
	 * @param entities the list of (potentially) existing indexed entities to remove
	 *                 from the index.
	 */
	<T extends BareNode> IndexerResponse remove(List<T> entities);
}
