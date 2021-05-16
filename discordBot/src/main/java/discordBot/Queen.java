package discordBot;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.ServerVoiceChannelBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.concurrent.TimeUnit;


/**
 * This is QueenBot
 * She can be found at:
 * https://discord.com/api/oauth2/authorize?client_id=807463236143546388&permissions=1111489648&scope=bot
 * 
 * QueenBot will end the program if the authorized terminator sends the kill 
 * code which is /@botname !terminate.
 * 
 * @author Jaraad
 *
 */
public class Queen {
	private static final String CREATOR = /* discord User id of authorized terminator */ "";
	private static final String TOKEN = /* Bot Token that discord gives you */ "";
	private static final String SAVED_DATA_FILE = /* directory where you want data saved */ "";
	private static final String HELP_COMMAND = "!help";
	private static final Random RANDOM = new Random();
	private static int instances = 0;
	private static Queen instance = null;
	
	private boolean hasActiveVoiceChannels, isRunning;
	private String help;
	private DiscordApi api;
	private HashMap<Long, ServerCustomCommands> customsMap;
	private HashMap<String, Response> responseMap;
	private HashMap<Long, VoiceChannelServer> activeVoiceMap;
	private HashSet<Response> responseContainsSet;

	/**
	 * private constructor to make sure there is only one instance of this class
	 */
	private Queen() {     
        
		help = "Hello I am QueenBot\n"
	              + "I have a list of commands or phrases u may want to use:\n"
	              + "!help" + Utilities.addSpaces("!help") + ": brings up my commandments";
        customsMap = new HashMap<>();
        activeVoiceMap = new HashMap<>();
        responseMap = new HashMap<>();
        responseContainsSet = new HashSet<>();
        
		
		
        setUpResponses();
	}

	
	
	public static Queen getInstance() {
		if (instances == 0) {
			instance = new Queen();
			instances = 1;
		}
		return instance;
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
	 * @param commandStr
	 * @return true if the command string is in the responses map.
	 */
	public boolean hasResponse(String commandStr) {
		return responseMap.containsKey(commandStr);
	}
	
	
	/**
	 * This is the add response method. It will take in all the arguments needed
	 * by the response constructor. Additionally it takes in a boolean adminOnly. 
	 * This specifies whether or not the command will be seen when the !help is
	 * called. This also takes in a lambda expression which is the code that
	 *  will be run when the response is called. 
	 * @param commandStr
	 * @param responseStr
	 * @param response2
	 * @param displayMes
	 * @param contains
	 * @param odd
	 * @param adminOnly
	 * @param lambda
	 * @throws IllegalArgumentException if command is null or both responses and
	 * lambda are null.
	 */
		
	public void addResponse(String commandStr, String responseStr, 
				String displayMes, boolean contains, boolean adminOnly, 
				Executable lambda) {
		
		if (commandStr == null) {
			throw new IllegalArgumentException("You must have an agrument for command");
		}
		
		if (responseStr == null && lambda == null) {
			throw new IllegalArgumentException(
					"If you have no responses then the executable must not be null"
			);
		}
		
		Response response = new DefaultResponse(commandStr, responseStr, contains, lambda);

		if (!adminOnly && !commandStr.equals(HELP_COMMAND)) {
			String helpMessage = DefaultResponse.getHelpStr();
			helpMessage = helpMessage == null ? "" : helpMessage;
			
			helpMessage += "\n" + commandStr +
					Utilities.addSpaces(commandStr) + ": " + displayMes;
			help = helpMessage;
			DefaultResponse.setHelpStr(helpMessage);
		}
		
		addRespToSet(response);
	}
	
	
	/*--------------------Methods Used to Set Up Instance---------------------*/

	/**
	 * sets up everything in the Queen object.
	 */
	private void setUpResponses() {
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
        
        /*
         * Loops through all the arrays above and
         * creates the responses objects and adds it to the 
         * responses array lists.
         */
        for (int i = 0; i < commands.length; i ++) {
        	String displayMess = responsesStr[i];
        	boolean admin = false;
        	
        	admin = i == 0 || (i > 0 && responsesStr[i] != null && 
        			responsesStr[i - 1] != null && responsesStr[i].equals(responsesStr[i - 1]));
        	
        	if (displayMess == null || displayMess.contains("null")) {
        		displayMess = responses2Str[i];
        		admin = true;
        	}
        	
        	if (odds[i] != 0 && i != 0) {
        		Integer index = i;
        		String responseDef = responsesStr[i] == null ? "" : responsesStr[i] + " / ";
        		addResponse(commands[i], null, responseDef +  responses2Str[i],
        			contains[i], admin, (api, event) -> {
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
        addCustomCommands();
        addCustomHelperCommands();
		loadCustoms();
		
		/**
		 * Adding a response that allows the creator to have the server save
		 * the current set of customs.
		 */
		addResponse("save", "saving", null, true, true, (api, event) -> {
			String message = event.getMessageContent();
			boolean performed = message.contains("" + api.getYourself().getId());
			performed = message.contains("save");
			performed = performed && event.getMessageAuthor().getDiscriminatedName().equals(CREATOR);
			
			if (performed) {
				saveCustoms();
			}

			
			return performed ? "saved" : null;
		});
		
		 /*
         * Adding a custom response that executes a bit of code (that makes
         * a voice channel server when allowed and specified).
         */
        addResponse("fight me", null, "*Special*", false, false, 
        		(api, event) -> {
        			
        	String input = event.getMessageContent().toLowerCase();
        	if (input.equals("fight me")) {
        		Server server = Utilities.getServer(event);
    			if (server != null) {
    				if (server.canYouCreateChannels()) {
    					event.getChannel().sendMessage("Join the ring then");
        				addVoice(api, server.getId(), event.getChannel().getId());
        				return null;
    				}
    				event.getChannel().sendMessage("I am not allowed to");
    			}
        	}
			
        	return null;
        });
	}
	
	/**
	 * Will add the specified response to the necessary set or map depending
	 * on the response. This is thread safe.
	 * @param response
	 */
	private void addRespToSet(Response response) {
		if (response.isContains()) {
			synchronized(responseContainsSet) {
				responseContainsSet.add(response);
			}
		}
		synchronized(responseMap) {
			responseMap.put(response.getCommand(), response);
		}
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
        	return terminatorExec(api, event);
        	
        });
	}

	/**
	 * Code that is run when the terminator command is called. Contains a lot of
	 * write operations. Clearing the activeVoce hashSet and the the customs 
	 * HashMap. Should be thread safe. 
	 * @param api
	 * @param event
	 * @return a null String reference. 
	 */
	private String terminatorExec(DiscordApi api, MessageCreateEvent event) {
		String input = event.getMessageContent().toLowerCase();
		boolean terminateMessage = false;
		boolean willListen = false;
		
		if (input.contains("" + api.getYourself().getId())) {
			terminateMessage = input.contains("!") && input.contains("terminate");
		}
		willListen = event.getMessageAuthor().getDiscriminatedName()
				.equals(CREATOR);
		
		if (terminateMessage && willListen) {
			if (hasActiveVoiceChannels) {
				event.getChannel().sendMessage("Deleting Voice Channels");
				deleteAllVoiceChannels();
			}
			if (customsMap.size() > 0) {
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
	}
	
	/**
	 * The method that adds the customs commands trigger. This method is called
	 * in the constructor. 
	 */
	private void addCustomCommands() {
		addResponse("!customs", null, "check custom commands", false, false, 
				(api, event) -> {
					return Utilities.customsExec(event);	
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
					
			return customsRemoveExec(event);
		});
		addRespToSet(remove);
	}

	/**
	 * Action performed when the customs remove is called by user.
	 * @param event
	 * @return a null string reference.
	 */
	private String customsRemoveExec(MessageCreateEvent event) {
		String input = event.getMessageContent().toLowerCase();
		if (input.contains("!customs-remove")
				&& input.charAt(0) == '!') {
			
			ServerCustomCommands custom = findCustom(event);
			if (custom != null) {
				String[] args = Utilities.interpreter(event.getMessageContent(), 1, true);
				if (args == null) {
					Utilities.sendErrorRemoveMessage(event);
					return null;
				}
				args[0] = args[0].toLowerCase();
				String confirmationMess = custom.remove(args[0]) ? 
						"Removed the " + "\"" + args[0] + "\" trigger" 
						: "Could not find command";
				event.getChannel().sendMessage(confirmationMess);
				return null;
			} 
			event.getChannel().sendMessage("you have to enable customs first");
		}
		return null;
	}

	/**
	 * This adds the customs add command. This will first check if the customs are
	 * on for the selected chat. If they are on then it will take the string after
	 * the command call and process it and then add a custom response to the
	 * ServerCustomCommands object associated with the server.
	 */
	private void addCustomsAdd() {
		Response customsAdd = new DefaultResponse("!customs-add", null, true, (api, event) -> {
			String input = event.getMessageContent().toLowerCase();
			if (input.contains("!customs-add") && input.charAt(0) == '!') {

				ServerCustomCommands custom = findCustom(event);
				if (custom != null) {
					String[] args = Utilities.interpreter(event.getMessageContent(), 2, true);
					if (args == null) {
						Utilities.sendErrorAddMessage(event, false);
					} else {
						args[0] = args[0].toLowerCase();
						boolean sendError = false;
						
						for (Response response : responseContainsSet) {
							if (args[0].contains(response.getCommand())) {
								sendError = true;
							}
						}
						if (sendError) {
							Utilities.sendErrorAddMessage(event,true);
							return null;
						}
						
						if (custom.addResponse(args[0], args[1], this)) {
							Utilities.sendAddConfirmation(event, args);
						} else {
							Utilities.sendErrorAddMessage(event,true);
						}
					}
					
					return null;
				} 
				event.getChannel().sendMessage("you have to enable customs first");
			}
			return null;
		});
		addRespToSet(customsAdd);
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
		Response customsOff, customsClear;
		customsOff = new DefaultResponse("!customs-off", null, false, (api, event) -> {
			return customsClearExecutable(event);
		});
		
		customsClear = new DefaultResponse("!customs-clear", null, false, (api, event) -> {
			return customsClearExecutable(event);
		});
		addRespToSet(customsClear);
		addRespToSet(customsOff);
	}



	/**
	 * This is the lambda expression used by both of the clear responses.
	 * @param event
	 * @return
	 */
	private String customsClearExecutable(MessageCreateEvent event) {
		String input = event.getMessageContent().toLowerCase();
		Server server = Utilities.getServer(event);
		
		if (input.equals("!customs-off") || input.equals("!customs-clear")) {
			
			ServerCustomCommands customObj = findCustom(event);
			if (customObj != null) {
				long serverId = server.getId();
				
				if (event.getMessageAuthor().isServerAdmin()) {
					customsMap.remove(serverId);
					event.getChannel().sendMessage("Removed all customs");
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
	 * has a ServerCustomCommands object associated with it in the HashMap of
	 * customs. (Will not add 2 customs for 1 server.) Then it checks to see if the
	 * message sender is the admin of the server (will not allow anyone who is not
	 * admin from turning on customs). Finally if it meets all the above criteria it
	 * creates a ServerCustomCommands object and puts in the server id. Thread Safe.
	 */
	private void addCustomsOn() {
		Response customsOn = new DefaultResponse("!customs-on", null, false, (api, event) -> {
			String input = event.getMessageContent().toLowerCase();
			Server server = Utilities.getServer(event);
			
			if (input.equals("!customs-on")) {
				if (findCustom(event) != null) {
					event.getChannel().sendMessage("Customs are already on");
				} else if (server != null) {
					long serverId = server.getId();
					if (event.getMessageAuthor().isServerAdmin()) {
						
						synchronized(customsMap) {
							customsMap.put(serverId, new ServerCustomCommands(serverId));
						}
						
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
		});
		addRespToSet(customsOn);
	}
	
	
	/**
	 * Reads the customs file if there is one and then adds customs to the queen
	 * object accordingly. This entire method is synchronized to be thread safe.
	 */
	@SuppressWarnings("unchecked")
	private synchronized void loadCustoms() {
		File file = new File(SAVED_DATA_FILE);
		if (!file.exists()) {
			return;
		}
		try {
			FileInputStream fileIn = new FileInputStream(file);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			customsMap = (HashMap<Long, ServerCustomCommands>) objectIn.readObject();
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
	
	
	/*--------------------Methods Used when bot is running--------------------*/
	
	/**
	 * Runs the api if there is not already 1 instance running.
	 */
	public void run () {
		if (!isRunning && TOKEN != null) {
			isRunning = true;			
			api = new DiscordApiBuilder().setToken(TOKEN).login().join();
			api.addMessageCreateListener(event -> {
        		ServerCustomCommands custom = findCustom(event);
	        	boolean hasCustoms = custom != null;
	        	
	        	long messageSender = event.getMessageAuthor().getId();
	        	boolean senderIsYou = messageSender == api.getYourself().getId();
	        	
	        	if (!senderIsYou) {
	        		findAndSend(event, custom, hasCustoms);
	        	}
	        	
	        	
	        	
	        });
		}
	}

	/**
	 * Finds and sends the appropriate message based on the event and any possible
	 * customs that are found in the run thread. Read only operations: Thread safe.
	 * @param event
	 * @param custom
	 * @param hasCustoms
	 * @param sent
	 */
	private void findAndSend(MessageCreateEvent event, ServerCustomCommands custom, 
			boolean hasCustoms) {
		
		String toSend, helpAddition, input;
		boolean sent = false;
		
		helpAddition = custom == null ? null : "\n" + custom.getCustomHelpAddition();
		input = event.getMessageContent().toLowerCase();
		
		/*
		 * The first thing that it does it check if the message is from a server
		 * with customs. It will then see if the input corresponds to a server
		 * custom. If it does then it will send that message and set sent to true.
		 * This will prevent any further messages from sending. 
		 */
		
		if (hasCustoms) {
			toSend = custom.returnResponse(input);
			if (toSend != null) {
				Utilities.sendMessage(event, toSend);
				sent = true;
			}
		}
		
		/*
		 * If the Bot has not already sent a message (meaning either no customs
		 * or it is a non custom message) it will retrieve the appropriate response
		 * from the HashMap if it is present. 
		 */
		if (!sent && responseMap.containsKey(input)) {
			sendResponse(event, helpAddition, responseMap.get(input));
			sent = true;
		}
		
		
		
		
		/*
		 * where the HashMap didn't have exact match but there still
		 * may be a chance that it is in rely contains.
		 */
		if (!sent) {
			for (Response responseCont : responseContainsSet) {
				toSend = responseCont.exec(api, event, helpAddition);
				sent = Utilities.foundResponseCont(event, toSend);
			}
		}
		
	}

	/**
	 * If the binary search for responses provides a valid value then it will 
	 * perform this method. This method executes the lambda expression if there
	 * is one and will send the appropriate messages. Read only operations: Thread safe.
	 * @param event
	 * @param helpAddition
	 * @param response
	 */
	private void sendResponse(MessageCreateEvent event, String helpAddition, Response response) {
		String toSend;
		toSend = response.exec(api, event, helpAddition);
		if (toSend != null && event.getMessageContent().equals("!help")) {
			sendHelpMessage(event, helpAddition);
			return;
		}
		Utilities.sendMessage(event, toSend);
	}

	/**
	 * Creates and sends a fancy embedded help message. This is thread safe.
	 * @param event
	 * @param helpAddition
	 */
	private void sendHelpMessage(MessageCreateEvent event, String helpAddition) {
		
		helpAddition = helpAddition == null ? "" : helpAddition;
		
		new MessageBuilder().setEmbed(new EmbedBuilder().setTitle("Commandments")
				.setColor(Color.yellow).setDescription(help + helpAddition))
				.send(event.getChannel());
	}
	
	
	
	
	/**
	 * Saved the customs to a file. This entire method is synchronized.
	 */
	private synchronized void saveCustoms() {
		
		try {
			FileOutputStream fileOutput = new FileOutputStream(SAVED_DATA_FILE);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOutput);
			objectOut.writeObject(customsMap);
			objectOut.flush();
			objectOut.close();
			fileOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}	
	
	/**
	 * Takes in an event and checks to see if there is a ServerCustomCommand object
	 * associated with the server.
	 * @param event
	 * @return ServerCustomCommand object if there is one associated. Null otherwise
	 */
	private ServerCustomCommands findCustom (MessageCreateEvent event) {
		Server server = Utilities.getServer(event);
		if (server != null) {			
			long serverId = server.getId();
			if (customsMap.containsKey(serverId)) {
				return customsMap.get(serverId);
			}
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
		
		// only makes if it is in a server
		if (server != null) {
			TextChannel originalChannel = server.getTextChannelById(textChannelID).get();
			
			// does nothing if there is already an active voice channel
			if (activeVoiceMap.containsKey(serverId)) {
				return;
			}
			
			// Creating voice channel
			ServerVoiceChannel channel = new ServerVoiceChannelBuilder(server)
	    			.setName("The Ring")
	    			.create()
	    			.join();
			
			long voiceId = channel.getId();
			synchronized(this) {
				hasActiveVoiceChannels = true;
			}
			
			VoiceChannelServer voiceServer = new VoiceChannelServer(server.getId(), voiceId);
			
			synchronized(activeVoiceMap) {
				activeVoiceMap.put(serverId, voiceServer);
				
			}
			
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

	/**
	 * This is called when a voice channel auto deletes. This method removes the
	 * voiceChannelServer object from the activeVoceMap. This method is thread
	 * safe.
	 * @param voiceObj
	 */
	private void adjustActiveVoiceIds(VoiceChannelServer voiceObj) {
		long serverId = voiceObj.getServerId();
		synchronized(activeVoiceMap) {
			activeVoiceMap.remove(serverId, voiceObj);
		}
		
		synchronized(this) {
			hasActiveVoiceChannels = activeVoiceMap.isEmpty();
		}
	}
	
	/**
	 * This method is called when the terminate sequence is called
	 * This will remove any of the open temporary channels. This entire method
	 * is synchronized.
	 */
	private synchronized void deleteAllVoiceChannels() {
		if (hasActiveVoiceChannels) {
			for (Map.Entry<Long, VoiceChannelServer> entry : activeVoiceMap.entrySet()) {
				VoiceChannelServer current = entry.getValue();
				
				serverMethod(current.getServerId(), (server) -> {
					server.getVoiceChannelById(current.getVoiceChannelId()).ifPresent(channel -> {
						synchronized(server) {
							channel.delete();
						}
					});
				});
			}
		}
		hasActiveVoiceChannels = false;
		activeVoiceMap.clear();
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
	
	/**
	 *  Private interface that allows for the use of a lambda expression.
	 * @author Jaraad
	 *
	 */
	@FunctionalInterface
	private interface ExecServer {
		public void executable(Server server);
	}

}
