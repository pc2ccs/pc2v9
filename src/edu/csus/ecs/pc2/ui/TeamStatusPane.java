package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IClarificationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ILoginListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.SiteEvent;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * Team Status Pane.
 * 
 * This presents the user with a grid of all local teams and whether the team has logged in, submitted a clarification, submitted a run or submitted a run and clarification by color.
 * <P>
 * There is a legend at the top which shows the color of each state of submission or login.
 * <P>
 * If one hovers over a team name will display the number of clarifications and runs that the team has submitted.
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

    public static final java.awt.Color HAS_LOGGED_IN_COLOR = java.awt.Color.pink;  //  @jve:decl-index=0:

    public static final java.awt.Color HAS_SUBMITTED_RUNS_ONLY_COLOR = java.awt.Color.blue;

    public static final java.awt.Color HAS_SUBMITTED_CLARS_ONLY_COLOR = java.awt.Color.orange;

    public static final java.awt.Color HAS_SUBMITTED_RUNS_AND_CLARS_COLOR = java.awt.Color.green;  //  @jve:decl-index=0:

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

    private JButton reloadButton = null;

    private JComboBox<Site> siteComboBox = null;

    private Site allSitesSite = new Site("All Sites", 0);
    
    // TODO fix when switching from all to one site, residue on screen.

    /**
     * Is the GUI being populated?.
     * 
     * This avoids a loop that happens on the combo box item changed
     * invocation of populateGUI.
     */
    private boolean populatingGUI = false;

    private JCheckBox showTeamsCheckBox = null;

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

    private void populateGUI() {
        
        if (populatingGUI){
            return;
        }
        populatingGUI = true;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    repopulateSitePulldown();
                    repopulateGrid(true);
                } catch (Exception e) {
                    getController().getLog().log(Log.WARNING, "Exception logged ", e);
                }
                populatingGUI = false;
            }
        });
    }

    protected void repopulateSitePulldown() {

        int selectedIndex = getSiteComboBox().getSelectedIndex();

        getSiteComboBox().removeAllItems();

        Site[] sites = getContest().getSites();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());

        getSiteComboBox().addItem(allSitesSite);

        for (Site site : sites) {
            getSiteComboBox().addItem(site);
        }

        if (selectedIndex > -1) {
            getSiteComboBox().setSelectedIndex(selectedIndex);
        } else {
            getSiteComboBox().setSelectedIndex(0);
        }
    }

    /**
     * Repopulate Grid, optionally populate with info.
     * 
     * @param populate update status'
     */
    private void repopulateGrid(boolean populate) {

        boolean allSites = false;

        Site site = null;
        int countOfSites = getContest().getSites().length;

        if (getSiteComboBox().getSelectedIndex() < 1) {
            allSites = true;
        } else {
            site = (Site) getSiteComboBox().getSelectedItem();
        }

        getCenterPane().removeAll();
        GridLayout gridLayout = new GridLayout();
        gridLayout.setColumns(8);
        gridLayout.setRows(0);
        gridLayout.setVgap(5);
        gridLayout.setHgap(5);
        centerPane.setLayout(gridLayout);

        Vector<Account> vectorAccounts;
        if (allSites) {
            vectorAccounts = getContest().getAccounts(ClientType.Type.TEAM);

        } else {
            vectorAccounts = getContest().getAccounts(ClientType.Type.TEAM, site.getSiteNumber());

        }
        Account[] accounts = (Account[]) vectorAccounts.toArray(new Account[vectorAccounts.size()]);
        Arrays.sort(accounts, new AccountComparator());
        
        boolean showTeamsOnBoardOnly = getShowTeamsCheckBox().isSelected();

        for (Account account : accounts) {
            
            if (showTeamsOnBoardOnly) {
                if (!account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD)) {
                    continue;
                }
                if (!account.isAllowed(Permission.Type.LOGIN)) {
                    continue;
                }
            }
            
            JLabel teamLabel = new JLabel();
            ClientId clientId = account.getClientId();
            String teamName = clientId.getName();
            if (allSites && countOfSites > 1){
                teamName = "S"+clientId.getSiteNumber()+" "+teamName;
            }
            String toolTipText = "No submissions";

            Color teamStatusColor = NO_CONTACT_COLOR;

            if (populate) {

                toolTipText = teamName;

                Run[] runs = getContest().getRuns(clientId);
                if (runs.length > 0) {
                    toolTipText = toolTipText + " " + runs.length + " runs";
                }
                Clarification[] clarifications = getContest().getClarifications(clientId);
                ArrayList<Clarification> myClarList = new ArrayList<Clarification>();
                if (clarifications.length > 0) {
                    for (int i = 0; i < clarifications.length; i++) {
                        Clarification clarification = clarifications[i];
                        if (clarification.getSubmitter().equals(clientId)) { // did I submit it?
                            myClarList.add(clarification);
                        }
                    }
                    clarifications = myClarList.toArray(new Clarification[myClarList.size()]);
                    if (clarifications.length > 0) {
                        toolTipText = toolTipText + " " + clarifications.length + " clarifications ";
                    }
                }

                teamStatusColor = getStatusColor(clientId, runs, clarifications);
            }

            toolTipText = toolTipText + " (" + account.getDisplayName() + ")";
            
            teamLabel.setText(teamName);
            teamLabel.setForeground(teamStatusColor);
            teamLabel.setToolTipText(toolTipText);
            centerPane.add(teamName, teamLabel);
        }
        centerPane.repaint();
    }

    private Color getStatusColor(ClientId clientId, Run[] runs, Clarification[] clarifications) {
        Color outColor = NO_CONTACT_COLOR;

        if (runs.length > 0 && clarifications.length > 0) {
            outColor = HAS_SUBMITTED_RUNS_AND_CLARS_COLOR;
        } else if (runs.length > 0) {
            outColor = HAS_SUBMITTED_RUNS_ONLY_COLOR;
        } else if (clarifications.length > 0) {
            outColor = HAS_SUBMITTED_CLARS_ONLY_COLOR;
        } else if (hasLoggedIn(clientId)) {
            outColor = HAS_LOGGED_IN_COLOR;
        }

        return outColor;
    }

    /**
     * Is client logged in ?
     * @param clientId
     * @return
     */
    private boolean hasLoggedIn(ClientId clientId) {
        
        if (getContest().isLocalLoggedIn(clientId) || getContest().isRemoteLoggedIn(clientId)){
            return true;
        }

        ClientSettings settings = getContest().getClientSettings(clientId);
        if (settings != null) {
            return settings.getProperty(ClientSettings.LOGIN_DATE) != null;
        }
        
        return false;
    }


    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        populateGUI();

        getContest().addAccountListener(new AccountListenerImplementation());
        getContest().addLoginListener(new LoginListenerImplementation());
        getContest().addRunListener(new RunListenerImplementation());
        getContest().addClarificationListener(new ClarificationListenerImplementation());
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    public class SiteListenerImplementation implements ISiteListener{

        public void siteProfileStatusChanged(SiteEvent event) {
            // TODO this UI does not use a change in profile status 
        }
        
        public void siteAdded(SiteEvent event) {
            populateGUI();
        }

        public void siteRemoved(SiteEvent event) {
            populateGUI();
        }

        public void siteChanged(SiteEvent event) {
            populateGUI();
        }

        public void siteLoggedOn(SiteEvent event) {
            // nada
            
        }

        public void siteLoggedOff(SiteEvent event) {
            // nada
            
        }

        public void sitesRefreshAll(SiteEvent siteEvent) {
            populateGUI();
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    public class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            populateGUI();
        }

        public void refreshRuns(RunEvent event) {
            populateGUI();
        }
        
        public void runChanged(RunEvent event) {
            populateGUI();
        }

        public void runRemoved(RunEvent event) {
            populateGUI();
        }

    }

    /**
     * What to when accounts change
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            populateGUI();
        }

        public void accountModified(AccountEvent event) {
            // ignore, does not affect this pane
        }

        public void accountsAdded(AccountEvent accountEvent) {
            populateGUI();
        }

        public void accountsModified(AccountEvent accountEvent) {
            // ignore, does not affect this pane
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {
            populateGUI();
        }
    }
   /**
     * 
     * @author pc2@ecs.csus.edu
     */
    private class ClarificationListenerImplementation implements IClarificationListener {

        public void clarificationAdded(ClarificationEvent event) {
            populateGUI();
        }
        
        public void refreshClarfications(ClarificationEvent event) {
            populateGUI();
        }

        public void clarificationChanged(ClarificationEvent event) {
            populateGUI();
        }

        public void clarificationRemoved(ClarificationEvent event) {
            populateGUI();
        }

    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    public class LoginListenerImplementation implements ILoginListener {

        public void loginAdded(LoginEvent event) {
            populateGUI();
        }

        public void loginRemoved(final LoginEvent event) {
            populateGUI();
        }

        public void loginDenied(LoginEvent event) {
            populateGUI();
        }
        
        public void loginRefreshAll(LoginEvent event) {
            populateGUI();
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
            FlowLayout flowLayout1 = new FlowLayout();
            flowLayout1.setHgap(25);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout1);
            buttonPane.add(getClearButton(), null);
            buttonPane.add(getReloadButton(), null);
            buttonPane.add(getSiteComboBox(), null);
            buttonPane.add(getShowTeamsCheckBox(), null);
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
            clearButton.setToolTipText("Clear display");
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
                repopulateGrid(false);
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

    /**
     * This method initializes reloadButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getReloadButton() {
        if (reloadButton == null) {
            reloadButton = new JButton();
            reloadButton.setText("Reload");
            reloadButton.setToolTipText("Reload display");
            reloadButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    populateGUI();
                }
            });
        }
        return reloadButton;
    }

    /**
     * This method initializes siteComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox<Site> getSiteComboBox() {
        if (siteComboBox == null) {
            siteComboBox = new JComboBox<Site>();
            siteComboBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    populateGUI();
                }
            });
        }
        return siteComboBox;
    }

    /**
     * This method initializes showTeamsCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getShowTeamsCheckBox() {
        if (showTeamsCheckBox == null) {
            showTeamsCheckBox = new JCheckBox();
            showTeamsCheckBox.setText("Show enabled teams");
            showTeamsCheckBox.setToolTipText("Show only teams who can login and are on the scoreboard");
            showTeamsCheckBox.setSelected(true);
            showTeamsCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    populateGUI();
                }
            });
        }
        return showTeamsCheckBox;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
