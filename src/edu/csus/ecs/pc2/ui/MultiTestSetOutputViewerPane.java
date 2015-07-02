package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
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

    protected Component pulldownSelectComparator;
    private JTextField textUserSpecify;
    private JTextField textField;

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
 
            //set a centering renderer on the table columns
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
            
            for (int col = 0; col < resultsTable.getColumnCount(); col++) {
                resultsTable.getColumnModel().getColumn(col).setCellRenderer(centerRenderer);
            }
            
            //add a footer panel containing control buttons
            JPanel resultsPaneFooterPanel = new JPanel();
            resultsPane.add(resultsPaneFooterPanel, BorderLayout.SOUTH);
            
            //add a control button to invoke comparison of the team and judge output files for a selected row 
            JButton btnCompareSelected = new JButton("Compare Selected Outputs");
            resultsPaneFooterPanel.add(btnCompareSelected);
            
            Component horizontalGlue_3 = Box.createHorizontalGlue();
            horizontalGlue_3.setPreferredSize(new Dimension(20, 20));
            resultsPaneFooterPanel.add(horizontalGlue_3);
            
            //add a control button to dismiss the frame
            final JButton btnClose = new JButton("Close");
            btnClose.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Window parentFrame = SwingUtilities.getWindowAncestor(btnClose);
                    parentFrame.dispose();
                }
            });
            resultsPaneFooterPanel.add(btnClose);
            

            //add a tab that will display options for managing the display of data for the test cases                        
            JPanel optionsPane = new JPanel();
            multiTestSetTabbedPane.addTab("Options", null, optionsPane, "Set options for tools used to display test set results");
            optionsPane.setLayout(new BorderLayout(0, 0));
            
            //add a panel that will hold the various chooser options
            JPanel panelChoosers = new JPanel();
            optionsPane.add(panelChoosers, BorderLayout.CENTER);
            
            //add a panel that will support choosing the comparator tool to be used
            JPanel chooseComparatorPanel = new JPanel();
            panelChoosers.add(chooseComparatorPanel);
            chooseComparatorPanel.setBounds(new Rectangle(0, 0, 0, 20));
            chooseComparatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            chooseComparatorPanel.setPreferredSize(new Dimension(220, 200));
            chooseComparatorPanel.setBorder(new TitledBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), "Choose Compare Program", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            chooseComparatorPanel.setLayout(new BoxLayout(chooseComparatorPanel, BoxLayout.Y_AXIS));
            
            Component verticalStrut = Box.createVerticalStrut(20);
            verticalStrut.setMinimumSize(new Dimension(0, 10));
            verticalStrut.setPreferredSize(new Dimension(0, 15));
            chooseComparatorPanel.add(verticalStrut);
            
            //add a button to select the built-in comparator
            JRadioButton rdbtnInternalCompareProgram = new JRadioButton("Built-in Comparator");
            rdbtnInternalCompareProgram.setSelected(true);
            buttonGroup.add(rdbtnInternalCompareProgram);
            chooseComparatorPanel.add(rdbtnInternalCompareProgram);
            
            //add a button to select from a list of available comparators
            final JRadioButton rdbtnPulldownCompareList = new JRadioButton("Select");
            chooseComparatorPanel.add(rdbtnPulldownCompareList);
            rdbtnPulldownCompareList.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (rdbtnPulldownCompareList.isSelected()) {
                        pulldownSelectComparator.setEnabled(true);
                    } else {
                        pulldownSelectComparator.setEnabled(false);
                    }
                }
            });
            buttonGroup.add(rdbtnPulldownCompareList);
            
            //add a panel to hold the pulldown list which allows a user to select a comparator
            JPanel panelSelectComparator = new JPanel();
            FlowLayout flowLayout = (FlowLayout) panelSelectComparator.getLayout();
            flowLayout.setAlignOnBaseline(true);
            panelSelectComparator.setAlignmentX(Component.LEFT_ALIGNMENT);
            chooseComparatorPanel.add(panelSelectComparator);
            
            //construct a dropdown list of available comparators and add it to the panel
            pulldownSelectComparator = new JComboBox(getAvailableComparatorsList());
            pulldownSelectComparator.setEnabled(false);
            panelSelectComparator.add(pulldownSelectComparator);
            
            //add a button that allows the user to edit a text field identifying the comparator to be used
            final JRadioButton rdbtnSpecifyCompareProgram = new JRadioButton("User Specified");
            chooseComparatorPanel.add(rdbtnSpecifyCompareProgram);
            rdbtnSpecifyCompareProgram.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (rdbtnSpecifyCompareProgram.isSelected()) {
                        textUserSpecify.setEnabled(true);
                    } else {
                        textUserSpecify.setEnabled(false);
                    }
                }
            });
            buttonGroup.add(rdbtnSpecifyCompareProgram);
            
            //add a panel to hold the user-specified comparator text box
            JPanel panelSpecifyComparator = new JPanel();
            FlowLayout flowLayout_1 = (FlowLayout) panelSpecifyComparator.getLayout();
            flowLayout_1.setAlignOnBaseline(true);
            panelSpecifyComparator.setAlignmentX(Component.LEFT_ALIGNMENT);
            chooseComparatorPanel.add(panelSpecifyComparator);
            
            //add a text box for the user to specify the comparator
            textUserSpecify = new JTextField();
            textUserSpecify.setEnabled(false);
            textUserSpecify.setText("<enter comparator name>");
            panelSpecifyComparator.add(textUserSpecify);
            textUserSpecify.setColumns(15);
            
            Component horizontalStrut = Box.createHorizontalStrut(20);
            horizontalStrut.setMinimumSize(new Dimension(40, 0));
            panelChoosers.add(horizontalStrut);
            
            //create a panel to hold the various components allowing choice of viewers
            JPanel chooseViewerPanel = new JPanel();
            chooseViewerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelChoosers.add(chooseViewerPanel);
            chooseViewerPanel.setPreferredSize(new Dimension(200, 200));
            chooseViewerPanel.setBorder(new TitledBorder(null, "Choose Viewer Program", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            chooseViewerPanel.setLayout(new BoxLayout(chooseViewerPanel, BoxLayout.Y_AXIS));
            
            Component verticalStrut_1 = Box.createVerticalStrut(20);
            verticalStrut_1.setPreferredSize(new Dimension(0, 15));
            verticalStrut_1.setMinimumSize(new Dimension(0, 10));
            chooseViewerPanel.add(verticalStrut_1);
            
            //add a button for selecting the built-in viewer
            JRadioButton rdbtnInternalViewerProgram = new JRadioButton("Built-in Viewer");
            buttonGroup_1.add(rdbtnInternalViewerProgram);
            chooseViewerPanel.add(rdbtnInternalViewerProgram);
            
            //add a button for selecting the viewer from a list of available viewers
            JRadioButton rdbtnPulldownViewerList = new JRadioButton("Select");
            buttonGroup_1.add(rdbtnPulldownViewerList);
            chooseViewerPanel.add(rdbtnPulldownViewerList);
            
            //add a panel to hold the drop-down list of available viewers
            JPanel panel = new JPanel();
            chooseViewerPanel.add(panel);
            
            //add a drop-down list of available viewers
            JComboBox comboBox = new JComboBox(getAvailableViewersList());
            panel.add(comboBox);
            
            //add a button allowing the user to enable a textbox for typing the name of a viewer
            JRadioButton rdbtnSpecifyViewerProgram = new JRadioButton("User Specified");
            buttonGroup_1.add(rdbtnSpecifyViewerProgram);
            chooseViewerPanel.add(rdbtnSpecifyViewerProgram);
            
            //add a panel to hold the text box
            JPanel panel_1 = new JPanel();
            chooseViewerPanel.add(panel_1);
            
            //add a text field for the user to specify a viewer
            textField = new JTextField();
            textField.setText("<enter viewer name>");
            panel_1.add(textField);
            textField.setColumns(15);
            
            //add a footer panel to hold control buttons 
            JPanel panelFooterButtons = new JPanel();
            optionsPane.add(panelFooterButtons, BorderLayout.SOUTH);
            
            //add a control button to restore defaults
            JButton btnRestoreDefaults = new JButton("Restore Defaults");
            panelFooterButtons.add(btnRestoreDefaults);
            
            Component horizontalGlue_4 = Box.createHorizontalGlue();
            horizontalGlue_4.setPreferredSize(new Dimension(20, 20));
            panelFooterButtons.add(horizontalGlue_4);
            
            //add a button to save currently-selected options
            JButton btnUpdate = new JButton("Update");
            btnUpdate.setEnabled(false);
            panelFooterButtons.add(btnUpdate);
            
            Component horizontalGlue_5 = Box.createHorizontalGlue();
            horizontalGlue_5.setPreferredSize(new Dimension(20, 20));
            panelFooterButtons.add(horizontalGlue_5);
            
            //add a button to cancel, restoring former settings
            JButton btnCancel = new JButton("Cancel");
            btnCancel.setEnabled(false);
            panelFooterButtons.add(btnCancel);
            
            Component horizontalGlue_6 = Box.createHorizontalGlue();
            horizontalGlue_6.setPreferredSize(new Dimension(20, 20));
            panelFooterButtons.add(horizontalGlue_6);
            
            //add a button to dismiss the frame
            JButton btnClose_1 = new JButton("Close");
            btnClose_1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Window parentFrame = SwingUtilities.getWindowAncestor(btnClose);
                    parentFrame.dispose();
                }
            });
            panelFooterButtons.add(btnClose_1);
            
        }
        return centerPanel;
    }
    
    
    /**
     * Returns an array of Strings listing the names of available (known)
     * output viewer tools.
     * 
     * @return a String array of viewer tool names
     */
    private String[] getAvailableViewersList() {
        // TODO figure out how to coordinate this with actual known viewers
        return new String [] {
                "gvim", "notepad", "write", "Another Viewer Tool"
        };
    }

    /**
     * Returns an array of Strings listing the names of available (known)
     * output comparator tools.
     * 
     * @return a String array of comparator tool names
     */
    private String[] getAvailableComparatorsList() {
        // TODO figure out how to coordinate this with actual known comparators
        return new String [] {
                "diff", "GVimDiff", "Another Diff Tool"
        };
    }

    /**
     * Returns an array of String arrays defining the rows of data to be entered into
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
