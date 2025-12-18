package net.oijon.oling.datatypes.phonology;

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

/**
 * Creates the equivalent of a cell in the IPA chart
 */
public class PhonoCell implements XMLDatatype {

    protected ArrayList<Phoneme> phonemes;
    private int index;

    /**
     * Creates a blank cell
     */
    public PhonoCell() {
        phonemes = new ArrayList<Phoneme>();
    }

    /**
     * Creates a blank cell at a given index relative to its PhonoCategory
     * @param index The index of this cell inside its PhonoCategory
     */
    public PhonoCell(int index) {
        phonemes = new ArrayList<Phoneme>();
        this.index = index;
    }

    /**
     * Creates a cell from a given list of phonemes
     * @param phonemes The phonemes to be put into the cell
     * @param index The index of this cell inside its PhonoCategory
     */
    public PhonoCell(ArrayList<Phoneme> phonemes, int index) {
        this.phonemes = phonemes;
        this.index = index;
    }

    /**
     * Creates a PhonoCell from an XML element
     * @param e The XML element to use
     */
    public PhonoCell(Element e) throws InvalidXMLException {
        fromXML(e);
    }

    /**
     * Adds a sound from a string, automatically setting the index to be at the end of the cell
     * @param s The sound to be added
     */
    public void addSound(String s) {
        Phoneme p = new Phoneme(s);
        int index = 0;
        for (Phoneme lp : phonemes) {
            if (lp.getIndex() > index) {
                index = lp.getIndex() + 1;
            }
        }
        p.setIndex(index);
        phonemes.add(p);
    }

    /**
     * Adds a sound from a phoneme, automatically checking for and fixing index conflicts
     * @param p The sound to be added
     */
    public void addSound(Phoneme p) {
        for (int i = 0; i < phonemes.size(); i++) {
            if (p.getIndex() == phonemes.get(i).getIndex()) {
                p.setIndex(p.getIndex() + 1);
                i = 0;
            }
        }
        phonemes.add(p);
    }

    /**
     * Gets the amount of phonemes in the cell
     * @return The amount of phonemes in the cell
     */
    public int size() {
        return phonemes.size();
    }

    /**
     * Gets all the phonemes in this cell as an ArrayList
     * @return All of the phonemes in the cell
     */
    public ArrayList<Phoneme> getPhonemes() {
        return phonemes;
    }

    /**
     * Gets the index of the cell relative to the PhonoCategory
     * @return The index of the cell in the PhonoCategory
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the index of the cell relative to the PhonoCategory
     * @param index The index of the cell in the PhonoCategory
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * For the legacy parser, spacers are required. However, spacers
     * really, really should not be in the XML. This gets the size without
     * spacer chars, to see if this really should be added to the XML at all.
     */
    public int sizeWithoutSpacers() {
        int startSize = size();
        for (int i = 0; i < size(); i++) {
            String sound = phonemes.get(i).getSound();
            if (sound.equals("#") || sound.equals("*")) {
                startSize--;
            }
        }
        return startSize;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PhonoCell) {
            PhonoCell pc = (PhonoCell) o;
            return (pc.getPhonemes().equals(phonemes) && pc.getIndex() == index);
        }
        return false;
    }

    @Override
    public Element toXML() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("cell");
        root.setAttribute("index", index + "");
        for (Phoneme p : phonemes) {
            Element pe = p.toXML();
            root.appendChild(pe);
        }

        return root;
    }

    @Override
    public void fromXML(Element e) throws InvalidXMLException {
        if (e.getTagName().equals("cell")) {
            index = Integer.parseInt(e.getAttribute("index"));
            NodeList nl = e.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n.getNodeName().equals("sound") && n.getNodeType() == Node.ELEMENT_NODE) {
                    Phoneme p = new Phoneme((Element) n);
                    phonemes.add(p);
                }
            }
        } else {
            throw new InvalidXMLException("Node name not expected name! Expected: cell; Actual: " + e.getTagName());
        }
    }
}
