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

import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipsefoundation.marketplace.namespace.DatabaseFieldNames;
import org.eclipsefoundation.persistence.dto.BareNode;
import org.eclipsefoundation.persistence.dto.NodeBase;
import org.eclipsefoundation.persistence.model.SortableField;
import org.eclipsefoundation.search.model.Indexed;
import org.eclipsefoundation.search.namespace.IndexerTextProcessingType;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * Domain object representing a marketplace listing.
 * 
 * @author Martin Lowe
 */
@Entity
@Table(
	indexes = {
		@Index(columnList="licenseType"),
		@Index(columnList="changed")
	}
)
public class Listing extends NodeBase {

	private String supportUrl;
	private String homepageUrl;
	@Lob
	@Indexed(textProcessing = IndexerTextProcessingType.AGGRESSIVE)
	private String teaser;
	@Lob
	@Indexed(textProcessing = IndexerTextProcessingType.AGGRESSIVE)
	private String body;
	private String status;
	private String logo;
	private boolean foundationMember;
	@Transient
	@SortableField(name = "installs_count")
	private Integer installsTotal;
	@Transient
	@SortableField(name = "installs_count_recent")
	private Integer installsRecent;

	@SortableField
	private long favoriteCount;
	@SortableField
	private String created;
	@SortableField
	private String changed;
	
	@JsonbProperty(DatabaseFieldNames.LICENSE_TYPE)
	private String licenseType;
	@ElementCollection
	private Set<String> screenshots;
	@ManyToMany
	@JoinColumn
	private Set<Category> categories;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn
	private Organization organization;
	@OneToMany(cascade = CascadeType.PERSIST)
	private Set<Author> authors;
	@ManyToMany(cascade = CascadeType.PERSIST)
	private Set<Tag> tags;
	@OneToMany
	@JoinColumn(name = "listingId")
	private Set<ListingVersion> versions;
	
	@JsonbTransient
	@OneToOne(mappedBy = "listing")
	@NotFound(action = NotFoundAction.IGNORE)
	private InstallMetrics metrics;
	private boolean isPromotion;

	/**
	 * Default constructor, sets lists to empty lists to stop null pointers
	 */
	public Listing() {
		this.authors = new HashSet<>();
		this.tags = new HashSet<>();
		this.versions = new HashSet<>();
		this.categories = new HashSet<>();
		this.screenshots = new HashSet<>();
	}

	@PostLoad
	private void post() {
		if (metrics != null) {
			// get total installs
			this.installsTotal = metrics.getTotal();
			
			// get recent installs
			Calendar c = Calendar.getInstance();
			int thisMonth = c.get(Calendar.MONTH);
			int thisYear = c.get(Calendar.YEAR);
			Optional<MetricPeriod> current = metrics.getPeriods().stream()
					.filter(p -> p.getStart().toInstant().get(ChronoField.MONTH_OF_YEAR) == thisMonth && p.getStart().toInstant().get(ChronoField.YEAR) == thisYear).findFirst();
			// check if we have an entry for the current month
			if (current.isPresent()) {
				this.installsRecent = current.get().getCount();
			}
		}
	}

	/**
	 * @return the supportUrl
	 */
	public String getSupportUrl() {
		return supportUrl;
	}

	/**
	 * @param supportUrl the supportUrl to set
	 */
	public void setSupportUrl(String supportUrl) {
		this.supportUrl = supportUrl;
	}

	/**
	 * @return the homepageUrl
	 */
	public String getHomepageUrl() {
		return homepageUrl;
	}

	/**
	 * @param homepageUrl the homepageUrl to set
	 */
	public void setHomepageUrl(String homepageUrl) {
		this.homepageUrl = homepageUrl;
	}

	/**
	 * @return the teaser
	 */
	public String getTeaser() {
		return teaser;
	}

	/**
	 * @param teaser the teaser to set
	 */
	public void setTeaser(String teaser) {
		this.teaser = teaser;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the logo
	 */
	public String getLogo() {
		return logo;
	}

	/**
	 * @param logo the logo to set
	 */
	public void setLogo(String logo) {
		this.logo = logo;
	}

	/**
	 * @return the foundationMember
	 */
	public boolean isFoundationMember() {
		return foundationMember;
	}

	/**
	 * @param foundationMember the foundationMember to set
	 */
	public void setFoundationMember(boolean foundationMember) {
		this.foundationMember = foundationMember;
	}

	/**
	 * @return the installsTotal
	 */
	@JsonbProperty("installs_count")
	public Integer getInstallsTotal() {
		return installsTotal;
	}

	/**
	 * @param installsTotal the installsTotal to set
	 */
	@JsonbTransient
	public void setInstallsTotal(Integer installsTotal) {
		this.installsTotal = installsTotal;
	}

	/**
	 * @return the installsRecent
	 */
	@JsonbProperty("installs_count_recent")
	public Integer getInstallsRecent() {
		return installsRecent;
	}

	/**
	 * @param installsRecent the installsRecent to set
	 */
	@JsonbTransient
	public void setInstallsRecent(Integer installsRecent) {
		this.installsRecent = installsRecent;
	}

	/**
	 * @return the favoriteCount
	 */
	public long getFavoriteCount() {
		return favoriteCount;
	}

	/**
	 * @param favoriteCount the favoriteCount to set
	 */
	public void setFavoriteCount(long favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	/**
	 * @return the created date
	 */
	public String getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(String created) {
		this.created = created;
	}

	/**
	 * @return the updateDate
	 */
	public String getChanged() {
		return changed;
	}

	/**
	 * @param changed the changed to set
	 */
	public void setChanged(String changed) {
		this.changed = changed;
	}

	/**
	 * @return the licenseType
	 */
	public String getLicenseType() {
		return licenseType;
	}

	/**
	 * @param licenseType the licenseType to set
	 */
	public void setLicenseType(String licenseType) {
		this.licenseType = licenseType;
	}

	/**
	 * @return the screenshots
	 */
	public Set<String> getScreenshots() {
		return new HashSet<>(screenshots);
	}

	/**
	 * @param screenshots the screenshots to set
	 */
	public void setScreenshots(Set<String> screenshots) {
		this.screenshots = new HashSet<>(screenshots);
	}

	/**
	 * @return the categories
	 */
	public Set<Category> getCategories() {
		return new HashSet<>(categories);
	}

	/**
	 * @param categories the categories to set
	 */
	public void setCategories(Set<Category> categories) {
		this.categories = new HashSet<>(categories);
	}

	/**
	 * @return the organization
	 */
	public Organization getOrganization() {
		return organization;
	}

	/**
	 * @param organization the organization to set
	 */
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	/**
	 * @return the authors
	 */
	public Set<Author> getAuthors() {
		return new HashSet<>(authors);
	}

	/**
	 * @param authors the authors to set
	 */
	public void setAuthors(Set<Author> authors) {
		Objects.requireNonNull(authors);
		this.authors = new HashSet<>(authors);
	}

	/**
	 * @return the tags
	 */
	public Set<Tag> getTags() {
		return new HashSet<>(tags);
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(Set<Tag> tags) {
		Objects.requireNonNull(tags);
		this.tags = new HashSet<>(tags);
	}

	/**
	 * @return the versions
	 */
	public Set<ListingVersion> getVersions() {
		return new HashSet<>(versions);
	}

	/**
	 * @param versions the versions to set
	 */
	@JsonbTransient
	public void setVersions(Set<ListingVersion> versions) {
		Objects.requireNonNull(versions);
		this.versions = new HashSet<>(versions);
	}

	/**
	 * @return the metrics
	 */
	public InstallMetrics getMetrics() {
		return metrics;
	}

	/**
	 * @param metrics the metrics to set
	 */
	public void setMetrics(InstallMetrics metrics) {
		this.metrics = metrics;
	}

	/**
	 * @return the isPromotion
	 */
	public boolean isPromotion() {
		return isPromotion;
	}

	/**
	 * @param isPromotion the isPromotion to set
	 */
	@JsonbTransient
	public void setPromotion(boolean isPromotion) {
		this.isPromotion = isPromotion;
	}

	@Override
	public boolean validate() {
		return super.validate() && licenseType != null && !authors.isEmpty() && !versions.isEmpty();
	}

	@Override
	public void initializeLazyFields() {
		super.initializeLazyFields();
		this.authors.forEach(BareNode::initializeLazyFields);
		this.categories.forEach(BareNode::initializeLazyFields);
		this.tags.forEach(BareNode::initializeLazyFields);
		this.versions.forEach(BareNode::initializeLazyFields);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(this.getAuthors(), body, this.getCategories(), created, favoriteCount, foundationMember,
				homepageUrl, installsRecent, installsTotal, licenseType, logo, organization, status, supportUrl, this.getTags(),
				teaser, changed, this.getVersions());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Listing other = (Listing) obj;
		return Objects.equals(this.getAuthors(), other.getAuthors()) && Objects.equals(body, other.body)
				&& Objects.equals(this.getCategories(), other.getCategories()) && created == other.created
				&& favoriteCount == other.favoriteCount && foundationMember == other.foundationMember
				&& Objects.equals(homepageUrl, other.homepageUrl) && installsRecent == other.installsRecent
				&& installsTotal == other.installsTotal && Objects.equals(logo, other.logo)
				&& Objects.equals(this.getOrganization(), other.getOrganization()) && Objects.equals(status, other.status)
				&& Objects.equals(supportUrl, other.supportUrl) && Objects.equals(this.getTags(), other.getTags())
				&& Objects.equals(teaser, other.teaser) && changed == other.changed
				&& Objects.equals(this.getVersions(), other.getVersions())
				&& Objects.equals(this.getScreenshots(), other.getScreenshots());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Listing [");
		sb.append(", id=").append(getId());
		sb.append(", name=").append(getTitle());
		sb.append(", url=").append(getUrl());
		sb.append(", supportUrl=").append(supportUrl);
		sb.append(", homepageUrl=").append(homepageUrl);
		sb.append(", teaser=").append(teaser);
		sb.append(", body=").append(body);
		sb.append(", status=").append(status);
		sb.append(", logo=").append(logo);
		sb.append(", foundationMember=").append(foundationMember);
		sb.append(", installsTotal=").append(installsTotal);
		sb.append(", installsRecent=").append(installsRecent);
		sb.append(", favoriteCount=").append(favoriteCount);
		sb.append(", created=").append(created);
		sb.append(", updateDate=").append(changed);
		sb.append(", license=").append(licenseType);
		sb.append(", organization=").append(organization);
		sb.append(", authors=").append(authors);
		sb.append(", tags=").append(tags);
		sb.append(", versions=").append(versions);
		sb.append(", screenshots=").append(screenshots);
		sb.append(", isPromotion=").append(isPromotion);
		sb.append(']');
		return sb.toString();
	}
}
