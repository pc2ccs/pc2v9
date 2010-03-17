package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.ClientId;

/**
 * Sort ClientIds by site, type and id.
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ClientIdComparator implements Comparator<ClientId>, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 6684993280342683013L;

    public int compare(ClientId clientId1, ClientId clientId2) {

        int site1 = clientId1.getSiteNumber();
        int site2 = clientId2.getSiteNumber();

        if (site1 == site2) {

            if (clientId1.getClientType().equals(clientId2.getClientType())) {
                return clientId1.getClientNumber() - clientId2.getClientNumber();
            } else {
                return clientId1.getClientType().compareTo(clientId2.getClientType());
            }

        } else {
            return site1 - site2;
        }
    }
}
