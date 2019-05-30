/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.model;

/**
 * Class denoting that given child bean is a document within Solr.
 * 
 * @author martin
 *
 */
public abstract class SolrBean {
	private String docid;

	/**
	 * @return the docid
	 */
	public String getDocid() {
		return docid;
	}

	/**
	 * @param docid the docid to set
	 */
	public void setDocid(String docid) {
		this.docid = docid;
	}
}
