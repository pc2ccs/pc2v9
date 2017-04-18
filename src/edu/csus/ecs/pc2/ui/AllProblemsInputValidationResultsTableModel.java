package edu.csus.ecs.pc2.ui;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class AllProblemsInputValidationResultsTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 1L;

    private static String[] colNames = { "Select", "Problem", "Overall Result", "Pass/Fail Count", "Failed Files...", "Input Validator", "I.V. Command" };

    private static Vector<String> columnNames = new Vector<String>(Arrays.asList(colNames));
    
    private Vector< Vector<InputValidationResult> > results ;

    
    /**
     * Constructs a Table Model containing a set of {@link InputValidationResults} for each currently-defined contest problem.
     * 
     * @param results a table containing one row for each problem, with each row containing one {@link InputValidationResults} for each
     *      judge's input data file in the problem
     */
    public AllProblemsInputValidationResultsTableModel(Vector <Vector<InputValidationResult>> results) {
        super(null, columnNames);
        setResults(results);
    }
    
    public AllProblemsInputValidationResultsTableModel() {
        super(null, columnNames);
        setResults(null);
        setRowCount(0);
    }

    public void setResults(Vector <Vector<InputValidationResult>> results) {
        if (results != null){
            setRowCount(results.size());
            this.results = results;
        } else {
            setRowCount(0);
            this.results = null;
        }
    }
    

    @Override
    public Object getValueAt(int row, int column) {

        Object obj = "Unknown";

        Vector<InputValidationResult> probResults = results.get(row);

//        colNames = { "Select", "Problem", "Overall Result", "Pass/Fail Count", "Failed Files...", "Input Validator", "I.V. Command" };
        switch (column) {
            case 0:
                obj = new Boolean(true);
                break;
            case 1:
                //all the InputValidationResults in the specified row should be for the same Problem; get the Problem name out of the 1st element
                obj = probResults.get(0).getProblem().getShortName();
                break;
            case 2:
                //the Input Validation status for the problem should be the same for all Results in the specified row
                obj = probResults.get(0).getProblem().getInputValidationStatus();
                break;
            case 3:
                //go through all the results for the problem in the specified row, tallying passed/failed results
                int passed = 0;
                int failed = 0;
                for (int i=0; i<probResults.size(); i++) {
                    if (probResults.get(i).isPassed()) {
                        passed++;
                    } else {
                        failed++;
                    }
                }
                obj = "" + passed + "/" + failed;
                break;
            case 4:
                //go through all the results for the problem in the specified row, finding the names of all failed files
                String retStr = "";
                for (int i=0; i<probResults.size(); i++) {
                    if (!probResults.get(i).isPassed()) {
                        if (retStr.equals("")) {
                            retStr = probResults.get(i).getFullPathFilename();
                        } else {
                            retStr += ";" + probResults.get(i).getFullPathFilename();
                        }
                    }
                }
                obj = retStr;
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
        Vector< Vector<InputValidationResult> > newResults = new Vector< Vector<InputValidationResult>>() ;

        for (int i=0; i<row; i++) {
            newResults.add(results.get(i));
        }
        for (int i=row+1; i<results.size(); i++) {
            newResults.add(results.get(i));
        }
        results = newResults;

        super.removeRow(row);
    }
    
    public Vector< Vector<InputValidationResult> > getResults() {
        return results;
    }
}
