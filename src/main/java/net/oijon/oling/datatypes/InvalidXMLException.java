package net.oijon.oling.datatypes;

public class InvalidXMLException extends Exception {


	private static final long serialVersionUID = 939202369881322740L;

	public InvalidXMLException() {
		super("Unable to properly parse XML");
	}

	public InvalidXMLException(String s) {
		super("Unable to properly parse XML: " + s);
	}

}
