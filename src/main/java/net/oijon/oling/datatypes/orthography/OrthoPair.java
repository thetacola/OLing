package net.oijon.oling.datatypes.orthography;

public class OrthoPair implements Comparable<OrthoPair> {

	// Last edit: 11/4/23 ~n3
	
	private String phonemes;
	private String graphemes;
	
	/**
	 * Creates a correlation between a given set of phonemes and a given set of graphemes
	 * @param phonemes The phonemes of this pair
	 * @param graphemes The graphemes of this pair
	 */
	public OrthoPair(String phonemes, String graphemes) {
		this.phonemes = phonemes;
		this.graphemes = graphemes;
	}
	
	/**
	 * Gets the set of phonemes of this pair
	 * @return The phonemes in question
	 */
	public String getPhonemes() {
		return phonemes;
	}
	
	/**
	 * Gets the set of graphemes of this pair
	 * @return The graphemes in question
	 */
	public String getGraphemes() {
		return graphemes;
	}
	
	/**
	 * Sets the set of phonemes of this pair
	 * @param phonemes The new set of phonemes
	 */
	public void setPhonemes(String phonemes) {
		this.phonemes = phonemes;
	}
	
	/**
	 * Sets the set of graphemes of this pair
	 * @param graphemes The new set of graphemes
	 */
	public void setGraphemes(String graphemes) {
		this.graphemes = graphemes;
	}

	@Override
	public int compareTo(OrthoPair o) {
		return phonemes.compareTo(o.getPhonemes());
	}
	
	@Override
	public String toString() {
		return phonemes + ":" + graphemes;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof OrthoPair) {
			OrthoPair op = (OrthoPair) o;
			if (op.getPhonemes().equals(phonemes) & op.getGraphemes().equals(graphemes)) {
				return true;
			}
		}
		return false;
	}
	
}
