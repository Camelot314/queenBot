package discordBot;

import java.util.Comparator;

/**
 * This is a comparator for the VoiceChannelServer that compares by 
 * The voiceChannelId.
 * @author Jaraad
 *
 */
public final class VoiceChSrvVoiceIdComp implements Comparator<VoiceChannelServer>{
	
	/**
	 * Ordering given by this comparator compares by the voiceChannelId numbers.
	 */
	@Override
	public int compare(VoiceChannelServer a, VoiceChannelServer b) {
		return Long.compare(a.getVoiceChannelId(), b.getVoiceChannelId());
	}
}
