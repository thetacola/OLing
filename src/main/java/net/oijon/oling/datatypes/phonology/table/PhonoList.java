package net.oijon.oling.datatypes.phonology.table;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.phonology.feature.FeaturalXMLDatatype;
import net.oijon.oling.datatypes.phonology.feature.Feature;
import net.oijon.oling.datatypes.phonology.feature.FeatureLevel;
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

	public PhonoList() {
		super();
		initFeatures();
	}

	public PhonoList(String name) {
		super();
		this.name = name;
		initFeatures();
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
		initFeatures();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.newDocument();
		Element root = doc.createElement("list");
		root.setAttribute("name", name);
		
		for (Feature f : super.features.values()) {
			Element fe = (Element) doc.importNode(f.toXML(), true);
			root.appendChild(fe);
		}
		
		for (FeaturalXMLDatatype fxd : super.lowerObj) {
			if (fxd instanceof Phoneme) {
				Phoneme p = (Phoneme) fxd;
				Element pe = (Element) doc.importNode(p.toXML(), true);
				root.appendChild(pe);
			}
		}

		return root;
	}

	@Override
	public void fromXML(Element e) throws InvalidXMLException {
		initFeatures();
		if (e.getTagName().equals("list")) {
			name = e.getAttribute("name");
			String attrPart = e.getAttribute("part");
		    switch (attrPart) {
		    	case "": 
		    	case "NONE":
		    		break;
		    	case "ONSET":
		    		this.features.put("SYLLPART_ONSET", new Feature("SYLLPART_ONSET", true, FeatureLevel.TABLE));
		    		break;
		    	case "NUCLEUS":
		    		this.features.put("SYLLPART_NUCLEUS", new Feature("SYLLPART_NUCLEUS", true, FeatureLevel.TABLE));
		    		break;
		    	case "CODA":
		    		this.features.put("SYLLPART_CODA", new Feature("SYLLPART_CODA", true, FeatureLevel.TABLE));
		    		break;
		    	case "ONSET_NUCLEUS":
		    		this.features.put("SYLLPART_ONSET", new Feature("SYLLPART_ONSET", true, FeatureLevel.TABLE));
		    		this.features.put("SYLLPART_NUCLEUS", new Feature("SYLLPART_NUCLEUS", true, FeatureLevel.TABLE));
		    		break;
		    	case "NUCLEUS_CODA":
		    		this.features.put("SYLLPART_NUCLEUS", new Feature("SYLLPART_NUCLEUS", true, FeatureLevel.TABLE));
		    		this.features.put("SYLLPART_CODA", new Feature("SYLLPART_CODA", true, FeatureLevel.TABLE));
		    		break;
		    	case "ONSET_CODA":
		    		this.features.put("SYLLPART_ONSET", new Feature("SYLLPART_ONSET", true, FeatureLevel.TABLE));
		    		this.features.put("SYLLPART_CODA", new Feature("SYLLPART_CODA", true, FeatureLevel.TABLE));
		    		break;
		    	case "ANY":
		    		this.features.put("SYLLPART_ONSET", new Feature("SYLLPART_ONSET", true, FeatureLevel.TABLE));
		    		this.features.put("SYLLPART_NUCLEUS", new Feature("SYLLPART_NUCLEUS", true, FeatureLevel.TABLE));
		    		this.features.put("SYLLPART_CODA", new Feature("SYLLPART_CODA", true, FeatureLevel.TABLE));
		    		break;
		    	default:
		    		log.warn("Found attribute for syllable part on list " + name + 
		    				", though matched with no known value! Given: " + attrPart);
		    }
			NodeList nl = e.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if (n.getNodeName().equals("sound") && n.getNodeType() == Node.ELEMENT_NODE) {
					Phoneme p = new Phoneme((Element) n);
					super.lowerObj.add(p);
				} else if (n.getNodeName().equals("feature") && n.getNodeType() == Node.ELEMENT_NODE) {
					Feature f = new Feature((Element) n, level);
					super.features.put(f.getName(), f);
				}
			}
		} else {
			throw new InvalidXMLException("Node name not expected name! Expected: list; Actual: " + e.getTagName());
		}
	}
	
	@Override
	protected void initFeatures() {
		super.level = FeatureLevel.TABLE;
	}

}
