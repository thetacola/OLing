package net.oijon.oling.datatypes.phonology.table;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.phonology.feature.FeaturalXMLDatatype;
import net.oijon.oling.datatypes.phonology.feature.Feature;
import net.oijon.oling.datatypes.phonology.feature.FeatureLevel;

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
public class PhonoCell extends FeaturalXMLDatatype {

    //protected ArrayList<Phoneme> phonemes = new ArrayList<>();
    private int index;

    /**
     * Creates a blank cell
     */
    public PhonoCell() {
    	initFeatures();
    }

    /**
     * Creates a blank cell at a given index relative to its PhonoCategory
     * @param index The index of this cell inside its PhonoCategory
     */
    public PhonoCell(int index) {
    	initFeatures();
        this.index = index;
    }

    /**
     * Creates a cell from a given list of phonemes
     * @param phonemes The phonemes to be put into the cell
     * @param index The index of this cell inside its PhonoCategory
     */
    public PhonoCell(ArrayList<Phoneme> phonemes, int index) {
    	initFeatures();
        for (int i = 0; i < phonemes.size(); i++) {
        	super.lowerObj.add(phonemes.get(i));
        }
        this.index = index;
    }

    /**
     * Creates a PhonoCell from an XML element
     * @param e The XML element to use
     * @throws InvalidXMLException Thrown when the XML element given is malformed
     */
    public PhonoCell(Element e) throws InvalidXMLException {
        fromXML(e);
    }

    /**
     * Adds a sound from a string, automatically setting the index to be at the end of the cell
     * @param s The sound to be added
     */
    public void addSound(String s) {
        if (!s.equals("*") && !s.equals("#")) {
            Phoneme p = new Phoneme(s);
            int index = 0;
            for (FeaturalXMLDatatype lp : super.lowerObj) {
            	if (lp instanceof Phoneme) {
            		Phoneme lpp = (Phoneme) lp;
	                if (lpp.getIndex() > index) {
	                    index = lpp.getIndex() + 1;
	                }
            	}
            }
            p.setIndex(index);
            super.lowerObj.add(p);
        }
    }

    /**
     * Adds a sound from a phoneme, automatically checking for and fixing index conflicts
     * @param p The sound to be added
     */
    public void addSound(Phoneme p) {
        for (int i = 0; i < super.lowerObj.size(); i++) {
        	FeaturalXMLDatatype fxd = super.lowerObj.get(i);
        	if (fxd instanceof Phoneme) {
        		Phoneme fxdp = (Phoneme) fxd;
	            if (p.getIndex() == fxdp.getIndex()) {
	                p.setIndex(p.getIndex() + 1);
	                i = 0;
	            }
        	}
        }
        super.lowerObj.add(p);
    }

    /**
     * Gets the amount of phonemes in the cell
     * @return The amount of phonemes in the cell
     */
    public int size() {
        return super.lowerObj.size();
    }

    /**
     * Gets all the phonemes in this cell as an ArrayList
     * @return All of the phonemes in the cell
     */
    public ArrayList<Phoneme> getPhonemes() {
    	ArrayList<Phoneme> alp = new ArrayList<Phoneme>();
    	for (FeaturalXMLDatatype fxd : super.lowerObj) {
    		if (fxd instanceof Phoneme) {
    			alp.add((Phoneme) fxd);
    		}
    	}
        return alp;
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
     * @return The size of the cell without spacer characters
     */
    public int sizeWithoutSpacers() {
        int startSize = size();
        for (int i = 0; i < size(); i++) {
        	FeaturalXMLDatatype fxd = super.lowerObj.get(i);
        	if (fxd instanceof Phoneme) {
        		Phoneme fxdp = (Phoneme) fxd;
	            String sound = fxdp.getSound();
	            if (sound.equals("#") || sound.equals("*")) {
	                startSize--;
	            }
        	}
        }
        return startSize;
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof PhonoCell) {
            PhonoCell pc = (PhonoCell) o;
            return (pc.lowerObj.equals(super.lowerObj));
        }
        return false;
    }

	@Override
	public String toString() {
		return "[" + index + ": " + super.lowerObj.toString() + "]";
	}

    @Override
    public Element toXML() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("cell");
        root.setAttribute("index", index + "");
        
        for (int i = 0; i < features.size(); i++) {
        	Feature f = features.get(i);
        	if (f.getValue() && f.getLevel() == FeatureLevel.CELL) {
        		Element featureElement = doc.createElement("feature");
        		featureElement.setTextContent(features.get(i).getName());
        		root.appendChild(featureElement);
        	}
        }
        
        for (FeaturalXMLDatatype fxd : super.lowerObj) {
        	if (fxd instanceof Phoneme) {
        		Phoneme p = (Phoneme) fxd;
	            Element pe = (Element) doc.importNode(p.toXML(), true);
	            if (!p.getSound().equals("*") && !p.getSound().equals("#")) {
	                root.appendChild(pe);
	            }
        	}
        }

        return root;
    }

    @Override
    public void fromXML(Element e) throws InvalidXMLException {
    	initFeatures();
        if (e.getTagName().equals("cell")) {
            index = Integer.parseInt(e.getAttribute("index"));
            NodeList nl = e.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n.getNodeName().equals("sound") && n.getNodeType() == Node.ELEMENT_NODE) {
                    Phoneme p = new Phoneme((Element) n);
                    super.lowerObj.add(p);
                } else if (n.getNodeName().equals("feature") && n.getNodeType() == Node.ELEMENT_NODE) {
                	String textContent = ((Element) n).getTextContent();
        			Feature f = new Feature(textContent, true, FeatureLevel.CELL);
        			this.addFeature(f);
                }
            }
        } else {
            throw new InvalidXMLException("Node name not expected name! Expected: cell; Actual: " + e.getTagName());
        }
        
        applyFeatures();
    }

	@Override
	protected void initFeatures() {
		super.level = FeatureLevel.CELL;
	}
}
