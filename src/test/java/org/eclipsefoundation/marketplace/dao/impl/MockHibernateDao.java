package org.eclipsefoundation.marketplace.dao.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.NoResultException;

import org.eclipsefoundation.marketplace.dto.Catalog;
import org.eclipsefoundation.marketplace.dto.Category;
import org.eclipsefoundation.marketplace.dto.ErrorReport;
import org.eclipsefoundation.marketplace.dto.Listing;
import org.eclipsefoundation.marketplace.dto.ListingVersion;
import org.eclipsefoundation.marketplace.dto.Market;
import org.eclipsefoundation.persistence.dto.BareNode;
import org.eclipsefoundation.persistence.model.RDBMSQuery;

import com.google.common.collect.Sets;

import io.quarkus.test.Mock;

/**
 * To keep tests separate from datastore, set up a dummy endpoint that returns
 * copies of static data.
 * 
 * @author Martin Lowe
 *
 */
@Mock
@ApplicationScoped
public class MockHibernateDao extends DefaultHibernateDao {
	private Map<Class<?>, Object> mockData;

	// allow query to be captured and exposed for test validation
	public RDBMSQuery<?> capturedQuery;
	
	@PostConstruct
	public void init() {
		this.mockData = new HashMap<>();
		
		// create the listing
		Listing l = new Listing();
		l.setId(UUID.randomUUID());
		l.setBody("");
		mockData.put(Listing.class, l);
		
		// create a listing version
		ListingVersion lv = new ListingVersion();
		lv.setId(UUID.randomUUID());
		lv.setVersion("sample");
		mockData.put(ListingVersion.class, lv);
		l.setVersions(Sets.newHashSet(lv));
		
		Market m = new Market();
		m.setId(UUID.randomUUID());
		mockData.put(Market.class, m);
		
		Category c = new Category();
		mockData.put(Category.class, c);

		Catalog cl = new Catalog();
		mockData.put(Catalog.class, cl);
		
		ErrorReport er = new ErrorReport();
		mockData.put(ErrorReport.class, er);
		
	}
	
	@Override
	public <T extends BareNode> List<T> get(RDBMSQuery<T> q) {
		capturedQuery = q;
		Optional<String> useTestData = q.getWrapper().getFirstParam("test-data-exists");
		if (useTestData.isPresent() && "false".equals(useTestData.get())) {
			return Collections.emptyList();
		}
		// if this is ever wrong, then there was bad mock data
		@SuppressWarnings("unchecked")
		T o = (T) mockData.get(q.getDocType());
		if (o != null) {
			return Arrays.asList(o);
		}
		return Collections.emptyList();
	}

	@Override
	public <T extends BareNode> void add(RDBMSQuery<T> q, List<T> documents) {
		capturedQuery = q;
		// do nothing for add events, return
		return;
	}

	@Override
	public <T extends BareNode> CompletionStage<Long> count(RDBMSQuery<T> q) {
		capturedQuery = q;
		// Complete with an empty default value
		CompletableFuture<Long> future = new CompletableFuture<Long>();
		future.complete(0L);

		return future;
	}

	@Override
	public <T extends BareNode> void delete(RDBMSQuery<T> q) {
		capturedQuery = q;
		// throw the same exception as the main would
		Optional<String> useTestData = q.getWrapper().getFirstParam("test-data-exists");
		if (useTestData.isPresent() && "false".equals(useTestData.get())) {
			throw new NoResultException("Could not find any documents with given filters");
		}
	}
}
