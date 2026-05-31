package net.oijon.oling.datatypes.phonology.feature;

public class RankedFeature extends Feature {

	// useful for sonority
	
	private int rank = -1;
	
	public RankedFeature(String name, boolean value, FeatureLevel level, int rank) {
		super(name, value, level);
		this.rank = rank;
		// TODO Auto-generated constructor stub
	}
	
	public int getRank() {
		return rank;
	}

}
