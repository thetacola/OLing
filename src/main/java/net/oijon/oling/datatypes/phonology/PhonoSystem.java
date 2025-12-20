package net.oijon.oling.datatypes.phonology;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;
import net.oijon.olog.Log;
import net.oijon.oling.LegacyParser;
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
 * A way to transcribe all sounds allowed in a vocal tract. IPA is specified here as that
 * is a standard for human sounds, however PhonoSystems can be created for non-human
 * sounds as well.
 * @author alex
 *
 */
public class PhonoSystem implements XMLDatatype {

	private String name;
	private ArrayList<PhonoTable> tables = new ArrayList<PhonoTable>();
	private ArrayList<String> diacriticList = new ArrayList<String>();
	private ArrayList<PhonoList> lists = new ArrayList<PhonoList>();
	private ArrayList<PhonoAnomaly> anomalies = new ArrayList<PhonoAnomaly>();

	static Log log = Info.log;
	
	/**
	 * Creates an IPA preset. Useful when we just want the default PhonoSystem.
	 */
	
	public static final PhonoSystem IPA = loadIPA();
	
	private static PhonoSystem loadIPA() {
		PhonoSystem IPA = new PhonoSystem("Blank");
		try {
			InputStream IPAStream = PhonoSystem.class.getResourceAsStream("/IPA.phosys");
			File tempFile = File.createTempFile(String.valueOf(IPAStream.hashCode()), ".tmp");
			log.debug("IPA temp file created at " + tempFile.toString());
	        tempFile.deleteOnExit();
	        
	        try (FileOutputStream toTemp = new FileOutputStream(tempFile)) {
	        	writeStreams(IPAStream, toTemp);	        	
	        	IPA = new PhonoSystem(tempFile);
	        }
	        
		} catch (IOException e) {
			e.printStackTrace();
			log.critical("Unable to open default IPA PhonoSystem!!! (Is the program corrupted?)");
		}
		return IPA;
		
	}
	/**
	 * Creates a PhonoSystem object with a pre-defined ArrayList
	 * @param name The name of the phono system
	 * @param tables The list of all tables used in the phono system
	 */
	public PhonoSystem(String name, ArrayList<PhonoTable> tables) {
		this.name = name;
		this.tables = tables;
	}
	/**
	 * Creates a PhonoSystem object with a pre-defined ArrayList and diacritic list
	 * @param name The name of the phono system
	 * @param tables The list of all tables used in the phono system
	 * @param diacriticList The list of all tables used in the phono system
	 */
	public PhonoSystem(String name, ArrayList<PhonoTable> tables, ArrayList<String> diacriticList) {
		this.name = name;
		this.tables = tables;
		this.diacriticList = diacriticList;
	}
	/**
	 * Creates a PhonoSystem object with a blank category list. This list will need something added to it to work!
	 * @param name The name of the phono system
	 */
	public PhonoSystem(String name) {
		this.name = name;
	}

	/**
	 * Creates a PhonoSystem from an XML element
	 * @param e The XML element in question
	 * @throws InvalidXMLException thrown when the tag name is wrong or the element is invalid for whatever reason
	 */
	public PhonoSystem(Element e) throws InvalidXMLException {
		fromXML(e);
	}

	/**
	 * Copy constructor
	 * @param ps The PhonoSystem to be copied
	 */
	public PhonoSystem(PhonoSystem ps) {
		this.name = ps.name;
		this.tables = new ArrayList<PhonoTable>(ps.tables);
		this.diacriticList = new ArrayList<String>(ps.diacriticList);
	}
	
	/**
	 * Loads a PhonoSystem object from a file
	 * @param file The file to load from
	 * @deprecated as of 3.0.0, as this uses the old .language format. Instead, parse your file into an XML element, then create a PhonoSystem from the element
	 */
	public PhonoSystem(File file) {
		try {
			LegacyParser parser = new LegacyParser(file);
			// this is silly
			PhonoSystem parsedSys = parser.parsePhonoSys();
			this.diacriticList = parsedSys.getDiacritics();
			this.name = parsedSys.getName();
			this.tables = parsedSys.getTables();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.print("\n");
			for (int i = 0; i < 30; i++) {
				System.err.print("+=");
			}
			System.err.print("\n");
			System.err.println("Exception encountered! " + e.toString());
			System.err.println("Defaulting to IPA...");
			this.name = PhonoSystem.IPA.getName();
			this.tables = PhonoSystem.IPA.getTables();
		}
	}
	
	/**
	 * Gets the name of the phono system
	 * @return The name of the phono system
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets an ArrayList of all of the categories added
	 * @return ArrayList of several PhonoCategory instances
	 */
	public ArrayList<PhonoTable> getTables() {
		return tables;
	}
	
	/**
	 * Removes table based off name. As this is slower than removing via index, removing via index is preferred.
	 * @param name Name of category to be removed
	 */
	public void removeTable(String name) {
		for (int i = 0; i < tables.size(); i++) {
			if (tables.get(i).getName().equals(name)) {
				tables.remove(i);
				break;
			}
		}
	}
	
	/**
	 * Allows use of an XYZ coordinate system to get sounds
	 * @param i Index of table
	 * @param x Index of category
	 * @param y Index of cell
     * @param z Index of sound
	 * @return The sound at both indexes
	 */
	public String getSound(int i, int x, int y, int z) {
		return tables.get(i).getRow(x).getCell(y).getPhonemes().get(z).getSound();
	}
	/**
	 * Sets the diacritic list to a new list
	 * @param newList The new list of diacritics
	 */
	public void setDiacritics(ArrayList<String> newList) {
		diacriticList = newList;
	}
	/**
	 * Gets the list of diacritics
	 * @return The list of diacritics
	 */
	public ArrayList<String> getDiacritics() {
		return diacriticList;
	}
	
	
	/**
	 * Used once in here to read to an InputStream and write to an OutputStream.
	 * Will be replaced by OStream soon (once it comes out that is)
	 * @param is The input stream to read from
	 * @param os The output stream to write to
	 * @throws IOException
	 */
	private static void writeStreams(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[1024];
    	int bytesRead;
    	while ((bytesRead = is.read(buffer)) != -1) {
    		os.write(buffer, 0, bytesRead);
    	}
	}
	
	/**
	 * Converts a PhonoSystem object to a string
	 */
	public String toString() {
		String output = "===Tablelist Start===\n";
		output += "tablelistName:" + name + "\n";
		output += "diacriticList:";
		for (int i = 0; i < diacriticList.size(); i++) {
			output += diacriticList.get(i) + ",";
		}
		if (output.charAt(output.length() - 1) == ',') {
			output = output.substring(0, output.length() - 1);
		}
		output += "\n";
		for (int i = 0; i < tables.size(); i++) {
			output += tables.get(i).toString() + "\n";
		}
		output += "===Tablelist End===";
		return output;
	}
	/**
	 * Checks if a given value exists in a phono system.
	 * @param value The value to be checked
	 * @return Returns true if value is found in phono system, false if not
	 */
	public boolean contains(String value) {
		for (int i = 0; i < diacriticList.size(); i++) {
			value = value.replace(Character.toString(diacriticList.get(i).charAt(0)), "");
		}
		if (value.length() > 1) {
			value = Character.toString(value.charAt(0));
		}
		for (int i = 0; i < tables.size(); i++) {
			if (tables.get(i).getSoundList().contains(value)) {
				return true;
			}
		}
		for (int i = 0; i < lists.size(); i++) {
			ArrayList<Phoneme> phonemesInList = lists.get(i).getPhonemes();
			for (int j = 0; j < phonemesInList.size(); j++) {
				if (phonemesInList.get(j).getSound().equals(value)) {
					return true;
				}
			}
		}

		return false;
	}

	public PhonoList getList(int index) {
		return lists.get(index);
	}

	public void addList(PhonoList list) {
		lists.add(list);
	}

	public void removeList(int index) {
		lists.remove(index);
	}

	public int listsSize() {
		return lists.size();
	}

	/**
	 * Removes encoding anomalies from a given string
	 * @param input The input to be scrubbed of anomalies
	 * @return The input with all anomalies replaced with their proper representations
	 */
	public String normalize(String input) {
		String output = input;
		for (int i = 0; i < anomalies.size(); i++) {
			output = anomalies.get(i).normalize(output);
		}
		return output;
	}

	/**
	 * Creates a file of the PhonoSystem.
	 * @Deprecated since v3.0.0, as this makes a file with the old format. Instead, write the XML element of this to a file.
	 */
	public void toFile() {
		String output = "===PHOSYS Start===\n";
		output += toString();
		output += "\n===PHOSYS End===";
		
		File mainDir = new File(System.getProperty("user.home") + "/Susquehanna/phonoSystems");
		mainDir.mkdirs();
		File systemFile = new File(System.getProperty("user.home") + "/Susquehanna/phonoSystems/" + getName() + ".phosys");
		PrintWriter out;
		try {
			out = new PrintWriter(systemFile);
			out.println(output);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PhonoSystem) {
			PhonoSystem p = (PhonoSystem) obj;			
			if (p.name.equals(name) && p.tables.equals(tables) &
					p.diacriticList.equals(diacriticList)) {
				return true;
			}
			
		}
		return false;
	}

    @Override
    public Element toXML() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("tables");
        root.setAttribute("name", name);

        Element diacritics = doc.createElement("diacritics");
        for (String s : diacriticList) {
            Element diacritic = doc.createElement("diacritic");
            diacritic.appendChild(doc.createTextNode(s));
            diacritics.appendChild(diacritic);
        }
        root.appendChild(diacritics);

		for (PhonoList list : lists) {
			root.appendChild(list.toXML());
		}

        for (PhonoTable pt : tables) {
            root.appendChild(pt.toXML());
        }

        Element anomaliesE = doc.createElement("anomalies");
        for (PhonoAnomaly pa : anomalies) {
            anomaliesE.appendChild(pa.toXML());
        }
        root.appendChild(anomaliesE);

        return root;
    }

    @Override
    public void fromXML(Element e) throws InvalidXMLException {
	    if (e.getTagName().equals("tables")) {
		    name = e.getAttribute("name");
		    NodeList nl = e.getChildNodes();
		    for (int i = 0; i < nl.getLength(); i++) {
			    Node n = nl.item(i);
			    switch (n.getNodeName()) {
				    case "diacritics":
					    NodeList diacritics = n.getChildNodes();
					    for (int j = 0; j < diacritics.getLength(); j++) {
						    Node diacritic = diacritics.item(j);
						    this.diacriticList.add(diacritic.getTextContent());
					    }
                        break;
				    case "table":
					    if (n.getNodeType() == Node.ELEMENT_NODE) {
							tables.add(new PhonoTable((Element) n));
					    }
                        break;
				    case "list":
						if (n.getNodeType() == Node.ELEMENT_NODE) {
							lists.add(new PhonoList((Element) n));
						}
                        break;
				    case "anomalies":
						if (n.getNodeType() == Node.ELEMENT_NODE) {
							NodeList anomalyNodes = n.getChildNodes();
							for (int j = 0; j < anomalyNodes.getLength(); j++) {
								Node a = anomalyNodes.item(j);
								if (a.getNodeType() == Node.ELEMENT_NODE) {
									anomalies.add(new PhonoAnomaly((Element) a));
								}
							}
						}
                        break;
				    default:

			    }
		    }
	    } else {
		    throw new InvalidXMLException("Node name not expected name! Expected: tables; Actual: " + e.getTagName());
	    }
    }
}
