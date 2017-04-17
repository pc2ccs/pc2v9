package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * A JFrame to hold a RunInputValidators Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
// $Id$

public class RunInputValidatorsFrame extends JFrame implements UIPlugin {

    private static final long serialVersionUID = 1;

    private IInternalContest contest;

    private IInternalController controller;

    private RunInputValidatorsPane runInputValidatorsPane = null;

    /**
     * Constructs a JFrame containing a {@link RunInputValidatorsPane}.
     * 
     */
    public RunInputValidatorsFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this JFrame.
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(800, 700));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getRunInputValidatorsPane());
        this.setTitle("Run Input Validators");

        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

        getRunInputValidatorsPane().setContestAndController(contest, controller);
        getRunInputValidatorsPane().setParentFrame(this);

    }

    public String getPluginTitle() {
        return "Run Input Validators Frame";
    }

    /**
     * This method creates and initializes a RunInputValidators pane.
     * 
     * @return edu.csus.ecs.pc2.ui.RunInputValidatorsPane
     */
    private RunInputValidatorsPane getRunInputValidatorsPane() {
        if (runInputValidatorsPane == null) {
            runInputValidatorsPane = new RunInputValidatorsPane();
        }
        return runInputValidatorsPane;
    }

}
