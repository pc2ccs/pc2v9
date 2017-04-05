package edu.csus.ecs.pc2.ui;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * This class defines a {@link TableModel} for a table holding {@link InputValidationResult}s.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class InputValidationResultsTableModel extends DefaultTableModel {

    private static String[] colNames = { "File", "Result", "Validator StdOut", "Validator StdErr" };

    private static Vector<String> columnNames = new Vector<String>(Arrays.asList(colNames));
    
    private InputValidationResult [] results ;

    private static final long serialVersionUID = 1L;
    
    public InputValidationResultsTableModel(InputValidationResult [] results) {
        super(null, columnNames);
        setResults(results);
    }
    
    public InputValidationResultsTableModel() {
        super(null, columnNames);
        setRowCount(0);
    }

    public void setResults(InputValidationResult [] results) {
        if (results != null){
            setRowCount(results.length);
            this.results = results;
        } else {
            setRowCount(0);
            this.results = null;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {

        Object obj = "Unknown";

        //code from TestCaseTableModel, from which this file was cloned:
        switch (column) {
            case 0:
                obj = results[row].getFullPathFilename();
                break;
            case 1:
                //TODO: need to add a CellRenderer to set true=green and false=red
                obj = results[row].isPassed();
                break;
            case 2:
                //TODO: need to return a string which can be used as a LINK to the file
                obj = results[row].getValidatorStdOut();
                break;
            case 3:
                //TODO: need to return a string which can be used as a LINK to the file
                obj = results[row].getValidatorStdErr();
                break;
            default:
                break;

        }
        return obj;
    }

    /**
     * Remove the specified row from the table.  Note that row numbers start with zero!
     * 
     * @param row - the row number to be removed, where the first row is row zero
     */
    @Override
    public void removeRow(int row) {
        InputValidationResult [] newResults = new InputValidationResult [results.length-1];
        for (int i=0; i<row; i++) {
            newResults[i] = results[i];
        }
        for (int i=row+1; i<results.length; i++) {
            newResults[i-1] = results[i];
        }
        results = newResults;

        super.removeRow(row);
    }
    
    public InputValidationResult [] getResults() {
        return results;
    }

}
