package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.ServerConnection;

/**
 * 
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface APIPlugin {

    /**
     * Set contest and server connection (controller).
     * 
     * @param inServerConnection
     * @param inContest
     */
    void setContestAndServerConnection(ServerConnection inServerConnection, IContest inContest);

    /**
     * @return name of this plugin, used in choosing plugin.
     */
    String getPluginTitle();
}
