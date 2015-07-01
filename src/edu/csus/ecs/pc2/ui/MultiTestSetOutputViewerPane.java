package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

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
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private final ButtonGroup buttonGroup_1 = new ButtonGroup();

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
            JPanel resultsPaneHeaderPanel = new JPanel();
            resultsPaneHeaderPanel.setBorder(new LineBorder(Color.BLUE, 2));
            resultsPane.add(resultsPaneHeaderPanel, BorderLayout.NORTH);
            
            //add a label to the header showing the Problem for which this set of test results applies
            //TODO: replace the following label text with the problem title from the current submission
            JLabel lblProblemTitle = new JLabel("Problem: XXX");
            resultsPaneHeaderPanel.add(lblProblemTitle);
            
            Component horizontalGlue = Box.createHorizontalGlue();
            horizontalGlue.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue);
            
            //add a label to the header showing the Team for which this set of test results applies
            //TODO: replace the following label text with the Team Name and Number from the current submission
            JLabel lblTeamName = new JLabel("Team: XXX");
            resultsPaneHeaderPanel.add(lblTeamName);
            
            Component horizontalGlue_1 = Box.createHorizontalGlue();
            horizontalGlue_1.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue_1);
            
            JLabel lblRunID = new JLabel("Run ID: XXX");
            resultsPaneHeaderPanel.add(lblRunID);
            
            Component horizontalGlue_2 = Box.createHorizontalGlue();
            horizontalGlue_2.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue_2);
            
            //add a label to the header showing the total number of test cases for this problem
           //TODO: replace the "XX" in the following label text with the actual number of test cases for the
            // problem in the current submission
            JLabel lblNumTestCases = new JLabel("Num Test Cases: XXX");
            resultsPaneHeaderPanel.add(lblNumTestCases);
            
            //add a scrollpane to hold the table of results
            JScrollPane resultsScrollPane = new JScrollPane();
            resultsPane.add(resultsScrollPane, BorderLayout.CENTER);
            
            //define the column headers for the table of results
            final String[] columnNames = {"Data Set #", "Result", "Time(ms)", "Team Output", 
                    "Judge's Output", "Judge's Data" } ;
            
            //get the row data for the table of results
            final String[][] rowData = getRowData() ;
            
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
            
            JPanel resultsPaneFooterPanel = new JPanel();
            resultsPane.add(resultsPaneFooterPanel, BorderLayout.SOUTH);
            
            JButton btnCompareSelected = new JButton("Compare Selected Outputs");
            resultsPaneFooterPanel.add(btnCompareSelected);
            
            Component horizontalGlue_3 = Box.createHorizontalGlue();
            horizontalGlue_3.setPreferredSize(new Dimension(20, 20));
            resultsPaneFooterPanel.add(horizontalGlue_3);
            
            final JButton btnClose = new JButton("Close");
            btnClose.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Window parentFrame = SwingUtilities.getWindowAncestor(btnClose);
                    parentFrame.dispose();
                }
            });
            resultsPaneFooterPanel.add(btnClose);
            
            //set a centering renderer on the table columns
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
            
            for (int col = 0; col < resultsTable.getColumnCount(); col++) {
                resultsTable.getColumnModel().getColumn(col).setCellRenderer(centerRenderer);
            }
//            resultsTable.setDefaultRenderer(String.class, centerRenderer);
            
            //TODO: add a bottom panel with a "Close" button
            

 
            //add a tab with a JPanel that will display options for managing the display of data for the test cases                        
            JPanel optionsPane = new JPanel();
            multiTestSetTabbedPane.addTab("Options", null, optionsPane, "Set options for tools used to display test set results");
            
            JPanel chooseCompareProgramPanel = new JPanel();
            chooseCompareProgramPanel.setPreferredSize(new Dimension(200, 200));
            chooseCompareProgramPanel.setBorder(new TitledBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), "Choose Compare Program", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            optionsPane.add(chooseCompareProgramPanel);
            chooseCompareProgramPanel.setLayout(new BoxLayout(chooseCompareProgramPanel, BoxLayout.Y_AXIS));
            
            JRadioButton rdbtnInternalCompareProgram = new JRadioButton("Built-in Comparator");
            buttonGroup.add(rdbtnInternalCompareProgram);
            chooseCompareProgramPanel.add(rdbtnInternalCompareProgram);
            
            JRadioButton rdbtnPulldownCompareList = new JRadioButton("Select");
            buttonGroup.add(rdbtnPulldownCompareList);
            chooseCompareProgramPanel.add(rdbtnPulldownCompareList);
            
            JRadioButton rdbtnSpecifyCompareProgram = new JRadioButton("User Specified");
            buttonGroup.add(rdbtnSpecifyCompareProgram);
            chooseCompareProgramPanel.add(rdbtnSpecifyCompareProgram);
            
            JPanel chooseViewerProgramPanel = new JPanel();
            chooseViewerProgramPanel.setPreferredSize(new Dimension(200, 200));
            chooseViewerProgramPanel.setBorder(new TitledBorder(null, "Choose Viewer Program", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            optionsPane.add(chooseViewerProgramPanel);
            chooseViewerProgramPanel.setLayout(new BoxLayout(chooseViewerProgramPanel, BoxLayout.Y_AXIS));
            
            JRadioButton rdbtnInternalViewerProgram = new JRadioButton("Built-in Viewer");
            buttonGroup_1.add(rdbtnInternalViewerProgram);
            chooseViewerProgramPanel.add(rdbtnInternalViewerProgram);
            
            JRadioButton rdbtnPulldownViewerList = new JRadioButton("Select");
            buttonGroup_1.add(rdbtnPulldownViewerList);
            chooseViewerProgramPanel.add(rdbtnPulldownViewerList);
            
            JRadioButton rdbtnSpecifyViewerProgram = new JRadioButton("User Specified");
            buttonGroup_1.add(rdbtnSpecifyViewerProgram);
            chooseViewerProgramPanel.add(rdbtnSpecifyViewerProgram);
            
            //TODO: add Strawman fields to Options pane
            
        }
        return centerPanel;
    }
    
    /**
     * Returns an array of Strings defining the rows of data to be entered into
     * the test case results table.
     * 
     * @return an array of arrays of Strings defining the table data
     */
    private String [][] getRowData() {
        
        //TODO: replace the following with code that gets the actual row data from the model, 
        // including creating hyperlinks (labels) to open each output file, and also including 
        // additional links to "compare selected rows" (see Strawman diagram)
        return new String[][]  { 
                {"1", "Pass", "100", "Link1", "Link2", "Link3"},
                {"2", "Fail", "200", "Link1", "Link2", "Link3"},
                {"3", "Pass", "100", "Link1", "Link2", "Link3"},
                {"4", "Fail", "150", "Link1", "Link2", "Link3"},
                {"5", "Fail", "50", "Link1", "Link2", "Link3"},
                {"6", "Pass", "100", "Link1", "Link2", "Link3"},
                {"7", "Fail", "1000", "Link1", "Link2", "Link3"},
                {"8", "Pass", "100", "Link1", "Link2", "Link3"},
                {"9", "Fail", "1500", "Link1", "Link2", "Link3"},
                {"10", "Fail", "10", "Link1", "Link2", "Link3"},
       } ;
    }

    public void showMessage(final String message) {
        JOptionPane.showMessageDialog(this, message);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
