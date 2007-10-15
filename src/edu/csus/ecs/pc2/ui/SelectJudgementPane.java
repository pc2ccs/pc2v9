package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.Executable;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.DisplayTeamName;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;

/**
 * Add/Edit Run Pane
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// $Id$
public class SelectJudgementPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 4560827389735037513L;

    private JPanel messagePane = null;

    private JPanel buttonPane = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private JLabel messageLabel = null;

    private Run run = null;

    private RunFiles runFiles = null;

    private JPanel generalPane = null;

    private Log log = null;

    private JButton executeButton = null;

    private JButton viewSourceButton = null;

    private JButton extractButton = null;

    private Executable executable;

    private JComboBox judgementComboBox = null;

    private JLabel runInfoLabel = null;

    private JLabel judgementLabel = null;

    private JLabel statusLabel = null;

    private JLabel statusTitleLabel = null;

    private JLabel problemTitleLabel = null;

    private JLabel languageTitleLabel = null;

    private JLabel elapsedTitleLabel = null;

    private IFileViewer sourceViewer;

    private IFileViewer answerFileViewer;

    private IFileViewer dataFileViewer;

    private boolean populatingGUI = true;

    private JLabel problemNameLabel = null;

    private JLabel languageNameLabel = null;

    private JLabel elapsedTimeLabel = null;

    private JCheckBox notifyTeamCheckBox = null;

    private PermissionList permissionList = new PermissionList();

    private JLabel validatorRecommendsLabel = null;

    private JLabel validatorJudgementLabel = null;

    private JButton acceptValidatorJudgementButton = null;

    private JPanel eastPane = null;

    private JButton viewDataFileButton = null;

    private JButton viewAnswerFileButton = null;

    private JButton shellButton = null;

    private DisplayTeamName displayTeamName = null;

    private IFileViewer executableFileViewer;

    /**
     * This method initializes
     * 
     */
    public SelectJudgementPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(666,294));

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getGeneralPane(), java.awt.BorderLayout.CENTER);
        this.add(getEastPane(), java.awt.BorderLayout.EAST);
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);
        log = getController().getLog();

        displayTeamName = new DisplayTeamName();
        displayTeamName.setContestAndController(inContest, inController);

        initializePermissions();

    }

    public String getPluginTitle() {
        return "Edit Run Pane";
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
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePane = new JPanel();
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new java.awt.Dimension(25, 25));
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(50);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.add(getUpdateButton(), null);
            buttonPane.add(getExecuteButton(), null);
            buttonPane.add(getExtractButton(), null);
            buttonPane.add(getCancelButton(), null);
        }
        return buttonPane;
    }

    private Run getRunFromFields() {

        return run;
    }

    /**
     * This method initializes updateButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdateButton() {
        if (updateButton == null) {
            updateButton = new JButton();
            updateButton.setText("Update");
            updateButton.setEnabled(false);
            updateButton.setMnemonic(java.awt.event.KeyEvent.VK_U);
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateRun();
                }
            });
        }
        return updateButton;
    }

    private void cancelRun() {

        enableUpdateButtons(false);
        closeViewerWindows();
        Run newRun = getRunFromFields();
        getController().cancelRun(newRun);

    }

    private void closeViewerWindows() {
        closeViewer(dataFileViewer);
        closeViewer(answerFileViewer);
        closeViewer(sourceViewer);
        closeViewer(executableFileViewer);
    }
    
    private void closeViewer(IFileViewer fileViewer) {
        if (fileViewer != null) {
            fileViewer.dispose();
        }
    }

    protected void updateRun() {

        Run newRun = getRunFromFields();

        enableUpdateButtons(false);
        closeViewerWindows();
        
        JudgementRecord judgementRecord = null;
        RunResultFiles runResultFiles = null;

        if (judgementChanged()) {
            newRun.setStatus(RunStates.JUDGED);

            boolean solved = getJudgementComboBox().getSelectedIndex() == 0;
            Judgement judgement = (Judgement) getJudgementComboBox().getSelectedItem();

            judgementRecord = new JudgementRecord(judgement.getElementId(), getContest().getClientId(), solved, false);
            judgementRecord.setSendToTeam(getNotifyTeamCheckBox().isSelected());

        }

        getController().submitRunJudgement(newRun, judgementRecord, runResultFiles);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    /**
     * This method initializes cancelButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleCancelButton();
                }
            });
        }
        return cancelButton;
    }

    public void handleCancelButton() {

        if (getUpdateButton().isEnabled()) {

            // Something changed, are they sure ?

            int result = FrameUtilities.yesNoCancelDialog("Run modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                updateRun();
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            }
            if (result == JOptionPane.NO_OPTION) {
                cancelRun();
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            }

        } else {
            cancelRun();
            if (getParentFrame() != null) {
                getParentFrame().setVisible(false);
            }
        }
    }

    public Run getRun() {
        return run;
    }

    public void setRun(final Run run) {
        ContestInformation contestInformation = getContest().getContestInformation();
        displayTeamName.setTeamDisplayMask(contestInformation.getTeamDisplayMode());

        this.run = run;

        showMessage("Waiting for run...");
        FrameUtilities.waitCursor(this);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(run);
                enableUpdateButtons(false);
            }
        });
    }

    private void populateGUI(Run theRun) {

        populatingGUI = true;

        showValidatorControls(false);

        if (theRun != null) {
            getUpdateButton().setVisible(true);

            String teamName = getTeamDisplayName(theRun.getSubmitter());

            runInfoLabel.setText("Run " + theRun.getNumber() + " (Site " + theRun.getSiteNumber() + ") from " + teamName);
            statusLabel.setText(run.getStatus().toString());
            elapsedTimeLabel.setText(new Long(run.getElapsedMins()).toString());

            problemNameLabel.setText(getContest().getProblem(run.getProblemId()).toString());
            languageNameLabel.setText(getContest().getLanguage(run.getLanguageId()).toString());
            boolean showFile ;
            if (getProblemDataFiles() != null && getProblemDataFiles().getJudgesDataFile() != null) {
                showFile = true;
            } else {
                showFile = false;
            }
            getViewAnswerFileButton().setVisible(showFile);
            if (getProblemDataFiles() != null && getProblemDataFiles().getJudgesDataFile() != null) {
                showFile = true;
            } else {
                showFile = false;
            }
            getViewDataFileButton().setVisible(showFile);
            getViewSourceButton().setVisible(true);

        } else {
            getUpdateButton().setVisible(false);

            runInfoLabel.setText("Could not get run " + +theRun.getNumber() + " (Site " + theRun.getSiteNumber() + ")");
            statusLabel.setText("");
            elapsedTimeLabel.setText("");

            problemNameLabel.setText("");
            languageNameLabel.setText("");
            getViewAnswerFileButton().setVisible(false);
            getViewDataFileButton().setVisible(false);
            getViewSourceButton().setVisible(false);

        }
        populateComboBoxes();

        populatingGUI = false;

    }

    private void populateComboBoxes() {

        int selectedIndex = -1;
        int index = 0;

        getJudgementComboBox().removeAllItems();

        if (run == null) {
            return; // No run no combo boxes.
        }

        selectedIndex = -1;
        index = 0;

        ElementId judgementId = null;

        if (run.isJudged()) {
            judgementId = run.getJudgementRecord().getJudgementId();
        }

        for (Judgement judgement : getContest().getJudgements()) {
            getJudgementComboBox().addItem(judgement);
            if (judgement.getElementId().equals(judgementId)) {
                selectedIndex = index;
            }
            index++;
        }

        getJudgementComboBox().setSelectedIndex(selectedIndex);

    }

    protected void enableUpdateButtons(boolean editedText) {
        if (editedText) {
            cancelButton.setText("Cancel");
        } else {
            cancelButton.setText("Close");
        }

        // Can only run or extract if there are run files...
        getExecuteButton().setEnabled(runFiles != null);
        getExecuteButton().setEnabled(runFiles != null);
        getViewSourceButton().setEnabled(runFiles != null);

        if (runFiles == null) {
            log.log(Log.WARNING, "No run files in requested run " + run);
            showMessage("No Run Files in requested run");
        }

        updateButton.setEnabled(editedText);
    }

    /**
     * Enable or disable Update button based on comparison of run to fields.
     * 
     */
    public void enableUpdateButton() {

        if (populatingGUI) {
            return;
        }

        boolean enableButton = false;

        if (run != null) {
            enableButton |= judgementChanged();
        }

        enableUpdateButtons(enableButton);
    }

    private boolean judgementChanged() {
        if (run.isJudged()) {

            Judgement judgement = (Judgement) getJudgementComboBox().getSelectedItem();
            if (judgement != null) {
                return !run.getJudgementRecord().getJudgementId().equals(judgement.getElementId());
            }

        } else if (getJudgementComboBox().getSelectedIndex() > -1) {
            return true;
        }

        return false;
    }

    /**
     * This method initializes generalPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGeneralPane() {
        if (generalPane == null) {
            validatorJudgementLabel = new JLabel();
            validatorJudgementLabel.setBounds(new java.awt.Rectangle(171, 171, 282, 19));
            validatorJudgementLabel.setText("Validator Judgement");
            validatorJudgementLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            validatorJudgementLabel.setForeground(Color.BLUE);
            validatorRecommendsLabel = new JLabel();
            validatorRecommendsLabel.setBounds(new java.awt.Rectangle(17, 171, 142, 19));
            validatorRecommendsLabel.setText("Validator Recommends");
            validatorRecommendsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            validatorRecommendsLabel.setForeground(Color.BLUE);
            elapsedTimeLabel = new JLabel();
            elapsedTimeLabel.setBounds(new java.awt.Rectangle(171, 60, 254, 19));
            elapsedTimeLabel.setText("Elapsed");
            elapsedTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            languageNameLabel = new JLabel();
            languageNameLabel.setBounds(new java.awt.Rectangle(171, 110, 254, 19));
            languageNameLabel.setText("Language");
            languageNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            problemNameLabel = new JLabel();
            problemNameLabel.setBounds(new java.awt.Rectangle(171, 85, 254, 19));
            problemNameLabel.setText("Problem");
            problemNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            elapsedTitleLabel = new JLabel();
            elapsedTitleLabel.setBounds(new java.awt.Rectangle(17, 60, 142, 19));
            elapsedTitleLabel.setText("Elapsed");
            elapsedTitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            languageTitleLabel = new JLabel();
            languageTitleLabel.setBounds(new java.awt.Rectangle(17, 110, 142, 19));
            languageTitleLabel.setText("Language");
            languageTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            problemTitleLabel = new JLabel();
            problemTitleLabel.setBounds(new java.awt.Rectangle(17, 85, 142, 19));
            problemTitleLabel.setText("Problem");
            problemTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            statusTitleLabel = new JLabel();
            statusTitleLabel.setBounds(new java.awt.Rectangle(17, 32, 142, 19));
            statusTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            statusTitleLabel.setText("Status");
            statusLabel = new JLabel();
            statusLabel.setBounds(new java.awt.Rectangle(171, 32, 254, 19));
            statusLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            statusLabel.setText("JLabel");
            judgementLabel = new JLabel();
            judgementLabel.setBounds(new java.awt.Rectangle(17, 137, 142, 19));
            judgementLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            judgementLabel.setText("Judgement");
            runInfoLabel = new JLabel();
            runInfoLabel.setBounds(new java.awt.Rectangle(5, -1, 424, 25));
            runInfoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            runInfoLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            runInfoLabel.setText("Run Info");
            generalPane = new JPanel();
            generalPane.setLayout(null);
            generalPane.add(getJudgementComboBox(), null);
            generalPane.add(runInfoLabel, null);
            generalPane.add(judgementLabel, null);
            generalPane.add(statusLabel, null);
            generalPane.add(statusTitleLabel, null);
            generalPane.add(problemTitleLabel, null);
            generalPane.add(languageTitleLabel, null);
            generalPane.add(elapsedTitleLabel, null);
            generalPane.add(problemNameLabel, null);
            generalPane.add(languageNameLabel, null);
            generalPane.add(elapsedTimeLabel, null);
            generalPane.add(getNotifyTeamCheckBox(), null);
            generalPane.add(validatorRecommendsLabel, null);
            generalPane.add(validatorJudgementLabel, null);
            generalPane.add(getAcceptValidatorJudgementButton(), null);
        }
        return generalPane;
    }

    public void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(message);
            }
        });
    }

    /**
     * This method initializes executeButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getExecuteButton() {
        if (executeButton == null) {
            executeButton = new JButton();
            executeButton.setText("Execute");
            executeButton.setMnemonic(java.awt.event.KeyEvent.VK_X);
            executeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    new Thread(new Runnable() {
                        public void run() {
                            executeRun();
                        }
                    }).start();
                }
            });
        }
        return executeButton;
    }

    /**
     * This method initializes viewSourceButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getViewSourceButton() {
        if (viewSourceButton == null) {
            viewSourceButton = new JButton();
            viewSourceButton.setText("View Source");
            viewSourceButton.setMnemonic(java.awt.event.KeyEvent.VK_V);
            viewSourceButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    viewSourceFile();
                }
            });
        }
        return viewSourceButton;
    }

    /**
     * This method initializes extractButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getExtractButton() {
        if (extractButton == null) {
            extractButton = new JButton();
            extractButton.setText("Extract");
            extractButton.setMnemonic(java.awt.event.KeyEvent.VK_T);
            extractButton.setVisible(false);
            extractButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    extractRun();
                }
            });
        }
        return extractButton;
    }

    protected void extractRun() {
        showMessage("Would have extracted run");
        // TODO code extract run
    }

    protected void executeRun() {

        System.gc();

        executable = new Executable(getContest(), getController(), run, runFiles);

        if (executableFileViewer != null) {
            executableFileViewer.dispose();
        }
        executableFileViewer = executable.execute();

        // Show validator results, if there are any.

        showValidatorControls(false);
        if (executable.isValidationSuccess()) {
            String results = executable.getValidationResults();
            if (results != null && results.trim().length() > 1) {
                validatorJudgementLabel.setText(executable.getValidationResults());
                showValidatorControls(true);
            } else {
                log.warning("execute indicated validator success but getValidationResults returns \"\" or null");
            }
        }

        executableFileViewer.setVisible(true);
    }

    public void setRunAndFiles(Run theRun, RunFiles runFiles2) {

        FrameUtilities.regularCursor(this);

        showMessage("");
        log.info("Fetched run " + theRun + " to edit");

        run = theRun;
        runFiles = runFiles2;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(run);
                enableUpdateButtons(false);
            }
        });

    }

    /**
     * This method initializes judgementComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getJudgementComboBox() {
        if (judgementComboBox == null) {
            judgementComboBox = new JComboBox();
            judgementComboBox.setLocation(new java.awt.Point(171, 135));
            judgementComboBox.setSize(new java.awt.Dimension(263, 22));
            judgementComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return judgementComboBox;
    }

    private String getExecuteDirectoryName() {
        Executable tempEexecutable = new Executable(getContest(), getController(), run, runFiles);
        return tempEexecutable.getExecuteDirectoryName();
    }

    private void createAndViewFile(IFileViewer fileViewer, SerializedFile file, String title) {
        // TODO the executable dir name should be from the model, eh ?
        String targetDirectory = getExecuteDirectoryName();
        Utilities.insureDir(targetDirectory);
        String targetFileName = targetDirectory + File.separator + file.getName();
        try {
            file.writeFile(targetFileName);

            if (new File(targetFileName).isFile()) {
                fileViewer.addFilePane(title, targetFileName);
                fileViewer.setVisible(true);
            } else {
                fileViewer.addTextPane(title, "Could not create file at " + targetFileName);
                fileViewer.setVisible(true);
            }
        } catch (IOException e) {
            fileViewer.addTextPane(title, "Could not create file at " + targetFileName + "Exception " + e.getMessage());
            fileViewer.setVisible(true);
        }
    }

    /**
     * This method initializes notifyTeamCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getNotifyTeamCheckBox() {
        if (notifyTeamCheckBox == null) {
            notifyTeamCheckBox = new JCheckBox();
            notifyTeamCheckBox.setBounds(new java.awt.Rectangle(171, 199, 112, 21));
            notifyTeamCheckBox.setSelected(true);
            notifyTeamCheckBox.setText("Notify Team");
        }
        return notifyTeamCheckBox;
    }

    private String getTeamDisplayName(ClientId clientId) {
        if (isJudge() && isTeam(clientId)){
            return displayTeamName.getDisplayName(clientId);
        }

        return clientId.getName();
    }

    private boolean isAllowed(Permission.Type type) {
        return permissionList.isAllowed(type);
    }

    private boolean isTeam(ClientId clientId) {
        return clientId == null || clientId.getClientType().equals(Type.TEAM);
    }
    
    private boolean isJudge(ClientId clientId) {
        return clientId == null || clientId.getClientType().equals(Type.JUDGE);
    }
    
    private boolean isJudge(){
        return isJudge(getContest().getClientId());
    }

    private void initializePermissions() {
        Account account = getContest().getAccount(getContest().getClientId());
        if (account != null) {
            permissionList.clearAndLoadPermissions(account.getPermissionList());
        }
    }

    /**
     * Update GUI permissions.
     * 
     * Invoked if account permission changes.
     */
    private void updateGUIperPermissions() {

        extractButton.setVisible(isAllowed(Permission.Type.EXTRACT_RUNS));
    }

    /**
     * Account Listener Implementation.
     * 
     * @author pc2@ecs.csus.edu
     */
    public class AccountListenerImplementation implements IAccountListener {

        public void accountAdded(AccountEvent accountEvent) {
            // ignore
        }

        public void accountModified(AccountEvent accountEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateGUIperPermissions();
                }
            });
        }
    }

    /**
     * This method initializes acceptValidatorJudgementButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAcceptValidatorJudgementButton() {
        if (acceptValidatorJudgementButton == null) {
            acceptValidatorJudgementButton = new JButton();
            acceptValidatorJudgementButton.setBounds(new java.awt.Rectangle(313, 199, 140, 23));
            acceptValidatorJudgementButton.setText("Accept Validator");
            acceptValidatorJudgementButton.setForeground(Color.BLUE);
            acceptValidatorJudgementButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    acceptValidatorJudgement();
                }
            });
        }
        return acceptValidatorJudgementButton;
    }

    private void showValidatorControls(boolean showControls) {
        getAcceptValidatorJudgementButton().setVisible(showControls);
        validatorJudgementLabel.setVisible(showControls);
        validatorRecommendsLabel.setVisible(showControls);
    }

    protected void acceptValidatorJudgement() {

        Run newRun = getRunFromFields();

        enableUpdateButtons(false);

        closeViewerWindows();
        RunResultFiles runResultFiles = null;

        JudgementRecord judgementRecord = null;

        String results = validatorJudgementLabel.getText();

        boolean solved = false;

        // Try to find result text in judgement list
        ElementId elementId = getContest().getJudgements()[1].getElementId();
        for (Judgement judgement : getContest().getJudgements()) {
            if (judgement.getDisplayName().equals(results)) {
                elementId = judgement.getElementId();
            }
        }

        // Or perhaps it is a yes? yes?
        Judgement yesJudgement = getContest().getJudgements()[0];
        if (yesJudgement.getDisplayName().equalsIgnoreCase(results)) {
            elementId = yesJudgement.getElementId();
            solved = true;
        }

        newRun.setStatus(RunStates.JUDGED);

        judgementRecord = new JudgementRecord(elementId, getContest().getClientId(), solved, true);
        judgementRecord.setValidatorResultString(results);

        judgementRecord.setSendToTeam(getNotifyTeamCheckBox().isSelected());

        getController().submitRunJudgement(newRun, judgementRecord, runResultFiles);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }

    }

    /**
     * This method initializes eastPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getEastPane() {
        if (eastPane == null) {
            FlowLayout flowLayout1 = new FlowLayout();
            flowLayout1.setVgap(25);
            eastPane = new JPanel();
            eastPane.setLayout(flowLayout1);
            eastPane.setPreferredSize(new java.awt.Dimension(132, 132));
            eastPane.add(getViewSourceButton(), null);
            eastPane.add(getViewDataFileButton(), null);
            eastPane.add(getViewAnswerFileButton(), null);
            eastPane.add(getShellButton(), null);
        }
        return eastPane;
    }

    /**
     * This method initializes viewDataFile
     * 
     * @return javax.swing.JButton
     */
    private JButton getViewDataFileButton() {
        if (viewDataFileButton == null) {
            viewDataFileButton = new JButton();
            viewDataFileButton.setText("View Data File");
            viewDataFileButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    viewDataFile();
                }
            });
        }
        return viewDataFileButton;
    }

    /**
     * This method initializes viewAnswerFile
     * 
     * @return javax.swing.JButton
     */
    private JButton getViewAnswerFileButton() {
        if (viewAnswerFileButton == null) {
            viewAnswerFileButton = new JButton();
            viewAnswerFileButton.setText("View Answer File");
            viewAnswerFileButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    viewAnswerFile();
                }
            });
        }
        return viewAnswerFileButton;
    }

    private ProblemDataFiles getProblemDataFiles() {
        Problem problem = getContest().getProblem(run.getProblemId());
        return getContest().getProblemDataFile(problem);
    }

    protected void viewSourceFile() {
        if (sourceViewer != null) {
            sourceViewer.dispose();
        }
        sourceViewer = new MultipleFileViewer(getController().getLog());
        createAndViewFile(sourceViewer, runFiles.getMainFile(), "Team's source");
    }

    protected void viewDataFile() {
        if (getProblemDataFiles() != null) {
            if (getProblemDataFiles().getJudgesDataFile() != null) {
                if (dataFileViewer != null) {
                    dataFileViewer.dispose();
                }
                dataFileViewer = new MultipleFileViewer(getController().getLog());
                createAndViewFile(dataFileViewer, getProblemDataFiles().getJudgesDataFile(), "Judge's data file");
            } else {
                JOptionPane.showMessageDialog(this, "No data file defined");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No data file defined");
        }
    }

    protected void viewAnswerFile() {
        if (getProblemDataFiles() != null) {
            if (getProblemDataFiles().getJudgesAnswerFile() != null) {
                if (answerFileViewer != null) {
                    answerFileViewer.dispose();
                }
                answerFileViewer = new MultipleFileViewer(getController().getLog());
                createAndViewFile(answerFileViewer, getProblemDataFiles().getJudgesAnswerFile(), "Judge's answer file");
            } else {
                JOptionPane.showMessageDialog(this, "No Answer File defined");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No Answer File defined");
        }
    }

    /**
     * This method initializes shellButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getShellButton() {
        if (shellButton == null) {
            shellButton = new JButton();
            shellButton.setText("Shell ");
            shellButton.setVisible(false);
            shellButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    shellToExecuteDirectory();
                }
            });
        }
        return shellButton;
    }

    protected void shellToExecuteDirectory() {
        String executeDir = getExecuteDirectoryName();
        File runDir = new File(executeDir);
        Utilities.insureDir(executeDir);

        String fs = File.separator;

        String winShellCommand = fs + "windows" + fs + "system32" + fs + "cmd.exe";
        String unixShellCommand = fs + "bin" + fs + "sh";

        String[] env = null;

        if (new File(winShellCommand).exists()) {

            // System.out.println("debug "+winShellCommand+" to "+executeDir);

            try {
                Runtime.getRuntime().exec("cmd.exe", env, runDir);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Unable to run command " + winShellCommand + " " + e.getMessage());
                e.printStackTrace();
            }
        } else if (new File(unixShellCommand).exists()) {

            // System.out.println("debug "+unixShellCommand+" to "+executeDir);

            try {
                Runtime.getRuntime().exec(unixShellCommand, env, runDir);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Unable to run command " + unixShellCommand + " " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
