/*
 * Copyright (C) 2019 Eclipse Foundation and others.
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
*/
package org.eclipsefoundation.marketplace.dto;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipsefoundation.persistence.dto.NodeBase;

/**
 * Represents a listing category in the marketplace
 * 
 * @author Martin Lowe
 *
 */
@Entity
@Table
public class Category extends NodeBase {
}
