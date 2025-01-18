package net.oijon.oling.datatypes;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.oijon.oling.info.Info;
import net.oijon.olog.Log;

/**
 * Used to prevent repeating code throughout every single datatype
 */
public abstract class XMLComponent {

	protected Element element;
	public Log log = Info.log;
	
	/**
	 * Creates an empty document to use for this component.
	 */
	public XMLComponent() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			element = db.newDocument().getDocumentElement();
		} catch (ParserConfigurationException e) {
			log.critical("Unable to parse empty file (‽) - " + e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates the component via an XML node. Useful for parsing via document.
	 * @param element The element to parse the datatype from
	 */
	public XMLComponent(Element element) {
		this.element = element;
		parseFromElement();
	}
	
	/**
	 * Gets the node of the object. Useful for writing to files.
	 * @return
	 */
	public Element getElement() {
		return element;
	}
	
	protected String getSingleChildElementValue(String elementName, Element parent) {
		String returnString = "";
		
		NodeList list = parent.getElementsByTagName(elementName);
		
		// check if the list is the proper length (1), print a warning otherwise
		if (list.getLength() < 1) {
			log.warn("No child elements named " + elementName + "  of parent " + parent.getTagName()
				+ " found! Returning blank string...");
		} else if (list.getLength() > 1) {
			log.warn(list.getLength() + " child elements named " + elementName + "  of parent " + parent.getTagName()
			+ " found! Returning first result...");
			returnString = list.item(0).getNodeValue();
		} else {
			returnString = list.item(0).getNodeValue();
		}
		
		return returnString;
	}
	
	protected String[] getMultiChildElementValue(String elementName, Element parent) {
		NodeList list = parent.getElementsByTagName(elementName);
		String[] returnArr = new String[list.getLength()];
		
		for (int i = 0; i < returnArr.length; i++) {
			returnArr[i] = list.item(i).getNodeValue();
		}
		return returnArr;
	}
	
	/**
	 * Updates the node with any changes made via OLing
	 */
	protected abstract void updateElement();
	/**
	 * Sets the field of each variable from the node
	 */
	protected abstract void parseFromElement();
}
