// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
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

    private EditProblemPane editProblemPane = null;

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
        this.setSize(new Dimension(900, 800));
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setContentPane(getEditProblemPane());
        this.setTitle("New Problem");

        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

        getEditProblemPane().setContestAndController(contest, controller);
        editProblemPane.setParentFrame(this);

    }

    public void setProblem(Problem problem) {
        if (problem == null) {
            setTitle("Add New Problem");
        } else {
            setTitle("Edit Problem " + problem.getDisplayName());
            if (problem.isUsingExternalDataFiles()) {
                setTitle("Edit Problem " + problem.getDisplayName()+" [External Data]" );
            }
            
        }
        getEditProblemPane().setProblem(problem);
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
            getEditProblemPane().setProblem(problem);
        } else {
            setTitle("Edit Problem " + problem.getDisplayName());
            if (problem.isUsingExternalDataFiles()) {
                setTitle("Edit Problem " + problem.getDisplayName()+" ("+problem.getExternalDataFileLocation()+")");
            }  
            getEditProblemPane().setProblem(problem, problemDataFiles);
        }
    }

    public String getPluginTitle() {
        return "Edit Problem Frame";
    }

    /**
     * This method returns a singleton instance of an {@link EditProblemPane}.
     * If no instance has yet been created then one is created.
     * 
     * @return edu.csus.ecs.pc2.ui.EditProblemPane
     */
    private EditProblemPane getEditProblemPane() {
        if (editProblemPane == null) {
            editProblemPane = new EditProblemPane();
        }
        return editProblemPane;
    }

    public void setProblemCopy(Problem problem, ProblemDataFiles problemDataFiles) {
        setTitle("Add New Problem");
        getEditProblemPane().setProblem(problem, problemDataFiles);
    }

} // @jve:decl-index=0:visual-constraint="10,10"
