package BeesAndHornets;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultBoundedRangeModel;

/**
 * Class representing the size of the environment and some modeling parameters.
 * 
 * @version 2.0.0.3
 * @author Fabien Michel
 */
public class BeeEnvironment {

    private Dimension envSize;

    private final DefaultBoundedRangeModel queenAcceleration = new DefaultBoundedRangeModel(5, 1, 0, 21);
    private final DefaultBoundedRangeModel beeAcceleration = new DefaultBoundedRangeModel(3, 1, 0, 21);
    private final DefaultBoundedRangeModel hornetAcceleration = new DefaultBoundedRangeModel(3, 1, 0, 21);
    private final DefaultBoundedRangeModel hornetVelocity = new DefaultBoundedRangeModel(12, 1, 0, 21);
    private final DefaultBoundedRangeModel queenVelocity = new DefaultBoundedRangeModel(12, 1, 0, 21);
    private final DefaultBoundedRangeModel beeVelocity = new DefaultBoundedRangeModel(9, 1, 0, 21);

    private final List<Bee> bees = new ArrayList<>();
    private final List<HornetAgent> hornets = new ArrayList<>();

 // Acceleration and velocity getters
    public DefaultBoundedRangeModel getQueenAcceleration() {
        return queenAcceleration;
    }

    public DefaultBoundedRangeModel getBeeAcceleration() {
        return beeAcceleration;
    }

    public DefaultBoundedRangeModel getQueenVelocity() {
        return queenVelocity;
    }

    public DefaultBoundedRangeModel getBeeVelocity() {
        return beeVelocity;
    }
    
    public DefaultBoundedRangeModel getHornetVelocity() {
        return hornetVelocity;
    }
    
    public DefaultBoundedRangeModel getHornetAcceleration() {
        return hornetAcceleration;
    }

    

    public int getWidth() {
    	return envSize.width;
        }

        public int getHeight() {
    	return envSize.height;
        }

        public BeeEnvironment() {
    	this(new Dimension());
        }

        /**
         * @param envSize
         */
        public BeeEnvironment(Dimension envSize) {
    	this.envSize = envSize;
        }

        /**
         * @param envSize
         *            the envSize to set
         */
        public final void setEnvSize(Dimension envSize) {
    	this.envSize = envSize;
        }
    
    // Bee and Hornet management
    public void addBee(Bee bee) {
        bees.add(bee);
    }

    public void addHornet(HornetAgent hornet) {
        hornets.add(hornet);
    }

    public List<Bee> getAllBees() {
        return new ArrayList<>(bees);
    }

    public List<HornetAgent> getAllHornets() {
        return new ArrayList<>(hornets);
    }

    public void removeBee(AbstractBee bee) {
        bees.remove(bee);
    }

    public void removeHornet(HornetAgent hornet) {
        hornets.remove(hornet);
    }

    // Get all bees within a given range of a position
    public List<Bee> getNearbyBees(Point position, double range) {
        List<Bee> nearbyBees = new ArrayList<>();
        for (Bee bee : bees) {
            // Avoid a null pointer exception if the bee's information is not initialized
            if (bee.myInformation != null && position.distance(bee.myInformation.getCurrentPosition()) <= range) {
                nearbyBees.add(bee);
            }
        }
        return nearbyBees;
    }

    // Get all hornets within a given range of a position
    public List<HornetAgent> getNearbyHornets(Point position, double range) {
        List<HornetAgent> nearbyHornets = new ArrayList<>();
        for (HornetAgent hornet : hornets) {
            // Avoid a null pointer exception if the hornet's information is not initialized
            if (hornet.myInformation != null && position.distance(hornet.myInformation.getCurrentPosition()) <= range) {
                nearbyHornets.add(hornet);
            }
        }
        return nearbyHornets;
    }
}
