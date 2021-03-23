package discordBot;

import java.awt.Color;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

/**
 * Utilities class that provides some stuff and holds all static methods.
 * @author Jaraad
 *
 */
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
	
	/**
	 * Method called when a response that relies on contains is found in the for 
	 * loop
	 * @param event
	 * @param toSend
	 * @param sent
	 * @return boolean true if it sent the message false if not
	 */
	public static boolean foundResponseCont(MessageCreateEvent event, String toSend) {
		boolean sent = false;
		if (toSend != null) {
			event.getChannel().sendMessage(toSend);
			sent = true;
		}
		return sent;
	}
	
	/**
	 * Sends a message in the channel it received the message.
	 * @param event
	 * @param toSend
	 */
	public static void sendMessage(MessageCreateEvent event, String toSend) {
		if (toSend != null) {
			event.getChannel().sendMessage(toSend);
		}
	}
	
	/**
	 * Sends the appropriate embedded Message
	 * @param event
	 * @param commandStr
	 */
	public static void sendCustomsListMessage(MessageCreateEvent event) {
		
		String commandStr = "!customs-on/off" + Utilities.addSpaces("!customs on/off") 
			+ ": enables or disables customs\n" + "!customs-clear" 
			+ Utilities.addSpaces("!customs clear") + ": clears all customs\n"
			+ "!customs-add" + Utilities.addSpaces("!customs add") 
			+ ": adds custom (input and output need to be separated by a \":\""
			+ "!customs-remove" + Utilities.addSpaces("!customs remove") 
			+ ": removes the specified custom by input";
		
		new MessageBuilder().setEmbed(new EmbedBuilder()
				.setTitle("Customs Settings")
				.setColor(Color.yellow)
				.setDescription(commandStr)
			).send(event.getChannel());
	}
	
	/**
	 * Sends an error message when the parser doesn't function.
	 * @param event
	 */
	public static void sendErrorRemoveMessage(MessageCreateEvent event) {
		event.getChannel().sendMessage(
				"I could not understand\n" +
				"Make sure your text reads the command then "
				+ "\"input of custom\""
			);
	}
	
	/**
	 * Sends a message confirming that the response has been added.
	 * @param event
	 * @param args
	 */
	public static void sendAddConfirmation(MessageCreateEvent event, String[] args) {
		event.getChannel().sendMessage(
				"\"" + args[0] + "\" triggers" 
				+ "\"" + args[1] + "\""
		);
	}
	
	/**
	 * Sends an error message depending on the boolean.
	 * @param event
	 * @param alreadyUsed
	 */
	public static void sendErrorAddMessage(MessageCreateEvent event, boolean alreadyUsed) {
		if (alreadyUsed) {
			event.getChannel().sendMessage(
					"Sorry but I can't let"
					+ "you add this response it may already be used"
					+ " for reasons");
			return;
		}
		event.getChannel().sendMessage(
				"I could not understand\n" +
				"Make sure your text reads the command then "
				+ "\"input : output\""
			);
	}
}
