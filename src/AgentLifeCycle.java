import madkit.kernel.Agent;

public class AgentLifeCycle extends Agent {

    @Override
    protected void activate() {
       getLogger().info("\tHello World!!\n\n\tI am activating...");
       pause(2000);
    }

    @Override
    protected void live() {
       int nb = 10;
       while (nb-- > 0) {
           getLogger().info("Living... I will quit in " + nb + " seconds");
           pause(1000);
       }
    }

    @Override
    protected void end() {
       getLogger().info("Bye bye !");
       pause(2000);
    }

    public static void main(String[] args) {
       executeThisAgent();
    }
}
