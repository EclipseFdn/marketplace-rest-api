package org.eclipsefoundation.marketplace.request;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;

import org.apache.commons.lang3.StringUtils;
import org.eclipsefoundation.core.namespace.DefaultUrlParameterNames;
import org.eclipsefoundation.core.request.CacheBypassFilter.BypassCondition;

/**
 * Designates that the request should bypass caching when query string is set.
 * 
 * @author Martin Lowe
 */
@ApplicationScoped
public class SearchBypassCondition implements BypassCondition {

	@Override
	public boolean matches(ContainerRequestContext requestContext, HttpServletRequest request) {
		String[] queryStringVals = request.getParameterValues(DefaultUrlParameterNames.QUERY_STRING.getParameterName());
		return queryStringVals != null && !StringUtils.isAllBlank(queryStringVals);
	}

}
