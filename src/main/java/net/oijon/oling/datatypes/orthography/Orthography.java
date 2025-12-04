package net.oijon.oling.datatypes.orthography;

import java.util.ArrayList;

import net.oijon.olog.Log;

import net.oijon.oling.Parser;
import net.oijon.oling.datatypes.phonology.Phonology;
import net.oijon.oling.datatypes.tags.Multitag;
import net.oijon.oling.datatypes.tags.Tag;

//last edit: 11/4/23 -N3

/**
 * The writing system of a language. Connects phonemes to graphemes, allowing
 * a user to convert between phonetic transcription and standard writing system.
 * @author alex
 *
 */
public class Orthography {

	private Phonology ph = new Phonology();
	private ArrayList<OrthoPair> orthoList = new ArrayList<>();
	
	static Log log = Parser.getLog();
	
	// TODO: allow ortho rules to have exceptions
	
	/**
	 * Creates an empty orthography
	 */
	public Orthography() {
		
	}
	
	/**
	 * Creates an orthography with a set phonology
	 * @param ph The phonology to be used
	 */
	public Orthography(Phonology ph) {
		this.ph = ph;
	}
	
	/**
	 * Copy constructor
	 * @param o The orthography to copy
	 */
	public Orthography(Orthography o) {
		this.ph = new Phonology(o.ph);
		this.orthoList = new ArrayList<>(o.orthoList);
	}
	
	/**
	 * Adds an orthopair with given constituents
	 * @param phonemes The phoneme(s) for the grapheme(s)
	 * @param graphemes The grapheme(s) for the phoneme(s)
	 */
	public void add(String phonemes, String graphemes) {
		// TODO: check if phonemes are actually in phonology
		orthoList.add(new OrthoPair(phonemes, graphemes));
		sortOrthoList();
	}
	
	/**
	 * Gets the phonology used
	 * @return The phonology in question
	 */
	public Phonology getPhono() {
		return ph;
	}
	
	/**
	 * Sets a new phonology for the orthography to use
	 * @param p The new phonology to use
	 */
	public void setPhono(Phonology p) {
		this.ph = p;
	}
	
	/**
	 * Sorts the list
	 */
	private void sortOrthoList() {
		for (int i = 1; i < orthoList.size() - 1; i++) {
			if (orthoList.get(i).compareTo(orthoList.get(i + 1)) > 0) {
				OrthoPair temp = orthoList.get(i + 1);
				orthoList.set(i + 1, orthoList.get(i));
				orthoList.set(i, temp);
				sortOrthoList();
			}
		}
	}
	
	/**
	 * Gets a pair at the given index
	 * @param i The index of the pair in the orthography
	 * @return The pair specified
	 */
	public OrthoPair getPair(int i) throws IndexOutOfBoundsException {
		return orthoList.get(i);
	}
	
	/**
	 * Removes a pair from an orthography based on index
	 * @param i The index of the pair to be removed
	 */
	public void remove(int i) throws IndexOutOfBoundsException {
		orthoList.remove(i);
	}
	
	public static Orthography parse(Multitag docTag) {
		try {
			Orthography ortho = new Orthography();
			Multitag orthoTag = docTag.getMultitag("Orthography");
			ArrayList<Tag> orthoPairs = orthoTag.getSubtags();
            for (Tag op : orthoPairs) {
                ortho.add(op.getName(), op.value());
            }
            ortho.sortOrthoList();
			return ortho;
		} catch (Exception e) {
			log.err("No orthography found! Has one been created? Returning a blank orthography...");
            log.debug(e.toString());
            e.printStackTrace();
			return new Orthography();
		}
	}
	
	public String toString() {
		sortOrthoList();
		StringBuilder returnString = new StringBuilder("===Orthography Start===\n");
        for (OrthoPair orthoPair : orthoList) {
            returnString.append(orthoPair.toString()).append("\n");
        }
		returnString.append("===Orthography End===");
		
		
		return returnString.toString();
	}
	
	/**
	 * Gets the size of the Orthography
	 * @return The amount of orthography pairs
	 */
	public int size() {
		return orthoList.size();
	}
	
	/**
	 * Checks if two ortholists are equal. Note that this is order-dependent.
	 * @param o The orthography to check
	 * @return true if equal, false otherwise
	 */
	private boolean orthoListEqual(Orthography o) {
        if (orthoList.size() != o.orthoList.size()) {
            return false;
        }

		for (int i = 0; i < orthoList.size(); i++) {
            try {
                if (!orthoList.get(i).equals(o.getPair(i))) {
                    return false;
                }
            } catch (IndexOutOfBoundsException e) {
                // If we go over the array index, they cannot be equal.
                return false;
            }
		}
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Orthography) {
			Orthography o = (Orthography) obj;
			return orthoListEqual(o);
		}
		return false;
	}
	
}
