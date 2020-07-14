package org.eclipsefoundation.persistence.dto;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.id.uuid.StandardRandomStrategy;
import org.hibernate.search.annotations.Field;

/**
 * Represents a bare node with just ID and title for sake of persistence.
 * 
 * @author Martin Lowe
 */
@MappedSuperclass
public abstract class BareNode {
	@Id
	@Column(unique = true, nullable = false, columnDefinition = "BINARY(16)")
	@Field
	private UUID id;
	private String title;

	/**
	 * Use auto-generated value internally, rather than generator. Currently
	 * generators are incredibly finnicky around pre-set string identifiers for
	 * whatever reason.
	 */
	public BareNode() {
		this.id = StandardRandomStrategy.INSTANCE.generateUUID(null);
	}

	/**
	 * Initializes lazy fields through access. This is used to avoid issues with JPA
	 * sessions closing before access.
	 */
	public void initializeLazyFields() {
		// intentionally empty
	}

	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BareNode other = (BareNode) obj;
		return Objects.equals(id, other.id) && Objects.equals(title, other.title);
	}

}
