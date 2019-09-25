/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.eclipsefoundation.marketplace.dto.filter.DtoFilter;
import org.eclipsefoundation.marketplace.helper.SortableHelper;
import org.eclipsefoundation.marketplace.helper.SortableHelper.Sortable;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.eclipsefoundation.marketplace.resource.AnnotationClassInjectionFilter;
import org.eclipsefoundation.marketplace.service.CachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

/**
 * Wrapper for initializing MongoDB BSON filters, sort clauses, and document
 * type when interacting with MongoDB. This should only be called from within
 * the scope of a request with a defined {@link ResourceDataType}
 * 
 * @author Martin Lowe
 */
public class MongoQuery<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoQuery.class);

	private CachingService<List<T>> cache;
	private RequestWrapper qps;
	private DtoFilter<T> dtoFilter;

	private Bson filter;
	private Bson sort;
	private SortOrder order;
	private List<Bson> aggregates;

	public MongoQuery(RequestWrapper qps, DtoFilter<T> dtoFilter, CachingService<List<T>> cache) {
		this.qps = qps;
		this.dtoFilter = dtoFilter;
		this.cache = cache;
		this.aggregates = new ArrayList<>();

		init();
	}

	/**
	 * Initializes the query object using the current query string parameters and
	 * type object. This can be called again to reset the parameters if needed due
	 * to updated fields.
	 */
	public void init() {
		// clear old values if set to default
		this.filter = null;
		this.sort = null;
		this.order= SortOrder.NONE;
		this.aggregates = new ArrayList<>();

		// get the filters for the current DTO
		List<Bson> filters = new ArrayList<>();
		filters.addAll(dtoFilter.getFilters(qps));
		
		// get fields that make up the required fields to enable pagination and check
		Optional<String> sortOpt = qps.getFirstParam(UrlParameterNames.SORT);
		if (sortOpt.isPresent()) {
			String sortVal = sortOpt.get();
			// split sort string of `<fieldName> <SortOrder>`
			int idx = sortVal.indexOf(' ');
			// check if the sort string matches the RANDOM sort order
			if (SortOrder.RANDOM.equals(SortOrder.getOrderByName(sortVal))) {
				this.order = SortOrder.RANDOM;
			} else if (idx > 0) {
				setSort(sortVal.substring(0, idx), sortVal.substring(idx + 1), filters);
			}
		}
		LOGGER.error("{}", filters);
		if (!filters.isEmpty()) {
			this.filter = Filters.and(filters);
		}
		this.aggregates = dtoFilter.getAggregates(qps);
		
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
		// add sample if we aren't sorting
		if (sort == null || SortOrder.RANDOM.equals(order)) {
			out.add(Aggregates.sample(limit));
		}
		if (sort != null) {
			out.add(Aggregates.sort(sort));
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
		Optional<String> limitVal = qps.getFirstParam(UrlParameterNames.LIMIT);
		if (limitVal.isPresent() && StringUtils.isNumeric(limitVal.get())) {
			return Integer.parseInt(limitVal.get());
		}
		return -1;
	}

	private void setSort(String sortField, String sortOrder, List<Bson> filters) {
		Optional<String> lastOpt = qps.getFirstParam(UrlParameterNames.LAST_SEEN);
		
		List<Sortable<?>> fields = SortableHelper.getSortableFields(getDocType());
		Optional<Sortable<?>> fieldContainer = SortableHelper.getSortableFieldByName(fields, sortField);

		LOGGER.error("{}:{}", sortField, sortOrder);
		if (fieldContainer.isPresent()) {
			this.order = SortOrder.getOrderByName(sortOrder);
			LOGGER.error("{}", order);
			// add sorting query if the sortOrder matches a defined order
			switch (order) {
			case RANDOM:
				// TODO support for random, implement the following (in this order)
				// 1. Add not in clause that checks Cache for previously read objects
				// 2. Set useAggregate flag to true to signal to DAO to use aggregate selection
				// rather than traditional find

				break;
			case ASCENDING:
				// if last seen is set, add a filter to shift the results
				if (lastOpt.isPresent()) {
					filters.add(Filters.gte(sortField, fieldContainer.get().castValue(lastOpt.get())));
				}
				this.sort = Filters.eq(sortField, order.getOrder());
				break;
			case DESCENDING:
				// if last seen is set, add a filter to shift the results
				if (lastOpt.isPresent()) {
					filters.add(Filters.lte(sortField, fieldContainer.get().castValue(lastOpt.get())));
				}
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
	 * @return the docType
	 */
	public Class<T> getDocType() {
		return (Class<T>) qps.getAttribute(AnnotationClassInjectionFilter.ATTRIBUTE_NAME);
	}

	/**
	 * @return the qps
	 */
	public RequestWrapper getQps() {
		return qps;
	}

	/**
	 * @param qps the qps to set
	 */
	public void setQps(RequestWrapper qps) {
		this.qps = qps;
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
