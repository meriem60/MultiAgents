package communication_RolePlaying_games;

import madkit.kernel.Madkit;

public class main {
    public static void main(String[] args) {
    	
	    	// Lancer 1 instance de `EmitterAgents` et 1 de `CounterAgents`
	        new Madkit("--launchAgents", EmitterAgents.class.getName() + ",true,2;",
	        		CounterAgents.class.getName() + ",true,2;",
	        		ControllerAgents.class.getName() + ",true,1;");	    
        //String[] launchOptions = {"--launchAgents", "ControllerAgent"};
        //Madkit.main(launchOptions);
    }
}
