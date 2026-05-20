package net.oijon.oling.datatypes.phonology.table;

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
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("column");
        root.setAttribute("index", index + "");
        
        Element nameE = doc.createElement("name");
        nameE.setTextContent(name);
        root.appendChild(nameE);
        
        for (Feature f : super.features) {
        	Element featureE = doc.createElement("feature");
        	featureE.setTextContent(f.getName());
        	root.appendChild(featureE);
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
                	String textContent = ((Element) n).getTextContent();
        			Feature f = new Feature(textContent, true, FeatureLevel.COLUMN);
        			this.addFeature(f);
                }
            }
        } else {
            throw new InvalidXMLException("Node name not expected name! Expected: row; Actual: " + e.getTagName());
        }
        
        applyFeatures();
	}

	@Override
	protected void initFeatures() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String toString() {
		String str = "[" + index + ": " + name + ", [";
		for (Feature f : features) {
			str += f.toString();
		}
		str+= "]]";
		return str;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PhonoColumn) {
			PhonoColumn pc = (PhonoColumn) o;
			if (pc.name.equals(name) && pc.index == index) {
				boolean oneNotFound = false;
				for (int i = 0; i < this.features.size(); i++) {
					boolean found = false;
					for (int j = 0; j < pc.features.size(); j++) {
						if (features.get(i).equals(pc.features.get(j))) {
							found = true;
							break;
						}
					}
					if (!found) {
						oneNotFound = true;
						break;
					}
				}
				return !oneNotFound;
			}
		}
		return false;
	}

}
