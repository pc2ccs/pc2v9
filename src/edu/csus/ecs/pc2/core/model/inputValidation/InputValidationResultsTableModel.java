package edu.csus.ecs.pc2.core.model.inputValidation;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import edu.csus.ecs.pc2.core.Utilities;

/**
 * This class defines a {@link TableModel} for a table holding {@link InputValidationResult}s.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class InputValidationResultsTableModel extends DefaultTableModel {

    private static String[] colNames = { "File", "Result", "Validator StdOut", "Validator StdErr" };

    private static Vector<String> columnNames = new Vector<String>(Arrays.asList(colNames));
    
    private Vector<InputValidationResult> results ;

    private static final long serialVersionUID = 1L;
    
    public InputValidationResultsTableModel(InputValidationResult [] results) {
        super(null, columnNames);
        setResults(results);
    }
    
    public InputValidationResultsTableModel() {
        this(null);
    }

    public void setResults(InputValidationResult [] results) {
        this.results = new Vector<InputValidationResult>();
        if (results != null){
            for (int i=0; i<results.length; i++) {
                this.results.add(results[i]);
            }
            setRowCount(results.length);
        } else {
            setRowCount(0);
        }
    }

    @Override
    public Object getValueAt(int row, int column) {

        Object obj = "Unknown";

        if (results != null && results.get(row) != null) {

            switch (column) {
                case 0:
//                    obj = results.get(row).getFullPathFilename();
                    String shortName = Utilities.basename(results.get(row).getFullPathFilename());
                    obj = new JLabel(shortName);
                    break;
                case 1:
                    obj = results.get(row).isPassed();
                    break;
                case 2:
//                    obj = results.get(row).getValidatorStdOut();
                    obj = (new JLabel(results.get(row).getValidatorStdOut().getName()));
                    break;
                case 3:
//                    obj = results.get(row).getValidatorStdErr();
                    obj = (new JLabel(results.get(row).getValidatorStdErr().getName()));
                    break;
                default:
                    break;

            }
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
        results.remove(row);
        super.removeRow(row);
    }
    
    public void addRow (InputValidationResult result) {
        results.add(result);
        Vector<InputValidationResult> newRow = new Vector<InputValidationResult>();
        newRow.add(result);
        super.addRow(newRow);
    }
    
    
    public Iterable<InputValidationResult> getResults() {
        return results;
    }

    public InputValidationResult getResultAt(int row) {
        return results.get(row);
    }

}
