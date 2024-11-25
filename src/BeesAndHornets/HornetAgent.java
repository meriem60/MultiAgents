package BeesAndHornets;

import static madkit.bees.BeeLauncher.BEE_ROLE;
import static madkit.bees.BeeLauncher.COMMUNITY;
import static madkit.bees.BeeLauncher.QUEEN_ROLE;
import static madkit.bees.BeeLauncher.SIMU_GROUP;
import static BeesAndHornets.BeeLauncher.HORNET_ROLE;

import java.awt.Point;
import java.util.List;
import java.util.logging.Level;

import madkit.kernel.AgentAddress;
import madkit.kernel.Message;
import madkit.message.ObjectMessage;

public class HornetAgent extends AbstractBee {

    private static final long serialVersionUID = 1L;

    private BeeInformation leaderInfo = null;
    private AgentAddress leader = null;

    // Constants for attack and surrounding range
    private static final int ATTACK_RANGE = 50; // Distance to attack
    private static final int SURROUND_RANGE = 30; // Distance to consider surrounded
    private long killStartTime = 0; // Time when attack starts
    private boolean isSurrounded = false; // Track if surrounded

    // Constructor
    public HornetAgent() {
        this.myInformation = new BeeInformation("hornet");
    }

    @Override
    public void activate() {
        requestRole(COMMUNITY, SIMU_GROUP, HORNET_ROLE, null); // Request the hornet role in the simulation
    }

    @Override
    protected void computeNewVelocities() {
        Point location = myInformation.getCurrentPosition();
        AbstractBee targetBee = findNearbyBee(location, ATTACK_RANGE);

        if (targetBee != null && !isSurrounded) {
            // Move towards the bee
            int dx = targetBee.myInformation.getCurrentPosition().x - location.x;
            int dy = targetBee.myInformation.getCurrentPosition().y - location.y;
            dX += dx / 10; // Slow movement towards the target
            dY += dy / 10;

            // Start attack if within range
            if (Math.abs(dx) + Math.abs(dy) < 10) {
                if (killStartTime == 0) {
                    killStartTime = System.currentTimeMillis();
                }
                if (System.currentTimeMillis() - killStartTime > 3000) { // Kill after 3 seconds
                    beeWorld.removeBee(targetBee); // Remove the bee after the attack
                    killStartTime = 0; // Reset attack timer
                }
            }
        } else {
            killStartTime = 0; // Reset if no target is found
        }

        // Check if surrounded by more than 4 bees
        isSurrounded = beeWorld.getNearbyBees(location, SURROUND_RANGE).size() > 4;
        if (isSurrounded) {
            killStartTime = 0; // Can't attack if surrounded
            // If surrounded by more than 7 bees, the hornet gets removed
            if (beeWorld.getNearbyBees(location, SURROUND_RANGE).size() > 7) {
                beeWorld.removeHornet(this); // Remove hornet after being surrounded for a while
            }
        }
    }

    // Helper method to find the closest bee within a certain range
    private AbstractBee findNearbyBee(Point location, int range) {
        return beeWorld.getNearbyBees(location, range).stream().findFirst().orElse(null);
    }

    @Override
    protected int getMaxVelocity() {
        if (beeWorld != null) {
            return beeWorld.getHornetVelocity().getValue();
        }
        return 0;
    }
    

}
