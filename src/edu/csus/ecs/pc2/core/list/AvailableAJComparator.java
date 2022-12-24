// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.list;

import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.AvailableAJ;
import edu.csus.ecs.pc2.core.model.ClientId;

/**
 * Compare sort by site, clienttype, client number.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class AvailableAJComparator implements Comparator<AvailableAJ> {

    @Override
    public int compare(AvailableAJ availableAJOne, AvailableAJ availableAJTwo) {

        ClientId clientId1 = availableAJOne.getClientId();
        ClientId clientId2 = availableAJTwo.getClientId();

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
