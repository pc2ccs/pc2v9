package edu.csus.ecs.pc2.api.implementation;

import java.util.Comparator;

import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IProblemDetails;

/**
 * Sort ProblemDetails by site #, client #, then {@link IProblemDetails#getProblemId()}
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProblemDetailsComparator implements Comparator<IProblemDetails> {

    public int compare(IProblemDetails detail1, IProblemDetails detail2) {
        
        IClient client1 = detail1.getClient();
        IClient client2 = detail2.getClient();

        if (client1.getSiteNumber() != client2.getSiteNumber()) {

            if (client1.getAccountNumber() != client2.getAccountNumber()) {
                return client1.getAccountNumber() - client2.getAccountNumber();
            } else {
                return client1.getSiteNumber() - client2.getSiteNumber();
            }
        }

        return detail1.getProblemId() - detail2.getProblemId();
    }

}
