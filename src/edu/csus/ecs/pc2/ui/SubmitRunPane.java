package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.Executable;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ContestTimeEvent;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IContestTimeListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ILanguageListener;
import edu.csus.ecs.pc2.core.model.IProblemListener;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageEvent;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemEvent;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * A submit run pane.
 * 
 * @see edu.csus.ecs.pc2.Starter
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class SubmitRunPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 5700304771581849261L;

    private String lastOpenedFile = null;

    private JPanel mainViewPane = null;

    private JButton submitRunButton = null;

    private JComboBox<Problem> problemComboBox = null;

    private JComboBox<Language> languageComboBox = null;

    private JButton pickFileButton = null;

    private JPanel filenamePane = null;

    private JLabel fileNameLabel = null;

    private Log log = null;

    private JPanel problemPane = null;

    private JPanel languagePane = null;

    private JButton testButton = null;

    private Executable executable = null;

    private JPanel additionalFilesPane = null;

    private JPanel additonalFilesButtonPane = null;

    private JButton addAdditionalFilesButton = null;

    private JButton removeAdditionalFilesButton = null;

    private MCLB additionalFilesMCLB = null;

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
        this.setSize(new java.awt.Dimension(486, 410));
        this.add(getMainViewPane(), java.awt.BorderLayout.CENTER);

    }

    protected void promptAndExit() {
        int result = FrameUtilities.yesNoCancelDialog(null, "Are you sure you want to exit PC^2?", "Exit PC^2");

        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * Reloads the Problem Combo Box.
     * 
     * Invoker is responsible for ensuring this is run on the AWT thread.
     */
    private void reloadProblems() {
        getProblemComboBox().removeAllItems();
        Problem problemN = new Problem("Select Problem");
        getProblemComboBox().addItem(problemN);

        for (Problem problem : getContest().getProblems()) {
            if (problem.isActive()){
                getProblemComboBox().addItem(problem);
            }
        }
    }

    /**
     * Reloads the Language Combo Box.
     * 
     * Invoker is responsible for ensuring this is run on the AWT thread.
     */
    private void reloadLanguages() {
        getLanguageComboBox().removeAllItems();
        Language languageN = new Language("Select Language");
        getLanguageComboBox().addItem(languageN);

        for (Language language : getContest().getLanguages()) {
            if (language.isActive()){
                getLanguageComboBox().addItem(language);
            }
        }
    }

    private void populateGUI() {
        reloadProblems();
        reloadLanguages();

        setButtonsActive(getContest().getContestTime().isContestRunning());
        if (!isTeam()) {
            setButtonsActive(true);
        }
    }

    private boolean isThisSite(int siteNumber) {
        return siteNumber == getContest().getSiteNumber();
    }

    protected boolean isTeam(ClientId id) {
        return id != null && id.getClientType().equals(ClientType.Type.TEAM);
    }

    protected boolean isTeam() {
        return isTeam(getContest().getClientId());
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
                if (isTeam()) {
                    // Only turn buttons on and off if a Team
                    getSubmitRunButton().setEnabled(turnButtonsOn);
                    getPickFileButton().setEnabled(turnButtonsOn);
                    getTestButton().setEnabled(turnButtonsOn);
                    getAddAdditionalFilesButton().setEnabled(turnButtonsOn);
                    getRemoveAdditionalFilesButton().setEnabled(turnButtonsOn);
                }
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
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
        }

        public void contestTimeRemoved(ContestTimeEvent event) {
        }

        public void contestTimeChanged(ContestTimeEvent event) {
        }

        public void contestStarted(ContestTimeEvent event) {
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
        }

        public void contestStopped(ContestTimeEvent event) {
            if (isThisSite(event.getSiteNumber())) {
                setButtonsActive(event.getContestTime().isContestRunning());
            }
        }

        public void refreshAll(ContestTimeEvent event) {
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

        public void problemChanged(final ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    int selectedIndex = getProblemComboBox().getSelectedIndex();
                    reloadProblems();
                    if (selectedIndex > -1) {
                        getProblemComboBox().setSelectedIndex(selectedIndex);
                    }
                }
            });
        }

        public void problemRemoved(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadProblems();
                }
            });
        }

        public void problemRefreshAll(ProblemEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadProblems();
                }
            });
        }
    }

    /**
     * Language Listener for SubmtitRunPane.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    private class LanguageListenerImplementation implements ILanguageListener {

        public void languageAdded(final LanguageEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    getLanguageComboBox().addItem(event.getLanguage());
                }
            });
        }

        public void languageChanged(LanguageEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    int selectedIndex = getLanguageComboBox().getSelectedIndex();
                    reloadLanguages();
                    if (selectedIndex > -1 && selectedIndex < getLanguageComboBox().getItemCount()) {
                        getLanguageComboBox().setSelectedIndex(selectedIndex);
                    }
                }
            });
        }

        public void languageRemoved(LanguageEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadLanguages();
                }
            });
        }

        public void languageRefreshAll(LanguageEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadLanguages();
                }
            });
        }

        @Override
        public void languagesAdded(LanguageEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadLanguages();
                }
            });
        }

        @Override
        public void languagesChanged(LanguageEvent event) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    reloadLanguages();
                }
            });
        }
    }

    /**
     * Auto populate problem, language and submitted file.
     *
     */
    protected void autoPopulate() {

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
        
        String [] paths = {"", "samps", "samps/src", "/usr/pc2/samps/src" };
        
        for (String dirname : paths) {
            String fullpath = dirname + "/" + "Sumit.java"; 
            if ("".equals(dirname)) {
                fullpath = "Sumit.java";
            }
            File file = new File(fullpath);
            if (file.exists()) {
                fileNameLabel.setText(fullpath);
            }
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
            mainViewPane.add(getAdditionalFilesPane(), null);
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
            submitRunButton.setEnabled(true);
            submitRunButton.setLocation(new java.awt.Point(356,350));
            submitRunButton.setSize(new java.awt.Dimension(100, 26));
            submitRunButton.setPreferredSize(new java.awt.Dimension(100, 26));
            submitRunButton.setMnemonic(java.awt.event.KeyEvent.VK_S);
            submitRunButton.setToolTipText("Submit run to judges");
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
     * Validates that the user has selected problem, language and a valid filename.
     * 
     * @param submitTheRun
     *            if true, submits the run.
     */
    protected void testOrSubmitRun(boolean submitTheRun) {

        Problem problem = ((Problem) getProblemComboBox().getSelectedItem());
        Language language = ((Language) getLanguageComboBox().getSelectedItem());
        SerializedFile [] otherFiles = null;

        if (getProblemComboBox().getSelectedIndex() < 1) {
            showMessage( "Please select problem");
            return;
        }

        if (getLanguageComboBox().getSelectedIndex() < 1) {
            showMessage( "Please select language");
            return;
        }

        String filename = fileNameLabel.getText();

        if (fileNameLabel.getText().equals("")) {
            showMessage( "Please select a Main file");
            return;
        }

        if (!fileExists(filename)) {
            File curdir = new File(".");

            String message = filename + " not found";
            try {
                message = message + " in " + curdir.getCanonicalPath();
            } catch (Exception e) {
                // ignore exception
                message = message + ""; // What a waste of time and code.
            }
            showMessage( message);
            return;
        }
        
        if (additionalFilesMCLB.getRowCount() > 0){
            try {
                otherFiles = getAdditionalSerializedFiles();
            } catch (Exception e) {
                showMessage( e.getMessage());
                log.log(Log.WARNING, "Exception logged ", e);
            }
        }

        if (submitTheRun) {
            try {
                String confirmQuestion = "<HTML><FONT SIZE=+1>Do you wish to submit run for<BR><BR>" + "Problem:  <FONT COLOR=BLUE>" + Utilities.forHTML(problem.toString()) + "</FONT><BR><BR>"
                        + "Language:  <FONT COLOR=BLUE>" + Utilities.forHTML(language.toString()) + "</FONT><BR><BR>" + "File: <FONT COLOR=BLUE>" + Utilities.forHTML(filename)
                        + "</FONT><BR><BR></FONT>";

                int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), confirmQuestion, "Confirm Submission");

                if (result == JOptionPane.YES_OPTION) {

                    log.info("submitRun for " + problem + " " + language + " file: " + filename);
                    getController().submitRun(problem, language, filename, otherFiles);
                }

            } catch (Exception e) {
                // TODO need to make this cleaner
                showMessage( "Exception " + e.getMessage());
            }
        } else {
            // Test run
            testRun(problem, language, filename, otherFiles);
        }
    }

    /**
     * Get AdditionalFiles from the MCLB.
     * 
     * @return 
     * @throws Exception 
     */
    private SerializedFile[] getAdditionalSerializedFiles() throws Exception {
        
        SerializedFile [] files = new SerializedFile[additionalFilesMCLB.getRowCount()];
        
        for (int i = 0; i < additionalFilesMCLB.getRowCount(); i ++){
            
            String filename = (String) additionalFilesMCLB.getRow(i)[0];
            SerializedFile file = new SerializedFile(filename);
            if (file.getBuffer() == null) {
                throw new Exception("Could not find/read file: "+filename);
            }
            files[i] = file;
        }
        
        return files;
    }

    /**
     * Execute a test run, show results to user.
     * 
     * @param problem
     * @param language
     * @param filename
     */
    private void testRun(Problem problem, Language language, String filename, SerializedFile [] additionalFiles) {

        String dataFileName = problem.getDataFileName();
        String message = "";
        boolean needsData = false;
        if (dataFileName != null) {
            if (dataFileName.trim().length() > 0 && !problem.isReadInputDataFromSTDIN()) {
                // verify presence of data file
                File sourceFile = new File(filename);
                File dataFile = new File(sourceFile.getParent() + File.separator + dataFileName);
                if (dataFile.exists()) {
                    if (dataFile.isFile()) {
                        if (!dataFile.canRead()) {
                            message = "Cannot read file.";
                            needsData = true;
                        }
                    } else {
                        message = "Is not a file";
                        needsData = true;
                    }
                } else {
                    message = "Cannot find file in " + sourceFile.getParent();
                    needsData = true;
                }
                if (needsData) {
                    JOptionPane.showMessageDialog(getParent(), "Problem with data file: " + dataFileName + "\n\n" + message, "Problem with data file", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        setButtonsActive(false);

        try {
            log.info("test run for " + problem + " " + language + " file: " + filename);
            Run run = new Run(getContest().getClientId(), language, problem);
            RunFiles runFiles = new RunFiles(run, new SerializedFile(filename), additionalFiles);

            executable = new Executable(getContest(), getController(), run, runFiles);
            if (! isTeam()){
                executable.setExecuteDirectoryNameSuffix("SR");
            }
            executable.setTestRunOnly(true);

            IFileViewer fileViewer;
            fileViewer = executable.execute();

            final IFileViewer finalViewer = fileViewer;

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    finalViewer.setTitle("Test Program Output");
                    finalViewer.setVisible(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace(System.err); // TODO remove stacktrace
            log.log(Log.SEVERE, "Exception during test run ", e);
        }

        setButtonsActive(true);
    }

    /**
     * This method initializes problemComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox<Problem> getProblemComboBox() {
        if (problemComboBox == null) {
            problemComboBox = new JComboBox<Problem>();
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
    private JComboBox<Language> getLanguageComboBox() {
        if (languageComboBox == null) {
            languageComboBox = new JComboBox<Language>();
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
            pickFileButton.setEnabled(true);
            pickFileButton.setMnemonic(java.awt.event.KeyEvent.VK_L);
            pickFileButton.setToolTipText("Select main file");
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
            chooser.setDialogTitle("Open Source Code File");
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File newFile = chooser.getSelectedFile().getCanonicalFile();
                boolean newFileProblem = true;
                if (newFile.exists()) {
                    if (newFile.isFile()) {
                        if (newFile.canRead()) {
                            lastOpenedFile = chooser.getCurrentDirectory().toString();
                            fileNameLabel.setText(newFile.getCanonicalFile().toString());
                            fileNameLabel.setToolTipText(newFile.getCanonicalFile().toString());
                            newFileProblem = false;
                        }
                    }
                }
                if (newFileProblem) {
                    log.warning("Problem reading new main file selection " + newFile.getCanonicalPath() + ", main file not changed");
                    JOptionPane.showMessageDialog(getParentFrame(), "Main file not changed, could not open file " + newFile, "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting selected file, try again.");
            e.printStackTrace(System.err);
            // getLog().log(Log.CONFIG, "Error getting selected file, try again.", e);
        }
        chooser = null;

    }

    private void updateGUIperPermissions() {

        // testButton.setVisible(isAllowed(Permission.Type.JUDGE_RUN));
        submitRunButton.setVisible(isAllowed(Permission.Type.SUBMIT_RUN));
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        this.log = getController().getLog();

        getContest().addContestTimeListener(new ContestTimeListenerImplementation());
        getContest().addLanguageListener(new LanguageListenerImplementation());
        getContest().addProblemListener(new ProblemListenerImplementation());

        // TODO add listeners for accounts, login and site.

        getContest().addAccountListener(new AccountListenerImplementation());
        // getContest().addLoginListener(new LoginListenerImplementation());
        // getContest().addSiteListener(new SiteListenerImplementation());

        initializePermissions();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI();
                updateGUIperPermissions();
                
//                setVisible(true);
            }
        });


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
            filenamePane.setLocation(new java.awt.Point(19, 144));
            filenamePane.setSize(new java.awt.Dimension(435, 55));
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
            problemPane.setLocation(new java.awt.Point(19, 17));
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
            languagePane.setLocation(new java.awt.Point(21, 81));
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
            testButton.setText("Test");
            testButton.setEnabled(true);
            testButton.setLocation(new java.awt.Point(21,350));
            testButton.setSize(new java.awt.Dimension(100, 26));
            testButton.setPreferredSize(new java.awt.Dimension(100, 26));
            testButton.setMnemonic(java.awt.event.KeyEvent.VK_T);
            testButton.setToolTipText("Test run");
            testButton.setVisible(true);
            testButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    new Thread(new Runnable() {
                        public void run() {
                            testOrSubmitRun(false);
                        }
                    }).start();
                }
            });
        }
        return testButton;
    }

    /**
     * Account Listener for SubmitRunPane.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    protected class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            // ignore doesn't affect this pane
        }

        public void accountModified(AccountEvent event) {
            // check if is this account
            Account account = event.getAccount();
            /**
             * If this is the account then update the GUI display per the potential change in Permissions.
             */
            if (getContest().getClientId().equals(account.getClientId())) {
                // They modified us!!
                initializePermissions();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateGUIperPermissions();
                    }
                });

            } // else nothing

        }

        public void accountsAdded(AccountEvent accountEvent) {
            // Will not apply to this pane, account already added for this user
        }

        public void accountsModified(AccountEvent accountEvent) {
            Account[] accounts = accountEvent.getAccounts();
            for (Account account : accounts) {
                if (getContest().getClientId().equals(account.getClientId())) {
                    initializePermissions();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            updateGUIperPermissions();
                        }
                    });
                }
            }
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {
            accountsModified (accountEvent);
        }
    }

    /**
     * This method initializes additionalFilesPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAdditionalFilesPane() {
        if (additionalFilesPane == null) {
            additionalFilesPane = new JPanel();
            additionalFilesPane.setLayout(new BorderLayout());
            additionalFilesPane.setBounds(new java.awt.Rectangle(20,217,438,117));
            additionalFilesPane.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray,1));
            additionalFilesPane.add(getAdditonalFilesButtonPane(), java.awt.BorderLayout.SOUTH);
            additionalFilesPane.add(getAdditionalFilesMCLB(), java.awt.BorderLayout.CENTER);
        }
        return additionalFilesPane;
    }

    /**
     * This method initializes additonalFilesButtonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAdditonalFilesButtonPane() {
        if (additonalFilesButtonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(100);
            additonalFilesButtonPane = new JPanel();
            additonalFilesButtonPane.setLayout(flowLayout);
            additonalFilesButtonPane.add(getAddAdditionalFilesButton(), null);
            additonalFilesButtonPane.add(getRemoveAdditionalFilesButton(), null);
        }
        return additonalFilesButtonPane;
    }

    /**
     * This method initializes addAdditionalFilesButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddAdditionalFilesButton() {
        if (addAdditionalFilesButton == null) {
            addAdditionalFilesButton = new JButton();
            addAdditionalFilesButton.setText("Add");
            addAdditionalFilesButton.setPreferredSize(new Dimension(100, 26));
            addAdditionalFilesButton.setToolTipText("Add an additional file");
            addAdditionalFilesButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addFile();
                }
            });
        }
        return addAdditionalFilesButton;
    }

    protected void addFile() {

        JFileChooser chooser = new JFileChooser(lastOpenedFile);
        try {
            chooser.setDialogTitle("Open Additional Source Code File");
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File newFile = chooser.getSelectedFile().getCanonicalFile();
                boolean newFileProblem = true;
                if (newFile.exists()) {
                    if (newFile.isFile()) {
                        if (newFile.canRead()) {
                            lastOpenedFile = chooser.getCurrentDirectory().toString();
                            String[] cols = new String[1];
                            cols[0] = newFile.getCanonicalFile().toString();
                            additionalFilesMCLB.addRow(cols);
                            additionalFilesMCLB.autoSizeAllColumns();
                            newFileProblem = false;
                        }
                    }
                }
                if (newFileProblem) {
                    log.warning("Problem reading additional file selection " + newFile.getCanonicalPath() + ", file not added");
                    JOptionPane.showMessageDialog(getParentFrame(), "File not added, could not open file " + newFile, "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting selected file, try again.");
            e.printStackTrace(System.err);
        }
        chooser = null;
    }

    /**
     * This method initializes removeAdditionalFilesButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRemoveAdditionalFilesButton() {
        if (removeAdditionalFilesButton == null) {
            removeAdditionalFilesButton = new JButton();
            removeAdditionalFilesButton.setText("Remove");
            removeAdditionalFilesButton.setPreferredSize(new Dimension(100, 26));
            removeAdditionalFilesButton.setToolTipText("remove selected additional file");
            removeAdditionalFilesButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    removeSelectedAdditionalFile();
                }
            });
        }
        return removeAdditionalFilesButton;
    }

    protected void removeSelectedAdditionalFile() {
        int selectedIndex = additionalFilesMCLB.getSelectedIndex();

        if (additionalFilesMCLB.getRowCount() < 0){
            showMessage("No files to remove");
            return;
        }
        
        if (selectedIndex < 0){
            showMessage("Select a file to remove");
            return;
        }
        
        additionalFilesMCLB.removeRow(selectedIndex);
    }

    private void showMessage(final String string) {
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(getParentFrame(), string);
            }
        });
    }

    /**
     * This method initializes additionalFilesMCLB
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private MCLB getAdditionalFilesMCLB() {
        if (additionalFilesMCLB == null) {
            additionalFilesMCLB = new MCLB();
            
            String [] cols ={"Additional Files" };
            
            additionalFilesMCLB.addColumns(cols);
            
            cols = null;
            additionalFilesMCLB.getColumnInfo(0).setWidth(400);
            
        }
        return additionalFilesMCLB;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
