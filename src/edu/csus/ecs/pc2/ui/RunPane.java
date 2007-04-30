package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.io.File;
import java.io.IOException;

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

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.Executable;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Run.RunStates;

/**
 * Add/Edit Run Pane
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class RunPane extends JPanePlugin {

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

    private JComboBox judgementComboBox = null;

    private JLabel runInfoLabel = null;

    private JCheckBox deleteCheckBox = null;

    private JLabel judgementLabel = null;

    private JLabel statusLabel = null;

    private JLabel statusTitleLabel = null;

    private JComboBox problemComboBox = null;

    private JComboBox languageComboBox = null;

    private JLabel problemLabel = null;

    private JLabel languageLabel = null;

    private JLabel jLabel = null;

    private JTextField elapsedTimeTextField = null;

    private IFileViewer sourceViewer;

    private JCheckBox notifyTeamCheckBox = null;

    private boolean populatingGUI = true;

    /**
     * This method initializes
     * 
     */
    public RunPane() {
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

    public void setModelAndController(IModel inModel, IController inController) {
        super.setModelAndController(inModel, inController);
        log = getController().getLog();
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
        updateButton.setEnabled(false);

        JudgementRecord judgementRecord = null;
        RunResultFiles runResultFiles = null;

        if (judgementChanged()) {
            newRun.setStatus(RunStates.JUDGED);

            boolean solved = getJudgementComboBox().getSelectedIndex() == 0;
            Judgement judgement = (Judgement) getJudgementComboBox().getSelectedItem();

            judgementRecord = new JudgementRecord(judgement.getElementId(), getModel().getClientId(), solved, false);
            judgementRecord.setSendToTeam(getNotifyTeamCheckBox().isSelected());
            
            System.out.println("debug22 Run "+newRun);
            System.out.println("debug22 judgement "+judgementRecord.isSolved()+" "+judgementRecord.isSendToTeam()+" "+judgement);
        }

        newRun.setDeleted(deleteCheckBox.isSelected());
        
        int elapsed = getIntegerValue(getElapsedTimeTextField().getText());
        newRun.setElapsedMins(elapsed);

        ElementId problemId = ((Problem) getProblemComboBox().getSelectedItem()).getElementId();
        if (problemId != null) {
            run.setProblemId(problemId);
        }
        ElementId languageId = ((Language) getLanguageComboBox().getSelectedItem()).getElementId();
        if (languageId != null) {
            run.setLanguageId(languageId);
        }
        
        newRun.setProblemId(problemId);

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

            int result = FrameUtilities.yesNoCancelDialog("Run modified, save changes?", "Confirm Choice");

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
            statusLabel.setText(run.getStatus().toString());
            elapsedTimeTextField.setText(new Long(run.getElapsedMins()).toString());

        } else {
            getUpdateButton().setVisible(false);

            runInfoLabel.setText("Could not get run " + +run2.getNumber() + " (Site " + run2.getSiteNumber() + ")");
            deleteCheckBox.setSelected(false);
            statusLabel.setText("");
            elapsedTimeTextField.setText("");

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

        if (run == null) {
            return; // No run no combo boxes.
        }

        for (Problem problem : getModel().getProblems()) {
            getProblemComboBox().addItem(problem);
            if (problem.getElementId().equals(run.getProblemId())) {
                selectedIndex = index;
            }
            index++;
        }

        getProblemComboBox().setSelectedIndex(selectedIndex);

        selectedIndex = -1;
        index = 0;

        for (Language language : getModel().getLanguages()) {
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

        if (run.isJudged()) {
            judgementId = run.getJudgementRecord().getJudgementId();
        }

        for (Judgement judgement : getModel().getJudgements()) {
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
        }

        getUpdateButton().setEnabled(enableButton);

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
            statusLabel = new JLabel();
            statusLabel.setBounds(new java.awt.Rectangle(224, 35, 271, 19));
            statusLabel.setText("JLabel");
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
            generalPane.add(statusLabel, null);
            generalPane.add(statusTitleLabel, null);
            generalPane.add(getProblemComboBox(), null);
            generalPane.add(getLanguageComboBox(), null);
            generalPane.add(problemLabel, null);
            generalPane.add(languageLabel, null);
            generalPane.add(jLabel, null);
            generalPane.add(getElapsedTimeTextField(), null);
            generalPane.add(getNotifyTeamCheckBox(), null);
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
                    executeRun();
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
        showMessage("Would have extracted run");
        // TODO code extract run
    }

    protected void executeRun() {

        System.gc();

        executable = new Executable(getModel(), getController(), run, runFiles);

        IFileViewer fileViewer = executable.execute();
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
    private JComboBox getJudgementComboBox() {
        if (judgementComboBox == null) {
            judgementComboBox = new JComboBox();
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
    private JComboBox getProblemComboBox() {
        if (problemComboBox == null) {
            problemComboBox = new JComboBox();
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
    private JComboBox getLanguageComboBox() {
        if (languageComboBox == null) {
            languageComboBox = new JComboBox();
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
        Executable tempEexecutable = new Executable(getModel(), getController(), run, runFiles);
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
            notifyTeamCheckBox.setSelected(true);
            notifyTeamCheckBox.setText("Notify Team");
        }
        return notifyTeamCheckBox;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
