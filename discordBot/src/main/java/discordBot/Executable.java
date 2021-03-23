package discordBot;

import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

/**
 * This is the Single method interface that is used for the 
 * lambda expressions in the queen and response files.
 * @author Jaraad
 *
 */
@FunctionalInterface
public interface Executable {
	public String exec (DiscordApi api, MessageCreateEvent event);
}
