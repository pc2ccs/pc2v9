package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

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
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private final ButtonGroup buttonGroup_1 = new ButtonGroup();

    protected Component pulldownSelectComparator;
    private JTextField textUserSpecifyComparator;
    private JTextField textUserSpecifyViewer;

    private JComboBox pulldownSelectViewer;

    private JButton btnCompareSelected;

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
            JLabel lblNumTestCases = new JLabel("Test Cases: XXX");
            resultsPaneHeaderPanel.add(lblNumTestCases);
            
            Component horizontalGlue_7 = Box.createHorizontalGlue();
            horizontalGlue_7.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue_7);
            
            JLabel lblNumFailedTestCases = new JLabel("Failed Test Cases: XXX");
            resultsPaneHeaderPanel.add(lblNumFailedTestCases);
            
            //add a scrollpane to hold the table of results
            JScrollPane resultsScrollPane = new JScrollPane();
            resultsPane.add(resultsScrollPane, BorderLayout.CENTER);
            
            //get the table of results and put it in the scrollpane
            resultsTable = getResultsTable();
            resultsScrollPane.setViewportView(resultsTable);
 
            
            //add a footer panel containing control buttons
            JPanel resultsPaneFooterPanel = new JPanel();
            resultsPane.add(resultsPaneFooterPanel, BorderLayout.SOUTH);
            
            //add a control button to invoke comparison of the team and judge output files for a selected row 
            btnCompareSelected = new JButton("Compare Selected");
            btnCompareSelected.setToolTipText("Show comparison between Team and Judge output for selected row (only one row may be selected at a time)");
            btnCompareSelected.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //display a comparison between the Team and Judge output in the selected table row
                    showComparison(resultsTable.getSelectedRows()); 
                }
            });
            btnCompareSelected.setEnabled(false);
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
                        textUserSpecifyComparator.setEnabled(true);
                    } else {
                        textUserSpecifyComparator.setEnabled(false);
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
            textUserSpecifyComparator = new JTextField();
            textUserSpecifyComparator.setEnabled(false);
            textUserSpecifyComparator.setText("<enter comparator name>");
            panelSpecifyComparator.add(textUserSpecifyComparator);
            textUserSpecifyComparator.setColumns(15);
            
            Component horizontalStrut = Box.createHorizontalStrut(20);
            horizontalStrut.setMinimumSize(new Dimension(40, 0));
            panelChoosers.add(horizontalStrut);
            
            //create a panel to hold the various components allowing choice of viewers
            JPanel chooseViewerPanel = new JPanel();
            chooseViewerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panelChoosers.add(chooseViewerPanel);
            chooseViewerPanel.setPreferredSize(new Dimension(220, 200));
            chooseViewerPanel.setBorder(new TitledBorder(null, "Choose Viewer Program", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            chooseViewerPanel.setLayout(new BoxLayout(chooseViewerPanel, BoxLayout.Y_AXIS));
            
            Component verticalStrut_1 = Box.createVerticalStrut(20);
            verticalStrut_1.setPreferredSize(new Dimension(0, 15));
            verticalStrut_1.setMinimumSize(new Dimension(0, 10));
            chooseViewerPanel.add(verticalStrut_1);
            
            //add a button for selecting the built-in viewer
            JRadioButton rdbtnInternalViewerProgram = new JRadioButton("Built-in Viewer");
            rdbtnInternalViewerProgram.setSelected(true);
            rdbtnInternalViewerProgram.setPreferredSize(new Dimension(117, 23));
            rdbtnInternalViewerProgram.setMinimumSize(new Dimension(117, 23));
            rdbtnInternalViewerProgram.setMaximumSize(new Dimension(117, 23));
            buttonGroup_1.add(rdbtnInternalViewerProgram);
            chooseViewerPanel.add(rdbtnInternalViewerProgram);
            
            //add a button for selecting the viewer from a list of available viewers
            final JRadioButton rdbtnPulldownViewerList = new JRadioButton("Select");
            rdbtnPulldownViewerList.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (rdbtnPulldownViewerList.isSelected()) {
                        pulldownSelectViewer.setEnabled(true);
                    } else {
                        pulldownSelectViewer.setEnabled(false);
                    }
               }
            });
            buttonGroup_1.add(rdbtnPulldownViewerList);
            chooseViewerPanel.add(rdbtnPulldownViewerList);
            
            //add a panel to hold the drop-down list of available viewers
            JPanel panel = new JPanel();
            panel.setAlignmentX(Component.LEFT_ALIGNMENT);
            chooseViewerPanel.add(panel);
            
            //add a drop-down list of available viewers
            pulldownSelectViewer = new JComboBox(getAvailableViewersList());
            pulldownSelectViewer.setEnabled(false);
            panel.add(pulldownSelectViewer);
            
            //add a button allowing the user to enable a textbox for typing the name of a viewer
            final JRadioButton rdbtnSpecifyViewerProgram = new JRadioButton("User Specified");
            rdbtnSpecifyViewerProgram.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (rdbtnSpecifyViewerProgram.isSelected()) {
                        textUserSpecifyViewer.setEnabled(true);
                    } else {
                        textUserSpecifyViewer.setEnabled(false);
                    }
                }
            });
            buttonGroup_1.add(rdbtnSpecifyViewerProgram);
            chooseViewerPanel.add(rdbtnSpecifyViewerProgram);
            
            //add a panel to hold the text box
            JPanel panel_1 = new JPanel();
            panel_1.setEnabled(false);
            chooseViewerPanel.add(panel_1);
            
            //add a text field for the user to specify a viewer
            textUserSpecifyViewer = new JTextField();
            textUserSpecifyViewer.setEnabled(false);
            textUserSpecifyViewer.setText("<enter viewer name>");
            panel_1.add(textUserSpecifyViewer);
            textUserSpecifyViewer.setColumns(15);
            
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
     * Returns a JTable containing the results information for each test case.
     * The method sets not only the table data but the appropriate cell renderers
     * and action/mouse listeners for the table.
     */
    private JTable getResultsTable() {
        
        final int COLUMN_SELECT = 0;
        final int COLUMN_DATASET = 1;
        final int COLUMN_RESULT = 2;
        final int COLUMN_TIME = 3;
        final int COLUMN_TEAM_OUTPUT = 4;
        final int COLUMN_JUDGE_OUTPUT = 5;
        final int COLUMN_JUDGE_DATA = 6 ;
        //yes, I know about enums... you can't define one in this scope :(
        
        final JTable resultsTable ;
        
        //define the column headers for the table of results
        final String[] columnNames = {"Select", "Data Set #", "Result", "Time(ms)", "Team Output", 
                "Judge's Output", "Judge's Data" } ;
        
        //get the row data for the table of results
        final Object[][] tableData = getTableData() ;
        
//        //define a model for the table data
//        //TODO: this model assumes all data are Strings; that's probably not a good idea
//        // (see for example method setValueAt() )
//        TableModel tableModel = new AbstractTableModel() {
//            private static final long serialVersionUID = 1L;
//            public String getColumnName(int col) {
//                return columnNames[col].toString();
//            }
//            public int getRowCount() { return tableData.length; }
//            public int getColumnCount() { return columnNames.length; }
//            public Object getValueAt(int row, int col) { return tableData[row][col]; }
//            public boolean isCellEditable(int row, int col) { return false; }
//            public void setValueAt(Object value, int row, int col) {
//                tableData[row][col] = (String) value;
//                fireTableCellUpdated(row, col);
//            }
//        };
//        
//        resultsTable = new JTable(tableModel);
        
        resultsTable = new JTable(tableData, columnNames);
        resultsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        resultsTable.setFillsViewportHeight(true);
        resultsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            //insure "compare" button is only enabled when exactly ONE table row is selected
            public void valueChanged(ListSelectionEvent e) {
                if (resultsTable.getSelectedRowCount() >= 1) {
                    btnCompareSelected.setEnabled(true);
                } else {
                    btnCompareSelected.setEnabled(false);
                }
                
            }
        });
        
        //set a centering renderer on desired table columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
        resultsTable.getColumnModel().getColumn(COLUMN_SELECT).setCellRenderer(new CheckBoxRenderer());
        resultsTable.getColumnModel().getColumn(COLUMN_DATASET).setCellRenderer(centerRenderer);
        
        //set a LinkRenderer on those cells containing links
        resultsTable.getColumnModel().getColumn(COLUMN_TEAM_OUTPUT).setCellRenderer(new LinkRenderer());
        resultsTable.getColumnModel().getColumn(COLUMN_JUDGE_OUTPUT).setCellRenderer(new LinkRenderer());
        resultsTable.getColumnModel().getColumn(COLUMN_JUDGE_DATA).setCellRenderer(new LinkRenderer());

        
        resultsTable.getColumnModel().getColumn(COLUMN_RESULT).setCellRenderer(new PassFailCellRenderer());
        
        //force table column widths to nice values
//        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        resultsTable.getColumnModel().getColumn(COLUMN_SELECT).setPreferredWidth(45);
        
        //add a listener to allow users to click an output or data file name and display it
        resultsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                
                if (column>=COLUMN_TEAM_OUTPUT && column<COLUMN_JUDGE_DATA) {
                    showListing (row, column);
                }
            }
          });
        
        return resultsTable;
    }
    
    /**
     * Uses the currently-defined Viewer to display output file listed in the specified table row/col.
     * @param row - the selected table row (0-based)
     * @param col - the selected table column (0-based)
     */
    private void showListing(int row, int col) {
        int dataSet = row+1;
        String outputType = col==3?"Team Output":col==4?"Judge's Output":"Judge's Data";
        System.out.println ("Would have displayed " + outputType + " for Data Set " + dataSet);
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
     * Returns a set of Objects defining the rows of data to be entered into
     * the test case results table.
     * 
     * @return an array of arrays of Strings defining the table data
     */
    private Object [][] getTableData() {
        
        //TODO: replace the following with code that gets the actual row data from the model, 
        // including creating hyperlinks (labels) to open each output file, and also including 
        // additional links to "compare selected rows" (see Strawman diagram)
        return new Object[][]  { 
                {new JCheckBox(),  "1", new JLabel("Pass"), "100",  new JLabel("Link"), new JLabel("Link"), new JLabel("Link")},
                {new JCheckBox(),  "2", new JLabel("Fail"), "200",  new JLabel("Link"), new JLabel("Link"), new JLabel("Link")},
                {new JCheckBox(),  "3", new JLabel("Pass"), "100",  new JLabel("Link"), new JLabel("Link"), new JLabel("Link")},
                {new JCheckBox(),  "4", new JLabel("Fail"), "150",  new JLabel("Link"), new JLabel("Link"), new JLabel("Link")},
                {new JCheckBox(),  "5", new JLabel("Unknown"), "50",   new JLabel("Link"), new JLabel("Link"), new JLabel("Link")},
                {new JCheckBox(),  "6", new JLabel("Pass"), "100",  new JLabel("Link"), new JLabel("Link"), new JLabel("Link")},
                {new JCheckBox(),  "7", new JLabel("Fail"), "1000", new JLabel("Link"), new JLabel("Link"), new JLabel("Link")},
                {new JCheckBox(),  "8", new JLabel("Pass"), "100",  new JLabel("Link"), new JLabel("Link"), new JLabel("Link")},
                {new JCheckBox(),  "9", new JLabel("Unknown"), "1500", new JLabel("Link"), new JLabel("Link"), new JLabel("Link")},
                {new JCheckBox(), "10", new JLabel("Fail"), "10",   new JLabel("Link"), new JLabel("Link"), new JLabel("Link")},
       } ;
    }

    
    /**
     * Displays a window showing side-by-side comparison of the Team's and Judge's output
     * for each of the specified table rows. 
     * 
     * @param rows - an array containing the index numbers of the currently selected rows in the results table
     */
    private void showComparison(int[] rows) {
        if (rows.length<=0) {
            System.out.println("Comparison requested but no rows selected!?");
        } else {
            System.out.print ("Would have shown comparisons for the following data sets: ");
            for (int i=0; i<rows.length; i++) {
                System.out.print(rows[i]+1);   
                if (i<rows.length-1) {
                    System.out.print (", ");
                }
            }
            System.out.println();
        }
    }
    
    public void showMessage(final String message) {
        JOptionPane.showMessageDialog(this, message);
    }
    
    public class LinkRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        public void setValue(Object value) {
            setForeground(Color.BLUE);
            setText(((JLabel)value).getText());
            Font font = getFont();
            Map attributes = font.getAttributes();
            attributes.put(TextAttribute.UNDERLINE,  TextAttribute.UNDERLINE_ON);
            setFont(font.deriveFont(attributes));
            setHorizontalAlignment( SwingConstants.CENTER );
        }
        
    }
    
    private static int count = 1 ;
    
    public class PassFailCellRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;
        
        public void setValue(Object value) {
            String testResult = ((JLabel)value).getText();
            if (testResult.equalsIgnoreCase("Pass")) {
                setBackground(Color.green);
                setText("Pass");
            } else if (testResult.equalsIgnoreCase("Fail")) {
                setBackground(Color.red);
                setText("Fail");
            } else {
                //illegal value
                setBackground(Color.yellow);
                setText("??");
            }
            setHorizontalAlignment( SwingConstants.LEFT );
            setBorder(new EmptyBorder(0,30,0,0));
        }
        
    }
    
    public class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {

        private static final long serialVersionUID = 1L;

        public CheckBoxRenderer() {
          setHorizontalAlignment(SwingConstants.CENTER);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
          if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
          } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
          }
          setSelected(isSelected);
          return this;
        }
}

} // @jve:decl-index=0:visual-constraint="10,10"
