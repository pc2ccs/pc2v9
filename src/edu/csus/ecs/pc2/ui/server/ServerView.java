package edu.csus.ecs.pc2.ui.server;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.ILoginListener;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.ISiteListener;
import edu.csus.ecs.pc2.core.model.InternalDump;
import edu.csus.ecs.pc2.core.model.LoginEvent;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.SiteEvent;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.IntegerDocument;
import edu.csus.ecs.pc2.ui.LogWindow;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * GUI for Server.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ServerView extends JFrame implements UIPlugin {

    public static final String SVN_ID = "$Id$";

    private IModel model = null;

    // TODO remove @SuppressWarnings for serverController
    @SuppressWarnings("unused")
    private IController serverController = null;

    /**
     * 
     */
    private static final long serialVersionUID = 4547574494017009634L;

    private JPanel mainViewPane = null;

    private JPanel runPane = null;

    private JScrollPane runScrollPane = null;

    private JList runJList = null;

    private DefaultListModel runListModel = new DefaultListModel();

    private JTabbedPane mainTabbedPane = null;

    private JPanel generateAccountsPane = null;

    private JTextField judgeCountTextField = null;

    private JPanel genButtonPane = null;

    private JButton genButton = null;

    private JTextField teamCountTextField = null;

    private JTextField boardCountTextField = null;

    private JTextField adminCountTextField = null;

    private JPanel centerPane2 = null;

    private JLabel genAdminLabel = null;

    private JLabel genJudgeLabel = null;

    private JLabel genTeamLabels = null;

    private JLabel genScoreboardLabel = null;

    private JPanel sitePane = null;

    private JLabel genSiteLabel = null;

    private JTextField sitesCountTextBox = null;

    private JButton generateSitesAccountButton = null;

    private JPanel buttonPane = null;

    private JButton viewReportButton = null;

    private JTextField editorCommandTextField = null;

    private JLabel editorCommandLabel = null;

    private JPanel optionsPanel = null;

    private JCheckBox showLogWindowCheckBox = null;

    private LogWindow logWindow = null;
    
    /**
     * This method initializes
     * 
     */
    public ServerView() {
        super();
        initialize();
        updateListBox(getPluginTitle() + " Build " + new VersionInfo().getBuildNumber());

    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(518, 327));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("Server View");
        this.setContentPane(getMainViewPane());
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                promptAndExit();
            }
        });
        if (logWindow == null) {
            logWindow = new LogWindow();
        }
        setVisible(true);

        FrameUtilities.centerFrameTop(this);
    }

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog("Are you sure you want to exit PC^2?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.err.println("Server halting");
            System.exit(0);
        }
    }

    private void updateListBox(final String messageString) {
        Runnable messageRunnable = new Runnable() {
            public void run() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:MM:ss");
                String dateString = simpleDateFormat.format(new Date());

                runListModel.insertElementAt(dateString + " " + messageString, 0);
                System.out.println("debug Box: " + messageString);
            }
        };
        SwingUtilities.invokeLater(messageRunnable);
    }

    /**
     * Implementation for the Run Listener.
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    private class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            updateListBox(event.getRun() + " ADDED ");
        }

        public void runChanged(RunEvent event) {
            updateListBox(event.getRun() + " CHANGED ");
        }

        public void runRemoved(RunEvent event) {
            updateListBox(event.getRun() + " REMOVED ");
        }
    }

    private String accountText(Account account) {
        if (account == null) {
            return "null";
        } else {
            return account.getClientId().toString();
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    public class LoginListenerImplementation implements ILoginListener {

        public void loginAdded(LoginEvent event) {
            updateListBox("Login " + event.getAction() + " " + event.getClientId());
        }

        public void loginRemoved(LoginEvent event) {
            updateListBox("Login " + event.getAction() + " " + event.getClientId());
        }

        public void loginDenied(LoginEvent event) {
            updateListBox("Login " + event.getAction() + " " + event.getClientId());
        }
    }

    /**
     * Implementation for a Account Listener.
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            updateListBox("Account " + accountEvent.getAction() + " " + accountText(accountEvent.getAccount()));

        }

        public void accountModified(AccountEvent accountEvent) {
            updateListBox("Account " + accountEvent.getAction() + " " + accountText(accountEvent.getAccount()));

        }
    }

    /**
     * Site Listener for use by ServerView.
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    public class SiteListenerImplementation implements ISiteListener {

        public void siteAdded(SiteEvent event) {
            updateListBox("Site " + event.getAction() + " " + event.getSite());
            updateGenerateTitles();
        }

        public void siteRemoved(SiteEvent event) {
            updateListBox("Site " + event.getAction() + " " + event.getSite());
            updateGenerateTitles();
        }

        public void siteLoggedOn(SiteEvent event) {
            updateListBox("Site " + event.getAction() + " " + event.getSite());
        }

        public void siteLoggedOff(SiteEvent event) {
            updateListBox("Site " + event.getAction() + " " + event.getSite());
        }

        public void siteChanged(SiteEvent event) {
            updateListBox("Site " + event.getAction() + " " + event.getSite());
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
     * This method initializes runPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getRunPane() {
        if (runPane == null) {
            runPane = new JPanel();
            runPane.setLayout(new BorderLayout());
            runPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Runs", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
                    null));
            runPane.add(getRunScrollPane(), java.awt.BorderLayout.CENTER);
            runPane.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        }
        return runPane;
    }

    /**
     * This method initializes runScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getRunScrollPane() {
        if (runScrollPane == null) {
            runScrollPane = new JScrollPane();
            runScrollPane.setViewportView(getRunJList());
        }
        return runScrollPane;
    }

    /**
     * This method initializes runJList
     * 
     * @return javax.swing.JList
     */
    private JList getRunJList() {
        if (runJList == null) {
            runJList = new JList(runListModel);
        }
        return runJList;
    }

    /**
     * Puts this frame to right of input frame.
     * 
     * @param sourceFrame
     */
    public void windowToRight(JFrame sourceFrame) {
        int rightX = sourceFrame.getX() + sourceFrame.getWidth();
        setLocation(rightX, getY());
    }

    /**
     * This method initializes mainTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getMainTabbedPane() {
        if (mainTabbedPane == null) {
            mainTabbedPane = new JTabbedPane();
            mainTabbedPane.addTab("Runs Submitted", null, getRunPane(), null);
            mainTabbedPane.addTab("Generate Accounts", null, getGenerateAccountsPane(), null);
            mainTabbedPane.addTab("Sites", null, getSitePane(), null);
            mainTabbedPane.addTab("Options", null, getOptionsPanel(), null);
        }
        return mainTabbedPane;
    }

    /**
     * This method initializes generateAccountsPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGenerateAccountsPane() {
        if (generateAccountsPane == null) {
            generateAccountsPane = new JPanel();
            generateAccountsPane.setLayout(new BorderLayout());
            generateAccountsPane.add(getGenButtonPane(), java.awt.BorderLayout.SOUTH);
            generateAccountsPane.add(getCenterPane2(), java.awt.BorderLayout.CENTER);
        }
        return generateAccountsPane;
    }

    /**
     * This method initializes adminAccountNumber
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJudgeCountTextField() {
        if (judgeCountTextField == null) {
            judgeCountTextField = new JTextField();
            judgeCountTextField.setBounds(new java.awt.Rectangle(356, 65, 52, 31));
            judgeCountTextField.setDocument(new IntegerDocument());
        }
        return judgeCountTextField;
    }

    /**
     * This method initializes genButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGenButtonPane() {
        if (genButtonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setAlignment(java.awt.FlowLayout.RIGHT);
            genButtonPane = new JPanel();
            genButtonPane.setLayout(flowLayout);
            genButtonPane.add(getGenButton(), null);
        }
        return genButtonPane;
    }

    /**
     * This method initializes genButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getGenButton() {
        if (genButton == null) {
            genButton = new JButton();
            genButton.setText("Generate");
            genButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {

                    generateAccounts();
                }
            });
        }
        return genButton;
    }

    private int getIntegerValue(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    protected void generateAccounts() {

        try {
            int count = getIntegerValue(adminCountTextField.getText());
            if (count > 0) {
                model.generateNewAccounts(ClientType.Type.ADMINISTRATOR.toString(), count, true);
            }

            count = getIntegerValue(judgeCountTextField.getText());
            if (count > 0) {
                model.generateNewAccounts(ClientType.Type.JUDGE.toString(), count, true);
            }

            count = getIntegerValue(teamCountTextField.getText());
            if (count > 0) {
                model.generateNewAccounts(ClientType.Type.TEAM.toString(), count, true);
            }

            count = getIntegerValue(boardCountTextField.getText());
            if (count > 0) {
                model.generateNewAccounts(ClientType.Type.SCOREBOARD.toString(), count, true);
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        updateGenerateTitles();
    }

    private void updateGenerateTitles() {

        // Update the Number of accounts

        int number = model.getAccounts(ClientType.Type.SCOREBOARD).size();
        genScoreboardLabel.setText("Scoreboards (" + number + ")");

        number = model.getAccounts(ClientType.Type.TEAM).size();
        genTeamLabels.setText("Teams (" + number + ")");

        number = model.getAccounts(ClientType.Type.JUDGE).size();
        genJudgeLabel.setText("Judges (" + number + ")");

        number = model.getAccounts(ClientType.Type.ADMINISTRATOR).size();
        genAdminLabel.setText("Administrators (" + number + ")");

        number = model.getSites().length;
        genSiteLabel.setText("Sites (" + number + ")");
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTeamCountTextField() {
        if (teamCountTextField == null) {
            teamCountTextField = new JTextField();
            teamCountTextField.setBounds(new java.awt.Rectangle(356, 115, 52, 31));
            teamCountTextField.setDocument(new IntegerDocument());
        }
        return teamCountTextField;
    }

    /**
     * This method initializes jTextField1
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getBoardCountTextField() {
        if (boardCountTextField == null) {
            boardCountTextField = new JTextField();
            boardCountTextField.setBounds(new java.awt.Rectangle(356, 165, 52, 31));
            boardCountTextField.setDocument(new IntegerDocument());
        }
        return boardCountTextField;
    }

    /**
     * This method initializes jTextField3
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getAdminCountTextField() {
        if (adminCountTextField == null) {
            adminCountTextField = new JTextField();
            adminCountTextField.setBounds(new java.awt.Rectangle(356, 15, 52, 31));
            adminCountTextField.setDocument(new IntegerDocument());
        }
        return adminCountTextField;
    }

    /**
     * This method initializes centerPane2
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane2() {
        if (centerPane2 == null) {
            genScoreboardLabel = new JLabel();
            genScoreboardLabel.setBounds(new java.awt.Rectangle(62, 165, 257, 31));
            genScoreboardLabel.setText("Scoreboards");
            genTeamLabels = new JLabel();
            genTeamLabels.setBounds(new java.awt.Rectangle(62, 115, 257, 31));
            genTeamLabels.setText("Teams");
            genJudgeLabel = new JLabel();
            genJudgeLabel.setBounds(new java.awt.Rectangle(62, 65, 257, 31));
            genJudgeLabel.setText("Judges");
            genAdminLabel = new JLabel();
            genAdminLabel.setBounds(new java.awt.Rectangle(62, 15, 257, 31));
            genAdminLabel.setText("Administrators");
            centerPane2 = new JPanel();
            centerPane2.setLayout(null);
            centerPane2.add(getAdminCountTextField(), null);
            centerPane2.add(getBoardCountTextField(), null);
            centerPane2.add(getTeamCountTextField(), null);
            centerPane2.add(getJudgeCountTextField(), null);
            centerPane2.add(genAdminLabel, null);
            centerPane2.add(genJudgeLabel, null);
            centerPane2.add(genTeamLabels, null);
            centerPane2.add(genScoreboardLabel, null);
        }
        return centerPane2;
    }

    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.serverController = inController;
        setTitle("PC^2 Server (Site " + model.getSiteNumber() + ")");
        updateGenerateTitles();

        model.addRunListener(new RunListenerImplementation());
        model.addAccountListener(new AccountListenerImplementation());
        model.addLoginListener(new LoginListenerImplementation());
        model.addSiteListener(new SiteListenerImplementation());

        // TODO add listeners for languages and problems

        // model.addLanguageListener(new LanguageListenerImplementation());
        // model.addProblemListener(new ProblemListenerImplementation());
    }

    public String getPluginTitle() {
        return "Server Main GUI";
    }

    /**
     * This method initializes sitePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSitePane() {
        if (sitePane == null) {
            genSiteLabel = new JLabel();
            genSiteLabel.setBounds(new java.awt.Rectangle(51, 46, 147, 20));
            genSiteLabel.setText("Sites");
            sitePane = new JPanel();
            sitePane.setLayout(null);
            sitePane.add(genSiteLabel, null);
            sitePane.add(getSitesCountTextBox(), null);
            sitePane.add(getGenerateSitesAccountButton(), null);
        }
        return sitePane;
    }

    /**
     * This method initializes sitesCountTextBox
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getSitesCountTextBox() {
        if (sitesCountTextBox == null) {
            sitesCountTextBox = new JTextField();
            sitesCountTextBox.setBounds(new java.awt.Rectangle(249, 42, 61, 24));
            sitesCountTextBox.setDocument(new IntegerDocument());
        }
        return sitesCountTextBox;
    }

    /**
     * This method initializes generateSitesAccountButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getGenerateSitesAccountButton() {
        if (generateSitesAccountButton == null) {
            generateSitesAccountButton = new JButton();
            generateSitesAccountButton.setBounds(new java.awt.Rectangle(361, 41, 93, 25));
            generateSitesAccountButton.setText("Generate");
            generateSitesAccountButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    generateSiteAccounts();
                }
            });
        }
        return generateSitesAccountButton;
    }

    private Site createSite(int nextSiteNumber) {
        Site site = new Site("Site " + nextSiteNumber, nextSiteNumber);
        Properties props = new Properties();
        props.put(Site.IP_KEY, "localhost");
        int port = 50002 + (nextSiteNumber - 1) * 1000;
        props.put(Site.PORT_KEY, "" + port);
        site.setConnectionInfo(props);
        site.setPassword("site" + nextSiteNumber);
        return site;
    }

    protected void generateSiteAccounts() {
        try {
            int count = getIntegerValue(sitesCountTextBox.getText());
            if (count > 0) {
                int numSites = model.getSites().length;
                for (int i = 0; i < count; i++) {
                    int nextSiteNumber = i + 1 + numSites;
                    Site site = createSite(nextSiteNumber);
                    serverController.addNewSite(site);
                }

                updateGenerateTitles();
            }
        } catch (Exception e) {
            // TODO: log handle exception
            StaticLog.log("Exception logged ", e);
        }
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            editorCommandLabel = new JLabel();
            editorCommandLabel.setBounds(new java.awt.Rectangle(21, 10, 102, 23));
            editorCommandLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            editorCommandLabel.setText("View Command");
            buttonPane = new JPanel();
            buttonPane.setLayout(null);
            buttonPane.setPreferredSize(new java.awt.Dimension(45, 45));
            buttonPane.add(getViewReportButton(), null);
            buttonPane.add(getEditorCommandTextField(), null);
            buttonPane.add(editorCommandLabel, null);
        }
        return buttonPane;
    }

    /**
     * This method initializes viewReportButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getViewReportButton() {
        if (viewReportButton == null) {
            viewReportButton = new JButton();
            viewReportButton.setBounds(new java.awt.Rectangle(347, 8, 124, 24));
            viewReportButton.setText("View Dump");
            viewReportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    viewDumpFile();
                }
            });
        }
        return viewReportButton;
    }

    protected void viewDumpFile() {
        InternalDump internalDump = new InternalDump(model);
        internalDump.setEditorNameFullPath(getEditorCommandTextField().getText());
        internalDump.viewContestData();
    }

    /**
     * This method initializes editorCommandTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getEditorCommandTextField() {
        if (editorCommandTextField == null) {
            editorCommandTextField = new JTextField();
            editorCommandTextField.setBounds(new java.awt.Rectangle(144, 10, 182, 21));
            editorCommandTextField.setText("/windows/vi.bat");

            editorCommandTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        viewDumpFile();
                    }
                }
            });
        }
        return editorCommandTextField;
    }

    /**
     * This method initializes optionsPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getOptionsPanel() {
        if (optionsPanel == null) {
            optionsPanel = new JPanel();
            optionsPanel.add(getShowLogWindowCheckBox(), null);
        }
        return optionsPanel;
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

    protected void showLog(boolean showLogWindow) {
       logWindow.setVisible(showLogWindow);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
