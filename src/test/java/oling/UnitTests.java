package oling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import net.oijon.oling.datatypes.language.LanguageProperties;
import net.oijon.oling.datatypes.phonology.*;
import org.junit.jupiter.api.Test;

import net.oijon.olog.Log;

import net.oijon.oling.LegacyParser;
import net.oijon.oling.datatypes.language.Language;
import net.oijon.oling.datatypes.lexicon.Lexicon;
import net.oijon.oling.datatypes.lexicon.Word;
import net.oijon.oling.datatypes.lexicon.WordProperties;
import net.oijon.oling.datatypes.orthography.Orthography;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class UnitTests {

	Log log = new Log(System.getProperty("user.home") + "/.oling");

	@SuppressWarnings("deprecation")
    @Test
    void testLegacyToXML() {
        try {
            LegacyParser parser = new LegacyParser(Paths.get(UnitTests.class.getClassLoader().getResource("testish.language").toURI()).toFile());
            Language testLang = parser.parseLanguage();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            File f = Paths.get(UnitTests.class.getClassLoader().getResource("testish.xml").toURI()).toFile();
            log.debug("Reading testish.xml from " + f.toString());
            Scanner reader = new Scanner(f, StandardCharsets.UTF_8);
            boolean firstLine = true;
            String data = "";
            while (reader.hasNextLine()) {
                if (firstLine) {
                    data = reader.nextLine();
                    String[] splitData = data.split("<\\?xml");
                    data = "<?xml" + splitData[1];
                    firstLine = false;
                } else {

                    data += reader.nextLine() + "\n";
                }
            }
            reader.close();
            Document doc = builder.parse(new InputSource(new StringReader(data)));
            Element element = doc.getDocumentElement();
            Language newLang = new Language(element);

			LanguageProperties oldLP = testLang.getProperties();
	        LanguageProperties newLP = newLang.getProperties();
			assertEquals(oldLP, newLP);

			Phonology oldPhono = testLang.getPhono();
			Phonology newPhono = newLang.getPhono();

			List<String> oldPL = oldPhono.getList();
			List<String> newPL = newPhono.getList();
			assertEquals(oldPL, newPL);

			PhonoSystem oldPS = oldPhono.getPhonoSystem();
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
			assertEquals(oldPS, newPS);
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
