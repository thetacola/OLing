package net.oijon.oling.datatypes.phonology;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;

public class Foot implements XMLDatatype {
	
	private Syllable syll1;
	private Syllable syll2;
	
	public Foot(Syllable syll1) {
		this.syll1 = syll1;
	}
	
	public Foot(Syllable syll1, Syllable syll2) {
		this.syll1 = syll1;
		this.syll2 = syll2;
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
	
	@Override
	public Element toXML() throws ParserConfigurationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fromXML(Element e) throws InvalidXMLException {
		// TODO Auto-generated method stub
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
