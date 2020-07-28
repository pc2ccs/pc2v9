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

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * This class defines a JPanel containing textfield components for entering the file name of an Input Validator Program
 * and the command used to execute the Input Validator program.
 * 
 * @author John
 *
 */
public class DefineCustomInputValidatorPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;
    
    private JLabel inputValidatorProgramNameLabel;
    
    private JTextField inputValidatorProgramNameTextField;
    
    private JLabel lblInputValidatorInvocation;
    
    private JTextField inputValidatorCommandTextField;

    private JPanePlugin parentPane;

    private String lastDirectory;
    
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
        this.add(getInputValidatorProgramNameTextField(), createGbc(1, 0));
        this.add(getLblInputValidatorInvocation(), createGbc(0, 1));
        this.add(getInputValidatorCommandTextField(), createGbc(1, 1));

    }
    
    @Override
    public void setEnabled (boolean enableComponents) {
        super.setEnabled(enableComponents);
        getInputValidatorProgramNameLabel().setEnabled(enableComponents);
        getInputValidatorProgramNameTextField().setEnabled(enableComponents);
        getLblInputValidatorInvocation().setEnabled(enableComponents);
        getInputValidatorCommandTextField().setEnabled(enableComponents);
    }
    
    /**
     * Returns the Input Validator Program name contained in this DefineInputValidator panel.
     * 
     * @return a String containing the Input Validator Name, or null or the empty string if no Input Validator Program has been defined
     * @deprecated  Use {@link #getInputValidatorFile()}
     */
    public String getInputValidatorProgramName() {
        return getInputValidatorProgramNameTextField().getText();
    }
    
    public SerializedFile getInputValidatorFile() {
        return inputValidatorFile;
    }
    
    /**
     * Sets the Input Validator Program name displayed in this DefineInputValidatorPane to the specified text.
     * Also sets the tooltip for the display textbox to match the specified Input Validator Program name.
     * 
     * @param progname a String containing the Input Validator Program name
     */
    public void setInputValidatorProgramName(String progName) {
        getInputValidatorProgramNameTextField().setText(progName);
        getInputValidatorProgramNameTextField().setToolTipText(progName);
    }
    
    public void setInputValidatorFile(SerializedFile inputValidatorFile) {
        this.inputValidatorFile = inputValidatorFile;
        if (inputValidatorFile == null){
            getInputValidatorProgramNameTextField().setText("");
            getInputValidatorProgramNameTextField().setToolTipText(null);
        } else {
            getInputValidatorProgramNameTextField().setText(inputValidatorFile.getName());
            getInputValidatorProgramNameTextField().setToolTipText(inputValidatorFile.getAbsolutePath());
        }
    }
    
    public String getInputValidatorCommand() {
        return getInputValidatorCommandTextField().getText();
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
    
    private JTextField getInputValidatorProgramNameTextField() {
        if (inputValidatorProgramNameTextField == null) {
            inputValidatorProgramNameTextField = new JTextField();
            inputValidatorProgramNameTextField.setPreferredSize(new Dimension(200, 25));
            inputValidatorProgramNameTextField.setMinimumSize(new Dimension(200, 25));
            inputValidatorProgramNameTextField.setColumns(20);
            inputValidatorProgramNameTextField.setText("");
            inputValidatorProgramNameTextField.setToolTipText(null);
            inputValidatorProgramNameTextField.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent event) {
                    enableUpdateButton();
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
    
    private JTextField getInputValidatorCommandTextField() {
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
                }
            });
        }
        return inputValidatorCommandTextField;
    }
    

    /**
     * Calls enableUpdateButton() in the grandparent EditProblemPane if that pane exists.
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
                System.err.println ("No grandparent pane (EditProblemPane) accessible from DefineInputValidatorPane; cannot enable Add/Update button");
                getController().getLog().warning("No grandparent pane (EditProblemPane) accessible from  DefineInputValidatorPane; cannot enable Add/Update button");
            }
        } else {
            System.err.println ("No parent pane in DefineCustomInputValidatorPane; cannot enable Add/Update button");
            getController().getLog().warning("No parent pane in DefineCustomInputValidatorPane; cannot enable Add/Update button");
        }
    }
    
   
    /**
     * Displays a FileChooser dialog for selecting a file. If a file is actually selected then the method updates 
     * the specified JTextField.
     * 
     * @param textField -- a JTextField whose value will be updated if a file is chosen
     * @param dialogTitle
     *            title for file chooser
     * @return True if a file was select and the JTextField updated
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

    public void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(null, message);
            }
        });
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

    public void setInputValidatorProgramNameToolTipText(String text) {
        getInputValidatorCommandTextField().setToolTipText(text);
    }

    /**
     * Sets the Input Validator Command displayed in this DefineInputValidator panel to the specified String.
     * 
     * @param command the Input Validator Command to set
     */
    public void setInputValidatorCommand(String command) {
        getInputValidatorCommandTextField().setText(command);
        
    }

    /**
     * Sets the ToolTip text for the Input Validator Command displayed in this DefineInputValidatorPane.
     * 
     * @param text the ToolTip text to set
     */
    public void setInputValidatorCommandToolTipText(String text) {
        getInputValidatorCommandTextField().setToolTipText(text);
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
