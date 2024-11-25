package communication_RolePlaying_games;

import static communication.ex01.Society.COMMUNITY;
import static communication.ex01.Society.GROUP;
import static communication.ex01.Society.ROLE;

import java.util.Random;
import java.util.logging.Level;


import madkit.gui.AgentFrame;
import madkit.kernel.AgentAddress;
import madkit.kernel.Madkit;
import madkit.kernel.Message;
import madkit.kernel.Agent;

public class EmitterAgents extends Agent{
	private Random random = new Random();
	
	protected void activate() {
		getLogger().setLevel(Level.FINEST);
		createGroupIfAbsent("communication", "GroupTest");// Create the group GroupTest in the community communication.
		requestRole("communication", "GroupTest", "Emetteur");
		pause(500);
	    }
	    @Override
	    protected void live() {
	    // Random number of messages to send
	    int messageCount = random.nextInt(10) + 1;
		AgentAddress other = null;
		// Until we have an agent (in the group GroupTest in the community communication) having the role RoleTest2.


		//pause(1000);
		// Envoyer un certain nombre de messages à l'agent compteur trouvé
        for (int i = 0; i < messageCount; i++) {
            pause(random.nextInt(2000)); // Pause aléatoire avant chaque envoi
            
            int trialInt = 0;
    		while (other == null && trialInt<60 ) {
    		    other = getAgentWithRole("communication", "GroupTest", "counter");
    		    pause(100);
    		    trialInt++;
    		}
    		getLogger().info("\n\tJ'ai trouvé un agent compteur !!\n" + other + "\n\n");
            // Envoi d'un message à l'agent compteur
            sendMessage(other, new Message());
            getLogger().info("Message envoyé à l'agent compteur."+other);
        }
        //waiting for a response
		//while (nextMessage() != null)
		   // pause(6000);
	    
	    
	    }

	    /*
	     * Set where the agent's window will be for avoiding a clear presentation. This method give
		 * the opportunity to modify the default settings of the frame.
	     * It will be more explained in GUI tutorial.
	     */
	    @Override
	    public void setupFrame(AgentFrame frame) {
		super.setupFrame(frame);
		frame.setLocation(100, 320 * (hashCode() - 2));
	    }

	    /**
	     * @param argss
	     *            Running Agent1 will launch 2 instances of both Agent1 and Agent2. They will send themselves 1 message.
	     */
	   
	    

}
