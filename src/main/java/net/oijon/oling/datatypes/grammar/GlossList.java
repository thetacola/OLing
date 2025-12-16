package net.oijon.oling.datatypes.grammar;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;

//last edit: 12/15/25 -N3

/**
 * Creates a list of Gloss objects, which can be accessed all at once to create
 * templates such as Leipzig.
 * @author alex
 *
 */

public class GlossList extends ArrayList<Gloss> implements XMLDatatype {

	private static final long serialVersionUID = 2940848265098898582L;
	private String name;
	
	/**
	 * Creates an empty GlossList
	 * @param name The name of the GlossList (ex. Leipzig)
	 */
	public GlossList(String name) {
		super();
		this.name = name;
	}
	
	/**
	 * Copy constructor
	 * @param gl The GlossList to copy
	 */
	public GlossList(GlossList gl) {
		super(gl);
		this.name = gl.name;
	}
	
	/**
	 * Gets the name of the GlossList
	 * @return The name of the GlossList
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the GlossList
	 * @param name The name of the GlossList
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		String returnString = "===GlossList Start===\n";
		returnString += "name:" + name + "\n";
		returnString += "===Glosses Start===\n";
		for (int i = 0; i < this.size(); i++) {
			returnString += this.get(i).toString() + "\n";
		}
		returnString += "===Glosses End===\n";
		returnString += "===GlossList End===";
		return returnString;
	}

    @Override
    public Element toXML() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("glosses");
        root.setAttribute("name", this.getName());
        for (Gloss gloss : this) {
            Element e = gloss.toXML();
            root.appendChild(e);
        }

        return root;
    }

    @Override
    public void fromXML(Element e) throws InvalidXMLException {

        if (e.getTagName().equals("glosses")) {
            this.name = e.getAttribute("name");
            NodeList nl = e.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i).getNodeName().equals("gloss") & nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Gloss g = new Gloss((Element) nl.item(i));
                    this.add(g);
                }
            }
        } else {
            throw new InvalidXMLException("Node name not expected name! Expected: glosses; Actual: " + e.getTagName());
        }

    }
}
