package net.oijon.oling.datatypes.lexicon;

import java.time.Instant;
import java.util.Date;

import net.oijon.oling.Parser;
import net.oijon.olog.Log;

/**
 * Metadata for word objects
 * @author alex
 */
public class WordProperties {

	public Log log = Parser.getLog();
	// 0 = name, 1 = meaning, 2 = pronounciation, 3 = etymology
	private String[] strings = {" ", " ", " ", " "};
	// 0 = creation date, 1 = edit date
	private Date[] dates = {Date.from(Instant.now()), Date.from(Instant.now())};
	
	/**
	 * Creates empty metadata
	 */
	public WordProperties() {
		
	}
	
	/**
	 * Copy constructor
	 * @param wp The properties to copy from
	 */
	public WordProperties(WordProperties wp) {
		strings[0] = wp.getProperty(WordProperty.NAME);
		strings[1] = wp.getProperty(WordProperty.MEANING);
		strings[2] = wp.getProperty(WordProperty.PRONOUNCIATION);
		strings[3] = wp.getProperty(WordProperty.ETYMOLOGY);
		dates[0] = wp.getCreationDate();
		dates[1] = wp.getEditDate();
	}
	
	/**
	 * Gets a given property from metadata
	 * @param wp The property to get
	 * @return The value of the given property
	 */
	public String getProperty(WordProperty wp) {
		switch(wp) {
			case NAME: return strings[0];
			case MEANING: return strings[1];
			case PRONOUNCIATION: return strings[2];
			case ETYMOLOGY: return strings[3];
		}
		return " ";
	}
	
	/**
	 * Gets the creation date of a word. 
	 * @return The date the word was created
	 */
	public Date getCreationDate() {
		return (Date) dates[0].clone();
	}
	
	/**
	 * Gets the edit date of a word
	 * @return The date the word was last edited
	 */
	public Date getEditDate() {
		return (Date) dates[1].clone();
	}
	
	/**
	 * Sets a property with a given value and property.
	 * Note that this does not work for dates.
	 * @param wp The property to change
	 * @param value The new value of the property
	 */
	public void setProperty(WordProperty wp, String value) {
		switch(wp) {
			case NAME: strings[0] = value;
			break;
			case MEANING: strings[1] = value;
			break;
			case PRONOUNCIATION: strings[2] = value;
			break;
			case ETYMOLOGY: strings[3] = value;
			break;
		}
	}
	
	/**
	 * Sets the creation date of the word
	 * @param creationDate The date the word was created
	 */
	public void setCreationDate(Date creationDate) {
		dates[0] = (Date) creationDate.clone();
	}
	/**
	 * Sets the edit date of the word
	 * @param editedDate The date the word was last edited
	 */
	public void setEditDate(Date editedDate) {
		dates[1] = (Date) editedDate.clone();
	}
	
	@Override
	public boolean equals(Object o) {
		// does not check edited date, as that can still be different
		if (o instanceof WordProperties) {
			WordProperties wp = (WordProperties) o;
			for (int i = 0; i < strings.length; i++) {
				if (!strings[i].equals(wp.strings[i])) {
					return false;
				}
			}
			for (int i = 0; i < dates.length; i++) {
				if (!dates[i].equals(wp.dates[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
}
