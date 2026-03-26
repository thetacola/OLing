package net.oijon.oling.datatypes.grammar;

//last edit: 12/15/25 -N3

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Attaches a meaning to a gloss abbreviation, allowing users to create their
 * own, or see what a certain abbreviation means.
 * 
 * @author alex
 *
 */
public class Gloss implements XMLDatatype {

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
     * Creates a Gloss from an XML element
     * @param e The element to use
     * @throws InvalidXMLException Thrown when the XML element given is malformed
     */
    public Gloss(Element e) throws InvalidXMLException {
        this.fromXML(e);
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
	public Element toXML() throws ParserConfigurationException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.newDocument();
		Element root = doc.createElement("gloss");

		Element abb = doc.createElement("abb");
		abb.appendChild(doc.createTextNode(this.abbreviation));
		root.appendChild(abb);

		Element meaning = doc.createElement("meaning");
		meaning.appendChild(doc.createTextNode(this.meaning));
		root.appendChild(meaning);

		return root;
	}

	@Override
	public void fromXML(Element e) throws InvalidXMLException {
        if (e.getTagName().equals("gloss")) {
            NodeList nl = e.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                // Note that this doesn't throw an exception if it can't find any of the elements.
                // This is intentional, as they should just be null if that's the case.
                switch (nl.item(i).getNodeName()) {
                    case "abb":
                        this.setAbbreviation(nl.item(i).getTextContent());
                        break;
                    case "meaning":
                        this.setMeaning(nl.item(i).getTextContent());
                        break;
                    default:

                }
            }
        } else {
            throw new InvalidXMLException("Node name not expected name! Expected: gloss; Actual: " + e.getTagName());
        }
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
            return g.getAbbreviation().equals(abbreviation) && g.getMeaning().equals(meaning);
		}
		return false;
	}

}
