package oling;

import net.oijon.oling.Parser;
import net.oijon.oling.datatypes.grammar.Gloss;
import net.oijon.oling.datatypes.grammar.GlossList;
import net.oijon.oling.datatypes.language.Language;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class NewUnitTests {

    /**
     * Grammar Tests
     */

    // gloss

    @Test
    void glossTest() {
        Gloss gloss = new Gloss("test", "test meaning");

        assertEquals("test", gloss.getAbbreviation());
        assertEquals("test meaning", gloss.getMeaning());

        gloss.setAbbreviation("test2");
        gloss.setMeaning("test meaning2");

        String expectedGlossStr = "===Gloss Start===\nabb:test2\nmeaning:test meaning2\n===Gloss End===";
        Gloss gloss2 = new Gloss(gloss);

        assertEquals(expectedGlossStr, gloss.toString());
        assertEquals(expectedGlossStr, gloss2.toString());
    }

    // glosslist

    @Test
    void glossListTest() {
        GlossList gl = new GlossList("Test list");
        // TODO: automatically sort gloss lists on adding, then test for arbitrary order
        gl.add(new Gloss("1", "first person"));
        gl.add(new Gloss("2", "second person"));
        gl.add(new Gloss("3", "third person"));

        assertEquals("Test list", gl.getName());
        gl.setName("Test list2");

        String expectedListStr = "===GlossList Start===\nname:Test list2\n===Glosses Start===\n===Gloss Start===\nabb:1\n" +
                "meaning:first person\n===Gloss End===\n===Gloss Start===\nabb:2\nmeaning:second person\n" +
                "===Gloss End===\n===Gloss Start===\nabb:3\nmeaning:third person\n===Gloss End===\n===Glosses End===\n" +
                "===GlossList End===";

        assertEquals(expectedListStr, gl.toString());
    }

    // grammar

    /**
     * Language tests
     */

    @Test
    void testLanguage() {
        try {
            Parser parser = new Parser(Paths.get(getClass().getClassLoader().getResource("testish.language").toURI()).toFile());
            Language testLang = parser.parseLanguage();
            testLang.toFile(new File(System.getProperty("user.home") + "/.oling/testish.language"));
            Parser parser2 = new Parser(new File(System.getProperty("user.home") + "/.oling/testish.language"));
            Language compareLanguage = parser2.parseLanguage();
            testLang.setOrtho(compareLanguage.getOrtho()); // ortho not created in testlang, is in comparelang
            assertEquals(testLang, compareLanguage);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    // language properties

    /**
     * Lexicon tests
     */

    // lexicon

    // word

    // wordproperties

    /**
     * Orthography tests
     */

    // guesser

    // orthography

    // orthopair

    /**
     * Phonology tests
     */

    // phonocategory

    // phonology

    // phonosystem

    // phonotable

    /**
     * Tags
     */

    // multitag

    // multitagutils

    // tag

    /**
     * Parser
     */


}
