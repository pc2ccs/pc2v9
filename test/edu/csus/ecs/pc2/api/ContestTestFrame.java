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
import edu.csus.ecs.pc2.api.listener.IRunEventListener;
import edu.csus.ecs.pc2.ui.FrameUtilities;

/**
 * API Contest Test Frame
 * @author pc2@ecs.csus.edu
 * @version $Id: ContestTestFrame.java 1242 2008-02-03 22:30:35Z
laned $
 */

// $HeadURL:
http://pc2.ecs.csus.edu/repos/pc2v9/trunk/test/edu/csus/ecs/pc2/api/ContestTestFrame.java
$
public class ContestTestFrame extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID =
-3146831894495294017L;

    private JPanel mainPain = null;

    private JPanel centerPane = null;

    private JPanel buttonPane = null;

    private JButton goButton = null;

    private JButton exitButton = null;

    private JButton testRunButton = null;

    private JButton loginButton = null;

    private JTextField loginTextField = null;

    private JTextField passwordTextField = null;

    private ServerConnection serverConnection = new
ServerConnection();

    private IContest contest = null;

    private JLabel loginLabel = null;

    private JLabel jLabel = null;

    private JButton listTeamsButton = null;

    private JButton logoffButton = null;

    private JCheckBox runListenerCheckBox = null;

    private JCheckBox configListenerCheckBox = null;

    private RunListener runListener = null;

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

        FrameUtilities.setFramePosition(this,
FrameUtilities.HorizontalPosition.LEFT,
FrameUtilities.VerticalPosition.CENTER);

    }

    /**
     *
     * @author pc2@ecs.csus.edu
     * @version $Id: ContestTestFrame.java 1242 2008-02-03 22:30:35Z
laned $
     */

    // $HeadURL:
http://pc2.ecs.csus.edu/repos/pc2v9/trunk/test/edu/csus/ecs/pc2/api/ContestTestFrame.java
$
    protected class RunListener implements IRunEventListener {

        public void runAdded(IRun run) {
            System.out.println("Run added Site " +
run.getSiteNumber() + " Run " + run.getNumber() + " from
"
                    + run.getTeam().getLoginName() + " at " +
run.getSubmissionTime());
        }

        public void runRemoved(IRun run) {
            System.out.println("Run removed Site " +
run.getSiteNumber() + " Run " + run.getNumber() + " from
"
                    + run.getTeam().getLoginName() + " at " +
run.getSubmissionTime());
        }

        public void runJudged(IRun run) {
            System.out.println("Run judged Site " +
run.getSiteNumber() + " Run " + run.getNumber() + " from
"
                    + run.getTeam().getLoginName() + " at " +
run.getSubmissionTime());
        }

        public void runUpdated(IRun run) {
            System.out.println("Run updated Site " +
run.getSiteNumber() + " Run " + run.getNumber() + " from
"
                    + run.getTeam().getLoginName() + " at " +
run.getSubmissionTime());
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
            mainPain.add(getCenterPane(),
java.awt.BorderLayout.CENTER);
            mainPain.add(getButtonPane(),
java.awt.BorderLayout.SOUTH);
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
            jLabel.setBounds(new java.awt.Rectangle(205, 29, 83,
16));
            jLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel.setText("Password");
            loginLabel = new JLabel();
            loginLabel.setBounds(new java.awt.Rectangle(26, 29, 53,
16));
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
            buttonPane.setLayout(flowLayout);
            buttonPane.add(getGoButton(), null);
            buttonPane.add(getExitButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes goButton
     *
     * @return javax.swing.JButton
     */
    private JButton getGoButton() {
        if (goButton == null) {
            goButton = new JButton();
            goButton.setText("Go");
            goButton.addActionListener(new
java.awt.event.ActionListener() {
                public void
actionPerformed(java.awt.event.ActionEvent e) {
                    System.out.println("actionPerformed()"); // TODO
Auto-generated Event stub actionPerformed()
                }
            });
        }
        return goButton;
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
            exitButton.addActionListener(new
java.awt.event.ActionListener() {
                public void
actionPerformed(java.awt.event.ActionEvent e) {
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
            testRunButton.setBounds(new java.awt.Rectangle(16, 82,
117, 32));
            testRunButton.setText("getRuns API");
            testRunButton.addActionListener(new
java.awt.event.ActionListener() {
                public void
actionPerformed(java.awt.event.ActionEvent e) {
                    getRunsTest();
                }
            });
        }
        return testRunButton;
    }

    protected void getRunsTest() {

        if (contest == null) {
            showMessage("Not logged in");
            return;
        }

        // System.out.println("There are "+contest.getLanguages()+"
languages");
        // for (ILanguage language : contest.getLanguages()){
        // System.out.println(language.getTitle());
        // }

        System.out.println();

        for (IRun run : contest.getRuns()) {

            System.out.print("Run " + run.getNumber() + " Site " +
run.getSiteNumber());

            System.out.print(" @ " + run.getSubmissionTime() + " by
" + run.getTeam().getLoginName());
            System.out.print(" problem: " +
run.getProblem().getName());
            System.out.print(" in " + run.getLanguage().getName());

            if (run.isJudged()) {
                System.out.println("  Judgement: " +
run.getJudgementName());
            } else {
                System.out.println("  Judgement: not judged yet ");
            }

            System.out.println();
        }
        System.out.println();

    }

    /**
     * This method initializes loginButton
     *
     * @return javax.swing.JButton
     */
    private JButton getLoginButton() {
        if (loginButton == null) {
            loginButton = new JButton();
            loginButton.setBounds(new java.awt.Rectangle(421, 20,
93, 32));
            loginButton.setText("Login");
            loginButton.addActionListener(new
java.awt.event.ActionListener() {
                public void
actionPerformed(java.awt.event.ActionEvent e) {
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
            info("Logged in at " + login + " took " + totalMs + "ms
(" + (totalMs / 1000) + " seconds)");
            setTitle("Contest " +
contest.getMyClient().getLoginName() + " " +
contest.getSiteName());
            getLoginButton().setEnabled(false);
        } catch (LoginFailureException e) {
            contest = null;
            showMessage("Unable to login " + e.getMessage());
        }
        FrameUtilities.regularCursor(this);

    }

    private void info(String string) {
        System.out.println(new Date() + " " +
Thread.currentThread().getName() + " " + string);
        // TODO Auto-generated method stub

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
            loginTextField.setBounds(new java.awt.Rectangle(90, 21,
89, 24));
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
            passwordTextField.setBounds(new java.awt.Rectangle(300,
22, 95, 23));
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
            listTeamsButton.setBounds(new java.awt.Rectangle(161,
82, 128, 32));
            listTeamsButton.setText("getTeams API");
            listTeamsButton.addActionListener(new
java.awt.event.ActionListener() {
                public void
actionPerformed(java.awt.event.ActionEvent e) {
                    getTeamsTest();
                }
            });
        }
        return listTeamsButton;
    }

    protected void getTeamsTest() {

        if (contest == null) {
            showMessage("Not logged in");
            return;
        }

        System.out.println("There are " + contest.getTeams().length
+ " team ");
        for (ITeam team : contest.getTeams()) {
            System.out.println(team.getLoginName() + " title: " +
team.getLoginName() + " group: " +
team.getGroup().getName());
        }

    }

    /**
     * This method initializes logoffButton
     *
     * @return javax.swing.JButton
     */
    private JButton getLogoffButton() {
        if (logoffButton == null) {
            logoffButton = new JButton();
            logoffButton.setBounds(new java.awt.Rectangle(418, 77,
93, 32));
            logoffButton.setText("Logoff");
            logoffButton.addActionListener(new
java.awt.event.ActionListener() {
                public void
actionPerformed(java.awt.event.ActionEvent e) {
                    logoff();
                }
            });
        }
        return logoffButton;
    }

    protected void logoff() {

        if (contest == null) {
            showMessage("Not logged in");
            return;
        }

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
            runListenerCheckBox.setBounds(new java.awt.Rectangle(25,
158, 152, 18));
            runListenerCheckBox.setText("Run Listener On");
            runListenerCheckBox.addActionListener(new
java.awt.event.ActionListener() {
                public void
actionPerformed(java.awt.event.ActionEvent e) {
                    runListenerChanged(runListenerCheckBox.isSelected());
                }
            });
        }
        return runListenerCheckBox;
    }

    /**
     * Turn run listener on and off
     *
     * @param listenerON true add listener, false no listener.
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
            System.out.println("Run Listener added");
        } else {
            if (runListener != null) {
                contest.removeRunListener(runListener);
                System.out.println("Run Listener removed");
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
            configListenerCheckBox.setBounds(new
java.awt.Rectangle(24, 192, 157, 21));
            configListenerCheckBox.setText("Config Listener On");
            configListenerCheckBox.addActionListener(new
java.awt.event.ActionListener() {
                public void
actionPerformed(java.awt.event.ActionEvent e) {
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

        System.out.println("configListenerChanged todo " +
listenerON); // todo CODE
    }

    public static void main(String[] args) {
        new ContestTestFrame().setVisible(true);
    }

} // @jve:decl-index=0:visual-constraint="10,10"


