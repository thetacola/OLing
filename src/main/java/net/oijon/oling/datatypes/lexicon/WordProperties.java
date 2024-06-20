package net.oijon.oling.datatypes.lexicon;

import java.time.Instant;
import java.util.Date;

public class WordProperties {

	// 0 = name, 1 = meaning, 2 = pronounciation, 3 = etymology
	private String[] strings = {" ", " ", " ", " "};
	// 0 = creation date, 1 = edit date
	private Date[] dates = {Date.from(Instant.now()), Date.from(Instant.now())};
	
	public WordProperties() {
		
	}
	
	public WordProperties(WordProperties wp) {
		strings[0] = wp.getProperty(WordProperty.NAME);
		strings[1] = wp.getProperty(WordProperty.MEANING);
		strings[2] = wp.getProperty(WordProperty.PRONOUNCIATION);
		strings[3] = wp.getProperty(WordProperty.ETYMOLOGY);
		dates[0] = wp.getCreationDate();
		dates[1] = wp.getEditDate();
	}
	
	public String getProperty(WordProperty wp) {
		switch(wp) {
			case NAME: return strings[0];
			case MEANING: return strings[1];
			case PRONOUNCIATION: return strings[2];
			case ETYMOLOGY: return strings[3];
		}
		return " ";
	}
	public Date getCreationDate() {
		return (Date) dates[0].clone();
	}
	public Date getEditDate() {
		return (Date) dates[1].clone();
	}
	
	public void setProperty(WordProperty wp, String value) {
		switch(wp) {
			case NAME: strings[0] = value;
			case MEANING: strings[1] = value;
			case PRONOUNCIATION: strings[2] = value;
			case ETYMOLOGY: strings[3] = value;
		}
	}
	
	public void setCreationDate(Date creationDate) {
		dates[0] = (Date) creationDate.clone();
	}
	public void setEditDate(Date editedDate) {
		dates[1] = (Date) editedDate.clone();
	}
	
	@Override
	public boolean equals(Object o) {
		// does not check edited date, as that can still be different
		if (o instanceof WordProperties) {
			WordProperties wp = (WordProperties) o;
			if (strings.equals(wp.strings) & dates[0].equals(wp.getCreationDate())) {
				return true;
			}
		}
		return false;
	}
	
}
