/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.persistence.helper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.eclipsefoundation.persistence.model.SortableField;
import org.hibernate.boot.model.naming.Identifier;

/**
 * Reflection based helper that reads in a type and reads annotations present on
 * class, drilling down into child types to generate paths to nested types for
 * usage in document queries. Currently max depth is 2.
 * 
 * @author Martin Lowe
 */
public class SortableHelper {
	private static final int MAX_DEPTH = 2;

	// set up the internal conversion functions
	private static final Map<Class<?>, Function<String, ?>> CONVERSION_FUNCTIONS = new HashMap<>();
	static {
		CONVERSION_FUNCTIONS.put(long.class, Long::valueOf);
		CONVERSION_FUNCTIONS.put(int.class, Integer::valueOf);
		CONVERSION_FUNCTIONS.put(String.class, s -> s);
	}

	/**
	 * <p>
	 * Recursively parse target classes to a given depth. Each parsed class will
	 * have its Fields reflectively read, checking for SortableField annotations
	 * marking it as a field to be sortable in MongoDB.
	 * </p>
	 * <p>
	 * Each field will then, if max depth hasn't been reached, checked for further
	 * annotations on fields. The parent Fields annotation values will be passed to
	 * the recursive call to create compound path keys for queries.
	 * </p>
	 * 
	 * @param tgt the class to reflectively read the fields of
	 * @return list of annotated field properties
	 */
	public static List<Sortable<?>> getSortableFields(Class<?> tgt) {
		Objects.requireNonNull(tgt);
		
		return parseClass(0, tgt, null, new LinkedList<>());
	}

	/**
	 * Using a list of Sortable field values, streams and retrieves the first
	 * Sortable that matches the passed field name if it exists as an optional
	 * value.
	 * 
	 * @param fields    the Sortable fields for the current operation
	 * @param fieldName the field name to retrieve a Sortable for
	 * @return the sortable object if it exists as an Optional value.
	 */
	public static Optional<Sortable<?>> getSortableFieldByName(List<Sortable<?>> fields, String fieldName) {
		Objects.requireNonNull(fields);
		Objects.requireNonNull(fieldName);
		
		return fields.stream().filter(c -> c.getName().equals(fieldName)).findFirst();
	}

	private static List<Sortable<?>> parseClass(int depth, Class<?> tgt, Sortable<?> parent, List<Sortable<?>> coll) {
		for (Field f : tgt.getDeclaredFields()) {
			// create new container for field
			Sortable<?> c = new Sortable<>(f.getType());
			c.name = Identifier.toIdentifier(f.getName()).getText();
			c.path = c.name;

			// if annotation exists, get values from it
			SortableField sf = f.getAnnotation(SortableField.class);
			// if not sortable, still generate in case children fields require parent data
			if (sf == null) {
				// if parent exists, concat the paths
				if (parent != null) {
					c.path = parent.path + '.' + c.path;
				}
			} else {
				// if field name isn't empty, update the default name + path name
				if (!"".equals(sf.name())) {
					c.name = sf.name();
					c.path = c.name;
				}
				// if parent exists, concat the paths
				if (parent != null) {
					c.path = parent.path + '.' + c.path;
				}
				if (!"".equals(sf.path())) {
					c.path = sf.path();
				}
				// add collection to list as it is sortable
				coll.add(c);
			}

			// recurse if we haven't reached max depth
			if (depth < MAX_DEPTH) {
				parseClass(depth + 1, c.getType(), c, coll);
			}
		}
		return coll;
	}

	// blank as this is a static helper and should not be instantiated
	private SortableHelper() {
	}

	/**
	 * Container for sortable field data. This should only be created by the
	 * SortableHelper class on importing field annotation values.
	 * 
	 * @author Martin Lowe
	 *
	 * @param <T> the type of data contained in the given field
	 */
	public static final class Sortable<T> {
		private String name;
		private String path;
		private final Class<T> type;
		private final Function<String, ?> func;

		private Sortable(Class<T> type) {
			this.type = type;
			// get conversion function which is required for searching properly for value in DB.
			this.func = CONVERSION_FUNCTIONS.get(type);
		}
		
		/**
		 * Using typed conversion functions, converts values passed using the container
		 * as a guide to how to convert for use in queries.
		 * 
		 * @param <T>       The final type of value after conversion
		 * @param value     the value to be converted
		 * @return the converted value
		 */
		@SuppressWarnings("unchecked")
		public T castValue(String value) {
			if (func == null) {
				throw new IllegalStateException("Could not find a converter for value of type " + type);
			}
			return (T) func.apply(value);
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @return the path
		 */
		public String getPath() {
			return path;
		}

		/**
		 * @return the type
		 */
		public Class<T> getType() {
			return type;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Container[");
			sb.append("name=").append(name);
			sb.append(",path=").append(path);
			sb.append(",type=").append(type);
			sb.append(']');
			return sb.toString();
		}
	}

}
