package edu.csus.ecs.pc2.ui.judge;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.LogWindow;
import edu.csus.ecs.pc2.ui.UIPlugin;
import javax.swing.JCheckBox;

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

    private IModel model;

    @SuppressWarnings("unused")
    private IController controller;

    private JTabbedPane mainTabbedPane = null;

    private JPanel allRunsPane = null;

    private JScrollPane scrollPane = null;

    private JList runListBox = null;

    private DefaultListModel runListModel = new DefaultListModel(); // @jve:decl-index=0:visual-constraint=""

    private JPanel jPanel = null;

    private JCheckBox showLogWindowCheckBox = null;

    private LogWindow logWindow = null;
    
    public JudgeView() {
        super();
        initialize();

        updateListBox(getPluginTitle() + " Build " + new VersionInfo().getBuildNumber());
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
        setVisible(true);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptAndExit();
            }
        });
        FrameUtilities.centerFrame(this);

        if (logWindow == null) {
            logWindow = new LogWindow();
        }
    }

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog("Are you sure you want to exit PC^2?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    private class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            updateListBox("Added run " + event.getRun());
        }

        public void runChanged(RunEvent event) {
            updateListBox(event.getRun() + " CHANGED ");
        }

        public void runRemoved(RunEvent event) {
            updateListBox(event.getRun() + " REMOVED ");
        }
    }

    private void updateListBox(String string) {
        getRunListModel().addElement(string);
    }

    /**
     * This method initializes mainTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getMainTabbedPane() {
        if (mainTabbedPane == null) {
            mainTabbedPane = new JTabbedPane();
            mainTabbedPane.addTab("All Runs", null, getAllRunsPane(), null);
            mainTabbedPane.addTab("Option", null, getJPanel(), null);
        }
        return mainTabbedPane;
    }

    /**
     * This method initializes allRunsPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAllRunsPane() {
        if (allRunsPane == null) {
            allRunsPane = new JPanel();
            allRunsPane.setLayout(new BorderLayout());
            allRunsPane.add(getScrollPane(), java.awt.BorderLayout.NORTH);
            allRunsPane.add(getRunListBox(), java.awt.BorderLayout.CENTER);
        }
        return allRunsPane;
    }

    /**
     * This method initializes scrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
        }
        return scrollPane;
    }

    /**
     * This method initializes runListBox
     * 
     * @return javax.swing.JList
     */
    private JList getRunListBox() {
        if (runListBox == null) {
            runListBox = new JList();
            runListBox.setModel(runListModel);
        }
        return runListBox;
    }

    /**
     * This method initializes runListModel1
     * 
     * @return javax.swing.DefaultListModel
     */
    private DefaultListModel getRunListModel() {
        if (runListModel == null) {
            runListModel = new DefaultListModel();
        }
        return runListModel;
    }

    private void setFrameTitle(final boolean contestStarted) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (contestStarted) {
                    setTitle("PC^2 Judge " + model.getTitle() + " [STARTED] Build " + new VersionInfo().getBuildNumber());
                } else {
                    setTitle("PC^2 Judge " + model.getTitle() + " [STOPPED] Build " + new VersionInfo().getBuildNumber());
                }
            }
        });
        FrameUtilities.regularCursor(this);
    }

    private boolean isThisSite(int siteNumber) {
        return siteNumber == model.getSiteNumber();
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
                setFrameTitle(event.getContestTime().isContestRunning());
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
                setFrameTitle(event.getContestTime().isContestRunning());
            }
        }

        public void contestStopped(ContestTimeEvent event) {
            updateListBox("ContestTime site " + event.getSiteNumber() + " STOPPED " + event.getContestTime().getElapsedTimeStr());
            if (isThisSite(event.getSiteNumber())) {
                setFrameTitle(event.getContestTime().isContestRunning());
            }
        }
    }

    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.controller = inController;

        model.addRunListener(new RunListenerImplementation());
        model.addContestTimeListener(new ContestTimeListenerImplementation());
        // TODO add langauge and problem listeners
        // model.addLanguageListener(new LanguageListenerImplementation());
        // model.addProblemListener(new ProblemListenerImplementation());

        // TODO add listeners for accounts, login and site.

        // model.addAccountListener(new AccountListenerImplementation());
        // model.addLoginListener(new LoginListenerImplementation());
        // model.addSiteListener(new SiteListenerImplementation());

        setFrameTitle(model.getContestTime().isContestRunning());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI();
            }
        });
    }

    private void populateGUI() {

        for (Run run : model.getRuns()) {
            updateListBox("Existing run " + run);
        }

        updateListBox("Contest Time elapsed: " + model.getContestTime().getElapsedTimeStr());

    }

    public String getPluginTitle() {
        return "Judge Main GUI";
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.add(getShowLogWindowCheckBox(), null);
        }
        return jPanel;
    }

    /**
     * This method initializes jCheckBox
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

    protected void showLog(boolean showLogWindow) {
        logWindow.setVisible(showLogWindow);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
