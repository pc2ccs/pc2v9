package edu.csus.ecs.pc2.core;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;


/**
 * Holds submission counts for clients and elements.
 * 
 * Class holds per Client, counts of a particular elementId.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class SummaryCounts {

    private Hashtable<ClientId, Hashtable<ElementId, Integer>> bigHash = new Hashtable<ClientId, Hashtable<ElementId, Integer>>();

    /**
     * increment by one the count for elements that clientId has.
     * 
     * @param clientId
     * @param elementId
     */
    public void increment(ClientId clientId, ElementId elementId) {

        Hashtable<ElementId, Integer> innerHash = bigHash.get(clientId);
        if (innerHash == null) {
            innerHash = new Hashtable<ElementId, Integer>();
            bigHash.put(clientId, innerHash);
        }

        Integer integer = innerHash.get(elementId);
        if (integer == null) {
            integer = new Integer(1);
        } else {
            integer++;
        }
        innerHash.put(elementId, integer);
    }

    /**
     * Get number of unique element ids that this client has.
     * 
     * @param clientId
     * @return
     */
    public int getCount(ClientId clientId) {
        Hashtable<ElementId, Integer> innerHash = bigHash.get(clientId);
        if (innerHash == null) {
            return 0;
        }
        return innerHash.size();
    }

    /**
     * Get count for elementIds for this client.
     * 
     * @param clientId
     * @param elementId
     * @return
     */
    public int getCount(ClientId clientId, ElementId elementId) {
        Hashtable<ElementId, Integer> innerHash = bigHash.get(clientId);
        if (innerHash == null) {
            return 0;
        }
        Integer integer = innerHash.get(elementId);
        if (integer == null) {
            return 0;
        } else {
            return integer.intValue();
        }
    }

    /**
     * Get list of clients who have elementIds.
     * 
     * @param elementId
     * @return
     */
    public ClientId[] getClients(ElementId elementId) {

        Vector<ClientId> clients = new Vector<ClientId>();

        Enumeration<ClientId> enumeration = bigHash.keys();

        while (enumeration.hasMoreElements()) {
            ClientId element = (ClientId) enumeration.nextElement();

            if (getCount(element, elementId) > 0) {
                clients.addElement(element);
            }
        }

        return (ClientId[]) clients.toArray(new ClientId[clients.size()]);
    }
}
