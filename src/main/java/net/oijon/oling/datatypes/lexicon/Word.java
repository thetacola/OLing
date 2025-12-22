package net.oijon.oling.datatypes.lexicon;

import java.util.ArrayList;
import java.util.Date;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;
import net.oijon.oling.datatypes.tags.Multitag;
import net.oijon.oling.datatypes.tags.Tag;
import net.oijon.oling.info.Info;
import net.oijon.olog.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

//last edit: 6/20/24 -N3

/**
 * Stores a word, including various properties about the word.
 * @author alex
 *
 */

//TODO: re-add source language
public class Word implements XMLDatatype {

	public static Log log = Info.log;
	
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
     * Creates a word from an XML element.
     * @param e The XML element to use.
     */
    public Word(Element e) throws InvalidXMLException {
        this.fromXML(e);
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
	
	/**
	 * Gets the word properties
	 * @return The properties of the word
	 */
	public WordProperties getProperties() {
		return wp;
	}
	
	/**
	 * Parses a word via a multitag.
	 * If you are using a lexicon, try {@link net.oijon.oling.LegacyParser#parseLexicon()} instead
	 * @param wordTag The multitag to parse
	 * @return A word object from the given multitag
	 * @throws Exception
     * @deprecated since 3.0.0, as OLing now uses XML
	 */
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
	
	/**
	 * Sets metadata for a word
	 * @param wp The metadata for the word
	 */
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
			// does not check for synonyms or homonyms, see issue #4
			if (w.getProperties().equals(wp)) {
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

    @Override
    public Element toXML() throws ParserConfigurationException {
        // FIXME: i somehow mangle word names! eek!

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("word");

        Element name = doc.createElement("name");
        name.appendChild(doc.createTextNode(wp.getProperty(WordProperty.NAME)));

        Element meaning = doc.createElement("meaning");
        meaning.appendChild(doc.createTextNode(wp.getProperty(WordProperty.MEANING)));

        Element pronunciation = doc.createElement("pronunciation");
        pronunciation.appendChild(doc.createTextNode(wp.getProperty(WordProperty.PRONOUNCIATION)));

        Element etymology = doc.createElement("etymology");
        etymology.appendChild(doc.createTextNode(wp.getProperty(WordProperty.ETYMOLOGY)));

        Element created = doc.createElement("timeCreated");
        created.appendChild(doc.createTextNode(wp.getCreationDate().toInstant().toEpochMilli() + ""));

        Element edited = doc.createElement("timeEdited");
        edited.appendChild(doc.createTextNode(wp.getEditDate().toInstant().toEpochMilli() + ""));
        root.appendChild(edited);

        Element synonyms = doc.createElement("synonyms");
        for (Word w : this.synonyms) {
            Element synonym = doc.createElement("synonym");
            synonym.appendChild(doc.createTextNode(w.getProperties().getProperty(WordProperty.NAME)));
            synonyms.appendChild(synonym);
        }

        Element homonyms = doc.createElement("homonyms");
        for (Word w : this.homonyms) {
            Element homonym = doc.createElement("homonym");
            homonym.appendChild(doc.createTextNode(w.getProperties().getProperty(WordProperty.MEANING)));
            homonyms.appendChild(homonym);
        }

        Element classes = doc.createElement("classes");
        for (String s : this.classes) {
            Element wordclass = doc.createElement("class");
            wordclass.appendChild(doc.createTextNode(s));
            classes.appendChild(wordclass);
        }

        root.appendChild(name);
        root.appendChild(meaning);
        root.appendChild(pronunciation);
        root.appendChild(etymology);
        root.appendChild(created);
        root.appendChild(synonyms);
        root.appendChild(homonyms);
        root.appendChild(classes);

        return root;
    }

    @Override
    public void fromXML(Element e) throws InvalidXMLException {
        if (e.getTagName().equals("word")) {
            NodeList nl = e.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                // Note that this doesn't throw an exception if it can't find any of the elements.
                // This is intentional, as they should just be null if that's the case.
                switch (nl.item(i).getNodeName()) {
                    case "name":
                        this.wp.setProperty(WordProperty.NAME, nl.item(i).getTextContent());
                        break;
                    case "meaning":
                        this.wp.setProperty(WordProperty.MEANING, nl.item(i).getTextContent());
                        break;
                    case "pronunciation":
                        this.wp.setProperty(WordProperty.PRONOUNCIATION, nl.item(i).getTextContent());
                        break;
                    case "etymology":
                        this.wp.setProperty(WordProperty.ETYMOLOGY, nl.item(i).getTextContent());
                        break;
                    case "timeCreated":
                        this.wp.setCreationDate(new Date(Long.parseLong(nl.item(i).getTextContent())));
                        break;
                    case "lastEdited":
                        this.wp.setEditDate(new Date(Long.parseLong(nl.item(i).getTextContent())));
                        break;
                    case "classes":
                        NodeList classList = nl.item(i).getChildNodes();
                        for (int j = 0; j < classList.getLength(); j++) {
                            if (classList.item(j).getNodeName().equals("class")) {
                                this.classes.add(classList.item(j).getTextContent());
                            }
                        }
                        break;
                    default:
                        // TODO: add synonyms and homonyms
                }
            }
        } else {
            throw new InvalidXMLException("Node name not expected name! Expected: word; Actual: " + e.getTagName());
        }
    }
}
