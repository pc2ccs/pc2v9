package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.core.model.JudgementNotification;

/**
 * Test Pane for JudgementNotification.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class NotificationPaneTestFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = -3735898874290491780L;

    private JPanel mainFrame = null;

    private JPanel buttonPane = null;

    private NotificationPane notificationPane = null;

    private JButton showValuesButton = null;

    /**
     * This method initializes
     * 
     */
    public NotificationPaneTestFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(613, 211));
        this.setTitle("Test NotificationSettings Pane");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(getMainFrame());

    }

    /**
     * This method initializes mainFrame
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainFrame() {
        if (mainFrame == null) {
            mainFrame = new JPanel();
            mainFrame.setLayout(new BorderLayout());
            mainFrame.add(getButtonPane(), BorderLayout.SOUTH);
            mainFrame.add(getNotificationPane(), BorderLayout.CENTER);
        }
        return mainFrame;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout());
            buttonPane.add(getShowValuesButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes notificationPane
     * 
     * @return edu.csus.ecs.pc2.ui.NotificationPane
     */
    private NotificationPane getNotificationPane() {
        if (notificationPane == null) {
            notificationPane = new NotificationPane();
        }
        return notificationPane;
    }

    /**
     * This method initializes showValuesButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getShowValuesButton() {
        if (showValuesButton == null) {
            showValuesButton = new JButton();
            showValuesButton.setText("Dump");
            showValuesButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    dumpFields();
                }
            });
        }
        return showValuesButton;
    }

    protected void dumpFields() {

        JudgementNotification judgementNotification;

        judgementNotification = getNotificationPane().getYesJudgementNotificationFromFields();
        System.out.println("          Send Yes " + judgementNotification.isNotificationSupressed() + " cuttoff at " + judgementNotification.getCuttoffMinutes());

        judgementNotification = getNotificationPane().getNoJudgementNotificationFromFields();
        System.out.println("          Send No  " + judgementNotification.isNotificationSupressed() + " cuttoff at " + judgementNotification.getCuttoffMinutes());
        System.out.println();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        NotificationPaneTestFrame frame = new NotificationPaneTestFrame();
        FrameUtilities.centerFrame(frame);
        frame.setVisible(true);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
