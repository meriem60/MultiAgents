package communication_RolePlaying_games;

import madkit.kernel.Agent;
import madkit.message.IntegerMessage;

public class ControllerAgents extends Agent {

    @Override
    protected void activate() {
        // Créer et rejoindre le groupe
        createGroupIfAbsent("communication", "GroupTest");
        requestRole("communication", "GroupTest", "controller");
       
        
    }

    @Override
    protected void live() {
        while (true) {
            // Attendre un message d'un compteur
            IntegerMessage message = (IntegerMessage) waitNextMessage(5000);
            if (message != null) {
            	//int finalCount = message;
                System.out.println("Contrôleur a reçu le nombre de messages : " + message);

            } else {
                // No message was received within the wait time, so exit the loop
                System.out.println("No message received in the last " + (5000 / 1000) + " seconds. Ending...");
                break;
            }

            // Lancer un nouveau compteur avec le même nombre de messages
            CounterAgents newCounter = new CounterAgents();
            launchAgent(newCounter,true);
            System.out.println("Lancement d'un nouveau compteur.");
        }
    }
}

