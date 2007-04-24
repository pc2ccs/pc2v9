package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class EditRunFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -3349295529036840178L;

    private IModel model;

    private IController controller;

    private RunPane runPane = null;

    /**
     * This method initializes
     * 
     */
    public EditRunFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(549, 278));
        this.setContentPane(getRunPane());
        this.setTitle("New Run");

        FrameUtilities.centerFrame(this);

    }

    public void setModelAndController(IModel inModel, IController inController) {
        this.model = inModel;
        this.controller = inController;

        getRunPane().setModelAndController(model, controller);
        getRunPane().setParentFrame(this);
    }

    public void setRun(Run run) {
        if (run == null) {
            setTitle("Add New Run");
        } else {
            setTitle("Edit Run " + run.getNumber() + " (Site " + run.getSiteNumber() + ")");
        }
        getRunPane().setRun(run);
    }

    public String getPluginTitle() {
        return "Edit Run Frame";
    }

    /**
     * This method initializes runPane
     * 
     * @return edu.csus.ecs.pc2.ui.RunPane
     */
    private RunPane getRunPane() {
        if (runPane == null) {
            runPane = new RunPane();
        }
        return runPane;
    }

} // @jve:decl-index=0:visual-constraint="10,10"
