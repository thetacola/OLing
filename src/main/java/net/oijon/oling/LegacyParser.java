package net.oijon.oling;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import net.oijon.olog.Log;
import net.oijon.oling.info.Info;
import net.oijon.oling.datatypes.language.Language;
import net.oijon.oling.datatypes.language.LanguageProperties;
import net.oijon.oling.datatypes.language.LanguageProperty;
import net.oijon.oling.datatypes.lexicon.Lexicon;
import net.oijon.oling.datatypes.lexicon.Word;
import net.oijon.oling.datatypes.orthography.Orthography;
import net.oijon.oling.datatypes.phonology.PhonoSystem;
import net.oijon.oling.datatypes.phonology.PhonoTable;
import net.oijon.oling.datatypes.phonology.Phonology;
import net.oijon.oling.datatypes.tags.Multitag;
import net.oijon.oling.datatypes.tags.MultitagUtils;
import net.oijon.oling.datatypes.tags.Tag;

//last edit: 1/13/2025 -N3

/**
 * Parses a .language file, and allows various parts to be accessed
 * @author alex
 * @deprecated as of 3.0.0, as files are now stored via XML
 */
public class LegacyParser {
	
	public static Log log = Info.log;
	private Multitag tag;
	
	/**
	 * Creates an object to hold the contents of a .language structured string
	 * @param input The string to be parsed.
	 */
	public LegacyParser(String input) {
		initString(input);
	}
	
	/**
	 * Creates an object to hold the contents of a .language structured file
	 * @param file The file to be read
	 */
	public LegacyParser(File file) {
		// check for converted .xml file existing already
		File parentDir = file.getParentFile();
		File estimatedConvertedFile = new File(parentDir + file.getName().replace(".language", "-converted.xml"));
		
		if (estimatedConvertedFile.exists()) {
			log.warn("You are parsing from a legacy file when a converted file already exists!");
			log.warn("The converted file can be found at " + estimatedConvertedFile.toString() + ".");
		}
		
		// even if there's already a converted file, let's assume that the 
		// person parsing a legacy file knows what they're doing
		log.setDebug(true);
		try {
			Scanner scanner = new Scanner(file);
			String wholeFile = "";
			while (scanner.hasNextLine()) {
				wholeFile += scanner.nextLine() + "\n";
			}
			scanner.close();
			initString(wholeFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.critical("File " + file.toString() + " not found! Cannot parse.");
		}
		
	}
	
	private void initString(String input) {
		log.warn("You are using the legacy parser for files made before OLing 3!");
		log.warn("If this is parsing from a file, a converted file will be made.");
		input.replace("	", "");
		String[] splitLines = input.split("\n");
		/**
		 * New plan:
		 * Because the entire file has to be in a PHOSYS tag, just use that with parseMulti :)
		 */
		if (splitLines[0].equals("===PHOSYS Start===")) {
			parseMulti(input);
		} else {
			log.err("Input is not a valid PHOSYS file!");
		}
	}
	
	/**
	 * Gets the containing tag, including all subtags
	 * @return The containing tag
	 */
	public Multitag getPHOSYSTag() {
		return this.tag;
	}	
	
	/**
	 * Checks if a closing tag is the correct tag for a given tag name
	 * @param line The line to check for a closing tag
	 * @param name The name of the expected closing tag
	 * @return true if line is the expected closing tag, false if either not a closing tag or a closing tag for a different multitag
	 */
	private boolean isCloseForName(String line, String name) {
		if (MultitagUtils.isMultitagEnd(line)) {
			if (MultitagUtils.getMarkerTagName(line).equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if the line provided is representative of a Tag object
	 * @param line The line to check
	 * @return true if is in the format of a Tag in string form, false otherwise
	 */
	private boolean isDataTag(String line) {
		String[] splitColon = line.split(":");
		if (splitColon.length == 2) {
			return true;
		}
		return false;
	}
	
	/**
	 * Parses a single tag from a line.
	 * @param line The line of the file to parse from
	 * @return A Tag object that the line represents
	 */
	private Tag parseSingle(String line) {
		if (isDataTag(line)) {
			String[] splitColon = line.split(":");
			// Extracts the name of the tag
			String name = splitColon[0];
			// Extracts the data of the tag
			String data = splitColon[1];
			// Creates a tag object in memory
			Tag childTag = new Tag(name, data);
			// Adds tag object to parent
			return childTag;
		} else {
			return null;
		}
	}
	
	/**
	 * Parses a multitag from a .language structured string
	 * @param input The .language structured string to be read
	 * @return A multitag object with all data inside.
	 * 
	 * This method has a history of being spaghetti code. Although it
	 * has been cleaned up, each line will still remain commented for future
	 * use. 
	 */
	private Multitag parseMulti(String input) {
		// Removes tabs from input. Planned addition, but never added...
		input = input.replace("	", "");
		// Splits each line based off line breaks
		String[] splitLines = input.split("\n");
		// Gets the tag name from the start tag. This removes the beginning '==='
		String tagName = MultitagUtils.getMarkerTagName(splitLines[0]);
		
		// This creates a new Multitag in memory named after the tag just named
		Multitag tag = new Multitag(tagName);
		// Loop over each line in file
		for (int i = 1; i < splitLines.length; i++) {
			// Checks if the line matches the pattern of a multitag start marker
			if (MultitagUtils.isMultitagStart(splitLines[i])) {
				tag.addMultitag(readChildMultitag(splitLines, i));
			// Checks if line is named tag
			} else if (isDataTag(splitLines[i])) {
				// Adds tag object to parent
				tag.addTag(parseSingle(splitLines[i]));
			// Checks if line is nameless (weird, but happens in very old .language files)
			} else if (splitLines[i] != "") {
				// Creates a tag with no name in memory
				Tag childTag = new Tag("", splitLines[i]);
				// Adds tag object to parent
				tag.addTag(childTag);
			}
		}
		// Not sure what this is doing, probably setting parent for future multitags?
		this.tag = tag;
		// Returns the parsed multitag
		return tag;
	}
	
	private Multitag readChildMultitag(String[] splitLines, int startLine) {
		// Gets the name of the start marker
		String name = MultitagUtils.getMarkerTagName(splitLines[startLine]);
		// Gets the line number the tag was found on
		int lineNum = startLine + 1;
		// Creates a variable for the loop below
		String tagInput = "";
		// Loops over the lines in the file, starting at the start marker
		for (int j = startLine; j < splitLines.length; j++) {
			// Checks if a line is an end marker
			if (isCloseForName(splitLines[j], name)){
				// Starts parsing tagInput
				Multitag childTag = parseMulti(tagInput);
				// Adds the parsed multitag to the current tag
				return childTag;
			// Checks if at the end of the file
			} else if (j == splitLines.length - 1) {
				// Logs if at end of file and close has not been found
				log.err("Tag " + name + " on line " + lineNum + " is not closed!");
			// Line is not an end marker nor the 
			} else {
				// Adds line to tagInput if it is not an end marker
				tagInput += splitLines[j] + "\n";
			}
		}
		return null;
	}
	
	/**
	 * Parses a phonology system from a Parser
	 * @return A PhonoSystem object with data from the Parser.
	 * @throws Exception Thrown when a phonology system could not be found
	 */
	public PhonoSystem parsePhonoSys() throws Exception {
		try {
			Multitag tablelist = this.tag.getMultitag("Tablelist");
			Tag diacriticList;
			try {
				diacriticList = tablelist.getDirectChild("diacriticList");
			} catch (Exception e) {
				log.warn(e.toString());
				diacriticList = new Tag("diacriticList", "");
			}			
			PhonoSystem phonoSystem = new PhonoSystem(tablelist.getDirectChild("tablelistName").value());
			ArrayList<String> diacritics = new ArrayList<String>(Arrays.asList(diacriticList.value().split(",")));
			phonoSystem.setDiacritics(diacritics);
			for (int i = 0; i < tablelist.getSubMultitags().size(); i++) {
				if (tablelist.getSubMultitags().get(i).getName().equals("PhonoTable")) {
					Multitag phonoTableTag = tablelist.getSubMultitags().get(i);
					PhonoTable phonoTable = PhonoTable.parse(phonoTableTag);
					phonoSystem.getTables().add(phonoTable);
				}
			}
			return phonoSystem;
		} catch (Exception e) {
			log.err(e.toString());
			throw e;
		}
	}
	
	/**
	 * Parses a phonology from a Parser
	 * @return A Phonology object with data from the Parser.
	 * @throws Exception Thrown when a phonology could not be found
	 */
	public Phonology parsePhono() throws Exception {
		try {
			PhonoSystem phonoSystem = parsePhonoSys();
			Multitag phonoTag = this.tag.getMultitag("Phonology");
			Tag soundListTag = phonoTag.getDirectChild("soundlist");
			String soundData = soundListTag.value();
			String[] soundList = soundData.split(",");
			// TODO: parse phonotactics
			Phonology phono = new Phonology(soundList, phonoSystem);
			return phono;
		} catch (Exception e) {
			e.printStackTrace();
			log.err(e.toString());
			throw e;
		}
	}
	
	/**
	 * Parses a language from a Parser
	 * @return A Language object with data from the Parser.
	 * @throws Exception Thrown when a language could not be found
	 */
	public Language parseLanguage() throws Exception {
		// parse language properties, as a name is required
		LanguageProperties lp = LanguageProperties.parse(this.tag);
		Language lang = new Language(lp.getProperty(LanguageProperty.NAME));		
				
		// parse each property
		lang.setPhono(parsePhono());
		lang.setOrtho(parseOrtho());
		lang.setLexicon(parseLexicon());
		lang.setProperties(lp);
		return lang;
	}
	
	public Orthography parseOrtho() {
		try {
			Orthography ortho = new Orthography();
			Multitag orthoTag = this.tag.getMultitag("Orthography");
			ArrayList<Tag> orthoPairs = orthoTag.getSubtags();
			for (int i = 0; i < orthoPairs.size(); i++) {
				Tag op = orthoPairs.get(i);
				ortho.add(op.getName(), op.value());
			}
			return ortho;
		} catch (Exception e) {
			log.err("No orthography found! Has one been created? Returning a blank orthography...");
			return new Orthography();
		}
	}
	
	/**
	 * Parses a lexicon from a Parser
	 * @return A Lexicon object with data from the Parser.
	 */
	public Lexicon parseLexicon() {
		try {
			Lexicon lexicon = new Lexicon();
			Multitag lexiconTag = this.tag.getMultitag("Lexicon");
			ArrayList<Multitag> wordList = lexiconTag.getSubMultitags();
			for (int i = 0; i < wordList.size(); i++) {
				if (wordList.get(i).getName().equals("Word")) {
					Multitag wordTag = wordList.get(i);
					lexicon.addWord(Word.parse(wordTag));
				}
			}
			return lexicon;
		} catch (Exception e) {
			log.err("No lexicon found! Has one been created? Returning a blank lexicon...");
			return new Lexicon();
		}
	}
	
	/**
	 * Gets the log the parser outputs to. Useful for parse methods in data types.
	 * @return The log the parser is outputting to.
	 */
	public static Log getLog() {
		return log;
	}
}
