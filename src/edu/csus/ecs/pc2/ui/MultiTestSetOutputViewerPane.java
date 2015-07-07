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
import java.io.File;
import java.io.IOException;
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
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.Executable;
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
    
    private enum COLUMN {SELECT_CHKBOX, DATASET_NUM, RESULT, TIME, TEAM_OUTPUT, JUDGE_OUTPUT, JUDGE_DATA} ;
    
//    private final static int COLUMN_SELECT_CHKBOX = 0;
//    private final static int COLUMN_DATASET_NUM = 1;
//    private final static int COLUMN_RESULT = 2;
//    private final static int COLUMN_TIME = 3;
//    private final static int COLUMN_TEAM_OUTPUT = 4;
//    private final static int COLUMN_JUDGE_OUTPUT = 5;
//    private final static int COLUMN_JUDGE_DATA = 6 ;

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

    private JLabel lblLanguage;

    private JLabel lblNumFailedTestCases;

    private JLabel lblNumTestCases;
    
    private IFileViewer currentViewer ;

    
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
            
            resultsPaneHeaderPanel.add(getRunIDLabel());
            
            Component horizontalGlue_1 = Box.createHorizontalGlue();
            horizontalGlue_1.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue_1);
            
            //add a label to the header showing the Problem for which this set of test results applies
            resultsPaneHeaderPanel.add(getProblemTitleLabel());
            
            Component horizontalGlue = Box.createHorizontalGlue();
            horizontalGlue.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue);
            
            //add a label to the header showing the Team for which this set of test results applies
            //TODO: replace the following label text with the Team Name and Number from the current submission
            resultsPaneHeaderPanel.add(getTeamNumberLabel());
            
            Component horizontalGlue_2 = Box.createHorizontalGlue();
            horizontalGlue_2.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue_2);
            
            resultsPaneHeaderPanel.add(getLanguageLabel());
            
            Component horizontalGlue_8 = Box.createHorizontalGlue();
            horizontalGlue_8.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue_8);
            
            //add a label to the header showing the total number of test cases for this problem
            resultsPaneHeaderPanel.add(getNumTestCasesLabel());
            
            Component horizontalGlue_7 = Box.createHorizontalGlue();
            horizontalGlue_7.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue_7);
            
            resultsPaneHeaderPanel.add(getNumFailedTestCasesLabel());
            
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
            
            JCheckBox chckbxShowFailuresOnly = new JCheckBox("Show Failures Only");
            chckbxShowFailuresOnly.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if ( ((JCheckBox)(e.getSource())).isSelected()) {
                        loadTableWithFailedTestCases();
                    } else {
                        loadTableWithAllTestCases();
                    }
                }

            });
            
            resultsPaneFooterPanel.add(chckbxShowFailuresOnly);
            
            Component horizontalStrut_1 = Box.createHorizontalStrut(20);
            resultsPaneFooterPanel.add(horizontalStrut_1);
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
     * @return
     */
    private JLabel getNumTestCasesLabel() {
        if (lblNumTestCases == null) {
            lblNumTestCases= new JLabel("Test Cases: XXX");
        }
        return lblNumTestCases;
    }

    /**
     * @return
     */
    private JLabel getNumFailedTestCasesLabel() {
        if (lblNumFailedTestCases == null) {
            lblNumFailedTestCases= new JLabel("Failed: XXX");
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
                //fill in the basic header information
                getProblemTitleLabel().setText("Problem:  " + currentProblem.getLetter() + " - " + currentProblem.getShortName());
                getTeamNumberLabel().setText("Team:  " + currentRun.getSubmitter().getClientNumber());
                getRunIDLabel().setText("Run ID:  " + currentRun.getNumber());
                getLanguageLabel().setText("Language:  " + getCurrentRunLanguageName());
                
                //get the test case results for the current run
                RunTestCase [] testCases = currentRun.getRunTestCases();
                
                //fill in the test case summary information
                getNumTestCasesLabel().setText("Test Cases:  " + testCases.length);
                for (int i=0; i<testCases.length; i++) {
                    System.out.println ("Test case " + testCases[i].getTestNumber() + " result: " + testCases[i]);
                }
                
                int failedCount = getNumFailedTestCases(testCases);
                if (failedCount > 0) {
                    getNumFailedTestCasesLabel().setForeground(Color.red);
                    getNumFailedTestCasesLabel().setText("Failed:  " + failedCount);
                } else {
                    getNumFailedTestCasesLabel().setForeground(Color.green);
                    getNumFailedTestCasesLabel().setText("ALL PASSED");
                }
                
                //the following code is from the class where it was copied from; need to do the "equivalent" operations
                // for THIS type of pane...
//                selectDisplayRadioButton();
//                getJudgesDefaultAnswerTextField().setText(contestInformation.getJudgesDefaultAnswer());
//                getJCheckBoxShowPreliminaryOnBoard().setSelected(contestInformation.isPreliminaryJudgementsUsedByBoard());
//                getJCheckBoxShowPreliminaryOnNotifications().setSelected(contestInformation.isPreliminaryJudgementsTriggerNotifications());
//                getAdditionalRunStatusCheckBox().setSelected(contestInformation.isSendAdditionalRunStatusInformation());
//                getAutoRegistrationCheckbox().setSelected(contestInformation.isEnableAutoRegistration());
//                setContestInformation(contestInformation);
//                setEnableButtons(false);
                //...
                
                
            }

        });

    }
    

    private int getNumFailedTestCases(RunTestCase[] testCases) {
//        int failed = 0 ;
//        for (int i=0; i<testCases.length; i++) {
//            if (testCases[i].get
//                ...
//        }
        return 3;
    }

    /**
     * Returns the name of the language used in the "current run" defined by the field "currentRun".
     * @return the language display name as defined by the toString() method of the defined language.
     */
    private String getCurrentRunLanguageName() {
        Language language = getContest().getLanguage(currentRun.getLanguageId());
        return language.toString();    }
    
    /**
     * Loads the result table with data for all test case results.
     */
    private void loadTableWithAllTestCases() {
        //TODO fill in this method
        System.out.println ("Would have reloaded table with all test case results.");
        
    }

    /**
     * Loads the result table with data for all test case results.
     */
    private void loadTableWithFailedTestCases() {
        //TODO fill in this method
        System.out.println ("Would have reloaded table with failed test case results (only).");
    }

    /**
     * Returns a JTable containing the results information for each test case.
     * The method sets not only the table data but the appropriate cell renderers
     * and action/mouse listeners for the table.
     */
    private JTable getResultsTable() {
        
        final JTable resultsTable ;
        
        //define the column headers for the table of results
        final String[] columnNames = {"Select", "Data Set #", "Result", "Time (ms)", "Team Output", 
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
        resultsTable.getColumnModel().getColumn(COLUMN.SELECT_CHKBOX.ordinal()).setCellRenderer(new CheckBoxRenderer());
        resultsTable.getColumnModel().getColumn(COLUMN.DATASET_NUM.ordinal()).setCellRenderer(centerRenderer);
        
        //set a LinkRenderer on those cells containing links
        resultsTable.getColumnModel().getColumn(COLUMN.TEAM_OUTPUT.ordinal()).setCellRenderer(new LinkRenderer());
        resultsTable.getColumnModel().getColumn(COLUMN.JUDGE_OUTPUT.ordinal()).setCellRenderer(new LinkRenderer());
        resultsTable.getColumnModel().getColumn(COLUMN.JUDGE_DATA.ordinal()).setCellRenderer(new LinkRenderer());

        //render Result column as Pass/Fail on Green/Red
        resultsTable.getColumnModel().getColumn(COLUMN.RESULT.ordinal()).setCellRenderer(new PassFailCellRenderer());
        
        //render Time column right-justified
        resultsTable.getColumnModel().getColumn(COLUMN.TIME.ordinal()).setCellRenderer(new TimeRenderer());
        
        //force table column widths to nice values
//        resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        resultsTable.getColumnModel().getColumn(COLUMN_SELECT).setPreferredWidth(45);
        
        //add a listener to allow users to click an output or data file name and display it
        resultsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                
                if (column>=COLUMN.TEAM_OUTPUT.ordinal() && column<=COLUMN.JUDGE_DATA.ordinal()) {
                    showListing (row, column);
                }
            }
          });
        
        return resultsTable;
    }
    
    /**
     * Uses the currently-defined Viewer to display output file listed in the specified table row/col.
     * TODO: add code to actually display the specified output file.
     * @param row - the selected table row (0-based)
     * @param col - the selected table column (0-based)
     */
    private void showListing(int row, int col) {
        int dataSet = row+1;
        String outputType = col == COLUMN.TEAM_OUTPUT.ordinal() ? "Team Output"
                : col == COLUMN.JUDGE_OUTPUT.ordinal() ? "Judge's Output"
                        : col == COLUMN.JUDGE_DATA.ordinal() ? "Judge's Data"
                                : "??";
        System.out.println ("Showing " + outputType + " for Data Set " + dataSet);
        viewFile(row, col);
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
                {new JCheckBox(),  "1", new JLabel("Pass"),    "100",   new JLabel("View   Compare"), new JLabel("View"), new JLabel("View")},
                {new JCheckBox(),  "2", new JLabel("Fail"),    "200",   new JLabel("View   Compare"), new JLabel("View"), new JLabel("View")},
                {new JCheckBox(),  "3", new JLabel("Pass"),    "100",   new JLabel("View   Compare"), new JLabel("View"), new JLabel("View")},
                {new JCheckBox(),  "4", new JLabel("Fail"),    "150",   new JLabel("View   Compare"), new JLabel("View"), new JLabel("View")},
                {new JCheckBox(),  "5", new JLabel("Unknown"),  "50",   new JLabel("View   Compare"), new JLabel("View"), new JLabel("View")},
                {new JCheckBox(),  "6", new JLabel("Pass"),    "100",   new JLabel("View   Compare"), new JLabel("View"), new JLabel("View")},
                {new JCheckBox(),  "7", new JLabel("Fail"),   "1000",   new JLabel("View   Compare"), new JLabel("View"), new JLabel("View")},
                {new JCheckBox(),  "8", new JLabel("Pass"),    "100",   new JLabel("View   Compare"), new JLabel("View"), new JLabel("View")},
                {new JCheckBox(),  "9", new JLabel("Unknown"),"1500",   new JLabel("View   Compare"), new JLabel("View"), new JLabel("View")},
                {new JCheckBox(), "10", new JLabel("Fail"),     "10",   new JLabel("View   Compare"), new JLabel("View"), new JLabel("View")},
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
    
    /**
     * Pops up a viewer window for the file defined at the specified row/col in the results table.
     * @param row - the test case row number
     * @param col - the column in the table: team output, judge's output, or judge's data
     */
    protected void viewFile(int row, int col) {
        if (col != COLUMN.TEAM_OUTPUT.ordinal() && col != COLUMN.JUDGE_OUTPUT.ordinal() && col != COLUMN.JUDGE_DATA.ordinal()) {
            Log log = getController().getLog();
            log.log(Log.WARNING, "MTSV: invalid column number for file viewing request");
            return;
        }
        if (currentViewer != null) {
            currentViewer.dispose();
        }
        currentViewer = new MultipleFileViewer(getController().getLog());
        String title = col==COLUMN.TEAM_OUTPUT.ordinal()?"Team Output"
                :col==COLUMN.JUDGE_OUTPUT.ordinal()?"Judge's Output"
                        :col==COLUMN.JUDGE_DATA.ordinal()?"Judge's Data"
                                :"<unknown";
        createAndViewFile(currentViewer, getFileForTableCell(row,col), title, true);
    }

    private SerializedFile getFileForTableCell(int row, int col) {
        return new SerializedFile("TestFileName");
    }

    private void createAndViewFile(IFileViewer fileViewer, SerializedFile file, String title, boolean visible) {
        // TODO the executable dir name should be from the model, eh ?
        String targetDirectory = getExecuteDirectoryName();
        Utilities.insureDir(targetDirectory);
        String targetFileName = targetDirectory + File.separator + file.getName();
        try {
            file.writeFile(targetFileName);

            if (new File(targetFileName).isFile()) {
                fileViewer.addFilePane(title, targetFileName);
            } else {
                fileViewer.addTextPane(title, "Could not create file at " + targetFileName);
            }
        } catch (IOException e) {
            fileViewer.addTextPane(title, "Could not create file at " + targetFileName + "Exception " + e.getMessage());
        }
        if (visible) {
            fileViewer.setVisible(true);
        }
    }

    private String getExecuteDirectoryName() {
        Executable tempEexecutable = new Executable(getContest(), getController(), currentRun, /*runFiles*/ null);
        return tempEexecutable.getExecuteDirectoryName();
    }

    private ProblemDataFiles getProblemDataFiles() {
        Problem problem = getContest().getProblem(currentRun.getProblemId());
        return getContest().getProblemDataFile(problem);
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
                Log log = getController().getLog();
                log.log(Log.SEVERE, "MTSV PassFailCellRenderer: unknown pass/fail result: ", value);
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
    
    public class TimeRenderer extends DefaultTableCellRenderer {
        
        private static final long serialVersionUID = 1L;
        
        public void setValue(Object value) {
            setHorizontalAlignment( SwingConstants.RIGHT );
            setBorder(new EmptyBorder(0,0,0,30));
            setText((String)value);
        }

    }

    public void setData(Run run, Problem problem, ProblemDataFiles problemDataFiles) {
         
        this.currentRun = run;
        this.currentProblem = problem;
        this.currentProblemDataFiles = problemDataFiles;
        
        populateGUI();
    }

} // @jve:decl-index=0:visual-constraint="10,10"
