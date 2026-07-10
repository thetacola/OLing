package net.oijon.oling.datatypes.phonology;

import java.util.HashMap;

import net.oijon.oling.datatypes.phonology.feature.Diacritic;
import net.oijon.oling.datatypes.phonology.table.Phoneme;
import net.oijon.oling.info.Info;
import net.oijon.olog.Log;

public class Sound {

	static Log log = Info.log;
	private String character; // what a non-intuitive name we have here
	private Phoneme phoneme;
	private HashMap<String, Diacritic> diacritics;
	private Phonology linkedPhono;
	
	/**
	 * Creates a sound from a character, checking if it's in the phonology system linked. All sounds need to be linked
	 * to a phonology.
	 * @param character
	 * @param phono
	 */
	public Sound(String character, Phonology phono) {
		// Checking to make sure someone didn't pass null, without logging this, it'd likely fail non-intuitively
		if (phono == null) {
			log.err("Sound given null phonology on creation!!!");
		}
		// We don't need to actually prevent creation if that happens, because it'll just fail.
		this.character = character;
		this.linkedPhono = phono;
		
		
		// find any diacritics, strip them, and add to the hashmap
		for (String key : phono.getPhonoSystem().getDiacriticKeys()) {
			
		}
		
		
		if (phono.getPhonoSystem().contains(character)) {
			
		} else {
			log.warn("Sound " + character + " not in phonology system " + phono.getPhonoSystem().getName() + "!");
		}
		
	}
	
}
