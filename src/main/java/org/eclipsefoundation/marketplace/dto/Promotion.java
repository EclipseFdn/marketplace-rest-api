package org.eclipsefoundation.marketplace.dto;

import org.eclipsefoundation.persistence.dto.BareNode;

public class Promotion extends BareNode {

	private String listingId;
	private int weight = 1;

	/**
	 * @return the listingId
	 */
	public String getListingId() {
		return listingId;
	}

	/**
	 * @param listingId the listingId to set
	 */
	public void setListingId(String listingId) {
		this.listingId = listingId;
	}

	/**
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}

}
