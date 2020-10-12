// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.ExecuteException;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Problem.INPUT_VALIDATOR_TYPE;
import edu.csus.ecs.pc2.core.model.Problem.InputValidationStatus;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.model.inputValidation.InputValidationResult;
import edu.csus.ecs.pc2.core.model.inputValidation.InputValidationResultsTableModel;
import edu.csus.ecs.pc2.validator.inputValidator.CustomInputValidatorRunner;
import edu.csus.ecs.pc2.validator.inputValidator.VivaAdapter;
import edu.csus.ecs.pc2.validator.inputValidator.VivaDataFileTestResult;

/**
 * This class defines a plugin pane (a JPanel) containing components for defining Input Validator(s) to be used
 * for verifying that the judge's input data files associated with a contest problem are 'valid' -- meaning, that
 * they satisfy constraints as defined by the specified Input Validator(s).
 * 
 * The class defines a GUI allowing selection of either (or both) of two Input Validators:  the "VIVA" Input Validator
 * and/or an arbitrary (user-defined) "Custom Input Validator".  The GUI also provides buttons for executing the configured
 * Input Validator(s) against the judge's data files currently configured in the problem and for viewing the most recently
 * executed Input Validator results.
 *  
 * @author pc2@ecs.csus.edu
 *
 */
public class InputValidatorPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;

    private INPUT_VALIDATOR_TYPE currentlySelectedInputValidatorType = INPUT_VALIDATOR_TYPE.NONE;
    
    private INPUT_VALIDATOR_TYPE mostRecentlyRunInputValidatorType = INPUT_VALIDATOR_TYPE.NONE;
    
    //Custom Input Validator status fields
    private boolean customInputValidatorHasBeenRun = false;
    private InputValidationStatus customInputValidationStatus = InputValidationStatus.NOT_TESTED;
    private InputValidationResult[] customInputValidatorResults = null;
    private InputValidationResult[] accumulatingCustomResults;
    
    //VIVA Input Validator status fields
    private boolean vivaInputValidatorHasBeenRun = false;
    private InputValidationStatus vivaInputValidationStatus = InputValidationStatus.NOT_TESTED;
    private InputValidationResult[] vivaInputValidatorResults = null;
    private VivaAdapter vivaAdapter = null;

    private JPanePlugin parentPane;
    
    protected InputValidationResultFrame resultFrame;
    
    private JPanel showInputValidatorResultButtonPane;
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
    private JPanel customOptionsPanel;
    private JButton runVivaButton;
    private JPanel vivaOptionsButtonPanel;
    private JPanel customInputValidatorOptionsButtonPanel;
    private JButton chooseCustomInputValidatorProgramButton;
    private JButton runCustomInputValidatorButton;
    private JButton removeCustomInputValidatorButton;
    private DefineCustomInputValidatorPane customInputValidatorProgramPanel;

    public InputValidatorPane() {
        setPreferredSize(new Dimension(800, 600));
        setMaximumSize(new Dimension(800, 400));
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setAlignmentY(Component.TOP_ALIGNMENT);
//        this.setBorder (new LineBorder(Color.RED,1));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        this.add(getVerticalStrut_1());
        this.add(getNoInputValidatorPanel());
        this.add(getVerticalStrut_2());
        this.add(getVivaInputValidatorPanel());
        this.add(getVerticalStrut_3());
        this.add(getCustomInputValidatorPanel());
        this.add(getRigidArea());
        this.add(getVerticalStrut_4());
        this.add(getShowInputValidatorResultButtonPanel());
        this.add(getVerticalStrut_5());
        
        getValidatorChoiceButtonGroup().setSelected(getUseNoInputValidatorRadioButton().getModel(), true);
        
        resultFrame = new InputValidationResultFrame();
        resultFrame.setParentPane(this);
    }
    
    private ButtonGroup getValidatorChoiceButtonGroup() {
        if (validatorChoiceButtonGroup == null) {
            validatorChoiceButtonGroup = new ButtonGroup();
            validatorChoiceButtonGroup.add(getUseNoInputValidatorRadioButton());
            validatorChoiceButtonGroup.add(getUseVivaInputValidatorRadioButton());
            validatorChoiceButtonGroup.add(getUseCustomInputValidatorRadioButton());
        }
        return validatorChoiceButtonGroup;
    }


    private JPanel getNoInputValidatorPanel() {
        if (noInputValidatorPanel == null) {
            noInputValidatorPanel = new JPanel();
            noInputValidatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
//            noInputValidatorPanel.setBorder(new LineBorder(Color.magenta,1));
            noInputValidatorPanel.setMaximumSize(new Dimension(500,100));
            FlowLayout flowLayout = (FlowLayout) noInputValidatorPanel.getLayout();
            flowLayout.setHgap(10);
            flowLayout.setAlignment(FlowLayout.LEFT);            
            noInputValidatorPanel.add(getUseNoInputValidatorRadioButton());
        }
        return noInputValidatorPanel;
    }
    
    protected JRadioButton getUseNoInputValidatorRadioButton() {
        if (noInputValidatorRadioButton==null) {
            noInputValidatorRadioButton = new JRadioButton("Problem has no Input Validator");
            noInputValidatorRadioButton.setSelected(true);
            noInputValidatorRadioButton.addActionListener( new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    setCurrentInputValidatorType(INPUT_VALIDATOR_TYPE.NONE);
                    updateInputValidatorPaneComponents();
                    enableUpdateButton();
                }
            });
        }
        return noInputValidatorRadioButton ;
    }
    
    private JPanel getVivaInputValidatorPanel() {
        if (vivaInputValidatorPanel == null) {
            vivaInputValidatorPanel = new JPanel();
            vivaInputValidatorPanel.setMaximumSize(new Dimension(700, 600));
            vivaInputValidatorPanel.setMinimumSize(new Dimension(400, 200));
            vivaInputValidatorPanel.setPreferredSize(new Dimension(500, 500));
            vivaInputValidatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
//            vivaInputValidatorPanel.setBorder(new LineBorder(Color.green,1));
            
            vivaInputValidatorPanel.setLayout(new BorderLayout(0, 0));
            vivaInputValidatorPanel.add(getVivaOptionRadioButtonPanel(), BorderLayout.NORTH);
            vivaInputValidatorPanel.add(getRigidArea_1(), BorderLayout.WEST);
            vivaInputValidatorPanel.add(getVivaOptionsSubPanel(), BorderLayout.CENTER);
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
    
    protected JRadioButton getUseVivaInputValidatorRadioButton() {
        if (useVivaInputValidatorRadioButton==null) {
            useVivaInputValidatorRadioButton = new JRadioButton("Use VIVA Input Validator");
            useVivaInputValidatorRadioButton.setSelected(false);
            
            useVivaInputValidatorRadioButton.addActionListener( new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    setCurrentInputValidatorType(INPUT_VALIDATOR_TYPE.VIVA);
                    updateInputValidatorPaneComponents();
                    enableUpdateButton();
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

            + "\n\nTo use VIVA, enter a valid VIVA \"pattern\" (or load one from a file), then click the \"Run VIVA\" button"
            + "\nto verify that all data files currently loaded on the \"Test Data Files\" tab conform to the specified VIVA pattern."
            + "\n(The results of running VIVA against the Test Data Files will be displayed in the \"Input Validation Results\" pane)."

            + "\n\nFor more information on VIVA patterns, see the VIVA User's Guide under the PC^2 \"docs\" folder."
            + "\nFor additional information, or to download a copy of VIVA, see the VIVA website at http://viva.vanb.org/.";

    private JPanel getVivaOptionsSubPanel() {
        if (vivaOptionsPanel == null) {
            vivaOptionsPanel = new JPanel();
            vivaOptionsPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Viva Options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(255, 0, 0)));
            vivaOptionsPanel.setMinimumSize(new Dimension(20, 200));
            vivaOptionsPanel.setPreferredSize(new Dimension(200, 200));
            vivaOptionsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
            vivaOptionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            FlowLayout flowLayout = (FlowLayout) vivaOptionsPanel.getLayout();
            flowLayout.setAlignment(FlowLayout.LEFT);
            vivaOptionsPanel.add(getRigidArea_2());
            vivaOptionsPanel.add(getVivaPatternLabel());
            vivaOptionsPanel.add(getVivaPatternTextScrollPane());
            vivaOptionsPanel.add(getRigidArea_4());
            vivaOptionsPanel.add(getVivaOptionsButtonPanel());
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
    
    private JPanel getVivaOptionsButtonPanel() {
        if (vivaOptionsButtonPanel==null) {
            vivaOptionsButtonPanel = new JPanel();
            vivaOptionsButtonPanel.setLayout(new BoxLayout(vivaOptionsButtonPanel,BoxLayout.Y_AXIS));
            vivaOptionsButtonPanel.add(getLoadVivaPatternButton());
            vivaOptionsButtonPanel.add(getRigidArea_5());
            vivaOptionsButtonPanel.add(getRunVivaButton());
    }
        return vivaOptionsButtonPanel;
    }
    
    private JButton getLoadVivaPatternButton() {
        if (loadVivaPatternButton == null) {
            loadVivaPatternButton = new JButton("Load Pattern...");
            loadVivaPatternButton.addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    
                    if (selectFile(getLoadVivaPatternButton(), "Choose VIVA pattern file")) {
                        //if we get here the user selected a file and the Load pattern button tooltip was updated
                        // with the full path to the file
                        String selectedFile = getLoadVivaPatternButton().getToolTipText();
                        String [] lines = null;
                        try {
                            //get the lines from the file
                            lines = Utilities.loadFile(selectedFile);
                            //clear text area
                            getVivaPatternTextArea().setText(null);
                            //add each line of new text to the text area
                            for (String line : lines) {
                                getVivaPatternTextArea().append(line + System.lineSeparator());
                            }
                            
                            //refresh enabled condition of Input Validator GUI components
                            updateInputValidatorPaneComponents() ;

                            //Viva pattern has changed; mark any previous Viva execution invalid
                            setVivaInputValidatorHasBeenRun(false);

                            //pattern updated; enable Edit Problem "Update" button
                            ((EditProblemPane)getParentPane()).enableUpdateButton();
                            
                        } catch (IOException e1) {
                            getController().getLog().warning("IOException reading file '" + selectedFile + "': " + e1);
                            JOptionPane.showConfirmDialog(null, "IOException reading file '"+selectedFile+"': "+e1, "Error Reading File", JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE);
                        } catch (ClassCastException e2) {
                            getController().getLog().severe("Parent of InputValidatorPane is not an EditProblemPane; not supported: " + e2); 
                            JOptionPane.showMessageDialog(null, "Internal error; see logs and please report this to the PC2 Development team (pc2@ecs.csus.edu)",
                                                    "Internal Error", JOptionPane.ERROR_MESSAGE); 
                        }
                        
                    }
                }
            });
        }
        return loadVivaPatternButton;
    }

    private JButton getRunVivaButton() {
        if (runVivaButton == null) {
            runVivaButton = new JButton("Run VIVA");
            runVivaButton.setEnabled(false);
            runVivaButton.addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    runVivaInputValidator();
                    getShowMostRecentResultButton().setEnabled(true);
                    setInputValidationSourceText("VIVA");
                    mostRecentlyRunInputValidatorType = INPUT_VALIDATOR_TYPE.VIVA;
                }
            });
        }
        return runVivaButton;
    }

    private JScrollPane getVivaPatternTextScrollPane () {
        if (vivaPatternTextScrollPane==null) {
            vivaPatternTextScrollPane = new JScrollPane(getVivaPatternTextArea());
            vivaPatternTextScrollPane.setPreferredSize(new Dimension(350, 150));
            vivaPatternTextScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            vivaPatternTextScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            vivaPatternTextScrollPane.setViewportBorder(new LineBorder(Color.BLUE,1));
        }
        return vivaPatternTextScrollPane;
    }
    
    public JTextArea getVivaPatternTextArea() {
        if (vivaPatternTextArea == null) {
            vivaPatternTextArea = new JTextArea(8,30);

            vivaPatternTextArea.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent event) {
                    enableUpdateButton();
                    updateInputValidatorPaneComponents();
                    //Viva pattern has changed; mark any previous Viva execution invalid
                    setVivaInputValidatorHasBeenRun(false);
                }
            });
        }
        return vivaPatternTextArea;
    }
    
    private JPanel getCustomInputValidatorPanel() {
        if (customInputValidatorPanel == null) {
            customInputValidatorPanel = new JPanel();
            
            customInputValidatorPanel.setMaximumSize(new Dimension(750, 600));
            customInputValidatorPanel.setMinimumSize(new Dimension(750, 200));
            customInputValidatorPanel.setPreferredSize(new Dimension(750, 500));
            customInputValidatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
//            customInputValidatorPanel.setBorder(new LineBorder(Color.CYAN,1));

            customInputValidatorPanel.setLayout(new BorderLayout(0, 0));  //0,0 = hgap,vgap   
            
            customInputValidatorPanel.add(getCustomOptionRadioButtonPanel(),BorderLayout.NORTH);
            customInputValidatorPanel.add(getRigidArea_3(),BorderLayout.WEST);
            customInputValidatorPanel.add(getCustomOptionsSubPanel(),BorderLayout.CENTER);
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
    
    protected JRadioButton getUseCustomInputValidatorRadioButton() {
        if (useCustomInputValidatorRadioButton==null) {
            useCustomInputValidatorRadioButton = new JRadioButton("Use Custom (user-supplied) Input Validator");
            useCustomInputValidatorRadioButton.setSelected(false);
            useCustomInputValidatorRadioButton.addActionListener( new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    setCurrentInputValidatorType(INPUT_VALIDATOR_TYPE.CUSTOM);
                    updateInputValidatorPaneComponents();
                    enableUpdateButton();
                }
                
            });
        }
        return useCustomInputValidatorRadioButton ;
    }
    
    protected JPanel getCustomOptionsSubPanel() {
        if (customOptionsPanel == null) {
            customOptionsPanel = new JPanel();
            
            customOptionsPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Custom Validator Options", 
                                                            TitledBorder.LEADING, TitledBorder.TOP, null, new Color(255, 0, 0)));

            customOptionsPanel.setMinimumSize(new Dimension(500, 200));
            customOptionsPanel.setPreferredSize(new Dimension(500, 200));
            customOptionsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
            customOptionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            FlowLayout flowLayout = (FlowLayout) customOptionsPanel.getLayout();
            flowLayout.setAlignment(FlowLayout.LEFT);
            customOptionsPanel.add(getRigidArea_6());
            customOptionsPanel.add(getCustomInputValidatorProgramPanel());
            customOptionsPanel.add(getRigidArea_7());
            customOptionsPanel.add(getCustomInputValidatorOptionsButtonPanel());
        }
        return customOptionsPanel;
    }
        
    public DefineCustomInputValidatorPane getCustomInputValidatorProgramPanel() {
        if (customInputValidatorProgramPanel==null) {
            customInputValidatorProgramPanel = new DefineCustomInputValidatorPane();
            customInputValidatorProgramPanel.setParentPane(this);
        }
        return customInputValidatorProgramPanel;
    }

    private JPanel getCustomInputValidatorOptionsButtonPanel() {
        if (customInputValidatorOptionsButtonPanel==null) {
            customInputValidatorOptionsButtonPanel = new JPanel();
//            customInputValidatorOptionsButtonPanel.setBorder(new LineBorder(Color.BLUE, 1));
            customInputValidatorOptionsButtonPanel.setLayout(new BoxLayout(customInputValidatorOptionsButtonPanel,BoxLayout.Y_AXIS));
            customInputValidatorOptionsButtonPanel.add(getRigidArea_10());
            customInputValidatorOptionsButtonPanel.add(getChooseCustomInputValidatorProgramButton());
            customInputValidatorOptionsButtonPanel.add(getRigidArea_8());
            customInputValidatorOptionsButtonPanel.add(getRunCustomInputValidatorButton());
            customInputValidatorOptionsButtonPanel.add(getRigidArea_9());
            customInputValidatorOptionsButtonPanel.add(getRemoveCustomInputValidatorButton());
        }
        return customInputValidatorOptionsButtonPanel;
    }

    private JButton getChooseCustomInputValidatorProgramButton() {
        if (chooseCustomInputValidatorProgramButton==null) {
            chooseCustomInputValidatorProgramButton = new JButton("Choose Program...");
            chooseCustomInputValidatorProgramButton.addActionListener(new ActionListener() {                
                
                public void actionPerformed(ActionEvent e) {
                    JTextField tf = getCustomInputValidatorProgramPanel().getCustomInputValidatorProgramNameTextField();
                    //ask the user to select a Custom Input Validator file
                    if (selectFile(tf, "Choose Custom Input Validator Program")) {
                        //if we get here, selectFile() returned true -- meaning a file was selected 
                        // AND the "tf" textfield was updated with the file name
                        try {
                            //construct SerializedFile containing Custom Input Validator file
                            SerializedFile customValidatorSF = new SerializedFile(tf.getText());
                            
                            //make sure no errors occurred during SerializedFile construction
                            String errMsg = customValidatorSF.getErrorMessage();
                            Exception ex = customValidatorSF.getException();
                            if (errMsg!=null || ex!=null) {
                                getController().getLog().warning("Error accessing file '" + tf.getText() + "': " + errMsg + ": " + ex); 
                                JOptionPane.showMessageDialog(null, "Error accessing file '" + tf.getText() + "': " + errMsg + ": " + ex,
                                                        "Error Accessing File", JOptionPane.ERROR_MESSAGE);                                 
                            } else {
                                //no errors; save SerializedFile in InputValidatorPane
                                setCustomInputValidatorFile(customValidatorSF);
                                
                                //update textfield tooltiptext to contain full path to file
                                tf.setToolTipText(customValidatorSF.getAbsolutePath());
                                
                                //if the Custom Input Validator is a Java class file, update the Validator Command
                                if (tf.getText() != null && tf.getText().endsWith(".class")) {
                                    getCustomInputValidatorProgramPanel().getCustomInputValidatorCommandTextField().setText("java {:basename}");
                                }

                                //if the Custom Input Validator is a (Windows) .bat file, update the Validator Command
                                if (tf.getText() != null && tf.getText().toLowerCase().endsWith(".bat")) {
                                    String customValidatorName = tf.getText();
                                    getCustomInputValidatorProgramPanel().getCustomInputValidatorCommandTextField().setText("cmd /c " + customValidatorName);
                                }

                               //refresh enabled condition of Custom Input Validator GUI components
                                updateInputValidatorPaneComponents() ;
                                
                                //validator has changed; mark any previous execution as no longer valid
                                setCustomInputValidatorHasBeenRun(false);
                                
                                //enable Edit Problem "Update" button
                                ((EditProblemPane)getParentPane()).enableUpdateButton();
                            }
                        } catch (ClassCastException e2) {
                            getController().getLog().severe("Parent of InputValidatorPane is not an EditProblemPane; not supported: " + e2); 
                            JOptionPane.showMessageDialog(null, "Internal error; see logs and please report this to the PC2 Development team (pc2@ecs.csus.edu)",
                                                    "Internal Error", JOptionPane.ERROR_MESSAGE); 
                        }
                        
                        //the user selected a new Custom Input Validator, so it has not been run yet
                        setCustomInputValidatorHasBeenRun(false);
                        updateMostRecentResultButton();
                        setCustomInputValidationStatus(InputValidationStatus.NOT_TESTED);
                    }
                }
            });
        }
        return chooseCustomInputValidatorProgramButton;
    }
    

    private JButton getRunCustomInputValidatorButton() {
        if (runCustomInputValidatorButton==null) {
            runCustomInputValidatorButton = new JButton("Run Custom Input Validator");
            runCustomInputValidatorButton.setEnabled(false);
            runCustomInputValidatorButton.addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    runCustomInputValidator();
                    String source = getCustomInputValidatorFile().getName();
                    if (source==null || source.equals("")) {
                        source = "Unknown";
                    }
                    setInputValidationSourceText(source);
                    mostRecentlyRunInputValidatorType = INPUT_VALIDATOR_TYPE.CUSTOM;
                }
            });
        }
        return runCustomInputValidatorButton;
    }
    
    private JButton getRemoveCustomInputValidatorButton() {
        if (removeCustomInputValidatorButton==null) {
            removeCustomInputValidatorButton = new JButton("Remove Custom Input Validator");
            removeCustomInputValidatorButton.setEnabled(false);
            removeCustomInputValidatorButton.addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    
                    int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove the Custom Input Validator from the Problem?",
                            "Confirm removal", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    
                    if (response==JOptionPane.YES_OPTION) {
                        
                        //remove all vestiges of the custom input validator
                        setCustomInputValidatorFile(null);
                        setCustomInputValidatorCommand("");
                        setCustomInputValidatorCommandToolTipText(null);

                        //refresh enabled condition of Custom Input Validator GUI components
                        updateInputValidatorPaneComponents() ;
                        
                        //validator has changed; mark any previous execution as no longer valid
                        setCustomInputValidatorHasBeenRun(false);
                        
                        try {
                            //enable Edit Problem "Update" button
                            ((EditProblemPane) getParentPane()).enableUpdateButton();
                        } catch (ClassCastException e2) {
                            getController().getLog().severe("Parent of InputValidatorPane is not an EditProblemPane; not supported: " + e2); 
                            JOptionPane.showMessageDialog(null, "Internal error; see logs and please report this to the PC2 Development team (pc2@ecs.csus.edu)",
                                                    "Internal Error", JOptionPane.ERROR_MESSAGE); 
                        }
                        
                        //the user removed the Custom Input Validator, so it has not been run yet
                        updateMostRecentResultButton();
                        setCustomInputValidationStatus(InputValidationStatus.NOT_TESTED);
                    }
                }
            });
        }
        return removeCustomInputValidatorButton;
    }

    /**
     * Returns the text contained in the "VIVA Pattern" text area in the GUI.
     * The text is returned as a single String, with embedded line separators, 
     * as if method {@link JTextArea#getText()} was invoked.
     * 
     * @return a String containing the text in the VIVA pattern text area.
     */
    public String getVivaPatternText() {
        return getVivaPatternTextArea().getText();
    }

   /**
     * Returns the Custom Input Validator Command currently entered into the CustomInputValidatorProgramPanel text field.
     * 
     * @return a String containing the command to be used to invoke a Custom Input Validator.
     */
    public String getCustomInputValidatorCommand() {
        String command = getCustomInputValidatorProgramPanel().getCustomInputValidatorCommand();
        return command;
    }

    /**
     * Returns the Custom Input Validator program name currently associated with the problem defined in this InputValidatorPane GUI.
     * The Input Validator program name is defined by the {@link SerializedFile} defining the 
     * Custom Input Validator. 
     * 
     * @return a String containing the program name defined by the Custom Input Validator SerializedFile, or the empty string
     *          if there is no Custom Input Validator SerializedFile associated with the problem.
     * 
     * @see {@link #getCustomInputValidatorFile()}
     * @see {@link SerializedFile#getName()}
     * 
     * @return a String containing the text in the Custom Input Validator Program text field.
     */
    public String getCustomInputValidatorProgramName() {
        SerializedFile inputValidatorFile = getCustomInputValidatorFile();
        if (inputValidatorFile!=null) {
            return inputValidatorFile.getName();
        } else {
            return "";
        }
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
     * Sets the ToolTip text for the Custom Input Validator Command displayed in this InputValidatorPane.
     * 
     * @param text
     *            the ToolTip text to set
     */
    public void setCustomInputValidatorCommandToolTipText(String text) {
        getCustomInputValidatorProgramPanel().setCustomInputValidatorCommandToolTipText(text);

    }

    private JPanel getShowInputValidatorResultButtonPanel() {
        if (showInputValidatorResultButtonPane==null) {
            showInputValidatorResultButtonPane = new JPanel();
            showInputValidatorResultButtonPane.setAlignmentX(LEFT_ALIGNMENT);
            showInputValidatorResultButtonPane.add(getShowMostRecentResultButton());
        }
        return showInputValidatorResultButtonPane;
    }
    
    
    public void updateInputValidatorPaneComponents() {
        if (getUseNoInputValidatorRadioButton().isSelected()) {
            enableVivaValidatorComponents(false);
            enableCustomValidatorComponents(false);
        } else if (getUseVivaInputValidatorRadioButton().isSelected()) {
            enableVivaValidatorComponents(true);
            enableCustomValidatorComponents(false);
        } else if (getUseCustomInputValidatorRadioButton().isSelected()) {
            enableVivaValidatorComponents(false);
            enableCustomValidatorComponents(true);
        } 
        updateMostRecentResultButton();
    }

    private void enableVivaValidatorComponents(boolean enableComponents) {
        getVivaOptionsSubPanel().setEnabled(enableComponents);
        getVivaPatternLabel().setEnabled(enableComponents);
        getVivaPatternTextScrollPane().setEnabled(enableComponents);
        getVivaPatternTextArea().setEnabled(enableComponents);
        getLoadVivaPatternButton().setEnabled(enableComponents);
        
        //enable the Run Viva button, but only if there is some kind of text in the Viva pattern
        String vivaPattern = getVivaPatternTextArea().getText();
        getRunVivaButton().setEnabled(enableComponents && vivaPattern!=null && !vivaPattern.equals(""));
    }
    
    private void enableCustomValidatorComponents(boolean enableComponents) {
        getCustomOptionsSubPanel().setEnabled(enableComponents);
        getCustomInputValidatorProgramPanel().setEnabled(enableComponents);
        getCustomInputValidatorOptionsButtonPanel().setEnabled(enableComponents);
        getChooseCustomInputValidatorProgramButton().setEnabled(enableComponents);
        
        //only allow enabling the Run/Remove Custom Input Validator buttons if there is a Custom Input Validator defined
        boolean okToEnableRunAndRemoveCustomIVButtons = getCustomInputValidatorFile()!=null  
                    && getCustomInputValidatorCommand()!=null && !getCustomInputValidatorCommand().equals("") 
                    && getCustomInputValidatorProgramName()!=null
                    && !getCustomInputValidatorProgramName().equals("");
        getRunCustomInputValidatorButton().setEnabled(enableComponents && okToEnableRunAndRemoveCustomIVButtons);
        getRemoveCustomInputValidatorButton().setEnabled(enableComponents && okToEnableRunAndRemoveCustomIVButtons);
    }
    
    /**
     * Enables the ShowMostRecentResultButton if and only if the currently selected Input Validator has been run.
     * 
     * Also updates the button text to show which validator results will be displayed.
     */
    private void updateMostRecentResultButton() {
        //default to "no validator has been run, so button should be disabled"
        getShowMostRecentResultButton().setText("Show Most Recent Result");
        getShowMostRecentResultButton().setEnabled(false);

        if (getUseVivaInputValidatorRadioButton().isSelected() && vivaInputValidatorHasBeenRun ) {
            getShowMostRecentResultButton().setText("Show Most Recent Viva Result");
            getShowMostRecentResultButton().setEnabled(true);
        } else if (getUseCustomInputValidatorRadioButton().isSelected() && customInputValidatorHasBeenRun) {
            getShowMostRecentResultButton().setText("Show Most Recent Custom IV Result");
            getShowMostRecentResultButton().setEnabled(true); 
        }
    }
    
    /**
     * Spawns a separate thread to run the VIVA Input Validator against all the data files for the current Problem.
     * 
     */
    private void runVivaInputValidator() {
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (okToRunInputValidator()) {

                    getRunVivaButton().setEnabled(false); // this is set back true when the spawner finishes, via a call to cleanup()
                    getShowOnlyFailedFilesCheckbox().setEnabled(false); //disallow changing during result production
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    
                    spawnInputValidatorRunnerThread(Problem.INPUT_VALIDATOR_TYPE.VIVA);

                } else {
                    // determine why it's not ok to run the Input Validator and display an appropriate message
                    boolean msgShown = false;
                    if (!problemHasInputDataFiles()) {
                        JOptionPane.showMessageDialog(null, "No Judge's Data Files defined; cannot run VIVA", "Missing Data Files", JOptionPane.INFORMATION_MESSAGE);
                        msgShown = true;
                    } else if (getUseVivaInputValidatorRadioButton().isSelected() && getVivaPatternTextArea().getText().trim().equals("")) {
                        JOptionPane.showMessageDialog(null, "Missing VIVA pattern; cannot run VIVA", "Missing VIVA Pattern", JOptionPane.INFORMATION_MESSAGE);
                        msgShown = true;
                    }
                    if (!msgShown) {
                        JOptionPane.showMessageDialog(null, "Internal error: cannot run VIVA Input Validator (unknown reason)."
                                + "\nPlease see logs and report this issue to the PC2 Development Team (pc2@ecs.csus.edu)", 
                                "Cannot run VIVA Input Validator", JOptionPane.ERROR_MESSAGE);                        
                        getLog().severe("Internal error: cannot run VIVA Input Validator even though Problem has data files and a VIVA pattern; reason unknown.");
                    }

                }
            }
        });

    }

    /**
     * Spawns a separate thread to run the currently-specified Custom Input Validator against all the data files for the current Problem.
     * 
     */
    private void runCustomInputValidator() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (okToRunInputValidator()) {

                    getRunCustomInputValidatorButton().setEnabled(false); // this is set back true when the spawner finishes, via a call to cleanup()
                    getShowOnlyFailedFilesCheckbox().setEnabled(false); //disallow changing during result production
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    
                    spawnInputValidatorRunnerThread(Problem.INPUT_VALIDATOR_TYPE.CUSTOM);

                } else {
                    // determine why it's not ok to run the Input Validator and display an appropriate message
                    boolean msgShown = false;
                    if (!problemHasInputDataFiles()) {
                        JOptionPane.showMessageDialog(null, "No Judge's Data Files defined; cannot run Input Validator", "Missing Data Files", JOptionPane.INFORMATION_MESSAGE);
                        msgShown = true;
                    } else if (getUseCustomInputValidatorRadioButton().isSelected() && !problemHasCustomInputValidatorCommand()) {
                        JOptionPane.showMessageDialog(null, "Missing Custom Input Validator Command; cannot run Input Validator", "Missing Command", JOptionPane.INFORMATION_MESSAGE);
                        msgShown = true;
                    }
                    if (!msgShown) {
                        JOptionPane.showMessageDialog(null, "Internal error: cannot run Custom Input Validator (unknown reason)."
                                + "\nPlease see logs and report this issue to the PC2 Development Team (pc2@ecs.csus.edu)", 
                                "Cannot run Input Validator", JOptionPane.ERROR_MESSAGE);                        
                        getLog().severe("Internal error: cannot run Custom Input Validator even though Problem has an Input Validator Command; reason unknown.");
                    }

                }
            }
        });
    }

    /**
     * Verifies that all conditions necessary to run the Input Validator associated with this InputValidatorPane are true. 
     * If the "VIVA" Input Validator has been selected, the conditions include that there is a pattern in the VIVA Pattern text box
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
            ok = patternText!=null && !patternText.trim().equals("") && problemHasInputDataFiles();
            
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
        String customInputValidatorCommand = getCustomInputValidatorProgramPanel().getCustomInputValidatorCommand();
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
     * Spawns a separate {@link SwingWorker} thread to run the specified type of Input Validator.
     * 
     * The worker thread publishes each separate InputValidationResult as it is generated; each published result is automatically 
     * picked up and handled by the worker's process() method. Once the worker thread completes it assigns the results to a global 
     * variable which is accessible by external clients via an accessor.
     * 
     * See https://docs.oracle.com/javase/tutorial/uiswing/concurrency/interim.html for details on how SwingWorker threads publish results.
     */
    private void spawnInputValidatorRunnerThread(Problem.INPUT_VALIDATOR_TYPE validatorType) {

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

                    // get the problem for which the data files apply
                    Problem prob = epp.getProblem();

                    // get the execution directory being used by the EditProblemPane
                    String executeDir = epp.getExecuteDirectoryName();

                    // clear the results table in preparation for adding new results
                    ((InputValidationResultsTableModel) resultFrame.getInputValidationResultPane().getInputValidationResultsTable().getModel()).setResults(null);
                    ((AbstractTableModel) resultFrame.getInputValidationResultPane().getInputValidationResultsTable().getModel()).fireTableDataChanged();

                    // clear the result accumulator
                    accumulatingCustomResults = null;
                    
                    // run the specified Input Validator on each data file
                    int filesProcessed = 0;
                    for (int fileNum = 0; fileNum < dataFiles.length; fileNum++) {

                        // get the next data file
                        SerializedFile dataFile = dataFiles[fileNum];

                        // should figure out how to run each of these calls on a separate thread and still return intermediate results...
                        // this might need to be done here, or else in method runInputValidator()...
                        // One thing to worry about: each call to runInputValidator() uses the (same?) execute directory; that can cause collisions
                        // with the files which are being created therein (e.g. the stdout.pc2 and stderr.pc2 files)
                        try {
                            //run the appropriate Input Validator
                            switch (validatorType) {
                                case CUSTOM:
                                    validationResults[fileNum] = runCustomInputValidator(fileNum + 1, prob, getCustomInputValidatorFile(), getCustomInputValidatorCommand(), dataFile, executeDir);
                                    setCustomInputValidatorHasBeenRun(true);
                                    break;
                            
                                case VIVA:
                                    validationResults[fileNum] = runVivaInputValidator(fileNum + 1, prob, getVivaPatternText(), dataFile, executeDir);
                                    setVivaInputValidatorHasBeenRun(true);
                                    break;
                               
                                default:
                                    String msg = "Internal error: SwingWorker.doInBackground() invoked with invalid validator type: " + validatorType ;
                                    msg += "\nPlease see logs and report this error to the PC2 Development Team (pc2@ecs.csus.edu";
                                    showMessage(epp, "Internal Error", msg);
                                    getController().getLog().severe("SwingWorker.doInBackground() invoked with incorrect validator type: " + validatorType);
                                    return null;
                            }
                            epp.enableUpdateButton();

                            // publish the validator result for the current data file, to be picked up by the process() method (below)
                            publish(validationResults[fileNum]);

                        } catch (Exception e) {
                            getController().getLog().warning("Exception running Input Validator of type '" + validatorType 
                                                            + "' : " + e.getMessage());
                        }
                        
                        filesProcessed++;
                        
                        //short-circuit the testing loop if an error occurred during Input Validator execution
                        // (note this does not mean "if a data file failed validation"; it means the Input Validator failed to execute properly.
                        // In this case there's no point in continuing to try to execute it repeatedly...)
                        if (validationResults[fileNum].getStatus()==InputValidationStatus.ERROR) {
                            break;
                        }
                    }

                    //check if we actually processed all the data files
                    if (filesProcessed<dataFiles.length) {
                        //no (we must have short-circuited above); construct a smaller return array
                        InputValidationResult [] shortResults = new InputValidationResult[filesProcessed];
                        for (int i=0; i<filesProcessed; i++) {
                            shortResults[i] = validationResults[i];
                        }
                        return shortResults;
                    } else {
                        return validationResults;
                    }
                    
                } else {
                    // the parent is not an EditProblemPane; the current code doesn't support returning results to any other Type
                    getController().getLog().warning("Attempted to return Input Validator results to a " + parent.getClass() 
                                                    + "; currently only EditProblemPane is supported for receiving results");
                    System.err.println("Warning: attempt to assign Input Validator results to an unsupported type: " + parent.getClass());
                    return null;
                }

            }

            /**
             * This method is called by the Worker thread's publish() method each time one or more results are finished. 
             * The input to the method is a list of results which have been completed since the last call to this method. 
             * This method adds each published result to the results table, by calling addToResultTable().
             */
            @Override
            public void process(List<InputValidationResult> resultList) {
                // display the results (which may be partial) in the InputValidatorPane's InputValidationResults table

                for (InputValidationResult result : resultList) {
                    if (result!=null) {
                        if (!resultFrame.getInputValidationResultPane().getShowOnlyFailedFilesCheckbox().isSelected() || !result.isPassed()) {
                            addResultToTable(result);
                        }
                        addResultToAccumulatedList(result);
                        updateInputValidationSummaryText(accumulatingCustomResults);
                        // addResultToProblem(result); //we don't want to do this until Add/Update is pressed
                        // updateProblemValidationStatus(result); // ditto ""
                    }
                }
            }

            /**
             * This method is invoked by the Worker thread when it is completely finished with its doInBackground task(s). 
             * Calling get() fetches the set of data returned by the Worker thread's doInBackground() method -- that is, 
             * an array of InputValidationResults. This method saves those results so they can be accessed by external code 
             * via the {@link #getCustomInputValidatorResults()} or (@link getVivaInputValidatorResults()} methods.
             * The method also updates the GUI based on the results, and finally calls cleanup() to restore the GUI state.
             */
            @Override
            public void done() {

                try {
                    switch (validatorType) {
                        case CUSTOM:
                            customInputValidatorResults = get();
                            if (customInputValidatorResults != null && customInputValidatorResults.length > 0) {
                                updateInputValidationSummaryText(customInputValidatorResults);
                                updateCustomInputValidationStatus(customInputValidatorResults);
                            }
                            break;
                        case VIVA:
                            vivaInputValidatorResults = get();
                            if (vivaInputValidatorResults != null && vivaInputValidatorResults.length > 0) {
                                updateInputValidationSummaryText(vivaInputValidatorResults);
                                updateVivaInputValidationStatus(vivaInputValidatorResults);
                            }
                            break;
                        default:
                            System.err.println("Internal error: SwingWorker.done(): unexpected validator type: " + validatorType);
                            System.err.println("Please checks logs and report this error to the PC2 Development Team (pc2@ecs.csus.edu)");
                            getController().getLog().severe("InputValidatorPane SwingWorker.done(): unexpected validator type: " + validatorType);
                            JOptionPane.showMessageDialog(null, "Internal error: unexpected Input Validator type: " + validatorType 
                                    + "\nPlease checks logs and report this error to the PC2 Development Team (pc2@ecs.csus.edu)", 
                                    "Internal Error", JOptionPane.ERROR_MESSAGE);
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
     * This method is called when spawnInputValidatorRunnerThread() finishes. Its job is to restore the GUI state, 
     * including resetting the cursor and reenabling the appropriate RunInputValidator button.
     */
    private void cleanup() {
        //TODO: update to account for VIVA
        getRunCustomInputValidatorButton().setEnabled(true);
        getShowOnlyFailedFilesCheckbox().setEnabled(true); //note this is only ENABLING the checkbox, not changing its "checked state"
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        updateInputValidatorPaneComponents();
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
        if (accumulatingCustomResults == null || accumulatingCustomResults.length <= 0) {
            accumulatingCustomResults = new InputValidationResult[1];
            accumulatingCustomResults[0] = newResult;
        } else {
            InputValidationResult[] temp = new InputValidationResult[accumulatingCustomResults.length + 1];
            int i = 0;
            for (InputValidationResult res : accumulatingCustomResults) {
                temp[i++] = res;
            }
            temp[i] = newResult;
            accumulatingCustomResults = temp;
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
     * Updates the Custom Input Validator Run Status flag in this InputValidatorPane. Note that this does NOT update the Problem; 
     * just the local status flag. The Problem only gets updated when Add or Update
     * is pressed on the EditProblemPane.
     * 
     * If the received InputValidationResult array is null or empty, no change is made in the current status. 
     * Otherwise, if all the results in the array are "Passed" then the status is set to Passed;
     * if one or more results in the array are "Failed" then the status is set to Failed.
     * 
     * @param runResultsArray
     *            an array of InputValidationResults from having run a Custom Input Validator
     */
    private void updateCustomInputValidationStatus(InputValidationResult[] runResultsArray) {

        if (runResultsArray != null && runResultsArray.length > 0) {

            boolean foundFailure = false;
            for (InputValidationResult res : runResultsArray) {
                if (!res.isPassed()) {
                    foundFailure = true;
                    break;
                }
            }

            if (foundFailure) {
                setCustomInputValidationStatus(InputValidationStatus.FAILED);
            } else {
                setCustomInputValidationStatus(InputValidationStatus.PASSED);
            }
        }
    }
    
    /**
     * Returns the {@link InputValidationStatus} for the Custom Input Validator (if any) defined by this {@link InputValidatorPane}. 
     * The returned value indicates whether a Custom Input Validator has been run, and if so whether all data files passed or not.
     * 
     * @return the Custom Input Validation Status for this pane
     */
    protected InputValidationStatus getCustomInputValidationStatus() {
        return this.customInputValidationStatus;
    }

    protected void setCustomInputValidationStatus(InputValidationStatus newStatus) {
        this.customInputValidationStatus = newStatus;
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
    private InputValidationResult runCustomInputValidator(int seqNum, Problem problem, SerializedFile validatorProg, String validatorCommand, SerializedFile dataFile, String executeDir) throws Exception {

        CustomInputValidatorRunner runner = new CustomInputValidatorRunner(getContest(), getController());
        InputValidationResult result = null;
        try {
            result = runner.runCustomInputValidator(seqNum, problem, validatorProg, validatorCommand, executeDir, dataFile);
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
     * Runs the VIVA Input Validator Program, passing it a VIVA pattern and a data file and returning an 
     * {@link InputValidationResult} giving indication of whether the 
     * data file matches the VIVA pattern.
     * 
     * NOTE: TODO: this should be done on a separate thread. However, see the note in spawnCustomInputValidatorRunnerThread() 
     * about collisions in folders...
     * 
     * @param seqNum a sequence number for associating with generated file names (although VIVA doesn't generate any such files...)
     * @param prob the Contest Problem associated with the Input Validator
     * @param vivaPatternText the VIVA pattern text which the specified data file must match
     * @param dataFile the data file to be passed to the VIVA Input Validator as input to be validated
     * @param executeDir the execution directory to be used (i.e. the folder in which to run the Input Validator Program - not used by VIVA)
     * 
     * @return an InputValidationResult containing the result of running VIVA against the specified datafile using the specified pattern,
     *              or null if the pattern is invalid or if an error occurred while running VIVA.
     */
    private InputValidationResult runVivaInputValidator(int seqNum, Problem prob, String vivaPatternText, SerializedFile dataFile, String executeDir) {
        
        //use the VivaAdapter to invoke VIVA to test the data file against the pattern
        VivaDataFileTestResult vivaTestResult = getVivaAdapter().testFile(vivaPatternText, dataFile);
        
        //check if Viva returned an error (which it will do, for example, if the pattern is invalid or if a processing error occurred)
        InputValidationStatus vivaTestStatus = vivaTestResult.getStatus();
        if (vivaTestStatus==InputValidationStatus.ERROR) {
            //return an InputValidationResult error
            SerializedFile dummyVivaStderr = new SerializedFile("VivaStderr"); //because Viva doesn't produce any stderr, but its needed for the following constructor
            return new InputValidationResult(prob, dataFile.getAbsolutePath(), false, vivaTestStatus, vivaTestResult.getVivaOutput(), dummyVivaStderr);
        }
        
        //we got back a result from VIVA; convert it to an InputValidationResult:
        
        //read the stdout from VIVA
        SerializedFile vivaStdout = vivaTestResult.getVivaOutput();
        
        //construct an empty stderr file (Viva sends nothing to stderr, but the InputValidationResult constructed below needs a stderr file)
        SerializedFile dummyStderr = new SerializedFile("VivaStderr");

        InputValidationStatus status = vivaTestResult.passed() ? Problem.InputValidationStatus.PASSED : Problem.InputValidationStatus.FAILED;
        
        //construct a results object holding the Viva results
        InputValidationResult result = new InputValidationResult(prob, dataFile.getAbsolutePath(), vivaTestResult.passed(), status, vivaStdout, dummyStderr);
        
        return result;
        
    }


    /**
     * @return the customInputValidatorHasBeenRun flag
     */
    public boolean isCustomInputValidatorHasBeenRun() {
        return customInputValidatorHasBeenRun;
    }

    /**
     * @param customInputValidatorHasBeenRun
     *            the value to which the customInputValidatorHasBeenRun flag should be set
     */
    protected void setCustomInputValidatorHasBeenRun(boolean customInputValidatorHasBeenRun) {
        this.customInputValidatorHasBeenRun = customInputValidatorHasBeenRun;
    }

    /**
     * Sets the specified array of InputValidationResults as the current Custom Input Validator Results. 
     * @param results an array of InputValidationResults which is to be saved in this class.
     */
    protected void setCustomInputValidatorResults(InputValidationResult[] results) {
        this.customInputValidatorResults = results;
    }

    /**
     * Inserts the elements of the specified Iterable into an array and saves that array as the Custom Input Validator results.
     * 
     * @param customIterableResults an Iterable giving a set of Custom Input Validator results.
     */
    protected void setCustomInputValidatorResults(Iterable<InputValidationResult> customIterableResults) {
        
        Vector<InputValidationResult> temp = new Vector<InputValidationResult>();
        for (InputValidationResult res : customIterableResults) {
            temp.add(res);
        }
        InputValidationResult [] array = new InputValidationResult[temp.size()];
        customInputValidatorResults = temp.toArray(array);
    }

    
    //*** VIVA Input Validator methods *** //
    
    /**
     * Returns true if the Viva Pattern Text Area in the GUI contains text; false otherwise.
     * Note that this method makes no determination of whether the text in the Text Area constitutes
     * a LEGAL Viva Pattern; it simply returns an indication of whether or not there is ANY text in
     * the text area or not.
     * 
     */
    protected boolean isProblemHasVivaInputValidatorPattern() {
        return (getVivaPatternText()!=null && !getVivaPatternText().equals(""));
    }

    /**
     * Returns the boolean flag indicating whether the Viva Input Validator has been run.
     * 
     * @return the VivaInputValidatorHasBeenRun flag
     */
    public boolean isVivaInputValidatorHasBeenRun() {
        return this.vivaInputValidatorHasBeenRun;
    }

    /**
     * @param vivaHasBeenRun
     *            the value to which the vivaInputValidatorHasBeenRun flag should be set
     */
    public void setVivaInputValidatorHasBeenRun(boolean vivaHasBeenRun) {
        this.vivaInputValidatorHasBeenRun = vivaHasBeenRun;
    }

    /**
     * Returns the {@link InputValidationResult}s for the most recent execution of the Viva Input Validator.
     * 
     * @return an array of InputValidationResults from the most recent Viva Input Validator execution.
     */
    protected InputValidationResult[] getVivaInputValidatorResults() {
        return vivaInputValidatorResults;
    }

    /**
     * Sets the specified array of InputValidationResults as the current Viva Input Validator Results. 
     * @param results an array of InputValidationResults which is to be saved in this class.
     */
    protected void setVivaInputValidatorResults(InputValidationResult[] results) {
        this.vivaInputValidatorResults = results;
    }

    /**
     * Inserts the elements of the specified Iterable into an array and saves that array as the Viva Input Validator results.
     * 
     * @param vivaIterableResults an Iterable giving a set of Viva Input Validator results.
     */
    protected void setVivaInputValidatorResults(Iterable<InputValidationResult> vivaIterableResults) {
        
        Vector<InputValidationResult> temp = new Vector<InputValidationResult>();
        for (InputValidationResult res : vivaIterableResults) {
            temp.add(res);
        }
        InputValidationResult [] array = new InputValidationResult[temp.size()];
        vivaInputValidatorResults = temp.toArray(array);
    }

    /**
     * Updates the Viva Input Validator Run Status flag in this InputValidatorPane. Note that this does NOT update the Problem; 
     * just the local status flag. The Problem only gets updated when Add or Update
     * is pressed on the EditProblemPane.
     * 
     * If the received InputValidationResult array is null or empty, no change is made in the current status. 
     * Otherwise, if all the results in the array are "Passed" then the status is set to Passed;
     * if one or more results in the array are "Failed" then the status is set to Failed.
     * 
     * @param runResultsArray
     *            an array of InputValidationResults from having run the Viva Input Validator
     */
    private void updateVivaInputValidationStatus(InputValidationResult[] runResultsArray) {

        if (runResultsArray != null && runResultsArray.length > 0) {

            //TODO: need to make sure there is at least one "passed" result
            boolean foundFailure = false;
            for (InputValidationResult res : runResultsArray) {
                if (res!=null && !res.isPassed()) {
                    foundFailure = true;
                    break;
                }
            }

            if (foundFailure) {
                setVivaInputValidationStatus(InputValidationStatus.FAILED);
            } else {
                setVivaInputValidationStatus(InputValidationStatus.PASSED);
            }
        }
    }
    
    /**
     * Returns the {@link InputValidationStatus} for the VIVA Input Validator (if any) defined by this {@link InputValidatorPane}. 
     * The returned value indicates whether the VIVA Input Validator has been run, and if so whether all data files passed or not.
     * 
     * @return the VIVA Input Validation Status for this pane
     */
    protected InputValidationStatus getVivaInputValidationStatus() {
        return this.vivaInputValidationStatus;
    }

    protected void setVivaInputValidationStatus(InputValidationStatus newStatus) {
        this.vivaInputValidationStatus = newStatus;
    }


    //*** General Input Validation Status methods *** /
    
    public void setInputValidationSummaryMessageText(String msg) {
        resultFrame.getInputValidationResultPane().setInputValidationSummaryMessageText(msg);
    }

    public void setInputValidationSummaryMessageColor(Color color) {
        resultFrame.getInputValidationResultPane().setInputValidationSummaryMessageColor(color);
    }
    
    public void setInputValidationSourceText(String msg) {
        resultFrame.getInputValidationResultPane().setInputValidationResultSourceText(msg);
    }

    public void updateInputValidationStatusMessage(InputValidationResult[] results) {
        resultFrame.getInputValidationResultPane().updateInputValidationStatusMessage(results);
    }

    /**
     * Constructs a new singleton instance of the VivaAdapter class (if not already constructed)
     * and returns that instance.
     * 
     * @return a singleton VivaAdapter
     */
    public VivaAdapter getVivaAdapter() {
        if (vivaAdapter==null) {
            vivaAdapter = new VivaAdapter(getController());
        }
        return vivaAdapter;
    }

    public InputValidationResultsTableModel getResultsTableModel() {
        return (InputValidationResultsTableModel) resultFrame.getInputValidationResultPane().getInputValidationResultsTable().getModel();
    }

    public JCheckBox getShowOnlyFailedFilesCheckbox() {
        return resultFrame.getInputValidationResultPane().getShowOnlyFailedFilesCheckbox();
    }

    /**
     * Returns the {@link SerializedFile} containing the Custom Input Validator program code associated with the current problem.
     * 
     * @return a SerializedFile containing the Custom Input Validator code.
     */
    public SerializedFile getCustomInputValidatorFile() {
        return getCustomInputValidatorProgramPanel().getCustomInputValidatorFile();
    }
    
    /**
     * Sets the {@link SerializedFile} containing the Custom Input Validator program code to be associated with the current problem.
     * 
     */
    public void setCustomInputValidatorFile(SerializedFile inputValidatorFile) {
        getCustomInputValidatorProgramPanel().setCustomInputValidatorFile(inputValidatorFile);
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
    private JButton showMostRecentResultButton;
    private Component rigidArea;
    private Component rigidArea_4;
    private Component rigidArea_5;
    private Component rigidArea_6;
    private Component rigidArea_7;
    private Component rigidArea_8;
    private Component rigidArea_9;
    private Component rigidArea_10;

    
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

    public JButton getShowMostRecentResultButton() {
        if (showMostRecentResultButton == null) {
        	showMostRecentResultButton = new JButton("Show Most Recent Result");
        	showMostRecentResultButton.setEnabled(false);
        	showMostRecentResultButton.addActionListener(new ActionListener() {
        	    
        	    public void actionPerformed(ActionEvent e) {
        	        
        	        if (getUseVivaInputValidatorRadioButton().isSelected()) {
        	            resultFrame.getInputValidationResultPane().updateResultsTable(vivaInputValidatorResults);
        	        } else if (getUseCustomInputValidatorRadioButton().isSelected()) {
                        resultFrame.getInputValidationResultPane().updateResultsTable(customInputValidatorResults);        	            
        	        }
        	        resultFrame.setVisible(true);
        	    }
        	});
        }
        return showMostRecentResultButton;
    }
    private Component getRigidArea() {
        if (rigidArea == null) {
        	rigidArea = Box.createRigidArea(new Dimension(20, 20));
        }
        return rigidArea;
    }

    private Component getRigidArea_4() {
        if (rigidArea_4 == null) {
        	rigidArea_4 = Box.createRigidArea(new Dimension(20, 20));
        }
        return rigidArea_4;
    }
    
    private Component getRigidArea_5() {
        if (rigidArea_5 == null) {
        	rigidArea_5 = Box.createRigidArea(new Dimension(20, 20));
        }
        return rigidArea_5;
    }
    
    private Component getRigidArea_6() {
        if (rigidArea_6 == null) {
            rigidArea_6 = Box.createRigidArea(new Dimension(20, 20));
        }
        return rigidArea_6;
    }
    
    private Component getRigidArea_7() {
        if (rigidArea_7 == null) {
            rigidArea_7 = Box.createRigidArea(new Dimension(20, 20));
        }
        return rigidArea_7;
    }
    
    private Component getRigidArea_8() {
        if (rigidArea_8 == null) {
            rigidArea_8 = Box.createRigidArea(new Dimension(20, 20));
        }
        return rigidArea_8;
    }
    
    private Component getRigidArea_9() {
        if (rigidArea_9 == null) {
            rigidArea_9 = Box.createRigidArea(new Dimension(20, 20));
        }
        return rigidArea_9;
    }
    
    private Component getRigidArea_10() {
        if (rigidArea_10 == null) {
            rigidArea_10 = Box.createRigidArea(new Dimension(20, 20));
        }
        return rigidArea_10;
    }
    
    @Override
    public void setContestAndController(IInternalContest contest, IInternalController controller) {
        super.setContestAndController(contest, controller);
        resultFrame.setContestAndController(contest, controller);
    }
    
    /**
     * Select file; if a file is actually picked, updates the specified JComponent's tooltiptext.
     * 
     * @param comp the JComponent to be updated if a file is picked
     * @param dialogTitle title for file chooser
     * @return True if a file was selected and label updated
     */
    private boolean selectFile(JComponent comp, String dialogTitle) {
        boolean result = false;
        // toolTip should always have the full path
        String oldFile = comp.getToolTipText();
        String startDir;
        JFileChooser chooser;
        try {
            if (oldFile == null || oldFile.equalsIgnoreCase("")) {
                startDir = ((EditProblemPane) getParentPane()).getLastDirectory();
            } else {
                startDir = oldFile;
            }
            chooser = new JFileChooser(startDir);
            if (dialogTitle != null) {
                chooser.setDialogTitle(dialogTitle);
            }

            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                ((EditProblemPane) getParentPane()).setLastDirectory(chooser.getCurrentDirectory().toString());
                comp.setToolTipText(chooser.getSelectedFile().getCanonicalFile().toString());
                result = true;
            }
        } catch (ClassCastException e) {
            getLog().log(Log.SEVERE, "Internal error: parent pane of InputValidatorPane is not an EditProblemPane; not supported", e);
            JOptionPane.showMessageDialog(this, "Internal error: InputValidatorPane parent is not an EditProblemPane; "
                    + "please report this error to the PC2 Development Team (pc2@ecs.csus.edu)", "Internal Error", JOptionPane.ERROR_MESSAGE);
            result = false;
        } catch (Exception e) {
            getLog().log(Log.INFO, "Error getting selected file, try again.", e);
            JOptionPane.showMessageDialog(this, "Error getting selected file", "File Error", JOptionPane.WARNING_MESSAGE);
            result = false;
        }
        chooser = null;
        return result;
    }
    
    /**
     * Displays a FileChooser dialog for selecting a file. If a file is actually selected then the method updates 
     * the specified JTextField with the selected file name.
     * 
     * @param textField -- a JTextField whose value will be updated if a file is chosen
     * @param dialogTitle title for file chooser
     * @return True if a file was selected and the JTextField updated
     */
    private boolean selectFile(JTextField textField, String dialogTitle) {
        boolean result = false;
        // toolTip should always have the full path
        String oldFile = textField.getToolTipText();
        String startDir;
        JFileChooser chooser;
        try {
            if (oldFile == null || oldFile.equalsIgnoreCase("")) {
                startDir = ((EditProblemPane) getParentPane()).getLastDirectory();
            } else {
                startDir = oldFile;
            }
            chooser = new JFileChooser(startDir);
            if (dialogTitle != null) {
                chooser.setDialogTitle(dialogTitle);
            }

            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                ((EditProblemPane) getParentPane()).setLastDirectory(chooser.getCurrentDirectory().toString());
                textField.setText(chooser.getSelectedFile().getCanonicalFile().toString());
                result = true;
            }
        } catch (ClassCastException e) {
            getLog().log(Log.SEVERE, "Internal error: parent pane of InputValidatorPane is not an EditProblemPane; not supported", e);
            JOptionPane.showMessageDialog(this, "Internal error: InputValidatorPane parent is not an EditProblemPane; " 
                                            + "please report this error to the PC2 Development Team (pc2@ecs.csus.edu)",
                                            "Internal Error", JOptionPane.ERROR_MESSAGE);
            result = false;
        } catch (Exception e) {
            showMessage("Error getting selected file, try again: \n" + e.getMessage());
            getLog().log(Log.INFO, "Error getting selected file: ", e);
            result = false;
        }
        chooser = null;
        return result;
    }

    public void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(null, message);
            }
        });
    }

    public void setCustomInputValidatorProgramName(String progName) {
        getCustomInputValidatorProgramPanel().setCustomInputValidatorProgramName(progName);

    }

    public void setCustomInputValidatorProgramNameToolTipText(String text) {
        getCustomInputValidatorProgramPanel().setCustomInputValidatorProgramNameToolTipText(text);
        
    }

    public void setCustomInputValidatorCommand(String command) {
        getCustomInputValidatorProgramPanel().setCustomInputValidatorCommand(command);       
    }

    /**
     * Calls enableUpdateButton() in the parent EditProblemPane if that pane exists.
     * What that method does is check the various GUI components to determine if anything about the
     * problem configuration has changed, and if so enables the Update button on the EditProblemPane, allowing
     * the user to save the problem changes.
     */
    private void enableUpdateButton() {
        JPanePlugin parent = getParentPane();
        if (parent != null && parent instanceof EditProblemPane) {
            
                 SwingUtilities.invokeLater( new Runnable() {
                    public void run () {
                        ((EditProblemPane)parent).enableUpdateButton();
                    }
               });
                 
        } else {
            System.err.println ("Internal error: parent of InputValidatorPane is not an EditProblemPane; cannot enable Add/Update button"
                    + "\nPlease report this problem to the PC2 Development Team (pc2@ecs.csus.edu");
            getController().getLog().severe("parent of InputValidatorPane is not an EditProblemPane");
        }
    }

    /**
     * Returns the current Input Validator type specified in this InputValidatorPane.  The current type is
     * based on which Radio Button -- useNoInputValidator, useVivaInputValidator, or useCustomInputValidator --
     * is currently selected (these buttons are in a ButtonGroup so only one can be selected at any given time; the 
     * selected button defines the "current Input Validator type").
     * 
     * @return an element of {@link INPUT_VALIDATOR_TYPE} indicating the currently-selected Input Valdator type.
     */
    protected INPUT_VALIDATOR_TYPE getCurrentInputValidatorType() {
        return currentlySelectedInputValidatorType;
    }

    /**
     * Sets the current Input Validator type specified in this InputValidatorPane.  The current type is
     * normally based on (changed by) which Radio Button -- useNoInputValidator, useVivaInputValidator, 
     * or useCustomInputValidator -- is currently selected (these buttons are in a ButtonGroup so only one 
     * can be selected at any given time; the selected button defines the "current Input Validator type").
     * The current Input Validator type can also be set when this InputValidatorPane is initialized.
     * 
     * @param currentlySelectedInputValidatorType the currentlySelectedInputValidatorType to set
     */
    protected void setCurrentInputValidatorType(INPUT_VALIDATOR_TYPE currentInputValidatorType) {
        this.currentlySelectedInputValidatorType = currentInputValidatorType;
    }
    

    /**
     * Returns the {@link InputValidationResult}s for the most recent execution of a Custom Input Validator.
     * 
     * @return an array of InputValidationResults from the most recent Custom Input Validator execution.
     */
    protected InputValidationResult[] getCustomInputValidatorResults() {
        return customInputValidatorResults;
    }


    /**
     * Clears the "accumulating results", discarding any previously accumulated results.
     */
    protected void resetCustomInputValidatorAccumulatingResults() {
        this.accumulatingCustomResults = null ;
    }

    /**
     * Updates the Results JTable in the {@link InputValidationResultPane} to display the specified InputValidationResults.
     * 
     * @param inputValidationResults the results to display in the InputValidationResultPane's JTable.
     */
    protected void updateResultsTable(InputValidationResult[] inputValidationResults) {
        getResultsTableModel().setResults(inputValidationResults);
    }

    /**
     * This method verifies that an Input Validator is currently selected and that all necessary configuration
     * required to run that Input Validator is complete; if so, it runs the currently selected Input Validator.
     * 
     */
    protected void runCurrentlySelectedInputValidator() {
        if (okToRunInputValidator()) {
            INPUT_VALIDATOR_TYPE currentIV = getCurrentInputValidatorType();
            switch (currentIV) {
                case VIVA:
                    getLog().info("Running VIVA Input Validator");
                    runVivaInputValidator();
                    break;
                case CUSTOM:
                    getLog().info("Running Custom Input Validator");
                    runCustomInputValidator();
                    break;
                case NONE:
                    String msg = "Internal error: 'oktoRunInputValidator()' returned true but no Input Validator is selected.";
                    msg += "\nPlease report this error to the PC2 Development Team (pc2@ecs.csus.edu)";
                    msg += "\nSee logs for additional information";
                    System.err.println(msg);
                    getLog().severe("'okToRunInputValidator()' returned true but no Input Validator is selected.");
                    JOptionPane.showMessageDialog(null, msg, "Internal Error", JOptionPane.ERROR_MESSAGE);
                    break;
                default:
                    String msg2 = "Internal error: current Input Validator is not an element of INPUT_VALIDATOR_TYPE enum.";
                    msg2 += "\nPlease report this error to the PC2 Development Team (pc2@ecs.csus.edu)";
                    msg2 += "\nSee logs for additional information";
                    System.err.println(msg2);
                    getLog().severe("Error: current Input Validator Type is not an element of INPUT_VALIDATOR_TYPE enum.");
                    JOptionPane.showMessageDialog(null, msg2, "Internal Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } else {
            String msg = "Unable to run Input Validator; no properly configured Input Validator is selected.";
            getLog().warning(msg);
            JOptionPane.showMessageDialog(null, msg, "Cannot run Input Validator", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * @return the field defining the mostRecentlyRunInputValidatorType
     */
    protected INPUT_VALIDATOR_TYPE getMostRecentlyRunInputValidatorType() {
        return mostRecentlyRunInputValidatorType;
    }

}
