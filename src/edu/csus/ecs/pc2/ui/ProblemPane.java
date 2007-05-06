package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import javax.swing.ButtonGroup;

/**
 * Add/Edit Problem Pane
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class ProblemPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -1060536964672397704L;

    private JPanel messagePane = null;

    private JPanel buttonPane = null;

    private JButton addButton = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private JLabel messageLabel = null;

    private Problem problem = null;

    private JTabbedPane mainTabbedPane = null;

    private JPanel generalPane = null;

    private JTextField problemNameTextField = null;

    private JTextField timeOutSecondTextField = null;

    private JCheckBox problemRequiresDataCheckBox = null;

    private JPanel dataProblemPane = null;

    private JPanel readsFromPane = null;

    private JPanel inputDataFilePane = null;

    private JRadioButton stdinRadioButton = null;

    private JRadioButton fileRadioButton = null;

    private JPanel fileNamePane = null;

    private JButton selectFileButton = null;

    private JCheckBox judgesHaveAnswerFiles = null;

    private JPanel answerFilePane = null;

    private JPanel answerFilenamePane = null;

    private JButton answerBrowseButton = null;

    private JLabel inputDataFileLabel = null;

    private JLabel answerFileNameLabel = null;

    private JLabel problemNameLabel = null;

    private JLabel timeoutLabel = null;

    private Log log = null;

    private boolean populatingGUI = true;

    /**
     * last directory where searched for files.
     */
    private String lastDirectory;

    private ProblemDataFiles newProblemDataFiles;

    private JCheckBox useInternalValidatorCheckBox = null;

    private ButtonGroup teamReadsFrombuttonGroup = null; // @jve:decl-index=0:visual-constraint="598,159"

    /**
     * This method initializes
     * 
     */
    public ProblemPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(536, 405));

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getMainTabbedPane(), java.awt.BorderLayout.EAST);
        this.add(getGeneralPane(), java.awt.BorderLayout.CENTER);
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);

        // getContest().addProblemListener(new Proble)

        log = getController().getLog();
    }

    public String getPluginTitle() {
        return "Edit Problem Pane";
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
            buttonPane.add(getAddButton(), null);
            buttonPane.add(getUpdateButton(), null);
            buttonPane.add(getCancelButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes addButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add");
            addButton.setMnemonic(java.awt.event.KeyEvent.VK_A);
            addButton.setEnabled(false);
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addProblem();
                }
            });
        }
        return addButton;
    }

    protected void addProblem() {

        if (problemNameTextField.getText().trim().length() < 1) {
            showMessage("Enter a problem name");
            return;
        }

        Problem newProblem = getProblemFromFields();

        if (newProblem == null) {
            // new problem invalid, just return, message issued earlier
            return;
        }

        getController().addNewProblem(newProblem, newProblemDataFiles);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
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
     * Enable or disable Update button based on comparison of run to fields.
     * 
     */
    public void enableUpdateButton() {

        if (populatingGUI) {
            return;
        }

        boolean enableButton = false;

        if (problem != null) {

            enableButton |= (!problem.getDisplayName().equals(getProblemNameTextField().getText()));

            int timeOutSeconds = getIntegerValue(timeOutSecondTextField.getText());
            enableButton |= (timeOutSeconds != problem.getTimeOutInSeconds());

            boolean hasDataFile = problem.getDataFileName() != null;
            enableButton |= (hasDataFile != problemRequiresDataCheckBox.isSelected());
            if (hasDataFile) {
                enableButton |= (!inputDataFileLabel.getText().equals(problem.getDataFileName()));
            }

            boolean hasAnswerFile = problem.getAnswerFileName() != null;
            enableButton |= (hasAnswerFile != judgesHaveAnswerFiles.isSelected());
            if (hasAnswerFile) {
                enableButton |= (!answerFileNameLabel.getText().equals(problem.getAnswerFileName()));
            }

            enableButton |= (stdinRadioButton.isSelected() != problem.isReadInputDataFromSTDIN());

            enableButton |= (fileRadioButton.isSelected() && problem.isReadInputDataFromSTDIN());
        } else {
            if (getAddButton().isVisible()) {
                enableButton = true;
            }
        }

        enableUpdateButtons(enableButton);

    }

    /**
     * Create a Problem from the fields.
     * 
     * This also populates newProblemDataFiles for the data files.
     * 
     * @return
     */
    private Problem getProblemFromFields() {
        if (problem == null) {
            problem = new Problem(problemNameTextField.getText());
        } else {
            problem.setDisplayName(problemNameTextField.getText());
        }

        newProblemDataFiles = new ProblemDataFiles(problem);

        int secs = getIntegerValue(timeOutSecondTextField.getText());
        problem.setTimeOutInSeconds(secs);

        if (problemRequiresDataCheckBox.isSelected()) {

            String fileName = inputDataFileLabel.getText();
            if (fileName.trim().length() == 0) {
                showMessage("Problem Requires Input Data checked, select a file ");
                return null;
            }

            SerializedFile serializedFile = new SerializedFile(fileName);

            if (serializedFile.getBuffer() == null) {
                showMessage("Unable to read file " + fileName + " choose data file again");
                return null;
            }

            problem.setDataFileName(serializedFile.getName());
            newProblemDataFiles.setJudgesDataFile(serializedFile);
        } else {
            problem.setDataFileName(null);
        }

        if (judgesHaveAnswerFiles.isSelected()) {
            String fileName = answerFileNameLabel.getText();

            if (fileName.trim().length() == 0) {
                // TODO more specific message about which file is required
                showMessage("Judges Have Provided Answer File checked, select a file ");
                return null;
            }

            SerializedFile serializedFile = new SerializedFile(fileName);

            if (serializedFile.getBuffer() == null) {
                showMessage("Unable to read file " + fileName + " choose answer file again");
                return null;
            }

            problem.setAnswerFileName(serializedFile.getName());
            newProblemDataFiles.setJudgesAnswerFile(serializedFile);
        } else {
            problem.setAnswerFileName(null);
        }

        if (stdinRadioButton.isSelected() && fileRadioButton.isSelected()) {
            // TODO make radio button group to obviate this message
            showMessage("Pick just one radio button TODO fix all TODOs!");
            return null;
        }

        if (fileRadioButton.isSelected()) {

            problem.setReadInputDataFromSTDIN(false);

        } else if (stdinRadioButton.isSelected()) {

            problem.setReadInputDataFromSTDIN(true);
        }

        problem.setUsingPC2Validator(useInternalValidatorCheckBox.isSelected());

        return problem;
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
                    updateProblem();
                }
            });
        }
        return updateButton;
    }

    protected void updateProblem() {

        if (!ensureProblem()) {
            // new problem is invalid, just return, message issued earlier
            return;
        }
        Problem newProblem = getProblemFromFields();

        if (newProblem == null) {
            // new problem invalid, just return, message issued earlier
            return;
        }

        getController().updateProblem(newProblem, newProblemDataFiles);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    private boolean ensureProblem() {
        if (problemNameTextField.getText().trim().length() < 1) {
            showMessage("Enter a problem name");
            return false;
        }
        String fileName = inputDataFileLabel.getText();
        if (getProblemRequiresDataCheckBox().isSelected()) {
            // this check is outside so we can provide a specific message
            if (fileName.trim().length() == 0) {
                showMessage("Problem Requires Input Data checked, select a file ");
                return false;
            }
            if (!checkFile(fileName)) {
                return false;
            }
        } else {
            if (fileName != null && fileName.trim().length() > 0) {
                // file selected, but checkbox not clicked
                int verifyInputDataFile = JOptionPane.showConfirmDialog(getParent(), "Use selected data file?");
                switch (verifyInputDataFile) {
                    case JOptionPane.CANCEL_OPTION:
                        showMessage("Update cancelled");
                        return false;
                    case JOptionPane.YES_OPTION:
                        getProblemRequiresDataCheckBox().setSelected(true);
                        break;
                    case JOptionPane.NO_OPTION:
                        inputDataFileLabel.setText("");
                        break;
                    default:
                        return false;
                }
            }
        }
        fileName = answerFileNameLabel.getText();
        if (getJudgesHaveAnswerFiles().isSelected()) {
            // this check is outside so we can provide a specific message
            if (fileName.trim().length() == 0) {
                showMessage("Problem Requires Judges' Answer File checked, select a file ");
                return false;
            }
            if (!checkFile(fileName)) {
                return false;
            }
            if (fileName != null && fileName.trim().length() > 0) {
                // file selected, but checkbox not clicked
                int verifyAnswerFile = JOptionPane.showConfirmDialog(getParent(), "Use selected answer file?");
                switch (verifyAnswerFile) {
                    case JOptionPane.CANCEL_OPTION:
                        showMessage("Update cancelled");
                        return false;
                    case JOptionPane.YES_OPTION:
                        getJudgesHaveAnswerFiles().setSelected(true);
                        break;
                    case JOptionPane.NO_OPTION:
                        answerFileNameLabel.setText("");
                        break;
                    default:
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks to ensure the fileName exists, is a file, and is readable.
     * 
     * @param fileName the file to check
     * @return true if the is readable
     */
    private boolean checkFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            if (file.isFile()) {
                if (!file.canRead()) {
                    showMessage("Could not read file " + fileName);
                    return false;
                } // else exists, is a file, and is readable
            } else {
                // not a file
                showMessage(fileName + " is not a file");
                return false;
            }
        } else {
            showMessage(fileName + " does not exist");
            return false;
        }
        return true;
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

    protected void handleCancelButton() {

        if (getAddButton().isEnabled() || getUpdateButton().isEnabled()) {

            // Something changed, are they sure ?

            int result = FrameUtilities.yesNoCancelDialog("Problem modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                if (getAddButton().isEnabled()) {
                    addProblem();
                } else {
                    updateProblem();
                }
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            } else if (result == JOptionPane.NO_OPTION) {
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

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(final Problem problem) {

        this.problem = problem;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(problem);
                enableUpdateButtons(false);
                showMessage("");
            }
        });
    }

    private void populateGUI(Problem inProblem) {

        populatingGUI = true;

        if (inProblem != null) {

            getAddButton().setVisible(false);
            getUpdateButton().setVisible(true);

            problemNameTextField.setText(inProblem.getDisplayName());
            timeOutSecondTextField.setText(inProblem.getTimeOutInSeconds() + "");
            inputDataFileLabel.setText(inProblem.getDataFileName());
            answerFileNameLabel.setText(inProblem.getAnswerFileName());

            judgesHaveAnswerFiles.setSelected(inProblem.getAnswerFileName() != null);
            problemRequiresDataCheckBox.setSelected(inProblem.getDataFileName() != null);

            if (inProblem.isReadInputDataFromSTDIN()) {
                fileRadioButton.setSelected(false);
                stdinRadioButton.setSelected(true);
            } else {
                fileRadioButton.setSelected(true);
                stdinRadioButton.setSelected(false);
            }

        } else {

            getAddButton().setVisible(true);
            getUpdateButton().setVisible(false);
            addButton.setEnabled(true);
            updateButton.setEnabled(false);

            problemNameTextField.setText("");
            timeOutSecondTextField.setText("");
            judgesHaveAnswerFiles.setSelected(false);
            problemRequiresDataCheckBox.setSelected(false);
            inputDataFileLabel.setText("");
            answerFileNameLabel.setText("");
            fileRadioButton.setSelected(true);
            stdinRadioButton.setSelected(false);

        }

        populatingGUI = false;
    }

    protected void enableUpdateButtons(boolean editedText) {
        if (editedText) {
            cancelButton.setText("Cancel");
        } else {
            cancelButton.setText("Close");
        }
        updateButton.setEnabled(editedText);
        addButton.setEnabled(editedText);
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
            timeoutLabel = new JLabel();
            timeoutLabel.setBounds(new java.awt.Rectangle(23, 46, 175, 16));
            timeoutLabel.setText("Run Timeout Limit (Secs)");
            problemNameLabel = new JLabel();
            problemNameLabel.setBounds(new java.awt.Rectangle(23, 14, 179, 16));
            problemNameLabel.setText("Problem name");
            generalPane = new JPanel();
            generalPane.setLayout(null);
            generalPane.add(getProblemNameTextField(), null);
            generalPane.add(getJTextField(), null);
            generalPane.add(getProblemRequiresDataCheckBox(), null);
            generalPane.add(getDataProblemPane(), null);
            generalPane.add(getJudgesHaveAnswerFiles(), null);
            generalPane.add(getAnswerFilePane(), null);
            generalPane.add(problemNameLabel, null);
            generalPane.add(timeoutLabel, null);
            generalPane.add(getUseInternalValidatorCheckBox(), null);
        }
        return generalPane;
    }

    /**
     * This method initializes problemNameTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getProblemNameTextField() {
        if (problemNameTextField == null) {
            problemNameTextField = new JTextField();
            problemNameTextField.setPreferredSize(new java.awt.Dimension(120, 20));
            problemNameTextField.setSize(new java.awt.Dimension(273, 20));
            problemNameTextField.setLocation(new java.awt.Point(220, 12));
            problemNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return problemNameTextField;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJTextField() {
        if (timeOutSecondTextField == null) {
            timeOutSecondTextField = new JTextField();
            timeOutSecondTextField.setBounds(new java.awt.Rectangle(220, 44, 120, 20));
            timeOutSecondTextField.setPreferredSize(new java.awt.Dimension(120, 20));
            timeOutSecondTextField.setDocument(new IntegerDocument());
            timeOutSecondTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return timeOutSecondTextField;
    }

    /**
     * This method initializes problemRequiresDataTextField
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getProblemRequiresDataCheckBox() {
        if (problemRequiresDataCheckBox == null) {
            problemRequiresDataCheckBox = new JCheckBox();
            problemRequiresDataCheckBox.setBounds(new java.awt.Rectangle(23, 76, 257, 26));
            problemRequiresDataCheckBox.setText("Problem Requires Input Data");
            problemRequiresDataCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return problemRequiresDataCheckBox;
    }

    /**
     * This method initializes DataProblemPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDataProblemPane() {
        if (dataProblemPane == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(2);
            gridLayout.setHgap(5);
            gridLayout.setVgap(6);
            dataProblemPane = new JPanel();
            dataProblemPane.setLayout(gridLayout);
            dataProblemPane.setBounds(new java.awt.Rectangle(53, 114, 423, 113));
            dataProblemPane.add(getReadsFromPane(), null);
            dataProblemPane.add(getInputDataFilePane(), null);
        }
        return dataProblemPane;
    }

    /**
     * This method initializes readsFromPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getReadsFromPane() {
        if (readsFromPane == null) {
            FlowLayout flowLayout1 = new FlowLayout();
            flowLayout1.setHgap(35);
            flowLayout1.setAlignment(java.awt.FlowLayout.LEFT);
            flowLayout1.setVgap(0);
            readsFromPane = new JPanel();
            readsFromPane.setLayout(flowLayout1);
            readsFromPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Team Reads From", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), new java.awt.Color(51, 51, 51)));
            readsFromPane.add(getFileRadioButton(), null);
            readsFromPane.add(getStdinRadioButton(), null);
            getTeamReadsFrombuttonGroup().setSelected(getFileRadioButton().getModel(), true);
        }
        return readsFromPane;
    }

    /**
     * This method initializes inputDataFilePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInputDataFilePane() {
        if (inputDataFilePane == null) {
            BorderLayout borderLayout = new BorderLayout();
            borderLayout.setHgap(15);
            borderLayout.setVgap(5);
            inputDataFilePane = new JPanel();
            inputDataFilePane.setLayout(borderLayout);
            inputDataFilePane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Input Data File", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            inputDataFilePane.setPreferredSize(new java.awt.Dimension(98, 45));
            inputDataFilePane.add(getFileNamePane(), java.awt.BorderLayout.CENTER);
            inputDataFilePane.add(getSelectFileButton(), java.awt.BorderLayout.EAST);
        }
        return inputDataFilePane;
    }

    /**
     * This method initializes stdinRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getStdinRadioButton() {
        if (stdinRadioButton == null) {
            stdinRadioButton = new JRadioButton();
            stdinRadioButton.setText("Stdin");
            stdinRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return stdinRadioButton;
    }

    /**
     * This method initializes fileRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getFileRadioButton() {
        if (fileRadioButton == null) {
            fileRadioButton = new JRadioButton();
            fileRadioButton.setText("File");
            fileRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return fileRadioButton;
    }

    /**
     * This method initializes fileNamePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getFileNamePane() {
        if (fileNamePane == null) {
            inputDataFileLabel = new JLabel();
            inputDataFileLabel.setText("");
            fileNamePane = new JPanel();
            fileNamePane.setLayout(new BorderLayout());
            fileNamePane.add(inputDataFileLabel, java.awt.BorderLayout.CENTER);
        }
        return fileNamePane;
    }

    /**
     * This method initializes selectFileButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSelectFileButton() {
        if (selectFileButton == null) {
            selectFileButton = new JButton();
            selectFileButton.setText("Browse");
            selectFileButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectFile(inputDataFileLabel)) {
                        inputDataFileLabel.setToolTipText(inputDataFileLabel.getText());
                    }
                    enableUpdateButton();
                }
            });
        }
        return selectFileButton;
    }

    /**
     * This method initializes jCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getJudgesHaveAnswerFiles() {
        if (judgesHaveAnswerFiles == null) {
            judgesHaveAnswerFiles = new JCheckBox();
            judgesHaveAnswerFiles.setBounds(new java.awt.Rectangle(23, 239, 302, 24));
            judgesHaveAnswerFiles.setText("Judges Have Provided Answer File");
            judgesHaveAnswerFiles.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return judgesHaveAnswerFiles;
    }

    /**
     * This method initializes answerFilePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAnswerFilePane() {
        if (answerFilePane == null) {
            BorderLayout borderLayout1 = new BorderLayout();
            borderLayout1.setHgap(15);
            borderLayout1.setVgap(5);
            answerFilePane = new JPanel();
            answerFilePane.setLayout(borderLayout1);
            answerFilePane.setBounds(new java.awt.Rectangle(53, 275, 423, 52));
            answerFilePane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Answer File", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            answerFilePane.add(getAnswerFilenamePane(), java.awt.BorderLayout.CENTER);
            answerFilePane.add(getAnswerBrowseButton(), java.awt.BorderLayout.EAST);
        }
        return answerFilePane;
    }

    /**
     * This method initializes answerFilenamePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAnswerFilenamePane() {
        if (answerFilenamePane == null) {
            answerFileNameLabel = new JLabel();
            answerFileNameLabel.setText("");
            answerFilenamePane = new JPanel();
            answerFilenamePane.setLayout(new BorderLayout());
            answerFilenamePane.add(answerFileNameLabel, java.awt.BorderLayout.CENTER);
        }
        return answerFilenamePane;
    }

    /**
     * This method initializes answerBrowseButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAnswerBrowseButton() {
        if (answerBrowseButton == null) {
            answerBrowseButton = new JButton();
            answerBrowseButton.setText("Browse");
            answerBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectFile(answerFileNameLabel)) {
                        answerFileNameLabel.setToolTipText(answerFileNameLabel.getText());
                    }
                    enableUpdateButton();
                }
            });
        }
        return answerBrowseButton;
    }

    public void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                messageLabel.setText(message);
            }
        });
    }

    /**
     * select file, if file picked updates label.
     * 
     * @param label
     * @return
     * @throws Exception
     */
    private boolean selectFile(JLabel label) {
        JFileChooser chooser = new JFileChooser(lastDirectory);
        try {
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                lastDirectory = chooser.getCurrentDirectory().toString();
                label.setText(chooser.getSelectedFile().getCanonicalFile().toString());
            }
        } catch (Exception e) {
            log.log(Log.INFO, "Error getting selected file, try again.", e);
        }
        chooser = null;
        return true;
    }

    /**
     * This method initializes useInternalValidator
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getUseInternalValidatorCheckBox() {
        if (useInternalValidatorCheckBox == null) {
            useInternalValidatorCheckBox = new JCheckBox();
            useInternalValidatorCheckBox.setBounds(new java.awt.Rectangle(23, 357, 340, 16));
            useInternalValidatorCheckBox.setText("Use PC^2 Validator");
        }
        return useInternalValidatorCheckBox;
    }

    /**
     * This method initializes teamReadsFrombuttonGroup
     * 
     * @return javax.swing.ButtonGroup
     */
    private ButtonGroup getTeamReadsFrombuttonGroup() {
        if (teamReadsFrombuttonGroup == null) {
            teamReadsFrombuttonGroup = new ButtonGroup();
            teamReadsFrombuttonGroup.add(getStdinRadioButton());
            teamReadsFrombuttonGroup.add(getFileRadioButton());
        }
        return teamReadsFrombuttonGroup;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
