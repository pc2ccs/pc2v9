package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Problem;

/**
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class EditProblemFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -3349295529036840178L;

    private IModel model;

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

    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.controller = inController;

        getProblemPane().setModelAndController(model, controller);
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
