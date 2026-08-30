package net.oijon.oling.datatypes.phonology.surface;

import java.util.ArrayList;
import java.util.Arrays;

import net.oijon.oling.datatypes.phonology.Phonology;
import net.oijon.oling.datatypes.phonology.feature.Feature;
import net.oijon.oling.datatypes.phonology.table.Phoneme;
import net.oijon.oling.info.Info;
import net.oijon.olog.Log;

public class SyllableString {

	static final Log log = Info.log;
	private final Syllable[] sylls;
	
	public SyllableString() {
		this.sylls = new Syllable[0];
	}
	
	public SyllableString(Syllable[] s) {
		this.sylls = Arrays.copyOf(s, s.length);
	}
	
	public SyllableString(Phonology p, String str) {
		ArrayList<Syllable> sylls = new ArrayList<>();
		SoundString sounds = new SoundString(p, str);
		
		// group into clusters of nuclei and non-nuclei
		ArrayList<SoundString> groupedSounds = new ArrayList<>();
		SoundString currentGrouping = new SoundString();
		boolean lastWasNucleus = false;
		
		if (sounds.length() > 0) {
			// creates a blank group if starting with a nucleus
			// very helpful for alignment
			for (int i = 0; i < sounds.length(); i++) {
				Sound sound = sounds.soundAt(i);
				Phoneme phoneme = sound.getPhoneme();
				Feature nucleusF = phoneme.getFeatures().get("SYLLPART_NUCLEUS");
				boolean isNucleus = (nucleusF == null) ? false : nucleusF.getValue();
				
				if (isNucleus == lastWasNucleus) {
					currentGrouping = currentGrouping.concat(sound);
				} else {
					groupedSounds.add(currentGrouping);
					currentGrouping = new SoundString();
					currentGrouping = currentGrouping.concat(sound);
				}
				
				if (i == sounds.length() - 1) {
					groupedSounds.add(currentGrouping);
				}
				lastWasNucleus = isNucleus;
			}
			
			SoundString currentOnset = new SoundString();
			SoundString currentNucleus = new SoundString();
			SoundString currentCoda = new SoundString();
			for (int i = 0; i < groupedSounds.size(); i++) {
				SoundString group = groupedSounds.get(i);
				if (i == 0) {
					// must be all onset
					for (int j = 0; j < group.length(); j++) {
						Sound sound = group.soundAt(j);
						Feature onsetF = sound.getFeatures().get("SYLLPART_ONSET");
						boolean isOnset = (onsetF == null) ? false : onsetF.getValue();
						if (!isOnset) {
							log.err("Found sound [" + sound + "] that must logically be in onset position "
									+ "of syllable, but is not allowed per phonological system! "
									+ "Ignoring sound in syllable creation...");
						} else {
							currentOnset = currentOnset.concat(sound);
						}
					}
				} else if (i % 2 == 1) {
					// must be all nucleus
					// previously verified by grouping logic
					currentNucleus = group;
				} else if ((i < groupedSounds.size() - 1)) {
					int breakpoint = -1;
					// can either be onset or coda
					boolean hasBreakpoint = false;
					for (int j = 0; j < group.length(); j++) {
						Sound sound = group.soundAt(j);
						Feature codaF = sound.getFeatures().get("SYLLPART_CODA");
						boolean isCoda = (codaF == null) ? false : codaF.getValue();
						if (!hasBreakpoint && !isCoda) {
							hasBreakpoint = true;
							breakpoint = j;
						}
					}
					
					
					if (!hasBreakpoint) {
						// what we're looking for here is where sonorancy drops the most
						int lowestSonorancy = Integer.MAX_VALUE;
						int lowestIndex = -1;
						for (int j = 0; j < group.length(); j++) {
							int sonorancy = group.soundAt(j).getSonorancy();
							if (sonorancy <= lowestSonorancy) {
								lowestSonorancy = sonorancy;
								lowestIndex = j;
							}
						}
						breakpoint = lowestIndex;
					}
					
					for (int j = 0; j < breakpoint; j++) {
						currentCoda = currentCoda.concat(group.soundAt(j));
					}
					sylls.add(new Syllable(currentOnset, currentNucleus, currentCoda, p));
					currentOnset = new SoundString();
					currentNucleus = new SoundString();
					currentCoda = new SoundString();
					for (int j = breakpoint; j < group.length(); j++) {
						currentOnset = currentOnset.concat(group.soundAt(j));
					}
				} else {
					// must be all coda
					for (int j = 0; j < group.length(); j++) {
						Sound sound = group.soundAt(j);
						Feature codaF = sound.getFeatures().get("SYLLPART_CODA");
						boolean isCoda = (codaF == null) ? false : codaF.getValue();
						if (!isCoda) {
							log.err("Found sound [" + sound + "] that must logically be in coda position "
									+ "of syllable, but is not allowed per phonological system! "
									+ "Ignoring sound in syllable creation...");
						} else {
							currentCoda = currentCoda.concat(sound);
						}
					}
				}
			}
			sylls.add(new Syllable(currentOnset, currentNucleus, currentCoda, p));
		}
		this.sylls = sylls.toArray(new Syllable[0]);
	}
	
	public SyllableString(SyllableString s) {
		sylls = Arrays.copyOf(s.sylls, s.sylls.length);
	}
	
	public Syllable syllableAt(int i) {
		return sylls[i];
	}
	
	public SyllableString concat(SyllableString s) {
		Syllable[] newArr = new Syllable[this.sylls.length + s.sylls.length];
		for (int i = 0; i < this.sylls.length; i++) {
			newArr[i] = this.sylls[i];
		}
		for (int i = 0; i < s.sylls.length; i++) {
			newArr[this.sylls.length + i] = s.sylls[i];
		}
		return new SyllableString(newArr);
	}
	
	public SyllableString concat(Syllable s) {
		Syllable[] newArr = new Syllable[this.sylls.length + 1];
		for (int i = 0; i < this.sylls.length; i++) {
			newArr[i] = this.sylls[i];
		}
		newArr[this.sylls.length] = s;
		return new SyllableString(newArr);
	}
	
	public boolean endsWith(SyllableString suffix) {
		int startIndex = this.sylls.length - suffix.sylls.length;
		if (startIndex < 0) {
			return false;
		} else {
			for (int i = startIndex; i < this.sylls.length; i++) {
				if (!this.sylls[i].equals(suffix.sylls[i - startIndex])) {
					return false;
				}
			}
			return true;
		}
	}
	
	public void getSounds(int srcBegin, int srcEnd, Syllable[] dst, int dstBegin) {
		for (int i = srcBegin; i < srcEnd; i++) {
			dst[i + dstBegin] = this.sylls[i];
		}
	}
	
	public int indexOf(Syllable s, int start) {
		for (int i = start; i < this.sylls.length; i++) {
			if (this.sylls[i].equals(s)) {
				return i;
			}
		}
		return -1;
	}
	
	public int indexOf(Syllable s) {
		return indexOf(s, 0);
	}
	
	public boolean isEmpty() {
		return sylls.length == 0;
	}
	
	public int lastIndexOf(Syllable s, int offset) {
		for (int i = this.sylls.length; i >= offset; i--) {
			if (this.sylls[i].equals(s)) {
				return i;
			}
		}
		return -1;
	}
	
	public int lastIndexOf(Syllable s) {
		return lastIndexOf(s, 0);
	}
	
	public int length() {
		return this.sylls.length;
	}
	
	public SyllableString replace(Syllable oldS, Syllable newS) {
		Syllable[] newArr = new Syllable[this.sylls.length];
		for (int i = 0; i < this.sylls.length; i++) {
			if (this.sylls[i].equals(oldS)) {
				newArr[i] = newS;
			} else {
				newArr[i] = this.sylls[i];
			}
		}
		
		return new SyllableString(newArr);
	}
	
	public SyllableString replace(SyllableString oldS, SyllableString newS) {
		int numFound = 0;
		for (int i = 0; i < this.sylls.length - oldS.sylls.length; i++) {
			boolean found = true;
			for (int j = 0; j < oldS.sylls.length; j++) {
				if (!this.sylls[i + j].equals(oldS.sylls[j])) {
					found = false;
					break;
				}
			}
			if (found) {
				numFound++;
			}
		}
		
		Syllable[] newArr = new Syllable[this.sylls.length + (numFound * (newS.sylls.length - oldS.sylls.length))];
		
		int currentIndex = 0;
		for (int i = 0; i < this.sylls.length - oldS.sylls.length; i++) {
			boolean found = true;
			for (int j = 0; j < oldS.sylls.length; j++) {
				if (!this.sylls[i + j].equals(oldS.sylls[j])) {
					found = false;
					break;
				}
			}
			if (found) {
				for (int j = 0; j < newS.sylls.length; j++) {
					newArr[currentIndex] = newS.sylls[j];
					currentIndex++;
				}
			} else {
				newArr[currentIndex] = this.sylls[i];
				currentIndex++;
			}
		}
		
		return new SyllableString(newArr);
	}
	
	public boolean startsWith(SyllableString s, int offset) {
		if (s.sylls.length > this.sylls.length - offset) {
			return false;
		} else {
			for (int i = 0; i < s.sylls.length; i++) {
				if (!s.sylls[i].equals(this.sylls[i + offset])) {
					return false;
				}
			}
			return true;
		}
	}
	
	public boolean startsWith(SyllableString s) {
		return startsWith(s, 0);
	}
	
	public SyllableString substring(int begin, int end) {
		Syllable[] newSylls = new Syllable[end - begin];
		for (int i = begin; i < end; i++) {
			newSylls[i - begin] = this.sylls[i];
		}
		return new SyllableString(newSylls);
	}
	
	public SyllableString substring(int begin) {
		return substring(begin, this.sylls.length);
	}
	
	public Syllable[] toSyllableArray() {
		return Arrays.copyOf(sylls, this.sylls.length);
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		for (int i = 0; i < this.sylls.length; i++) {
			hash += sylls[i].hashCode() * 31 ^ (this.sylls.length - i - 1);
		}
		return hash;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof SyllableString) {
			SyllableString s = (SyllableString) o;
			return Arrays.equals(sylls, s.sylls);
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sylls.length; i++) {
			sb.append(sylls[i].toString());
			if (i < sylls.length - 1) {
				sb.append(".");
			}
		}
		return sb.toString();
	}
	
}
