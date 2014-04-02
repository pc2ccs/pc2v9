package edu.csus.ecs.pc2.ui.eventfeed;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.ui.ContestClockDisplay;
import edu.csus.ecs.pc2.ui.ContestClockDisplay.DisplayTimes;
import edu.csus.ecs.pc2.ui.EventFeedServerPane;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.JPanePlugin;
import edu.csus.ecs.pc2.ui.OptionsPane;
import edu.csus.ecs.pc2.ui.PacketMonitorPane;
import edu.csus.ecs.pc2.ui.PluginLoadPane;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Event Feeder and other feeders.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class FeederView extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 2157337344457051296L;

    private IInternalContest contest;

    private IInternalController controller;

    private JTabbedPane mainTabbedPane = null;

    private Log log;

    private JPanel mainViewPane = null;

    private JPanel northPane = null;

    private JLabel clockLabel = null;

    private JLabel messageLabel = null;

    private JPanel eastPane = null;

    private JButton exitButton = null;

    private ContestClockDisplay contestClockDisplay = null;

    private JPanel clockPanel = null;
    
    /**
     * This method initializes
     * 
     */
    public FeederView() {
        super();
        initialize();
    }


//    /**
//     * 
//     * @author pc2@ecs.csus.edu
//     * @version $Id$
//     */
//    
//    // $HeadURL$
//    public class PropertyChangeListenerImplementation implements PropertyChangeListener {
//
//        public void propertyChange(PropertyChangeEvent evt) {
//            if (evt.getPropertyName().equalsIgnoreCase("standings")) {
//                if (evt.getNewValue() != null && !evt.getNewValue().equals(evt.getOldValue())) {
//                    // standings have changed
//                    // SOMEDAY take this off the awt thread
//                    generateOutput((String) evt.getNewValue());
//                }
//            }
//        }
//    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(515, 319));
        this.setContentPane(getMainViewPane());
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("Feeder");

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptAndExit();
            }
        });

        overRideLookAndFeel();
        FrameUtilities.centerFrame(this);
    }
    
    private void overRideLookAndFeel(){
        // SOMEDAY eventually move this method to on location 
        String value = IniFile.getValue("client.plaf");
        if (value != null && value.equalsIgnoreCase("java")){
            FrameUtilities.setJavaLookAndFeel();
        }
        if (value != null && value.equalsIgnoreCase("native")){
            FrameUtilities.setNativeLookAndFeel();
        }
    }

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog(null, "Are you sure you want to exit PC^2?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

        contest.addContestTimeListener(new ContestTimeListenerImplementation());

        log = controller.getLog();
        log.info("Started Feeder View");
        
        contestClockDisplay = new ContestClockDisplay(controller.getLog(), contest.getContestTime(), contest.getSiteNumber(), true, null);
        contestClockDisplay.addLabeltoUpdateList(clockLabel, DisplayTimes.REMAINING_TIME, contest.getSiteNumber());
        controller.register(contestClockDisplay);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setTitle("PC^2 " + contest.getTitle() + " Build " + new VersionInfo().getBuildNumber());

                controller.startLogWindow(contest);
                
                setFrameTitle(contest.getContestTime().isContestRunning());

//                balloonHandler.setContestAndController(contest, controller);

                try {
                    EventFeedServerPane eventFeedServerPane = new EventFeedServerPane();
                    addUIPlugin(getMainTabbedPane(), "Event Server", eventFeedServerPane);
                } catch (Exception e) {
                    if (StaticLog.getLog() != null) {
                        StaticLog.getLog().log(Log.WARNING, "Exception", e);
                        e.printStackTrace(System.err);
                    } else {
                        e.printStackTrace(System.err);
                    }
                }
                
                if (Utilities.isDebugMode()) {
                    
                    try {
                        PacketMonitorPane pane = new PacketMonitorPane();
                        addUIPlugin(getMainTabbedPane(), "Packets", pane);
                    } catch (Exception e) {
                        logException(e);
                    }
                
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
                
                OptionsPane optionsPanel = new OptionsPane();
                addUIPlugin(getMainTabbedPane(), "Options", optionsPanel);

                showMessage("");

                setVisible(true);
            }
        });
    }

    public String getPluginTitle() {
        return "Feeder View";
    }

    protected void addUIPlugin(JTabbedPane tabbedPane, String tabTitle, JPanePlugin plugin) {
        controller.register(plugin);
        plugin.setParentFrame(this);
        plugin.setContestAndController(contest, controller);
        tabbedPane.add(plugin, tabTitle);
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
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getNorthPane() {
        if (northPane == null) {
            messageLabel = new JLabel();
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            messageLabel.setText("JLabel");
            clockLabel = new JLabel();
            clockLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
            clockLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            clockLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            clockLabel.setText("STOPPED ");
            northPane = new JPanel();
            northPane.setLayout(new BorderLayout());
            northPane.add(messageLabel, java.awt.BorderLayout.CENTER);
            northPane.add(getEastPane(), java.awt.BorderLayout.EAST);
            northPane.add(getClockPanel(), BorderLayout.WEST);
        }
        return northPane;
    }

    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getEastPane() {
        if (eastPane == null) {
            eastPane = new JPanel();
//            eastPane.add(getRefreshButton(), null);
            eastPane.add(getExitButton(), null);
        }
        return eastPane;
    }

    /**
     * This method initializes jButton
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

    private void setFrameTitle(final boolean contestStarted) {
        final JFrame thisFrame = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                FrameUtilities.setFrameTitle(thisFrame, contest.getTitle(), contestStarted, new VersionInfo());
                if (contestStarted) {
                    contestClockDisplay.fireClockStateChange(contest.getContestTime());
                } else {
                    clockLabel.setText("STOPPED");
                }

                if (contestClockDisplay.getClientFrame() == null) {
                    contestClockDisplay.setClientFrame(thisFrame);
                }
            }
        });

        FrameUtilities.regularCursor(this);
    }

    
    protected boolean isThisSite (int siteNumber){
        return contest.getSiteNumber() == siteNumber;
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    class ContestTimeListenerImplementation implements IContestTimeListener {

        public void contestTimeAdded(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        public void contestTimeChanged(ContestTimeEvent event) {
            ContestTime contestTime = event.getContestTime();
            if (isThisSite(contestTime.getSiteNumber())){
                setFrameTitle (contestTime.isContestRunning());
            }
        }

        public void contestStarted(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        public void contestStopped(ContestTimeEvent event) {
            contestTimeChanged(event);
        }

        public void refreshAll(ContestTimeEvent event) {
            contestTimeChanged(event);
        }
        
    }
    
    private void showMessage(final String string) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(string);
                messageLabel.setToolTipText(string);
            }
        });

    }

    /**
     * This method initializes refreshButton
     * 
     * @return javax.swing.JButton
     */
//    private JButton getRefreshButton() {
//        if (refreshButton == null) {
//            refreshButton = new JButton();
//            refreshButton.setPreferredSize(new java.awt.Dimension(100, 26));
//            refreshButton.setToolTipText("Re-generate the HTML");
//            refreshButton.setMnemonic(java.awt.event.KeyEvent.VK_R);
//            refreshButton.setText("Refresh");
//            refreshButton.addActionListener(new java.awt.event.ActionListener() {
//                public void actionPerformed(java.awt.event.ActionEvent e) {
//                    if (currentXMLString.length() > 0) {
//                        new Thread(new Runnable() {
//                            public void run() {
//                                generateOutput(currentXMLString);
//                            }
//                        }).start();
//                    } else{
//                        JOptionPane.showMessageDialog(getParent(), "XML currently unavailable", "Please wait", JOptionPane.WARNING_MESSAGE);
//                    }
//                }
//            });
//        }
//        return refreshButton;
//    }
    
    private void logException(Exception e) {

        if (StaticLog.getLog() != null) {
            StaticLog.getLog().log(Log.WARNING, "Exception", e);
            e.printStackTrace(System.err);
        } else {
            e.printStackTrace(System.err);
        }
    }

    /**
     * This method initializes clockPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getClockPanel() {
        if (clockPanel == null) {
            clockPanel = new JPanel();
            clockPanel.setPreferredSize(new java.awt.Dimension(85,34));
            clockPanel.setLayout(new BorderLayout());
            clockPanel.add(clockLabel, BorderLayout.CENTER);
        }
        return clockPanel;
    }
    
} // @jve:decl-index=0:visual-constraint="10,10"
