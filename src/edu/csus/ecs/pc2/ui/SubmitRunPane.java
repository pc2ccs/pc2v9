package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.ILanguageListener;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageEvent;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.model.RunEvent;

/**
 * A submit run pane.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class SubmitRunPane extends JPanePlugin {

    public static final String SVN_ID = "$Id$";

    private IModel model = null;

    private IController teamController = null;

    private String lastOpenedFile = null;

    /**
     * 
     */
    private static final long serialVersionUID = 8225187691479543638L;

    private JPanel mainViewPane = null;

    private JButton submitRunButton = null;

    private JComboBox problemComboBox = null;

    private JComboBox languageComboBox = null;

    private JButton pickFileButton = null;

    private JPanel filenamePane = null;

    private JLabel fileNameLabel = null;

    private Log log = null;

    private JPanel problemPane = null;

    private JPanel languagePane = null;

    private JButton testButton = null;

    /**
     * Nevermind this constructor, needed for VE and other reasons.
     * 
     */
    public SubmitRunPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(486, 310));
        this.add(getMainViewPane(), java.awt.BorderLayout.CENTER);

    }

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog("Are you sure you want to exit PC^2?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void populateGUI() {

        getProblemComboBox().removeAllItems();
        Problem problemN = new Problem("None Selected");
        getProblemComboBox().addItem(problemN);

        for (Problem problem : model.getProblems()) {
            getProblemComboBox().addItem(problem);
        }

        getLanguageComboBox().removeAllItems();
        Language languageN = new Language("None Selected");
        getLanguageComboBox().addItem(languageN);

        for (Language language : model.getLanguages()) {
            getLanguageComboBox().addItem(language);
        }

        setButtonsActive(model.getContestTime().isContestRunning());
    }

    private boolean isThisSite(int siteNumber) {
        return siteNumber == model.getSiteNumber();
    }

    /**
     * Enable or disable submission buttons.
     * 
     * @param turnButtonsOn
     *            if true, buttons enabled.
     */
    private void setButtonsActive(final boolean turnButtonsOn) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getSubmitRunButton().setEnabled(turnButtonsOn);
                getPickFileButton().setEnabled(turnButtonsOn);
                getTestButton().setEnabled(turnButtonsOn);

            }
        });
        FrameUtilities.regularCursor(this);
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    private class ContestTimeListenerImplementation implements IContestTimeListener {

        public void contestTimeAdded(ContestTimeEvent event) {
            log.info("debug ContestTime site " + event.getSiteNumber() + " ADDED " + event.getContestTime().getElapsedTimeStr());
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
            log.info("debug ContestTime site " + event.getSiteNumber() + " REMOVED ");
        }

        public void contestTimeChanged(ContestTimeEvent event) {
            log.info("debug ContestTime site " + event.getSiteNumber() + " CHANGED ");
        }

        public void contestStarted(ContestTimeEvent event) {
            log.info("debug ContestTime site " + event.getSiteNumber() + " STARTED " + event.getContestTime().getElapsedTimeStr());
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
        }

        public void contestStopped(ContestTimeEvent event) {
            log.info("debug ContestTime site " + event.getSiteNumber() + " STOPPED " + event.getContestTime().getElapsedTimeStr());
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    private class ProblemListenerImplementation implements IProblemListener {

        public void problemAdded(final ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    getProblemComboBox().addItem(event.getProblem());
                }
            });
        }

        public void problemChanged(ProblemEvent event) {
            log.info("debug Problem CHANGED  " + event.getProblem());
        }

        public void problemRemoved(ProblemEvent event) {
            log.info("debug Problem REMOVED  " + event.getProblem());
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    private class LanguageListenerImplementation implements ILanguageListener {

        public void languageAdded(final LanguageEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    getLanguageComboBox().addItem(event.getLanguage());
                }
            });
        }

        public void languageChanged(LanguageEvent event) {
            log.info("debug Language CHANGED  " + event.getLanguage());
        }

        public void languageRemoved(LanguageEvent event) {
            log.info("debug Language REMOVED  " + event.getLanguage());
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    private class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
            // updateListBox(event.getRun() + " ADDED ");
            log.info("debug Added run " + event.getRun());
        }

        public void runChanged(RunEvent event) {
            log.info("debug "+event.getRun() + " CHANGED ");
        }

        public void runRemoved(RunEvent event) {
            log.info("debug "+event.getRun() + " REMOVED ");
        }
    }

    protected void autoPopulate() {
        // TODO: auto populate

        String matchProblemString = "umit";
        for (int i = 0; i < problemComboBox.getItemCount(); i++) {
            Problem problem = (Problem) problemComboBox.getItemAt(i);
            int idx = problem.toString().indexOf(matchProblemString);
            if (idx > -1) {
                problemComboBox.setSelectedIndex(i);
            }
        }

        String matchLanguageString = "Java";
        for (int i = 0; i < languageComboBox.getItemCount(); i++) {
            Language language = (Language) languageComboBox.getItemAt(i);
            int idx = language.toString().indexOf(matchLanguageString);
            if (idx > -1) {
                languageComboBox.setSelectedIndex(i);
            }
        }

        // problemComboBox.
        // getProblemCombo().setSelectedIndex(getProblemCombo().getItemCount() - 1);
        // getLanguageCombo().setSelectedIndex(getLanguageCombo().getItemCount() - 1);

        try {
            String filename = "samps/Sumit.java";
            File file = new File(filename);
            if (file.exists()) {
                fileNameLabel.setText(filename);
            }
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
        }

        try {
            String filename = "/pc2/samps/sumit.java";
            File file = new File(filename);
            if (file.exists()) {
                fileNameLabel.setText(filename);
            }
        } catch (Exception e) {
            log.log(Log.WARNING, "Exception logged ", e);
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
            mainViewPane.setLayout(null);

            mainViewPane.add(getFilenamePane(), null);
            mainViewPane.add(getSubmitRunButton(), null);
            mainViewPane.add(getProblemPane(), null);
            mainViewPane.add(getLanguagePane(), null);
            mainViewPane.add(getTestButton(), null);
        }
        return mainViewPane;
    }

    /**
     * This method initializes submitRunButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSubmitRunButton() {
        if (submitRunButton == null) {
            submitRunButton = new JButton();
            submitRunButton.setEnabled(false);
            submitRunButton.setBounds(new java.awt.Rectangle(381, 254, 74, 26));
            submitRunButton.setText("Submit");
            submitRunButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    testOrSubmitRun(true);
                }
            });
        }
        return submitRunButton;
    }

    private boolean fileExists(String fileName) {
        File file = new File(fileName);
        return file.isFile();
    }
    
    /**
     * Submit run or test run.
     * 
     * Validates that the user has selected problem,
     * language and a valid filename.
     * 
     * @param submitTheRun if true, submits the run.
     */
    protected void testOrSubmitRun(boolean submitTheRun) {

        Problem problem = ((Problem) getProblemComboBox().getSelectedItem());
        Language language = ((Language) getLanguageComboBox().getSelectedItem());

        if (getProblemComboBox().getSelectedIndex() < 1) {
            JOptionPane.showMessageDialog(this, "Please select problem");
            return;
        }

        if (getLanguageComboBox().getSelectedIndex() < 1) {
            JOptionPane.showMessageDialog(this, "Please select language");
            return;
        }

        String filename = fileNameLabel.getText();

        if (!fileExists(filename)) {
            File curdir = new File(".");

            String message = filename + " not found";
            try {
                message = message + " in " + curdir.getCanonicalPath();
            } catch (Exception e) {
                // ignore exception
                message = message + ""; // What a waste of time and code.
            }
            JOptionPane.showMessageDialog(this, message);
            return;
        }

        if (submitTheRun){
            try {
                teamController.submitRun(problem, language, filename);
            } catch (Exception e) {
                // TODO need to make this cleaner
                JOptionPane.showMessageDialog(this, "Exception " + e.getMessage());
            }
        } else {
            // Test run
            testRun (problem, language, filename);
        }
    }

    private void testRun(Problem problem, Language language, String filename) {
        
        System.err.println ("Would have tested a run ");
    }

    /**
     * This method initializes problemComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getProblemComboBox() {
        if (problemComboBox == null) {
            problemComboBox = new JComboBox();
            problemComboBox.setBounds(new java.awt.Rectangle(38, 42, 221, 28));
            problemComboBox.addItem(new Problem("Select Problem"));
        }
        return problemComboBox;
    }

    /**
     * This method initializes jComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getLanguageComboBox() {
        if (languageComboBox == null) {
            languageComboBox = new JComboBox();
            languageComboBox.setBounds(new java.awt.Rectangle(49, 97, 221, 28));
            languageComboBox.addItem(new Language("Select Language"));
        }
        return languageComboBox;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getPickFileButton() {
        if (pickFileButton == null) {
            pickFileButton = new JButton();
            pickFileButton.setEnabled(false);
            pickFileButton.setText("Select");
            pickFileButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    selectMainFile();
                }
            });
        }
        return pickFileButton;
    }

    private void selectMainFile() {
        JFileChooser chooser = new JFileChooser(lastOpenedFile);
        try {
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                lastOpenedFile = chooser.getCurrentDirectory().toString();
                fileNameLabel.setText(chooser.getSelectedFile().getCanonicalFile().toString());
                fileNameLabel.setToolTipText(chooser.getSelectedFile().getCanonicalFile().toString());

            }
        } catch (Exception e) {
            System.err.println("Error getting selected file, try again.");
            e.printStackTrace(System.err);
            // getLog().log(Log.CONFIG, "Error getting selected file, try again.", e);
        }
        chooser = null;

    }

    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.teamController = inController;

        this.log = teamController.getLog();
        
        model.addRunListener(new RunListenerImplementation());
        model.addContestTimeListener(new ContestTimeListenerImplementation());
        model.addLanguageListener(new LanguageListenerImplementation());
        model.addProblemListener(new ProblemListenerImplementation());

        // TODO add listeners for accounts, login and site.

        // model.addAccountListener(new AccountListenerImplementation());
        // model.addLoginListener(new LoginListenerImplementation());
        // model.addSiteListener(new SiteListenerImplementation());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI();
            }
        });

        setVisible(true);
    }

    public String getPluginTitle() {
        return "Submit Run Pane ";
    }

    /**
     * This method initializes filenamePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getFilenamePane() {
        if (filenamePane == null) {
            fileNameLabel = new JLabel();
            fileNameLabel.setText("");
            fileNameLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() > 1 && e.isShiftDown()) {
                        autoPopulate();
                    }
                }
            });
            filenamePane = new JPanel();
            filenamePane.setLayout(new BorderLayout());
            filenamePane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Main File", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), new java.awt.Color(51, 51, 51)));
            filenamePane.setLocation(new java.awt.Point(20, 178));
            filenamePane.setSize(new java.awt.Dimension(435, 50));
            filenamePane.add(getPickFileButton(), java.awt.BorderLayout.EAST);
            filenamePane.add(fileNameLabel, java.awt.BorderLayout.CENTER);
        }
        return filenamePane;
    }

    /**
     * This method initializes problemPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getProblemPane() {
        if (problemPane == null) {
            problemPane = new JPanel();
            problemPane.setLayout(new BorderLayout());
            problemPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Problem", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            problemPane.setLocation(new java.awt.Point(20, 26));
            problemPane.setSize(new java.awt.Dimension(435, 50));
            problemPane.add(getProblemComboBox(), java.awt.BorderLayout.CENTER);
        }
        return problemPane;
    }

    /**
     * This method initializes languagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getLanguagePane() {
        if (languagePane == null) {
            languagePane = new JPanel();
            languagePane.setLayout(new BorderLayout());
            languagePane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Language", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            languagePane.setLocation(new java.awt.Point(20, 102));
            languagePane.setSize(new java.awt.Dimension(332, 50));
            languagePane.add(getLanguageComboBox(), java.awt.BorderLayout.CENTER);
        }
        return languagePane;
    }

    /**
     * This method initializes testButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getTestButton() {
        if (testButton == null) {
            testButton = new JButton();
            testButton.setBounds(new java.awt.Rectangle(20, 254, 74, 26));
            testButton.setText("Test");
            testButton.setEnabled(false);
            testButton.setVisible(true);
            testButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    testOrSubmitRun(false);
                }
            });
        }
        return testButton;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
