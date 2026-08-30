package net.oijon.oling.datatypes.phonology.surface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.oijon.oling.datatypes.phonology.Phonology;
import net.oijon.oling.datatypes.phonology.table.Phoneme;
import net.oijon.oling.datatypes.phonology.table.PhonoSystem;

/**
 * Creates a string out of sounds.
 * Like Java strings, they are immutable. Treat them as such.
 */
public class SoundString {

	private final Sound[] sounds;
	
	public SoundString() {
		this.sounds = new Sound[0];
	}
	
	public SoundString(Phonology p, String str) {
		PhonoSystem ps = p.getPhonoSystem();
		
		// FIXME: cannot handle diacritics preceding a character!
		/*
		 * this could be fixed by marking in the phono system xml the position of a diacritic in
		 * relation to the base phoneme character, then looking for the indecies of these
		 * when a pre-diacritic is found, it should then skip to the next phoneme when searching
		 */
		
		// sorts the two in cases where someone tries using a system with multiple chars for an atomic phoneme
		// if one atomic phoneme is represented with 'a', and another as 'ab', without sorting this would be UB
		// doesn't happen in IPA, though it is supported in the way it's being stored
		// that said, perhaps it shouldn't be...
		ArrayList<Sound> foundSounds = new ArrayList<>();
		
		ArrayList<Phoneme> allPhonemes = ps.getAllPhonemes();
		allPhonemes.sort((a, b) -> {return -1 * Integer.compare(a.getSound().length(),
				b.getSound().length());});
		
		
		ArrayList<String> allDiacritics = ps.getDiacriticKeys();
		allDiacritics.sort((a, b) -> {return -1 * Integer.compare(a.length(), b.length());});
		
		// TODO: go through the diacritic list, check if it should appear before or after,
		// then put in separate lists
		
		StringBuilder phonemeSB = new StringBuilder();
		phonemeSB.append('(');
		for (int i = 0; i < allPhonemes.size(); i++) {
			phonemeSB.append(Pattern.quote(allPhonemes.get(i).getSound()));
			if (i < allPhonemes.size() - 1) {
				phonemeSB.append('|');
			}
		}
		phonemeSB.append(')');
		
		StringBuilder endDiacriticSB = new StringBuilder();
		endDiacriticSB.append('(');
		for (int i = 0; i < allDiacritics.size(); i++) {
			endDiacriticSB.append(Pattern.quote(allDiacritics.get(i)));
			if (i < allDiacritics.size() - 1) {
				endDiacriticSB.append('|');
			}
		}
		endDiacriticSB.append(")*");
		
		// should look something like (\Qg\E)*(\Qab\E|\Qa\E|\Qb\E|\Qc\E)(\Qd\E|\Qe\E|\Qf\E)*
		// right now looks more like (\Qab\E|\Qa\E|\Qb\E|\Qc\E)(\Qd\E|\Qe\E|\Qf\E)*
		Pattern phonemeRegex = Pattern.compile(phonemeSB.toString() + endDiacriticSB.toString());
		
		Matcher matcher = phonemeRegex.matcher(str);
		
		while (matcher.find()) {
			Sound s = new Sound(matcher.group(), p);
			foundSounds.add(s);
		}
		
		this.sounds = foundSounds.toArray(new Sound[0]);
	}
	
	public SoundString(SoundString s) {
		this.sounds = new Sound[s.sounds.length];
		for (int i = 0; i < this.sounds.length; i++) {
			this.sounds[i] = new Sound(s.sounds[i]);
		};
	}
	
	public SoundString(Sound[] sounds) {
		this.sounds = new Sound[sounds.length];
		for (int i = 0; i < sounds.length; i++) {
			this.sounds[i] = new Sound(sounds[i]);
		}
	}
	
	public SoundString(Sound[] sounds, int offset, int count) {
		this.sounds = new Sound[count];
		for (int i = 0; i < count; i++) {
			this.sounds[i] = new Sound(sounds[i + offset]);
		}
	}
	
	public Sound soundAt(int index) {
		return sounds[index];
	}
	
	public SoundString concat(SoundString s) {
		int len = s.sounds.length + this.sounds.length;
		Sound[] newSounds = new Sound[len];
		for (int i = 0; i < this.sounds.length; i++) {
			newSounds[i] = this.sounds[i];
		}
		for (int i = 0; i < s.sounds.length; i++) {
			newSounds[i + this.sounds.length] = s.sounds[i];
		}
		SoundString ret = new SoundString(newSounds);
		return ret;
	}
	
	public SoundString concat(Sound s) {
		Sound[] newSounds = new Sound[this.sounds.length + 1];
		for (int i = 0; i < this.sounds.length; i++) {
			newSounds[i] = this.sounds[i];
		}
		newSounds[this.sounds.length] = s;
		return new SoundString(newSounds);
	}
	
	public boolean endsWith(SoundString suffix) {
		int startIndex = this.sounds.length - suffix.sounds.length;
		if (startIndex < 0) {
			return false;
		} else {
			for (int i = startIndex; i < this.sounds.length; i++) {
				if (!this.sounds[i].equals(suffix.sounds[i - startIndex])) {
					return false;
				}
			}
			return true;
		}
	}
	
	public void getSounds(int srcBegin, int srcEnd, Sound[] dst, int dstBegin) {
		for (int i = srcBegin; i < srcEnd; i++) {
			dst[i + dstBegin] = new Sound(this.sounds[i]);
		}
	}
	
	public int getWeight() {
		int weight = 0;
		for (int i = 0; i < this.sounds.length; i++) {
			weight += this.sounds[i].getWeight();
		}
		return weight;
	}
	
	public int indexOf(Sound s, int fromIndex) {
		int firstIndex = -1;
		for (int i = fromIndex; i < this.sounds.length; i++) {
			if (s.equals(this.sounds[i])) {
				firstIndex = i;
				break;
			}
		}
		return firstIndex;
	}
	
	public int indexOf(Sound s) {
		return indexOf(s, 0);
	}
	
	public boolean isEmpty() {
		return this.sounds.length == 0 ? true : false;
	}
	
	public int lastIndexOf(Sound s, int fromIndex) {
		for (int i = this.sounds.length - 1; i >= fromIndex; i--) {
			if (this.sounds[i].equals(s)) {
				return i;
			}
		}
		return -1;
	}
	
	public int lastIndexOf(Sound s) {
		return lastIndexOf(s, 0);
	}
	
	public int length() {
		return this.sounds.length;
	}
	
	// TODO: implement regionMatches
	
	public SoundString replace(Sound oldSound, Sound newSound) {
		Sound[] newArr = new Sound[this.sounds.length];
		for (int i = 0; i < newArr.length; i++) {
			if (this.sounds[i].equals(oldSound)) {
				newArr[i] = newSound;
			} else {
				newArr[i] = this.sounds[i];
			}
		}
		return new SoundString(newArr);
	}
	
	public SoundString replace(SoundString oldS, SoundString newS) {
		int count = 0;
		for (int i = 0; i < this.sounds.length - oldS.length() + 1; i++) {
			boolean found = true;
			for (int j = 0; j < oldS.length(); j++) {
				if (!sounds[i + j].equals(oldS.soundAt(i + j))) {
					found = false;
					break;
				}
			}
			if (found) {
				count++;
			}
		}
		
		int newLen = this.sounds.length + ((newS.length() - oldS.length()) * count);
		Sound[] sounds = new Sound[newLen];
		
		int newIndex = 0;
		for (int i = 0; i < this.sounds.length; i++) {
			boolean found = false;
			if (oldS.length() < this.sounds.length - i) {
				found = true;
				for (int j = 0; j < oldS.length(); j++) {
					if (!sounds[i + j].equals(oldS.soundAt(i + j))) {
						found = false;
						break;
					}
				}
			}
			if (found) {
				for (int j = 0; j < newS.length(); j++) {
					sounds[newIndex] = newS.soundAt(j);
					newIndex++;
				}
			} else {
				sounds[newIndex] = this.sounds[i];
				newIndex++;
			}
		}
		
		return new SoundString(sounds);
	}
	
	public boolean startsWith(SoundString s, int toffset) {
		if (toffset <= this.sounds.length && s.length() <= this.sounds.length) {
			for (int i = toffset; i < this.sounds.length; i++) {
				if (!this.sounds[i].equals(s.soundAt(i - toffset))) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	public boolean startsWith(SoundString s) {
		return startsWith(s, 0);
	}
	
	public SoundString substring(int beginIndex, int endIndex) {
		Sound[] newArr = new Sound[endIndex - beginIndex];
		for (int i = beginIndex; i < endIndex; i++) {
			newArr[i - beginIndex] = this.sounds[i];
		}
		return new SoundString(newArr);
	}
	
	public SoundString substring(int beginIndex) {
		return substring(beginIndex, this.sounds.length);
	}
	
	public Sound[] toSoundArray() {
		Sound[] retArr = Arrays.copyOf(this.sounds, this.sounds.length);
		return retArr;
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		for (int i = 0; i < this.sounds.length; i++) {
			hash += sounds[i].hashCode() * 31 ^ (this.sounds.length - i - 1);
		}
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SoundString) {
			SoundString s = (SoundString) obj;
			if (s.sounds.length == this.sounds.length) {
				for (int i = 0; i < this.sounds.length; i++) {
					if (!s.sounds[i].equals(this.sounds[i])) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.sounds.length; i++) {
			sb.append(this.sounds[i].toString());
		}
		return sb.toString();
	}
}
