package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import edu.csus.ecs.pc2.core.OSType;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Problem.SandboxType;

public class EditProblemSandboxPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;

    /**
     * The problem to which this Edit Problem Sandbox pane relates.
     */
    private Problem problem;
    
    /**
     * The EditProblemPane which is the parent holding this Sandbox pane.
     */
    private EditProblemPane parentPane;

    private JRadioButton useNoSandboxRadioButton;
    private JRadioButton usePC2SandboxRadioButton;
    private JRadioButton useCustomSandboxRadioButton;
    
    private JPanel noSandboxPanel;
    
    private JPanel pc2SandboxPanel;
    private JPanel pc2SandboxOptionButtonPanel;
    private JPanel pc2SandboxOptionsSubPanel;
    private JLabel pc2SandboxOptionMemLimitLabel;
    private JTextField pc2SandboxOptionMemLimitTextField;
    private JLabel lblWhatsThisPC2Sandbox; 
    
    private JPanel customSandboxPanel;
    private JPanel customSandboxOptionButtonPanel;
    private JPanel customSandboxOptionsSubPanel; 
    private JLabel customSandboxProgramNameLabel;    
    private JTextField customSandboxProgramNameTextField;
    private JButton chooseSandboxProgramButton;
    private JLabel customSandboxCommandLineLabel;    
    private JTextField customSandboxCommandLineTextField;   
    
    private ButtonGroup sandboxChoiceButtonGroup;
    
    private Component verticalStrut_1;
    private Component verticalStrut_2;
    private Component verticalStrut_3;
    private Component verticalStrut_4;
    private Component horizontalStrut_1;
    private Component horizontalStrut_2;

    /**
     * last directory where searched for files.
     */
    private String lastDirectory; 
    
    private SandboxType mostRecentlySelectedType ;
    
    //setting this to True will cause the Sandbox Pane GUI panels to show boundary borders... useful for layout debugging.
    private boolean debugShowPanelBorders = false;
    
    //setting this to True will override the prohibition on selecting a Sandbox when running on Windows.
    // Note that THIS IS FOR DEBUGGING PURPOSES; it does NOT imply any support for Windows sandboxing.
    private boolean debugAllowSandboxSelectionOnWindows = true;



    public EditProblemSandboxPane() {
        if (debugShowPanelBorders) {
            setBorder(new LineBorder(Color.RED));
        }
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setAlignmentY(Component.TOP_ALIGNMENT);
        this.setMaximumSize(new Dimension(500, 200));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(getVerticalStrut_1());
        this.add(getNoSandboxPanel());
        this.add(getVerticalStrut_2());
        this.add(getPC2SandboxPanel());
        this.add(getVerticalStrut_3());
        this.add(getCustomSandboxPanel());
        this.add(getVerticalStrut_4());
        this.getSandboxChoiceButtonGroup().setSelected(getUseNoSandboxRadioButton().getModel(), true);
        mostRecentlySelectedType = SandboxType.NONE;
        this.enableEditProblemSandboxTabComponents();
        
    }


    @Override
    public String getPluginTitle() {
        return "Edit Problem Sandbox Pane";
    }
    
    public void setProblem(Problem problem) {
        this.problem = problem;
        
        //activate the appropriate sandbox selection button (the buttons are in a group so selecting one will deselect the others)
        if (this.problem.getSandboxType() == SandboxType.NONE) {
            getUseNoSandboxRadioButton().setSelected(true);
            
        } else if (this.problem.getSandboxType() == SandboxType.PC2_INTERNAL_SANDBOX) {
            getUsePC2SandboxRadioButton().setSelected(true);
            getPC2SandboxOptionMemLimitTextbox().setText(Integer.toString(problem.getMemoryLimitMB()));
            
        } else if (this.problem.getSandboxType() == SandboxType.EXTERNAL_SANDBOX) {
            getLog().severe("Problem has 'Custom sandbox' selected -- should not be possible in this version of PC^2!");
            //default to "None"
            getUseNoSandboxRadioButton().setSelected(true);
        }
    }

    public void setParentPane(EditProblemPane editProblemPane) {
        parentPane = editProblemPane;
    }
    

    /**
     * Initializes the fields of this Sandbox Pane with the values from the specified {@link Problem}.
     * 
     * @param inProblem the Problem used to initialize the Sandbox Pane.
     * 
     */
    protected void initializeFields(Problem inProblem) {

        if (inProblem == null) {
            getLog().warning("EditProblemSandboxPane.initializeFields() called with null Problem; cannot initialize");
        } else {

            // fill in the sandbox text fields from the problem
            getPC2SandboxOptionMemLimitTextbox().setText(new Integer(inProblem.getMemoryLimitMB()).toString());
            getCustomSandboxCommandLineTextField().setText(inProblem.getSandboxCmdLine());
            getCustomSandboxExecutableProgramTextField().setText(inProblem.getSandboxProgramName());

            // enable the appropriate sandbox radio button and disable other subpanels
            SandboxType sandboxType = inProblem.getSandboxType();
            if (sandboxType == SandboxType.NONE) {
                getUseNoSandboxRadioButton().setSelected(true);
                setPanelEnabled(getPC2SandboxOptionsSubPanel(), false);
                setPanelEnabled(getCustomSandboxOptionsSubPanel(), false);
            } else if (sandboxType == SandboxType.PC2_INTERNAL_SANDBOX) {
                getUsePC2SandboxRadioButton().setSelected(true);
                setPanelEnabled(getPC2SandboxOptionsSubPanel(), true);
                setPanelEnabled(getCustomSandboxOptionsSubPanel(), false);
            } else if (sandboxType == SandboxType.EXTERNAL_SANDBOX) {
                getUseCustomSandboxRadioButton().setSelected(true);
                setPanelEnabled(getPC2SandboxOptionsSubPanel(), false);
                setPanelEnabled(getCustomSandboxOptionsSubPanel(), true);
            } else {
                getLog().warning("EditProblemSandboxPane.initializeFields() called with Problem containing unknown sandbox type '" 
                                    + sandboxType + "'; cannot initialize pane");
            }
        }
    }
    
    /**
     * Returns a singleton JPanel containing a "Use no sandbox" RadioButton.
     * @return {@link javax.swing.JPanel}
     */
    private JPanel getNoSandboxPanel() {
        if (noSandboxPanel == null) {
            noSandboxPanel = new JPanel();
            noSandboxPanel.setMaximumSize(new Dimension(500, 50));
            noSandboxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            //set borders without or without lines depending on debugShowPanelBorders
            Border emptyBorder = new EmptyBorder(5,30,5,5);             //this border provides the desired inset spacing
            Border lineBorder = new LineBorder(new Color(0,255,0));     //this border provides colored lines for debugging purposes
            Border finalBorder;
            if (debugShowPanelBorders) {
                finalBorder = BorderFactory.createCompoundBorder(emptyBorder, lineBorder);
            } else {
                finalBorder = emptyBorder;
            }
            noSandboxPanel.setBorder(finalBorder);
            
            FlowLayout flowLayout = (FlowLayout) noSandboxPanel.getLayout();
            flowLayout.setHgap(10);
            flowLayout.setAlignment(FlowLayout.LEFT);
            noSandboxPanel.add(getUseNoSandboxRadioButton());
        }
        return noSandboxPanel;
    }
    
    private JPanel getPC2SandboxPanel() {
        if (pc2SandboxPanel == null) {
            pc2SandboxPanel = new JPanel();
            
            //set borders without or without lines depending on debugShowPanelBorders
            Border emptyBorder = new EmptyBorder(5,30,5,5);             //this border provides the desired inset spacing
            Border lineBorder = new LineBorder(new Color(0,0,255));     //this border provides colored lines for debugging purposes
            Border finalBorder;
            if (debugShowPanelBorders) {
                finalBorder = BorderFactory.createCompoundBorder(emptyBorder, lineBorder);
            } else {
                finalBorder = emptyBorder;
            }
            pc2SandboxPanel.setBorder(finalBorder);

            pc2SandboxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            pc2SandboxPanel.setLayout(new BorderLayout(0, 0));
            pc2SandboxPanel.setMaximumSize(new Dimension(500, 120));
            pc2SandboxPanel.add(getPC2SandboxOptionButtonPanel(), BorderLayout.NORTH);
            pc2SandboxPanel.add(getHorizontalStrut_1(), BorderLayout.WEST);
            pc2SandboxPanel.add(getPC2SandboxOptionsSubPanel());
        }
        return pc2SandboxPanel;
    }
    
    private JPanel getPC2SandboxOptionButtonPanel() {
        if (pc2SandboxOptionButtonPanel == null) {
            pc2SandboxOptionButtonPanel = new JPanel();
            pc2SandboxOptionButtonPanel.setBorder(null);
            pc2SandboxOptionButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            FlowLayout fl_pc2SandboxOptionButtonPanel = new FlowLayout(FlowLayout.LEFT);
            fl_pc2SandboxOptionButtonPanel.setHgap(0);
            pc2SandboxOptionButtonPanel.setLayout(fl_pc2SandboxOptionButtonPanel);
            pc2SandboxOptionButtonPanel.add(getUsePC2SandboxRadioButton());
            pc2SandboxOptionButtonPanel.add(getLblWhatsThisPC2Sandbox());
        }
        return pc2SandboxOptionButtonPanel;
    }

 
    private JPanel getCustomSandboxPanel() {
        if (customSandboxPanel == null) {
            customSandboxPanel = new JPanel();

            //set borders without or without lines depending on debugShowPanelBorders
            Border emptyBorder = new EmptyBorder(5,30,5,5);             //this border provides the desired inset spacing
            Border lineBorder = new LineBorder(new Color(0,255,255));   //this border provides colored lines for debugging purposes
            Border finalBorder;
            if (debugShowPanelBorders) {
                finalBorder = BorderFactory.createCompoundBorder(emptyBorder, lineBorder);
            } else {
                finalBorder = emptyBorder;
            }
            customSandboxPanel.setBorder(finalBorder);

            customSandboxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            customSandboxPanel.setLayout(new BorderLayout(0, 0));
            customSandboxPanel.setMaximumSize(new Dimension(700, 200));
            customSandboxPanel.add(getCustomSandboxOptionButtonPanel(), BorderLayout.NORTH);
            customSandboxPanel.add(getHorizontalStrut_2(), BorderLayout.WEST);
            customSandboxPanel.add(getCustomSandboxOptionsSubPanel());
        }
        return customSandboxPanel;
    }

    private JPanel getCustomSandboxOptionButtonPanel() {
        if (customSandboxOptionButtonPanel == null) {
            customSandboxOptionButtonPanel = new JPanel();
            customSandboxOptionButtonPanel.setBorder(null);
            customSandboxOptionButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            FlowLayout fl_customSandboxOptionButtonPanel = new FlowLayout(FlowLayout.LEFT);
            fl_customSandboxOptionButtonPanel.setHgap(0);
            customSandboxOptionButtonPanel.setLayout(fl_customSandboxOptionButtonPanel);
            customSandboxOptionButtonPanel.add(getUseCustomSandboxRadioButton());
            customSandboxOptionButtonPanel.add(getLblWhatsThisCustomSandbox());
        }
        return customSandboxOptionButtonPanel;
    }

 

    /**
     * This method initializes the useNoSandbox RadioButton.
     * 
     * @return javax.swing.JRadioButton
     */
    protected JRadioButton getUseNoSandboxRadioButton() {
        if (useNoSandboxRadioButton == null) {
            useNoSandboxRadioButton = new JRadioButton();
            useNoSandboxRadioButton.setText("Do not use a Sandbox");
            useNoSandboxRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableEditProblemSandboxTabComponents();
                    enableUpdateButton();
                    mostRecentlySelectedType = SandboxType.NONE;
                }
            });
        }
        return useNoSandboxRadioButton;
    }


    /**
     * Notifies the EditProblemPane parent pane to enable its update button.
     */
    protected void enableUpdateButton() {
        if (getParentPane()!=null) {
            getParentPane().enableUpdateButton();
        } else {
            getLog().warning("EditProblemSandboxPane has null EditProblemPane parent!");
        }
    }
    
    protected void setPanelEnabled(JPanel panel, Boolean isEnabled) {

        panel.setEnabled(isEnabled);

        Component[] components = panel.getComponents();

        for (Component component : components) {
            if (component instanceof JPanel) {
                setPanelEnabled((JPanel) component, isEnabled);
            }
            component.setEnabled(isEnabled);
        }
    }
        



    /**
     * Temporary method for testing...
     */
    protected void updateCustomSandboxCommandLine() {
        System.out.println ("Unimplemented method 'updateCustomSandboxCommandLine() invoked...");
        JOptionPane.showMessageDialog(this, "Method 'updateCustomSandboxCommandLine()' invoked but not implemented");
        
    }


    protected JRadioButton getUsePC2SandboxRadioButton() {
        if (usePC2SandboxRadioButton == null) {
            usePC2SandboxRadioButton = new JRadioButton();
            usePC2SandboxRadioButton.setMargin(new Insets(2, 12, 2, 2));
            usePC2SandboxRadioButton.setText("Use PC^2 Sandbox (not available on Windows)");
            usePC2SandboxRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (debugAllowSandboxSelectionOnWindows || ! (Utilities.getOSType().equals(OSType.WINDOWS) ) ) {
                        enableEditProblemSandboxTabComponents();
                        enableUpdateButton();
                        mostRecentlySelectedType = SandboxType.PC2_INTERNAL_SANDBOX;
                    } else  {
                        JOptionPane.showMessageDialog(getParentPane(), notAvailableOnWindowsMsg, "Not Available on Windows", JOptionPane.WARNING_MESSAGE);
                        if (mostRecentlySelectedType == SandboxType.NONE) {
                            getUseNoSandboxRadioButton().setSelected(true);
                        } else if (mostRecentlySelectedType == SandboxType.PC2_INTERNAL_SANDBOX) {
                                getUsePC2SandboxRadioButton().setSelected(true);
                            }
                    }
                }
            });
        }
        return usePC2SandboxRadioButton;
    }
    
    private String notAvailableOnWindowsMsg = "It appears you are running on a Microsoft Windows platform; this option is not available on Windows. ";
    
    protected JRadioButton getUseCustomSandboxRadioButton() {
        if (useCustomSandboxRadioButton == null) {
            useCustomSandboxRadioButton = new JRadioButton();
            useCustomSandboxRadioButton.setMargin(new Insets(2, 12, 2, 2));
            useCustomSandboxRadioButton.setText("Use Custom (User-supplied) Sandbox");
            useCustomSandboxRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //custom sandbox option not available in this version
                    JOptionPane.showMessageDialog(getParentPane(), customSandboxNotAvailableMsg, "Option not available", 
                                                    JOptionPane.INFORMATION_MESSAGE);
                    if (mostRecentlySelectedType == SandboxType.NONE) {
                        getUseNoSandboxRadioButton().setSelected(true);
                    } else if (mostRecentlySelectedType == SandboxType.PC2_INTERNAL_SANDBOX) {
                            getUsePC2SandboxRadioButton().setSelected(true);
                        }
                    }
            });
        }
        return useCustomSandboxRadioButton;
    }
    
    private String customSandboxNotAvailableMsg = "Sorry; this option is not available in the current version of PC^2" ;
    
    protected void enableEditProblemSandboxTabComponents() {
        if (getUseNoSandboxRadioButton().isSelected()) {
            enableNoSandboxComponents(true);
            enablePC2SandboxComponents(false);
            enableCustomSandboxComponents(false);
        } else if (getUsePC2SandboxRadioButton().isSelected()) {
            enableNoSandboxComponents(false);
            enablePC2SandboxComponents(true);
            enableCustomSandboxComponents(false);
        } else if (getUseCustomSandboxRadioButton().isSelected()) {
            enableNoSandboxComponents(false);
            enablePC2SandboxComponents(false);
            enableCustomSandboxComponents(true);
        } else {
            // No sandbox button is selected !?!
            getLog().warning("No sandbox selection radio button is selected!?");
        }
    }


    /**
     * Enables or disables the subcomponents of the No Sandbox Options panel.
     * Does NOT affect the No Sandbox selection radio button.
     * Note: currently there are no subcomponents in the No Sandbox option, so this
     * method does nothing; it is provided for possible future expansion.
     * 
     * @param isEnabled whether the panel subcomponents should be enabled or disabled.
     */
    private void enableNoSandboxComponents(boolean isEnabled) {
        // there are no sub-components on the No Sandbox panel to enable/disable, and we don't want the 
        //  panel itself disabled (else we'll never be able to select the "No sandbox" option again)
        // setPanelEnabled(getNoSandboxPanel(), isEnabled);
    }

    /**
     * Enables or disables the subcomponents of the PC2 Sandbox Options panel.
     * Does NOT affect the PC2 Sandbox selection radio button.
     * 
     * @param isEnabled whether the panel subcomponents should be enabled or disabled.
     */
    private void enablePC2SandboxComponents(boolean isEnabled) {
        setPanelEnabled(getPC2SandboxOptionsSubPanel(), isEnabled);
    }

    /**
     * Enables or disables the subcomponents of the Custom Sandbox Options panel.
     * Does NOT affect the Custom Sandbox selection radio button.
     * 
     * @param isEnabled whether the panel subcomponents should be enabled or disabled.
     */
    private void enableCustomSandboxComponents(boolean isEnabled) {
        setPanelEnabled(getCustomSandboxOptionsSubPanel(), isEnabled);
    }


    protected JPanel getPC2SandboxOptionsSubPanel() {
        if (pc2SandboxOptionsSubPanel == null) {
            pc2SandboxOptionsSubPanel = new JPanel();
            pc2SandboxOptionsSubPanel
                    .setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "PC^2 Sandbox options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 255)));

            //the following commented-out section was the initial layout, which creates a grid (really, gridbag) layout for a tabular
            // arrangement of components in the PC2 Sandbox Options subPanel.  Since there is currently only a single row in the subpanel
            // (containing the memory limit elements), it was easier to replace this code with a simple FlowLayout (see below).
            // This code block was left here to make it easier for someone later to see how to add additional rows using GridBagLayout.
//            GridBagLayout gbl_pc2SandboxOptionsSubPanel = new GridBagLayout();
//            gbl_pc2SandboxOptionsSubPanel.columnWidths = new int[] { 100, 50, 100 };
//            gbl_pc2SandboxOptionsSubPanel.rowHeights = new int[] { 30 };
//            gbl_pc2SandboxOptionsSubPanel.columnWeights = new double[] { 0.0, 0.0, 0 };
//            gbl_pc2SandboxOptionsSubPanel.rowWeights = new double[] { 1.0 };
//            pc2SandboxOptionsSubPanel.setLayout(gbl_pc2SandboxOptionsSubPanel);
//
//            GridBagConstraints gbc_pc2SandboxOptionMemLimitLabel = new GridBagConstraints();
//            gbc_pc2SandboxOptionMemLimitLabel.insets = new Insets(15, 20, 5, 5);
//            gbc_pc2SandboxOptionMemLimitLabel.gridx = 0;
//            gbc_pc2SandboxOptionMemLimitLabel.gridy = 0;
//            pc2SandboxOptionsSubPanel.add(getPC2SandboxOptionMemLimitLabel(), gbc_pc2SandboxOptionMemLimitLabel);
//
//            GridBagConstraints gbc_pc2SandboxOptionMemLimitTextField = new GridBagConstraints();
//            gbc_pc2SandboxOptionMemLimitTextField.anchor = GridBagConstraints.WEST;
//            gbc_pc2SandboxOptionMemLimitTextField.fill = GridBagConstraints.VERTICAL;
//            gbc_pc2SandboxOptionMemLimitTextField.weightx = 1.0;
//            gbc_pc2SandboxOptionMemLimitTextField.insets = new Insets(15, 5, 5, 0);
//            gbc_pc2SandboxOptionMemLimitTextField.gridx = 1;
//            gbc_pc2SandboxOptionMemLimitTextField.gridy = 0;
//            pc2SandboxOptionsSubPanel.add(getPC2SandboxOptionMemLimitTextbox(), gbc_pc2SandboxOptionMemLimitTextField);
//            
//            GridBagConstraints gbc_pc2SandboxOptionMemLimitWhatsThisLabel = new GridBagConstraints();
//            gbc_pc2SandboxOptionMemLimitWhatsThisLabel.anchor = GridBagConstraints.WEST;
//            gbc_pc2SandboxOptionMemLimitWhatsThisLabel.fill = GridBagConstraints.VERTICAL;
//            gbc_pc2SandboxOptionMemLimitWhatsThisLabel.weightx = 1.0;
//            gbc_pc2SandboxOptionMemLimitWhatsThisLabel.insets = new Insets(15, 0, 5, 0);
//            gbc_pc2SandboxOptionMemLimitWhatsThisLabel.gridx = 2;
//            gbc_pc2SandboxOptionMemLimitWhatsThisLabel.gridy = 0;
//            pc2SandboxOptionsSubPanel.add(getLblWhatsThisPC2SandboxMemLimit(), gbc_pc2SandboxOptionMemLimitWhatsThisLabel);

            //use a simple FlowLayout when there's just a single row
            pc2SandboxOptionsSubPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            FlowLayout fl_pc2SandboxOptionsSubPanel = new FlowLayout(FlowLayout.LEFT);
            fl_pc2SandboxOptionsSubPanel.setHgap(10);
            pc2SandboxOptionsSubPanel.setLayout(fl_pc2SandboxOptionsSubPanel);

            pc2SandboxOptionsSubPanel.add(getPC2SandboxOptionMemLimitLabel());
            pc2SandboxOptionsSubPanel.add(getPC2SandboxOptionMemLimitTextbox());
            pc2SandboxOptionsSubPanel.add(getLblWhatsThisPC2SandboxMemLimit());

        }
        return pc2SandboxOptionsSubPanel;
    }

    protected JTextField getPC2SandboxOptionMemLimitTextbox() {
        if (pc2SandboxOptionMemLimitTextField == null) {
            pc2SandboxOptionMemLimitTextField = new JTextField(6);
            pc2SandboxOptionMemLimitTextField.setPreferredSize(new Dimension(100,25));
            
            pc2SandboxOptionMemLimitTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return pc2SandboxOptionMemLimitTextField;
    }


    private JLabel getPC2SandboxOptionMemLimitLabel() {
        if (pc2SandboxOptionMemLimitLabel == null) {
            pc2SandboxOptionMemLimitLabel = new JLabel("Memory limit (MB):");
            pc2SandboxOptionMemLimitLabel.setBorder(new EmptyBorder(10,25,5,5));
        }
        return pc2SandboxOptionMemLimitLabel;
    }


    protected JPanel getCustomSandboxOptionsSubPanel() {
        
        if (customSandboxOptionsSubPanel == null) {

            // define the Custom Sandbox subpanel
            customSandboxOptionsSubPanel = new JPanel();
            customSandboxOptionsSubPanel
                    .setBorder(new TitledBorder(null, "Custom Sandbox options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 255)));

            // define the (GridBag) layout for the Custom sandbox subpanel
            GridBagLayout gbl_customSandboxOptionsPanel = new GridBagLayout();
            gbl_customSandboxOptionsPanel.columnWidths = new int[] { 140, 150, 50 };
            gbl_customSandboxOptionsPanel.rowHeights = new int[] { 40, 40 };
            gbl_customSandboxOptionsPanel.columnWeights = new double[] { 0.0, 0.0, 0.0 };
            gbl_customSandboxOptionsPanel.rowWeights = new double[] { 0.0, 0.0 };
            customSandboxOptionsSubPanel.setLayout(gbl_customSandboxOptionsPanel);

            // add the Custom Sandbox Executable Program Label to the subpanel
            GridBagConstraints gbc_customSandboxProgramNameLabel = new GridBagConstraints();
            gbc_customSandboxProgramNameLabel.anchor = GridBagConstraints.EAST;
            gbc_customSandboxProgramNameLabel.insets = new Insets(15, 0, 5, 5);
            gbc_customSandboxProgramNameLabel.gridx = 0;
            gbc_customSandboxProgramNameLabel.gridy = 0;
            customSandboxOptionsSubPanel.add(getCustomSandboxExecutableProgramLabel(), gbc_customSandboxProgramNameLabel);

            // add the Custom Sandbox Executable Program TextField to the subpanel
            GridBagConstraints gbc_customSandboxProgramNameTextField = new GridBagConstraints();
            gbc_customSandboxProgramNameTextField.insets = new Insets(15, 0, 5, 5);
            gbc_customSandboxProgramNameTextField.fill = GridBagConstraints.HORIZONTAL;
            gbc_customSandboxProgramNameTextField.gridx = 1;
            gbc_customSandboxProgramNameTextField.gridy = 0;
            customSandboxOptionsSubPanel.add(getCustomSandboxExecutableProgramTextField(), gbc_customSandboxProgramNameTextField);

            // add the Choose Custom Sandbox Program button to the subpanel
            GridBagConstraints gbc_sandboxProgramButton = new GridBagConstraints();
            gbc_sandboxProgramButton.anchor = GridBagConstraints.NORTHWEST;
            gbc_sandboxProgramButton.insets = new Insets(15, 0, 5, 0);
            gbc_sandboxProgramButton.gridx = 2;
            gbc_sandboxProgramButton.gridy = 0;
            customSandboxOptionsSubPanel.add(getChooseCustomSandboxProgramButton(), gbc_sandboxProgramButton);

            // add the Sandbox Command Line label to the subpanel
            GridBagConstraints gbc_customSandboxCommandLineLabel = new GridBagConstraints();
            gbc_customSandboxCommandLineLabel.anchor = GridBagConstraints.EAST;
            gbc_customSandboxCommandLineLabel.insets = new Insets(15, 0, 15, 5);
            gbc_customSandboxCommandLineLabel.gridx = 0;
            gbc_customSandboxCommandLineLabel.gridy = 1;
            customSandboxOptionsSubPanel.add(getCustomSandboxCommandLabel(), gbc_customSandboxCommandLineLabel);

            // add the Custom Sandbox Command Textfield to the subpanel
            GridBagConstraints gbc_customSandboxCommandLineTextField = new GridBagConstraints();
            gbc_customSandboxCommandLineTextField.insets = new Insets(15, 0, 15, 5);
            gbc_customSandboxCommandLineTextField.fill = GridBagConstraints.HORIZONTAL;
            gbc_customSandboxCommandLineTextField.gridx = 1;
            gbc_customSandboxCommandLineTextField.gridy = 1;
            customSandboxOptionsSubPanel.add(getCustomSandboxCommandLineTextField(), gbc_customSandboxCommandLineTextField);

        }
        return customSandboxOptionsSubPanel;
    }


    private JLabel getCustomSandboxExecutableProgramLabel() {
        if (customSandboxProgramNameLabel == null) {
            customSandboxProgramNameLabel = new JLabel("Custom sandbox program:");
        }
        return customSandboxProgramNameLabel;
    }
   

    protected JTextField getCustomSandboxExecutableProgramTextField() {
        if (customSandboxProgramNameTextField == null) {
            customSandboxProgramNameTextField = new JTextField();
            customSandboxProgramNameTextField.setEnabled(false);
            customSandboxProgramNameTextField.setColumns(25);
            customSandboxProgramNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });

        }
        return customSandboxProgramNameTextField;
    }



    private JButton getChooseCustomSandboxProgramButton() {
        if (chooseSandboxProgramButton == null) {
            chooseSandboxProgramButton = new JButton();
            chooseSandboxProgramButton.setText("Choose...");
            chooseSandboxProgramButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectFile(getCustomSandboxExecutableProgramTextField(), "Select Sandbox Program")) {
                        getCustomSandboxExecutableProgramTextField().setToolTipText((getCustomSandboxExecutableProgramTextField().getText()));
                        enableUpdateButton();
                    }
                }
            });
        }
        return chooseSandboxProgramButton;
    }

    private JLabel getCustomSandboxCommandLabel() {
        if (customSandboxCommandLineLabel == null) {
            customSandboxCommandLineLabel = new JLabel("Custom Sandbox Command Line:");
        }
        return customSandboxCommandLineLabel;
    }


    protected JTextField getCustomSandboxCommandLineTextField() {
        if (customSandboxCommandLineTextField == null) {
            customSandboxCommandLineTextField = new JTextField();
            customSandboxCommandLineTextField.setEnabled(false);
            customSandboxCommandLineTextField.setMaximumSize(new Dimension(100, 20));
            customSandboxCommandLineTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    updateCustomSandboxCommandLine();
                    enableUpdateButton();
                }
            });
        }
        return customSandboxCommandLineTextField;
    }

    private ButtonGroup getSandboxChoiceButtonGroup() {
        if (sandboxChoiceButtonGroup == null) {
            sandboxChoiceButtonGroup = new ButtonGroup();
            sandboxChoiceButtonGroup.add(getUseNoSandboxRadioButton());
            sandboxChoiceButtonGroup.add(getUsePC2SandboxRadioButton());
            sandboxChoiceButtonGroup.add(getUseCustomSandboxRadioButton());
        }
        return sandboxChoiceButtonGroup;
    }

    /**
     * Generates and returns a JLabel that displays a Question Mark icon and when clicked displays
     * a "What's This" information panel about the PC2 Sandbox option.
     * 
     * @return a clickable JLabel
     */
    private JLabel getLblWhatsThisPC2Sandbox() {
        if (lblWhatsThisPC2Sandbox == null) {
            Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");
            if (questionIcon == null || !(questionIcon instanceof ImageIcon)) {
                // the current PLAF doesn't have an OptionPane.questionIcon that's an ImageIcon
                lblWhatsThisPC2Sandbox = new JLabel("<What's This?>");
                lblWhatsThisPC2Sandbox.setForeground(Color.blue);
            } else {
                Image image = ((ImageIcon) questionIcon).getImage();
                lblWhatsThisPC2Sandbox = new JLabel(new ImageIcon(getScaledImage(image, 20, 20)));
            }

            lblWhatsThisPC2Sandbox.setToolTipText("What's This? (click for additional information)");
            lblWhatsThisPC2Sandbox.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JOptionPane.showMessageDialog(null, whatsThisPC2SandboxMessage, "PC2 Sandbox", JOptionPane.INFORMATION_MESSAGE, null);
                }
            });
            lblWhatsThisPC2Sandbox.setBorder(new EmptyBorder(0, 15, 0, 0));
        }
        return lblWhatsThisPC2Sandbox;
    }

    /**
     * The message displayed when the "What's This" icon on the PC2 Sandbox option radio button panel is clicked.
     */
    private String whatsThisPC2SandboxMessage = "Selecting this option allows you to use the PC^2 \"Sandbox\" to encapsulate team submissions."

            + "\n\nNormally, PC^2 enforces the TIME LIMIT for a problem but does not enforce any MEMORY limit (other than"
            + "\n   the limits imposed by the language runtime and the underlying operating system and hardware)."
            + "\nSelecting the PC^2 Sandbox allows enforcing a memory limit for the problem (specified on this screen),"
            + "\n   while also enforcing the time limit (specified on the \"General\" tab)."

            + "\n\nNOTE: the PC2 Sandbox implementation currently only supports Unix-like platforms (Linux, MacOS, etc.);"
            + "\n    it is not available on Windows (we're working on it...)"

            ;

    private JLabel lblWhatsThisPC2MemLimit;
    /**
     * Generates and returns a JLabel that displays a Question Mark icon and when clicked displays
     * a "What's This" information panel about the PC2 Sandbox Memory Limit.
     * 
     * @return a clickable JLabel
     */
    private JLabel getLblWhatsThisPC2SandboxMemLimit() {
        if (lblWhatsThisPC2MemLimit == null) {
            Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");
            if (questionIcon == null || !(questionIcon instanceof ImageIcon)) {
                // the current PLAF doesn't have an OptionPane.questionIcon that's an ImageIcon
                lblWhatsThisPC2MemLimit = new JLabel("<What's This?>");
                lblWhatsThisPC2MemLimit.setForeground(Color.blue);
            } else {
                Image image = ((ImageIcon) questionIcon).getImage();
                lblWhatsThisPC2MemLimit = new JLabel(new ImageIcon(getScaledImage(image, 20, 20)));
            }

            lblWhatsThisPC2MemLimit.setToolTipText("What's This? (click for additional information)");
            lblWhatsThisPC2MemLimit.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JOptionPane.showMessageDialog(null, whatsThisPC2MemLimitMessage, "PC2 Sandbox Memory Limit", JOptionPane.INFORMATION_MESSAGE, null);
                }
            });
            lblWhatsThisPC2MemLimit.setBorder(new EmptyBorder(0, 15, 0, 0));
        }
        return lblWhatsThisPC2MemLimit;
    }

    /**
     * The message displayed when the "What's This" icon on the PC2 Sandbox Memory Limit textbox is clicked.
     */
    private String whatsThisPC2MemLimitMessage = "The value in this textbox specifies the memory limit (in MB) to be enforced for this problem by the PC2 sandbox."

            + "\n\nA value of zero indicates 'no limit' -- meaning the only limit is whatever is enforced by the"
            + "\n   underlying hardware, the OS platform, and the language-specific runtime system."
            ;
    
    
    private JLabel lblWhatsThisCustomSandbox;
    /**
     * Generates and returns a JLabel that displays a Question Mark icon and when clicked displays
     * a "What's This" information panel about the Custom Sandbox option.
     * 
     * @return a clickable JLabel
     */
    private JLabel getLblWhatsThisCustomSandbox() {
        if (lblWhatsThisCustomSandbox == null) {
            Icon questionIcon = UIManager.getIcon("OptionPane.questionIcon");
            if (questionIcon == null || !(questionIcon instanceof ImageIcon)) {
                // the current PLAF doesn't have an OptionPane.questionIcon that's an ImageIcon
                lblWhatsThisCustomSandbox = new JLabel("<What's This?>");
                lblWhatsThisCustomSandbox.setForeground(Color.blue);
            } else {
                Image image = ((ImageIcon) questionIcon).getImage();
                lblWhatsThisCustomSandbox = new JLabel(new ImageIcon(getScaledImage(image, 20, 20)));
            }

            lblWhatsThisCustomSandbox.setToolTipText("What's This? (click for additional information)");
            lblWhatsThisCustomSandbox.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JOptionPane.showMessageDialog(null, whatsThisCustomSandboxMessage, "Custom Sandbox", JOptionPane.INFORMATION_MESSAGE, null);
                }
            });
            lblWhatsThisCustomSandbox.setBorder(new EmptyBorder(0, 15, 0, 0));
        }
        return lblWhatsThisCustomSandbox;
    }

    /**
     * The message displayed when the "What's This" icon on the Custom Sandbox option radio button panel is clicked.
     */
    private String whatsThisCustomSandboxMessage = "A future version of PC^2 will support defining a user-writen \"Sandbox\" to encapsulate team submissions."

            + "\n\nThis option is not available in the current version (we're working on it...)"

            ;

    /**
     * Accepts an Image object and returns new version of that Image scaled to the specified width (w) and height (h).
     * @param srcImg the original image to be scaled.
     * @param w the desired width of the scaled (returned) image.
     * @param h the desired height of the scaled (returned) image.
     * @return an {@link Image} which is a scaled version of the input Image.
     */
    private Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    
    /**
     * Displays a file selection dialog; if a file is picked then the specified JTextField is updated with the file name.
     * 
     * @param textField
     *            -- a JTextField whose value will be updated if a file is chosen.
     * @param dialogTitle
     *            title for file chooser.
     * @return True if a file was select and the JTextField updated.
     * @throws Exception
     */
    private boolean selectFile(JTextField textField, String dialogTitle) {
        boolean result = false;
        // toolTip should always have the full path
        String oldFile = textField.getToolTipText();
        String startDir;
        if (oldFile == null || oldFile.equalsIgnoreCase("")) {
            startDir = lastDirectory;
        } else {
            startDir = oldFile;
        }
        JFileChooser chooser = new JFileChooser(startDir);
        if (dialogTitle != null) {
            chooser.setDialogTitle(dialogTitle);
        }
        try {
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                lastDirectory = chooser.getCurrentDirectory().toString();
                textField.setText(chooser.getSelectedFile().getCanonicalFile().toString());
                result = true;
            }
        } catch (Exception e) {
            showMessage("Error getting selected file, try again: \n" + e.getMessage());
            getLog().log(Log.INFO, "Error getting selected file: ", e);
            result = false;
        }
        chooser = null;
        return result;
    }
    
    
    private EditProblemPane getParentPane() {
        return this.parentPane;
    }
    
    /**
     * Returns the name of the last directory from which a file was selected/loaded
     * by a component of this EditProblemSandboxPane.
     * 
     * @return a String containing the last directory name
     */
    public String getLastDirectory() {
        return lastDirectory;
    }

    /**
     * Saves the name of the last directory from which a file was selected/loaded
     * by a component of this EditProblemSandboxPane.
     * 
     * @param directory the name of the last directory from which a file was selected/loaded.
     */
    public void setLastDirectory(String directory) {
        lastDirectory = directory;
        
    }

    public void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(null, message);
            }
        });
    }


     private Component getVerticalStrut_1() {
        if (verticalStrut_1 == null) {
            verticalStrut_1 = Box.createVerticalStrut(40);
        }
        return verticalStrut_1;
    }

    private Component getVerticalStrut_2() {
        if (verticalStrut_2 == null) {
            verticalStrut_2 = Box.createVerticalStrut(5);
        }
        return verticalStrut_2;
    }

    private Component getVerticalStrut_3() {
        if (verticalStrut_3 == null) {
            verticalStrut_3 = Box.createVerticalStrut(20);
        }
        return verticalStrut_3;
    }

    private Component getVerticalStrut_4() {
        if (verticalStrut_4 == null) {
            verticalStrut_4 = Box.createVerticalStrut(20);
        }
        return verticalStrut_4;
    }

    private Component getHorizontalStrut_1() {
        if (horizontalStrut_1 == null) {
            horizontalStrut_1 = Box.createHorizontalStrut(40);
        }
        return horizontalStrut_1;
    }

    private Component getHorizontalStrut_2() {
        if (horizontalStrut_2 == null) {
            horizontalStrut_2 = Box.createHorizontalStrut(40);
        }
        return horizontalStrut_2;
    }


    //main() method for testing only
    public static void main (String [] args) {
        JFrame frame = new JFrame();
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Sandbox", new EditProblemSandboxPane());
        frame.getContentPane().add(tabbedPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 900);
        frame.pack();
        frame.setVisible(true);
    }

}
