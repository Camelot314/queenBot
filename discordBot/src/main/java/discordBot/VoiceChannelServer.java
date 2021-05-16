package discordBot;

/**
 * This is a final class that basically
 * just houses the id for the server and voice channel
 * that it is provided.
 * @author Jaraad
 *
 */
public final class VoiceChannelServer {
	private long serverId, voiceChannelId;
	
	/**
	 * Constructor
	 * @param serverId
	 * @param voiceChannelId
	 */
	public VoiceChannelServer(long serverId, long voiceChannelId) {
		this.serverId = serverId;
		this.voiceChannelId = voiceChannelId;
	}

	/**
	 * Returns the server id
	 * @return long
	 */
	public long getServerId() {
		return serverId;
	}

	/**
	 * returns the voice channel id
	 * @return long.
	 */
	public long getVoiceChannelId() {
		return voiceChannelId;
	}
	
	/**
	 * overrides the equals method. Objects are compared by the serverId and 
	 * voiceChannelId.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) 
			return true;
		if (!(obj instanceof VoiceChannelServer)) 
			return false;
		VoiceChannelServer other = (VoiceChannelServer) obj;
		
		return other.serverId == serverId || other.voiceChannelId == voiceChannelId;
		
	}
	
	/**
	 * Hash code so it can be used in hashing.
	 */
	@Override
	public int hashCode() {
		return Long.hashCode(serverId);
	}

}
