package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.inputValidation.InputValidationResult;
import edu.csus.ecs.pc2.core.model.inputValidation.ProblemInputValidationResults;
import edu.csus.ecs.pc2.ui.cellRenderer.CheckBoxCellRenderer;
import edu.csus.ecs.pc2.ui.cellRenderer.PassFailCellRenderer;

/**
 * A pane for running the input validators for currently defined problems and displaying the results.
 * 
 * @author $Author$ John
 * @version $Id$
 */
public class RunInputValidatorsPane extends JPanePlugin  {

    private static final long serialVersionUID = 1;

    private JButton closeButton;

    private JButton runSelectedButton;
    
    private JButton runAllButton;

    private JPanel msgPanel;

    private JPanel resultsPanel;

    private JScrollPane resultsScrollPane;

    private JTable resultsTable;

    private TableModel allProblemsInputValidationResultsTableModel = new AllProblemsInputValidationResultsTableModel();

    private JPanel buttonPanel;

    private boolean listenersAdded;


    public RunInputValidatorsPane() {
        
        initialize();
    }


    private void initialize() {

        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(775, 536));

        this.add(getMessagePanel(), java.awt.BorderLayout.NORTH);
        this.add(getResultsPanel(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);

    }
    
    
    
    private JPanel getMessagePanel() {
        if (msgPanel == null) {
            msgPanel = new JPanel();
            msgPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

            JLabel msgPanelLabel = new JLabel("");
            msgPanelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            msgPanel.add(msgPanelLabel);
        }
        return msgPanel;
    }
    
    private JPanel getResultsPanel() {
        if (resultsPanel == null) {
            resultsPanel = new JPanel();
            resultsPanel.setPreferredSize(new Dimension(700, 700));
            resultsPanel.setMinimumSize(new Dimension(700, 700));
            resultsPanel.setBorder(new TitledBorder(null, "Run Results", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            resultsPanel.add(getInputValidatorResultsScrollPane());
        }
        return resultsPanel;
    }
    
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.add(getRunSelectedButton());
            buttonPanel.add(getRunAllButton());
            buttonPanel.add(getCloseButton());
        }
        return buttonPanel;

    }
    
    private JScrollPane getInputValidatorResultsScrollPane() {
        if (resultsScrollPane == null) {
            resultsScrollPane = new JScrollPane();
            resultsScrollPane.setMinimumSize(new Dimension(450, 450));
            resultsScrollPane.setPreferredSize(new Dimension(700, 450));
            resultsScrollPane.setViewportView(getInputValidatorResultsTable());
        }
        return resultsScrollPane;
    }

    //copied from EditProblemPane; needs to be updated for this class
    public JTable getInputValidatorResultsTable() {
        if (resultsTable == null) {
            resultsTable = new JTable(allProblemsInputValidationResultsTableModel);
            resultsTable.setMinimumSize(new Dimension(450, 0));
            
            //set the desired options on the table
            resultsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            resultsTable.setFillsViewportHeight(true);
            resultsTable.setRowSelectionAllowed(false);
            resultsTable.getTableHeader().setReorderingAllowed(false);
            
            //the following statement is necessary (it's one way to force the Table to use the Table Model's rowcount, which it doesn't in all cases)
            //Another way might be to add a custom row sorter, but make sure the row sorter's model is updated every time the Table Model is updated...
            //@see http://stackoverflow.com/questions/23626951/jtable-row-count-vs-model-row-count
            resultsTable.setAutoCreateRowSorter(true);

            //code from MultipleDataSetPane:
            // insert a renderer that will center cell contents
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

            for (int i = 0; i < resultsTable.getColumnCount(); i++) {
                resultsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            resultsTable.setDefaultRenderer(String.class, centerRenderer);
//
//            // also center column headers (which use a different CellRenderer)
            //(this code came from MultipleDataSetPane, but the JTable here already has centered headers...
//            ((DefaultTableCellRenderer) testDataSetsListBox.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

            // change the header font
            JTableHeader header = resultsTable.getTableHeader();
            header.setFont(new Font("Dialog", Font.BOLD, 12));
            
            // render Select column as a checkbox
            resultsTable.getColumn("Select").setCellRenderer(new CheckBoxCellRenderer());
            
            // render Result column as Pass/Fail on Green/Red background
            resultsTable.getColumn("Overall Result").setCellRenderer(new PassFailCellRenderer());


        }
        return resultsTable;
    }

    private JButton getRunSelectedButton() {
        if (runSelectedButton == null) {
            runSelectedButton = new JButton("Run Selected");
            runSelectedButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null, "This function isn't implemented yet...", "Not Implemented", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
        return runSelectedButton;
    }
    
    private JButton getRunAllButton() {
        if (runAllButton == null) {
            runAllButton = new JButton("Run All");
            runAllButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null, "This function isn't implemented yet...", "Not Implemented", JOptionPane.INFORMATION_MESSAGE);
                }
            });
        }
        return runAllButton;
    }
    
    private JButton getCloseButton() {
        if (closeButton == null) {
            closeButton = new JButton("Close");
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleCloseButton();
                }
            });
        }
        return closeButton;
    }
    
    private void handleCloseButton() {
        this.getParentFrame().setVisible(false);
    }
    

    /**
     * Populates the GUI fields with Problem InputValidationResults data from the underlying table model.
     */
    protected void populateGUI() {
        
        //get the currently-defined problems
        Problem [] probs = getContest().getProblems();
        
        //get the Input Validation Results for each problem (note that this could be empty for any given problem, or all problems)
        Vector<ProblemInputValidationResults> tableData = getInputValidationResultsTableData(probs);
               
        //put the table data into the table model
        ((AllProblemsInputValidationResultsTableModel)getInputValidatorResultsTable().getModel()).setResults(tableData);
        
        //fire table data changed to update the display
        ((AllProblemsInputValidationResultsTableModel)getInputValidatorResultsTable().getModel()).fireTableDataChanged();
        
    }

    
    private Vector<ProblemInputValidationResults> getInputValidationResultsTableData(Problem [] probs) {
        
        Vector<ProblemInputValidationResults> tableData = new Vector<ProblemInputValidationResults> ();
        
        for (int prob=0; prob<probs.length; prob++) {
            if (probs[prob].isProblemHasInputValidator()) {
                
                ProblemInputValidationResults result = new ProblemInputValidationResults(probs[prob]);
                
                // add each result for the current problem to the current row
                for (InputValidationResult res : probs[prob].getInputValidationResults()) {

                    result.addResult(res); // do we need to clone the InputValidationResult?
                }
                
                tableData.add(result);
            }
        }
        
        return tableData;
    }
    
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        addWindowListeners();
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI();
            }
        });

    }
  
//    /**
//     * Runs the Input Validator specified in the GUI, using the GUI-specified Input Validator Command, against 
//     */
//    private void runInputDataValidationTest() {
//        
//        //get the command line from the GUI
//        String cmdline = getInputValidatorCommandLine();
//        
//        //get an execute directory name
//        String executeDir = getExecuteDirectoryName();        
//  
//        //TODO: need to save the Serialized File in the model (but don't do that in this method - do it in the Add/Update button handler)
//        String validatorProgName = getInputValidatorPane().getInputValidatorProgramName;
//        SerializedFile validatorProg = new SerializedFile(validatorProgName);
//        
//        //make sure the SerializedFile was created ok
//        try {
//            if (Utilities.serializedFileError(validatorProg)) {
//                throw new RuntimeException("Error creating SerializedFile from file ' " + validatorProgName + " '");
//            }
//        } catch (Exception e) {
//            showMessage(getParentFrame(), "Error Creating Validator", "An error occurred while creating the validator program" + e.getMessage());
//            setInputValidationStatus(InputValidationStatus.ERROR);
//            return;
//        }
//        
//        SerializedFile [] dataFiles = getDataFiles();
//        
//        InputValidatorRunner runner = new InputValidatorRunner(getContest(), getController());
//        
//        results = null;
//        try {
//            results = runner.runInputValidator(getProblem(), validatorProg, cmdline, executeDir, dataFiles);
//            inputValidatorHasBeenRun = true;
//        } catch (ExecuteException e) {
//            JOptionPane.showMessageDialog(this, "Error running Input Validator: \n" + e.getMessage() + "\nCheck logs for further details", "Input Validator Error", JOptionPane.WARNING_MESSAGE);
//            getInputValidationResultPane().getInputValidationResultSummaryTextLabel().setText("Errror Running Input Validator");
//            getInputValidationResultPane().getInputValidationResultSummaryTextLabel().setForeground(Color.RED);
//            setInputValidationStatus(InputValidationStatus.ERROR);
//        }
//            
//        //update the results table
//        ((InputValidationResultsTableModel)getInputValidationResultPane().getInputValidatorResultsTable().getModel()).setResults(results);
//        ((AbstractTableModel) getInputValidationResultPane().getInputValidatorResultsTable().getModel()).fireTableDataChanged();
//        
//        //adjust the column widths in the updated table
////        getInputValidatorResultsTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
////        TableColumnAdjuster tca = new TableColumnAdjuster(getInputValidatorResultsTable());
////        tca.adjustColumns();  //this is shrinking columns to use less than the component width; looks ugly
//        
//        if (results != null) {
//            // update the result summary label
//
//            boolean allPassed = true;
//            for (int i = 0; i < results.length; i++) {
//                if (!results[i].isPassed()) {
//                    allPassed = false;
//                    break;
//                    
//                }
//            }
//            String resultSummaryString = allPassed ? "All Input Data Files passed validation"
//                                                    : "One or more Input Data Files FAILED validation";
//            Color color = allPassed ? Color.green : Color.red;
//            getInputValidationResultPane().getInputValidationResultSummaryTextLabel().setText(resultSummaryString);
//            getInputValidationResultPane().getInputValidationResultSummaryTextLabel().setForeground(color);
//            if (allPassed) {
//                setInputValidationStatus(InputValidationStatus.PASSED);   
//            } else {
//                setInputValidationStatus(InputValidationStatus.FAILED);
//            }
//        }
//    }
//    
//
//    private String getInputValidatorCommandLine() {
//       
//        String progName = getDefineInputValidatorPane().getInputValidatorProgramNameTextField().getText();
//        String cmd = getDefineInputValidatorPane().getInputValidatorCommandTextField().getText();
//        
//        cmd = replaceString(cmd, "{:inputvalidator}", progName);
//        cmd = replaceString(cmd, "{:basename}", Utilities.basename(removeExtension(progName)));
//        
//        return cmd;
//    }
//    
//    /**
//     * Returns an array of SerializedFiles containing data files to be validated.
//     * 
//     * @return
//     */
//    private SerializedFile [] getDataFiles() {
//        
//        SerializedFile [] retArray = null;
//        
//        //check if the files are coming from either the MSTOVPane or from a folder
//        if (getExecuteInputValidatorPane().getFilesOnDiskInFolderRadioButton().isSelected() || 
//                getExecuteInputValidatorPane().getFilesJustLoadedRadioButton().isSelected()) {
//
//            //get the names of the data files to be validated
//            String [] inputFileNames = getInputFileNames();
//            
//            if (inputFileNames == null || inputFileNames.length == 0) {
//                showMessage(getParentFrame(), "No Data Files found", "Error - no input data files found");
//                getLog().log(Log.INFO, "Request to run Input Validator, but no input data files found");
//                throw new RuntimeException("Request to run Input Validator, but no input data files found");
//            } else {
//                
//                //construct SerializedFiles from the specified file names
//                
//                retArray = new SerializedFile [inputFileNames.length];
//                
//                for (int i=0; i< inputFileNames.length; i++) {
//                    
//                    retArray[i] = new SerializedFile(inputFileNames[i]);
//                    
//                    //check to make sure the file serialized ok
//                    try {
//                        if (Utilities.serializedFileError(retArray[i])) {
//                            throw new RuntimeException("Error creating SerializedFile from file ' " + inputFileNames[i] + " '");
//                        }
//                    } catch (Exception e) {
//                        //serializedFileError threw an exception -- i.e. it found an exception in the SerializedFile
//                        showMessage(getParentFrame(), "Error creating data files", "An error occurred while serializing the data files: " + e.getMessage());
//                        getLog().log(Log.WARNING, "An error occurred while serializing the data files: " + e.getMessage());
//                        return null;
//                    }
//                }
//            }
//            
//        } else if (getExecuteInputValidatorPane().getFilesPreviouslyLoadedRadioButton().isSelected()) {
//            //get the Serialized Judge's Data files out of the contest model and return that
//            
//            retArray = originalProblemDataFiles.getJudgesDataFiles();
//            
//        } else {
//            //we should never be able to get here -- the button group should insure that exactly one button is pushed
//            System.err.println ("Undefined condition in EditProblemPane.getDataFiles(): no Input Data File radio button is selected!");
//            getLog().log(Log.WARNING, "Undefined condition in EditProblemPane.getDataFiles(): no Input Data File radio button is selected!");
//        }
//        
//        return retArray;
//        
//    }
    
//    /**
//     * Returns an array of Strings giving the names of Problem Input Data Files which are to be checked 
//     * by running the currently-specified Input Validator using each file as input.
//     * 
//     * Uses the currently active "Input Data Files to Validate" button to determine the set of data file
//     * names to be returned.  If no data files could be found at the specified source, null is returned.
//     *  
//     * @return an Array of Strings containing Input Data file names, or null if no files were found
//     */
//    private String[] getInputFileNames() {
//
//        String [] retVal = null;
//        
//        if (getExecuteInputValidatorPane().getFilesOnDiskInFolderRadioButton().isSelected()) {
//            //read the specified folder and return the names of files in that folder
//            String folderName = getExecuteInputValidatorPane().getInputValidatorFilesOnDiskTextField().getText();
//            if (folderName != null && !folderName.trim().equals("")) {
//                
//                File folderPath = new File(folderName);
//                if (folderPath.exists() && folderPath.isDirectory() && folderPath.canRead()) {
//                    
//                    //at this point we know the folder exists and we can read it; copy the names of its files
//                    retVal = folderPath.list();
//                    for (int i = 0; i < retVal.length; i++) {
//                        retVal[i] = folderName + File.separator + retVal[i];                        
//                    }
//                }
//            }
//            
//        } else if (getExecuteInputValidatorPane().getFilesJustLoadedRadioButton().isSelected()) {
//            
//            //read the MTSOVPane data table and return the files in that table (if any)
//            JTable inputDataFilesTable = getMultipleDataSetPane().getTestDataSetsListBox();
//            if (inputDataFilesTable != null) {
//                
//                TestCaseTableModel tableModel = (TestCaseTableModel) inputDataFilesTable.getModel();
//            
//                if (tableModel != null) {
//                    
//                    ProblemDataFiles pdf = tableModel.getFiles();
//                    
//                    if (pdf != null) {
//                        
//                        SerializedFile [] dataFiles = pdf.getJudgesDataFiles();
//                        
//                        if (dataFiles.length > 0 ) {
//                            
//                            //at this point we know there are judge's data files in the MTSOVPane table; copy their names
//                            retVal = new String [dataFiles.length];
//                            for (int file = 0; file < dataFiles.length; file++) {
//                                retVal[file] = dataFiles[file].getAbsolutePath();
//                            }
//                        }
//                    }
//                }
//            }
//            
//        } else if (getExecuteInputValidatorPane().getFilesPreviouslyLoadedRadioButton().isSelected()) {
//            
//            //reach into the Problem (if defined) and get the judge's data files (if any)
//            if (problem != null) {
//                
//                ProblemDataFiles pdf = getContest().getProblemDataFile(problem);
//                
//                if (pdf != null) {
//                    
//                    SerializedFile [] dataFiles = pdf.getJudgesDataFiles();
//                    
//                    if (dataFiles.length > 0 ) {
//                        
//                        //at this point we know there are judge's data files in the problem; copy their names
//                        retVal = new String [dataFiles.length];
//                        for (int file = 0; file < dataFiles.length; file++) {
//                            retVal[file] = dataFiles[file].getAbsolutePath();
//                        }
//                    }
//                }
//            }
//            
//        } else {
//            //none of the three radio buttons is selected
//            getLog().log(Log.SEVERE, "Error - no 'Input Data Files to Validate' button is selected (shouldn't be possible)");
//        }
//        
//        return retVal ;
//    }

    /**
     * Displays a JFileChooser allowing the user to select a directory.
     * 
     * Returns the selected directory name, or null if no directory was selected. 
     * Also updates the specified JTextField to contain the selected directory name.
     * 
     * @param dialogTitle a String giving the title to display on the dialog
     * 
     * @return a String giving the name of the chosen directory
     */
    @SuppressWarnings("unused")
    private String selectDirectory(JTextField textField, String dialogTitle) {

        String chosenDir = null;
        
        String startDir = null;
        if (textField != null) {
            String toolTip = textField.getToolTipText();
            if (toolTip != null && !toolTip.equals("")) {
                startDir = toolTip;
            }
        }

        JFileChooser chooser = new JFileChooser(startDir);

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (dialogTitle != null) {
            chooser.setDialogTitle(dialogTitle);
        }
        try {
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                chosenDir = chooser.getSelectedFile().toString();
                textField.setText(chosenDir);
            }
        } catch (Exception e) {
//            showMessage("Error selecting input folder, try again: \n" + e.getMessage());
            getController().getLog().log(Log.INFO, "Error in JFileChooser getting selected directory", e);
        }
        chooser = null;
        return chosenDir;
        
         
    }


    @Override
    public String getPluginTitle() {
        return "Run Input Validators Pane";
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
                            handleCloseButton();
                        }
                    });
                    listenersAdded = true;
                }
            }
        });
    }

//    /**
//     * Sets the "Run Input Validator" button enabled or disabled according to whether all attributes necessary to run the
//     * Input Validator have been specified.
//     */
//    private void updateRunValidatorButtonState() {
//        boolean enableButton = true ;
//        
//        //don't enable the button if there's no validator command defined
//        if (getDefineInputValidatorPane().getInputValidatorCommandTextField().getText() == null || 
//                getDefineInputValidatorPane().getInputValidatorCommandTextField().getText().equals("")) {
//            enableButton = false;
//        }
//        
//        //don't enable the button if "Files on disk in folder" is selected but there's no folder specified
//        if (getExecuteInputValidatorPane().getFilesOnDiskInFolderRadioButton().isSelected()) {
//            if (getExecuteInputValidatorPane().getInputValidatorFilesOnDiskTextField().getText() == null || 
//                    getExecuteInputValidatorPane().getInputValidatorFilesOnDiskTextField().getText().equals("")) {
//                enableButton = false;
//            }
//        }
//        
//        //don't enable the button if "Files just loaded via 'input data files' pane" is selected but there's no files on the MTSOV pane
//        if (getExecuteInputValidatorPane().getFilesJustLoadedRadioButton().isSelected()) {
//            if (getMultipleDataSetPane().getTestDataSetsListBox().getModel().getRowCount() <= 0) {
//                //there are no data rows in the MTSOVPane table
//                enableButton = false ;               
//            }
//        }
//        
//        // don't enable the button if "Files previously loaded into PC2" is selected but there's no data files loaded
//        if (getExecuteInputValidatorPane().getFilesPreviouslyLoadedRadioButton().isSelected()) {
//            
//            //make sure we have a problem from which we can possibly load data files
//            if (problem != null) {
//                
//                // make sure the problem in the contest model has data files
//                ProblemDataFiles pdf = getContest().getProblemDataFile(getProblem());
//                if (pdf != null) {
//                    //make sure the data files contain judge's answer files
//                    SerializedFile[] answerFiles = pdf.getJudgesAnswerFiles();
//                    if (answerFiles == null || answerFiles.length <= 0) {
//                        //problem has no judge's answer files; don't enable Run button
//                        enableButton = false;
//                    }
//                } else {
//                    //problem has no data files; don't enable Run button
//                    enableButton = false;
//                }
//            } else {
//                //problem is null; don't enable Run button
//                enableButton = false ;
//            }
//        }
//        
//        //set the button-enabled condition based on the above determinations
//        getExecuteInputValidatorPane().getRunInputValidatorButton().setEnabled(enableButton);
//        
//        //update the tooltip to match the current state
//        if (enableButton) {
//            getExecuteInputValidatorPane().getRunInputValidatorButton().setToolTipText("Run the defined Input Validator command using the specified set of Input Data files");
//        } else {
//            //there must be something blocking permission to run the input validator; set the tooltip to indicate the condition(s)
//            String toolTip = "";
//            
//            //check for the required validator command line
//            if (getDefineInputValidatorPane().getInputValidatorCommandTextField() == null || 
//                    getDefineInputValidatorPane().getInputValidatorCommandTextField().getText().equals("")) {
//                toolTip += "No Input Validator Command defined";
//            }
//            
//            //check whether, if files are coming from a disk folder, there is a folder defined
//            if (getExecuteInputValidatorPane().getFilesOnDiskInFolderRadioButton().isSelected()) {
//                if (getExecuteInputValidatorPane().getInputValidatorFilesOnDiskTextField().getText() == null || 
//                        getExecuteInputValidatorPane().getInputValidatorFilesOnDiskTextField().getText().equals("")) {
//                    if (toolTip.equals("")) {
//                        toolTip = "No Input File folder defined";
//                    } else {
//                        toolTip += "; no Input File folder defined"; 
//                    }
//                }
//            }
//            
//            //check whether, if files are coming from the Load Data Files pane, there are files in that pane
//            if (getExecuteInputValidatorPane().getFilesJustLoadedRadioButton().isSelected()) {
//                if (getMultipleDataSetPane().getTestDataSetsListBox().getRowCount() <= 0) {
//                    if (toolTip.equals("")) {
//                        toolTip = "No Input Files defined on Input Data Files pane";
//                    } else {
//                        toolTip += "; no Input Files defined on Input Data Files pane"; 
//                    }
//                }
//            }
//            
//            //check whether, if files are coming from those previously loaded, there actually ARE files loaded
//            if (getExecuteInputValidatorPane().getFilesPreviouslyLoadedRadioButton().isSelected()) {
//                
//                //make sure we have a problem from which we can possibly load data files
//                if (problem == null || getContest().getProblemDataFile(getProblem()) == null 
//                        || getContest().getProblemDataFile(getProblem()).getJudgesDataFiles() == null
//                        || getContest().getProblemDataFile(getProblem()).getJudgesDataFiles().length <= 0) {
//                    if (toolTip.equals("")) {
//                        toolTip = "No Input Files saved in current problem";
//                    } else {
//                        toolTip += "; no Input Files saved in current problem"; 
//                    }
//                }
//            }
//            
//            if (!toolTip.equals("")) {
//                toolTip += "; cannot run Input Validator";
//            } else {
//                //we shouldn't be able to get here; the tooltip shouldn't be empty if 'enable' was false (there must be some condition
//                // suppressing the enable, so why didn't we pick it up in the above set of 'if' statements??)
//                getLog().log(Log.WARNING, "Empty Run Validator button tooltip when this shouldn't be possible");
//            }
//            
//            getExecuteInputValidatorPane().getRunInputValidatorButton().setToolTipText(toolTip);
//        }
//
//    }
}
