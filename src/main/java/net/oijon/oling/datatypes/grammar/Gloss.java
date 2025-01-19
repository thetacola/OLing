package net.oijon.oling.datatypes.grammar;

import jakarta.xml.bind.annotation.XmlElement;

//last edit: 1/19/25 -N3

/**
 * Attaches a meaning to a gloss abbreviation, allowing users to create their
 * own, or see what a certain abbreviation means.
 * 
 * @author alex
 *
 */

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "gloss")
@XmlType(propOrder = {"abbreviation", "meaning"})
public class Gloss {

	private String abbreviation;
	private String meaning;
	
	/**
	 * Creates a Gloss based off an abbreviation and a plaintext meaning
	 * @param abbreviation The abbreviation of the gloss
	 * @param meaning What the gloss means
	 */
	public Gloss(String abbreviation, String meaning) {
		this.abbreviation = abbreviation;
		this.meaning = meaning;
	}
	
	/**
	 * Copy constructor
	 * @param gloss The gloss to copy
	 */
	public Gloss(Gloss gloss) {
		this.abbreviation = gloss.abbreviation;
		this.meaning = gloss.meaning;
	}
	
	/**
	 * Gets the abbreviation of the gloss
	 * @return The abbreviation of the gloss
	 */
	public String getAbbreviation() {
		return abbreviation;
	}
	
	/**
	 * Gets the meaning of the gloss
	 * @return The meaning of the gloss
	 */
	public String getMeaning() {
		return meaning;
	}
	
	/**
	 * Sets the abbreviation of the gloss
	 * @param abbreviation The new abbreviation for the gloss
	 */
	@XmlElement(name = "abbreviation") 
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}
	
	/**
	 * Sets the meaning of the gloss
	 * @param meaning The new meaning of the gloss
	 */
	@XmlElement(name = "meaning")
	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}
	
	
	
	@Override
	public String toString() {
		String returnString = "Gloss\n";
		returnString += "├─ Abbreviation: " + abbreviation + "\n";
		returnString += "└─ Meaning: " + meaning + "\n";
		return returnString;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Gloss) {
			Gloss g = (Gloss) o;
			if (g.getAbbreviation().equals(abbreviation) & g.getMeaning().equals(meaning)) {
				return true;
			}
		}
		return false;
	}	


}
