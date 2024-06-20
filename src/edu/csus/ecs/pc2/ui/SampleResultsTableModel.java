// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunTestCase;

/**
 * This class defines a table model for tables holding judge Sample Test Case Results.
 *
 * @author John Buck
 *
 */
public class SampleResultsTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 1L;

    private boolean debug = false;


    /**
     * Constructs a Table Model for Sample Test Case Results. The data and columnName values
     * are stored in Vectors in the DefaultTableModel parent class.
     * @param contest - the contest to which the results in this table model apply (needed for access to problem configuration)
     * @param testCaseResults - the data to go in the model
     * @param columnNames - Strings defining the table column headers
     */
    public SampleResultsTableModel(IInternalContest contest, RunTestCase[] testCaseResults, Object[] columnNames) {

        //create a default table model with zero rows and columns
        super ();

        //add the column names to the model
        for (int col=0; col<columnNames.length; col++) {
            super.setColumnIdentifiers(columnNames);
        }

        if (debug) {
            int numTestCases;
            if (testCaseResults == null || testCaseResults.length == 0) {
                numTestCases = 0;
            } else {
                numTestCases = testCaseResults.length;
            }
            System.out.println("SampleResultsTableModel(): numTestCases = " + numTestCases);
        }

        if (testCaseResults != null) {
            // add the row data for each test case to the model
            for (int row = 0; row < testCaseResults.length; row++) {

                if (debug) {
                    System.out.println("  Test Case " + (row + 1) + ": " + testCaseResults[row].toString());
                }

                // test case number (data set number + 1)
                String testCaseNum = Integer.toString(testCaseResults[row].getTestNumber());

                // test case result: one of "No Validator", "Pass", or "Fail"
                String resultString = "";
                boolean probHasValidator = testCaseResults[row].isValidated();
                if (!probHasValidator) {
                    resultString = "No Validator";
                } else {
                    //problem has a Validator; see what the validation process produced
                    //(see comments in Executable.executeAndValidateDataSet() for an explanation of what isPassed() returns)
                    boolean passed = testCaseResults[row].isPassed();
                    if (passed) {
                        resultString = "Pass";
                    } else {
                        ElementId judgmentId = testCaseResults[row].getJudgementId();
                        resultString = "Fail";
                        if(judgmentId != null) {
                            Judgement judgment = contest.getJudgement(judgmentId);
                            if(judgment != null) {
                                resultString = String.format("Fail - %s : %s", judgment.getDisplayName(), judgment.getAcronym());
                            }
                        }
                    }
                }
                JLabel resultLabel = new JLabel(resultString);

                // elapsed time of test case
                long execTimeMS = testCaseResults[row].getElapsedMS();
                String time = String.format("%d.%03ds", execTimeMS/1000, execTimeMS%1000);

                JLabel judgesOutputViewLabel = new JLabel("");

                //link for viewing judge's data file (but only if the judge's data file exists)
                JLabel judgesDataViewLabel = new JLabel("");

                //only provide compare & judges-output links if the problem associated with this test case has a judge's answer file
                //only provide judge's data file link if the problem has a data file for this test case
                // (Note that the RunTestCase contains the runElementID BUT NOT THE RUN... :(  So we must go get the Run from the Contest Model
                //   so we can get the Problem so we can find out whether it has things like judge's output, judge's data, and a Validator )
                if (contest!=null && testCaseResults[row].getRunElementId()!=null) {
                    Run theRun = contest.getRun(testCaseResults[row].getRunElementId());
                    if (theRun!=null) {
                        ElementId probID = theRun.getProblemId();
                        if (probID!=null) {
                            Problem prob = contest.getProblem(probID);
                            if (prob!=null) {
                                String extPath = contest.getContestInformation().getJudgeCDPBasePath();
                                String judgesFileName;
                                //update team-compare and judges-answer-file links if there is a judge's answer file
                                String answerFileName = prob.getAnswerFileName(row+1);
                                if (answerFileName!=null && answerFileName.length()>0) {
                                    //there is a judge's answer file for this problem for this test case
                                    judgesOutputViewLabel.setText(answerFileName);
                                    judgesFileName = Utilities.locateJudgesDataFile(prob, answerFileName, extPath);
                                    if(judgesFileName == null) {
                                        judgesFileName = answerFileName;
                                    }
                                    judgesOutputViewLabel.setToolTipText(judgesFileName);
                                }
                                //update judge's data file link if the problem has a judge's data file
                                String dataFileName = prob.getDataFileName(row+1);
                                if (dataFileName!=null && dataFileName.length()>0) {
                                    //there is a judge's data file for this problem for this test case
                                    judgesDataViewLabel.setText(dataFileName);
                                    judgesFileName = Utilities.locateJudgesDataFile(prob, dataFileName, extPath);
                                    if(judgesFileName == null) {
                                        judgesFileName = dataFileName;
                                    }
                                    judgesDataViewLabel.setToolTipText(judgesFileName);
                                }
                            }
                        }
                    }
                }

                // build the row object and add it to the model
                Object[] rowData = new Object[] { testCaseNum, resultLabel, time,
                        judgesDataViewLabel, judgesOutputViewLabel};

                super.addRow(rowData);
            }
        }
    }

    /**
     * Returns the Class of objects contained in the specified table column.
     * The Class is determined by the type of object in the first row of
     * the specified column.
     */
    @Override
    public Class<?> getColumnClass(int col) {
        //the data for the model is stored in the parent class vector "dataVector"
        @SuppressWarnings("unchecked")
        Vector<Object> v = (Vector<Object>)dataVector.elementAt(0);
        return v.elementAt(col).getClass();
    }

    @Override
    public int getRowCount() {
        return dataVector.size();
    }

    @Override
    public int getColumnCount() {
        return columnIdentifiers.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return ((Vector<?>)(dataVector.elementAt(rowIndex))).elementAt(columnIndex);
    }

    /**
     * Returns whether the specified cell is editable, which in the case of a cell containing a checkbox means whether the checkbox can be changed.
     * The only editable cells in a Test Case Results table are those in the "select row"
     * column; all other cells in all other columns are not editable.  Further, the "select row"
     * column should only be editable (selectable) when the "Results" column contains either the string
     * "Passed" or the string "Failed".  (This keeps "Not Executed" test cases from being selectable).
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {

        return false;
    }

    /**
     * Allows adding a row to the table model.
     * This method delegates to super.add(); i.e., the add() method in {@link TableModel}.
     */
    public void addRow(String testCaseNum, SampleResultsRowData data ) {

        //test case result (passed/failed)
        JLabel resultLabel = new JLabel(data.getResultString());

        //link for viewing judge's output
        JLabel judgesOutputViewJLabel = new JLabel(data.getJudgesOutputViewLabel());

        JLabel judgesDataViewJLabel = new JLabel(data.getJudgesDataViewLabel());


        //build the row object and add it to the model
        Object [] rowData = new Object [] {testCaseNum, resultLabel, data.getTime(),
                judgesDataViewJLabel, judgesOutputViewJLabel };

        super.addRow(rowData);
    }

    @Override
    public void addRow(Object [] rowData) {
        super.addRow(rowData);
    }

}
