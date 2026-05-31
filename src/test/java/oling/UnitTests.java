package oling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.oijon.oling.datatypes.language.LanguageProperties;
import net.oijon.oling.datatypes.phonology.*;
import net.oijon.oling.datatypes.phonology.feature.Feature;
import net.oijon.oling.datatypes.phonology.table.Phoneme;
import net.oijon.oling.datatypes.phonology.table.PhonoCategory;
import net.oijon.oling.datatypes.phonology.table.PhonoCell;
import net.oijon.oling.datatypes.phonology.table.PhonoColumn;
import net.oijon.oling.datatypes.phonology.table.PhonoSystem;
import net.oijon.oling.datatypes.phonology.table.PhonoTable;

import org.junit.jupiter.api.Test;

import net.oijon.olog.Log;

import net.oijon.oling.LegacyParser;
import net.oijon.oling.datatypes.language.Language;
import net.oijon.oling.datatypes.lexicon.Lexicon;
import net.oijon.oling.datatypes.lexicon.Word;
import net.oijon.oling.datatypes.lexicon.WordProperties;
import net.oijon.oling.datatypes.orthography.Orthography;

public class UnitTests {

	Log log = new Log(System.getProperty("user.home") + "/.oling");
	
	@Test
	void readWriteEquivalency() {
		log.info("Testing read-write equivalancy...");
		try {
			long start = System.currentTimeMillis();
			File f = Paths.get(UnitTests.class.getClassLoader().getResource("testish.xml").toURI()).toFile();
			Language oldL = Language.parse(f);
			long read = System.currentTimeMillis();
			
			File newFile = File.createTempFile("testish", "xml");
			log.debug("Writing to " + newFile + ", then reading from same file");
			oldL.toFile(newFile);
			long write = System.currentTimeMillis();
			log.info("Time taken to read from file: " + (read - start) + "ms");
			log.info("Time taken to write to file: " + (write - read) + "ms");
			
			Language newL = Language.parse(newFile);
			
			//log.info("Old language: " + oldL.toString());
			//log.info("New language:" + newL.toString());
			
			
			assertEquals(oldL, newL);
			
		} catch (Exception e) {
			e.printStackTrace();
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
			ArrayList<Feature> features = new ArrayList<Feature>();
			Phonology phono = l.getPhono();
			PhonoSystem ps = phono.getPhonoSystem();
			PhonoTable table = ps.getTables().get(0);
			PhonoCategory row = table.getRow(1);
			PhonoCell cell = row.getCell(0);
			PhonoColumn column = table.getColumn(cell.getIndex());
			Phoneme p = cell.getPhonemes().get(0);
			
			features.addAll(p.getFeatures().values());
			features.addAll(column.getFeatures().values());
			features.addAll(cell.getFeatures().values());
			features.addAll(row.getFeatures().values());
			features.addAll(table.getFeatures().values());
			features.addAll(ps.getFeatures().values());
			
			ArrayList<Feature> phonemeFeatures = new ArrayList<Feature>(p.getFeatures().values());
			// remove duplicates, helpful for logging
			
			for (int i = 0; i < features.size(); i++) {
				for (int j = 0; j < features.size(); j++) {
					if (i != j) {
						if (features.get(i).getName().equals(features.get(j).getName())) {
							features.remove(j);
							i = 0;
							j = 0;
						}
					}
				}
			}
			
			log.info("Phoneme is reporting " + phonemeFeatures.size() + " features.");
			log.info("Phoneme features: " + phonemeFeatures);
			log.info("Manual count of features is reporting " + features.size() + " features.");
			log.info("Manual features: " + features);
			if (phonemeFeatures.size() == 0 || features.size() == 0) {
				log.err("No features found in table or in phoneme!");
				log.err("Test failed!");
				fail();
			}
			int count = 0;
			int total = features.size();
			// the level and value is in some instances expected to be different, so only check the name
			for (int i = 0; i < features.size(); i++) {
				boolean found = false;
				for (int j = 0; j < phonemeFeatures.size(); j++) {
					if (features.get(i).getName().equals(phonemeFeatures.get(j).getName())) {
						count++;
						log.info("Found feature " + count + "/" + total +
								" " + features.get(i).toString());
						found = true;
					}
				}
				if (!found) {
					log.err("Could not find feature " + features.get(i) + " in phoneme!");
					log.err("Test failed!");
					fail();
				}
			}
			log.info("Found all expected features in phoneme!");
			
		} catch (Exception e) {
			e.printStackTrace();
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

    }

}
