package net.oijon.oling.datatypes.tags;

public class MultitagUtils {


	/**
	 * Checks if a line contains a starting multitag marker
	 * @param line The line to be checked
	 * @return true if the line is a starting multitag marker, false otherwise
	 */
	public static boolean isMultitagStart(String line) {
		if (getSecondPartOfMarker(line).equals("Start===")) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if a line contains an ending multitag marker
	 * @param line The line to be checked
	 * @return true if the line is an ending multitag marker, false otherwise
	 */
	public static boolean isMultitagEnd(String line) {
		if (getSecondPartOfMarker(line).equals("End===")) {
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the second part of a multitag marker. Should either be "Start===" or "End==="
	 * @param line The line to use for grabbing the second part of the marker
	 * @return The second part of the multitag marker. If not a multitag marker, returns a blank string.
	 */
	public static String getSecondPartOfMarker(String line) {
		String[] splitSpace = line.split(" ");
		if (isMultitagMarker(line)) {
			return splitSpace[1];
		}
		return "";
	}
	
	/**
	 * Gets the name of a multitag from its marker
	 * @param line The line with the marker in it
	 * @return The name of the given multitag
	 */
	public static String getMarkerTagName(String line) {
		if (isMultitagMarker(line)) {
			String[] splitSpace = line.split(" ");
			String name = splitSpace[0].substring(3);
			return name;
		} else {
			return "";
		}
	}
	
	/**
	 * Checks if a line contains a multitag marker
	 * @param line The line to be checked
	 * @return true if the line is a multitag marker, false otherwise
	 */
	public static boolean isMultitagMarker(String line) {
		String[] splitSpace = line.split(" ");
		String[] splitColon = line.split(":");
		if (splitSpace.length == 2 & splitColon.length != 2) {
			return true;
		}
		return false;
	}
	
}
