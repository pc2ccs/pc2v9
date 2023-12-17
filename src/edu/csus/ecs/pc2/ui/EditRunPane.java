// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.Executable;
import edu.csus.ecs.pc2.core.execute.ExecuteTimerFrame;
import edu.csus.ecs.pc2.core.execute.ExecutionData;
import edu.csus.ecs.pc2.core.execute.JudgementUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.report.ExtractRuns;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import java.awt.Point;
import java.awt.Dimension;

/**
 * Add/Edit Run Pane
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditRunPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 8747938709622932819L;

    private JPanel messagePane = null;

    private JPanel buttonPane = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private JLabel messageLabel = null;

    private Run run = null;

    private RunFiles runFiles = null;

    private JTabbedPane mainTabbedPane = null;

    private JPanel generalPane = null;

    private Log log = null;

    private JButton executeButton = null;

    private JButton viewSourceButton = null;

    private JButton extractButton = null;

    private Executable executable;

    private JComboBox<Judgement> judgementComboBox = null;

    private JLabel runInfoLabel = null;

    private JCheckBox deleteCheckBox = null;

    private JLabel judgementLabel = null;

    private JLabel statusTitleLabel = null;

    private JComboBox<Problem> problemComboBox = null;
    
    private JComboBox<Run.RunStates> runStatusComboBox = null;

    private JComboBox<Language> languageComboBox = null;

    private JLabel problemLabel = null;

    private JLabel languageLabel = null;

    private JLabel jLabel = null;

    private JTextField elapsedTimeTextField = null;

    private IFileViewer sourceViewer;

    private JCheckBox notifyTeamCheckBox = null;

    private boolean populatingGUI = true;

    private ExtractRuns extractRuns;

    /**
     * This method initializes
     * 
     */
    public EditRunPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(536, 307));

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getMainTabbedPane(), java.awt.BorderLayout.EAST);
        this.add(getGeneralPane(), java.awt.BorderLayout.CENTER);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        log = getController().getLog();
        addWindowCloserListener();
        extractRuns = new ExtractRuns(inContest);
    }
    
    private void addWindowCloserListener() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (getParentFrame() != null) {
                    getParentFrame().addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            handleCancelButton();
                        }
                    });
                } 
            }
        });
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
            messageLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePane = new JPanel();
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new java.awt.Dimension(35,35));
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
            flowLayout.setHgap(15);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.add(getUpdateButton(), null);
            buttonPane.add(getViewSourceButton(), null);
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

    protected void updateRun() {

        Run newRun = getRunFromFields();

        cancelButton.setText("Close");
        updateButton.setEnabled(false); // disable update button

        JudgementRecord judgementRecord = null;
        RunResultFiles runResultFiles = null;

        if (judgementChanged()) {
            newRun.setStatus(RunStates.JUDGED);

            boolean solved = getJudgementComboBox().getSelectedIndex() == 0;
            Judgement judgement = (Judgement) getJudgementComboBox().getSelectedItem();

            judgementRecord = new JudgementRecord(judgement.getElementId(), getContest().getClientId(), solved, false);
            judgementRecord.setSendToTeam(getNotifyTeamCheckBox().isSelected());
        }

        newRun.setDeleted(deleteCheckBox.isSelected());
        
        int elapsed = getIntegerValue(getElapsedTimeTextField().getText());
        newRun.setElapsedMins(elapsed);

        ElementId problemId = ((Problem) getProblemComboBox().getSelectedItem()).getElementId();
        if (problemId != null) {
            newRun.setProblemId(problemId);
        }
        ElementId languageId = ((Language) getLanguageComboBox().getSelectedItem()).getElementId();
        if (languageId != null) {
            newRun.setLanguageId(languageId);
        }

        if (isStatusChanged()) {

            RunStates prevState = run.getStatus();
            RunStates newRunState = (Run.RunStates) runStatusComboBox.getSelectedItem();
            String errMsg = null;
            
            // Make sure it's safe to change the runstate
            switch(newRunState) {
            case JUDGED:
            case MANUAL_REVIEW:
            case REJUDGE:           // TODO: remove REJUDGE state completely since nobody appears to use it -- JB
                // All the above states imply that the run was previously judged and has a JudgementRecord (or more)
                // If not, then the state can not be set.
                if(newRun.getJudgementRecord() == null) {
                    errMsg = "The run does not have any judgment records";
                }
                break;
                
            case BEING_RE_JUDGED:
                // It is completely unreasonable to set a run's status to being re-judged
                errMsg = "You are not allowed to set the Run Status to BEING_RE_JUDGED";
                break;

            default:
                // all other states should be ok and not cause any issues.
                break;
            }
            // notify user on bad status change
            if(errMsg != null) {
                FrameUtilities.showMessage(this, "Can not set Run Status", errMsg);
                enableUpdateButton();
                return;                
            }
            int result = FrameUtilities.yesNoCancelDialog(this, "Are you sure you want to change status from " + //
                    prevState.toString() + " to " + newRunState.toString() + "?", "Update/Change run status?");

            if (result != JOptionPane.YES_OPTION) {
                enableUpdateButton(); // required to re-enable Update button
                return;
            }

            newRun.setStatus(newRunState);
        }

        ExecutionData executionData = null;
        if (executable != null) {
            executionData = executable.getExecutionData();
        }
 
        runResultFiles = new RunResultFiles(newRun, newRun.getProblemId(), judgementRecord, executionData);
        getController().updateRun(newRun, judgementRecord, runResultFiles);

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

            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Run modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                updateRun();
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            }
            if (result == JOptionPane.NO_OPTION) {
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            }

        } else {
            if (getParentFrame() != null) {
                getParentFrame().setVisible(false);
            }
        }
    }

    public Run getRun() {
        return run;
    }

    public void setRun(final Run run) {

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

    private void populateGUI(Run run2) {
        
        populatingGUI = true;

        if (run2 != null) {
            getUpdateButton().setVisible(true);

            ClientId id = run2.getSubmitter();
            String teamName = id.getName();

            runInfoLabel.setText("Run " + run2.getNumber() + " (Site " + run2.getSiteNumber() + ") from " + teamName);
            deleteCheckBox.setSelected(run2.isDeleted());
            elapsedTimeTextField.setText(new Long(run.getElapsedMins()).toString());
            
            getNotifyTeamCheckBox().setSelected(notifyTeam());

        } else {
            getUpdateButton().setVisible(false);

            runInfoLabel.setText("Could not get run");
            deleteCheckBox.setSelected(false);
            elapsedTimeTextField.setText("");

            getNotifyTeamCheckBox().setSelected(false);
        }
        populateComboBoxes();
        
        populatingGUI = false;

    }

    private void populateComboBoxes() {

        int selectedIndex = -1;
        int index = 0;

        getProblemComboBox().removeAllItems();
        getLanguageComboBox().removeAllItems();
        getJudgementComboBox().removeAllItems();
        
        runStatusComboBox.removeAllItems();

        if (run == null) {
            return; // No run no combo boxes values
        }

        for (Problem problem : getContest().getProblems()) {
            getProblemComboBox().addItem(problem);
            if (problem.getElementId().equals(run.getProblemId())) {
                selectedIndex = index;
            }
            index++;
        }

        getProblemComboBox().setSelectedIndex(selectedIndex);

        selectedIndex = -1;
        index = 0;

        for (Language language : getContest().getLanguages()) {
            getLanguageComboBox().addItem(language);
            if (language.getElementId().equals(run.getLanguageId())) {
                selectedIndex = index;
            }
            index++;
        }

        getLanguageComboBox().setSelectedIndex(selectedIndex);

        selectedIndex = -1;
        index = 0;

        ElementId judgementId = null;

        judgementLabel.setText("Judgement");

        if (run.isJudged()) {
            JudgementRecord jr = run.getJudgementRecord();
            judgementId = jr.getJudgementId();

            // Add judgement description to judgement tool tip
            Judgement judgement = getContest().getJudgement(judgementId);
            String judgementDescription = judgement.getAcronym() + " " + judgement.getDisplayName();

            String valString = jr.getValidatorResultString();
            if (valString != null) {
                judgementDescription += " Validator returns '" + valString + "'";
                judgementLabel.setText("Judgement*");
            }

            log.info("Edit Run " + run.getNumber() + " judgement is " + judgementDescription);
            judgementLabel.setToolTipText(judgementDescription);
        }
        
        for (Judgement judgement : getContest().getJudgements()) {
            getJudgementComboBox().addItem(judgement);
            if (judgement.getElementId().equals(judgementId)) {
                selectedIndex = index;
            }
            index++;
        }
        
        // Select judgement in combo box
        judgementComboBox.setSelectedIndex(selectedIndex);
        
        selectedIndex = -1;
        index = 0;

        runStatusComboBox.setSelectedIndex(selectedIndex);
        
        RunStates[] states = Run.RunStates.values();
        for (RunStates runStates : states) {
            runStatusComboBox.addItem(runStates);
            if (run.getStatus().equals(runStates)) {
                selectedIndex = index;
            }
            index++;
        }
        
        runStatusComboBox.setSelectedIndex(selectedIndex);

    }

    protected void enableUpdateButtons(boolean editedText) {
        if (editedText) {
            cancelButton.setText("Cancel");
        } else {
            cancelButton.setText("Close");
        }

        // Can only run/extract if there are run files...
        getExecuteButton().setEnabled(runFiles != null);
        getExecuteButton().setEnabled(runFiles != null);
        getViewSourceButton().setEnabled(runFiles != null);

        updateButton.setEnabled(editedText);
    }

    private int getIntegerValue(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
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
            int elapsed = getIntegerValue(getElapsedTimeTextField().getText());
            enableButton |= (elapsed != run.getElapsedMins());

            ElementId problemId = ((Problem) getProblemComboBox().getSelectedItem()).getElementId();
            enableButton |= (!run.getProblemId().equals(problemId));

            ElementId languageId = ((Language) getLanguageComboBox().getSelectedItem()).getElementId();
            enableButton |= (!run.getLanguageId().equals(languageId));

            enableButton |= (run.isDeleted() != getDeleteCheckBox().isSelected());

            enableButton |= judgementChanged();
            
            enableButton |= isStatusChanged();
            
            enableButton |= notifyTeamChanged();
            
        }

        getUpdateButton().setEnabled(enableButton);

    }

    /**
     * return if status changed by user.
     * 
     * @return true if input run status different than combobox status
     */
    private boolean isStatusChanged() {

        RunStates prevState = run.getStatus();
        RunStates newRunState = (Run.RunStates) runStatusComboBox.getSelectedItem();

        return !prevState.equals(newRunState);
    }

    private boolean judgementChanged() {
        if (run.isJudged()) {
            
            if (notifyTeamChanged()){
                return true;
            }

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
     * For this run, send notification to team?
     * @return 
     */
    private boolean notifyTeam(){
        if (run.isJudged()) {

            JudgementRecord judgementRecord = run.getJudgementRecord();
            if (judgementRecord != null) {

                return judgementRecord.isSendToTeam();
            }
        }
        return false;
    }
    
    /**
     * Has the notify team status changed ?
     * @return true if changed, false otherwise.
     */
    private boolean notifyTeamChanged() {
        return notifyTeam() != getNotifyTeamCheckBox().isSelected();
    }

    /**
     * This method initializes mainTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getMainTabbedPane() {
        if (mainTabbedPane == null) {
            mainTabbedPane = new JTabbedPane();
        }
        return mainTabbedPane;
    }

    /**
     * This method initializes generalPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGeneralPane() {
        if (generalPane == null) {
            jLabel = new JLabel();
            jLabel.setBounds(new java.awt.Rectangle(73, 67, 142, 16));
            jLabel.setText("Elapsed");
            jLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            languageLabel = new JLabel();
            languageLabel.setBounds(new java.awt.Rectangle(73, 166, 142, 16));
            languageLabel.setText("Language");
            languageLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            problemLabel = new JLabel();
            problemLabel.setBounds(new java.awt.Rectangle(73, 133, 142, 16));
            problemLabel.setText("Problem");
            problemLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            statusTitleLabel = new JLabel();
            statusTitleLabel.setBounds(new java.awt.Rectangle(73, 35, 142, 19));
            statusTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            statusTitleLabel.setText("Status");
            judgementLabel = new JLabel();
            judgementLabel.setBounds(new java.awt.Rectangle(73, 99, 142, 19));
            judgementLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            judgementLabel.setText("Judgement");
            runInfoLabel = new JLabel();
            runInfoLabel.setBounds(new java.awt.Rectangle(6, -1, 511, 25));
            runInfoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            runInfoLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            runInfoLabel.setText("Run Info");
            generalPane = new JPanel();
            generalPane.setLayout(null);
            generalPane.add(getJudgementComboBox(), null);
            generalPane.add(runInfoLabel, null);
            generalPane.add(getDeleteCheckBox(), null);
            generalPane.add(judgementLabel, null);
            generalPane.add(statusTitleLabel, null);
            generalPane.add(getProblemComboBox(), null);
            generalPane.add(getLanguageComboBox(), null);
            generalPane.add(problemLabel, null);
            generalPane.add(languageLabel, null);
            generalPane.add(jLabel, null);
            generalPane.add(getElapsedTimeTextField(), null);
            generalPane.add(getNotifyTeamCheckBox(), null);
            
            runStatusComboBox = new JComboBox<Run.RunStates>();
            runStatusComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    new Thread(new Runnable() {
                        public void run() {
                            enableUpdateButton();
                        }
                    }).start();
                }
            });

            runStatusComboBox.setSize(new Dimension(263, 22));
            runStatusComboBox.setLocation(new Point(224, 97));
            runStatusComboBox.setBounds(224, 34, 263, 22);
            generalPane.add(runStatusComboBox);
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

    protected void viewSourceFile() {

        createAndViewFile(runFiles.getMainFile(), "Team's source");

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
            extractButton.setToolTipText("Extract Run contents");
            extractButton.setMnemonic(java.awt.event.KeyEvent.VK_T);
            extractButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    extractRun();
                }
            });
        }
        return extractButton;
    }

    protected void extractRun() {
        try {
            boolean extracted = extractRuns.extractRun(getRun().getElementId());
            if (extracted) {
                JOptionPane.showMessageDialog(this, "Extracted 1 run to \"extract\" dir.");
            } else {
                JOptionPane.showMessageDialog(this, "Problem extracting run.");
            }
        } catch (IOException e) {
            log.throwing("RunPane", "extractRun", e);
            showMessage("Problem extracting run.");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Problem extracting run. "+e.getMessage());
            e.printStackTrace();
        } catch (FileSecurityException e) {
            JOptionPane.showMessageDialog(this, "Problem extracting run. "+e.getMessage());
            e.printStackTrace();
        }
    }

    protected void executeRun() {

        System.gc();
        
        ExecuteTimerFrame executeFrame = new ExecuteTimerFrame();
        
        executable = new Executable(getContest(), getController(), run, runFiles, executeFrame);

        IFileViewer fileViewer = executable.execute();
        
        // Dump execution results files to log
        String executeDirctoryName = JudgementUtilities.getExecuteDirectoryName(getContest().getClientId());
        Problem problem = getContest().getProblem(run.getProblemId());
        ClientId clientId = getContest().getClientId();
        List<Judgement> judgements = JudgementUtilities.getLastTestCaseJudgementList(getContest(), run);
        JudgementUtilities.dumpJudgementResultsToLog(log, clientId, run, executeDirctoryName, problem, judgements, executable.getExecutionData(), "", new Properties());
        
        fileViewer.setVisible(true);
    }

    public void setRunAndFiles(Run run2, RunFiles runFiles2) {

        FrameUtilities.regularCursor(this);

        showMessage("");
        log.info("Fetched run " + run2 + " to edit");

        run = run2;
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
    private JComboBox<Judgement> getJudgementComboBox() {
        if (judgementComboBox == null) {
            judgementComboBox = new JComboBox<Judgement>();
            judgementComboBox.setLocation(new java.awt.Point(224, 97));
            judgementComboBox.setSize(new java.awt.Dimension(263, 22));
            judgementComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return judgementComboBox;
    }

    /**
     * This method initializes deleteCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getDeleteCheckBox() {
        if (deleteCheckBox == null) {
            deleteCheckBox = new JCheckBox();
            deleteCheckBox.setBounds(new java.awt.Rectangle(224, 196, 114, 21));
            deleteCheckBox.setText("Delete Run");
            deleteCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return deleteCheckBox;
    }

    /**
     * This method initializes problemComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox<Problem> getProblemComboBox() {
        if (problemComboBox == null) {
            problemComboBox = new JComboBox<Problem>();
            problemComboBox.setBounds(new java.awt.Rectangle(224, 130, 263, 22));
            problemComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return problemComboBox;
    }

    /**
     * This method initializes languageComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox<Language> getLanguageComboBox() {
        if (languageComboBox == null) {
            languageComboBox = new JComboBox<Language>();
            languageComboBox.setBounds(new java.awt.Rectangle(224, 163, 263, 22));
            languageComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return languageComboBox;
    }

    /**
     * This method initializes elapsedTimeTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getElapsedTimeTextField() {
        if (elapsedTimeTextField == null) {
            elapsedTimeTextField = new JTextField();
            elapsedTimeTextField.setBounds(new java.awt.Rectangle(224, 65, 65, 21));
            elapsedTimeTextField.setDocument(new IntegerDocument());

            elapsedTimeTextField.addKeyListener(new KeyAdapter() {
                // public void keyPressed(java.awt.event.KeyEvent e) {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return elapsedTimeTextField;
    }

    private void createAndViewFile(SerializedFile file, String title) {
        // TODO the executeable dir name should be from the model, eh ?
        Executable tempEexecutable = new Executable(getContest(), getController(), run, runFiles, null);
        String targetDirectory = tempEexecutable.getExecuteDirectoryName();
        Utilities.insureDir(targetDirectory);
        String targetFileName = targetDirectory + File.separator + file.getName();
        showMessage("Create: " + targetFileName);
        try {
            file.writeFile(targetFileName);
            if (sourceViewer != null) {
                sourceViewer.dispose();
            }
            sourceViewer = new MultipleFileViewer(getController().getLog());

            if (new File(targetFileName).isFile()) {
                sourceViewer.addFilePane(title, targetFileName);
                sourceViewer.setVisible(true);
            } else {
                sourceViewer.addTextPane(title, "Could not create file at " + targetFileName);
                sourceViewer.setVisible(true);
            }
        } catch (IOException e) {
            sourceViewer.addTextPane(title, "Could not create file at " + targetFileName + "Exception "+e.getMessage());
            sourceViewer.setVisible(true);
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
            notifyTeamCheckBox.setBounds(new java.awt.Rectangle(347, 197, 134, 19));
            notifyTeamCheckBox.setSelected(false);
            notifyTeamCheckBox.setText("Notify Team");
            notifyTeamCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return notifyTeamCheckBox;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
