package net.oijon.oling.datatypes.phonology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;
import net.oijon.olog.Log;
import net.oijon.oling.info.Info;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

//last edit: 12/17/25 -N3

/**
 * The sounds of a language. Makes a list of sounds based off a PhonoSystem.
 * @author alex
 *
 */
public class Phonology implements XMLDatatype {

	private List<String> phonoList = new ArrayList<String>(Arrays.asList(" "));
	private PhonoSystem phonoSystem;
	static Log log = Info.log;
	
	/**
	 * Converts a string array of sounds into a phonology
	 * @param array The array to be converted
	 */
	public Phonology(String[] array) {
		this.phonoSystem = PhonoSystem.IPA;
		for (int i = 0; i < array.length; i++) {
			if (phonoSystem.contains(array[i])) {
				phonoList.add(array[i]);
			}
		}
	}
	
	/**
	 * Converts a non-IPA string array into a phonology.
	 * @param array The array to be converted
	 * @param sys The system to be used for this new phonology
	 */
	public Phonology(String[] array, PhonoSystem sys) {
		this.phonoSystem = sys;
		for (int i = 0; i < array.length; i++) {
			if (phonoSystem.contains(array[i])) {
				phonoList.add(array[i]);
			} else if (!array[i].equals(" ") && !array[i].equals("")) {
				log.warn(array[i] + " is not in PhonoSystem " + sys.getName());
			}
		}
	}

    /**
     * Creates a phonology from a given XML element
     * @param e The element to create from
     * @throws InvalidXMLException thrown if the XML tag is the wrong name or is otherwise invalid
     */
    public Phonology(Element e) throws InvalidXMLException {
        fromXML(e);
    }

	/**
	 * Allows the creation of an empty phonology from a file
	 */
	public Phonology() {
		setPhonoSystem(PhonoSystem.IPA);
	}
	
	/**
	 * Copy constructor
	 * @param p The phonology to be copied
	 */
	public Phonology(Phonology p) {
		this.phonoList = new ArrayList<String>(p.phonoList);
		this.phonoSystem = new PhonoSystem(p.phonoSystem);
	}
	
	/**
	 * Gets the phono system attached to the phonology
	 * @return The phonology system attached
	 */
	public PhonoSystem getPhonoSystem() {
		return phonoSystem;
	}
	
	/**
	 * Sets the phono system attached to the phonology.
	 * This is a private method because it should only be used when creating a phonology.
	 * @param phonoSystem
	 */
	private void setPhonoSystem(PhonoSystem phonoSystem) {
		this.phonoSystem = phonoSystem;
	}
	
	/**
	 * Gets the list of all phonemes
	 * @return The list of all phonemes
	 */
	public List<String> getList() {
		return phonoList;
	}
	
	/**
	 * Adds a phoneme to the phonology
	 * @param value The phoneme to be added
	 */
	public void add(String value) {
		if (phonoSystem.contains(value)) {
			phonoList.add(value);
		}
	}
	
	/**
	 * Removes all instances of a phoneme from the phonology
	 * @param value The phoneme to be removed
	 */
	public void removeAll(String value) {
		for (int i = 0; i < phonoList.size(); i++) {
			if (value.equals(phonoList.get(i))) {
				phonoList.remove(i);
			}
		}
	}
	
	@Override
	public String toString() {
		String returnString = "===Phonology Start===\n";
		returnString += "soundlist:";
		for (int i = 0; i < phonoList.size(); i++) {
			returnString += this.getList().get(i) + ",";
		}
		if (returnString.charAt(returnString.length() - 1) == ',') {
			returnString = returnString.substring(0, returnString.length() - 1); // removes final comma
		}
		returnString += "\n";
		returnString += this.phonoSystem.toString() + "\n";
		returnString += "===Phonology End===";
		return returnString;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Phonology) {
			Phonology p = (Phonology) obj;
			if (p.phonoList.equals(phonoList) && p.phonoSystem.equals(phonoSystem)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Clears all sounds in a phonology
	 */
	public void clear() {
		phonoList.clear();
	}

    @Override
    public Element toXML() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("phonology");

        Element sounds = doc.createElement("sounds");
        for (String s : phonoList) {
            Element sound = doc.createElement("sound");
            sound.appendChild(doc.createTextNode(s));
            sound.appendChild(sounds);
        }
        root.appendChild(sounds);

        Element phosys = (Element) doc.importNode(phonoSystem.toXML(), true);
        root.appendChild(phosys);

        return root;
    }

    @Override
    public void fromXML(Element e) throws InvalidXMLException {
        if (e.getTagName().equals("phonology")) {
            NodeList nl = e.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                switch (n.getNodeName()) {
                    case "sounds":
                        NodeList sounds = n.getChildNodes();
                        for (int j = 0; j < sounds.getLength(); j++) {
                            Node sound = sounds.item(j);
                            if (!phonoList.contains(sound.getTextContent())) {
                                this.phonoList.add(sound.getTextContent());
                            }
                        }
                        break;
                    case "tables":
                        if (n.getNodeType() == Node.ELEMENT_NODE) {
                            phonoSystem = new PhonoSystem((Element) n);
                        }
                        break;
                    default:

                }
            }
        } else {
            throw new InvalidXMLException("Node name not expected name! Expected: phonology; Actual: " + e.getTagName());
        }
    }
}
