package edu.csus.ecs.pc2.core.transport;

import java.util.Hashtable;

/**
 * Maintains a list of ConnectionHandler's.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL: http://pc2.ecs.csus.edu/repos/v9wip/trunk/src/edu/csus/ecs/pc2/core/transport/ConnectionHandlerList.java $
public class ConnectionHandlerList {
    public static final String SVN_ID = "$Id: ConnectionHandlerList.java 872 2006-12-08 05:20:08Z laned $";

    private Hashtable<ConnectionHandlerID, ConnectionHandler> hashtable = new Hashtable<ConnectionHandlerID, ConnectionHandler>();

    public ConnectionHandlerList() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ConnectionHandlerList(ConnectionHandlerID key, ConnectionHandler value) {
        super();
        add(key, value);
    }

    public void add(ConnectionHandlerID key, ConnectionHandler value) {
        hashtable.put(key, value);
    }

    public ConnectionHandler get(ConnectionHandlerID key) {
        return (ConnectionHandler) hashtable.get(key);
    }

    public Object remove(ConnectionHandlerID key) {
        return hashtable.remove(key);
    }
}
