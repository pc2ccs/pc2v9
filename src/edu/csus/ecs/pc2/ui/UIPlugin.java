package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IModel;

/**
 * Interface for plugin UI or GUI.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface UIPlugin {

    /**
     * set the Model and Controller.
     * 
     * @param model
     *            contest data
     * @param controller
     *            contest controller
     */
    void setModelController(IModel model, IController controller);

    /**
     * @return name of this plugin, used in choosing plugin.
     */
    String getPluginTitle();

}
