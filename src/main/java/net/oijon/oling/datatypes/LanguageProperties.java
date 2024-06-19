package net.oijon.oling.datatypes;

import java.time.Instant;
import java.util.Date;

import net.oijon.oling.Parser;
import net.oijon.oling.info.Info;
import net.oijon.olog.Log;

public class LanguageProperties {

	public static Log log = Parser.getLog();
	
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
	
	/**
	 * Parses language properties from a multitag
	 * @param docTag The multitag to parse from
	 * @return The language properties parsed
	 * @throws Exception Thrown when properties are mangled or invalid
	 */
	public static LanguageProperties parse(Multitag docTag) throws Exception {
		LanguageProperties lp = new LanguageProperties();
		
		Multitag meta = docTag.getMultitag("Meta");
		lp.checkID(meta);
		lp.setName(meta.getDirectChild("name").value());
		lp.setAutonym(meta.getDirectChild("autonym").value());
		lp.setReadOnly(Boolean.parseBoolean(meta.getDirectChild("readonly").value()));
		lp.setCreated(new Date(Long.parseLong(meta.getDirectChild("timeCreated").value())));
		lp.setEdited(new Date(Long.parseLong(meta.getDirectChild("lastEdited").value())));
		Tag ver = new Tag("utilsVersion");
		lp.checkVersion(meta, ver);
		lp.setVersionEdited(ver.value());
		
		return lp;
	}
	
	@SuppressWarnings("deprecation")
	/**
	 * Generates an ID for the language
	 */
	private void generateID() {
		// theoretically this prevents an id from being overwritten
		if (id.equals("null")) {
			int rand = (int) (Math.random() * 100000);
			// "its deprecated" i dont care
			// why does DateTimeFormatter not accept date objects :(
			this.setID(name.toUpperCase() +
					created.getYear() +
					created.getMonth() +
					created.getDay() +
					created.getHours() +
					created.getMinutes() +
					created.getSeconds()
					+ rand);
		}
	}
	
	private void checkID(Multitag meta) {
		Tag id = new Tag("id");
		try {
			id = meta.getDirectChild("id");
			if (!id.value().isBlank() & !id.value().equals("null")) {
				log.info("ID of language is " + id.value());
				this.setID(id.value());
			} else {
				log.err("This language appears to have a blank or null ID!");
				log.warn("Generating new ID, this may break relations with other languages!");
				generateID();
				log.warn("New ID: " + id + ". If other languages are related to this language, "
						+ "a manual switch to the new ID will be neccessary.");
			}
		} catch (Exception e) {
			//TODO: keep old ID instead of generating new one
			log.warn("This language appears to have been created with a very early version of Oijon Utils!");
			log.warn("The id tag was required as of 1.2.0.");
			generateID();
		}
	}
	
	/**
	 * Checks if the version tag is using a deprecated format
	 * @param meta The meta tag
	 * @param ver The version tag
	 * @throws Exception Thrown if neither utilsVersion nor susquehannaVersion exist
	 */
	private void checkVersion(Multitag meta, Tag ver) throws Exception {
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
