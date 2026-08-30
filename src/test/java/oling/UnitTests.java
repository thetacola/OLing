package oling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import net.oijon.oling.datatypes.language.LanguageProperties;
import net.oijon.oling.datatypes.language.LanguageProperty;
import net.oijon.oling.datatypes.phonology.*;
import net.oijon.oling.datatypes.phonology.feature.Feature;
import net.oijon.oling.datatypes.phonology.feature.sonorancy.SonorancyTree;
import net.oijon.oling.datatypes.phonology.surface.Syllable;
import net.oijon.oling.datatypes.phonology.surface.SyllableString;
import net.oijon.oling.datatypes.phonology.table.Phoneme;
import net.oijon.oling.datatypes.phonology.table.PhonoCategory;
import net.oijon.oling.datatypes.phonology.table.PhonoCell;
import net.oijon.oling.datatypes.phonology.table.PhonoColumn;
import net.oijon.oling.datatypes.phonology.table.PhonoSystem;
import net.oijon.oling.datatypes.phonology.table.PhonoTable;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import net.oijon.olog.Log;

import net.oijon.oling.LegacyParser;
import net.oijon.oling.datatypes.InvalidXMLException;
import net.oijon.oling.datatypes.language.Language;
import net.oijon.oling.datatypes.lexicon.Lexicon;
import net.oijon.oling.datatypes.lexicon.Word;
import net.oijon.oling.datatypes.lexicon.WordProperties;
import net.oijon.oling.datatypes.orthography.Orthography;

public class UnitTests {

	Log log = new Log(System.getProperty("user.home") + "/.oling");
	
	@Test
	void readWriteEquivalency() {
		log.info("Testing read-write equivalency...");
		try {
			long start = System.currentTimeMillis();
			File f = Paths.get(UnitTests.class.getClassLoader().getResource("testish.xml").toURI()).toFile();
			Language oldL = Language.parse(f);
			long read = System.currentTimeMillis();
			
			File newFile = File.createTempFile("testish", "xml");
			log.debug("Writing to " + newFile + ", then reading from same file");
			oldL.toFile(newFile);
			long write = System.currentTimeMillis();
			log.debug("Time taken to read from file: " + (read - start) + "ms");
			log.debug("Time taken to write to file: " + (write - read) + "ms");
			
			Language newL = Language.parse(newFile);
			
			//log.info("Old language: " + oldL.toString());
			//log.info("New language:" + newL.toString());
			assertEquals(oldL.getPhono().getPhonoSystem().getSonorancyTree(),
					newL.getPhono().getPhonoSystem().getSonorancyTree());
			assertEquals(oldL, newL);
			log.info("Read-write equivalency successfully verified!");
		} catch (Exception e) {
			e.printStackTrace();
			log.err("Could not load needed resources!");
			fail();
		}
	}
	
	@Test
	void testFeatures() {
		log.info("Testing featural phonology...");
		try {
			File f = Paths.get(UnitTests.class.getClassLoader().getResource("testish.xml").toURI()).toFile();
			Language l = Language.parse(f);
			// should be /m/
			//Phoneme p = l.getPhono().getPhonoSystem().getTables().get(0).getRow(1).getCell(0).getPhonemes().get(0);
			//log.info(p.getSound() + " - " + p.getFeatures().toString());
			HashMap<String, Feature> features = new HashMap<String, Feature>();
			Phonology phono = l.getPhono();
			PhonoSystem ps = phono.getPhonoSystem();
			PhonoTable table = ps.getTables().get(0);
			PhonoCategory row = table.getRow(1);
			PhonoCell cell = row.getCell(0);
			PhonoColumn column = table.getColumn(cell.getIndex());
			Phoneme p = cell.getPhonemes().get(0);
			
			features.putAll(ps.getFeatures());
			features.putAll(table.getFeatures());
			features.putAll(column.getFeatures());
			features.putAll(row.getFeatures());
			features.putAll(cell.getFeatures());
			features.putAll(p.getFeatures());
			
			//features.addAll(p.getFeatures().values());
			//features.addAll(column.getFeatures().values());
			//features.addAll(cell.getFeatures().values());
			//features.addAll(row.getFeatures().values());
			//features.addAll(table.getFeatures().values());
			//features.addAll(ps.getFeatures().values());
			
			HashMap<String, Feature> phonemeFeatures = new HashMap<>(p.getFeatures());
			//ArrayList<Feature> phonemeFeatures = new ArrayList<Feature>(p.getFeatures().values());
			
			log.debug("Phoneme is reporting " + phonemeFeatures.size() + " features.");
			log.debug("Phoneme features: " + phonemeFeatures);
			log.debug("Manual count of features is reporting " + features.size() + " features.");
			log.debug("Manual features: " + features);
			if (phonemeFeatures.size() == 0 || features.size() == 0) {
				log.err("No features found in table or in phoneme!");
				log.err("Test failed!");
				fail();
			}
			int count = 0;
			int total = features.size();
			// the level and value is in some instances expected to be different, so only check the name
			
			if (features.size() == phonemeFeatures.size()) {
				for (String s : features.keySet()) {
					if (features.get(s).equals(phonemeFeatures.get(s))) {
						count++;
						log.debug("Found feature " + count + "/" + total +
								" " + features.get(s).toString());
					} else {
						log.err("Could not find feature " + features.get(s) + " in phoneme!");
						log.err("Test failed!");
						fail();
					}
				}
			}
			
			log.info("Found all expected features in phoneme!");
			
		} catch (Exception e) {
			e.printStackTrace();
			log.err("Could not load needed resources!");
			fail();
		}
	}
	
	@SuppressWarnings("deprecation")
    @Test
    void testLegacyToXML() {
		log.info("Testing legacy file parsing...");
        try {
            LegacyParser parser = new LegacyParser(Paths.get(UnitTests.class.getClassLoader().getResource("testish.language").toURI()).toFile());
            Language testLang = parser.parseLanguage();
            File f = File.createTempFile("testlang", "xml");
            testLang.toFile(f);
            
            log.debug("Reading testlang.xml from " + f.toString());
            Language newLang = Language.parse(f);

			LanguageProperties oldLP = testLang.getProperties();
	        LanguageProperties newLP = newLang.getProperties();
			assertEquals(oldLP, newLP);

			Phonology oldPhono = testLang.getPhono();
			Phonology newPhono = newLang.getPhono();

			List<String> oldPL = oldPhono.getList();
			List<String> newPL = newPhono.getList();
			assertEquals(oldPL, newPL);

			PhonoSystem oldPS = PhonoSystem.IPA;
			PhonoSystem newPS = newPhono.getPhonoSystem();

			String oldPSName = oldPS.getName();
			String newPSName = newPS.getName();
			assertEquals(oldPSName, newPSName);

			ArrayList<PhonoTable> oldPSTables = oldPS.getTables();
			ArrayList<PhonoTable> newPSTables = newPS.getTables();
			assertEquals(oldPSTables.size(), newPSTables.size());
			for (int i = 0; i < oldPSTables.size(); i++) {
				PhonoTable oldT = oldPSTables.get(i);
				PhonoTable newT = newPSTables.get(i);

				String oldTName = oldT.getName();
				String newTName = newT.getName();
				assertEquals(oldTName, newTName);

				ArrayList<String> oldCNames = oldT.getColumnNames();
				ArrayList<String> newCNames = newT.getColumnNames();
				assertEquals(oldCNames, newCNames);

				assertEquals(oldT.size(), newT.size());
				for (int j = 0; j < oldT.size(); j++) {
					PhonoCategory oldR = oldT.getRow(j);
					PhonoCategory newR = newT.getRow(j);

					String oldRName = oldR.getName();
					String newRName = newR.getName();
					assertEquals(oldRName, newRName);

					ArrayList<PhonoCell> oldC = oldR.getCells();
					ArrayList<PhonoCell> newC = newR.getCells();
					assertEquals(oldC, newC);

					assertEquals(oldR, newR);
				}

				assertEquals(oldT, newT);
			}
			assertEquals(oldPSTables, newPSTables);
			// the parser changes it to the IPA system when it detects that it needs to be updated
			assertEquals(oldPS, PhonoSystem.IPA);
			oldPhono.setPhonoSystem(PhonoSystem.IPA);
			assertEquals(oldPhono, newPhono);

			Orthography oldO = testLang.getOrtho();
			Orthography newO = newLang.getOrtho();
			assertEquals(oldO, newO);

			Lexicon oldL = testLang.getLexicon();
			Lexicon newL = newLang.getLexicon();
			assertEquals(oldL.size(), newL.size());
			for (int i = 0; i < oldL.size(); i++) {
				Word oldW = oldL.getWord(i);
				Word newW = newL.getWord(i);

				WordProperties oldWP = oldW.getProperties();
				WordProperties newWP = newW.getProperties();

				Date oldED = oldWP.getEditDate();
				Date newED = newWP.getEditDate();
				assertEquals(oldED, newED);

				assertEquals(oldWP, newWP);

				assertEquals(oldW, newW);
	        }

			assertEquals(oldL, newL);

            assertEquals(testLang, newLang);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        log.info("Legacy file parsing successfully verified!");
    }
	
	@Test
	void testIPAEquivalency() {
		log.info("Testing equivalency between known-good IPA in language and IPA backup system");
		try {
			File f = Paths.get(UnitTests.class.getClassLoader().getResource("testish.xml").toURI()).toFile();
			Language l = Language.parse(f);
			
			SonorancyTree lTree = l.getPhono().getPhonoSystem().getSonorancyTree();
			SonorancyTree IPATree = PhonoSystem.IPA.getSonorancyTree();
			
			assertEquals(l.getPhono().getPhonoSystem(), PhonoSystem.IPA);
			assertEquals(lTree, IPATree);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.err("Could not load needed resources!");
			fail();
		}
		log.info("IPA equivalence successfully verified!");
	}

	@Test
	void testIPASonorancy() {
		log.info("Testing proper sonorancy...");
		PhonoSystem IPA = PhonoSystem.IPA;
		SonorancyTree st = IPA.getSonorancyTree();
		ArrayList<Phoneme> phonemes = IPA.getAllPhonemes();
		
		Phoneme[] testSet = new Phoneme[11];	
		
		// get /g/ and /k/
		for (Phoneme p : phonemes) {
			switch (p.getSound()) {
				case "k":
					testSet[0] = p;
					break;
				case "g":
					testSet[1] = p;
					break;
				case "x":
					testSet[2] = p;
					break;
				case "ɣ":
					testSet[3] = p;
					break;
				case "n":
					testSet[4] = p;
					break;
				case "l":
					testSet[5] = p;
					break;
				case "ɾ":
					testSet[6] = p;
					break;
				case "j":
					testSet[7] = p;
					break;
				case "i":
					testSet[8] = p;
					break;
				case "o":
					testSet[9] = p;
					break;
				case "ɑ":
					testSet[10] = p;
					break;
			}
		}
		
		int[] rankings = new int[testSet.length];
		for (int i = 0; i < testSet.length; i++) {
			rankings[i] = st.getValue(testSet[i]);
			log.debug("/" + testSet[i].getSound() + "/ == " + rankings[i] +
					" (" + Integer.toBinaryString(rankings[i]) + ")");
			if (i != 0) {
				assertTrue(rankings[i] > rankings[i - 1]);
			}
		}
		log.info("Sonorancy successfully verified!");
	}
	
	@Test
	void testDiacriticEquivalency() {
		log.info("Testing diacritic equivalency between known-good file and IPA backup system");
		try {
			File f = Paths.get(UnitTests.class.getClassLoader().getResource("testish.xml").toURI()).toFile();
			Language l = Language.parse(f);
			
			PhonoSystem lSys = l.getPhono().getPhonoSystem();
			PhonoSystem IPA = PhonoSystem.IPA;
			
			for (String key : IPA.getDiacriticKeys()) {
				assertEquals(lSys.getDiacritic(key), IPA.getDiacritic(key));
			}
			
			assertEquals(l.getPhono().getPhonoSystem(), PhonoSystem.IPA);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.err("Could not load needed resources!");
			fail();
		}
		log.info("Diacritic equivalency successfully verified!");
	}
	
	@Test
	void testBrokenID() {
		log.info("Testing broken ID repair");
		try {
			File f = Paths.get(UnitTests.class.getClassLoader().getResource("testish.xml").toURI()).toFile();
			Language l = Language.parse(f);
			
			l.getProperties().setProperty(LanguageProperty.ID, "null");
			
			File temp = File.createTempFile("testlang", "xml");
            l.toFile(temp);
			
            Language newLang = Language.parse(temp);
            
            assertNotEquals(newLang.getProperties().getProperty(LanguageProperty.ID), "null");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.err("Could not load needed resources!");
			fail();
		}
		log.info("Broken ID repair successfully verified!");
	}
	
	@Test
	void testSyllabification() {
		log.info("Testing syllabification from phoneme string...");
		try {
			File f = Paths.get(UnitTests.class.getClassLoader().getResource("testish.xml").toURI()).toFile();
			Language l = Language.parse(f);
			
			String word = "oɱ̚ɱælʃkʰo";
			SyllableString sylls = new SyllableString(l.getPhono(), word);
			String resultsStr = sylls.toString();
			log.info("[oɱ̚ɱælʃkʰo] → [oɱ̚.ɱælʃ.kʰo] expected, got [" + resultsStr + "]");
			assertEquals("oɱ̚.ɱælʃ.kʰo", resultsStr);
			
		} catch (URISyntaxException | ParserConfigurationException | IOException | SAXException | InvalidXMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		log.info("Syllabification successfully verified!");
	}
}
