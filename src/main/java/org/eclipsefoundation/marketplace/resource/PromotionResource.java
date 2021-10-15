package org.eclipsefoundation.marketplace.resource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipsefoundation.core.helper.ResponseHelper;
import org.eclipsefoundation.core.model.RequestWrapper;
import org.eclipsefoundation.core.namespace.DefaultUrlParameterNames;
import org.eclipsefoundation.core.service.CachingService;
import org.eclipsefoundation.marketplace.dto.Promotion;
import org.eclipsefoundation.persistence.dao.PersistenceDao;
import org.eclipsefoundation.persistence.dto.filter.DtoFilter;
import org.eclipsefoundation.persistence.model.RDBMSQuery;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource for interacting with promotions within the API.
 * 
 * @author Martin Lowe
 *
 */
@Path("/promotions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class PromotionResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(PromotionResource.class);

	@Inject
	PersistenceDao dao;
	@Inject
	DtoFilter<Promotion> dtoFilter;
	@Inject
	CachingService<List<Promotion>> cachingService;

	@Inject
	RequestWrapper params;
	@Inject
	ResponseHelper responseBuider;

	/**
	 * Endpoint for /promotions/ to retrieve all promotions from the database along
	 * with the given query string parameters.
	 * 
	 * @return response for the browser with requested data, or an error response
	 */
	@GET
	@RolesAllowed({ "marketplace_promotion_get", "marketplace_admin_access" })
	public Response select() {
		RDBMSQuery<Promotion> q = new RDBMSQuery<>(params, dtoFilter);
		// retrieve the possible cached object
		Optional<List<Promotion>> cachedResults = cachingService.get("all", params, () -> dao.get(q));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached promotions");
			return Response.serverError().build();
		}

		// return the results as a response
		return responseBuider.build("all", params, cachedResults.get());
	}

	/**
	 * Endpoint for /promotions/ to post a new promotion to the persistence layer.
	 * 
	 * @param promotion the promotion object to insert into the database.
	 * @return response for the browser with requested data, or an error response
	 */
	@PUT
	@RolesAllowed({ "marketplace_promotion_put", "marketplace_admin_access" })
	public Response putPromotion(Promotion promotion) {
		if (promotion.getId() != null) {
			params.addParam(DefaultUrlParameterNames.ID, promotion.getId().toString());
		}
		RDBMSQuery<Promotion> q = new RDBMSQuery<>(params, dtoFilter);
		// add the object, and await the result
		dao.add(q, Arrays.asList(promotion));

		// return the results as a response
		return Response.ok().build();
	}

	/**
	 * Endpoint for /promotions/\<promotionId\> to retrieve a specific promotion
	 * from the database.
	 * 
	 * @param promotionId the promotion ID
	 * @return response for the browser with requested data, or an error response
	 */
	@GET
	@RolesAllowed({ "marketplace_promotion_get", "marketplace_admin_access" })
	@Path("/{promotionId}")
	public Response select(@PathParam("promotionId") String promotionId) {
		params.addParam(DefaultUrlParameterNames.ID.getParameterName(), promotionId);

		RDBMSQuery<Promotion> q = new RDBMSQuery<>(params, dtoFilter);
		// retrieve a cached version of the value for the current listing
		Optional<List<Promotion>> cachedResults = cachingService.get(promotionId, params, () -> dao.get(q));
		if (!cachedResults.isPresent()) {
			LOGGER.error("Error while retrieving cached listing for ID {}", promotionId);
			return Response.serverError().build();
		}

		// return the results as a response
		return responseBuider.build(promotionId, params, cachedResults.get());
	}

	/**
	 * Endpoint for /promotions/\<promotionId\> to retrieve a specific promotion
	 * from the database.
	 * 
	 * @param promotionId the promotion ID
	 * @return response for the browser with requested data, or an error response
	 */
	@DELETE
	@RolesAllowed({ "marketplace_promotion_delete", "marketplace_admin_access" })
	@Path("/{promotionId}")
	public Response delete(@PathParam("promotionId") String promotionId) {
		params.addParam(DefaultUrlParameterNames.ID.getParameterName(), promotionId);
		RDBMSQuery<Promotion> q = new RDBMSQuery<>(params, dtoFilter);
		// delete the currently selected asset
		dao.delete(q);
		
		// return the results as a response
		return Response.ok().build();
	}
}
