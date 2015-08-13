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
import java.util.ArrayList;
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
import javax.swing.table.TableModel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * Multiple data set viewer pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: AutoJudgeSettingsPane.java 2825 2014-08-12 23:22:50Z boudreat $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/ui/AutoJudgeSettingsPane.java $
public class MultiTestSetOutputViewerPane extends JPanePlugin {

    private static final long serialVersionUID = 7363093989131251458L;

    public static enum COLUMN {
        SELECT_CHKBOX, DATASET_NUM, RESULT, TIME, TEAM_OUTPUT_VIEW, TEAM_OUTPUT_COMPARE, 
            JUDGE_OUTPUT, JUDGE_DATA
    };

    // define the column headers for the table of results
    private String[] columnNames = { "Select", "Data Set #", "Result", "Time (ms)", 
                                        "Team View", "Team Compare", 
                                        "Judge's Output", "Judge's Data" };

    // get the row data for the table of results
    private Object[][] tableData ;

    private JPanel centerPanel = null;

    private final ButtonGroup buttonGroup = new ButtonGroup();

    private final ButtonGroup buttonGroup_1 = new ButtonGroup();

    protected Component pulldownSelectComparator;

    private JTextField textUserSpecifyComparator;

    private JTextField textUserSpecifyViewer;

    private JComboBox pulldownSelectViewer;

    private JButton btnCompareSelected;

    private JTable resultsTable;

    private JLabel lblProblemTitle;

    private JLabel lblTeamNumber;

    private JLabel lblRunID;

    private Run currentRun;

    private Problem currentProblem;

    private ProblemDataFiles currentProblemDataFiles;
    
    private String [] currentTeamOutputFileNames ;

    private JLabel lblLanguage;

    private JLabel lblNumFailedTestCases;

    private JLabel lblNumTestCases;

    private IFileViewer currentViewer;
    
    private MultiFileComparator currentComparator ;

    private JScrollPane resultsScrollPane;
    
    

    /**
     * Constructs an instance of a plugin pane for viewing multi-testset output values.
     * 
     */
    public MultiTestSetOutputViewerPane() {
        super();
        initialize();
    }

    /**
     * This method initializes the pane.
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
     * This method initializes and returns the center panel (JPanel) containing the 
     * JTabbedPane holding the output results and options panes. Note that the method
     * does not fill in any live data; that cannot be done until the View Pane's
     * "setData()" method has been invoked, which doesn't happen until after construction
     * of the View Pane is completed.
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPanel() {
        if (centerPanel == null) {
            BorderLayout borderLayout = new BorderLayout();
            borderLayout.setVgap(0);
            centerPanel = new JPanel();
            centerPanel.setLayout(borderLayout);

            // put a tabbed pane in the center of the panel
            JTabbedPane multiTestSetTabbedPane = new JTabbedPane(JTabbedPane.TOP);
            multiTestSetTabbedPane.setName("multiTestSetTabbedPane");
            multiTestSetTabbedPane.setBorder(new LineBorder(new Color(0, 0, 0)));
            centerPanel.add(multiTestSetTabbedPane, BorderLayout.CENTER);

            // add a tab with a JPanel that will display the results for the test cases
            JPanel resultsPane = new JPanel();
            resultsPane.setName("ViewDataSets");
            multiTestSetTabbedPane.addTab("Data Set Results", null, resultsPane, 
                    "Show the results of this submission for each test data set");
            resultsPane.setLayout(new BorderLayout(0, 0));

            // add a header for holding labels to the results panel
            JPanel resultsPaneHeaderPanel = new JPanel();
            resultsPaneHeaderPanel.setBorder(new LineBorder(Color.BLUE, 2));
            resultsPane.add(resultsPaneHeaderPanel, BorderLayout.NORTH);

            resultsPaneHeaderPanel.add(getRunIDLabel());

            Component horizontalGlue_1 = Box.createHorizontalGlue();
            horizontalGlue_1.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue_1);

            // add a label to the header showing the Problem for which this set of test results applies
            resultsPaneHeaderPanel.add(getProblemTitleLabel());

            Component horizontalGlue = Box.createHorizontalGlue();
            horizontalGlue.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue);

            // add a label to the header showing the Team for which this set of test results applies
            // TODO: replace the following label text with the Team Name and Number from the current submission
            resultsPaneHeaderPanel.add(getTeamNumberLabel());

            Component horizontalGlue_2 = Box.createHorizontalGlue();
            horizontalGlue_2.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue_2);

            resultsPaneHeaderPanel.add(getLanguageLabel());

            Component horizontalGlue_8 = Box.createHorizontalGlue();
            horizontalGlue_8.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue_8);

            // add a label to the header showing the total number of test cases for this problem
            resultsPaneHeaderPanel.add(getNumTestCasesLabel());

            Component horizontalGlue_7 = Box.createHorizontalGlue();
            horizontalGlue_7.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue_7);

            resultsPaneHeaderPanel.add(getNumFailedTestCasesLabel());

            // add a scrollpane to hold the table of results
            resultsScrollPane = new JScrollPane();
            resultsPane.add(resultsScrollPane, BorderLayout.CENTER);

            // create an (empty) table of results and put it in the scrollpane
            resultsTable = new JTable(12,7);
            resultsTable.setValueAt(true, 0, 0);
            resultsScrollPane.setViewportView(resultsTable);

            // add a footer panel containing control buttons
            JPanel resultsPaneFooterPanel = new JPanel();
            resultsPane.add(resultsPaneFooterPanel, BorderLayout.SOUTH);

            // add a control button to invoke comparison of the team and judge output files for selected row(s)
            btnCompareSelected = new JButton("Compare Selected");
            btnCompareSelected.setToolTipText("Show comparison between Team and Judge output for selected row(s)");
            btnCompareSelected.setEnabled(true);
            btnCompareSelected.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // display a comparison between the Team and Judge output in the selected table row(s)
                    compareFiles( getSelectedRowNums() );
                }
            });

            JCheckBox chkboxShowFailuresOnly = new JCheckBox("Show Failures Only", false);
            chkboxShowFailuresOnly.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showConfirmDialog(null, 
                            "Sorry, this option has not been implemented yet.",
                            "Not Implemented", 
                            JOptionPane.OK_CANCEL_OPTION, 
                            JOptionPane.INFORMATION_MESSAGE, 
                            null); 
                    if (((JCheckBox) (e.getSource())).isSelected()) {
                        loadTableWithFailedTestCases();
                    } else {
                        loadTableWithAllTestCases();
                    }
                }

            });

            resultsPaneFooterPanel.add(chkboxShowFailuresOnly);

            Component horizontalStrut_1 = Box.createHorizontalStrut(20);
            resultsPaneFooterPanel.add(horizontalStrut_1);

            final JButton btnSelectAll = new JButton("Select All");
            btnSelectAll.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // mark every checkbox in the "Select" column of the results table as "Selected" 
                    TableModel tm = resultsTable.getModel();
                    if (tm != null) {
                        int col = resultsTable.getColumn(columnNames[COLUMN.SELECT_CHKBOX.ordinal()]).getModelIndex();
                        for (int row = 0; row < tm.getRowCount(); row++) {
                            tm.setValueAt(new Boolean(true), row, col);
                        }
                        btnCompareSelected.setEnabled(true);
                    }
                }
            });
            resultsPaneFooterPanel.add(btnSelectAll);
            
            Component horizontalStrut_3 = Box.createHorizontalStrut(20);
            resultsPaneFooterPanel.add(horizontalStrut_3);
            
            JButton btnUnselectAll = new JButton("Unselect All");
            btnUnselectAll.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // mark every checkbox in the "Select" column of the results table as "Unselected" 
                    TableModel tm = resultsTable.getModel();
                    if (tm != null) {
                        int col = resultsTable.getColumn(columnNames[COLUMN.SELECT_CHKBOX.ordinal()]).getModelIndex();
                        for (int row = 0; row < tm.getRowCount(); row++) {
                            tm.setValueAt(new Boolean(false), row, col);
                        }
                        btnCompareSelected.setEnabled(false);
                    }
                }
            });
            resultsPaneFooterPanel.add(btnUnselectAll);

            Component horizontalStrut_2 = Box.createHorizontalStrut(20);
            resultsPaneFooterPanel.add(horizontalStrut_2);
            btnCompareSelected.setEnabled(true);
            resultsPaneFooterPanel.add(btnCompareSelected);

            Component horizontalGlue_3 = Box.createHorizontalGlue();
            horizontalGlue_3.setPreferredSize(new Dimension(20, 20));
            resultsPaneFooterPanel.add(horizontalGlue_3);

            // add a control button to dismiss the frame
            final JButton btnClose = new JButton("Close");
            btnClose.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (currentComparator != null) {
                        currentComparator.dispose();
                    }
                    Window parentFrame = SwingUtilities.getWindowAncestor(btnClose);
                    parentFrame.dispose();
                }
            });
            resultsPaneFooterPanel.add(btnClose);

            // add a tab that will display options for managing the display of data for the test cases
            JPanel optionsPane = new JPanel();
            multiTestSetTabbedPane.addTab("Options", null, optionsPane, "Set options for tools used to display test set results");
            optionsPane.setLayout(new BorderLayout(0, 0));

            // add a panel that will hold the various chooser options
            JPanel panelChoosers = new JPanel();
            optionsPane.add(panelChoosers, BorderLayout.CENTER);

            // add a panel that will support choosing the comparator tool to be used
            JPanel chooseComparatorPanel = new JPanel();
            panelChoosers.add(chooseComparatorPanel);
            chooseComparatorPanel.setBounds(new Rectangle(0, 0, 0, 20));
            chooseComparatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            chooseComparatorPanel.setPreferredSize(new Dimension(220, 200));
            chooseComparatorPanel.setBorder(new TitledBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)),
                    "Choose Compare Program", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            chooseComparatorPanel.setLayout(new BoxLayout(chooseComparatorPanel, BoxLayout.Y_AXIS));

            Component verticalStrut = Box.createVerticalStrut(20);
            verticalStrut.setMinimumSize(new Dimension(0, 10));
            verticalStrut.setPreferredSize(new Dimension(0, 15));
            chooseComparatorPanel.add(verticalStrut);

            // add a button to select the built-in comparator
            JRadioButton rdbtnInternalCompareProgram = new JRadioButton("Built-in Comparator");
            rdbtnInternalCompareProgram.setSelected(true);
            buttonGroup.add(rdbtnInternalCompareProgram);
            chooseComparatorPanel.add(rdbtnInternalCompareProgram);

            // add a button to select from a list of available comparators
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

            // add a panel to hold the pulldown list which allows a user to select a comparator
            JPanel panelSelectComparator = new JPanel();
            FlowLayout flowLayout = (FlowLayout) panelSelectComparator.getLayout();
            flowLayout.setAlignOnBaseline(true);
            panelSelectComparator.setAlignmentX(Component.LEFT_ALIGNMENT);
            chooseComparatorPanel.add(panelSelectComparator);

            // construct a dropdown list of available comparators and add it to the panel
            pulldownSelectComparator = new JComboBox(getAvailableComparatorsList());
            pulldownSelectComparator.setEnabled(false);
            panelSelectComparator.add(pulldownSelectComparator);

            // add a button that allows the user to edit a text field identifying the comparator to be used
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

            // add a panel to hold the user-specified comparator text box
            JPanel panelSpecifyComparator = new JPanel();
            FlowLayout flowLayout_1 = (FlowLayout) panelSpecifyComparator.getLayout();
            flowLayout_1.setAlignOnBaseline(true);
            panelSpecifyComparator.setAlignmentX(Component.LEFT_ALIGNMENT);
            chooseComparatorPanel.add(panelSpecifyComparator);

            // add a text box for the user to specify the comparator
            textUserSpecifyComparator = new JTextField();
            textUserSpecifyComparator.setEnabled(false);
            textUserSpecifyComparator.setText("<enter comparator name>");
            panelSpecifyComparator.add(textUserSpecifyComparator);
            textUserSpecifyComparator.setColumns(15);

            Component horizontalStrut = Box.createHorizontalStrut(20);
            horizontalStrut.setMinimumSize(new Dimension(40, 0));
            panelChoosers.add(horizontalStrut);

            // create a panel to hold the various components allowing choice of viewers
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

            // add a button for selecting the built-in viewer
            JRadioButton rdbtnInternalViewerProgram = new JRadioButton("Built-in Viewer");
            rdbtnInternalViewerProgram.setSelected(true);
            rdbtnInternalViewerProgram.setPreferredSize(new Dimension(117, 23));
            rdbtnInternalViewerProgram.setMinimumSize(new Dimension(117, 23));
            rdbtnInternalViewerProgram.setMaximumSize(new Dimension(117, 23));
            buttonGroup_1.add(rdbtnInternalViewerProgram);
            chooseViewerPanel.add(rdbtnInternalViewerProgram);

            // add a button for selecting the viewer from a list of available viewers
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

            // add a panel to hold the drop-down list of available viewers
            JPanel panel = new JPanel();
            panel.setAlignmentX(Component.LEFT_ALIGNMENT);
            chooseViewerPanel.add(panel);

            // add a drop-down list of available viewers
            pulldownSelectViewer = new JComboBox(getAvailableViewersList());
            pulldownSelectViewer.setEnabled(false);
            panel.add(pulldownSelectViewer);

            // add a button allowing the user to enable a textbox for typing the name of a viewer
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

            // add a panel to hold the text box
            JPanel panel_1 = new JPanel();
            panel_1.setEnabled(false);
            chooseViewerPanel.add(panel_1);

            // add a text field for the user to specify a viewer
            textUserSpecifyViewer = new JTextField();
            textUserSpecifyViewer.setEnabled(false);
            textUserSpecifyViewer.setText("<enter viewer name>");
            panel_1.add(textUserSpecifyViewer);
            textUserSpecifyViewer.setColumns(15);

            // add a footer panel to hold control buttons
            JPanel panelFooterButtons = new JPanel();
            optionsPane.add(panelFooterButtons, BorderLayout.SOUTH);

            // add a control button to restore defaults
            JButton btnRestoreDefaults = new JButton("Restore Defaults");
            panelFooterButtons.add(btnRestoreDefaults);

            Component horizontalGlue_4 = Box.createHorizontalGlue();
            horizontalGlue_4.setPreferredSize(new Dimension(20, 20));
            panelFooterButtons.add(horizontalGlue_4);

            // add a button to save currently-selected options
            JButton btnUpdate = new JButton("Update");
            btnUpdate.setEnabled(false);
            panelFooterButtons.add(btnUpdate);

            Component horizontalGlue_5 = Box.createHorizontalGlue();
            horizontalGlue_5.setPreferredSize(new Dimension(20, 20));
            panelFooterButtons.add(horizontalGlue_5);

            // add a button to cancel, restoring former settings
            JButton btnCancel = new JButton("Cancel");
            btnCancel.setEnabled(false);
            panelFooterButtons.add(btnCancel);

            Component horizontalGlue_6 = Box.createHorizontalGlue();
            horizontalGlue_6.setPreferredSize(new Dimension(20, 20));
            panelFooterButtons.add(horizontalGlue_6);

            // add a button to dismiss the frame
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
     * Returns an array of ints containing the table row numbers of those rows whose 
     * "Select" checkbox is checked.
     * 
     * @return - an int [] of selected rows
     */
    private int [] getSelectedRowNums() {
        ArrayList<Integer> selectedRows = new ArrayList<Integer>();
        for (int i=0; i<resultsTable.getRowCount(); i++) {
            if ((Boolean)resultsTable.getValueAt(i, COLUMN.SELECT_CHKBOX.ordinal())){
                selectedRows.add(new Integer(i));
            }
        }
        Integer [] selectedRowNumbers = selectedRows.toArray(new Integer[selectedRows.size()]);
        int [] intArray = new int[selectedRowNumbers.length];
        for (int i=0; i<intArray.length; i++) {
            intArray[i] = selectedRowNumbers[i].intValue();
        }

        return intArray;
    }

    /**
     * @return
     */
    private JLabel getNumTestCasesLabel() {
        if (lblNumTestCases == null) {
            lblNumTestCases = new JLabel("Test Cases: XXX");
        }
        return lblNumTestCases;
    }

    /**
     * @return
     */
    private JLabel getNumFailedTestCasesLabel() {
        if (lblNumFailedTestCases == null) {
            lblNumFailedTestCases = new JLabel("Failed: XXX");
        }
        return lblNumFailedTestCases;
    }

    private JLabel getLanguageLabel() {
        if (lblLanguage == null) {
            lblLanguage = new JLabel("Language: XXX");
        }
        return lblLanguage;
    }

    private JLabel getRunIDLabel() {
        if (lblRunID == null) {
            lblRunID = new JLabel("Run ID: XXX");
        }

        return lblRunID;
    }

    private JLabel getTeamNumberLabel() {

        if (lblTeamNumber == null) {
            lblTeamNumber = new JLabel("Team: XXX");
        }

        return lblTeamNumber;
    }

    private JLabel getProblemTitleLabel() {
        if (lblProblemTitle == null) {
            lblProblemTitle = new JLabel("Problem: XXX");
        }
        return lblProblemTitle;
    }

    private void populateGUI() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                
                // fill in the basic header information
                getProblemTitleLabel().setText("Problem:  " + currentProblem.getLetter() + " - " + currentProblem.getShortName());
                getTeamNumberLabel().setText("Team:  " + currentRun.getSubmitter().getClientNumber());
                getRunIDLabel().setText("Run ID:  " + currentRun.getNumber());
                getLanguageLabel().setText("Language:  " + getCurrentRunLanguageName());

                // get the test case results for the current run
                RunTestCase[] testCases = currentRun.getRunTestCases();

                // fill in the test case summary information
                getNumTestCasesLabel().setText("Test Cases:  " + testCases.length);
                
                System.out.println("MTSVPane.populateGUI(): loading " + testCases.length + " test cases into GUI pane...");
//                for (int i = 0; i < testCases.length; i++) {
//                    System.out.println("  Test Case " + testCases[i].getTestNumber() + ": " + testCases[i]);
//                }

                int failedCount = getNumFailedTestCases(testCases);
                if (failedCount > 0) {
                    getNumFailedTestCasesLabel().setForeground(Color.red);
                    getNumFailedTestCasesLabel().setText("Failed:  " + failedCount);
                } else {
                    getNumFailedTestCasesLabel().setForeground(Color.green);
                    getNumFailedTestCasesLabel().setText("ALL PASSED");
                }
                
                resultsTable = getResultsTable(testCases);
                resultsScrollPane.setViewportView(resultsTable);
            }

        });

    }

    private int getNumFailedTestCases(RunTestCase[] testCases) {
        int failed = 0 ;
        for (int i = 0; i < testCases.length; i++) {
            if (!testCases[i].isPassed()) {
                failed++;
//                int num = i+1;
//                System.out.println("Found failed test case: " + num);
            }
        }
        System.out.println ("  (including " + failed + " failed cases)");
        return failed;
    }

    /**
     * Returns the name of the language used in the "current run" defined by the field "currentRun".
     * 
     * @return the language display name as defined by the toString() method of the defined language.
     */
    private String getCurrentRunLanguageName() {
        Language language = getContest().getLanguage(currentRun.getLanguageId());
        return language.toString();
    }

    /**
     * Loads the result table with data for all test case results.
     */
    private void loadTableWithAllTestCases() {
        // TODO fill in this method
        System.out.println("Would have reloaded table with all test case results.");

    }

    /**
     * Loads the result table with data for all test case results.
     */
    private void loadTableWithFailedTestCases() {
        // TODO fill in this method
        System.out.println("Would have reloaded table with failed test case results (only).");
    }

    /**
     * Returns a JTable containing the results information for the specified set of test cases. 
     * The method sets not only the table data model but also the appropriate cell renderers and 
     * action/mouse listeners for the table.
     */
    private JTable getResultsTable(RunTestCase [] testCases) {

        final JTable resultsTable;

        //create the results table
        TableModel tableModel = new TestCaseResultsTableModel(testCases, columnNames) ;
        
//        System.out.println ("Table model contains:");
//        System.out.println ("--------------");
//        for (int row=0; row<tableModel.getRowCount(); row++) {
//            for (int col=0; col<tableModel.getColumnCount(); col++) {
//                System.out.print("[" + tableModel.getValueAt(row, col) + "]");
//            }
//            System.out.println();
//        }
//        System.out.println ("--------------");
        
        resultsTable = new JTable(tableModel);
        
        //set the desired options on the table
        resultsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        resultsTable.setFillsViewportHeight(true);
        resultsTable.setRowSelectionAllowed(false);
        resultsTable.getTableHeader().setReorderingAllowed(false);
        
        //add a listener for selection events on the table
        resultsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            // insure "compare" button is only enabled when at least one table row is selected
            public void valueChanged(ListSelectionEvent e) {
                btnCompareSelected.setEnabled(getSelectedRowNums().length>0);
            }
        });
        
        //initialize column renderers based on column type

        // set a centering renderer on desired table columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        resultsTable.getColumn(columnNames[COLUMN.DATASET_NUM.ordinal()]).setCellRenderer(centerRenderer);
       
        //set our own checkbox renderer (don't know how to add an ActionListener to the default checkboxes
        resultsTable.getColumn(columnNames[COLUMN.SELECT_CHKBOX.ordinal()]).setCellRenderer(new CheckBoxRenderer());

        // set a LinkRenderer on those cells containing links
        resultsTable.getColumn(columnNames[COLUMN.TEAM_OUTPUT_VIEW.ordinal()]).setCellRenderer(new LinkRenderer());
        resultsTable.getColumn(columnNames[COLUMN.TEAM_OUTPUT_COMPARE.ordinal()]).setCellRenderer(new LinkRenderer());
        resultsTable.getColumn(columnNames[COLUMN.JUDGE_OUTPUT.ordinal()]).setCellRenderer(new LinkRenderer());
        resultsTable.getColumn(columnNames[COLUMN.JUDGE_DATA.ordinal()]).setCellRenderer(new LinkRenderer());

        // render Result column as Pass/Fail on Green/Red
        resultsTable.getColumn(columnNames[COLUMN.RESULT.ordinal()]).setCellRenderer(new PassFailCellRenderer());

        // render Time column right-justified
        resultsTable.getColumn(columnNames[COLUMN.TIME.ordinal()]).setCellRenderer(new RightJustifyRenderer());

        // force table column widths to nice values
//         resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
         resultsTable.getColumn(columnNames[COLUMN.SELECT_CHKBOX.ordinal()]).setPreferredWidth(15);

        // add a listener to allow users to click an output or data file name and display it
        resultsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable) e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                
                System.out.println ("MTSVPane.mouseClicked(): row=" + row + ", col=" + column);

                if (column == COLUMN.TEAM_OUTPUT_VIEW.ordinal() || column == COLUMN.JUDGE_OUTPUT.ordinal() || column == COLUMN.JUDGE_DATA.ordinal()) {
                    viewFile(row, column);
                } else if (column == COLUMN.TEAM_OUTPUT_COMPARE.ordinal() || e.getClickCount() > 1) {
                    // compare the team and judge's output in the active row
                    int[] rows = new int[] { row };
                    compareFiles(rows);
                }
            }
        });

        return resultsTable;
    }
    
    /**
     * Returns an array of Strings listing the names of available (known) output viewer tools.
     * 
     * @return a String array of viewer tool names
     */
    private String[] getAvailableViewersList() {
        // TODO figure out how to coordinate this with actual known viewers
        return new String[] { "gvim", "notepad", "write", "Another Viewer Tool" };
    }

    /**
     * Returns an array of Strings listing the names of available (known) output comparator tools.
     * 
     * @return a String array of comparator tool names
     */
    private String[] getAvailableComparatorsList() {
        // TODO figure out how to coordinate this with actual known comparators
        return new String[] { "diff", "GVimDiff", "Another Diff Tool" };
    }

    public void showMessage(final String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public class LinkRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        public void setValue(Object value) {
            setForeground(Color.BLUE);
            setText(((JLabel) value).getText());
            Font font = getFont();
            Map attributes = font.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            setFont(font.deriveFont(attributes));
            setHorizontalAlignment(SwingConstants.CENTER);
        }

    }

    /**
     * Pops up a viewer window for the file defined at the specified row/col in the results table.
     * 
     * @param row
     *            - the test case row number
     * @param col
     *            - the column in the table: team output, judge's output, or judge's data
     */
    protected void viewFile(int row, int col) {
        if (col != COLUMN.TEAM_OUTPUT_VIEW.ordinal() && col != COLUMN.JUDGE_OUTPUT.ordinal() && 
                col != COLUMN.JUDGE_DATA.ordinal() ) {
            Log log = getController().getLog();
            log.log(Log.WARNING, "MTSVPane.viewFile(): invalid column number for file viewing request: "
                    + col);
            System.err.println ("Invalid column number for file viewing request: " + col);
            return;
        }
        if (currentViewer != null) {
            currentViewer.dispose();
        }
        currentViewer = new MultipleFileViewer(getController().getLog());
        
        //get a title based on what column was selected
        String title = col == COLUMN.TEAM_OUTPUT_VIEW.ordinal() ? "Team Output" 
                : col == COLUMN.JUDGE_OUTPUT.ordinal() ? "Judge's Output" 
                        : col == COLUMN.JUDGE_DATA.ordinal() ? "Judge's Data" : "<unknown>";
        
        //get the file associated with the specified cell
        SerializedFile targetFile = getFileForTableCell(row,col);
        if (targetFile != null) {
            int testCaseNum = row + 1;
            showFile(currentViewer, targetFile, title, "Test Case "+testCaseNum, true);
        } else {
            String logMsg = "MTSVPane.viewFile(): unable to find file for table cell (" 
                    + row + "," + col + ")  (Contest configuration error?)" ;
            Log log = getController().getLog();
            log.log(Log.WARNING, logMsg);
            String errMsg = "Unable to find file for table cell (" 
                    + row + "," + col + ") (Contest configuration error?)" ;
            JOptionPane.showMessageDialog(getParentFrame(), errMsg, 
                    "File Not Found", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Generates a {@link MultiFileComparator} comparing team and judge's outputs for each of 
     * the test cases specified by the elements of the input "rows" array.
     * @param rows - the rows of the table for which team/judge outputs should be compared
     */
    private void compareFiles(int [] rows) {
        
        System.out.print ("MTSVPane.compareFiles(): displaying comparison of files for test case(s) ");
        for (int i=0; i<rows.length; i++) {
            System.out.print ((int) (new Integer((String)(resultsTable.getModel().getValueAt(rows[i], 1)))) + " ");
        }
        System.out.println ();
        
        //make sure we have a comparator
        if (currentComparator == null) {
            currentComparator = new MultiFileComparator();
            currentComparator.setContestAndController(getContest(), getController());
        }

        //create arrays to hold data to be loaded into the comparator
        int [] testCases = new int [rows.length];
        String [] judgesOutputFileNames = new String [rows.length];
        String [] judgesDataFileNames = new String [rows.length];
        String [] teamOutputFileNames = new String [rows.length];
        
        //get the judge's information defined in the current problem
        SerializedFile [] judgesAnswerFiles = currentProblemDataFiles.getJudgesAnswerFiles();
        SerializedFile [] judgesDataFiles = currentProblemDataFiles.getJudgesDataFiles();
        
        
        //load the comparator input arrays
        for (int i=0; i<rows.length; i++) {
            //get the test case defined in the second column of the current table row
            testCases[i] = (int) (new Integer((String)(resultsTable.getModel().getValueAt(rows[i], 1))));
            //get the full path to the judge's answer and data files as defined in the SerializedFiles
            judgesOutputFileNames[i] = judgesAnswerFiles[testCases[i]].getAbsolutePath();
            judgesDataFileNames[i] = judgesDataFiles[testCases[i]].getAbsolutePath();
            //make sure the team output file(s) were defined (they have to be loaded by a client
            // making a separate call to setTeamOutputFileNames; make sure the client complied)
            if (currentTeamOutputFileNames == null || currentTeamOutputFileNames.length<teamOutputFileNames.length) {
                Log log = getLog();
                if (log!=null) {
                    log.warning("MTSVPane.compareFiles(): invalid team output file names array");
                } else {
                    System.err.println ("MTSVPane.compareFiles(): invalid team output file names array");
                }
            } else {
                //get the team output file name, which should be provided by the client as a full path
                teamOutputFileNames[i] = currentTeamOutputFileNames[i];  
            }  
        }
                
        //put the data into the comparator
        currentComparator.setData(currentRun.getNumber(), testCases, teamOutputFileNames, judgesOutputFileNames, judgesDataFileNames);
        
        //make the comparator visible
        currentComparator.setVisible(true);

    }

    /**
     * Returns a SerializedFile corresponding to the "link" in the results table at the 
     * specified row and column. If the specified column is not one of {team output, 
     * judge's output(answer), judge's input(data)}, or if no file can be found for
     * the specified cell, null is returned.
     *
     * @param row - a row in the Test Case Results table
     * @param col - a column in the Test Case Results table
     * @return a SerializedFile corresponding to the table cell, or null
     */
    private SerializedFile getFileForTableCell(int row, int col) {
        
        Problem prob = getContest().getProblem(currentRun.getProblemId());
        
        ProblemDataFiles problemDataFiles = getController().getProblemDataFiles(prob);
        
        //declare the value to be returned
        SerializedFile returnFile = null ;
        
        if (col == COLUMN.TEAM_OUTPUT_VIEW.ordinal() || col == COLUMN.TEAM_OUTPUT_COMPARE.ordinal()) {
            //get team output file corresponding to test case "row"
            if (currentTeamOutputFileNames != null || currentTeamOutputFileNames.length >= row) {
                // get the team output file name, which should be provided by the client as a full path
                returnFile = new SerializedFile(currentTeamOutputFileNames[row]);
            }
        } else if (col == COLUMN.JUDGE_OUTPUT.ordinal()) {
            //get judge's output corresponding to test case "row"
            SerializedFile [] answerFiles = problemDataFiles.getJudgesAnswerFiles();
            //make sure we got back some answer files and that there is an answer file for the test case
            if (answerFiles != null && row < answerFiles.length) {
                returnFile = answerFiles[row];       
            } else {
                //there is no answer file for the specified test case (row)
                returnFile = null ;
            }

            
        } else if (col == COLUMN.JUDGE_DATA.ordinal()) {
            //get judge's input data corresponding to test case "row"
            SerializedFile [] inputDataFiles = problemDataFiles.getJudgesDataFiles();
            //make sure we got back some data files and that there is a data file for the test case
            if (inputDataFiles != null && row < inputDataFiles.length) {
                returnFile = inputDataFiles[row];       
            } else {
                //there is no data file for the specified test case (row)
                returnFile = null ;
            }

        } else {
            //the column is not one of those containing "view" links; return null
            returnFile = null;
        }


        return returnFile;
    }

    /**
     * Uses the specified fileViewer to display the specified file, setting the title and message
     * on the viewer to the specified values and invoking "setVisible()" on the viewer if desired.
     * @param fileViewer - the viewer to be used
     * @param file - the file to be displayed in the viewer
     * @param title - the title to be set on the viewer title bar
     * @param tabLabel - the label to be put on the viewer pane tab
     * @param visible - whether or not to invoke setVisible(true) on the viewer
     */
    private void showFile(IFileViewer fileViewer, SerializedFile file, String title, String tabLabel, boolean visible) {
        System.out.println ("MTSVPane.showFile():");
        String viewerString = fileViewer==null?"<null>":fileViewer.getClass().toString();
        String filePathString = file==null?"<null>":file.getAbsolutePath().toString();
        System.out.println ("  Viewer='" + viewerString + "'" 
                            + "  File='" + filePathString + "'"
                            + "  Title='" + title + "'"
                            + "  setVisible='" + visible + "'");
        if (fileViewer == null || file == null) {
            Log log = getController().getLog();
            log.log(Log.WARNING, "MTSVPane.showFile(): fileViewer or file is null");
            JOptionPane.showMessageDialog(getParentFrame(), 
                    "System Error: null fileViewer or file; contact Contest Administrator (check logs)", 
                    "System Error", JOptionPane.ERROR_MESSAGE);
            return ;
        }
        fileViewer.setTitle(title);
        fileViewer.addFilePane(tabLabel, file.getAbsolutePath());
        fileViewer.enableCompareButton(false);
        fileViewer.setInformationLabelText("File: " + file.getName());

        if (visible) {
            fileViewer.setVisible(true);
        }
    }

    public class PassFailCellRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        public void setValue(Object value) {
            String testResult = ((JLabel) value).getText();
            if (testResult.equalsIgnoreCase("Pass")) {
                setBackground(Color.green);
                setForeground(Color.black);
                setText("Pass");
            } else if (testResult.equalsIgnoreCase("Fail")) {
                setBackground(Color.red);
                setForeground(Color.white);
                setText("Fail");
            } else {
                // illegal value
                setBackground(Color.yellow);
                setText("??");
                Log log = getController().getLog();
                log.log(Log.SEVERE, "MTSV PassFailCellRenderer: unknown pass/fail result: ", value);
            }
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(0, 30, 0, 0));
        }

    }

    public class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {

        private static final long serialVersionUID = 1L;

        public CheckBoxRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setSelected((Boolean)value);
            return this;
        }
    }

    /**
     * A cell renderer for displaying right-justified values but with some margin space.
     * @author John
     *
     */
    public class RightJustifyRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        public void setValue(Object value) {
            setHorizontalAlignment(SwingConstants.RIGHT);
            setBorder(new EmptyBorder(0, 0, 0, 30));
            setText((String) value);
        }

    }

    public void setData(Run run, Problem problem, ProblemDataFiles problemDataFiles) {

        this.currentRun = run;
        this.currentProblem = problem;
        this.currentProblemDataFiles = problemDataFiles;

        populateGUI();
    }
    
    /**
     * Set new team output filenames.
     */
    public void setTeamOutputFileNames(String [] filenames){
        this.currentTeamOutputFileNames = filenames ;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
