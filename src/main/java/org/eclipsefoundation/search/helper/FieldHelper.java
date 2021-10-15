package org.eclipsefoundation.search.helper;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.eclipsefoundation.search.model.IndexedClassDescriptor.IndexedDescriptor;
import org.eclipsefoundation.search.namespace.IndexerTextProcessingType;

public class FieldHelper {

	public static String convertNameToField(IndexedDescriptor c) {
		return convertNameToField(c, false);
	}
	
	public static String convertNameToField(IndexedDescriptor c, boolean nested) {
		String name = c.getName();
		if (c.getType().equals(List.class) && !nested) {
			// not supported operation
			if (!c.getTextProcessing().equals(IndexerTextProcessingType.NONE)) {
				return "ignored_" + name;
			}
			return convertNameToField(c, true) + "s";
		} else if (c.getType().equals(String.class)) {
			return name + getTextProcessingSuffix(c);
		} else if (c.getType().equals(UUID.class)) {
			return name + "_s";
		} else if (c.getType().equals(Integer.class)) {
			return name + "_i";
		} else if (c.getType().equals(Long.class)) {
			return name + "_l";
		} else if (c.getType().equals(Float.class)) {
			return name + "_f";
		} else if (c.getType().equals(Boolean.class)) {
			return name + "_b";
		} else if (c.getType().equals(Double.class)) {
			return name + "_d";
		} else if (c.getType().equals(Date.class)) {
			return name + "_dt";
		} else {
			// fallback for unknown types getting indexed
			return "ignored_" + name;
		}
	}
	
	public static String getTextProcessingSuffix(IndexedDescriptor c) {
		switch (c.getTextProcessing()) {
		case GENERAL:
			return "_txt_gen";
		case STANDARD:
			return "_txt_en";
		case AGGRESSIVE:
			return "_txt_en_split";
		default:
			return "_s";
		}
	}

	private FieldHelper() {
	}
}
