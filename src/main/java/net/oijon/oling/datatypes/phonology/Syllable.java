package net.oijon.oling.datatypes.phonology;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;

public class Syllable implements XMLDatatype {

	private ArrayList<Sound> onset;
	private ArrayList<Sound> nucleus;
	private ArrayList<Sound> coda;
	private Phonology linkedPhono;

	// TODO: add various levels of stress

	private int nucleusWeight = 0;
	private int codaWeight = 0;

	public int getMoraicWeight() {
		return nucleusWeight + codaWeight;
	}

	public ArrayList<Sound> getOnset() {
		return onset;
	}

	public void setOnset(ArrayList<Sound> onset) {
		this.onset = onset;
	}

	public ArrayList<Sound> getNucleus() {
		return nucleus;
	}

	public void setNucleus(ArrayList<Sound> nucleus) {
		this.nucleus = nucleus;
		nucleusWeight = nucleus.size();
	}

	public ArrayList<Sound> getCoda() {
		return coda;
	}

	public void setCoda(ArrayList<Sound> coda) {
		this.coda = coda;
		codaWeight = coda.size();
	}

	@Override
	public String toString() {
		String returnString = "";
		for (int i = 0; i < onset.size(); i++) {
			returnString += onset.get(i).toString();
		}
		for (int i = 0; i < nucleus.size(); i++) {
			returnString += nucleus.get(i).toString();
		}
		for (int i = 0; i < coda.size(); i++) {
			returnString += coda.get(i).toString();
		}
		return returnString;
	}

	@Override
	public Element toXML() throws ParserConfigurationException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.newDocument();
		Element root = doc.createElement("syllable");

		Element onsetE = doc.createElement("onset");
		for (int i = 0; i < onset.size(); i++) {
			onsetE.appendChild(doc.importNode(onset.get(i).toXML(), true));
		}
		root.appendChild(onsetE);

		Element nucleusE = doc.createElement("nucleus");
		for (int i = 0; i < nucleus.size(); i++) {
			nucleusE.appendChild(doc.importNode(nucleus.get(i).toXML(), true));
		}
		root.appendChild(nucleusE);

		Element codaE = doc.createElement("coda");
		for (int i = 0; i < coda.size(); i++) {
			codaE.appendChild(doc.importNode(coda.get(i).toXML(), true));
		}
		root.appendChild(codaE);

		return root;
	}

	@Override
	public void fromXML(Element e) throws InvalidXMLException {
		if (e.getTagName().equals("syllable")) {
			NodeList nl = e.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element childE = (Element) n;
					switch (childE.getTagName()) {
						case "onset":
							NodeList onsetNL = childE.getChildNodes();
							for (int j = 0; j < onsetNL.getLength(); j++) {
								if (onsetNL.item(j).getNodeType() == Node.ELEMENT_NODE) {
									Element soundE = (Element) onsetNL.item(j);
									Sound s = new Sound(soundE, linkedPhono);
									this.onset.add(s);
								}
							}
							break;
						case "nucleus":
							NodeList nucleusNL = childE.getChildNodes();
							for (int j = 0; j < nucleusNL.getLength(); j++) {
								if (nucleusNL.item(j).getNodeType() == Node.ELEMENT_NODE) {
									Element soundE = (Element) nucleusNL.item(j);
									Sound s = new Sound(soundE, linkedPhono);
									this.nucleus.add(s);
								}
							}
							break;
						case "coda":
							NodeList codaNL = childE.getChildNodes();
							for (int j = 0; j < codaNL.getLength(); j++) {
								if (codaNL.item(j).getNodeType() == Node.ELEMENT_NODE) {
									Element soundE = (Element) codaNL.item(j);
									Sound s = new Sound(soundE, linkedPhono);
									this.coda.add(s);
								}
							}
							break;
					}
				}
			}
		} else {
			throw new InvalidXMLException("Node name not expected name! Expected: sound; Actual: " + e.getTagName());
		}
	}
}
