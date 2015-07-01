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
public class MultiSetOutputViewerPane extends JPanePlugin {

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
    public MultiSetOutputViewerPane() {
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
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPanel() {
        if (centerPanel == null) {
            BorderLayout borderLayout = new BorderLayout();
            borderLayout.setVgap(0);
            centerPanel = new JPanel();
            centerPanel.setLayout(borderLayout);
            
            JTabbedPane multiSetTabbedPane = new JTabbedPane(JTabbedPane.TOP);
            multiSetTabbedPane.setName("multiTestSetTabbedPane");
            multiSetTabbedPane.setBorder(new LineBorder(new Color(0, 0, 0)));
            centerPanel.add(multiSetTabbedPane, BorderLayout.CENTER);
            
            JPanel resultsPane = new JPanel();
            resultsPane.setName("ViewDataSets");
            multiSetTabbedPane.addTab("Data Set Results", null, resultsPane, "Show the results of this submission for each test data set");
            resultsPane.setLayout(new BorderLayout(0, 0));
            
            JPanel resultsPaneHeader = new JPanel();
            resultsPaneHeader.setBorder(new LineBorder(Color.BLUE, 2));
            resultsPane.add(resultsPaneHeader, BorderLayout.NORTH);
            
            JLabel lblProblemTitle = new JLabel("Problem Title");
            resultsPaneHeader.add(lblProblemTitle);
            
            Component horizontalStrut = Box.createHorizontalStrut(20);
            resultsPaneHeader.add(horizontalStrut);
            
            JLabel lblTeamName = new JLabel("Team Name");
            resultsPaneHeader.add(lblTeamName);
            
            Component horizontalStrut_1 = Box.createHorizontalStrut(20);
            resultsPaneHeader.add(horizontalStrut_1);
            
            JLabel lblNumTestCases = new JLabel("Num Test Cases");
            resultsPaneHeader.add(lblNumTestCases);
            
            JScrollPane resultsScrollPane = new JScrollPane();
            resultsPane.add(resultsScrollPane, BorderLayout.CENTER);
            
            final String[] columnNames = {"Data Set #", "Result", "Time(ms)", "Team Output", 
                    "Judge's Output", "Judge's Data" } ;
            
            //TODO: replace the following with code that loads the actual row data
            final String[][] rowData = { 
                    {"1", "Pass", "100", "Link1", "Link2", "Link3"},
                    {"2", "Fail", "200", "Link1", "Link2", "Link3"}
            } ;
            
            TableModel tableModel = new AbstractTableModel() {
                public String getColumnName(int col) {
                    return columnNames[col].toString();
                }
                public int getRowCount() { return rowData.length; }
                public int getColumnCount() { return columnNames.length; }
                public Object getValueAt(int row, int col) {
                    return rowData[row][col];
                }
                public boolean isCellEditable(int row, int col) { return false; }
                public void setValueAt(Object value, int row, int col) {
                    rowData[row][col] = (String) value;
                    fireTableCellUpdated(row, col);
                }
            };
            
            resultsTable = new JTable(tableModel);
            resultsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            resultsScrollPane.setViewportView(resultsTable);
            

 
                        
            JPanel optionsPane = new JPanel();
            multiSetTabbedPane.addTab("Options", null, optionsPane, "Set options for tools used to display submission results");
        }
        return centerPanel;
    }

    public void showMessage(final String message) {
        JOptionPane.showMessageDialog(this, message);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
