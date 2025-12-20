package net.oijon.oling.datatypes.lexicon;

import java.util.ArrayList;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;
import net.oijon.oling.datatypes.grammar.Gloss;
import net.oijon.olog.Log;

import net.oijon.oling.info.Info;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

//last edit: 12/16/25 -N3

/**
 * The words and meaning of a language
 * @author alex
 *
 */
public class Lexicon implements XMLDatatype {

	private ArrayList<Word> wordList = new ArrayList<Word>();
	
	static Log log = Info.log;
	
	/**
	 * Creates an empty lexicon.
	 */
	public Lexicon() {
		
	}
	
	/**
	 * Creates a lexicon from an ArrayList of words
	 * @param words The ArrayList of words to use.
	 */
	public Lexicon(ArrayList<Word> words) {
		for (int i = 0; i < words.size(); i++) {
			this.addWord(words.get(i));
		}
	}

    /**
     * Creates a lexicon from an XML element
     * @param e The XML element to use
     */
    public Lexicon(Element e) throws InvalidXMLException {
        fromXML(e);
    }

	/**
	 * Copy constructor
	 * @param l The lexicon to copy
	 */
	public Lexicon(Lexicon l) {
		for (int i = 0; i < l.wordList.size(); i++) {
			this.addWord(l.getWord(i));
		}
	}
	
	/**
	 * Adds a word to the lexicon
	 * @param word The word to be added
	 */
	public void addWord(Word word) {
		wordList.add(word);
		checkSynonyms();
		checkHomonyms();
	} 
	
	/**
	 * Removes a word from the lexicon
	 * @param word The word to be removed
	 */
	public void removeWord(Word word) {
		for (int i = 0; i < wordList.size(); i++) {
			Word checkWord = wordList.get(i);
			if (checkWord.getProperties().getProperty(WordProperty.NAME).equals(
					word.getProperties().getProperty(WordProperty.NAME))
					& checkWord.getProperties().getProperty(WordProperty.MEANING).equals(
							word.getProperties().getProperty(WordProperty.MEANING))) {
				wordList.remove(i);
			}
		}
	}
	
	/**
	 * Gets the amount of words in the lexicon
	 * @return The amount of words in the lexicon
	 */
	public int size() {
		return wordList.size();
	}
	
	/**
	 * Gets a word in a lexicon via index number
	 * @param i The index number to use
	 * @return The word at position i
	 */
	public Word getWord(int i) {
		return wordList.get(i);
	}
	
	/**
	 * Checks for synonyms inside the lexicon, and marks them as such.
	 */
	public void checkSynonyms() {
		for (int i = 0; i < wordList.size(); i++) {
			for (int j = 0; j < wordList.size(); j++) {
				if (i != j) {
					if (wordList.get(i).getProperties().getProperty(WordProperty.MEANING).equals(
							wordList.get(j).getProperties().getProperty(WordProperty.MEANING))) {
						wordList.get(i).addSynonym(wordList.get(j));
					}
				}
			}
		}
	}
	
	/**
	 * Checks for homonyms inside the lexicon, and marks them as such.
	 */
	public void checkHomonyms() {
		for (int i = 0; i < wordList.size(); i++) {
			for (int j = 0; j < wordList.size(); j++) {
				if (i != j) {
					if (wordList.get(i).getProperties().getProperty(WordProperty.NAME).equals(
							wordList.get(j).getProperties().getProperty(WordProperty.NAME))) {
						wordList.get(i).addHomonym(wordList.get(j));
					}
				}
			}
		}
	}
	
	@Override
	public String toString() {
		String returnString = "===Lexicon Start===\n";
		for (int i = 0; i < wordList.size(); i++) {
			returnString += wordList.get(i).toString() + "\n";
		}
		returnString += "===Lexicon End===";
		return returnString;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Lexicon) {
			Lexicon l = (Lexicon) obj;
			if (wordList.containsAll(l.wordList) &
					l.wordList.containsAll(wordList)) {
				return true;
			}
		}
		return false;
	}

    @Override
    public Element toXML() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("lexicon");

        for (Word w : this.wordList) {
            root.appendChild(doc.importNode(w.toXML(), true));
        }

        return root;
    }

    @Override
    public void fromXML(Element e) throws InvalidXMLException {
        if (e.getTagName().equals("lexicon")) {
            NodeList nl = e.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i).getNodeName().equals("word") && nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Word w = new Word((Element) nl.item(i));
                    wordList.add(w);
                }
            }
        } else {
            throw new InvalidXMLException("Node name not expected name! Expected: lexicon; Actual: " + e.getTagName());
        }
    }
}
