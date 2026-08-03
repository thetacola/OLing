package net.oijon.oling.datatypes.phonology;

import java.util.HashMap;

import net.oijon.oling.datatypes.phonology.feature.Diacritic;
import net.oijon.oling.datatypes.phonology.table.Phoneme;
import net.oijon.oling.datatypes.phonology.table.PhonoSystem;
import net.oijon.oling.info.Info;
import net.oijon.olog.Log;

public class Sound {

	static Log log = Info.log;
	private String character; // what a non-intuitive name we have here
	private Phoneme phoneme;
	private HashMap<String, Diacritic> diacritics;
	private PhonoSystem linkedSys;
	
	/**
	 * Creates a sound from a character, checking if it's in the phonology system linked.
	 * All sounds need to be linked
	 * to a phonology.
	 * @param character The string to create a sound from. Despite being called "character", it can
	 * consist of multiple characters, and is expected to when taking diacritics. This is called "character"
	 * as that is what the representation of a sound is typically referred to in an IPA context.
	 * @param sys The PhonoSystem this sound should be using.
	 */
	public Sound(String character, PhonoSystem sys) {
		// Checking to make sure someone didn't pass null, without logging this, it'd likely fail non-intuitively
		if (sys == null) {
			log.err("Sound given null phono system on creation!!!");
		}
		// We don't need to actually prevent creation if that happens, because it'll just fail.
		this.character = character;
		this.linkedSys = sys;
		
		
		// find any diacritics, strip them, and add to the hashmap
		for (String key : sys.getDiacriticKeys()) {
			
		}
		
		
		if (sys.contains(character)) {
			
		} else {
			log.err("Sound " + character + " not in phonology system " + sys.getName() + "!");
		}
		
	}
	
}
