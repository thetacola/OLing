package net.oijon.oling.datatypes.phonology;

import net.oijon.oling.datatypes.InvalidXMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

// last edited: 12/18/25 -N3

/**
 * Lists out sounds that exist in a phono system, though may not particularly fit well in a table
 */
public class PhonoList extends PhonoCell {

	private String name;

	public PhonoList() {
		super();
	}

	public PhonoList(String name) {
		super();
		this.name = name;
	}

	public PhonoList(Element e) throws InvalidXMLException {
		fromXML(e);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Element toXML() throws ParserConfigurationException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.newDocument();
		Element root = doc.createElement("list");
		root.setAttribute("name", name);
		for (Phoneme p : phonemes) {
			Element pe = p.toXML();
			root.appendChild(pe);
		}

		return root;
	}

	@Override
	public void fromXML(Element e) throws InvalidXMLException {
		if (e.getTagName().equals("list")) {
			name = e.getAttribute("name");
			NodeList nl = e.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if (n.getNodeName().equals("sound") && n.getNodeType() == Node.ELEMENT_NODE) {
					Phoneme p = new Phoneme((Element) n);
					phonemes.add(p);
				}
			}
		} else {
			throw new InvalidXMLException("Node name not expected name! Expected: list; Actual: " + e.getTagName());
		}
	}

}
