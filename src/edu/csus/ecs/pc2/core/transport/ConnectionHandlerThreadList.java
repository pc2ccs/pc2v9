package edu.csus.ecs.pc2.core.transport;

import java.util.Hashtable;

/**
 * Maintains a list of ConnectionHandlerThread's.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ConnectionHandlerThreadList {
    public static final String SVN_ID = "$Id$";

    private Hashtable<ConnectionHandlerID, ConnectionHandlerThread> hashtable = new Hashtable<ConnectionHandlerID, ConnectionHandlerThread>();

    public ConnectionHandlerThreadList() {
        super();
    }

    public ConnectionHandlerThreadList(ConnectionHandlerID key, ConnectionHandlerThread value) {
        super();
        add(key, value);
    }

    public void add(ConnectionHandlerID key, ConnectionHandlerThread value) {
        hashtable.put(key, value);
    }

    public ConnectionHandlerThread get(ConnectionHandlerID key) {
        return (ConnectionHandlerThread) hashtable.get(key);
    }

    public Object remove(ConnectionHandlerID key) {
        return hashtable.remove(key);
    }
}
