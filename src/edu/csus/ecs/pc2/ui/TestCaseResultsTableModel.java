package edu.csus.ecs.pc2.ui;

import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

import com.ibm.webrunner.j2mclb.util.TableModel;

import edu.csus.ecs.pc2.core.model.RunTestCase;

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
    public TestCaseResultsTableModel(RunTestCase[] testCases, Object[] columnNames) {
        
        //create a default table model with zero rows and columns
        super ();
        
        //add the column names to the model
        for (int col=0; col<columnNames.length; col++) {
            super.setColumnIdentifiers(columnNames);
        }
        
            if (testCases != null) {
            //add the row data for each test case to the model
            for (int row=0; row< testCases.length; row++) {
                
                //selection checkbox state
                Boolean selected = new Boolean (!testCases[row].isPassed());
                
                //test case number (row+1)
                String testCaseNum = new String(Integer.toString(row+1));
                
                //test case result (passed/failed)
                boolean result = testCases[row].isPassed();
                String resultString = "Fail";
                if (result) {
                    resultString = "Pass";
                }
                result = testCases[row].isValidated();
                if (!result) {
                    resultString = "N/A"; // this will be overwritten by PassFailCellRenderer
                }
                JLabel resultLabel = new JLabel(resultString);
                
                //elapsed time of test case
                String time = new String(Long.toString(testCases[row].getElapsedMS()));
                
                //link for viewing team output
                JLabel teamOutputViewLabel = new JLabel("View");
                
                //link for comparing team output with corresponding judge's output
                JLabel teamOutputCompareLabel = new JLabel("Compare");
                
                //link for viewing judge's output
                JLabel judgesOutputViewLabel = new JLabel("View");
                
                JLabel judgesDataViewLabel = new JLabel("View");
                
                // link for validator stdout
                JLabel validatorOutputViewLabel = new JLabel("View");
                
                // link for validator stderr
                JLabel validatorStderrViewLabel = new JLabel("View");
                
                //build the row object and add it to the model
                Object [] rowData = new Object [] {selected, testCaseNum, resultLabel, time, 
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
     * Returns whether the specified cell is editable.
     * The only editable cells in a Test Case model are those in the "row selected" 
     * column; all other cells in all other columns are not editable.
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        boolean retVal = ( columnIndex == MultiTestSetOutputViewerPane.COLUMN.SELECT_CHKBOX.ordinal() );
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
