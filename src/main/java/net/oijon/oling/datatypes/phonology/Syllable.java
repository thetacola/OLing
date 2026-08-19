package net.oijon.oling.datatypes.phonology;

import java.util.ArrayList;
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
		ArrayList<Syllable> sylls = new ArrayList<>();
		ArrayList<Sound> sounds = Sound.getSoundsFromString(str, p);
		
		// group into clusters of nuclei and non-nuclei
		ArrayList<ArrayList<Sound>> groupedSounds = new ArrayList<>();
		ArrayList<Sound> currentGrouping = new ArrayList<>();
		boolean lastWasNucleus = false;
		
		if (sounds.size() > 0) {
			// creates a blank group if starting with a nucleus
			// very helpful for alignment
			for (int i = 0; i < sounds.size(); i++) {
				Sound sound = sounds.get(i);
				Phoneme phoneme = sound.getPhoneme();
				Feature nucleusF = phoneme.getFeatures().get("SYLLPART_NUCLEUS");
				boolean isNucleus = (nucleusF == null) ? false : nucleusF.getValue();
				
				if (isNucleus == lastWasNucleus) {
					currentGrouping.add(sound);
				} else {
					groupedSounds.add(new ArrayList<Sound>(currentGrouping));
					currentGrouping.clear();
					currentGrouping.add(sound);
				}
				
				if (i == sounds.size() - 1) {
					groupedSounds.add(new ArrayList<Sound>(currentGrouping));
				}
				lastWasNucleus = isNucleus;
			}
			
			ArrayList<Sound> currentOnset = new ArrayList<>();
			ArrayList<Sound> currentNucleus = new ArrayList<>();
			ArrayList<Sound> currentCoda = new ArrayList<>();
			for (int i = 0; i < groupedSounds.size(); i++) {
				ArrayList<Sound> group = groupedSounds.get(i);
				if (i == 0) {
					// must be all onset
					for (int j = 0; j < group.size(); j++) {
						Sound sound = group.get(j);
						Feature onsetF = sound.getFeatures().get("SYLLPART_ONSET");
						boolean isOnset = (onsetF == null) ? false : onsetF.getValue();
						if (!isOnset) {
							log.err("Found sound [" + sound + "] that must logically be in onset position "
									+ "of syllable, but is not allowed per phonological system! "
									+ "Ignoring sound in syllable creation...");
						} else {
							currentOnset.add(sound);
						}
					}
				} else if (i % 2 == 1) {
					// must be all nucleus
					// previously verified by grouping logic
					currentNucleus.addAll(group);
				} else if ((i < groupedSounds.size() - 1)) {
					int breakpoint = -1;
					// can either be onset or coda
					boolean hasBreakpoint = false;
					for (int j = 0; j < group.size(); j++) {
						Sound sound = group.get(j);
						Feature codaF = sound.getFeatures().get("SYLLPART_CODA");
						boolean isCoda = (codaF == null) ? false : codaF.getValue();
						if (!hasBreakpoint && !isCoda) {
							hasBreakpoint = true;
							breakpoint = j;
						}
					}
					
					
					if (!hasBreakpoint) {
						// what we're looking for here is where sonorancy drops the most
						int lowestSonorancy = Integer.MAX_VALUE;
						int lowestIndex = -1;
						for (int j = 0; j < group.size(); j++) {
							int sonorancy = group.get(j).getSonorancy();
							if (sonorancy < lowestSonorancy) {
								lowestSonorancy = sonorancy;
								lowestIndex = j;
							}
						}
						breakpoint = lowestIndex;
					}
					
					for (int j = 0; j < breakpoint; j++) {
						currentCoda.add(group.get(j));
					}
					sylls.add(new Syllable(new ArrayList<Sound>(currentOnset),
							new ArrayList<Sound>(currentNucleus),
							new ArrayList<Sound>(currentCoda),
							p));
					currentOnset.clear();
					currentNucleus.clear();
					currentCoda.clear();
					for (int j = breakpoint; j < group.size(); j++) {
						currentOnset.add(group.get(j));
					}
				} else {
					// must be all coda
					for (int j = 0; j < group.size(); j++) {
						Sound sound = group.get(j);
						Feature codaF = sound.getFeatures().get("SYLLPART_CODA");
						boolean isCoda = (codaF == null) ? false : codaF.getValue();
						if (!isCoda) {
							log.err("Found sound [" + sound + "] that must logically be in coda position "
									+ "of syllable, but is not allowed per phonological system! "
									+ "Ignoring sound in syllable creation...");
						} else {
							currentCoda.add(sound);
						}
					}
				}
			}
			sylls.add(new Syllable(new ArrayList<Sound>(currentOnset),
					new ArrayList<Sound>(currentNucleus),
					new ArrayList<Sound>(currentCoda),
					p));
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
			nucleusWeight = nucleus.size();
			codaWeight = coda.size();
		} else {
			throw new InvalidXMLException("Node name not expected name! Expected: syllable; Actual: " + e.getTagName());
		}
	}
}
