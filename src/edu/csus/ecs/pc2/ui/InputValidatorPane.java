package edu.csus.ecs.pc2.ui;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.execute.ExecuteException;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.inputValidation.InputValidationResult;
import edu.csus.ecs.pc2.core.model.inputValidation.InputValidationResultsTableModel;
import edu.csus.ecs.pc2.validator.inputValidator.InputValidatorRunner;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;

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

    private Component verticalStrut_1;
    private Component verticalStrut_3;
    private Component verticalStrut_4;

    private JPanePlugin parentPane;
    private JButton runInputValidatorButton;
    private Component verticalStrut_5;


    public InputValidatorPane() {
        
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(getVerticalStrut_4());

        this.add(getDefineInputValidatorPanel());
        add(getVerticalStrut_5());
        add(getRunInputValidatorButton());
        this.add(getVerticalStrut_1());
        this.add(getInputValidationResultPanel());
        this.add(getVerticalStrut_3());
    }
    
    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        getDefineInputValidatorPanel().setContestAndController(inContest, inController);
        getInputValidationResultPanel().setContestAndController(inContest, inController);
    }
    
    /**
     * Returns the Input Validator Program Name currently entered in this InputValidatorPane.
     * 
     * @return a String containing the Input Validator Program Name
     */
    public String getInputValidatorProgramName() {
        String progName = getDefineInputValidatorPanel().getInputValidatorProgramName();
        return progName;
    }
    
    /**
     * Sets the Input Validator Program name displayed in this InputValidatorPane to the specified text.
     * 
     * @param inputValidatorProg a String containing the Input Validator Program name
     */
    public void setInputValidatorProgramName(String progName) {
       getDefineInputValidatorPanel().setInputValidatorProgramName(progName);

    }
    
    /**
     * Returns the Input Validator Command currently entered into this InputValidatorPane.
     * 
     * @return a String containing the command to be used to invoke the Input Validator
     */
    public String getInputValidatorCommand() {
        String command = getDefineInputValidatorPanel().getInputValidatorCommand();
        return command;
    }

    private DefineInputValidatorPane getDefineInputValidatorPanel() {
        if (defineInputValidatorPane == null) {
            defineInputValidatorPane = new DefineInputValidatorPane();
            defineInputValidatorPane.setContestAndController(this.getContest(), this.getController());
            defineInputValidatorPane.setParentPane(this);
        }
        return defineInputValidatorPane;
    }
    

    private InputValidationResultPane getInputValidationResultPanel() {
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
        getDefineInputValidatorPanel().setInputValidatorProgramNameToolTipText(text);
        
    }

    /**
     * Sets the Input Validator Command displayed in this InputValidatorPane to the specified value.
     * 
     * @param command a String containing the command used to invoke the Input Validator
     */
    public void setInputValidatorCommand(String command) {
        getDefineInputValidatorPanel().setInputValidatorCommand(command);
        
    }

    /**
     * Sets the ToolTip text for the Input Validator Command displayed in this InputValidatorPane.
     * 
     * @param text the ToolTip text to set
     */
    public void setInputValidatorCommandToolTipText(String text) {
        getDefineInputValidatorPanel().setInputValidatorCommandToolTipText(text);
        
    }

    private JButton getRunInputValidatorButton() {
        if (runInputValidatorButton == null) {
        	runInputValidatorButton = new JButton("Run Input Validator");
        	runInputValidatorButton.addActionListener(new ActionListener() {
        	    
        	    public void actionPerformed(ActionEvent e) {

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run () {
                            if (okToRunInputValidator()) {
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
            EditProblemPane epp = (EditProblemPane) parent ;
            if (epp.getMultipleDataSetPane().getProblemDataFiles().getJudgesDataFiles().length>0) {
                return true;
            }
        } 
        return false;
    }

    /**
     * Spawns a separate {@link SwingWorker} thread to run the Input Validator.
     * 
     * The worker thread publishes each separate InputValidationResult as it is generated; each published result
     * is automatically picked up and handled by the worker's process() method.
     * 
     * See https://docs.oracle.com/javase/tutorial/uiswing/concurrency/interim.html for details on how SwingWorker threads publish results.
     */
    private void spawnInputValidatorRunnerThread() {

        SwingWorker<InputValidationResult[], InputValidationResult> worker = new SwingWorker<InputValidationResult[], InputValidationResult>() {

            /**
             * This method is invoked when the Worker thread's execute() method is called.  The method runs the Input Validator in the background
             * against all the judge's input data files currently defined on the EditProblemPane's Input Data Files pane, publishing each result
             * as it finishes.
             */
            @Override
            public InputValidationResult[] doInBackground() throws Exception {

                //determine what Type will receive the results
                JPanePlugin parent = getParentPane();
                
                if (parent instanceof EditProblemPane) {
                    
                    //we're going to publish results to an EditProblemPane
                    EditProblemPane editProbPane = (EditProblemPane) parent;
                    
                    //get each of the data files from the EditProblemPane's Input Data Files tab
                    SerializedFile[] dataFiles = editProbPane.getMultipleDataSetPane().getProblemDataFiles().getJudgesDataFiles();

                    //an array to hold the results as they are created
                    final InputValidationResult[] validationResults = new InputValidationResult[dataFiles.length];

                    //get the Input Validator Program to be run
                    SerializedFile validatorProg = new SerializedFile(getInputValidatorProgramName());
                    
                    //get the problem for which the data files apply
                    Problem prob = editProbPane.getProblem();
                    
                    //get the execution directory being used by the EditProblemPane 
                    String executeDir = editProbPane.getExecuteDirectoryName();


                    //clear the results table in preparation for adding new results
                    ((InputValidationResultsTableModel)getInputValidationResultPanel().getInputValidatorResultsTable().getModel()).setResults(null);
                    ((AbstractTableModel) getInputValidationResultPanel().getInputValidatorResultsTable().getModel()).fireTableDataChanged();
                   
                    //run the Input Validator on each data file
                    for (int fileNum = 0; fileNum < dataFiles.length; fileNum++) {
                        
                        //get the next data file
                        SerializedFile dataFile = dataFiles[fileNum];
                        
                        //should figure out how to run each of these calls on a separate thread and still return intermediate results...
                        // this might need to be done here, or else in method runInputValidator()...
                        validationResults[fileNum] = runInputValidator(prob, validatorProg, getInputValidatorCommand(), dataFile, executeDir);
                        
                        //publish the validator result for the current data file, to be picked up by the process() method (below)
                        publish(validationResults[fileNum]);

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

            //this code would be used if we were going to wait for the entire batch of InputValidationResults to be finished;
            // however, we're expecting results to be "published" as they are generated
//            @Override
//            public void done() {
//                try {
//                    validationResults = get();
//                } catch (InterruptedException ignore) {
//                }
//                catch (java.util.concurrent.ExecutionException e) {
//                    String why = null;
//                    Throwable cause = e.getCause();
//                    if (cause != null) {
//                        why = cause.getMessage();
//                    } else {
//                        why = e.getMessage();
//                    }
//                    System.err.println("Error retrieving validation results: " + why);
//                }
//            }
            
            
            /**
             * This method is called by the Worker thread's publish() method each time one or more results are finished.
             * The input to the method is a list of results which have been completed since the last call to this method.
             * This method adds each published result to the results table, by calling addToResultTable().
             */
            @Override
            public void process(List<InputValidationResult> resultList) {
                //display the results (which may be partial) in the InputValidatorPane's InputValidationResults table
                
                for (InputValidationResult result : resultList) {
                    addToResultTable(result);
                }
            }
        };

        worker.execute();
    }
    
    /**
     * Adds the specified Input Validation execution result to the Input Validation Results table in this InputValidatorPane.
     * 
     * @param result the result to be added to the table
     */
    private void addToResultTable(InputValidationResult result) {
        
        ((InputValidationResultsTableModel)getInputValidationResultPanel().getInputValidatorResultsTable().getModel()).addRow(result);
        ((InputValidationResultsTableModel)getInputValidationResultPanel().getInputValidatorResultsTable().getModel()).fireTableDataChanged();
        
    }
    
    
    /**
     * Runs the specified Input Validator Program using the specified parameters.
     * 
     * NOTE: TODO: this should be done on a separate thread.
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
}
