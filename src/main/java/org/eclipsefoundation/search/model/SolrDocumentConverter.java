package org.eclipsefoundation.search.model;

import java.lang.reflect.InvocationTargetException;

import org.apache.solr.common.SolrInputDocument;
import org.eclipsefoundation.persistence.dto.BareNode;
import org.eclipsefoundation.search.model.IndexedClassDescriptor.IndexedDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrDocumentConverter<T extends BareNode> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SolrDocumentConverter.class);

	private Class<T> clazz;
	private IndexedClassDescriptor<T> internal;

	public SolrDocumentConverter(Class<T> clazz) {
		this.clazz = clazz;
		this.internal = new IndexedClassDescriptor<>(clazz);
	}

	public SolrInputDocument convert(T entity) {
		SolrInputDocument in = new SolrInputDocument();
		try {
			for (IndexedDescriptor c : internal.getDescriptors()) {
				in.addField(c.name, c.getter.invoke(entity));
			}
		} catch (IllegalAccessException e) {
			LOGGER.error("Could not invoke getter while converting entity of type {}", clazz.getName(), e);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Unexpected argument encountered in getter while converting entity of type {}",
					clazz.getName(), e);
		} catch (InvocationTargetException e) {
			LOGGER.error("Unknown exception while converting entity of type {}", clazz.getName(), e);
		}
		return in;
	}
}
