package net.oijon.oling.datatypes;

import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;

public interface XMLDatatype {

	public abstract Element toXML() throws ParserConfigurationException;
	public abstract void fromXML(Element e) throws InvalidXMLException;

}
