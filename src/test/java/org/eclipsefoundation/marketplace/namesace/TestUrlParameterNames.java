package org.eclipsefoundation.marketplace.namesace;

import org.eclipsefoundation.core.namespace.UrlParameterName;

public enum TestUrlParameterNames implements UrlParameterName {

	TEST_DATA_EXISTS("test-data-exists");
	
	private String parameterName;
	private TestUrlParameterNames(String parameterName) {
		this.parameterName = parameterName;
	}
	
	/**
	 * @return the URL parameters name
	 */
	@Override
	public String getParameterName() {
		return parameterName;
	}

}
