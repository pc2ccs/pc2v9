package edu.csus.ecs.pc2.ui;

import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

import com.ibm.webrunner.j2mclb.util.TableModel;

import edu.csus.ecs.pc2.core.model.RunTestCaseResult;
import edu.csus.ecs.pc2.ui.TestResultsPane.COLUMN;

/**
 * This class defines a table model for tables holding Test Case Results.
 * 
 * @author john
 *
 */
public class TestCaseResultsTableModel extends DefaultTableModel {
    private static final long serialVersionUID = 1L;
    
    
    /**
     * Constructs a Table Model for Test Case Results. The data and columnName values
     * are stored in Vectors in the DefaultTableModel parent class.
     * @param testCases - the data to go in the model
     * @param columnNames - Strings defining the table column headers
     */
    public TestCaseResultsTableModel(RunTestCaseResult[] testCases, Object[] columnNames) {
        
        //create a default table model with zero rows and columns
        super ();
        
        //add the column names to the model
        for (int col=0; col<columnNames.length; col++) {
            super.setColumnIdentifiers(columnNames);
        }
        
//        int numTestCases ;
//        if (testCases==null || testCases.length==0) {
//            numTestCases = 0;
//        } else {
//            numTestCases = testCases.length;
//        }
//        System.out.println("TestCaseResultsTableModel(): numTestCases = " + numTestCases);
        
        if (testCases != null) {
            // add the row data for each test case to the model
            for (int row = 0; row < testCases.length; row++) {

//                System.out.println("  Test Case " + (row + 1) + ": " + testCases[row].toString());

                // selection checkbox state
                Boolean selected = new Boolean(!testCases[row].isPassed());

                // test case number (row+1)
                String testCaseNum = new String(Integer.toString(row + 1));

                // test case result: one of "No Validator", "Pass", or "Fail"
                String resultString = "";
                boolean probHasValidator = testCases[row].isValidated();
                if (!probHasValidator) {
                    resultString = "No Validator";
                } else {
                    //problem has a validator; see what the Validator process produced
                    //(see comments in Executable.executeAndValidateDataSet() for an explanation of what isPassed() returns)
                    boolean passed = testCases[row].isPassed();  
                    if (passed) {
                        resultString = "Pass";
                    } else {
                        resultString = "Fail";
                    }
                }
                JLabel resultLabel = new JLabel(resultString);

                // elapsed time of test case
                String time = new String(Long.toString(testCases[row].getElapsedMS()));

                // link for viewing team output
                JLabel teamOutputViewLabel = new JLabel("View");

                // link for comparing team output with corresponding judge's output
                JLabel teamOutputCompareLabel = new JLabel("Compare");

                // link for viewing judge's output
                JLabel judgesOutputViewLabel = new JLabel("View");

                JLabel judgesDataViewLabel = new JLabel("View");

                // link for validator stdout
                JLabel validatorOutputViewLabel = new JLabel("View");

                // link for validator stderr
                JLabel validatorStderrViewLabel = new JLabel("View");

                // build the row object and add it to the model
                Object[] rowData = new Object[] { selected, testCaseNum, resultLabel, time,
                        teamOutputViewLabel, teamOutputCompareLabel, judgesOutputViewLabel,
                        judgesDataViewLabel, validatorOutputViewLabel, validatorStderrViewLabel };

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

//        System.out.println ("TestCaseResultsTableModel.isCellEditable(): row=" + rowIndex + "; col=" + columnIndex);
        boolean retVal = false;
        //sanity-check the input
        if (rowIndex>getRowCount()-1 || columnIndex>getColumnCount()-1 ) {
            //parameters point to a cell outside the table; indicate "cell not editable"
            retVal = false;
            
        } else {

            int selectionColumnIndex = TestResultsPane.COLUMN.SELECT_CHKBOX.ordinal();
            if (columnIndex != selectionColumnIndex) {
                // columns other than the row-selection checkbox are automatically not editable)
                retVal = false;
            } else {
                // the column is the row-selection checkbox column; see if the "results" column indicates this test case was actually executed                
                String resultString = "";
                try {
                    resultString = ((JLabel)getValueAt(rowIndex, COLUMN.RESULT.ordinal())).getText(); 
                    if (!resultString.equalsIgnoreCase("Pass") && !resultString.equalsIgnoreCase("Fail")) {
                        // the result for this row is neither "pass" nor "fail"; disallow editing the row-selection cell (checkbox)
//                        System.out.println("  Found results column string '" + resultString + "'");
                        retVal = false;
                    } else {
                        // the results string is either "pass" or "fail", so the test case was executed; allow selection
                        retVal = true;
                    }
                } catch (ClassCastException e1) {
                    System.err.println("TestCaseResultsTableModel.isCellEditable(): expected to find a JLabel in resultsTable; exception: "
                                + e1.getMessage());
                    retVal = false;
                }
            }
        }
        
//        System.out.println ("  returning " + retVal);
        return retVal;
    }
    
    /**
     * Allows adding a row to the table model.
     * This method delegates to super.add(); i.e., the add() method in {@link TableModel}.
     */
    public void addRow(Boolean selected, String testCaseNum, String resultString, String time, 
            String teamOutputViewLabel, String teamOutputCompareLabel, 
            String judgesOutputViewLabel, String judgesDataViewLabel,
            String validatorOutputViewLabel, String validatorStderrViewLabel ) {
        
        //test case result (passed/failed)
        JLabel resultLabel = new JLabel(resultString);
                
        //link for viewing team output
        JLabel teamOutputViewJLabel = new JLabel(teamOutputViewLabel);
        
        //link for comparing team output with corresponding judge's output
        JLabel teamOutputCompareJLabel = new JLabel(teamOutputCompareLabel);
        
        //link for viewing judge's output
        JLabel judgesOutputViewJLabel = new JLabel(judgesOutputViewLabel);
        
        JLabel judgesDataViewJLabel = new JLabel(judgesDataViewLabel);
        
        // link for validator stdout
        JLabel validatorOutputViewJLabel = new JLabel(validatorOutputViewLabel);
        
        // link for validator stderr
        JLabel validatorStderrViewJLabel = new JLabel(validatorStderrViewLabel);

        //build the row object and add it to the model
        Object [] rowData = new Object [] {selected, testCaseNum, resultLabel, time, 
                teamOutputViewJLabel, teamOutputCompareJLabel, judgesOutputViewJLabel, 
                judgesDataViewJLabel, validatorOutputViewJLabel, validatorStderrViewJLabel };

        super.addRow(rowData);
    }
    
    @Override
    public void addRow(Object [] rowData) {
        super.addRow(rowData);
    }

}
