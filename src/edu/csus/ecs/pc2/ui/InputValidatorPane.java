// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.ExecuteException;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Problem.InputValidationStatus;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.inputValidation.InputValidationResult;
import edu.csus.ecs.pc2.core.model.inputValidation.InputValidationResultsTableModel;
import edu.csus.ecs.pc2.validator.inputValidator.InputValidatorRunner;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

/**
 * This class defines a plugin pane (a JPanel) containing components for (1) defining an Input Validator program name and Invocation Command, and (2) Viewing the results of an input validator
 * execution.
 * 
 * @author pc2@ecs.csus.edu
 *
 */
public class InputValidatorPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;

    private boolean inputValidatorHasBeenRun;
    
    private InputValidationStatus inputValidationStatus = InputValidationStatus.NOT_TESTED;
    
    private JPanePlugin parentPane;
    
    private InputValidationResult[] runResults;
    private InputValidationResult[] accumulatingResults;
    
    protected InputValidationResultFrame resultFrame;
    
    private JButton runInputValidatorButton;
    private JPanel runInputValidatorButtonPane;
    private ButtonGroup validatorChoiceButtonGroup;
    private JPanel noInputValidatorPanel;
    private JRadioButton noInputValidatorRadioButton;
    private JPanel vivaInputValidatorPanel;
    private JRadioButton useVivaInputValidatorRadioButton;
    private JPanel vivaOptionsPanel;
    private JPanel vivaOptionButtonPanel;
    private JLabel lblWhatsThisViva;
    private JLabel vivaPatternLabel;
    private JButton loadVivaPatternButton;
    private JScrollPane vivaPatternTextScrollPane;
    private JTextArea vivaPatternTextArea;
    private JPanel customInputValidatorPanel;
    private JPanel customOptionButtonPanel;
    private JRadioButton useCustomInputValidatorRadioButton;
    private DefineCustomInputValidatorPane customOptionsPanel;




    public InputValidatorPane() {
        setMaximumSize(new Dimension(500, 400));
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setAlignmentY(Component.TOP_ALIGNMENT);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        this.add(getVerticalStrut_1());
        this.add(getNoInputValidatorPanel());
        this.add(getVerticalStrut_2());
        this.add(getVivaInputValidatorPanel());
        this.add(getVerticalStrut_3());
        this.add(getCustomInputValidatorPanel());
        add(getRigidArea());
        this.add(getVerticalStrut_4());
        this.add(getRunInputValidatorButtonPanel());
        this.add(getVerticalStrut_5());
        
        getValidatorChoiceButtonGroup().setSelected(getNoInputValidatorRadioButton().getModel(), true);
        
        resultFrame = new InputValidationResultFrame();
    }
    
    private ButtonGroup getValidatorChoiceButtonGroup() {
        if (validatorChoiceButtonGroup == null) {
            validatorChoiceButtonGroup = new ButtonGroup();
            validatorChoiceButtonGroup.add(getNoInputValidatorRadioButton());
            validatorChoiceButtonGroup.add(getUseVivaInputValidatorRadioButton());
            validatorChoiceButtonGroup.add(getUseCustomInputValidatorRadioButton());
        }
        return validatorChoiceButtonGroup;
    }


    private JPanel getNoInputValidatorPanel() {
        if (noInputValidatorPanel == null) {
            noInputValidatorPanel = new JPanel();
            noInputValidatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            noInputValidatorPanel.setBorder(null);
            noInputValidatorPanel.setMaximumSize(new Dimension(500,100));
            FlowLayout flowLayout = (FlowLayout) noInputValidatorPanel.getLayout();
            flowLayout.setHgap(10);
            flowLayout.setAlignment(FlowLayout.LEFT);            
            noInputValidatorPanel.add(getNoInputValidatorRadioButton());
        }
        return noInputValidatorPanel;
    }
    
    public JRadioButton getNoInputValidatorRadioButton() {
        if (noInputValidatorRadioButton==null) {
            noInputValidatorRadioButton = new JRadioButton("Problem has no Input Validator");
            noInputValidatorRadioButton.setSelected(true);
            noInputValidatorRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (noInputValidatorRadioButton.isSelected()) {
                        getRunInputValidatorButton().setEnabled(false);
                    }
                }
            });
        }
        return noInputValidatorRadioButton ;
    }
    
    private JPanel getVivaInputValidatorPanel() {
        if (vivaInputValidatorPanel == null) {
            vivaInputValidatorPanel = new JPanel();
            vivaInputValidatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            vivaInputValidatorPanel.setLayout(new BorderLayout(0, 0));  //0,0 = hgap,vgap
            vivaInputValidatorPanel.setMaximumSize(new Dimension(500, 200));
            vivaInputValidatorPanel.add(getVivaOptionRadioButtonPanel(), BorderLayout.NORTH);
            vivaInputValidatorPanel.add(getRigidArea_1(), BorderLayout.WEST);
            vivaInputValidatorPanel.add(getVivaOptionsSubPanel());
        }
        return vivaInputValidatorPanel;
        
    }

    private JPanel getVivaOptionRadioButtonPanel() {
        if (vivaOptionButtonPanel == null) {
            vivaOptionButtonPanel = new JPanel();
            vivaOptionButtonPanel.setBorder(null);
            vivaOptionButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            FlowLayout fl_vivaOptionButtonPanel = new FlowLayout(FlowLayout.LEFT);
            fl_vivaOptionButtonPanel.setHgap(10);
            vivaOptionButtonPanel.setLayout(fl_vivaOptionButtonPanel);
            vivaOptionButtonPanel.add(getUseVivaInputValidatorRadioButton());
            vivaOptionButtonPanel.add(getLblWhatsThisViva());
        }
        return vivaOptionButtonPanel;
    }
    
    private JRadioButton getUseVivaInputValidatorRadioButton() {
        if (useVivaInputValidatorRadioButton==null) {
            useVivaInputValidatorRadioButton = new JRadioButton("Use VIVA Input Validator");
            useVivaInputValidatorRadioButton.setSelected(false);
            useVivaInputValidatorRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (useVivaInputValidatorRadioButton.isSelected()) {
                        getRunInputValidatorButton().setEnabled(true);
                    }
                }
            });
        }
        return useVivaInputValidatorRadioButton ;
    }

    private JLabel getLblWhatsThisViva() {
        if (lblWhatsThisViva == null) {
            Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");
            if (questionIcon == null || !(questionIcon instanceof ImageIcon)) {
                // the current PLAF doesn't have an OptionPane.questionIcon that's an ImageIcon
                lblWhatsThisViva = new JLabel("<What's This?>");
                lblWhatsThisViva.setForeground(Color.blue);
            } else {
                Image image = ((ImageIcon) questionIcon).getImage();
                lblWhatsThisViva = new JLabel(new ImageIcon(getScaledImage(image, 20, 20)));
            }

            lblWhatsThisViva.setToolTipText("What's This? (click for additional information)");
            lblWhatsThisViva.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JOptionPane.showMessageDialog(null, whatsThisVivaMessage, "VIVA Input Validator", JOptionPane.INFORMATION_MESSAGE, null);
                }
            });
            lblWhatsThisViva.setBorder(new EmptyBorder(0, 15, 0, 0));
        }
        return lblWhatsThisViva;
    }

    private String whatsThisVivaMessage = "Selecting this option allows you to use the \"VIVA\" Input Validator embedded within PC^2."

            + "\n\nVIVA is \"Vanb's Input Verification Assistant\", implemented by and provided courtesy of David \"vanb\" Van Brackle."

            + "\n\nVIVA supports a complex \"pattern recognition\" language, allowing the Contest Administrator to write a detailed specification"
            + "\nto which input test data files must conform."
            
            + "\n\nThe VIVA pattern language includes operations such as the ability to specify input data type requirements; range constraints "
            + "\nconsisting of expressions including logical, relational, and arithmetic operators; and data repetition patterns both within lines"
            + "\nand across different lines. The language also includes a variety of functions (such as \"length(string)\", \"distance(x1,y1,x2,y2)\","
            + "\n\"unique(x)\", and many others) which can be used to constrain input data."

            + "\n\nTo use VIVA, enter a valid VIVA \"pattern\" (or load one from a file), then click the \"Run Input Validator\" button"
            + "\nto verify that all data files currently loaded on the \"Test Data Files\" tab conform to the specified VIVA pattern."
            + "\n(The results of running VIVA against the Test Data Files will be displayed in the \"Input Validation Results\" pane, below)."

            + "\n\nFor more information on VIVA patterns, see the VIVA User's Guide under the PC^2 \"docs\" folder."
            + "\nFor additional information, or to download a copy of VIVA, see the VIVA website at http://viva.vanb.org/.";

    
    private JPanel getVivaOptionsSubPanel() {
        if (vivaOptionsPanel == null) {
            vivaOptionsPanel = new JPanel();
            vivaOptionsPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Viva Options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
            vivaOptionsPanel.setMinimumSize(new Dimension(20, 200));
            vivaOptionsPanel.setPreferredSize(new Dimension(200, 200));
            vivaOptionsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
            vivaOptionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            FlowLayout flowLayout = (FlowLayout) vivaOptionsPanel.getLayout();
            flowLayout.setAlignment(FlowLayout.LEFT);
            vivaOptionsPanel.add(getRigidArea_2());
            vivaOptionsPanel.add(getVivaPatternLabel());
            vivaOptionsPanel.add(getVivaPatternTextScrollPane());
            vivaOptionsPanel.add(getLoadVivaPatternButton());
        }
        return vivaOptionsPanel;
        
    }
    
    private JLabel getVivaPatternLabel() {
        if (vivaPatternLabel == null) {
            vivaPatternLabel = new JLabel("Pattern:  ");
            vivaPatternLabel.setAlignmentY(Component.TOP_ALIGNMENT);
            vivaPatternLabel.setVerticalTextPosition(SwingConstants.TOP);
            vivaPatternLabel.setVerticalAlignment(SwingConstants.TOP);
        }
        return vivaPatternLabel;
    }
    private JButton getLoadVivaPatternButton() {
        if (loadVivaPatternButton == null) {
            loadVivaPatternButton = new JButton("Load Pattern...");
        }
        return loadVivaPatternButton;
    }
    private JScrollPane getVivaPatternTextScrollPane () {
        if (vivaPatternTextScrollPane==null) {
            vivaPatternTextScrollPane = new JScrollPane(getVivaPatternTextArea());
            vivaPatternTextScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            vivaPatternTextScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        }
        return vivaPatternTextScrollPane;
    }
    
    public JTextArea getVivaPatternTextArea() {
        if (vivaPatternTextArea == null) {
            vivaPatternTextArea = new JTextArea(50,80);
//            textArea.setPreferredSize(new Dimension(400, 100));
        }
        return vivaPatternTextArea;
    }
    
    private JPanel getCustomInputValidatorPanel() {
        if (customInputValidatorPanel == null) {
            customInputValidatorPanel = new JPanel();
            customInputValidatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            customInputValidatorPanel.setLayout(new BorderLayout(0, 0));  //0,0 = hgap,vgap
            customInputValidatorPanel.setMaximumSize(new Dimension(500, 200));    
            
            customInputValidatorPanel.add(getCustomOptionRadioButtonPanel(),BorderLayout.NORTH);
            customInputValidatorPanel.add(getRigidArea_3(),BorderLayout.WEST);
            customInputValidatorPanel.add(getCustomOptionsSubPanel());
        }
        return customInputValidatorPanel;

    }

    private JPanel getCustomOptionRadioButtonPanel() {
        if (customOptionButtonPanel == null) {
            customOptionButtonPanel = new JPanel();
            customOptionButtonPanel.setBorder(null);
            customOptionButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            FlowLayout fl_customOptionButtonPanel = new FlowLayout(FlowLayout.LEFT);
            fl_customOptionButtonPanel.setHgap(10);
            customOptionButtonPanel.setLayout(fl_customOptionButtonPanel);
            customOptionButtonPanel.add(getUseCustomInputValidatorRadioButton());
        }
        return customOptionButtonPanel;
    }
    
    private JRadioButton getUseCustomInputValidatorRadioButton() {
        if (useCustomInputValidatorRadioButton==null) {
            useCustomInputValidatorRadioButton = new JRadioButton("Use Custom (user-supplied) Input Validator");
            useCustomInputValidatorRadioButton.setSelected(false);
            useCustomInputValidatorRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (useCustomInputValidatorRadioButton.isSelected()) {
                        getRunInputValidatorButton().setEnabled(true);
                    }
                }
            });
        }
        return useCustomInputValidatorRadioButton ;
    }

    protected DefineCustomInputValidatorPane getCustomOptionsSubPanel() {
        if (customOptionsPanel == null) {
            customOptionsPanel = new DefineCustomInputValidatorPane();
            customOptionsPanel.setBorder(new TitledBorder(null, "Custom Validator Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            getCustomOptionsSubPanel().setParentPane(this);
            customOptionsPanel.setMinimumSize(new Dimension(20, 200));
        }
        return customOptionsPanel;
    }
    
    
    /**
     * Returns the Custom Input Validator Command currently entered into the CustomInputValidatorPane.
     * 
     * @return a String containing the command to be used to invoke a Custom Input Validator
     */
    public String getCustomInputValidatorCommand() {
        String command = getCustomOptionsSubPanel().getInputValidatorCommand();
        return command;
    }


    @Override
    public String getPluginTitle() {

        return "Input Validator Pane";
    }

    public void setParentPane(JPanePlugin parentPane) {
        this.parentPane = parentPane;
    }

    public JPanePlugin getParentPane() {
        return this.parentPane;
    }

    /**
     * Sets the ToolTip text for the Custom Input Validator Program Name displayed in this InputValidatorPane.
     * 
     * @param text
     *            the ToolTip text to set
     */
    public void setInputValidatorProgramNameToolTipText(String text) {
        getCustomOptionsSubPanel().setInputValidatorProgramNameToolTipText(text);

    }

    /**
     * Sets the Custom Input Validator Command displayed in this InputValidatorPane to the specified value.
     * 
     * @param command
     *            a String containing the command used to invoke the Custom Input Validator
     */
    public void setInputValidatorCommand(String command) {
        getCustomOptionsSubPanel().setInputValidatorCommand(command);

    }

    /**
     * Sets the ToolTip text for the Custom Input Validator Command displayed in this InputValidatorPane.
     * 
     * @param text
     *            the ToolTip text to set
     */
    public void setInputValidatorCommandToolTipText(String text) {
        getCustomOptionsSubPanel().setInputValidatorCommandToolTipText(text);

    }

    private JPanel getRunInputValidatorButtonPanel() {
        if (runInputValidatorButtonPane==null) {
            runInputValidatorButtonPane = new JPanel();
            runInputValidatorButtonPane.setAlignmentX(LEFT_ALIGNMENT);
            runInputValidatorButtonPane.add(getRunInputValidatorButton());
            runInputValidatorButtonPane.add(getGlue());
            runInputValidatorButtonPane.add(getShowLastResultButton());
        }
        return runInputValidatorButtonPane;
    }
    
    private JButton getRunInputValidatorButton() {
        if (runInputValidatorButton == null) {
            runInputValidatorButton = new JButton("Run Input Validator");
            runInputValidatorButton.setEnabled(false);
            runInputValidatorButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    runCustomInputValidator();
                }
            });
        }
        return runInputValidatorButton;
    }
    


    protected void runCustomInputValidator() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (okToRunInputValidator()) {

                    getRunInputValidatorButton().setEnabled(false); // this is set back true when the spawner finishes, via a call to cleanup()
                    getShowOnlyFailedFilesCheckbox().setEnabled(false);
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    
                    spawnCustomInputValidatorRunnerThread();

                } else {
                    // determine why it's not ok to run the Input Validator and display an appropriate message
                    boolean msgShown = false;
                    if (!problemHasInputDataFiles()) {
                        JOptionPane.showMessageDialog(null, "No Judge's Data Files defined; cannot run Input Validator", "Missing Data Files", JOptionPane.INFORMATION_MESSAGE);
                        msgShown = true;
                    } else if (getUseVivaInputValidatorRadioButton().isSelected()) {
                        String vivaPatternText = getVivaPatternTextArea().getText() ;
                        if (vivaPatternText==null || vivaPatternText.equals("")) {
                            JOptionPane.showMessageDialog(null, "Missing VIVA pattern text; cannot run VIVA Input Validator", "Missing Pattern", JOptionPane.INFORMATION_MESSAGE);
                            msgShown = true;
                        }
                    } else if (getUseCustomInputValidatorRadioButton().isSelected() && !problemHasCustomInputValidatorCommand()) {
                        JOptionPane.showMessageDialog(null, "Missing Custom Input Validator Command; cannot run Input Validator", "Missing Command", JOptionPane.INFORMATION_MESSAGE);
                        msgShown = true;
                    }
                    if (!msgShown) {
                        JOptionPane.showMessageDialog(null, "Cannot run Input Validator (unknown reason)", "Cannot run Input Validator", JOptionPane.ERROR_MESSAGE);                        
                    }

                }
            }
        });
    }

    /**
     * Verifies that all conditions necessary to run the Input Validator associated with this InputValidatorPane are true. 
     * If the "VIVA" Input Validator has been selected, the conditions include that there is a VIVA pattern in the text box
     * and that there are input data files on which VIVA can operate.
     * If "Custom" InputValidator has been selected, the conditions include that there is a Custom Input Validator Command line and that there
     * are Input Data Files on which the command can operate. 
     * 
     * @return true if it is ok to run the currently selected Input Validator; false if not
     */
    protected boolean okToRunInputValidator() {
        boolean ok = false;
        if (getUseVivaInputValidatorRadioButton().isSelected()) {
            //make sure there is some text in the VIVA pattern area
            String patternText = getVivaPatternTextArea().getText();
            ok = patternText!=null && !patternText.equals("") && problemHasInputDataFiles();
            
        } else if (getUseCustomInputValidatorRadioButton().isSelected()) {
            ok = problemHasCustomInputValidatorCommand() && problemHasInputDataFiles();
        }
        return ok ;
    }

    /**
     * Verifies that this InputValidatorPane has a command for running a Custom Input Validator.
     * 
     * @return true if the Custom Input Validator command is a non-null, non-empty String; false otherwise
     */
    private boolean problemHasCustomInputValidatorCommand() {
        String customInputValidatorCommand = getCustomOptionsSubPanel().getInputValidatorCommand();
        return (customInputValidatorCommand != null && !customInputValidatorCommand.equals(""));
    }

    /**
     * Verifies that there are judge's data files against which we can run an Input Validator.
     * 
     * @return true if there is one or more Judge's Data Files stored in the Data Files pane
     */
    private boolean problemHasInputDataFiles() {
        JPanePlugin parent = getParentPane();
        if (parent != null && parent instanceof EditProblemPane) {
            EditProblemPane epp = (EditProblemPane) parent;
            // check for data files on the Input Data Files tab
            if (epp.getMultipleDataSetPane().getProblemDataFiles() != null && epp.getMultipleDataSetPane().getProblemDataFiles().getJudgesDataFiles() != null
                    && epp.getMultipleDataSetPane().getProblemDataFiles().getJudgesDataFiles().length > 0) {
                return true;
            } else {
                // there's no data files on the Input Data Files tab; check to see if they've entered a single data file on the General tab
                if (epp.inputDataFileLabel != null && epp.inputDataFileLabel.getText() != null && !epp.inputDataFileLabel.getText().equals("")) {
                    return true;
                }
            }
        }
        // no data files were found anywhere (or the parent isn't an EditProblemPane)
        return false;
    }

    /**
     * Spawns a separate {@link SwingWorker} thread to run the Input Validator.
     * 
     * The worker thread publishes each separate InputValidationResult as it is generated; each published result is automatically 
     * picked up and handled by the worker's process() method. Once the worker thread completes it assigns the results to a global 
     * variable which is accessible by external clients via an accessor.
     * 
     * See https://docs.oracle.com/javase/tutorial/uiswing/concurrency/interim.html for details on how SwingWorker threads publish results.
     */
    private void spawnCustomInputValidatorRunnerThread() {

        // define a SwingWorker thread to run the Input Validator in the background against each of the data files
        SwingWorker<InputValidationResult[], InputValidationResult> worker = new SwingWorker<InputValidationResult[], InputValidationResult>() {

            /**
             * Method doInBackground() is invoked when the Worker thread's execute() method is called (which happens below, after the worker
             * thread has been constructed). The method runs the Input Validator in the background against all the judge's input data files
             * currently defined on the EditProblemPane's Test Data Files pane, publishing each result as it finishes. When the
             * method is finished it returns an array of all InputValidationResults; this array is accessible by the Worker thread's done()
             * method (via a call to get()).
             */
            @Override
            public InputValidationResult[] doInBackground() throws Exception {

                // determine what Type will receive the results
                JPanePlugin parent = getParentPane();

                if (parent instanceof EditProblemPane) {

                    // we need the parent EditProblemPane to fetch the test data files
                    EditProblemPane epp = (EditProblemPane) parent;

                    // get the data files from the EditProblemPane's Input Data Files tab
                    SerializedFile[] dataFiles = epp.getMultipleDataSetPane().getProblemDataFiles().getJudgesDataFiles();

                    // make sure we got some data files
                    boolean found = false;
                    if (dataFiles != null && dataFiles.length > 0) {
                        found = true;
                    } else {
                        
                        // no files found on the Test Data Files tab; see if perhaps there is a single data file name defined on the General tab
                        if (epp.inputDataFileLabel != null && !epp.inputDataFileLabel.getText().equals("")) {

                            // there is a file name on the General tab; try getting that file (whose full name is stored in the ToolTip)
                            String fileName = epp.inputDataFileLabel.getToolTipText();
                            if (fileName != null && !fileName.equals("")) {

                                try {
                                    SerializedFile sf = new SerializedFile(fileName);

                                    // check for serialization error (which will throw any exception found in the SerializedFile)
                                    if (Utilities.serializedFileError(sf)) {
                                        getController().getLog().warning("Error obtaining SerializedFile for data file ' " + fileName + " ' -- cannot run Input Validator");
                                        System.err.println("Error obtaining SerializedFile for data file ' " + fileName + " ' (from General tab)");
                                        return null;
                                    } else {
                                        // we got a valid SerializedFile from the General tab; use that
                                        dataFiles = new SerializedFile[1];
                                        dataFiles[1] = sf;
                                        found = true;
                                    }
                                } catch (Exception e) {
                                    getController().getLog().warning("Exception obtaining SerializedFile for data file ' " + fileName + " ' : " + e.getMessage());
                                    System.err.println("Exception obtaining SerializedFile for data file ' " + fileName + " ' : " + e.getMessage());
                                    return null;
                                }
                            }

                        }
                    }

                    if (!found) {
                        // we found no data files on either tab
                        getController().getLog().warning("No data files found -- cannot run Input Validator");
                        System.err.println("Warning: No data files found -- cannot run Input Validator");
                        return null;
                    }

                    // if we get here we know we have data files to process...

                    // create an array to hold the results as they are created
                    InputValidationResult[] validationResults = new InputValidationResult[dataFiles.length];

                    // get the Custom Input Validator Program to be run
                    SerializedFile validatorProg = epp.getInputValidatorPane().getInputValidatorFile();
                    if (validatorProg == null) {
                        System.err.println("No input validator (SerializedFile) defined ");
                        getController().getLog().warning("No input validator (SerializedFile) defined ");
                        throw new Exception("No input validator (SerializedFile) defined ");
                    }

                    // get the problem for which the data files apply
                    Problem prob = epp.getProblem();

                    // get the execution directory being used by the EditProblemPane
                    String executeDir = epp.getExecuteDirectoryName();

                    // clear the results table in preparation for adding new results
                    ((InputValidationResultsTableModel) resultFrame.getInputValidationResultPane().getInputValidationResultsTable().getModel()).setResults(null);
                    ((AbstractTableModel) resultFrame.getInputValidationResultPane().getInputValidationResultsTable().getModel()).fireTableDataChanged();

                    // clear the result accumulator
                    accumulatingResults = null;

                    // run the Input Validator on each data file
                    for (int fileNum = 0; fileNum < dataFiles.length; fileNum++) {

                        // get the next data file
                        SerializedFile dataFile = dataFiles[fileNum];

                        // should figure out how to run each of these calls on a separate thread and still return intermediate results...
                        // this might need to be done here, or else in method runInputValidator()...
                        // One thing to worry about: each call to runInputValidator() uses the (same?) execute directory; that can cause collisions
                        // with the files which are being created therein (e.g. the stdout.pc2 and stderr.pc2 files)
                        try {
                            // run the input validator
                            validationResults[fileNum] = runInputValidator(fileNum + 1, prob, validatorProg, getCustomInputValidatorCommand(), dataFile, executeDir);

                            inputValidatorHasBeenRun = true;
                            epp.enableUpdateButton();

                            // publish the validator result for the current data file, to be picked up by the process() method (below)
                            publish(validationResults[fileNum]);

                        } catch (Exception e) {
                            getController().getLog().warning("Exception running Input Validator ' " + validatorProg.getName() + " ' : " + e.getMessage());
                        }
                    }

                    return validationResults;

                } else {
                    // the parent is not an EditProblemPane; the current code doesn't support returning results to any other Type
                    getController().getLog().warning("Attempted to return Input Validator results to a " + parent.getClass() 
                                                    + "; currently only EditProblemPane is supported for receiving results");
                    System.err.println("Warning: attempt to assign Input Validator results to an unsupported type: " + parent.getClass());
                    return null;
                }

            }

            /**
             * This method is called by the Worker thread's publish() method each time one or more results are finished. The input to the method is a list of results which have been completed since
             * the last call to this method. This method adds each published result to the results table, by calling addToResultTable().
             */
            @Override
            public void process(List<InputValidationResult> resultList) {
                // display the results (which may be partial) in the InputValidatorPane's InputValidationResults table

                for (InputValidationResult result : resultList) {
                    if (!resultFrame.getInputValidationResultPane().getShowOnlyFailedFilesCheckbox().isSelected() || !result.isPassed()) {
                        addResultToTable(result);
                    }
                    addResultToAccumulatedList(result);
                    updateInputValidationSummaryText(accumulatingResults);
                    // addResultToProblem(result); //we don't want to do this until Add/Update is pressed
                    // updateProblemValidationStatus(result); // ditto ""
                }
            }

            /**
             * This method is invoked by the Worker thread when it is completely finished with its doInBackground task(s). 
             * Calling get() fetches the set of data returned by the Worker thread's doInBackground() method -- that is, 
             * an array of InputValidationResults. This method saves those results so they can be accessed by external code 
             * via the {@link #getRunResults()} method.
             * The method also updates the GUI based on the results, and finally calls cleanup() to restore the GUI state.
             */
            @Override
            public void done() {
                try {
                    runResults = get();
                    if (runResults != null && runResults.length > 0) {
                        updateInputValidationSummaryText(runResults);
                        updateInputValidationStatus(runResults);
                    }
                } catch (InterruptedException e) {
                    getController().getLog().warning("Exception in SwingWorker.done(): " + e.getMessage());
                } catch (java.util.concurrent.ExecutionException e) {
                    String why = null;
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        why = cause.getMessage();
                    } else {
                        why = e.getMessage();
                    }
                    System.err.println("Error retrieving validation results: " + why);
                    getController().getLog().warning("Exception in SwingWorker.done(): " + e.getMessage());
                } finally {
                    cleanup();
                }
            }

        }; // end of SwingWorker definition

        // start the SwingWorker thread running the Custom Input Validator in the background against all data files
        worker.execute();
        
        //display the ResultFrame (to which the SwingWorker sends its results as they are generated)
        resultFrame.setVisible(true);
        
    } // end method spawnCustomInputValidatorRunnerThread()

    /**
     * This method is called when spawnCustomInputValidatorRunnerThread() finishes. Its job is to restore the GUI state, 
     * including resetting the cursor and reenabling the RunInputValidator button.
     */
    private void cleanup() {
        getRunInputValidatorButton().setEnabled(true);
        getShowOnlyFailedFilesCheckbox().setEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Adds the specified Input Validation execution result to the Input Validation Results table in the InputValidationResultFrame.
     * 
     * @param result
     *            the result to be added to the table
     */
    private void addResultToTable(InputValidationResult result) {

        ((InputValidationResultsTableModel) resultFrame.getInputValidationResultPane().getInputValidationResultsTable().getModel()).addRow(result);
        ((InputValidationResultsTableModel) resultFrame.getInputValidationResultPane().getInputValidationResultsTable().getModel()).fireTableDataChanged();

    }

    private void addResultToAccumulatedList(InputValidationResult newResult) {
        if (accumulatingResults == null || accumulatingResults.length <= 0) {
            accumulatingResults = new InputValidationResult[1];
            accumulatingResults[0] = newResult;
        } else {
            InputValidationResult[] temp = new InputValidationResult[accumulatingResults.length + 1];
            int i = 0;
            for (InputValidationResult res : accumulatingResults) {
                temp[i++] = res;
            }
            temp[i] = newResult;
            accumulatingResults = temp;
        }
    }

    /**
     * Updates the Input Validation Results summary message in the InputValidationResultFrame to match the results contained 
     * in the received array of {@link InputValidationResult}s.
     */
    private void updateInputValidationSummaryText(InputValidationResult[] runResultsArray) {
        resultFrame.getInputValidationResultPane().updateInputValidationStatusMessage(runResultsArray);
    }

    /**
     * Updates the Input Validator Run Status flag in this InputValidatorPane. Note that this does NOT update the Problem; 
     * just the local status flag. The Problem only gets updated when Add or Update
     * is pressed on the EditProblemPane.
     * 
     * If the received InputValidationResult array is null or empty, no change is made in the current status. 
     * Otherwise, if all the results in the array are "Passed" then the status is set to Passed;
     * if one or more results in the array are "Failed" then the status is set to Failed.
     * 
     * @param runResultsArray
     *            an array of InputValidationResults from having run an Input Validator
     */
    private void updateInputValidationStatus(InputValidationResult[] runResultsArray) {

        if (runResultsArray != null && runResultsArray.length > 0) {

            boolean foundFailure = false;
            for (InputValidationResult res : runResultsArray) {
                if (!res.isPassed()) {
                    foundFailure = true;
                    break;
                }
            }

            if (foundFailure) {
                setInputValidationStatus(InputValidationStatus.FAILED);
            } else {
                setInputValidationStatus(InputValidationStatus.PASSED);
            }
        }
    }

    protected void setInputValidationStatus(InputValidationStatus newStatus) {
        this.inputValidationStatus = newStatus;
    }

    /**
     * Returns the {@link InputValidationStatus} for this {@link InputValidatorPane}. 
     * The returned value indicates whether an Input Validator has been run, and if so whether all runs passed or not.
     * 
     * @return the Input Validation Status for this pane
     */
    protected InputValidationStatus getInputValidationStatus() {
        return this.inputValidationStatus;
    }

    /**
     * Runs the specified Custom Input Validator Program using the specified parameters.
     * 
     * NOTE: TODO: this should be done on a separate thread. However, see the note in spawnCustomInputValidatorRunnerThread() 
     * about collisions in folders...
     * 
     * @param seqNum a sequence number for associating with generated file names
     * @param problem the Contest Problem associated with the Input Validator
     * @param validatorProg the Custom Input Validator Program to be run
     * @param validatorCommand the command used to run the Custom Input Validator Program
     * @param dataFile the data file to be passed to the Custom Input Validator as input to be validated
     * @param executeDir the execution directory to be used (i.e. the folder in which to run the Input Validator Program)
     * 
     * @return an InputValidationResult
     * 
     * @throws ExecutionException if an ExecutionException occurs during execution of the Input Validator
     * @throws Exception if an Exception other than ExecutionException occurs during execution of the Input Validator
     */
    private InputValidationResult runInputValidator(int seqNum, Problem problem, SerializedFile validatorProg, String validatorCommand, SerializedFile dataFile, String executeDir) throws Exception {

        InputValidatorRunner runner = new InputValidatorRunner(getContest(), getController());
        InputValidationResult result = null;
        try {
            result = runner.runInputValidator(seqNum, problem, validatorProg, validatorCommand, executeDir, dataFile);
        } catch (ExecuteException e) {
            getController().getLog().warning("Exeception executing Input Validator: " + e.getMessage());
            throw e;
        } catch (Exception e1) {
            getController().getLog().warning("Exeception executing Input Validator: " + e1.getMessage());
            throw e1;
        }

        return result;
    }


    /**
     * @return the inputValidatorHasBeenRun
     */
    public boolean isInputValidatorHasBeenRun() {
        return inputValidatorHasBeenRun;
    }

    /**
     * @param inputValidatorHasBeenRun
     *            the inputValidatorHasBeenRun to set
     */
    public void setInputValidatorHasBeenRun(boolean inputValidatorHasBeenRun) {
        this.inputValidatorHasBeenRun = inputValidatorHasBeenRun;
    }

    public InputValidationResult[] getRunResults() {
        return this.runResults;
    }

    public void setRunResults(InputValidationResult[] inRunResults) {
        this.runResults = inRunResults;
    }

    public void setInputValidationSummaryMessageText(String msg) {
        resultFrame.getInputValidationResultPane().setInputValidationSummaryMessageText(msg);
    }

    public void setInputValidationSummaryMessageColor(Color color) {
        resultFrame.getInputValidationResultPane().setInputValidationSummaryMessageColor(color);
    }

    public void updateInputValidationStatusMessage(InputValidationResult[] results) {
        resultFrame.getInputValidationResultPane().updateInputValidationStatusMessage(results);
    }

    public InputValidationResultsTableModel getResultsTableModel() {
        return (InputValidationResultsTableModel) resultFrame.getInputValidationResultPane().getInputValidationResultsTable().getModel();
    }

    public JCheckBox getShowOnlyFailedFilesCheckbox() {
        return resultFrame.getInputValidationResultPane().getShowOnlyFailedFilesCheckbox();
    }

    public void updateResultsTable() {
        resultFrame.getInputValidationResultPane().updateResultsTable(runResults);
    }

    public SerializedFile getInputValidatorFile() {
        return getCustomOptionsSubPanel().getInputValidatorFile();
    }
    
    public void setInputValidatorFile(SerializedFile inputValidatorFile) {
        getCustomOptionsSubPanel().setInputValidatorFile(inputValidatorFile);
    }
  
    private Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    private Component rigidArea_1;
    private Component rigidArea_2;
    private Component rigidArea_3;
    private Component verticalStrut_1;
    private Component verticalStrut_2;
    private Component verticalStrut_3;
    private Component verticalStrut_4;
    private Component verticalStrut_5;
    private JButton showLastResultButton;
    private Component rigidArea;
    private Component glue;

    
    private Component getRigidArea_1() {
        if (rigidArea_1 == null) {
            rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
            rigidArea_1.setPreferredSize(new Dimension(35, 20));
        }
        return rigidArea_1;
    }
    
    private Component getRigidArea_2() {
        if (rigidArea_2 == null) {
            rigidArea_2 = Box.createRigidArea(new Dimension(20, 20));
        }
        return rigidArea_2;
    }
    
    private Component getRigidArea_3() {
        if (rigidArea_3 == null) {
            rigidArea_3 = Box.createRigidArea(new Dimension(20, 20));
            rigidArea_3.setPreferredSize(new Dimension(35, 20));
        }
        return rigidArea_3;
    }

    private Component getVerticalStrut_2() {
        if (verticalStrut_2 == null) {
            verticalStrut_2 = Box.createVerticalStrut(20);
        }
        return verticalStrut_2;
    }
    
    private Component getVerticalStrut_1() {
        if (verticalStrut_1 == null) {
            verticalStrut_1 = Box.createVerticalStrut(20);
        }
        return verticalStrut_1;
    }

    private Component getVerticalStrut_3() {
        if (verticalStrut_3==null) {
            verticalStrut_3 = Box.createVerticalStrut(20);
        }
        return verticalStrut_3;
    }
    
    private Component getVerticalStrut_4() {
        if (verticalStrut_4==null) {
            verticalStrut_4 = Box.createVerticalStrut(20);
        }
        return verticalStrut_4;
    }

    private Component getVerticalStrut_5() {
        if (verticalStrut_5==null) {
            verticalStrut_5 = Box.createVerticalStrut(20);
        }
        return verticalStrut_5;
    }

    //main() method for testing only
    public static void main (String [] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new InputValidatorPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private JButton getShowLastResultButton() {
        if (showLastResultButton == null) {
        	showLastResultButton = new JButton("Show Most Recent Result");
        	showLastResultButton.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) {
        	        resultFrame.setVisible(true);
        	    }
        	});
        }
        return showLastResultButton;
    }
    private Component getRigidArea() {
        if (rigidArea == null) {
        	rigidArea = Box.createRigidArea(new Dimension(20, 20));
        }
        return rigidArea;
    }
    private Component getGlue() {
        if (glue == null) {
        	glue = Box.createGlue();
        	glue.setPreferredSize(new Dimension(30, 0));
        }
        return glue;
    }
}
