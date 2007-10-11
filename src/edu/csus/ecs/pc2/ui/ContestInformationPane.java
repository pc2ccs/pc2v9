package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformationEvent;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.IContestInformationListener;
import edu.csus.ecs.pc2.core.model.ContestInformation.TeamDisplayMask;

/**
 * Contest Information edit/update Pane.
 * 
 * Update contest title.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
// $Id$
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
        this.setSize(new java.awt.Dimension(533, 238));
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
            buttonPanel = new JPanel();
            buttonPanel.add(getUpdateButton(), null);
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
        }
        return contestTitleTextField;
    }

    @Override
    public String getPluginTitle() {
        return "Contest Information Pane";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        super.setContestAndController(inContest, inController);

        populateGUI();

        getContest().addContestInformationListener(new ContestInformationListenerImplementation());

    }

    private void populateGUI() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ContestInformation contestInformation = getContest().getContestInformation();
                getContestTitleTextField().setText(contestInformation.getContestTitle());
                selectDisplayRadioButton();
            }
        });

    }

    private void updateContestInformation() {
        ContestInformation contestInformation = getContest().getContestInformation();
        contestInformation.setContestTitle(getContestTitleTextField().getText());
        
        contestInformation.setTeamDisplayMode(TeamDisplayMask.LOGIN_NAME_ONLY);
        
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
        }
        
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

        }

        public void contestInformationChanged(ContestInformationEvent event) {
            populateGUI();

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
                    getDisplayNameButtonGroup().setSelected(getDisplayNamesOnlyRadioButton().getModel(), true);
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

    // private ButtonGroup getTeamReadsFrombuttonGroup() {
    // if (teamReadsFrombuttonGroup == null) {
    // teamReadsFrombuttonGroup = new ButtonGroup();
    // teamReadsFrombuttonGroup.add(getStdinRadioButton());
    // teamReadsFrombuttonGroup.add(getFileRadioButton());
    // }
    // return teamReadsFrombuttonGroup;
    // }

} // @jve:decl-index=0:visual-constraint="10,10"
