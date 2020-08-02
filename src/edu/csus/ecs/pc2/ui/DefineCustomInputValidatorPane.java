// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.model.Problem.InputValidationStatus;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * This class defines a JPanel containing textfield components for entering the file name of an Input Validator Program
 * and the command used to execute the Input Validator program.
 * The class also holds a field containing the {@link SerializedFile} comprising the program code for the Custom Input
 * Validator.  
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 *
 */
public class DefineCustomInputValidatorPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;
    
    private JLabel inputValidatorProgramNameLabel;
    
    private JTextField inputValidatorProgramNameTextField;
    
    private JLabel lblInputValidatorInvocation;
    
    private JTextField inputValidatorCommandTextField;

    private JPanePlugin parentPane;
    
    private SerializedFile inputValidatorFile;
    
    public DefineCustomInputValidatorPane() {
        setAlignmentX(Component.LEFT_ALIGNMENT);
//        setBorder(new LineBorder(new Color(0, 0, 0)));
        setPreferredSize(new Dimension(400, 100));
        
        GridBagLayout gbl_defineInputValidatorPanel = new GridBagLayout();
        gbl_defineInputValidatorPanel.columnWidths = new int[] {30, 30};
        gbl_defineInputValidatorPanel.rowHeights = new int[]{35, 35};
        gbl_defineInputValidatorPanel.columnWeights = new double[]{0.0, 0.0};
        gbl_defineInputValidatorPanel.rowWeights = new double[]{0.0, 1.0};
        this.setLayout(gbl_defineInputValidatorPanel);
        
        this.add(getInputValidatorProgramNameLabel(), createGbc(0, 0));
        this.add(getCustomInputValidatorProgramNameTextField(), createGbc(1, 0));
        this.add(getLblInputValidatorInvocation(), createGbc(0, 1));
        this.add(getCustomInputValidatorCommandTextField(), createGbc(1, 1));

    }
    
    @Override
    public void setEnabled (boolean enableComponents) {
        super.setEnabled(enableComponents);
        getInputValidatorProgramNameLabel().setEnabled(enableComponents);
        getCustomInputValidatorProgramNameTextField().setEnabled(enableComponents);
        getLblInputValidatorInvocation().setEnabled(enableComponents);
        getCustomInputValidatorCommandTextField().setEnabled(enableComponents);
    }
    
    /**
     * Returns the program name of the Custom Input Validator program file associated with the problem defined by the 
     * {@link InputValidatorPane} GUI, or the empty string.
     * 
     * Note that if there is a Custom Input Validator {@link SerializedFile} associated with the problem, this method 
     * returns the base name of the program file (not including the path to the file).
     * 
     * @return a String containing the program name of the Custom Input Validator SerializedFile, or the empty string if
     *      there is no Custom Input Validator SerializedFile associated with the problem.
     * 
     * @see {@link #getCustomInputValidatorFile()}
     * @see {@link SerializedFile#getName()}
     */
    public String getCustomInputValidatorProgramName() {
        SerializedFile inputValidatorFile = getCustomInputValidatorFile();
        if (inputValidatorFile!=null) {
            return inputValidatorFile.getName();
        } else {
            return "";
        }

    }
    
    /**
     * Sets the Input Validator Program name displayed in this DefineInputValidatorPane to the specified text.
     * 
     * @param progname a String containing the Input Validator Program name.
     */
    public void setCustomInputValidatorProgramName(String progName) {
        getCustomInputValidatorProgramNameTextField().setText(progName);
    }
     
    /**
     * Returns the {@link SerializedFile} containing the Custom Input Validator program code associated with the current problem.
     * 
     * @return a SerializedFile containing the Custom Input Validator program code.
     */
    public SerializedFile getCustomInputValidatorFile() {
        return inputValidatorFile;
    }
    
   /**
    * Sets the {@link SerializedFile} containing the program code for the Custom Input Validator associated with the current problem.
    * If the specified file is not null, this method also updates the Custom Input Validator Program Name text field and the
    * corresponding text field ToolTip text to contain the file name and the file absolute path, respectively, given in the 
    * specified SerializedFile (or sets the Program Name text field to the empty string and the ToolTip text to null if the specified
    * SerializedFile is null).
    *  
    * @param inputValidatorFile a SerializedFile containing the Custom Input Validator program code.
    */
    public void setCustomInputValidatorFile(SerializedFile inputValidatorFile) {
        this.inputValidatorFile = inputValidatorFile;
        if (inputValidatorFile == null){
            getCustomInputValidatorProgramNameTextField().setText("");
            getCustomInputValidatorProgramNameTextField().setToolTipText(null);
        } else {
            getCustomInputValidatorProgramNameTextField().setText(inputValidatorFile.getName());
            getCustomInputValidatorProgramNameTextField().setToolTipText(inputValidatorFile.getAbsolutePath());
        }
    }
    
    /**
     * Returns the Custom Input Validator command (that is, the command to be used to invoke the Custom Input Validator)
     * displayed in the Custom Input Validator Command text field in the GUI.
     * 
     * @return a String containing the text in the Custom Input Validator Command text field.
     */
    public String getCustomInputValidatorCommand() {
        return getCustomInputValidatorCommandTextField().getText();
    }
    
    /**
     * Sets the Custom Input Validator Command displayed in this DefineCustomInputValidatorPane to the specified String.
     * 
     * @param command the Custom Input Validator Command to set.
     */
    public void setCustomInputValidatorCommand(String command) {
        getCustomInputValidatorCommandTextField().setText(command);
        
    }

    /**
     * Sets the ToolTip text for the Custom Input Validator Program Name text field displayed in this DefineInputValidatorPane.
     * The ToolTip text is normally used to hold the full path to the Custom Input Validator program (whereas the text field
     * itself normally holds just the "basename" -- the actual program file name without a path).
     * 
     * @param text the Custom Input Valdator Program Name ToolTip text to set
     */
    public void setCustomInputValidatorProgramNameToolTipText(String text) {
        getCustomInputValidatorProgramNameTextField().setToolTipText(text);
    }

    /**
     * Sets the ToolTip text for the Input Validator Command displayed in this DefineInputValidatorPane.
     * 
     * @param text the ToolTip text to set.
     */
    public void setCustomInputValidatorCommandToolTipText(String text) {
        getCustomInputValidatorCommandTextField().setToolTipText(text);
    }

    private JLabel getInputValidatorProgramNameLabel() {
        if (inputValidatorProgramNameLabel == null) {
            inputValidatorProgramNameLabel = new JLabel("Input Validator Program: ");
            inputValidatorProgramNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
            inputValidatorProgramNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            inputValidatorProgramNameLabel.setToolTipText("The name, including the full path to it, of the program to be used to validate input data");
        }
        return inputValidatorProgramNameLabel;
    }
    
    public JTextField getCustomInputValidatorProgramNameTextField() {
        if (inputValidatorProgramNameTextField == null) {
            inputValidatorProgramNameTextField = new JTextField();
            inputValidatorProgramNameTextField.setPreferredSize(new Dimension(200, 25));
            inputValidatorProgramNameTextField.setMinimumSize(new Dimension(200, 25));
            inputValidatorProgramNameTextField.setColumns(20);
            inputValidatorProgramNameTextField.setText("");
            inputValidatorProgramNameTextField.setToolTipText(null);
            inputValidatorProgramNameTextField.setEditable(false);  //program name is determined by the Input Validator SerializedFile
            
            inputValidatorProgramNameTextField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent event) {
                    //sincde the text field is created as "uneditable", this code should never be executed... but just in case:
                    JOptionPane.showMessageDialog(null, "Use the 'Choose Program...' button to change the Custom Input Validator program", "Choose Program", 
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                    //the following code is from when the program name text field was directly editable; it is now uneditable because
                    // the program name is determined by the Custom Input Validator SerializedFile
//                    enableUpdateButton();
//                    updateInputValidatorPaneComponents();
//                    //validator program name has changed; mark any previous execution invalid
//                    try {
//                        ((InputValidatorPane) getParentPane()).setCustomInputValidatorHasBeenRun(false);
//                        ((InputValidatorPane) getParentPane()).setCustomInputValidationStatus(InputValidationStatus.NOT_TESTED);
//                    } catch (ClassCastException e) {
//                        getController().getLog().severe("Parent of DefineCustomInputValidatorPane is not an InputValidatorPane; not supported: " + e); 
//                        JOptionPane.showMessageDialog(null, "Internal error: parent of DefineCustomInputValidatorPane is not an InputValidatorPane; "
//                                + "\nSee logs and please report this to the PC2 Development team (pc2@ecs.csus.edu)",
//                                                "Internal Error", JOptionPane.ERROR_MESSAGE); 
//                    }
                }
            });
        }
        return inputValidatorProgramNameTextField;
    }
    
    private JLabel getLblInputValidatorInvocation() {
        if (lblInputValidatorInvocation == null) {
            lblInputValidatorInvocation = new JLabel("Input Validator command: ");
            lblInputValidatorInvocation.setHorizontalAlignment(SwingConstants.RIGHT);
            lblInputValidatorInvocation.setToolTipText("The command to be used to invoke the Input Validator");
            lblInputValidatorInvocation.setFont(new Font("Tahoma", Font.PLAIN, 11));
        }
        return lblInputValidatorInvocation;
    }
    
    protected JTextField getCustomInputValidatorCommandTextField() {
        if (inputValidatorCommandTextField == null) {
            inputValidatorCommandTextField = new JTextField();
            inputValidatorCommandTextField.setPreferredSize(new Dimension(200, 25));
            inputValidatorCommandTextField.setMinimumSize(new Dimension(200, 25));
            inputValidatorCommandTextField.setColumns(20);
            inputValidatorCommandTextField.setText("");
            inputValidatorCommandTextField.setToolTipText(null);
            
            inputValidatorCommandTextField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent event) {
                    enableUpdateButton();
                    updateInputValidatorPaneComponents();
                    //command has changed; mark any previous execution invalid
                    try {
                        ((InputValidatorPane) getParentPane()).setCustomInputValidatorHasBeenRun(false);
                        ((InputValidatorPane) getParentPane()).setCustomInputValidationStatus(InputValidationStatus.NOT_TESTED);
                    } catch (ClassCastException e) {
                        getController().getLog().severe("Parent of DefineCustomInputValidatorPane is not an InputValidatorPane; not supported: " + e); 
                        JOptionPane.showMessageDialog(null, "Internal error: parent of DefineCustomInputValidatorPane is not an InputValidatorPane; "
                                + "\nSee logs and please report this to the PC2 Development team (pc2@ecs.csus.edu)",
                                                "Internal Error", JOptionPane.ERROR_MESSAGE); 
                    }
                }
            });
        }
        return inputValidatorCommandTextField;
    }
    

    /**
     * Calls enableUpdateButton() in the grandparent EditProblemPane if that pane exists.
     * What that method does is check the various GUI components to determine if anything about the
     * problem configuration has changed, and if so enables the Update button on the EditProblemPane, allowing
     * the user to save the problem changes.
     */
    private void enableUpdateButton() {
        JPanePlugin parent = getParentPane();
        if (parent != null && parent instanceof InputValidatorPane) {
            final JPanePlugin grandParent = ((InputValidatorPane)parent).getParentPane();
            if (grandParent != null && grandParent instanceof EditProblemPane) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run () {
                        ((EditProblemPane)grandParent).enableUpdateButton();
                    }
               });
            } else {
                System.err.println ("Internal error: grandparent of DefineCustomInputValidatorPane is not an EditProblemPane; cannot enable Add/Update button"
                        + "\nPlease report this problem to the PC2 Development Team (pc2@ecs.csus.edu");
                getController().getLog().severe("grandparent of DefineCustomInputValidatorPane is not an EditProblemPane");
            }
        } else {
            System.err.println ("Internal error: parent of DefineCustomInputValidatorPane is not an InputValidatorPane; cannot enable Add/Update button"
                    + "\nPlease report this problem to the PC2 Development Team (pc2@ecs.csus.edu");
            getController().getLog().severe("parent of DefineCustomInputValidatorPane is not an InputValidatorPane");
        }
    }
    
    /**
     * Calls {@link InputValidatorPane#updateInputValidatorPaneComponents()} in the parent {@link InputValidatorPane}
     * if that pane exists.  The effect of this is to update the enabled/disabled state of all GUI components on the 
     * InputValidatorPane according to the current GUI state.  (For example, reducing the text in the Input Validator Program
     * Name text field to 'empty' results in the "Run Custom Input Validator" button becoming disabled, since there is no
     * longer any defined program to be run.)
     * 
     */
    private void updateInputValidatorPaneComponents() {
        JPanePlugin parent = getParentPane();
        if (parent != null && parent instanceof InputValidatorPane) {
            
                SwingUtilities.invokeLater( new Runnable() {
                    public void run () {
                        ((InputValidatorPane)parent).updateInputValidatorPaneComponents();
                    }
               });
                
        } else {
            System.err.println ("Internal error: parent of DefineCustomInputValidatorPane is not an InputValidatorPane."
                    + "\nPlease report this error to the PC2 Development Team: pc2@ecs.csus.edu.  See logs for additional information.");
            getController().getLog().severe("Internal error: parent of DefineCustomInputValidatorPane is not an InputValidatorPane.");
        }
    }
    
       @Override
    public String getPluginTitle() {

        return "Define Custom Input Validator Pane";
    }

    public void setParentPane(JPanePlugin parentPane) {
        this.parentPane = parentPane;
    }
    
    public JPanePlugin getParentPane() {
        return this.parentPane;
    }

    //the following code is due to https://stackoverflow.com/users/522444/hovercraft-full-of-eels, 
    // from https://stackoverflow.com/questions/9851688/how-to-align-left-or-right-inside-gridbaglayout-cell
    private static final Insets WEST_INSETS = new Insets(5, 0, 5, 5);
    private static final Insets EAST_INSETS = new Insets(5, 5, 5, 0);

    private GridBagConstraints createGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        gbc.anchor = (x == 0) ? GridBagConstraints.WEST : GridBagConstraints.EAST;
        gbc.fill = (x == 0) ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;

        gbc.insets = (x == 0) ? WEST_INSETS : EAST_INSETS;
        gbc.weightx = (x == 0) ? 0.1 : 1.0;
        gbc.weighty = 1.0;
        return gbc;
     }

}
