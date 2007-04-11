package edu.csus.ecs.pc2.ui;

import javax.swing.JPanel;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IModel;

/**
 * Base JPanel class that can be added a JFrame.
 * 
 * 
 * @author pc2@ecs.csus.edu
 */
// $HeadURL$

public abstract class JPanePlugin extends JPanel implements UIPlugin {

    private IController controller;

    private IModel model;

    public void setModelAndController(IModel inModel, IController inController) {
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

    public IModel getModel() {
        return model;
    }

    public void setModel(IModel model) {
        this.model = model;
    }
}
