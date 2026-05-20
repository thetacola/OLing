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

//last edit: 5/20/26 -N3

/**
 * Creates the equivalent of a row on the IPA chart.
 * @author alex
 *
 */
public class PhonoCategory extends FeaturalXMLDatatype {

	private String name;
    private int index;

    /**
     * Creates a phono category at a given index inside the table for already created list
     * @param name The name of the category
     * @param cells a preëxisting ArrayList of each cell
     * @param index the index of the row in reference to the table it is a part of
     */
    public PhonoCategory(String name, ArrayList<PhonoCell> cells, int index) {
    	initFeatures();
        this.name = name;
        for (PhonoCell pc : cells) {
        	super.lowerObj.add(pc);
        }
        this.index = index;
    }

	/**
	 * Creates phono category for already created list
	 * @param name the name of the category
	 * @param cells a pre-existing ArrayList of each cell
	 */
	public PhonoCategory(String name, ArrayList<PhonoCell> cells) {
		initFeatures();
		this.name = name;
		for (PhonoCell c : cells) {
			super.lowerObj.add(c);
		}
        this.index = 0;
	}
	
	/**
	 * Creates phono category for as-of-yet created list
	 * @param name the name of the category
	 */
	public PhonoCategory(String name) {
		initFeatures();
		this.name = name;
        this.index = 0;
	}

	/**
	 * Creates a phono category from an XML node
	 * @param e The XML element of the row
	 * @throws InvalidXMLException when the given XML is invalid
	 */
	public PhonoCategory(Element e) throws InvalidXMLException {
		fromXML(e);
	}

	/**
	 * Copy constructor
	 * @param pc The PhonoCategory to be copied
	 */
	public PhonoCategory(PhonoCategory pc) {
		initFeatures();
		this.name = pc.name;
        this.index = pc.index;
        super.lowerObj = pc.lowerObj;
	}
	
	/**
	 * Gets list of all cells in category
	 * @return all cells in category
	 */
	public ArrayList<PhonoCell> getCells() {
		ArrayList<PhonoCell> cells = new ArrayList<>();
		
		for (FeaturalXMLDatatype fxd : super.lowerObj) {
			if (fxd instanceof PhonoCell) {
				PhonoCell c = (PhonoCell) fxd;
				cells.add(c);
			}
		}
		
		return cells;
	}
	
	/**
	 * Gets category name
	 * @return category name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets cell at index i
	 * @param i index
	 * @return cell
	 */
	public PhonoCell getCell(int i) {
		FeaturalXMLDatatype fxd = super.lowerObj.get(i);
		if (fxd instanceof PhonoCell) {
			return (PhonoCell) fxd;
		} else {
			// This should never happen, but just in case...
			return null;
		}
	}
	
	/**
	 * Deletes cell
	 * @param i index of cell to be deleted
	 */
	public void removeCell(int i) {
		super.lowerObj.remove(i);
	}
	
	/**
	 * Adds cell to end of list
	 * @param phonoCell the cell to be added
	 */
	public void addCell(PhonoCell phonoCell) {
        for (int i = 0; i < super.lowerObj.size(); i++) {
        	FeaturalXMLDatatype fxd = super.lowerObj.get(i);
        	if (fxd instanceof PhonoCell) {
        		PhonoCell pc = (PhonoCell) fxd;
	            if (phonoCell.getIndex() == pc.getIndex()) {
	                phonoCell.setIndex(phonoCell.getIndex() + 1);
	                i = 0;
	            }
        	}
        }
        super.lowerObj.add(phonoCell);
	}

    /**
     * Gets the index relative to the table the PhonoCategory is a part of
     * @return The index in question
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the index of the PhonoCategory inside the PhonoTable
     * @param index The index to be used
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
	 * Returns the amount of cells in a phono category
	 * @return The amount of cells
	 */
	public int size() {
		return super.lowerObj.size();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PhonoCategory) {
			PhonoCategory p = (PhonoCategory) obj;
            return p.name.equals(name) && p.lowerObj.equals(lowerObj);
		}
		return false;
	}

    @Override
    public Element toXML() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("row");
        root.setAttribute("name", name);
        root.setAttribute("index", index + "");
        
        for (int i = 0; i < features.size(); i++) {
        	Feature f = features.get(i);
        	if (f.getValue() && f.getLevel() == FeatureLevel.ROW) {
        		Element featureElement = doc.createElement("feature");
        		featureElement.setTextContent(features.get(i).getName());
        		root.appendChild(featureElement);
        	}
        }
        
        for (FeaturalXMLDatatype fxd : super.lowerObj) {
            // Assuming here that there's no row of *all* spacers, because why in the world would you do that
            // Of course, this is not a guarantee, so this may make unneeded rows if that truly is the case.
            // This is such an edge case with such a trivial result that I *really* don't feel like fixing this
            // Just to clarify: This edge case only happens if the user has done something profoundly odd and
            // unintuitive with zero benefit (unless you count a few extra millis when parsing a benefit?!).
            // If this edge case does appear, there is a 99.9% chance the user has done something wrong and has
            // manually edited config files to add a completely useless spacer row.
        	if (fxd instanceof PhonoCell) {
        		PhonoCell pc = (PhonoCell) fxd;
	            if (pc.sizeWithoutSpacers() != 0 || pc.size() == 0) {
	                Element pe = (Element) doc.importNode(pc.toXML(), true);
	                root.appendChild(pe);
	            }
        	}
        }

        return root;
    }

    @Override
    public void fromXML(Element e) throws InvalidXMLException {
    	initFeatures();
        if (e.getTagName().equals("row")) {
            index = Integer.parseInt(e.getAttribute("index"));
            name = e.getAttribute("name");
            NodeList nl = e.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n.getNodeName().equals("cell") && n.getNodeType() == Node.ELEMENT_NODE) {
                    PhonoCell pc = new PhonoCell((Element) n);
                    super.lowerObj.add(pc);
                } else if (n.getNodeName().equals("feature") && n.getNodeType() == Node.ELEMENT_NODE) {
                	String textContent = ((Element) n).getTextContent();
        			Feature f = new Feature(textContent, true, FeatureLevel.ROW);
        			this.addFeature(f);
                }
            }
        } else {
            throw new InvalidXMLException("Node name not expected name! Expected: row; Actual: " + e.getTagName());
        }
        
        applyFeatures();
    }
    
    @Override
    public String toString() {
    	String returnString = "categoryName:" + name + "\n";
    	for (int i = 0; i < super.lowerObj.size(); i++) {
    		returnString += super.lowerObj.get(i) + "\n";
    	}
    	returnString += "index:" + index;
    	return returnString;
    }

	@Override
	protected void initFeatures() {
		super.level = FeatureLevel.ROW;
	}
}
