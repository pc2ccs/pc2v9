package edu.csus.ecs.pc2.api.implementation;

import java.util.Comparator;

import edu.csus.ecs.pc2.api.IClient;

/**
 * Sort ProblemDetails by site #, client #, then {@link edu.csus.ecs.pc2.api.IProblemDetails#getProblemId()}
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ClientComparator implements Comparator<IClient> {

    public int compare(IClient client1, IClient client2) {

        if (client1.getSiteNumber() != client2.getSiteNumber()) {

            if (client1.getAccountNumber() != client2.getAccountNumber()) {
                return client1.getAccountNumber() - client2.getAccountNumber();
            } else {
                return client1.getSiteNumber() - client2.getSiteNumber();
            }
        }

        return client1.getAccountNumber() - client2.getAccountNumber();

    }
}
