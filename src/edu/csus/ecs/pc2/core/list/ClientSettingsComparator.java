package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientSettings;

/**
 * Order ClientSettings in Site, type, number
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ClientSettingsComparator implements Comparator<ClientSettings>, Serializable {


    /**
     * 
     */
    private static final long serialVersionUID = 8763331558110829051L;

    public int compare(ClientSettings clientSettings1, ClientSettings clientSettings2) {

        int site1 = clientSettings1.getSiteNumber();
        int site2 = clientSettings2.getSiteNumber();

        if (site1 == site2) {
            
            ClientId clientId1 = clientSettings1.getClientId();
            ClientId clientId2 = clientSettings2.getClientId();
            
            if (clientId1.getClientType().equals(clientId2.getClientType())){
                return clientId1.getClientNumber() - clientId2.getClientNumber();
            } else {
                return clientId1.getClientType().compareTo(clientId2.getClientType());
            }
            
        } else {
            return site1 - site2;
        }
    
    }

}
