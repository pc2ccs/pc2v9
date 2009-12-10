package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;

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

    private IInternalContest contest;

    private IInternalController controller;

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
        this.setSize(new java.awt.Dimension(549,535));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getProblemPane());
        this.setTitle("New Problem");

        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
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

    /**
     * This is called on a copy, as the fields are still in flux, this is always
     * an "Add New Problem".
     * 
     * @param problem
     * @param problemDataFiles
     */
    public void setProblem(Problem problem, ProblemDataFiles problemDataFiles) {
        setTitle("Add New Problem");
        getProblemPane().setProblem(problem, problemDataFiles);
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
