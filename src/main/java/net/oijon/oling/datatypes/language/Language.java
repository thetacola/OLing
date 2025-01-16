package net.oijon.oling.datatypes.language;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import net.oijon.olog.Log;

import net.oijon.oling.info.Info;
import net.oijon.oling.LegacyParser;
import net.oijon.oling.datatypes.lexicon.Lexicon;
import net.oijon.oling.datatypes.orthography.Orthography;
import net.oijon.oling.datatypes.phonology.Phonology;
import net.oijon.oling.datatypes.tags.Multitag;

//last edit: 6/19/24 -N3

/**
 * Bundles all parts of a language together into one object
 * @author alex
 *
 */

// TODO: re-add parent
public class Language {

	public static Log log = Info.log;
	public static final Language NULL = new Language("null");
	
	private LanguageProperties properties = new LanguageProperties();
	private Phonology phono = new Phonology();
	private Lexicon lexicon = new Lexicon();
	private Orthography ortho = new Orthography();
	
	/**
	 * Gets a list of all .language files in a specified directory. Does not currently support looking into subdirectories.
	 * @param f The directory to look in
	 * @return A list of all .language files in a specified directory.
	 */
	public static File[] getLanguageFiles(File f) {
		//TODO: Search in subdirectories as well
		File[] files;
		try {
			FilenameFilter filter = new FilenameFilter() {
	            @Override
	            public boolean accept(File f, String name) {
	            	if (name.startsWith(".")) {
	            		return false;
	            	} else if (name.endsWith(".language")) {
	            		return true;
	            	}
	            	return false;
	            }
	        };
	        files = f.listFiles(filter);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            files = null;
        }
		return files;
	}
		
	/**
	 * Creates a Language object
	 * @param name The name of the language
	 */
	public Language(String name) {
		this.properties.setProperty(LanguageProperty.NAME, name);
	}
	
	/**
	 * Copy constructor
	 * @param l The language to copy
	 */
	public Language(Language l) {
		properties = new LanguageProperties(l.getProperties());
		phono = new Phonology(l.phono); 
		lexicon = new Lexicon(l.lexicon);
		ortho = new Orthography(l.ortho);
	}
	
	public LanguageProperties getProperties() {
		return properties;
	}
	
	public void setProperties(LanguageProperties properties) {
		this.properties = properties;
	}
	
	/**
	 * Gets a phonology of a language
	 * @return a Phonology object of the language
	 */
	public Phonology getPhono() {
		return phono;
	}
	
	/**
	 * Sets a new phonology for a language
	 * @param phono The Phonology object to be set
	 */
	public void setPhono(Phonology phono) {
		this.phono = phono;
	}
	
	/**
	 * Gets an orthography of a language
	 * @return an Orthography object of the language
	 */
	public Orthography getOrtho() {
		return ortho;
	}
	
	public void setOrtho(Orthography ortho) {
		this.ortho = ortho;
	}	
	
	/**
	 * Gets a lexicon of a language
	 * @return a Lexicon object of the language
	 */
	public Lexicon getLexicon() {
		return lexicon;
	}
	
	/**
	 * Sets a new lexicon for a language
	 * @param lexicon The Lexicon object to be set
	 */
	public void setLexicon(Lexicon lexicon) {
		this.lexicon = lexicon;
	}
	
	/**
	 * Writes a language to a file
	 * @param file The file to write to
	 * @throws IOException Should never be thrown, however would not compile without it. If thrown, something has gone horribly wrong...
	 */
	public void toFile(File file) throws IOException {
		properties.setEdited(Date.from(Instant.now()));
		properties.setProperty(LanguageProperty.VERSION_EDITED, Info.getVersion());
		
		lexicon.checkHomonyms();
		lexicon.checkSynonyms();
		
		String data = "===PHOSYS Start===\n" +
				this.toString() +
				"\n===PHOSYS End===";
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(data);
		bw.close();
	}
	
	/**
	 * Converts a language into a string
	 */
	public String toString() {
		String returnString = "===Meta Start===\n";
		returnString += "utilsVersion:" + properties.getProperty(LanguageProperty.VERSION_EDITED) + "\n" +
				"name:" + properties.getProperty(LanguageProperty.NAME) + "\n" +
				"id:" + properties.getProperty(LanguageProperty.ID) + "\n" +
				"autonym:" + properties.getProperty(LanguageProperty.AUTONYM) + "\n" +
				"timeCreated:" + properties.getCreated().getTime() + "\n" +
				"lastEdited:" + properties.getEdited().getTime() + "\n" +
				"readonly:" + properties.isReadOnly() + "\n" +
				"parent:" + "null" + "\n" +
				"===Meta End===\n" +
				phono.toString() + "\n" +
				ortho.toString() + "\n" +
				lexicon.toString();
		return returnString;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Language) {
			Language l = (Language) obj;
			
			if (l.properties.equals(properties) & l.phono.equals(phono) &
					l.lexicon.equals(lexicon)) {
				/*
				 * Does not check for:
				 * - parent (may be removed in future)
				 * - date edited (will never be equal)
				 * - version edited (may or may not be equal)
				 */
				return true;
			}
		}
		return false;
	}
	
}
