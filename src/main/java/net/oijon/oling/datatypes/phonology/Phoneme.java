package net.oijon.oling.datatypes.phonology;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Creates the equivalent of an individual phoneme on the IPA chart
 */
public class Phoneme implements XMLDatatype {

    private int index;
    private String sound;

    /**
     * Creates a phoneme with a given sound in a string. Note that if this is to be used in a PhonoTable, it needs
     * an index to work properly!
     * @param sound The sound this phoneme represents
     */
    public Phoneme(String sound) {
        this.sound = sound;
        index = 0;
    }

    /**
     * Creates a phoneme with a given sound in a string and an index relative to its PhonoCell
     * @param sound The sound this phonemem represents
     * @param index The index of this sound inside its PhonoCell
     */
    public Phoneme(String sound, int index) {
        this.sound = sound;
        this.index = index;
    }

    /**
     * Creates a phoneme from an XML element
     * @param e The XML element to use
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
        root.appendChild(builder.newDocument().createTextNode(sound));

        return root;
    }

    @Override
    public void fromXML(Element e) throws InvalidXMLException {
        if (e.getTagName().equals("sound")) {
            index = Integer.parseInt(e.getAttribute("index"));
            sound = e.getTextContent();
        } else {
            throw new InvalidXMLException("Node name not expected name! Expected: sound; Actual: " + e.getTagName());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof  Phoneme) {
            Phoneme p = (Phoneme) o;
            return p.getSound().equals(sound) && p.getIndex() == index;
        }
        return false;
    }

    @Override
    public String toString() {
        String returnString = "[Phoneme]\n";
        returnString += "Sound == " + sound + "\n";
        returnString += "Index == " + index + "\n";
        return returnString;
    }
}
