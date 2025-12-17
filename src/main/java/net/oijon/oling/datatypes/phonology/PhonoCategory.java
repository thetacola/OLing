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

//last edit: 12/16/25 -N3

/**
 * Creates the equivalent of a row on the IPA chart.
 * @author alex
 *
 */
public class PhonoCategory implements XMLDatatype {

	private String name;
	private ArrayList<PhonoCell> cells;
    private int index;

    /**
     * Creates a phono category at a given index inside the table for already created list
     * @param name The name of the category
     * @param cells a preëxisting ArrayList of each cell
     * @param index the index of the row in reference to the table it is a part of
     */
    public PhonoCategory(String name, ArrayList<PhonoCell> cells, int index) {
        this.name = name;
        this.cells = cells;
        this.index = index;
    }

	/**
	 * Creates phono category for already created list
	 * @param name the name of the category
	 * @param cells a pre-existing ArrayList of each cell
	 */
	public PhonoCategory(String name, ArrayList<PhonoCell> cells) {
		this.name = name;
		this.cells = cells;
        this.index = 0;
	}
	
	/**
	 * Creates phono category for as-of-yet created list
	 * @param name the name of the category
	 */
	public PhonoCategory(String name) {
		this.name = name;
		this.cells = new ArrayList<PhonoCell>();
        this.index = 0;
	}
	
	/**
	 * Copy constructor
	 * @param pc The PhonoCategory to be copied
	 */
	public PhonoCategory(PhonoCategory pc) {
		this.name = pc.name;
		this.cells = new ArrayList<PhonoCell>(pc.cells);
        this.index = pc.index;
	}
	
	/**
	 * Gets list of all cells in category
	 * @return all cells in category
	 */
	public ArrayList<PhonoCell> getCells() {
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
		return cells.get(i);
	}
	
	/**
	 * Deletes cell
	 * @param i index of cell to be deleted
	 */
	public void removeCell(int i) {
		cells.remove(i);
	}
	
	/**
	 * Adds cell to end of list
	 * @param phonoCell the cell to be added
	 */
	public void addCell(PhonoCell phonoCell) {
        for (int i = 0; i < cells.size(); i++) {
            if (phonoCell.getIndex() == cells.get(i).getIndex()) {
                phonoCell.setIndex(phonoCell.getIndex() + 1);
                i = 0;
            }
        }
        cells.add(phonoCell);
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
		return cells.size();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PhonoCategory) {
			PhonoCategory p = (PhonoCategory) obj;
            return p.name.equals(name) & p.cells.equals(cells);
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
        for (PhonoCell pc : cells) {
            // Assuming here that there's no row of *all* spacers, because why in the world would you do that
            // Of course, this is not a guarantee, so this may make unneeded rows if that truly is the case.
            // This is such an edge case with such a trivial result that I *really* don't feel like fixing this
            if (pc.sizeWithoutSpacers() != 0 | pc.size() == 0) {
                Element pe = pc.toXML();
                root.appendChild(pe);
            }
        }

        return root;
    }

    @Override
    public void fromXML(Element e) throws InvalidXMLException {
        if (e.getTagName().equals("row")) {
            index = Integer.parseInt(e.getAttribute("index"));
            name = e.getAttribute("name");
            NodeList nl = e.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n.getNodeName().equals("cell") & n.getNodeType() == Node.ELEMENT_NODE) {
                    PhonoCell pc = new PhonoCell((Element) n);
                    cells.add(pc);
                }
            }
        } else {
            throw new InvalidXMLException("Node name not expected name! Expected: row; Actual: " + e.getTagName());
        }
    }
}
