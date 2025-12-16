package net.oijon.oling.datatypes;

public class InvalidXMLException extends Exception {


	public InvalidXMLException() {
		super("Unable to properly parse XML");
	}

	public InvalidXMLException(String s) {
		super("Unable to properly parse XML: " + s);
	}

}
