// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * This class displays a frame holding the results of running an Input Validator on the problem data files.
 * 
 * @author John Clevenger, PC2 Development Team (pc2@ecs.csus.edu)
 */

public class InputValidationResultFrame extends JFrame implements UIPlugin {

    private static final long serialVersionUID = 1L;

    private IInternalContest contest;
    private IInternalController controller;

    private InputValidationResultPane resultsPane = null;

    private JPanel buttonPanel;

    private JButton closeButton;

    /**
     * This method initializes
     * 
     */
    public InputValidationResultFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(900, 800));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        this.add(getInputValidationResultPane(),BorderLayout.CENTER);
        this.add(getButtonPanel(),BorderLayout.SOUTH);
        this.setTitle("Input Validation Results");

        FrameUtilities.centerFrame(this);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

        getInputValidationResultPane().setContestAndController(contest, controller);
        resultsPane.setParentFrame(this);

    }

    public String getPluginTitle() {
        return "Edit Problem Frame";
    }

    /**
     * This method initializes resultsPane, and acts as a public accessor to the resultsPane.
     * 
     * @return edu.csus.ecs.pc2.ui.ProblemPane
     */
    public InputValidationResultPane getInputValidationResultPane() {
        if (resultsPane == null) {
            resultsPane = new InputValidationResultPane();
        }
        return resultsPane;
    }
    
    JPanel getButtonPanel() {
        if (buttonPanel==null) {
            buttonPanel = new JPanel();
            buttonPanel.add(getCloseButton()) ;
        }
        return buttonPanel;
    }
    
    JButton getCloseButton() {
        if (closeButton==null) {
            closeButton = new JButton("Close");
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
        }
        return closeButton;
    }
    //main() method for testing only
    public static void main (String [] args) {
        InputValidationResultFrame frame = new InputValidationResultFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
