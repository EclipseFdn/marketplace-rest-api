package org.eclipsefoundation.search.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.solr.common.SolrDocument;
import org.eclipsefoundation.core.model.RequestWrapper;
import org.eclipsefoundation.core.namespace.DefaultUrlParameterNames;
import org.eclipsefoundation.persistence.dao.PersistenceDao;
import org.eclipsefoundation.persistence.dto.BareNode;
import org.eclipsefoundation.persistence.dto.filter.DtoFilter;
import org.eclipsefoundation.persistence.model.RDBMSQuery;
import org.eclipsefoundation.search.dao.SearchIndexDao;
import org.eclipsefoundation.search.service.PersistenceBackedSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Martin Lowe
 *
 */
@ApplicationScoped
public class DefaultPersistenceBackedSearchService implements PersistenceBackedSearchService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPersistenceBackedSearchService.class);

	@Inject
	SearchIndexDao searchDAO;
	@Inject
	PersistenceDao dbDAO;

	@Override
	public <T extends BareNode> List<T> find(RequestWrapper wrap, DtoFilter<T> filter) {
		// get search term
		Optional<String> searchTerm = wrap.getFirstParam(DefaultUrlParameterNames.QUERY_STRING);
		List<String> resultsOrder = null;
		if (searchTerm.isPresent()) {
			// get the ranked results from search engine. Results should have docids as id
			List<SolrDocument> rankedResults = searchDAO.get(searchTerm.get(), filter.getType());
			// if we got results, store the ranked order and set restriction to request
			if (rankedResults != null && !rankedResults.isEmpty()) {
				resultsOrder = rankedResults.stream().map(d -> (String) d.getFieldValue("id"))
						.collect(Collectors.toList());
				// restrict id results to given IDs (if supported)
				resultsOrder.forEach(id -> wrap.addParam("ids", id));
			}
		}
		// get the results from the DB dao
		List<T> results = dbDAO.get(new RDBMSQuery<T>(wrap, filter));
		// if we have an order to apply
		if (resultsOrder != null) {
			return getMarshalledList(resultsOrder, results);
		}
		// if we couldn't properly search, return native order
		return results;
	}
	
	private <T extends BareNode> List<T> getMarshalledList(List<String> resultsOrder, List<T> results){
		// create a sized array list
		List<T> marshelledResults = new ArrayList<>(Math.max(resultsOrder.size(), results.size()));
		for (String id : resultsOrder) {
			// get ID for current ordered result
			UUID docid = UUID.fromString(id);
			LOGGER.debug("Checking for result document with ID {}", docid);
			// iterate through the results and add them to the marshalled results
			for (T result : results) {
				if (docid.equals(result.getId())) {
					marshelledResults.add(result);
					LOGGER.debug("Found result document with ID {}", docid);
					break;
				}
			}
		}
		return marshelledResults;
	}

}
