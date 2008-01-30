package edu.csus.ecs.pc2.ui;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfilesPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 9075523788534575300L;

    private JLabel profileNameLabel = null;

    private JLabel profileLabel = null;

    private JComboBox profileComboBox = null;

    private JButton switchButton = null;

    private JButton setButton = null;

    private JTextField profileTextField = null;

    private JPanel centerPane = null;

    private JPanel buttonPane = null;

    private JButton newButton = null;

    private JButton jButton = null;

    private JButton jButton1 = null;

    private JButton jButton2 = null;

    private JLabel notificationOfNonImplementationLabel = null;

    /**
     * This method initializes
     * 
     */
    public ProfilesPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        profileLabel = new JLabel();
        profileLabel.setBounds(new java.awt.Rectangle(26, 88, 94, 23));
        profileLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        profileLabel.setText("Profiles");
        profileNameLabel = new JLabel();
        profileNameLabel.setBounds(new java.awt.Rectangle(14, 40, 106, 22));
        profileNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        profileNameLabel.setText("Profile Name");
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(729,319));
        this.add(getCenterPane(), java.awt.BorderLayout.CENTER);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);

    }

    public String getPluginTitle() {
        return "Profiles Pane";
    }

    /**
     * This method initializes profileComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getProfileComboBox() {
        if (profileComboBox == null) {
            profileComboBox = new JComboBox();
            profileComboBox.setBounds(new java.awt.Rectangle(134, 85, 339, 28));
        }
        return profileComboBox;
    }

    /**
     * This method initializes Set
     * 
     * @return javax.swing.JButton
     */
    private JButton getSwitchButton() {
        if (switchButton == null) {
            switchButton = new JButton();
            switchButton.setBounds(new java.awt.Rectangle(497, 85, 73, 28));
            switchButton.setEnabled(false);
            switchButton.setText("Switch");
        }
        return switchButton;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSetButton() {
        if (setButton == null) {
            setButton = new JButton();
            setButton.setBounds(new java.awt.Rectangle(497, 38, 73, 26));
            setButton.setEnabled(false);
            setButton.setText("Set");
            setButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
                }
            });
        }
        return setButton;
    }

    /**
     * This method initializes profileTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getProfileTextField() {
        if (profileTextField == null) {
            profileTextField = new JTextField();
            profileTextField.setBounds(new java.awt.Rectangle(134, 36, 339, 30));
        }
        return profileTextField;
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            notificationOfNonImplementationLabel = new JLabel();
            notificationOfNonImplementationLabel.setBounds(new java.awt.Rectangle(0,132,733,113));
            notificationOfNonImplementationLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            notificationOfNonImplementationLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
            notificationOfNonImplementationLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            notificationOfNonImplementationLabel.setText("<html><center>This pane will allow switching automatically between<br>Practice and Real contest configurations (work-in-progress)</html>");
            centerPane = new JPanel();
            centerPane.setLayout(null);
            centerPane.add(profileNameLabel, null);
            centerPane.add(profileLabel, null);
            centerPane.add(getProfileComboBox(), null);
            centerPane.add(getSwitchButton(), null);
            centerPane.add(getSetButton(), null);
            centerPane.add(getProfileTextField(), null);

            centerPane.add(notificationOfNonImplementationLabel, null);
        }
        return centerPane;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(35);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.setPreferredSize(new java.awt.Dimension(35, 35));
            buttonPane.add(getNewButton(), null);
            buttonPane.add(getJButton(), null);
            buttonPane.add(getJButton1(), null);
            buttonPane.add(getJButton2(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes newButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getNewButton() {
        if (newButton == null) {
            newButton = new JButton();
            newButton.setText("New");
            newButton.setEnabled(false);
        }
        return newButton;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton() {
        if (jButton == null) {
            jButton = new JButton();
            jButton.setText("Save As...");
            jButton.setEnabled(false);
        }
        return jButton;
    }

    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton1() {
        if (jButton1 == null) {
            jButton1 = new JButton();
            jButton1.setText("Delete");
            jButton1.setEnabled(false);
        }
        return jButton1;
    }

    /**
     * This method initializes jButton2
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton2() {
        if (jButton2 == null) {
            jButton2 = new JButton();
            jButton2.setText("Export");
            jButton2.setEnabled(false);
        }
        return jButton2;
    }
} // @jve:decl-index=0:visual-constraint="25,9"
