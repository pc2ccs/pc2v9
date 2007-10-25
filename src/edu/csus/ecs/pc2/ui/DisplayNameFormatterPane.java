package edu.csus.ecs.pc2.ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.JRadioButton;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;

/**
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class DisplayNameFormatterPane extends JPanePlugin {
    /**
     * 
     */
    private static final long serialVersionUID = 3268060113515844016L;

    public static final String CHANGE_PROPERTY = "displayChoice";
    /**
     * What is the valid choices
     */
    public enum  DisplayNameChoice {
        TEAMANDSHORTSCHOOLNAME,
        SCHOOLNAME,
        SHORTSCHOOLNAME,
        TEAMNAME
    }
    private DisplayNameChoice savedChoice = DisplayNameChoice.TEAMANDSHORTSCHOOLNAME;

    private JPanel buttonPanel = null;

    private JButton applyButton = null;

    private JButton cancelButton = null;

    private JPanel choicesPanel = null;

    private JRadioButton teamAndSchoolNameButton = null;

    private JRadioButton schoolNameButton = null;

    private JRadioButton shortSchoolNameButton = null;

    private JRadioButton teamNameButton = null;

    private ButtonGroup displayNameButtonGroup = null; // @jve:decl-index=0:visual-constraint="654,59"

    /**
     * 
     */
    public DisplayNameFormatterPane() {
        super();
        initialize();
        // TODO Auto-generated constructor stub
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(578, 251));
        this.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
        this.add(getChoicesPanel(), java.awt.BorderLayout.CENTER);
        enableUpdateButton();
    }

    @Override
    public String getPluginTitle() {
        return "Display Name Formatter Pane";
    }

    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(25);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(flowLayout);
            buttonPanel.setPreferredSize(new java.awt.Dimension(10, 35));
            buttonPanel.add(getApplyButton(), null);
            buttonPanel.add(getCancelButton(), null);
        }
        return buttonPanel;
    }

    /**
     * This method initializes applyButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getApplyButton() {
        if (applyButton == null) {
            applyButton = new JButton();
            applyButton.setText("Apply");
            applyButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    DisplayNameChoice newChoice = getCurrentSelection();
                    if (newChoice != null && !savedChoice.equals(newChoice)) {
                        firePropertyChange(CHANGE_PROPERTY, savedChoice.toString(), newChoice.toString());
                        savedChoice = newChoice;
                    }
                    enableUpdateButton();
                }
            });
        }
        return applyButton;
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
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    switch (savedChoice) {
                        case TEAMANDSHORTSCHOOLNAME:
                            getDisplayNameButtonGroup().setSelected(getTeamAndSchoolNameButton().getModel(), true);
                            break;

                        case SCHOOLNAME:
                            getDisplayNameButtonGroup().setSelected(getSchoolNameButton().getModel(), true);
                            break;
                        
                        case SHORTSCHOOLNAME:
                            getDisplayNameButtonGroup().setSelected(getShortSchoolNameButton().getModel(), true);
                            break;
                        
                        case TEAMNAME:
                            getDisplayNameButtonGroup().setSelected(getTeamNameButton().getModel(), true);
                            break;

                        default:
                            break;
                    }
                    enableUpdateButton();
                }
            });
        }
        return cancelButton;
    }

    /**
     * This method initializes choicesPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getChoicesPanel() {
        if (choicesPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(4);
            gridLayout.setColumns(1);
            choicesPanel = new JPanel();
            choicesPanel.setLayout(gridLayout);
            choicesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Display Name Choices" + "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), new java.awt.Color(51, 51, 51)));

            choicesPanel.add(getTeamAndSchoolNameButton(), null);
            choicesPanel.add(getSchoolNameButton(), null);
            choicesPanel.add(getTeamAndSchoolNameButton(), null);
            choicesPanel.add(getShortSchoolNameButton(), null);
            choicesPanel.add(getTeamNameButton(), null);
            getDisplayNameButtonGroup().setSelected(getTeamAndSchoolNameButton().getModel(), true);
        }
        return choicesPanel;
    }

    /**
     * This method initializes teamAndSchoolNameButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getTeamAndSchoolNameButton() {
        if (teamAndSchoolNameButton == null) {
            teamAndSchoolNameButton = new JRadioButton();
            teamAndSchoolNameButton.setText("Team and Short School Name ex: Hornet 1 (Sacramento State)");
            teamAndSchoolNameButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // getActionCommand called with text from button
                    // getSource returns the JRadioButton
                    enableUpdateButton();
                }
            });
        }
        return teamAndSchoolNameButton;
    }

    /**
     * This method initializes schoolNameButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getSchoolNameButton() {
        if (schoolNameButton == null) {
            schoolNameButton = new JRadioButton();
            schoolNameButton.setText("School Name ex:  California State University, Sacramento");
            schoolNameButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // getActionCommand called with text from button
                    // getSource returns the JRadioButton
                    enableUpdateButton();
                }
            });
        }
        return schoolNameButton;
    }

    protected void enableUpdateButton() {
        DisplayNameChoice newChoice = getCurrentSelection();
        if (newChoice.equals(savedChoice)) {
            setEnableButtons(false);
        } else {
            setEnableButtons(true);
        }
    }

    void setEnableButtons(boolean isEnabled) {
        getApplyButton().setEnabled(isEnabled);
        getCancelButton().setEnabled(isEnabled);
    }

    /**
     * This method initializes shortSchoolNameButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getShortSchoolNameButton() {
        if (shortSchoolNameButton == null) {
            shortSchoolNameButton = new JRadioButton();
            shortSchoolNameButton.setText("Short School Name ex: Sacramento State");
            shortSchoolNameButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // getActionCommand called with text from button
                    // getSource returns the JRadioButton
                    enableUpdateButton();
                }
            });
        }
        return shortSchoolNameButton;
    }

    /**
     * This method initializes teamNameButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getTeamNameButton() {
        if (teamNameButton == null) {
            teamNameButton = new JRadioButton();
            teamNameButton.setText("Team Name ex: Hornet 1");
            teamNameButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // getActionCommand called with text from button
                    // getSource returns the JRadioButton
                    enableUpdateButton();
                }
            });
        }
        return teamNameButton;
    }

    /**
     * This method initializes displayNameButtonGroup
     * 
     * @return javax.swing.ButtonGroup
     */
    private ButtonGroup getDisplayNameButtonGroup() {
        if (displayNameButtonGroup == null) {
            displayNameButtonGroup = new ButtonGroup();
            displayNameButtonGroup.add(getSchoolNameButton());
            displayNameButtonGroup.add(getTeamAndSchoolNameButton());
            displayNameButtonGroup.add(getShortSchoolNameButton());
            displayNameButtonGroup.add(getTeamNameButton());
        }
        return displayNameButtonGroup;
    }

    /**
     * At startup no change is fired, must call this to determine initial state.
     * 
     * @return DisplayNameChoice
     */
    public DisplayNameChoice getCurrentSelection() {
        DisplayNameChoice newChoice = null;
        if (getTeamAndSchoolNameButton().isSelected()) {
            newChoice = DisplayNameChoice.TEAMANDSHORTSCHOOLNAME;
        }
        if (getSchoolNameButton().isSelected()) {
            newChoice = DisplayNameChoice.SCHOOLNAME;
        }
        if (getShortSchoolNameButton().isSelected()) {
            newChoice = DisplayNameChoice.SHORTSCHOOLNAME;
        }
        if (getTeamNameButton().isSelected()) {
            newChoice = DisplayNameChoice.TEAMNAME;
        }
        return newChoice;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
