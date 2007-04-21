package edu.csus.ecs.pc2.ui.team;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.ui.ClarificationsPane;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.JPanePlugin;
import edu.csus.ecs.pc2.ui.LogWindow;
import edu.csus.ecs.pc2.ui.RunsPanel;
import edu.csus.ecs.pc2.ui.SubmitClarificationPane;
import edu.csus.ecs.pc2.ui.SubmitRunPane;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Team Client View/GUI.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class TeamView extends JFrame implements UIPlugin {

    public static final String SVN_ID = "$Id$";

    private IModel model = null;

    private IController teamController = null;

    /**
     * 
     */
    private static final long serialVersionUID = 8225187691479543638L;

    private JPanel mainViewPane = null;

    private JTabbedPane mainTabbedPane = null;

    private DefaultListModel runListModel = new DefaultListModel();

    private JPanel optionsPane = null;

    private JCheckBox showLogWindowCheckBox = null;

    private LogWindow logWindow = null;

    /**
     * Nevermind this constructor, needed for VE and other reasons.
     * 
     */
    public TeamView() {
        super();
        initialize();
        updateListBox(getPluginTitle() + " Build " + new VersionInfo().getBuildNumber());
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(481, 351));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getMainViewPane());
        this.setTitle("The TeamView");
        FrameUtilities.waitCursor(this);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptAndExit();
            }
        });

        FrameUtilities.centerFrame(this);
        setTitle("PC^2 Team - Not Logged In ");

    }

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog("Are you sure you want to exit PC^2?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }


    private void updateListBox(String string) {
        runListModel.insertElementAt(string, 0);
        StaticLog.unclassified(string);
    }

    private boolean isThisSite(int siteNumber) {
        return siteNumber == model.getSiteNumber();
    }

    
    private void updateFrameTitle(final boolean turnButtonsOn) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (turnButtonsOn) {
                    setTitle("PC^2 Team " + model.getTitle() + " [STARTED] Build " + new VersionInfo().getBuildNumber());
                } else {
                    setTitle("PC^2 Team " + model.getTitle() + " [STOPPED] Build " + new VersionInfo().getBuildNumber());
                }
            }
        });
        FrameUtilities.regularCursor(this);

    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    private class ContestTimeListenerImplementation implements IContestTimeListener {

        public void contestTimeAdded(ContestTimeEvent event) {
            updateListBox("ContestTime site " + event.getSiteNumber() + " ADDED " + event.getContestTime().getElapsedTimeStr());
            if (isThisSite(event.getSiteNumber())) {
                updateFrameTitle(event.getContestTime().isContestRunning());
            }
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
            updateListBox("ContestTime site " + event.getSiteNumber() + " REMOVED ");
        }

        public void contestTimeChanged(ContestTimeEvent event) {
            updateListBox("ContestTime site " + event.getSiteNumber() + " CHANGED ");
        }

        public void contestStarted(ContestTimeEvent event) {
            updateListBox("ContestTime site " + event.getSiteNumber() + " STARTED " + event.getContestTime().getElapsedTimeStr());
            if (isThisSite(event.getSiteNumber())) {
                updateFrameTitle(event.getContestTime().isContestRunning());
            }
        }

        public void contestStopped(ContestTimeEvent event) {
            updateListBox("ContestTime site " + event.getSiteNumber() + " STOPPED " + event.getContestTime().getElapsedTimeStr());
            if (isThisSite(event.getSiteNumber())) {
                updateFrameTitle(event.getContestTime().isContestRunning());
            }
        }
    }

    /**
     * This method initializes mainViewPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainViewPane() {
        if (mainViewPane == null) {
            mainViewPane = new JPanel();
            mainViewPane.setLayout(new BorderLayout());
            mainViewPane.add(getMainTabbedPane(), java.awt.BorderLayout.CENTER);
        }
        return mainViewPane;
    }

    /**
     * This method initializes viewTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getMainTabbedPane() {
        if (mainTabbedPane == null) {
            mainTabbedPane = new JTabbedPane();
            mainTabbedPane.addTab("Option", null, getOptionsPane(), null);
        }
        return mainTabbedPane;
    }

    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.teamController = inController;
        
        if (logWindow == null) {
            logWindow = new LogWindow();
        }
        logWindow.setModelAndController(model, teamController);
        logWindow.setTitle("Log "+model.getClientId().toString());
        
        model.addContestTimeListener(new ContestTimeListenerImplementation());

        SubmitRunPane submitRunPane = new SubmitRunPane();
        addUIPlugin(getMainTabbedPane(), "Submit Run", submitRunPane);
        
        RunsPanel runsPanel = new RunsPanel();
        addUIPlugin(getMainTabbedPane(), "View Runs", runsPanel);
        
        ClarificationsPane clarificationsPane = new ClarificationsPane();
        addUIPlugin(getMainTabbedPane(), "View Clarifications", clarificationsPane);
        
        SubmitClarificationPane submitClarificationPane = new SubmitClarificationPane();
        addUIPlugin(getMainTabbedPane(), "Request Clarification", submitClarificationPane);
        
        updateFrameTitle(model.getContestTime().isContestRunning());
        
        setVisible(true);
    }

    public String getPluginTitle() {
        return "Team Main GUI";
    }

    /**
     * This method initializes optionsPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getOptionsPane() {
        if (optionsPane == null) {
            optionsPane = new JPanel();
            optionsPane.add(getShowLogWindowCheckBox(), null);
        }
        return optionsPane;
    }

    protected void showLog(boolean showLogWindow) {
        logWindow.setVisible(showLogWindow);
    }

    /**
     * This method initializes showLogWindowCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getShowLogWindowCheckBox() {
        if (showLogWindowCheckBox == null) {
            showLogWindowCheckBox = new JCheckBox();
            showLogWindowCheckBox.setText("Show Log");
            showLogWindowCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    showLog(showLogWindowCheckBox.isSelected());
                }
            });
        }
        return showLogWindowCheckBox;
    }
    
    protected void addUIPlugin(JTabbedPane tabbedPane, String tabTitle, JPanePlugin plugin) {
        plugin.setModelAndController(model, teamController);
        tabbedPane.add(plugin, tabTitle);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
