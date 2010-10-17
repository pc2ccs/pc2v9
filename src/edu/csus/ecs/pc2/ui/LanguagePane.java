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
public class LanguagePane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 6229906311932197623L;

    private JPanel buttonPane = null;

    private JButton addButton = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private JPanel jPanel = null;

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

    private JComboBox autoPopulateLanguageComboBox = null;

    private static final String NO_CHANGE_TITLE = "No Change";

    private Language language = null;

    private boolean populatingGUI = true;

    private JCheckBox deleteLanguageCheckbox = null;

    /**
     * This method initializes
     * 
     */
    public LanguagePane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(517, 251));

        this.add(getJPanel(), java.awt.BorderLayout.CENTER);
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
            Language newLanguage = getLanguageFromFields();

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

    private Language getLanguageFromFields() throws InvalidFieldValue {
        if (language == null) {
            language = new Language(displayNameTextField.getText());
        } else {
            language.setDisplayName(displayNameTextField.getText());
        }

        language.setCompileCommandLine(compileCommandLineTextField.getText());
        language.setExecutableIdentifierMask(getExecutableFilenameTextField().getText());
        language.setProgramExecuteCommandLine(programExecutionCommandLineTextField.getText());

        language.setActive(!getDeleteLanguageCheckbox().isSelected());
        
        checkForEmptyField (language.getDisplayName(), "Enter a language name");
        checkForEmptyField (language.getCompileCommandLine(), "Enter a compiler command line");
        checkForEmptyField (language.getProgramExecuteCommandLine(), "Enter a execute command line");

        return language;
    }
    
    /**
     * Check for empty value, if empty throw exception if value missing.
     * 
     * @param value
     * @param comment
     * @throws InvalidFieldValue
     */
    private void checkForEmptyField(String value, String comment)  throws InvalidFieldValue {
        if (value == null || value.trim().length() == 0){
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
            Language newLanguage = getLanguageFromFields();

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
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            jLabel5 = new JLabel();
            jLabel5.setBounds(new java.awt.Rectangle(67, 13, 129, 20));
            jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
            jLabel5.setText("Auto Populate with");
            jLabel5.setName("AutoPopulateLabel");
            jLabel4 = new JLabel();
            jLabel4.setBounds(new java.awt.Rectangle(13, 145, 244, 20));
            jLabel4.setName("ProgramExLabel");
            jLabel4.setMaximumSize(new Dimension(2147483647, 2147483647));
            jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
            jLabel4.setText("Program Execution Command Line");
            jLabel4.setForeground(new Color(0, 0, 0));
            jLabel3 = new JLabel();
            jLabel3.setBounds(new java.awt.Rectangle(14, 112, 182, 20));
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
            jPanel = new JPanel();
            jPanel.setLayout(null);
            jPanel.setName("advancedEdit");
            jPanel.add(jLabel, jLabel.getName());
            jPanel.add(jLabel1, jLabel1.getName());
            jPanel.add(jLabel2, jLabel2.getName());
            jPanel.add(getDisplayNameTextField(), getDisplayNameTextField().getName());
            jPanel.add(getCompileCommandLineTextField(), getCompileCommandLineTextField().getName());
            jPanel.add(getExecutableFilenameTextField(), getExecutableFilenameTextField().getName());
            jPanel.add(jLabel3, jLabel3.getName());
            jPanel.add(getJTextField3(), getJTextField3().getName());
            jPanel.add(jLabel4, jLabel4.getName());
            jPanel.add(getProgramExecutionCommandLineTextField(), getProgramExecutionCommandLineTextField().getName());
            jPanel.add(jLabel5, jLabel5.getName());
            jPanel.add(getAutoPopulateLanguageComboBox(), getAutoPopulateLanguageComboBox().getName());
            jPanel.add(getDeleteLanguageCheckbox(), null);
        }
        return jPanel;
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
            executableFilenameTextField.setBounds(new java.awt.Rectangle(208, 112, 264, 20));
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
            programExecutionCommandLineTextField.setBounds(new java.awt.Rectangle(274, 145, 198, 20));
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
    private JComboBox getAutoPopulateLanguageComboBox() {
        if (autoPopulateLanguageComboBox == null) {
            autoPopulateLanguageComboBox = new JComboBox();
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
         * From LanguageAutoFill <li>Title for Language <li>Compiler Command Line <li>Executable Identifier Mask <li>Execute command line
         */

        String[] values = LanguageAutoFill.getAutoFillValues(languageToFill);

        displayNameTextField.setText(values[4]);
        compileCommandLineTextField.setText(values[1]);
        executableFilenameTextField.setText(values[2]);
        programExecutionCommandLineTextField.setText(values[3]);

        enableUpdateButtons(true);
    }

    /**
     * Enable or disable Update button based on comparison of run to fields.
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
                newLanguage = getLanguageFromFields();
            } catch (InvalidFieldValue e) {
                
                // TODO there has to be a better way to ignore this exception
                // without CheckStyle complaining.
                
                /**
                 * getLanguageFromFields throws an InvalidFieldValue but in
                 * this case we ignore it because we don't care, if there
                 * are invalid fields that is ok at this point in the code.
                 * If there are problems getting the language then clearly
                 * the edited and original are different.
                 */
                
                e.printStackTrace();
            }

            enableButton |= language.isSameAs(newLanguage);

            // enableButton |= (!displayNameTextField.getText().equals(language.getDisplayName()));
            // enableButton |= (!compileCommandLineTextField.getText().equals(language.getCompileCommandLine()));
            // enableButton |= (!executableFilenameTextField.getText().equals(language.getExecutableIdentifierMask()));
            // enableButton |= (!programExecutionCommandLineTextField.getText().equals(language.getProgramExecuteCommandLine()));

        } else {
            if (getAddButton().isVisible()) {
                enableButton = true;
            }
        }

        enableUpdateButtons(enableButton);
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

            getAddButton().setVisible(false);
            getUpdateButton().setVisible(true);

        } else {
            displayNameTextField.setText("");
            compileCommandLineTextField.setText("");
            executableFilenameTextField.setText("");
            programExecutionCommandLineTextField.setText("");
            getDeleteLanguageCheckbox().setSelected(false);

            getAutoPopulateLanguageComboBox().setSelectedIndex(0);

            getAddButton().setVisible(true);
            getUpdateButton().setVisible(false);
        }

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
            deleteLanguageCheckbox.setBounds(new Rectangle(276, 179, 224, 21));
            deleteLanguageCheckbox.setText("Hide Language");
            deleteLanguageCheckbox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return deleteLanguageCheckbox;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
