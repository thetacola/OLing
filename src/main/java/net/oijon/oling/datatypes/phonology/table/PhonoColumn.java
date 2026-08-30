package net.oijon.oling.datatypes.phonology.table;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.phonology.feature.FeaturalXMLDatatype;
import net.oijon.oling.datatypes.phonology.feature.Feature;
import net.oijon.oling.datatypes.phonology.feature.FeatureLevel;

/**
 * Acts as a column in a phonotable. Note that this does not contain the actual data inside
 * the column (ie. the phonemes themselves), but instead contains information about the given
 * column that is then applied based on the index of the cells. To get the cells, go through
 * the rows.
 */
public class PhonoColumn extends FeaturalXMLDatatype {
	
	private String name;
	private int index;
	
	public PhonoColumn(String name, int index) {
		this.name = name;
		this.index = index;
	}
	
	public PhonoColumn(Element column) throws InvalidXMLException {
		fromXML(column);
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public Element toXML() throws ParserConfigurationException {
		initFeatures();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("column");
        root.setAttribute("index", index + "");
        
        Element nameE = doc.createElement("name");
        nameE.setTextContent(name);
        root.appendChild(nameE);
        
        for (Feature f : super.features.values()) {
        	if (f.getValue() && f.getLevel() == level) {
        		Element fe = (Element) doc.importNode(f.toXML(), true);
        		root.appendChild(fe);
        	}
        }
        
		return root;
	}

	@Override
	public void fromXML(Element e) throws InvalidXMLException {
		initFeatures();
        if (e.getTagName().equals("column")) {
            index = Integer.parseInt(e.getAttribute("index"));
            NodeList nl = e.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n.getNodeName().equals("name") && n.getNodeType() == Node.ELEMENT_NODE) {
                    this.name = n.getTextContent();
                } else if (n.getNodeName().equals("feature") && n.getNodeType() == Node.ELEMENT_NODE) {
                	Feature f = new Feature((Element) n, level);
        			this.addFeature(f);
                }
            }
        } else {
            throw new InvalidXMLException("Node name not expected name! Expected: column; Actual: " + e.getTagName());
        }
        
        applyFeatures();
	}

	@Override
	protected void initFeatures() {
		this.level = FeatureLevel.COLUMN;
	}
	
	@Override
	public String toString() {
		String str = "[" + index + ": " + name + ", [";
		for (Feature f : features.values()) {
			str += f.toString();
		}
		str+= "]]";
		return str;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PhonoColumn) {
			PhonoColumn pc = (PhonoColumn) o;
			if (pc.name.equals(name) && pc.index == index && pc.features.size() == features.size()) {
				ArrayList<String> keys = new ArrayList<>(features.keySet());
				for (String key : keys) {
					if (!features.get(key).equals(pc.features.get(key))) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

}
