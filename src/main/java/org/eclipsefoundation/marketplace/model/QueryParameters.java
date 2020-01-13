package org.eclipsefoundation.marketplace.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Container for query parameters, using a map allowing for multiple values set
 * to a single key.
 * 
 * @author Martin Lowe
 *
 */
public class QueryParameters {
	private final Map<String, List<String>> parameters;

	/**
	 * Generates an empty internal parameter map
	 */
	public QueryParameters() {
		this(Collections.emptyMap());
	}

	/**
	 * Creates a copy of the passed parameter map.
	 * 
	 * @param parameters map of parameter keys and values to use.
	 */
	public QueryParameters(Map<String, List<String>> parameters) {
		this.parameters = new HashMap<>(parameters);
	}

	/**
	 * Returns a copy of the values available for the given key.
	 * 
	 * @param key string key to retrieve values for
	 * @return list of values if set, or an empty list.
	 */
	public List<String> getValues(String key) {
		return parameters.getOrDefault(key, Collections.emptyList());
	}

	/**
	 * Helper for map to retrieve first value available if one has been set for
	 * params.
	 * 
	 * @param key    key to retrieve first value for
	 * @param params parameter map for query to retrieve value from
	 * @return value wrapped in optional if present, otherwise empty optional.
	 */
	public Optional<String> getFirstIfPresent(String key) {
		List<String> vals = parameters.get(key);
		if (vals != null && !vals.isEmpty()) {
			return Optional.ofNullable(vals.get(0));
		}
		return Optional.empty();
	}

	public void add(String key, String value) {
		this.parameters.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
	}

	public void remove(String key) {
		this.parameters.remove(key);
	}

	public Map<String, List<String>> asMap() {
		return new HashMap<>(parameters);
	}
}
