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
	
}
