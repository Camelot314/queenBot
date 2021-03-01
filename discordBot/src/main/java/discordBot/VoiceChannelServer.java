package discordBot;

import java.util.Comparator;

/**
 * This is a final class that basically
 * just houses the id for the server and voice channel
 * that it is provided.
 * @author kjara
 *
 */
public final class VoiceChannelServer implements Comparable<VoiceChannelServer>{
	private long serverId, voiceChannelId;
	public static final Comparator<VoiceChannelServer> VOICE_CHANNEL_ORDER 
												= new VoiceChSrvVoiceIdComp();
	
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
	 * Compares objects by the ServerId. There is a comparator that sorts by
	 * voiceChannelId.
	 */
	@Override
	public int compareTo(VoiceChannelServer other) {
		return Long.compare(serverId, other.serverId);
	}

}
