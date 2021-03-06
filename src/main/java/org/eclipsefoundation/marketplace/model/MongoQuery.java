/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.eclipsefoundation.marketplace.dto.filter.DtoFilter;
import org.eclipsefoundation.marketplace.helper.SortableHelper;
import org.eclipsefoundation.marketplace.helper.SortableHelper.Sortable;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

/**
 * Wrapper for initializing MongoDB BSON filters, sort clauses, and document
 * type when interacting with MongoDB.
 * 
 * @author Martin Lowe
 */
public class MongoQuery<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoQuery.class);

	private QueryParameters params;
	private DtoFilter<T> dtoFilter;

	private Bson filter;
	private Bson sort;
	private SortOrder order;
	private List<Bson> aggregates;

	public MongoQuery(RequestWrapper wrapper, DtoFilter<T> dtoFilter) {
		this(wrapper, Collections.emptyMap(), dtoFilter);
	}

	public MongoQuery(RequestWrapper wrapper, Map<String, List<String>> params, DtoFilter<T> dtoFilter) {
		this.dtoFilter = dtoFilter;
		this.aggregates = new ArrayList<>();
		// allow for parameters to be either explicitly set or use wrapper params
		this.params = new QueryParameters(wrapper == null ? params : wrapper.asMap());
		init();
	}

	/**
	 * Initializes the query object using the current query string parameters and
	 * type object. This can be called again to reset the parameters if needed due
	 * to updated fields.
	 */
	private void init() {
		// clear old values if set to default
		this.filter = null;
		this.sort = null;
		this.order = SortOrder.NONE;
		this.aggregates = new ArrayList<>();

		// get the filters for the current DTO
		List<Bson> filters = new ArrayList<>();
		filters.addAll(dtoFilter.getFilters(params, null));

		// get fields that make up the required fields to enable pagination and check
		Optional<String> sortOpt = params.getFirstIfPresent(UrlParameterNames.SORT.getParameterName());
		if (sortOpt.isPresent()) {
			String sortVal = sortOpt.get();
			SortOrder ord = SortOrder.getOrderFromValue(sortOpt.get());
			// split sort string of `<fieldName> <SortOrder>`
			int idx = sortVal.indexOf(' ');
			// check if the sort string matches the RANDOM sort order
			if (SortOrder.RANDOM.equals(ord)) {
				this.order = SortOrder.RANDOM;
			} else if (ord != SortOrder.NONE) {
				setSort(sortVal.substring(0, idx), sortVal.substring(idx + 1));
			}
		}

		if (!filters.isEmpty()) {
			this.filter = Filters.and(filters);
		}
		this.aggregates = dtoFilter.getAggregates(params);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("MongoDB query initialized with filter: {}", this.filter);
		}
	}

	/**
	 * Generates a list of BSON documents representing an aggregation pipeline using
	 * random sampling to get data.
	 * 
	 * @param limit the number of documents to return
	 * @return the aggregation pipeline
	 */
	public List<Bson> getPipeline(int limit) {
		if (limit < 0) {
			throw new IllegalStateException("Aggregate pipeline document limit must be greater than 0");
		}
		List<Bson> out = new ArrayList<>();
		// add filters first
		if (filter != null) {
			out.add(Aggregates.match(filter));
		}
		// add base aggregates (joins)
		out.addAll(aggregates);
		if (sort != null) {
			out.add(Aggregates.sort(sort));
		}
		// check if the page param has been set, defaulting to the first page if not set
		int page = 1;
		Optional<String> pageOpt = params.getFirstIfPresent(UrlParameterNames.PAGE.getParameterName());
		if (pageOpt.isPresent() && StringUtils.isNumeric(pageOpt.get())) {
			int tmpPage = Integer.parseInt(pageOpt.get());
			if (tmpPage > 0) {
				page = tmpPage;
				LOGGER.debug("Found a set page of {} for current query", page);
			}
		}
		
		out.add(Aggregates.skip((page - 1) * limit));
		// add sample if we aren't sorting
		if ((sort == null || SortOrder.RANDOM.equals(order)) && dtoFilter.useLimit()) {
			out.add(Aggregates.sample(limit));
		}
		return out;
	}

	/**
	 * Checks the URL parameter of {@link UrlParameterNames.LIMIT} for a numeric
	 * value and returns it if present.
	 * 
	 * @return the value of the URL parameter {@link UrlParameterNames.LIMIT} if
	 *         present and numeric, otherwise returns -1.
	 */
	public int getLimit() {
		Optional<String> limitVal = params.getFirstIfPresent(UrlParameterNames.LIMIT.getParameterName());
		if (limitVal.isPresent() && StringUtils.isNumeric(limitVal.get())) {
			return Integer.parseInt(limitVal.get());
		}
		return -1;
	}

	private void setSort(String sortField, String sortOrder) {
		List<Sortable<?>> fields = SortableHelper.getSortableFields(getDocType());
		Optional<Sortable<?>> fieldContainer = SortableHelper.getSortableFieldByName(fields, sortField);
		if (fieldContainer.isPresent()) {
			this.order = SortOrder.getOrderByName(sortOrder);
			// add sorting query if the sortOrder matches a defined order
			switch (order) {
			case ASCENDING:
				this.sort = Filters.eq(sortField, order.getOrder());
				break;
			case DESCENDING:
				this.sort = Filters.eq(sortField, order.getOrder());
				break;
			default:
				// intentionally empty, no sort
				break;
			}
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Field with name '{}' is not marked as sortable, skipping", sortField);
		}
	}

	/**
	 * @return the filter
	 */
	public Bson getFilter() {
		return this.filter;
	}

	/**
	 * @return the DTO filter
	 */
	public DtoFilter<T> getDTOFilter() {
		return this.dtoFilter;
	}

	/**
	 * @return the docType
	 */
	public Class<T> getDocType() {
		return dtoFilter.getType();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MongoQuery<").append(getDocType().getSimpleName());
		sb.append(">[query=");
		if (filter != null) {
			sb.append(filter.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()).toJson());
		}
		sb.append(",aggregates=");
		getPipeline(1).forEach(bson -> {
			sb.append(bson.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()).toJson());
			sb.append(',');
		});

		sb.append(']');

		return sb.toString();
	}
}
