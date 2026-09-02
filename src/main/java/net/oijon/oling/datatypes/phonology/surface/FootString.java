package net.oijon.oling.datatypes.phonology.surface;

import java.util.Arrays;

public class FootString {

	private final Foot[] feet;
	private final Syllable leftOver;
	private final boolean isLeftOverLeft;
	private final FootAlignment fa;
	private final FootBinarity fb;
	
	public FootString() {
		feet = new Foot[0];
		leftOver = null;
		fa = FootAlignment.LEFT;
		fb = FootBinarity.BINARY;
		this.isLeftOverLeft = true;
	}
	
	public FootString(Foot[] feet) {
		FootBinarity foundBinarity = FootBinarity.BINARY;
		FootAlignment foundAlignment = FootAlignment.LEFT;
		
		if (feet.length > 0) {
			Foot begin = feet[0];
			Foot end = feet[feet.length - 1];
			
			if (begin.getRight() == null) {
				foundAlignment = FootAlignment.RIGHT;
				foundBinarity = FootBinarity.PARSE;
			} else if (end.getRight() == null) {
				foundAlignment = FootAlignment.LEFT;
				foundBinarity = FootBinarity.PARSE;
			}
		}
		// if neither of the two options in the if block, assumes left alignment and binary syllables
		// note that this is somewhat arbitrary, this is just done so there's *something*
		this.feet = Arrays.copyOf(feet, feet.length);
		this.leftOver = null;
		this.isLeftOverLeft = true;
		this.fb = foundBinarity;
		this.fa = foundAlignment;
	}
	
	public FootString(Foot[] feet, FootAlignment fa, FootBinarity fb) {
		this.fa = fa;
		this.fb = fb;
		Boolean isLeftOverLeft = false;
		Syllable retSyll = new Syllable();
		this.feet = align(feet, null, fa, fb, retSyll, isLeftOverLeft);
		this.leftOver = retSyll;
		this.isLeftOverLeft = isLeftOverLeft;
	}
	
	
	// TODO: see if it makes sense to make this static
	/**
	 * Takes an array of feet, and optionally a left over syllable, and aligns it to a given FootAlignment and
	 * FootBinarity. If an alignment creates a leftover syllable, it will set returnSyll to it. It will also set
	 * isReturnSyllLeft if that leftover syllable is meant to be at the left of the foot array. This is very useful
	 * for things like concatenation, where there may be invalid, single mora feet in the middle that are meant to
	 * be a leftover.
	 * @param feet
	 * @param fa
	 * @param fb
	 * @param returnSyll
	 * @param isReturnSyllLeft
	 * @return
	 */
	private Foot[] align(Foot[] feet, Syllable leftOver, FootAlignment fa, FootBinarity fb, Syllable returnSyll,
			Boolean isReturnSyllLeft) {
		int numSylls = 0;
		if (leftOver != null) {
			numSylls++;
		}
		for (int i = 0; i < feet.length; i++) {
			if (feet[i].getLeft() != null) {
				numSylls++;
			}
			if (feet[i].getRight() != null) {
				numSylls++;
			}
		}
		
		
		
		Foot[] newFeet = new Foot[feet.length];
		
		return newFeet;
	}
}
