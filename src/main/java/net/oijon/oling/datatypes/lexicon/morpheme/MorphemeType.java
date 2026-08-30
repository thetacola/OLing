package net.oijon.oling.datatypes.lexicon.morpheme;

/**
 * Describes the type of morpheme.
 */
public enum MorphemeType {
	/**
	 * Unbound morpheme, ex. the "bed" in "bedroom". A MorphemeWord may contain more than one,
	 * as is the case in compounds.
	 */
	FREE(false),
	/**
	 * Morphemes that show either how the word relates to others, or inflect for some
	 * grammatical attribute, ex. the "s" in "beds"
	 */
	INFLECTIONAL(true),
	/**
	 * Changes the part of speech or the meaning of the word, ex. the "y" in "sleepy" or the
	 * "pre" in "preheat"
	 */
	DERIVATIONAL(true),
	/**
	 * Used for morphemes that are not free, and have no independent meaning, ex. the "cran"
	 * in "cranberry" (hence the name)
	 */
	CRANBERRY(true),
	/**
	 * Used for morphemes that are used solely for phonological reasons, ex. the "o" in
	 * "speedometer"
	 */
	EMPTY(true),
	/**
	 * Used for morphemes that have meaning but no phonological realization, ex. the "Ø" in the
	 * plural of "sheep"
	 */
	NULL(true);
	
	private final boolean isBound;
	
	private MorphemeType(boolean isBound) {
		this.isBound = isBound;
	}
	
	/**
	 * Gets whether or not the type is bound
	 * @return true if bound, false otherwise
	 */
	public boolean isBound() {
		return isBound;
	}
}
