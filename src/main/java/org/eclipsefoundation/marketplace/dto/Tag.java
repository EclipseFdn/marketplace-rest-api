/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.dto;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipsefoundation.persistence.dto.NodeBase;

/**
 * Domain object representing a marketplace listing tag. Special object in that
 * its
 * 
 * @author Martin Lowe
 */
@Entity
@Table
public class Tag extends NodeBase {

}
