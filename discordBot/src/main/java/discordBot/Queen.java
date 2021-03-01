package discordBot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

/**
 * This is QueenBot
 * She can be found at:
 * https://discord.com/api/oauth2/authorize?client_id=807463236143546388&permissions=1111489648&scope=bot
 * 
 * QueenBot will end the program if the authorized terminator sends the kill 
 * code which is /@botname !terminate.
 * 
 * @author Jaraad Kamal
 *
 */
public class Queen {
	private static final String USERNAME_OF_AUTH_TERMINATOR = /* discord UserID of the person you want to have control */ null;
	private static final String TOKEN = /* Bot token provided by discord */ null;
	private static final String HELP_COMMAND = "!help";
	private String help;
	private DiscordApi api;
	private boolean isRunning;	
	private ArrayList<Response> responses;
	private ArrayList<Response> responseRelyContains;
	private static final Random RANDOM = new Random();
	
	public Queen() {     
        
		help = "Hello I am QueenBot\n"
	              + "I have a list of commands or phrases u may want to use:\n"
	              + "!help" + Utilities.addSpaces("!help") + ": brings up my commandments";        
        
        
        // creates the arraylists.
        responses = new ArrayList<>();
        responseRelyContains = new ArrayList<>();
        
        addResponse(HELP_COMMAND, help, help, false, false, null);
        addResponse("?", null, "yes / no", true, false, (api, event) -> {
			String message = RANDOM.nextBoolean() ? "yes" : "no";			
			event.getChannel().sendMessage(message);			
			return null;
		});
        addTerminatorCommand();
	}

	
	
	/**
	 * This is the add response method. It will take in all the arguments needed
	 * by the response constructor. Additionally it takes in a boolean adminOnly. 
	 * This specifies whether or not the command will be seen when the !help is
	 * called. This also takes in a lambda expression which is the code that
	 *  will be run when the response is called. 
	 * @param command
	 * @param response
	 * @param response2
	 * @param displayMes
	 * @param contains
	 * @param odd
	 * @param adminOnly
	 * @param lambda
	 * @throws IllegalArgumentException if command is null or both responses and
	 * lambda are null.
	 */
		
	public void addResponse(String command, String response, 
				String displayMes, boolean contains, boolean adminOnly, 
				Executable lambda) {
		
		if (command == null) {
			throw new IllegalArgumentException("You must have an agrument for command");
		}
		
		if (response == null && lambda == null) {
			throw new IllegalArgumentException(
					"If you have no responses then the executable must not be null"
			);
		}
		Response temp = new DefaultResponse(command, response, contains, lambda);

		if (!adminOnly) {
			String helpMessage = DefaultResponse.getHelpStr();
			helpMessage = helpMessage == null ? "" : helpMessage;
			
			helpMessage += "\n" + command +
					Utilities.addSpaces(command) + ": " + displayMes;
			help = helpMessage;
			DefaultResponse.setHelpStr(helpMessage);
		}
		responses.add(temp);
		if (contains) {
			responseRelyContains.add(temp);
		}
	}
	
	/**
	 * Runs the api if there is not already 1 instance running.
	 */
	public void run () {
		if (!isRunning && TOKEN != null) {
			isRunning = true;
			Collections.sort(responses);
			
			api = new DiscordApiBuilder().setToken(TOKEN).login().join();
			api.addMessageCreateListener(event -> {
				Collections.sort(responses);
	        	String toSend = null;
	        	String input;
        		DefaultResponse toFind;
        		Response response;
	        	
	        	long messageSender = event.getMessageAuthor().getId();
	        	boolean senderIsYou = messageSender == api.getYourself().getId();
	        	
	        	if (!senderIsYou) {
	        		input = event.getMessageContent().toLowerCase();
	        		toFind = new DefaultResponse(input);
	        		
	        		int index = Collections.binarySearch(responses, toFind);
	        		response = index > -1 ? responses.get(index) : null;
	        		
	        		if (response != null) {
						toSend = response.exec(api, event);
						if (toSend != null && toSend.equals(help)) {
							new MessageBuilder().setEmbed(new EmbedBuilder().setTitle("Commandments")
									.setColor(Color.yellow).setDescription(help))
									.send(event.getChannel());
						} else if (toSend != null) {
							event.getChannel().sendMessage(toSend);
						} 
	        		} else {
	        			// where the binary search didn't work but there still
	        			// may be a chance that it is in rely contains.
	        			
	        			for (Response responseCont : responseRelyContains) {
	        				toSend = responseCont.exec(api, event);
	        				if (toSend != null) {
	        					event.getChannel().sendMessage(toSend);
	        				}	
	        			}
	        		}
	        	}
	        	
	        	
	        	
	        });
		}
	}
	
	/**
	 * The toString method.
	 */
	@Override
	public String toString() {
		return help;
	}
	
	/**
	 * Returns true if the response input is equal (compared by command) to one 
	 * of the default commands in the responses string.
	 * @param toCheck
	 * @return true if the response is in the responses.
	 */
	public boolean hasResponse(Response toCheck) {
		int index = Collections.binarySearch(responses, toCheck);
		if (index > -1) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * Adding the terminator sequence. When the code in the terminator is run It
	 * will check to see first if the api/ bot was mentioned. Then it will look to
	 * see if the author of the message was the authorized terminator. Then it will
	 * check to see if the content was !terminate. If all those are true then
	 * executes the termination sequence which closes all active voice channels and
	 * ends the bot.
	 */
	private void addTerminatorCommand() {
		addResponse("!terminate", null, "terminates", true, true, (api, event) -> {
        	String input = event.getMessageContent().toLowerCase();
        	boolean terminateMessage = false;
        	boolean willListen = false;
        	if (input.contains("" + api.getYourself().getId())) {
        		terminateMessage = input.contains("!") && input.contains("terminate");
        	}
        	willListen = event.getMessageAuthor().getDiscriminatedName()
        			.equals(USERNAME_OF_AUTH_TERMINATOR);
        	
        	if (terminateMessage && willListen) {
            	event.getChannel().sendMessage("Terminating");
            	System.out.println("input: " + input );
            	System.out.println("User: " + event.getMessageAuthor().getDiscriminatedName());
            	System.out.println("User id: " + event.getMessageAuthor().getId());
            	System.out.println("QueenBot user id:" + api.getYourself().getId());
            	System.exit(0);
        	}
        	return null;
        	
        });
	}

}
