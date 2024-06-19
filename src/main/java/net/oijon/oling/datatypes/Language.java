package net.oijon.oling.datatypes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import net.oijon.olog.Log;

import net.oijon.oling.info.Info;
import net.oijon.oling.Parser;

//last edit: 6/19/24 -N3

/**
 * Bundles all parts of a language together into one object
 * @author alex
 *
 */
public class Language {

	public static Log log = Parser.getLog();
	public static final Language NULL = new Language("null");
	
	//private String autonym = "null";
	//private String id = "null";
	//private String name = "null";
	private LanguageProperties properties = new LanguageProperties();
	private Phonology phono = new Phonology();
	private Lexicon lexicon = new Lexicon();
	private Orthography ortho = new Orthography();
	private Language parent = Language.NULL;
	//private boolean isReadOnly = false;
	//private Date created = Date.from(Instant.now());
	//private Date edited = Date.from(Instant.now());
	//private String versionEdited = Info.getVersion();
	
	private static File[] generateFiles(File startDir) {
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
        File[] files = startDir.listFiles(filter);
        return files;
	}
	
	/**
	 * Gets all the .language files in the Susquehanna directory
	 * @return A list of .language files
	 * @deprecated as of v1.1.2, as this was Oijon Susquehanna-specific. Please use {@link #getLanguageFiles(File) getLanguageFiles(File)} instead. 
	 */
	@Deprecated
	public static File[] getLanguageFiles() {
		File[] files;
		try {
            File f = new File(System.getProperty("user.home") + "/Susquehanna/");
            files = generateFiles(f);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            files = null;
        }
		return files;
	}
	
	/**
	 * Gets a list of all .language files in a specified directory. Does not currently support looking into subdirectories.
	 * @param f The directory to look in
	 * @return A list of all .language files in a specified directory.
	 */
	public static File[] getLanguageFiles(File f) {
		//TODO: Search in subdirectories as well
		File[] files;
		try {
            files = generateFiles(f);
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
		this.properties.setName(name);
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
		parent = new Language(l.parent);
	}
	
	
	public static Language parse(Multitag docTag) throws Exception {
		Multitag meta = docTag.getMultitag("Meta");
		Tag ver = new Tag("utilsVersion");
		try {
			ver = meta.getDirectChild("utilsVersion");
			if (!ver.value().isBlank()) {
				log.info("Language created with " + ver.value());
			}
		} catch (Exception e) {
			ver = meta.getDirectChild("susquehannaVersion");
			if (!ver.value().isBlank()) {
				log.info("Language created with " + ver.value());
			}
			log.warn("This language appears to have been created with a very early version of Oijon Utils!");
			log.warn("The susquehannaVersion tag was deprecated as of 1.2.0.");
		}
		
		// properties that must be parsed before anything else can be parsed get put up here
		Tag parent = meta.getDirectChild("parent");
		Language lang = new Language(meta.getDirectChild("name").value());
		
		Tag id = new Tag("id");
		try {
			id = meta.getDirectChild("id");
			if (!id.value().isBlank() & !id.value().equals("null")) {
				log.info("ID of language is " + id.value());
				lang.setID(id.value());
			} else {
				log.err("This language appears to have a blank or null ID!");
				log.warn("Generating new ID, this may break relations with other languages!");
				lang.generateID();
				log.warn("New ID: " + lang.getID() + ". If other languages are related to this language, "
						+ "a manual switch to the new ID will be neccessary.");
			}
		} catch (Exception e) {
			log.warn("This language appears to have been created with a very early version of Oijon Utils!");
			log.warn("The id tag was required as of 1.2.0.");
			lang.generateID();
		}
		
		// parse each property
		lang.setPhono(Phonology.parse(docTag));
		lang.setOrtho(Orthography.parse(docTag));
		lang.setLexicon(Lexicon.parse(docTag));
		lang.setCreated(new Date(Long.parseLong(meta.getDirectChild("timeCreated").value())));
		lang.setEdited(new Date(Long.parseLong(meta.getDirectChild("lastEdited").value())));
		lang.setAutonym(meta.getDirectChild("autonym").value());
		lang.setReadOnly(Boolean.parseBoolean(meta.getDirectChild("readonly").value()));
		// TODO: remove this/change to planned ID system
		lang.setParent(new Language(parent.value()));
		lang.setVersion(ver.value());
		return lang;
	}
	
	public LanguageProperties getProperties() {
		return properties;
	}
	
	public void setProperties(LanguageProperties properties) {
		this.properties = properties;
	}
	
	/**
	 * Gets the name of a Language object
	 * @return The name of the language
	 * @deprecated as of 1.0.4, please use getProperties().getName() instead
	 */
	public String getName() {
		return properties.getName();
	}
	
	/**
	 * Gets the autonym of a language
	 * @return The autonym of the language
	 * @deprecated as of 1.0.4, please use getProperties().getAutonym() instead
	 */
	public String getAutonym() {
		return properties.getAutonym();
	}
	
	/**
	 * Sets an autonym for a language
	 * @param autonym The autonym to be set
	 * @deprecated as of 1.0.4, please use getProperties().setAutonym() instead
	 */
	public void setAutonym(String autonym) {
		properties.setAutonym(autonym);
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
	 * Gets the parent language of a language (for example, the parent language of a dialect)
	 * This method used to be deprecated, however no longer is.
	 * @return a Language object representing the parent language
	 */
	public Language getParent() {
		return parent;
	}
	
	/**
	 * Gets the name of the parent language. Please note that this will most likely return null.
	 * @return The name of the parent language
	 */
	public String getParentName() {
		return parent.getName();
	}
	
	/**
	 * Sets a parent language of a language
	 * @param parent The language to be the parent
	 * @deprecated as of v1.1.2, as this just straight up does not work currently. The way this is currently set, an *entire language* would be stored for each parent. 
	 * 		If we, say, stored English like this, we would have language files going all the way back to Proto-Indo-European, which would be a disaster file-size  and memory wise.
	 * 		Please use {@link setParent(String, File) setParent(String, File)} instead.
	 */
	public void setParent(Language parent) {
		this.parent = parent;
	}
	
	/**
	 * Sets a parent language of a language
	 * @param parentName The name of the parent language.
	 * @param dir The directory that the language is in.
	 */
	public void setParent(String parentName, File dir) {
		File[] potentialFiles = getLanguageFiles(dir);
		for (int i = 0; i < potentialFiles.length; i++) {
			Parser parser = new Parser(potentialFiles[i]);
			try {
				Language parent = parser.parseLanguage();
				if (parent.getName().equals(parentName)) {
					this.parent = parent;
					return;
				}
			} catch (Exception e) {
				
			}
		}
	}
	/**
	 * Gets the datetime when the language was created.
	 * @return The datetime when the language was created.
	 * @deprecated as of 1.0.4, please use getProperties().getCreated() instead
	 */
	public Date getCreated() {
		return properties.getCreated();
	}
	/**
	 * Sets the creation date for a Language. Should only be used for reading in files, should not be used for writing to files.
	 * @param date The datetime when the language was created.
	 * @deprecated as of 1.0.4, please use getProperties().setCreated() instead
	 */
	public void setCreated(Date date) {
		properties.setCreated(date);
	}
	/**
	 * Gets the last edit date of a language.
	 * @return The datetime when the language was last edited.
	 * @deprecated as of 1.0.4, please use getProperties().getEdited() instead
	 */
	public Date getEdited() {
		return properties.getEdited();
	}
	/**
	 * Sets the last edit date of a language. Should be used when changing anything about a language.
	 * @param date The datetime (preferably the exact time the method was called) that the language was last edited.
	 * @deprecated as of 1.0.4, please use getProperties().setEdited() instead
	 */
	public void setEdited(Date date) {
		properties.setEdited(date);
	}
	/**
	 * Checks if the language is flagged as read-only
	 * @return true if read-only, false otherwise.
	 * @deprecated as of 1.0.4, please use getProperties().isReadOnly() instead
	 */
	public boolean isReadOnly() {
		return properties.isReadOnly();
	}
	/**
	 * Sets the read-only status of a language
	 * Please note! This only makes it so that Utils will not edit it, however that cannot be said for the file itself.
	 * @param bool The read-only status desired
	 * @deprecated as of 1.0.4, please use getProperties().setReadOnly() instead
	 */
	public void setReadOnly(boolean bool) {
		properties.setReadOnly(bool);
	}
	/**
	 * Gets the version that the language was last edited in.
	 * @return The version the language was last edited in.
	 * @deprecated as of 1.0.4, please use getProperties().getVersionEdited() instead
	 */
	public String getVersion() {
		return properties.getVersionEdited();
	}
	/**
	 * Sets the version that the language was last edited in. Should be called after every edit.
	 * @param version The version the language was last edited in.
	 * @deprecated as of 1.0.4, please use getProperties().setVersionEdited() instead
	 */
	public void setVersion(String version) {
		properties.setVersionEdited(version);
	}
	
	/**
	 * Gets the ID of a language.
	 * @return The ID of the language
	 * @deprecated as of 1.0.4, please use getProperties().getID() instead
	 */
	public String getID() {
		return properties.getID();
	}
	
	/**
	 * Sets the ID of a language. Should only really be used when creating object
	 * from file or copying object, changing the ID of a pre-established language
	 * can cause some schenanigans
	 * @param id The new ID of the language
	 * @deprecated as of 1.0.4, please use getProperties().setID() instead
	 */
	public void setID(String id) {
		properties.setID(id);
	}
	
	/**
	 * Writes a language to a file
	 * @param file The file to write to
	 * @throws IOException Should never be thrown, however would not compile without it. If thrown, something has gone horribly wrong...
	 */
	public void toFile(File file) throws IOException {
		properties.setEdited(Date.from(Instant.now()));
		properties.setVersionEdited(Info.getVersion());
		
		lexicon.checkHomonyms();
		lexicon.checkSynonyms();
		
		String data = "===PHOSYS Start===\n";
		data += this.toString();
		data += "\n===PHOSYS End===";
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(data);
		bw.close();
	}
	
	@SuppressWarnings("deprecation")
	/**
	 * Generates an ID for the language
	 */
	public void generateID() {
		// theoretically this prevents an id from being overwritten
		if (properties.getID().equals("null")) {
			int rand = (int) (Math.random() * 100000);
			// "its deprecated" i dont care
			// why does DateTimeFormatter not accept date objects :(
			properties.setID(properties.getName().toUpperCase() +
					properties.getCreated().getYear() +
					properties.getCreated().getMonth() +
					properties.getCreated().getDay() +
					properties.getCreated().getHours() +
					properties.getCreated().getMinutes() +
					properties.getCreated().getSeconds()
					+ rand);
		}
	}
	
	/**
	 * Converts a language into a string
	 */
	public String toString() {
		String returnString = "===Meta Start===\n";
		returnString += "utilsVersion:" + properties.getVersionEdited() + "\n";
		returnString += "name:" + properties.getName() + "\n";
		returnString += "id:" + properties.getID() + "\n";
		returnString += "autonym:" + properties.getAutonym() + "\n";
		returnString += "timeCreated:" + properties.getCreated().getTime() + "\n";
		returnString += "lastEdited:" + properties.getEdited().getTime() + "\n";
		returnString += "readonly:" + properties.isReadOnly() + "\n";
		returnString += "parent:" + parent.getName() + "\n";
		returnString += "===Meta End===\n";
		returnString += phono.toString() + "\n";
		returnString += ortho.toString() + "\n";
		returnString += lexicon.toString();
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
