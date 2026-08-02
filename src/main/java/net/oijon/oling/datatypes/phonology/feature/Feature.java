package net.oijon.oling.datatypes.phonology.feature;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;

public class Feature implements XMLDatatype {

	private String name;
	private boolean value;
	private FeatureLevel level;
	
	public Feature(String name, boolean value, FeatureLevel level) {
		this.name = name;
		this.value = value;
		this.level = level;
	}
	
	public Feature(Element e, FeatureLevel level) throws InvalidXMLException {
		fromXML(e);
		// The level is stored via the position of the parent, we can't get that so the level needs to be defined here
		this.level = level;
	}
	
	/**
	 * Gets the name of a feature
	 * @return The name of the feature
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the value of a feature. True for +, False for -
	 * @return The value of a feature
	 */
	public boolean getValue() {
		return value;
	}
	
	/**
	 * Sets the value of a feature. Useful for diacritics.
	 * @param value The new value of the feature
	 */
	public void setValue(boolean value) {
		this.value = value;
	}
	
	public FeatureLevel getLevel() {
		return level;
	}
	
	public void setLevel(FeatureLevel level) {
		this.level = level;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Feature) {
			Feature f = (Feature) o;
			if (f.getName().equals(name) && f.getValue() == value) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		String returnString = "[" + name + ", " + value + ", " +
				this.getLevel().toString() + "]";
		return returnString;
	}

	@Override
	public Element toXML() throws ParserConfigurationException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        
        Element featureElement = doc.createElement("feature");
		featureElement.setTextContent(name);
		
		return featureElement;
	}

	@Override
	public void fromXML(Element e) throws InvalidXMLException {
		if (e.getTagName().equals("feature")) {
			String textContent = e.getTextContent();
			this.name = textContent;
			this.value = true;
		} else {
            throw new InvalidXMLException("Node name not expected name! Expected: feature; Actual: " + e.getTagName());
        }
	}
	
}
