package edu.csus.ecs.pc2.ui;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import edu.csus.ecs.pc2.core.model.ProblemDataFiles;

// TODO need to figure out update key used when update data files 
// to avoid duping ProblemDataFiles on update.

public class TestCaseTableModel extends DefaultTableModel {

    private static String[] colNames = { "Test Case", "Data File", "Answer File" };

    private static Vector<String> columnNames = new Vector<String>(Arrays.asList(colNames));

    private ProblemDataFiles files;

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public TestCaseTableModel(ProblemDataFiles files) {
        super(null, columnNames);
        setFiles(files);
    }
    
    public TestCaseTableModel() {
        super(null, columnNames);
        setRowCount(0);
    }

    public void setFiles(ProblemDataFiles files) {
        if (files != null){
            setRowCount(files.getJudgesAnswerFiles().length);
            this.files = files;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {

        Object obj = "Unknown";

        switch (column) {
            case 0:
                obj = "" + (row + 1);
                break;
            case 1:
                obj = files.getJudgesDataFiles()[row].getName();
                break;
            case 2:
                obj = files.getJudgesAnswerFiles()[row].getName();
                break;
            default:
                break;

        }
        return obj;
    }
    
    public ProblemDataFiles getFiles() {
        return files;
    }

}
