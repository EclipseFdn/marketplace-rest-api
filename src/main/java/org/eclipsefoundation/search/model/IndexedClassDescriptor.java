package org.eclipsefoundation.search.model;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipsefoundation.persistence.dto.BareNode;
import org.eclipsefoundation.search.namespace.IndexerTextProcessingType;

/**
 * Describes an entity type that should be indexed. This class works on
 * reflection of the Runtime class to retrieve internal fields that have been
 * annotated with the Indexed field
 * 
 * @author Martin Lowe
 *
 * @param <T> the entity type this descriptor describes.
 */
public class IndexedClassDescriptor<T extends BareNode> {
	private List<IndexedDescriptor> internal;

	public IndexedClassDescriptor(Class<T> clazz) {
		this.internal = new ArrayList<>();
		try {
			for (Field f : clazz.getDeclaredFields()) {
				Indexed annotation = f.getAnnotation(Indexed.class);
				if (annotation != null) {
					String name = f.getName();
					Optional<PropertyDescriptor> propertyOpt = Arrays
							.asList(Introspector.getBeanInfo(clazz).getPropertyDescriptors()).stream()
							.filter(pd -> name.equals(pd.getName())).findFirst();
					if (!propertyOpt.isPresent()) {
						throw new RuntimeException("Could not generate SolrDocumentConverter for " + clazz.getName());
					}
					PropertyDescriptor property = propertyOpt.get();
					internal.add(new IndexedDescriptor(f, property.getReadMethod(), annotation));
				}
			}
		} catch (IntrospectionException e) {
			throw new RuntimeException("Could not generate SolrDocumentConverter for " + clazz.getName(), e);
		}
	}

	public List<IndexedDescriptor> getDescriptors() {
		return new ArrayList<>(internal);
	}

	public static class IndexedDescriptor {
		final String name;
		final Method getter;
		final float boost;
		final boolean stored;
		final IndexerTextProcessingType textProcessing;
		final Class<?> type;
		final Class<?> subtype;

		private IndexedDescriptor(Field field, Method getter, Indexed annotation) {
			this.name = field.getName();
			this.getter = getter;
			this.boost = annotation.boost();
			this.stored = annotation.stored();
			this.textProcessing = annotation.textProcessing();
			this.type = field.getType();
			// if the generic type is different from the base, indicates a generic
			// this will not work for entity types nested within themselves(List in List, Map in Map etc.)
			if (field.getGenericType() instanceof ParameterizedType) {
				// using reflection, gets declared type from the raw source
				this.subtype = (Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
			} else {
				this.subtype = null;
			}
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the getter
		 */
		public Method getGetter() {
			return getter;
		}

		/**
		 * @return the boost
		 */
		public float getBoost() {
			return boost;
		}

		/**
		 * @return the stored
		 */
		public boolean isStored() {
			return stored;
		}

		/**
		 * @return the textProcessing
		 */
		public IndexerTextProcessingType getTextProcessing() {
			return textProcessing;
		}

		public Class<?> getType() {
			return this.type;
		}
		
		public Class<?> getSubtype() {
			return this.subtype;
		}
	}
}
