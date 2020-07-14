package org.eclipsefoundation.search.namespace;

/**
 * Allows for the support of multiple different types of text post-processing
 * based on the types of requirements on the data. Generally the more complex
 * the sort the more expensive it is computationally both at query and index
 * time.
 * 
 * <ul>
 * <li>
 * <p>
 * <strong>GENERAL:</strong>
 * </p>
 * <p>
 * Uses generic cross-language processing when available. This is appropriate
 * for fields/searches where English may not be the primary language but complex
 * indexing and searching is desired.
 * </p>
 * </li>
 * <li>
 * <p>
 * <strong>STANDARD:</strong>
 * </p>
 * <p>
 * Uses standard processing given English language text stopwords and protected
 * words. This field should be used when the language of the data entered is
 * known to be English as it provides more accurate results and allows for qtime
 * analysis for synonyms.
 * </p>
 * </li>
 * <li>
 * <p>
 * <strong>AGGRESSIVE:</strong>
 * </p>
 * <p>
 * Uses aggressive processing given English language text stopwords and
 * protected words. This type of processing will also process individual words
 * to allow matching on potentially split words using indicators of word
 * boundaries like changed casing, numbers, and non alpha-numeric characters.
 * This will allow for matches on things like brand names like ASCIIDoc on match
 * for searches like "ascii doc".
 * </p>
 * </li>
 * <li>
 * <p>
 * <strong>NONE:</strong>
 * </p>
 * <p>
 * No text processing will be done on this text and it will be posted and processed as is.
 * </p>
 * </li>
 * </ul>
 * 
 * @author Martin Lowe
 *
 */
public enum IndexerTextProcessingType {
	GENERAL, STANDARD, AGGRESSIVE, NONE;
}
