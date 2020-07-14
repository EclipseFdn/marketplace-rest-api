package org.eclipsefoundation.marketplace.service;

import java.util.List;

import org.eclipsefoundation.core.model.RequestWrapper;
import org.eclipsefoundation.marketplace.dto.Listing;
import org.eclipsefoundation.marketplace.dto.Promotion;

/**
 * Interface for retrieving promotions within the application.
 * 
 * @author Martin Lowe
 *
 */
public interface PromotionService {

	/**
	 * Retrieves listings associated with the given list of promotions
	 * 
	 * @param wrapper wrapper for the current request
	 * @param promos  list of promotions to retrieve listings for
	 * @return a list of listings for the list of promos, where data could be found,
	 *         or an empty list if no corresponding listings could be found for the
	 *         passed promotions.
	 */
	List<Listing> getListingsForPromotions(RequestWrapper wrapper, List<Promotion> promos);

	/**
	 * Adds a number of promotions into the given listing set.
	 * 
	 * @param wrapper  wrapper for the current request
	 * @param listings listings to inject promotions into
	 * @return a list containing the new promotions, if any are found, along with
	 *         the original listings.
	 */
	List<Listing> retrievePromotions(RequestWrapper wrapper, List<Listing> listings);
}
