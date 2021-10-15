package org.eclipsefoundation.search.model;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.index.Term;
import org.apache.lucene.queries.function.BoostedQuery;
import org.apache.lucene.queries.function.valuesource.ConstValueSource;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.eclipsefoundation.persistence.dto.BareNode;
import org.eclipsefoundation.search.helper.FieldHelper;
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
		// don't index documents with no fields to index
		if (internal.getDescriptors().isEmpty()) {
			return null;
		}
		SolrInputDocument in = new SolrInputDocument();
		try {
			for (IndexedDescriptor c : internal.getDescriptors()) {
				Object data = c.getter.invoke(entity);
				in.addField(FieldHelper.convertNameToField(c), data);
			}
			// get the standard fields
			in.addField("id", entity.getId().toString());
			in.addField("type_s", clazz.getName());
			in.addField("title_s", entity.getTitle());
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

	public SolrParams getBaseQuery(String searchTerm) {
		// build text match query, where at least 1 values needs to match
		Builder textMatches = new Builder();
		// add title manually
		textMatches.add(new TermQuery(new Term("title_s", searchTerm)), Occur.SHOULD);
		for (IndexedDescriptor c : internal.getDescriptors()) {
			TermQuery base = new TermQuery(new Term(FieldHelper.convertNameToField(c), searchTerm));
			if (c.getBoost() != 1.0f) {
				textMatches.add(
						new BooleanClause(new BoostedQuery(base, new ConstValueSource(c.getBoost())), Occur.SHOULD));
			} else {
				textMatches.add(base, Occur.SHOULD);
			}
		}
		// build document type + text match boolean query
		Builder textAndTypeBuilder = new Builder();
		textAndTypeBuilder.add(textMatches.build(), Occur.MUST);
		textAndTypeBuilder.add(new TermQuery(new Term("type_s", clazz.getName())), Occur.MUST);
		
		// set up base query from the required values
		Map<String, String> queryParamMap = new HashMap<>();
		queryParamMap.put("q", textAndTypeBuilder.build().toString());
		queryParamMap.put("fl", "id");
		queryParamMap.put("df", "*");
		return new MapSolrParams(queryParamMap);
	}
}
