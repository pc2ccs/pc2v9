package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * Multiple Test Data set UI.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class MultipleDataSetPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -5975163495479418935L;

    private JPanel centerPane = null;

    private JPanel buttonPane = null;

    private MCLB testDataSetsListBox = null;

    private ProblemDataFiles problemDataFiles;

    /**
     * This method initializes
     * 
     */
    public MultipleDataSetPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(440, 229));
        this.add(getCenterPane(), BorderLayout.CENTER);
        this.add(getButtonPane(), BorderLayout.SOUTH);

    }

    @Override
    public String getPluginTitle() {
        return "Mulitple Data Set Pane";
    }

    public boolean isChanged() {
        // TODO loop through all text fields and check it out!!
        return false;
    }

    protected Object[] buildTestDataSetRow(int setNumber, SerializedFile judgeFile, SerializedFile answerFile) {

        int numberColumns = testDataSetsListBox.getColumnCount();
        Object[] columns = new String[numberColumns];

        // String [] cols = {"Set #", "Data File", "Answer File"};

        int i = 0;
        columns[i] = Integer.toString(setNumber);

        i++;

        if (judgeFile != null) {
            columns[i] = judgeFile.getName();
        } else {
            columns[i] = "";
        }

        i++;

        if (answerFile != null) {
            columns[i] = answerFile.getName();
        } else {
            columns[i] = "";
        }

        return columns;
    }

    public void setProblemDataFiles(ProblemDataFiles problemDataFiles) {

        this.problemDataFiles = problemDataFiles;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateUI();
            }
        });

    }

    protected void populateUI() {

        testDataSetsListBox.removeAllRows();

        SerializedFile[] judgeFiles = problemDataFiles.getJudgesDataFiles();
        SerializedFile[] answerFiles = problemDataFiles.getJudgesAnswerFiles();
        
        System.out.println("debug 22 - there are "+judgeFiles.length+" test data sets");

        for (int i = 0; i < judgeFiles.length; i++) {
            Object[] cols = buildTestDataSetRow(i + 1, judgeFiles[i], answerFiles[i]);
            testDataSetsListBox.addRow(cols, new Integer(i + 1));
        }
        
        testDataSetsListBox.autoSizeAllColumns();
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            centerPane = new JPanel();
            centerPane.setLayout(new BorderLayout());
            centerPane.add(getTestDataSetsListBox(), BorderLayout.CENTER);
        }
        return centerPane;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout());
        }
        return buttonPane;
    }

    /**
     * This method initializes testDataSetsListBox
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private MCLB getTestDataSetsListBox() {
        if (testDataSetsListBox == null) {
            testDataSetsListBox = new MCLB();

            String[] cols = { "Set #", "Data File", "Answer File" };

            testDataSetsListBox.addColumns(cols);

        }
        return testDataSetsListBox;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
