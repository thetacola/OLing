package net.oijon.oling.datatypes.phonology.feature;

public class Feature {

	private String name;
	private boolean value;
	
	public Feature(String name, boolean value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Gets the name of a feature
	 * @return The name of the feature
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the value of a feature. True for +, False for -
	 * @return The value of a feature
	 */
	public boolean getValue() {
		return value;
	}
	
	/**
	 * Sets the value of a feature. Useful for diacritics.
	 * @param value The new value of the feature
	 */
	public void setValue(boolean value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Feature) {
			Feature f = (Feature) o;
			if (f.getName().equals(name) && f.getValue() == value) {
				return true;
			}
		}
		return false;
	}
	
}
