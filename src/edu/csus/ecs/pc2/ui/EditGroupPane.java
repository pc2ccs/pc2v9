package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Arrays;

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
import edu.csus.ecs.pc2.core.list.SiteComparatorBySiteNumber;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Site;

/**
 * Add/Edit Group Pane
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditGroupPane extends JPanePlugin {

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

    private JTextField displayNameTextField = null;

    private JLabel jLabel4 = null;

    private JTextField externalIdTextField = null;

    private static final String NONE_SELECTED = "NONE SELECTED";

    private Group group = null;

    private boolean populatingGUI = true;

    private JComboBox<Serializable> siteComboBox = null;

    private JCheckBox displayOnScoreboardCheckbox = null;

    /**
     * This method initializes
     * 
     */
    public EditGroupPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(517, 197));

        this.add(getJPanel(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);

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
        return "Edit Group Pane";
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
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addGroup();
                }
            });
        }
        return addButton;
    }

    protected void addGroup() {

        if (!validateGroupFields()) {
            // new problem is invalid, just return, message issued by validateProblemFields
            return;
        }

        Group newGroup = null;
        try {
            newGroup = getGroupFromFields(null);
        } catch (InvalidFieldValue e) {
            StaticLog.log("Error getGroupFromFields", e);
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }

        getController().addNewGroup(newGroup);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    private Group getGroupFromFields(Group checkGroup) {
        if (checkGroup == null) {
            checkGroup = new Group(getDisplayNameTextField().getText());
        } else {
            checkGroup.setDisplayName(getDisplayNameTextField().getText());
        }

        checkGroup.setDisplayName(displayNameTextField.getText());

        if (getSiteComboBox().getSelectedIndex() > 0) {
            Site site = (Site) getSiteComboBox().getSelectedItem();
            checkGroup.setSite(site.getElementId());
        } else {
            // alow them to clear this field
            checkGroup.setSite(null);
        }
        if (getExternalidTextField().getText().length() > 0) {
            checkGroup.setGroupId(Integer.parseInt(externalIdTextField.getText()));
        }
        checkGroup.setDisplayOnScoreboard(getDisplayOnScoreboardCheckbox().isSelected());
        return checkGroup;
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
                    updateGroup();
                }
            });
        }
        return updateButton;
    }

    protected void updateGroup() {

        if (!validateGroupFields()) {
            // new group is invalid, just return, message issued by validateGroupFields
            return;
        }

        Group newGroup = null;

        try {
            newGroup = getGroupFromFields(group);
        } catch (InvalidFieldValue e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            // showMessage(e.getMessage());
            return;
        }

        getController().updateGroup(newGroup);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    /**
     * Validate that all group fields are ok.
     * 
     * @return
     */
    private boolean validateGroupFields() {

        if (getDisplayNameTextField().getText().trim().length() < 1) {
            JOptionPane.showMessageDialog(this,"Enter a group display name");
            return false;
        }
        return true;
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

            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Group modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                if (getAddButton().isEnabled()) {
                    addGroup();
                } else {
                    updateGroup();
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
            jLabel4 = new JLabel();
            jLabel4.setBounds(new Rectangle(18, 125, 182, 20));
            jLabel4.setName("ExternalIdLabel");
            jLabel4.setMaximumSize(new Dimension(2147483647, 2147483647));
            jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
            jLabel4.setText("External Id");
            jLabel4.setForeground(new Color(0, 0, 0));
            jLabel1 = new JLabel();
            jLabel1.setBounds(new Rectangle(18, 16, 182, 20));
            jLabel1.setName("DisplayNameLabel");
            jLabel1.setMaximumSize(new Dimension(2147483647, 2147483647));
            jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
            jLabel1.setText("Display Name");
            jLabel1.setForeground(Color.black);
            jLabel = new JLabel();
            jLabel.setBounds(new Rectangle(18, 52, 182, 20));
            jLabel.setName("PC2SiteLabel");
            jLabel.setMaximumSize(new Dimension(2147483647, 2147483647));
            jLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            jLabel.setText("PC^2 Site");
            jLabel.setForeground(Color.black);
            jPanel = new JPanel();
            jPanel.setLayout(null);
            jPanel.setName("advancedEdit");
            jPanel.add(jLabel, jLabel.getName());
            jPanel.add(jLabel1, jLabel1.getName());
            jPanel.add(getDisplayNameTextField(), getDisplayNameTextField().getName());
            jPanel.add(jLabel4, jLabel4.getName());
            jPanel.add(getExternalidTextField(), getExternalidTextField().getName());
            jPanel.add(getSiteComboBox(), null);
            jPanel.add(getDisplayOnScoreboardCheckbox(), null);
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
            displayNameTextField.setBounds(new Rectangle(213, 16, 263, 20));
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
     * This method initializes ExternalIdTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getExternalidTextField() {
        if (externalIdTextField == null) {
            externalIdTextField = new JTextField();
            externalIdTextField.setBounds(new Rectangle(213, 125, 76, 20));
            externalIdTextField.setToolTipText("");
            externalIdTextField.setName("externalIdTextField");
            externalIdTextField.setDocument(new IntegerDocument());
            externalIdTextField.setDocument(new IntegerDocument());
            externalIdTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });

        }
        return externalIdTextField;
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

        if (group != null) {
            try {
                Group changedGroup = getGroupFromFields(null);
                if (!group.isSameAs(changedGroup)) {
                    enableButton = true;
                }

            } catch (InvalidFieldValue e) {
                // invalid field, but that is ok as they are entering data
                // will be caught and reported when they hit update or add.
                getController().getLog().log(Log.DEBUG, "Input Group (but not saving) ", e);
                enableButton = true;
            }

        } else {
            if (getAddButton().isVisible()) {
                enableButton = true;
            }
        }

        enableUpdateButtons(enableButton);
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(final Group group) {

        this.group = group;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(group);
                enableUpdateButtons(false);
            }
        });
    }

    /**
     * Populate site combo box.
     * 
     * @param siteNumber
     */
    private void populateSiteCombo(int siteNumber) {

        int siteIndex = 0;

        getSiteComboBox().removeAllItems();
        getSiteComboBox().addItem(NONE_SELECTED);

        int i = 1; // 1 since we started with noneSelected
        Site[] sites = getContest().getSites();
        Arrays.sort(sites, new SiteComparatorBySiteNumber());
        for (Site site : sites) {
            getSiteComboBox().addItem(site);

            if (siteNumber == site.getSiteNumber()) {
                siteIndex = i;
            }
            i++;
        }

        getSiteComboBox().setSelectedIndex(siteIndex);
    }

    private void populateGUI(Group group2) {

        populatingGUI = true;

        if (group2 != null) {
            getDisplayNameTextField().setText(group2.getDisplayName());
            getExternalidTextField().setText(String.valueOf(group2.getGroupId()));
            getDisplayOnScoreboardCheckbox().setSelected(group2.isDisplayOnScoreboard());

            int siteNumber = 0;
            if (group2.getSite() != null) {
                siteNumber = group2.getSite().getSiteNumber();
            }
            populateSiteCombo(siteNumber);
            getAddButton().setVisible(false);
            getUpdateButton().setVisible(true);

        } else {
            getDisplayNameTextField().setText("");
            getExternalidTextField().setText("");
            getDisplayOnScoreboardCheckbox().setSelected(true);

            populateSiteCombo(0);
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
        addButton.setEnabled(editedText);
        updateButton.setEnabled(editedText);
    }

    /**
     * This method initializes siteComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox<Serializable> getSiteComboBox() {
        if (siteComboBox == null) {
            siteComboBox = new JComboBox<Serializable>();
            siteComboBox.setBounds(new Rectangle(213, 50, 247, 25));
            siteComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return siteComboBox;
    }

    /**
     * This method initializes displayOnScoreboardCheckbox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getDisplayOnScoreboardCheckbox() {
        if (displayOnScoreboardCheckbox == null) {
            displayOnScoreboardCheckbox = new JCheckBox();
            displayOnScoreboardCheckbox.setBounds(new Rectangle(18, 88, 182, 21));
            displayOnScoreboardCheckbox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
            displayOnScoreboardCheckbox.setHorizontalAlignment(SwingConstants.RIGHT);
            displayOnScoreboardCheckbox.setText("Display on Scoreboard?");
            displayOnScoreboardCheckbox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return displayOnScoreboardCheckbox;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
