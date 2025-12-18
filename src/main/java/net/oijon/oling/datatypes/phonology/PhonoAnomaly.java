package net.oijon.oling.datatypes.phonology;

// last edited: 12/18/25 -N3

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.regex.Pattern;

/**
 * Sometimes, encoding anomalies in Unicode make it such that certain characters have multiple codepoints. This class
 * creates a link between the anomaly and the correct phoneme.
 */
public class PhonoAnomaly implements XMLDatatype {
	private String fromStr;
	private String toStr;

	/**
	 * Creates a pair that links an encoding anomaly to the proper character
	 * @param fromStr The encoding anomaly to link
	 * @param toStr The correct character to link to
	 */
	public PhonoAnomaly(String fromStr, String toStr) {
		this.fromStr = fromStr;
		this.toStr = toStr;
	}

	/**
	 * Creates a pair from an XML element
	 * @param e The element to read
	 * @throws InvalidXMLException Thrown when the XML given is invalid for whatever reason
	 */
	public PhonoAnomaly(Element e) throws InvalidXMLException {
		fromXML(e);
	}

	/**
	 * Gets the anomalous string
	 * @return The anomoly
	 */
	public String getFromStr() {
		return fromStr;
	}

	/**
	 * Gets the correct string
	 * @return The proper string
	 */
	public String getToStr() {
		return toStr;
	}

	/**
	 * Sets the anomoly
	 * @param fromStr The anomoly
	 */
	public void setFromStr(String fromStr) {
		this.fromStr = fromStr;
	}

	/**
	 * Sets the proper string
	 * @param toStr The proper string
	 */
	public void setToStr(String toStr) {
		this.toStr = toStr;
	}

	/**
	 * Changes all instances of the anomaly to the proper phoneme in the given string
	 * @param input A string to normalize
	 * @return The normalized string, with all anomalous codepoints replaced with the proper phoneme string
	 */
	public String normalize(String input) {
		String output = input.replaceAll(Pattern.quote(fromStr), Pattern.quote(toStr));
		return output;
	}

	@Override
	public Element toXML() throws ParserConfigurationException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.newDocument();
		Element root = doc.createElement("anomoly");

		Element fromE = doc.createElement("from");
		fromE.appendChild(doc.createTextNode(fromStr));
		root.appendChild(fromE);

		Element toE = doc.createElement("to");
		toE.appendChild(doc.createTextNode(toStr));
		root.appendChild(toE);

		return root;
	}

	@Override
	public void fromXML(Element e) throws InvalidXMLException {
		if (e.getTagName().equals("anomoly")) {
			NodeList nl = e.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				switch (n.getNodeName()) {
					case "from":
						if (n.getNodeType() == Node.ELEMENT_NODE) {
							fromStr = n.getTextContent();
						}
					case "to":
						if (n.getNodeType() == Node.ELEMENT_NODE) {
							toStr = n.getTextContent();
						}
					default:

				}
			}
		} else {
			throw new InvalidXMLException("Node name not expected name! Expected: anomoly; Actual: " + e.getTagName());
		}
	}
}
