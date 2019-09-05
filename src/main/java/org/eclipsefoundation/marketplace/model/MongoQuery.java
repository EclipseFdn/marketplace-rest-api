/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.eclipsefoundation.marketplace.helper.SortableHelper;
import org.eclipsefoundation.marketplace.helper.SortableHelper.Sortable;
import org.eclipsefoundation.marketplace.model.filter.ListingFilter;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

import io.quarkus.mongodb.FindOptions;

/**
 * Wrapper for intializing MongoDB BSON filters, sort clauses, and document type
 * when interacting with MongoDB.
 * 
 * @author Martin Lowe
 */
public class MongoQuery<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoQuery.class);

	private Class<T> docType;
	private QueryParams qps;
	private Bson filter;
	private Bson sort;

	// flag that indicates that aggregate should be used as filter
	private boolean useAggregate = false;

	/**
	 * Intializes the query object using the document type and parameters passed.
	 * 
	 * @param docType type of object being queried
	 * @param qps     the parameters for the current request
	 */
	public MongoQuery(Class<T> docType, QueryParams qps) {
		Objects.requireNonNull(docType);
		Objects.requireNonNull(qps);

		this.docType = docType;
		this.qps = qps;

		// initializes the filters for the current query
		init();
	}

	/**
	 * Initializes the query object using the current query string parameters and
	 * type object. This can be called again to reset the parameters if needed due
	 * to updated fields.
	 */
	public void init() {
		List<Bson> filters = new ArrayList<>();
		filters.addAll(new ListingFilter().getFilters(qps));

		// get fields that make up the required fields to enable pagination and check
		Optional<String> lastOpt = qps.getFirstParam(UrlParameterNames.LAST_SEEN);
		Optional<String> sortOpt = qps.getFirstParam(UrlParameterNames.SORT);
		if (sortOpt.isPresent()) {
			String sortVal = sortOpt.get();
			// split sort string of `<fieldName> <SortOrder>`
			int idx = sortVal.indexOf(' ');
			// check if the sort string matches the RANDOM sort order
			if (SortOrder.RANDOM.equals(SortOrder.getOrderByName(sortVal))) {
				this.useAggregate = true;
			} else if (idx > 0) {
				setSort(sortVal.substring(0, idx), sortVal.substring(idx + 1), lastOpt, filters);
			}
		}
		if (!filters.isEmpty()) {
			this.filter = Filters.and(filters);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("MongoDB query initialized with filter: {}", this.filter);
		}
	}

	/**
	 * Generates a FindOptions object, which is required in the Quarkus
	 * implementation of MongoDB to set a sort filter. This set of options includes
	 * the filter generated for current parameters and the sort filter for the
	 * current query.
	 * 
	 * @return a set of options for use in filtering documents in a MongoDB Find
	 *         operation
	 */
	public FindOptions getFindOptions() {
		FindOptions fOpts = new FindOptions();
		fOpts.filter(this.filter);
		fOpts.sort(this.sort);
		return fOpts;
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
		if (filter != null) {
			out.add(Aggregates.match(filter));
		}
		out.add(Aggregates.sample(limit));

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

	private void setSort(String sortField, String sortOrder, Optional<String> lastOpt, List<Bson> filters) {
		List<Sortable<?>> fields = SortableHelper.getSortableFields(getDocType());
		Optional<Sortable<?>> fieldContainer = SortableHelper.getSortableFieldByName(fields, sortField);
		if (fieldContainer.isPresent()) {
			SortOrder so = SortOrder.getOrderByName(sortOrder);
			// add sorting query if the sortOrder matches a defined order
			switch (so) {
			case RANDOM:
				// TODO support for random, implement the following (in this order)
				// 1. Add not in clause that checks Cache for previously read objects
				// 2. Set useAggregate flag to true to signal to DAO to use aggregate selection
				// rather than traditional find

				this.useAggregate = true;
				break;
			case ASCENDING:
				// if last seen is set, add a filter to shift the results
				if (lastOpt.isPresent()) {
					filters.add(
							Filters.gte(sortField, fieldContainer.get().castValue(lastOpt.get())));
				}
				this.sort = Filters.eq(sortField, so.getOrder());
				break;
			case DESCENDING:
				// if last seen is set, add a filter to shift the results
				if (lastOpt.isPresent()) {
					filters.add(
							Filters.lte(sortField, fieldContainer.get().castValue(lastOpt.get())));
				}
				this.sort = Filters.eq(sortField, so.getOrder());
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
		return docType;
	}

	/**
	 * @param docType the docType to set
	 */
	public void setDocType(Class<T> docType) {
		this.docType = docType;
	}

	/**
	 * @return the qps
	 */
	public QueryParams getQps() {
		return qps;
	}

	/**
	 * @param qps the qps to set
	 */
	public void setQps(QueryParams qps) {
		this.qps = qps;
	}

	public boolean isAggregate() {
		return this.useAggregate;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MongoQuery<").append(docType.getSimpleName());
		sb.append(">[query=").append(filter.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry()).toJson());
		sb.append(']');
		

		return sb.toString();
	}
}
