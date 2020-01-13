package org.eclipsefoundation.marketplace.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipsefoundation.marketplace.dao.MongoDao;
import org.eclipsefoundation.marketplace.dto.Listing;
import org.eclipsefoundation.marketplace.dto.Promotion;
import org.eclipsefoundation.marketplace.dto.filter.DtoFilter;
import org.eclipsefoundation.marketplace.helper.StreamHelper;
import org.eclipsefoundation.marketplace.model.MongoQuery;
import org.eclipsefoundation.marketplace.model.RequestWrapper;
import org.eclipsefoundation.marketplace.namespace.MicroprofilePropertyNames;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.eclipsefoundation.marketplace.service.CachingService;
import org.eclipsefoundation.marketplace.service.PromotionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of promotion service. Uses weighting to allow for
 * promotions to appear more often than others. By using the property
 * {@link MicroprofilePropertyNames.PROMO_WEIGHT_DEFAULT}, weighting defaults
 * can shift outside of code builds once data is modified.
 * 
 * @author Martin Lowe
 *
 */
@ApplicationScoped
public class DefaultPromotionService implements PromotionService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPromotionService.class);

	@ConfigProperty(name = MicroprofilePropertyNames.PROMO_SERVE_COUNT, defaultValue = "2")
	int promoCount;
	@ConfigProperty(name = MicroprofilePropertyNames.PROMO_WEIGHT_DEFAULT, defaultValue = "1")
	int defaultWeight;

	@Inject
	MongoDao dao;

	@Inject
	DtoFilter<Listing> listingFilter;
	@Inject
	CachingService<List<Listing>> listingCache;

	@Inject
	DtoFilter<Promotion> promotionFilter;
	@Inject
	CachingService<List<Promotion>> promoCache;

	// random used for shuffling collections
	private Random r = new Random();

	@Override
	public List<Listing> getListingsForPromotions(RequestWrapper wrapper, List<Promotion> promos) {
		if (promos == null || promos.isEmpty()) {
			LOGGER.debug("No promotions were passed, returning empty list");
			return Collections.emptyList();
		}
		// create mapping to get a list of specific IDs, and to add context to the call
		// for caching
		Map<String, List<String>> adds = new HashMap<>();
		adds.put("type", Arrays.asList("Listing"));
		adds.put(UrlParameterNames.IDS.getParameterName(),
				promos.stream().map(Promotion::getListingId).collect(Collectors.toList()));

		MongoQuery<Listing> q = new MongoQuery<>(null, adds, listingFilter);
		// retrieve the possible cached object
		Optional<List<Listing>> cachedResults = listingCache.get("promo|listings", wrapper, adds,
				() -> StreamHelper.awaitCompletionStage(dao.get(q)));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached promotion listings");
			return Collections.emptyList();
		}

		// return the results as a response
		return cachedResults.get();
	}

	@Override
	public List<Listing> retrievePromotions(RequestWrapper wrapper, List<Listing> listings) {
		// create an empty promo query to get all promos
		MongoQuery<Promotion> q = new MongoQuery<>(null, Collections.emptyMap(), promotionFilter);
		// retrieve the possible cached object
		Optional<List<Promotion>> cachedResults = promoCache.get("all|promo", wrapper, Collections.emptyMap(),
				() -> StreamHelper.awaitCompletionStage(dao.get(q)));
		if (!cachedResults.isPresent() || cachedResults.get().isEmpty()) {
			LOGGER.debug("Could not find any promotions to inject, returning");
			return listings;
		}
		// make a copy of the array to not impact cached values
		List<Promotion> promos = new ArrayList<>(cachedResults.get());
		List<Promotion> promoHolding = new ArrayList<>(promoCount);
		LOGGER.debug("Found {} promotions, maximum number to inject {}", promos.size(), promoCount);
		// check each promotion to see if it should be injected
		Promotion curr = getWeightedPromotion(promos);
		if (curr != null) {
			promos.remove(curr);
		}
		while (curr != null && promoHolding.size() <= promoCount) {
			// create a local final field referencing the current promotion for stream ref
			final Promotion p = curr;
			LOGGER.debug("Checking promo {}", p.getListingId());
			// check if current promo matches any of the listing IDs
			if (promos.stream().noneMatch(l -> l.getId().equals(p.getListingId()))) {
				LOGGER.debug("Preparing promo with listing ID '{}' to be injected into result set",
						curr.getListingId());

				promoHolding.add(curr);
			}
			curr = getWeightedPromotion(promos);
			if (curr != null) {
				promos.remove(curr);
			}
		}

		// if we couldn't find enough promos, reinsert records
		List<Listing> out = new ArrayList<>(listings);
		if (promoHolding.isEmpty()) {
			LOGGER.debug("Could not find any promos to inject");
		} else {
			for (Listing listing : getListingsForPromotions(wrapper, promoHolding)) {
				LOGGER.debug("Injecting promo with listing ID '{}' ", listing.getId());
				listing.setPromotion(true);
				out.add(0, listing);
			}
		}
		return out;
	}

	/**
	 * Using the weighting set in the promotions (or default if not set), retrieve a
	 * random result, taking weighting into account.
	 * 
	 * @param promos list of promotions to retrieve a result from. This list will be
	 *               modified as part of this call to shuffle and pop the chosen
	 *               entry.
	 * @return the chosen weighted and randomized promotion, or null if none are
	 *         appropriate.
	 */
	private Promotion getWeightedPromotion(List<Promotion> promos) {
		// return if there are no promotions to choose from
		if (promos.isEmpty()) {
			return null;
		}
		int totalWeighting = promos.stream().mapToInt(Promotion::getWeight).sum();
		// get a random number in the range of the total weighting
		int rnd = r.nextInt(totalWeighting);
		Promotion result = null;
		for (Promotion p: promos) {
			// reduce the random number by the weight
			rnd -= p.getWeight();
			// check if we are in range of the current entry
			if (rnd <= 0) {
				result = p;
				break;
			}
		}
		return result;
	}

}
