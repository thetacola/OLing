package net.oijon.oling.info;

/**
 * A class to get the version information of the current build
 * @author alex
 *
 */
public class Info {

	private static String versionNum = generateVersionNum();
	private static String fullVersion = "OLing - v" + versionNum;
	
	private static String generateVersionNum() {
		return "2.1.0";
	}
	
	/**
	 * Gets the current version of OLing, for example "OLing - v1.2.0"
	 * @return The current version of OLing
	 */
	public static String getVersion() {
		return fullVersion;
	}
	
	/**
	 * Gets the version number of OLing, for example "1.1.1"
	 * @return The current version number of OLing
	 */
	public static String getVersionNum() {
		return versionNum;
	}
	
}
