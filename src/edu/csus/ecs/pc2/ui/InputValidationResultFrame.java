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
 * The result data displayed by this pane (actually, by the {@link InputValidationResultPane} contained
 * within this frame) is fetched from the "parent pane" of this frame -- meaning that it is the responsibility
 * of the class which instantiates this frame to call this frame's {@link #setParentPane()} method to set the
 * reference back to the parent pane holding the Input Validation Results.
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
    
    private JPanePlugin parentPane;

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

    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

        getInputValidationResultPane().setContestAndController(contest, controller);

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
            resultsPane.setParentFrame(this);
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
    
    /**
     * Returns the pane which is the parent of this frame -- typically, an {@link InputValidatorPane} which 
     * created this InputValidationResultFrame and which holds the results of executing an Input Validator.
     * 
     * @return the parent pane of this frame.
     */
    public JPanePlugin getParentPane() {
        return parentPane;
    }
    
    /**
     * Sets the reference to the parent pane of this frame -- that is, a reference to the pane which
     * created this frame and which holds the Input Validation results to be displayed by this frame.
     * 
     * @param parentPane a JPanePlugin containing the Input Validation results to be displayed by this frame.
     */
    public void setParentPane(JPanePlugin parentPane) {
        this.parentPane = parentPane;
    }
    
    //main() method for testing only
    public static void main (String [] args) {
        InputValidationResultFrame frame = new InputValidationResultFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
