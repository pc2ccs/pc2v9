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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultComboBoxModel;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.execute.Executable;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.ClientSettings;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.ui.cellRenderer.CheckBoxCellRenderer;
import edu.csus.ecs.pc2.ui.cellRenderer.LinkCellRenderer;
import edu.csus.ecs.pc2.ui.cellRenderer.PassFailCellRenderer;
import edu.csus.ecs.pc2.ui.cellRenderer.RightJustifiedCellRenderer;

/**
 * Multiple data set viewer pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: AutoJudgeSettingsPane.java 2825 2014-08-12 23:22:50Z boudreat $
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/pc2v9/trunk/src/edu/csus/ecs/pc2/ui/AutoJudgeSettingsPane.java $
public class MultiTestSetOutputViewerPane extends JPanePlugin implements TableModelListener {

    private static final long serialVersionUID = 7363093989131251458L;

    /**
     * comparator radio selection
     */
    protected static final String CTYPES = "CTYPES";
    /**
     * viewer radio selection
     */
    protected static final String VTYPES = "VTYPES";
    /**
     * comparator command
     */
    protected static final String COMPARATOR_CMD = "COMPARATOR_CMD";
    /**
     * viewer command
     */
    protected static final String VIEWER_CMD = "VIEWER_CMD";

    /**
     * list of columns
     */
    protected enum COLUMN {
        SELECT_CHKBOX, DATASET_NUM, RESULT, TIME, TEAM_OUTPUT_VIEW, TEAM_OUTPUT_COMPARE, 
            JUDGE_OUTPUT, JUDGE_DATA, VALIDATOR_OUTPUT, VALIDATOR_ERR
    };

    // define the column headers for the table of results
    private String[] columnNames = { "Select", "Data Set #", "Result", "Time (ms)", 
                                        "Team Output", "Compare Outputs", 
                                        "Judge's Output", "Judge's Data", "Validator StdOut",
                                        "Validator StdErr" };

    private JPanel centerPanel = null;

    private final ButtonGroup buttonGroup = new ButtonGroup();

    private final ButtonGroup buttonGroup_1 = new ButtonGroup();

    private  JComboBox<String> pulldownSelectComparator;

    private JTextField textUserSpecifyComparator;

    private JTextField textUserSpecifyViewer;

    private JComboBox<String> pulldownSelectViewer;

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

    private JLabel lblNumTestCasesActuallyRun;

    private MultipleFileViewer currentViewer;
    
    private MultiFileComparator currentComparator ;

    private JScrollPane resultsScrollPane;
    
    private String lastViewer = ""; // internal
    private String lastComparator = ""; // internal

    private Log log ;

    private RunFiles currentRunFiles;

    /**
     * The execute directory for this run.
     */
    private String executableDir;

    private String currentComparatorCmd;

    private String currentViewerCmd;

    private JButton btnCancel;

    private JButton btnUpdate;

    private JTabbedPane multiTestSetTabbedPane;

    /**
     * 
     * TYPES on the radio selects on the options tab
     */
    private enum TYPES { 
        /**
         *  internal comparator/viewer
         */
        INTERNAL,
        /**
         * the selector list
         */
        LIST,
        /**
         * user input
         */
        USER
        };
    
    // default to the INTERNAL comparator/viewer
    private TYPES ctype = TYPES.INTERNAL;

    private TYPES vtype = TYPES.INTERNAL;
    
    private ClientSettings clientSettings = null;

    private JRadioButton rdbtnInternalCompareProgram;

    private JRadioButton rdbtnSpecifyCompareProgram;

    private JRadioButton rdbtnPulldownCompareList;

    private JRadioButton rdbtnInternalViewerProgram;

    private JRadioButton rdbtnPulldownViewerList;

    private JRadioButton rdbtnSpecifyViewerProgram;

    private String[] currentValidatorOutputFileNames;

    private String[] currentValidatorStderrFileNames;

    private JPanel resultsPane;

    private JPanel optionsPane;

    private JPanel choosersPanel;

    private JPanel chooseCompareProgramPanel;

    private JPanel chooseViewerProgramPanel;

    private JPanel optionsButtonPanel;

    private JButton btnRestoreDefaults;

    private JPanel resultsPaneHeaderPanel;

    private JPanel resultsPaneButtonPanel;

    private JCheckBox showFailuresOnlyCheckBox;

    private JButton selectAllButton;

    private JButton unselectAllButton;

    private JButton compareSelectedButton;

    private JButton optionsPaneCloseButton;

    private JButton resultsPaneCloseButton;
    private JLabel lblTotalTestCases;
    private Component horizontalGlue_9;

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
        this.setSize(new Dimension(717, 363));
        this.add(getCenterPanel(), java.awt.BorderLayout.CENTER);

        // TODO Bug 918

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        log = getController().getLog();
        clientSettings = getContest().getClientSettings();
        // attempt to load settings from clientSettings (if set)
        if (clientSettings != null) {
            String tmp = clientSettings.getProperty(CTYPES);
            if (tmp != null) {
                ctype = TYPES.valueOf(tmp);
            }
            tmp = clientSettings.getProperty(COMPARATOR_CMD);
            if (tmp != null) {
                lastComparator = tmp;
                currentComparatorCmd = tmp;
            }
            tmp = clientSettings.getProperty(VTYPES);
            if (tmp != null) {
                vtype = TYPES.valueOf(tmp);

            }
            tmp = clientSettings.getProperty(VIEWER_CMD);
            if (tmp != null) {
                lastViewer = tmp;
                currentViewerCmd = tmp;
            }
            // initialize default lists if needed
            if (ctype == TYPES.LIST) {
                pulldownSelectComparator.setSelectedItem(currentComparatorCmd);
            }
            if (vtype == TYPES.LIST) {
                pulldownSelectViewer.setSelectedItem(currentViewerCmd);
            }
            // now initialize the default text if needed
            if (ctype == TYPES.USER) {
                textUserSpecifyComparator.setText(currentComparatorCmd);
            } else {
                textUserSpecifyComparator.setText("<enter comparator name>");
            }
            if (vtype == TYPES.USER) {
                textUserSpecifyViewer.setText(currentViewerCmd);
            } else {
                textUserSpecifyViewer.setText("<enter viewer name>");
            }
            // initialize the button group selections
            ButtonModel model = null;
            switch (ctype) {
                case LIST:
                    model = rdbtnPulldownCompareList.getModel();
                    break;
                case USER:
                    model = rdbtnSpecifyCompareProgram.getModel();
                    break;
                default:
                    model = rdbtnInternalCompareProgram.getModel();
                    break;
            }
            buttonGroup.setSelected(model, true);
            switch (ctype) {
                case INTERNAL:
                    model = rdbtnInternalViewerProgram.getModel();
                    break;
                case LIST:
                    model = rdbtnPulldownViewerList.getModel();
                    break;
                case USER:
                    model = rdbtnSpecifyViewerProgram.getModel();
                    break;
                default:
                    model = rdbtnInternalViewerProgram.getModel();
                    break;
            }
            buttonGroup_1.setSelected(model, true);
            // make sure these are initialized properly
            getCurrentComparator().setComparatorCommand(lastComparator);
            getCurrentViewer().setViewerCommand(lastViewer);
            // make sure the buttons are disabled now
            enableUpdateCancel(currentComparatorCmd, currentViewerCmd);
        }
    }

    public String getPluginTitle() {
        return "MultiTestSetOutputViewer Pane";
    }

    /**
     * This method initializes and returns a JPanel containing a 
     * JTabbedPane holding the output results and options panes. Note that the method
     * does not fill in any live data; that cannot be done until the View Pane's
     * "setData()" method has been invoked, which doesn't happen until after construction
     * of the View Pane is completed.
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPanel() {
        
        if (centerPanel == null) {
            
            centerPanel = new JPanel();
            BorderLayout cpBorderLayout = new BorderLayout();
            cpBorderLayout.setVgap(0);
            centerPanel.setLayout(cpBorderLayout);

            centerPanel.add(getMultiTestSetTabbedPane(), BorderLayout.CENTER);


        }
        return centerPanel;
    }
    
    /** Returns a JTabbedPane containing the various tabs 
     * for the MultiTestSet results display.
     * 
     * @return the multiTestSetTabbedPane JTabbedPane
     */
    public JTabbedPane getMultiTestSetTabbedPane() {
        
        if (multiTestSetTabbedPane == null) {
            
            multiTestSetTabbedPane = new JTabbedPane(JTabbedPane.TOP);
            multiTestSetTabbedPane.setName("multiTestSetTabbedPane");
            getMultiTestSetTabbedPane().setBorder(new LineBorder(new Color(0, 0, 0)));
            
            // add a tab with a JPanel that will display the results for the test cases
            multiTestSetTabbedPane.addTab("Data Set Results", null, getResultsPane(), 
                    "Show the results of this submission for each test data set");

            // add a tab with a JPanel that will display options for managing the display of data for the test cases
            multiTestSetTabbedPane.addTab("Options", null, getOptionsPane(), "Set options for tools used to display test set results");

        }
        return multiTestSetTabbedPane;
    }

    /**
     * Defines and returns a JPanel containing a Header Panel describing the current run, a JTable displaying
     * test case results for the Run, and a button panel with various control Buttons.
     * 
     * @return the Results Pane JPanel
     */
    private JPanel getResultsPane() {
        
        if (resultsPane == null) {
            
            resultsPane = new JPanel();
            resultsPane.setName("ViewDataSets");
            resultsPane.setLayout(new BorderLayout(0, 0));

            // add a header for holding labels to the results panel
            resultsPane.add(getResultsPaneHeaderPanel(),BorderLayout.NORTH);
            
            //add a scrollpane holding the actual test set results
            resultsPane.add(getResultsScrollPane(), BorderLayout.CENTER);
            
            //add a button panel at the bottom
            resultsPane.add(getResultsPaneButtonPanel(), BorderLayout.SOUTH);
        }
        return resultsPane;
        
    }
    
    /**
     * Returns a JPanel containing options for specifying the current "compare program" and the
     * current "viewer program", along with a button panel with various control Buttons.
     * 
     * @return the Options Pane JPanel
     */
    private JPanel getOptionsPane() {
        
        if (optionsPane == null ) {
            
            optionsPane = new JPanel();
            optionsPane.setLayout(new BorderLayout(0, 0));

            // add a panel that will hold the various chooser options
            optionsPane.add(getChoosersPanel(), BorderLayout.CENTER);
            
            //add a button panel to the bottom of the options pane
            optionsPane.add(getOptionsButtonPanel(),BorderLayout.SOUTH);
        }
        
        return optionsPane;
    }
    

    /**
     * Defines and returns a panel containing various "Choosers" -- one for choosing the Comparator Program,
     * one for choosing the Viewer Program -- along with a button panel for activating chooser options.
     * 
     * @return a ChoosersPanel JPanel
     */
    private JPanel getChoosersPanel() {
        
        if (choosersPanel == null) {
            
            choosersPanel = new JPanel();
            choosersPanel.setLayout (new FlowLayout());

            // add a panel that will support choosing the comparator tool to be used
            choosersPanel.add(getChooseComparatorProgramPanel());
            
            //put some space between the comparator and viewer panels
            Component horizontalStrut = Box.createHorizontalStrut(20);
            horizontalStrut.setMinimumSize(new Dimension(40, 0));
            choosersPanel.add(horizontalStrut);

            //add a panel that will support choosing the viewer program to be used
            choosersPanel.add(getChooseViewerProgramPanel());
        }
        
        return choosersPanel;
    }
            
    /**
     * Defines a JPanel containing the components supporting choosing a Compare Program.
     * 
     * @return the Choose Compare Program JPanel
     */
    private JPanel getChooseComparatorProgramPanel() {
        
        if (chooseCompareProgramPanel == null) {
            
            //define the compare-program chooser panel
            chooseCompareProgramPanel = new JPanel();
            chooseCompareProgramPanel.setBounds(new Rectangle(0, 0, 0, 20));
            chooseCompareProgramPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            chooseCompareProgramPanel.setPreferredSize(new Dimension(220, 200));
            chooseCompareProgramPanel.setBorder(new TitledBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)),
                "Choose Compare Program", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            chooseCompareProgramPanel.setLayout(new BoxLayout(chooseCompareProgramPanel, BoxLayout.Y_AXIS));

            //add some space at the top of the panel
            Component verticalStrut = Box.createVerticalStrut(20);
            verticalStrut.setMinimumSize(new Dimension(0, 10));
            verticalStrut.setPreferredSize(new Dimension(0, 15));
            chooseCompareProgramPanel.add(verticalStrut);

            //add a button to choose the internal built-in comparator
            rdbtnInternalCompareProgram = new JRadioButton("Built-in Comparator");
            rdbtnInternalCompareProgram.setSelected(true);
            buttonGroup.add(rdbtnInternalCompareProgram);
            chooseCompareProgramPanel.add(rdbtnInternalCompareProgram);

            // add a button to enable selection from a list of predefined external comparators
            rdbtnPulldownCompareList = new JRadioButton("Select");
            chooseCompareProgramPanel.add(rdbtnPulldownCompareList);
            buttonGroup.add(rdbtnPulldownCompareList);

            // add a panel to hold the pulldown list of predefined external comparators
            JPanel selectComparatorPanel = new JPanel();
            FlowLayout flowLayout = (FlowLayout) selectComparatorPanel.getLayout();
            flowLayout.setAlignOnBaseline(true);
            selectComparatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            chooseCompareProgramPanel.add(selectComparatorPanel);

            // construct a dropdown list of available comparators and add it to the panel
            String [] comparators = getAvailableComparatorsList();
            //the WindowBuilder GUI builder tool doesn't support the following:
            //pulldownSelectComparator = new JComboBox<String>(comparators);
            //so we'll do it the long way:
            pulldownSelectComparator = new JComboBox<String>();
            pulldownSelectComparator.setModel(new DefaultComboBoxModel<String>(comparators));
            selectComparatorPanel.add(pulldownSelectComparator);
        
            //disable the pulldown by default
            pulldownSelectComparator.setEnabled(false);

            //add a button to enable user-specified compare program
            rdbtnSpecifyCompareProgram = new JRadioButton("User Specified");
            chooseCompareProgramPanel.add(rdbtnSpecifyCompareProgram);
            buttonGroup.add(rdbtnSpecifyCompareProgram);
        
            // add a panel to hold the user-specified comparator text box
            JPanel userSpecifiedComparatorPanel = new JPanel();
            FlowLayout flowLayout_1 = (FlowLayout) userSpecifiedComparatorPanel.getLayout();
            flowLayout_1.setAlignOnBaseline(true);
            userSpecifiedComparatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            chooseCompareProgramPanel.add(userSpecifiedComparatorPanel);

            // add a text box for the user to specify a comparator
            textUserSpecifyComparator = new JTextField();
            textUserSpecifyComparator.setEnabled(false);
            userSpecifiedComparatorPanel.add(textUserSpecifyComparator);
            textUserSpecifyComparator.setColumns(15);
        }
        
        return chooseCompareProgramPanel;
    }


    /**
     * Defines a JPanel containing the components supporting choosing a Viewer Program.
     * 
     * @return the Choose Viewer Program JPanel
     */
    private JPanel getChooseViewerProgramPanel() {

        if (chooseViewerProgramPanel == null) {

            // create a panel to hold the various components allowing choice of viewers
            chooseViewerProgramPanel = new JPanel();
            chooseViewerProgramPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            chooseViewerProgramPanel.setPreferredSize(new Dimension(220, 200));
            chooseViewerProgramPanel.setBorder(new TitledBorder(null, "Choose Viewer Program", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            chooseViewerProgramPanel.setLayout(new BoxLayout(chooseViewerProgramPanel, BoxLayout.Y_AXIS));

            // add some space at the top of the panel
            Component verticalStrut_1 = Box.createVerticalStrut(20);
            verticalStrut_1.setPreferredSize(new Dimension(0, 15));
            verticalStrut_1.setMinimumSize(new Dimension(0, 10));
            chooseViewerProgramPanel.add(verticalStrut_1);

            // add a button to select the built-in viewer program
            rdbtnInternalViewerProgram = new JRadioButton("Built-in Viewer");
            rdbtnInternalViewerProgram.setSelected(true);
            rdbtnInternalViewerProgram.setPreferredSize(new Dimension(117, 23));
            rdbtnInternalViewerProgram.setMinimumSize(new Dimension(117, 23));
            rdbtnInternalViewerProgram.setMaximumSize(new Dimension(117, 23));
            buttonGroup_1.add(rdbtnInternalViewerProgram);
            chooseViewerProgramPanel.add(rdbtnInternalViewerProgram);

            // add a button to enable selection of the viewer program from a list
            rdbtnPulldownViewerList = new JRadioButton("Select");
            buttonGroup_1.add(rdbtnPulldownViewerList);
            chooseViewerProgramPanel.add(rdbtnPulldownViewerList);

            // add a panel to hold the drop-down list of available viewers
            JPanel panel = new JPanel();
            panel.setAlignmentX(Component.LEFT_ALIGNMENT);
            chooseViewerProgramPanel.add(panel);

            // add a drop-down list of available viewers to the panel
            // the WindowBuilder GUI builder tool doesn't support the following:
            // pulldownSelectViewer = new JComboBox<String>(getAvailableViewersList());
            // so we'll do it the long way:
            pulldownSelectViewer = new JComboBox<String>();
            pulldownSelectViewer.setModel(new DefaultComboBoxModel<String>(getAvailableViewersList()));
            panel.add(pulldownSelectViewer);

            // set the pulldown disabled by default
            pulldownSelectViewer.setEnabled(false);

            // add a button to enable selection of a user-specified viewer program
            rdbtnSpecifyViewerProgram = new JRadioButton("User Specified");
            buttonGroup_1.add(rdbtnSpecifyViewerProgram);
            chooseViewerProgramPanel.add(rdbtnSpecifyViewerProgram);

            // add a panel to hold the text box defining the user-specified viewer program
            JPanel panel_1 = new JPanel();
            panel_1.setEnabled(false);
            chooseViewerProgramPanel.add(panel_1);

            // add a text field for the user to specify a viewer
            textUserSpecifyViewer = new JTextField();
            textUserSpecifyViewer.setEnabled(false);
            panel_1.add(textUserSpecifyViewer);
            textUserSpecifyViewer.setColumns(15);
        }
        return chooseViewerProgramPanel;
    }

    /**
     * Defines a panel holding buttons for the Options pane tab.
     * 
     * @return the Options Pane button panel
     */
    private JPanel getOptionsButtonPanel() {
        
        if (optionsButtonPanel == null) {
            
            // define the panel to hold control buttons
            optionsButtonPanel = new JPanel();

            // add a control button to restore defaults
            optionsButtonPanel.add(getRestoreDefaultsButton());

            Component horizontalGlue_4 = Box.createHorizontalGlue();
            horizontalGlue_4.setPreferredSize(new Dimension(20, 20));
            optionsButtonPanel.add(horizontalGlue_4);

            optionsButtonPanel.add(getUpdateButton());

            Component horizontalGlue_5 = Box.createHorizontalGlue();
            horizontalGlue_5.setPreferredSize(new Dimension(20, 20));
            optionsButtonPanel.add(horizontalGlue_5);

            optionsButtonPanel.add(getCancelButton());
            
            Component horizontalGlue_6 = Box.createHorizontalGlue();
            horizontalGlue_6.setPreferredSize(new Dimension(20, 20));
            optionsButtonPanel.add(horizontalGlue_6);

            // add a button to dismiss the frame
            optionsButtonPanel.add(getOptionsPaneCloseButton());

            addOptionsButtonHandlers();
        }
        
        return optionsButtonPanel;
    }
    
    /**
     * Returns the RestoreDefaults button.
     * @return a JButton
     */
    private JButton getRestoreDefaultsButton() {
        if (btnRestoreDefaults == null) {
            btnRestoreDefaults = new JButton("Restore Defaults");
        }
        return btnRestoreDefaults;
    }
    
    /**
     * Returns the Update button.
     * @return a JButton
     */
    private JButton getUpdateButton() {
        if (btnUpdate == null) {
            btnUpdate = new JButton("Update");
            btnUpdate.setEnabled(false);
            
            btnUpdate.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    lastComparator = currentComparatorCmd;
                    lastViewer = currentViewerCmd;
                    getCurrentComparator().setComparatorCommand(lastComparator);
                    getCurrentViewer().setViewerCommand(lastViewer);
                    enableUpdateCancel(currentComparatorCmd, currentViewerCmd);
                    if (clientSettings == null) {
                        clientSettings = new ClientSettings();
                    }
                    clientSettings.put(CTYPES, ctype.toString());
                    clientSettings.put(VTYPES, vtype.toString());
                    clientSettings.put(COMPARATOR_CMD, currentComparatorCmd);
                    clientSettings.put(VIEWER_CMD, currentViewerCmd);
                    getController().updateClientSettings(clientSettings);
                }
            });
        }
        return btnUpdate;
    }
    
    /**
     * Returns the Cancel button.
     * @return a JButton
     */
    private JButton getCancelButton() {
        if (btnCancel == null) {
            btnCancel = new JButton("Cancel");
            btnCancel.setEnabled(false);
            
            //add an action handler for the Cancel button
            btnCancel.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if (ctype == TYPES.USER) {
                        textUserSpecifyComparator.setText(lastComparator);
                    }
                    if (vtype == TYPES.USER) {
                        textUserSpecifyViewer.setText(lastViewer);
                    }
                    // TODO reset selected buttongroup
                    currentComparatorCmd = lastComparator;
                    currentViewerCmd = lastViewer;
                }
            });
        }
        return btnCancel;
    }
        
    private void addOptionsButtonHandlers() {
        
        currentViewerCmd = lastViewer;
        currentComparatorCmd = lastComparator;
        
        //initialize the Update and Cancel buttons
        enableUpdateCancel(currentComparatorCmd,currentViewerCmd);

        //add a state-change handler to the "select built-in compare program" radio button
        rdbtnInternalCompareProgram.addItemListener(new ItemListener() {
            
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (rdbtnInternalCompareProgram.isSelected()) {
                    currentComparatorCmd = "";
                    enableUpdateCancel(currentComparatorCmd,currentViewerCmd);
                    ctype = TYPES.INTERNAL;
                }
            }

        });
        
        // add a state-change handler to the "select compare program from list" radio button
        rdbtnPulldownCompareList.addItemListener(new ItemListener() {
            
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (rdbtnPulldownCompareList.isSelected()) {
                    pulldownSelectComparator.setEnabled(true);
                    ctype = TYPES.LIST;
                } else {
                    pulldownSelectComparator.setEnabled(false);
                }
                currentComparatorCmd = (String) pulldownSelectComparator.getSelectedItem();
                enableUpdateCancel(currentComparatorCmd,currentViewerCmd);
            }
        });
        
        // add a state-change listener to the "select user-specified compare program" radio button
        rdbtnSpecifyCompareProgram.addItemListener(new ItemListener() {
            
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (rdbtnSpecifyCompareProgram.isSelected()) {
                    textUserSpecifyComparator.setEnabled(true);
                    currentComparatorCmd = textUserSpecifyComparator.getText();
                    ctype = TYPES.USER;
                } else {
                    textUserSpecifyComparator.setEnabled(false);
                }
                enableUpdateCancel(currentComparatorCmd,currentViewerCmd);
            }
        });
        
        // add a state-change listener to the "select build-in viewer program" radio button
        rdbtnInternalViewerProgram.addItemListener(new ItemListener() {
            
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if (rdbtnInternalViewerProgram.isSelected()) {
                    currentViewerCmd = "";
                    vtype = TYPES.INTERNAL;
                }
                enableUpdateCancel(currentComparatorCmd,currentViewerCmd);
            }
        });
        
        // add a state-change listener to the "select viewer program from list" radio button
        rdbtnPulldownViewerList.addItemListener(new ItemListener() {
            
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (rdbtnPulldownViewerList.isSelected()) {
                    currentViewerCmd = (String) pulldownSelectViewer.getSelectedItem();
                    pulldownSelectViewer.setEnabled(true);
                    vtype = TYPES.LIST;
                } else {
                    pulldownSelectViewer.setEnabled(false);
                }
                enableUpdateCancel(currentComparatorCmd,currentViewerCmd);
            }
        });
        
        // add a state-change listener to the "select user-specified viewer program" radio button
        rdbtnSpecifyViewerProgram.addItemListener(new ItemListener() {
            
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (rdbtnSpecifyViewerProgram.isSelected()) {
                    textUserSpecifyViewer.setEnabled(true);
                    vtype = TYPES.USER;
                    currentViewerCmd = textUserSpecifyViewer.getText();
                } else {
                    textUserSpecifyViewer.setEnabled(false);
                }
                enableUpdateCancel(currentComparatorCmd,currentViewerCmd);
            }
        });

        //add keylisteners to update the Update and Cancel buttons when the user types in the Choose Viewer textbox
        textUserSpecifyViewer.addKeyListener(new KeyListener() {
            
            @Override
            public void keyTyped(KeyEvent arg0) {
                // unused
            }
            
            @Override
            public void keyReleased(KeyEvent arg0) {
                if (vtype == TYPES.USER) {
                    currentViewerCmd = textUserSpecifyViewer.getText();
                    enableUpdateCancel(currentComparatorCmd, currentViewerCmd);
                }
            }
            
            @Override
            public void keyPressed(KeyEvent arg0) {
                // unused
            }
        });
        
        //add keylisteners to update the Update and Cancel buttons when the user types in the Choose Compare Program textbox
        textUserSpecifyComparator.addKeyListener(new KeyListener() {
            
            @Override
            public void keyTyped(KeyEvent arg0) {
                // unused
            }
            
            @Override
            public void keyReleased(KeyEvent arg0) {
                if (ctype == TYPES.USER) {
                    currentComparatorCmd = textUserSpecifyComparator.getText();
                    enableUpdateCancel(currentComparatorCmd, currentViewerCmd);
                }
            }
            
            @Override
            public void keyPressed(KeyEvent arg0) {
                // unused
            }
        });
    }

    
    /**
     * Defines a header panel for the results pane containing information about the run whose results are being displayed.
     * Note that this accessor does not fill in actual data; that cannot be done until the Test Results pane is populated
     * via a call to {@link #setData(Run, RunFiles, Problem, ProblemDataFiles)}. 
     * 
     * @return a JPanel containing run information
     */
    private JPanel getResultsPaneHeaderPanel() {
        
        if (resultsPaneHeaderPanel == null) {
            
            resultsPaneHeaderPanel = new JPanel();
            resultsPaneHeaderPanel.setBorder(new LineBorder(Color.BLUE, 2));

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
            resultsPaneHeaderPanel.add(getTeamNumberLabel());

            Component horizontalGlue_2 = Box.createHorizontalGlue();
            horizontalGlue_2.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue_2);

            resultsPaneHeaderPanel.add(getLanguageLabel());

            Component horizontalGlue_8 = Box.createHorizontalGlue();
            horizontalGlue_8.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue_8);
            resultsPaneHeaderPanel.add(getTotalTestCasesLabel());
            resultsPaneHeaderPanel.add(getHorizontalGlue_9());

            // add a label to the header showing the total number of test cases for this problem
            resultsPaneHeaderPanel.add(getNumTestCasesActuallyRunLabel());

            Component horizontalGlue_7 = Box.createHorizontalGlue();
            horizontalGlue_7.setPreferredSize(new Dimension(20, 20));
            resultsPaneHeaderPanel.add(horizontalGlue_7);

            resultsPaneHeaderPanel.add(getNumFailedTestCasesLabel());
        }
        return resultsPaneHeaderPanel;
    }

    /**
     * Returns a {@link JScrollPane} containing a {@link JTable} for holding test case results.
     * @return
     */
    private JScrollPane getResultsScrollPane() {
        
        if (resultsScrollPane == null) {
            
            // add a scrollpane to hold the table of results
            resultsScrollPane = new JScrollPane();

            // create an (empty) table of results and put it in the scrollpane
            resultsTable = new JTable(12,7);
            resultsTable.setValueAt(true, 0, 0);
            resultsScrollPane.setViewportView(resultsTable);
        }
        return resultsScrollPane;
    }
    
    /**
     * Returns a JPanel containing control buttons for the Results Pane.
     * @return the resultsPaneButtonPanel
     */
    private JPanel getResultsPaneButtonPanel() {
        
        if (resultsPaneButtonPanel == null) {
            
            resultsPaneButtonPanel = new JPanel();

            //add a checkbox to allow filtering between all test cases and only failed test cases
            resultsPaneButtonPanel.add(getShowFailuresOnlyCheckbox());

            //add space
            Component horizontalStrut_1 = Box.createHorizontalStrut(20);
            resultsPaneButtonPanel.add(horizontalStrut_1);

            //add a button to select all test cases in the grid
            resultsPaneButtonPanel.add(getSelectAllButton());
            
            //add space
            Component horizontalStrut_3 = Box.createHorizontalStrut(20);
            resultsPaneButtonPanel.add(horizontalStrut_3);
            
            //add a button to unselect all the test cases in the grid
            resultsPaneButtonPanel.add(getUnselectAllButton());

            //add space
            Component horizontalStrut_2 = Box.createHorizontalStrut(20);
            resultsPaneButtonPanel.add(horizontalStrut_2);
            
            // add a control button to invoke comparison of the team and judge output files for selected row(s)
            resultsPaneButtonPanel.add(getCompareSelectedButton());

            //add space
            Component horizontalGlue_3 = Box.createHorizontalGlue();
            horizontalGlue_3.setPreferredSize(new Dimension(20, 20));
            resultsPaneButtonPanel.add(horizontalGlue_3);

            // add a control button to dismiss the frame
            resultsPaneButtonPanel.add(getResultsPaneCloseButton());
        }
        return resultsPaneButtonPanel;
    }
    
    /**
     * Defines a checkbox whose action is to allow filtering to show either all test cases or only failed test cases.
     * @return the ShowFailuresOnly JCheckBox
     */
    private JCheckBox getShowFailuresOnlyCheckbox() {
        
        if (showFailuresOnlyCheckBox == null) {
            
            showFailuresOnlyCheckBox = new JCheckBox("Show Failures Only", false);
            showFailuresOnlyCheckBox.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) { 
                    if (((JCheckBox) (e.getSource())).isSelected()) {
                        loadTableWithFailedTestCases();
                    } else {
                        loadTableWithAllTestCases();
                    }
                    updateCompareSelectedButton();
                }
    
            });
        }
        return showFailuresOnlyCheckBox;
    }
    
    /**
     * Returns a JButton whose action is to select all test cases in the results grid.
     * @return the Select All JButton
     */
    private JButton getSelectAllButton() {
        
        if (selectAllButton == null) {
            
            selectAllButton = new JButton("Select All");
            selectAllButton.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    // mark every checkbox in the "Select" column of the results table as "Selected" 
                    TableModel tm = resultsTable.getModel();
                    if (tm != null) {
                        int col = resultsTable.getColumn(columnNames[COLUMN.SELECT_CHKBOX.ordinal()]).getModelIndex();
                        for (int row = 0; row < tm.getRowCount(); row++) {
                            tm.setValueAt(new Boolean(true), row, col);
                        }
                        updateCompareSelectedButton();
                    }
                }
            });
        }
        return selectAllButton;
    }
    
    /**
     * Returns a JButton whose action is to unselect all test cases in the results grid.
     * @return the Unselect All JButton
     */
    private JButton getUnselectAllButton() {
        
        if (unselectAllButton == null) {
            
            unselectAllButton = new JButton("Unselect All");
            unselectAllButton.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    // mark every checkbox in the "Select" column of the results table as "Unselected" 
                    TableModel tm = resultsTable.getModel();
                    if (tm != null) {
                        int col = resultsTable.getColumn(columnNames[COLUMN.SELECT_CHKBOX.ordinal()]).getModelIndex();
                        for (int row = 0; row < tm.getRowCount(); row++) {
                            tm.setValueAt(new Boolean(false), row, col);
                        }
                        //disable compare button when there are no test cases selected!
                        updateCompareSelectedButton();
                    }
                }
            });
        }
        return unselectAllButton;
    }
    
    /**
     * Returns a JButton whose action is to open a comparator to compare all selected test cases in the results grid.
     * @return the Compare Selected JButton
     */
    private JButton getCompareSelectedButton() {
        
        if (compareSelectedButton == null) {
            
            compareSelectedButton = new JButton("Compare Selected");
            compareSelectedButton.setToolTipText("Show comparison between Team and Judge output for selected row(s)");
            
            compareSelectedButton.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    //insure there are some selected rows (it shouldn't be possible to get here if not - the button shouldn't have been enabled)
                    int [] selectedRowNums = getSelectedRowNums();
                    if (selectedRowNums.length <= 0) {
                        getController().getLog().log(Log.SEVERE, "Compare Selected button enabled when there are no selected table data rows to compare! ");
                        throw new RuntimeException("Compare Selected button enabled when there are no selected table data rows to compare! ");
                    } else {
                        // display a comparison between the Team and Judge output in the selected table row(s)
                        compareFiles( selectedRowNums );
                    }
                }
            });

            //disable the compare button by default -- it should only be enabled when the grid is populated (and there are selected test cases)
            compareSelectedButton.setEnabled(false);
        }
        return compareSelectedButton;
    }
     
    /**
     * Returns a JButton whose action is to depends on whether it is invoked from the Options pane or the Results pane.
     * If invoked from the Options pane, it simply makes the Results pane the active pane.
     * If invoked from the Results pane, it closes any open child windows (such as a {@link MultiFileComparator}), and
     * then disposes this MultiTestSetOutputViewerPane's parent window.
     * 
     * Note that the Close button appears on both the Options pane and the Results pane -- but it is the same button,
     * not two different instances (this is a result of the Singleton pattern implementation).
     * 
     * Addendum:  the above was the INTENT, and it is legal in Java/Swing to code it that way.  However, doing so generates 
     * an error in the WindowBuilder (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=341111).  As a result, the code was
     * refactored to provide two distinct Close buttons: one for the Options pane and a different one for the Results pane.
     * 
     * @return the Close JButton
     */
   private JButton getOptionsPaneCloseButton() {
        if (optionsPaneCloseButton == null) {
            optionsPaneCloseButton = new JButton("Close");
            
            //add an action handler for the Close button
            optionsPaneCloseButton.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    //check if we're closing the Options pane
                    if (getMultiTestSetTabbedPane().getSelectedIndex() > 0) {
                        //yes; just go back to the Test Results pane
                        getMultiTestSetTabbedPane().setSelectedIndex(0);
                    } else {
                        //no; we're closing the Test Results pane -- get rid of any child panes
                        if (currentComparator != null) {
                            currentComparator.dispose();
                        }
                        Window parentFrame = SwingUtilities.getWindowAncestor(optionsPaneCloseButton);
                        if (parentFrame != null) {
                            parentFrame.dispose();                    
                        }
                    }
                }
            });
        }
        return optionsPaneCloseButton;
    }
   
   /**
    * Returns a JButton whose action is to depends on whether it is invoked from the Options pane or the Results pane.
    * If invoked from the Options pane, it simply makes the Results pane the active pane.
    * If invoked from the Results pane, it closes any open child windows (such as a {@link MultiFileComparator}), and
    * then disposes this MultiTestSetOutputViewerPane's parent window.
    * 
    * Note that the Close button appears on both the Options pane and the Results pane -- but it is the same button,
    * not two different instances (this is a result of the Singleton pattern implementation).
    * 
    * Addendum:  the above was the INTENT, and it is legal in Java/Swing to code it that way.  However, doing so generates 
    * an error in the WindowBuilder (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=341111).  As a result, the code was
    * refactored to provide two distinct Close buttons: one for the Options pane and a different one for the Results pane.
    * 
    * @return the Close JButton
    */
  private JButton getResultsPaneCloseButton() {
       if (resultsPaneCloseButton == null) {
           resultsPaneCloseButton = new JButton("Close");
           
           //add an action handler for the Close button
           resultsPaneCloseButton.addActionListener(new ActionListener() {
               
               @Override
               public void actionPerformed(ActionEvent e) {
                   //check if we're closing the Options pane
                   if (getMultiTestSetTabbedPane().getSelectedIndex() > 0) {
                       //yes; just go back to the Test Results pane
                       getMultiTestSetTabbedPane().setSelectedIndex(0);
                   } else {
                       //no; we're closing the Test Results pane -- get rid of any child panes
                       if (currentComparator != null) {
                           currentComparator.dispose();
                       }
                       Window parentFrame = SwingUtilities.getWindowAncestor(resultsPaneCloseButton);
                       if (parentFrame != null) {
                           parentFrame.dispose();                    
                       }
                   }
               }
           });
       }
       return resultsPaneCloseButton;
   }
  
   /**
    * Checks the current Results JTable and enables the "Compare Selected" button if there is at least one row selected
    * (that is, with a check in the left-most column's checkbox); if there is no selected (checked) row, the button is
    * disabled.
    */
   public void updateCompareSelectedButton() {
       getCompareSelectedButton().setEnabled(false);
       if (resultsTable != null) {
           for (int i=0; i<resultsTable.getRowCount(); i++) {
               if ((Boolean)resultsTable.getValueAt(i, COLUMN.SELECT_CHKBOX.ordinal())){
                   //found at least one selected row
                   getCompareSelectedButton().setEnabled(true);
                   break;
               }
           }
       }
   }
    

    /**
     * @return the currentViewer
     */
    public MultipleFileViewer getCurrentViewer() {
        if (currentViewer == null) {
            currentViewer = new MultipleFileViewer(getController().getLog());
        }
        return currentViewer;
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

    private JLabel getTotalTestCasesLabel() {
        if (lblTotalTestCases == null) {
            lblTotalTestCases = new JLabel("Total Test Cases: XXX");
        }
        return lblTotalTestCases;
    }

    /**
     * @return
     */
    private JLabel getNumTestCasesActuallyRunLabel() {
        if (lblNumTestCasesActuallyRun == null) {
            lblNumTestCasesActuallyRun = new JLabel("Test Cases Run: XXX");
        }
        return lblNumTestCasesActuallyRun;
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
                // remove any middle tabs (compiler output/stderr)
                while(getMultiTestSetTabbedPane().getTabCount() > 2) {
                    getMultiTestSetTabbedPane().removeTabAt(1);
                }
                populateCompilerOutput();
                getMultiTestSetTabbedPane().setSelectedIndex(0);
                // fill in the basic header information
                String letter = currentProblem.getLetter();
                String shortName = currentProblem.getShortName();
                // HACK around EditProblemPane not requiring (or setting) a letter
                if (letter == null || letter.equals("null") || letter.equals("")) {
                    Problem[] problems = getContest().getProblems();
                    for (int i = 0; i < problems.length; i++) {
                        if (problems[i].equals(currentProblem)) {
                            int asciiLetter = i+65;
                            letter = Character.toString((char)asciiLetter);
                        }
                    }
                }
                // HACK around EditProblempane not requiring (or setting) a shortName
                if (shortName == null || shortName.equals("")) {
                    shortName = currentProblem.getDisplayName().toLowerCase().trim();
                    int spaceIndex = shortName.indexOf(" ");
                    if (spaceIndex > 0) {
                        shortName = shortName.substring(0, spaceIndex);
                    }
                }
                getProblemTitleLabel().setText("Problem:  " + letter + " - " + shortName);
                getTeamNumberLabel().setText("Team:  " + currentRun.getSubmitter().getClientNumber());
                getRunIDLabel().setText("Run ID:  " + currentRun.getNumber());
                getLanguageLabel().setText("Language:  " + getCurrentRunLanguageName());

                //display in the GUI the total test cases configured in the problem
                int totalTestCases = currentProblem.getNumberTestCases();
                getTotalTestCasesLabel().setText("Total Test Cases: " + totalTestCases);
                
                // get the actually-run test case results for the current run
                RunTestCase[] testCases = getCurrentTestCases(currentRun);

                // fill in the test case summary information
                if (testCases == null) {
                    getNumTestCasesActuallyRunLabel().setText("Test Cases Run:  0");
                } else {
                    getNumTestCasesActuallyRunLabel().setText("Test Cases Run:  " + testCases.length);
//                    System.out.println("MTSVPane.populateGUI(): loading " + testCases.length + " test cases into GUI pane...");
//                  for (int i = 0; i < testCases.length; i++) {
//                    System.out.println("  Test Case " + testCases[i].getTestNumber() + ": " + testCases[i]);
//                  }
                }

                int failedCount = getNumFailedTestCases(testCases);
                if (!currentProblem.isValidatedProblem()) {
                    // problem is not validated, cannot be failed or passed
                    getNumFailedTestCasesLabel().setForeground(Color.black);
                    getNumFailedTestCasesLabel().setText("No validator set");
                } else  if (failedCount > 0) {
                    getNumFailedTestCasesLabel().setForeground(Color.red);
                    getNumFailedTestCasesLabel().setText("Failed:  " + failedCount);
                } else if (testCases == null || testCases.length == 0) {
                    getNumFailedTestCasesLabel().setForeground(Color.ORANGE);
                    getNumFailedTestCasesLabel().setText("ERROR");
                } else {
                    getNumFailedTestCasesLabel().setForeground(Color.green);
                    getNumFailedTestCasesLabel().setText("ALL PASSED");
                }
                
                resultsTable = getResultsTable(testCases);
                getResultsScrollPane().setViewportView(resultsTable);
                
                updateCompareSelectedButton();
            }

        });

    }

    // copied from MultipleFileViewer
    public boolean loadFile(JTextArea jPane, String filename) {
        try {

            jPane.setFont(new Font("Courier", Font.PLAIN, 12));
            BufferedReader inFile = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            
            StringBuffer sb = new StringBuffer();
            char[] cbuf = new char[8000];
            int n = inFile.read(cbuf);
            while(n > -1) {
                sb.append(cbuf, 0, n);
                n = inFile.read(cbuf);
            }
            inFile.close();
            inFile = null;
            String s = sb.toString();

//            String oldTitle = getTitle();
//            setTitle("Loading " + filename + " ... ");

            jPane.append(s);
            jPane.setCaretPosition(0);

//            setTitle(oldTitle);
            return true;
        } catch (Exception e) {
            if (getController().getLog() != null) {
                getController().getLog().severe("MultiTestSetOutputViewerPane.loadFile(): exception " + e);
            }
            System.err.println("MultiTestSetOutputViewerPane.loadFile(): exception " + e);
        }
        return false;
    }

    public boolean addFilePane(String title, String filename) {

        if (title == null) {
            title = filename;
        }
        if (title.length() < 1) {
            title = filename;
        }

        JTextArea textArea = new JTextArea();
        textArea.setBounds(0, 0, 11, 6);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(textArea);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new java.awt.BorderLayout());
        jPanel.add(scrollPane, "Center");

        getMultiTestSetTabbedPane().insertTab(title, null, jPanel, null, 1);

        return loadFile(textArea, filename);
    }

    private void populateCompilerOutput() {
        // removing existing compile tabs handled in populateGUI
        String outputFile = executableDir + File.separatorChar+Executable.COMPILER_STDOUT_FILENAME;
        File file = new File(outputFile);
        if (file.isFile() && file.length() > 0) {
            addFilePane("Compiler stdout", outputFile);
        }

        outputFile = executableDir+ File.separatorChar + Executable.COMPILER_STDERR_FILENAME ;
        file = new File(outputFile);
        if (file.isFile() && file.length() > 0) {
            addFilePane("Compiler stderr", outputFile);
        }
    }

    private int getNumFailedTestCases(RunTestCase[] testCases) {
        int failed = 0 ;
        if (testCases != null) {
            for (int i = 0; i < testCases.length; i++) {
                if (!testCases[i].isPassed()) {
                    failed++;
                    // int num = i+1;
                    // System.out.println("Found failed test case: " + num);
                }
            }
        }
//        System.out.println ("  (including " + failed + " failed cases)");
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
     * Looks at all the TestCases for a run and filters
     * that list to just the most recent.
     * 
     * @param run
     * @return most recent RunTestCases
     */
    private RunTestCase[] getCurrentTestCases(Run run) {
        RunTestCase[] testCases = null;
        RunTestCase[] allTestCases = run.getRunTestCases();
        // hope the lastTestCase has the highest testNumber....
        if (allTestCases != null && allTestCases.length > 0) {
            testCases = new RunTestCase[allTestCases[allTestCases.length-1].getTestNumber()];
            for (int i = allTestCases.length-1; i >= 0; i--) {
                RunTestCase runTestCase = allTestCases[i];
                int testCaseNumIndex = runTestCase.getTestNumber()-1;
                if (testCases[testCaseNumIndex] == null) {
                    testCases[testCaseNumIndex] = runTestCase;
                    if (testCaseNumIndex == 0) {
                        break;
                    }
                }
            }
        }
        return testCases;
    }

    /**
     * Loads the result table with data for all test cases.
     */
    private void loadTableWithAllTestCases() {
        
        // get the test case results for the current run
        RunTestCase[] allTestCases = getCurrentTestCases(currentRun);
        
        //build a new table with the test cases and install it in the scrollpane
        resultsTable = getResultsTable(allTestCases);
        resultsScrollPane.setViewportView(resultsTable);
    }

    /**
     * Loads the result table with data for failed test cases (only).
     */
    private void loadTableWithFailedTestCases() {
        
        // get the test case results for the current run
        RunTestCase[] allTestCases = getCurrentTestCases(currentRun);
        
        //extract failed cases into a Vector (list)
        Vector<RunTestCase> failedTestCaseList = new Vector<RunTestCase>();
        for (int i=0; i<allTestCases.length; i++) {
            if (!allTestCases[i].isPassed()) {
                failedTestCaseList.add(allTestCases[i]);
            }
        }

        //convert Vector to array
        RunTestCase[] failedTestCases =  failedTestCaseList.toArray(new RunTestCase[failedTestCaseList.size()]);

        //build a new table with just the failed cases and install it in the scrollpane
        resultsTable = getResultsTable(failedTestCases);
        resultsScrollPane.setViewportView(resultsTable);
    }

    /**
     * Returns a JTable containing the results information for the specified set of test cases. 
     * The method sets not only the table data model but also the appropriate cell renderers and 
     * action/mouse listeners for the table.
     */
    private JTable getResultsTable(RunTestCase [] testCases) {

        final JTable localResultsTable;

        //create the results table
        TableModel tableModel = new TestCaseResultsTableModel(testCases, columnNames) ;
        
        tableModel.addTableModelListener(this);
        
//        System.out.println ("Table model contains:");
//        System.out.println ("--------------");
//        for (int row=0; row<tableModel.getRowCount(); row++) {
//            for (int col=0; col<tableModel.getColumnCount(); col++) {
//                System.out.print("[" + tableModel.getValueAt(row, col) + "]");
//            }
//            System.out.println();
//        }
//        System.out.println ("--------------");
        
        localResultsTable = new JTable(tableModel);
        
        //set the desired options on the table
        localResultsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        localResultsTable.setFillsViewportHeight(true);
        localResultsTable.setRowSelectionAllowed(false);
        localResultsTable.getTableHeader().setReorderingAllowed(false);
        
        //add a listener for selection events on the table
        localResultsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            // insure "compare" button is only enabled when at least one table row is selected
            public void valueChanged(ListSelectionEvent e) {
                compareSelectedButton.setEnabled(getSelectedRowNums().length>0);
            }
        });
        
        //initialize column renderers based on column type

        // set a centering renderer on desired table columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        localResultsTable.getColumn(columnNames[COLUMN.DATASET_NUM.ordinal()]).setCellRenderer(centerRenderer);
       
        //set our own checkbox renderer (don't know how to add an ActionListener to the default checkboxes)
        localResultsTable.getColumn(columnNames[COLUMN.SELECT_CHKBOX.ordinal()]).setCellRenderer(new CheckBoxCellRenderer());

        // set a LinkRenderer on those cells containing links
        localResultsTable.getColumn(columnNames[COLUMN.TEAM_OUTPUT_VIEW.ordinal()]).setCellRenderer(new LinkCellRenderer());
        localResultsTable.getColumn(columnNames[COLUMN.TEAM_OUTPUT_COMPARE.ordinal()]).setCellRenderer(new LinkCellRenderer());
        localResultsTable.getColumn(columnNames[COLUMN.JUDGE_OUTPUT.ordinal()]).setCellRenderer(new LinkCellRenderer());
        localResultsTable.getColumn(columnNames[COLUMN.JUDGE_DATA.ordinal()]).setCellRenderer(new LinkCellRenderer());
        localResultsTable.getColumn(columnNames[COLUMN.VALIDATOR_OUTPUT.ordinal()]).setCellRenderer(new LinkCellRenderer());
        localResultsTable.getColumn(columnNames[COLUMN.VALIDATOR_ERR.ordinal()]).setCellRenderer(new LinkCellRenderer());

        // render Result column as Pass/Fail on Green/Red
        localResultsTable.getColumn(columnNames[COLUMN.RESULT.ordinal()]).setCellRenderer(new PassFailCellRenderer());

        // render Time column right-justified
        localResultsTable.getColumn(columnNames[COLUMN.TIME.ordinal()]).setCellRenderer(new RightJustifiedCellRenderer());

        // force table column widths to nice values
//         resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
         localResultsTable.getColumn(columnNames[COLUMN.SELECT_CHKBOX.ordinal()]).setPreferredWidth(15);

        // add a listener to allow users to click an output or data file name and display it
        localResultsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable) e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                
                String resultString = "";
                try {
                    resultString = ((JLabel)target.getValueAt(row, COLUMN.RESULT.ordinal())).getText(); 
                } catch (ClassCastException e1) {
                     if (getController().getLog() != null) {
                        getController().getLog().warning("MultiTestSetOutputViewerPane.getResultsTable(): expected to find a JLabel in resultsTable; exception: "
                                + e1.getMessage());
                    } else {
                        System.err.println("MultiTestSetOutputViewerPane.getResultsTable(): expected to find a JLabel in resultsTable; exception: "
                                + e1.getMessage());
                    }
                    return;
                }
                boolean rowRepresentsExecutedTestCase = resultString.equalsIgnoreCase("Pass") || resultString.equalsIgnoreCase("Fail");
                
                if ((column == COLUMN.TEAM_OUTPUT_VIEW.ordinal() && rowRepresentsExecutedTestCase) 
                        || column == COLUMN.JUDGE_OUTPUT.ordinal() 
                        || column == COLUMN.JUDGE_DATA.ordinal() 
                        || (column == COLUMN.VALIDATOR_OUTPUT.ordinal() && rowRepresentsExecutedTestCase)
                        || (column == COLUMN.VALIDATOR_ERR.ordinal() && rowRepresentsExecutedTestCase)) {
                    viewFile(row, column);
                } else if ((column == COLUMN.TEAM_OUTPUT_COMPARE.ordinal() || e.getClickCount() > 1) && rowRepresentsExecutedTestCase) {
                    // compare the team and judge's output in the active row
                    int[] rows = new int[] { row };
                    compareFiles(rows);
                }
            }
        });

        //only add unexecuted test cases to the results display if we're NOT "showing failures only"
        // (if we are "showingFailuresOnly" then there won't be any "unexecuted" test cases in the results -- only failed test cases
        if (!getShowFailuresOnlyCheckbox().isSelected()) {
            addAnyUnexecutedTestCasesToResultsTable(localResultsTable);
        }
        
        return localResultsTable;
    }
    
    private void addAnyUnexecutedTestCasesToResultsTable(JTable resultsTable) {
        
//        System.out.println ("In addAnyUnexecutedTestCasesToResultsTable...");
        
        //there can only be missing (unexecuted) test case results if the problem is stopOnFirstFailedTestCase
        if (!currentProblem.isStopOnFirstFailedTestCase()) {
//            System.out.println("...problem is not StopOnFirstFailedTestCase; there cannot be any unexecuted test cases.");
            return;
        } 

        TestCaseResultsTableModel tableModel = (TestCaseResultsTableModel) resultsTable.getModel();
        
        int totalTestCaseCount = currentProblem.getNumberTestCases();
        
//        System.out.println("Total test cases defined in problem: " + totalTestCaseCount);
        
        //there *might* be missing test cases -- check whether this is actually so
        int testCasesInTableModel = tableModel.getRowCount();
        
//        System.out.println("Test case rows already in results table: " + testCasesInTableModel);
        
        if (totalTestCaseCount > testCasesInTableModel) {
            //yes, there are missing cases; add them to the table
            for (int testCaseNum=testCasesInTableModel+1; testCaseNum<=totalTestCaseCount; testCaseNum++) {
                //add the current unexecuted test case to the table model
//                System.out.println ("...adding unexecuted test case " + testCaseNum + " to results table");
                //build the row object and add it to the model
                tableModel.addRow(
                        new Boolean(false),                         //selection checkbox
                        new String(Integer.toString(testCaseNum)),  //test case number
                        "Not Executed",                             //result string
                        "--  ",                                     //execution time (of which there is none since the test case wasn't executed)
                        "",                                         //link to team output (none)
                        "",                                         //link to team compare-with-judge label (none)
                        "View",                                     //link to judge's data
                        "View",                                     //link to judge's output (answer file)
                        "",                                         //link to validator stdout (none)
                        "" );                                       //link to validator stderr (none)
            }
            
        } else {
//            System.out.println("all test cases were executed and are already in the results table.");
        }
    }
    
    /**
     * Invoked when table data changes; checks to see if the change took place in the "Select" column and if so
     * updates the Compare Selected button (enables or disables it as necessary).
     * 
     * @param e the TableModelEvent describing the change in the table model
     */
    public void tableChanged(TableModelEvent e) {
        int column = e.getColumn();
        TableModel model = (TableModel)e.getSource();
        String columnName = model.getColumnName(column);
        if (columnName == columnNames[COLUMN.SELECT_CHKBOX.ordinal()]) {
            updateCompareSelectedButton();
        }
    }

    
    /**
     * Returns an array of Strings listing the names of available (known) output viewer tools.
     * 
     * @return a String array of viewer tool names
     */
    private String[] getAvailableViewersList() {
        // TODO figure out how to coordinate this with actual known viewers
        // gvim options
        // --nofork so we can kill it when the pane closes
        // -n to not create swap files
        // -M do not allow changes
        return new String[] { "gvim --nofork -n -M", "notepad", "write" };
    }

    /**
     * This method updates the state of the Update & Cancel buttons as appropriate.
     * 
     * @param theCurrentComparatorCmd
     * @param theCurrentViewerCmd
     */
    private void enableUpdateCancel(String theCurrentComparatorCmd, String theCurrentViewerCmd) {
        boolean state = true;
        if (theCurrentComparatorCmd.equals(lastComparator) && theCurrentViewerCmd.equals(lastViewer)) {
            state = false;
        }
        btnUpdate.setEnabled(state);
        btnCancel.setEnabled(state);
    }

    /**
     * Returns an array of Strings listing the names of available (known) output comparator tools.
     * 
     * @return a String array of comparator tool names
     */
    private String[] getAvailableComparatorsList() {
        // TODO figure out how to coordinate this with actual known comparators
        // gvim options
        // --nofork so we can kill it when the pane closes
        // -n to not create swap files
        // -M do not allow changes
        // -d diff mode
        return new String[] { "gvim --nofork -n -M -d" };
    }

    public void showMessage(final String message) {
        JOptionPane.showMessageDialog(this, message);
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
        if (col != COLUMN.TEAM_OUTPUT_VIEW.ordinal() && col != COLUMN.JUDGE_OUTPUT.ordinal() && col != COLUMN.JUDGE_DATA.ordinal() && col != COLUMN.VALIDATOR_OUTPUT.ordinal()
                && col != COLUMN.VALIDATOR_ERR.ordinal()) {
            if (log != null) {
                log.log(Log.WARNING, "MTSOVPane.viewFile(): invalid column number for file viewing request: " + col);
            } else {
                System.err.println ("Invalid column number for file viewing request: " + col);
            }
            return;
        }
        
        //make sure this isn't a selection in a row representing an un-executed test case
        RunTestCase[] allTestCases = getCurrentTestCases(currentRun);
        
        //check if the selected row was added as an "unexecuted test case"; skip it unless the click was on Judge's Output or data in that row
        if (row >= allTestCases.length && col != COLUMN.JUDGE_OUTPUT.ordinal() && col != COLUMN.JUDGE_DATA.ordinal() ) {
            return;
        }
        
        if (getCurrentViewer() != null) {
            getCurrentViewer().dispose();
            // we are viewing one file, make sure it will only have the 1 tab
            currentViewer = null;
        }
        
        // and make sure its in the right mode
        getCurrentViewer().setViewerCommand(currentViewerCmd);

        //get a title based on what column was selected
        String title = "<unknown>";
        if (col == COLUMN.TEAM_OUTPUT_VIEW.ordinal()) {
            title = "Team Output"; 
        } else if (col == COLUMN.JUDGE_OUTPUT.ordinal()) {
            title = "Judge's Output";
        } else if (col == COLUMN.JUDGE_DATA.ordinal()) {
            title = "Judge's Data";
        } else if (col == COLUMN.VALIDATOR_OUTPUT.ordinal()) {
            title = "Validator STDOUT";
        } else if (col == COLUMN.VALIDATOR_ERR.ordinal()) {
            title = "Validator STDERR";
        }
        
        //get the file associated with the specified cell
        String targetFile = getFileForTableCell(row,col);
        if (targetFile != null) {
            int testCaseNum = row + 1;
            showFile(getCurrentViewer(), targetFile, title, "Test Case "+testCaseNum, true);
        } else {
            if (col == COLUMN.TEAM_OUTPUT_VIEW.ordinal()) {
                String msg = "No Team Output file found (has the Team submission been executed?)" ;
                if (log != null) {
                    log.log(Log.WARNING, "MTSOVPane.viewFile(): " + msg);
                } else {
                    System.err.println ("MTSOVPane.viewFile(): " + msg);
                }
                JOptionPane.showMessageDialog(getParentFrame(), msg, 
                        "File Not Found", JOptionPane.WARNING_MESSAGE);
            } else {
                String msg = "No "+title+" found";
                JOptionPane.showMessageDialog(getParentFrame(), msg, 
                        "File Not Found", JOptionPane.INFORMATION_MESSAGE);
                
            }
        }
    }
    
    /**
     * Generates a {@link MultiFileComparator} comparing team and judge's outputs for each of 
     * the test cases specified by the elements of the input "rows" array.
     * @param rows - the rows of the table for which team/judge outputs should be compared
     */
    private void compareFiles(int [] rows) {
        
//        System.out.print ("MTSVPane.compareFiles(): displaying comparison of files for test case(s) ");
//        for (int i=0; i<rows.length; i++) {
//            System.out.print ((int) (new Integer((String)(resultsTable.getModel().getValueAt(rows[i], 1)))) + " ");
//        }
//        System.out.println ();
 
        //create arrays to hold data to be loaded into the comparator
        int [] testCases = new int [rows.length];
        String [] judgesOutputFileNames = new String [rows.length];
        String [] judgesDataFileNames = new String [rows.length];
        String [] teamOutputFileNames = new String [rows.length];

        //get the judge's information defined in the current problem
        String[] judgesAnswerFiles = currentProblemDataFiles.getFullJudgesAnswerFilenames(getContest(), executableDir);
        String[] judgesDataFiles = currentProblemDataFiles.getFullJudgesDataFilenames(getContest(), executableDir);
        
        
        //load the comparator input arrays
        for (int i=0; i<rows.length; i++) {
            //get the test case defined in the second column of the current table row
            testCases[i] = (int) (new Integer((String)(resultsTable.getModel().getValueAt(rows[i], 1))));
            //get the full path to the judge's answer and data files
            int testCaseIndex = testCases[i]-1;
            if (judgesAnswerFiles.length > testCaseIndex) {
                judgesOutputFileNames[i] = judgesAnswerFiles[testCaseIndex];
            }
            if (judgesDataFiles.length > testCaseIndex) {
                judgesDataFileNames[i] = judgesDataFiles[testCaseIndex];
            }
            //make sure the team output file(s) were defined (they have to be loaded by a client
            // making a separate call to setTeamOutputFileNames; make sure the client complied)
            if (currentTeamOutputFileNames == null || currentTeamOutputFileNames.length<teamOutputFileNames.length) {
                if (log!=null) {
                    log.warning("MTSOVPane.compareFiles(): invalid team output file names array");
                } else {
                    System.err.println ("MTSOVPane.compareFiles(): invalid team output file names array");
                }
            } else {
                //get the team output file name, which should be provided by the client as a full path
                teamOutputFileNames[i] = currentTeamOutputFileNames[testCases[i]-1];  
            }  
        }
                
        //put the data into the comparator
        getCurrentComparator().setData(currentRun.getNumber(), testCases, teamOutputFileNames, judgesOutputFileNames, judgesDataFileNames);
        
        //make the comparator visible
        getCurrentComparator().setVisible(true);

    }

    /**
     * 
     */
    private MultiFileComparator getCurrentComparator() {
        //make sure we have a comparator
        if (currentComparator == null) {
            currentComparator = new MultiFileComparator();
            currentComparator.setContestAndController(getContest(), getController());
        }
        return currentComparator;
    }

    /**
     * @return
     */
    private String getExecuteDir() {
        Executable tempExecutable;
        if (currentRunFiles == null) {
            if (log!=null) {
                log.warning("MTSOVPane.getExecuteDir(): currentRunFiles is null");
            } else {
                System.err.println("MTSOVPane.getExecuteDir(): currentRunFiles is null");
            }
            return null;
        }
        tempExecutable = new Executable(getContest(), getController(), currentRun, currentRunFiles);
        String exeDir = tempExecutable.getExecuteDirectoryName();
        return exeDir;
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
    private String getFileForTableCell(int row, int col) {
        
        Problem prob = getContest().getProblem(currentRun.getProblemId());
        
        ProblemDataFiles problemDataFiles = getController().getProblemDataFiles(prob);
        
        //declare the value to be returned
        String returnFile = null ;
        
        if (col == COLUMN.TEAM_OUTPUT_VIEW.ordinal() || col == COLUMN.TEAM_OUTPUT_COMPARE.ordinal()) {
            //get team output file corresponding to test case "row"
            if (currentTeamOutputFileNames != null || currentTeamOutputFileNames.length >= row) {
                // get the team output file name, which should be provided by the client as a full path
                if (currentTeamOutputFileNames[row] == null) {
                    returnFile = null;
                } else {
                    returnFile = currentTeamOutputFileNames[row];
                }
            }
        } else if (col == COLUMN.JUDGE_OUTPUT.ordinal()) {
            //get judge's output corresponding to test case "row"
            String[] answerFiles = problemDataFiles.getFullJudgesAnswerFilenames(getContest(), executableDir);
            //make sure we got back some answer files and that there is an answer file for the test case
            if (answerFiles != null && row < answerFiles.length) {
                returnFile = answerFiles[row];       
            } else {
                //there is no answer file for the specified test case (row)
                returnFile = null ;
            }
            
        } else if (col == COLUMN.JUDGE_DATA.ordinal()) {
            //get judge's input data corresponding to test case "row"
            String[] inputDataFiles = problemDataFiles.getFullJudgesDataFilenames(getContest(), executableDir);
            //make sure we got back some data files and that there is a data file for the test case
            if (inputDataFiles != null && row < inputDataFiles.length) {
                returnFile = inputDataFiles[row];       
            } else {
                //there is no data file for the specified test case (row)
                returnFile = null ;
            }

        } else if (col == COLUMN.VALIDATOR_OUTPUT.ordinal()) {
            //get validator output file corresponding to test case "row"
            if (currentValidatorOutputFileNames != null && currentValidatorOutputFileNames.length >= row) {
                // this will either be null or contain the filename
                if (row >= currentValidatorOutputFileNames.length) {
                    returnFile = null;
                } else {
                    returnFile = currentValidatorOutputFileNames[row];
                }
            }
        } else if (col == COLUMN.VALIDATOR_ERR.ordinal()) {
            //get validator output file corresponding to test case "row"
            if (currentValidatorStderrFileNames != null && currentValidatorStderrFileNames.length >= row) {
                // this will either be null or contain the filename
                if (row >= currentValidatorStderrFileNames.length) {
                    returnFile = null;
                } else {
                    returnFile = currentValidatorStderrFileNames[row];
                }
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
    private void showFile(MultipleFileViewer fileViewer, String file, String title, String tabLabel, boolean visible) {
        
//        System.out.println ("MTSVPane.showFile():");
//        String viewerString = "<null>";
//        if (fileViewer != null) {
//            viewerString = fileViewer.getClass().toString();
//        }
//        System.out.println ("  Viewer='" + viewerString + "'" 
//                            + "  File='" + file + "'"
//                            + "  Title='" + title + "'"
//                            + "  setVisible='" + visible + "'");
        
        if (fileViewer == null || file == null) {
            log = getController().getLog();
            log.log(Log.WARNING, "MTSVPane.showFile(): fileViewer or file is null");
            JOptionPane.showMessageDialog(getParentFrame(), 
                    "System Error: null fileViewer or file; contact Contest Administrator (check logs)", 
                    "System Error", JOptionPane.ERROR_MESSAGE);
            return ;
        }
        File myFile = new File(file);
        if (! myFile.isFile()) {
            JOptionPane.showMessageDialog(getParentFrame(), 
                "Error: could not find file: " + file, 
                "File Missing", JOptionPane.ERROR_MESSAGE);
            log = getController().getLog();
            log.warning("MTSVPane.showFile(): could not find file "+file);
            return;
        }
        fileViewer.setTitle(title);
        fileViewer.addFilePane(tabLabel, file);
        fileViewer.enableCompareButton(false);
        fileViewer.setInformationLabelText("File: " + myFile.getName());

        if (visible) {
            fileViewer.setVisible(true);
        }
    }

    public void setData(Run run, RunFiles runFiles, Problem problem, ProblemDataFiles problemDataFiles) {

        this.currentRun = run;
        this.currentRunFiles = runFiles;
        this.currentProblem = problem;
        this.currentProblemDataFiles = problemDataFiles;
        executableDir = getExecuteDir();
        populateGUI();
        try {
            currentProblemDataFiles.checkAndCreateFiles(getContest(), executableDir);
        } catch (FileNotFoundException e) {
            // TODO should this instead show some kind of error on the pane?
            JOptionPane.showMessageDialog(getParentFrame(), 
                    "Error: could not find file: " + e.getMessage(), 
                    "File Missing", JOptionPane.ERROR_MESSAGE);
            if (log!=null) {
                log.warning("MTSOVPane.compareFiles(): could not find file "+e.getMessage());
            } else {
                System.err.println ("MTSOVPane.compareFiles(): could not find file "+e.getMessage());
            }
        }
    }
    
    /**
     * Set new team output filenames.
     */
    public void setTeamOutputFileNames(String [] filenames){
        this.currentTeamOutputFileNames = filenames ;
    }

    public void setValidatorOutputFileNames(String[] filenames) {
        this.currentValidatorOutputFileNames = filenames ;
    }

    public void setValidatorStderrFileNames(String[] filenames) {
        this.currentValidatorStderrFileNames = filenames ;
    }

    private Component getHorizontalGlue_9() {
        if (horizontalGlue_9 == null) {
            horizontalGlue_9 = Box.createHorizontalGlue();
            horizontalGlue_9.setPreferredSize(new Dimension(20, 20));
        }
        return horizontalGlue_9;
    }
} // @jve:decl-index=0:visual-constraint="10,10"
