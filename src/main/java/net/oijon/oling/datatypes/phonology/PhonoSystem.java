package net.oijon.oling.datatypes.phonology;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import net.oijon.olog.Log;
import net.oijon.oling.LegacyParser;
import net.oijon.oling.info.Info;

//last edit: 6/20/24 -N3

/**
 * A way to transcribe all sounds allowed in a vocal tract. IPA is specified here as that
 * is a standard for human sounds, however PhonoSystems can be created for non-human
 * sounds as well.
 * @author alex
 *
 */
public class PhonoSystem {

	private String name;
	private ArrayList<PhonoTable> tables = new ArrayList<PhonoTable>();
	private ArrayList<String> diacriticList = new ArrayList<String>();
	
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
	 * Allows use of an XY coordinate system to get sounds
	 * @param i Index of table
	 * @param x Index of category
	 * @param y Index of sound
	 * @return The sound at both indexes
	 */
	public String getSound(int i, int x, int y) {
		return tables.get(i).getRow(x).getSound(y);
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
		return false;
	}
	
	/**
	 * Creates a file of the PhonoSystem.
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
			if (p.name.equals(name) & p.tables.equals(tables) &
					p.diacriticList.equals(diacriticList)) {
				return true;
			}
			
		}
		return false;
	}
}
