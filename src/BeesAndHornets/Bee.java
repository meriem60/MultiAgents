package BeesAndHornets;

import static madkit.bees.BeeLauncher.COMMUNITY;
import static madkit.bees.BeeLauncher.QUEEN_ROLE;
import static madkit.bees.BeeLauncher.SIMU_GROUP;

import java.awt.Point;
import java.util.List;

import madkit.kernel.AgentAddress;
import madkit.message.ObjectMessage;

/**
 * @version 2.3
 * @author Fabien Michel, Olivier Gutknecht
 */
public class Bee extends AbstractBee {

    private static final long serialVersionUID = -2393301912353816186L;
    BeeInformation leaderInfo = null;
    AgentAddress leader = null;
    
    public Bee() {
        this.myInformation = new BeeInformation("bee");
    }

    @Override
    public void activate() {
        // Request role as bee and follower
        requestRole("buzz", SIMU_GROUP, "bee", null);
        requestRole("buzz", SIMU_GROUP, "follower", null);
    }

    /** The "do it" method called by the activator */
    @Override
    public void buzz() {
        updateLeader();
        super.buzz();
    }

    private void updateLeader() {
        // Get the next message from the bee's message queue
        ObjectMessage<BeeInformation> m = (ObjectMessage<BeeInformation>) nextMessage();
        if (m == null) {
            return;
        }

        // Handle leader update
        if (m.getSender().equals(leader)) {
            // If the leader quits, reset the leader information
            leader = null;
            leaderInfo = null;
        } else {
            // Otherwise, follow the new leader if no leader is set or change leader randomly
            if (leader == null) {
                followNewLeader(m);
            } else {
                List<AgentAddress> queens = getAgentsWithRole(COMMUNITY, SIMU_GROUP, QUEEN_ROLE);
                if (queens != null && generator.nextDouble() < (1.0 / queens.size())) {
                    followNewLeader(m);
                }
            }
        }
    }

    private void followNewLeader(ObjectMessage<BeeInformation> leaderMessage) {
        leader = leaderMessage.getSender();
        leaderInfo = leaderMessage.getContent();
        myInformation.setBeeColor(leaderInfo.getBeeColor()); // Update the bee's color to match the leader's
    }

    @Override
    protected void computeNewVelocities() {
        Point location = myInformation.getCurrentPosition();
        
        int dtx;
    	int dty;

        // Step 1: Check for nearby hornets
        List<HornetAgent> nearbyHornets = beeWorld.getNearbyHornets(location, 100); // Detect hornets within 100 units
        if (!nearbyHornets.isEmpty()) {
            // Defensive behavior if hornets are nearby
            HornetAgent targetHornet = nearbyHornets.get(0); // Focus on the first detected hornet
            Point hornetLocation = targetHornet.myInformation.getCurrentPosition();

            // Move towards the hornet to "surround" or engage
            int dx = hornetLocation.x - location.x;
            int dy = hornetLocation.y - location.y;
            dX += dx / 10; // Slow, deliberate movement
            dY += dy / 10;
            return; // Skip following the queen if defending
        }

        // Step 2: Follow the queen if no hornets are nearby
        if (leaderInfo != null) {
    	    final Point leaderLocation = leaderInfo.getCurrentPosition();
    	    dtx = leaderLocation.x - location.x;
    	    dty = leaderLocation.y - location.y;
    	}
    	else {
    	    dtx = generator.nextInt(5);
    	    dty = generator.nextInt(5);
    	    if (generator.nextBoolean()) {
    		dtx = -dtx;
    		dty = -dty;
    	    }
    	}
    	int acc = 0;
    	if (beeWorld != null) {
    	    acc = beeWorld.getBeeAcceleration().getValue();
    	}
    	int dist = Math.abs(dtx) + Math.abs(dty);
    	if (dist == 0)
    	    dist = 1; // avoid dividing by zero
    	// the randomFromRange adds some extra jitter to prevent the bees from flying in formation
    	dX += ((dtx * acc) / dist) + randomFromRange(2);
    	dY += ((dty * acc) / dist) + randomFromRange(2);
        }
    

    @Override
    protected int getMaxVelocity() {
        if (beeWorld != null) {
            return beeWorld.getBeeVelocity().getValue();
        }
        return 0;
    }
    
}
