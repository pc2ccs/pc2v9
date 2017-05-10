package edu.csus.ecs.pc2.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.ExecuteException;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.Problem.InputValidationStatus;
import edu.csus.ecs.pc2.core.model.inputValidation.InputValidationResult;
import edu.csus.ecs.pc2.core.model.inputValidation.InputValidationResultsTableModel;
import edu.csus.ecs.pc2.validator.inputValidator.InputValidatorRunner;

/**
 * This class defines a plugin pane (a JPanel) containing components for 
 * (1) defining an Input Validator program name and Invocation Command, and
 * (2) Viewing the results of an input validator execution.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class InputValidatorPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;

    private DefineInputValidatorPane defineInputValidatorPane;

    private InputValidationResultPane inputValidationResultPane;
    
    private boolean inputValidatorHasBeenRun;

    private InputValidationStatus inputValidationStatus = InputValidationStatus.NOT_TESTED;

    private Component verticalStrut_1;
    private Component verticalStrut_3;
    private Component verticalStrut_4;

    private JPanePlugin parentPane;
    private JButton runInputValidatorButton;
    private Component verticalStrut_5;

    private InputValidationResult[] runResults;

    private InputValidationResult[] accumulatingResults;


    public InputValidatorPane() {
        
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(getVerticalStrut_4());

        this.add(getDefineInputValidatorPane());
        add(getVerticalStrut_5());
        add(getRunInputValidatorButton());
        this.add(getVerticalStrut_1());
        this.add(getInputValidationResultPane());
        this.add(getVerticalStrut_3());
    }
    
    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        getDefineInputValidatorPane().setContestAndController(inContest, inController);
        getInputValidationResultPane().setContestAndController(inContest, inController);
    }
    
    /**
     * Returns the Input Validator Program Name currently entered in this InputValidatorPane.
     * 
     * @return a String containing the Input Validator Program Name
     */
    public String getInputValidatorProgramName() {
        String progName = getDefineInputValidatorPane().getInputValidatorProgramName();
        return progName;
    }
    
    /**
     * Sets the Input Validator Program name displayed in this InputValidatorPane to the specified text.
     * 
     * @param inputValidatorProg a String containing the Input Validator Program name
     */
    public void setInputValidatorProgramName(String progName) {
       getDefineInputValidatorPane().setInputValidatorProgramName(progName);

    }
    
    /**
     * Returns the Input Validator Command currently entered into this InputValidatorPane.
     * 
     * @return a String containing the command to be used to invoke the Input Validator
     */
    public String getInputValidatorCommand() {
        String command = getDefineInputValidatorPane().getInputValidatorCommand();
        return command;
    }

    private DefineInputValidatorPane getDefineInputValidatorPane() {
        if (defineInputValidatorPane == null) {
            defineInputValidatorPane = new DefineInputValidatorPane();
            defineInputValidatorPane.setContestAndController(this.getContest(), this.getController());
            defineInputValidatorPane.setParentPane(this);
        }
        return defineInputValidatorPane;
    }
    

    private InputValidationResultPane getInputValidationResultPane() {
        if (inputValidationResultPane == null) {
            inputValidationResultPane = new InputValidationResultPane();
            inputValidationResultPane.setContestAndController(this.getContest(), this.getController());
            inputValidationResultPane.setParentPane(this);
        }
        return inputValidationResultPane;
    }

    private Component getVerticalStrut_1() {
        if (verticalStrut_1 == null) {
            verticalStrut_1 = Box.createVerticalStrut(20);
        }
        return verticalStrut_1;
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
    
    @Override
    public String getPluginTitle() {

        return "Input Validator Pane";
    }

    public void setParentPane(JPanePlugin parentPane) {
        this.parentPane = parentPane; 
    }
    
    public JPanePlugin getParentPane() {
        return this.parentPane;
    }

    /**
     * Sets the ToolTip text for the InputValidatorProgramName displayed in this InputValidatorPane.
     * 
     * @param text the ToolTip text to set
     */
    public void setInputValidatorProgramNameToolTipText(String text) {
        getDefineInputValidatorPane().setInputValidatorProgramNameToolTipText(text);
        
    }

    /**
     * Sets the Input Validator Command displayed in this InputValidatorPane to the specified value.
     * 
     * @param command a String containing the command used to invoke the Input Validator
     */
    public void setInputValidatorCommand(String command) {
        getDefineInputValidatorPane().setInputValidatorCommand(command);
        
    }

    /**
     * Sets the ToolTip text for the Input Validator Command displayed in this InputValidatorPane.
     * 
     * @param text the ToolTip text to set
     */
    public void setInputValidatorCommandToolTipText(String text) {
        getDefineInputValidatorPane().setInputValidatorCommandToolTipText(text);
        
    }

    private JButton getRunInputValidatorButton() {
        if (runInputValidatorButton == null) {
        	runInputValidatorButton = new JButton("Run Input Validator");
        	runInputValidatorButton.addActionListener(new ActionListener() {
        	    
        	    public void actionPerformed(ActionEvent e) {

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run () {
                            if (okToRunInputValidator()) {
                                
                                getRunInputValidatorButton().setEnabled(false);  //this is set back true when the spawner finishes, via a call to cleanup()
                                getShowOnlyFailedFilesCheckbox().setEnabled(false);
                                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                
                                spawnInputValidatorRunnerThread();
                                
                            } else {
                                JOptionPane.showMessageDialog(null, "Missing Input Validator Command or Judge's Data files; cannot run Input Validator", 
                                        "Missing Data", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    });
        	    }
        	});
        }
        return runInputValidatorButton;
    }
    
    /**
     * Verifies that all conditions necessary to run the Input Validator associated with this InputValidatorPane are true.
     * These include that there is an Input Validator Command line and that there are Input Data Files on which the command can operate.
     * 
     * @return true if it is ok to run the Input Validator; false if not
     */
    private boolean okToRunInputValidator() {
        if (problemHasInputValidatorCommand() && problemHasInputDataFiles()) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Verifies that this InputValidatorPane has a command for running the Input Validator.
     * 
     * @return true if the Input Validator command is a non-null, non-empty String; false otherwise
     */
    private boolean problemHasInputValidatorCommand() {
        if (getInputValidatorCommand() != null && !getInputValidatorCommand().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Verifies that there are judge's data files against which we can run an Input Validator.
     * 
     * @return true if there is one or more Judge's Data Files stored in the Data Files pane
     */
    private boolean problemHasInputDataFiles() {
        JPanePlugin parent = getParentPane();
        if (parent != null && parent instanceof EditProblemPane) {
            EditProblemPane epp = (EditProblemPane) parent;
            // check for data files on the Input Data Files tab
            if (epp.getMultipleDataSetPane().getProblemDataFiles().getJudgesDataFiles().length > 0) {
                return true;
            } else {
                // there's no data files on the Input Data Files tab; check to see if they've entered a single data file on the General tab
                if (epp.inputDataFileLabel != null && epp.inputDataFileLabel.getText() != null && epp.inputDataFileLabel.getText().equals("")) {
                    return true;
                }
            }
        }
        //no data files were found anywhere (or the parent isn't an EditProblemPane)
        return false;
    }

    /**
     * Spawns a separate {@link SwingWorker} thread to run the Input Validator.
     * 
     * The worker thread publishes each separate InputValidationResult as it is generated; each published result
     * is automatically picked up and handled by the worker's process() method.  Once the worker thread completes it 
     * assigns the results to a global variable which is accessible by external clients via an accessor.
     * 
     * See https://docs.oracle.com/javase/tutorial/uiswing/concurrency/interim.html for details on how SwingWorker threads publish results.
     */
    private void spawnInputValidatorRunnerThread() {

        //define a SwingWorker thread to run the Input Validator in the background against each of the data files
        SwingWorker<InputValidationResult[], InputValidationResult> worker = new SwingWorker<InputValidationResult[], InputValidationResult>() {

            /**
             * Method doInBackground() is invoked when the Worker thread's execute() method is called (which happens below, after the worker thread has been constructed).
             * The method runs the Input Validator in the background against all the judge's input data files currently defined on the EditProblemPane's 
             * Input Data Files pane, publishing each result as it finishes.  When the method is finished it returns an array of all InputValidationResults; 
             * this array is accessible by the Worker thread's done() method (via a call to get()).
             */
            @Override
            public InputValidationResult[] doInBackground() throws Exception {

                //determine what Type will receive the results
                JPanePlugin parent = getParentPane();
                
                if (parent instanceof EditProblemPane) {
                    
                    //we're going to publish results to an EditProblemPane
                    EditProblemPane epp = (EditProblemPane) parent;
                    
                    //get the data files from the EditProblemPane's Input Data Files tab
                    SerializedFile[] dataFiles = epp.getMultipleDataSetPane().getProblemDataFiles().getJudgesDataFiles();
                    
                    // make sure we got some data files
                    boolean found = false;
                    if (dataFiles != null && dataFiles.length > 0) {
                        found = true;
                    } else {

                        // no files found on the Input Data Files tab; see if perhaps there is a single data file name defined on the General tab
                        if (epp.inputDataFileLabel != null && !epp.inputDataFileLabel.getText().equals("")) {

                            // there is a file name on the General tab; try getting that file (whose full name is stored in the ToolTip)
                            String fileName = epp.inputDataFileLabel.getToolTipText();
                            if (fileName != null && !fileName.equals("")) {

                                try {
                                    SerializedFile sf = new SerializedFile(fileName);

                                    // check for serialization error (which will throw any exception found in the SerializedFile)
                                    if (Utilities.serializedFileError(sf)) {
                                        getController().getLog().warning("Error obtaining SerializedFile for data file ' " + fileName
                                                + " ' -- cannot run Input Validator");
                                        System.err.println("Error obtaining SerializedFile for data file ' " + fileName + " ' (from General tab)");
                                        return null;
                                    } else {
                                        // we got a valid SerializedFile from the General tab; use that
                                        dataFiles = new SerializedFile[1];
                                        dataFiles[1] = sf;
                                        found = true;
                                    }
                                } catch (Exception e) {
                                    getController().getLog().warning("Exception obtaining SerializedFile for data file ' " + fileName + " ' : " + e.getMessage());
                                    System.err.println("Exception obtaining SerializedFile for data file ' " + fileName + " ' : " + e.getMessage());
                                    return null;
                                }
                            }

                        }
                    }

                    if (!found) {
                        // we found no data files on either tab
                        getController().getLog().warning("No data files found -- cannot run Input Validator");
                        System.err.println("Warning: No data files found -- cannot run Input Validator");
                        return null;
                    }

                    //if we get here we know we have data files to process...
                    
                    //create an array to hold the results as they are created
                    InputValidationResult[] validationResults = new InputValidationResult[dataFiles.length];

                    //get the name of the Input Validator Program to be run
                    String valProgName = getInputValidatorProgramName();
                    
                    //create a SerializedFile for the validator program
                    SerializedFile validatorProg;
                    try {
                        validatorProg = new SerializedFile(valProgName);

                        // check for serialization error (which will throw any exception found in the SerializedFile)
                        if (Utilities.serializedFileError(validatorProg)) {
                            getController().getLog().warning("Error obtaining SerializedFile for validator program file ' " + validatorProg
                                    + " ' -- cannot run Input Validator");
                            System.err.println("Error obtaining SerializedFile for validator program file ' " + validatorProg + " '");
                            return null;
                        } 
                    } catch (Exception e) {
                        getController().getLog().warning("Exception obtaining SerializedFile for validator program file ' " + valProgName + " ' : " + e.getMessage());
                        System.err.println("Exception obtaining SerializedFile for validator program file ' " + valProgName + " ' : " + e.getMessage());
                        return null;
                    }
                    
                    //get the problem for which the data files apply
                    Problem prob = epp.getProblem();
                    
                    //get the execution directory being used by the EditProblemPane 
                    String executeDir = epp.getExecuteDirectoryName();

                    //clear the results table in preparation for adding new results
                    ((InputValidationResultsTableModel)getInputValidationResultPane().getInputValidatorResultsTable().getModel()).setResults(null);
                    ((AbstractTableModel) getInputValidationResultPane().getInputValidatorResultsTable().getModel()).fireTableDataChanged();
                    
                    //clear the result accumulator
                    accumulatingResults = null;
                   
                    //run the Input Validator on each data file
                    for (int fileNum = 0; fileNum < dataFiles.length; fileNum++) {
                        
                        //get the next data file
                        SerializedFile dataFile = dataFiles[fileNum];
                        
                        //should figure out how to run each of these calls on a separate thread and still return intermediate results...
                        // this might need to be done here, or else in method runInputValidator()...
                        // One thing to worry about:  each call to runInputValidator() uses the (same?) execute directory; that can cause collisions
                        //  with the files which are being created therein (e.g. the stdout.pc2 and stderr.pc2 files)
                        try {
                            //run the input validator
                            validationResults[fileNum] = runInputValidator(prob, validatorProg, getInputValidatorCommand(), dataFile, executeDir);
                            
                            inputValidatorHasBeenRun = true;
                            epp.enableUpdateButton();
                            
                            //publish the validator result for the current data file, to be picked up by the process() method (below)
                            publish(validationResults[fileNum]);

                        } catch (Exception e) {
                            getController().getLog().warning("Exception running Input Validator ' " + validatorProg.getName() + " ' : " + e.getMessage());
                        }
                    }
                    
                    return validationResults;
                    
                } else {
                    //the parent is not an EditProblemPane; the current code doesn't support returning results to any other Type
                    getController().getLog().warning("Attempted to return Input Validator results to a " + parent.getClass() 
                            + "; currently only EditProblemPane is supported for receiving results");
                    System.err.println ("Warning: attempt to assign Input Validator results to an unsupported type: " + parent.getClass());
                    return null;
                }

            }

            /**
             * This method is called by the Worker thread's publish() method each time one or more results are finished.
             * The input to the method is a list of results which have been completed since the last call to this method.
             * This method adds each published result to the results table, by calling addToResultTable().
             */
            @Override
            public void process(List<InputValidationResult> resultList) {
                //display the results (which may be partial) in the InputValidatorPane's InputValidationResults table
                
                for (InputValidationResult result : resultList) {
                    if (!getInputValidationResultPane().getShowOnlyFailedFilesCheckbox().isSelected() || !result.isPassed()) {
                        addResultToTable(result);                        
                    }
                    addResultToAccumulatedList(result);
                    updateInputValidationSummaryText(accumulatingResults);
//                    addResultToProblem(result);   //we don't want to do this until Add/Update is pressed
//                    updateProblemValidationStatus(result);    // ditto ""
                }
            }
            
            /**
             * This method is invoked by the Worker thread when it is completely finished with its doInBackground task(s). 
             * Calling get() fetches the set of data returned by the Worker thread's doInBackground() method -- that is,
             * an array of InputValidationResults.  This method saves those results so they can be accessed by external code
             * via the {@link #getRunResults()} method.  The method also updates the GUI based on the results, and finally 
             * calls cleanup() to restore the GUI state.
             */
            @Override
            public void done() {
                try {
                    runResults = get();
                    if (runResults != null && runResults.length > 0) {
                        updateInputValidationSummaryText(runResults);
                        updateInputValidationStatus(runResults);                        
                    }
                } catch (InterruptedException e) {
                    getController().getLog().warning("Exception in SwingWorker.done(): " + e.getMessage());
                }
                catch (java.util.concurrent.ExecutionException e) {
                    String why = null;
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        why = cause.getMessage();
                    } else {
                        why = e.getMessage();
                    }
                    System.err.println("Error retrieving validation results: " + why);
                    getController().getLog().warning("Exception in SwingWorker.done(): " + e.getMessage());
                } finally {
                    cleanup();
                }
            }

        };  //end of SwingWorker definition

        //start the SwingWorker thread running the Input Validator in the background against all data files
        worker.execute();
        
    } //end method spawnInputValidatorRunnerThread()
    
    /**
     * This method is called when spawnInputValidatorRunnerThread() finishes.
     * Its job is to restore the GUI state, including resetting the cursor and reenabling the RunInputValidator button.
     */
    private void cleanup() {
        getRunInputValidatorButton().setEnabled(true);
        getShowOnlyFailedFilesCheckbox().setEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Adds the specified Input Validation execution result to the Input Validation Results table in this InputValidatorPane.
     * 
     * @param result the result to be added to the table
     */
    private void addResultToTable(InputValidationResult result) {
        
        ((InputValidationResultsTableModel)getInputValidationResultPane().getInputValidatorResultsTable().getModel()).addRow(result);
        ((InputValidationResultsTableModel)getInputValidationResultPane().getInputValidatorResultsTable().getModel()).fireTableDataChanged();
        
    }
    
    private void addResultToAccumulatedList(InputValidationResult newResult) {
        if (accumulatingResults == null || accumulatingResults.length <= 0) {
            accumulatingResults = new InputValidationResult [1];
            accumulatingResults[0] = newResult;
        } else {
            InputValidationResult [] temp = new InputValidationResult [accumulatingResults.length+1];
            int i=0;
            for (InputValidationResult res : accumulatingResults) {
                temp[i++] = res;
            }
            temp[i] = newResult;
            accumulatingResults = temp;
        }
    }
    
    /**
     * Updates the Input Validation Results summary message to match the results contained in the received array of {@link InputValidationResult}s. 
     */
    private void updateInputValidationSummaryText(InputValidationResult [] runResults) {
        getInputValidationResultPane().updateInputValidationStatusMessage(runResults);
    }
    
    /**
     * Updates the Input Validator Run Status flag in this InputValidatorPane.  Note that this does NOT update the Problem; just the local
     * status flag.  The Problem only gets updated when Add or Update is pressed on the EditProblemPane.
     * 
     * If the received InputValidationResult array is null or empty, no change is made in the current status.
     * Otherwise, if all the results in the array are "Passed" then the status is set to Passed; if one or more results in
     * the array are "Failed" then the status is set to Failed.
     * 
     * @param runResults an array of InputValidationResults from having run an Input Validator
     */
    private void updateInputValidationStatus (InputValidationResult [] runResults) {
        
        if (runResults != null && runResults.length > 0) {

            boolean foundFailure = false;
            for (InputValidationResult res : runResults) {
                if (!res.isPassed()) {
                    foundFailure = true;
                    break;
                }
            }
            
            if (foundFailure) {
                setInputValidationStatus(InputValidationStatus.FAILED);
            } else {
                setInputValidationStatus(InputValidationStatus.PASSED);        }
            }
    }
    
    protected void setInputValidationStatus(InputValidationStatus newStatus) {
        this.inputValidationStatus = newStatus;
    }
    
    /**
     * Returns the {@link InputValidationStatus} for this {@link InputValidatorPane}.  The returned value indicates whether an
     * Input Validator has been run, and if so whether all runs passed or not.
     * 
     * @return the Input Validation Status for this pane
     */
    protected InputValidationStatus getInputValidationStatus() {
        return this.inputValidationStatus;
    }
    
    
    
    /**
     * Runs the specified Input Validator Program using the specified parameters.
     * 
     * NOTE: TODO: this should be done on a separate thread. However, see the note in spawnInputValidatorRunnerThread() about collisions in folders...
     * 
     * @param problem the Contest Problem associated with the Input Validator
     * @param validatorProg the Input Validator Program to be run
     * @param validatorCommand the command used to run the Input Validator Program
     * @param dataFile the data file to be passed to the Input Validator as input to be validated
     * @param executeDir the execution directory to be used (in which to run the Input Validator Program
     * 
     * @return an InputValidationResult
     * 
     * @throws ExecutionException if an ExecutionException occurs during execution of the Input Validator
     * @throws Exception if an Exception other than ExecutionException occurs during execution of the Input Validator
     */
    private InputValidationResult runInputValidator(Problem problem, SerializedFile validatorProg, String validatorCommand, SerializedFile dataFile, String executeDir) throws Exception {

        InputValidatorRunner runner = new InputValidatorRunner(getContest(), getController());
        InputValidationResult result = null;
        try {
            result = runner.runInputValidator(problem, validatorProg, validatorCommand, executeDir, dataFile);
        } catch (ExecuteException e) {
            getController().getLog().warning("Exeception executing Input Validator: " + e.getMessage());
            throw e;
        } catch (Exception e1) {
            getController().getLog().warning("Exeception executing Input Validator: " + e1.getMessage());
            throw e1;
        }
        
        return result;
    }
        

    private Component getVerticalStrut_5() {
        if (verticalStrut_5 == null) {
        	verticalStrut_5 = Box.createVerticalStrut(20);
        }
        return verticalStrut_5;
    }

    /**
     * @return the inputValidatorHasBeenRun
     */
    public boolean isInputValidatorHasBeenRun() {
        return inputValidatorHasBeenRun;
    }

    /**
     * @param inputValidatorHasBeenRun the inputValidatorHasBeenRun to set
     */
    public void setInputValidatorHasBeenRun(boolean inputValidatorHasBeenRun) {
        this.inputValidatorHasBeenRun = inputValidatorHasBeenRun;
    }

    public InputValidationResult[] getRunResults() {
        return this.runResults ;
    }

    public void setRunResults(InputValidationResult[] inRunResults) {
        this.runResults = inRunResults;
    }

    public void setInputValidationSummaryMessageText(String msg) {
        getInputValidationResultPane().setInputValidationSummaryMessageText(msg);
    }

    public void setInputValidationSummaryMessageColor(Color color) {
        getInputValidationResultPane().setInputValidationSummaryMessageColor(color);
    }

    public void updateInputValidationStatusMessage(InputValidationResult[] results) {
        getInputValidationResultPane().updateInputValidationStatusMessage(results);
    }

    public InputValidationResultsTableModel getResultsTableModel() {
        return (InputValidationResultsTableModel) getInputValidationResultPane().getInputValidatorResultsTable().getModel();
    }
    
    public JCheckBox getShowOnlyFailedFilesCheckbox() {
        return getInputValidationResultPane().getShowOnlyFailedFilesCheckbox();
    }

    public void updateResultsTable() {
        getInputValidationResultPane().updateResultsTable(runResults);
    }
}
