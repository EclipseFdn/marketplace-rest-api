/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.apache.solr.client.solrj.SolrQuery;
import org.eclipsefoundation.marketplace.dao.mapper.ListingMapper;
import org.eclipsefoundation.marketplace.dao.mapper.SolrBeanMapper;
import org.eclipsefoundation.marketplace.dto.Listing;

/**
 * Used to pass fake data back to test classes.
 * 
 * @author Martin Lowe
 */
@Alternative()
@Priority(1)
@ApplicationScoped
public class MockSolrDao extends DefaultSolrDao {

	@Override
	public <T, T1 extends SolrBeanMapper<T>> List<T> get(SolrQuery q, T1 mapper) {
		List<T> out = new ArrayList<>(1);
		// if the mapper is a listingmapper, add an empty listing to the list and return
		// it.
		if (mapper instanceof ListingMapper) {
			out.add((T) new Listing());
			return out;
		}

		// return empty list
		return out;
	}

}
