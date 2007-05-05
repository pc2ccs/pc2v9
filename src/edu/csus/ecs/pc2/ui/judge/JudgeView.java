package edu.csus.ecs.pc2.ui.judge;

import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.ui.ClarificationsPane;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.JPanePlugin;
import edu.csus.ecs.pc2.ui.LogWindow;
import edu.csus.ecs.pc2.ui.OptionsPanel;
import edu.csus.ecs.pc2.ui.RunsPanel;
import edu.csus.ecs.pc2.ui.SubmitRunPane;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Judge GUI.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

public class JudgeView extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 5365837218548110171L;

    private IContest contest;

    @SuppressWarnings("unused")
    private IController controller;

    private JTabbedPane mainTabbedPane = null;

    private LogWindow logWindow = null;

    private Logger log;
    
    public JudgeView() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(569, 299));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getMainTabbedPane());
        setTitle("PC^2 Judge - Not Logged In ");
        FrameUtilities.centerFrame(this);
        setVisible(true);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptAndExit();
            }
        });

    }

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog("Are you sure you want to exit PC^2?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * This method initializes mainTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getMainTabbedPane() {
        if (mainTabbedPane == null) {
            mainTabbedPane = new JTabbedPane();
        }
        return mainTabbedPane;
    }

    protected void addUIPlugin(JTabbedPane tabbedPane, String tabTitle, JPanePlugin plugin) {

        plugin.setContestAndController(contest, controller);
        tabbedPane.add(plugin, tabTitle);

    }


    private void setFrameTitle(final boolean contestStarted) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (contestStarted) {
                    setTitle("PC^2 Judge " + contest.getTitle() + " [STARTED] Build " + new VersionInfo().getBuildNumber());
                } else {
                    setTitle("PC^2 Judge " + contest.getTitle() + " [STOPPED] Build " + new VersionInfo().getBuildNumber());
                }
            }
        });
        FrameUtilities.regularCursor(this);
    }

    private boolean isThisSite(int siteNumber) {
        return siteNumber == contest.getSiteNumber();
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        
        log = controller.getLog();
        
        if (logWindow == null) {
            logWindow = new LogWindow();
        }
        logWindow.setContestAndController(contest, controller);
        logWindow.setTitle("Log "+contest.getClientId().toString());

        setFrameTitle(contest.getContestTime().isContestRunning());
        
        RunsPanel runsPanel = new RunsPanel();
        addUIPlugin(getMainTabbedPane(), "All Runs", runsPanel);

        ClarificationsPane clarificationsPane = new ClarificationsPane();
        addUIPlugin(getMainTabbedPane(), "All clarifications", clarificationsPane);

        SubmitRunPane submitRunPane = new SubmitRunPane();
        addUIPlugin(getMainTabbedPane(), "Test Run", submitRunPane);
        
        OptionsPanel optionsPanel = new OptionsPanel();
        addUIPlugin(getMainTabbedPane(), "Options", optionsPanel);
        optionsPanel.setLogWindow(logWindow);
    }

    public String getPluginTitle() {
        return "Judge Main GUI";
    }

    protected void showLog(boolean showLogWindow) {
        logWindow.setVisible(showLogWindow);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
