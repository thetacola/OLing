package net.oijon.oling.datatypes.phonology.surface;

import java.util.Arrays;

import net.oijon.oling.datatypes.phonology.Phonology;

public class FootString {
	
	private Foot[] feet;
	private Syllable leftOver;
	private boolean isLeftOverLeft = false;
	private FootBinarity fb;
	private FootAlignment fa;

	public FootString() {
		// these two defaults were chosen somewhat arbitrarily
		this(FootBinarity.PARSE, FootAlignment.LEFT);
	}
	
	public FootString(FootBinarity fb, FootAlignment fa) {
		feet = new Foot[0];
		this.fb = fb;
		this.fa = fa;
	}
	
	public FootString(Phonology p, String string, FootBinarity fb, FootAlignment fa) {
		this.fb = fb;
		this.fa = fa;
		SyllableString sylls = new SyllableString(p, string);
		boolean hasOddSylls = sylls.length() % 2 != 0;
		int startIndex = 0;
		int endIndex = sylls.length();
		int numFeet = (sylls.length() / 2) +
				((sylls.length() % 2) & (fb == FootBinarity.PARSE ? 1 : 0));
		feet = new Foot[numFeet];
		if (hasOddSylls) {
			if (fb == FootBinarity.BINARY) {
				if (fa == FootAlignment.LEFT) {
					this.isLeftOverLeft = false;
					leftOver = sylls.syllableAt(sylls.length() - 1);
					endIndex--;
				} else {
					// if not left, must be right
					this.isLeftOverLeft = true;
					leftOver = sylls.syllableAt(0);
					startIndex++;
				}
			} else {
				// if not FtBin, must be Parse
				// i'm iffy about making this an else if, as i'm not sure what would happen if both return false
				if (fa == FootAlignment.LEFT) {
					feet[feet.length - 1] = new Foot(sylls.syllableAt(sylls.length() - 1));
					endIndex--;
				} else {
					// if not left, must be right
					feet[0] = new Foot(sylls.syllableAt(0));
					startIndex++;
				}
			}
		}
		
		int currentFoot = (fb == FootBinarity.PARSE ? startIndex : 0);
		for (int i = startIndex; i < endIndex - 1; i += 2) {
			feet[currentFoot] = new Foot(sylls.syllableAt(i), sylls.syllableAt(i + 1));
			currentFoot++;
		}
		
	}
	
	public FootString(FootString fs) {
		this.feet = Arrays.copyOf(fs.feet, fs.feet.length);
		this.fa = fs.fa;
		this.fb = fs.fb;
		this.isLeftOverLeft = fs.isLeftOverLeft;
		this.leftOver = fs.leftOver;
	}
	
	public FootString(Foot[] feet) {
		this(feet, 0, feet.length);
	}
	
	public FootString(Foot[] feet, int start, int end) {
		this.feet = Arrays.copyOfRange(feet, start, end);
		// this attempts to infer the needed properties
		Foot beginF = this.feet[0];
		Foot endF = this.feet[this.feet.length - 1];
		
		if (beginF.getRight() == null) {
			this.fa = FootAlignment.RIGHT;
			this.fb = FootBinarity.PARSE;
		} else if (endF.getRight() == null) {
			this.fa = FootAlignment.LEFT;
			this.fb = FootBinarity.PARSE;
		} else {
			// If it's an even pairing throughout, directionality can't be intuited
			// Marking it as left just so there's *something*, this does mean this constructor
			// can never make right aligned binary FootStrings
			this.fa = FootAlignment.LEFT;
			this.fb = FootBinarity.BINARY;
		}
	}
	
	public FootString(Foot[] feet, FootAlignment fa, FootBinarity fb) {
		this(feet, fa, fb, 0, feet.length);
	}
	
	public FootString(Foot[] feet, FootAlignment fa, FootBinarity fb, int start, int end) {
		this.feet = Arrays.copyOfRange(feet, start, end);
		this.fa = fa;
		this.fb = fb;
	}
	
	public FootString(Foot[] feet, FootAlignment fa, FootBinarity fb, Syllable leftOver, boolean isLeftOverLeft) {
		this.feet = Arrays.copyOf(feet, feet.length);
		this.fa = fa;
		this.fb = fb;
		this.leftOver = leftOver;
		this.isLeftOverLeft = isLeftOverLeft;
	}
	
	public Foot footAt(int index) {
		return feet[index];
	}
	
	public boolean endsWith(FootString fs) {
		if (fs.feet.length > this.feet.length) {
			return false;
		}
		
		// a rather quick way to check for the ending in left binary odd-numbered syll strings
		if (this.fa == FootAlignment.LEFT && this.fb == FootBinarity.BINARY) {
			if (this.leftOver != null && fs.leftOver != null) {
				if (!this.leftOver.equals(fs.leftOver)) {
					return false;
				}
			} else if (this.leftOver != fs.leftOver) {
				// one is null, other isn't
				return false;
			}
		}
		
		for (int i = 0; i < fs.feet.length; i++) {
			Foot fsFoot = fs.footAt(fs.feet.length - i - 1);
			Foot thisFoot = this.footAt(this.feet.length - i - 1);
			if (!fsFoot.equals(thisFoot)) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isLeftOverLeft() {
		return isLeftOverLeft;
	}
	
	public boolean hasLeftOver() {
		return (leftOver != null);
	}
	
	public Syllable getLeftOver() {
		return leftOver;
	}
	
	public FootAlignment getAlignment() {
		return fa;
	}
	
	public FootBinarity getBinarity() {
		return fb;
	}
	
	/**
	public FootString setAlignment(FootAlignment fa) {
		FootString fs;
		
		return fs;
	}
	
	public FootString setBinarity(FootBinarity fb) {
		FootString fs;
		
		return fs;
	}
	*/
}
