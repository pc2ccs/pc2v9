package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.export.ExportYAML;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.CustomValidatorSettings;
import edu.csus.ecs.pc2.core.model.ClicsValidatorSettings;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.report.IReport;
import edu.csus.ecs.pc2.core.report.ProblemsReport;
import edu.csus.ecs.pc2.core.report.SingleProblemReport;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;

/**
 * Add/Edit Problem Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditProblemPane extends JPanePlugin {

    // TODO 917 automatic check on load when external/internal data sets changed

    public static final String DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND = "{:validator} {:infile} {:outfile} {:ansfile} {:resfile} ";

    private static boolean debug22EditProblem = false;

    /**
     *  
     */
    private static final long serialVersionUID = -1060536964672397704L;

    private String lastSaveDirectory = null;

    private JPanel messagePane = null;

    private JPanel buttonPane = null;

    private JButton addButton = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private JLabel messageLabel = null;

    /**
     * The original/input problem.
     */
    private Problem problem = null; // @jve:decl-index=0:

    private JTabbedPane mainTabbedPane = null;

    private JPanel generalPane = null;

    private JPanel judgingTypePane = null;

    private JTextField problemNameTextField = null;

    private JTextField timeOutSecondTextField = null;

    private JTextField problemLetterTextField = null;

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

    /**
     * Is the form/GUI being currently populated? Used to avoid reEntry/race conditions populating GUI.
     */
    private boolean populatingGUI = true;

    /**
     * last directory where searched for files.
     */
    private String lastDirectory; // @jve:decl-index=0:

    @SuppressWarnings("unused")
    private String lastYamlLoadDirectory;

    /**
     * The current/original data files, used to compare with changes.
     */
    protected ProblemDataFiles originalProblemDataFiles;

    protected ProblemDataFiles newProblemDataFiles;

    private ButtonGroup teamReadsFrombuttonGroup = null; // @jve:decl-index=0:visual-constraint="586,61"

    private ButtonGroup judgingTypeGroup = null; // @jve:decl-index=0:visual-constraint="586,61"

    //the top-level tabbed pane holding settings for various validator options
    private JPanel validatorPane = null;

    private JRadioButton useNOValidatatorRadioButton = null;

    private JRadioButton usePC2ValidatorRadioButton = null;

    private JRadioButton useCLICSValidatorRadioButton = null;

    private JRadioButton useCustomValidatorRadioButton = null;

    private JCheckBox showValidatorToJudgesCheckBox = null;

    private JCheckBox isCLICSCaseSensitiveCheckBox = null;
    
    private JCheckBox isCLICSSpaceSensitiveCheckBox = null;

    private JCheckBox showCompareCheckBox = null;

    private JCheckBox doShowOutputWindowCheckBox = null;

    private ButtonGroup validatorChoiceButtonGroup = null; // @jve:decl-index=0:visual-constraint="595,128"

    private static final String NL = System.getProperty("line.separator");

    private JRadioButton computerJudgingRadioButton = null;

    private JRadioButton manualJudgingRadioButton = null;

    private JCheckBox manualReviewCheckBox = null;

    private JCheckBox prelimaryNotificationCheckBox = null;

    private JCheckBox deleteProblemCheckBox = null;

    private boolean listenersAdded = false;

    private JButton loadButton = null;

    private ContestSnakeYAMLLoader loader = null;

    private JButton exportButton = null;

    private JButton reportButton = null;

    private MultipleDataSetPane multipleDataSetPane = null;

    private JPanel judgeTypeInnerPane = null;

    private JPanel ccsSettingsPane = null;

    private JCheckBox ccsValidationEnabledCheckBox = null;

    private JTextField shortNameTextfield;

    private String fileNameOne;
    
    //the panel holding the "Do not use validator" radio button
    private JPanel noValidatorPanel;

    //the panel holding the "use CLICS default validator" radio button and the corresponding options panel
    private JPanel clicsValidatorPanel = null;
    private JPanel clicsValidatorOptionsSubPanel = null;    
      private JCheckBox floatRelativeToleranceCheckBox;
      private JTextField floatRelativeToleranceTextField;
      private JCheckBox floatAbsoluteToleranceCheckBox;
      private JTextField floatAbsoluteToleranceTextField;

    //the panel holding the "use custom validator" radio button and the corresponding options panel
    private JPanel customValidatorPanel = null;
    private Component horizontalStrut_1;
    private JPanel customValidatorOptionsSubPanel;
      private JLabel customValidatorCommandLabel;
      private JTextField customValidatorCommandTextField;
      private JButton chooseValidatorProgramButton = null;
      private JLabel customValidatorCommandOptionsLabel = null;
      private JTextField customValidatorCommandOptionsTextField = null;

    
    private Component horizontalStrut;    
    private Component verticalStrut;
    private Component verticalStrut_1;
    private Component verticalStrut_2;
    private Component verticalStrut_3;
    private Component verticalStrut_4;
    private Component verticalStrut_5;
    private JPanel pc2ValidatorPanel;
    private Component horizontalStrut_2;
    private JPanel pc2ValidatorOptionsSubPanel;
      private JLabel pc2ValidatorOptionComboBoxLabel;
      private JComboBox<String> pc2ValidatorOptionComboBox;
      private JCheckBox pc2ValidatorIgnoreCaseCheckBox;

    private JLabel lblWhatsThis;

    /**
     * This method initializes
     * 
     */
    public EditProblemPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(539, 511));

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getMainTabbedPane(), java.awt.BorderLayout.CENTER);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        addWindowListeners();

        getMultipleDataSetPane().setContestAndController(inContest, inController);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getLoadButton().setVisible(Utilities.isDebugMode());
                getExportButton().setVisible(Utilities.isDebugMode());
            }
        });

        if (IniFile.isFilePresent()) {
            String value = IniFile.getValue("client.debug");
            if (value != null) {
                debug22EditProblem = value.equalsIgnoreCase("true");
            }
        }

    }

    private void addWindowListeners() {

        if (listenersAdded) {
            // No need to add the listeners twice or more.
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (getParentFrame() != null) {
                    getParentFrame().addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            handleCancelButton();
                        }

                        @Override
                        public void windowOpened(WindowEvent e) {
                            getProblemNameTextField().requestFocus();
                        }

                        public void windowActivated(WindowEvent e) {
                            getProblemNameTextField().requestFocus();
                        };
                    });
                    listenersAdded = true;
                }
            }
        });
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
            buttonPane.add(getReportButton(), null);
            buttonPane.add(getLoadButton(), null);
            buttonPane.add(getExportButton(), null);
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

    /**
     * Add Problem to the fields.
     * 
     */
    protected void addProblem() {

        if (problemNameTextField.getText().trim().length() < 1) {
            showMessage("Enter a problem name (\"General\" tab)");
            return;
        }

        if (!validateProblemFields()) {
            // new problem is invalid, just return, message issued by validateProblemFields
            System.err.println ("DEBUG: validateProblemFields() returned false");
            return;
        }

        Problem newProblem = null;
        try {
            newProblemDataFiles = getProblemDataFilesFromFields();

            newProblem = getProblemFromFields(null, newProblemDataFiles);

            SerializedFile sFile;
            // SOMEDAY should we loop thru the files doing the check?
            if (newProblemDataFiles.getJudgesDataFiles().length == 1) {
                sFile = newProblemDataFiles.getJudgesDataFile();
                if (sFile != null) {
                    if (checkFileFormat(sFile)) {
                        newProblemDataFiles.setJudgesDataFile(sFile);
                    }
                }
            }
            if (newProblemDataFiles.getJudgesAnswerFiles().length == 1) {
                sFile = newProblemDataFiles.getJudgesAnswerFile();
                if (sFile != null) {
                    if (checkFileFormat(sFile)) {
                        newProblemDataFiles.setJudgesAnswerFile(sFile);
                    }
                }
            }
            sFile = newProblemDataFiles.getValidatorFile();
            if (sFile != null) {
                if (checkFileFormat(sFile)) {
                    newProblemDataFiles.setValidatorFile(sFile);
                }
            }
        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

        if (!newProblem.getElementId().equals(newProblemDataFiles.getProblemId())) {
            // this is like ProblemDataFiles.copy but without overwriting the ProblemId, which is the problem
            // we are trying to fix here
            ProblemDataFiles clone = new ProblemDataFiles(newProblem);
            clone.setSiteNumber(newProblemDataFiles.getSiteNumber());
            clone.setValidatorFile(newProblemDataFiles.getValidatorFile());
            clone.setValidatorRunCommand(newProblemDataFiles.getValidatorRunCommand());
            clone.setValidatorFile(newProblemDataFiles.getValidatorRunFile());
            clone.setJudgesAnswerFiles(newProblemDataFiles.getJudgesAnswerFiles());
            clone.setJudgesDataFiles(newProblemDataFiles.getJudgesDataFiles());
            clone.setValidatorFiles(newProblemDataFiles.getValidatorFiles());
            // without this the problemId is wrong in newProblemDataFiles. so the controller.getProblemDataFiles(problem) does not find it
            newProblemDataFiles = clone;
        }

        // Add next letter to problem.
        int numberProblems = getContest().getProblems().length;
        String nextLetter = Utilities.getProblemLetter(numberProblems + 1);
        newProblem.setLetter(nextLetter);

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
        String updateToolTip = "";

        if (problem != null) {

            try {
                Problem changedProblem = getProblemFromFields(null, newProblemDataFiles);
                if (!problem.isSameAs(changedProblem) || getMultipleDataSetPane().hasChanged(originalProblemDataFiles)) {
                    enableButton = true;
                    updateToolTip = "Problem changed";
                }
                ProblemDataFiles pdf = getContest().getProblemDataFile(problem);
                ProblemDataFiles proposedPDF = getMultipleDataSetPane().getProblemDataFiles();
                if (pdf != null) {
                    int fileChanged = 0;
                    SerializedFile[] judgesDataFiles = pdf.getJudgesDataFiles();
                    SerializedFile[] judgesDataFilesNew = null;
                    if (proposedPDF != null) {
                        judgesDataFilesNew = proposedPDF.getJudgesDataFiles();
                    }
                    if ((judgesDataFiles == null && judgesDataFilesNew != null) || (judgesDataFiles != null & judgesDataFilesNew == null)) {
                        // one was null the other was not
                        if (updateToolTip.equals("")) {
                            updateToolTip = "Judges data";
                        } else {
                            updateToolTip += ", Judges data";
                        }
                        enableButton = true;
                    } else if (judgesDataFiles.length != judgesDataFilesNew.length) {
                        fileChanged += Math.abs(judgesDataFiles.length - judgesDataFilesNew.length);
                        if (updateToolTip.equals("")) {
                            updateToolTip = "Judges data";
                        } else {
                            updateToolTip += ", Judges data";
                        }
                        enableButton = true;
                    } else {
                        // compare each file
                        boolean changed = false;
                        if (judgesDataFiles != null) {
                            for (int i = 0; i < judgesDataFiles.length; i++) {
                                SerializedFile serializedFile = judgesDataFiles[i];
                                // external true, we just the sha we do not need to load the data
                                SerializedFile serializedFile2 = new SerializedFile(serializedFile.getAbsolutePath(), true);
                                if (!serializedFile.getName().equals(judgesDataFilesNew[i].getName())) {
                                    // name changed
                                    fileChanged++;
                                    changed = true;
                                } else if (!serializedFile.getSHA1sum().equals(serializedFile2.getSHA1sum())) {
                                    // contents have changed on disk
                                    fileChanged++;
                                    changed = true;
                                }
                            }
                            if (changed) {
                                if (updateToolTip.equals("")) {
                                    updateToolTip = "Judges data";
                                } else {
                                    updateToolTip += ", Judges data";
                                }
                                enableButton = true;
                            }
                        }
                    }
                    SerializedFile[] judgesAnswerFiles = pdf.getJudgesAnswerFiles();
                    SerializedFile[] judgesAnswerFilesNew = null;
                    if (proposedPDF != null) {
                        judgesAnswerFilesNew = proposedPDF.getJudgesAnswerFiles();
                    }
                    if ((judgesAnswerFiles == null && judgesAnswerFilesNew != null) || (judgesAnswerFiles != null & judgesAnswerFilesNew == null)) {
                        // one was null the other was not
                        if (updateToolTip.equals("")) {
                            updateToolTip = "Judges answer";
                        } else {
                            updateToolTip += ", Judges answer";
                        }
                        enableButton = true;
                        fileChanged++;
                    } else if (judgesAnswerFiles.length != judgesAnswerFilesNew.length) {
                        fileChanged += Math.abs(judgesAnswerFiles.length - judgesAnswerFilesNew.length);
                        if (updateToolTip.equals("")) {
                            updateToolTip = "Judges answer";
                        } else {
                            updateToolTip += ", Judges answer";
                        }
                        enableButton = true;
                    } else {
                        // compare each file
                        boolean changed = false;
                        if (judgesAnswerFiles != null) {
                            for (int i = 0; i < judgesAnswerFiles.length; i++) {
                                SerializedFile serializedFile = judgesAnswerFiles[i];
                                // external true, we just the sha we do not need to load the data
                                SerializedFile serializedFile2 = new SerializedFile(serializedFile.getAbsolutePath(), true);
                                if (!serializedFile.getName().equals(judgesAnswerFilesNew[i].getName())) {
                                    fileChanged++;
                                    changed = true;
                                } else if (!serializedFile.getSHA1sum().equals(serializedFile2.getSHA1sum())) {
                                    // contents have changed on disk
                                    fileChanged++;
                                    changed = true;
                                }
                            }
                            if (changed) {
                                if (updateToolTip.equals("")) {
                                    updateToolTip = "Judges answer";
                                } else {
                                    updateToolTip += ", Judges answer";
                                }
                                enableButton = true;
                            }
                        }
                    }
                    String fileName = changedProblem.getValidatorProgramName();
                    if (!problem.isUsingPC2Validator() && fileName != null && fileName.length() > 0) {
                        if (!fileSameAs(pdf.getValidatorFile(), changedProblem.getValidatorProgramName())) {
                            enableButton = true;
                            fileChanged++;
                            if (updateToolTip.equals("")) {
                                updateToolTip = "Validator";
                            } else {
                                updateToolTip += ", Validator";
                            }
                        }
                    }
                    if (fileChanged > 0) {
                        if (fileChanged == 1) {
                            updateToolTip += " file changed";
                        } else {
                            updateToolTip += " files changed";

                        }
                    }
                } else {
                    logDebugException("No ProblemDataFiles for " + problem);
                }

            } catch (InvalidFieldValue e) {
                // invalid field, but that is ok as they are entering data
                // will be caught and reported when they hit update or add.
                logDebugException("Input Problem (but not saving) ", e);

                enableButton = true;
            } catch (Exception ex) {
                logDebugException("Edit Problem ", ex);
                showMessage("Error, check logs.  " + ex.getMessage());
            }

        } else {
            if (getAddButton().isVisible()) {
                enableButton = true;
            }
        }

        if (updateToolTip.equals("")) {
            // otherwise we get a sliver of a tooltip
            getUpdateButton().setToolTipText(null);
        } else {
            getUpdateButton().setToolTipText(updateToolTip);
        }
        enableUpdateButtons(enableButton);

    }

    private void logDebugException(String string) {
        if (Utilities.isDebugMode()) {
            System.err.print("Debug message " + string);
        }
        getLog().log(Log.DEBUG, string);
    }

    private void logDebugException(String string, Exception e) {

        if (Utilities.isDebugMode()) {
            System.err.print("Debug message " + string);
            e.printStackTrace(System.err);
        }
        getLog().log(Log.DEBUG, string, e);

    }

    /**
     * 
     * @param file
     * @param fileName
     * @return false if fileName exists and has changed checksums
     */
    private boolean fileSameAs(SerializedFile file, String fileName) {
        if (fileName != null && !fileName.trim().equals("")) {
            // files changed, treat that as the same
            if (file != null && !fileName.equals(file.getName())) {
                return true;
            }
            return !needsFreshening(file, fileName);
        }
        // default to true
        return true;
    }

    /**
     * Create a Problem from the fields.
     * 
     * This also populates newProblemDataFiles for the data files.
     * 
     * @param checkProblem
     *            will update this Problem if supplied, if null creates a new Problem
     * @param dataFiles
     * @return Problem based on fields
     * @throws InvalidFieldValue
     */
    public Problem getProblemFromFields(Problem checkProblem, ProblemDataFiles dataFiles) {
        
        boolean isAdding;
        
        /**
         * Data file from General tab.
         */
        SerializedFile lastDataFile = null;

        if (debug22EditProblem) {
            Utilities.dump(newProblemDataFiles, "debug 22 in getProblemFromFields start");
        }

        /**
         * Answer file from General Tab.
         */
        SerializedFile lastAnsFile = null;

        if (checkProblem == null) {
            checkProblem = new Problem(problemNameTextField.getText());
            isAdding = true;
            if (newProblemDataFiles == null) {
                // only overwrite if they do not exist already
                newProblemDataFiles = new ProblemDataFiles(checkProblem);
            }
        } else {
            checkProblem.setDisplayName(problemNameTextField.getText());
            checkProblem.setElementId(problem); // duplicate ElementId so that Problem key/lookup is identical
            newProblemDataFiles = dataFiles;
            // newProblemDataFiles = new ProblemDataFiles(problem);
            isAdding = false;

        }

        if (debug22EditProblem) {
            Utilities.dump(newProblemDataFiles, "debug 22 in getProblemFromFields after IF");
        }

        checkProblem.setUsingExternalDataFiles(getMultipleDataSetPane().isUsingExternalDataFiles());

        checkProblem.setTimeOutInSeconds(getIntegerValue(timeOutSecondTextField.getText()));

        checkProblem.setLetter(problemLetterTextField.getText());

        checkProblem.setActive(!getDeleteProblemCheckBox().isSelected());

        checkProblem.setCcsMode(getCcsValidationEnabledCheckBox().isSelected());

        checkProblem.setShortName(shortNameTextfield.getText());
        if (!checkProblem.isValidShortName()) {
            throw new InvalidFieldValue("Invalid problem short name");
        }

        if (problemRequiresDataCheckBox.isSelected()) {

            String fileName = inputDataFileLabel.getText();
            if (fileName == null || fileName.trim().length() == 0) {
                throw new InvalidFieldValue("Problem Requires Input Data checked, select a file ");
            }

            if (fileName.trim().length() != inputDataFileLabel.getToolTipText().length()) {
                fileName = inputDataFileLabel.getToolTipText() + "";
            }

            if (isAdding) {
                SerializedFile serializedFile = new SerializedFile(fileName);

                if (serializedFile.getBuffer() == null) {
                    throw new InvalidFieldValue("Unable to read file " + fileName + " choose data file again (adding)");
                }

                checkProblem.setDataFileName(serializedFile.getName());
                lastDataFile = serializedFile;

            } else {
                if (originalProblemDataFiles.getJudgesDataFiles().length < 2) {
                    // TODO this is not MTS safe
                    SerializedFile serializedFile = originalProblemDataFiles.getJudgesDataFile();
                    if (serializedFile == null || !serializedFile.getAbsolutePath().equals(fileName)) {
                        // they've added a new file
                        serializedFile = new SerializedFile(fileName);
                        checkFileFormat(serializedFile);
                    } else {
                        serializedFile = freshenIfNeeded(serializedFile, fileName);
                    }

                    checkProblem.setDataFileName(serializedFile.getName());
                    lastDataFile = serializedFile;
                } else if (originalProblemDataFiles.getJudgesDataFiles().length > 1) {
                    lastDataFile = originalProblemDataFiles.getJudgesDataFiles()[0];
                    checkProblem.setDataFileName(lastDataFile.getName());
                }
            }
        } else {
            checkProblem.setDataFileName(null);
        }

        if (judgesHaveAnswerFiles.isSelected()) {

            String fileName = answerFileNameLabel.getText();
            if (fileName == null || fileName.trim().length() == 0) {
                throw new InvalidFieldValue("Judges Have Provided Answer File checked, select a file");
            }

            if (fileName.trim().length() != answerFileNameLabel.getToolTipText().length()) {
                fileName = answerFileNameLabel.getToolTipText() + "";
            }

            if (isAdding) {
                SerializedFile serializedFile = new SerializedFile(fileName);

                if (serializedFile.getBuffer() == null) {
                    throw new InvalidFieldValue("Unable to read file " + fileName + " choose answer file again (adding)");
                }

                checkProblem.setAnswerFileName(serializedFile.getName());
                // only do this if we do not already have a JudgesAnswerFile
                if (newProblemDataFiles.getJudgesAnswerFiles().length == 0) {
                    newProblemDataFiles.setJudgesAnswerFile(serializedFile);
                }
                lastAnsFile = serializedFile;
            } else {
                if (originalProblemDataFiles.getJudgesAnswerFiles().length < 2) {
                    // TODO this is not MTS safe
                    SerializedFile serializedFile = originalProblemDataFiles.getJudgesAnswerFile();
                    if (serializedFile == null || !serializedFile.getAbsolutePath().equals(fileName)) {
                        // they've added a new file
                        serializedFile = new SerializedFile(fileName);
                        checkFileFormat(serializedFile);
                    } else {
                        serializedFile = freshenIfNeeded(serializedFile, fileName);
                    }
                    lastAnsFile = serializedFile;

                    checkProblem.setAnswerFileName(serializedFile.getName());
                } else if (originalProblemDataFiles.getJudgesAnswerFiles().length > 0) {
                    lastAnsFile = originalProblemDataFiles.getJudgesAnswerFiles()[0];
                    checkProblem.setAnswerFileName(lastAnsFile.getName());
                }
            }
        } else {
            checkProblem.setAnswerFileName(null);
        }

        checkProblem.setReadInputDataFromSTDIN(stdinRadioButton.isSelected());

        //set the flags indicating which validator (if any) is being used
        checkProblem.setValidatedProblem(!getUseNOValidatatorRadioButton().isSelected());

        if (checkProblem.isValidatedProblem()) {
        	//update validator selection from radio buttons
            checkProblem.setUsingPC2Validator(getUsePC2ValidatorRadioButton().isSelected());
            checkProblem.setUsingCLICSValidator(getUseCLICSValidatorRadioButton().isSelected());
            checkProblem.setUsingCustomValidator(getUseCustomValidatorRadioButton().isSelected());
        }

        //initialize PC2 validator settings to defaults
        checkProblem.setValidatorCommandLine(getCustomValidatorCommandOptionsTextField().getText());
        checkProblem.setWhichPC2Validator(0);
        checkProblem.setIgnoreSpacesOnValidation(false);
        checkProblem.setValidatorProgramName(null);
        
        if (checkProblem.isUsingPC2Validator()) {

            // java -cp ..\..\lib\pc2.jar edu.csus.ecs.pc2.validator.Validator sumit.dat estdout.pc2 sumit.ans 212XRSAM.txt -pc2 1 false
            // "{:validator} {:infle} {:outfile} {:ansfile} {:resfile} ";

            checkProblem.setWhichPC2Validator(getPc2ValidatorOptionComboBox().getSelectedIndex());
            checkProblem.setIgnoreSpacesOnValidation(getPc2ValidatorIgnoreCaseCheckBox().isSelected());
            checkProblem.setValidatorCommandLine(DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND + " -pc2 " + checkProblem.getWhichPC2Validator() + " " + checkProblem.isIgnoreSpacesOnValidation());
            checkProblem.setValidatorProgramName(Problem.INTERNAL_VALIDATOR_NAME);
        }
        
        if (checkProblem.isUsingCLICSValidator()) {
            //update CLICS Default Validator settings from fields.
            checkProblem.setCLICSValidatorSettings(getCLICSValidatorSettingsFromFields());
        }
        
        if (checkProblem.isUsingCustomValidator()) {
            //update Custom validator settings from fields.
            checkProblem.setCustomValidatorSettings(getCustomValidatorSettingsFromFields());
        }

        checkProblem.setShowValidationToJudges(getShowValidatorToJudgesCheckBox().isSelected());

        checkProblem.setHideOutputWindow(!getDoShowOutputWindowCheckBox().isSelected());
        checkProblem.setShowCompareWindow(getShowCompareCheckBox().isSelected());

        // selecting a file is optional
        String newValidatorFileName = getCustomValidatorExecutableCommandTextField().getText();
        if (newValidatorFileName != null) {
            newValidatorFileName = newValidatorFileName.trim();
        } else {
            newValidatorFileName = "";
        }

        if (getUseCustomValidatorRadioButton().isSelected() && newValidatorFileName.length() > 0) {
       	
        	// external (custom) validator and they have specified a validator file name
            String existingValidatorFilename = newValidatorFileName;
            if (existingValidatorFilename.length() != getCustomValidatorExecutableCommandTextField().getToolTipText().length()) {
                existingValidatorFilename = getCustomValidatorExecutableCommandTextField().getToolTipText() + "";
            }

            if (isAdding) {
                SerializedFile serializedFile = new SerializedFile(existingValidatorFilename);

                if (serializedFile.getBuffer() == null) {
                    throw new InvalidFieldValue("Unable to read file '" + existingValidatorFilename + "'; choose validator file again (adding)");
                }

                checkProblem.setValidatorProgramName(serializedFile.getName());
                // for some reason on validator this is borked
                // newProblemDataFiles.setValidatorFile(freshenIfNeeded(serializedFile, existingValidatorFilename));
                newProblemDataFiles.setValidatorFile(serializedFile);

            } else {

                // existing validator loaded name.
                SerializedFile serializedFile = getController().getProblemDataFiles(problem).getValidatorFile();

                if (newValidatorFileName.equals(existingValidatorFilename)) {

                    // refresh/check validator file

                    if (serializedFile != null) {
                        serializedFile = freshenIfNeeded(serializedFile, existingValidatorFilename);
                        newProblemDataFiles.setValidatorFile(serializedFile);
                        checkProblem.setValidatorProgramName(serializedFile.getName());
                    } else {
                        newProblemDataFiles.setValidatorFile(null);
                        checkProblem.setValidatorProgramName(existingValidatorFilename);
                    }

                } else {

                    // different file name

                    serializedFile = new SerializedFile(newValidatorFileName);
                    checkFileFormat(serializedFile);
                    newProblemDataFiles.setValidatorFile(serializedFile);
                    checkProblem.setValidatorProgramName(serializedFile.getName());
                }
            }
        }

        checkProblem.setComputerJudged(computerJudgingRadioButton.isSelected());

        if (computerJudgingRadioButton.isSelected()) {
            checkProblem.setManualReview(manualReviewCheckBox.isSelected());
            if (manualReviewCheckBox.isSelected()) {
                checkProblem.setPrelimaryNotification(prelimaryNotificationCheckBox.isSelected());
            } else {
                checkProblem.setPrelimaryNotification(false);
            }
        } else {
            checkProblem.setManualReview(false);
            checkProblem.setPrelimaryNotification(false);
        }

        checkProblem.setExternalDataFileLocation(getMultipleDataSetPane().getLoadDirectory());

        if (debug22EditProblem) {
            Utilities.dump(newProblemDataFiles, "debug 22 before populateProblemTestSetFilenames");
        }

        if (dataFiles == null) {
            if (lastAnsFile != null) {
                newProblemDataFiles.setJudgesAnswerFile(lastAnsFile);
            }

            if (lastDataFile != null) {
                newProblemDataFiles.setJudgesDataFile(lastDataFile);
            }

            checkProblem.addTestCaseFilenames(getName(lastAnsFile), getName(lastDataFile));

        } else {
            populateProblemTestSetFilenames(checkProblem, dataFiles);
        }

        if (debug22EditProblem) {
            Utilities.dump(newProblemDataFiles, "debug 22 after populateProblemTestSetFilenames");
        }

        return checkProblem;

    }

    private String getName(SerializedFile serializedFile) {
        if (serializedFile != null) {
            return serializedFile.getName();
        }

        return null;
    }

    /**
     * Populate the test data set file lists in Problem.
     * 
     * @param inProblem
     * @param dataFiles
     */
    private void populateProblemTestSetFilenames(Problem inProblem, ProblemDataFiles dataFiles) {

        String[] dataList = getTestDataList(dataFiles);
        String[] answerList = getTestAnswerList(dataFiles);

        inProblem.removeAllTestCaseFilenames();
        if (dataList != null) {
            for (int i = 0; i < dataList.length; i++) {
                inProblem.addTestCaseFilenames(dataList[i], answerList[i]);
            }
        }
    }

    private String[] getTestAnswerList(ProblemDataFiles dataFiles) {

        ArrayList<String> list = new ArrayList<String>();

        SerializedFile[] filelist = dataFiles.getJudgesAnswerFiles();
        SerializedFile[] dataFileList = dataFiles.getJudgesDataFiles();

        for (SerializedFile serializedFile : filelist) {
            list.add(serializedFile.getName());
        }

        padListIfNeeded(list, filelist, dataFileList);

        return (String[]) list.toArray(new String[list.size()]);
    }

    private String[] getTestDataList(ProblemDataFiles dataFiles) {

        ArrayList<String> list = new ArrayList<String>();
        SerializedFile[] filelist = dataFiles.getJudgesAnswerFiles();
        SerializedFile[] dataFileList = dataFiles.getJudgesDataFiles();

        for (SerializedFile serializedFile : dataFileList) {
            list.add(serializedFile.getName());
        }

        padListIfNeeded(list, filelist, dataFileList);

        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * pad list with nulls if needed.
     * 
     * @param list
     * @param filelist
     * @param dataFileList
     */
    private void padListIfNeeded(ArrayList<String> stringList, SerializedFile[] filelist, SerializedFile[] dataFileList) {

        // find max of both lists
        int max = Math.max(filelist.length, dataFileList.length);

        for (int i = 0; i < max - stringList.size(); i++) {
            stringList.add("");
        }
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

        if (!validateProblemFields()) {
            // new problem is invalid, just return, message issued by validateProblemFields
            return;
        }

        Problem newProblem = null;

        try {
            ProblemDataFiles dataFiles = getProblemDataFilesFromFields();
            newProblem = getProblemFromFields(problem, dataFiles);
            if (dataFiles != null) {
                // ensure what we got from the fields is what is actually on disk
                // enableUpdateButton() would enable if the sha1 sums changed.
                boolean changed = false;
                SerializedFile[] judgesDataFiles = dataFiles.getJudgesDataFiles();
                if (judgesDataFiles != null) {
                    if (judgesDataFiles.length > 0) {
                        for (int i = 0; i < judgesDataFiles.length; i++) {
                            SerializedFile serializedFile = judgesDataFiles[i];
                            SerializedFile serializedFile2 = new SerializedFile(serializedFile.getAbsolutePath(), newProblem.isUsingExternalDataFiles());
                            if (!serializedFile.getSHA1sum().equals(serializedFile2.getSHA1sum())) {
                                // contents have changed on disk
                                judgesDataFiles[i] = serializedFile2;
                                changed = true;
                            }
                        }
                    } else {
                        // judgesDataFile.length is 0, but maybe the user has loaded something on the main page
                        if (problemRequiresDataCheckBox.isSelected()) {
                            // use it
                            SerializedFile serializedFile = new SerializedFile(inputDataFileLabel.getText(), newProblem.isUsingExternalDataFiles());
                            judgesDataFiles = new SerializedFile[1];
                            judgesDataFiles[0] = serializedFile;
                            changed = true;
                        }
                    }
                    if (changed) {
                        dataFiles.setJudgesDataFiles(judgesDataFiles);
                    }
                }
                SerializedFile[] judgesAnswerFiles = dataFiles.getJudgesAnswerFiles();
                if (judgesAnswerFiles != null) {
                    // compare each file
                    changed = false;
                    if (judgesAnswerFiles.length > 0) {
                        for (int i = 0; i < judgesAnswerFiles.length; i++) {
                            SerializedFile serializedFile = judgesAnswerFiles[i];
                            SerializedFile serializedFile2 = new SerializedFile(serializedFile.getAbsolutePath(), newProblem.isUsingExternalDataFiles());
                            if (!serializedFile.getSHA1sum().equals(serializedFile2.getSHA1sum())) {
                                // contents have changed on disk
                                judgesAnswerFiles[i] = serializedFile2;
                                changed = true;
                            }
                        }
                    } else {
                        // judgesAnswerFile.length is 0, but maybe the user has loaded something on the main page
                        if (judgesHaveAnswerFiles.isSelected()) {
                            // use it
                            SerializedFile serializedFile = new SerializedFile(answerFileNameLabel.getText(), newProblem.isUsingExternalDataFiles());
                            judgesAnswerFiles = new SerializedFile[1];
                            judgesAnswerFiles[0] = serializedFile;
                            changed = true;
                        }
                    }
                    if (changed) {
                        dataFiles.setJudgesAnswerFiles(judgesAnswerFiles);
                    }
                }
            }

        } catch (InvalidFieldValue e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            // showMessage(e.getMessage());
            return;
        }

        if (newProblem.getLetter() == null || newProblem.getLetter().length() == 0) {

            // Update/Add next letter to problem.
            int problemNumber = Utilities.getProblemNumber(getContest(), problem);
            String letter = Utilities.getProblemLetter(problemNumber);
            newProblem.setLetter(letter);
        }

        getController().updateProblem(newProblem, newProblemDataFiles);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    /**
     * Populate new data sets.
     * 
     * @param problem2
     * 
     * @param problem2
     * @return
     */
    protected ProblemDataFiles getProblemDataFilesFromFields() {

        /**
         * These are the judge data and ans from the first pane, they need to replace the first data set files.
         */

        newProblemDataFiles = multipleDataSetPane.getProblemDataFiles();

        if (debug22EditProblem) {
            Utilities.dump(newProblemDataFiles, "debug 22 in getProblemDataFilesFromFields");
        }

        return newProblemDataFiles;
    }

    /**
     * Validate that all problem fields are ok.
     * 
     * @return
     */
    private boolean validateProblemFields() {

        if (problemNameTextField.getText().trim().length() < 1) {
            showMessage("Enter a problem name (\"General\" tab)");
            return false;
        }

        if (getUsePC2ValidatorRadioButton().isSelected()) {
            if (pc2ValidatorOptionComboBox.getSelectedIndex() < 1) {
                showMessage("Select a Validator option");
                return false;
            }
        }
        
        if (getUseCustomValidatorRadioButton().isSelected()) {
            if (getCustomValidatorExecutableCommandTextField().getText() == null
                    || getCustomValidatorExecutableCommandTextField().getText().trim().length() == 0) {
                showMessage("Use Custom Validator selected; must specify Validator executable program (\"Validator\" tab)");
                return false;
            }
        }

        //validate the validator tolerance fields
        if (getFloatRelativeToleranceCheckBox().isSelected()) {
            String text = getFloatRelativeToleranceTextField().getText();
            try {
                Float.parseFloat(text);
            } catch (NumberFormatException | NullPointerException e) {
                showMessage("Float Relative Tolerance selected; must specify a valid tolerance (\"Validator\" tab)");
                return false;
            }
        }
        if (getFloatAbsoluteToleranceCheckBox().isSelected()) {
            String text = getFloatAbsoluteToleranceTextField().getText();
            try {
                Float.parseFloat(text);
            } catch (NumberFormatException | NullPointerException e) {
                showMessage("Float Absolute Tolerance selected; must specify a valid tolerance (\"Validator\" tab)");
                return false;
            }
        }


        if (getProblemRequiresDataCheckBox().isSelected()) {

            String fileName = inputDataFileLabel.getText();
            // this check is outside so we can provide a specific message
            if (fileName == null || fileName.trim().length() == 0) {
                showMessage("Problem Requires Input Data checked, must specify a file (\"General\" tab)");
                return false;
            }

            if (fileName.trim().length() != inputDataFileLabel.getToolTipText().length()) {
                fileName = inputDataFileLabel.getToolTipText() + "";
            }

            if (!checkFile(fileName)) {
                // note: if error, then checkFile will showMessage
                return false;
            }
        }

        if (getJudgesHaveAnswerFiles().isSelected()) {

            //note: the Judge's Answer File name is displayed in a JLabel (not a textfield)
            String answerFileName = answerFileNameLabel.getText();

            // this check is outside so we can provide a specific message
            if (answerFileName == null || answerFileName.trim().length() == 0) {
                showMessage("Problem Requires Judges' Answer File checked, select a file (\"General\" tab)");
                return false;
            }

            if (answerFileName.trim().length() != answerFileNameLabel.getToolTipText().length()) {
                answerFileName = answerFileNameLabel.getToolTipText() + "";
            }

            if (!checkFile(answerFileName)) {
                // note: if error, then checkFile will showMessage
                return false;
            }
        }

        if (getUseComputerJudgingRadioButton().isSelected()) {

            if (useNOValidatatorRadioButton.isSelected()) {
                showMessage("Computer Judging is selected (\"Judging Type\" tab), must select a validator");
                return false;
            }

        }

        return true;
    }

    /**
     * Checks to ensure the fileName exists, is a file, and is readable.
     * <P>
     * If error found will show Message to user.
     * 
     * @param fileName
     *            the file to check
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
            Object[] options = { "Ok", "Cancel", "Ignore" };
            int n = JOptionPane.showOptionDialog(null, fileName + " does not exist", "Message", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (n < 2) {
                return false;
            } // only Ignore will fall thru to true
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

            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Problem modified, save changes?", "Confirm Choice");

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

    /**
     * Set Problem and ProblemDataFiles to be edited.
     */
    public void setProblem(final Problem inProblem, final ProblemDataFiles problemDataFiles) {

        problem = inProblem;
        this.newProblemDataFiles = null;
        originalProblemDataFiles = problemDataFiles;

        if (debug22EditProblem) {
            fileNameOne = createProblemReport(inProblem, problemDataFiles, "stuf1");
            Utilities.dump(originalProblemDataFiles, "debug 22   ORIGINAL  setProblem");
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                // first clear the old ones
                getMultipleDataSetPane().clearDataFiles();
                // now set the new ones
                try {
                    // bug 1002: if we do not send the problem here, the default for the pane will be used
                    // for the data source.
                    getMultipleDataSetPane().setProblemDataFiles(inProblem, problemDataFiles);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                // this sets the tableModel files list, which is what the getProblemDataFiles uses
                getMultipleDataSetPane().populateUI();
                populateGUI(inProblem);
                // do not automatically set this to no update, the files may have changed on disk
                if (inProblem == null) {
                    // new problem
                    enableUpdateButtons(false);
                } else {
                    enableUpdateButton();
                }
                // populatingGUI = true;
                // setForm(inProblem, problemDataFiles);
                // getAddButton().setVisible(true);
                // getUpdateButton().setVisible(false);
                // enableUpdateButtons(true);
                //
                // enableValidatorComponents();
                // enableRequiresInputDataComponents(problemRequiresDataCheckBox.isSelected());
                // enableProvideAnswerFileComponents(judgesHaveAnswerFiles.isSelected());

                // populatingGUI = false;
            }
        });
    }

    public MultipleDataSetPane getMultipleDataSetPane() {
        if (multipleDataSetPane == null) {
            multipleDataSetPane = new MultipleDataSetPane();
            multipleDataSetPane.setContestAndController(getContest(), getController());
            multipleDataSetPane.setParentPane(this);
        }
        return multipleDataSetPane;

    }

    /**
     * Set/populate (or remove) General tab judge's files.
     * 
     * If no first test set then clears fields.
     * 
     */
    public void setJudgingTestSetOne(ProblemDataFiles datafiles) {

        /**
         * Were fields assigned values from dataFiles ?
         */
        boolean assignedValues = false;

        if (datafiles == null) {
            deleteAllDataSets();
        } else {
            SerializedFile[] answerFiles = datafiles.getJudgesAnswerFiles();

            if (answerFiles.length > 0) {

                // Replace data files on General tab
                judgesHaveAnswerFiles.setSelected(true);
                problemRequiresDataCheckBox.setSelected(true);

                SerializedFile[] files = datafiles.getJudgesDataFiles();
                if (files.length > 0) {
                    inputDataFileLabel.setText(files[0].getName());
                    inputDataFileLabel.setToolTipText(files[0].getAbsolutePath());
                }
                files = datafiles.getJudgesAnswerFiles();
                if (files.length > 0) {
                    answerFileNameLabel.setText(files[0].getName());
                    answerFileNameLabel.setToolTipText(files[0].getAbsolutePath());
                }

                assignedValues = true;
            }
        }

        if (!assignedValues) {

            // Replace data files on General tab
            judgesHaveAnswerFiles.setSelected(false);
            problemRequiresDataCheckBox.setSelected(false);

            inputDataFileLabel.setText("");
            inputDataFileLabel.setToolTipText("");
            answerFileNameLabel.setText("");
            answerFileNameLabel.setToolTipText("");

        }

        enableRequiresInputDataComponents(problemRequiresDataCheckBox.isSelected());
        enableProvideAnswerFileComponents(judgesHaveAnswerFiles.isSelected());

    }

    /**
     * Remove all data sets
     */
    private void deleteAllDataSets() {
        ProblemDataFiles dataFiles = getMultipleDataSetPane().getProblemDataFiles();
        if (dataFiles != null) {
            dataFiles.removeAll();
        }
        getMultipleDataSetPane().setProblemDataFiles(dataFiles);
    }

    /**
     * Set new Problem to be edited.
     * 
     * @param problem
     */
    public void setProblem(final Problem problem) {

        this.problem = problem;
        this.newProblemDataFiles = null;
        this.originalProblemDataFiles = null;

        if (debug22EditProblem) {
            fileNameOne = createProblemReport(problem, originalProblemDataFiles, "stuf1");
            System.out.println("Created problem report " + fileNameOne);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getMultipleDataSetPane().clearDataFiles();

                populateGUI(problem);
                // do not automatically set this to no update, the files may have changed on disk
                if (problem == null) {
                    // new problem
                    enableUpdateButtons(false);

                    getMultipleDataSetPane().getInputDataStoragePanel().setEnabled(true);
                    getMultipleDataSetPane().getRdbtnCopyDataFiles().setEnabled(true);
                    getMultipleDataSetPane().getRdBtnKeepDataFilesExternal().setEnabled(true);

                } else {
                    enableUpdateButton();
                }
            }
        });
    }

    private void populateGUI(Problem inProblem) {

        populatingGUI = true;

        if (debug22EditProblem) {
            Utilities.dump(originalProblemDataFiles, "debug 22   ORIGINAL  populateGUI A");
        }

        if (inProblem != null) {

            getAddButton().setVisible(false);
            getUpdateButton().setVisible(true);

            setForm(inProblem, originalProblemDataFiles);

            getCcsValidationEnabledCheckBox().setSelected(inProblem.isCcsMode());

            try {
                @SuppressWarnings("unused")
                Problem changedProblem = getProblemFromFields(inProblem, originalProblemDataFiles);
            } catch (InvalidFieldValue e) {
                logException("Problem with input Problem fields", e);
                e.printStackTrace(System.err);
            }

        } else {
            clearForm();
        }

        enableValidatorComponents();

        enableRequiresInputDataComponents(problemRequiresDataCheckBox.isSelected());

        enableProvideAnswerFileComponents(judgesHaveAnswerFiles.isSelected());

        if (debug22EditProblem) {
            Utilities.dump(originalProblemDataFiles, "debug 22 ORIGINAL  populateGUI B");
        }

        try {
            getMultipleDataSetPane().setProblemDataFiles(problem, originalProblemDataFiles);
        } catch (Exception e) {
            String message = "Error loading/editing problem data files: " + e.getMessage();
            showMessage(message + " check logs.");
            getLog().log(Log.WARNING, message, e);
            if (debug22EditProblem) {
                e.printStackTrace(); // debug 22
            }
        }

        // select the general tab
        getMainTabbedPane().setSelectedIndex(0);
        populatingGUI = false;

        if (debug22EditProblem) {
            Utilities.dump(originalProblemDataFiles, "debug 22   ORIGINAL  populateGUI Z");
        }

    }

    @SuppressWarnings("unused")
    private void dumpProblem(String filename, ProblemDataFiles pdf) {

        PrintWriter out = new PrintWriter(System.out, true);
        ProblemsReport report = new ProblemsReport();
        report.setContestAndController(getContest(), getController());
        report.writeProblemDataFiles(out, pdf);

        if (filename != null) {
            try {
                FileOutputStream stream = new FileOutputStream(filename, false);
                out = new PrintWriter(stream, true);
                out.println("Problem = " + problem);
                report.writeProblemDataFiles(out, pdf);
                out.close();
                out = null;
            } catch (FileNotFoundException e) {
                e.printStackTrace(System.err);
            }
        }
        System.out.flush();
        System.err.flush();
        System.err.println("Write to vi " + filename);

    }

    /**
     * Set Form data.
     * 
     * Populates the form, no error checking is performed.
     * 
     * @param inProblem
     * @param problemDataFiles
     */
    private void setForm(Problem inProblem, ProblemDataFiles problemDataFiles) {

        problem = inProblem;
        originalProblemDataFiles = problemDataFiles;

        problemNameTextField.setText(inProblem.getDisplayName());
        timeOutSecondTextField.setText(inProblem.getTimeOutInSeconds() + "");
        problemLetterTextField.setText(inProblem.getLetter());

        inputDataFileLabel.setText(inProblem.getDataFileName());
        answerFileNameLabel.setText(inProblem.getAnswerFileName());
        /**
         * Set tool tip with complete paths.
         */

        inputDataFileLabel.setToolTipText("");
        answerFileNameLabel.setToolTipText("");

        if (problemDataFiles != null) {
            SerializedFile[] files = problemDataFiles.getJudgesDataFiles();
            if (files.length > 0) {
                inputDataFileLabel.setToolTipText(files[0].getAbsolutePath());
            }
            files = problemDataFiles.getJudgesAnswerFiles();
            if (files.length > 0) {
                answerFileNameLabel.setToolTipText(files[0].getAbsolutePath());
            }
        }

        judgesHaveAnswerFiles.setSelected(inProblem.getAnswerFileName() != null);
        problemRequiresDataCheckBox.setSelected(inProblem.getDataFileName() != null);

        if (inProblem.isReadInputDataFromSTDIN()) {
            fileRadioButton.setSelected(false);
            stdinRadioButton.setSelected(true);
        } else {
            fileRadioButton.setSelected(true);
            stdinRadioButton.setSelected(false);
        }

        getPc2ValidatorOptionComboBox().setSelectedIndex(0);
        getPc2ValidatorIgnoreCaseCheckBox().setSelected(true);
        getCustomValidatorExecutableCommandTextField().setText("");
        getCustomValidatorExecutableCommandTextField().setToolTipText("");
        getPc2ValidatorIgnoreCaseCheckBox().setSelected(false);

        if (inProblem.isValidatedProblem()) {

            if (inProblem.isUsingPC2Validator()) {
            	
                getCustomValidatorCommandOptionsTextField().setText(DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND);
                getUsePC2ValidatorRadioButton().setSelected(true);
                getUseCLICSValidatorRadioButton().setSelected(false);
                getUseCustomValidatorRadioButton().setSelected(false);
                getPc2ValidatorOptionComboBox().setSelectedIndex(inProblem.getWhichPC2Validator());
                getPc2ValidatorIgnoreCaseCheckBox().setSelected(inProblem.isIgnoreSpacesOnValidation());
                
            } else if (inProblem.isUsingCLICSValidator()) {
            	
                getUsePC2ValidatorRadioButton().setSelected(false);
                getUseCLICSValidatorRadioButton().setSelected(true);
                getUseCustomValidatorRadioButton().setSelected(false);
                getCLICSValidatorCaseSensitiveCheckBox().setSelected(inProblem.getClicsValidatorSettings().isCaseSensitive());
                getCLICSSpaceSensitiveCheckBox().setSelected(inProblem.getClicsValidatorSettings().isSpaceSensitive());
                getFloatAbsoluteToleranceCheckBox().setSelected(inProblem.getClicsValidatorSettings().isFloatAbsoluteToleranceSpecified());
                if (getFloatAbsoluteToleranceCheckBox().isSelected()) {
                    getFloatAbsoluteToleranceTextField().setText(inProblem.getClicsValidatorSettings().getFloatAbsoluteTolerance()+"");
                } else {
                    getFloatAbsoluteToleranceTextField().setText("");   
                }
                getFloatRelativeToleranceCheckBox().setSelected(inProblem.getClicsValidatorSettings().isFloatRelativeToleranceSpecified());
                if (getFloatRelativeToleranceCheckBox().isSelected()) {
                    getFloatRelativeToleranceTextField().setText(inProblem.getClicsValidatorSettings().getFloatRelativeTolerance()+"");
                } else {
                    getFloatRelativeToleranceTextField().setText("");   
                }
                
            } else if (inProblem.isUsingCustomValidator()) {

                getUsePC2ValidatorRadioButton().setSelected(false);
                getUseCLICSValidatorRadioButton().setSelected(false);
                getUseCustomValidatorRadioButton().setSelected(true);
                
                CustomValidatorSettings settings = inProblem.getCustomValidatorSettings();
                getCustomValidatorExecutableCommandTextField().setText(settings.getCustomValidatorInvocationCommand());
                getCustomValidatorExecutableCommandTextField().setToolTipText(settings.getCustomValidatorInvocationCommand());
                getCustomValidatorCommandOptionsTextField().setText(settings.getCustomValidatorCommandOptions());
                
                SerializedFile sFile = problemDataFiles.getValidatorFile();
                if (sFile != null) {
                    if (sFile.getAbsolutePath() != null) {
                    	getCustomValidatorExecutableCommandTextField().setToolTipText(sFile.getAbsolutePath());
                    } else {
                    	getCustomValidatorExecutableCommandTextField().setToolTipText("");
                    }
                }
            }

        } else {
        	//the problem is not using a validator
            getCustomValidatorCommandOptionsTextField().setText(DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND); //why? (copied from old code)
            useNOValidatatorRadioButton.setSelected(true);
            getUsePC2ValidatorRadioButton().setSelected(false);
            getUseCLICSValidatorRadioButton().setSelected(false);
            getUseCustomValidatorRadioButton().setSelected(false);
        }

        getShowValidatorToJudgesCheckBox().setSelected(inProblem.isShowValidationToJudges());
        getDoShowOutputWindowCheckBox().setSelected(!inProblem.isHideOutputWindow());
        getShowCompareCheckBox().setSelected(inProblem.isShowCompareWindow());
        getShowCompareCheckBox().setEnabled(getDoShowOutputWindowCheckBox().isSelected());

        getDeleteProblemCheckBox().setSelected(!inProblem.isActive());

        populateJudging(inProblem);

        getMultipleDataSetPane().setLoadDirectory(inProblem.getExternalDataFileLocation());

        /**
         * Short problem name
         */

        shortNameTextfield.setText(inProblem.getShortName());

    }

    /*
     * Sets the Judging Type radio and checkboxes in a sane manner.
     */
    private void populateJudging(Problem inProblem) {
        if (inProblem != null && inProblem.isComputerJudged()) {
            computerJudgingRadioButton.setSelected(true);
            manualReviewCheckBox.setSelected(inProblem.isManualReview());
            manualReviewCheckBox.setEnabled(true);

            prelimaryNotificationCheckBox.setSelected(inProblem.isPrelimaryNotification());
            if (manualReviewCheckBox.isSelected()) {
                prelimaryNotificationCheckBox.setEnabled(true);
            } else {
                prelimaryNotificationCheckBox.setEnabled(false);
            }
        } else {
            computerJudgingRadioButton.setSelected(false);
            manualJudgingRadioButton.setSelected(true);
            if (inProblem == null) {
                manualReviewCheckBox.setSelected(false);
                prelimaryNotificationCheckBox.setSelected(false);
            } else {
                manualReviewCheckBox.setSelected(inProblem.isManualReview());
                prelimaryNotificationCheckBox.setSelected(inProblem.isPrelimaryNotification());
            }

            manualReviewCheckBox.setEnabled(false);
            prelimaryNotificationCheckBox.setEnabled(false);
        }
    }

    /**
     * update/enable Update button.
     * 
     * @param fieldsChanged
     *            if false assumes changest must be undone aka Canceled.
     */
    protected void enableUpdateButtons(boolean fieldsChanged) {
        if (fieldsChanged) {
            cancelButton.setText("Cancel");
        } else {
            cancelButton.setText("Close");
        }

        if (getUpdateButton().isVisible()) {
            getUpdateButton().setEnabled(fieldsChanged);
        } else {
            getAddButton().setEnabled(fieldsChanged);
        }
    }

    /**
     * This method initializes mainTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getMainTabbedPane() {
        if (mainTabbedPane == null) {
            mainTabbedPane = new JTabbedPane();
            mainTabbedPane.setPreferredSize(new Dimension(400, 500));
            mainTabbedPane.insertTab("Data Files", null, getMultipleDataSetPane(), null, 0);
            mainTabbedPane.insertTab("Validator", null, getValidatorPane(), null, 0);
            mainTabbedPane.insertTab("Judging Type", null, getJudgingTypePanel(), null, 0);
            mainTabbedPane.insertTab("General", null, getGeneralPane(), null, 0);
        }
        return mainTabbedPane;
    }

    /**
     * This method initializes generalPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJudgingTypePanel() {
        if (judgingTypePane == null) {
            judgingTypePane = new JPanel();
            judgingTypePane.setLayout(new BorderLayout());
            judgingTypePane.add(getJudgeTypeInnerPane(), BorderLayout.NORTH);
            judgingTypePane.add(getCcsSettingsPane(), BorderLayout.CENTER);
            getJudgingTypeGroup().setSelected(getManualJudgingRadioButton().getModel(), true);
        }
        return judgingTypePane;
    }

    /**
     * This method initializes generalPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGeneralPane() {
        if (generalPane == null) {
            timeoutLabel = new JLabel();
            timeoutLabel.setBounds(new Rectangle(23, 46, 150, 16));
            timeoutLabel.setText("Run Timeout Limit (Secs)");
            problemNameLabel = new JLabel();
            problemNameLabel.setBounds(new Rectangle(23, 14, 150, 16));
            problemNameLabel.setText("Problem name");
            generalPane = new JPanel();
            generalPane.setLayout(null);
            generalPane.add(getProblemNameTextField(), null);
            generalPane.add(getTimeOutTextField(), null);
            generalPane.add(getProblemRequiresDataCheckBox(), null);
            generalPane.add(getDataProblemPane(), null);
            generalPane.add(getJudgesHaveAnswerFiles(), null);
            generalPane.add(getAnswerFilePane(), null);
            generalPane.add(problemNameLabel, null);
            generalPane.add(timeoutLabel, null);
            generalPane.add(getShowCompareCheckBox(), null);
            generalPane.add(getDoShowOutputWindowCheckBox(), null);
            generalPane.add(getDeleteProblemCheckBox(), null);

            generalPane.add(getProblemLetterTextField(), null);

            JLabel lblShortName = new JLabel();
            lblShortName.setText("Short Name");
            lblShortName.setBounds(new Rectangle(23, 14, 179, 16));
            lblShortName.setBounds(285, 46, 84, 16);
            generalPane.add(lblShortName);

            shortNameTextfield = new JTextField();
            shortNameTextfield.setPreferredSize(new Dimension(120, 20));
            shortNameTextfield.setBounds(new Rectangle(220, 44, 120, 20));
            shortNameTextfield.setBounds(379, 44, 97, 20);
            shortNameTextfield.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
            generalPane.add(shortNameTextfield);
        }
        return generalPane;
    }

    /**
     * This method initializes problemNameTextField
     * 
     * @return javax.swing.JTextField
     */
    protected JTextField getProblemNameTextField() {
        if (problemNameTextField == null) {
            problemNameTextField = new JTextField();
            problemNameTextField.setPreferredSize(new java.awt.Dimension(120, 20));
            problemNameTextField.setSize(new Dimension(293, 20));
            problemNameTextField.setLocation(new Point(183, 12));
            problemNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return problemNameTextField;
    }

    /**
     * This method initializes the timeOut textfield
     * 
     * @return javax.swing.JTextField holding the timeOut
     */
    private JTextField getTimeOutTextField() {
        if (timeOutSecondTextField == null) {
            timeOutSecondTextField = new JTextField();
            timeOutSecondTextField.setBounds(new Rectangle(183, 44, 74, 20));
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

    public JTextField getProblemLetterTextField() {
        // SOMEDAY - add field to form, define and make visible.
        if (problemLetterTextField == null) {
            problemLetterTextField = new JTextField();
        }
        return problemLetterTextField;
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
                    enableRequiresInputDataComponents(problemRequiresDataCheckBox.isSelected());
                    enableUpdateButton();
                }
            });
        }
        return problemRequiresDataCheckBox;
    }

    protected void enableRequiresInputDataComponents(boolean enableButtons) {
        getInputDataFilePane().setEnabled(enableButtons);
        getFileRadioButton().setEnabled(enableButtons);
        getStdinRadioButton().setEnabled(enableButtons);
        getReadsFromPane().setEnabled(enableButtons);
        getSelectFileButton().setEnabled(enableButtons);
        getInputDataFilePane().setEnabled(enableButtons);
        getFileNamePane().setEnabled(enableButtons);
        getInputDataFilePane().setEnabled(enableButtons);
        getFileNamePane().setEnabled(enableButtons);
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
            getValidatorChoiceButtonGroup().setSelected(getUseNOValidatatorRadioButton().getModel(), true);
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
                    if (selectFile(inputDataFileLabel, "Open Input Data File")) {
                        inputDataFileLabel.setToolTipText(inputDataFileLabel.getText());
                        ProblemDataFiles datafiles = multipleDataSetPane.getProblemDataFiles();
                        if (datafiles != null) {
                            SerializedFile[] sFiles = datafiles.getJudgesDataFiles();
                            if (sFiles != null && sFiles.length > 0) {
                                sFiles[0] = new SerializedFile(inputDataFileLabel.getText());
                            } else {
                                sFiles = new SerializedFile[1];
                                sFiles[0] = new SerializedFile(inputDataFileLabel.getText());
                            }
                            datafiles.setJudgesDataFiles(sFiles);
                            multipleDataSetPane.setProblemDataFiles(datafiles);
                        } // else previous handling will take care of it
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
                    enableProvideAnswerFileComponents(judgesHaveAnswerFiles.isSelected());
                    enableUpdateButton();
                }
            });
        }
        return judgesHaveAnswerFiles;
    }

    protected void enableProvideAnswerFileComponents(boolean enableComponents) {
        getAnswerFilenamePane().setEnabled(enableComponents);
        getAnswerBrowseButton().setEnabled(enableComponents);
        answerFileNameLabel.setEnabled(enableComponents);
        answerFilePane.setEnabled(enableComponents);
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
                    if (selectFile(answerFileNameLabel, "Open Judges Answer File")) {
                        answerFileNameLabel.setToolTipText(answerFileNameLabel.getText());
                        ProblemDataFiles datafiles = multipleDataSetPane.getProblemDataFiles();
                        if (datafiles != null) {
                            SerializedFile[] sFiles = datafiles.getJudgesAnswerFiles();
                            if (sFiles.length > 0) {
                                sFiles[0] = new SerializedFile(answerFileNameLabel.getText());
                            } else {
                                sFiles = new SerializedFile[1];
                                sFiles[0] = new SerializedFile(answerFileNameLabel.getText());
                            }
                            datafiles.setJudgesAnswerFiles(sFiles);
                            multipleDataSetPane.setProblemDataFiles(datafiles);
                        } // else previous handling will take care of it
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
                JOptionPane.showMessageDialog(null, message);
            }
        });
    }

    /**
     * select file, if file picked updates label.
     * 
     * @param label
     * @param dialogTitle
     *            title for file chooser
     * @return True if a file was select and label updated
     * @throws Exception
     */
    private boolean selectFile(JLabel label, String dialogTitle) {
        boolean result = false;
        // toolTip should always have the full path
        String oldFile = label.getToolTipText();
        String startDir;
        if (oldFile.equalsIgnoreCase("")) {
            startDir = lastDirectory;
        } else {
            startDir = oldFile;
        }
        JFileChooser chooser = new JFileChooser(startDir);
        if (dialogTitle != null) {
            chooser.setDialogTitle(dialogTitle);
        }
        try {
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                lastDirectory = chooser.getCurrentDirectory().toString();
                label.setText(chooser.getSelectedFile().getCanonicalFile().toString());
                result = true;
            }
        } catch (Exception e) {
            log.log(Log.INFO, "Error getting selected file, try again.", e);
            result = false;
        }
        chooser = null;
        return result;
    }

    /**
     * select file, if file picked updates specified JTextField.
     * 
     * @param textField -- a JTextField whose value will be updated if a file is chosen
     * @param dialogTitle
     *            title for file chooser
     * @return True if a file was select and the JTextField updated
     * @throws Exception
     */
    private boolean selectFile(JTextField textField, String dialogTitle) {
        boolean result = false;
        // toolTip should always have the full path
        String oldFile = textField.getToolTipText();
        String startDir;
        if (oldFile.equalsIgnoreCase("")) {
            startDir = lastDirectory;
        } else {
            startDir = oldFile;
        }
        JFileChooser chooser = new JFileChooser(startDir);
        if (dialogTitle != null) {
            chooser.setDialogTitle(dialogTitle);
        }
        try {
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                lastDirectory = chooser.getCurrentDirectory().toString();
                textField.setText(chooser.getSelectedFile().getCanonicalFile().toString());
                result = true;
            }
        } catch (Exception e) {
            showMessage("Error getting selected file, try again: \n" + e.getMessage());
            log.log(Log.INFO, "Error getting selected file: ", e);
            result = false;
        }
        chooser = null;
        return result;
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

    private ButtonGroup getJudgingTypeGroup() {
        if (judgingTypeGroup == null) {
            judgingTypeGroup = new ButtonGroup();
            judgingTypeGroup.add(getUseComputerJudgingRadioButton());
            judgingTypeGroup.add(getManualJudgingRadioButton());
        }
        return judgingTypeGroup;
    }

    /**
     * This method initializes validatorPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getValidatorPane() {
        if (validatorPane == null) {
            validatorPane = new JPanel();
            validatorPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            validatorPane.setAlignmentY(Component.TOP_ALIGNMENT);
            validatorPane.setMaximumSize(new Dimension(500, 300));
            validatorPane.setLayout(new BoxLayout(validatorPane, BoxLayout.Y_AXIS));
            validatorPane.add(getVerticalStrut_4());
            validatorPane.add(getNoValidatorPanel());
            validatorPane.add(getVerticalStrut_1());
            validatorPane.add(getPc2ValidatorPanel());
            validatorPane.add(getVerticalStrut_5());
            validatorPane.add(getClicsValidatorPanel());
            validatorPane.add(getVerticalStrut());
            validatorPane.add(getCustomValidatorPanel());
            validatorPane.add(getVerticalStrut_2());
            validatorPane.add(getShowValidatorToJudgesCheckBox());
            validatorPane.add(getVerticalStrut_3());
        }
        return validatorPane;
    }

    /**
     * This method initializes useValidatorRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getUseNOValidatatorRadioButton() {
        if (useNOValidatatorRadioButton == null) {
            useNOValidatatorRadioButton = new JRadioButton();
            useNOValidatatorRadioButton.setText("Do not use Validator");
            useNOValidatatorRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableValidatorComponents();
                    enableUpdateButton();
                }
            });
        }
        return useNOValidatatorRadioButton;
    }

    protected void enableValidatorComponents() {
        if (getUsePC2ValidatorRadioButton().isSelected()) {
            enablePC2ValidatorComponents(true);
            enableClicsValidatorComponents(false);
            enableCustomValidatorComponents(false);
            getShowValidatorToJudgesCheckBox().setEnabled(true);
        } else if (getUseCLICSValidatorRadioButton().isSelected()) {
            enablePC2ValidatorComponents(false);
            enableClicsValidatorComponents(true);
            enableCustomValidatorComponents(false);
            getShowValidatorToJudgesCheckBox().setEnabled(true);
        } else if (getUseCustomValidatorRadioButton().isSelected()) {
            enablePC2ValidatorComponents(false);
            enableClicsValidatorComponents(false);
            enableCustomValidatorComponents(true);
            getShowValidatorToJudgesCheckBox().setEnabled(true);
        } else {
            // None used
            enablePC2ValidatorComponents(false);
            enableClicsValidatorComponents(false);
            enableCustomValidatorComponents(false);
            getShowValidatorToJudgesCheckBox().setEnabled(false);
        }
    }

    private void enablePC2ValidatorComponents(boolean enableComponents) {
        getPc2ValidatorOptionsSubPanel().setEnabled(enableComponents);
        getPc2ValidatorOptionComboBoxLabel().setEnabled(enableComponents);
        getPc2ValidatorOptionComboBox().setEnabled(enableComponents);
        getPc2ValidatorIgnoreCaseCheckBox().setEnabled(enableComponents);
    }

    /**
     * This method initializes the CLICS Validator jRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getUseCLICSValidatorRadioButton() {
        if (useCLICSValidatorRadioButton == null) {
            useCLICSValidatorRadioButton = new JRadioButton();
            useCLICSValidatorRadioButton.setMargin(new Insets(2, 12, 2, 2));
            useCLICSValidatorRadioButton.setText("Use CLICS Validator");
            useCLICSValidatorRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableValidatorComponents();
                    enableUpdateButton();
                }
            });
        }
        return useCLICSValidatorRadioButton;
    }

    private JLabel getLblWhatsThis() {
        if (lblWhatsThis == null) {
//            lblWhatsThis = new JLabel("<What's This?>");
//            lblWhatsThis.setForeground(Color.blue);

            ImageIcon iconImage = (ImageIcon) UIManager.getIcon("OptionPane.informationIcon");
            Image image = iconImage.getImage();
            lblWhatsThis = new JLabel(new ImageIcon(getScaledImage(image, 20, 20)));
            lblWhatsThis.setToolTipText("Click for additional information");
            lblWhatsThis.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JOptionPane.showMessageDialog(null, whatsThisMessage, "CLICS Validator", JOptionPane.INFORMATION_MESSAGE, null);
                }
            });
            lblWhatsThis.setBorder(new EmptyBorder(0, 15, 0, 0));
        }
        return lblWhatsThis;
    }
    
    private String whatsThisMessage = "Selecting this option allows you to use the \"CLICS Validator\"."
            
            + "\n\nCLICS is the Competitive Learning Initiative Contest System specification, used among other things to define "
            + "\nrequirements for Contest Control Systems used at the ICPC World Finals. The CLICS specification includes a"
            + "\ndefinition for a \"standard validator\" used as the default when no other validator is selected."
            
            + "\n\nThe CLICS Validator \"tokenizes\" the Judge's Answer file and the Team Output file, ignoring case and whitespace"
            + "\nby default, and determines \"equivalence\" by comparing the corresponding tokens."
            
            + "\n\nOptions allow the user to require case-sensitivity and/or \"space-sensitivity\" (i.e., an exact match in whitespace),"
            + "\nand to specify tolerance requirements which floating-point tokens must meet to be considered equal."
            
            + "\n\nIf both absolute and relative tolerance values are specified, floating-point tokens are considered equivalent if"
            + "\nif they match within EITHER of the specified tolerances.  If neither absolute nor relative tolerance is specified,"
            + "\nfloating-point tokens must match character-for-character to be considered equivalent."
            
            + "\n\nFor more information, see the CLICS specification at https://clics.ecs.baylor.edu/index.php/Problem_format#Validators.  ";

    private JPanel clicsOptionButtonPanel;
    
    protected void enableCustomValidatorComponents(boolean enableComponents) {
        getCustomValidatorOptionsSubPanel().setEnabled(enableComponents);
        getChooseValidatorProgramButton().setEnabled(enableComponents);
        getCustomValidatorExecutableCommandLabel().setEnabled(enableComponents);
        getCustomValidatorExecutableCommandTextField().setEnabled(enableComponents);
        getCustomValidatorCommandOptionsLabel().setEnabled(enableComponents);
        getCustomValidatorCommandOptionsTextField().setEnabled(enableComponents);
    }

    protected void enableClicsValidatorComponents(boolean enableComponents) {
        getClicsValidatorOptionsSubPanel().setEnabled(enableComponents);
        getCLICSValidatorCaseSensitiveCheckBox().setEnabled(enableComponents);
        getCLICSSpaceSensitiveCheckBox().setEnabled(enableComponents);
        getFloatAbsoluteToleranceCheckBox().setEnabled(enableComponents);
        getFloatRelativeToleranceCheckBox().setEnabled(enableComponents);
        getFloatAbsoluteToleranceTextField().setEnabled(enableComponents);
        getFloatRelativeToleranceTextField().setEnabled(enableComponents);
    }

    /**
     * This method initializes the Custom Validator jRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getUseCustomValidatorRadioButton() {
        if (useCustomValidatorRadioButton == null) {
            useCustomValidatorRadioButton = new JRadioButton();
            useCustomValidatorRadioButton.setMargin(new Insets(2, 12, 2, 2));
            useCustomValidatorRadioButton.setText("Use Custom (User-supplied) Validator");
            useCustomValidatorRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableValidatorComponents();
                    enableUpdateButton();
                }
            });
        }
        return useCustomValidatorRadioButton;
    }

    /**
     * This method initializes showValidatorToJudgesCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getShowValidatorToJudgesCheckBox() {
        if (showValidatorToJudgesCheckBox == null) {
            showValidatorToJudgesCheckBox = new JCheckBox();
            showValidatorToJudgesCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
            showValidatorToJudgesCheckBox.setMargin(new Insets(2, 12, 2, 2));
            showValidatorToJudgesCheckBox.setText("Show Validator To Judges (SVTJ)");
            showValidatorToJudgesCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return showValidatorToJudgesCheckBox;
    }

    /**
     * This method initializes the sub-panel containing the Default Validator options
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getClicsValidatorOptionsSubPanel() {
        if (clicsValidatorOptionsSubPanel == null) {
            
            clicsValidatorOptionsSubPanel = new JPanel();
            clicsValidatorOptionsSubPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "CLICS Validator options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
            
            GridBagLayout gbl_clicsValidatorOptionsSubPanel = new GridBagLayout();
            gbl_clicsValidatorOptionsSubPanel.columnWidths = new int[] {30, 100, 150};
            gbl_clicsValidatorOptionsSubPanel.rowHeights = new int[] {25, 25};
            gbl_clicsValidatorOptionsSubPanel.columnWeights = new double[]{0.0, 0.0, 0.0};
            gbl_clicsValidatorOptionsSubPanel.rowWeights = new double[]{0.0, 0.0};
            clicsValidatorOptionsSubPanel.setLayout(gbl_clicsValidatorOptionsSubPanel);
            
            GridBagConstraints gbc_CaseSensitiveCheckBox = new GridBagConstraints();
            gbc_CaseSensitiveCheckBox.fill = GridBagConstraints.BOTH;
            gbc_CaseSensitiveCheckBox.insets = new Insets(0, 0, 5, 5);
            gbc_CaseSensitiveCheckBox.gridx = 0;
            gbc_CaseSensitiveCheckBox.gridy = 0;
            clicsValidatorOptionsSubPanel.add(getCLICSValidatorCaseSensitiveCheckBox(), gbc_CaseSensitiveCheckBox);
            
            GridBagConstraints gbc_FloatRelativeToleranceCheckBox = new GridBagConstraints();
            gbc_FloatRelativeToleranceCheckBox.fill = GridBagConstraints.BOTH;
            gbc_FloatRelativeToleranceCheckBox.insets = new Insets(0, 0, 5, 5);
            gbc_FloatRelativeToleranceCheckBox.gridx = 1;
            gbc_FloatRelativeToleranceCheckBox.gridy = 0;
            clicsValidatorOptionsSubPanel.add(getFloatRelativeToleranceCheckBox(), gbc_FloatRelativeToleranceCheckBox);
            
            GridBagConstraints gbc_FloatRelativeToleranceTextField = new GridBagConstraints();
            gbc_FloatRelativeToleranceTextField.insets = new Insets(0, 0, 5, 5);
            gbc_FloatRelativeToleranceTextField.fill = GridBagConstraints.BOTH;
            gbc_FloatRelativeToleranceTextField.gridx = 2;
            gbc_FloatRelativeToleranceTextField.gridy = 0;
            clicsValidatorOptionsSubPanel.add(getFloatRelativeToleranceTextField(), gbc_FloatRelativeToleranceTextField);
            
            GridBagConstraints gbc_SpaceSensitiveCheckBox = new GridBagConstraints();
            gbc_SpaceSensitiveCheckBox.fill = GridBagConstraints.BOTH;
            gbc_SpaceSensitiveCheckBox.insets = new Insets(0, 0, 5, 5);
            gbc_SpaceSensitiveCheckBox.gridx = 0;
            gbc_SpaceSensitiveCheckBox.gridy = 1;
            clicsValidatorOptionsSubPanel.add(getCLICSSpaceSensitiveCheckBox(), gbc_SpaceSensitiveCheckBox);
            
            GridBagConstraints gbc_FloatAbsoluteToleranceCheckBox = new GridBagConstraints();
            gbc_FloatAbsoluteToleranceCheckBox.anchor = GridBagConstraints.WEST;
            gbc_FloatAbsoluteToleranceCheckBox.insets = new Insets(0, 0, 5, 5);
            gbc_FloatAbsoluteToleranceCheckBox.gridx = 1;
            gbc_FloatAbsoluteToleranceCheckBox.gridy = 1;
            clicsValidatorOptionsSubPanel.add(getFloatAbsoluteToleranceCheckBox(), gbc_FloatAbsoluteToleranceCheckBox);
            
            GridBagConstraints gbc_FloatAbsoluteToleranceTextField = new GridBagConstraints();
            gbc_FloatAbsoluteToleranceTextField.insets = new Insets(0, 0, 5, 5);
            gbc_FloatAbsoluteToleranceTextField.fill = GridBagConstraints.BOTH;
            gbc_FloatAbsoluteToleranceTextField.gridx = 2;
            gbc_FloatAbsoluteToleranceTextField.gridy = 1;
            clicsValidatorOptionsSubPanel.add(getFloatAbsoluteToleranceTextField(), gbc_FloatAbsoluteToleranceTextField);
        }
        return clicsValidatorOptionsSubPanel;
    }

    /**
     * This method initializes the Custom Validator panel (and its Options sub-panel)
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCustomValidatorPanel() {
        if (customValidatorPanel == null) {
            customValidatorPanel = new JPanel();
            customValidatorPanel.setMaximumSize(new Dimension(700, 200));
            customValidatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            customValidatorPanel.setLayout(new BorderLayout(0, 0));
            customValidatorPanel.add(getUseCustomValidatorRadioButton(), BorderLayout.NORTH);
            customValidatorPanel.add(getHorizontalStrut_1(), BorderLayout.WEST);
            customValidatorPanel.add(getCustomValidatorOptionsSubPanel());
        }
        return customValidatorPanel;
    }

    /**
     * This method initializes the isCaseSensitive checkbox for the old (deprecated) PC2 Validator.
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getCLICSValidatorCaseSensitiveCheckBox() {
        if (isCLICSCaseSensitiveCheckBox == null) {
            isCLICSCaseSensitiveCheckBox = new JCheckBox();
            isCLICSCaseSensitiveCheckBox.setText("Case-sensitive");
            isCLICSCaseSensitiveCheckBox.setSelected(false);
            isCLICSCaseSensitiveCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return isCLICSCaseSensitiveCheckBox;
    }

    /**
     * This method initializes the isSpaceSensitive checkbox.
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getCLICSSpaceSensitiveCheckBox() {
        if (isCLICSSpaceSensitiveCheckBox == null) {
            isCLICSSpaceSensitiveCheckBox = new JCheckBox();
            isCLICSSpaceSensitiveCheckBox.setText("Space-sensitive");
            isCLICSSpaceSensitiveCheckBox.setSelected(false);
            isCLICSSpaceSensitiveCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return isCLICSSpaceSensitiveCheckBox;
    }

    /**
     * This method initializes the FloatRelativeTolerance checkbox.
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getFloatRelativeToleranceCheckBox() {
        if (floatRelativeToleranceCheckBox == null) {
            floatRelativeToleranceCheckBox = new JCheckBox();
            floatRelativeToleranceCheckBox.setText("Float relative tolerance:");
            floatRelativeToleranceCheckBox.setSelected(false);
            floatRelativeToleranceCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return floatRelativeToleranceCheckBox;
    }

    /**
     * This method initializes the FloatRelativeTolerance Text Field.
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getFloatRelativeToleranceTextField() {
        if (floatRelativeToleranceTextField == null) {
            floatRelativeToleranceTextField = new JTextField();
            floatRelativeToleranceTextField.setToolTipText("Enter the relative tolerance for floating point numbers");
            floatRelativeToleranceTextField.setMaximumSize(new Dimension(100, 20));
            floatRelativeToleranceTextField.setColumns(20);
            floatRelativeToleranceTextField.setEnabled(false);
        }
        return floatRelativeToleranceTextField;
    }

    /**
     * This method initializes the FloatAbsoluteTolerance checkbox.
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getFloatAbsoluteToleranceCheckBox() {
        if (floatAbsoluteToleranceCheckBox == null) {
            floatAbsoluteToleranceCheckBox = new JCheckBox();
            floatAbsoluteToleranceCheckBox.setText("Float absolute tolerance:");
            floatAbsoluteToleranceCheckBox.setSelected(false);
            floatAbsoluteToleranceCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return floatAbsoluteToleranceCheckBox;
    }

    /**
     * This method initializes the floatAbsoluteTolerance Text Field.
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getFloatAbsoluteToleranceTextField() {
        if (floatAbsoluteToleranceTextField == null) {
            floatAbsoluteToleranceTextField = new JTextField();
            floatAbsoluteToleranceTextField.setToolTipText("Enter the absolute tolerance for floating point numbers");
            floatAbsoluteToleranceTextField.setMaximumSize(new Dimension(100, 20));
            floatAbsoluteToleranceTextField.setColumns(10);
            floatAbsoluteToleranceTextField.setEnabled(false);
        }
        return floatAbsoluteToleranceTextField;
    }



    /**
     * Show diff between files using gvim.exe.
     * 
     * @param fileOne
     * @param fileTwo
     */
    protected void showFilesDiff(String fileOne, String fileTwo) {

        String command = "gvim.exe -d " + fileOne + " " + fileTwo;
        System.out.println("cmd = " + command);
        try {
            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            showMessage("Unable to diff " + e.getMessage());
            System.out.println("debug diff cmd: " + command);
            e.printStackTrace();
        }
    }

    /**
     * This method initializes validatorProgramJButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getChooseValidatorProgramButton() {
        if (chooseValidatorProgramButton == null) {
            chooseValidatorProgramButton = new JButton();
            chooseValidatorProgramButton.setText("Choose...");
            chooseValidatorProgramButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
//                    showMessage ("Not implemented yet...");
                    if (selectFile(getCustomValidatorExecutableCommandTextField(), "Select Validator Program")) {
                        getCustomValidatorExecutableCommandTextField().setToolTipText((getCustomValidatorExecutableCommandTextField().getText()));
                        enableUpdateButton();
                    }
                }
            });
        }
        return chooseValidatorProgramButton;
    }
    
    

    /**
     * This method initializes validatorCommandLineTextBox
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getCustomValidatorCommandOptionsTextField() {
        if (customValidatorCommandOptionsTextField == null) {
            customValidatorCommandOptionsTextField = new JTextField();
            customValidatorCommandOptionsTextField.setEnabled(false);
            customValidatorCommandOptionsTextField.setMaximumSize(new Dimension(100, 20));
            customValidatorCommandOptionsTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return customValidatorCommandOptionsTextField;
    }

    /**
     * This method initializes showCompareCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getShowCompareCheckBox() {
        if (showCompareCheckBox == null) {
            showCompareCheckBox = new JCheckBox();
            showCompareCheckBox.setBounds(new Rectangle(23, 374, 207, 21));
            showCompareCheckBox.setText("Show Compare");
            showCompareCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return showCompareCheckBox;
    }

    /**
     * This method initializes jCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getDoShowOutputWindowCheckBox() {
        if (doShowOutputWindowCheckBox == null) {
            doShowOutputWindowCheckBox = new JCheckBox();
            doShowOutputWindowCheckBox.setBounds(new Rectangle(23, 338, 225, 24));
            doShowOutputWindowCheckBox.setSelected(true);
            doShowOutputWindowCheckBox.setText("Show the output window");
            doShowOutputWindowCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                    getShowCompareCheckBox().setEnabled(getDoShowOutputWindowCheckBox().isSelected());
                }
            });
        }
        return doShowOutputWindowCheckBox;
    }

    /**
     * This method initializes validatorChoiceButtonGroup
     * 
     * @return javax.swing.ButtonGroup
     */
    private ButtonGroup getValidatorChoiceButtonGroup() {
        if (validatorChoiceButtonGroup == null) {
            validatorChoiceButtonGroup = new ButtonGroup();
            validatorChoiceButtonGroup.add(getUseNOValidatatorRadioButton());
            validatorChoiceButtonGroup.add(getUsePC2ValidatorRadioButton());
            validatorChoiceButtonGroup.add(getUseCLICSValidatorRadioButton());
            validatorChoiceButtonGroup.add(getUseCustomValidatorRadioButton());
        }
        return validatorChoiceButtonGroup;
    }

    /**
     * Checks whether needs to freshen, prompt user before freshening.
     * 
     * @param serializedFile
     * @param fileName
     * @return
     * @throws InvalidFieldValue
     */
    private SerializedFile freshenIfNeeded(SerializedFile serializedFile, String fileName) {

        if (serializedFile == null) {
            return null;

        }
        if (serializedFile.getBuffer() == null) {
            throw new InvalidFieldValue("Unable to read file " + fileName + " choose file again (updating)");
        }

        // only check freshening if it is still the same fileName
        if (fileName != null && fileName.equals(serializedFile.getAbsolutePath())) {
            // only do this if we are not populating the gui
            if (!populatingGUI && needsFreshening(serializedFile, fileName)) {

                int result = JOptionPane.showConfirmDialog(this, "File (" + fileName + ") has changed; reload from disk?", "Freshen file " + serializedFile.getAbsolutePath() + "?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (result == JOptionPane.YES_OPTION) {
                    serializedFile = new SerializedFile(serializedFile.getAbsolutePath());
                    checkFileFormat(serializedFile);
                    return serializedFile;
                } else if (result == JOptionPane.CANCEL_OPTION) {
                    throw new InvalidFieldValue("Update cancelled");
                }
            } // else nothing to update
        } else {
            if (fileName != null) {
                serializedFile = new SerializedFile(fileName);
            }
        }

        checkFileFormat(serializedFile);
        return serializedFile;
    }

    /**
     * Has this file been updated on disk ?
     * 
     * @param serializedFile
     *            existing saved file
     * @param fileName
     *            name for file that might need freshening.
     * @return true if file on disk different than saved file.
     */
    public boolean needsFreshening(SerializedFile serializedFile, String fileName) {

        if (serializedFile == null) {
            return false;
        }

        try {
            File f = new File(serializedFile.getAbsolutePath());

            if (f.exists()) {
                // Only can check whether to update if file is on disk

                // Now compare them
                // Can't use SerializeFile.getFile() because it may return null... sigh.

                SerializedFile newSerializedFile = new SerializedFile(f.getAbsolutePath());

                return !serializedFile.getSHA1sum().equals(newSerializedFile.getSHA1sum());
            } // else no need to refresh, no file found.

        } catch (Exception ex99) {
            logDebugException("Exception ", ex99);
        }

        return false;
    }

    /**
     * @param newFile
     * @return true if the file was converted
     */
    public boolean checkFileFormat(SerializedFile newFile) {

        if (newFile == null) {
            showMessage("Warning new file is null");
            return false;
        }

        /*
         * DOS FILE 0x0D 0x0A UNIX FILE 0xA MAC FILE 0xD
         */
        int currentOS = 0;
        // compare OS Versions.

        if (NL.length() == 2) {
            currentOS = Constants.FILETYPE_DOS;
        } else if (NL.charAt(0) == 0x0A) {
            currentOS = Constants.FILETYPE_UNIX;
        } else if (NL.charAt(0) == 0x0D) {
            currentOS = Constants.FILETYPE_MAC;
        }

        if ((currentOS != newFile.getFileType()) && (newFile.getFileType() != Constants.FILETYPE_BINARY) && (newFile.getFileType() != Constants.FILETYPE_ASCII_GENERIC)
                && (newFile.getFileType() != Constants.FILETYPE_ASCII_OTHER)) {

            String fileName = newFile.getName();
            String question = "The file (" + fileName + ") you are loading appears to be of type '";

            if (newFile.getFileType() == Constants.FILETYPE_BINARY) {
                question = question + Constants.FILETYPE_BINARY_TEXT;
            } else if (newFile.getFileType() == Constants.FILETYPE_DOS) {
                question = question + Constants.FILETYPE_DOS_TEXT;
            } else if (newFile.getFileType() == Constants.FILETYPE_MAC) {
                question = question + Constants.FILETYPE_MAC_TEXT;
            } else if (newFile.getFileType() == Constants.FILETYPE_UNIX) {
                question = question + Constants.FILETYPE_UNIX_TEXT;
            } else if (newFile.getFileType() == Constants.FILETYPE_ASCII_GENERIC) {
                question = question + Constants.FILETYPE_ASCII_GENERIC_TEXT;
            } else if (newFile.getFileType() == Constants.FILETYPE_ASCII_OTHER) {
                question = question + Constants.FILETYPE_ASCII_OTHER_TEXT;
            }

            question = question + "'." + NL + NL;
            question = question + "The Current OS is '" + System.getProperty("os.name", "?");
            question = question + "'." + NL + NL;
            question = question + "Do you want the file converted to the current OS file format as it is loaded into PC^2?";

            if (!newFile.isExternalFile()) {
                int answer = JOptionPane.showConfirmDialog(this, question, "File Format Mismatch", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    newFile.convertFile(currentOS);
                    return true;
                } else if (answer == JOptionPane.CANCEL_OPTION) {
                    throw new InvalidFieldValue("Update canceled");
                }
            }

        }
        return false;
    }

    /**
     * This resets the form, eg for a new problem.
     */
    void clearForm() {
        getAddButton().setVisible(true);
        getUpdateButton().setVisible(false);
        addButton.setEnabled(true);
        updateButton.setEnabled(false);

        problemNameTextField.setText("");
        timeOutSecondTextField.setText(Integer.toString(Problem.DEFAULT_TIMEOUT_SECONDS));
        judgesHaveAnswerFiles.setSelected(false);
        problemRequiresDataCheckBox.setSelected(false);

        stdinRadioButton.setSelected(true);
        inputDataFileLabel.setText("");
        inputDataFileLabel.setToolTipText("");
        answerFileNameLabel.setText("");
        answerFileNameLabel.setToolTipText("");

        fileRadioButton.setSelected(false);
        stdinRadioButton.setSelected(false);
        
        getUseNOValidatatorRadioButton().setSelected(true);
        
        //clear PC2 Validator options
        getPc2ValidatorOptionComboBox().setSelectedIndex(0);
        getPc2ValidatorIgnoreCaseCheckBox().setSelected(false);
        
        //clear CLICS default validator options
        getCLICSValidatorCaseSensitiveCheckBox().setSelected(false);
        getCLICSSpaceSensitiveCheckBox().setSelected(false);
        getFloatAbsoluteToleranceCheckBox().setSelected(false);
        getFloatRelativeToleranceCheckBox().setSelected(false);
        getFloatAbsoluteToleranceTextField().setText("");
        getFloatRelativeToleranceTextField().setText("");

        //clear custom validator options
        getCustomValidatorCommandOptionsTextField().setText(DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND);
        getCustomValidatorExecutableCommandTextField().setText("");
        getCustomValidatorExecutableCommandTextField().setToolTipText("");
        
        getShowValidatorToJudgesCheckBox().setSelected(true);
        getDoShowOutputWindowCheckBox().setSelected(true);
        getShowCompareCheckBox().setSelected(true);
        getShowCompareCheckBox().setEnabled(getDoShowOutputWindowCheckBox().isSelected());

        getDeleteProblemCheckBox().setSelected(false);

        shortNameTextfield.setText("");

        populateJudging(null);
    }
    

    /**
     * This method initializes the useComputerJudging Radio Button
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getUseComputerJudgingRadioButton() {
        if (computerJudgingRadioButton == null) {
            computerJudgingRadioButton = new JRadioButton();
            computerJudgingRadioButton.setText("Computer Judging");
            computerJudgingRadioButton.setBounds(new Rectangle(32, 14, 173, 21));
            computerJudgingRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    getManualReviewCheckBox().setEnabled(true);
                    getPrelimaryNotificationCheckBox().setEnabled(getManualReviewCheckBox().isSelected());
                    getPrelimaryNotificationCheckBox().setEnabled(getManualReviewCheckBox().isSelected());
                    enableUpdateButton();
                }
            });

        }
        return computerJudgingRadioButton;
    }

    /**
     * This method initializes the manualJudging Radio Button
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getManualJudgingRadioButton() {
        if (manualJudgingRadioButton == null) {
            manualJudgingRadioButton = new JRadioButton();
            manualJudgingRadioButton.setText("Manual Judging");
            manualJudgingRadioButton.setBounds(new Rectangle(32, 132, 257, 21));
            manualJudgingRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    getManualReviewCheckBox().setEnabled(false);
                    getPrelimaryNotificationCheckBox().setEnabled(false);
                    enableUpdateButton();
                }
            });
        }

        return manualJudgingRadioButton;
    }

    /**
     * This method initializes the manualReview CheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getManualReviewCheckBox() {
        if (manualReviewCheckBox == null) {
            manualReviewCheckBox = new JCheckBox();
            manualReviewCheckBox.setText("Manual Review");
            manualReviewCheckBox.setBounds(new Rectangle(57, 47, 186, 21));
            manualReviewCheckBox.setEnabled(false);
            manualReviewCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    getPrelimaryNotificationCheckBox().setEnabled(getManualReviewCheckBox().isSelected());
                    getPrelimaryNotificationCheckBox().setEnabled(getManualReviewCheckBox().isSelected());
                    enableUpdateButton();
                }
            });

        }
        return manualReviewCheckBox;
    }

    /**
     * This method initializes prelimaryNotification
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getPrelimaryNotificationCheckBox() {
        if (prelimaryNotificationCheckBox == null) {
            prelimaryNotificationCheckBox = new JCheckBox();
            prelimaryNotificationCheckBox.setText("Send Preliminary Notification to the team");
            prelimaryNotificationCheckBox.setBounds(new Rectangle(100, 80, 328, 21));
            prelimaryNotificationCheckBox.setEnabled(false);
            prelimaryNotificationCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });

        }
        return prelimaryNotificationCheckBox;
    }

    /**
     * This method initializes deleteProblemCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getDeleteProblemCheckBox() {
        if (deleteProblemCheckBox == null) {
            deleteProblemCheckBox = new JCheckBox();
            deleteProblemCheckBox.setBounds(new Rectangle(285, 340, 182, 21));
            deleteProblemCheckBox.setText("Hide Problem");
            deleteProblemCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return deleteProblemCheckBox;
    }

    /**
     * This method initializes loadButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoadButton() {
        if (loadButton == null) {
            loadButton = new JButton();
            loadButton.setText("Load");
            loadButton.setToolTipText("Load problem def from problem.yaml");
            loadButton.setMnemonic(KeyEvent.VK_L);
            loadButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    loadProblemInfoFile();
                }
            });
        }
        return loadButton;
    }

    /**
     * Load problem info.
     * 
     * If selects problem.yaml then load yaml and files If selects directory will scan for .in and .ans files and load them
     * 
     */
    protected void loadProblemInfoFile() {

        showMessage("Load not implemented, yet.");

        // huh
    }

    public File selectYAMLFileDialog(Component parent, String title, String startDirectory) {

        JFileChooser chooser = new JFileChooser(startDirectory);
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        FileFilter filterYAML = new FileNameExtensionFilter("YAML document (*.yaml)", "yaml");
        chooser.addChoosableFileFilter(filterYAML);

        chooser.setAcceptAllFileFilterUsed(false);
        // bug 759 java7 requires us to select it, otherwise the default choice would be empty
        chooser.setFileFilter(filterYAML);

        int action = chooser.showOpenDialog(parent);

        switch (action) {
            case JFileChooser.APPROVE_OPTION:
                File file = chooser.getSelectedFile();
                lastYamlLoadDirectory = chooser.getCurrentDirectory().toString();
                return file;
            case JFileChooser.CANCEL_OPTION:
            case JFileChooser.ERROR_OPTION:
            default:
                break;
        }
        return null;
    }

    // private String selectFileName(String title, String dirname) throws IOException {
    //
    // String chosenFile = null;
    // File file = selectYAMLFileDialog(this, title, lastDirectory);
    // if (file != null) {
    // chosenFile = file.getCanonicalFile().toString();
    // return chosenFile;
    // } else {
    // return null;
    // }
    // }

    public ContestSnakeYAMLLoader getLoader() {
        if (loader == null) {
            loader = new ContestSnakeYAMLLoader();
        }
        return loader;
    }

    /**
     * This method initializes exportButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getExportButton() {
        if (exportButton == null) {
            exportButton = new JButton();
            exportButton.setText("Export");
            exportButton.setToolTipText("Export problem and files");
            exportButton.setMnemonic(KeyEvent.VK_X);
            exportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (Utilities.isDebugMode()) {
                        saveAndCompare();
                    } else {
                        showMessage("Export not implmented yet"); // SOMEDAY export problem data
                    }
                }
            });
        }
        return exportButton;
    }

    public static String getReportFilename(String prefix, IReport selectedReport) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.SSS");
        // "yyMMdd HHmmss.SSS");
        String reportName = selectedReport.getReportTitle();

        while (reportName.indexOf(' ') > -1) {
            reportName = reportName.replace(" ", "_");
        }
        return prefix + "report." + reportName + "." + simpleDateFormat.format(new Date()) + ".txt";

    }

    void saveAndCompare() {

        try {
            if (debug22EditProblem) {
                System.out.println("debug 22   ORIGINAL  load dump");
                Utilities.dump(originalProblemDataFiles, "debug 22 in load orig");

                String[] s2 = getTestDataList(originalProblemDataFiles);
                System.out.println("debug 22 Number of   ORIGINAL  problem data files is " + s2.length);

                String[] s = getTestDataList(newProblemDataFiles);
                System.out.println("debug 22 B4 Number of new problem data files is " + s.length);
            }
            
            newProblemDataFiles = getProblemDataFilesFromFields();

            if (debug22EditProblem) {
                String[] s = getTestDataList(newProblemDataFiles);
                System.out.println("debug 22 B5 Number of new problem data files is " + s.length);
            }

            Problem newProblem = getProblemFromFields(problem, newProblemDataFiles);

            if (debug22EditProblem) {
                String[] s = getTestDataList(newProblemDataFiles);
                System.out.println("debug 22 B6 Number of new problem data files is " + s.length);

                Utilities.dump(newProblemDataFiles, "debug 22 in load new");
                System.out.flush();
            }

            String fileNameTwo = createProblemReport(newProblem, newProblemDataFiles, "stuf2");
            System.out.println("Created problem report " + fileNameOne);

            showFilesDiff(fileNameOne, fileNameTwo);
        } catch (Exception e) {
            e.printStackTrace(); // debug 22
        }

    }

    private String createProblemReport(Problem prob, ProblemDataFiles datafiles, String fileNamePrefix) {

        ProblemsReport report = new ProblemsReport();
        report.setContestAndController(getContest(), getController());
        String filename = getReportFilename(fileNamePrefix + ".prob.txt", report);

        try {
            PrintWriter printWriter = null;
            printWriter = new PrintWriter(new FileOutputStream(filename, false), true);
            if (problem == null) {
                printWriter.println("  Problem is null");
            } else {
                report.writeRow(printWriter, prob, datafiles);
            }
            printWriter.close();
            printWriter = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        report = null;

        return filename;
    }

    protected void saveProblemYaml() {

        try {

            if (lastSaveDirectory == null) {
                lastSaveDirectory = new File(".").getCanonicalPath() + File.separator + "export";
            }

            char currentLetter = currentDirectoryLetter(lastSaveDirectory);

            String nextDirectory = findNextDirectory(lastSaveDirectory);
            ExportYAML exportYAML = new ExportYAML();

            newProblemDataFiles = getProblemDataFilesFromFields();
            Problem newProblem = getProblemFromFields(problem, newProblemDataFiles);

            String problemYamlFile = nextDirectory + File.separator + IContestLoader.DEFAULT_PROBLEM_YAML_FILENAME;
            String[] filelist = exportYAML.writeProblemYAML(getContest(), newProblem, problemYamlFile, newProblemDataFiles);

            String results = compareDirectories(lastSaveDirectory + File.separator + currentLetter, nextDirectory);
            System.out.println("Comparison : " + results);

            System.out.println("Last dir: " + lastSaveDirectory);
            System.out.println("Wrote " + problemYamlFile);
            for (String string : filelist) {
                System.out.println("Wrote " + string);
            }
        } catch (Exception e) {
            e.printStackTrace(); // debug 22
        }
    }

    /**
     * get Letter.
     * 
     * 
     * @param directory
     * @return
     */
    char currentDirectoryLetter(String directory) {
        char letter = 'A';
        String nextDirectory = directory + File.separator + letter;
        File file = new File(nextDirectory);

        while (file.isDirectory()) {
            System.out.println("Found directory: " + nextDirectory);

            letter++;
            nextDirectory = directory + File.separator + letter;
            file = new File(nextDirectory);
            if (!file.isDirectory()) {
                letter--;
            }
        }

        return letter;

    }

    private String findNextDirectory(String directory) {

        char letter = currentDirectoryLetter(directory);
        String nextDirectory = directory + File.separator + letter;
        File file = new File(nextDirectory);

        while (file.isDirectory()) {
            System.out.println("Found directory: " + nextDirectory);

            letter++;
            nextDirectory = directory + File.separator + letter;
            file = new File(nextDirectory);
        }

        if (!file.isDirectory()) {
            file.mkdirs();
            System.out.println("Created dir " + nextDirectory);
        }

        return nextDirectory;
    }

    protected void goodsaveProblemYaml() {

        Problem newProblem = null;

        try {
            newProblemDataFiles = getProblemDataFilesFromFields();
            newProblem = getProblemFromFields(problem, newProblemDataFiles);

        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

        try {
            JFileChooser chooser = new JFileChooser(lastSaveDirectory);
            FileFilter filterYAML = new FileNameExtensionFilter("YAML File", "yaml");
            chooser.setDialogTitle("Save problem to problem.YAML ");
            File file = new File("problem.yaml");
            chooser.setSelectedFile(file);
            chooser.setFileFilter(filterYAML);
            int result = chooser.showSaveDialog(this);

            if (result == JOptionPane.YES_OPTION) {
                File selectedFile = chooser.getSelectedFile().getCanonicalFile();
                // chooser.setCurrentDirectory(new File(lastSaveDirectory));

                if (selectedFile.exists()) {
                    result = FrameUtilities.yesNoCancelDialog(this, "Overwrite " + selectedFile.getName(), "Overwrite existing file?");

                    if (result != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                ExportYAML exportYAML = new ExportYAML();

                String[] filelist = exportYAML.writeProblemYAML(getContest(), newProblem, selectedFile.getAbsolutePath(), newProblemDataFiles);

                String fileComment = "";
                if (filelist.length > 0) {
                    fileComment = "(" + filelist.length + " data files written)";
                }

                showMessage("Wrote problem YAML to " + selectedFile.getName() + " " + fileComment);

                if (Utilities.isDebugMode()) {
                    FrameUtilities.viewFile(selectedFile.getAbsolutePath(), selectedFile.getName(), getLog());
                }

            } else {
                showMessage("No file selected/saved");
            }

        } catch (IOException e) {
            showMessage("Problem saving yaml file " + e.getMessage());
        }
    }

    /**
     * This method initializes reportButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getReportButton() {
        if (reportButton == null) {
            reportButton = new JButton();
            reportButton.setText("Report");
            reportButton.setMnemonic(KeyEvent.VK_R);
            reportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    viewProblemReport();
                }
            });
        }
        return reportButton;
    }

    protected void viewProblemReport() {
        SingleProblemReport singleProblemReport = new SingleProblemReport();

        try {
            newProblemDataFiles = getProblemDataFilesFromFields();
            Problem newProblem = getProblemFromFields(problem, newProblemDataFiles);
            singleProblemReport.setProblem(newProblem, newProblemDataFiles);
            Utilities.viewReport(singleProblemReport, "Problem Report " + getProblemNameTextField().getText(), getContest(), getController());
        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

    }

    /**
     * This method initializes judgeTypeInnerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJudgeTypeInnerPane() {
        if (judgeTypeInnerPane == null) {
            judgeTypeInnerPane = new JPanel();
            judgeTypeInnerPane.setLayout(null);
            judgeTypeInnerPane.setPreferredSize(new Dimension(190, 190));
            judgeTypeInnerPane.add(getUseComputerJudgingRadioButton(), null);
            judgeTypeInnerPane.add(getManualReviewCheckBox(), null);
            judgeTypeInnerPane.add(getPrelimaryNotificationCheckBox(), null);
            judgeTypeInnerPane.add(getManualJudgingRadioButton(), null);
        }
        return judgeTypeInnerPane;
    }

    /**
     * This method initializes ccsSettingsPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCcsSettingsPane() {
        if (ccsSettingsPane == null) {
            ccsSettingsPane = new JPanel();
            ccsSettingsPane.setLayout(null);
            ccsSettingsPane.setBorder(BorderFactory.createTitledBorder(null, "CCS Standard Problem Settings", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog",
                    Font.BOLD, 12), new Color(51, 51, 51)));
            ccsSettingsPane.add(getCcsValidationEnabledCheckBox(), null);
        }
        return ccsSettingsPane;
    }

    /**
     * This method initializes ccsValidationEnabledCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getCcsValidationEnabledCheckBox() {
        if (ccsValidationEnabledCheckBox == null) {
            ccsValidationEnabledCheckBox = new JCheckBox();
            ccsValidationEnabledCheckBox.setToolTipText("Use validator that uses exit code to return judgement");
            ccsValidationEnabledCheckBox.setBounds(new Rectangle(27, 33, 375, 28));
            ccsValidationEnabledCheckBox.setText("Use CCS validator interface");

            ccsValidationEnabledCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return ccsValidationEnabledCheckBox;
    }

    /**
     * 
     * @param string
     * @param nextDirectory
     * @return
     */
    public String compareDirectories(String directory, String nextDirectory) {

        ArrayList<String> filelist = getFileEntries(directory, "", 0);
        ArrayList<String> filelistTwo = getFileEntries(nextDirectory, "", 0);

        int matching = 0;

        if (filelist.size() == filelistTwo.size()) {
            for (int i = 0; i < filelist.size(); i++) {
                String name1 = filelist.get(i);
                String name2 = filelistTwo.get(i);
                if (name1.equals(name2)) {
                    matching++;
                } else {
                    System.err.println("Miss match " + name1 + " vs " + name2);
                }
            }
        }

        if (matching == filelist.size()) {
            return "All " + matching + " matching";
        } else {
            return filelist.size() + " vs " + filelistTwo.size();
        }
    }

    /**
     * Returns all filenames (relative path) under input directory.
     * 
     * The list does not contain the string directory. Only directories under the directory will be included.
     * 
     * @param directory
     * @param relativeDirectory
     * @param level
     * @return all files names with relative paths.
     */
    private ArrayList<String> getFileEntries(String directory, String relativeDirectory, int level) {

        ArrayList<String> list = new ArrayList<>();

        File[] files = new File(directory).listFiles();

        if (relativeDirectory.length() > 0) {
            relativeDirectory += File.separator;
        }

        for (File entry : files) {
            if (entry.isFile()) {
                list.add(relativeDirectory + entry.getName());
            }
        }

        // recurse

        for (File entry : files) {
            if (entry.isDirectory() && !(entry.getName().equals(".") || entry.getName().equals(".."))) {
                list.addAll(getFileEntries(directory + File.separator + entry.getName(), //
                        relativeDirectory + entry.getName(), level + 1));
            }
        }

        return list;
    }

    public JTextField getShortNameTextfield() {
        return shortNameTextfield;
    }
    private JPanel getNoValidatorPanel() {
        if (noValidatorPanel == null) {
        	noValidatorPanel = new JPanel();
        	noValidatorPanel.setMaximumSize(new Dimension(500, 200));
        	noValidatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        	noValidatorPanel.setBorder(null);
        	FlowLayout flowLayout = (FlowLayout) noValidatorPanel.getLayout();
        	flowLayout.setHgap(10);
        	flowLayout.setAlignment(FlowLayout.LEFT);
        	noValidatorPanel.add(getUseNOValidatatorRadioButton());
        }
        return noValidatorPanel;
    }
    private JPanel getClicsValidatorPanel() {
        if (clicsValidatorPanel == null) {
        	clicsValidatorPanel = new JPanel();
        	clicsValidatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            clicsValidatorPanel.setLayout(new BorderLayout(0, 0));
        	clicsValidatorPanel.setMaximumSize(new Dimension(500, 200));
            clicsValidatorPanel.add(getClicsOptionButtonPanel(), BorderLayout.NORTH);
        	clicsValidatorPanel.add(getHorizontalStrut(), BorderLayout.WEST);
        	clicsValidatorPanel.add(getClicsValidatorOptionsSubPanel());
        }
        return clicsValidatorPanel;
    }
    
    private Component getHorizontalStrut() {
        if (horizontalStrut == null) {
        	horizontalStrut = Box.createHorizontalStrut(20);
        	horizontalStrut.setPreferredSize(new Dimension(35, 0));
        }
        return horizontalStrut;
    }
    
    private JPanel getClicsOptionButtonPanel() {
        if (clicsOptionButtonPanel == null) {
            clicsOptionButtonPanel = new JPanel();
            clicsOptionButtonPanel.setBorder(null);
            clicsOptionButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            FlowLayout fl_clicsOptionButtonPanel = new FlowLayout(FlowLayout.LEFT);
            fl_clicsOptionButtonPanel.setHgap(0);
            clicsOptionButtonPanel.setLayout(fl_clicsOptionButtonPanel);
            clicsOptionButtonPanel.add(getUseCLICSValidatorRadioButton());
            clicsOptionButtonPanel.add(getLblWhatsThis());
        }
        return clicsOptionButtonPanel;
    }
    
    private JPanel getCustomValidatorOptionsSubPanel() {
        if (customValidatorOptionsSubPanel == null) {
        	customValidatorOptionsSubPanel = new JPanel();
        	customValidatorOptionsSubPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        	customValidatorOptionsSubPanel.setPreferredSize(new Dimension(500, 150));
        	customValidatorOptionsSubPanel.setBorder(new TitledBorder(null, "Validator options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        	
        	GridBagLayout gbl_customValidatorOptionsPanel = new GridBagLayout();
        	gbl_customValidatorOptionsPanel.columnWidths = new int[] {140, 150, 50};
        	gbl_customValidatorOptionsPanel.rowHeights = new int[] {30, 30};
        	gbl_customValidatorOptionsPanel.columnWeights = new double[]{0.0, 0.0, 0.0};
        	gbl_customValidatorOptionsPanel.rowWeights = new double[]{0.0, 0.0};
        	customValidatorOptionsSubPanel.setLayout(gbl_customValidatorOptionsPanel);
        	
        	GridBagConstraints gbc_lblValidatorCommand = new GridBagConstraints();
        	gbc_lblValidatorCommand.anchor = GridBagConstraints.EAST;
        	gbc_lblValidatorCommand.insets = new Insets(0, 0, 5, 5);
        	gbc_lblValidatorCommand.gridx = 0;
        	gbc_lblValidatorCommand.gridy = 0;
        	customValidatorOptionsSubPanel.add(getCustomValidatorExecutableCommandLabel(), gbc_lblValidatorCommand);
        	
        	GridBagConstraints gbc_textField = new GridBagConstraints();
        	gbc_textField.insets = new Insets(0, 0, 5, 5);
        	gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        	gbc_textField.gridx = 1;
        	gbc_textField.gridy = 0;
        	customValidatorOptionsSubPanel.add(getCustomValidatorExecutableCommandTextField(), gbc_textField);
        	
        	GridBagConstraints gbc_validatorProgramJButton = new GridBagConstraints();
        	gbc_validatorProgramJButton.anchor = GridBagConstraints.NORTHWEST;
        	gbc_validatorProgramJButton.insets = new Insets(0, 0, 5, 0);
        	gbc_validatorProgramJButton.gridx = 2;
        	gbc_validatorProgramJButton.gridy = 0;
        	customValidatorOptionsSubPanel.add(getChooseValidatorProgramButton(), gbc_validatorProgramJButton);
        	
        	GridBagConstraints gbc_CustomValidatorCommandOptionsLabel = new GridBagConstraints();
        	gbc_CustomValidatorCommandOptionsLabel.anchor = GridBagConstraints.EAST;
        	gbc_CustomValidatorCommandOptionsLabel.insets = new Insets(0, 0, 0, 5);
        	gbc_CustomValidatorCommandOptionsLabel.gridx = 0;
        	gbc_CustomValidatorCommandOptionsLabel.gridy = 1;
        	customValidatorOptionsSubPanel.add(getCustomValidatorCommandOptionsLabel(), gbc_CustomValidatorCommandOptionsLabel);
        	
        	GridBagConstraints gbc_validatorCommandLineTextBox = new GridBagConstraints();
        	gbc_validatorCommandLineTextBox.insets = new Insets(0, 0, 0, 5);
        	gbc_validatorCommandLineTextBox.fill = GridBagConstraints.HORIZONTAL;
        	gbc_validatorCommandLineTextBox.gridx = 1;
        	gbc_validatorCommandLineTextBox.gridy = 1;
        	customValidatorOptionsSubPanel.add(getCustomValidatorCommandOptionsTextField(), gbc_validatorCommandLineTextBox);
        }
        return customValidatorOptionsSubPanel;
    }
    
    private Component getHorizontalStrut_1() {
        if (horizontalStrut_1 == null) {
        	horizontalStrut_1 = Box.createHorizontalStrut(20);
        	horizontalStrut_1.setPreferredSize(new Dimension(35, 0));
        }
        return horizontalStrut_1;
    }
    
    private JLabel getCustomValidatorExecutableCommandLabel() {
        if (customValidatorCommandLabel == null) {
        	customValidatorCommandLabel = new JLabel("Validator program:");
        }
        return customValidatorCommandLabel;
    }
    
    private JLabel getCustomValidatorCommandOptionsLabel() {
        if (customValidatorCommandOptionsLabel == null) {
            customValidatorCommandOptionsLabel = new JLabel("Validator Command Line:");
        }
        return customValidatorCommandOptionsLabel;
    }    
    
    private JTextField getCustomValidatorExecutableCommandTextField() {
        if (customValidatorCommandTextField == null) {
        	customValidatorCommandTextField = new JTextField();
        	customValidatorCommandTextField.setEnabled(false);
        	customValidatorCommandTextField.setColumns(25);
        	customValidatorCommandTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });

        }
        return customValidatorCommandTextField;
    }
    private Component getVerticalStrut() {
        if (verticalStrut == null) {
        	verticalStrut = Box.createVerticalStrut(20);
        	verticalStrut.setPreferredSize(new Dimension(0, 40));
        }
        return verticalStrut;
    }
    private Component getVerticalStrut_1() {
        if (verticalStrut_1 == null) {
        	verticalStrut_1 = Box.createVerticalStrut(20);
        }
        return verticalStrut_1;
    }
    private Component getVerticalStrut_2() {
        if (verticalStrut_2 == null) {
        	verticalStrut_2 = Box.createVerticalStrut(20);
        }
        return verticalStrut_2;
    }
    private Component getVerticalStrut_3() {
        if (verticalStrut_3 == null) {
        	verticalStrut_3 = Box.createVerticalStrut(20);
        }
        return verticalStrut_3;
    }
    private Component getVerticalStrut_4() {
        if (verticalStrut_4 == null) {
        	verticalStrut_4 = Box.createVerticalStrut(20);
        }
        return verticalStrut_4;
    }
    private Component getVerticalStrut_5() {
        if (verticalStrut_5 == null) {
        	verticalStrut_5 = Box.createVerticalStrut(20);
        }
        return verticalStrut_5;
    }
    private JPanel getPc2ValidatorPanel() {
        if (pc2ValidatorPanel == null) {
        	pc2ValidatorPanel = new JPanel();
        	pc2ValidatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        	pc2ValidatorPanel.setLayout(new BorderLayout(0, 0));
        	pc2ValidatorPanel.setMaximumSize(new Dimension(500, 200));
        	pc2ValidatorPanel.add(getUsePC2ValidatorRadioButton(), BorderLayout.NORTH);
        	pc2ValidatorPanel.add(getHorizontalStrut_2(), BorderLayout.WEST);
        	pc2ValidatorPanel.add(getPc2ValidatorOptionsSubPanel());
        }
        return pc2ValidatorPanel;
    }
    private JRadioButton getUsePC2ValidatorRadioButton() {
        if (usePC2ValidatorRadioButton == null) {
        	usePC2ValidatorRadioButton = new JRadioButton("Use PC^2 Validator");
        	usePC2ValidatorRadioButton.setPreferredSize(new Dimension(21, 23));
        	usePC2ValidatorRadioButton.setMinimumSize(new Dimension(21, 23));
        	usePC2ValidatorRadioButton.setMaximumSize(new Dimension(21, 23));
        	usePC2ValidatorRadioButton.setMargin(new Insets(2, 12, 2, 2));
        	usePC2ValidatorRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableValidatorComponents();
                    enableUpdateButton();
                }
            });
        }
        return usePC2ValidatorRadioButton;
    }
    private Component getHorizontalStrut_2() {
        if (horizontalStrut_2 == null) {
        	horizontalStrut_2 = Box.createHorizontalStrut(20);
        	horizontalStrut_2.setPreferredSize(new Dimension(35, 0));
        }
        return horizontalStrut_2;
    }
    private JPanel getPc2ValidatorOptionsSubPanel() {
        if (pc2ValidatorOptionsSubPanel == null) {
        	pc2ValidatorOptionsSubPanel = new JPanel();
        	pc2ValidatorOptionsSubPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "PC^2 Validator options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        	
        	GridBagLayout gbl_pc2ValidatorOptionsSubPanel = new GridBagLayout();
        	gbl_pc2ValidatorOptionsSubPanel.columnWidths = new int[] {100, 100};
        	gbl_pc2ValidatorOptionsSubPanel.rowHeights = new int[]{20,20};
        	gbl_pc2ValidatorOptionsSubPanel.columnWeights = new double[]{0.0, 0.0};
        	gbl_pc2ValidatorOptionsSubPanel.rowWeights = new double[]{0.0,0.0};
        	pc2ValidatorOptionsSubPanel.setLayout(gbl_pc2ValidatorOptionsSubPanel);
        	
        	GridBagConstraints gbc_pc2ValidatorOptionComboBoxLabel = new GridBagConstraints();
        	gbc_pc2ValidatorOptionComboBoxLabel.insets = new Insets(0, 20, 5, 5);
        	gbc_pc2ValidatorOptionComboBoxLabel.gridx = 0;
        	gbc_pc2ValidatorOptionComboBoxLabel.gridy = 0;
        	pc2ValidatorOptionsSubPanel.add(getPc2ValidatorOptionComboBoxLabel(), gbc_pc2ValidatorOptionComboBoxLabel);
        	
        	GridBagConstraints gbc_pc2ValidatorOptionComboBox = new GridBagConstraints();
        	gbc_pc2ValidatorOptionComboBox.anchor = GridBagConstraints.WEST;
        	gbc_pc2ValidatorOptionComboBox.fill = GridBagConstraints.VERTICAL;
        	gbc_pc2ValidatorOptionComboBox.weightx = 1.0;
        	gbc_pc2ValidatorOptionComboBox.insets = new Insets(0, 0, 5, 0);
        	gbc_pc2ValidatorOptionComboBox.gridx = 1;
        	gbc_pc2ValidatorOptionComboBox.gridy = 0;
        	pc2ValidatorOptionsSubPanel.add(getPc2ValidatorOptionComboBox(), gbc_pc2ValidatorOptionComboBox);
        	
        	GridBagConstraints gbc_pc2ValidatorIgnoreCaseCheckBox = new GridBagConstraints();
        	gbc_pc2ValidatorIgnoreCaseCheckBox.anchor = GridBagConstraints.WEST;
        	gbc_pc2ValidatorIgnoreCaseCheckBox.insets = new Insets(0, 30, 0, 5);
        	gbc_pc2ValidatorIgnoreCaseCheckBox.gridx = 0;
        	gbc_pc2ValidatorIgnoreCaseCheckBox.gridy = 1;
        	pc2ValidatorOptionsSubPanel.add(getPc2ValidatorIgnoreCaseCheckBox(), gbc_pc2ValidatorIgnoreCaseCheckBox);
        	


        	
        	
        }
        return pc2ValidatorOptionsSubPanel;
    }

    private JLabel getPc2ValidatorOptionComboBoxLabel() {
        if (pc2ValidatorOptionComboBoxLabel == null) {
        	pc2ValidatorOptionComboBoxLabel = new JLabel("Validator mode:");
        }
        return pc2ValidatorOptionComboBoxLabel;
    }
    
    private JComboBox<String> getPc2ValidatorOptionComboBox() {
        if (pc2ValidatorOptionComboBox == null) {
            pc2ValidatorOptionComboBox = new JComboBox<String>();
//            pc2ValidatorOptionComboBox.setBounds(new java.awt.Rectangle(158, 24, 255, 26));

            pc2ValidatorOptionComboBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    enableUpdateButton();

                }
            });
            pc2ValidatorOptionComboBox.addItem("None Selected");
            pc2ValidatorOptionComboBox.addItem("1 - diff");
            pc2ValidatorOptionComboBox.addItem("2 - ignore whitespace at start of file");
            pc2ValidatorOptionComboBox.addItem("3 - ignore leading whitespace on lines");
            pc2ValidatorOptionComboBox.addItem("4 - ignore all whitespace on lines");
            pc2ValidatorOptionComboBox.addItem("5 - ignore empty lines");

        }
        return pc2ValidatorOptionComboBox;
    }
    
    private JCheckBox getPc2ValidatorIgnoreCaseCheckBox() {
        if (pc2ValidatorIgnoreCaseCheckBox == null) {
        	pc2ValidatorIgnoreCaseCheckBox = new JCheckBox("Ignore Case In Output");
        	pc2ValidatorIgnoreCaseCheckBox.setMinimumSize(new Dimension(80, 23));
        	pc2ValidatorIgnoreCaseCheckBox.setMaximumSize(new Dimension(80, 23));
        	pc2ValidatorIgnoreCaseCheckBox.setPreferredSize(new Dimension(150, 23));
        	pc2ValidatorIgnoreCaseCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return pc2ValidatorIgnoreCaseCheckBox;
    }
    
    /**
     * Returns a CustomValidatorSettings object containing the values currently displayed in the GUI.
     * 
     * @return a CustomValidatorSettings object populated from the GUI
     */
    private CustomValidatorSettings getCustomValidatorSettingsFromFields() {
        CustomValidatorSettings settings = new CustomValidatorSettings();
        settings.setCustomValidatorInvocationCommand(this.getCustomValidatorExecutableCommandTextField().getText());
        settings.setCustomValidatorCommandOptions(this.getCustomValidatorCommandOptionsTextField().getText());
        
        return settings;
    }

    /**
     * Returns a {@link ClicsValidatorSettings} object containing the values currently displayed in the GUI.
     * Displays an error message and throws {@link InvalidFieldValue} if either the absolute or relative
     * tolerance are selected and the string in the corresponding text box is invalid.
     * 
     * @return a ClicsValidatorSettings object populated from the GUI
     * @throws {@link InvalidFieldValue} if an invalid tolerance value is detected
     */
    private ClicsValidatorSettings getCLICSValidatorSettingsFromFields() {
        
        ClicsValidatorSettings settings = new ClicsValidatorSettings();
        
        settings.setCaseSensitive(getCLICSValidatorCaseSensitiveCheckBox().isSelected());
        settings.setSpaceSensitive(getCLICSSpaceSensitiveCheckBox().isSelected());
        
        if (getFloatAbsoluteToleranceCheckBox().isSelected()) {

            double absTol;
            try {
                absTol = Double.parseDouble(getFloatAbsoluteToleranceTextField().getText());
            } catch (NumberFormatException e) {
                showMessage("Invalid absolute tolerance value");
                throw new InvalidFieldValue("Invalid absolute tolerance value");
            }
            settings.setFloatAbsoluteTolerance(absTol);
        }
        
        if (getFloatRelativeToleranceCheckBox().isSelected()) {

            double relTol;
            try {
                relTol = Double.parseDouble(getFloatRelativeToleranceTextField().getText());
            } catch (NumberFormatException e) {
                showMessage("Invalid relative tolerance value");
                throw new InvalidFieldValue("Invalid relative tolerance value");
            }
            settings.setFloatRelativeTolerance(relTol);
        }
        
        return settings;
    }
    
    private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

} // @jve:decl-index=0:visual-constraint="10,10"

