package net.oijon.oling.datatypes.phonology.surface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.oijon.oling.datatypes.phonology.Phonology;
import net.oijon.oling.datatypes.phonology.feature.Diacritic;
import net.oijon.oling.datatypes.phonology.feature.Feature;
import net.oijon.oling.datatypes.phonology.feature.FeatureLevel;
import net.oijon.oling.datatypes.phonology.table.Phoneme;
import net.oijon.oling.datatypes.phonology.table.PhonoSystem;
import net.oijon.oling.info.Info;
import net.oijon.olog.Log;

public class Sound {

	static final Log log = Info.log;
	private final String character; // what a non-intuitive name we have here
	private final Phoneme phoneme;
	private final HashMap<String, Diacritic> diacritics = new HashMap<>();
	private final HashMap<String, Feature> features = new HashMap<>();
	private final Phonology linkedPhono;
	
	/**
	 * Creates a sound from a character, checking if it's in the phonology linked.
	 * All sounds need to be linked to a phonology.
	 * @param character The string to create a sound from. Despite being called "character", it can
	 * consist of multiple characters, and is expected to when taking diacritics. This is called "character"
	 * as that is what the representation of a sound is typically referred to in an IPA context.
	 * @param phono The Phonology this sound should be using.
	 */
	public Sound(String character, Phonology phono) {
		if (phono == null) {
			throw new IllegalArgumentException("Phonology cannot be null.");
		}
		this.character = character;
		this.linkedPhono = phono;
		
		PhonoSystem ps = linkedPhono.getPhonoSystem();
		ArrayList<String> diacritics = ps.getDiacriticKeys();
		
		String baseForm = this.character;
		for (int i = 0; i < diacritics.size(); i++) {
			// TODO: check the direction the diacritic is in relation to the character! Some IPA diacritics
			// are dependent on direction!
			String diaS = diacritics.get(i);
			if (this.character.contains(diaS)) {
				this.diacritics.put(diaS, ps.getDiacritic(diaS));
				baseForm = baseForm.replace(diaS, "");
			}
		}
		
		Phoneme baseP = ps.find(baseForm);
		if (baseP != null) {
			this.phoneme = baseP;
			HashMap<String, Feature> baseFeatures = baseP.getFeatures();
			this.features.putAll(baseFeatures);
		} else {
			log.err("Unable to find phoneme when linking to sound! Searched for /" + baseForm +
					"/ for sound [" + this.character + "]!");
			this.phoneme = null;
		}
		
		for (String key : this.diacritics.keySet()) {
			Diacritic d = this.diacritics.get(key);
			for (String featKey : d.getFeatures().keySet()) {
				Feature f = d.getFeatures().get(featKey);
				this.features.put(f.getName(), new Feature(f.getName(), f.getValue(), FeatureLevel.DIACRITIC));
			}
		}
	}
	
	
	public Sound(Sound s) {
		this.character = s.character;
		this.phoneme = s.phoneme;
		this.features.putAll(s.features);
		this.linkedPhono = s.linkedPhono;
		
		for (String key : s.diacritics.keySet()) {
			Diacritic oldD = s.diacritics.get(key);
			Diacritic newD = new Diacritic(oldD.getCharacter(), oldD.getFeatures());
			this.diacritics.put(newD.getCharacter(), newD);
		}
		
	}
	
	public HashMap<String, Diacritic> getDiacritics() {
		return new HashMap<String, Diacritic>(diacritics);
	}
	
	public Phoneme getPhoneme() {
		return phoneme;
	}
	
	public int getSonorancy() {
		return linkedPhono.getPhonoSystem().getSonorancyTree().getValue(this);
	}
	
	public int getWeight() {
		// TODO: make this change based on things such as length
		return 1;
	}
	
	public HashMap<String, Feature> getFeatures() {
		return new HashMap<String, Feature>(features);
	}
	
	@Override
	public String toString() {
		return character;
	}
}
