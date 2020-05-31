// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui.team;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.ui.AboutPane;
import edu.csus.ecs.pc2.ui.ClarificationsPane;
import edu.csus.ecs.pc2.ui.ContestClockDisplay;
import edu.csus.ecs.pc2.ui.ContestClockDisplay.DisplayTimes;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.JPanePlugin;
import edu.csus.ecs.pc2.ui.OptionsPane;
import edu.csus.ecs.pc2.ui.PluginLoadPane;
import edu.csus.ecs.pc2.ui.RunsPane;
import edu.csus.ecs.pc2.ui.SubmitClarificationPane;
import edu.csus.ecs.pc2.ui.SubmitRunPane;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Team Client View/GUI.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class TeamView extends JFrame implements UIPlugin {

    private IInternalContest contest = null;

    private IInternalController teamController = null;

    /**
     * 
     */
    private static final long serialVersionUID = 8225187691479543638L;

    private JPanel mainViewPane = null;

    private JTabbedPane mainTabbedPane = null;

    private JPanel northPane = null;

    private JPanel exitPane = null;

    private JPanel messagePane = null;

    private JPanel clockPane = null;

    private JLabel clockLabel = null;

    private JLabel messageLabel = null;

    private JButton exitButton = null;
    
    private ContestClockDisplay contestClockDisplay = null;

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
        this.setSize(new java.awt.Dimension(620,521));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getMainViewPane());
        this.setTitle("The TeamView");
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptAndExit();
            }
        });

        overRideLookAndFeel();
        FrameUtilities.centerFrame(this);
        setTitle("PC^2 Team - Not Logged In ");
        FrameUtilities.waitCursor(this);

    }
    
    private void overRideLookAndFeel(){
        // TODO eventually move this method to on location 
        String value = IniFile.getValue("client.plaf");
        if (value != null && value.equalsIgnoreCase("java")){
            FrameUtilities.setJavaLookAndFeel();
        }
        if (value != null && value.equalsIgnoreCase("native")){
            FrameUtilities.setNativeLookAndFeel();
        }
    }

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog(this, "Are you sure you want to exit PC^2?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private boolean isThisSite(int siteNumber) {
        return siteNumber == contest.getSiteNumber();
    }

    private void updateFrameTitle(final boolean turnButtonsOn) {
        
        final JFrame thisFrame = this;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                FrameUtilities.setFrameTitle(thisFrame, contest.getTitle(), turnButtonsOn, new VersionInfo());

                if (contestClockDisplay.getClientFrame() == null){
                    contestClockDisplay.setClientFrame(getThisFrame());
                }

                contestClockDisplay.fireClockStateChange(contest.getContestTime());

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

        public void refreshAll(ContestTimeEvent event) {
            if (isThisSite(event.getSiteNumber())) {
                updateFrameTitle(event.getContestTime().isContestRunning());
            }
        }
        
        /** This method exists to support differentiation between manual and automatic starts,
         * in the event this is desired in the future.
         * Currently it just delegates the handling to the contestStarted() method.
         */
        @Override
        public void contestAutoStarted(ContestTimeEvent event) {
            contestStarted(event);
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
    
    protected TeamView getThisFrame(){
        return this;
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.teamController = inController;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                teamController.startLogWindow(contest);
        
                contest.addContestTimeListener(new ContestTimeListenerImplementation());
        
                SubmitRunPane submitRunPane = new SubmitRunPane();
                addUIPlugin(getMainTabbedPane(), "Submit Run", submitRunPane);
        
                RunsPane runsPanel = new RunsPane();
                runsPanel.setShowJudgesInfo(false);
                addUIPlugin(getMainTabbedPane(), "View Runs", runsPanel);
        
                SubmitClarificationPane submitClarificationPane = new SubmitClarificationPane();
                addUIPlugin(getMainTabbedPane(), "Request Clarification", submitClarificationPane);

                ClarificationsPane clarificationsPane = new ClarificationsPane();
                addUIPlugin(getMainTabbedPane(), "View Clarifications", clarificationsPane);
        
                OptionsPane optionsPanel = new OptionsPane();
                addUIPlugin(getMainTabbedPane(), "Options", optionsPanel);
                
                if (Utilities.isDebugMode()) {
                    try {
                        PluginLoadPane pane = new PluginLoadPane();
                        pane.setParentTabbedPane(getMainTabbedPane());
                        addUIPlugin(getMainTabbedPane(), "Plugin Load", pane);
                    } catch (Exception e) {
                        if (StaticLog.getLog() != null) {
                            StaticLog.getLog().log(Log.WARNING, "Exception", e);
                            e.printStackTrace(System.err);
                        } else {
                            e.printStackTrace(System.err);
                        }
                    }
                }          
                
                AboutPane aboutPane = new AboutPane();
                addUIPlugin(getMainTabbedPane(), "About", aboutPane);


                updateFrameTitle(contest.getContestTime().isContestRunning());
                
                contestClockDisplay = new ContestClockDisplay(teamController.getLog(), contest.getContestTime(), contest.getSiteNumber(), isTeam(), null);
                contestClockDisplay.addLabeltoUpdateList(clockLabel, DisplayTimes.REMAINING_TIME, contest.getSiteNumber());
                teamController.register(contestClockDisplay);
                
                setVisible(true);
                //the following was fixed under Bug 800
//                //TODO This needs to be resolved. The submitClarifcaitonPane is bleeding through the other tabs
//                getMainTabbedPane().setSelectedComponent(submitClarificationPane);
//                getMainTabbedPane().doLayout();
//                getMainTabbedPane().setSelectedComponent(submitRunPane);
            }
        });
    }

    private boolean isTeam(ClientId id) {
        return id != null && id.getClientType().equals(ClientType.Type.TEAM);
    }
    private boolean isTeam() {
        return isTeam(contest.getClientId());
    }

    public String getPluginTitle() {
        return "Team Main GUI";
    }

    protected void addUIPlugin(JTabbedPane tabbedPane, String tabTitle, JPanePlugin plugin) {
        teamController.register (plugin);
        plugin.setParentFrame(this);
        plugin.setContestAndController(contest, teamController);
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
            clockLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
            clockLabel.setPreferredSize(new java.awt.Dimension(82,21));
            clockLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            clockPane = new JPanel();
            clockPane.setLayout(new BorderLayout());
            clockPane.setPreferredSize(new java.awt.Dimension(85,34));
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
            exitButton.setToolTipText("Click here to Shutdown PC^2");
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
