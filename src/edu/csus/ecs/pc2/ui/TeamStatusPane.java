package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IClarificationListener;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.ILoginListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import javax.swing.JButton;
import java.awt.FlowLayout;

/**
 * Team Status Pane.
 * 
 * This presents the user with a grid of all local teams
 * and whether the team has logged in, submitted a clarification,
 * submitted a run or submitted a run and clarification by color.
 * <P>
 * There is a legend at the top which shows the color of each
 * state of submission or login.
 * <P>
 * If one hovers over a team name will display the number
 * of clarifications and runs that the team has submitted.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class TeamStatusPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -7316022621929111614L;

    private JPanel messagePane = null;

    private JPanel centerPane = null;

    private JLabel messageLabel = null;

    public static final java.awt.Color NO_CONTACT_COLOR = java.awt.Color.red;

    public static final java.awt.Color HAS_LOGGED_IN_COLOR = java.awt.Color.pink;

    public static final java.awt.Color HAS_SUBMITTED_RUNS_ONLY_COLOR = java.awt.Color.blue;

    public static final java.awt.Color HAS_SUBMITTED_CLARS_ONLY_COLOR = java.awt.Color.orange;

    public static final java.awt.Color HAS_SUBMITTED_RUNS_AND_CLARS_COLOR = java.awt.Color.green;

    private JPanel teamStatusPane = null;

    private JPanel buttonPane = null;

    private JButton clearButton = null;

    private JPanel statusTitlePane = null;

    private JLabel statusTitleLabel = null;

    private JPanel stateDescriptonPane = null;

    private JLabel nocontactLabel = null;

    private JLabel hasLoggedInLabel = null;

    private JLabel submittedRunsLabel = null;

    private JLabel submittedClarsLabel = null;

    private JLabel readyLabel = null;

    /**
     * This method initializes
     * 
     */
    public TeamStatusPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(583, 193));
        this.add(getTeamStatusPane(), java.awt.BorderLayout.CENTER);
        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        FrameUtilities.centerFrameTop(this);

        nocontactLabel.setForeground(NO_CONTACT_COLOR);
        hasLoggedInLabel.setForeground(HAS_LOGGED_IN_COLOR);
        submittedRunsLabel.setForeground(HAS_SUBMITTED_RUNS_ONLY_COLOR);
        submittedClarsLabel.setForeground(HAS_SUBMITTED_CLARS_ONLY_COLOR);
        readyLabel.setForeground(HAS_SUBMITTED_RUNS_AND_CLARS_COLOR);
    }

    @Override
    public String getPluginTitle() {
        return "Teams Pane";
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
            messagePane = new JPanel();
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new java.awt.Dimension(30, 30));
            messagePane.add(messageLabel, java.awt.BorderLayout.NORTH);
        }
        return messagePane;
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setVgap(5);
            gridLayout.setHgap(5);
            gridLayout.setColumns(8);
            centerPane = new JPanel();
            centerPane.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
            centerPane.setLayout(gridLayout);
        }
        return centerPane;
    }

    private void repopulateGrid() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                repopulateGrid(getContest().getSiteNumber(), true);
            }
        });
    }

    private void repopulateGrid(int siteNumber, boolean populate) {

        getCenterPane().removeAll();
        GridLayout gridLayout = new GridLayout();
        gridLayout.setColumns(8);
        gridLayout.setRows(0);
        gridLayout.setVgap(5);
        gridLayout.setHgap(5);
        centerPane.setLayout(gridLayout);

        AccountList accountList = new AccountList();
        accountList.generateNewAccounts(ClientType.Type.TEAM, 25, 1, PasswordType.JOE, 1, true);

        Vector<Account> vectorAccounts = getContest().getAccounts(ClientType.Type.TEAM, siteNumber);
        Account[] accounts = (Account[]) vectorAccounts.toArray(new Account[vectorAccounts.size()]);
        Arrays.sort(accounts, new AccountComparator());

        for (Account account : accounts) {
            JLabel teamLabel = new JLabel();
            ClientId clientId = account.getClientId();
            String teamName = clientId.getName();
            String teamSubmittions = "No submissions";

            Color teamStatusColor = NO_CONTACT_COLOR;

            if (populate) {
                
                teamSubmittions = teamName;
                
                Run[] runs = getContest().getRuns(clientId);
                if (runs.length > 0) {
                    teamSubmittions = teamSubmittions + " " + runs.length + " runs";
                }
                Clarification[] clarifications = getContest().getClarifications(clientId);
                if (clarifications.length > 0) {
                    teamSubmittions = teamSubmittions + " " + clarifications.length + " clarifications ";
                }

                teamStatusColor = getStatusColor(clientId, runs, clarifications);
            }

            teamLabel.setText(teamName);
            teamLabel.setForeground(teamStatusColor);
            teamLabel.setToolTipText(teamSubmittions);
            centerPane.add(teamName, teamLabel);
        }
    }

    private Color getStatusColor(ClientId clientId, Run[] runs, Clarification[] clarifications) {
        Color outColor = NO_CONTACT_COLOR;

        if (runs.length > 0 && clarifications.length > 0) {
            outColor = HAS_SUBMITTED_RUNS_AND_CLARS_COLOR;
        } else if (runs.length > 0) {
            outColor = HAS_SUBMITTED_RUNS_ONLY_COLOR;
        } else if (clarifications.length > 0) {
            outColor = HAS_SUBMITTED_CLARS_ONLY_COLOR;
        } else if (getContest().isLocalLoggedIn(clientId)) {
            outColor = HAS_LOGGED_IN_COLOR;
        }

        return outColor;
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);

        repopulateGrid();

        getContest().addLoginListener(new LoginListenerImplementation());
        getContest().addRunListener(new RunListenerImplementation());
        getContest().addClarificationListener(new ClarificationListenerImplementation());
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    public class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            repopulateGrid();
        }

        public void runChanged(RunEvent event) {
            repopulateGrid();
        }

        public void runRemoved(RunEvent event) {
            repopulateGrid();
        }

    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    private class ClarificationListenerImplementation implements IClarificationListener {

        public void clarificationAdded(ClarificationEvent event) {
            repopulateGrid();
        }

        public void clarificationChanged(ClarificationEvent event) {
            repopulateGrid();
        }

        public void clarificationRemoved(ClarificationEvent event) {
            repopulateGrid();
        }

    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    public class LoginListenerImplementation implements ILoginListener {

        public void loginAdded(LoginEvent event) {
            repopulateGrid();
        }

        public void loginRemoved(final LoginEvent event) {
            repopulateGrid();
        }

        public void loginDenied(LoginEvent event) {
            repopulateGrid();
        }
    }

    /**
     * This method initializes teamStatusPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTeamStatusPane() {
        if (teamStatusPane == null) {
            teamStatusPane = new JPanel();
            teamStatusPane.setLayout(new BorderLayout());
            teamStatusPane.add(getStatusTitlePane(), java.awt.BorderLayout.NORTH);
            teamStatusPane.add(getCenterPane(), java.awt.BorderLayout.CENTER);
        }
        return teamStatusPane;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            buttonPane = new JPanel();
            buttonPane.add(getClearButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes clearButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getClearButton() {
        if (clearButton == null) {
            clearButton = new JButton();
            clearButton.setText("Clear");
            clearButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            clearButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    promptAndClearButtons();
                }
            });
        }
        return clearButton;
    }

    protected void promptAndClearButtons() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                repopulateGrid(getContest().getSiteNumber(), false);
            }
        });

    }

    /**
     * This method initializes statusTitlePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getStatusTitlePane() {
        if (statusTitlePane == null) {
            statusTitleLabel = new JLabel();
            statusTitleLabel.setText("STATUS");
            statusTitleLabel.setPreferredSize(new java.awt.Dimension(32, 22));
            statusTitleLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            statusTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            statusTitlePane = new JPanel();
            statusTitlePane.setLayout(new BorderLayout());
            statusTitlePane.setPreferredSize(new java.awt.Dimension(50, 50));
            statusTitlePane.add(statusTitleLabel, java.awt.BorderLayout.NORTH);
            statusTitlePane.add(getStateDescriptonPane(), java.awt.BorderLayout.CENTER);
        }
        return statusTitlePane;
    }

    /**
     * This method initializes stateDescriptonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getStateDescriptonPane() {
        if (stateDescriptonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(30);
            readyLabel = new JLabel();
            readyLabel.setText("READY");
            readyLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            submittedClarsLabel = new JLabel();
            submittedClarsLabel.setText("Submitted Clar(s)");
            submittedClarsLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            submittedRunsLabel = new JLabel();
            submittedRunsLabel.setText("Submitted Run(s)");
            submittedRunsLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            hasLoggedInLabel = new JLabel();
            hasLoggedInLabel.setText("Has Logged In");
            hasLoggedInLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            nocontactLabel = new JLabel();
            nocontactLabel.setText("No Contact");
            nocontactLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            stateDescriptonPane = new JPanel();
            stateDescriptonPane.setLayout(flowLayout);
            stateDescriptonPane.add(nocontactLabel, null);
            stateDescriptonPane.add(hasLoggedInLabel, null);
            stateDescriptonPane.add(submittedRunsLabel, null);
            stateDescriptonPane.add(submittedClarsLabel, null);
            stateDescriptonPane.add(readyLabel, null);
        }
        return stateDescriptonPane;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
