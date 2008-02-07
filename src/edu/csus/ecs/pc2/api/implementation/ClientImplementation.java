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

    private int number;

    private int siteNumber;

    private ClientType clientType;
    
    public ClientImplementation(ClientId clientId, IInternalContest contest) {
        Account account = contest.getAccount(clientId);
        if (account != null) {
            shortName = clientId.getName();
            title = account.getDisplayName();
        }
        number = clientId.getClientNumber();
        siteNumber = clientId.getSiteNumber();
        //TODO: set the clientType;
    }

    public String getLoginName() {
        return shortName;
    }

    public String getDisplayName() {
        return title;
    }

    public int getSiteNumber() {
        return siteNumber;
    }

    public int getAccountNumber() {
        return number;
    }

    public ClientType getType() {
        return clientType;
    }
}
