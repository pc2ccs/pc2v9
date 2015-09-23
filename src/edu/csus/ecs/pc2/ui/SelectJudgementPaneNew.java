package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.Executable;
import edu.csus.ecs.pc2.core.execute.ExecutionData;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.AccountEvent;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.DisplayTeamName;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IAccountListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IJudgementListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementEvent;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.ui.judge.JudgeView;

/**
 * Select a Judgement Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SelectJudgementPaneNew extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 4560827389735037513L;

    private JPanel messagePanel = null;

    private JPanel buttonPanel = null;

    private JButton acceptChosenSelectionButton = null;

    private JButton cancelButton = null;

    private JLabel messageLabel = null;

    private Run run = null;

    /**
     * Submitted run files from user.
     * 
     * This is used as an indicator as to whether this client/judge has a checked out run.
     */
    private RunFiles runFiles = null;

    private RunResultFiles[] runResultFiles = null;

    private JPanel runInfoPanel = null;

    private Log log = null;

    private JButton executeButton = null;

    private JButton viewSourceButton = null;

    private JButton extractButton = null;

    private Executable executable;

    private JComboBox<Judgement> judgementComboBox = null;

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

    private JButton acceptValidatorJudgementButton = null;

    private JButton shellButton = null;

    private DisplayTeamName displayTeamName = null;

    private JPanel mainPanel = null;

    private JPanel assignJudgementPanel = null;

    private JLabel runInfoLabel = null;

    private JButton detailsButton = null;

    private JLabel validatorAnswerLabel = null;

    private JLabel validatorAnswer = null;

    private JLabel selectJudgementCheckboxLabel = null;

    private JButton viewOutputsButton = null;

    private GregorianCalendar startTimeCalendar;
    
    private long executeTimeMS = 0;

    private MultiTestSetOutputViewerFrame multiTestSetOutputViewerFrame = null;

    /**
     * Saved team output names.
     */
    private List<String> saveOutputFileNames = null;

    /**
     * saved validator output names
     */
    private List<String> saveValidatorOutputFileNames = null;
    
    /**
     * saved validator stderr names
     */
    private List<String> saveValidatorErrFileNames = null;
    
    /**
     * This method initializes
     * 
     */
    public SelectJudgementPaneNew() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(10);
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(800,400));

        this.setMaximumSize(new java.awt.Dimension(32767,32767));
        this.setMinimumSize(new java.awt.Dimension(800,100));
        this.setPreferredSize(new java.awt.Dimension(800,400));
        this.add(getMessagePanel(), java.awt.BorderLayout.NORTH);
        this.add(getMainPanel(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        log = getController().getLog();

        displayTeamName = new DisplayTeamName();
        displayTeamName.setContestAndController(inContest, inController);

        //getComputerJudgementPanel().setContestAndController(getContest(), getController());
        //getManualRunResultsPanel().setContestAndController(getContest(), getController());

        initializePermissions();
        
        getContest().addJudgementListener(new JudgementListenerImplementation());
    }

    public String getPluginTitle() {
        return "Select Judgement Pane";
    }

    /**
     * This method initializes messagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePanel() {
        if (messagePanel == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setPreferredSize(new java.awt.Dimension(25, 25));
            messagePanel.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePanel;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            flowLayout.setVgap(20);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(flowLayout);
            buttonPanel.add(getExtractButton(), null);
            buttonPanel.add(getViewSourceButton(), null);
            buttonPanel.add(getViewOutputsButton(), null);
            buttonPanel.add(getDetailsButton(), null);
            buttonPanel.add(getCancelButton(), null);
        }
        return buttonPanel;
    }

    private Run getRunFromFields() {

        return run;
    }

    /**
     * This method initializes updateButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAcceptChosenSelectionButton() {
        if (acceptChosenSelectionButton == null) {
            acceptChosenSelectionButton = new JButton();
            acceptChosenSelectionButton.setText("Accept Selected");
            acceptChosenSelectionButton.setEnabled(false);
            acceptChosenSelectionButton.setActionCommand("Ok");
            acceptChosenSelectionButton.setBounds(new java.awt.Rectangle(480,120,152,26));
            acceptChosenSelectionButton.setLocation(new java.awt.Point(555,98));
            acceptChosenSelectionButton.setSize(new java.awt.Dimension(165,37));
            acceptChosenSelectionButton.setMnemonic(java.awt.event.KeyEvent.VK_O);
            acceptChosenSelectionButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (confirmInconsistentJudgements()) {
                        updateRun();
                    }
                }
            });
        }
        return acceptChosenSelectionButton;
    }

    private void cancelRun() {

        enableUpdateButtons(false);
        closeViewerWindows();
        Run newRun = getRunFromFields();
        JudgeView.setAlreadyJudgingRun(false);

        if (runFiles != null) {
            // Cancel/return run only if the run has actually been checked out.
            getController().cancelRun(newRun);
        }
    }

    /**
     * Compares the validatorJudgemnt (if available) and the manualJudgement.
     * 
     * @return True to continue the Update, else false
     */
    private boolean confirmInconsistentJudgements() {
        if (getAcceptValidatorJudgementButton().isVisible()) {
            ElementId elementId = getValidatorResultElementID(validatorAnswer.getText());
            Judgement manualJudgement = (Judgement) getJudgementComboBox().getSelectedItem();
            Judgement autoJudgement = getContest().getJudgement(elementId);
            if (!manualJudgement.equals(autoJudgement)) {
                String message = "You selected '" + manualJudgement + "'\n but the validator returned '" + validatorAnswer.getText() + "'."
                        + "\nDid you intend to assign a different result than the validator?" + "\n(Click Yes to accept your selection; click No to cancel)";
                int result = JOptionPane.showConfirmDialog(this, message);
                /*               
                 *          Rather than forcing "no" to mean "cancel", a better option is to allow the user to choose
                 *          between Yes, No, and Cancel as different meanings.  The following code will display an OptionPane
                 *          with three different options; the "message" above needs to be changed, and the CALLING CODE
                 *          needs to be changed to get back an indication of which of the three options was selected (rather
                 *          than a boolean...)
                 *           Object [] options = new Object [] {new JButton("Yes, accept my selection"), 
                 new JButton("No, accept validator recommendation"),
                 new JButton("Cancel")};
                 int result = JOptionPane.showOptionDialog(this, message, "Confirm Change", JOptionPane.YES_NO_CANCEL_OPTION, 
                 JOptionPane.QUESTION_MESSAGE, null, options, null );
                 */
                if (result != JOptionPane.YES_OPTION) {
                    return false;
                }
            }
        }
        return true;
    }

    private void closeViewerWindows() {
        closeViewer(dataFileViewer);
        closeViewer(answerFileViewer);
        closeViewer(sourceViewer);
        // because mtsovf is not a IFileViewer
        if (multiTestSetOutputViewerFrame != null) {
            multiTestSetOutputViewerFrame.dispose();
        }
        //getManualRunResultsPanel().closeViewerWindows();
        //getComputerJudgementPanel().closeViewerWindows();
    }

    private void closeViewer(IFileViewer fileViewer) {
        if (fileViewer != null) {
            fileViewer.dispose();
        }
    }

    protected void updateRun() {
        try {
            Run newRun = getRunFromFields();
    
            enableUpdateButtons(false);
            closeViewerWindows();
    
            JudgementRecord judgementRecord = null;
            RunResultFiles newRunResultFiles = null;
    
            if (judgementChanged()) {
                newRun.setStatus(RunStates.JUDGED);
    
                boolean solved = getJudgementComboBox().getSelectedIndex() == 0;
                Judgement judgement = (Judgement) getJudgementComboBox().getSelectedItem();
    
                judgementRecord = new JudgementRecord(judgement.getElementId(), getContest().getClientId(), solved, false);
                judgementRecord.setSendToTeam(getNotifyTeamCheckBox().isSelected());
                TimeZone tz = TimeZone.getTimeZone("GMT");
                GregorianCalendar cal = new GregorianCalendar(tz);
    
                long milliDiff = cal.getTime().getTime() - startTimeCalendar.getTime().getTime();
                long totalSeconds = milliDiff / 1000;
                judgementRecord.setHowLongToJudgeInSeconds(totalSeconds);
            }
    
            JudgeView.setAlreadyJudgingRun(false);
    
            ExecutionData executionData = null;
            if (executable != null) {
                executionData = executable.getExecutionData();
                if (judgementRecord != null) {
                    judgementRecord.setExecuteMS(executionData.getExecuteTimeMS());
                }
            }
    
            newRunResultFiles = new RunResultFiles(newRun, newRun.getProblemId(), judgementRecord, executionData);
    
            getController().submitRunJudgement(newRun, judgementRecord, newRunResultFiles);
    
            if (getParentFrame() != null) {
                getParentFrame().setVisible(false);
            }
        } catch (Exception e) {
            log.log(Log.WARNING,"updateRun()", e);
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

        if (runFiles == null) {
            JudgeView.setAlreadyJudgingRun(false);
            if (getParentFrame() != null) {
                getParentFrame().setVisible(false);
            }
            return;
        }

        if (getAcceptChosenSelectionButton().isEnabled()) {

            // Something changed, are they sure ?

            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Run modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                if (confirmInconsistentJudgements()) {
                    updateRun();
                    if (getParentFrame() != null) {
                        getParentFrame().setVisible(false);
                    }
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
        startTimeCalendar = null; // time does not start until we get the run checkout
        showMessage("Waiting for run...");
        FrameUtilities.waitCursor(this);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(run);
                enableUpdateButtons(false);
                enableOutputsButton(false);
                showValidatorControls(false);
            }
        });
    }

    private void populateGUI(Run theRun) {

        populatingGUI = true;

        if (theRun != null) {
            
            getAcceptChosenSelectionButton().setVisible(true);

            String teamName = getTeamDisplayName(theRun.getSubmitter());

            //populate the Run Info panel fields
            runInfoLabel.setText("Run " + theRun.getNumber() + " (Site " + theRun.getSiteNumber() + ") from " + teamName);
            statusLabel.setText(run.getStatus().toString());
            elapsedTimeLabel.setText(new Long(run.getElapsedMins()).toString());
            problemNameLabel.setText(getContest().getProblem(run.getProblemId()).toString());
            languageNameLabel.setText(getContest().getLanguage(run.getLanguageId()).toString());
            
            //show the View Source button 
            getViewSourceButton().setVisible(true);

            // get the computer-assigned preliminary judgement record (if any)
            JudgementRecord computerJudgement = theRun.getComputerJudgementRecord();

            // if there IS a computer judgement, try to find a corresponding RunResultFile record
            RunResultFiles matchingResult = null;
            if (computerJudgement != null) {
                // search the RunResultFiles array for a matching result
                if (runResultFiles != null) {
                    for (int i = 0; i < runResultFiles.length; i++) {
                        if (runResultFiles[i].getJudgementId().equals(computerJudgement.getJudgementId())) {
                            matchingResult = runResultFiles[i];
                            break;
                        }
                    }
                }
            } 
            
            //if there is a result matching the computer judgement (validator), show it 
            if (matchingResult != null) {
                // RunResultsFiles.getValidationResults() returns null for the Undetermined case
                String judgement = computerJudgement.getValidatorResultString();
                String oJudgement = getContest().getJudgement(matchingResult.getJudgementId()).toString();
                if (judgement == null || judgement.trim().equals("")) {
                    judgement = oJudgement;
                }
                // TODO bug 394 color depending on yes or no or unknown judgement (black)
                validatorAnswer.setText(judgement);
                showValidatorControls(true, computerJudgement);
            } else {
                showValidatorControls(false);
            }

        } else { //the run was null
            
            getAcceptChosenSelectionButton().setVisible(false);

            runInfoLabel.setText("Could not get run");
            statusLabel.setText("");
            elapsedTimeLabel.setText("");
            problemNameLabel.setText("");
            languageNameLabel.setText("");
            
            getViewSourceButton().setVisible(false);
            showValidatorControls(false);

        }
        reloadComboBoxes();

        populatingGUI = false;

    }

    protected void regularCursor() {
        FrameUtilities.regularCursor(this);
    }

    private void reloadComboBoxes() {

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
            
            if (judgement.isActive()){
                getJudgementComboBox().addItem(judgement);
                if (judgement.getElementId().equals(judgementId)) {
                    selectedIndex = index;
                }
                index++;
            }
        }

        getJudgementComboBox().setSelectedIndex(selectedIndex);

    }

    /**
     * Can be used to disable select controls during execution.
     * 
     * @param b
     */
    public void setEnabledButtonStatus(boolean b) {
        getExecuteButton().setEnabled(b && runFiles != null);
        getJudgementComboBox().setEnabled(b && runFiles != null);
        getCancelButton().setEnabled(b);
        getAcceptValidatorJudgementButton().setEnabled(b);
//        getViewOutputsButton().setEnabled(b);
        getAcceptChosenSelectionButton().setEnabled(b && getJudgementComboBox().getSelectedIndex() != -1);
    }

    public void enableUpdateButtons(boolean editedText) {
        if (editedText) {
            cancelButton.setText("Cancel");
        } else {
            cancelButton.setText("Close");
        }

        // Can only run or extract if there are run files...
        getExecuteButton().setEnabled(runFiles != null);
        getViewSourceButton().setEnabled(runFiles != null);
        getJudgementComboBox().setEnabled(runFiles != null);
        getNotifyTeamCheckBox().setEnabled(runFiles != null);
        
        //can only accept chosen selection if a selection was made
        getAcceptChosenSelectionButton().setEnabled(editedText);

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

    private void enableOutputsButton(boolean b) {
//            getViewOutputsButton().setEnabled(b);
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
    private JPanel getRunInfoPanel() {
        if (runInfoPanel == null) {
            TitledBorder titledBorder1 = BorderFactory.createTitledBorder(null, "Run Information", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                    new Font("Dialog", Font.BOLD, 18), new Color(51, 51, 51));
            titledBorder1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
            titledBorder1.setTitleFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
            runInfoLabel = new JLabel();
            runInfoLabel.setBounds(new java.awt.Rectangle(146,21,330,20));
            runInfoLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            runInfoLabel.setText("            ");
            elapsedTimeLabel = new JLabel();
            elapsedTimeLabel.setText("<unknown>");
            elapsedTimeLabel.setSize(new java.awt.Dimension(180, 19));
            elapsedTimeLabel.setLocation(new java.awt.Point(95,75));
            elapsedTimeLabel.setBounds(new java.awt.Rectangle(473,52,102,25));
            elapsedTimeLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            elapsedTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            languageNameLabel = new JLabel();
            languageNameLabel.setText("<unknown>");
            languageNameLabel.setSize(new java.awt.Dimension(211, 19));
            languageNameLabel.setLocation(new java.awt.Point(385,75));
            languageNameLabel.setBounds(new java.awt.Rectangle(177,82,199,26));
            languageNameLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            languageNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            problemNameLabel = new JLabel();
            problemNameLabel.setText("<unknown>");
            problemNameLabel.setSize(new java.awt.Dimension(212, 19));
            problemNameLabel.setLocation(new java.awt.Point(385,45));
            problemNameLabel.setBounds(new java.awt.Rectangle(177,51,215,26));
            problemNameLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            problemNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            elapsedTitleLabel = new JLabel();
            elapsedTitleLabel.setText("Time: ");
            elapsedTitleLabel.setLocation(new java.awt.Point(25,75));
            elapsedTitleLabel.setSize(new java.awt.Dimension(58, 19));
            elapsedTitleLabel.setBounds(new java.awt.Rectangle(404,52,58,26));
            elapsedTitleLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            elapsedTitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            languageTitleLabel = new JLabel();
            languageTitleLabel.setText("Language: ");
            languageTitleLabel.setLocation(new java.awt.Point(295,75));
            languageTitleLabel.setSize(new java.awt.Dimension(77, 19));
            languageTitleLabel.setBounds(new java.awt.Rectangle(83,81,90,28));
            languageTitleLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            languageTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            problemTitleLabel = new JLabel();
            problemTitleLabel.setText("Problem: ");
            problemTitleLabel.setLocation(new java.awt.Point(295,45));
            problemTitleLabel.setSize(new java.awt.Dimension(80, 19));
            problemTitleLabel.setBounds(new java.awt.Rectangle(82,52,90,23));
            problemTitleLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            problemTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            statusTitleLabel = new JLabel();
            statusTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            statusTitleLabel.setLocation(new java.awt.Point(11,43));
            statusTitleLabel.setSize(new java.awt.Dimension(75,23));
            statusTitleLabel.setBounds(new java.awt.Rectangle(399,85,62,26));
            statusTitleLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            statusTitleLabel.setText("Status: ");
            statusLabel = new JLabel();
            statusLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            statusLabel.setSize(new java.awt.Dimension(191,21));
            statusLabel.setLocation(new java.awt.Point(95,45));
            statusLabel.setBounds(new java.awt.Rectangle(472,85,219,25));
            statusLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            statusLabel.setText("<unknown>");
            runInfoPanel = new JPanel();
            runInfoPanel.setLayout(null);
            runInfoPanel.setPreferredSize(new java.awt.Dimension(250, 125));
            runInfoPanel.setBorder(titledBorder1);
            runInfoPanel.add(runInfoLabel, null);
            runInfoPanel.add(statusLabel, null);
            runInfoPanel.add(statusTitleLabel, null);
            runInfoPanel.add(problemTitleLabel, null);
            runInfoPanel.add(languageTitleLabel, null);
            runInfoPanel.add(elapsedTitleLabel, null);
            runInfoPanel.add(problemNameLabel, null);
            runInfoPanel.add(languageNameLabel, null);
            runInfoPanel.add(elapsedTimeLabel, null);
            runInfoPanel.add(getShellButton(), null);
        }
        return runInfoPanel;
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
    public JButton getExecuteButton() {
        if (executeButton == null) {
            executeButton = new JButton();
            executeButton.setText("Execute Run");
            executeButton.setActionCommand("Execute");
            executeButton.setBounds(new java.awt.Rectangle(60,120,129,26));
            executeButton.setLocation(new java.awt.Point(60,99));
            executeButton.setSize(new java.awt.Dimension(131,36));
            executeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
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
        
        executeTimeMS = 0;
        System.gc();

        executable = new Executable(getContest(), getController(), run, runFiles);

        //getManualRunResultsPanel().clear();
        setEnabledButtonStatus(false);
        executable.execute();
        saveOutputFileNames = executable.getTeamsOutputFilenames();
        saveValidatorOutputFileNames = executable.getValidatorOutputFilenames();
        saveValidatorErrFileNames = executable.getValidatorErrFilenames();
        sendTeamOutputFileNames();
        sendValidatorOutputFileNames();
        sendValidatorStderrFileNames();
        // only if do not show output is not checked
        if (!getContest().getProblem(run.getProblemId()).isHideOutputWindow()) {
            getMultiTestSetOutputViewerFrame().setVisible(true);
        }
        executeTimeMS = executable.getExecutionData().getExecuteTimeMS();

        // Show validator results, if there are any.

        JudgementRecord judgementRecord = null;

        showValidatorControls(false);
        if (executable.isValidationSuccess()) {
            String results = executable.getValidationResults();
            if (results != null && results.trim().length() > 1) {
                
                if (results.equalsIgnoreCase("accepted") || results.equalsIgnoreCase("yes")) {
                    results = getContest().getJudgements()[0].getDisplayName();
                }
                validatorAnswer.setText(results);

                boolean solved = false;

                ElementId elementId = getValidatorResultElementID(results);
                Judgement yesJudgement = getContest().getJudgements()[0];
                if (yesJudgement.getElementId().equals(elementId)) {
                    solved = true;
                } 
                judgementRecord = new JudgementRecord(elementId, run.getSubmitter(), solved, true);
                judgementRecord.setValidatorResultString(results);

                judgementRecord.setSendToTeam(getNotifyTeamCheckBox().isSelected());
                judgementRecord.setExecuteMS(executeTimeMS);

                showValidatorControls(true, judgementRecord);

            } else {
                log.warning("execute indicated validator success but getValidationResults returns \"\" or null");
            }
        }
        ExecutionData eData = executable.getExecutionData();
        if (eData != null && !eData.isCompileSuccess()) {
            String results = "No - Compilation Error";
            validatorAnswer.setText(results);
            showValidatorControls(true);
            ElementId elementId = getValidatorResultElementID(results);
            judgementRecord = new JudgementRecord(elementId, run.getSubmitter(), false, true);
            judgementRecord.setValidatorResultString(results);

            judgementRecord.setSendToTeam(getNotifyTeamCheckBox().isSelected());
        }

//        RunResultFiles rrf = new RunResultFiles(run, run.getProblemId(), judgementRecord, executable.getExecutionData());
        //getManualRunResultsPanel().populatePane(rrf, "Manual Results");
        //getManualRunResultsPanel().setVisible(true);
                
        enableOutputsButton(true);
        setEnabledButtonStatus(true);
    }

    /**
     * Send team output names to Multi Test Set Viewer.
     */
    private void sendTeamOutputFileNames() {

        if (getMultiTestSetOutputViewerFrame() != null) {

            String[] teamOutputNames = new String[getProblemDataFiles().getJudgesDataFiles().length];

            // null out list
            for (int i = 0; i < teamOutputNames.length; i++) {
                teamOutputNames[i] = null;
            }

            // add entries from acutal team test output
            if (saveOutputFileNames != null) {

                for (int i = 0; i < saveOutputFileNames.size(); i++) {
                    teamOutputNames[i] = saveOutputFileNames.get(i);
                    if (new File(teamOutputNames[i]).length() == 0) {
                        teamOutputNames[i] = null; // null null, happily null!!
                    }
                }
            }

            getMultiTestSetOutputViewerFrame().setTeamOutputFileNames(teamOutputNames);
        }
    }

    /**
     * Send validator output names to Multi Test Set Viewer.
     */
    private void sendValidatorOutputFileNames() {

        if (getMultiTestSetOutputViewerFrame() != null) {

            String[] validatorOutputNames = new String[getProblemDataFiles().getJudgesDataFiles().length];

            // null out list
            for (int i = 0; i < validatorOutputNames.length; i++) {
                validatorOutputNames[i] = null;
            }

            // add entries from actual team test output
            if (saveValidatorOutputFileNames != null) {

                for (int i = 0; i < saveValidatorOutputFileNames.size(); i++) {
                    validatorOutputNames[i] = saveValidatorOutputFileNames.get(i);
                    if (new File(validatorOutputNames[i]).length() == 0) {
                        validatorOutputNames[i] = null; // null null, happily null!!
                    }
                }
            }

            getMultiTestSetOutputViewerFrame().setValidatorOutputFileNames(validatorOutputNames);
        }
    }

    /**
     * Send validator output names to Multi Test Set Viewer.
     */
    private void sendValidatorStderrFileNames() {

        if (getMultiTestSetOutputViewerFrame() != null) {

            String[] validatorErrFileNames = new String[getProblemDataFiles().getJudgesDataFiles().length];

            // null out list
            for (int i = 0; i < validatorErrFileNames.length; i++) {
                validatorErrFileNames[i] = null;
            }

            // add entries from actual team test output
            if (saveValidatorErrFileNames != null) {

                for (int i = 0; i < saveValidatorErrFileNames.size(); i++) {
                    validatorErrFileNames[i] = saveValidatorErrFileNames.get(i);
                    if (new File(validatorErrFileNames[i]).length() == 0) {
                        validatorErrFileNames[i] = null; // null null, happily null!!
                    }
                }
            }

            getMultiTestSetOutputViewerFrame().setValidatorStderrFileNames(validatorErrFileNames);
        }
    }

    private void showValidatorControls(boolean b, JudgementRecord judgementRecord) {
        showValidatorControls(b);
        String toolTip = "";
        if (b) {
            // these only matter if being shown
            Color color = Color.RED;
            boolean enableButton = true;
            String judgement = judgementRecord.getValidatorResultString();
            String oJudgement = getContest().getJudgement(judgementRecord.getJudgementId()).toString();
            if (!judgement.equals(oJudgement)) {
                enableButton = false;
                color = Color.BLACK;
                toolTip = "NOTE: This response does not match any defined Judgements.";
            } else {
                if (judgementRecord.isSolved()) {
                    color = Color.green;
                } // else default to RED
            }
            validatorAnswer.setForeground(color);
            getAcceptValidatorJudgementButton().setEnabled(enableButton);
        }
        validatorAnswer.setToolTipText(toolTip);
    }

    public void setRunAndFiles(Run theRun, RunFiles runFiles2, RunResultFiles[] theRunResultFiles) {

        FrameUtilities.regularCursor(this);

        showMessage("");
        log.info("Fetched run " + theRun + " to edit");

        // start the time to judge
        TimeZone tz = TimeZone.getTimeZone("GMT");
        startTimeCalendar = new GregorianCalendar(tz);

        run = theRun;
        runFiles = runFiles2;
        runResultFiles = theRunResultFiles;

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
    private JComboBox<Judgement> getJudgementComboBox() {
        if (judgementComboBox == null) {
            judgementComboBox = new JComboBox<Judgement>();
            judgementComboBox.setMinimumSize(new java.awt.Dimension(150, 25));
            judgementComboBox.setBounds(new java.awt.Rectangle(225,120,225,25));
            judgementComboBox.setLocation(new java.awt.Point(225,101));
            judgementComboBox.setSize(new java.awt.Dimension(271,33));
            judgementComboBox.setPreferredSize(new java.awt.Dimension(150, 25));
            judgementComboBox.setMaximumRowCount(15);
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

    private void createAndViewFile(IFileViewer fileViewer, SerializedFile file, String title, boolean visible) {
        // SOMEDAY the executable dir name should be from the model, eh ?
        String targetDirectory = getExecuteDirectoryName();
        Utilities.insureDir(targetDirectory);
        String targetFileName = targetDirectory + File.separator + file.getName();
        try {
            file.writeFile(targetFileName);

            if (new File(targetFileName).isFile()) {
                fileViewer.addFilePane(title, targetFileName);
            } else {
                fileViewer.addTextPane(title, "Could not create file at " + targetFileName);
            }
        } catch (IOException e) {
            fileViewer.addTextPane(title, "Could not create file at " + targetFileName + "Exception " + e.getMessage());
        }
        if (visible) {
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
            notifyTeamCheckBox.setSelected(true);
            notifyTeamCheckBox.setBounds(new java.awt.Rectangle(578,65,119,30));
            notifyTeamCheckBox.setText("Notify Team");
        }
        return notifyTeamCheckBox;
    }

    private String getTeamDisplayName(ClientId clientId) {
        if (isJudge() && isTeam(clientId)) {
            return displayTeamName.getDisplayName(clientId);
        }

        return clientId.getName();
    }

    private boolean isTeam(ClientId clientId) {
        return clientId == null || clientId.getClientType().equals(Type.TEAM);
    }

    private boolean isJudge(ClientId clientId) {
        return clientId == null || clientId.getClientType().equals(Type.JUDGE);
    }

    private boolean isJudge() {
        return isJudge(getContest().getClientId());
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
                    initializePermissions();
                    updateGUIperPermissions();
                }
            });
        }

        public void accountsAdded(AccountEvent accountEvent) {
            // ignore this does not affect me

        }

        public void accountsModified(AccountEvent accountEvent) {
            // TODO is this not dependent on us being modified???
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    initializePermissions();
                    updateGUIperPermissions();
                }
            });
        }

        public void accountsRefreshAll(AccountEvent accountEvent) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    initializePermissions();
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
            acceptValidatorJudgementButton.setText("Accept Validator");
            acceptValidatorJudgementButton.setPreferredSize(new java.awt.Dimension(150, 26));
            acceptValidatorJudgementButton.setBounds(new java.awt.Rectangle(479,46,150,26));
            acceptValidatorJudgementButton.setSize(new java.awt.Dimension(167,34));
            acceptValidatorJudgementButton.setLocation(new java.awt.Point(554,27));
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
        validatorAnswerLabel.setVisible(showControls);
        validatorAnswer.setVisible(showControls);
        getAcceptValidatorJudgementButton().setVisible(showControls);
    }

    /*
     * Get the ElementId corresponding to the Validator Judgement. @returns 1st No if not found
     */
    private ElementId getValidatorResultElementID(String results) {
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
        }
        return elementId;
    }

    protected void acceptValidatorJudgement() {

        Run newRun = getRunFromFields();

        enableUpdateButtons(false);

        closeViewerWindows();
        RunResultFiles newRunResultFiles = null;

        JudgementRecord judgementRecord = null;

        String results = validatorAnswer.getText();

        boolean solved = false;

        ElementId elementId = getValidatorResultElementID(results);
        Judgement yesJudgement = getContest().getJudgements()[0];
        if (yesJudgement.getElementId().equals(elementId)) {
            solved = true;
        }
        if (getJudgementComboBox().getSelectedIndex() > -1) {
            Judgement manualJudgement = (Judgement) getJudgementComboBox().getSelectedItem();
            Judgement autoJudgement = getContest().getJudgement(elementId);
            if (!manualJudgement.equals(autoJudgement)) {
                String message = "You selected 'Accept Validator' but have manually selected '" + manualJudgement + "'.  Did you intend to accept '" + results + "'?";
                int result = JOptionPane.showConfirmDialog(this, message);
                if (result != JOptionPane.YES_OPTION) {
                    enableUpdateButtons(true);
                    return;
                }
            }
        }
        newRun.setStatus(RunStates.JUDGED);

        judgementRecord = new JudgementRecord(elementId, getContest().getClientId(), solved, true);
        judgementRecord.setValidatorResultString(results);

        judgementRecord.setSendToTeam(getNotifyTeamCheckBox().isSelected());
        TimeZone tz = TimeZone.getTimeZone("GMT");
        GregorianCalendar cal = new GregorianCalendar(tz);

        long milliDiff = cal.getTime().getTime() - startTimeCalendar.getTime().getTime();
        long totalSeconds = milliDiff / 1000;
        judgementRecord.setHowLongToJudgeInSeconds(totalSeconds);

        JudgeView.setAlreadyJudgingRun(false);

        ExecutionData executionData = null;
        // this will be null if we are accepting the computer judgement
        if (executable != null) {
            executionData = executable.getExecutionData();
        }
        newRunResultFiles = new RunResultFiles(newRun, newRun.getProblemId(), judgementRecord, executionData);

        getController().submitRunJudgement(newRun, judgementRecord, newRunResultFiles);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }

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
        createAndViewFiles(sourceViewer, runFiles.getMainFile(), "Main File (" + runFiles.getMainFile().getName() + ")", runFiles.getOtherFiles());
    }

    private void createAndViewFiles(IFileViewer fileViewer, SerializedFile file, String title, SerializedFile[] otherFiles) {
        if (otherFiles != null && otherFiles.length > 0) {
            for (int i = otherFiles.length; i > 0; i--) {
                createAndViewFile(fileViewer, otherFiles[i - 1], otherFiles[i - 1].getName(), false);
            }
        }
        createAndViewFile(fileViewer, file, title, false);
        fileViewer.setSelectedIndex(0);
        fileViewer.setVisible(true);
    }

    protected void viewOutputs() {
        
//        if (executableFileViewer != null) {
//            executableFileViewer.setVisible(true);
//        } else {
//            JOptionPane.showMessageDialog(this, "No output yet!");
//        }

        Problem problem = getContest().getProblem(run.getProblemId());
        getMultiTestSetOutputViewerFrame().setData(run, runFiles, problem, getProblemDataFiles());
        sendTeamOutputFileNames();
        sendValidatorOutputFileNames();
        sendValidatorStderrFileNames();
        getMultiTestSetOutputViewerFrame().setVisible(true);
    }

    private MultiTestSetOutputViewerFrame getMultiTestSetOutputViewerFrame() {
        if (multiTestSetOutputViewerFrame == null) {
            multiTestSetOutputViewerFrame = new MultiTestSetOutputViewerFrame();
            multiTestSetOutputViewerFrame.setContestAndController(getContest(), getController());
    
            FrameUtilities.centerFrame(multiTestSetOutputViewerFrame);
        }
        return multiTestSetOutputViewerFrame;
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
            shellButton.setBounds(new java.awt.Rectangle(509, 93, 65, 20));
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

        // String winShellCommand = fs + "windows" + fs + "system32" + fs + "cmd.exe";
        // String unixShellCommand = fs + "bin" + fs + "sh";

        String[] env = null;

        if (fs.equals("\\")) {

            System.out.println("win debug " + executeDir);

            try {
                Runtime.getRuntime().exec("cmd /C start cmd", env, runDir);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(getParentFrame(), "Unable to run command cmd.exe " + e.getMessage());
                e.printStackTrace();
            }
        } else if (fs.equals("/")) {

            System.out.println("unix debug to " + executeDir);

            try {
                Runtime.getRuntime().exec("/bin/sh", env, runDir);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(getParentFrame(), "Unable to run command /bin/sh" + " " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("missed it " + fs);
        }
    }

    protected void setRunFiles(RunFiles inRunFiles) {
        runFiles = inRunFiles;
    }

    /**
     * This method initializes jTestPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.setPreferredSize(new java.awt.Dimension(700,300));
            mainPanel.add(getRunInfoPanel(), java.awt.BorderLayout.NORTH);
            mainPanel.add(getAssignJudgementPanel(), java.awt.BorderLayout.CENTER);
        }
        return mainPanel;
    }

    /**
     * This method initializes jManualJudgementPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAssignJudgementPanel() {
        if (assignJudgementPanel == null) {
            TitledBorder titledBorder = BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.blue, 2), 
                    "Assign Judgement", 
                    TitledBorder.DEFAULT_JUSTIFICATION, 
                    TitledBorder.DEFAULT_POSITION, 
                    new Font("Dialog", Font.BOLD, 16), 
                    new Color(51, 51, 51)
            );
            titledBorder.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.blue,1));
            selectJudgementCheckboxLabel = new JLabel();
            selectJudgementCheckboxLabel.setBounds(new java.awt.Rectangle(252,75,212,19));
            selectJudgementCheckboxLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            selectJudgementCheckboxLabel.setText("Select Judgement");
            validatorAnswer = new JLabel();
            validatorAnswer.setBounds(new java.awt.Rectangle(299,30,196,31));
            validatorAnswer.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            validatorAnswer.setForeground(Color.RED);
            validatorAnswer.setText("<unknown>");
            validatorAnswerLabel = new JLabel();
            validatorAnswerLabel.setBounds(new java.awt.Rectangle(75,30,211,30));
            validatorAnswerLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
            validatorAnswerLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            validatorAnswerLabel.setText("Validator Recommends: ");
            validatorAnswerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

            assignJudgementPanel = new JPanel();
            assignJudgementPanel.setLayout(null);
            assignJudgementPanel.setPreferredSize(new java.awt.Dimension(700,300));
            assignJudgementPanel.setMaximumSize(new java.awt.Dimension(1280, 1280));
            assignJudgementPanel.setBorder(titledBorder);
            assignJudgementPanel.add(getExecuteButton(), null);
            assignJudgementPanel.add(getJudgementComboBox(), null);
            assignJudgementPanel.add(getAcceptChosenSelectionButton(), null);
            assignJudgementPanel.add(getAcceptValidatorJudgementButton(), null);
            assignJudgementPanel.add(getNotifyTeamCheckBox(), null);
            assignJudgementPanel.add(validatorAnswerLabel, null);
            assignJudgementPanel.add(validatorAnswer, null);
            assignJudgementPanel.add(selectJudgementCheckboxLabel, null);
        }
        return assignJudgementPanel;
    }

    /**
     * This method initializes detailsButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getDetailsButton() {
        if (detailsButton == null) {
            detailsButton = new JButton();
            detailsButton.setText("Details...");
            detailsButton.setEnabled(false);
            detailsButton.setVisible(false); // TODO re-enable this
        }
        return detailsButton;
    }

    /**
     * This method initializes viewOutputsButton
     *
     * @return javax.swing.JButton
     */
    private JButton getViewOutputsButton() {
        if (viewOutputsButton == null) {
            viewOutputsButton = new JButton();
            viewOutputsButton.setActionCommand("View Test Results");
            viewOutputsButton.setEnabled(true);
            viewOutputsButton.setToolTipText("View/Compare Test Results and data sets");
            viewOutputsButton.setText("View Outputs & Data");
            viewOutputsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    viewOutputs();
                }
            });
        }
        return viewOutputsButton;
    }
    
    /**
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */

    // $HeadURL$
    private class JudgementListenerImplementation implements IJudgementListener {

        public void judgementAdded(JudgementEvent event) {
            reloadComboBoxes();
        }

        public void judgementChanged(JudgementEvent event) {
            reloadComboBoxes();
        }

        public void judgementRemoved(JudgementEvent event) {
            reloadComboBoxes();
        }

        public void judgementRefreshAll(JudgementEvent judgementEvent) {
            reloadComboBoxes();
        }
    }


} // @jve:decl-index=0:visual-constraint="10,10"
