package edu.csus.ecs.pc2.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

/**
 * This class defines a JPanel containing a set of Radio Buttons for choosing a set of Input Data source files
 * and a button to run an Input Validator on that set of files.
 * 
 * @author John
 *
 */
public class ExecuteInputValidatorPane extends JPanePlugin {

    private static final long serialVersionUID = 1L;
    private JPanel inputValidatorDataFilesPanel;
    private JRadioButton filesPreviouslyLoadedRadioButton;
    
    private final ButtonGroup inputFileLocationButtonGroup = new ButtonGroup();
    private JButton validateInputDataButton;
    private JButton chooseInputFilesButton;
    private JTextField inputValidatorFilesOnDiskTextField;
    private JRadioButton filesJustLoadedRadioButton;
    private JRadioButton filesOnDiskInFolderRadioButton;

    private Component horizontalStrut_1;
    private Component horizontalStrut_2;

    public ExecuteInputValidatorPane() {
        this.setBorder(new TitledBorder(null, "Execute Input Validator", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(getHorizontalStrut_1());
        this.add(getRunInputValidatorButton());
        this.add(getHorizontalStrut_2());
        this.add(getInputValidatorDataFilesPanel());
    }
    
    private JPanel getInputValidatorDataFilesPanel() {
        if (inputValidatorDataFilesPanel == null) {
            
            inputValidatorDataFilesPanel = new JPanel();
            inputValidatorDataFilesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            inputValidatorDataFilesPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Input Data Files to Validate:", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
            GridBagLayout gbl_inputValidatorDataFilesPanel = new GridBagLayout();
            gbl_inputValidatorDataFilesPanel.columnWidths = new int[] {50, 200, 50};
            gbl_inputValidatorDataFilesPanel.rowHeights = new int[] {25, 25, 25};
            gbl_inputValidatorDataFilesPanel.columnWeights = new double[]{0.0, 0.0, 0.0};
            gbl_inputValidatorDataFilesPanel.rowWeights = new double[]{0.0, 0.0, 0.0};
            inputValidatorDataFilesPanel.setLayout(gbl_inputValidatorDataFilesPanel);
            GridBagConstraints gbc_filesPreviouslyLoadedRadioButton = new GridBagConstraints();
            gbc_filesPreviouslyLoadedRadioButton.gridwidth = 2;
            gbc_filesPreviouslyLoadedRadioButton.anchor = GridBagConstraints.WEST;
            gbc_filesPreviouslyLoadedRadioButton.insets = new Insets(0, 0, 5, 5);
            gbc_filesPreviouslyLoadedRadioButton.gridx = 0;
            gbc_filesPreviouslyLoadedRadioButton.gridy = 0;
            inputValidatorDataFilesPanel.add(getFilesPreviouslyLoadedRadioButton(), gbc_filesPreviouslyLoadedRadioButton);
            GridBagConstraints gbc_filesJustLoadedRadioButton = new GridBagConstraints();
            gbc_filesJustLoadedRadioButton.gridwidth = 2;
            gbc_filesJustLoadedRadioButton.anchor = GridBagConstraints.WEST;
            gbc_filesJustLoadedRadioButton.insets = new Insets(0, 0, 5, 5);
            gbc_filesJustLoadedRadioButton.gridx = 0;
            gbc_filesJustLoadedRadioButton.gridy = 1;
            inputValidatorDataFilesPanel.add(getFilesJustLoadedRadioButton(), gbc_filesJustLoadedRadioButton);
            GridBagConstraints gbc_filesOnDiskInFolderRadioButton = new GridBagConstraints();
            gbc_filesOnDiskInFolderRadioButton.anchor = GridBagConstraints.WEST;
            gbc_filesOnDiskInFolderRadioButton.insets = new Insets(0, 0, 0, 5);
            gbc_filesOnDiskInFolderRadioButton.gridx = 0;
            gbc_filesOnDiskInFolderRadioButton.gridy = 2;
            inputValidatorDataFilesPanel.add(getFilesOnDiskInFolderRadioButton(), gbc_filesOnDiskInFolderRadioButton);
            GridBagConstraints gbc_inputValidatorFilesOnDiskTextField = new GridBagConstraints();
            gbc_inputValidatorFilesOnDiskTextField.anchor = GridBagConstraints.WEST;
            gbc_inputValidatorFilesOnDiskTextField.insets = new Insets(0, 0, 5, 5);
            gbc_inputValidatorFilesOnDiskTextField.gridx = 1;
            gbc_inputValidatorFilesOnDiskTextField.gridy = 2;
            inputValidatorDataFilesPanel.add(getInputValidatorFilesOnDiskTextField(), gbc_inputValidatorFilesOnDiskTextField);
            GridBagConstraints gbc_chooseInputFilesButton = new GridBagConstraints();
            gbc_chooseInputFilesButton.insets = new Insets(0, 0, 0, 5);
            gbc_chooseInputFilesButton.gridx = 2;
            gbc_chooseInputFilesButton.gridy = 2;
            inputValidatorDataFilesPanel.add(getChooseInputFilesButton(), gbc_chooseInputFilesButton);
        }
        return inputValidatorDataFilesPanel;
    }
    
    public JRadioButton getFilesPreviouslyLoadedRadioButton() {
        if (filesPreviouslyLoadedRadioButton == null) {
            filesPreviouslyLoadedRadioButton = new JRadioButton("Files previously loaded into PC2");
            filesPreviouslyLoadedRadioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
//     FIXME               enableUpdateButton();
//                    updateRunValidatorButtonState();
                }
            });
            inputFileLocationButtonGroup.add(filesPreviouslyLoadedRadioButton);
            filesPreviouslyLoadedRadioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        return filesPreviouslyLoadedRadioButton;
    }
    
    public JRadioButton getFilesJustLoadedRadioButton() {
        if (filesJustLoadedRadioButton == null) {
            filesJustLoadedRadioButton = new JRadioButton("Files just loaded via \"Input Data Files\" pane");
            filesJustLoadedRadioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.err.println("ExecuteInputValidatorPane.getFilesJustLoadedRadioButton(): fixme");
//   FIXME                 enableUpdateButton();
//                    updateRunValidatorButtonState();
                }
            });
            inputFileLocationButtonGroup.add(filesJustLoadedRadioButton);
        }
        return filesJustLoadedRadioButton;
    }
    
    public JRadioButton getFilesOnDiskInFolderRadioButton() {
        if (filesOnDiskInFolderRadioButton == null) {
            filesOnDiskInFolderRadioButton = new JRadioButton("Files on disk in folder:");
            filesOnDiskInFolderRadioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.err.println("ExecuteInputValidatorPane.getFilesOnDiskInFolderRadioButton(): fixme");
//   FIXME                 enableUpdateButton();
//                    updateRunValidatorButtonState();
                }
            });
            inputFileLocationButtonGroup.add(filesOnDiskInFolderRadioButton);
        }
        return filesOnDiskInFolderRadioButton;
    }

    public JTextField getInputValidatorFilesOnDiskTextField() {
        if (inputValidatorFilesOnDiskTextField == null) {
            inputValidatorFilesOnDiskTextField = new JTextField();
            inputValidatorFilesOnDiskTextField.setMinimumSize(new Dimension(300, 25));
            inputValidatorFilesOnDiskTextField.setPreferredSize(new Dimension(300, 25));
            inputValidatorFilesOnDiskTextField.setColumns(50);
            
            inputValidatorFilesOnDiskTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    System.err.println("ExecuteInputValidatorPane.getInputValidatorFilesOnDiskTextField(): fixme");
//    FIXME                enableUpdateButton();
//                    updateRunValidatorButtonState();
                }
            });

        }
        return inputValidatorFilesOnDiskTextField;
    }
    
    /**
     * A button allowing the user to choose the directory from which Input Data Files to be validated
     * are to be loaded.
     * 
     * @return A JButton which displays a chooser dialog
     */
    private JButton getChooseInputFilesButton() {
        if (chooseInputFilesButton == null) {
            chooseInputFilesButton = new JButton("Choose...");
            chooseInputFilesButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.err.println("ExecuteInputValidatorPane.getChooseInputFilesButton(): fixme");
//    FIXME                String directory = selectDirectory(getInputValidatorFilesOnDiskTextField(),"Select Input File Folder");
//                    if (directory != null && !directory.equals("")) {
//                        getInputValidatorFilesOnDiskTextField().setText(directory);
//                        getInputValidatorFilesOnDiskTextField().setToolTipText(directory);
//                        enableUpdateButton();
//                        updateRunValidatorButtonState();
//                    }
//                }
                }
            });
        }
        return chooseInputFilesButton;
    }

    public JButton getRunInputValidatorButton() {
        if (validateInputDataButton == null) {
            validateInputDataButton = new JButton("Run Input Validator");
            validateInputDataButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.err.println("ExecuteInputValidatorPane.getRunInputValidatorButton(): fixme");
//  FIXME                  runInputDataValidationTest() ;
//                    enableUpdateButton();
                }
            });
        }
        return validateInputDataButton;
    }
    
    private Component getHorizontalStrut_1() {
        if (horizontalStrut_1 == null) {
            horizontalStrut_1 = Box.createHorizontalStrut(20);
            horizontalStrut_1.setPreferredSize(new Dimension(35, 0));
        }
        return horizontalStrut_1;
    }

    private Component getHorizontalStrut_2() {
        if (horizontalStrut_2 == null) {
            horizontalStrut_2 = Box.createHorizontalStrut(20);
            horizontalStrut_2.setPreferredSize(new Dimension(35, 0));
        }
        return horizontalStrut_2;
    }



    @Override
    public String getPluginTitle() {
        return "Execute Input Validator Pane";
    }
    
}
