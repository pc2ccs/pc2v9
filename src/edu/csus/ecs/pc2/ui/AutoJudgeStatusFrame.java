package edu.csus.ecs.pc2.ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * Auto Judge Status Frame.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AutoJudgeStatusFrame extends javax.swing.JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = -4357639159960022863L;

    private JPanel mainAJStausPane = null;

    private JPanel buttonPane = null;

    private JPanel centerPane = null;

    private JPanel messagePanel = null;

    private JButton stopAutoJudgingButton = null;

    private JLabel messageLabel = null;

    private JLabel stuf = null;

    /**
     * This method initializes
     * 
     */
    public AutoJudgeStatusFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(519, 251));
        this.setContentPane(getMainAJStausPane());
        this.setTitle("Auto Judge Status");

    }

    /**
     * This method initializes mainAJStausPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMainAJStausPane() {
        if (mainAJStausPane == null) {
            mainAJStausPane = new JPanel();
            mainAJStausPane.setLayout(new BorderLayout());
            mainAJStausPane.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
            mainAJStausPane.add(getCenterPane(), java.awt.BorderLayout.CENTER);
            mainAJStausPane.add(getMessagePanel(), java.awt.BorderLayout.NORTH);
        }
        return mainAJStausPane;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            buttonPane = new JPanel();
            buttonPane.setPreferredSize(new java.awt.Dimension(45, 45));
            buttonPane.add(getStopAutoJudgingButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            stuf = new JLabel();
            stuf.setText("Waiting for runs");
            stuf.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            stuf.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 36));
            centerPane = new JPanel();
            centerPane.setLayout(new BorderLayout());
            centerPane.add(stuf, java.awt.BorderLayout.CENTER);
        }
        return centerPane;
    }

    /**
     * This method initializes messagePanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePanel() {
        if (messagePanel == null) {
            messageLabel = new JLabel();
            messageLabel.setText("## - Problem Title  (Run NN, Site YY)");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messageLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
            messagePanel = new JPanel();
            messagePanel.setLayout(new BorderLayout());
            messagePanel.setPreferredSize(new java.awt.Dimension(45, 45));
            messagePanel.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePanel;
    }

    /**
     * This method initializes stopAutoJudgingButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStopAutoJudgingButton() {
        if (stopAutoJudgingButton == null) {
            stopAutoJudgingButton = new JButton();
            stopAutoJudgingButton.setText("Stop AutoJudging");
        }
        return stopAutoJudgingButton;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
