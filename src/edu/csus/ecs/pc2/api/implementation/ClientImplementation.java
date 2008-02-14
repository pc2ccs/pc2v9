package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
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

    private ElementId elementId;
    
    public ClientImplementation(ClientId clientId, IInternalContest contest) {
        Account account = contest.getAccount(clientId);
        if (account != null) {
            shortName = clientId.getName();
            title = account.getDisplayName();
        }
        number = clientId.getClientNumber();
        siteNumber = clientId.getSiteNumber();
        switch (clientId.getClientType()) {
            case JUDGE:
                clientType = ClientType.JUDGE_CLIENT;
                break;
            case SCOREBOARD:
                clientType = ClientType.SCOREBOARD_CLIENT;
                break;
            case TEAM:
            default:
                clientType = ClientType.TEAM_CLIENT;
                break;
        }
        elementId = account.getElementId();
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
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof ClientImplementation) {
            ClientImplementation clientImplementation = (ClientImplementation) obj;
            return (clientImplementation.elementId.equals(elementId));
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return elementId.toString().hashCode();
    }
}
