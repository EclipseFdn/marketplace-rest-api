/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.namespace;

import org.eclipsefoundation.marketplace.dto.Catalog;
import org.eclipsefoundation.marketplace.dto.Category;
import org.eclipsefoundation.marketplace.dto.ErrorReport;
import org.eclipsefoundation.marketplace.dto.Install;
import org.eclipsefoundation.marketplace.dto.InstallMetrics;
import org.eclipsefoundation.marketplace.dto.Listing;
import org.eclipsefoundation.marketplace.dto.ListingVersion;
import org.eclipsefoundation.marketplace.dto.Market;
import org.eclipsefoundation.marketplace.dto.MetricPeriod;
import org.eclipsefoundation.persistence.model.DtoTable;
import org.eclipsefoundation.marketplace.dto.Promotion;

/**
 * Mapping of DTO classes to their respective tables in the DB.
 * 
 * @author Martin Lowe
 *
 */
public enum DtoTableNames {
	LISTING(new DtoTable(Listing.class, "l")), CATEGORY(new DtoTable(Category.class, "ct")),
	CATALOG(new DtoTable(Catalog.class, "cl")), MARKET(new DtoTable(Market.class, "m")),
	ERRORREPORT(new DtoTable(ErrorReport.class, "er")), INSTALL(new DtoTable(Install.class, "i")),
	INSTALL_METRIC(new DtoTable(InstallMetrics.class, "im")), METRIC_PERIOD(new DtoTable(MetricPeriod.class, "mp")),
	LISTING_VERSION(new DtoTable(ListingVersion.class, "lv")), PROMOTION(new DtoTable(Promotion.class, "p"));

	private DtoTable table;

	private DtoTableNames(DtoTable table) {
		this.table = table;
	}

	public static DtoTableNames getTableByType(Class<?> targetBase) {
		for (DtoTableNames dtoName : values()) {
			if (dtoName.getType() == targetBase) {
				return dtoName;
			}
		}
		return null;
	}

	public DtoTable getTable() {
		return this.table;
	}

	public Class<?> getType() {
		return this.table.getType();
	}

	public String getAlias() {
		return this.table.getAlias();
	}
}
