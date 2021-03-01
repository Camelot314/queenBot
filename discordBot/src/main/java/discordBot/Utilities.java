package discordBot;



public final class Utilities {

	
	/**
	 * Adds spaces to a string. will add 30 - the initial string length.
	 * @param initial
	 * @return
	 */
	public static String addSpaces(String initial) {
		int needed = 30 - initial.length();
		StringBuffer spaces = new StringBuffer();
		for (int i = 0; i < needed; i ++) {
			spaces.append(" ");
		}
		return spaces.toString();
	}
}
