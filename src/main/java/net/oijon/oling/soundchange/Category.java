package net.oijon.oling.soundchange;

import java.util.ArrayList;

/**
 * Creates a category for sound changes
 * @deprecated This will be part of Phonutate instead
 */
public class Category {

	private String name = "";
	private ArrayList<String> children = new ArrayList<String>();
	
	public Category(String parseStr) throws InvalidCategorySyntaxException {
		String[] splitStr = parseStr.split("=");
		try {
			if (splitStr[0].length() == 1) {
				name = splitStr[0];
			} else {
				throw new InvalidCategorySyntaxException("Category names must be 1 character");
			}
			for (int i = 0; i < splitStr[1].length(); i++) {
				children.add(Character.toString(splitStr[1].charAt(i)));
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new InvalidCategorySyntaxException(e);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<String> getChildren() {
		return children;
	}
	
	public void setName(String s) {
		this.name = s;
	}
	
}
