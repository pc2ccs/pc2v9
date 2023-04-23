// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageAutoFill;

/**
 * Add/Edit Language Pane
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditLanguagePane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 6229906311932197623L;

    private JPanel buttonPane = null;

    private JButton addButton = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private JPanel mainPanel = null;

    private JLabel jLabel = null;

    private JLabel jLabel1 = null;

    private JLabel jLabel2 = null;

    private JTextField displayNameTextField = null;

    private JTextField compileCommandLineTextField = null;

    private JTextField executableFilenameTextField = null;

    private JLabel jLabel3 = null;

    private JTextField jTextField3 = null;

    private JLabel jLabel4 = null;

    private JTextField programExecutionCommandLineTextField = null;

    private JLabel jLabel5 = null;

    private JComboBox<String> autoPopulateLanguageComboBox = null;

    private static final String NO_CHANGE_TITLE = "No Change";

    private Language language = null;

    private boolean populatingGUI = true;

    private JCheckBox deleteLanguageCheckbox = null;

    private JCheckBox interpretedLanguageCheckBox = null;

    /**
     * Judge's command line.
     */
    private JTextField judgeCommandLineTextBox = null;

    private JLabel lblJudgeCmdLine = null;
    
    /**
     * Checkbox for judges's command line
     */
    private JCheckBox chckbxJudgesCommandLine = null;

    private JTextField idTextfield;

    /**
     * This method initializes
     * 
     */
    public EditLanguagePane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(517, 388));

        this.add(getMainPanel(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);

        loadComboBox();
    }

    private void loadComboBox() {

        getAutoPopulateLanguageComboBox().addItem(NO_CHANGE_TITLE);
        for (String languageName : LanguageAutoFill.getLanguageList()) {
            getAutoPopulateLanguageComboBox().addItem(languageName);
        }
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        addWindowCloserListener();
    }

    private void addWindowCloserListener() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (getParentFrame() != null) {
                    getParentFrame().addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            handleCancelButton();
                        }
                    });
                }
            }
        });
    }

    public String getPluginTitle() {
        return "Edit Language Pane";
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(15);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.add(getAddButton(), null);
            buttonPane.add(getUpdateButton(), null);
            buttonPane.add(getCancelButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes addButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add");
            addButton.setEnabled(false);
            addButton.setMnemonic(java.awt.event.KeyEvent.VK_A);
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addLanguage();
                }
            });
        }
        return addButton;
    }

    protected void addLanguage() {

        try {
            Language newLanguage = getLanguageFromFields(language);

            getController().addNewLanguage(newLanguage);

            cancelButton.setText("Close");
            addButton.setEnabled(false);
            updateButton.setEnabled(false);

            if (getParentFrame() != null) {
                getParentFrame().setVisible(false);
            }

        } catch (InvalidFieldValue e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Invalid Language field", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    private Language getLanguageFromFields(Language existingLanguage) {

        if (existingLanguage == null) {
            existingLanguage = new Language(displayNameTextField.getText());
        } else {
            existingLanguage.setDisplayName(displayNameTextField.getText());
        }

        existingLanguage.setCompileCommandLine(compileCommandLineTextField.getText());
        existingLanguage.setInterpreted(getInterpretedLanguageCheckBox().isSelected());
        existingLanguage.setExecutableIdentifierMask(getExecutableFilenameTextField().getText());
        existingLanguage.setProgramExecuteCommandLine(programExecutionCommandLineTextField.getText());
        existingLanguage.setActive(!getDeleteLanguageCheckbox().isSelected());
        existingLanguage.setID(getIDTextField().getText());

        existingLanguage.setJudgeProgramExecuteCommandLine(judgeCommandLineTextBox.getText());
        existingLanguage.setUsingJudgeProgramExecuteCommandLine(chckbxJudgesCommandLine.isSelected());

        checkForEmptyField(existingLanguage.getDisplayName(), "Enter a language name");
        checkForEmptyField(existingLanguage.getCompileCommandLine(), "Enter a compiler command line");
        checkForEmptyField(existingLanguage.getProgramExecuteCommandLine(), "Enter a execute command line");

        if (existingLanguage.isUsingJudgeProgramExecuteCommandLine()) {
            checkForEmptyField(existingLanguage.getJudgeProgramExecuteCommandLine(), "Judge execution command line");
        }

        return existingLanguage;
    }

    private JTextField getIDTextField() {
        if (idTextfield == null) {
            idTextfield = new JTextField();
            idTextfield.setBounds(new Rectangle(209, 310, 263, 20));
            idTextfield.setToolTipText("ID to use in Contest API");
            idTextfield.setName("idTextField");
            idTextfield.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });

        }
        return idTextfield;
    }

    /**
     * Check for empty value, if empty throw exception if value missing.
     * 
     * @param value
     * @param comment
     * @throws InvalidFieldValue
     */
    private void checkForEmptyField(String value, String comment) {
        if (value == null || value.trim().length() == 0) {
            throw new InvalidFieldValue(comment);
        }
    }

    /**
     * This method initializes updateButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdateButton() {
        if (updateButton == null) {
            updateButton = new JButton();
            updateButton.setText("Update");
            updateButton.setEnabled(false);
            updateButton.setMnemonic(java.awt.event.KeyEvent.VK_U);
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateLanguage();
                }
            });
        }
        return updateButton;
    }

    protected void updateLanguage() {

        try {
            Language newLanguage = getLanguageFromFields(language);

            getController().updateLanguage(newLanguage);

            cancelButton.setText("Close");
            addButton.setEnabled(false);
            updateButton.setEnabled(false);

            if (getParentFrame() != null) {
                getParentFrame().setVisible(false);
            }

        } catch (InvalidFieldValue e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Invalid Language field", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    /**
     * This method initializes cancelButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleCancelButton();
                }
            });
        }
        return cancelButton;
    }

    protected void handleCancelButton() {

        if (getAddButton().isEnabled() || getUpdateButton().isEnabled()) {

            // Something changed, are they sure ?

            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Language modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                if (getAddButton().isEnabled()) {
                    addLanguage();
                } else {
                    updateLanguage();
                }
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            } else if (result == JOptionPane.NO_OPTION) {
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            }
        } else {
            if (getParentFrame() != null) {
                getParentFrame().setVisible(false);
            }
        }
    }

    /**
     * This method initializes mainPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            jLabel5 = new JLabel();
            jLabel5.setBounds(new java.awt.Rectangle(67, 13, 129, 20));
            jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
            jLabel5.setText("Auto Populate with");
            jLabel5.setName("AutoPopulateLabel");
            jLabel4 = new JLabel();
            jLabel4.setBounds(new Rectangle(13, 246, 244, 20));
            jLabel4.setName("ProgramExLabel");
            jLabel4.setMaximumSize(new Dimension(2147483647, 2147483647));
            jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
            jLabel4.setText("Program Execution Command Line");
            jLabel4.setForeground(new Color(0, 0, 0));
            jLabel3 = new JLabel();
            jLabel3.setBounds(new Rectangle(14, 113, 182, 20));
            jLabel3.setName("ExeFilenameLabel");
            jLabel3.setMaximumSize(new Dimension(2147483647, 2147483647));
            jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
            jLabel3.setText("Executable Filename");
            jLabel3.setForeground(Color.black);
            jLabel2 = new JLabel();
            jLabel2.setBounds(new java.awt.Rectangle(0, 0, 0, 0));
            jLabel2.setName("SourceExtLabel");
            jLabel2.setForeground(Color.black);
            jLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
            jLabel2.setText("Source Extensions");
            jLabel2.setVisible(false);
            jLabel1 = new JLabel();
            jLabel1.setBounds(new java.awt.Rectangle(14, 46, 182, 20));
            jLabel1.setName("DisplayNameLabel");
            jLabel1.setMaximumSize(new Dimension(2147483647, 2147483647));
            jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
            jLabel1.setText("Display Name");
            jLabel1.setForeground(Color.black);
            jLabel = new JLabel();
            jLabel.setBounds(new java.awt.Rectangle(14, 79, 182, 20));
            jLabel.setName("CompileCmdLineLabel");
            jLabel.setMaximumSize(new Dimension(2147483647, 2147483647));
            jLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            jLabel.setText("Compile Cmd Line");
            jLabel.setForeground(Color.black);
            mainPanel = new JPanel();
            mainPanel.setLayout(null);
            mainPanel.setName("advancedEdit");
            mainPanel.add(jLabel, jLabel.getName());
            mainPanel.add(jLabel1, jLabel1.getName());
            mainPanel.add(jLabel2, jLabel2.getName());
            mainPanel.add(getDisplayNameTextField(), getDisplayNameTextField().getName());
            mainPanel.add(getCompileCommandLineTextField(), getCompileCommandLineTextField().getName());
            mainPanel.add(getExecutableFilenameTextField(), getExecutableFilenameTextField().getName());
            mainPanel.add(jLabel3, jLabel3.getName());
            mainPanel.add(getJTextField3(), getJTextField3().getName());
            mainPanel.add(jLabel4, jLabel4.getName());
            mainPanel.add(getProgramExecutionCommandLineTextField(), getProgramExecutionCommandLineTextField().getName());
            mainPanel.add(jLabel5, jLabel5.getName());
            mainPanel.add(getAutoPopulateLanguageComboBox(), getAutoPopulateLanguageComboBox().getName());
            mainPanel.add(getDeleteLanguageCheckbox(), null);
            mainPanel.add(getInterpretedLanguageCheckBox(), null);

            chckbxJudgesCommandLine = new JCheckBox();
            chckbxJudgesCommandLine.setToolTipText("Use Judge command line instead of Program Execution Command Line");
            chckbxJudgesCommandLine.setHorizontalAlignment(SwingConstants.RIGHT);
            chckbxJudgesCommandLine.setText("Use Judge specific execution command line");
            chckbxJudgesCommandLine.setBounds(18, 182, 290, 21);
            chckbxJudgesCommandLine.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                    enableJudgeCommandControls();
                }
            });

            mainPanel.add(chckbxJudgesCommandLine);

            lblJudgeCmdLine = new JLabel("Judge Execution Command Line");
            lblJudgeCmdLine.setName("JudgeExeLabel");
            lblJudgeCmdLine.setHorizontalAlignment(SwingConstants.RIGHT);
            lblJudgeCmdLine.setBounds(13, 208, 244, 20);
            mainPanel.add(lblJudgeCmdLine);
            
            judgeCommandLineTextBox = new JTextField();
            judgeCommandLineTextBox.setToolTipText("Execute command line for Judges");
            judgeCommandLineTextBox.setName("judgesProgramCommandLine");
            judgeCommandLineTextBox.setBounds(271, 208, 201, 20);
            judgeCommandLineTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });

            mainPanel.add(judgeCommandLineTextBox);

            JLabel lblIdcode = new JLabel("ID/Code");
            lblIdcode.setHorizontalAlignment(SwingConstants.TRAILING);
            lblIdcode.setBounds(67, 310, 129, 20);
            mainPanel.add(lblIdcode);
            mainPanel.add(getIDTextField(), getIDTextField().getName());
        }
        return mainPanel;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getDisplayNameTextField() {
        if (displayNameTextField == null) {
            displayNameTextField = new JTextField();
            displayNameTextField.setBounds(new java.awt.Rectangle(209, 46, 263, 20));
            displayNameTextField.setToolTipText("Name to display to users");
            displayNameTextField.setName("displayNameTextField");
            displayNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return displayNameTextField;
    }

    /**
     * This method initializes jTextField1
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getCompileCommandLineTextField() {
        if (compileCommandLineTextField == null) {
            compileCommandLineTextField = new JTextField();
            compileCommandLineTextField.setBounds(new java.awt.Rectangle(208, 79, 264, 20));
            compileCommandLineTextField.setToolTipText("Command Line for compiler");
            compileCommandLineTextField.setName("commandLineTextField");
            compileCommandLineTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });

        }
        return compileCommandLineTextField;
    }

    /**
     * This method initializes jTextField2
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getExecutableFilenameTextField() {
        if (executableFilenameTextField == null) {
            executableFilenameTextField = new JTextField();
            executableFilenameTextField.setBounds(new Rectangle(208, 113, 264, 20));
            executableFilenameTextField.setToolTipText("Form: exe");
            executableFilenameTextField.setName("programExeTextField");
            executableFilenameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });

        }
        return executableFilenameTextField;
    }

    /**
     * This method initializes jTextField3
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJTextField3() {
        if (jTextField3 == null) {
            jTextField3 = new JTextField();
            jTextField3.setBounds(new java.awt.Rectangle(0, 0, 0, 0));
            jTextField3.setName("sourceExtTextField");
            jTextField3.setToolTipText("Form: *.cpp;*.c");
            jTextField3.setVisible(false);
        }
        return jTextField3;
    }

    /**
     * This method initializes jTextField4
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getProgramExecutionCommandLineTextField() {
        if (programExecutionCommandLineTextField == null) {
            programExecutionCommandLineTextField = new JTextField();
            programExecutionCommandLineTextField.setBounds(new Rectangle(271, 246, 201, 20));
            programExecutionCommandLineTextField.setToolTipText("Form: exe");
            programExecutionCommandLineTextField.setName("programCommandLine");
            programExecutionCommandLineTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });

        }
        return programExecutionCommandLineTextField;
    }

    /**
     * This method initializes jComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox<String> getAutoPopulateLanguageComboBox() {
        if (autoPopulateLanguageComboBox == null) {
            autoPopulateLanguageComboBox = new JComboBox<String>();
            autoPopulateLanguageComboBox.setBounds(new java.awt.Rectangle(209, 12, 259, 23));
            autoPopulateLanguageComboBox.setName("LangPopulateComboBox");
            autoPopulateLanguageComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    autoFillFields();
                }
            });
        }
        return autoPopulateLanguageComboBox;
    }

    protected void autoFillFields() {

        String languageToFill = (String) getAutoPopulateLanguageComboBox().getSelectedItem();

        if (NO_CHANGE_TITLE.equals(languageToFill)) {
            return;
        }

        /**
         * From LanguageAutoFill
         * <li>Title for Language
         * <li>Compiler Command Line
         * <li>Executable Identifier Mask
         * <li>Execute command line
         */

        String[] values = LanguageAutoFill.getAutoFillValues(languageToFill);

        displayNameTextField.setText(values[4]);
        compileCommandLineTextField.setText(values[1]);
        executableFilenameTextField.setText(values[2]);
        programExecutionCommandLineTextField.setText(values[3]);

        judgeCommandLineTextBox.setText(values[3]);
        chckbxJudgesCommandLine.setSelected(false);
        idTextfield.setText(values[6]);
        boolean isScript = LanguageAutoFill.isInterpretedLanguage(languageToFill);

        getInterpretedLanguageCheckBox().setSelected(isScript);

        if (isScript) {
            executableFilenameTextField.setText("");
        }

        executableFilenameTextField.setEnabled(!isScript);

        deleteLanguageCheckbox.setSelected(false);

        enableUpdateButtons(true);
    }

    /**
     * Enable or disable Update button based on comparison of language to fields.
     * 
     */
    public void enableUpdateButton() {

        if (populatingGUI) {
            return;
        }

        boolean enableButton = false;

        if (language != null) {

            Language newLanguage = null;
            try {
                newLanguage = getLanguageFromFields(null);
            } catch (InvalidFieldValue e) {

                // SOMEDAY there has to be a better way to ignore this exception
                // without CheckStyle complaining.

                /**
                 * getLanguageFromFields throws an InvalidFieldValue but in this case we ignore it because we don't care, if there are invalid fields that is ok at this point in the code. If there are
                 * problems getting the language then clearly the edited and original are different.
                 */

                e.printStackTrace();
            }

            enableButton = !language.isSameAs(newLanguage);

        } else {
            if (getAddButton().isVisible()) {
                enableButton = true;
            }
        }

        enableUpdateButtons(enableButton);
    }

    /**
     * Enable judge command label and edit box if checkbox is selected, otherwise
     * the user can not edit the judge's command.
     */
    public void enableJudgeCommandControls()
    {
        boolean enableControls = chckbxJudgesCommandLine.isSelected();
        
        judgeCommandLineTextBox.setEnabled(enableControls);
        lblJudgeCmdLine.setEnabled(enableControls);
       
    }
    
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(final Language language) {

        this.language = language;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(language);
                enableUpdateButtons(false);
            }
        });
    }

    private void populateGUI(Language language2) {

        populatingGUI = true;

        if (language2 != null) {
            displayNameTextField.setText(language2.getDisplayName());
            compileCommandLineTextField.setText(language2.getCompileCommandLine());
            executableFilenameTextField.setText(language2.getExecutableIdentifierMask());
            programExecutionCommandLineTextField.setText(language2.getProgramExecuteCommandLine());

            getAutoPopulateLanguageComboBox().setSelectedIndex(0);
            getDeleteLanguageCheckbox().setSelected(!language2.isActive());
            getInterpretedLanguageCheckBox().setSelected(language2.isInterpreted());
            getExecutableFilenameTextField().setEnabled(!language2.isInterpreted());
            chckbxJudgesCommandLine.setSelected(language2.isUsingJudgeProgramExecuteCommandLine());
            judgeCommandLineTextBox.setText(language2.getJudgeProgramExecuteCommandLine());
            getIDTextField().setText(language2.getID());
            getAddButton().setVisible(false);
            getUpdateButton().setVisible(true);

        } else {
            displayNameTextField.setText("");
            compileCommandLineTextField.setText("");
            executableFilenameTextField.setText("");
            programExecutionCommandLineTextField.setText("");

            chckbxJudgesCommandLine.setSelected(false);
            judgeCommandLineTextBox.setText("");

            getDeleteLanguageCheckbox().setSelected(false);
            getInterpretedLanguageCheckBox().setSelected(false);
            getExecutableFilenameTextField().setEnabled(true);
            getAutoPopulateLanguageComboBox().setSelectedIndex(0);
            getIDTextField().setText("");

            getAddButton().setVisible(true);
            getUpdateButton().setVisible(false);
        }

        enableJudgeCommandControls();
        populatingGUI = false;
    }

    protected void enableUpdateButtons(boolean editedText) {
        if (editedText) {
            cancelButton.setText("Cancel");
        } else {
            cancelButton.setText("Close");
        }
        // only enable the visible one, we are either editing or adding not both
        if (getUpdateButton().isVisible()) {
            getUpdateButton().setEnabled(editedText);
        } else {
            getAddButton().setEnabled(editedText);
        }
    }

    /**
     * This method initializes deleteLanguageCheckbox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getDeleteLanguageCheckbox() {
        if (deleteLanguageCheckbox == null) {
            deleteLanguageCheckbox = new JCheckBox();
            deleteLanguageCheckbox.setBounds(new Rectangle(209, 273, 224, 21));
            deleteLanguageCheckbox.setText("Hide Language");
            deleteLanguageCheckbox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return deleteLanguageCheckbox;
    }

    /**
     * This method initializes interpretedLanguageCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getInterpretedLanguageCheckBox() {
        if (interpretedLanguageCheckBox == null) {
            interpretedLanguageCheckBox = new JCheckBox();
            interpretedLanguageCheckBox.setBounds(new Rectangle(209, 143, 275, 21));
            interpretedLanguageCheckBox.setText("Script or Interpreted Language");
            interpretedLanguageCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    toggleExecutable();
                }
            });
        }
        return interpretedLanguageCheckBox;
    }

    protected void toggleExecutable() {
        boolean intLang = getInterpretedLanguageCheckBox().isSelected();
        getExecutableFilenameTextField().setEnabled(!intLang);
        enableUpdateButton();
    }
} // @jve:decl-index=0:visual-constraint="10,10"
