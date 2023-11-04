package net.oijon.oling.datatypes;

public class OrthoPair implements Comparable<OrthoPair> {

	// Last edit: 11/4/23 ~n3
	
	private String phonemes;
	private String graphemes;
	
	public OrthoPair(String phonemes, String graphemes) {
		this.phonemes = phonemes;
		this.graphemes = graphemes;
	}
	
	public String getPhonemes() {
		return phonemes;
	}
	
	public String getGraphemes() {
		return graphemes;
	}
	
	public void setPhonemes(String phonemes) {
		this.phonemes = phonemes;
	}
	
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
