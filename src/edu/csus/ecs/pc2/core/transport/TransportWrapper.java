package edu.csus.ecs.pc2.core.transport;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * Transport Wrapper package used to transmit an encapsulated object.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class TransportWrapper implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -7482263258599397916L;

    public static final String SVN_ID = "$Id$";

    private Hashtable<String, Serializable> hashtable = new Hashtable <String, Serializable>();

    public TransportWrapper() {
        super();
    }

    public TransportWrapper(String name, Serializable value) {
        super();
        add(name, value);
    }

    public void add(String name, Serializable value) {
        hashtable.put(name, value);
    }

    public Object get(String name) {
        return hashtable.get(name);
    }

    public Object remove(String name) {
        return hashtable.remove(name);
    }
}
