package edu.csus.ecs.pc2.ui;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IContest;

/**
 * Interface for plugin UI or GUI.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public interface UIPlugin {

    /**
     * Provide model and controller information.
     * 
     * The class that invokes this method will pass the
     * contents of the model and controller to the class
     * that implements this method.
     * 
     * @param model
     *            contest data
     * @param controller
     *            contest controller
     */
    void setModelAndController(IContest model, IController controller);

    /**
     * @return name of this plugin, used in choosing plugin.
     */
    String getPluginTitle();

}
