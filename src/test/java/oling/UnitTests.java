package oling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.nio.file.Paths;
import java.util.Date;

import org.junit.jupiter.api.Test;

import net.oijon.olog.Log;

import net.oijon.oling.LegacyParser;
import net.oijon.oling.datatypes.language.Language;
import net.oijon.oling.datatypes.language.LanguageProperty;
import net.oijon.oling.datatypes.lexicon.Lexicon;
import net.oijon.oling.datatypes.lexicon.Word;
import net.oijon.oling.datatypes.lexicon.WordProperty;
import net.oijon.oling.datatypes.orthography.Orthography;

public class UnitTests {

	Log log = new Log(System.getProperty("user.home") + "/.oling");
	
	@Test
	void testLegacyLanguage() {
		log.info("====BEGIN LANGUAGE TEST====");
		log.info("Parsing testish.language...");
		try {
			log.setDebug(true);
			LegacyParser parser = new LegacyParser(Paths.get(getClass().getClassLoader().getResource("testish.language").toURI()).toFile());
			Language testLang = parser.parseLanguage();
			testLang.toFile(new File(System.getProperty("user.home") + "/.oling/testish.language"));
			LegacyParser parser2 = new LegacyParser(new File(System.getProperty("user.home") + "/.oling/testish.language"));
			Language compareLanguage = parser2.parseLanguage();
			testLang.setOrtho(compareLanguage.getOrtho()); // ortho not created in testlang, is in comparelang
			assertEquals(testLang, compareLanguage);
		} catch (Exception e) {
			e.printStackTrace();
			log.err(e.toString());
			fail();
		}
		log.info("=====END LANGUAGE TEST=====");
	}
	
	@Test
	void testLegacyOrthography() {
		log.info("===BEGIN ORTHOGRAPHY TEST===");
		log.setDebug(true);
		try {
			log.info("Parsing testish.language...");
			log.info("Expect an error about no orthography existing, this test creates it.");
			LegacyParser parser = new LegacyParser(Paths.get(UnitTests.class.getClassLoader().getResource("testish.language").toURI()).toFile());
			Language testLang = parser.parseLanguage();
			Orthography testOrtho = testLang.getOrtho();
			// sourced via wikipedia
			// big block here i know, this is meant to be done by user input and not manually like this
			testOrtho.add("b", "b");
			testOrtho.add("d", "d");
			testOrtho.add("dj", "d");
			testOrtho.add("dʒ", "j");
			testOrtho.add("ð", "th");
			testOrtho.add("f", "f");
			testOrtho.add("g", "g");
			testOrtho.add("h", "h");
			testOrtho.add("hw", "wh");
			testOrtho.add("j", "y");
			testOrtho.add("k", "k");
			testOrtho.add("k", "c");
			testOrtho.add("l", "l");
			testOrtho.add("lj", "l");
			testOrtho.add("m", "m");
			testOrtho.add("n", "n");
			testOrtho.add("nj", "n");
			testOrtho.add("ŋ", "ng");
			testOrtho.add("p", "p");
			testOrtho.add("ɹ", "r");
			testOrtho.add("s", "s");
			testOrtho.add("s", "c");
			testOrtho.add("sj", "s");
			testOrtho.add("ʃ", "sh");
			testOrtho.add("t", "t");
			testOrtho.add("tj", "t");
			testOrtho.add("tʃ", "ch");
			testOrtho.add("tʃ", "tch");
			testOrtho.add("θ", "th");
			testOrtho.add("θj", "th");
			testOrtho.add("v", "v");
			testOrtho.add("w", "w");
			testOrtho.add("z", "z");
			testOrtho.add("zj", "z");
			testOrtho.add("ʒ", "s");
			log.info("Testish consonants added!");
			testOrtho.add("ɑː", "a");
			testOrtho.add("ɒ", "o");
			testOrtho.add("æ", "a");
			testOrtho.add("aɪ", "i");
			testOrtho.add("aɪ", "ie");
			testOrtho.add("aʊ", "ou");
			testOrtho.add("aʊ", "ow");
			testOrtho.add("ɛ", "e");
			testOrtho.add("eɪ", "a");
			testOrtho.add("ɪ", "i");
			testOrtho.add("iː", "ee");
			testOrtho.add("iː", "e");
			testOrtho.add("oʊ", "oa");
			testOrtho.add("ɔː", "ough");
			testOrtho.add("ɔː", "au");
			testOrtho.add("ɔː", "augh");
			testOrtho.add("ɔɪ", "oi");
			testOrtho.add("ʊ", "oo");
			testOrtho.add("uː", "oo");
			testOrtho.add("uː", "u");
			testOrtho.add("ʌ", "u");
			testOrtho.add("ɑːɹ", "ar");
			testOrtho.add("ɒɹ", "or");
			testOrtho.add("æɹ", "arr");
			testOrtho.add("aɪəɹ", "ire");
			testOrtho.add("aʊəɹ", "our");
			testOrtho.add("ɛɹ", "err");
			testOrtho.add("ɛəɹ", "are");
			testOrtho.add("ɛəɹ", "ar");
			testOrtho.add("ɪɹ", "irr");
			testOrtho.add("ɪɹ", "ir");
			testOrtho.add("ɪəɹ", "ear");
			testOrtho.add("ɪəɹ", "er");
			testOrtho.add("ɔːɹ", "or");
			testOrtho.add("ɔːɹ", "oar");
			testOrtho.add("ɔɪəɹ", "oir");
			testOrtho.add("ʊɹ", "our");
			testOrtho.add("ʊəɹ", "our");
			testOrtho.add("ʊəɹ", "ure");
			testOrtho.add("ɜːɹ", "ur");
			testOrtho.add("ɜːɹ", "urr");
			testOrtho.add("ɜːɹ", "or");
			testOrtho.add("ʌɹ", "urr");
			testOrtho.add("ə", "a");
			testOrtho.add("ə", "o");
			testOrtho.add("ɪ", "i");
			testOrtho.add("i", "i");
			testOrtho.add("i", "y");
			testOrtho.add("u", "u");
			testOrtho.add("əɹ", "er");
			testOrtho.add("əɹ", "ar");
			testOrtho.add("əɹ", "or");
			testOrtho.add("oʊ", "o");
			testOrtho.add("oʊ", "ow");
			testOrtho.add("iə", "ia");
			testOrtho.add("uə", "ue");
			testOrtho.add("əl", "le");
			log.info("Added vowels!");
			testLang.setOrtho(testOrtho);
			log.info("Printing language...");
			log.info(testLang.toString());
			String expectedOrthoString = "===Orthography Start===\n"
					+ "ɔː:ough\n"
					+ "ɔː:augh\n"
					+ "tʃ:tch\n"
					+ "æɹ:arr\n"
					+ "aɪəɹ:ire\n"
					+ "aʊəɹ:our\n"
					+ "ɛɹ:err\n"
					+ "ɛəɹ:are\n"
					+ "ɪɹ:irr\n"
					+ "ɪəɹ:ear\n"
					+ "ɔːɹ:oar\n"
					+ "ɔɪəɹ:oir\n"
					+ "ʊɹ:our\n"
					+ "ʊəɹ:our\n"
					+ "ʊəɹ:ure\n"
					+ "ɜːɹ:urr\n"
					+ "ʌɹ:urr\n"
					+ "ð:th\n"
					+ "hw:wh\n"
					+ "ŋ:ng\n"
					+ "ʃ:sh\n"
					+ "tʃ:ch\n"
					+ "θ:th\n"
					+ "θj:th\n"
					+ "aɪ:ie\n"
					+ "aʊ:ou\n"
					+ "aʊ:ow\n"
					+ "iː:ee\n"
					+ "oʊ:oa\n"
					+ "ɔː:au\n"
					+ "ɔɪ:oi\n"
					+ "ʊ:oo\n"
					+ "uː:oo\n"
					+ "ɑːɹ:ar\n"
					+ "ɒɹ:or\n"
					+ "ɛəɹ:ar\n"
					+ "ɪɹ:ir\n"
					+ "ɪəɹ:er\n"
					+ "ɔːɹ:or\n"
					+ "ɜːɹ:ur\n"
					+ "ɜːɹ:or\n"
					+ "əɹ:er\n"
					+ "əɹ:ar\n"
					+ "əɹ:or\n"
					+ "oʊ:ow\n"
					+ "iə:ia\n"
					+ "uə:ue\n"
					+ "əl:le\n"
					+ "b:b\n"
					+ "d:d\n"
					+ "dj:d\n"
					+ "dʒ:j\n"
					+ "f:f\n"
					+ "g:g\n"
					+ "h:h\n"
					+ "j:y\n"
					+ "k:k\n"
					+ "k:c\n"
					+ "l:l\n"
					+ "lj:l\n"
					+ "m:m\n"
					+ "n:n\n"
					+ "nj:n\n"
					+ "p:p\n"
					+ "ɹ:r\n"
					+ "s:s\n"
					+ "s:c\n"
					+ "sj:s\n"
					+ "t:t\n"
					+ "tj:t\n"
					+ "v:v\n"
					+ "w:w\n"
					+ "z:z\n"
					+ "zj:z\n"
					+ "ʒ:s\n"
					+ "ɑː:a\n"
					+ "ɒ:o\n"
					+ "æ:a\n"
					+ "aɪ:i\n"
					+ "ɛ:e\n"
					+ "eɪ:a\n"
					+ "ɪ:i\n"
					+ "iː:e\n"
					+ "uː:u\n"
					+ "ʌ:u\n"
					+ "ə:a\n"
					+ "ə:o\n"
					+ "ɪ:i\n"
					+ "i:i\n"
					+ "i:y\n"
					+ "u:u\n"
					+ "oʊ:o\n"
					+ "===Orthography End===";
			//Orthography expectedOrtho = Orthography.legacyParse(new Multitag(expectedOrthoString));
			//assertEquals(testOrtho, expectedOrtho);
			log.info("Parsing new language...");
			testLang.toFile(new File(System.getProperty("user.home") + "/.oling/testish2.language"));
			LegacyParser newparser = new LegacyParser(new File(System.getProperty("user.home") + "/.oling/testish2.language"));
			Language newLang = newparser.parseLanguage();
			log.info(newLang.toString());
			assertEquals(testOrtho, newLang.getOrtho());
			
			// broken, as the guesser is not all that robust currently...
			//assertEquals(Guesser.phonoGuess("ough", testOrtho), "ɔː");
			//assertEquals(Guesser.orthoGuess("jutɪlz", testOrtho), "yutylz");
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.err(e.toString());
		}
		log.info("====END ORTHOGRAPHY TEST====");
	}
	
	@Test
	void testLegacyNullIDCatch() {
		try {
			LegacyParser parser = new LegacyParser(Paths.get(UnitTests.class.getClassLoader().getResource("testish.language").toURI()).toFile());
			Language testLang = parser.parseLanguage();
			
			log.debug("Old ID: " + testLang.getProperties().getProperty(LanguageProperty.ID));
			assertFalse(testLang.getProperties().getProperty(LanguageProperty.ID).equals("null"));
			
			testLang.getProperties().setProperty(LanguageProperty.ID, "null");
			log.debug("Null ID: " + testLang.getProperties().getProperty(LanguageProperty.ID));
			assertTrue(testLang.getProperties().getProperty(LanguageProperty.ID).equals("null"));
			
			testLang.toFile(new File(System.getProperty("user.home") + "/.oling/testish2.language"));
			
			LegacyParser newparser = new LegacyParser(new File(System.getProperty("user.home") + "/.oling/testish2.language"));
			Language testLang2 = newparser.parseLanguage();
			
			log.debug("New ID: " + testLang2.getProperties().getProperty(LanguageProperty.ID));
			assertFalse(testLang2.getProperties().getProperty(LanguageProperty.ID).equals("null"));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	void testLegacyLexicon() {
		try {
			LegacyParser parser = new LegacyParser(Paths.get(UnitTests.class.getClassLoader().getResource("testish.language").toURI()).toFile());
			Language testLang = parser.parseLanguage();
			
			Word w1 = new Word("hello", "hi");
			w1.getProperties().setCreationDate(new Date(1234567890987654L));
			w1.getProperties().setEditDate(new Date(2000000000000L));
			
			Word w2 = new Word("foo", "bar");
			w2.getProperties().setProperty(WordProperty.PRONOUNCIATION, "foobar");
			w2.getProperties().setProperty(WordProperty.ETYMOLOGY, "foo + bar");
			w2.getProperties().setCreationDate(new Date(1L));
			w2.getProperties().setEditDate(new Date(99999999999999L));
			
			Lexicon l = new Lexicon();
			l.addWord(w1);
			l.addWord(w2);
			
			testLang.setLexicon(l);
			
			testLang.toFile(new File(System.getProperty("user.home") + "/.oling/testish2.language"));
			
			LegacyParser newparser = new LegacyParser(new File(System.getProperty("user.home") + "/.oling/testish2.language"));
			Language testLang2 = newparser.parseLanguage();
			
			boolean testedHi = false;
			boolean testedFoo = false;
			for (int i = 0; i < testLang2.getLexicon().size(); i++) {
				Word w = testLang2.getLexicon().getWord(i);
				if (w.getProperties().getProperty(WordProperty.NAME).equals("hello")) {
					testedHi = true;
					assertEquals("hi", w.getProperties().getProperty(WordProperty.MEANING));
					assertEquals(new Date(1234567890987654L), w.getProperties().getCreationDate());
					assertEquals(new Date(2000000000000L), w.getProperties().getEditDate());
				} else if (w.getProperties().getProperty(WordProperty.NAME).equals("foo")) {
					testedFoo = true;
					assertEquals(new Date(1L), w.getProperties().getCreationDate());
					assertEquals(new Date(99999999999999L), w.getProperties().getEditDate());
					assertEquals("foo + bar", w.getProperties().getProperty(WordProperty.ETYMOLOGY));
					assertEquals("foobar", w.getProperties().getProperty(WordProperty.PRONOUNCIATION));
				}
			}
			assertTrue(testedHi);
			assertTrue(testedFoo);
		
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
