/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.filter;

import javax.enterprise.context.ApplicationScoped;

import org.eclipsefoundation.marketplace.dto.Catalog;

/**
 * Filter implementation for the {@link Catalog} class.
 * 
 * @author Martin Lowe
 * 
 */
@ApplicationScoped
public class CatalogFilter implements DtoFilter<Catalog> {

	@Override
	public Class<Catalog> getType() {
		return Catalog.class;
	}
}
