package net.oijon.oling.datatypes.phonology;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.info.Info;
import net.oijon.olog.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

// last edited: 5/3/26 -N3

/**
 * Lists out sounds that exist in a phono system, though may not particularly fit well in a table
 */
public class PhonoList extends PhonoCell {

	private static Log log = Info.log;
	private String name;
	private SyllablePart part;

	public PhonoList() {
		super();
	}

	public PhonoList(String name) {
		super();
		this.name = name;
		this.part = SyllablePart.ANY;
	}
	
	public PhonoList(String name, SyllablePart part) {
		this(name);
		this.part = part;
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
		root.setAttribute("part", part.name());
		for (Phoneme p : phonemes) {
			Element pe = (Element) doc.importNode(p.toXML(), true);
			root.appendChild(pe);
		}

		return root;
	}

	@Override
	public void fromXML(Element e) throws InvalidXMLException {
		if (e.getTagName().equals("list")) {
			name = e.getAttribute("name");
			try {
		    	part = SyllablePart.valueOf(e.getAttribute("part"));
		    } catch (NullPointerException e1) {
		    	log.warn("No syllable part specified for phono list " + name +
		    			". Defaulting to any.");
		    	part = SyllablePart.ANY;
		    } catch (IllegalArgumentException e1) {
		    	log.err("Given syllable part on list " + name + " not valid! Got: \"" 
		    			+ e.getAttribute("part") + "\". Defaulting to any.");
		    	part = SyllablePart.ANY;
		    }
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
