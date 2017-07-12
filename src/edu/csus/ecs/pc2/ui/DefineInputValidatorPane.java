package edu.csus.ecs.pc2.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * This class defines a JPanel containing textfield components for entering the file name of an Input Validator Program
 * and the command used to execute the Input Validator program.
 * 
 * @author John
 *
 */
public class DefineInputValidatorPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;
    
    private JLabel inputValidatorProgramNameLabel;
    
    private JTextField inputValidatorProgramNameTextField;
    
    private JLabel lblInputValidatorInvocation;
    
    private JTextField inputValidatorCommandTextField;
    
    private JButton chooseInputValidatorProgramButton;

    private JPanePlugin parentPane;

    private String lastDirectory;
    
    private SerializedFile inputValidatorFile;
    
    public DefineInputValidatorPane() {

        this.setBorder(new TitledBorder(null, "Define Input Validator", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        GridBagLayout gbl_defineInputValidatorPanel = new GridBagLayout();
        gbl_defineInputValidatorPanel.columnWidths = new int[] {20, 60, 20};
        gbl_defineInputValidatorPanel.rowHeights = new int[]{35, 35};
        gbl_defineInputValidatorPanel.columnWeights = new double[]{0.0, 0.0, 0.0};
        gbl_defineInputValidatorPanel.rowWeights = new double[]{0.0, 1.0};
        this.setLayout(gbl_defineInputValidatorPanel);
        
        GridBagConstraints gbc_inputValidatorProgramNameLabel = new GridBagConstraints();
        gbc_inputValidatorProgramNameLabel.anchor = GridBagConstraints.EAST;
        gbc_inputValidatorProgramNameLabel.insets = new Insets(0, 20, 5, 0);
        gbc_inputValidatorProgramNameLabel.gridx = 0;
        gbc_inputValidatorProgramNameLabel.gridy = 0;
        this.add(getInputValidatorProgramNameLabel(), gbc_inputValidatorProgramNameLabel);
        
        GridBagConstraints gbc_inputValidatorProgramNameTextField = new GridBagConstraints();
        gbc_inputValidatorProgramNameTextField.anchor = GridBagConstraints.WEST;
        gbc_inputValidatorProgramNameTextField.insets = new Insets(0, 0, 5, 5);
        gbc_inputValidatorProgramNameTextField.gridx = 1;
        gbc_inputValidatorProgramNameTextField.gridy = 0;
        this.add(getInputValidatorProgramNameTextField(), gbc_inputValidatorProgramNameTextField);
        
        GridBagConstraints gbc_chooseInputValidatorProgramButton = new GridBagConstraints();
        gbc_chooseInputValidatorProgramButton.anchor = GridBagConstraints.WEST;
        gbc_chooseInputValidatorProgramButton.insets = new Insets(0, 0, 0, 5);
        gbc_chooseInputValidatorProgramButton.gridx = 2;
        gbc_chooseInputValidatorProgramButton.gridy = 0;
        this.add(getChooseInputValidatorProgramButton(), gbc_chooseInputValidatorProgramButton);
        
        GridBagConstraints gbc_lblInputValidatorInvocation = new GridBagConstraints();
        gbc_lblInputValidatorInvocation.anchor = GridBagConstraints.EAST;
        gbc_lblInputValidatorInvocation.insets = new Insets(0, 20, 5, 0);
        gbc_lblInputValidatorInvocation.gridx = 0;
        gbc_lblInputValidatorInvocation.gridy = 1;
        this.add(getLblInputValidatorInvocation(), gbc_lblInputValidatorInvocation);
        
        GridBagConstraints gbc_inputValidatorCommandTextField = new GridBagConstraints();
        gbc_inputValidatorCommandTextField.insets = new Insets(0, 0, 5, 5);
        gbc_inputValidatorCommandTextField.anchor = GridBagConstraints.WEST;
        gbc_inputValidatorCommandTextField.gridx = 1;
        gbc_inputValidatorCommandTextField.gridy = 1;
        this.add(getInputValidatorCommandTextField(), gbc_inputValidatorCommandTextField);

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
            inputValidatorProgramNameLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            inputValidatorProgramNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
            inputValidatorProgramNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            inputValidatorProgramNameLabel.setToolTipText("The name, including the full path to it, of the program to be used to validate input data");
        }
        return inputValidatorProgramNameLabel;
    }
    
    private JTextField getInputValidatorProgramNameTextField() {
        if (inputValidatorProgramNameTextField == null) {
            inputValidatorProgramNameTextField = new JTextField();
            inputValidatorProgramNameTextField.setPreferredSize(new Dimension(300, 25));
            inputValidatorProgramNameTextField.setMinimumSize(new Dimension(300, 25));
            inputValidatorProgramNameTextField.setColumns(50);
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
            lblInputValidatorInvocation = new JLabel("Input Validator command:");
            lblInputValidatorInvocation.setAlignmentX(Component.RIGHT_ALIGNMENT);
            lblInputValidatorInvocation.setToolTipText("The command to be used to invoke the Input Validator");
            lblInputValidatorInvocation.setFont(new Font("Tahoma", Font.PLAIN, 11));
        }
        return lblInputValidatorInvocation;
    }
    
    private JTextField getInputValidatorCommandTextField() {
        if (inputValidatorCommandTextField == null) {
            inputValidatorCommandTextField = new JTextField();
            inputValidatorCommandTextField.setPreferredSize(new Dimension(300, 25));
            inputValidatorCommandTextField.setMinimumSize(new Dimension(300, 25));
            inputValidatorCommandTextField.setColumns(50);
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
    
    private JButton getChooseInputValidatorProgramButton() {
        
        if (chooseInputValidatorProgramButton == null) {
            
            chooseInputValidatorProgramButton = new JButton("Choose...");
            
            chooseInputValidatorProgramButton.addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent e) {
                    if (selectFile(getInputValidatorProgramNameTextField(), "Select Input Validator")) {
                        String fullFilePath = getInputValidatorProgramNameTextField().getText();
                        try {
                            getInputValidatorProgramNameTextField().setToolTipText((fullFilePath));
                            if (getInputValidatorProgramNameTextField().getText() != null && getInputValidatorProgramNameTextField().getText().endsWith(".class")) {
                                getInputValidatorCommandTextField().setText("java {:basename}");
                            }
                            enableUpdateButton();
                        } catch (Exception e2) {
                            showMessage(null, "Unable to load Input Validator file: " + fullFilePath, "Error loading " + fullFilePath + " " + e2.getMessage());
                            e2.printStackTrace();
                        }
                        inputValidatorFile = new SerializedFile(fullFilePath);

                    }
                }
            });
        }
        return chooseInputValidatorProgramButton;
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
            System.err.println ("No parent pane in DefineInputValidatorPane; cannot enable Add/Update button");
            getController().getLog().warning("No parent pane in DefineInputValidatorPane; cannot enable Add/Update button");
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

        return "Define Input Validator Pane";
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


}
