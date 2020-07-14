package org.eclipsefoundation.search.model;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.eclipsefoundation.search.namespace.IndexerTextProcessingType;

/**
 * Annotation controlling how entities are indexed via the search DAO.
 * 
 * @author Martin Lowe
 *
 */
@Retention(RUNTIME)
@Target({ TYPE, FIELD })
public @interface Indexed {

	String fieldName() default "";

	/**
	 * Boost value for the given field.
	 * 
	 * @return the boost value for the field, or the default value of 1
	 */
	float boost() default 1f;

	/**
	 * Whether the value should be stored as is and returned.
	 * 
	 * @return true if the value should be stored on index, false otherwise.
	 */
	boolean stored() default false;
	
	IndexerTextProcessingType textProcessing() default IndexerTextProcessingType.NONE;
}
