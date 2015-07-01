package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

import javax.swing.JTabbedPane;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import java.awt.Color;

import javax.swing.JLabel;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * Multiple data set viewer pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: AutoJudgeSettingsPane.java 2825 2014-08-12 23:22:50Z boudreat $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/ui/AutoJudgeSettingsPane.java $
public class MultiTestSetOutputViewerPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 7363093989131251458L;

    private JPanel centerPanel = null;
    private JTable resultsTable;

    /**
     * This method initializes
     * 
     */
    public MultiTestSetOutputViewerPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(568, 363));

        this.add(getCenterPanel(), java.awt.BorderLayout.CENTER);
        
        // TODO Bug 918

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
    }

    public String getPluginTitle() {
        return "MultiTestSetOutputViewer Pane";
    }

    /**
     * This method initializes and returns the center panel (JPanel) containing the JTabbedPane
     * holding the output results and options panes.
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPanel() {
        if (centerPanel == null) {
            BorderLayout borderLayout = new BorderLayout();
            borderLayout.setVgap(0);
            centerPanel = new JPanel();
            centerPanel.setLayout(borderLayout);
            
            //put a tabbed pane in the center of the panel
            JTabbedPane multiTestSetTabbedPane = new JTabbedPane(JTabbedPane.TOP);
            multiTestSetTabbedPane.setName("multiTestSetTabbedPane");
            multiTestSetTabbedPane.setBorder(new LineBorder(new Color(0, 0, 0)));
            centerPanel.add(multiTestSetTabbedPane, BorderLayout.CENTER);
            
            //add a tab with a JPanel that will display the results for the test cases
            JPanel resultsPane = new JPanel();
            resultsPane.setName("ViewDataSets");
            multiTestSetTabbedPane.addTab("Data Set Results", null, resultsPane, "Show the results of this submission for each test data set");
            resultsPane.setLayout(new BorderLayout(0, 0));
            
            //add a header for holding labels to the results panel
            JPanel resultsPaneHeader = new JPanel();
            resultsPaneHeader.setBorder(new LineBorder(Color.BLUE, 2));
            resultsPane.add(resultsPaneHeader, BorderLayout.NORTH);
            
            //add a label to the header showing the Problem for which this set of test results applies
            //TODO: replace the following label text with the problem title from the current submission
            JLabel lblProblemTitle = new JLabel("Problem Title");
            resultsPaneHeader.add(lblProblemTitle);
            
            //put some space between the header labels
            Component horizontalStrut = Box.createHorizontalStrut(20);
            resultsPaneHeader.add(horizontalStrut);
            
            //add a label to the header showing the Team for which this set of test results applies
            //TODO: replace the following label text with the Team Name and Number from the current submission
            JLabel lblTeamName = new JLabel("Team Name");
            resultsPaneHeader.add(lblTeamName);
            
            //put some space between the header labels
            Component horizontalStrut_1 = Box.createHorizontalStrut(20);
            resultsPaneHeader.add(horizontalStrut_1);
            
            //add a label to the header showing the total number of test cases for this problem
           //TODO: replace the "XX" in the following label text with the actual number of test cases for the
            // problem in the current submission
            JLabel lblNumTestCases = new JLabel("Num Test Cases: XX");
            resultsPaneHeader.add(lblNumTestCases);
            
            //add a scrollpane to hold the table of results
            JScrollPane resultsScrollPane = new JScrollPane();
            resultsPane.add(resultsScrollPane, BorderLayout.CENTER);
            
            //define the column headers for the table of results
            final String[] columnNames = {"Data Set #", "Result", "Time(ms)", "Team Output", 
                    "Judge's Output", "Judge's Data" } ;
            
            //define the row data for the table of results
            //TODO: replace the following with code that loads the actual row data, including 
            // hyperlinks (labels) to open each output file, and also including 
            // additional links to "compare selected rows" (see Strawman diagram)
            final String[][] rowData = { 
                    {"1", "Pass", "100", "Link1", "Link2", "Link3"},
                    {"2", "Fail", "200", "Link1", "Link2", "Link3"}
            } ;
            
            //define a model for the table data
            //TODO: this model assumes all data are Strings; that's probably not a good idea
            // (see for example method setValueAt() )
            TableModel tableModel = new AbstractTableModel() {
                private static final long serialVersionUID = 1L;
                public String getColumnName(int col) {
                    return columnNames[col].toString();
                }
                public int getRowCount() { return rowData.length; }
                public int getColumnCount() { return columnNames.length; }
                public Object getValueAt(int row, int col) { return rowData[row][col]; }
                public boolean isCellEditable(int row, int col) { return false; }
                public void setValueAt(Object value, int row, int col) {
                    rowData[row][col] = (String) value;
                    fireTableCellUpdated(row, col);
                }
            };
            
            //add a table containing the test set results to the scrollpane in the tabbed pane's panel
            resultsTable = new JTable(tableModel);
            resultsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            resultsScrollPane.setViewportView(resultsTable);
            
            //TODO: add a bottom panel with a "Close" button
            

 
            //add a tab with a JPanel that will display options for managing the display of data for the test cases                        
            JPanel optionsPane = new JPanel();
            multiTestSetTabbedPane.addTab("Options", null, optionsPane, "Set options for tools used to display test set results");
            
            //TODO: add Strawman fields to Options pane
            
        }
        return centerPanel;
    }

    public void showMessage(final String message) {
        JOptionPane.showMessageDialog(this, message);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
