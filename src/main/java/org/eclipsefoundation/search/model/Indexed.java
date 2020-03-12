package org.eclipsefoundation.search.model;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ TYPE, FIELD })
public @interface Indexed {

	String fieldName() default "";

	/**
	 * Boost value for the given field.
	 * 
	 * @return the boost value for the field, or the default value of 1
	 */
	int boost() default 1;
}
