// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.list.SubmissionSample;
import edu.csus.ecs.pc2.ui.cellRenderer.LinkCellRenderer;
import edu.csus.ecs.pc2.ui.cellRenderer.RightJustifiedCellRenderer;
import edu.csus.ecs.pc2.ui.cellRenderer.TestCaseResultCellRenderer;

/**
 * Multiple data set viewer pane.
 *
 * @author John Buck
 * @version $Id: AutoJudgeSettingsPane.java 2825 2014-08-12 23:22:50Z boudreat $
 */

public class SampleResultsPane extends JPanePlugin implements TableModelListener {

    private static final long serialVersionUID = 1L;

    /**
     * list of columns
     */
    protected enum COLUMN {
        DATASET_NUM, RESULT, TIME, JUDGE_OUTPUT, JUDGE_DATA
    };

    // define the column headers for the table of results
    private String[] columnNames = { "Data Set #", "Result", "Time (s)",
                                        "Judge's Output", "Judge's Data" };

    private JPanel centerPanel = null;

    private JTable resultsTable;

    private JLabel lblProblemTitle;

    private JLabel lblRunID;

    private Run currentRun;

    private Problem currentProblem;

    private ProblemDataFiles currentProblemDataFiles;

    private JLabel lblLanguage;

    private JScrollPane resultsScrollPane;

    private Log log ;

    private JButton btnCancel;

    private JTabbedPane multiTestSetTabbedPane;

    private JPanel resultsPane;

    private JPanel resultsPaneHeaderPanel;

    private JPanel resultsPaneButtonPanel;

    private JButton resultsPaneCloseButton;
    private JLabel lblTotalTestCases;
    private JLabel lblNumTestCasesActuallyRun;
    private JLabel lblNumFailedTestCases;
    private Component horizontalGlue_9;

    private boolean debug = false;

    /**
     * Constructs an instance of a plugin pane for viewing multi-testset output values.
     *
     */
    public SampleResultsPane() {
        super();
        initialize();
    }

    /**
     * This method initializes the pane.
     *
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(717, 363));
        this.add(getCenterPanel(), java.awt.BorderLayout.CENTER);

        // TODO Bug 918

    }

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        log = getController().getLog();
    }

    @Override
    public String getPluginTitle() {
        return "Sample Execute Times Pane";
    }

    /**
     * This method initializes and returns a JPanel containing a
     * JTabbedPane holding the output results and options panes. Note that the method
     * does not fill in any live data; that cannot be done until the View Pane's
     * "setData()" method has been invoked, which doesn't happen until after construction
     * of the View Pane is completed.
     *
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPanel() {

        if (centerPanel == null) {

            centerPanel = new JPanel();
            BorderLayout cpBorderLayout = new BorderLayout();
            cpBorderLayout.setVgap(0);
            centerPanel.setLayout(cpBorderLayout);

            centerPanel.add(getResultsPane(), BorderLayout.CENTER);


        }
        return centerPanel;
    }


    /**
     * Defines and returns a JPanel containing a Header Panel describing the current run, a JTable displaying
     * test case results for the Run, and a button panel with various control Buttons.
     *
     * @return the Results Pane JPanel
     */
    private JPanel getResultsPane() {

        if (resultsPane == null) {

            resultsPane = new JPanel();
            resultsPane.setName("View Execution Times");
            resultsPane.setLayout(new BorderLayout(0, 0));

            // add a header for holding labels to the results panel
            resultsPane.add(getResultsPaneHeaderPanel(),BorderLayout.NORTH);

            //add a scrollpane holding the actual test set results
            resultsPane.add(getResultsScrollPane(), BorderLayout.CENTER);

            //add a button panel at the bottom
            resultsPane.add(getResultsPaneButtonPanel(), BorderLayout.SOUTH);
        }
        return resultsPane;

    }

    /**
     * Defines a header panel for the results pane containing information about the run whose results are being displayed.
     * Note that this accessor does not fill in actual data; that cannot be done until the Test Results pane is populated
     * via a call to {@link #setData(Run, RunFiles, Problem, ProblemDataFiles)}.
     *
     * @return a JPanel containing run information
     */
    private JPanel getResultsPaneHeaderPanel() {

        if (resultsPaneHeaderPanel == null) {

            resultsPaneHeaderPanel = new JPanel();
            resultsPaneHeaderPanel.setBorder(new LineBorder(Color.BLUE, 2));

            resultsPaneHeaderPanel.add(getRunIDLabel());

            Component horizontalGlue_1 = Box.createHorizontalGlue();
            horizontalGlue_1.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue_1);

            // add a label to the header showing the Problem for which this set of test results applies
            resultsPaneHeaderPanel.add(getProblemTitleLabel());

            Component horizontalGlue = Box.createHorizontalGlue();
            horizontalGlue.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue);

            Component horizontalGlue_2 = Box.createHorizontalGlue();
            horizontalGlue_2.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue_2);

            resultsPaneHeaderPanel.add(getLanguageLabel());

            Component horizontalGlue_8 = Box.createHorizontalGlue();
            horizontalGlue_8.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue_8);
            resultsPaneHeaderPanel.add(getTotalTestCasesLabel());
            resultsPaneHeaderPanel.add(getHorizontalGlue_9());

            // add a label to the header showing the total number of test cases for this problem
            resultsPaneHeaderPanel.add(getNumTestCasesActuallyRunLabel());

            Component horizontalGlue_7 = Box.createHorizontalGlue();
            horizontalGlue_7.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue_7);

            resultsPaneHeaderPanel.add(getNumFailedTestCasesLabel());
        }
        return resultsPaneHeaderPanel;
    }

    /**
     * Returns a {@link JScrollPane} containing a {@link JTable} for holding test case results.
     * @return
     */
    private JScrollPane getResultsScrollPane() {

        if (resultsScrollPane == null) {

            // add a scrollpane to hold the table of results
            resultsScrollPane = new JScrollPane();

            // create an (empty) table of results and put it in the scrollpane
            resultsTable = new JTable(12,7);
            resultsTable.setValueAt(true, 0, 0);
            resultsScrollPane.setViewportView(resultsTable);
        }
        return resultsScrollPane;
    }

    /**
     * Returns a JPanel containing control buttons for the Results Pane.
     * @return the resultsPaneButtonPanel
     */
    private JPanel getResultsPaneButtonPanel() {

        if (resultsPaneButtonPanel == null) {

            resultsPaneButtonPanel = new JPanel();

            // add a control button to dismiss the frame
            resultsPaneButtonPanel.add(getResultsPaneCloseButton());
        }
        return resultsPaneButtonPanel;
    }

    /**
     * Returns a JButton whose action is to depends on whether it is invoked from the Options pane or the Results pane.
     * If invoked from the Options pane, it simply makes the Results pane the active pane.
     * If invoked from the Results pane, it closes any open child windows (such as a {@link MultiFileComparator}), and
     * then disposes this MultiTestSetOutputViewerPane's parent window.
     *
     * Note that the Close button appears on both the Options pane and the Results pane -- but it is the same button,
     * not two different instances (this is a result of the Singleton pattern implementation).
     *
     * Addendum:  the above was the INTENT, and it is legal in Java/Swing to code it that way.  However, doing so generates
     * an error in the WindowBuilder (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=341111).  As a result, the code was
     * refactored to provide two distinct Close buttons: one for the Options pane and a different one for the Results pane.
     *
     * @return the Close JButton
     */
   private JButton getResultsPaneCloseButton() {
        if (resultsPaneCloseButton == null) {
            resultsPaneCloseButton = new JButton("Close");

            //add an action handler for the Close button
            resultsPaneCloseButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    Window parentFrame = SwingUtilities.getWindowAncestor(resultsPaneCloseButton);
                    if (parentFrame != null) {
                        parentFrame.dispose();
                    }
                }
            });
        }
        return resultsPaneCloseButton;
    }

    private JLabel getTotalTestCasesLabel() {
        if (lblTotalTestCases == null) {
            lblTotalTestCases = new JLabel("Total Test Cases: XXX");
        }
        return lblTotalTestCases;
    }

    /**
     * @return
     */
    private JLabel getNumTestCasesActuallyRunLabel() {
        if (lblNumTestCasesActuallyRun == null) {
            lblNumTestCasesActuallyRun = new JLabel("Test Cases Run: XXX");
        }
        return lblNumTestCasesActuallyRun;
    }

    /**
     * @return
     */
    private JLabel getNumFailedTestCasesLabel() {
        if (lblNumFailedTestCases == null) {
            lblNumFailedTestCases = new JLabel("Failed: XXX");
        }
        return lblNumFailedTestCases;
    }

    private JLabel getLanguageLabel() {
        if (lblLanguage == null) {
            lblLanguage = new JLabel("Language: XXX");
        }
        return lblLanguage;
    }

    private JLabel getRunIDLabel() {
        if (lblRunID == null) {
            lblRunID = new JLabel("Run ID: XXX");
        }

        return lblRunID;
    }

    private JLabel getProblemTitleLabel() {
        if (lblProblemTitle == null) {
            lblProblemTitle = new JLabel("Problem: XXX");
        }
        return lblProblemTitle;
    }

    private void populateGUI() {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // fill in the basic header information
                String letter = currentProblem.getLetter();
                String shortName = currentProblem.getShortName();
                // HACK around EditProblemPane not requiring (or setting) a letter
                if (letter == null || letter.equals("null") || letter.equals("")) {
                    Problem[] problems = getContest().getProblems();
                    for (int i = 0; i < problems.length; i++) {
                        if (problems[i].equals(currentProblem)) {
                            int asciiLetter = i+65;
                            letter = Character.toString((char)asciiLetter);
                        }
                    }
                }
                // HACK around EditProblempane not requiring (or setting) a shortName
                if (shortName == null || shortName.equals("")) {
                    shortName = currentProblem.getDisplayName().toLowerCase().trim();
                    int spaceIndex = shortName.indexOf(" ");
                    if (spaceIndex > 0) {
                        shortName = shortName.substring(0, spaceIndex);
                    }
                }
                getProblemTitleLabel().setText("Problem:  " + letter + " - " + shortName);
                getRunIDLabel().setText("Run ID:  " + currentRun.getNumber());
                getLanguageLabel().setText("Language:  " + getCurrentRunLanguageName());

                //display in the GUI the total test cases configured in the problem
                int totalTestCases = currentProblem.getNumberTestCases();
                getTotalTestCasesLabel().setText("Total Test Cases: " + totalTestCases);

                // get the actually-run test case results for the current run
                RunTestCase[] testCases = getCurrentTestCaseResults(currentRun);

                // fill in the test case summary information
                if (testCases == null || testCases.length==0) {
                    getNumTestCasesActuallyRunLabel().setText("Test Cases Run:  0");
                } else {
                    getNumTestCasesActuallyRunLabel().setText("Test Cases Run:  " + testCases.length);
                }

                //set the status label to the default (blank)
                getNumFailedTestCasesLabel().setForeground(Color.black);
                getNumFailedTestCasesLabel().setText("");

                int failedCount = getNumFailedTestCases(testCases);

                if (!currentProblem.isValidatedProblem()) {
                    // problem is not validated, cannot be failed or passed
                    getNumFailedTestCasesLabel().setForeground(Color.black);
                    getNumFailedTestCasesLabel().setText("(No validator)");
                } else  if (failedCount > 0) {
                    getNumFailedTestCasesLabel().setForeground(Color.red);
                    getNumFailedTestCasesLabel().setText("Failed:  " + failedCount);
                } else if (failedCount == 0 && testCases!=null && testCases.length>0) {
                    getNumFailedTestCasesLabel().setForeground(Color.green);
                    getNumFailedTestCasesLabel().setText("ALL PASSED");
                }

                resultsTable = getResultsTable(testCases);
                getResultsScrollPane().setViewportView(resultsTable);
            }

        });

    }


    private int getNumFailedTestCases(RunTestCase[] testCases) {
        int failed = 0 ;
        if (testCases != null) {
            for (int i = 0; i < testCases.length; i++) {
                if (!testCases[i].isPassed()) {
                    failed++;
                    // int num = i+1;
                    // System.out.println("Found failed test case: " + num);
                }
            }
        }
//        System.out.println ("  (including " + failed + " failed cases)");
        return failed;
    }

    /**
     * Returns the name of the language used in the "current run" defined by the field "currentRun".
     *
     * @return the language display name as defined by the toString() method of the defined language.
     */
    private String getCurrentRunLanguageName() {
        Language language = getContest().getLanguage(currentRun.getLanguageId());
        return language.toString();
    }

    /**
     * Looks at all the TestCaseResults for a run and filters
     * that list to just the most recent.
     *
     * @param run
     * @return most recent RunTestCaseResults
     */
    private RunTestCase[] getCurrentTestCaseResults(Run run) {
        RunTestCase[] testCases = null;
        RunTestCase[] allTestCases = run.getRunTestCases();
        // hope the lastTestCase has the highest testNumber....
        if (allTestCases != null && allTestCases.length > 0) {
            testCases = new RunTestCase[allTestCases[allTestCases.length-1].getTestNumber()];
            for (int i = allTestCases.length-1; i >= 0; i--) {
                RunTestCase runTestCaseResult = allTestCases[i];
                int testCaseNumIndex = runTestCaseResult.getTestNumber()-1;
                if (testCases[testCaseNumIndex] == null) {
                    testCases[testCaseNumIndex] = runTestCaseResult;
                    if (testCaseNumIndex == 0) {
                        break;
                    }
                }
            }
        }
        return testCases;
    }

    /**
     * Loads the result table with data for all test cases.
     */
    private void loadTableWithAllTestCaseResults() {

        // get the test case results for the current run
        RunTestCase[] allTestCases = getCurrentTestCaseResults(currentRun);

        //build a new table with the test cases and install it in the scrollpane
        resultsTable = getResultsTable(allTestCases);
        resultsScrollPane.setViewportView(resultsTable);
    }

    /**
     * Loads the result table with data for failed test cases (only).
     * The initial test for "failed" is to check the RunTestCase isPassed() flag, which is based on the value "passed" which is
     * stored in the RunTestCase when it is constructed (see @link{Executable#executeAndValidateDataSet}).
     * This value is TRUE (meaning isPassed() returns true) if and only if:
     *   the submitted program was successfully executed
     *   AND the problem has a validator
     *   AND method validateProgram() returned true (indicating the problem was correctly solved for the specified test case)
     *   AND the ExecutionData object for the run indicates that the submission solved the problem for the specified data case.
     *     (the ExecutionData object indicates the program solved the problem if:
     *        the program compiled successfully
     *        AND the system was able to successfully execute the program
     *        AND the program did not exceed the runtime limit
     *        AND the validator program ran successfully
     *        AND there were no exceptions during Validator execution
     *        AND the result string returned by the Validator was "accepted".
     *      The ExecutionData object returns false (the problem was NOT solved) if any of these conditions is false.
     *     )
     * If any of the above conditions is not true, isPassed() returns false.
     *
     * The above in turn means that test cases for runs where the Problem has no Validator will be considered "failed" (isPassed()==false)
     * -- but this is not really what the table should contain for "failed test cases".
     * Therefore an additional check for "isValidated()" needs to be made.
     *
     * The same is true for test cases which were never executed -- they will be considered "failed" but they really shouldn't
     * be displayed in a table of "failed test cases".
     * TODO: currently there is no easy way to determine whether a Test Case was actually executed, unless we assume that the
     * mere presence of a TestCaseResult indicates that it was executed...
     *
     */
    private void loadTableWithFailedTestCases() {

        // get the test case results for the current run
        RunTestCase[] allTestCases = getCurrentTestCaseResults(currentRun);

        //extract failed cases into a Vector (list)
        Vector<RunTestCase> failedTestCaseList = new Vector<RunTestCase>();
        if (allTestCases != null) {
            for (int i = 0; i < allTestCases.length; i++) {
                //check for actual "failure" - ignoring "no validator" and "not executed" cases
                // TODO: figure out how to implement the test for "wasExecuted"...
                if (!allTestCases[i].isPassed() && allTestCases[i].isValidated() /* && allTestCases[i].wasExecuted() */ ) {
                    failedTestCaseList.add(allTestCases[i]);
                }
            }
        }

        //convert Vector to array
        RunTestCase[] failedTestCases =  failedTestCaseList.toArray(new RunTestCase[failedTestCaseList.size()]);

        //build a new table with just the failed cases and install it in the scrollpane
        resultsTable = getResultsTable(failedTestCases);
        resultsScrollPane.setViewportView(resultsTable);
    }

    /**
     * Returns a JTable containing the results information for the specified set of test cases.
     * The method sets not only the table data model but also the appropriate cell renderers and
     * action/mouse listeners for the table.
     */
    private JTable getResultsTable(RunTestCase [] testCases) {

        final JTable localResultsTable;

        //create the results table
        TableModel tableModel = new SampleResultsTableModel(getContest(), testCases, columnNames) ;

        tableModel.addTableModelListener(this);

        if (debug) {
            System.out.println("In getResultsTable(); table model contains:");
            System.out.println("--------------");
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    System.out.print("[" + tableModel.getValueAt(row, col) + "]");
                }
                System.out.println();
            }
            System.out.println("--------------");
        }

        localResultsTable = new JTable(tableModel);

        //set the desired options on the table
        localResultsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        localResultsTable.setFillsViewportHeight(true);
        localResultsTable.setRowSelectionAllowed(false);
        localResultsTable.getTableHeader().setReorderingAllowed(false);

        //initialize column renderers based on column type

        // set a centering renderer on desired table columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        localResultsTable.getColumn(columnNames[COLUMN.DATASET_NUM.ordinal()]).setCellRenderer(centerRenderer);

        // set a LinkRenderer on those cells containing links
        localResultsTable.getColumn(columnNames[COLUMN.JUDGE_OUTPUT.ordinal()]).setCellRenderer(new LinkCellRenderer());
        localResultsTable.getColumn(columnNames[COLUMN.JUDGE_DATA.ordinal()]).setCellRenderer(new LinkCellRenderer());

        // render Result column as Pass/Fail on Green/Red (if the test case was validated), or else either "<No Validator>" or "(Not Executed)"
        localResultsTable.getColumn(columnNames[COLUMN.RESULT.ordinal()]).setCellRenderer(new TestCaseResultCellRenderer());

        // render Time column right-justified
        localResultsTable.getColumn(columnNames[COLUMN.TIME.ordinal()]).setCellRenderer(new RightJustifiedCellRenderer());

        return localResultsTable;
    }


    /**
     * Returns an array containing only test case results in the received array which represent test cases which failed.
     *
     * That is, removes any "passed" or "not executed" test cases (as well as any null elements) from the received array and
     * returns a new array containing whatever is left.
     *
     * @param testCaseResults - an array of RunTestCaseResults from which non-failures are to be removed
     * @return an array of RunTestCaseResults containing only the failed test cases in the received array,
     *          or returns null if the received array is null or zero-length.
     */
    private RunTestCase[] removeNonFailuresFromTestCaseResults(RunTestCase[] testCaseResults) {

        if (testCaseResults!=null && testCaseResults.length>0) {

            if (debug) {
                System.out.println("removeNonFailuresFromTestCaseResults(): received an array containing the following test case results: [");
                for (int i=0; i<testCaseResults.length; i++) {
                    System.out.println("  Test Case Result: " + testCaseResults[i].toString());
                }
                System.out.println ("]");
            }

            //convert to a List for easy removal (and automatic shifting of remaining elements)
            Vector<RunTestCase> testCaseResultList = new Vector<RunTestCase>(Arrays.asList(testCaseResults));

            Vector<RunTestCase> toBeRemoved = new Vector<RunTestCase>();

            //find all passed and non-validated results (non-validated means they couldn't be "passed")
            for (RunTestCase res : testCaseResultList) {
                if (res==null || res.isPassed() || !res.isValidated()) {
                    toBeRemoved.add(res);
                }
            }
            //remove all found results from the list
            for (RunTestCase removeRes : toBeRemoved) {
                testCaseResultList.remove(removeRes);
            }

            RunTestCase [] retArray = new RunTestCase[0];
            //return an array of the remaining TestCaseResults (i.e. the ones that "failed")
            retArray = testCaseResultList.toArray(retArray);

            if (debug) {
                System.out.println("removeNonFailuresFromTestCaseResults(): returning an array containing the following test case results: [");
                for (int i=0; i<retArray.length; i++) {
                    System.out.println("  Test Case Result: " + retArray[i].toString());
                }
                System.out.println ("]");
            }

            return retArray;

        } else {

            if (debug) {
                System.out.println("removeNonFailuresFromTestCaseResults(): returning null. ");
            }

            return null;
        }

    }

    /**
     * Checks the given JTable to see whether it already contains a row for every test data case defined in the current problem;
     * adds any unexecuted (and hence missing) test cases to the table.
     *
     * @param aResultsTable - a JTable expected to already contain one row for each test case which has been executed
     */
    private void addAnyUnexecutedTestCasesToResultsTable(JTable aResultsTable) {

        if (debug) {
            System.out.println ("In addAnyUnexecutedTestCasesToResultsTable(); checking for unexecuted test cases...");
        }

        //get the table model which defines the current table contents
        SampleResultsTableModel tableModel = (SampleResultsTableModel) aResultsTable.getModel();

        //get how many total test cases were configured into the problem
        int totalTestCaseCount = currentProblem.getNumberTestCases();

        if (debug) {
            System.out.println("...total test cases defined in problem: " + totalTestCaseCount);
        }

        //get how many test case rows are already in the table model
        int testCasesInTableModel = tableModel.getRowCount();

        if (debug) {
            System.out.println("...test case rows already in results table: " + testCasesInTableModel);
        }

        if (totalTestCaseCount > testCasesInTableModel) {
            //yes, there are missing cases; add them to the table
            for (int testCaseNum=testCasesInTableModel+1; testCaseNum<=totalTestCaseCount; testCaseNum++) {
                //add the current unexecuted test case to the table model

                if (debug) {
                    System.out.println ("...adding unexecuted test case " + testCaseNum + " to results table");
                }

                //build the variable portions of the row data
                String viewJudgeAnswerFile = "";
                if (currentProblem.getAnswerFileName(testCaseNum)!=null && currentProblem.getAnswerFileName(testCaseNum).length()>0) {
                    viewJudgeAnswerFile = "View";
                }
                String viewJudgeDataFile = "";
                if (currentProblem.getDataFileName(testCaseNum)!=null && currentProblem.getDataFileName(testCaseNum).length()>0) {
                    viewJudgeDataFile = "View";
                }
//                "Not Executed",                             //result string
//                "--  ",                                     //execution time (of which there is none since the test case wasn't executed)
//                "",                                         //link to team output (none since it wasn't executed)
//                "",                                         //link to team compare-with-judge label (disabled since there's no team output)
//                "",                                         //link to team stderr (none since it wasn't executed)
//                viewJudgeAnswerFile,                        //link to judge's output (answer file) if any
//                viewJudgeDataFile,                          //link to judge's data if any
//                "",                                         //link to validator stdout (none)
//                ""                                          //link to validator stderr (none)
                SampleResultsRowData rowData = new SampleResultsRowData("Not Executed", "--  ",viewJudgeAnswerFile,viewJudgeDataFile);
                tableModel.addRow(
                        new String(Integer.toString(testCaseNum)),  //test case number
                        rowData);
            }

        } else {
            if (debug) {
                System.out.println("...all test cases are already in the results table.");
            }
        }
    }

    /**
     * Clears the given JTable with empty test cases
     *
     * @param aResultsTable - a JTable expected to already contain one row for each test case which has been executed
     */
    public void resetResultsTable() {
        if(resultsTable != null) {
            //get the table model which defines the current table contents
            SampleResultsTableModel tableModel = (SampleResultsTableModel) resultsTable.getModel();

            //get how many test case rows are already in the table model
            int testCasesInTableModel = tableModel.getRowCount();

            for(int row = testCasesInTableModel-1; row >= 0; row--) {
                tableModel.removeRow(row);
            }

            // Add exactly one row as a place holder to tell the user judging is taking place
            //     "Loading",                                   //result string
            //      "--  ",                                     //execution time (of which there is none since the test case wasn't executed)
            //      "",                                         //link to team output (none since it wasn't executed)
            //      "",                                         //link to team compare-with-judge label (disabled since there's no team output)
            //      "",                                         //link to team stderr (none since it wasn't executed)
            //      "",                                         //link to judge's output (answer file) if any
            //      "",                                         //link to judge's data if any
            //      "",                                         //link to validator stdout (none)
            //      ""                                          //link to validator stderr (none)
            SampleResultsRowData rowData = new SampleResultsRowData("Judging", "--  ", "", "");
            // add new row
            tableModel.addRow(
                    new String("*"),                            //test case number
                    rowData);
            getNumFailedTestCasesLabel().setForeground(Color.cyan);
            getNumFailedTestCasesLabel().setText("(Loading...)");

        }
    }

    /**
     * Invoked when table data changes; checks to see if the change took place in the "Select" column and if so
     * updates the Compare Selected button (enables or disables it as necessary).
     *
     * @param e the TableModelEvent describing the change in the table model
     */
    @Override
    public void tableChanged(TableModelEvent e) {
        int column = e.getColumn();
        TableModel model = (TableModel)e.getSource();
        String columnName = model.getColumnName(column);
    }

    public void showMessage(final String message) {
        JOptionPane.showMessageDialog(this, message);
    }


    public void setData(SubmissionSample sub) {
        Run run = sub.getRun();

        if(run != null) {
            Problem problem = getContest().getProblem(sub.getProblem());
            ProblemDataFiles problemDataFiles = getContest().getProblemDataFile(problem);


            currentRun = run;
            currentProblem = problem;
            currentProblemDataFiles = problemDataFiles;
            populateGUI();
        }
    }

    private Component getHorizontalGlue_9() {
        if (horizontalGlue_9 == null) {
            horizontalGlue_9 = Box.createHorizontalGlue();
            horizontalGlue_9.setPreferredSize(new Dimension(20, 20));
        }
        return horizontalGlue_9;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
