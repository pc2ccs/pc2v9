package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;

/**
 * Multiple Test Data set UI.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// TODO 917 make font larger

// $HeadURL$
public class MultipleDataSetPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -5975163495479418935L;

    private JPanel centerPane = null;

//    private JPanel buttonPane = null;

//    private MCLB testDataSetsListBox = null;

    private TestCaseTableModel tableModel = new TestCaseTableModel();

    private JTable testDataSetsListBox = null;

    private ProblemDataFiles problemDataFiles;

    /**
     * Intial number of rows that were created, from test data sets.
     */
//    private int intialNumberOfRows = 0;
//    private JButton btnAddSet;
//    private JButton btnRemoveRow;

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
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(35, 35));
        add(buttonPanel, BorderLayout.SOUTH);
//        this.add(getButtonPane(), BorderLayout.SOUTH);

    }

    @Override
    public String getPluginTitle() {
        return "Mulitple Data Set Pane";
    }

//    protected Object[] buildTestDataSetRow(int setNumber, SerializedFile judgeFile, SerializedFile answerFile) {
//
//        int numberColumns = testDataSetsListBox.getColumnCount();
//        Object[] columns = new Object[numberColumns];
//
//        // String [] cols = {"Set #", "Data File", "Answer File"};
//
//        int i = 0;
//        columns[i] = Integer.toString(setNumber);
//
//        i++;
//
//        if (judgeFile != null) {
//            columns[i] = new JTextFieldSerializedFile(judgeFile);
//        } else {
//            columns[i] = new JTextFieldSerializedFile();
//        }
//
//        i++;
//
//        if (answerFile != null) {
//            columns[i] = new JTextFieldSerializedFile(answerFile);
//        } else {
//            columns[i] = new JTextFieldSerializedFile();
//        }
//
//        return columns;
//    }

    /**
     * Clone data files and populate in pane.
     * 
     * @param problemDataFiles
     * @throws CloneNotSupportedException
     */
    public void setProblemDataFiles(ProblemDataFiles problemDataFiles) throws CloneNotSupportedException  {
        
        Problem problem = getContest().getProblem(problemDataFiles.getProblemId());
        this.problemDataFiles = problemDataFiles.copy(problem);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateUI();
            }
        });

    }

    protected void populateUI() {

        if (problemDataFiles != null) {
            tableModel.setFiles(problemDataFiles);
            tableModel.fireTableDataChanged();
            System.out.println("debug 22 fire data changted "+problemDataFiles);
        }

        // TODO 917
//        testDataSetsListBox.autoSizeAllColumns();
    }

//    private void addSetRow(int rowNumber, SerializedFile serializedFile, SerializedFile answerSerializedFile) {
//        Object[] cols = buildTestDataSetRow(rowNumber, serializedFile, answerSerializedFile);
//        testDataSetsListBox.addRow(cols, new Integer(rowNumber));
//    }

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

//    /**
//     * This method initializes buttonPane
//     * 
//     * @return javax.swing.JPanel
//     */
//    private JPanel getButtonPane() {
//        if (buttonPane == null) {
//            buttonPane = new JPanel();
//            buttonPane.setMinimumSize(new Dimension(25, 25));
//            FlowLayout flbuttonPane = new FlowLayout();
//            flbuttonPane.setHgap(45);
//            buttonPane.setLayout(flbuttonPane);
//            buttonPane.add(getBtnAddSet());
//            buttonPane.add(getBtnRemoveRow());
//        }
//        return buttonPane;
//    }

    /**
     * This method initializes testDataSetsListBox
     * 
     * @return edu.csus.ecs.pc2.ui.MCLB
     */
    private JTable getTestDataSetsListBox() {
        if (testDataSetsListBox == null) {
            testDataSetsListBox = new JTable(tableModel);

        }
        return testDataSetsListBox;
    }
    
    public ProblemDataFiles getProblemDataFiles () {
        return tableModel.getFiles();
    }




//    private JButton getBtnAddSet() {
//        if (btnAddSet == null) {
//            btnAddSet = new JButton("Add Row");
//            btnAddSet.setToolTipText("Add row of data files");
//            btnAddSet.setMnemonic(java.awt.event.KeyEvent.VK_D);
//            btnAddSet.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    addSetRow(getTestDataSetsListBox().getRowCount()+1, null, null);
//                }
//            });
//        }
//        return btnAddSet;
//    }
//    private JButton getBtnRemoveRow() {
//        if (btnRemoveRow == null) {
//            btnRemoveRow = new JButton("Remove Row");
//            btnRemoveRow.setToolTipText("Remove selectec row");
//            btnRemoveRow.setMnemonic(java.awt.event.KeyEvent.VK_R);
//            btnRemoveRow.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    int rowNumber = getTestDataSetsListBox().getSelectedIndex();
//                    if (rowNumber != -1){
//                        getTestDataSetsListBox().removeRow(rowNumber);
//                    }
//                }
//            });
//        }
//        return btnRemoveRow;
//    }
} // @jve:decl-index=0:visual-constraint="10,10"
