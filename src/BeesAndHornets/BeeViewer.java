package BeesAndHornets;

import static madkit.bees.BeeLauncher.BEE_ROLE;
import static madkit.bees.BeeLauncher.COMMUNITY;
import static madkit.bees.BeeLauncher.SCHEDULER_ROLE;
import static madkit.bees.BeeLauncher.VIEWER_ROLE;
import static BeesAndHornets.BeeLauncher.HORNET_ROLE;
import static madkit.bees.BeeLauncher.SIMU_GROUP;
import static madkit.bees.BeeLauncher.LAUNCHER_ROLE;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import madkit.action.KernelAction;
import madkit.action.SchedulingAction;
import BeesAndHornets.BeeLauncher.BeeLauncherAction;
import madkit.gui.AgentFrame;
import madkit.gui.SwingUtil;
import madkit.gui.menu.*;
import madkit.message.*;
import madkit.simulation.probe.PropertyProbe;
import madkit.simulation.viewer.SwingViewer;

/**
 * Viewer for the bee simulation, displaying bees and hornets dynamically.
 * 
 * @version 2.0.0.3
 */
@SuppressWarnings("serial")
public class BeeViewer extends SwingViewer {

    private final BeeEnvironment environment;
    private JPanel display;
    private AbstractAction synchroPaint, artMode, randomMode, launch, trailModeAction, multicoreMode;
    private PropertyProbe<AbstractBee, BeeInformation> beeProbe;
    private PropertyProbe<HornetAgent, BeeInformation> hornetProbe;
    private final BeeScheduler scheduler;
    private int nbOfBeesToLaunch = 30000;
    public static int nbOfBroadcast = 0;

    public BeeViewer(BeeScheduler scheduler) {
        this.scheduler = scheduler;
        this.environment = new BeeEnvironment(new Dimension(1600, 1024));
    }

    @Override
    protected void activate() {
        requestRole(COMMUNITY, SIMU_GROUP, VIEWER_ROLE);

        // Initialize Bee probe
        beeProbe = new PropertyProbe<AbstractBee, BeeInformation>(COMMUNITY, SIMU_GROUP, BEE_ROLE, "myInformation") {
            @Override
            public void adding(AbstractBee bee) {
                super.adding(bee);
                bee.setEnvironment(environment); // Assign environment to bees
            }
        };
        addProbe(beeProbe);

        // Initialize Hornet probe
        hornetProbe = new PropertyProbe<HornetAgent, BeeInformation>(COMMUNITY, SIMU_GROUP, HORNET_ROLE, "myInformation") {
            @Override
            public void adding(HornetAgent agent) {
                super.adding(agent);
                agent.setEnvironment(environment); // Assign environment to hornets
            }
        };
        addProbe(hornetProbe);
    }

    @Override
    protected void end() {
        removeProbe(beeProbe);
        removeProbe(hornetProbe);
        sendMessage(COMMUNITY, SIMU_GROUP, LAUNCHER_ROLE, new KernelMessage(KernelAction.EXIT));
        sendMessage(COMMUNITY, SIMU_GROUP, SCHEDULER_ROLE, new SchedulingMessage(SchedulingAction.SHUTDOWN));
        leaveRole(COMMUNITY, SIMU_GROUP, VIEWER_ROLE);
    }

    @Override
    protected void render(Graphics g) {
        if (g != null) {
            computeFromInfoProbe(g);
        }
    }

    private void computeFromInfoProbe(Graphics g) {
    	g.drawString("You are watching " + beeProbe.size() + " MaDKit agents", 10, 10);
    	Color lastColor = null;
    	final boolean trailMode = (Boolean) trailModeAction.getValue(Action.SELECTED_KEY);
    	for (final AbstractBee arg0 : beeProbe.getCurrentAgentsList()) {
    	    final BeeInformation b = beeProbe.getPropertyValue(arg0);
    	    final Color c = b.getBeeColor();
    	    if (c != lastColor) {
    		lastColor = c;
    		g.setColor(lastColor);
    	    }
    	    final Point p = b.getCurrentPosition();
    	    
    	    if(b.getRole().equals("bee")) {
    	    	if (trailMode) {
    	    		final Point p1 = b.getPreviousPosition();
    	    		g.drawLine(p1.x, p1.y, p.x, p.y);
    	    	    }
    	    	 else {
    	    		g.drawLine(p.x, p.y, p.x, p.y);
    	    	    }
    	    }
    	    else if(b.getRole().equals("queen")) {
    	    	if (trailMode) {
    	    		final Point p1 = b.getPreviousPosition();
    	    		g.fillOval(p1.x, p1.y, 20, 20);
    	    	    }
    	    	 else {
    	    		g.fillOval(p.x, p.y, 20, 20);
    	    	    }
    	    }
    	    else {
    	    	if (trailMode) {
    	    		final Point p1 = b.getPreviousPosition();
    	    		g.fillRect(p1.x, p1.y, 20, 20);
    	    	    }
    	    	 else {
    	    		g.fillRect(p.x, p.y, 20, 20);
    	    	    }
    	    }
    	    
    	}
        }

//    private void renderBee(Graphics g, BeeInformation info, boolean trailMode) {
//        g.setColor(info.getBeeColor());
//        Point current = info.getCurrentPosition();
//        Point previous = info.getPreviousPosition();
//
//        if (trailMode) {
//            g.drawLine(previous.x, previous.y, current.x, current.y);
//        } else {
//            g.drawLine(current.x, current.y, current.x, current.y);
//        }
//    }
//
//    private void renderHornet(Graphics g, BeeInformation info) {
//        g.setColor(Color.RED); // Distinguish hornets
//        Point position = info.getCurrentPosition();
//        g.fillRect(position.x, position.y, 15, 15); // Hornets as red squares
//    }

    @Override
    public void setupFrame(AgentFrame frame) {
	super.setupFrame(frame);
	buildActions(frame);
	frame.setBackground(Color.black);
	JMenuBar jmenubar = new JMenuBar();
	jmenubar.add(new MadkitMenu(this));
	jmenubar.add(new AgentMenu(this));
	jmenubar.add(new LaunchAgentsMenu(this));
	jmenubar.add(new AgentLogLevelMenu(this));
	jmenubar.add(this.scheduler.getSchedulerMenu());
	JMenu options = new JMenu("Options");
	options.add(new JCheckBoxMenuItem(synchroPaint));
	options.add(new JCheckBoxMenuItem(artMode));
	options.add(new JCheckBoxMenuItem(randomMode));
	options.add(new JCheckBoxMenuItem(trailModeAction));
	options.add(launch);
	jmenubar.add(options);

	ActionListener beeLaunchActionListener = evt -> sendLaunchMessage(Integer.parseInt(evt.getActionCommand()));

	JMenu numberOfBees = new JMenu("Number of bees to launch when clicking the icon");
	JMenu launchBees = new JMenu("Launching");
	ButtonGroup bgroup = new ButtonGroup();
	int defaultBeesNb = 10000;
	for (int i = 1000; i <= 1000000; i *= 10) {
	    JRadioButtonMenuItem item = new JRadioButtonMenuItem("Launch " + i + " bees");
	    item.setActionCommand(new Integer(i).toString().toString());
	    item.addActionListener(e -> nbOfBeesToLaunch = Integer.parseInt(e.getActionCommand()));
	    JMenuItem it = new JMenuItem("Launch " + i + " bees");
	    it.addActionListener(beeLaunchActionListener);
	    it.setActionCommand("" + i);
	    launchBees.add(it);
	    item.setActionCommand("" + i);
	    if (i == defaultBeesNb)
		item.setSelected(true);
	    bgroup.add(item);
	    numberOfBees.add(item);
	}
	options.add(numberOfBees);
	jmenubar.add(launchBees);

	frame.setJMenuBar(jmenubar);
	frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
	display = new JPanel() {

	    @Override
	    protected void paintComponent(Graphics g) {
		if (!(Boolean) artMode.getValue(Action.SELECTED_KEY)) {
		    super.paintComponent(g);
		}
		render(g);
	    }
	};
	setDisplayPane(display);
	display.setBackground(Color.BLACK);
	display.setForeground(Color.white);
	frame.add(display);
	display.addComponentListener(new ComponentAdapter() {
	    @Override
	    public void componentResized(ComponentEvent e) {
		environment.setEnvSize(e.getComponent().getSize());
		if (beeProbe != null) {
		    beeProbe.initialize();
		}
	    }
	});
	JToolBar tb = new JToolBar();
	addButtonToToolbar(tb, randomMode);
	addButtonToToolbar(tb, artMode);
	addButtonToToolbar(tb, trailModeAction);
	addButtonToToolbar(tb, synchroPaint);
	addButtonToToolbar(tb, launch);
	addButtonToToolbar(tb, multicoreMode);

	JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT));
	tools.add(tb);

	tools.add(this.scheduler.getSchedulerToolBar());

	JToolBar modelProperties = new JToolBar();
	modelProperties.add(SwingUtil.createSliderPanel(environment.getQueenAcceleration(), "queen acceleration"));
	modelProperties.add(SwingUtil.createSliderPanel(environment.getQueenVelocity(), "queen velocity"));
	modelProperties.add(SwingUtil.createSliderPanel(environment.getBeeAcceleration(), "bee acceleration"));
	modelProperties.add(SwingUtil.createSliderPanel(environment.getBeeVelocity(), "bee velocity"));
	modelProperties.add(SwingUtil.createSliderPanel(environment.getHornetAcceleration(), "hornet acceleration"));
	modelProperties.add(SwingUtil.createSliderPanel(environment.getHornetVelocity(), "hornet velocity"));
	tools.add(modelProperties);

	frame.add(this.scheduler.getSchedulerStatusLabel(), BorderLayout.SOUTH);
	display.getParent().add(tools, BorderLayout.PAGE_START);
	frame.setLocationRelativeTo(null);
	frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * @param tb
     */
    private void addButtonToToolbar(JToolBar tb, Action a) {
	JToggleButton jt = new JToggleButton(a);
	jt.setText(null);
	tb.add(jt);
    }

    void buildActions(final JFrame frame) {
	synchroPaint = new AbstractAction("Synchronous painting") {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		setSynchronousPainting(!(Boolean) synchroPaint.getValue(Action.SELECTED_KEY));
	    }
	};
	initActionIcon(synchroPaint, "Deactivate the synchronous painting mode (faster)", "synchroPaint");
	synchroPaint.putValue(Action.SELECTED_KEY, false);
	artMode = new AbstractAction("Art mode") {

	    @Override
	    public void actionPerformed(ActionEvent e) {
	    }
	};
	initActionIcon(artMode, "A funny painting mode", "artMode");

	randomMode = new AbstractAction("Random mode") {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		sendMessage(COMMUNITY, SIMU_GROUP, LAUNCHER_ROLE, new EnumMessage<>(BeeLauncherAction.RANDOM_MODE, randomMode.getValue(SELECTED_KEY)));
	    }
	};
	initActionIcon(randomMode, "Random mode: Randomly launch or kill bees", "random");
	randomMode.putValue(Action.SELECTED_KEY, true);

	multicoreMode = new AbstractAction("Multicore mode") {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		sendMessage(COMMUNITY, SIMU_GROUP, BeeLauncher.SCHEDULER_ROLE, new ObjectMessage<>((Boolean) multicoreMode.getValue(SELECTED_KEY)));
	    }
	};
	initActionIcon(multicoreMode, "Multicore mode: Use several processor cores if available (more efficient if synchro painting is off", "multicore");
	// randomMode.putValue(Action.SELECTED_KEY, false);

	trailModeAction = new AbstractAction("Trail mode") {

	    @Override
	    public void actionPerformed(ActionEvent e) {
	    }
	};
	initActionIcon(trailModeAction, "Trails mode: display agents with trails or like point particles", "trail");
	trailModeAction.putValue(Action.SELECTED_KEY, true);

	launch = new AbstractAction("Launch bees") {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		sendLaunchMessage(nbOfBeesToLaunch);
	    }
	};
	initActionIcon(launch, "Launch some bees", "launch");
    }

    private void initActionIcon(AbstractAction a, String description, String actionCommand) {
	a.putValue(Action.SELECTED_KEY, false);
	a.putValue(Action.ACTION_COMMAND_KEY, actionCommand);
	a.putValue(AbstractAction.SHORT_DESCRIPTION, description);
	ImageIcon big = new ImageIcon(getClass().getResource("images/bees_" + actionCommand + ".png"));
	a.putValue(AbstractAction.LARGE_ICON_KEY, big);
	a.putValue(AbstractAction.SMALL_ICON, new ImageIcon(big.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
    }

    private void sendLaunchMessage(int nb) {
	sendMessage(COMMUNITY, SIMU_GROUP, LAUNCHER_ROLE, new EnumMessage<>(BeeLauncherAction.LAUNCH_BEES, nb));

    }
}
