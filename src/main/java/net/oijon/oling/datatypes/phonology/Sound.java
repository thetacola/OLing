package net.oijon.oling.datatypes.phonology;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;
import net.oijon.oling.datatypes.language.Language;
import net.oijon.oling.datatypes.phonology.feature.Diacritic;
import net.oijon.oling.datatypes.phonology.feature.FeaturalXMLDatatype;
import net.oijon.oling.datatypes.phonology.feature.Feature;
import net.oijon.oling.datatypes.phonology.feature.FeatureLevel;
import net.oijon.oling.datatypes.phonology.table.Phoneme;
import net.oijon.oling.datatypes.phonology.table.PhonoSystem;
import net.oijon.oling.info.Info;
import net.oijon.olog.Log;

public class Sound implements XMLDatatype {

	static Log log = Info.log;
	private String character; // what a non-intuitive name we have here
	private Phoneme phoneme;
	private HashMap<String, Diacritic> diacritics = new HashMap<>();
	private HashMap<String, Feature> features = new HashMap<>();
	private Phonology linkedPhono;
	
	/**
	 * Creates a sound from a character, checking if it's in the phonology linked.
	 * All sounds need to be linked to a phonology.
	 * @param character The string to create a sound from. Despite being called "character", it can
	 * consist of multiple characters, and is expected to when taking diacritics. This is called "character"
	 * as that is what the representation of a sound is typically referred to in an IPA context.
	 * @param phono The Phonology this sound should be using.
	 */
	public Sound(String character, Phonology phono) {
		// Checking to make sure someone didn't pass null, without logging this, it'd likely fail non-intuitively
		if (phono == null) {
			log.err("Sound given null phonology on creation!!!");
			this.character = "";
			this.linkedPhono = Language.NULL.getPhono();
		} else {
			this.character = character;
			this.linkedPhono = phono;
			generateFeatures();
		}		
	}
	
	public HashMap<String, Diacritic> getDiacritics() {
		return diacritics;
	}
	
	public Phoneme getPhoneme() {
		return phoneme;
	}
	
	private void generateFeatures() {
		PhonoSystem ps = linkedPhono.getPhonoSystem();
		ArrayList<String> diacritics = ps.getDiacriticKeys();
		
		String baseForm = this.character;
		for (int i = 0; i < diacritics.size(); i++) {
			// TODO: check the direction the diacritic is in relation to the character! Some IPA diacritics
			// are dependent on direction!
			String diaS = diacritics.get(i);
			if (this.character.contains(diaS)) {
				this.diacritics.put(diaS, ps.getDiacritic(diaS));
				baseForm.replace(diaS, "");
			}
		}
		
		Phoneme baseP = ps.find(baseForm);
		if (baseP != null) {
			HashMap<String, Feature> baseFeatures = baseP.getFeatures();
			this.features.putAll(baseFeatures);
		}
		
		for (String key : this.diacritics.keySet()) {
			Diacritic d = this.diacritics.get(key);
			for (String featKey : d.getFeatures().keySet()) {
				Feature f = d.getFeatures().get(featKey);
				this.features.put(key, new Feature(featKey, f.getValue(), FeatureLevel.DIACRITIC));
			}
		}
		
	}

	@Override
	public Element toXML() throws ParserConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fromXML(Element e) throws InvalidXMLException {
		// TODO Auto-generated method stub
		
	}

	protected void initFeatures() {
		PhonoSystem ps = linkedPhono.getPhonoSystem();
		ArrayList<String> diacritics = ps.getDiacriticKeys();
		
		String baseForm = this.character;
		for (int i = 0; i < diacritics.size(); i++) {
			// TODO: check the direction the diacritic is in relation to the character! Some IPA diacritics
			// are dependent on direction!
			String diaS = diacritics.get(i);
			if (this.character.contains(diaS)) {
				this.diacritics.put(diaS, ps.getDiacritic(diaS));
				baseForm.replace(diaS, "");
			}
		}
		
		Phoneme baseP = ps.find(baseForm);
		if (baseP != null) {
			HashMap<String, Feature> baseFeatures = baseP.getFeatures();
			this.features.putAll(baseFeatures);
		} else {
			log.err("Unable to find phoneme when linking to sound! Searched for /" + baseForm +
					"/ for sound [" + this.character + "]!");
		}
		
		for (String key : this.diacritics.keySet()) {
			Diacritic d = this.diacritics.get(key);
			for (String featKey : d.getFeatures().keySet()) {
				Feature f = d.getFeatures().get(featKey);
				this.features.put(key, new Feature(featKey, f.getValue(), FeatureLevel.DIACRITIC));
			}
		}
	}
	
	@Override
	public String toString() {
		return character;
	}
}
