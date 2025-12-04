package net.oijon.oling.datatypes.orthography;

public class Guesser {

	/**
	 * Guesses what the graphemes will be given phonemes
	 * @param input The phonemes to use
	 * @param o The orthography to use
	 * @return A guess on graphemes
	 */
	public static String orthoGuess(String input, Orthography o) {
		String returnString = input;
		for (int i = 0; i < o.size(); i++) {
            try {
                String phonemes = o.getPair(i).getPhonemes();
                String graphemes = o.getPair(i).getGraphemes();
                returnString = returnString.replaceAll(phonemes, graphemes);
            } catch (IndexOutOfBoundsException e) {
                // If this is thrown, then something's gone horribly wrong and the loop ought to be exited
                break;
            }
		}
		return returnString;
	}
	
	/**
	 * Guesses what the phonemes will be given graphemes
	 * @param input The graphemes to use
	 * @param o The orthography to use
	 * @return A guess on phonemes
	 */
	public static String phonoGuess(String input, Orthography o) {
		String returnString = input;
		
		for (int i = 0; i < o.size(); i++) {
			String phonemes = o.getPair(i).getPhonemes();
			String graphemes = o.getPair(i).getGraphemes();
			returnString = returnString.replaceAll(graphemes, phonemes);
		}
		return returnString;
	}
	
}
