package net.oijon.oling.datatypes.phonology;

import java.util.ArrayList;

import net.oijon.oling.datatypes.phonology.table.Phoneme;

public class Syllable {

	private ArrayList<Phoneme> onset;
	private ArrayList<Phoneme> nucleus;
	private ArrayList<Phoneme> coda;
	
	private int nucleusWeight = 0;
	private int codaWeight = 0;
	
	public int getMoraicWeight() {
		return nucleusWeight + codaWeight;
	}
}
