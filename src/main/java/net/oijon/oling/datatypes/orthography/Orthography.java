package net.oijon.oling.datatypes.orthography;

import java.util.ArrayList;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;
import net.oijon.olog.Log;

import net.oijon.oling.datatypes.phonology.Phonology;
import net.oijon.oling.info.Info;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

//last edit: 12/16/25 -N3

/**
 * The writing system of a language. Connects phonemes to graphemes, allowing
 * a user to convert between phonetic transcription and standard writing system.
 * @author alex
 *
 */
public class Orthography implements XMLDatatype {

	private Phonology ph = new Phonology();
	private ArrayList<OrthoPair> orthoList = new ArrayList<OrthoPair>();
	
	static Log log = Info.log;
	
	// TODO: allow ortho rules to have exceptions
	
	/**
	 * Creates an empty orthography
	 */
	public Orthography() {
		
	}
	
	/**
	 * Creates an orthography with a set phonology
	 * @param ph The phonology to be used
	 */
	public Orthography(Phonology ph) {
		this.ph = ph;
	}

    public Orthography(Element e) throws InvalidXMLException {
        fromXML(e);
    }

	/**
	 * Copy constructor
	 * @param o The orthography to copy
	 */
	public Orthography(Orthography o) {
		this.ph = new Phonology(o.ph);
		this.orthoList = new ArrayList<OrthoPair>(o.orthoList);
	}
	
	/**
	 * Adds an orthopair with given constituents
	 * @param phonemes The phoneme(s) for the grapheme(s)
	 * @param graphemes The grapheme(s) for the phoneme(s)
	 */
	public void add(String phonemes, String graphemes) {
		// TODO: check if phonemes are actually in phonology
		orthoList.add(new OrthoPair(phonemes, graphemes));
		sortOrthoList();
	}
	
	/**
	 * Gets the phonology used
	 * @return The phonology in question
	 */
	public Phonology getPhono() {
		return ph;
	}
	
	/**
	 * Sets a new phonology for the orthography to use
	 * @param p The new phonology to use
	 */
	public void setPhono(Phonology p) {
		this.ph = p;
	}
	
	/**
	 * Sorts the list
	 */
	private void sortOrthoList() {
		for (int i = 1; i < orthoList.size() - 1; i++) {
			if (orthoList.get(i).compareTo(orthoList.get(i + 1)) == 1) {
				OrthoPair temp = orthoList.get(i + 1);
				orthoList.set(i + 1, orthoList.get(i));
				orthoList.set(i, temp);
				sortOrthoList();
			}
		}
	}
	
	/**
	 * Gets a pair at the given index
	 * @param i The index of the pair in the orthography
	 * @return The pair specified
	 */
	public OrthoPair getPair(int i) {
		return orthoList.get(i);
	}
	
	/**
	 * Removes a pair from an orthography based on index
	 * @param i The index of the pair to be removed
	 */
	public void remove(int i) {
		orthoList.remove(i);
	}
	
	public String toString() {
		sortOrthoList();
		String returnString = "===Orthography Start===\n";
		for (int i = 0; i < orthoList.size(); i++) {
			returnString += orthoList.get(i).toString() + "\n";
		}
		returnString += "===Orthography End===";
		
		
		return returnString;
	}
	
	/**
	 * Gets the size of the Orthography
	 * @return The amount of orthography pairs
	 */
	public int size() {
		return orthoList.size();
	}
	
	/**
	 * Checks if two ortholists are equal. Note that this is order-dependent.
	 * @param o The orthography to check
	 * @return true if equal, false otherwise
	 */
	private boolean orthoListEqual(Orthography o) {
		for (int i = 0; i < orthoList.size(); i++) {
			if (!orthoList.get(i).equals(o.getPair(i))) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Orthography) {
			Orthography o = (Orthography) obj;
			return orthoListEqual(o);
		}
		return false;
	}

    @Override
    public Element toXML() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("orthography");

        for (OrthoPair op : orthoList) {
            Element pair = (Element) doc.importNode(op.toXML(), true);
            root.appendChild(pair);
        }

        return root;
    }

    @Override
    public void fromXML(Element e) throws InvalidXMLException {
        if (e.getTagName().equals("orthography")) {
            NodeList nl = e.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i).getNodeName().equals("pair") && nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    OrthoPair op = new OrthoPair((Element) nl.item(i));
                    orthoList.add(op);
                }
            }
        } else {
            throw new InvalidXMLException("Node name not expected name! Expected: orthography; Actual: " + e.getTagName());
        }
    }
}
