package net.oijon.oling.datatypes.phonology.surface;

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
import net.oijon.oling.datatypes.phonology.Phonology;

public class Foot implements XMLDatatype {
	
	private Syllable syll1;
	private Syllable syll2;
	private Phonology linkedPhono;
	
	public Foot(Syllable syll1, Phonology p) {
		linkedPhono = p;
		this.syll1 = syll1;
	}
	
	public Foot(Syllable syll1, Syllable syll2, Phonology p) {
		linkedPhono = p;
		this.syll1 = syll1;
		this.syll2 = syll2;
	}
	
	public Foot(Element e, Phonology p) throws InvalidXMLException {
		linkedPhono = p;
		fromXML(e);
	}
	
	public void trochee() {
		// TODO stress first syll, unstress second
	}
	
	public void iambic () {
		// TODO stress second syll, unstress first
	}
	
	public Syllable getLeft() {
		return syll1;
	}
	
	public Syllable getRight() {
		return syll2;
	}
	
	public Syllable[] getSyllables() {
		Syllable[] retArr;
		if (syll2 == null) {
			retArr = new Syllable[1];
			retArr[0] = syll1;
		} else {
			retArr = new Syllable[2];
			retArr[0] = syll1;
			retArr[1] = syll2;
		}
		return retArr;
	}
	
	public static ArrayList<Foot> getFeetFromString(String string, Phonology p) {
		ArrayList<Foot> feet = new ArrayList<>();
		
		ArrayList<Syllable> sylls = Syllable.getSyllablesFromString(string, p);
		ArrayList<Syllable> lefts = new ArrayList<>();
		ArrayList<Syllable> rights = new ArrayList<>();
		for (int i = 0; i < sylls.size(); i++) {
			if (i % 2 == 0) {
				lefts.add(sylls.get(i));
			} else {
				rights.add(sylls.get(i));
			}
		}
		
		// rights will always be smaller than lefts
		for (int i = 0; i < rights.size(); i++) {
			Foot foot = new Foot(lefts.get(i), rights.get(i), p);
			feet.add(foot);
		}
		if (lefts.size() > rights.size()) {
			Foot foot = new Foot(lefts.get(rights.size()), p);
			feet.add(foot);
		}
		
		return feet;
	}
	
	@Override
	public Element toXML() throws ParserConfigurationException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.newDocument();
		Element root = doc.createElement("foot");
		
		Element left = doc.createElement("left");
		left.appendChild(doc.importNode(syll1.toXML(), true));
		root.appendChild(left);
		
		Element right = doc.createElement("right");
		if (syll2 != null) {
			right.appendChild(doc.importNode(syll2.toXML(), true));
		}
		root.appendChild(right);
		
		return root;
	}

	@Override
	public void fromXML(Element e) throws InvalidXMLException {
		if (e.getTagName().equals("foot")) {
			NodeList nl = e.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element childE = (Element) n;
					switch (childE.getTagName()) {
						case "left":
							NodeList leftNL = childE.getChildNodes();
							for (int j = 0; j < leftNL.getLength(); j++) {
								if (leftNL.item(j).getNodeType() == Node.ELEMENT_NODE) {
									Element syllE = (Element) leftNL.item(j);
									syll1 = new Syllable(syllE, linkedPhono);
									break;
								}
							}
							break;
						case "right":
							NodeList rightNL = childE.getChildNodes();
							for (int j = 0; j < rightNL.getLength(); j++) {
								if (rightNL.item(j).getNodeType() == Node.ELEMENT_NODE) {
									Element syllE = (Element) rightNL.item(j);
									syll2 = new Syllable(syllE, linkedPhono);
									break;
								}
							}
							break;
					}
				}
			}
		} else {
			throw new InvalidXMLException("Node name not expected name! Expected: foot; Actual: " + e.getTagName());
		}
	}
	
	@Override
	public String toString() {
		String returnString = "";
		returnString += syll1.toString();
		if (syll2 != null) {
			returnString += "." + syll2.toString();
		}
		return returnString;
	}

}
