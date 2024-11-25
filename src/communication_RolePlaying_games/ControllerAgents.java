package communication_RolePlaying_games;

import java.util.logging.Level;

import madkit.gui.AgentFrame;
import madkit.kernel.Agent;
import madkit.message.IntegerMessage;

public class ControllerAgents extends Agent {

    @Override
    protected void activate() {
        // Créer et rejoindre le groupe
		getLogger().setLevel(Level.FINEST);
        createGroupIfAbsent("communication", "GroupTest");
        requestRole("communication", "GroupTest", "controller");
		pause(500);

        
    }

    @Override
    protected void live() {
        while (true) {
            // Attendre un message d'un compteur
            IntegerMessage message = (IntegerMessage) waitNextMessage(2000);//xx
            if (message != null) {
            	//int finalCount = message;
                getLogger().info("Contrôleur a reçu le nombre de messages : " + message);

            } else {
                // No message was received within the wait time, so exit the loop
            	getLogger().info("No message received in the last " + (5000 / 1000) + " seconds. Ending...");
                break;
            }

            // Lancer un nouveau compteur avec le même nombre de messages
            CounterAgents newCounter = new CounterAgents();
            newCounter.setNumberMessages(message);
            launchAgent(newCounter,true);
            getLogger().info("Lancement d'un nouveau compteur.");
        }
    }
    
    public void setupFrame(AgentFrame Frame) {
    	super.setupFrame(Frame);
    	Frame.setLocation(300,320*(hashCode()-2));
    }
}

