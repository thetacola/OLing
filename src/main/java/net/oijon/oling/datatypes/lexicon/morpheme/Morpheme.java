package net.oijon.oling.datatypes.lexicon.morpheme;

import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;
import net.oijon.oling.datatypes.phonology.Phonology;
import net.oijon.oling.datatypes.phonology.surface.Sound;
import net.oijon.oling.datatypes.phonology.surface.SyllableString;

/**
 * The atomic base of words. These base units cannot be broken into children. For those that can,
 * take a look at MorphemeWord
 * @see Lexeme
 * @author alex
 */
public class Morpheme implements XMLDatatype {

	private SyllableString sounds;
	private String meaning = "";
	
	public Morpheme(SyllableString sounds, String meaning) {
		this.sounds = sounds;
		this.meaning = meaning;
	}
	
	public Morpheme(Phonology p, String sounds, String meaning) {
		this.sounds = new SyllableString(p, sounds);
	}

	public Morpheme(Element e) throws InvalidXMLException {
		fromXML(e);
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
	
}
