package net.oijon.oling.datatypes.phonology.table;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.phonology.feature.Diacritic;
import net.oijon.oling.datatypes.phonology.feature.FeaturalXMLDatatype;
import net.oijon.oling.datatypes.phonology.feature.Feature;
import net.oijon.oling.datatypes.phonology.feature.FeatureLevel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Creates the equivalent of an individual phoneme on the IPA chart
 */
public class Phoneme extends FeaturalXMLDatatype {

    private int index;
    private String sound;
    private HashMap<String, Diacritic> diacritics = new HashMap<String, Diacritic>();

    /**
     * Creates a phoneme with a given sound in a string. Note that if this is to be used in a PhonoTable, it needs
     * an index to work properly!
     * @param sound The sound this phoneme represents
     */
    public Phoneme(String sound) {
    	initFeatures();
        this.sound = sound;
        index = 0;
    }

    /**
     * Creates a phoneme with a given sound in a string and an index relative to its PhonoCell
     * @param sound The sound this phonemem represents
     * @param index The index of this sound inside its PhonoCell
     */
    public Phoneme(String sound, int index) {
    	initFeatures();
        this.sound = sound;
        this.index = index;
    }

    /**
     * Creates a phoneme from an XML element
     * @param e The XML element to use
     * @throws InvalidXMLException Thrown when the XML element given is malformed
     */
    public Phoneme(Element e) throws InvalidXMLException {
        fromXML(e);
    }

    /**
     * Gets the index of this phoneme inside its cell
     * @return The index in question
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gets the string of the sound this phoneme represents
     * @return The sound in question
     */
    public String getSound() {
        return sound;
    }

    /**
     * Sets the index of this phoneme inside its cell
     * @param index The new index to use
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Sets the string of the sound this phoneme represents
     * @param sound The new sound to use
     */
    public void setSound(String sound) {
        this.sound = sound;
    }
    
    @Override
    public Element toXML() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("sound");
        root.setAttribute("index", index + "");
        if (!sound.equals("*") && !sound.equals("#")) {
            Element charElement = doc.createElement("char");
            charElement.appendChild(doc.createTextNode(sound));
            root.appendChild(charElement);
            for (Feature f : features.values()) {
            	if (f.getValue() && f.getLevel() == FeatureLevel.SOUND) {
            		root.appendChild(doc.importNode(f.toXML(), true));
            	}
            }
        }

        return root;
    }

    @Override
    public void fromXML(Element e) throws InvalidXMLException {
    	initFeatures();
        if (e.getTagName().equals("sound")) {
            index = Integer.parseInt(e.getAttribute("index"));
            
            boolean complex = false;
            for (int i = 0; i < e.getChildNodes().getLength(); i++) {
            	Node child = e.getChildNodes().item(i);
            	if (child.getNodeType() == Node.ELEMENT_NODE) {
            		// technically considered complex at this point, though the marker
            		// is moved into the name check to prevent null sounds
            		Element childE = (Element) child;
            		if (childE.getTagName().equals("char")) {
            			complex = true;
            			sound = childE.getTextContent();
            		} else if (childE.getTagName().equals("feature")) {
            			Feature f = new Feature(childE, level);
            			this.addFeature(f);
            		}
            	}
            }
            if (!complex) {
            	sound = e.getTextContent();
            }
        } else {
            throw new InvalidXMLException("Node name not expected name! Expected: sound; Actual: " + e.getTagName());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Phoneme) {
            Phoneme p = (Phoneme) o;
            if (p.features.size() == features.size()) {
            	ArrayList<String> keys = new ArrayList<>(features.keySet());
            	for (String key : keys) {
            		if (!p.features.get(key).equals(features.get(key))) {
            			return false;
            		}
            	}
            	return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String returnString = "[" + index + ": " + sound + "]";
        return returnString;
    }

	@Override
	protected void initFeatures() {
		super.level = FeatureLevel.SOUND;
	}
}
