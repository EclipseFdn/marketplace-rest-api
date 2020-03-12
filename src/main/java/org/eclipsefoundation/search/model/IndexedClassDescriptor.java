package org.eclipsefoundation.search.model;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipsefoundation.persistence.dto.BareNode;

public class IndexedClassDescriptor<T extends BareNode> {
	private List<IndexedDescriptor> internal;

	public IndexedClassDescriptor(Class<T> clazz) {
		try {
			for (Field f : clazz.getFields()) {
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
					internal.add(
							new IndexedDescriptor(property.getName(), property.getReadMethod(), annotation.boost()));
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
		String name;
		Method getter;
		int boost;

		private IndexedDescriptor(String name, Method getter, int boost) {
			this.name = name;
			this.getter = getter;
			this.boost = boost;
		}
	}
}
