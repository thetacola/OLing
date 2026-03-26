package net.oijon.oling.datatypes.language;

import java.time.Instant;
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

/**
 * Bundles metadata about a language into one object
 * @author alex
 */
public class LanguageProperties implements XMLDatatype {

	public static Log log = Info.log;
	
	// 0 = autonym, 1 = id, 2 = name, 3 = versionEdited
	private String[] strings = {"null", "null", "null", Info.getVersion()};	
	// 0 = created, 1 = edited
	private Date[] dates = {Date.from(Instant.now()), Date.from(Instant.now())};
	
	private boolean isReadOnly = false;
	
	public LanguageProperties() {
		
	}

    public LanguageProperties(Element e) throws InvalidXMLException {
        fromXML(e);
    }

	public LanguageProperties(LanguageProperties lp) {
		this.isReadOnly = lp.isReadOnly();
		this.strings[0] = lp.getProperty(LanguageProperty.AUTONYM);
		this.strings[1] = lp.getProperty(LanguageProperty.ID);
		this.strings[2] = lp.getProperty(LanguageProperty.NAME);
		this.strings[3] = lp.getProperty(LanguageProperty.VERSION_EDITED);
		this.dates[0] = lp.getCreated();
		this.dates[1] = lp.getEdited();
	}
	
	/**
	 * Parses language properties from a multitag
	 * @param docTag The multitag to parse from
	 * @return The language properties parsed
	 * @throws Exception Thrown when properties are mangled or invalid
	 * @deprecated As of OLing 3.0.0, this requires the legacy file format
	 */
	public static LanguageProperties parse(Multitag docTag) throws Exception {
		LanguageProperties lp = new LanguageProperties();
		
		Multitag meta = docTag.getMultitag("Meta");
		lp.setProperty(LanguageProperty.NAME, meta.getDirectChild("name").value());
		lp.checkID(meta);
		lp.setProperty(LanguageProperty.AUTONYM, meta.getDirectChild("autonym").value());
		lp.setReadOnly(Boolean.parseBoolean(meta.getDirectChild("readonly").value()));
		lp.setCreated(new Date(Long.parseLong(meta.getDirectChild("timeCreated").value())));
		lp.setEdited(new Date(Long.parseLong(meta.getDirectChild("lastEdited").value())));
		lp.checkVersion(meta);
		return lp;
	}
	
	@SuppressWarnings("deprecation")
	/**
	 * Generates an ID for the language
	 */
	private void generateID() {
		int rand = (int) (Math.random() * 100000);
		// "its deprecated" i dont care
		// why does DateTimeFormatter not accept date objects :(
		Date created = dates[0];
		this.setProperty(LanguageProperty.ID, strings[2].toUpperCase() +
				created.getYear() +
				created.getMonth() +
				created.getDay() +
				created.getHours() +
				created.getMinutes() +
				created.getSeconds()
				+ rand);
	}
	
	/**
	 * Checks if the ID tag is using an old, unsupported version. Useful for backwards-compatibility.
	 * @deprecated as of OLing v3.0.0, as this uses the old file format.
	 * @param meta
	 */
	private void checkID(Multitag meta) {
		Tag id = new Tag("id");
		try {
			id = meta.getDirectChild("id");
			if (!id.value().isBlank() && !id.value().equals("null")) {
				log.info("ID of language is " + id.value());
				this.setProperty(LanguageProperty.ID, id.value());
			} else {
				log.err("This language appears to have a blank or null ID!");
				log.warn("Generating new ID, this may break relations with other languages!");
				generateID();
				log.warn("New ID: " + this.getProperty(LanguageProperty.ID) + ". If other languages are related to this language, "
						+ "a manual switch to the new ID will be neccessary.");
			}
		} catch (Exception e) {
			log.warn("This language appears to have been created with a very early version of Oijon Utils!");
			log.warn("The id tag was required as of 1.2.0.");
			generateID();
		}
	}
	
	/**
	 * Checks if the version tag is using a deprecated format
	 * @param meta The meta tag
	 * @throws Exception Thrown if neither utilsVersion nor susquehannaVersion exist
	 * @deprecated as of OLing v3.0.0, as this uses the old file format.
	 */
	private void checkVersion(Multitag meta) throws Exception {
		Tag ver;
		try {
			ver = meta.getDirectChild("utilsVersion");
			if (!ver.value().isBlank()) {
				log.info("Language created with " + ver.value());
				setProperty(LanguageProperty.VERSION_EDITED, ver.value());
			}
		} catch (Exception e) {
			ver = meta.getDirectChild("susquehannaVersion");
			if (!ver.value().isBlank()) {
				log.info("Language created with " + ver.value());
				setProperty(LanguageProperty.VERSION_EDITED, ver.value());
			}
			log.warn("This language appears to have been created with a very early version of Oijon Utils!");
			log.warn("The susquehannaVersion tag was deprecated as of Oijon Utils 1.2.0.");
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
			if (lp.getProperty(LanguageProperty.AUTONYM).equals(strings[0]) && 
					lp.getProperty(LanguageProperty.ID).equals(strings[1]) && 
					lp.getProperty(LanguageProperty.NAME).equals(strings[2]) &
					lp.isReadOnly() == isReadOnly &
					lp.getCreated().equals(dates[0])) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets the value of a property
	 * @param p The name of the property
	 * @return The value of the property
	 */
	public String getProperty(LanguageProperty p) {
		switch(p) {
			case AUTONYM: return strings[0];
			case ID: return strings[1];
			case NAME: return strings[2];
			case VERSION_EDITED: return strings[3];
		}
		return " ";
	}
	
	/**
	 * Sets the value of a property
	 * @param p The property to set the value of
	 * @param value The value to change the property to
	 */
	public void setProperty(LanguageProperty p, String value) {
		switch(p) {
			case AUTONYM: strings[0] = value;
			break;
			case ID: strings[1] = value;
			break;
			case NAME: strings[2] = value;
			break;
			case VERSION_EDITED: strings[3] = value;
			break;
		}
	}
	
	/**
	 * Check if the language is marked as read only
	 * @return true if read-only, false if not
	 */
	public boolean isReadOnly() {
		return isReadOnly;
	}
	/**
	 * Gets the date the language was created
	 * @return The date the language was created
	 */
	public Date getCreated() {
		return (Date) dates[0].clone();
	}
	/**
	 * Gets the date the language was last edited
	 * @return The date the language was last edited
	 */
	public Date getEdited() {
		return (Date) dates[1].clone();
	}
	/**
	 * Set read only status
	 * @param isReadOnly The read only status to change to
	 */
	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}
	/**
	 * Set the date the language was created
	 * @param created The date the language was created
	 */
	public void setCreated(Date created) {
		this.dates[0] = (Date) created.clone();
	}
	/**
	 * Set the date the language was last edited
	 * @param edited The date the language was last edited
	 */
	public void setEdited(Date edited) {
		this.dates[1] = (Date) edited.clone();
	}

    @Override
    public Element toXML() throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("meta");

        Element ver = doc.createElement("version");
        ver.appendChild(doc.createTextNode(getProperty(LanguageProperty.VERSION_EDITED)));
        root.appendChild(ver);

        Element name = doc.createElement("name");
        name.appendChild(doc.createTextNode(getProperty(LanguageProperty.NAME)));
        root.appendChild(name);

        Element id = doc.createElement("id");
        id.appendChild(doc.createTextNode(getProperty(LanguageProperty.ID)));
        root.appendChild(id);

        Element autonym = doc.createElement("autonym");
        autonym.appendChild(doc.createTextNode(getProperty(LanguageProperty.AUTONYM)));
        root.appendChild(autonym);

        Element timeCreated = doc.createElement("timeCreated");
        timeCreated.appendChild(doc.createTextNode(getCreated().toInstant().toEpochMilli() + ""));
        root.appendChild(timeCreated);

        Element lastEdited = doc.createElement("lastEdited");
        lastEdited.appendChild(doc.createTextNode(getEdited().toInstant().toEpochMilli() + ""));
        root.appendChild(lastEdited);

        Element readonly = doc.createElement("readonly");
        readonly.appendChild(doc.createTextNode(isReadOnly + ""));
        root.appendChild(readonly);

        // TODO: add parent by language ID
        Element parent = doc.createElement("parent");
        parent.appendChild(doc.createTextNode(""));
        root.appendChild(parent);


        return root;
    }

    @Override
    public void fromXML(Element e) throws InvalidXMLException {
        if (e.getTagName().equals("meta")) {
            NodeList nl = e.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                switch (nl.item(i).getNodeName()) {
                    case "version":
                        this.setProperty(LanguageProperty.VERSION_EDITED, nl.item(i).getTextContent());
                        break;
                    case "name":
                        this.setProperty(LanguageProperty.NAME, nl.item(i).getTextContent());
                        break;
                    case "id":
                        this.setProperty(LanguageProperty.ID, nl.item(i).getTextContent());
                        break;
                    case "autonym":
                        this.setProperty(LanguageProperty.AUTONYM, nl.item(i).getTextContent());
                        break;
                    case "timeCreated":
                        this.setCreated(new Date(Long.parseLong(nl.item(i).getTextContent())));
                        break;
                    case "lastEdited":
                        this.setEdited(new Date(Long.parseLong(nl.item(i).getTextContent())));
                        break;
                    case "readonly":
                        this.setReadOnly(Boolean.parseBoolean(nl.item(i).getTextContent()));
                        break;
                    default:
                        // TODO: add parent by language ID

                }
            }
        } else {
            throw new InvalidXMLException("Node name not expected name! Expected: meta; Actual: " + e.getTagName());
        }
    }
}
