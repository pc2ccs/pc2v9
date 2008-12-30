package edu.csus.ecs.pc2.api.listener;

import edu.csus.ecs.pc2.api.IClient;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ConnectionEvent {
    
    /**
     * Action for ConnectionEvent.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    public enum Action {

        /**
         * Connection Dropped
         */
        DROPPED,
    }

    private Action action;

    private IClient client;

    public ConnectionEvent(Action action, IClient client) {
        super();
        this.action = action;
        this.client = client;
    }

    public Action getAction() {
        return action;
    }

    public IClient getClient() {
        return client;
    }
 
}
