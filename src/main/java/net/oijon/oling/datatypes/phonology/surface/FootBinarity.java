package net.oijon.oling.datatypes.phonology.surface;

public enum FootBinarity {
	/**
	 * Used for when all syllables must be in a foot, regardless if that violates binarity
	 */
	PARSE,
	/**
	 * Used for when feet must only consist of two syllables, even if it leaves a footless syllable
	 */
	BINARY;
}
