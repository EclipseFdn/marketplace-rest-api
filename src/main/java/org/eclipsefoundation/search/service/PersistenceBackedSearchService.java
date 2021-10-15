package org.eclipsefoundation.search.service;

import java.util.List;

import org.eclipsefoundation.core.model.RequestWrapper;
import org.eclipsefoundation.persistence.dto.BareNode;
import org.eclipsefoundation.persistence.dto.filter.DtoFilter;

public interface PersistenceBackedSearchService {

	<T extends BareNode> List<T> find(RequestWrapper wrap, DtoFilter<T> filter);
}
