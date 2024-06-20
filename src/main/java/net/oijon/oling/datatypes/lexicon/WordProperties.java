package net.oijon.oling.datatypes.lexicon;

import java.time.Instant;
import java.util.Date;

public class WordProperties {

	// 0 = name, 1 = meaning, 2 = pronounciation, 3 = etymology
	private String[] strings = {"", "", "", ""};
	// 0 = creation date, 1 = edit date
	private Date[] dates = {Date.from(Instant.now()), Date.from(Instant.now())};
	
	public WordProperties() {
		
	}
	
	public WordProperties(WordProperties wp) {
		strings[0] = wp.getName();
		strings[1] = wp.getMeaning();
		strings[2] = wp.getPronounciation();
		strings[3] = wp.getEtymology();
		dates[0] = wp.getCreationDate();
		dates[1] = wp.getEditDate();
	}
	
	public String getName() {
		return strings[0];
	}
	public String getMeaning() {
		return strings[1];
	}
	public String getPronounciation() {
		return strings[2];
	}
	public String getEtymology() {
		return strings[3];
	}
	public Date getCreationDate() {
		return (Date) dates[0].clone();
	}
	public Date getEditDate() {
		return (Date) dates[1].clone();
	}
	
	public void setName(String name) {
		strings[0] = name;
	}
	public void setMeaning(String meaning) {
		strings[1] = meaning;
	}
	public void setPronounciation(String meaning) {
		strings[2] = meaning;
	}
	public void setEtymology(String etymology) {
		strings[3] = etymology;
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
