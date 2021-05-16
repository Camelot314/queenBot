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
	private static final long serialVersionUID = -4192087201373479650L;
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
	 * Returns false all the time.
	 */
	@Override
	public boolean isContains() {
		return false;
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
		
		return command.equals(((SimpleResponse) obj).getCommand());
	}
	
	/**
	 * HashCode relies on command string.
	 */
	@Override
	public int hashCode() {
		return command.hashCode();
	}
	
	/**
	 * toString() method.
	 */
	@Override
	public String toString() {
		return getCommand() + " : " + getDefaultResponse();
	}
}
