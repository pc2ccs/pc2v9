package edu.csus.ecs.pc2.core.list;

import java.util.Date;
import java.util.Hashtable;

import edu.csus.ecs.pc2.core.transport.ConnectionHandlerID;

/**
 * Maintains a list of {@link edu.csus.ecs.pc2.core.transport.ConnectionHandlerID}s.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class ConnectionHandlerList {
    public static final String SVN_ID = "$Id$";

    private Hashtable<ConnectionHandlerID, Date> hashtable = new Hashtable<ConnectionHandlerID, Date>();

    public ConnectionHandlerList() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void add(ConnectionHandlerID key, Date value) {
        hashtable.put(key, value);
    }

    public Date get(ConnectionHandlerID key) {
        return (Date) hashtable.get(key);
    }

    public Object remove(ConnectionHandlerID key) {
        return hashtable.remove(key);
    }

    public ConnectionHandlerID[] getList() {
        return (ConnectionHandlerID[]) hashtable.keySet().toArray(new ConnectionHandlerID[hashtable.size()]);
    }
}
