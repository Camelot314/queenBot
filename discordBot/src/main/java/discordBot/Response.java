package discordBot;

import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

/**
 * Interface that is the response objects. They can either be the pre-loaded default
 * responses or the custom responses that are created by Discord Users.
 * @author Jaraad
 *
 */
public interface Response extends Comparable<Response> {
	
	/**
	 * Overloaded exec method that will return the response (and if the object
	 * it is called upon is the help command it will add the string add). It will
	 * also execute any lambda function. The lambda expression supersedes the return
	 * response.
	 * @param api
	 * @param event
	 * @param add
	 * @return String that is the message or null if some action is performed
	 */
	public String exec(DiscordApi api, MessageCreateEvent event, String add);
	
	/**
	 * Returns the command.
	 * @return the command string
	 */
	public String getCommand();
	
	/**
	 * Returns the default response.
	 * @return returns the default response string.
	 */
	public String getDefaultResponse();
	
	/**
	 * compareTo method needed by the Comparable interface. It will compare by
	 * the command.
	 */
	public default int compareTo(Response other) {
		return getCommand().compareTo(other.getCommand());
	}
	
}
