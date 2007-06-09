package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * Edit Problem.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// $Id$

public class EditProblemFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -5684386608226303728L;

    private IContest contest;

    private IController controller;

    private ProblemPane problemPane = null;

    /**
     * This method initializes
     * 
     */
    public EditProblemFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(549, 433));
        this.setContentPane(getProblemPane());
        this.setTitle("New Problem");

        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;

        getProblemPane().setContestAndController(contest, controller);
        problemPane.setParentFrame(this);

    }

    public void setProblem(Problem problem) {
        if (problem == null) {
            setTitle("Add New Problem");
        } else {
            setTitle("Edit Problem " + problem.getDisplayName());
        }
        getProblemPane().setProblem(problem);
    }

    public String getPluginTitle() {
        return "Edit Problem Frame";
    }

    /**
     * This method initializes problemPane
     * 
     * @return edu.csus.ecs.pc2.ui.ProblemPane
     */
    private ProblemPane getProblemPane() {
        if (problemPane == null) {
            problemPane = new ProblemPane();
        }
        return problemPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
