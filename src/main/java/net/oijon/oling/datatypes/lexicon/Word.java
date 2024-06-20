package net.oijon.oling.datatypes.lexicon;

import java.util.ArrayList;
import java.util.Date;

import net.oijon.oling.Parser;
import net.oijon.oling.datatypes.tags.Multitag;
import net.oijon.oling.datatypes.tags.Tag;
import net.oijon.olog.Log;

//last edit: 6/20/24 -N3

/**
 * Stores a word, including various properties about the word.
 * @author alex
 *
 */

//TODO: re-add source language
public class Word {

	public static Log log = Parser.getLog();
	
	private WordProperties wp = new WordProperties();
	private ArrayList<String> classes = new ArrayList<String>();
	private ArrayList<Word> synonyms = new ArrayList<Word>();
	private ArrayList<Word> homonyms = new ArrayList<Word>();
	
	/**
	 * Creates a word
	 * @param name The word in question
	 * @param meaning What the word means
	 */
	public Word(String name, String meaning) {
		wp.setProperty(WordProperty.NAME, name);
		wp.setProperty(WordProperty.MEANING, meaning);
		//TODO: automatically get IPA from name via orthography
	}
	
	/**
	 * Copy constructor
	 * @param w The word to be copied
	 */
	public Word(Word w) {
		this.wp = new WordProperties(w.getProperties());
		this.synonyms = new ArrayList<Word>(w.getSynonyms());
		this.homonyms = new ArrayList<Word>(w.getHomonyms());
	}
	
	public WordProperties getProperties() {
		return wp;
	}
	
	public static Word parse(Multitag wordTag) throws Exception {
		Tag valueTag = wordTag.getDirectChild("wordname");
		Tag meaningTag = wordTag.getDirectChild("meaning");
		Word word = new Word(valueTag.value(), meaningTag.value());
		// current tag string very useful for debugging this try/catch here :)
		String currentTag = "";
		try {
			currentTag = "pronounciation";
			Tag pronunciationTag = wordTag.getDirectChild("pronounciation");
			word.getProperties().setProperty(WordProperty.PRONOUNCIATION, pronunciationTag.value());
			currentTag = "etymology";
			Tag etymologyTag = wordTag.getDirectChild("etymology");
			word.getProperties().setProperty(WordProperty.ETYMOLOGY, etymologyTag.value());
			//TODO: Attempt to find ID of source language in Susquehanna folder. If not found, revert to null.
			//Tag sourceLanguageTag = wordTag.getDirectChild("sourceLanguage");
			//word.setSourceLanguage(null);
			currentTag = "creationDate";
			Tag creationDateTag = wordTag.getDirectChild("creationDate");
			word.getProperties().setCreationDate(new Date(Long.parseLong(creationDateTag.value())));
			currentTag = "editDate";
			Tag editDateTag = wordTag.getDirectChild("editDate");
			word.getProperties().setEditDate(new Date(Long.parseLong(editDateTag.value())));
		} catch (Exception e) {
			log.warn("Could not find optional property " + currentTag + " for '" + valueTag.value() + 
					"'. Was this word added manually?");
			e.printStackTrace();
		}
		return word;
	}
	
	public void setProperties(WordProperties wp) {
		this.wp = new WordProperties(wp);
	}
	
	/**
	 * Adds a synonym to a word, unless it has already been added
	 * @param syn The synonym to be added
	 */
	public void addSynonym(Word syn) {
		if (!synonyms.contains(syn)) {
			synonyms.add(syn);
		}
	}
	
	/**
	 * Removes a synonym at index i
	 * @param i The synonym to be removed
	 */
	public void removeSynonym(int i) {
		synonyms.remove(i);
	}
	
	/**
	 * Clears all synonyms from a word
	 */
	public void clearSynonyms() {
		synonyms.clear();
	}
	
	/**
	 * Replaces the synonym list with a new list
	 * @param synonyms The list replacing the old list
	 */
	public void setSynonyms(ArrayList<Word> synonyms) {
		synonyms = this.synonyms;
	}
	
	/**
	 * Gets a list of all synonyms
	 * @return a list of all synonyms
	 */
	public ArrayList<Word> getSynonyms() {
		return synonyms;
	}
	
	/**
	 * Adds a homonym to a word, unless it has already been added
	 * @param hom The homonym to be added
	 */
	public void addHomonym(Word hom) {
		if (!homonyms.contains(hom)) {
			homonyms.add(hom);
		}
	}
	
	/**
	 * Removes a homonym at index i
	 * @param i The homonym to be removed
	 */
	public void removeHomonym(int i) {
		homonyms.remove(i);
	}
	
	/**
	 * Replaces the homonym list with a new list
	 * @param homonyms The list replacing the old list
	 */
	public void setHomonyms(ArrayList<Word> homonyms) {
		homonyms = this.homonyms;
	}
	
	/**
	 * Gets a list of all homonyms
	 * @return a list of all homonyms
	 */
	public ArrayList<Word> getHomonyms() {
		return homonyms;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Word) {
			Word w = (Word) obj;
			if (w.getProperties().equals(wp) & w.synonyms.equals(synonyms) &
					w.homonyms.equals(homonyms)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		String returnString = "===Word Start===\n";
		returnString += "wordname:" + wp.getProperty(WordProperty.NAME) + "\n" +
				"meaning:" + wp.getProperty(WordProperty.MEANING) + "\n" +
				"pronounciation:" + wp.getProperty(WordProperty.PRONOUNCIATION) + "\n" +
				"etymology:" + wp.getProperty(WordProperty.ETYMOLOGY) + "\n" +
				"creationDate:" + wp.getCreationDate().getTime() + "\n" +
				"editDate:" + wp.getEditDate().getTime() + "\n" +
				"===Synonym Start===\n";
		for (int i = 0; i < synonyms.size(); i++) {
			returnString += ":" + synonyms.get(i).getProperties().getProperty(WordProperty.NAME) + "\n";
		}
		returnString += "===Synonym End===\n" +
				"===Homonym Start===\n";
		for (int i = 0; i < homonyms.size(); i++) {
			returnString += ":" + homonyms.get(i).getProperties().getProperty(WordProperty.MEANING) + "\n";
		}
		returnString += "===Homonym End===\n" +
				"===Classes Start===\n";
		for (int i = 0; i < classes.size(); i++) {
			returnString += ":" + classes.get(i) + "\n";
		}
		returnString += "===Classes End===\n" +
				"===Word End===";
		return returnString;
	}
}
