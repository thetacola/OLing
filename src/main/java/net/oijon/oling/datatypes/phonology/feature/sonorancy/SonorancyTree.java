package net.oijon.oling.datatypes.phonology.feature.sonorancy;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.phonology.feature.Feature;
import net.oijon.oling.datatypes.phonology.table.Phoneme;

public class SonorancyTree{

	SonorancyNode root = new SonorancyNode();
	
	public SonorancyTree() {
		
	}
	
	public SonorancyTree(Element e) throws InvalidXMLException {
		fromXML(e);
	}
	
	public SonorancyTree(Feature feature) {
		root = new SonorancyNode(feature);
	}
	
	public void calcAllValues() {
		ArrayList<SonorancyNode> visited = new ArrayList<>();
		visited.add(root);
		int maxDepth = root.findDeepest(visited);
		visited.clear();
		visited.add(root);
		
		if (root.left != null) {
			root.left.calcValue(0, true, 1, maxDepth, visited);
		}
		if (root.right != null) {
			root.right.calcValue(0, false, 1, maxDepth, visited);
		}
	}
	
	public SonorancyNode getRoot() {
		return root;
	}
	
	public int getPhonemeValue(Phoneme p) {
		return root.getPhonemeValue(p);
	}

	public Element toXML() throws ParserConfigurationException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.newDocument();
		Element root = doc.createElement("sonorancy");
		
		root.appendChild(doc.importNode(this.root.toXML(), true));
		return root;
	}
	
	public void fromXML(Element e) throws InvalidXMLException {
		NodeList nl = e.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element ne = (Element) n;
				if (ne.getTagName().equals("son-feature")) {
					this.root = new SonorancyNode(ne);
				}
			}
		}
		
		calcAllValues();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof SonorancyTree) {
			SonorancyTree st = (SonorancyTree) o;
			if (st.root.equals(root)) {
				return true;
			}
		}
		return false;
	}
}
