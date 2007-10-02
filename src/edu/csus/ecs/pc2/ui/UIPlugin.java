package edu.csus.ecs.pc2.ui;

import java.io.Serializable;

import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.model.IContest;

/**
 * Interface for plugin UI or GUI.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface UIPlugin extends Serializable {

    /**
     * Provide model and controller information.
     * 
     * The class that invokes this method will pass the
     * contents of the model and controller to the class
     * that implements this method.
     * 
     * @param inContest
     *            contest data
     * @param inController
     *            contest controller
     */
    void setContestAndController(IContest inContest, IController inController);

    /**
     * @return name of this plugin, used in choosing plugin.
     */
    String getPluginTitle();

}
