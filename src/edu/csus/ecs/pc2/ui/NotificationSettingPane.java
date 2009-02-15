package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.model.NotificationSetting;
import java.awt.GridLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * edit Notification Settings pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class NotificationSettingPane extends JPanePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -2435063240993379856L;

    private JPanel buttonPane = null;

    private JPanel mainPane = null;

    private JButton addButton = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private boolean populatingGUI;

    private NotificationSetting notificationSetting;

    private JPanel preliminaryPane = null;

    private JPanel finalPane = null;

    private JCheckBox yesPrelimCheckBox = null;

    private JCheckBox noPrelimJButton = null;

    private JLabel stopLabel = null;

    private JTextField yesPrelimCuttoffMinutesTextField = null;

    private JTextField noPrelimCuttoffMinutesTextField = null;

    private JLabel jLabel = null;

    private JLabel prelimYesMinLabel = null;

    private JLabel prelimNoMinLabel = null;

    private JCheckBox yesFinalCheckBox = null;

    private JCheckBox noFinalCheckBox = null;

    private JLabel cuttoff1 = null;

    private JLabel jLabel1 = null;

    private JLabel jLabel2 = null;

    private JLabel jLabel3 = null;

    private JTextField textfield1 = null;

    private JTextField jTextField = null;

    /**
     * This method initializes
     * 
     */
    public NotificationSettingPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(499, 205));
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getMainPane(), java.awt.BorderLayout.CENTER);

    }

    @Override
    public String getPluginTitle() {
        return "Edit Notification Setting";
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
            buttonPane.add(getAddButton(), null);
            buttonPane.add(getUpdateButton(), null);
            buttonPane.add(getCancelButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes mainPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainPane() {
        if (mainPane == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(2);
            gridLayout.setColumns(1);
            mainPane = new JPanel();
            mainPane.setLayout(gridLayout);
            mainPane.add(getPreliminaryPane(), null);
            mainPane.add(getFinalPane(), null);
        }
        return mainPane;
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
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
                }
            });
        }
        return addButton;
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
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
                }
            });
        }
        return updateButton;
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
                    System.out.println("actionPerformed()"); // TODO Auto-generated Event stub actionPerformed()
                }
            });
        }
        return cancelButton;
    }

    public void setNotificationSetting(final NotificationSetting notificationSetting) {

        this.notificationSetting = notificationSetting;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(notificationSetting);
                enableUpdateButtons(false);
            }
        });
    }

    private void populateGUI(NotificationSetting notificationSetting2) {

        populatingGUI = true;

        if (notificationSetting2 != null) {

            // TODO populate from notificationSetting2

            getAddButton().setVisible(false);
            getUpdateButton().setVisible(true);

        } else {

            // TODO set default values

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
     * This method initializes preliminaryPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPreliminaryPane() {
        if (preliminaryPane == null) {
            prelimNoMinLabel = new JLabel();
            prelimNoMinLabel.setBounds(new java.awt.Rectangle(260, 52, 213, 22));
            prelimNoMinLabel.setText("minutes before end of contest");
            prelimYesMinLabel = new JLabel();
            prelimYesMinLabel.setBounds(new java.awt.Rectangle(260, 21, 213, 22));
            prelimYesMinLabel.setText("minutes before end of contest");
            jLabel = new JLabel();
            jLabel.setBounds(new java.awt.Rectangle(100, 52, 96, 22));
            jLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel.setText("Cutoff Time");
            stopLabel = new JLabel();
            stopLabel.setBounds(new java.awt.Rectangle(100, 21, 96, 22));
            stopLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            stopLabel.setText("Cutoff Time");
            preliminaryPane = new JPanel();
            preliminaryPane.setLayout(null);
            preliminaryPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Preliminary Judgements", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            preliminaryPane.add(getYesPrelimCheckBox(), null);
            preliminaryPane.add(getNoPrelimJButton(), null);
            preliminaryPane.add(stopLabel, null);
            preliminaryPane.add(getYesPrelimCuttoffMinutesTextField(), null);
            preliminaryPane.add(getNoPrelimCuttoffMinutesTextField(), null);
            preliminaryPane.add(jLabel, null);
            preliminaryPane.add(prelimYesMinLabel, null);
            preliminaryPane.add(prelimNoMinLabel, null);
        }
        return preliminaryPane;
    }

    /**
     * This method initializes finalPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getFinalPane() {
        if (finalPane == null) {
            jLabel3 = new JLabel();
            jLabel3.setText("minutes before end of contest");
            jLabel3.setSize(new java.awt.Dimension(213, 22));
            jLabel3.setLocation(new java.awt.Point(260, 52));
            jLabel2 = new JLabel();
            jLabel2.setText("minutes before end of contest");
            jLabel2.setSize(new java.awt.Dimension(213, 22));
            jLabel2.setLocation(new java.awt.Point(260, 21));
            jLabel1 = new JLabel();
            jLabel1.setText("Cutoff Time");
            jLabel1.setSize(new java.awt.Dimension(96, 22));
            jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel1.setLocation(new java.awt.Point(100, 52));
            cuttoff1 = new JLabel();
            cuttoff1.setText("Cutoff Time");
            cuttoff1.setSize(new java.awt.Dimension(96, 22));
            cuttoff1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            cuttoff1.setLocation(new java.awt.Point(100, 21));
            finalPane = new JPanel();
            finalPane.setLayout(null);
            finalPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Final Judgements", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            finalPane.add(getYesFinalCheckBox(), null);
            finalPane.add(getNoFinalCheckBox(), null);
            finalPane.add(cuttoff1, null);
            finalPane.add(jLabel1, null);
            finalPane.add(jLabel2, null);
            finalPane.add(jLabel3, null);
            finalPane.add(getTextfield1(), null);
            finalPane.add(getJTextField(), null);
        }
        return finalPane;
    }

    /**
     * This method initializes yesPrelimJButton
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getYesPrelimCheckBox() {
        if (yesPrelimCheckBox == null) {
            yesPrelimCheckBox = new JCheckBox();
            yesPrelimCheckBox.setBounds(new java.awt.Rectangle(17, 21, 70, 22));
            yesPrelimCheckBox.setText("Yes");
        }
        return yesPrelimCheckBox;
    }

    /**
     * This method initializes noPrelimJButton
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getNoPrelimJButton() {
        if (noPrelimJButton == null) {
            noPrelimJButton = new JCheckBox();
            noPrelimJButton.setBounds(new java.awt.Rectangle(17, 52, 70, 22));
            noPrelimJButton.setText("No");
        }
        return noPrelimJButton;
    }

    /**
     * This method initializes yesPrelimCuttoffMinutesTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getYesPrelimCuttoffMinutesTextField() {
        if (yesPrelimCuttoffMinutesTextField == null) {
            yesPrelimCuttoffMinutesTextField = new JTextField();
            yesPrelimCuttoffMinutesTextField.setBounds(new java.awt.Rectangle(208, 22, 45, 21));
        }
        return yesPrelimCuttoffMinutesTextField;
    }

    /**
     * This method initializes noPrelimCuttoffMinutesTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getNoPrelimCuttoffMinutesTextField() {
        if (noPrelimCuttoffMinutesTextField == null) {
            noPrelimCuttoffMinutesTextField = new JTextField();
            noPrelimCuttoffMinutesTextField.setBounds(new java.awt.Rectangle(208, 52, 45, 23));
        }
        return noPrelimCuttoffMinutesTextField;
    }

    /**
     * This method initializes yesFinalCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getYesFinalCheckBox() {
        if (yesFinalCheckBox == null) {
            yesFinalCheckBox = new JCheckBox();
            yesFinalCheckBox.setText("Yes");
            yesFinalCheckBox.setLocation(new java.awt.Point(17, 22));
            yesFinalCheckBox.setSize(new java.awt.Dimension(67, 21));
        }
        return yesFinalCheckBox;
    }

    /**
     * This method initializes jCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getNoFinalCheckBox() {
        if (noFinalCheckBox == null) {
            noFinalCheckBox = new JCheckBox();
            noFinalCheckBox.setText("No");
            noFinalCheckBox.setLocation(new java.awt.Point(17, 52));
            noFinalCheckBox.setSize(new java.awt.Dimension(46, 24));
        }
        return noFinalCheckBox;
    }

    /**
     * This method initializes textfield1
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getTextfield1() {
        if (textfield1 == null) {
            textfield1 = new JTextField();
            textfield1.setLocation(new java.awt.Point(208, 22));
            textfield1.setSize(new java.awt.Dimension(45, 21));
        }
        return textfield1;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJTextField() {
        if (jTextField == null) {
            jTextField = new JTextField();
            jTextField.setLocation(new java.awt.Point(208, 52));
            jTextField.setPreferredSize(new java.awt.Dimension(4, 20));
            jTextField.setSize(new java.awt.Dimension(45, 21));
        }
        return jTextField;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
