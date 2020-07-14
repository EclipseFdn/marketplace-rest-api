/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.persistence.dto;

import java.util.Objects;

import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.StringUtils;

/**
 * Contains the basic fields for a node within Mongo
 * 
 * @author Martin Lowe
 */
@MappedSuperclass
public abstract class NodeBase extends BareNode {
	private String url;

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Call to check whether the current node is valid.
	 * 
	 * @return whether the current node is valid.
	 */
	public boolean validate() {
		return StringUtils.isAnyEmpty(url);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), url);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		NodeBase other = (NodeBase) obj;
		return super.equals(obj) && Objects.equals(url, other.url);
	}
}
