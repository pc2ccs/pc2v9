package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.ContestInformation.TeamDisplayMask;

/**
 * Contest Information edit/update Pane.
 * 
 * Update contest information.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$

public class ContestInformationPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -8408469113380938482L;

    private JPanel buttonPanel = null;

    private JPanel centerPane = null;

    private JButton updateButton = null;

    private JLabel contestTitleLabel = null;

    private JTextField contestTitleTextField = null;

    private JPanel teamDisplaySettingPane = null;

    private JRadioButton displayNoneRadioButton = null;

    private JRadioButton displayNumbersOnlyRadioButton = null;

    private JRadioButton displayNameAndNumberRadioButton = null;

    private JRadioButton displayAliasNameRadioButton = null;

    private JRadioButton displayNamesOnlyRadioButton = null;

    private ButtonGroup displayNameButtonGroup = null; // @jve:decl-index=0:visual-constraint="617,62"

    private JButton cancelButton = null;

    private JTextField judgesDefaultAnswerTextField = null;

    private JLabel judgesDefaultAnswerLabel = null;

    private JCheckBox jCheckBoxShowPreliminaryOnBoard = null;

    private JCheckBox jCheckBoxShowPreliminaryOnNotifications = null;

    private JCheckBox additionalRunStatusCheckBox = null;
    
    private ContestInformation savedContestInformation = null;  //  @jve:decl-index=0:

    /**
     * This method initializes
     * 
     */
    public ContestInformationPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(533,347));
        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
    }

    /**
     * This method initializes buttonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(35);
            buttonPanel = new JPanel();
            buttonPanel.setLayout(flowLayout);
            buttonPanel.add(getUpdateButton(), null);
            buttonPanel.add(getCancelButton(), null);
        }
        return buttonPanel;
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            contestTitleLabel = new JLabel();
            contestTitleLabel.setBounds(new java.awt.Rectangle(55, 21, 134, 27));
            contestTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            contestTitleLabel.setText("Contest Title");
            centerPane = new JPanel();
            centerPane.setLayout(null);
            centerPane.add(contestTitleLabel, null);
            centerPane.add(getContestTitleTextField(), null);
            centerPane.add(getTeamDisplaySettingPane(), null);
            centerPane.add(getJudgesDefaultAnswerLabel(), null);
            centerPane.add(getJudgesDefaultAnswerTextField(), null);
            centerPane.add(getJCheckBoxShowPreliminaryOnBoard(), null);
            centerPane.add(getJCheckBoxShowPreliminaryOnNotifications(), null);
            centerPane.add(getAdditionalRunStatusCheckBox(), null);
        }
        return centerPane;
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
            updateButton.setToolTipText("Save settings");
            updateButton.setPreferredSize(new java.awt.Dimension(100, 26));
            updateButton.setMnemonic(java.awt.event.KeyEvent.VK_U);
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateContestInformation();
                }
            });
        }
        return updateButton;
    }

    /**
     * This method initializes contestTitleTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getContestTitleTextField() {
        if (contestTitleTextField == null) {
            contestTitleTextField = new JTextField();
            contestTitleTextField.setBounds(new java.awt.Rectangle(204, 21, 287, 27));
            contestTitleTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return contestTitleTextField;
    }

    @Override
    public String getPluginTitle() {
        return "Contest Information Pane";
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        savedContestInformation = getContest().getContestInformation();
        populateGUI();

        getContest().addContestInformationListener(new ContestInformationListenerImplementation());

    }

    protected ContestInformation getFromFields() {
        ContestInformation contestInformation = new ContestInformation();
        ContestInformation currentContestInformation = getContest().getContestInformation();
        if (currentContestInformation.getContestURL() != null) {
            contestInformation.setContestURL(new String(currentContestInformation.getContestURL()));
        }
        contestInformation.setContestTitle(getContestTitleTextField().getText());
        if (getDisplayNoneRadioButton().isSelected()) {
            contestInformation.setTeamDisplayMode(TeamDisplayMask.NONE);
        } else if (getDisplayNameAndNumberRadioButton().isSelected()) {
            contestInformation.setTeamDisplayMode(TeamDisplayMask.NUMBERS_AND_NAME);
        } else if (getDisplayNumbersOnlyRadioButton().isSelected()) {
            contestInformation.setTeamDisplayMode(TeamDisplayMask.LOGIN_NAME_ONLY);
        } else if (getDisplayNamesOnlyRadioButton().isSelected()) {
            contestInformation.setTeamDisplayMode(TeamDisplayMask.DISPLAY_NAME_ONLY);
        } else if (getDisplayAliasNameRadioButton().isSelected()) {
            contestInformation.setTeamDisplayMode(TeamDisplayMask.ALIAS);
        } else {
            // DEFAULT
            contestInformation.setTeamDisplayMode(TeamDisplayMask.LOGIN_NAME_ONLY);
        }
        contestInformation.setJudgesDefaultAnswer(getJudgesDefaultAnswerTextField().getText());
        contestInformation.setPreliminaryJudgementsTriggerNotifications(getJCheckBoxShowPreliminaryOnNotifications().isSelected());
        contestInformation.setPreliminaryJudgementsUsedByBoard(getJCheckBoxShowPreliminaryOnBoard().isSelected());
        contestInformation.setSendAdditionalRunStatusInformation(getAdditionalRunStatusCheckBox().isSelected());
        
        if (savedContestInformation != null){
            contestInformation.setJudgementNotificationsList(savedContestInformation.getJudgementNotificationsList());
        }
        
        return(contestInformation);
    }
    
    protected void enableUpdateButton() {
        ContestInformation newChoice = getFromFields();
        if (getContest().getContestInformation().isSameAs(newChoice)) {
            setEnableButtons(false);
        } else {
            setEnableButtons(true);
        }
    }

    void setEnableButtons(boolean isEnabled) {
        getUpdateButton().setEnabled(isEnabled);
        getCancelButton().setEnabled(isEnabled);
    }


    private void populateGUI() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ContestInformation contestInformation = getContest().getContestInformation();
                getContestTitleTextField().setText(contestInformation.getContestTitle());
                selectDisplayRadioButton();
                getJudgesDefaultAnswerTextField().setText(contestInformation.getJudgesDefaultAnswer());
                getJCheckBoxShowPreliminaryOnBoard().setSelected(contestInformation.isPreliminaryJudgementsUsedByBoard());
                getJCheckBoxShowPreliminaryOnNotifications().setSelected(contestInformation.isPreliminaryJudgementsTriggerNotifications());
                getAdditionalRunStatusCheckBox().setSelected(contestInformation.isSendAdditionalRunStatusInformation());
                setEnableButtons(false);
            }
        });

    }

    private void updateContestInformation() {
        ContestInformation contestInformation = getFromFields();
        getController().updateContestInformation(contestInformation);
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     * 
     */
    class ContestInformationListenerImplementation implements IContestInformationListener {

        public void contestInformationAdded(ContestInformationEvent event) {
            populateGUI();
            savedContestInformation = event.getContestInformation();
        }

        public void contestInformationChanged(ContestInformationEvent event) {
            populateGUI();
            savedContestInformation = event.getContestInformation();

        }

        public void contestInformationRemoved(ContestInformationEvent event) {
            // TODO Auto-generated method stub

        }

    }

    /**
     * This method initializes teamDisplaySettingPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTeamDisplaySettingPane() {
        if (teamDisplaySettingPane == null) {
            teamDisplaySettingPane = new JPanel();
            teamDisplaySettingPane.setLayout(new FlowLayout());
            teamDisplaySettingPane.setBounds(new java.awt.Rectangle(111,59,381,101));
            teamDisplaySettingPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Team Information Displayed to Judges", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            teamDisplaySettingPane.add(getDisplayNoneRadioButton(), null);
            teamDisplaySettingPane.add(getDisplayNumbersOnlyRadioButton(), null);
            teamDisplaySettingPane.add(getDisplayNamesOnlyRadioButton(), null);
            teamDisplaySettingPane.add(getDisplayNameAndNumberRadioButton(), null);
            teamDisplaySettingPane.add(getDisplayAliasNameRadioButton(), null);
            
        }
        return teamDisplaySettingPane;
    }

    private void selectDisplayRadioButton() {
        
        ContestInformation contestInformation = getContest().getContestInformation();
        if (contestInformation == null || contestInformation.getTeamDisplayMode() == null){
            getDisplayNameButtonGroup().setSelected(getDisplayNamesOnlyRadioButton().getModel(), true);
            
        } else {
            switch (contestInformation.getTeamDisplayMode()) {
                case DISPLAY_NAME_ONLY:
                    getDisplayNameButtonGroup().setSelected(getDisplayNamesOnlyRadioButton().getModel(), true);
                    break;
                case LOGIN_NAME_ONLY:
                    getDisplayNameButtonGroup().setSelected(getDisplayNumbersOnlyRadioButton().getModel(), true);
                    break;
                case NUMBERS_AND_NAME:
                    getDisplayNameButtonGroup().setSelected(getDisplayNameAndNumberRadioButton().getModel(), true);
                    break;
                case ALIAS:
                    getDisplayNameButtonGroup().setSelected(getDisplayAliasNameRadioButton().getModel(), true);
                    break;
                case NONE:
                    getDisplayNameButtonGroup().setSelected(getDisplayNoneRadioButton().getModel(), true);
                    break;
                default:
                    break;
            }
        }
        // TODO Auto-generated method stub
        
//        getDisplayNameButtonGroup().setSelected(getDisplayNoneButton(), false)

    }

    /**
     * This method initializes displayNoneButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getDisplayNoneRadioButton() {
        if (displayNoneRadioButton == null) {
            displayNoneRadioButton = new JRadioButton();
            displayNoneRadioButton.setText("None");
            displayNoneRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // getActionCommand called with text from button
                    // getSource returns the JRadioButton
                    enableUpdateButton();
                }
            });
        }
        return displayNoneRadioButton;
    }

    /**
     * This method initializes displayNumbersOnlyRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getDisplayNumbersOnlyRadioButton() {
        if (displayNumbersOnlyRadioButton == null) {
            displayNumbersOnlyRadioButton = new JRadioButton();
            displayNumbersOnlyRadioButton.setText("Show Numbers Only");
            displayNumbersOnlyRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // getActionCommand called with text from button
                    // getSource returns the JRadioButton
                    enableUpdateButton();
                }
            });
        }
        return displayNumbersOnlyRadioButton;
    }

    /**
     * This method initializes displayNameAndNumberRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getDisplayNameAndNumberRadioButton() {
        if (displayNameAndNumberRadioButton == null) {
            displayNameAndNumberRadioButton = new JRadioButton();
            displayNameAndNumberRadioButton.setText("Show Number and Name");
            displayNameAndNumberRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // getActionCommand called with text from button
                    // getSource returns the JRadioButton
                    enableUpdateButton();
                }
            });
        }
        return displayNameAndNumberRadioButton;
    }

    /**
     * This method initializes displayAliasNameRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getDisplayAliasNameRadioButton() {
        if (displayAliasNameRadioButton == null) {
            displayAliasNameRadioButton = new JRadioButton();
            displayAliasNameRadioButton.setText("Show Alias");
            displayAliasNameRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // getActionCommand called with text from button
                    // getSource returns the JRadioButton
                    enableUpdateButton();
                }
            });
        }
        return displayAliasNameRadioButton;
    }

    /**
     * This method initializes showNamesOnlyRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getDisplayNamesOnlyRadioButton() {
        if (displayNamesOnlyRadioButton == null) {
            displayNamesOnlyRadioButton = new JRadioButton();
            displayNamesOnlyRadioButton.setText("Show Names only");
            displayNamesOnlyRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    // getActionCommand called with text from button
                    // getSource returns the JRadioButton
                    enableUpdateButton();
                }
            });
        }
        return displayNamesOnlyRadioButton;
    }

    /**
     * This method initializes displayNameButtonGroup
     * 
     * @return javax.swing.ButtonGroup
     */
    private ButtonGroup getDisplayNameButtonGroup() {
        if (displayNameButtonGroup == null) {
            displayNameButtonGroup = new ButtonGroup();
            displayNameButtonGroup.add(getDisplayNoneRadioButton());
            
            displayNameButtonGroup.add(getDisplayNamesOnlyRadioButton());
            displayNameButtonGroup.add(getDisplayNameAndNumberRadioButton());
            displayNameButtonGroup.add(getDisplayNumbersOnlyRadioButton());
            displayNameButtonGroup.add(getDisplayAliasNameRadioButton());
        }
        return displayNameButtonGroup;
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
            cancelButton.setToolTipText("Discard changes");
            cancelButton.setPreferredSize(new java.awt.Dimension(100, 26));
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    populateGUI();
                }
            });
        }
        return cancelButton;
    }

    /**
     * This method initializes JudgesDefaultAnswerTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJudgesDefaultAnswerTextField() {
        if (judgesDefaultAnswerTextField == null) {
            judgesDefaultAnswerTextField = new JTextField();
            judgesDefaultAnswerTextField.setText("");
            judgesDefaultAnswerTextField.setSize(new java.awt.Dimension(280, 27));
            judgesDefaultAnswerTextField.setLocation(new java.awt.Point(209, 169));
            judgesDefaultAnswerTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return judgesDefaultAnswerTextField;
    }

    /**
     * This method initializes judgesDefaultAnswerLabel
     * 
     * @return javax.swing.JLabel
     */
    private JLabel getJudgesDefaultAnswerLabel() {
        if (judgesDefaultAnswerLabel == null) {
            judgesDefaultAnswerLabel = new JLabel();
            judgesDefaultAnswerLabel.setText("Judges' Default Answer");
            judgesDefaultAnswerLabel.setHorizontalTextPosition(javax.swing.SwingConstants.TRAILING);
            judgesDefaultAnswerLabel.setLocation(new java.awt.Point(25, 165));
            judgesDefaultAnswerLabel.setSize(new java.awt.Dimension(175, 27));
            judgesDefaultAnswerLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        }
        return judgesDefaultAnswerLabel;
    }

    /**
     * This method initializes jCheckBoxShowPreliminaryOnBoard
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getJCheckBoxShowPreliminaryOnBoard() {
        if (jCheckBoxShowPreliminaryOnBoard == null) {
            jCheckBoxShowPreliminaryOnBoard = new JCheckBox();
            jCheckBoxShowPreliminaryOnBoard.setBounds(new Rectangle(60, 208, 427, 21));
            jCheckBoxShowPreliminaryOnBoard.setHorizontalAlignment(SwingConstants.LEFT);
            jCheckBoxShowPreliminaryOnBoard.setMnemonic(KeyEvent.VK_UNDEFINED);
            jCheckBoxShowPreliminaryOnBoard.setText("Include Preliminary Judgements in Scoring Algorithm");
            jCheckBoxShowPreliminaryOnBoard.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return jCheckBoxShowPreliminaryOnBoard;
    }

    /**
     * This method initializes jCheckBoxShowPreliminaryOnNotifications
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getJCheckBoxShowPreliminaryOnNotifications() {
        if (jCheckBoxShowPreliminaryOnNotifications == null) {
            jCheckBoxShowPreliminaryOnNotifications = new JCheckBox();
            jCheckBoxShowPreliminaryOnNotifications.setBounds(new Rectangle(60, 238, 427, 21));
            jCheckBoxShowPreliminaryOnNotifications.setHorizontalAlignment(SwingConstants.LEFT);
            jCheckBoxShowPreliminaryOnNotifications.setMnemonic(KeyEvent.VK_UNDEFINED);
            jCheckBoxShowPreliminaryOnNotifications.setText("Send Balloon Notifications for Preliminary Judgements");
            jCheckBoxShowPreliminaryOnNotifications.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return jCheckBoxShowPreliminaryOnNotifications;
    }

    /**
     * This method initializes additionalRunStatusCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getAdditionalRunStatusCheckBox() {
        if (additionalRunStatusCheckBox == null) {
            additionalRunStatusCheckBox = new JCheckBox();
            additionalRunStatusCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
            additionalRunStatusCheckBox.setSize(new java.awt.Dimension(427,21));
            additionalRunStatusCheckBox.setLocation(new java.awt.Point(60,268));
            additionalRunStatusCheckBox.setText("Send Additional Run Status Information");
            additionalRunStatusCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return additionalRunStatusCheckBox;
    }
    
    public ContestInformation getContestInformation() {
        return savedContestInformation;
    }

    public void setContestInformation(ContestInformation contestInformation) {
        this.savedContestInformation = contestInformation;
    }


} // @jve:decl-index=0:visual-constraint="10,10"
