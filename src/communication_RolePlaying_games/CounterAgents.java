package communication_RolePlaying_games;

import java.util.Random;
import java.util.logging.Level;

import madkit.kernel.Agent;
import madkit.kernel.Madkit;
import madkit.kernel.Message;
import madkit.message.IntegerMessage;

public class CounterAgents  extends Agent{
	private int messageCount = 0;
    private Random random = new Random();
    
    protected void activate(){
		getLogger().setLevel(Level.FINEST);

		createGroup("communication", "GroupTest");// Create the group GroupTest in the community communication.
		requestRole("communication", "GroupTest", "counter");
		pause(500);
	 }
    
    protected void live() {
        while (true) {
        	
            // Attendre un message d'un émetteur
            Message message =  waitNextMessage(5000);
            if (message != null) {
                // A message was received within the wait time
                messageCount++;
                System.out.println("Counter agent received a message. Total messages: " + messageCount);
            } else {
                // No message was received within the wait time, so exit the loop
                System.out.println("No message received in the last " + (5000 / 1000) + " seconds. Ending...");
                break;
            }

            
            // Décider aléatoirement quand se transformer en émetteur
            if (random.nextInt(10) > 7) {
                pause(random.nextInt(2000)); // Pause avant la transformation
                // Informer le contrôleur
                sendMessage("communication", "GroupTest", "controller", new IntegerMessage(messageCount));
                break;
            }
        }
        // Se transformer en émetteur
        launchAgent(new EmitterAgents(),true);
    }
    
   
    
}
