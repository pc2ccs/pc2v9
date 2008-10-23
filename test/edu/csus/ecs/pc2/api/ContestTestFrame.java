package edu.csus.ecs.pc2.api;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
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

import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
import edu.csus.ecs.pc2.api.listener.ContestEvent;
import edu.csus.ecs.pc2.api.listener.IConfigurationUpdateListener;
import edu.csus.ecs.pc2.api.listener.IRunEventListener;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.IntegerDocument;

/**
 * API Contest Test Frame.
 * 
 * Used to test the API functions, shows events and such.
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

    private APIAbstractTest[] reportsList = { 
            new  PrintRuns (),
            new PrintRun(),
            new  PrintMyClient (),
            new  PrintSites (),
            new  PrintClockInfo (),
            new  PrintJudgements (),
            new  PrintContestTitle (),
            new  PrintTeams (),
            new  PrintProblems (),
            new  PrintLanguages(),
            new  PrintStandings (),
            new  PrintClarifications (),
            new  PrintContestRunning(),
            new  PrintSiteName(),
            new  PrintGroups(),
            new  PrintLocalContactInfo(),
            new PrintMyClientSC(),
            new PrintGetContestSC(),
            new PrintLoggedInSC(),
            new PrintMyClientSC(),
    };

    private JPanel topPane = null;

    private JLabel loginLabel = null;

    private JLabel passwordLabel = null;

    private JPanel eastPane = null;

    private JPanel mainPanel = null;

    private JTextField numberTextField = null;

    private JLabel jLabel = null;

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
        this.setSize(new java.awt.Dimension(609,493));
        this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        this.setContentPane(getMainPain());
        this.setTitle("Contest Test Frame [NOT LOGGED IN]");

        FrameUtilities.setFramePosition(this, FrameUtilities.HorizontalPosition.LEFT, FrameUtilities.VerticalPosition.CENTER);

        loadReportList();

    }

    /**
     * Load report list
     * 
     */
    private void loadReportList() {

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
     * 
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
            jLabel.setBounds(new java.awt.Rectangle(28,171,138,16));
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
            buttonPane.setPreferredSize(new java.awt.Dimension(269, 42));
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

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    protected class PrintRun extends APIAbstractTest {

        @Override
        public void printTest() {

            int runNumber = getNumber();
            if (runNumber < 1) {
                println("No run number " + runNumber + " submitted/exists");
            } else {
                int siteNum = getSiteNumber();
                if (siteNum == 0) {
                    siteNum = contest.getMyClient().getSiteNumber();
                }
                boolean foundRun = false;

                for (IRun run : contest.getRuns()) {
                    if (run.getNumber() == runNumber && run.getSiteNumber() == siteNum) {
                        foundRun = true;
                        print("   Site " + run.getSiteNumber());
                        print(" Run " + run.getNumber());
                        print(", " + run.getProblem().getName());
                        print(", " + run.getLanguage().getName());
                        print(", del=" + run.isDeleted());
                        print(", judged=" + run.isJudged());
                        print(", solved=" + run.isSolved());
                        println();
                        if (run.isJudged()) {
                            for (IRunJudgement runJudgement : run.getRunJudgements()) {
                                println("     " + run.getJudgementName());
                                print("     ");
                                if (runJudgement.isActive()) {
                                    print("active");
                                } else {
                                    print("      ");
                                }
                                print(" " + runJudgement.getJudgement().getName());
                                print(", sendToTeam=" + runJudgement.isSendToTeam());
                                print(", computerJudged=" + runJudgement.isComputerJudgement());
                                println();
                            }

                        } else {
                            print("     ");
                            println("Run not judged.");
                        }
                        break;
                    }
                } // for IRun
                
                if (! foundRun){
                    println("No run number " + runNumber + " submitted/exists");
                }
            }
        }

        @Override
        String getTitle() {
            return "getRun";
        }
    }
    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintRuns extends APIAbstractTest {

        @Override
        public void printTest() {

            if (contest.getRuns().length == 0) {
                showMessage("No runs in system");
                return;
            }

            IRun[] runs = contest.getRuns();
            println("There are " + runs.length + " runs.");

            for (IRun run : runs) {

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
            }
        }

        @Override
        String getTitle() {
            return "getRuns, IRun, etc.";
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintMyClient extends APIAbstractTest {

        @Override
        public void printTest() {

            IClient client = contest.getMyClient();
            print("This client");
            print(", login=" + client.getLoginName());
            print(", name=" + client.getDisplayName());
            print(", type=" + client.getType());
            print(", account#" + client.getAccountNumber());
            print(", site=" + client.getSiteNumber());
            println();
        }

        @Override
        String getTitle() {
            return "getMyClient";
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintSites extends APIAbstractTest {

        @Override
        public void printTest() {
            ISite[] sites = contest.getSites();
            println("There are " + sites.length + " sites.");
            for (ISite site : sites) {
                println(" Site " + site.getNumber() + " name=" + site.getName());
            }
        }

        @Override
        String getTitle() {
            return "getSites";
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintClockInfo extends APIAbstractTest {

        @Override
        public void printTest() {
            IContestClock clock = contest.getContestClock();
            print("Clock:");
            print(" remaining=" + clock.getRemainingSecs());
            print(" elapsed=" + clock.getElapsedSecs());
            print(" length=" + clock.getContestLengthSecs());
            println();
        }

        @Override
        String getTitle() {
            return "getContestClock";
        }
    }

    // getJudgements

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintJudgements extends APIAbstractTest {

        @Override
        public void printTest() {
            IJudgement[] judgements = contest.getJudgements();
            println("There are " + judgements.length + " judgements.");
            for (IJudgement judgement : judgements) {
                println("judgement name = " + judgement.getName());
            }
            println();
        }

        @Override
        String getTitle() {
            return "getJudgements";
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintContestTitle extends APIAbstractTest {

        @Override
        public void printTest() {
            println("Contest title: '" + contest.getContestTitle() + "'");
        }

        @Override
        String getTitle() {
            return "getContestTitle";
        }
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
            loginButton.setBounds(new java.awt.Rectangle(318, 12, 70, 26));
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

        } catch (LoginFailureException e) {
            contest = null;
            showMessage("Unable to login " + e.getMessage());
        } catch (Exception e) {
            contest = null;
            showMessage("Unable to login " + e.getMessage());
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
            loginTextField.setBounds(new java.awt.Rectangle(155, 15, 127, 20));
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
            passwordTextField.setBounds(new java.awt.Rectangle(155, 50, 127, 20));
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

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintTeams extends APIAbstractTest {

        @Override
        public void printTest() {
            println("There are " + contest.getTeams().length + " team ");
            for (ITeam team : contest.getTeams()) {
                println(team.getLoginName() + " title: " + team.getLoginName() + " group: " + team.getGroup().getName());
            }
            println("");
            println();
        }

        @Override
        String getTitle() {
            return "getTeams";
        }
    }

    protected void printTeamsTest() {
        runReport(new PrintTeams());

    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintProblems extends APIAbstractTest {

        @Override
        public void printTest() {
            println("There are " + contest.getProblems().length + " team ");
            for (IProblem problem : contest.getProblems()) {
                print("Problem name = " + problem.getName());

                print(" data file = ");
                if (problem.hasDataFile()) {
                    print(problem.getJudgesDataFileName());
                } else {
                    print("<none>");
                }

                print(" answer file = ");
                if (problem.hasAnswerFile()) {
                    print(problem.getJudgesAnswerFileName());
                } else {
                    print("<none>");
                }

                print(" validator = ");
                if (problem.hasExternalValidator()) {
                    print(problem.getValidatorFileName());
                } else {
                    print("<none>");
                }

                if (problem.readsInputFromFile()) {
                    print(" reads from FILE");
                }
                if (problem.readsInputFromStdIn()) {
                    print(" reads from stdin");
                }
                println();
            }
            println();
        }

        @Override
        String getTitle() {
            return "getProblems";
        }
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
            logoffButton.setBounds(new java.awt.Rectangle(318, 47, 70, 26));
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
            runListenerCheckBox.setBounds(new java.awt.Rectangle(24, 203, 177, 18));
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
            configListenerCheckBox.setBounds(new java.awt.Rectangle(24, 264, 177, 21));
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
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintStandings extends APIAbstractTest {

        @Override
        public void printTest() {

            println("Standings - " + contest.getStandings().length + " teams to rank");
            for (IStanding standing : contest.getStandings()) {
                println("Rank " + standing.getRank() + " solved= " + standing.getNumProblemsSolved() + " pts= " + standing.getPenaltyPoints() + " " + standing.getClient().getLoginName());
            }

            println();
        }

        @Override
        String getTitle() {
            return "getStandings";
        }
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
     * 
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
        String getTitle() {
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
            clearButton.setBounds(new java.awt.Rectangle(247, 258, 94, 29));
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
            oneRunTest.setBounds(new java.awt.Rectangle(86, 14, 141, 29));
            oneRunTest.setToolTipText("View all Run's Source");
            oneRunTest.setText("Run View Source");
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
            showMessage("No runs in system");
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
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintClarifications extends APIAbstractTest {

        @Override
        public void printTest() {
            IClarification[] clarifications = contest.getClarifications();
            println("There are " + clarifications.length + " clarifications ");

            for (IClarification clarification : clarifications) {

                print("Clar " + clarification.getNumber() + " Site " + clarification.getSiteNumber());

                print(" @ " + clarification.getSubmissionTime() + " by " + clarification.getTeam().getLoginName());
                print(" problem: " + clarification.getProblem().getName());
                print(" " + trueFalseString(clarification.isAnswered(), "ANSWERED", "NOT ANSWERED"));
                print(" " + trueFalseString(clarification.isDeleted(), "DELETED", ""));
                println();
                println("  Question: " + clarification.getQuestion());
                if (clarification.isAnswered()) {
                    println("    Answer: " + clarification.getAnswer());
                }
            }
            println();
        }

        @Override
        String getTitle() {
            return "getClarifications";
        }
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintGroups extends APIAbstractTest {

        @Override
        public void printTest() {
            IGroup [] groups = contest.getGroups();
            println("There are " + groups.length + " groups");
            for (IGroup group : contest.getGroups()){
                println("Group = "+group.getName());
            }
        }

        @Override
        String getTitle() {
            return "getGroups";
        }
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintLocalContactInfo extends APIAbstractTest {

        @Override
        public void printTest() {
            println("Contacted: " + contest.getLocalContactedHostName() + " port=" + contest.getLocalContactedPortNumber());
        }

        @Override
        String getTitle() {
            return "getLocalContactedHostName";
        }
    }    
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintSiteName extends APIAbstractTest {

        @Override
        public void printTest() {
            println("Site Name = "+contest.getSiteName());
        }

        @Override
        String getTitle() {
            return "getSiteName";
        }
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintContestRunning extends APIAbstractTest {

        @Override
        public void printTest() {
            println("Contest running ? "+contest.isContestClockRunning());
        }

        @Override
        String getTitle() {
            return "isContestClockRunning";
        }
    }


    protected String trueFalseString(boolean value, String trueString, String falseString) {
        if (value) {
            return trueString;
        } else {
            return falseString;
        }
    }

    /**
     * This method initializes clarListenerCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getClarListenerCheckBox() {
        if (clarListenerCheckBox == null) {
            clarListenerCheckBox = new JCheckBox();
            clarListenerCheckBox.setBounds(new java.awt.Rectangle(24, 232, 200, 21));
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
            runContestButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    int selected = getReportJList().getSelectedIndex();
                    if (selected == -1) {
                        showMessage("No Re[port Selected");
                    } else {
                        runReport(getReportJList().getSelectedValue());
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
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintLanguages extends APIAbstractTest {

        @Override
        public void printTest() {
            ILanguage [] languages = contest.getLanguages();
            
            println("There are " + languages.length + " languages");
            for (ILanguage language : languages){
                println("Language "+language.getName());
            }
        }

        @Override
        String getTitle() {
            return "getLanguages";
        }
    }
    
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintMyClientSC extends APIAbstractTest {
        @Override
        public void printTest() {

            IClient client;
            try {
                client = serverConnection.getMyClient();
                print("This client");
                print(", login=" + client.getLoginName());
                print(", name=" + client.getDisplayName());
                print(", type=" + client.getType());
                print(", account#" + client.getAccountNumber());
                print(", site=" + client.getSiteNumber());
            } catch (NotLoggedInException e) {
                println("Exception during report "+e.getLocalizedMessage()+" "+e.getStackTrace()[0].getClassName());
                e.printStackTrace();
            }
            println();
        }

        @Override
        String getTitle() {
            return "getMyClient (ServerConnection)";
        }
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintLoggedInSC extends APIAbstractTest {
        @Override
        public void printTest() {
            print("This client, logged in = "+serverConnection.isLoggedIn());
            println();
        }

        @Override
        String getTitle() {
            return "isLoggedIn (ServerConnection)";
        }
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class PrintGetContestSC extends APIAbstractTest {
        @Override
        public void printTest() {
            try {
                IContest serverConnContest = serverConnection.getContest();
                if (serverConnContest != null) {
                    println("getContest from ServerConnection is " + serverConnContest);
                } else {
                    println("getContest from ServerConnection returns null");
                }
            } catch (NotLoggedInException e) {
                println("Exception during report " + e.getLocalizedMessage() + " " + e.getStackTrace()[0].getClassName());
                e.printStackTrace();
            }
            println();
        }

        @Override
        String getTitle() {
            return "isLoggedIn (ServerConnection)";
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
            passwordLabel.setBounds(new java.awt.Rectangle(74, 52, 58, 16));
            passwordLabel.setText("Password");
            passwordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            loginLabel = new JLabel();
            loginLabel.setBounds(new java.awt.Rectangle(101, 17, 31, 16));
            loginLabel.setText("Login");
            loginLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            topPane = new JPanel();
            topPane.setLayout(null);
            topPane.setPreferredSize(new java.awt.Dimension(100, 100));
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
            eastPane.add(getRunContestButton(), java.awt.BorderLayout.SOUTH);
            eastPane.add(getReportScrollPane(), java.awt.BorderLayout.CENTER);
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
            numberTextField.setBounds(new java.awt.Rectangle(178,170,73,22));
        }
        return numberTextField;
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
