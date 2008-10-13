package edu.csus.ecs.pc2.api;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.csus.ecs.pc2.api.exceptions.LoginFailureException;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;
import edu.csus.ecs.pc2.api.listener.IRunEventListener;
import edu.csus.ecs.pc2.ui.FrameUtilities;

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

    private JLabel loginLabel = null;

    private JLabel jLabel = null;

    private JButton listTeamsButton = null;

    private JButton logoffButton = null;

    private JCheckBox runListenerCheckBox = null;

    private JCheckBox configListenerCheckBox = null;

    private RunListener runListener = null;

    private ScrollyFrame scrollyFrame = new ScrollyFrame();

    private String line = "";

    private JButton jButton = null;

    private JButton jButton1 = null;

    private JButton clearButton = null;

    private JButton oneRunTest = null;

    private JButton showClarsButton = null;

    private JCheckBox clarListenerCheckBox = null;

    private ClarificationListener clarificationListener = null;;

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
        this.setSize(new java.awt.Dimension(553, 364));
        this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        this.setContentPane(getMainPain());
        this.setTitle("Contest Test Frame [NOT LOGGED IN]");

        FrameUtilities.setFramePosition(this, FrameUtilities.HorizontalPosition.LEFT, FrameUtilities.VerticalPosition.CENTER);

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
            println("Clar added Site " + clarification.getSiteNumber() + " Run " + clarification.getNumber() + " from " + clarification.getTeam().getLoginName() + " at "
                    + clarification.getSubmissionTime()+" Problem "+clarification.getProblem().getName());
        }

        public void clarificationRemoved(IClarification clarification) {
            println("Clar removed Site " + clarification.getSiteNumber() + " Run " + clarification.getNumber() + " from " + clarification.getTeam().getLoginName() + " at "
                    + clarification.getSubmissionTime()+" Problem "+clarification.getProblem().getName());
        }

        public void clarificationAnswered(IClarification clarification) {
            println("Clar answered Site " + clarification.getSiteNumber() + " Run " + clarification.getNumber() + " from " + clarification.getTeam().getLoginName() + " at "
                    + clarification.getSubmissionTime()+" Problem "+clarification.getProblem().getName());
        }

        public void clarificationUpdated(IClarification clarification) {
            println("Clar updated Site " + clarification.getSiteNumber() + " Run " + clarification.getNumber() + " from " + clarification.getTeam().getLoginName() + " at "
                    + clarification.getSubmissionTime()+" Problem "+clarification.getProblem().getName());
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
            mainPain.add(getCenterPane(), java.awt.BorderLayout.CENTER);
            mainPain.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        }
        return mainPain;
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            jLabel = new JLabel();
            jLabel.setBounds(new java.awt.Rectangle(205, 29, 83, 16));
            jLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel.setText("Password");
            loginLabel = new JLabel();
            loginLabel.setBounds(new java.awt.Rectangle(26, 29, 53, 16));
            loginLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            loginLabel.setText("Login");
            centerPane = new JPanel();
            centerPane.setLayout(null);
            centerPane.add(getTestRunButton(), null);
            centerPane.add(getLoginButton(), null);
            centerPane.add(getLoginTextField(), null);
            centerPane.add(getPasswordTextField(), null);
            centerPane.add(loginLabel, null);
            centerPane.add(jLabel, null);
            centerPane.add(getListTeamsButton(), null);
            centerPane.add(getLogoffButton(), null);
            centerPane.add(getRunListenerCheckBox(), null);
            centerPane.add(getConfigListenerCheckBox(), null);
            centerPane.add(getJButton(), null);
            centerPane.add(getJButton1(), null);
            centerPane.add(getJButton2(), null);
            centerPane.add(getOneRunTest(), null);
            centerPane.add(getShowClarsButton(), null);
            centerPane.add(getClarListenerCheckBox(), null);
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
            testRunButton.setBounds(new java.awt.Rectangle(306, 122, 94, 29));
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

        if (contest == null) {
            showMessage("Not logged in");
            return;
        }

        // println("There are "+contest.getLanguages()+" languages");
        // for (ILanguage language : contest.getLanguages()){
        // println(language.getTitle());
        // }

        println();

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
        println();

    }

    /**
     * This method initializes loginButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoginButton() {
        if (loginButton == null) {
            loginButton = new JButton();
            loginButton.setBounds(new java.awt.Rectangle(421, 20, 93, 29));
            loginButton.setToolTipText("Login to contest");
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

    private void showMessage(String string) {
        JOptionPane.showMessageDialog(null, string);
    }

    /**
     * This method initializes loginTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getLoginTextField() {
        if (loginTextField == null) {
            loginTextField = new JTextField();
            loginTextField.setBounds(new java.awt.Rectangle(90, 21, 89, 24));
            loginTextField.setText("scoreboard1");
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
            passwordTextField.setBounds(new java.awt.Rectangle(300, 22, 95, 23));
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
            listTeamsButton.setBounds(new java.awt.Rectangle(306, 173, 94, 29));
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

        if (contest == null) {
            showMessage("Not logged in");
            return;
        }

        println("There are " + contest.getTeams().length + " team ");
        for (ITeam team : contest.getTeams()) {
            println(team.getLoginName() + " title: " + team.getLoginName() + " group: " + team.getGroup().getName());
        }
        println("");

    }

    protected void printProblemsTest() {

        if (contest == null) {
            showMessage("Not logged in");
            return;
        }

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

    /**
     * This method initializes logoffButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLogoffButton() {
        if (logoffButton == null) {
            logoffButton = new JButton();
            logoffButton.setBounds(new java.awt.Rectangle(421, 77, 93, 29));
            logoffButton.setToolTipText("Logoff of contest");
            logoffButton.setText("Logoff");
            logoffButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        logoff();
                    } catch (NotLoggedInException e1) {
                        JOptionPane.showMessageDialog(null, "unable to logoff "+e1.getMessage());
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
            runListenerCheckBox.setBounds(new java.awt.Rectangle(24,182,177,18));
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
            configListenerCheckBox.setBounds(new java.awt.Rectangle(24,243,177,21));
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

        println("configListenerChanged todo " + listenerON); // todo CODE
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton() {
        if (jButton == null) {
            jButton = new JButton();
            jButton.setBounds(new java.awt.Rectangle(306, 220, 94, 29));
            jButton.setToolTipText("Print Standings");
            jButton.setText("Standings");
            jButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    printStandingsTest();
                }
            });
        }
        return jButton;
    }

    protected void printStandingsTest() {

        if (contest == null) {
            showMessage("Not logged in");
            return;
        }

        println("Standings");
        for (IStanding standing : contest.getStandings()) {
            println("Rank " + standing.getRank() + " solved= " + standing.getNumProblemsSolved() + " pts= " + standing.getPenaltyPoints() + " " + standing.getClient().getLoginName());
        }

    }

    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton1() {
        if (jButton1 == null) {
            jButton1 = new JButton();
            jButton1.setBounds(new java.awt.Rectangle(421, 172, 94, 29));
            jButton1.setToolTipText("Print all contest info");
            jButton1.setText("Print ALL");
            jButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    printAll();
                }
            });
        }
        return jButton1;
    }

    protected void printAll() {

        if (contest == null) {
            showMessage("Not logged in");
            return;
        }

        printStandingsTest();
        printTeamsTest();
        printProblemsTest();
        printRunsTest();
        printClarTest();

    }

    /**
     * This method initializes jButton2
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton2() {
        if (clearButton == null) {
            clearButton = new JButton();
            clearButton.setBounds(new java.awt.Rectangle(421, 219, 94, 29));
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
            oneRunTest.setBounds(new java.awt.Rectangle(263, 76, 141, 29));
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
            showClarsButton.setBounds(new java.awt.Rectangle(201, 123, 82, 29));
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
        if (contest == null) {
            showMessage("Not logged in");
            return;
        }

        println();

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
            clarListenerCheckBox.setBounds(new java.awt.Rectangle(24,211,177,21));
            clarListenerCheckBox.setToolTipText("Listen for Clarification events");
            clarListenerCheckBox.setText("View Clar Listener Listener");
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
            if (clarificationListener  == null) {
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

} // @jve:decl-index=0:visual-constraint="10,10"
