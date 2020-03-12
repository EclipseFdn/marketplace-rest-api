/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.persistence.model;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipsefoundation.core.model.RequestWrapper;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.eclipsefoundation.persistence.dto.BareNode;
import org.eclipsefoundation.persistence.dto.filter.DtoFilter;
import org.eclipsefoundation.persistence.helper.SortableHelper;
import org.eclipsefoundation.persistence.helper.SortableHelper.Sortable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper for initializing MongoDB BSON filters, sort clauses, and document
 * type when interacting with MongoDB. This should only be called from within
 * the scope of a request with a defined {@link ResourceDataType}
 * 
 * @author Martin Lowe
 */
public class RDBMSQuery<T extends BareNode> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RDBMSQuery.class);

	private RequestWrapper wrapper;
	private DtoFilter<T> dtoFilter;

	private ParameterizedSQLStatement filter;
	private SortOrder order;
	private int page = 1;

	public RDBMSQuery(RequestWrapper wrapper, DtoFilter<T> dtoFilter) {
		this.wrapper = wrapper;
		this.dtoFilter = dtoFilter;
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
		this.order = SortOrder.NONE;

		// get the filters for the current DTO
		this.filter = dtoFilter.getFilters(wrapper, true);

		// get fields that make up the required fields to enable pagination and check
		Optional<String> sortOpt = wrapper.getFirstParam(UrlParameterNames.SORT);
		if (sortOpt.isPresent()) {
			String sortVal = sortOpt.get();
			SortOrder ord = SortOrder.getOrderFromValue(sortOpt.get());
			// split sort string of `<fieldName> <SortOrder>`
			int idx = sortVal.indexOf(' ');
			// check if the sort string matches the RANDOM sort order
			if (SortOrder.RANDOM.equals(ord)) {
				filter.setOrder(SortOrder.RANDOM);
				this.order = SortOrder.RANDOM;
			} else if (ord != SortOrder.NONE) {
				setSort(sortVal.substring(0, idx), sortVal.substring(idx + 1));
			}
		}
		// check if the page param has been set, defaulting to the first page if not set
		Optional<String> pageOpt = wrapper.getFirstParam(UrlParameterNames.PAGE);
		int page = 1;
		if (pageOpt.isPresent() && StringUtils.isNumeric(pageOpt.get())) {
			int tmpPage = Integer.parseInt(pageOpt.get());
			if (tmpPage > 0) {
				page = tmpPage;
				LOGGER.debug("Found a set page of {} for current query", page);
			}
		}
	}

	/**
	 * Checks the URL parameter of {@link UrlParameterNames.LIMIT} for a numeric
	 * value and returns it if present.
	 * 
	 * @return the value of the URL parameter {@link UrlParameterNames.LIMIT} if
	 *         present and numeric, otherwise returns -1.
	 */
	public int getLimit() {
		Optional<String> limitVal = wrapper.getFirstParam(UrlParameterNames.LIMIT);
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
			case DESCENDING:
				this.filter.setOrder(order);
				this.filter.setSortField(sortField);
				break;
			case RANDOM:
				this.filter.setOrder(order);
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
	 * @return the page of results to retrieve for query
	 */
	public int getPage() {
		return this.page;
	}

	/**
	 * @return the filter
	 */
	public ParameterizedSQLStatement getFilter() {
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

	/**
	 * @return the wrapper
	 */
	public RequestWrapper getWrapper() {
		return wrapper;
	}

	/**
	 * @param wrapper the wrapper to set
	 */
	public void setWrapper(RequestWrapper wrapper) {
		this.wrapper = wrapper;
	}
}
