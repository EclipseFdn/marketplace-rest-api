package org.eclipsefoundation.marketplace.request;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;

import org.eclipsefoundation.core.request.CacheBypassFilter.BypassCondition;
import org.eclipsefoundation.marketplace.namespace.UrlParameterNames;
import org.eclipsefoundation.persistence.model.SortOrder;

/**
 * Designates that the request should bypass caching when sorting is set to random
 * 
 * @author Martin Lowe
 */
@ApplicationScoped
public class SortBypassCondition implements BypassCondition {

	@Override
	public boolean matches(ContainerRequestContext requestContext, HttpServletRequest request) {
		String[] sortVals = request.getParameterValues(UrlParameterNames.SORT.getParameterName());
		if (sortVals != null) {
			for (String sortVal : sortVals) {
				// check if the sort order for request matches RANDOM
				if (SortOrder.RANDOM.equals(SortOrder.getOrderFromValue(sortVal))) {
					return true;
				}
			}
		}
		return false;
	}

}
