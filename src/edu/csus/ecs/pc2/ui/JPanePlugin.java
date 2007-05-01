package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IContest;

/**
 * Base class for most UIPlugin panes.
 * 
 * 
 * @author pc2@ecs.csus.edu
 */
// $HeadURL$

public abstract class JPanePlugin extends JPanel implements UIPlugin {

    private IController controller;

    private IContest model;
    
    private JFrame parentFrame = null;

    public void setModelAndController(IContest inModel, IController inController) {
        this.controller = inController;
        this.model = inModel;
    }
    

    public abstract String getPluginTitle();

    public IController getController() {
        return controller;
    }

    public void setController(IController controller) {
        this.controller = controller;
    }

    public IContest getModel() {
        return model;
    }

    public void setModel(IContest model) {
        this.model = model;
    }


    public JFrame getParentFrame() {
        return parentFrame;
    }


    public void setParentFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }
}
