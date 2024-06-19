package net.oijon.oling.datatypes;

import java.time.Instant;
import java.util.Date;

import net.oijon.oling.info.Info;

public class LanguageProperties {

	private String autonym = "null";
	private String id = "null";
	private String name = "null";
	private boolean isReadOnly = false;
	private Date created = Date.from(Instant.now());
	private Date edited = Date.from(Instant.now());
	private String versionEdited = Info.getVersion();
	
	
	public LanguageProperties() {
		
	}
	
	public LanguageProperties(LanguageProperties lp) {
		this.autonym = lp.getAutonym();
		this.id = lp.getID();
		this.name = lp.getName();
		this.isReadOnly = lp.isReadOnly();
		this.created = lp.getCreated();
		this.edited = lp.getEdited();
		this.versionEdited = lp.getVersionEdited();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof LanguageProperties) {
			LanguageProperties lp = (LanguageProperties) o;
			/*
			 * Does not check for:
			 * - date edited (will never be equal)
			 * - version edited (may or may not be equal)
			 */
			if (lp.getAutonym().equals(autonym) & lp.getID().equals(id) & 
					lp.getName().equals(name) & lp.isReadOnly() == isReadOnly &
					lp.getCreated().equals(created)) {
				return true;
			}
		}
		return false;
	}
	
	public String getAutonym() {
		return autonym;
	}
	public String getID() {
		return id;
	}
	public String getName() {
		return name;
	}
	public boolean isReadOnly() {
		return isReadOnly;
	}
	public Date getCreated() {
		return (Date) created.clone();
	}
	public Date getEdited() {
		return (Date) edited.clone();
	}
	public String getVersionEdited() {
		return versionEdited;
	}
	
	public void setAutonym(String autonym) {
		this.autonym = autonym;
	}
	public void setID(String id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}
	public void setCreated(Date created) {
		this.created = (Date) created.clone();
	}
	public void setEdited(Date edited) {
		this.edited = (Date) edited.clone();
	}
	public void setVersionEdited(String versionEdited) {
		this.versionEdited = versionEdited;
	}
	
}
