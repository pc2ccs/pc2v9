package edu.csus.ecs.pc2.core;

import java.io.Serializable;
import java.util.Properties;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * An parent class for plugins.
 * 
 * Provides storage for data common to plugins.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public abstract class Plugin implements IPlugin, Serializable {

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
    
    public Log getLog() {
        return getController().getLog();
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

    /**
     * Make these method abstract so if other developers create plugins
     * they can fill in these fields.
     */
    //  TODO plugin make method abstract getAuthorEmailAddress
    //  TODO plugin make method abstract getAuthorURL
    //  TODO plugin make method abstract getAuthorName
    //  TODO plugin make method abstract getDescription
    //  TODO plugin make method abstract getTitle
    //  TODO plugin make method abstract getVersion
    
    @Override
    public String getAuthorEmailAddress() {
        return "pc2@ecs.csus.edu";
    }
    
    @Override
    public String getAuthorURL() {
        return "http://pc2.ecs.csus.edu";
    }
    
    @Override
    public String getAuthorName() {
        return "CSUS pc2 development team";
    }

    @Override
    public String getDescription() {
        return "Generic Plugin Title";
    }
    
    @Override
    public String getTitle() {
        return "Generic Plugin Title";
    }
    
    @Override
    public String getVersion() {
        return "9.3";
    }

}
