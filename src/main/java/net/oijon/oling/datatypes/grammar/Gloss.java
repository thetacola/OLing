package net.oijon.oling.datatypes.grammar;

import org.w3c.dom.Element;
import net.oijon.oling.datatypes.InvalidRootNodeException;
import net.oijon.oling.datatypes.XMLComponent;

//last edit: 1/18/25 -N3

/**
 * Attaches a meaning to a gloss abbreviation, allowing users to create their
 * own, or see what a certain abbreviation means.
 * 
 * @author alex
 *
 */
public class Gloss extends XMLComponent {

	private String abbreviation;
	private String meaning;
	
	public Gloss(Element element) throws InvalidRootNodeException {
		super(element);
		if (element.getNodeName().equals("gloss")) {
			abbreviation = super.getSingleChildElementValue("abbreviation", element);
			meaning = super.getSingleChildElementValue("meaning", element);		
		} else {
			throw new InvalidRootNodeException("Expected 'gloss', got '" + element.getNodeName() + "'.");
		}
	}
	
	/**
	 * Creates a Gloss based off an abbreviation and a plaintext meaning
	 * @param abbreviation The abbreviation of the gloss
	 * @param meaning What the gloss means
	 */
	public Gloss(String abbreviation, String meaning) {
		super();
		this.abbreviation = abbreviation;
		this.meaning = meaning;
	}
	
	/**
	 * Copy constructor
	 * @param gloss The gloss to copy
	 */
	public Gloss(Gloss gloss) {
		super();
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
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}
	
	/**
	 * Sets the meaning of the gloss
	 * @param meaning The new meaning of the gloss
	 */
	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}
	
	@Override
	public String toString() {
		String returnString = "===Gloss Start===\n";
		returnString += "abb:" + abbreviation + "\n";
		returnString += "meaning:" + meaning + "\n";
		returnString += "===Gloss End===";
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

	@Override
	protected void updateElement() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void parseFromElement() {
		// TODO Auto-generated method stub
		
	}

}
