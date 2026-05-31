package net.oijon.oling.datatypes.phonology.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.phonology.SyllablePart;
import net.oijon.oling.datatypes.phonology.feature.FeaturalXMLDatatype;
import net.oijon.oling.datatypes.phonology.feature.Feature;
import net.oijon.oling.datatypes.phonology.feature.FeatureLevel;
import net.oijon.oling.datatypes.tags.Multitag;
import net.oijon.oling.datatypes.tags.Tag;
import net.oijon.oling.info.Info;
import net.oijon.olog.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

//last edit: 5/22/26 -N3

/**
 * Like an IPA table, but readable in Java
 * @author alex
 *
 */
public class PhonoTable extends FeaturalXMLDatatype {

	private String name;
	private ArrayList<PhonoColumn> columns = new ArrayList<>();
	//private ArrayList<String> columnNames = new ArrayList<>();
	//private ArrayList<PhonoCategory> rows = new ArrayList<>();
	private SyllablePart part;
	@Deprecated
	private int soundsPerCell;
	
	private static Log log = Info.log;
	
	/**
	 * Creates a PhonoTable
	 * @param name The name of the PhonoTable (Consonants, Vowels, etc.)
	 * @param columnNames The names of the columns
	 * @param rows An ArrayList of all the rows to be added
	 * @param soundsPerCell How many sounds should be in a cell
	 * @param part The part of the syllable this elements of this table should go in
	 */
	public PhonoTable(String name, ArrayList<String> columnNames,
			ArrayList<PhonoCategory> rows, int soundsPerCell, SyllablePart part) {
		this(name, columnNames, rows, soundsPerCell);
		this.part = part;
	}
	
	/**
	 * Creates a PhonoTable
	 * @param name The name of the PhonoTable (Consonants, Vowels, etc.)
	 * @param columnNames The names of the columns
	 * @param rows An ArrayList of all the rows to be added
	 * @param soundsPerCell How many sounds should be in a cell
	 */
	public PhonoTable(String name, ArrayList<String> columnNames,
			ArrayList<PhonoCategory> rows, int soundsPerCell) {
		initFeatures();
		this.name = name;
		for (int i = 0; i < columnNames.size(); i++) {
			columns.add(new PhonoColumn(columnNames.get(i), i));
		}
		for (PhonoCategory pc : rows) {
			super.lowerObj.add(pc);
		}
		this.soundsPerCell = soundsPerCell;
        fixIndecies();
        this.part = SyllablePart.ANY;
        applyFeatures();
	}

	/**
	 * Parses a PhonoTable from an XML element
	 * @param e The XML element to use
	 * @throws InvalidXMLException Thrown if the XML element is the wrong name or is otherwise invalid
	 */
	public PhonoTable(Element e) throws InvalidXMLException {
		fromXML(e);
	}

	/**
	 * Copy constructor
	 * @param pt The PhonoTable to be copied
	 */
	public PhonoTable(PhonoTable pt) {
		initFeatures();
		this.name = pt.name;
		this.columns = new ArrayList<PhonoColumn>(pt.columns);
		super.lowerObj = new ArrayList<FeaturalXMLDatatype>(pt.lowerObj);
		this.soundsPerCell = pt.soundsPerCell;
		applyFeatures();
	}
	
	/**
	 * Parses a PhonoTable from a Multitag. Previously this function was a part 
	 * of PhonoSystem.parse(), however this allows the program to be maintained more
	 * easily.
	 * @param tag The tag to parse the PhonoTable from* @deprecated as of OLing v3.0.0, as this uses the old file format.
	 * @return The PhonoTable stored in the multitag
	 * @deprecated as of OLing v3.0.0, as this uses the old file format.
	 * @throws Exception Thrown when any data inside the PhonoTable is invalid, 
	 * for example if soundsPerCell is a non-integer.
	 */
	public static PhonoTable parse(Multitag tag) throws Exception {
		ArrayList<Tag> tableData = tag.getUnattachedData();
		
		String name = tag.getDirectChild("tableName").value();
		ArrayList<String> columns = new ArrayList<String>(Arrays.asList(tag.getDirectChild("columnNames").value().split(",")));
		ArrayList<String> rowNamesList = new ArrayList<String>(Arrays.asList(tag.getDirectChild("rowNames").value().split(",")));
		int perCell;
		try {
			perCell = Integer.parseInt(tag.getDirectChild("soundsPerCell").value());
		} catch (NumberFormatException nfe) {
			log.err("soundsPerCell must be integer in " + tag.getDirectChild("tableName").value());
			log.err(nfe.toString());
			throw nfe;
		}
		
		ArrayList<PhonoCategory> cats = new ArrayList<PhonoCategory>();
		for (int j = 0; j < rowNamesList.size(); j++) {
			PhonoCategory cat = new PhonoCategory(rowNamesList.get(j));
			// TODO: allow multiple character sounds?
			try {
				String catData = tableData.get(j).value();
				for (int k = 0; k < catData.length() / perCell; k++) {
                    PhonoCell cell = new PhonoCell(k);
                    for (int l = 0; l < perCell; l++) {
                        char c = catData.charAt((k * perCell) + l);
						if (c != '*' && c != '#') {
							Phoneme p = new Phoneme(Character.toString(c), l);
							cell.addSound(p);
						}
                    }
					if (cell.size() > 0) {
						cat.addCell(cell);
					}
				}
				cats.add(cat);
			} catch (IndexOutOfBoundsException e) {
				log.warn("No data found in table " + name);
			}
		}
		
		PhonoTable phonoTable = new PhonoTable(name, columns, cats, perCell);
		return phonoTable;
	}

	public String toString() {
		String returnString = "tableName: " + name + "\n" +
				"columns:" + columns.toString() + "\n";
		for (int i = 0; i < super.lowerObj.size(); i++) {
			returnString += super.lowerObj.get(i) + "\n";
		}
		returnString += "part:" + part.name() + "\n" +
				"soundsPerCell:" + soundsPerCell;
		return returnString;
	}
	
	/**
	 * Converts a PhonoTable to a string
	 * @deprecated Since v3.1.0, as it is only for the legacy parser.
	 */
	public String toLegacyString() {
		String returnString = "===PhonoTable Start===\ntableName:" + name + "\ncolumnNames:";
		for (int i = 0; i < columns.size(); i++) {
			returnString += columns.get(i).getName() + ",";
		}
		returnString = returnString.substring(0, returnString.length() - 1); // removes last comma
		returnString += "\nsoundsPerCell:" + soundsPerCell;
		returnString += "\nrowNames:";
		for (int i = 0; i < super.lowerObj.size(); i++) {
			if (super.lowerObj.get(i) instanceof PhonoCategory) {
				PhonoCategory row = (PhonoCategory) super.lowerObj.get(i);
				returnString += row.getName() + ",";
			}
		}
		returnString = returnString.substring(0, returnString.length() - 1); // removes last comma
		returnString += "\n";
		for (int i = 0; i < super.lowerObj.size(); i++) {
			returnString += ":";
			if (super.lowerObj.get(i) instanceof PhonoCategory) {
				PhonoCategory pc = (PhonoCategory) super.lowerObj.get(i);
				for (int j = 0; j < pc.size(); j++) {
	                for (int k = 0; k < pc.getCell(j).getPhonemes().size(); k++) {
	                    returnString += pc.getCell(j).getPhonemes().get(k).getSound();
	                }
				}
			}
			returnString += "\n";
		}
		returnString += "===PhonoTable End===";
		return returnString;
	}
	
	/**
	 * Gets the name of the PhonoTable
	 * @return the name of the PhonoTable
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the amount of rows in a PhonoTable
	 * @return The amount of rows in a PhonoTable
	 */
	public int size() {
		return super.lowerObj.size();
	}
	
	/**
	 * Gets the row in a PhonoTable at index i
	 * @param i The index number
	 * @return The row at index i
	 */
	public PhonoCategory getRow(int i) {
		// shouldn't break, despite it looking spooky
		return (PhonoCategory) super.lowerObj.get(i);
	}
	
	/**
	 * Gets a list of all the column names
	 * @return The column names
	 */
	public ArrayList<String> getColumnNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (PhonoColumn pc : columns) { 
			names.add(pc.getName());
		}
		return names;
	}
	
	public ArrayList<PhonoColumn> getColumns() {
		return columns;
	}
	
	public PhonoColumn getColumn(int index) {
		PhonoColumn c = null;
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).getIndex() == index) {
				c = columns.get(i);
				break;
			}
		}
		return c;
	}
	
	/**
	 * Gets the amount of data per cell
	 * @return The amount of sounds per cell
	 * @deprecated as of OLing v3.0.0, as this is not data used for the new file format.
	 */
	public int dataPerCell() {
		return soundsPerCell;
	}
	
	/**
	 * Gets a list of all sounds in the table
	 * @return A list of all sounds in the table
	 */
	public ArrayList<String> getSoundList() {
		ArrayList<String> list = new ArrayList<String>();

		for (int i = 0; i < super.lowerObj.size(); i++) {
            PhonoCategory row = (PhonoCategory) super.lowerObj.get(i);
			for (int j = 0; j < row.size(); j++) {
                PhonoCell cell = row.getCell(j);
                for (int k = 0; k < cell.size(); k++) {
                    list.add(cell.getPhonemes().get(k).getSound());
                }
            }
		}
		
		return list;
	}

    private void fixIndecies() {
        for (int i = 0; i < super.lowerObj.size(); i++) {
        	PhonoCategory pc = (PhonoCategory) super.lowerObj.get(i);
            if (pc.getIndex() == 0) {
                pc.setIndex(i);
            }
        }
    }
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PhonoTable) {
			PhonoTable p = (PhonoTable) obj;
			if (p.name.equals(name) && p.columns.equals(columns) && p.lowerObj.equals(super.lowerObj)) {
				return true;
			}
			
		}
		return false;
	}

    @Override
    public Element toXML() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("table");
        root.setAttribute("name", name);
        root.setAttribute("part", part.name());

        Element columnsE = doc.createElement("columns");
        for (PhonoColumn pc : columns) {
        	columnsE.appendChild(doc.importNode(pc.toXML(), true));
        }
        root.appendChild(columnsE);

        Element rowsE = doc.createElement("rows");        
        for (FeaturalXMLDatatype fxd : super.lowerObj) {
        	if (fxd instanceof PhonoCategory) {
        		PhonoCategory pc = (PhonoCategory) fxd;
        		rowsE.appendChild(doc.importNode(pc.toXML(), true));
        	}
        }
        
        for (Feature f : features.values()) {
        	if (f.getLevel().equals(level)) {
        		Element fe = (Element) doc.importNode(f.toXML(), true);
        		root.appendChild(fe);
        	}
        }
        
        root.appendChild(rowsE);

        return root;
    }

    @Override
    public void fromXML(Element e) throws InvalidXMLException {
    	initFeatures();
	    if (e.getTagName().equals("table")) {
		    name = e.getAttribute("name");
		    try {
		    	part = SyllablePart.valueOf(e.getAttribute("part"));
		    } catch (NullPointerException e1) {
		    	log.warn("No syllable part specified for phono table " + name +
		    			". Defaulting to any.");
		    	part = SyllablePart.ANY;
		    } catch (IllegalArgumentException e1) {
		    	log.err("Given syllable part on table " + name + " not valid! Got: \"" 
		    			+ e.getAttribute("part") + "\". Defaulting to any.");
		    	part = SyllablePart.ANY;
		    }
		    NodeList nl = e.getChildNodes();
		    for (int i = 0; i < nl.getLength(); i++) {
			    Node n = nl.item(i);
			    switch (n.getNodeName()) {
				    case "columns":
						NodeList columns = n.getChildNodes();
						for (int j = 0; j < columns.getLength(); j++) {
							Node column = columns.item(j);
							this.columns.add(new PhonoColumn((Element) column));
						}
                        break;
				    case "rows":
				    	NodeList rowList = n.getChildNodes();
				    	for (int j = 0; j < rowList.getLength(); j++) {
							Node row = rowList.item(j);
							Element rowE = (Element) row;
							super.lowerObj.add(new PhonoCategory(rowE));
						}
                        break;
				    case "feature":
				    	String textContent = ((Element) n).getTextContent();
	        			Feature f = new Feature(textContent, true, FeatureLevel.TABLE);
	        			this.addFeature(f);
				    default:

			    }
		    }
            fixIndecies();
	    } else {
		    throw new InvalidXMLException("Node name not expected name! Expected: table; Actual: " + e.getTagName());
	    }
	    this.applyFeatures();
    }
    
    @Override
    protected void applyFeatures() {
    	super.applyFeatures();
    	// essentially a copy of the super method, but for columns
    	for (PhonoColumn c : columns) {
			// set true features to lower datatype
			features.forEach(new BiConsumer<String, Feature>() {
				@Override
				public void accept(String name, Feature f) {
					if (f.getValue()) {
						c.addFeature(f);
					} else {
						c.addFeature(new Feature(name, false, FeatureLevel.SYSTEM));
					}
				}
			});
			// get missing features from lower datatype
			c.getFeatures().forEach(new BiConsumer<String, Feature>() {
				@Override
				public void accept(String name, Feature f) {
					// false features should be system level
					features.putIfAbsent(name, new Feature(name, false, FeatureLevel.SYSTEM));
				}
			});
    	}
    }

	@Override
	protected void initFeatures() {
		super.level = FeatureLevel.TABLE;
	}
}
