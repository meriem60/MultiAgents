package BeesAndHornets;

import static madkit.bees.BeeLauncher.BEE_ROLE;
import static madkit.bees.BeeLauncher.COMMUNITY;
import static madkit.bees.BeeLauncher.SCHEDULER_ROLE;
import static madkit.bees.BeeLauncher.SIMU_GROUP;

import madkit.action.SchedulingAction;
import madkit.kernel.AbstractAgent;
import madkit.kernel.Message;
import madkit.message.ObjectMessage;
import madkit.message.SchedulingMessage;
import madkit.simulation.activator.GenericBehaviorActivator;

/**
 * @version 2.0.0.2
 * @author Fabien Michel, Olivier Gutknecht
 */
public class BeeScheduler extends madkit.kernel.Scheduler {

    private GenericBehaviorActivator<AbstractBee> bees;

    @Override
    public void activate() {
        super.activate();

        // Request scheduler role
        requestRole(COMMUNITY, SIMU_GROUP, SCHEDULER_ROLE);

        // Explicitly specify the types in the GenericBehaviorActivator
        bees = new GenericBehaviorActivator<AbstractBee>(COMMUNITY, SIMU_GROUP, BEE_ROLE, "buzz");
        addActivator(bees);

        // Hornet behavior activator
        GenericBehaviorActivator<HornetAgent> hornetActivator = new GenericBehaviorActivator<HornetAgent>(COMMUNITY, SIMU_GROUP, BeeLauncher.HORNET_ROLE, "buzz");
        addActivator(hornetActivator);

        // Viewer (for rendering)
        GenericBehaviorActivator<AbstractAgent> viewer = new GenericBehaviorActivator<AbstractAgent>(COMMUNITY, SIMU_GROUP, "bee observer", "observe");
        addActivator(viewer);

        // Automatically start the scheduler
        receiveMessage(new SchedulingMessage(SchedulingAction.RUN));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void checkMail(Message m) {
        if (m != null) {
            try {
                boolean multiCore = ((ObjectMessage<Boolean>) m).getContent();
                if (multiCore) {
                    // Enable multicore for both bees and hornets
                    bees.useMulticore(Runtime.getRuntime().availableProcessors());
                } else {
                    bees.useMulticore(1);
                }
            } catch (ClassCastException e) {
                super.checkMail(m); // Default behavior
            }
        }
    }

}
