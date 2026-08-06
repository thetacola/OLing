package net.oijon.oling.datatypes.phonology.feature;

import java.util.HashMap;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.oijon.oling.datatypes.InvalidXMLException;

public class Diacritic extends FeaturalXMLDatatype {

	private String character = "";
	
	public Diacritic(String character) {
		initFeatures();
		this.character = character;
	}
	
	public Diacritic(String character, HashMap<String, Feature> features) {
		this(character);
		super.features = features;
	}
	
	public Diacritic(Element e) throws InvalidXMLException {
		this.fromXML(e);
	}
	
	public String getCharacter() {
		return character;
	}

	@Override
	public HashMap<String, Feature> getFeatures() {
    	return features;
    }
	
	@Override
	public Element toXML() throws ParserConfigurationException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.newDocument();
		Element root = doc.createElement("diacritic");
		
		Element charE = doc.createElement("char");
		charE.setTextContent(character);
		root.appendChild(charE);
		
		for (Feature f : super.features.values()) {
			root.appendChild(doc.importNode(f.toXML(), true));
		}
		
		return root;
	}
	
	public void fromXML(Element e) throws InvalidXMLException {
		initFeatures();
		if (e.getTagName().equals("diacritic")) {
			for (int i = 0; i < e.getChildNodes().getLength(); i++) {
				Node child = e.getChildNodes().item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					Element childE = (Element) child;
					switch (childE.getTagName()) {
						case "char":
							character = childE.getTextContent();
							break;
						case "feature":
							Feature f = new Feature(childE, level);
	            			this.addFeature(f);
	            			break;
					}
				}
			}
		} else {
			throw new InvalidXMLException("Node name not expected name! Expected: diacritic; Actual: " + e.getTagName()); 
		}
	}

	/*
	@Override
	public void fromXML(Element e) throws InvalidXMLException {
		initFeatures();
		if (e.getTagName().equals("diacritic")) {
			boolean complex = false;
            for (int i = 0; i < e.getChildNodes().getLength(); i++) {
            	Node child = e.getChildNodes().item(i);
            	if (child.getNodeType() == Node.ELEMENT_NODE) {
            		// technically considered complex at this point, though the marker
            		// is moved into the name check to prevent null diacritics
            		Element childE = (Element) child;
            		if (childE.getTagName().equals("char")) {
            			complex = true;
            			character = childE.getTextContent();
            		} else if (childE.getTagName().equals("feature")) {
            			Feature f = new Feature(childE, level);
            			this.addFeature(f);
            		}
            	}
            }
            if (!complex) {
            	character = e.getTextContent();
            }
		} else {
			throw new InvalidXMLException("Node name not expected name! Expected: diacritic; Actual: " + e.getTagName());
		}
	}
	*/

	@Override
	protected void initFeatures() {
		super.level = FeatureLevel.DIACRITIC;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Diacritic) {
			Diacritic d = (Diacritic) o;
			if (d.character.equals(character)) {
				for (String key : features.keySet()) {
					Feature f = features.get(key);
					Feature df = d.features.get(key);
					if (df == null) {
						return false;
					} else if (!f.equals(df)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public Set<String> getFeatureKeys() {
		return features.keySet();
	}
	
	@Override
	public String toString() {
		String retString = "char: " + character;
		retString += "\nfeatures:\n";
		for (String key : features.keySet()) {
			retString += key + ":" + features.get(key).getValue() + "\n";
		}
		return retString;
	}
}
