package discordBot;

import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

public interface Response extends Comparable<Response> {
	
	/**
	 * The exec method that will return the response and execute the lambda 
	 * expression. The lambda expression supersedes the return response. 
	 * @param api
	 * @param event
	 * @return
	 */
	public String exec(DiscordApi api, MessageCreateEvent event);
	
	/**
	 * Overloaded exec method that will return the response (and if the object
	 * it is called upon is the help command it will add the string add). It will
	 * also execute any lambda function. The lambda expression supersedes the return
	 * response.
	 * @param api
	 * @param event
	 * @param add
	 * @return
	 */
	public String exec(DiscordApi api, MessageCreateEvent event, String add);
	
	/**
	 * Returns the command.
	 * @return
	 */
	public String getCommand();
	
	/**
	 * Returns the default response.
	 * @return
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
