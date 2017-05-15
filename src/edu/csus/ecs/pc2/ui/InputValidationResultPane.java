package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Problem.InputValidationStatus;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.inputValidation.InputValidationResult;
import edu.csus.ecs.pc2.core.model.inputValidation.InputValidationResultsTableModel;
import edu.csus.ecs.pc2.ui.cellRenderer.LinkCellRenderer;
import edu.csus.ecs.pc2.ui.cellRenderer.PassFailCellRenderer;

/**
 * This class defines a JPanel for displaying the results of running an Input Validator on a set of Input Data files
 * (Judge's input data).
 * 
 * @author John
 *
 */
public class InputValidationResultPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;
    
    private JPanel inputValidationResultSummaryPanel;

    private JLabel inputValidationResultsSummaryLabel;

    private JLabel inputValidationResultSummaryTextLabel;

    private Component verticalStrut_1;

    private Component verticalStrut_2;

    private JPanel inputValidationResultDetailsPanel;

    private JScrollPane resultsScrollPane;

    private JTable resultsTable;
    
    private InputValidationResultsTableModel inputValidationResultsTableModel = new InputValidationResultsTableModel();
    
    /**
     * list of columns
     */
    enum COLUMN {
        FILE_NAME, RESULT, VALIDATOR_OUTPUT, VALIDATOR_ERR
    };

    // define the column headers for the table of results
    private String[] columnNames = { "File", "Result", "Validator StdOut", "Validator StdErr" };


    private JPanePlugin parentPane;
    private JCheckBox showOnlyFailedRunsCheckbox;
    private Component horizontalStrut_1;
    
    
    public InputValidationResultPane() {

        this.setBorder(new TitledBorder(null, "Input Validation Results", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(getVerticalStrut_1());
        this.add(getInputValidationResultSummaryPanel());
        this.add(getVerticalStrut_2());
        this.add(getInputValidationResultDetailsPanel());

    }

    
    private JPanel getInputValidationResultSummaryPanel() {
        if (inputValidationResultSummaryPanel == null) {
            inputValidationResultSummaryPanel = new JPanel();
            inputValidationResultSummaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            FlowLayout flowLayout = (FlowLayout) inputValidationResultSummaryPanel.getLayout();
            flowLayout.setHgap(10);
            flowLayout.setAlignment(FlowLayout.LEFT);
            inputValidationResultSummaryPanel.add(getInputValidationResultsSummaryLabel());
            inputValidationResultSummaryPanel.add(getInputValidationResultSummaryTextLabel());
            inputValidationResultSummaryPanel.add(getHorizontalStrut_1());
            inputValidationResultSummaryPanel.add(getShowOnlyFailedFilesCheckbox());
        }
        return inputValidationResultSummaryPanel;
    }
    
    private JLabel getInputValidationResultsSummaryLabel() {
        if (inputValidationResultsSummaryLabel == null) {
            inputValidationResultsSummaryLabel = new JLabel("Most Recent Status: ");
        }
        return inputValidationResultsSummaryLabel;
    }
    
    private JLabel getInputValidationResultSummaryTextLabel() {
        if (inputValidationResultSummaryTextLabel == null) {
            inputValidationResultSummaryTextLabel = new JLabel("<No Input Validation test run yet>");
            inputValidationResultSummaryTextLabel.setForeground(Color.black);
        }
        return inputValidationResultSummaryTextLabel;
    }

    private JPanel getInputValidationResultDetailsPanel() {
        if (inputValidationResultDetailsPanel == null) {
            inputValidationResultDetailsPanel = new JPanel();
            inputValidationResultDetailsPanel.setLayout(new BorderLayout(0, 0));
            inputValidationResultDetailsPanel.add(getInputValidatorResultsScrollPane(), BorderLayout.CENTER);           
        }
        return inputValidationResultDetailsPanel;
    }
    
    private JScrollPane getInputValidatorResultsScrollPane() {
        if (resultsScrollPane == null) {
            resultsScrollPane = new JScrollPane();
            resultsScrollPane.setViewportView(getInputValidatorResultsTable());
        }
        return resultsScrollPane;
    }
    
    protected JTable getInputValidatorResultsTable() {
        if (resultsTable == null) {
            resultsTable = new JTable(inputValidationResultsTableModel);
            
            //set the desired options on the table
            resultsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            resultsTable.setFillsViewportHeight(true);
            resultsTable.setRowSelectionAllowed(false);
            resultsTable.getTableHeader().setReorderingAllowed(false);

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

            //render file-name, std-out, and std-err file names as clickable links
            resultsTable.getColumn(columnNames[COLUMN.FILE_NAME.ordinal()]).setCellRenderer(new LinkCellRenderer());
            resultsTable.getColumn(columnNames[COLUMN.VALIDATOR_OUTPUT.ordinal()]).setCellRenderer(new LinkCellRenderer());
            resultsTable.getColumn(columnNames[COLUMN.VALIDATOR_ERR.ordinal()]).setCellRenderer(new LinkCellRenderer());

            // add a listener to allow users to click an output or data file name and display it
            resultsTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    JTable targetTable = (JTable) e.getSource();
                    int row = targetTable.getSelectedRow();
                    int column = targetTable.getSelectedColumn();
                    
                    if (column == COLUMN.FILE_NAME.ordinal() || column == COLUMN.VALIDATOR_OUTPUT.ordinal() || column == COLUMN.VALIDATOR_ERR.ordinal()) {
                        viewFiles(targetTable, row);
                    } 
                }
            });

            // change the header font
            JTableHeader header = resultsTable.getTableHeader();
            header.setFont(new Font("Dialog", Font.BOLD, 12));
            
            // render Result column as Pass/Fail on Green/Red background
            resultsTable.getColumn("Result").setCellRenderer(new PassFailCellRenderer());


        }
        return resultsTable;
    }


    /**
     * Displays the files listed in the Input Validation Results table File, StdOut, and StdErr columns in a single MultiFileViewer frame.
     * 
     * 
     * @param table the JTable from which the data is obtained
     * @param row the table row whose data is to be displayed
     */
    private void viewFiles(JTable table, int row) {

        //get the data file from the table
        int col = table.getColumn(columnNames[COLUMN.FILE_NAME.ordinal()]).getModelIndex();
        SerializedFile dataFile = getFileForTableCell(table,row,col);
        if (dataFile == null) {
            System.err.println("Got a null SerializedFile for Input Validator Results data file (table cell (" + row + "," + col + "))");
            getController().getLog().warning("Got a null SerializedFile for InputValidatorResults data file (table cell (" + row + "," + col + "))");
        }

        //get the stdout file from the table
        col = table.getColumn(columnNames[COLUMN.VALIDATOR_OUTPUT.ordinal()]).getModelIndex();
        SerializedFile stdOutFile = getFileForTableCell(table,row,col);
        if (stdOutFile == null) {
            System.err.println("Got a null SerializedFile for Input Validator Results stdout file (table cell (" + row + "," + col + "))");
            getController().getLog().warning("Got a null SerializedFile for InputValidatorResults stdout file  (table cell (" + row + "," + col + "))");
        }

        //get the stderr file from the table
        col = table.getColumn(columnNames[COLUMN.VALIDATOR_ERR.ordinal()]).getModelIndex();
        SerializedFile stdErrFile = getFileForTableCell(table,row,col);
        if (stdErrFile == null) {
            System.err.println("Got a null SerializedFile for Input Validator Results stderr file (table cell (" + row + "," + col + "))");
            getController().getLog().warning("Got a null SerializedFile for InputValidatorResults stderr file (table cell (" + row + "," + col + "))");
        }

        // get the execution directory being used by the EditProblemPane
        String executeDir;
        JPanePlugin parent = getParentPane();
        if (parent instanceof InputValidatorPane) {
            
            JPanePlugin grandParent = ((InputValidatorPane) parent).getParentPane();
            if (grandParent instanceof EditProblemPane) {
                
                JPanePlugin epp = grandParent;
                executeDir = ((EditProblemPane) epp).getExecuteDirectoryName();

                Utilities.insureDir(executeDir);
                MultipleFileViewer viewer = new MultipleFileViewer(getController().getLog());
                String title;
                
                boolean outputPaneAdded = false;
                
                if (dataFile != null) {
                    //display the data file in a viewer pane
                    String dataFileName = executeDir + File.separator + dataFile.getName();
                    try {
                        //write the data file to the execute directory
                        dataFile.writeFile(dataFileName);

                        //add the data file to the viewer frame
                        if (new File(dataFileName).isFile()) {
                            title = dataFile.getName();
                            viewer.addFilePane(title, dataFileName);
                        } else {
                            title = "Error accessing file";
                            viewer.addTextPane(title, "Could not access file ' " + dataFile.getName() + " '");
                        }
                    } catch (IOException e) {
                        title = "Error during file access";
                        viewer.addTextPane(title, "Could not create file " + dataFileName + "Exception " + e.getMessage());
                    }
                    outputPaneAdded = true;
                }
                
                if (stdOutFile != null) {
                    //display the stdout file in a viewer pane
                    String stdOutFileName = executeDir + File.separator + stdOutFile.getName();
                    try {
                        //write the stdout file to the execute directory
                        stdOutFile.writeFile(stdOutFileName);

                        //add the stdout file to the viewer frame
                        if (new File(stdOutFileName).isFile()) {
                            title = stdOutFile.getName();
                            viewer.addFilePane(title, stdOutFileName);
                        } else {
                            title = "Error accessing file";
                            viewer.addTextPane(title, "Could not access file ' " + stdOutFile.getName() + " '");
                        }
                    } catch (IOException e) {
                        title = "Error during file access";
                        viewer.addTextPane(title, "Could not create file " + stdOutFileName + "Exception " + e.getMessage());
                    }
                    outputPaneAdded = true;
                }
                
                if (stdErrFile != null) {
                    //display the stderr file in a viewer pane
                    String stdErrFileName = executeDir + File.separator + stdErrFile.getName();
                    try {
                        //write the stderr file to the execute directory
                        stdErrFile.writeFile(stdErrFileName);

                        //add the stderr file to the viewer frame
                        if (new File(stdErrFileName).isFile()) {
                            title = stdErrFile.getName();
                            viewer.addFilePane(title, stdErrFileName);
                        } else {
                            title = "Error accessing file";
                            viewer.addTextPane(title, "Could not access file ' " + stdErrFile.getName() + " '");
                        }
                    } catch (IOException e) {
                        title = "Error during file access";
                        viewer.addTextPane(title, "Could not create file " + stdErrFileName + "Exception " + e.getMessage());
                    }
                    outputPaneAdded = true;
                }
                
                //check if we actually added anything
                if (outputPaneAdded) {
                    //show the viewer containing the files
                    viewer.setVisible(true);
                } else {
                    getController().getLog().warning("Found no Input Validation Result files to add to MultiFileViewer");
                    System.err.println ("Request to display results files but found no Input Validation Result files to add to MultiFileViewer");
                }
                
            } else {
                getController().getLog().severe("Grandparent of InputValidationResultPane is not an EditProblemPane; not supported"); 
            }
        } else {
            getController().getLog().severe("Parent of InputValidationResultPane is not an InputValidatorPane; not supported");
        }
    }
    
    private SerializedFile getFileForTableCell(JTable table, int row, int col) {
        InputValidationResult res = ((InputValidationResultsTableModel)table.getModel()).getResultAt(row);
        SerializedFile file ;
        switch (col) {
            case 0:
                file = new SerializedFile(res.getFullPathFilename());
                try {
                    if (Utilities.serializedFileError(file)) {
                        getController().getLog().warning("Error obtaining SerializedFile for file ' " + res.getFullPathFilename() + " '");
                        System.err.println ("Error constructing SerializedFile (see log)");
                        file = null;
                    }
                } catch (Exception e) {
                    getController().getLog().getLogger().log(Log.SEVERE, "Error obtaining SerializedFile for file ' " + res.getFullPathFilename() + " '", e);
                    System.err.println ("Exception constructing SerializedFile (see log): " + e.getMessage());
                    file = null;
                }
                break;
            case 1:
                getController().getLog().getLogger().log(Log.SEVERE, "Got a mouse click on an unclickable table cell!");
                System.err.println ("Internal error: got a mouse click on a cell that shouldn't be clickable");
                file = null;
                break;
            case 2:
                file = res.getValidatorStdOut();
                break;
            case 3:
                file = res.getValidatorStdErr();
                break;
            default:
                getController().getLog().severe("Undefined JTable column");
                return null;
        }
        return file;

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

    @Override
    public String getPluginTitle() {
        return "Input Validation Result Pane";
    }

    public void setParentPane(JPanePlugin parentPane) {
       this.parentPane = parentPane;
    }
    
    public JPanePlugin getParentPane() {
        return this.parentPane;
    }

    public void setInputValidationSummaryMessageText(String msg) {
       getInputValidationResultSummaryTextLabel().setText(msg);
    }

    public void setInputValidationSummaryMessageColor(Color color) {
        getInputValidationResultSummaryTextLabel().setForeground(color);
    }

    /**
     * Examines the provided array of {@link InputValidationResult} values and sets the Status Message label text correspondingly.
     * 
     * @param runResults
     *            an array of InputValidationStatus values
     */
    public void updateInputValidationStatusMessage(InputValidationResult[] runResults) {

        Color color = Color.BLACK;
        String msg = "<No Input Validation test run yet>";

        if (runResults != null && runResults.length > 0) {

            // there are some results; see if there were any failures
            boolean foundFailure = false;
            for (InputValidationResult res : runResults) {
                if (!res.isPassed()) {
                    foundFailure = true;
                    break;
                }
            }
            
            InputValidationStatus overallStatus ;
            if (foundFailure) {
                overallStatus = InputValidationStatus.FAILED;
            } else {
                overallStatus = InputValidationStatus.PASSED;
            }

            switch (overallStatus) {

                case PASSED:
                    int count = runResults.length;
                    msg = "" + count + " of " + count + " input data files PASSED validation";
                    color = new Color(0x00, 0xC0, 0x00); // green, but with some shading
                    break;
                    
                case FAILED:
                    int totalCount = 0;
                    int failCount = 0;
                    for (InputValidationResult res : runResults) {
                        if (!res.isPassed()) {
                            failCount++;
                        }
                        totalCount++;
                    }
                    msg = "" + failCount + " of " + totalCount + " input data files FAILED validation";
                    color = Color.red;
                    break;
                    
                case ERROR:
                case NOT_TESTED:
                    msg = "Error occurred during input validation result display; check logs";
                    color = Color.YELLOW;
                    getController().getLog().severe("Unexpected error in computing Input Validation Status: found status '" + overallStatus + "' when "
                            + "only 'PASSED' or 'FAILED' should be possible");
                    break;
                default:
                    msg = "This message should never be displayed; please notify PC2 Developers: pc2@ecs.csus.edu";
                    color = Color.ORANGE;
            }
        }

        getInputValidationResultSummaryTextLabel().setText(msg);
        getInputValidationResultSummaryTextLabel().setForeground(color);

    }

    protected JCheckBox getShowOnlyFailedFilesCheckbox() {
        if (showOnlyFailedRunsCheckbox == null) {
        	showOnlyFailedRunsCheckbox = new JCheckBox("Show only failed input files");
        	showOnlyFailedRunsCheckbox.setSelected(true);
            
            showOnlyFailedRunsCheckbox.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) {
        	        SwingUtilities.invokeLater(new Runnable() {
        	            public void run () {
        	                JPanePlugin parent = getParentPane();
        	                if (parent instanceof InputValidatorPane) {
        	                    
        	                    //get the results of the latest run from the parent
        	                    InputValidationResult [] results = ((InputValidatorPane) parent).getRunResults();
        	                    
        	                    if (results != null && results.length > 0) {
        	                        
                                    //make a copy so we don't wipe out the parent's results
                                    InputValidationResult[] updatedResults = Arrays.copyOf(results, results.length);

                                    //if we are only going to show failed results, make a new array containing only failed results
                                    if (getShowOnlyFailedFilesCheckbox().isSelected()) {
                                        ArrayList<InputValidationResult> failedResultsList = new ArrayList<InputValidationResult>();
                                        for (int i = 0; i < updatedResults.length; i++) {
                                            if (!updatedResults[i].isPassed()) {
                                                failedResultsList.add(updatedResults[i]);
                                            }
                                        }
                                        updatedResults = new InputValidationResult [failedResultsList.size()];
                                        for (int i=0; i<updatedResults.length; i++) {
                                            updatedResults[i] = failedResultsList.get(i);
                                        }
                                    }
                                    
                                    //put the updated results in the table model and redraw the table
                                    ((InputValidationResultsTableModel) getInputValidatorResultsTable().getModel()).setResults(updatedResults);
                                    ((InputValidationResultsTableModel) getInputValidatorResultsTable().getModel()).fireTableDataChanged();
                                    
                                } else {
                                    getController().getLog().info("ShowOnlyFailedFiles checkbox selected but found no run results to display");
                                }
                                
                            } else {
        	                    getController().getLog().warning("InputValidationResultPane parent not an InputValidatorPane; cannot obtain results to update table");
        	                }
        	            }
        	        });
        	    }
        	});
        }
        return showOnlyFailedRunsCheckbox;
    }
    private Component getHorizontalStrut_1() {
        if (horizontalStrut_1 == null) {
        	horizontalStrut_1 = Box.createHorizontalStrut(20);
        }
        return horizontalStrut_1;
    }


    public void updateResultsTable(InputValidationResult[] runResults) {
        //put the results in the table model and redraw the table
        ((InputValidationResultsTableModel) getInputValidatorResultsTable().getModel()).setResults(runResults);
        ((InputValidationResultsTableModel) getInputValidatorResultsTable().getModel()).fireTableDataChanged();
        updateInputValidationStatusMessage(runResults);
    }
}
