package edu.csus.ecs.pc2.core.list;

import java.io.Serializable;
import java.util.Comparator;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;

/**
 * Account Comparator, Order the accounts by site #, client Type, then client  #.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class AccountComparator implements Comparator<Account>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6254851774817221276L;
    public static final String SVN_ID = "$Id$";

    public int compare(Account account1, Account account2) {

        // sort by site id, clientType, clientNumber

        int site1 = account1.getSiteNumber();
        int site2 = account2.getSiteNumber();

        if (site1 == site2) {
            
            ClientId clientId1 = account1.getClientId();
            ClientId clientId2 = account2.getClientId();
            
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
