package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
     * Intial number of rows that were created, from test data sets.
     */
    private int intialNumberOfRows = 0;
    private JButton btnAddSet;
    private JButton btnRemoveRow;

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

        MCLB box = getTestDataSetsListBox();

        if (intialNumberOfRows != box.getRowCount()) {
            return true;
        }

        for (int i = 0; i < box.getRowCount(); i++) {

            // String [] cols = {"Set #", "Data File", "Answer File"};
            Object[] row = box.getRow(i);

            JTextFieldSerializedFile textFile = (JTextFieldSerializedFile) row[1];

            if (textFile.isChanged()) {
                return true;
            }

            textFile = (JTextFieldSerializedFile) row[2];

            if (textFile.isChanged()) {
                return true;
            }
        }

        return false;
    }

    protected Object[] buildTestDataSetRow(int setNumber, SerializedFile judgeFile, SerializedFile answerFile) {

        int numberColumns = testDataSetsListBox.getColumnCount();
        Object[] columns = new Object[numberColumns];

        // String [] cols = {"Set #", "Data File", "Answer File"};

        int i = 0;
        columns[i] = Integer.toString(setNumber);

        i++;

        if (judgeFile != null) {
            columns[i] = new JTextFieldSerializedFile(judgeFile);
        } else {
            columns[i] = new JTextFieldSerializedFile();
        }

        i++;

        if (answerFile != null) {
            columns[i] = new JTextFieldSerializedFile(answerFile);
        } else {
            columns[i] = new JTextFieldSerializedFile();
        }

        return columns;
    }

    public void setProblemDataFiles(ProblemDataFiles problemDataFiles) {

        this.problemDataFiles = problemDataFiles;
        
        if (problemDataFiles == null){
            intialNumberOfRows = 0;
        } else {
            SerializedFile[] files = problemDataFiles.getJudgesAnswerFiles();
            if (files != null) {
                intialNumberOfRows = files.length;
            } else if (problemDataFiles.getJudgesDataFile() != null && problemDataFiles.getJudgesAnswerFile() != null) {
                intialNumberOfRows = 1;
            } else {
                intialNumberOfRows = 0;
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateUI();
            }
        });

    }

    protected void populateUI() {

        testDataSetsListBox.removeAllRows();
        
        if (problemDataFiles != null){
            
            
            SerializedFile[] judgeFiles = problemDataFiles.getJudgesDataFiles();
            SerializedFile[] answerFiles = problemDataFiles.getJudgesAnswerFiles();
            
            if (judgeFiles == null || judgeFiles.length == 0){
                // No data in the data set.
                judgeFiles = new SerializedFile[1];
                answerFiles = new SerializedFile[1];
                
                judgeFiles[0] = problemDataFiles.getJudgesDataFile();
                answerFiles[0] = problemDataFiles.getJudgesAnswerFile();
            } 
            
            for (int i = 0; i < judgeFiles.length; i++) {
                addSetRow(i + 1, judgeFiles[i], answerFiles[i]);
            }
            
        }

        testDataSetsListBox.autoSizeAllColumns();
    }

    private void addSetRow(int rowNumber, SerializedFile serializedFile, SerializedFile answerSerializedFile) {
        Object[] cols = buildTestDataSetRow(rowNumber, serializedFile, answerSerializedFile);
        testDataSetsListBox.addRow(cols, new Integer(rowNumber));
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
            buttonPane.setMinimumSize(new Dimension(25, 25));
            FlowLayout fl_buttonPane = new FlowLayout();
            fl_buttonPane.setHgap(45);
            buttonPane.setLayout(fl_buttonPane);
            buttonPane.add(getBtnAddSet());
            buttonPane.add(getBtnRemoveRow());
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

    private JButton getBtnAddSet() {
        if (btnAddSet == null) {
        	btnAddSet = new JButton("Add Row");
        	btnAddSet.setToolTipText("Add row of data files");
        	btnAddSet.setMnemonic(java.awt.event.KeyEvent.VK_D);
        	btnAddSet.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) {
        	        addSetRow(getTestDataSetsListBox().getRowCount()+1, null, null);
        	    }
        	});
        }
        return btnAddSet;
    }
    private JButton getBtnRemoveRow() {
        if (btnRemoveRow == null) {
        	btnRemoveRow = new JButton("Remove Row");
        	btnRemoveRow.setToolTipText("Remove selectec row");
        	btnRemoveRow.setMnemonic(java.awt.event.KeyEvent.VK_R);
        	btnRemoveRow.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int rowNumber = getTestDataSetsListBox().getSelectedColumnIndex();
                    if (rowNumber != -1){
                        getTestDataSetsListBox().removeRow(rowNumber);
                    }
                }
            });
        }
        return btnRemoveRow;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
