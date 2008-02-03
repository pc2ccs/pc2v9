package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * API IClient implementation.  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ClientImplementation implements IClient {

    private String shortName;

    private String title;

    private int clientNumber;

    private int siteNumber;

    public ClientImplementation(ClientId clientId, IInternalContest contest) {
        Account account = contest.getAccount(clientId);
        if (account != null) {
            shortName = clientId.getName();
            title = account.getDisplayName();
        }
        clientNumber = clientId.getClientNumber();
        siteNumber = clientId.getSiteNumber();
    }

    public String getShortName() {
        return shortName;
    }

    public String getTitle() {
        return title;
    }

    public int getSiteNumber() {
        return siteNumber;
    }

    public int getClientNumber() {
        return clientNumber;
    }

}
