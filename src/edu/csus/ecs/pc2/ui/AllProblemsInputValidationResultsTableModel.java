package edu.csus.ecs.pc2.ui;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Problem.InputValidationStatus;
import edu.csus.ecs.pc2.core.model.inputValidation.InputValidationResult;
import edu.csus.ecs.pc2.core.model.inputValidation.ProblemInputValidationResults;

/**
 * Table Model for All Problems Input Validation Results
 * 
 * @author ICPC
 *
 */
public class AllProblemsInputValidationResultsTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 1L;

    private static String[] colNames = { "Select", "Problem", "Overall Result", "Pass/Fail Count", "Failed Files...", "Input Validator", "I.V. Command" };

    private static Vector<String> columnNames = new Vector<String>(Arrays.asList(colNames));
    
    private Vector<ProblemInputValidationResults>  results ;

    
    /**
     * Constructs a Table Model containing a set of {@link InputValidationResults} for each currently-defined contest problem.
     * 
     * @param results a table containing one row for each problem, with each row containing one {@link InputValidationResults} for each
     *      judge's input data file in the problem
     */
    public AllProblemsInputValidationResultsTableModel(Iterable<ProblemInputValidationResults> results) {
        super(null, columnNames);
        setResults(results);
    }
    
    public AllProblemsInputValidationResultsTableModel() {
        super(null, columnNames);
        setResults(null);
        setRowCount(0);
    }

    /**
     * Stores the specified {@link ProblemInputValidationResults} in this TableModel.
     * 
     * @param results the Input Validation Results to store
     */
    public void setResults(Iterable<ProblemInputValidationResults> results) {
        
        //create an empty Vector to hold the problem results
        this.results = new Vector<ProblemInputValidationResults>() ;
        
        if (results != null){
            
            //copy all the problem results to the vector
            Iterator<ProblemInputValidationResults> itr = results.iterator();
            while (itr.hasNext()) {
                this.results.add(itr.next());
            }
            //mark the size of the table according to how many problems/problemResults we got
            setRowCount(this.results.size());
            
        } else {
            //we got a null Iterable; we have no results data
            setRowCount(0);

        }
    }
    

    @Override
    public Object getValueAt(int row, int column) {

        Object obj = "Unknown";

        if (row >= this.results.size()) {
            return "Unknown";
        }
        
        ProblemInputValidationResults probResults = results.get(row);
        Problem prob = probResults.getProblem();

//        colNames = { "Select", "Problem", "Overall Result", "Pass/Fail Count", "Failed Files...", "Input Validator", "I.V. Command" };
        switch (column) {
            case 0:
                //"Select" column
                obj = new Boolean(true);
                break;
            case 1:
                //"Problem" column
                obj = prob.getShortName();
                break;
            case 2:
                //"Overall Result" column
                InputValidationStatus status = probResults.getProblem().getInputValidationStatus(); 
                switch (status) {
                    case FAILED:
                        obj = new Boolean(false);
                        break;
                    case PASSED:
                        obj = new Boolean(true);
                        break;
                    default:
                        obj = "??";
                }
                break;
            case 3:
                //"Pass/Fail Count" column
                //go through all the results for the problem in the specified row, tallying passed/failed results
                int passed = 0;
                int failed = 0;
                Iterable<InputValidationResult> results1 = probResults.getResults();
                for (InputValidationResult result : results1) {
                    
                    if (result.isPassed()) {
                        passed++;
                    } else {
                        failed++;
                    }
                }
                obj = "" + passed + "/" + failed;
                break;
            case 4:
                //"Failed Files..." column
                //go through all the results for the problem in the specified row, finding the names of all failed files
                String retStr = "";
                Iterable<InputValidationResult> results2 = probResults.getResults();
                for (InputValidationResult result : results2) {
                    if (!result.isPassed()) {
                        if (retStr.equals("")) {
                            retStr = result.getFullPathFilename();
                        } else {
                            retStr += ";" + result.getFullPathFilename();
                        }
                    }
                }
                if (retStr.equals("")) {
                    obj = "<none>";
                } else {
                    obj = retStr;
                }
                break;
            case 5:
                //"Input Validator" column
                if (prob.isProblemHasInputValidator() && prob.getInputValidatorProgramName() != null) {
                    obj = getBaseName(prob.getInputValidatorProgramName());
                } else {
                    obj = "<none>";
                }
                break;
            case 6: 
                //"I.V. Command" column
                if (prob.isProblemHasInputValidator() && prob.getInputValidatorCommandLine() != null) {
                    obj = getBaseName(prob.getInputValidatorCommandLine());
                } else {
                    obj = "<none>";
                }
                break;
            default:
                break;

        }
        return obj;
    }

    private String getBaseName(String fileName) {
        int index = fileName.lastIndexOf(File.separatorChar);
        if (index == -1) {
            //no file separator found; return original string
            return fileName;
        } else {
            return fileName.substring(index+1);
        }
    }

    /**
     * Remove the specified row from the table.  Note that row numbers start with zero!
     * 
     * @param row - the row number to be removed, where the first row is row zero
     */
    @Override
    public void removeRow(int row) {
        Vector< ProblemInputValidationResults>  newResults = new Vector< ProblemInputValidationResults>() ;

        for (int i=0; i<row; i++) {
            newResults.add(results.get(i));
        }
        for (int i=row+1; i<results.size(); i++) {
            newResults.add(results.get(i));
        }
        results = newResults;

        super.removeRow(row);
    }
    
    /**
     * Returns a {@link Iterable} version of the current {@link ProblemInputValidationResults} stored in the Table Model.
     * @return the current ProblemInputValidationResults
     */
    public Iterable<ProblemInputValidationResults> getResults() {
        return results;
    }
}
