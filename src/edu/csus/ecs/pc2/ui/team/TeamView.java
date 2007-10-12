package edu.csus.ecs.pc2.ui.team;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.ui.ClarificationsPane;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.JPanePlugin;
import edu.csus.ecs.pc2.ui.LogWindow;
import edu.csus.ecs.pc2.ui.OptionsPanel;
import edu.csus.ecs.pc2.ui.RunsPanel;
import edu.csus.ecs.pc2.ui.SubmitClarificationPane;
import edu.csus.ecs.pc2.ui.SubmitRunPane;
import edu.csus.ecs.pc2.ui.UIPlugin;
import javax.swing.JLabel;
import javax.swing.JButton;

/**
 * Team Client View/GUI.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class TeamView extends JFrame implements UIPlugin {

    public static final String SVN_ID = "$Id$";

    private IContest model = null;

    private IController teamController = null;

    /**
     * 
     */
    private static final long serialVersionUID = 8225187691479543638L;

    private JPanel mainViewPane = null;

    private JTabbedPane mainTabbedPane = null;

    private LogWindow logWindow = null;

    private JPanel northPane = null;

    private JPanel exitPane = null;

    private JPanel messagePane = null;

    private JPanel clockPane = null;

    private JLabel clockLabel = null;

    private JLabel messageLabel = null;

    private JButton exitButton = null;

    /**
     * Nevermind this constructor, needed for VE and other reasons.
     * 
     */
    public TeamView() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(556,413));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getMainViewPane());
        this.setTitle("The TeamView");
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptAndExit();
            }
        });

        FrameUtilities.centerFrame(this);
        setTitle("PC^2 Team - Not Logged In ");
        FrameUtilities.waitCursor(this);

    }

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog("Are you sure you want to exit PC^2?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
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

        if (turnButtonsOn) {
            updateClockLabel("");
        } else {
            updateClockLabel("STOPPED");
        }
        
        FrameUtilities.regularCursor(this);

    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    private class ContestTimeListenerImplementation implements IContestTimeListener {

        public void contestTimeAdded(ContestTimeEvent event) {
            if (isThisSite(event.getSiteNumber())) {
                updateFrameTitle(event.getContestTime().isContestRunning());
            }
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
            // ignore
        }

        public void contestTimeChanged(ContestTimeEvent event) {
            if (isThisSite(event.getSiteNumber())) {
                updateFrameTitle(event.getContestTime().isContestRunning());
            }
        }

        public void contestStarted(ContestTimeEvent event) {
            if (isThisSite(event.getSiteNumber())) {
                updateFrameTitle(event.getContestTime().isContestRunning());
            }
        }

        public void contestStopped(ContestTimeEvent event) {
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
            mainViewPane.add(getNorthPane(), java.awt.BorderLayout.NORTH);
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
        }
        return mainTabbedPane;
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.model = inContest;
        this.teamController = inController;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (logWindow == null) {
                    logWindow = new LogWindow();
                }
                logWindow.setContestAndController(model, teamController);
                logWindow.setTitle("Log " + model.getClientId().toString());
        
                model.addContestTimeListener(new ContestTimeListenerImplementation());
        
                SubmitRunPane submitRunPane = new SubmitRunPane();
                addUIPlugin(getMainTabbedPane(), "Submit Run", submitRunPane);
        
                RunsPanel runsPanel = new RunsPanel();
                runsPanel.setShowJudgesInfo(false);
                addUIPlugin(getMainTabbedPane(), "View Runs", runsPanel);
        
                SubmitClarificationPane submitClarificationPane = new SubmitClarificationPane();
                addUIPlugin(getMainTabbedPane(), "Request Clarification", submitClarificationPane);
        
                ClarificationsPane clarificationsPane = new ClarificationsPane();
                addUIPlugin(getMainTabbedPane(), "View Clarifications", clarificationsPane);
        
                OptionsPanel optionsPanel = new OptionsPanel();
                addUIPlugin(getMainTabbedPane(), "Options", optionsPanel);
                optionsPanel.setLogWindow(logWindow);
        
                updateFrameTitle(model.getContestTime().isContestRunning());
        
                setVisible(true);
            }
        });
    }

    public String getPluginTitle() {
        return "Team Main GUI";
    }

    protected void addUIPlugin(JTabbedPane tabbedPane, String tabTitle, JPanePlugin plugin) {
        plugin.setContestAndController(model, teamController);
        tabbedPane.add(plugin, tabTitle);
    }

    /**
     * This method initializes northPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getNorthPane() {
        if (northPane == null) {
            northPane = new JPanel();
            northPane.setLayout(new BorderLayout());
            northPane.setPreferredSize(new java.awt.Dimension(40, 40));
            northPane.add(getExitPane(), java.awt.BorderLayout.EAST);
            northPane.add(getMessagePane(), java.awt.BorderLayout.CENTER);
            northPane.add(getClockPane(), java.awt.BorderLayout.WEST);
        }
        return northPane;
    }

    /**
     * This method initializes exitPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getExitPane() {
        if (exitPane == null) {
            exitPane = new JPanel();
            exitPane.add(getExitButton(), null);
        }
        return exitPane;
    }

    /**
     * This method initializes messagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePane() {
        if (messagePane == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePane = new JPanel();
            messagePane.setLayout(new BorderLayout());
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
    }

    /**
     * This method initializes clockPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getClockPane() {
        if (clockPane == null) {
            clockLabel = new JLabel();
            clockLabel.setText("STOPPED");
            clockLabel.setForeground(java.awt.Color.red);
            clockLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            clockLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            clockPane = new JPanel();
            clockPane.setLayout(new BorderLayout());
            clockPane.setPreferredSize(new java.awt.Dimension(80, 34));
            clockPane.add(clockLabel, java.awt.BorderLayout.CENTER);
        }
        return clockPane;
    }

    /**
     * This method initializes exitButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getExitButton() {
        if (exitButton == null) {
            exitButton = new JButton();
            exitButton.setText("Exit");
            exitButton.setMnemonic(java.awt.event.KeyEvent.VK_X);
            exitButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    promptAndExit();
                }
            });
        }
        return exitButton;
    }

    private void updateClockLabel(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                clockLabel.setText(string);
                clockLabel.setToolTipText(string);
            }
        });

    }

    @SuppressWarnings("unused")
    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
                messageLabel.setToolTipText(string);
            }
        });
    }

} // @jve:decl-index=0:visual-constraint="10,10"
