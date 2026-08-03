package net.oijon.oling.datatypes.phonology;

import java.util.ArrayList;

public class Syllable {

	private ArrayList<Sound> onset;
	private ArrayList<Sound> nucleus;
	private ArrayList<Sound> coda;
	
	private int nucleusWeight = 0;
	private int codaWeight = 0;
	
	public int getMoraicWeight() {
		return nucleusWeight + codaWeight;
	}
	
	public ArrayList<Sound> getOnset() {
		return onset;
	}
	
	public void setOnset(ArrayList<Sound> onset) {
		this.onset = onset;
	}
	
	public ArrayList<Sound> getNucleus() {
		return nucleus;
	}
	
	public void setNucleus(ArrayList<Sound> nucleus) {
		this.nucleus = nucleus;
		nucleusWeight = nucleus.size();
	}
	
	public ArrayList<Sound> getCoda() {
		return coda;
	}
	
	public void setCoda(ArrayList<Sound> coda) {
		this.coda = coda;
		codaWeight = coda.size();
	}
}
