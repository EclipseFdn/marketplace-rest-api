/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.filter;

import java.util.List;

import org.bson.conversions.Bson;
import org.eclipsefoundation.marketplace.model.RequestWrapper;

/**
 * @author martin
 *
 */
public interface DtoFilter<T> {

	List<Bson> getFilters(RequestWrapper wrap);
	
	List<Bson> getAggregates(RequestWrapper wrap);
	
	Class<T> getType();
}
