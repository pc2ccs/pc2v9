package edu.csus.ecs.pc2.core;

import java.io.Serializable;
import java.util.Properties;

import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public abstract class Plugin implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2400605590293442808L;

    private Properties pluginProperties = new Properties();

    private IInternalController controller;

    private IInternalContest contest;

    /**
     * Provide model and controller information.
     * 
     * The class that invokes this method will pass the contents of the model and controller to the class that implements this method.
     * 
     * @param inContest
     *            contest data
     * @param inController
     *            contest controller
     */
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.controller = inController;
        this.contest = inContest;
    }

    public void setContest(IInternalContest contest) {
        this.contest = contest;
    }

    public void setController(IInternalController controller) {
        this.controller = controller;
    }

    public IInternalController getController() {
        return controller;
    }

    public IInternalContest getContest() {
        return contest;
    }

    /**
     * @return name of this plugin, used in choosing plugin.
     */
    public abstract String getPluginTitle();

    /**
     * Get Plugin Properties
     * 
     * @return
     */
    public Properties getPluginProperties() {
        return pluginProperties;
    }

    public void addProperty(Object key, Object value) {
        pluginProperties.put(key, value);
    }

    /**
     * Provide a way to for the plugin to cleanup itself.
     */
    public abstract void dispose();

}
