package net.oijon.oling.datatypes.phonology;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.XMLDatatype;
import net.oijon.oling.datatypes.phonology.feature.Feature;
import net.oijon.oling.datatypes.phonology.table.Phoneme;
import net.oijon.oling.datatypes.phonology.table.PhonoSystem;
import net.oijon.oling.info.Info;
import net.oijon.olog.Log;

public class Syllable implements XMLDatatype {

	private static Log log = Info.log;
	
	private ArrayList<Sound> onset = new ArrayList<>();
	private ArrayList<Sound> nucleus = new ArrayList<>();
	private ArrayList<Sound> coda = new ArrayList<>();
	private Phonology linkedPhono;

	// TODO: add various levels of stress

	private int nucleusWeight = 0;
	private int codaWeight = 0;

	public Syllable(ArrayList<Sound> onset, ArrayList<Sound> nucleus, ArrayList<Sound> coda, Phonology p) {
		this.onset.addAll(onset);
		this.nucleus.addAll(nucleus);
		this.coda.addAll(coda);
		linkedPhono = p;
	}
	
	public Syllable(Element e, Phonology p) throws InvalidXMLException {
		linkedPhono = p;
		fromXML(e);
	}
	
	public static ArrayList<Syllable> getSyllablesFromString(String str, Phonology p) {
		PhonoSystem ps = p.getPhonoSystem();
		
		ArrayList<Syllable> sylls = new ArrayList<>();
		ArrayList<Sound> sounds = Sound.getSoundsFromString(str, p);
		
		// group into clusters of nuclei and non-nuclei
		ArrayList<ArrayList<Sound>> groupedSounds = new ArrayList<>();
		ArrayList<Sound> currentGrouping = new ArrayList<>();
		boolean lastWasNucleus = false;
		
		// the current logic ensures that an empty arraylist will be added at the beginning if
		// it starts with a nucleus (very helpful)
		for (int i = 0; i < sounds.size(); i++) {
			Sound sound = sounds.get(i);
			Phoneme phoneme = sound.getPhoneme();
			Feature nucleusF = phoneme.getFeatures().get("SYLLPART_NUCLEUS");
			boolean isNucleus = (nucleusF == null) ? false : nucleusF.getValue();
			
			if (!(isNucleus ^ lastWasNucleus) && i < sounds.size() - 1) {
				currentGrouping.add(sound);
			} else {
				currentGrouping.add(sound);
				groupedSounds.add(new ArrayList<Sound>(currentGrouping));
				currentGrouping.clear();
			}
			lastWasNucleus = isNucleus;
		}
		
		
		
		
		return sylls;
	}
	
	public int getMoraicWeight() {
		return nucleusWeight + codaWeight;
	}

	public ArrayList<Sound> getOnset() {
		return onset;
	}

	public void setOnset(ArrayList<Sound> onset) {
		this.onset = onset;
	}

	public ArrayList<Sound> getNucleus() {
		return nucleus;
	}

	public void setNucleus(ArrayList<Sound> nucleus) {
		this.nucleus = nucleus;
		nucleusWeight = nucleus.size();
	}

	public ArrayList<Sound> getCoda() {
		return coda;
	}

	public void setCoda(ArrayList<Sound> coda) {
		this.coda = coda;
		codaWeight = coda.size();
	}

	@Override
	public String toString() {
		String returnString = "";
		for (int i = 0; i < onset.size(); i++) {
			returnString += onset.get(i).toString();
		}
		for (int i = 0; i < nucleus.size(); i++) {
			returnString += nucleus.get(i).toString();
		}
		for (int i = 0; i < coda.size(); i++) {
			returnString += coda.get(i).toString();
		}
		return returnString;
	}

	@Override
	public Element toXML() throws ParserConfigurationException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.newDocument();
		Element root = doc.createElement("syllable");

		Element onsetE = doc.createElement("onset");
		for (int i = 0; i < onset.size(); i++) {
			onsetE.appendChild(doc.importNode(onset.get(i).toXML(), true));
		}
		root.appendChild(onsetE);

		Element nucleusE = doc.createElement("nucleus");
		for (int i = 0; i < nucleus.size(); i++) {
			nucleusE.appendChild(doc.importNode(nucleus.get(i).toXML(), true));
		}
		root.appendChild(nucleusE);

		Element codaE = doc.createElement("coda");
		for (int i = 0; i < coda.size(); i++) {
			codaE.appendChild(doc.importNode(coda.get(i).toXML(), true));
		}
		root.appendChild(codaE);

		return root;
	}

	@Override
	public void fromXML(Element e) throws InvalidXMLException {
		if (e.getTagName().equals("syllable")) {
			NodeList nl = e.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element childE = (Element) n;
					switch (childE.getTagName()) {
						case "onset":
							NodeList onsetNL = childE.getChildNodes();
							for (int j = 0; j < onsetNL.getLength(); j++) {
								if (onsetNL.item(j).getNodeType() == Node.ELEMENT_NODE) {
									Element soundE = (Element) onsetNL.item(j);
									Sound s = new Sound(soundE, linkedPhono);
									this.onset.add(s);
								}
							}
							break;
						case "nucleus":
							NodeList nucleusNL = childE.getChildNodes();
							for (int j = 0; j < nucleusNL.getLength(); j++) {
								if (nucleusNL.item(j).getNodeType() == Node.ELEMENT_NODE) {
									Element soundE = (Element) nucleusNL.item(j);
									Sound s = new Sound(soundE, linkedPhono);
									this.nucleus.add(s);
								}
							}
							break;
						case "coda":
							NodeList codaNL = childE.getChildNodes();
							for (int j = 0; j < codaNL.getLength(); j++) {
								if (codaNL.item(j).getNodeType() == Node.ELEMENT_NODE) {
									Element soundE = (Element) codaNL.item(j);
									Sound s = new Sound(soundE, linkedPhono);
									this.coda.add(s);
								}
							}
							break;
					}
				}
			}
		} else {
			throw new InvalidXMLException("Node name not expected name! Expected: syllable; Actual: " + e.getTagName());
		}
	}
}
