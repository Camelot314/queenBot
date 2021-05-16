package discordBot;

import java.io.Serializable;
import java.util.HashMap;

/**
 * This is an object associated with a server that keeps track of all the 
 * custom responses.
 * @author Jaraad
 *
 */
public class ServerCustomCommands implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2453323497723493303L;
	private HashMap<String, SimpleResponse> responseMap;
	private long serverId;
	private String customHelpAddition;
	
	/**
	 * Constructor.
	 * @param serverId
	 * @param queen
	 */
	public ServerCustomCommands(long serverId) {		
		responseMap = new HashMap<>();
		customHelpAddition = "";
		this.serverId = serverId;
	}
	
	/**
	 * Will take in two strings and add a response to the list. It will return 
	 * true if both input and output are not null and response is added. Will not add
	 * any commands that are duplicates or default to queen. This method is thread
	 * safe.
	 * @param input
	 * @param output
	 * @return boolean true if added.
	 */
	public boolean addResponse(String input, String output, Queen queen) {
		if (input != null && output != null && queen != null) {
			boolean noAdd = false;
			noAdd = queen.hasResponse(input) || responseMap.containsKey(input);
			if (!noAdd) {
				SimpleResponse toAdd = new SimpleResponse(input, output);
				synchronized(this) {
					responseMap.put(input, toAdd);
					
					if (customHelpAddition.isEmpty()) {
						customHelpAddition += "\nCustoms";
					}
					customHelpAddition += "\n" + input + 
							Utilities.addSpaces(input) + ": " + output;
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This will return the custom help addition which is just the list of custom
	 * commands with their responses.
	 * @return
	 */
	public String getCustomHelpAddition() {
		return customHelpAddition;
	}
	
	/**
	 * Returns the serverId of the custom.
	 * @return
	 */
	public long getServerId() {
		return serverId;
	}
	
	/**
	 * Gets the number of custom commands in the object.
	 * @return
	 */
	public int getNumberCustoms() {
		return responseMap.size();
	}
	
	/**
	 * This is the remove command. It will remove a response corresponding to the
	 * given input command. It will also edit the help message string. 
	 * This will return true if the command was found and removed. Method is 
	 * thread safe.
	 * @param command
	 * @return boolean true if successful.
	 */
	public boolean remove(String command) {		
		
		
		if (responseMap.containsKey(command)) {
			synchronized (responseMap) {
				responseMap.remove(command);
			}
			String tempHelpAddition = customHelpAddition;
			int commandStart = tempHelpAddition.indexOf(command);
			
			
			if (commandStart > 1) {
				// because of the new line character
				tempHelpAddition = customHelpAddition.substring(0, commandStart - 1);
				
				// starts at character after \n
				String lineToRemove = customHelpAddition.substring(commandStart);
				int nextNewLine = lineToRemove.indexOf('\n');
				String remaining = "";
				if (nextNewLine >= 0 && (nextNewLine + commandStart + 1) 
						< customHelpAddition.length()) {
					remaining = customHelpAddition.substring(
							nextNewLine + commandStart + 1
					); 
				}
				
				tempHelpAddition += remaining;
				synchronized(this) {
					customHelpAddition = tempHelpAddition;
				}
				
			}
			return true;
		}
		return false;
	}
	
	/**
	 * This will loop through all the responses and return a string that is a response
	 * to the message if there is one. Otherwise returns null.
	 * @param api
	 * @param event
	 * @return String response if it finds one to return
	 */
	public String returnResponse(String command) {
		
		command = command.toLowerCase();
		if (responseMap.containsKey(command)) {
			return responseMap.get(command).getDefaultResponse();
		}
		return null;
	}
	
	/**
	 * Overrides the equals method. Objects are compared by the serveriD.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) 
			return true;
		if (!(obj instanceof ServerCustomCommands))
			return false;
		
		ServerCustomCommands other = (ServerCustomCommands) obj;
		return other.serverId == serverId;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return Long.hashCode(serverId);
	}
	
	@Override
	public String toString() {
		return "" + serverId + getCustomHelpAddition().substring(8);
	}


}
