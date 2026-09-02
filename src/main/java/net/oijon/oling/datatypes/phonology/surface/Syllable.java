package net.oijon.oling.datatypes.phonology.surface;

import net.oijon.oling.datatypes.language.Language;
import net.oijon.oling.datatypes.phonology.Phonology;
import net.oijon.oling.info.Info;
import net.oijon.olog.Log;

public class Syllable {

	final static Log log = Info.log;
	
	private final SoundString onset;
	private final SoundString nucleus;
	private final SoundString coda;
	private final Phonology linkedPhono;

	// TODO: add various levels of stress

	private int nucleusWeight = 0;
	private int codaWeight = 0;

	public Syllable() {
		this.onset = new SoundString();
		this.nucleus = new SoundString();
		this.coda = new SoundString();
		this.linkedPhono = Language.NULL.getPhono();
	}
	
	public Syllable(SoundString onset, SoundString nucleus, SoundString coda, Phonology p) {
		this.onset = onset;
		this.nucleus = nucleus;
		this.coda = coda;
		linkedPhono = p;
	}
	
	public int getMoraicWeight() {
		return nucleusWeight + codaWeight;
	}

	public SoundString getOnset() {
		return onset;
	}

	public Syllable setOnset(SoundString onset) {
		return new Syllable(onset, this.nucleus, this.coda, this.linkedPhono);
	}

	public SoundString getNucleus() {
		return nucleus;
	}

	public Syllable setNucleus(SoundString nucleus) {
		return new Syllable(this.onset, nucleus, this.coda, this.linkedPhono);
	}

	public SoundString getCoda() {
		return coda;
	}

	public Syllable setCoda(SoundString coda) {
		return new Syllable(this.onset, this.nucleus, coda, this.linkedPhono);
	}

	@Override
	public String toString() {
		String returnString = "";
		returnString += onset.toString();
		returnString += nucleus.toString();
		returnString += coda.toString();
		return returnString;
	}
}
