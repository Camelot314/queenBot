package discordBot;

import java.io.Serializable;

import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

/**
 * The simple response class is the type of response that is created by the 
 * Discord User. 
 * @author Jaraad
 *
 */
public final class SimpleResponse implements Serializable, Response {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4192087201373479648L;
	private String command, response;
	
	/**
	 * Constructor
	 * @param command
	 * @param response
	 */
	public SimpleResponse(String command, String response) {
		this.command = command;
		this.response = response;
	}
	
	/**
	 * Constructor that is used for the Collections binary search method.
	 * The object is initialized with nothing other than the command. Do not
	 * use the object initialized with this constructor for anything else.
	 * @param command
	 */
	public SimpleResponse(String command) {
		this.command = command;
	}

	/**
	 * The same thing but with an additional param. This is needed to satisfy the
	 * interface.
	 */
	@Override
	public String exec(DiscordApi api, MessageCreateEvent event, String add) {
		return getDefaultResponse();
	}

	/**
	 * Returns the command.
	 */
	@Override
	public String getCommand() {
		return command;
	}

	/**
	 * Gets the response.
	 */
	@Override
	public String getDefaultResponse() {
		return response;
	}
	
	/**
	 * Equals method. It compares by the command.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) 
			return true;
		if (!(obj instanceof Response)) 
			return false;
		
		Response other = (Response) obj;
		return command.equals(other.getCommand());
	}
	
	/**
	 * toString() method.
	 */
	@Override
	public String toString() {
		return getCommand() + " : " + getDefaultResponse();
	}
}
