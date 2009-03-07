package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.model.JudgementNotification;

/**
 * A pane for Yes and No for a JudgementNotification.
 * 
 * @see JudgementNotification
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class NotificationPane extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 2315599651968166483L;

    private JTextField stopYesMinTextField = null;

    private JLabel minFromEndYesLabel = null;

    private JLabel minFromEndNoLabel = null;

    private JTextField stopNoMinTextField = null;

    private JCheckBox stopSendingYesCheckBox = null;

    private JCheckBox stopSendingNoCheckBox = null;

    private JudgementNotification judgementNotificationYes = new JudgementNotification();

    private JudgementNotification judgementNotificationNo = new JudgementNotification();

    /**
     * This method initializes
     * 
     */
    public NotificationPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        minFromEndNoLabel = new JLabel();
        minFromEndNoLabel.setBounds(new Rectangle(305, 62, 237, 18));
        minFromEndNoLabel.setText("mins from end of contest");
        minFromEndYesLabel = new JLabel();
        minFromEndYesLabel.setBounds(new Rectangle(305, 16, 237, 18));
        minFromEndYesLabel.setText("mins from end of contest");
        this.setLayout(null);
        this.setSize(new Dimension(572, 100));
        this.add(getStopYesMinTextField(), null);
        this.add(minFromEndYesLabel, null);
        this.add(minFromEndNoLabel, null);
        this.add(getStopNoMinTextField(), null);

        this.add(getStopSendingYesCheckBox(), null);
        this.add(getStopSendingNoCheckBox(), null);
    }

    /**
     * This method initializes stopYesMinTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getStopYesMinTextField() {
        if (stopYesMinTextField == null) {
            stopYesMinTextField = new JTextField();
            stopYesMinTextField.setBounds(new Rectangle(257, 17, 39, 21));
        }
        return stopYesMinTextField;
    }

    /**
     * This method initializes stopNoMinTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getStopNoMinTextField() {
        if (stopNoMinTextField == null) {
            stopNoMinTextField = new JTextField();
            stopNoMinTextField.setBounds(new Rectangle(257, 61, 39, 21));
        }
        return stopNoMinTextField;
    }

    /**
     * This method initializes stopSendingYesCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getStopSendingYesCheckBox() {
        if (stopSendingYesCheckBox == null) {
            stopSendingYesCheckBox = new JCheckBox();
            stopSendingYesCheckBox.setBounds(new Rectangle(10, 16, 237, 23));
            stopSendingYesCheckBox.setName("");
            stopSendingYesCheckBox.setText("Stop Sending Yes judgements at");
        }
        return stopSendingYesCheckBox;
    }

    /**
     * This method initializes stopSendingNoCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getStopSendingNoCheckBox() {
        if (stopSendingNoCheckBox == null) {
            stopSendingNoCheckBox = new JCheckBox();
            stopSendingNoCheckBox.setBounds(new Rectangle(10, 62, 237, 18));
            stopSendingNoCheckBox.setText("Stop Sending No judgements at");
        }
        return stopSendingNoCheckBox;
    }


    /**
     * Get the No Notification values.
     * 
     * @return
     */
    public JudgementNotification getNoJudgementNotificationFromFields() {
        JudgementNotification newJudgementNotification = new JudgementNotification();

        newJudgementNotification.setCuttoffMinutes(getIntegerValue(getStopNoMinTextField().getText()));
        newJudgementNotification.setNotificationSupressed(getStopSendingNoCheckBox().isSelected());

        return newJudgementNotification;
    }
    
    /**
     * Get the No Notification values.
     * 
     * @return
     */
    public JudgementNotification getYesJudgementNotificationFromFields() {
        JudgementNotification newJudgementNotification = new JudgementNotification();

        newJudgementNotification.setCuttoffMinutes(getIntegerValue(getStopYesMinTextField().getText()));
        newJudgementNotification.setNotificationSupressed(getStopSendingYesCheckBox().isSelected());

        return newJudgementNotification;
    }

    public void setJudgementNotifications(JudgementNotification yesJudgementNotification, JudgementNotification noJudgementNotification) {
        judgementNotificationYes = yesJudgementNotification;
        judgementNotificationNo = noJudgementNotification;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getStopYesMinTextField().setText(Integer.toString(judgementNotificationYes.getCuttoffMinutes()));
                getStopSendingYesCheckBox().setSelected(judgementNotificationYes.isNotificationSupressed());

                getStopNoMinTextField().setText(Integer.toString(judgementNotificationNo.getCuttoffMinutes()));
                getStopSendingNoCheckBox().setSelected(judgementNotificationNo.isNotificationSupressed());
            }
        });
    }

    /**
     * Return int for input string
     * 
     * @param s
     * @return zero if error, otherwise returns value.
     */
    private static int getIntegerValue(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

} // @jve:decl-index=0:visual-constraint="10,10"
