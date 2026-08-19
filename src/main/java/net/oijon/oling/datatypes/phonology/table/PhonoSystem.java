package net.oijon.oling.datatypes.phonology.table;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.BiConsumer;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.language.Language;
import net.oijon.oling.datatypes.phonology.PhonoAnomaly;
import net.oijon.oling.datatypes.phonology.feature.Diacritic;
import net.oijon.oling.datatypes.phonology.feature.FeaturalXMLDatatype;
import net.oijon.oling.datatypes.phonology.feature.Feature;
import net.oijon.oling.datatypes.phonology.feature.FeatureLevel;
import net.oijon.oling.datatypes.phonology.feature.sonorancy.SonorancyTree;
import net.oijon.olog.Log;
import net.oijon.oling.LegacyParser;
import net.oijon.oling.info.Info;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

//last edit: 5/22/2026 -N3

/**
 * A way to transcribe all sounds allowed in a vocal tract. IPA is specified here as that
 * is a standard for human sounds, however PhonoSystems can be created for non-human
 * sounds as well.
 * @author alex
 *
 */
public class PhonoSystem extends FeaturalXMLDatatype {

	private String name;
	//private ArrayList<PhonoTable> tables = new ArrayList<PhonoTable>();
	private HashMap<String, Diacritic> diacritics = new HashMap<String, Diacritic>();
	private ArrayList<PhonoList> lists = new ArrayList<PhonoList>();
	private ArrayList<PhonoAnomaly> anomalies = new ArrayList<PhonoAnomaly>();
	// FIXME: make a constructor without a feature, so that it can be filled by the parser
	private SonorancyTree sonorancyTree = new SonorancyTree();
	private int numDiacritics = -1;
	static Log log = Info.log;
	
	/**
	 * Creates an IPA preset. Useful when we just want the default PhonoSystem.
	 */
	
	public static final PhonoSystem IPA = loadIPA();
	
	private static PhonoSystem loadIPA() {
		PhonoSystem IPA = new PhonoSystem("Blank");
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
			
	        InputStream ipaIS = PhonoSystem.class.getClassLoader().getResourceAsStream("IPA.xml");
	        
	        Scanner reader = new Scanner(ipaIS, StandardCharsets.UTF_8);
	        boolean firstLine = true;
	        String data = "";
	        while (reader.hasNextLine()) {
	            if (firstLine) {
	                data = reader.nextLine();
	                String[] splitData = data.split("<\\?xml");
	                if (splitData.length > 1) {
	                	data = "<?xml" + splitData[1];
	                } else {
	                	data = splitData[0];
	                }
	                firstLine = false;
	            } else {
	                data += reader.nextLine().strip();
	            }
	        }
	        reader.close();
	        Document doc = builder.parse(new InputSource(new StringReader(data)));
	        Element element = doc.getDocumentElement();
	        IPA = new PhonoSystem(element);
		} catch (SAXException e) {
			log.err("SAXException when parsing IPA from program.");
			e.printStackTrace();
		} catch (IOException e) {
			log.err("Unable to read IPA from program.");
			e.printStackTrace();
		} catch (InvalidXMLException e) {
			log.err("Given IPA in program is invalid! Have resources been edited?");
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			log.critical("ParserConfigurationException when parsing default IPA!!!");
			e.printStackTrace();
		} catch (NullPointerException e) {
			log.critical("Unable to load IPA system from program!!! (Is the program corrupted?)");
			e.printStackTrace();
		}
		
		return IPA;
	}
	/**
	 * Creates a PhonoSystem object with a pre-defined ArrayList
	 * @param name The name of the phono system
	 * @param tables The list of all tables used in the phono system
	 */
	public PhonoSystem(String name, ArrayList<PhonoTable> tables) {
		initFeatures();
		this.name = name;
		super.lowerObj.addAll(tables);
	}
	
	/**
	 * Creates a PhonoSystem object with a pre-defined ArrayList and diacritics
	 * @param name The name of the phono system
	 * @param tables The list of all tables used in the phono system
	 * @param diacritics The list of all diacritics used in the phono system, indexed by their string representation
	 */
	public PhonoSystem(String name, ArrayList<PhonoTable> tables, HashMap<String, Diacritic> diacritics) {
		initFeatures();
		this.name = name;
		this.diacritics = new HashMap<String, Diacritic>(diacritics);
		for (Diacritic d : this.diacritics.values()) {
			for (String s : d.getFeatureKeys()) {
				this.features.putIfAbsent(s, new Feature(s, false, FeatureLevel.SYSTEM));
			}
		}
	}
	
	/**
	 * Creates a PhonoSystem object with a pre-defined ArrayList and diacritic list
	 * Note that this does not give features to diacritics
	 * @param name The name of the phono system
	 * @param tables The list of all tables used in the phono system
	 * @param diacriticList The list of all diacritic string representations used in the phono system
	 */
	public PhonoSystem(String name, ArrayList<PhonoTable> tables, ArrayList<String> diacriticList) {
		initFeatures();
		this.name = name;
		super.lowerObj.addAll(tables);
		addDiacriticsFromList(diacriticList);
	}
	/**
	 * Creates a PhonoSystem object with a blank category list. This list will need something added to it to work!
	 * @param name The name of the phono system
	 */
	public PhonoSystem(String name) {
		initFeatures();
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
		initFeatures();
		this.name = ps.name;
		this.lowerObj = new ArrayList<FeaturalXMLDatatype>(ps.lowerObj);
		this.diacritics = new HashMap<String, Diacritic>(ps.diacritics);
		
	}
	
	/**
	 * Loads a PhonoSystem object from a file
	 * @param file The file to load from
	 * @deprecated as of 3.0.0, as this uses the old .language format. Instead, parse your file into an XML element, then create a PhonoSystem from the element
	 */
	@Deprecated
	public PhonoSystem(File file) {
		try {
			LegacyParser parser = new LegacyParser(file);
			// this is silly
			PhonoSystem parsedSys = parser.parsePhonoSys();
			this.diacritics = parsedSys.diacritics;
			this.name = parsedSys.getName();
			super.lowerObj.addAll(parsedSys.getTables());
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
			super.lowerObj.addAll(PhonoSystem.IPA.getTables());
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
		ArrayList<PhonoTable> tables = new ArrayList<PhonoTable>();
		for (int i = 0; i < super.lowerObj.size(); i++) {
			if (super.lowerObj.get(i) instanceof PhonoTable) {
				tables.add((PhonoTable) super.lowerObj.get(i));
			}
		}
		return tables;
	}
	
	public void addTable(PhonoTable pt) {
		super.lowerObj.add(pt);
	}
	
	/**
	 * Removes table based off name. As this is slower than removing via index, removing via index is preferred.
	 * @param name Name of category to be removed
	 */
	public void removeTable(String name) {
		for (int i = 0; i < super.lowerObj.size(); i++) {
			PhonoTable pt = (PhonoTable) super.lowerObj.get(i);
			if (pt.getName().equals(name)) {
				super.lowerObj.remove(i);
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
		PhonoTable table = (PhonoTable) super.lowerObj.get(i);
		return table.getRow(x).getCell(y).getPhonemes().get(z).getSound();
	}

	/**
	 * Gets a diacritic in the phono system. Returns null if not found
	 * @param key The string representation of the diacritic
	 * @return The diacritic object
	 */
	public Diacritic getDiacritic(String key) {
		return diacritics.get(key);
	}
	
	/**
	 * Sets a diacritic, overwriting anything with the same character
	 * @param d The diacritic to set
	 */
	public void setDiacritic(Diacritic d) {
		Diacritic oldDiacritic = diacritics.get(d.getCharacter());
		if (oldDiacritic != null) {
			for (String s : oldDiacritic.getFeatureKeys()) {
				this.features.remove(s);
			}
		}
		diacritics.put(d.getCharacter(), d);
		for (String s : d.getFeatureKeys()) {
			this.features.putIfAbsent(s, new Feature(s, false, FeatureLevel.SYSTEM));
		}
	}
	
	/**
	 * Adds a diacritic only if there is no other diacritic with the same string representation
	 * @param d The diacritic to add
	 */
	public void addDiacritic(Diacritic d) {
		diacritics.putIfAbsent(d.getCharacter(), d);
		for (String s : d.getFeatureKeys()) {
			this.features.putIfAbsent(s, new Feature(s, false, FeatureLevel.SYSTEM));
		}
	}
	
	/**
	 * Removes a diacritic based off its string representation
	 * @param key The string representation of the diacritic
	 */
	public void removeDiacritic(String key) {
		diacritics.remove(key);
	}
	
	public SonorancyTree getSonorancyTree() {
		return sonorancyTree;
	}
	
	public void setSonorancyTree(SonorancyTree st) {
		this.sonorancyTree = st;
	}
	
	public String toString() {
		String returnString = "sysName:" + name + "\n";
		for (int i = 0; i < super.lowerObj.size(); i++) {
			returnString += super.lowerObj.get(i) + "\n";
		}
		returnString += "diacritics:" + diacritics + "\n";
		for (int i = 0; i < lists.size(); i++) {
			returnString += lists.get(i) + "\n";
		}
		returnString += "anomalies:" + anomalies.toString();
		return returnString;
	}
	
	/**
	 * Converts a PhonoSystem object to a string
	 * @deprecated Since v3.1.0, as it is only for the legacy parser.
	 * @return The string used to store the language in the legacy format
	 */
	@Deprecated
	public String toLegacyString() {
		String output = "===Tablelist Start===\n";
		output += "tablelistName:" + name + "\n";
		output += "diacriticList:";
		ArrayList<String> diacriticList = new ArrayList<String>(diacritics.keySet());
		for (int i = 0; i < diacriticList.size(); i++) {
			output += diacriticList.get(i) + ",";
		}
		if (output.charAt(output.length() - 1) == ',') {
			output = output.substring(0, output.length() - 1);
		}
		output += "\n";
		for (int i = 0; i < super.lowerObj.size(); i++) {
			output += super.lowerObj.get(i).toString() + "\n";
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
		return (find(value) != null);
	}
	
	/**
	 * Finds a particular phoneme object from a given string
	 * @param value The string representation of the wanted phoneme
	 * @return The phoneme that matches the given string.
	 */
	public Phoneme find(String value) {
		ArrayList<String> keys = new ArrayList<String>(diacritics.keySet());
		for (int i = 0; i < keys.size(); i++) {
			value = value.replace(keys.get(i), "");
		}
		// looks awful, pretty sure it's O(n) and not O(n⁴) though
		for (int i = 0; i < super.lowerObj.size(); i++) {
			PhonoTable pt = (PhonoTable) super.lowerObj.get(i);
			for (int j = 0; j < pt.size(); j++) {
				PhonoCategory pc = pt.getRow(j);
				for (int k = 0; k < pc.size(); k++) {
					PhonoCell pce = pc.getCell(k);
					for (int l = 0; l < pce.size(); l++) {
						Phoneme p = pce.getPhonemes().get(l);
						if (p.getSound().equals(value)) {
							return p;
						}
					}
				}
			}
		}
		for (int i = 0; i < lists.size(); i++) {
			ArrayList<Phoneme> phonemesInList = lists.get(i).getPhonemes();
			for (int j = 0; j < phonemesInList.size(); j++) {
				if (phonemesInList.get(j).getSound().equals(value)) {
					return phonemesInList.get(j);
				}
			}
		}
		return null;
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
	
	public ArrayList<String> getDiacriticKeys() {
		return new ArrayList<String>(diacritics.keySet());
	}

	/**
	 * Creates a file of the PhonoSystem.
	 * @deprecated since v3.0.0, as this makes a file with the old format. Instead, write the XML element of this to a file.
	 */
	@Deprecated
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
			if (p.name.equals(name) && p.lowerObj.equals(super.lowerObj) 
					&& p.sonorancyTree.equals(sonorancyTree)) {
				
				for (String key : diacritics.keySet()) {
					if (p.getDiacritic(key) == null ||
							!p.getDiacritic(key).equals(diacritics.get(key))) {
						return false;
					}
				}
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
        for (Diacritic d : this.diacritics.values()) {
            diacritics.appendChild(doc.importNode(d.toXML(), true));
        }
        root.appendChild(diacritics);
        root.appendChild(doc.importNode(sonorancyTree.toXML(), true));
        
		for (PhonoList list : lists) {
			root.appendChild(doc.importNode(list.toXML(), true));
		}

        for (FeaturalXMLDatatype fxd : super.lowerObj) {
        	if (fxd instanceof PhonoTable) {
        		PhonoTable pt = (PhonoTable) fxd;
        		root.appendChild(doc.importNode(pt.toXML(), true));
        	}
        }

        Element anomaliesE = doc.createElement("anomalies");
        for (PhonoAnomaly pa : anomalies) {
            anomaliesE.appendChild(doc.importNode(pa.toXML(), true));
        }
        root.appendChild(anomaliesE);

        return root;
    }

    @Override
    public void fromXML(Element e) throws InvalidXMLException {
    	initFeatures();
	    if (e.getTagName().equals("tables")) {
		    name = e.getAttribute("name");
		    NodeList nl = e.getChildNodes();
		    for (int i = 0; i < nl.getLength(); i++) {
			    Node n = nl.item(i);
			    switch (n.getNodeName()) {
				    case "diacritics":
					    NodeList diacritics = n.getChildNodes();
					    for (int j = 0; j < diacritics.getLength(); j++) {
					    	Node dn = diacritics.item(j);
					    	if (dn.getNodeType() == Node.ELEMENT_NODE) {
								Diacritic d = new Diacritic((Element) dn);
								this.diacritics.put(d.getCharacter(), d);
								for (String s : d.getFeatureKeys()) {
									this.features.putIfAbsent(s, new Feature(s, false, FeatureLevel.SYSTEM));
								}
							}
					    	
					    }
                        break;
				    case "table":
					    if (n.getNodeType() == Node.ELEMENT_NODE) {
							super.lowerObj.add(new PhonoTable((Element) n));
					    }
                        break;
				    case "list":
						if (n.getNodeType() == Node.ELEMENT_NODE) {
							lists.add(new PhonoList((Element) n));
						}
                        break;
				    case "sonorancy":
				    	if (n.getNodeType() == Node.ELEMENT_NODE) {
				    		sonorancyTree = new SonorancyTree((Element) n);
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
	    applyFeatures();
    }
	@Override
	protected void initFeatures() {
		super.level = FeatureLevel.SYSTEM;
	}
	
	@Override
	public ArrayList<Phoneme> getAllPhonemes() {
		ArrayList<Phoneme> phonemes = super.getAllPhonemes();
		
		for (int i = 0; i < lists.size(); i++) {
			PhonoList l = lists.get(i);
			phonemes.addAll(l.getAllPhonemes());
		}
		
		return phonemes;
	}
	
	@Override
	protected void applyFeatures() {
		super.applyFeatures();
	}
	
	public void addDiacriticsFromList(ArrayList<String> diacriticList) {
		for (String s : diacriticList) {
			Diacritic d = new Diacritic(s);
			diacritics.putIfAbsent(s, d);
			for (Feature f : d.getFeatures().values()) {
				this.features.putIfAbsent(f.getName(), new Feature(f.getName(), false, FeatureLevel.SYSTEM));
			}
		}
	}
	
	public void setAllSonorance() {
		ArrayList<Phoneme> phonemes = this.getAllPhonemes();
		for (int i = 0; i < phonemes.size(); i++) {
			Phoneme p = phonemes.get(i);
			int value = sonorancyTree.getPhonemeValue(p);
			p.setSonorancy(value);
		}
	}
}
