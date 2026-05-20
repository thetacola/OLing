package net.oijon.oling.datatypes.phonology.feature;

import java.util.ArrayList;

public class Diacritic {

	private String character;
	// Diacritics applied to a character override the features of a given phoneme
	private ArrayList<Feature> features;
	
	public Diacritic(String character) {
		this.character = character;
	}
	
	public Diacritic(String character, ArrayList<Feature> features) {
		this(character);
		this.features = features;
	}
	
	public String getCharacter() {
		return character;
	}
	
	public ArrayList<Feature> getFeatures() {
		return new ArrayList<Feature>(features);
	}
	
	public void addFeature(Feature f) {
		// There should only ever be one instance of a given feature in the list
		boolean found = false;
		for (int i = 0; i < features.size(); i++) {
			if (features.get(i).getName().equals(f.getName())) {
				found = true;
				break;
			}
		}
		
		if (!found) {
			this.features.add(f);
		}
	}
	
	public void removeFeature(String name) {
		for (int i = 0; i < features.size(); i++) {
			// As due to how addFeature works one can assume only one of each feature,
			// this can break on the first find
			if (features.get(i).getName().equals(name)) {
				features.remove(i);
				break;
			}
		}
	}
	
	public void removeFeature(Feature f) {
		removeFeature(f.getName());
	}
}
