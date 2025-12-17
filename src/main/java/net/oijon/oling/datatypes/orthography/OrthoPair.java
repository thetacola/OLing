package net.oijon.oling.datatypes.orthography;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class OrthoPair implements Comparable<OrthoPair>, XMLDatatype {

	// Last edit: 12/16/25 ~n3
	
	private String phonemes;
	private String graphemes;
	
	/**
	 * Creates a correlation between a given set of phonemes and a given set of graphemes
	 * @param phonemes The phonemes of this pair
	 * @param graphemes The graphemes of this pair
	 */
	public OrthoPair(String phonemes, String graphemes) {
		this.phonemes = phonemes;
		this.graphemes = graphemes;
	}

    /**
     * Creates a pair of phonemes and graphemes from an XML element
     * @param e The XML element to be used
     */
    public OrthoPair(Element e) throws InvalidXMLException {
        fromXML(e);
    }
	
	/**
	 * Gets the set of phonemes of this pair
	 * @return The phonemes in question
	 */
	public String getPhonemes() {
		return phonemes;
	}
	
	/**
	 * Gets the set of graphemes of this pair
	 * @return The graphemes in question
	 */
	public String getGraphemes() {
		return graphemes;
	}
	
	/**
	 * Sets the set of phonemes of this pair
	 * @param phonemes The new set of phonemes
	 */
	public void setPhonemes(String phonemes) {
		this.phonemes = phonemes;
	}
	
	/**
	 * Sets the set of graphemes of this pair
	 * @param graphemes The new set of graphemes
	 */
	public void setGraphemes(String graphemes) {
		this.graphemes = graphemes;
	}

	@Override
	public int compareTo(OrthoPair o) {
		return phonemes.compareTo(o.getPhonemes());
	}
	
	@Override
	public String toString() {
		return phonemes + ":" + graphemes;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof OrthoPair) {
			OrthoPair op = (OrthoPair) o;
			if (op.getPhonemes().equals(phonemes) && op.getGraphemes().equals(graphemes)) {
				return true;
			}
		}
		return false;
	}

    @Override
    public Element toXML() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("pair");

        Element phonemes = doc.createElement("phonemes");
        phonemes.appendChild(doc.createTextNode(this.phonemes));
        root.appendChild(phonemes);

        Element graphemes = doc.createElement("graphemes");
        graphemes.appendChild(doc.createTextNode(this.graphemes));
        root.appendChild(graphemes);

        return root;
    }

    @Override
    public void fromXML(Element e) throws InvalidXMLException {
        if (e.getTagName().equals("pair")) {
            NodeList nl = e.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                switch (nl.item(i).getNodeName()) {
                    case "phonemes":
                        this.setPhonemes(nl.item(i).getTextContent());
                    case "graphemes":
                        this.setGraphemes(nl.item(i).getTextContent());
                    default:

                }
            }
        } else {
            throw new InvalidXMLException("Node name not expected name! Expected: pair; Actual: " + e.getTagName());
        }
    }
}
