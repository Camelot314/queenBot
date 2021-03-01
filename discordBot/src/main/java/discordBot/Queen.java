package discordBot;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.ServerVoiceChannelBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;


/**
 * This is QueenBot
 * She can be found at:
 * https://discord.com/api/oauth2/authorize?client_id=807463236143546388&permissions=1111489648&scope=bot
 * 
 * QueenBot will end the program if the authorized terminator sends the kill 
 * code which is /@botname !terminate.
 * 
 * @author kjara
 *
 */
public class Queen {
    private static final String USERNAME_OF_AUTH_TERMINATOR = /* discord User id of authorized terminator */ "";
    private static final String TOKEN = /* Bot Token that discord gives you */ "";
	private static final String SAVED_DATA_FILE = "savedCustoms/savedData";
	private static final String HELP_COMMAND = "!help";
	private static final Random RANDOM = new Random();
	private String help;
	private DiscordApi api;
	private boolean hasActiveVoiceChannels, isRunning;
	private ArrayList<VoiceChannelServer> activeVoiceServers;
	
	private ArrayList<Response> responses;
	private ArrayList<Response> responseRelyContains;
	private ArrayList<ServerCustomCommands> customs;

	
	public Queen() {     
        
		help = "Hello I am QueenBot\n"
	              + "I have a list of commands or phrases u may want to use:\n"
	              + "!help" + Utilities.addSpaces("!help") + ": brings up my commandments";
		
		
        // commands list
        String[] commands =  {
        		 "!help", ".", "sorry",
                 "i'm sorry", "what", "how u",
                 "i see no problem", "feel", "i'm in",
                 "?", "i\u2014", "i--",
                 "stop", "*", "this is my 13th reason",
                 "my queen", "queenbot"
        };
        
        // responses list
        String[] responsesStr =  {
        		"", "PERIODT!", "As you should be",
                "As you should be", "It's a secret", "I am in pain",
                "Neither do I", "MOOD", "MOOD",
                "Yes", "\u2014I", "\u2014I",
                "No", null, "Do you want a 14th?",
                "Yes Peasant?", "Yes Peasant?"
        };
        
        /*
         * For the cases I do want the response to be random
         * I am specifying the other message that will be sent
         * as determined above. 
         */
        String[] responses2Str = {
        		"I refuse", null, null,
        		null, null, null,
        		null, null, null,
        		"No", null, null,
        		null, "Learn to type lol", null,
        		null, null
        };
        
        
        
        responsesStr[0] = help;
        
        /*
         * this specifies whether it will use the .contains() 
         * string method or the .equals string method
         * basically look throughout the message or just the
         * exact equality.
         */
        boolean[] contains = {
                false, true, false,
                false, false, false,
                false, true, true,
                true, true, true,
                false, true, false,
                true, true
        };
        
        /*
         * this is needed by the responses constructor.
         * Basically this is for cases where I wanted the response
         * given back to be random. The number represents the likelihood
         * it will occur (using Random.nextInt()). 5 means next int
         * will use 5 as the max and it will happen 1 in 5. 0 means always
         * happens. 
         */
        int[] odds = {
                5, 0, 0,
                0, 0, 0,
                0, 0, 0,
                2, 0, 0,
                0, 7, 0,
                0, 0
        };
        
        
        
        // creates the arraylists.
        responses = new ArrayList<>();
        activeVoiceServers = new ArrayList<>();
        responseRelyContains = new ArrayList<>();
        customs = new ArrayList<>();
        
        /*
         * Loops through all the arrays above and
         * creates the responses objects and adds it to the 
         * responses array lists.
         */
        for (int i = 0; i < commands.length; i ++) {
        	String displayMess = responsesStr[i];
        	boolean admin = false;
        	if (displayMess == null || displayMess.contains("null")) {
        		displayMess = responses2Str[i];
        	}
        	
        	if (i == 0) {
        		admin = true;
        	}
        	if (odds[i] != 0 && i != 0) {
        		Integer index = i;
        		String responseDef = responsesStr[i] == null ? "" : responsesStr[i] + " / ";
        		addResponse(commands[i], null, responseDef +  responses2Str[i],
        			contains[i], false, (api, event) -> {
        				if (RANDOM.nextInt(odds[index]) == 0) {
        					return responses2Str[index];
        				}
        				return responsesStr[index];
        			}
        		);
        		continue;
        	}
        	
        	addResponse(commands[i], responsesStr[i],
        			displayMess, contains[i], admin, null);
        }
        addResponse(HELP_COMMAND, help, help, false, false, null);
        addTerminatorCommand();
        
        /*
         * Adding a custom response that executes a bit of code (that makes
         * a voice channel server when allowed and specified).
         */
        addResponse("fight me", null, "*Special*", false, false, 
        		(api, event) -> {
        			
        	String input = event.getMessageContent().toLowerCase();
        	if (input.equals("fight me")) {
        		Server server = getServer(event);
    			if (server != null) {
    				if (server.canYouCreateChannels()) {
    					event.getChannel().sendMessage("Join the rig then");
        				addVoice(api, server.getId(), event.getChannel().getId());
    				} else {
    					event.getChannel().sendMessage("I am not allowed to");
    				}
    			}
        	}
			
        	return null;
        });
        
        addCustomCommands();
        addCustomHelperCommands();
		loadCustoms();
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

		if (!adminOnly && !command.equals(HELP_COMMAND)) {
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
	        	String helpAddition;
	        	String input;
        		DefaultResponse toFind;
        		Response response;
        		ServerCustomCommands custom = findCustom(event);
	        	boolean hasCustoms = custom != null, sent = false;
	        	
	        	long messageSender = event.getMessageAuthor().getId();
	        	boolean senderIsYou = messageSender == api.getYourself().getId();
	        	
	        	if (!senderIsYou) {
	        		
	        		helpAddition = custom == null ? "" : "\n" + custom.getCustomHelpAddition();
	        		input = event.getMessageContent().toLowerCase();
	        		toFind = new DefaultResponse(input);
	        		
	        		int index = Collections.binarySearch(responses, toFind);
	        		response = index > -1 ? responses.get(index) : null;
	        		
	        		if (response != null) {
						if (hasCustoms) {
							toSend = response.exec(api, event, helpAddition);
						} else {
							toSend = response.exec(api, event);
						}
						if (toSend != null && toSend.equals(help + helpAddition)) {
							new MessageBuilder().setEmbed(new EmbedBuilder().setTitle("Commandments")
									.setColor(Color.yellow).setDescription(help + helpAddition))
									.send(event.getChannel());
							sent = true;
						} else if (toSend != null) {
							event.getChannel().sendMessage(toSend);
							sent = true;
						} 
	        		} else {
	        			// where the binary search didn't work but there still
	        			// may be a chance that it is in rely contains.
	        			
	        			for (Response responseCont : responseRelyContains) {
	        				toSend = responseCont.exec(api, event, helpAddition);
	        				if (toSend != null) {
	        					event.getChannel().sendMessage(toSend);
	        					sent = true;
	        				}	
	        			}
	        			if (!sent && hasCustoms) {
	        				// was not in responses or rely contains. but is a
	        				// custom
	        				toSend = custom.returnCustomResponse(api, event);
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
            	if (hasActiveVoiceChannels) {
            		event.getChannel().sendMessage("Deleting Voice Channels");
            		deleteAllVoiceChannels();
            	}
            	if (customs.size() > 0) {
            		event.getChannel().sendMessage("Saving customs");
            		saveCustoms();
            	}
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
	
	/**
	 * Saved the customs to a file.
	 */
	private void saveCustoms() {
		
		try {
			FileOutputStream fileOutput = new FileOutputStream(SAVED_DATA_FILE);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOutput);
			objectOut.writeObject(customs);
			objectOut.flush();
			objectOut.close();
			fileOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Reads the customs file if there is one and then adds customs to the queen
	 * object accordingly.
	 */
	@SuppressWarnings("unchecked")
	private void loadCustoms() {
		
		try {
			FileInputStream fileIn = new FileInputStream(SAVED_DATA_FILE);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			customs = (ArrayList<ServerCustomCommands>) objectIn.readObject();
			objectIn.close();
			fileIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The method that adds the customs commands trigger. This method is called
	 * in the constructor. 
	 */
	private void addCustomCommands() {
		addResponse("!customs", null, "check custom commands", false, false, 
				(api, event) -> {
					if (event.getMessageContent().toLowerCase().equals("!customs")) {
						Server server = getServer(event);
						if (server != null) {
							String customCommands[] = {
									"!customs-on/off" + Utilities.addSpaces("!customs on/off") +
									": enables or disables customs",
									"!customs-clear" + Utilities.addSpaces("!customs clear") 
									+ ": clears all customs",
									"!customs-add" + Utilities.addSpaces("!customs add") + 
									": adds custom (input and output need to be separated by a \":\"",
									"!customs-remove" + Utilities.addSpaces("!customs remove") +
									": removes the specified custom by input"
							};
							
							String commandStr = customCommands[0];
							for (int i = 1; i < customCommands.length; i ++) {
								commandStr += "\n" + customCommands[i];
							}
							new MessageBuilder().setEmbed(new EmbedBuilder()
		        					.setTitle("Customs Settings")
		        					.setColor(Color.yellow)
		        					.setDescription(commandStr)
		        				).send(event.getChannel());
							
						} else {
							event.getChannel().sendMessage("No customs outside of servers");
						}
					}
					return null;	
		});
	}
	
	/**
	 * Adds all the necessary helper commands that are associated with customs. 
	 */
	private void addCustomHelperCommands() {
		addCustomsOn();
		addCustomsClear();
		addCustomsAdd();
		addCustomsRemove();
		
		
		
	}

	/**
	 * Adds the remove command. It first checks to see if customs are on in the
	 * chat. If true then it calls the ServerCustomCommands remove method with the
	 * string after the command call. If it successfully found the response to
	 * remove it will say removed. Otherwise it will say not found.
	 */
	private void addCustomsRemove() {
		Response remove = new DefaultResponse("!customs-remove", null, true, 
				(api, event) -> {
					
			String input = event.getMessageContent().toLowerCase();
			if (input.contains("!customs-remove")
					&& input.charAt(0) == '!') {
				
				ServerCustomCommands custom = findCustom(event);
				
				if (custom != null) {
					String[] args = Utilities.interpreter(event.getMessageContent(), 1, true);
					
					if (args == null) {
						event.getChannel().sendMessage(
								"I could not understand\n" +
								"Make sure your text reads the command then "
								+ "\"input of custom\""
							);
					} else {
						args[0] = args[0].toLowerCase();
						if (custom.remove(args[0])) {
							event.getChannel().sendMessage(
									"Removed the " +
									"\"" + args[0] + "\" trigger"
							);
						} else {
							event.getChannel().sendMessage("Could not find command");
						}
						
					}
					
					
				} else {
					event.getChannel().sendMessage("you have to enable customs first");
				}
			}
			
			return null;
		});
		responses.add(remove);
		responseRelyContains.add(remove);
	}



	/**
	 * This adds the customs add command. This will first check if the customs are
	 * on for the selected chat. If they are on then it will take the string after
	 * the command call and process it and then add a custom response to the
	 * ServerCustomCommands object associated with the server.
	 */
	private void addCustomsAdd() {
		Response add = new DefaultResponse("!customs-add", null, true, (api, event) -> {
			String input = event.getMessageContent().toLowerCase();
			if (input.contains("!customs-add")
					&& input.charAt(0) == '!') {

				ServerCustomCommands custom = findCustom(event);
				
				if (custom != null) {
					String[] args = Utilities.interpreter(event.getMessageContent(), 2, true);
					if (args == null) {
						event.getChannel().sendMessage(
								"I could not understand\n" +
								"Make sure your text reads the command then "
								+ "\"input : output\""
							);
					} else {
						args[0] = args[0].toLowerCase();
						boolean sendError = false;
						for (Response response : responseRelyContains) {
							if (args[0].contains(response.getCommand())) {
								sendError = true;
							}
						}
						if (sendError) {
							
							event.getChannel().sendMessage(
									"Sorry but I can't let"
									+ "you add this response it may already be used"
									+ " for reasons");
						} else if (custom.addResponse(args[0], args[1], this)) {
							event.getChannel().sendMessage(
									"\"" + args[0] + "\" triggers" 
									+ "\"" + args[1] + "\""
							);
						} else {
							event.getChannel().sendMessage(
									"Error :\\"
									+ " Already added or is already command"
									);
						}
						
					}
					
					
				} else {
					event.getChannel().sendMessage("you have to enable customs first");
				}
			}
			return null;
		});
		responses.add(add);
		responseRelyContains.add(add);
	}



	/**
	 * This will remove or clear all the customs on the server. First it checks if
	 * the current chat is on a server. (Will do nothing if not on a server.) It
	 * then checks to see if there is a customs object associated with the server in
	 * the arrayList customs. If both are true then it will check if the sender is
	 * the admin of the server. After all that it will then remove the
	 * ServerCustomCommands object from the customs arrayList.
	 */
	private void addCustomsClear() {
		responses.add(new DefaultResponse("!customs-off", null, false, (api, event) -> {
			return customsClearExecutable(event);
			}));
		responses.add(new DefaultResponse("!customs-clear", null, false, (api, event) -> {
			return customsClearExecutable(event);
		}));
	}



	/**
	 * This is the lambda expression used by both of the clear responses.
	 * @param event
	 * @return
	 */
	private String customsClearExecutable(MessageCreateEvent event) {
		String input = event.getMessageContent().toLowerCase();
		Server server = getServer(event);
		
		if (input.equals("!customs-off") || input.equals("!customs-clear")) {
			
			ServerCustomCommands custom = findCustom(event);
			if (custom != null) {
				if (event.getMessageAuthor().isServerAdmin()) {

					int index = Collections.binarySearch(customs, custom);
					if (index >= 0) {
						customs.remove(index);
						event.getChannel().sendMessage("Removed all customs");
					} else {
						event.getChannel().sendMessage("No customs to remove");
					}
					
					
				} else {
					event.getChannel().sendMessage(
							"You must be and admin to access customs duh"
					);
				}
			} else if (server != null) {
				event.getChannel().sendMessage("No customs to remove");
				
				
			} else {
				event.getChannel().sendMessage("I can only remove customs on servers dummy");
			}
		}
		return null;
	}
	
	/**
	 * This adds the customs on command. This command checks if the current chat is
	 * in a server (will not add if on). Then it checks if the given server already
	 * has a ServerCustomCommands object associated with it in the arrayList
	 * customs. (Will not add 2 customs for 1 server.) Then it checks to see if the
	 * message sender is the admin of the serer (will not allow anyone who is not
	 * admin from turning on customs.) Finally if it meets all the above criteria it
	 * creates a ServerCustomCommands object and puts in the server id.
	 */
	private void addCustomsOn() {
		responses.add(new DefaultResponse("!customs-on", null, false, (api, event) -> {
			String input = event.getMessageContent().toLowerCase();
			Server server = getServer(event);
			
			if (input.equals("!customs-on")) {
				
				if (findCustom(event) != null) {
					event.getChannel().sendMessage("Customs are already on");
				} else if (server != null) {
					long serverId = server.getId();
					
					if (event.getMessageAuthor().isServerAdmin()) {
						customs.add(new ServerCustomCommands(serverId));
						event.getChannel().sendMessage("Enabled them customs");
					} else {
						event.getChannel().sendMessage(
								"You must be and admin to access customs duh"
						);
					}
				} else {
					event.getChannel().sendMessage("I can only add customs on servers dummy");
				}
			}
			return null;
		}));
	}
	
	/**
	 * Given an event it will return a server reference if there is one.
	 * @param event
	 * @return reference to a server.
	 */
	private Server getServer(MessageCreateEvent event) {
		Server server;
		try {
			server = event.getServer().get();
		} catch (NoSuchElementException e) {
			server = null;
		}
		return server;
	}
	
	/**
	 * Takes in an event and checks to see if there is a ServerCustom command 
	 * associated with the server.
	 * @param event
	 * @return
	 */
	private ServerCustomCommands findCustom (MessageCreateEvent event) {
		Server server = getServer(event);
		int index = -1;
		if (server != null) {
			ServerCustomCommands key = new ServerCustomCommands(server.getId());
			index = Collections.binarySearch(customs, key);
			
		}
		if (index > - 1 ) {
			return customs.get(index);
		}
		return null;
		
	}
	
	/**
	 * This is the method used to create a new temporary voice channel. 
	 * It adds a VoiceChannelServer object to the arrayList once the channel
	 * is created so that QueenBot can keep track of all the open channels.
	 * It will then remove it from the list when the channel is being deleted.
	 * @param api
	 * @param serverId
	 * @param textChannelID
	 */
	private void addVoice(DiscordApi api, long serverId, long textChannelID) {

		Server server = api.getServerById(serverId).get();
		if (server != null) {
			// only makes if it is in a server
			TextChannel originalChannel = server.getTextChannelById(textChannelID).get();
			Collections.sort(activeVoiceServers);
			VoiceChannelServer toFind = new VoiceChannelServer(server.getId(), -1);
			int index = Collections.binarySearch(activeVoiceServers, toFind);
			if (index < 0) {
				// only adds if one has not been already added
				
		    	// Creating voice channel
				ServerVoiceChannel channel = new ServerVoiceChannelBuilder(server)
		    			.setName("The Ring")
		    			.create()
		    			.join();
				
				long voiceId = channel.getId();
				hasActiveVoiceChannels = true;
				VoiceChannelServer voiceServer = new VoiceChannelServer(server.getId(), voiceId);
				activeVoiceServers.add(voiceServer);
		    	
		    	// Delete the channel if the last user leaves
		    	channel.addServerVoiceChannelMemberLeaveListener(event -> {
		    	    if (event.getChannel().getConnectedUserIds().isEmpty()) {
		    	        
		    	    	event.getChannel().delete();
		    	        adjustActiveVoiceIds(voiceServer);
		    	        
						if (originalChannel != null) {
							originalChannel.sendMessage("Good Fight");
						}
		    	    }
		    	});

		    	// Delete the channel if no user joined in the first 30 seconds 
		    	api.getThreadPool().getScheduler().schedule(() -> {
		    	    if (channel.getConnectedUserIds().isEmpty()) {
		    	        
		    	    	channel.delete();
		    	    	adjustActiveVoiceIds(voiceServer);
		    	        
		    	        if (originalChannel != null) {
		    	        	originalChannel.sendMessage("cowards");
		    	        }
		    	    }
		    	}, 30, TimeUnit.SECONDS);
			}
			

		}		
    	
    }

	/**
	 * This does the removing to the voiceChannelServers arrayList.
	 * @param voiceObj
	 */
	private void adjustActiveVoiceIds(VoiceChannelServer voiceObj) {
		activeVoiceServers.remove(voiceObj);
		if (activeVoiceServers.size() == 0) {
			hasActiveVoiceChannels = false;
		} else {
			hasActiveVoiceChannels = true;
		}
	}
	
	/**
	 * This method is called when the terminate sequence is called
	 * This will remove any of the open temporary channels. 
	 */
	private void deleteAllVoiceChannels() {
		if (hasActiveVoiceChannels) {
			for (int i = 0; i < activeVoiceServers.size(); i ++) {
				VoiceChannelServer current = activeVoiceServers.get(i);
				
				serverMethod(current.getServerId(), (server) -> {
					ServerChannel channel;
					try {
						channel = server.getVoiceChannelById(
								current.getVoiceChannelId()
								).get();
					} catch (NoSuchElementException e) {
						channel = null;
					}
					if (channel != null) {
						channel.delete();
					}
							
				});
			}
		}
	}
	
	/**
	 * This is to some annoying code writing. The reason I am using
	 * server ids and then getting the server is because there is a 
	 * chance that the references or cache becomes out dated
	 * either from a server disconnect or just over time.
	 * By using the ids (which don't change) this shouldn't be a problem.
	 * @param serverId
	 * @param exec
	 */
	private void serverMethod(long serverId, ExecServer exec) {
		Server server = api.getServerById(serverId).get();
		if (server != null) {
			exec.executable(server);
		}
	}

}


interface ExecServer {
	public void executable(Server server);
}
