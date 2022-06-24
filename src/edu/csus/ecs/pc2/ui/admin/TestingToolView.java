// Copyright (C) 1989-20 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.ui.AboutPane;
import edu.csus.ecs.pc2.ui.ContestClockDisplay;
import edu.csus.ecs.pc2.ui.ContestClockDisplay.DisplayTimes;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.JPanePlugin;
import edu.csus.ecs.pc2.ui.LogWindow;
import edu.csus.ecs.pc2.ui.OptionsPane;
import edu.csus.ecs.pc2.ui.PluginLoadPane;
import edu.csus.ecs.pc2.ui.ReportPane;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * ReportsView - view that just shows report tab
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class TestingToolView extends JFrame implements UIPlugin, ChangeListener {

    private static final long serialVersionUID = 1L;

    private IInternalContest contest;

    private IInternalController controller; // @jve:decl-index=0:

    private JPanel jPanel = null;

    private JTabbedPane mainTabbedPanel = null;

    private JPanel statusPanel = null;

    private JPanel topPanel = null;

    private JButton exitButton = null;

    private LogWindow securityAlertLogWindow = null;

    private JPanel clockPane = null;

    private JPanel exitButtonPane = null;

    private JLabel clockLabel = null;

    private JPanel padPane = null;

    private ContestClockDisplay contestClockDisplay = null;

    private static final Color ACTIVE_TAB_COLOR = Color.BLUE;

    private static final Color INACTIVE_TAB_COLOR = Color.GRAY;

    private JPanel centerPane = null;

    private JPanel aMessagePane = null;

    private JLabel messageLabel = null;

    /**
     * This method initializes
     * 
     */
    public TestingToolView() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setBounds(new java.awt.Rectangle(0, 0, 1080, 750));
        this.setContentPane(getJPanel());
        this.setTitle("PC^2 Reports");
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptAndExit();
            }
        });

        getMainTabbedPanel().addChangeListener(this);

        overRideLookAndFeel();

        FrameUtilities.centerFrame(this);
    }

    private void overRideLookAndFeel() {
        String value = IniFile.getValue("client.plaf");
        if (value != null && value.equalsIgnoreCase("java")) {
            FrameUtilities.setJavaLookAndFeel();
        }
        if (value != null && value.equalsIgnoreCase("native")) {
            FrameUtilities.setNativeLookAndFeel();
        }
    }

    /**
     * Listeners for admin view.
     * 
     * This provides a way to refresh the admin view on refresh.
     * 
     * @author pc2@ecs.csus.edu
     */
    protected class Listeners implements UIPlugin {

        /**
         * 
         */
        private static final long serialVersionUID = 3733076435840880891L;

        public void setContestAndController(IInternalContest inContest, IInternalController inController) {
            ; // nuothing yet
        }

        public String getPluginTitle() {
            return "Listeners";
        }
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
        final JFrame thisFrame = this;

        Listeners listeners = new Listeners();
        listeners.setContestAndController(inContest, inController);
        controller.register(listeners);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                controller.startLogWindow(contest);

                initializeSecurityAlertWindow(contest);

                ReportPane reportPaneC = new ReportPane();
                addUIPlugin(getMainTabbedPanel(), "Reports", reportPaneC);

                PluginLoadPane pluginLoadPane = new PluginLoadPane();
                addUIPlugin(getMainTabbedPanel(), "UI Plugins", pluginLoadPane);
                pluginLoadPane.setParentTabbedPane(getMainTabbedPanel());

                OptionsPane optionsPanel = new OptionsPane();
                addUIPlugin(getMainTabbedPanel(), "Options", optionsPanel);
                optionsPanel.setSecurityLogWindow(securityAlertLogWindow);

                AboutPane aboutPane = new AboutPane();
                addUIPlugin(getMainTabbedPanel(), "About", aboutPane);

                /**
                 * Clock and frame title.
                 */

                contestClockDisplay = new ContestClockDisplay(controller.getLog(), contest.getContestTime(), contest.getSiteNumber(), false, null);
                contestClockDisplay.setContestAndController(contest, controller);
                contestClockDisplay.addLabeltoUpdateList(clockLabel, DisplayTimes.SCHEDULED_THEN_REMAINING_TIME, contest.getSiteNumber());
                contestClockDisplay.setClientFrame(thisFrame);

                contest.addContestTimeListener(new ContestTimeListenerImplementation());
                controller.register(contestClockDisplay);

                FrameUtilities.setFrameTitle(thisFrame, contest.getTitle(), contest.getContestTime().isContestRunning(), new VersionInfo());
                setVisible(true);
                
                VersionInfo versionInfo = new VersionInfo();
                String verString = "Version " + versionInfo.getPC2Version() + " Build " + versionInfo.getBuildNumber();
                showMessage("Testing Tool (" + verString + ")");

            }

        });


    }

    /**
     * Set the tab for the input name.
     * 
     * @param tabbedPane
     * @param name
     */
    protected void setSelectedTab(JTabbedPane tabbedPane, String name) {

        for (int i = 0; i < tabbedPane.getComponentCount(); i++) {
            String tabTitle = tabbedPane.getTitleAt(i);
            if (tabTitle != null && name.equals(tabTitle)) {
                tabbedPane.setSelectedIndex(i);
            }
        }
    }

    void logException(Exception e) {

        if (StaticLog.getLog() != null) {
            StaticLog.getLog().log(Log.WARNING, "Exception", e);
            e.printStackTrace(System.err);
        } else {
            e.printStackTrace(System.err);
        }
    }

    protected void initializeSecurityAlertWindow(IInternalContest inContest) {
        if (securityAlertLogWindow == null) {
            securityAlertLogWindow = new LogWindow(inContest.getSecurityAlertLog());
        }
        securityAlertLogWindow.setContestAndController(inContest, controller);
        securityAlertLogWindow.setTitle("Contest Security Alerts " + inContest.getClientId().toString());
        VersionInfo versionInfo = new VersionInfo();
        securityAlertLogWindow.getLog().info("Security Log Started " + versionInfo.getSystemVersionInfo());
    }

    public String getPluginTitle() {
        return "Reports GUI";
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout());
            jPanel.add(getMainTabbedPanel(), java.awt.BorderLayout.CENTER);
            jPanel.add(getTopPanel(), java.awt.BorderLayout.NORTH);
            jPanel.add(getStatusPanel(), java.awt.BorderLayout.SOUTH);
        }
        return jPanel;
    }

    /**
     * This method initializes mainTabbedPanel
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getMainTabbedPanel() {
        if (mainTabbedPanel == null) {
            mainTabbedPanel = new JTabbedPane();
            
            mainTabbedPanel.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    // clear message on tab selected/changed
                    showMessage("");
                }
            });

        }
        return mainTabbedPanel;
    }

    /**
     * This method initializes statusPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getStatusPanel() {
        if (statusPanel == null) {
            statusPanel = new JPanel();
            statusPanel.setPreferredSize(new java.awt.Dimension(30, 30));
        }
        return statusPanel;
    }

    /**
     * This method initializes topPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTopPanel() {
        if (topPanel == null) {
            topPanel = new JPanel();
            topPanel.setLayout(new BorderLayout());
            topPanel.setPreferredSize(new java.awt.Dimension(45, 45));
            topPanel.add(getExitButtonPane(), java.awt.BorderLayout.EAST);
            topPanel.add(getPadPane(), java.awt.BorderLayout.WEST);
            topPanel.add(getCenterPane(), BorderLayout.CENTER);
        }
        return topPanel;
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

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog(this, "Are you sure you want to exit PC^2?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    protected void addUIPlugin(JTabbedPane tabbedPane, String tabTitle, JPanePlugin plugin) {

        if (plugin == null) {
            return;
        }

        try {
            controller.register(plugin);
            plugin.setParentFrame(this);
            plugin.setContestAndController(contest, controller);
            tabbedPane.add(plugin, tabTitle);

        } catch (Exception e) {
            controller.getLog().log(Log.WARNING, "Exception loading plugin ", e);
            JOptionPane.showMessageDialog(this, "Error loading " + plugin.getPluginTitle());
        }

    }

    protected void showLog(boolean showLogWindow) {
        controller.showLogWindow(showLogWindow);
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
            clockLabel.setPreferredSize(new Dimension(100, 24));
            clockLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
            clockPane = new JPanel();
            clockPane.setLayout(new BorderLayout());
            clockPane.setPreferredSize(new java.awt.Dimension(85, 34));
            clockPane.add(clockLabel, BorderLayout.WEST);
        }
        return clockPane;
    }

    /**
     * This method initializes exitButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getExitButtonPane() {
        if (exitButtonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(10);
            flowLayout.setVgap(10);
            exitButtonPane = new JPanel();
            exitButtonPane.setLayout(flowLayout);
            exitButtonPane.add(getExitButton(), null);
        }
        return exitButtonPane;
    }

    /**
     * This method initializes padPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPadPane() {
        if (padPane == null) {
            padPane = new JPanel();
            padPane.setPreferredSize(new java.awt.Dimension(10, 10));
        }
        return padPane;
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == getMainTabbedPanel()) {
            // change all mainpanel tab text to black
            int tabCount = getMainTabbedPanel().getTabCount();
            for (int i = 0; i < tabCount; i++) {
                getMainTabbedPanel().setForegroundAt(i, INACTIVE_TAB_COLOR);
            }
            // change the currently selected mainpanel tab to red
            int selectedTab = getMainTabbedPanel().getSelectedIndex();
            getMainTabbedPanel().setForegroundAt(selectedTab, ACTIVE_TAB_COLOR);
        } else {
            throw new RuntimeException("Unexpected ChangeEvent: " + e);
        }
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            centerPane = new JPanel();
            centerPane.setLayout(new BorderLayout());
            centerPane.add(getClockPane(), BorderLayout.WEST);
            centerPane.add(getAMessagePane(), BorderLayout.CENTER);
        }
        return centerPane;
    }

    /**
     * This method initializes aMessagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAMessagePane() {
        if (aMessagePane == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            messageLabel.setFont(new Font("Dialog", Font.BOLD, 18));
            messageLabel.setForeground(new Color(0, 186, 0));
            messageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            aMessagePane = new JPanel();
            aMessagePane.setLayout(new BorderLayout());
            aMessagePane.add(messageLabel, BorderLayout.CENTER);
        }
        return aMessagePane;
    }

    public static void main(String[] args) {
        try {
            TestingToolView reportView = new TestingToolView();
            reportView.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected boolean isThisSite(int siteNumber) {
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
            if (isThisSite(contestTime.getSiteNumber())) {
                setFrameTitle(contestTime.isContestRunning());
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

        /**
         * This method exists to support differentiation between manual and automatic starts, in the event this is desired in the future. Currently it just delegates the handling to the
         * contestStarted() method, although it also pops up a notification dialog that the contest has been "auto-started".
         */
        @Override
        public void contestAutoStarted(ContestTimeEvent event) {
            contestStarted(event);

            Log log = controller.getLog();
            if (log != null) {
                log.info("Contest automatically started due to arrival of enabled scheduled start time.");
            }

            JOptionPane optionPane = new JOptionPane();
            optionPane.setMessage("Scheduled Start Time has arrived; contest has been automatically started!");
            optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
            JDialog dialog = optionPane.createDialog("Contest Started");
            dialog.setLocationRelativeTo(null); // center the dialog
            dialog.setModalityType(ModalityType.APPLICATION_MODAL);

            dialog.setAlwaysOnTop(true);

            dialog.setVisible(true);
        }

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

    private void showMessage(String s) {

        final String message = s;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(message);
            }
        });
    }

} // @jve:decl-index=0:visual-constraint="10,10"
