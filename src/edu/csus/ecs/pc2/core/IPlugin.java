package edu.csus.ecs.pc2.core;

import java.util.Properties;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Plugin interface/methods.
 *
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IPlugin {

    /**
     * Set model and controller.
     * 
     * The class that invokes this method will pass the contents of the model and controller to the class that implements this method.
     * 
     * @param inContest
     *            contest data
     * @param inController
     *            contest controller
     */
    public void setContestAndController(IInternalContest inContest, IInternalController inController);
    
    public void setContest(IInternalContest contest);

    public void setController(IInternalController controller);

    public IInternalController getController();

    public IInternalContest getContest();
    
    public Log getLog();

    /**
     * Get Plugin Properties
     * 
     * @return
     */
    public Properties getPluginProperties();

    public void addProperty(Object key, Object value);
    
    /**
     * Version for this plugin.
     * @return
     */
    public String getVersion();
    
    /**
     * Author's URL/home page.
     */
    public String getAuthorURL();
    
    /**
     * Author's email address.
     */
    public String getAuthorEmailAddress();
    
    /**
     * Title for plugin to show to users.
     */
    public String getTitle();

    /**
     * Description of plugin to show to users.
     */
    public String getDescription();

    /**
     * Name of Author. 
     */
    public String getAuthorName();

}
