package net.oijon.oling.datatypes.phonology.surface;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;
import net.oijon.oling.datatypes.phonology.Phonology;

public class Foot {
	
	private final Syllable syll1;
	private final Syllable syll2;
	
	public Foot(Syllable syll1, Phonology p) {
		this.syll1 = syll1;
		this.syll2 = null;
	}
	
	public Foot(Syllable syll1, Syllable syll2, Phonology p) {
		this.syll1 = syll1;
		this.syll2 = syll2;
	}
	
	public void trochee() {
		// TODO stress first syll, unstress second
	}
	
	public void iambic() {
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
	
	public static ArrayList<Foot> getFeetFromString(String string, Phonology p) {
		ArrayList<Foot> feet = new ArrayList<>();
		
		SyllableString sylls = new SyllableString(p, string);
		ArrayList<Syllable> lefts = new ArrayList<>();
		ArrayList<Syllable> rights = new ArrayList<>();
		for (int i = 0; i < sylls.length(); i++) {
			if (i % 2 == 0) {
				lefts.add(sylls.syllableAt(i));
			} else {
				rights.add(sylls.syllableAt(i));
			}
		}
		
		// rights will always be smaller than lefts
		for (int i = 0; i < rights.size(); i++) {
			Foot foot = new Foot(lefts.get(i), rights.get(i), p);
			feet.add(foot);
		}
		if (lefts.size() > rights.size()) {
			Foot foot = new Foot(lefts.get(rights.size()), p);
			feet.add(foot);
		}
		
		return feet;
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
