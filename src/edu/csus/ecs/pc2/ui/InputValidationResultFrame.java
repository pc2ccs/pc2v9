// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;

import javax.swing.JFrame;

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
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getInputValidationResultPane());
        this.setTitle("Input Validation Results");

        FrameUtilities.centerFrame(this);
        this.setVisible(true);

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
    
    //main() method for testing only
    public static void main (String [] args) {
        InputValidationResultFrame frame = new InputValidationResultFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
