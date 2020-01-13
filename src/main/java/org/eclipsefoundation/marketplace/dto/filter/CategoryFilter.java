/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto.filter;

import javax.enterprise.context.ApplicationScoped;

import org.eclipsefoundation.marketplace.dto.Category;


/**
 * Filter implementation for the {@link Category} class.
 * 
 * @author Martin Lowe
 * 
 */
@ApplicationScoped
public class CategoryFilter implements DtoFilter<Category> {

	@Override
	public Class<Category> getType() {
		return Category.class;
	}

}
