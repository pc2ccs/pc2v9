package edu.csus.ecs.pc2.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

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
    
    public JTextField getInputValidatorProgramNameTextField() {
        if (inputValidatorProgramNameTextField == null) {
            inputValidatorProgramNameTextField = new JTextField();
            inputValidatorProgramNameTextField.setPreferredSize(new Dimension(300, 25));
            inputValidatorProgramNameTextField.setMinimumSize(new Dimension(300, 25));
            inputValidatorProgramNameTextField.setColumns(50);
            inputValidatorProgramNameTextField.setText("");
            inputValidatorProgramNameTextField.setToolTipText("");
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
    
    public JTextField getInputValidatorCommandTextField() {
        if (inputValidatorCommandTextField == null) {
            inputValidatorCommandTextField = new JTextField();
            inputValidatorCommandTextField.setPreferredSize(new Dimension(300, 25));
            inputValidatorCommandTextField.setMinimumSize(new Dimension(300, 25));
            inputValidatorCommandTextField.setColumns(50);
            inputValidatorCommandTextField.setText("");
            inputValidatorCommandTextField.setToolTipText("");
            
            inputValidatorCommandTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    System.err.println ("DefineInputValidatorPane.getInputValidatorCommandTextField(): fixme");
//  FIXME                  enableUpdateButton();
//  FIXME                 updateRunValidatorButtonState();
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
                    System.err.println ("DefineInputValidatorPane.getChooseInputValidatorProgramButton(): fixme");
//                    if (selectFile(getInputValidatorProgramNameTextField(), "Select Input Validator")) {
//                        getInputValidatorProgramNameTextField().setToolTipText((getInputValidatorProgramNameTextField().getText()));
//                        enableUpdateButton();
//                    }
                }
            });
        }
        return chooseInputValidatorProgramButton;
    }
    

    @Override
    public String getPluginTitle() {

        return "Define Input Validator Pane";
    }

}
