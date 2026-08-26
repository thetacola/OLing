package net.oijon.oling.datatypes.lexicon.morpheme;

import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;
import net.oijon.oling.datatypes.phonology.Phonology;
import net.oijon.oling.datatypes.phonology.Sound;

/**
 * The atomic base of words. These base units cannot be broken into children. For those that can,
 * take a look at Lexeme
 * @see Lexeme
 * @author alex
 */
public class Morpheme implements XMLDatatype {

	private ArrayList<Sound> sounds = new ArrayList<>();
	private MorphemeType type;
	private String meaning = "";
	
	public Morpheme(ArrayList<Sound> sounds, String meaning, MorphemeType type) {
		this.sounds.addAll(sounds);
		this.meaning = meaning;
		this.type = type;
	}
	
	public Morpheme(Phonology p, String sounds, String meaning, MorphemeType type) {
		this.sounds = Sound.getSoundsFromString(sounds, p);
		this.meaning = meaning;
		this.type = type;
	}

	public Morpheme(Element e) throws InvalidXMLException {
		fromXML(e);
	}
	
	public MorphemeType getType() {
		return type;
	}
	
	public String getSoundsAsString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sounds.size(); i++) {
			sb.append(sounds.get(i));
		}
		return sb.toString();
	}
	
	public ArrayList<Sound> getSounds() {
		ArrayList<Sound> retList = new ArrayList<Sound>();
		for (int i = 0; i < sounds.size(); i++) {
			retList.add(sounds.get(i));
		}
		return retList;
	}
	
	public void setSounds(ArrayList<Sound> sounds) {
		
	}
	
	public void setType(MorphemeType type) {
		this.type = type;
	}
	
	public boolean isBound() {
		return type.isBound();
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
