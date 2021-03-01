package discordBot;

import java.util.Random;

import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

/**
 * This is the default response class. It is the object that houses all the pre-
 * loaded responses. these responses have lambda expressions. 
 * @author Jaraad Kamal
 *
 */
public class DefaultResponse implements Response {
	private String command, response, response2;
	private boolean contains, isProb;
	private int odd;
	private Executable lambda;
	private static Random random;
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
	 * @param isProb
	 * @param odd
	 */
	public DefaultResponse (String command, String response, String response2,
			boolean contains, boolean isProb, int odd, Executable lambda) {
		
		this.command = command;
		this.response = response;
		this.response2 = response2;
		this.contains = contains;

		this.odd = odd >= 0 ? odd : 0;
		if (odd == 0) {
			this.isProb = false;
		} else {
			this.isProb = true;
		}
		
		if (responseCount == 0) {
			helpMessage = response;
			helpCommand = command;
			random = new Random();
		}
		responseCount ++;
		
		this.lambda = lambda;
	}
	
	/**
	 * Another constructor
	 * @param command
	 * @param response
	 * @param contains
	 */
	public DefaultResponse (String command, String response,
			boolean contains) {
		this (command, response, null, contains, false, 0, null);
	}
	
	/**
	 * Constructor
	 * @param command
	 * @param response
	 * @param contains
	 * @param function
	 */
	public DefaultResponse (String command, String response, boolean contains, 
			Executable function) {
		
		this (command, response, null, contains, false, 0, function);
	}
	
	/**
	 * Constructor
	 * @param command
	 * @param response
	 * @param response2
	 * @param contains
	 * @param odd
	 */
	public DefaultResponse (String command, String response, String response2,
			boolean contains, int odd) {
		
		this (command, response, response2, contains, true, odd, null);
	}
	
	/**
	 * Constructor
	 * @param command
	 * @param response
	 * @param response2
	 * @param contains
	 * @param odd
	 * @param function
	 */
	public DefaultResponse (String command, String response, String response2,
			boolean contains, int odd, Executable function) {
		
		this (command, response, response2, contains, true, odd, function);
	}
	
	
	/**
	 * This is a constructor that will be used to binary search. The object
	 * initialized by this constructor should not be used by any other method
	 * than the searching in the queen class.
	 * @param command
	 */
	public DefaultResponse (String command) {
		this(command, null, false);
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
	 * @return
	 */
	public static String getHelpStr () {
		return helpMessage;
	}
	
	/**
	 * Executes the lambda expression
	 * @param input
	 */
	@Override
	public String exec (DiscordApi api, MessageCreateEvent event) {
		if (lambda != null) {
			return lambda.exec(api, event);
		} else {
			return isCommand(event.getMessageContent(), null);
		}
	}
	
	/**
	 * Executes the lambda expression but this is made for the cases where there
	 * are custom commands and the help must be changed.
	 * @param api
	 * @param event
	 * @param additonal
	 * @return
	 */
	@Override
	public String exec (DiscordApi api, MessageCreateEvent event, String additonal) {
		if (lambda != null) {
			return lambda.exec(api, event);
		} else {
			return isCommand(event.getMessageContent(), additonal);
		}
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
	 * ToString() method.
	 */
	@Override
	public String toString() {
		return getCommand() + " : " + getDefaultResponse();
	}
	
	/**
	 * Returns the command if based on the instance variables
	 * @param input
	 * @return
	 */
	private String isCommand(String input, String additional) {
		boolean send;
		String toReturn = null;
		input = input.toLowerCase();
		if (input != null) {
			send = contains && !input.contains(helpMessage) && input.contains(command) 
					|| input.equals(command);
			if (input.contains("yes peasant") || input.contains("do you want a 14th")) {
				send = false;
			} else if (send && !isProb) {
				toReturn = response;
			} else if (send && isProb) {
				if (random.nextInt(odd) == 0) {
					toReturn =  response2;
				} else {
					if (command.equals(helpCommand)) {
						toReturn = helpMessage;
						toReturn += additional == null ? "" : additional;
					} else {
						toReturn = response;
					}
				}
			}
		}
		return toReturn;
	}
}
