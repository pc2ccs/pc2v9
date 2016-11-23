package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import java.awt.Dimension;

/**
 * Edit Problem.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
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

    private EditProblemPane problemPane = null;

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
        this.setSize(new Dimension(800, 600));
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
            if (problem.isUsingExternalDataFiles()) {
                setTitle("Edit Problem " + problem.getDisplayName()+" [External/CCS]" );
            }
            
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
        if (problem == null){
            setTitle("Add New Problem");
            getProblemPane().setProblem(problem);
        } else {
            setTitle("Edit Problem " + problem.getDisplayName());
            if (problem.isUsingExternalDataFiles()) {
                setTitle("Edit Problem " + problem.getDisplayName()+" ("+problem.getExternalDataFileLocation()+")");
            }  
            getProblemPane().setProblem(problem, problemDataFiles);
        }
    }

    public String getPluginTitle() {
        return "Edit Problem Frame";
    }

    /**
     * This method initializes problemPane
     * 
     * @return edu.csus.ecs.pc2.ui.ProblemPane
     */
    private EditProblemPane getProblemPane() {
        if (problemPane == null) {
            problemPane = new EditProblemPane();
        }
        return problemPane;
    }

    public void setProblemCopy(Problem problem, ProblemDataFiles problemDataFiles) {
        setTitle("Add New Problem");
        getProblemPane().setProblem(problem, problemDataFiles);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
