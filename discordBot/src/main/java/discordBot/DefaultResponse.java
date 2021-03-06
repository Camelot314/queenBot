package discordBot;


import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

/**
 * This is the default response class. It is the object that houses all the pre-
 * loaded responses. these responses have lambda expressions. 
 * @author Jaraad
 *
 */
public class DefaultResponse implements Response {
	private String command, response;
	private boolean contains;
	private Executable lambda;
	private static String helpMessage;
	private static String helpCommand;
	private static int responseCount; 
	
	
	
	/**
	 * Constructor. 
	 * Initializes the static help message as the very first response
	 * that is put into the constructor. It also initializes a static
	 * random object that all the responses will use. 
	 * @param command
	 * @param response
	 * @param contains
	 * @param lambda
	 */
	public DefaultResponse (String command, String response, boolean contains, Executable lambda) {
		this.command = command;
		this.response = response;
		this.contains = contains;
		
		if (responseCount == 0) {
			helpMessage = response;
			helpCommand = command;
		}
		responseCount ++;
		
		this.lambda = lambda;
	}
	
	/**
	 * Sets the help message.
	 * @param help
	 * @return boolean true if changed false otherwise
	 */
	public static boolean setHelpStr (String help) {
		if (help != null) {
			helpMessage = help;
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the help string
	 * @return String the current help message for all responses.
	 */
	public static String getHelpStr () {
		return helpMessage;
	}
	
	/**
	 * Getter for the contains.
	 * @return boolean true if it uses the contains
	 */
	@Override
	public boolean isContains() {
		return contains;
	}
	
	/**
	 * Executes the lambda expression but this is made for the cases where there
	 * are custom commands and the help must be changed.
	 * @param api
	 * @param event
	 * @param additonal
	 * @return a string that will be sent either from response or lambda expression
	 *  or null if the input is different from the command.
	 */
	@Override
	public String exec (DiscordApi api, MessageCreateEvent event, String additonal) {
		String input = event.getMessageContent().toLowerCase();
		if ((contains && input.contains(command)) || input.equals(command)) {
			if (lambda != null) {
				return lambda.exec(api, event);
			}
			return isCommand(event.getMessageContent(), additonal);
		}
		return null;
	}
	
	
	/**
	 * Returns the command.
	 * @return
	 */
	@Override
	public String getCommand() {
		return command;
	}
	
	/**
	 * Returns the default response for the object.
	 * @return
	 */
	@Override
	public String getDefaultResponse() {
		if (response != null) {
			return response;
		}
		return "";
	}
	
	/**
	 * Compares responses by their command
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) 
			return true;
		if (!(obj instanceof Response))
			return false;
		
		Response other = (Response) obj;
		return other.getCommand().equals(command);
	}
	
	/**
	 * HashCode used for hashing. Relies on command string.
	 */
	@Override
	public int hashCode() {
		return command.hashCode();
	}
	
	
	/**
	 * ToString() method.
	 */
	@Override
	public String toString() {
		return getCommand() + " : " + getDefaultResponse();
	}
	
	/**
	 * Returns the command if based on the instance variables
	 * @param input
	 * @return a string.
	 */
	private String isCommand(String input, String additional) {
		String toReturn = null;
		
		additional = additional == null ? "" : additional;
		input = input.toLowerCase();
		if (input != null) {
			if (command.equals(helpCommand)) {
				toReturn = helpMessage;
				toReturn += additional == null ? "" : additional;
			} else {
				toReturn = response;
			}
		}
		return toReturn;
	}
}
