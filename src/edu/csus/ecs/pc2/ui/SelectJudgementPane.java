package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.Executable;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunResultFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;

/**
 * Add/Edit Run Pane
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class SelectJudgementPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -4185051421490716684L;

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

    private JLabel judgementLabel = null;

    private JLabel statusLabel = null;

    private JLabel statusTitleLabel = null;

    private JLabel problemTitleLabel = null;

    private JLabel languageTitleLabel = null;

    private JLabel elapsedTitleLabel = null;

    private IFileViewer sourceViewer;

    private boolean populatingGUI = true;

    private JLabel problemNameLabel = null;

    private JLabel languageNameLabel = null;

    private JLabel elapsedTimeLabel = null;

    private JCheckBox notifyTeamCheckBox = null;

    private PermissionList permissionList = new PermissionList();
    
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
        this.setSize(new java.awt.Dimension(536, 269));

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getMainTabbedPane(), java.awt.BorderLayout.EAST);
        this.add(getGeneralPane(), java.awt.BorderLayout.CENTER);
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);
        log = getController().getLog();
        
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
    
    private void cancelRun() {
        
        enableUpdateButtons(false);
        Run newRun = getRunFromFields();
        getController().cancelRun(newRun);
        
    }

    protected void updateRun() {

        Run newRun = getRunFromFields();

        enableUpdateButtons(false);

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

        if (theRun != null) {
            getUpdateButton().setVisible(true);

            ClientId id = theRun.getSubmitter();
            String teamName = id.getName();

            runInfoLabel.setText("Run " + theRun.getNumber() + " (Site " + theRun.getSiteNumber() + ") from " + teamName);
            statusLabel.setText(run.getStatus().toString());
            elapsedTimeLabel.setText(new Long(run.getElapsedMins()).toString());
            
            problemNameLabel.setText(getContest().getProblem(run.getProblemId()).toString());            
            languageNameLabel.setText(getContest().getLanguage(run.getLanguageId()).toString());            

        } else {
            getUpdateButton().setVisible(false);

            runInfoLabel.setText("Could not get run " + +theRun.getNumber() + " (Site " + theRun.getSiteNumber() + ")");
            statusLabel.setText("");
            elapsedTimeLabel.setText("");
            
            problemNameLabel.setText("");            
            languageNameLabel.setText("");            

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
            elapsedTimeLabel = new JLabel();
            elapsedTimeLabel.setBounds(new java.awt.Rectangle(223, 61, 254, 19));
            elapsedTimeLabel.setText("Elapsed");
            elapsedTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            languageNameLabel = new JLabel();
            languageNameLabel.setBounds(new java.awt.Rectangle(223, 111, 254, 19));
            languageNameLabel.setText("Language");
            languageNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            problemNameLabel = new JLabel();
            problemNameLabel.setBounds(new java.awt.Rectangle(223, 86, 254, 19));
            problemNameLabel.setText("Problem");
            problemNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            elapsedTitleLabel = new JLabel();
            elapsedTitleLabel.setBounds(new java.awt.Rectangle(69, 61, 142, 19));
            elapsedTitleLabel.setText("Elapsed");
            elapsedTitleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            languageTitleLabel = new JLabel();
            languageTitleLabel.setBounds(new java.awt.Rectangle(69, 111, 142, 19));
            languageTitleLabel.setText("Language");
            languageTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            problemTitleLabel = new JLabel();
            problemTitleLabel.setBounds(new java.awt.Rectangle(69, 86, 142, 19));
            problemTitleLabel.setText("Problem");
            problemTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            statusTitleLabel = new JLabel();
            statusTitleLabel.setBounds(new java.awt.Rectangle(69, 33, 142, 19));
            statusTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            statusTitleLabel.setText("Status");
            statusLabel = new JLabel();
            statusLabel.setBounds(new java.awt.Rectangle(223, 33, 254, 19));
            statusLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            statusLabel.setText("JLabel");
            judgementLabel = new JLabel();
            judgementLabel.setBounds(new java.awt.Rectangle(69, 138, 142, 19));
            judgementLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            judgementLabel.setText("Judgement");
            runInfoLabel = new JLabel();
            runInfoLabel.setBounds(new java.awt.Rectangle(5, -1, 511, 25));
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

        executable = new Executable(getContest(), getController(), run, runFiles);

        IFileViewer fileViewer = executable.execute();
        fileViewer.setVisible(true);
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
            judgementComboBox.setLocation(new java.awt.Point(223, 136));
            judgementComboBox.setSize(new java.awt.Dimension(263, 22));
            judgementComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return judgementComboBox;
    }

    private void createAndViewFile(SerializedFile file, String title) {
        // TODO the executeable dir name should be from the model, eh ?
        Executable tempEexecutable = new Executable(getContest(), getController(), run, runFiles);
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
            sourceViewer.addTextPane(title, "Could not create file at " + targetFileName + "Exception " + e.getMessage());
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
            notifyTeamCheckBox.setBounds(new java.awt.Rectangle(223, 174, 134, 21));
            notifyTeamCheckBox.setSelected(true);
            notifyTeamCheckBox.setText("Notify Team");
        }
        return notifyTeamCheckBox;
    }
    
    
    private boolean isAllowed (Permission.Type type){
        return permissionList.isAllowed(type);
    }
    
    
    private void initializePermissions() {
        Account account = getContest().getAccount(getContest().getClientId());
        if (account != null){
            permissionList.clearAndLoadPermissions(account.getPermissionList());
        }
    }

    private void updateGUIperPermissions() {
        
        extractButton.setVisible(isAllowed(Permission.Type.EXTRACT_RUNS));
    }
    
} // @jve:decl-index=0:visual-constraint="10,10"
