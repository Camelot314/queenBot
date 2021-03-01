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
	
	/**
	 * This takes in a string and processes it. The first thing it does is make 
	 * a substring after the space. It then trims and processes it. So that
	 * !ada heh hi : hello. returns a string array of ["heh hi", "hello"].
	 * if the size of the array does not match the argument number then it returns
	 * null.
	 * @param input
	 * @param argumentNumber
	 * @return
	 */
	public static String[] interpreter(String input, int argumentNumber, boolean skipFirstWord) {
		String[] toReturn = null;
		if (input == null || (skipFirstWord && input.indexOf(' ') < 0)) {
			return null;
		}
		String afterCommandCall;
		if (skipFirstWord) {
			afterCommandCall = input.substring(input.indexOf(' '));
		} else {
			afterCommandCall = input;
		}
		afterCommandCall = afterCommandCall.trim();
		if (afterCommandCall.isEmpty()) {
			return null;
		}
		int colonIndex = afterCommandCall.indexOf(':');
		String firstArg = null;
		String secondArg = null;
		if (argumentNumber > 1) {
			if (colonIndex < 1 || colonIndex == afterCommandCall.length() - 1) {
				return null;
			} else {
				firstArg = afterCommandCall.substring(0, colonIndex);
				firstArg = firstArg.trim();
				secondArg = afterCommandCall.substring(colonIndex + 1, afterCommandCall.length());
				secondArg = secondArg.trim();
				
				if (firstArg.isEmpty() || secondArg.isEmpty()) {
					return null;
				} else {
					toReturn = new String[] {
							firstArg, secondArg
					};
				}
			}
		} else if (argumentNumber == 1) {
			toReturn = new String[] {afterCommandCall};
		}
		return toReturn;		
		
	}
}
