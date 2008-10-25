package edu.csus.ecs.pc2.api;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.api.apireports.PrintClarifications;
import edu.csus.ecs.pc2.api.apireports.PrintMyClient;
import edu.csus.ecs.pc2.api.apireports.PrintProblems;
import edu.csus.ecs.pc2.api.apireports.PrintRuns;
import edu.csus.ecs.pc2.api.apireports.PrintStandings;
import edu.csus.ecs.pc2.api.apireports.PrintTeams;
import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
import edu.csus.ecs.pc2.api.listener.ContestEvent;
import edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener;
import edu.csus.ecs.pc2.api.listener.IRunEventListener;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.IntegerDocument;

/**
 * API 'contest' Test Frame.
 * 
 * Shows output of 'get' and 'is' methods for API Contest and
 * ServerConnection classes.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestTestFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = -3146831894495294017L;

    private JPanel mainPain = null;

    private JPanel centerPane = null;

    private JPanel buttonPane = null;

    private JButton exitButton = null;

    private JButton testRunButton = null;

    private JButton loginButton = null;

    private JTextField loginTextField = null;

    private JTextField passwordTextField = null;

    private ServerConnection serverConnection = new ServerConnection();

    private IContest contest = null;

    private JButton listTeamsButton = null;

    private JButton logoffButton = null;

    private JCheckBox runListenerCheckBox = null;

    private JCheckBox configListenerCheckBox = null;

    private RunListener runListener = null;

    private ScrollyFrame scrollyFrame = new ScrollyFrame();

    private String line = "";

    private JButton standingsButton = null;

    private JButton printAllButton = null;

    private JButton clearButton = null;

    private JButton oneRunTest = null;

    private JButton showClarsButton = null;

    private JCheckBox clarListenerCheckBox = null;

    private ClarificationListener clarificationListener = null;
    
    private ConfigurationUpdateListener configurationUpdateListener = null;

    private JButton runContestButton = null;

    private JScrollPane reportScrollPane = null;

    private JList reportJList = null;

    private DefaultListModel listModel = new DefaultListModel();

    private JPanel topPane = null;

    private JLabel loginLabel = null;

    private JLabel passwordLabel = null;

    private JPanel eastPane = null;

    private JPanel mainPanel = null;

    private JTextField numberTextField = null;

    private JLabel jLabel = null;

    private JPanel eastButtonPane = null;
    
    private APIAbstractTest [] reportsList = new APIPrintReports().getReportsList();

    /**
     * This method initializes
     * 
     */
    public ContestTestFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(609,552));
        this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        this.setContentPane(getMainPain());
        this.setTitle("Contest Test Frame [NOT LOGGED IN]");

        FrameUtilities.setFramePosition(this, FrameUtilities.HorizontalPosition.LEFT, FrameUtilities.VerticalPosition.CENTER);

        loadReportList();

    }
    

    /**
     * Comparer for APIAbstractTestComparator title.
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected  class APIAbstractTestComparator implements Comparator<APIAbstractTest>, Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 2262035579714795746L;
        
        public int compare(APIAbstractTest abstractTest1, APIAbstractTest abstractTest2){
            return abstractTest1.getTitle().compareTo(abstractTest2.getTitle());
        }
    }

    /**
     * Load report list
     * 
     */
    private void loadReportList() {
        
        Arrays.sort(reportsList, new APIAbstractTestComparator());

        for (APIAbstractTest abstractTest : reportsList) {
            listModel.addElement(abstractTest);
        }
    }

    /**
     * Run Listener for ContestTestFrame.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    protected class RunListener implements IRunEventListener {

        public void runAdded(IRun run) {
            println("Run added Site " + run.getSiteNumber() + " Run " + run.getNumber() + " from " + run.getTeam().getLoginName() + " at " + run.getSubmissionTime());
        }

        public void runRemoved(IRun run) {
            println("Run removed Site " + run.getSiteNumber() + " Run " + run.getNumber() + " from " + run.getTeam().getLoginName() + " at " + run.getSubmissionTime());
        }

        public void runJudged(IRun run) {
            println("Run judged Site " + run.getSiteNumber() + " Run " + run.getNumber() + " from " + run.getTeam().getLoginName() + " at " + run.getSubmissionTime());
        }

        public void runUpdated(IRun run) {
            println("Run updated Site " + run.getSiteNumber() + " Run " + run.getNumber() + " from " + run.getTeam().getLoginName() + " at " + run.getSubmissionTime());
        }
    }

    /**
     * Clar Listener for ContestTestFrame.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class ClarificationListener implements IClarificationEventListener {

        public void clarificationAdded(IClarification clarification) {
            println("Clar added Site " + clarification.getSiteNumber() + " Clarification " + clarification.getNumber() + " from " + clarification.getTeam().getLoginName() + " at "
                    + clarification.getSubmissionTime() + " Problem " + clarification.getProblem().getName());
        }

        public void clarificationRemoved(IClarification clarification) {
            println("Clar removed Site " + clarification.getSiteNumber() + " Clarification " + clarification.getNumber() + " from " + clarification.getTeam().getLoginName() + " at "
                    + clarification.getSubmissionTime() + " Problem " + clarification.getProblem().getName());
        }

        public void clarificationAnswered(IClarification clarification) {
            println("Clar answered Site " + clarification.getSiteNumber() + " Clarification " + clarification.getNumber() + " from " + clarification.getTeam().getLoginName() + " at "
                    + clarification.getSubmissionTime() + " Problem " + clarification.getProblem().getName());
        }

        public void clarificationUpdated(IClarification clarification) {
            println("Clar updated Site " + clarification.getSiteNumber() + " Clarification " + clarification.getNumber() + " from " + clarification.getTeam().getLoginName() + " at "
                    + clarification.getSubmissionTime() + " Problem " + clarification.getProblem().getName());
        }
    }
    
    /**
     * Listener for IConfigurationUpdateListener.
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    protected class ConfigurationUpdateListener implements IConfigurationUpdateListener {

        public void configurationItemAdded(ContestEvent contestEvent) {
            println("Config item added "+contestEvent.getEventType()+" "+getConfigInfo(contestEvent));
        }

        public void configurationItemUpdated(ContestEvent contestEvent) {
            println("Config item updated "+contestEvent.getEventType()+" "+getConfigInfo(contestEvent));
        }

        public void configurationItemRemoved(ContestEvent contestEvent) {
            println("Config item removed "+contestEvent.getEventType()+" "+getConfigInfo(contestEvent));
        }
    }

    /**
     * This method initializes mainPain
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPain() {
        if (mainPain == null) {
            mainPain = new JPanel();
            mainPain.setLayout(new BorderLayout());
            mainPain.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
            mainPain.add(getMainPanel(), java.awt.BorderLayout.CENTER);
        }
        return mainPain;
    }

    public String getConfigInfo(ContestEvent contestEvent) {
        switch (contestEvent.getEventType()) {
            case CLIENT:
                return contestEvent.getClient().toString();
            case CONTEST_CLOCK:
                return "running " + contestEvent.getContestClock().isContestClockRunning();
            case CONTEST_TITLE:
                return contestEvent.getContestTitle();
            case GROUP:
                return contestEvent.getGroup().getName();
            case JUDGEMENT:
                return contestEvent.getJudgement().getName();
            case LANGUAGE:
                return contestEvent.getLanguage().getName();
            case PROBLEM:
                return contestEvent.getProblem().getName();
            case SITE:
                return contestEvent.getSite().getName();
            default:
                return "Unknown type " + contestEvent.getEventType();
        }
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            jLabel = new JLabel();
            jLabel.setBounds(new java.awt.Rectangle(28,237,138,16));
            jLabel.setText("Run/Clar Number");
            jLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            centerPane = new JPanel();
            centerPane.setLayout(null);
            centerPane.setPreferredSize(new java.awt.Dimension(350,350));
            centerPane.add(getTestRunButton(), null);
            centerPane.add(getListTeamsButton(), null);
            centerPane.add(getRunListenerCheckBox(), null);
            centerPane.add(getConfigListenerCheckBox(), null);
            centerPane.add(getStandingsButton(), null);
            centerPane.add(getPrintAllButton(), null);
            centerPane.add(getJButton2(), null);
            centerPane.add(getOneRunTest(), null);
            centerPane.add(getShowClarsButton(), null);
            centerPane.add(getClarListenerCheckBox(), null);
            centerPane.add(getNumberTextField(), null);
            centerPane.add(jLabel, null);
        }
        return centerPane;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(55);
            buttonPane = new JPanel();
            buttonPane.setPreferredSize(new java.awt.Dimension(269,35));
            buttonPane.setLayout(flowLayout);
            buttonPane.add(getExitButton(), null);
        }
        return buttonPane;
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
            exitButton.setToolTipText("Exit this program");
            exitButton.setPreferredSize(new java.awt.Dimension(90,25));
            exitButton.setMnemonic(java.awt.event.KeyEvent.VK_X);
            exitButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.exit(0);
                }
            });
        }
        return exitButton;
    }

    /**
     * This method initializes testRunButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getTestRunButton() {
        if (testRunButton == null) {
            testRunButton = new JButton();
            testRunButton.setBounds(new java.awt.Rectangle(129, 60, 94, 29));
            testRunButton.setToolTipText("Print Run information list");
            testRunButton.setText("Runs");
            testRunButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    printRunsTest();
                }
            });
        }
        return testRunButton;
    }


    protected void printRunsTest() {
        runReport(new PrintRuns());
    }

    /**
     * This method initializes loginButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoginButton() {
        if (loginButton == null) {
            loginButton = new JButton();
            loginButton.setToolTipText("Login to contest");
            loginButton.setBounds(new java.awt.Rectangle(318,8,70,26));
            loginButton.setMnemonic(java.awt.event.KeyEvent.VK_L);
            loginButton.setText("Login");
            loginButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    attachToContest();
                }
            });
        }
        return loginButton;
    }

    protected void attachToContest() {

        FrameUtilities.waitCursor(this);

        String login = getLoginTextField().getText();
        String password = getPasswordTextField().getText();
        if (password == null || password.length() == 0) {
            password = login;
        }

        try {
            info("Logging in at " + login);
            long startSecs = new Date().getTime();
            contest = serverConnection.login(login, password);
            long totalMs = new Date().getTime() - startSecs;
            info("Logged in at " + login + " took " + totalMs + "ms (" + (totalMs / 1000) + " seconds)");
            setTitle("Contest " + contest.getMyClient().getLoginName() + " " + contest.getSiteName());
            getLoginButton().setEnabled(false);
            scrollyFrame.setVisible(true);
            
            VersionInfo versionInfo = new VersionInfo();
            
            println("Version "+versionInfo.getVersionNumber()+" build "+versionInfo.getBuildNumber());
            runReport (new PrintMyClient());
            println(contest.getRuns().length+" runs.");
            println(contest.getClarifications().length+" clarifications");

        } catch (LoginFailureException e) {
            contest = null;
            showMessage("Unable to login " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            contest = null;
            showMessage("Unable to login " + e.getMessage());
            e.printStackTrace();
        }
        FrameUtilities.regularCursor(this);

    }

    private void info(String string) {
        System.out.println(new Date() + " " + Thread.currentThread().getName() + " " + string);
        System.out.flush();
    }

    private void showMessage(String string, String title) {
        JOptionPane.showMessageDialog(null, string, title, JOptionPane.WARNING_MESSAGE);
    }

    private void showMessage(String string) {
        showMessage(string, "Note");
    }

    /**
     * This method initializes loginTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getLoginTextField() {
        if (loginTextField == null) {
            loginTextField = new JTextField();
            loginTextField.setText("judge1");
            loginTextField.setBounds(new java.awt.Rectangle(153,11,127,20));
            loginTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        attachToContest();
                    }
                }
            });
            
        }
        return loginTextField;
    }

    /**
     * This method initializes passwordTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getPasswordTextField() {
        if (passwordTextField == null) {
            passwordTextField = new JTextField();
            passwordTextField.setBounds(new java.awt.Rectangle(153,36,127,20));
            passwordTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        attachToContest();
                    }

                }
            });
        }
        return passwordTextField;
    }

    /**
     * This method initializes listTeamsButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getListTeamsButton() {
        if (listTeamsButton == null) {
            listTeamsButton = new JButton();
            listTeamsButton.setBounds(new java.awt.Rectangle(246, 14, 94, 29));
            listTeamsButton.setToolTipText("Print teams info");
            listTeamsButton.setText("Teams");
            listTeamsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    printTeamsTest();
                }
            });
        }
        return listTeamsButton;
    }


    protected void printTeamsTest() {
        runReport(new PrintTeams());

    }

    protected void printProblemsTest() {
        runReport(new PrintProblems());
    }

    /**
     * This method initializes logoffButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLogoffButton() {
        if (logoffButton == null) {
            logoffButton = new JButton();
            logoffButton.setToolTipText("Logoff of contest");
            logoffButton.setBounds(new java.awt.Rectangle(419,8,70,26));
            logoffButton.setMnemonic(java.awt.event.KeyEvent.VK_O);
            logoffButton.setText("Logoff");
            logoffButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        logoff();
                    } catch (NotLoggedInException e1) {
                        JOptionPane.showMessageDialog(null, "unable to logoff " + e1.getMessage());
                        e1.printStackTrace();
                    }
                }
            });
        }
        return logoffButton;
    }

    protected void logoff() throws NotLoggedInException {

        if (contest == null) {
            showMessage("Not logged in");
            return;
        }

        scrollyFrame.setVisible(false);

        serverConnection.logoff();
        contest = null;
        showMessage("No longer logged in");
        this.setTitle("Contest Test Frame [NOT LOGGED IN]");
        getLoginButton().setEnabled(true);
    }

    /**
     * This method initializes runListenerCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getRunListenerCheckBox() {
        if (runListenerCheckBox == null) {
            runListenerCheckBox = new JCheckBox();
            runListenerCheckBox.setBounds(new java.awt.Rectangle(24,269,177,18));
            runListenerCheckBox.setToolTipText("Listen for run events");
            runListenerCheckBox.setText("View Run Listener");
            runListenerCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    runListenerChanged(runListenerCheckBox.isSelected());
                }
            });
        }
        return runListenerCheckBox;
    }

    /**
     * Turn run listener on and off
     * 
     * @param listenerON
     *            true add listener, false no listener.
     */
    protected void runListenerChanged(boolean listenerON) {

        if (contest == null) {
            showMessage("Not logged in");
            return;
        }

        if (listenerON) {
            // turn it on
            if (runListener == null) {
                runListener = new RunListener();
            }
            contest.addRunListener(runListener);
            println("Run Listener added");
        } else {
            if (runListener != null) {
                contest.removeRunListener(runListener);
                println("Run Listener removed");
            }
        }
    }

    /**
     * This method initializes configListenerCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getConfigListenerCheckBox() {
        if (configListenerCheckBox == null) {
            configListenerCheckBox = new JCheckBox();
            configListenerCheckBox.setBounds(new java.awt.Rectangle(24,330,177,21));
            configListenerCheckBox.setToolTipText("Listen for all configuration change events");
            configListenerCheckBox.setText("View Config Listener");
            configListenerCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    configListenerChanged(configListenerCheckBox.isSelected());
                }
            });
        }
        return configListenerCheckBox;
    }

    protected void configListenerChanged(boolean listenerON) {

        if (contest == null) {
            showMessage("Not logged in");
            return;
        }
        
        if (listenerON) {
            // turn it on
            if (configurationUpdateListener == null) {
                configurationUpdateListener = new ConfigurationUpdateListener();
            }
            contest.addContestConfigurationUpdateListener(configurationUpdateListener);
            println("Configuration Listener added");
        } else {
            if (configurationUpdateListener != null) {
                contest.removeContestConfigurationUpdateListener(configurationUpdateListener);
                println("Configuration Listener removed");
            }
        }
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStandingsButton() {
        if (standingsButton == null) {
            standingsButton = new JButton();
            standingsButton.setBounds(new java.awt.Rectangle(246, 61, 94, 29));
            standingsButton.setToolTipText("Print Standings");
            standingsButton.setText("Standings");
            standingsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    printStandingsTest();
                }
            });
        }
        return standingsButton;
    }

    protected void printStandingsTest() {
        runReport(new PrintStandings());
    }


    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getPrintAllButton() {
        if (printAllButton == null) {
            printAllButton = new JButton();
            printAllButton.setBounds(new java.awt.Rectangle(244, 106, 94, 29));
            printAllButton.setToolTipText("Print all contest info");
            printAllButton.setMnemonic(java.awt.event.KeyEvent.VK_P);
            printAllButton.setText("Print ALL");
            printAllButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    printAll();
                }
            });
        }
        return printAllButton;
    }

    protected void printAll() {
        runReport(new PrintAllReports());
    }
    
    /**
     * Print all reports.
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintAllReports extends APIAbstractTest {

        @Override
        public void printTest() {
            
            if (contest == null) {
                showMessage("Not logged in", "Can not display info");
                return;
            }

            Arrays.sort(reportsList, new APIAbstractTestComparator());
            
            int submissionNumber = getIntegerValue (getNumberTextField().getText());
            
            for (APIAbstractTest abstractTest : reportsList) {
                println("-- Report "+abstractTest.getTitle());
                abstractTest.setAPISettings(scrollyFrame, contest, serverConnection);
                abstractTest.setNumber(submissionNumber);
                abstractTest.printTest();
            }
            println();
            println();
        }
   
        @Override
        public String getTitle() {
            return "ALL Reports";
        }
    }
    
    private int getIntegerValue(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }


    /**
     * This method initializes jButton2
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton2() {
        if (clearButton == null) {
            clearButton = new JButton();
            clearButton.setBounds(new java.awt.Rectangle(247,324,94,29));
            clearButton.setToolTipText("Clear Message List");
            clearButton.setText("Clear");
            clearButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    scrollyFrame.removeAll();
                }
            });
        }
        return clearButton;
    }

    /**
     * This method initializes oneRunTest
     * 
     * @return javax.swing.JButton
     */
    private JButton getOneRunTest() {
        if (oneRunTest == null) {
            oneRunTest = new JButton();
            oneRunTest.setBounds(new java.awt.Rectangle(42,14,185,29));
            oneRunTest.setToolTipText("View all Run's Source");
            oneRunTest.setText("View All Runs Source");
            oneRunTest.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    viewOneRunSource();
                }
            });
        }
        return oneRunTest;
    }

    protected void viewOneRunSource() {
        if (contest == null) {
            showMessage("Not logged in");
            return;
        }

        if (contest.getRuns().length == 0) {
            println("No runs in system");
            return;
        }

        for (IRun run : contest.getRuns()) {

            print("Run " + run.getNumber() + " Site " + run.getSiteNumber());

            print(" @ " + run.getSubmissionTime() + " by " + run.getTeam().getLoginName());
            print(" problem: " + run.getProblem().getName());
            print(" in " + run.getLanguage().getName());

            if (run.isJudged()) {
                println("  Judgement: " + run.getJudgementName());
            } else {
                println("  Judgement: not judged yet ");
            }

            println();
            println("Getting source file name(s)");

            byte[][] contents = run.getSourceCodeFileContents();
            String[] names = run.getSourceCodeFileNames();
            for (int i = 0; i < names.length; i++) {
                String s = names[i];
                println("Name[" + i + "] " + s + " " + contents[i].length);
            }

        }

        println("done");

    }

    /**
     * This method initializes showClarsButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getShowClarsButton() {
        if (showClarsButton == null) {
            showClarsButton = new JButton();
            showClarsButton.setBounds(new java.awt.Rectangle(24, 61, 82, 29));
            showClarsButton.setToolTipText("Print clarification info list");
            showClarsButton.setText("Clars");
            showClarsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    printClarTest();
                }
            });
        }
        return showClarsButton;
    }

    protected void printClarTest() {
        runReport(new PrintClarifications());
    }

    /**
     * This method initializes clarListenerCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getClarListenerCheckBox() {
        if (clarListenerCheckBox == null) {
            clarListenerCheckBox = new JCheckBox();
            clarListenerCheckBox.setBounds(new java.awt.Rectangle(24,298,200,21));
            clarListenerCheckBox.setToolTipText("Listen for Clarification events");
            clarListenerCheckBox.setText("View Clar Listener");
            clarListenerCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    clarListenerChanged(clarListenerCheckBox.isSelected());
                }
            });
        }
        return clarListenerCheckBox;
    }

    protected void clarListenerChanged(boolean listenerON) {
        // TODO Auto-generated method stub

        if (contest == null) {
            showMessage("Not logged in");
            return;
        }

        if (listenerON) {
            // turn it on
            if (clarificationListener == null) {
                clarificationListener = new ClarificationListener();
            }
            contest.addClarificationListener(clarificationListener);
            println("Clarification Listener added");
        } else {
            if (clarificationListener != null) {
                contest.removeClarificationListener(clarificationListener);
                println("Clarification Listener removed");
            }
        }

    }

    /**
     * This method initializes runContestButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRunContestButton() {
        if (runContestButton == null) {
            runContestButton = new JButton();
            runContestButton.setText("Run");
            runContestButton.setMnemonic(java.awt.event.KeyEvent.VK_R);
            runContestButton.setToolTipText("Run Selected API method report");
            runContestButton.setPreferredSize(new java.awt.Dimension(100,25));
            runContestButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (contest == null) {
                        showMessage("Not logged in", "Can not display info");
                        return;
                    }
                    int selected = getReportJList().getSelectedIndex();
                    if (selected == -1) {
                        showMessage("No Repport Selected");
                    } else {
                        println();
                        for (Object object: getReportJList().getSelectedValues()){
                            println("-- Report "+((APIAbstractTest)object).getTitle());
                            runReport(object);
                        }
                    }
                }
            });
        }
        return runContestButton;
    }

    /**
     * This method initializes reportScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getReportScrollPane() {
        if (reportScrollPane == null) {
            reportScrollPane = new JScrollPane();
            reportScrollPane.setViewportView(getReportJList());
        }
        return reportScrollPane;
    }

    /**
     * Run the input report.
     * @param source
     */
    protected void runReport(Object source) {
        if (contest == null) {
            showMessage("Not logged in", "Can not display info");
            return;
        }

        int submissionNumber = getIntegerValue (getNumberTextField().getText());
        
        try {
            APIAbstractTest test = (APIAbstractTest) source;
            test.setAPISettings(scrollyFrame, contest, serverConnection);
            test.setNumber(submissionNumber);
            test.printTest();
        } catch (Exception e) {
            println("Exception during report "+e.getLocalizedMessage()+" "+e.getStackTrace()[0].getClassName());
            e.printStackTrace();
        }
    }
    

    /**
     * This method initializes reportJList
     * 
     * @return javax.swing.JList
     */
    private JList getReportJList() {
        if (reportJList == null) {
            reportJList = new JList(listModel);
            reportJList.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() >= 2) {
                        runReport(getReportJList().getSelectedValue());
                    }
                }
            });
        }
        return reportJList;
    }

    /**
     * This method initializes topPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTopPane() {
        if (topPane == null) {
            passwordLabel = new JLabel();
            passwordLabel.setBounds(new java.awt.Rectangle(74,38,58,16));
            passwordLabel.setText("Password");
            passwordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            loginLabel = new JLabel();
            loginLabel.setBounds(new java.awt.Rectangle(101,13,31,16));
            loginLabel.setText("Login");
            loginLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            topPane = new JPanel();
            topPane.setLayout(null);
            topPane.setPreferredSize(new java.awt.Dimension(65,65));
            topPane.add(getPasswordTextField(), null);
            topPane.add(getLoginTextField(), null);
            topPane.add(loginLabel, null);
            topPane.add(passwordLabel, null);
            topPane.add(getLoginButton(), null);
            topPane.add(getLogoffButton(), null);
        }
        return topPane;
    }

    /**
     * This method initializes eastPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getEastPane() {
        if (eastPane == null) {
            BorderLayout borderLayout = new BorderLayout();
            borderLayout.setHgap(0);
            borderLayout.setVgap(3);
            eastPane = new JPanel();
            eastPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "API Methods", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            eastPane.setLayout(borderLayout);
            eastPane.add(getReportScrollPane(), java.awt.BorderLayout.CENTER);
            eastPane.add(getEastButtonPane(), java.awt.BorderLayout.SOUTH);
        }
        return eastPane;
    }

    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(getEastPane(), java.awt.BorderLayout.CENTER);
            mainPanel.add(getTopPane(), java.awt.BorderLayout.NORTH);
            mainPanel.add(getCenterPane(), java.awt.BorderLayout.WEST);
        }
        return mainPanel;
    }

    /**
     * This method initializes numberTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getNumberTextField() {
        if (numberTextField == null) {
            numberTextField = new JTextField();
            numberTextField.setDocument(new IntegerDocument());
            numberTextField.setToolTipText("Enter a Run Number or Clar Number");
            numberTextField.setBounds(new java.awt.Rectangle(178,236,73,22));
        }
        return numberTextField;
    }

    /**
     * This method initializes eastButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getEastButtonPane() {
        if (eastButtonPane == null) {
            FlowLayout flowLayout1 = new FlowLayout();
            flowLayout1.setHgap(2);
            flowLayout1.setVgap(2);
            eastButtonPane = new JPanel();
            eastButtonPane.setLayout(flowLayout1);
            eastButtonPane.setPreferredSize(new java.awt.Dimension(110,30));
            eastButtonPane.add(getRunContestButton(), null);
        }
        return eastButtonPane;
    }

    public static void main(String[] args) {
        new ContestTestFrame().setVisible(true);
    }

    private void println() {
        String s = "";
        if (line.length() > 0) {
            s = line + s;
            line = "";
        }
        scrollyFrame.addLine(s);
    }

    private void print(String s) {
        line = line + s;

    }

    private void println(String s) {
        if (line.length() > 0) {
            s = line + s;
            line = "";
        }
        scrollyFrame.addLine(s);
    }

} // @jve:decl-index=0:visual-constraint="21,23"
