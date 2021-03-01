package discordBot;

/**
 * This is the driver.
 * You can add new commands to the QueenBot
 * using the Queen.addResponse() method.
 * After all the adding is done execute the
 * Queen.run() method. 
 * @author kjara
 *
 */
public class Driver {
	public static void main(String[] args) {
		Queen queenBot = new Queen();
				
		queenBot.run();
		
	}
}