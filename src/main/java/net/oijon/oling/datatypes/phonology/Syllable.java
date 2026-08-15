package net.oijon.oling.datatypes.phonology;

import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;

public class Syllable implements XMLDatatype {

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
	
	@Override
	public String toString() {
		String returnString = "";
		for (int i = 0; i < onset.size(); i++) {
			returnString += onset.get(i).toString();
		}
		for (int i = 0; i < nucleus.size(); i++) {
			returnString += nucleus.get(i).toString();
		}
		for (int i = 0; i < coda.size(); i++) {
			returnString += coda.get(i).toString();
		}
		return returnString;
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
}
