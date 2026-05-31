package net.oijon.oling.datatypes.phonology.feature;

public enum FeatureLevel {
	LANGUAGE, // useful for certain things that are not applicable to all languages, though form some 
			  // cross-linguistic pattern, like moraic weight for example
	SOUND,
	CELL,
	ROW,
	COLUMN,
	TABLE,
	SYSTEM,
	OTHER;
}
