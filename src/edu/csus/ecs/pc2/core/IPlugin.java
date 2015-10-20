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
    void setContestAndController(IInternalContest inContest, IInternalController inController);
    
    void setContest(IInternalContest contest);

    void setController(IInternalController controller);

    IInternalController getController();

    IInternalContest getContest();
    
    Log getLog();

    /**
     * Get Plugin Properties
     * 
     * @return
     */
    Properties getPluginProperties();

    void addProperty(Object key, Object value);
    
    /**
     * Version for this plugin.
     * @return
     */
    String getVersion();
    
    /**
     * Author's URL/home page.
     */
    String getAuthorURL();
    
    /**
     * Author's email address.
     */
    String getAuthorEmailAddress();
    
    /**
     * Title for plugin to show to users.
     */
    String getTitle();

    /**
     * Description of plugin to show to users.
     */
    String getDescription();

    /**
     * Name of Author. 
     */
    String getAuthorName();

}
