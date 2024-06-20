package net.oijon.oling.datatypes.lexicon;

import java.util.ArrayList;

//last edit: 6/20/24 -N3

/**
 * Stores a word, including various properties about the word.
 * @author alex
 *
 */

//TODO: re-add source language
public class Word {

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
		wp.setName(name);
		wp.setMeaning(meaning);
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
		returnString += "wordname:" + wp.getName() + "\n" +
				"meaning:" + wp.getMeaning() + "\n" +
				"pronounciation:" + wp.getPronounciation() + "\n" +
				"etymology:" + wp.getEtymology() + "\n" +
				"creationDate:" + wp.getCreationDate().getTime() + "\n" +
				"editDate:" + wp.getEditDate().getTime() + "\n" +
				"===Synonym Start===\n";
		for (int i = 0; i < synonyms.size(); i++) {
			returnString += ":" + synonyms.get(i).getProperties().getName() + "\n";
		}
		returnString += "===Synonym End===\n" +
				"===Homonym Start===\n";
		for (int i = 0; i < homonyms.size(); i++) {
			returnString += ":" + homonyms.get(i).getProperties().getMeaning() + "\n";
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
