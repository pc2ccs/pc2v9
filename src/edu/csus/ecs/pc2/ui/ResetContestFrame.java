package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Reset Contest Dialog.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ResetContestFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 928051966964037016L;

    private JPanel buttonPane = null;

    private JPanel centerPane = null;

    private JButton resetButton = null;

    private JButton cancelButton = null;

    private JCheckBox removeProblemDefsCheckBox = null;

    private JCheckBox removeLanguageDefintions = null;

    private JLabel label1 = null;

    private JLabel label2 = null;

    private JPanel mainPanel = null;

    private IInternalContest contest;

    private IInternalController controller;

    /**
     * This method initializes
     * 
     */
    public ResetContestFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(495, 248));
        this.setContentPane(getMainPanel());
        this.setTitle("Reset Contest");

    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(45);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.setPreferredSize(new Dimension(35, 35));
            buttonPane.add(getResetButton(), null);
            buttonPane.add(getCancelButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes centerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCenterPane() {
        if (centerPane == null) {
            label2 = new JLabel();
            label2.setBounds(new Rectangle(35, 16, 411, 26));
            label2.setFont(new Font("Dialog", Font.BOLD, 14));
            label2.setHorizontalAlignment(SwingConstants.CENTER);
            label2.setText(" Reset will erase all runs, clarifications ");
            label1 = new JLabel();
            label1.setBounds(new Rectangle(36, 58, 411, 26));
            label1.setFont(new Font("Dialog", Font.BOLD, 14));
            label1.setHorizontalAlignment(SwingConstants.CENTER);
            label1.setText("and reset the contest time.");
            centerPane = new JPanel();
            centerPane.setLayout(null);
            centerPane.add(getRemoveProblemDefsCheckBox(), null);
            centerPane.add(getRemoveLanguageDefintions(), null);
            centerPane.add(label1, null);
            centerPane.add(label2, null);
        }
        return centerPane;
    }

    /**
     * This method initializes resetButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getResetButton() {
        if (resetButton == null) {
            resetButton = new JButton();
            resetButton.setText("Reset");
            resetButton.setMnemonic(KeyEvent.VK_R);
            resetButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    confirmReset();
                }
            });
        }
        return resetButton;
    }

    protected void confirmReset() {

        String stopMessage = "Reset will erase all runs and clarifications, and will reset the contest time.\n" + "Are you sure you want to reset this site?";

        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, stopMessage)) {
            
            boolean resetProblemDefs = getRemoveProblemDefsCheckBox().isSelected();
            boolean resetLanguageDefs = getRemoveLanguageDefintions().isSelected();
            
            controller.resetContest(contest.getClientId(), resetProblemDefs, resetLanguageDefs);
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
            cancelButton.setMnemonic(KeyEvent.VK_C);
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    setVisible(false);
                }
            });
        }
        return cancelButton;
    }

    /**
     * This method initializes removeProblemDefsCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getRemoveProblemDefsCheckBox() {
        if (removeProblemDefsCheckBox == null) {
            removeProblemDefsCheckBox = new JCheckBox();
            removeProblemDefsCheckBox.setBounds(new Rectangle(116, 100, 272, 21));
            removeProblemDefsCheckBox.setText("Remove problem definitions");
        }
        return removeProblemDefsCheckBox;
    }

    /**
     * This method initializes removeLanguageDefintions
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getRemoveLanguageDefintions() {
        if (removeLanguageDefintions == null) {
            removeLanguageDefintions = new JCheckBox();
            removeLanguageDefintions.setBounds(new Rectangle(116, 137, 278, 24));
            removeLanguageDefintions.setText("Remove language definitions");
        }
        return removeLanguageDefintions;
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;
    }

    public String getPluginTitle() {
        return "Reset Dialog";
    }

    /**
     * This method initializes mainPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(getButtonPane(), BorderLayout.SOUTH);
            mainPanel.add(getCenterPane(), BorderLayout.CENTER);
        }
        return mainPanel;
    }
    
} // @jve:decl-index=0:visual-constraint="10,10"
