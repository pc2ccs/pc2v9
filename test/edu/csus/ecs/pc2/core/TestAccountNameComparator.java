package edu.csus.ecs.pc2.core;

import java.util.Arrays;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.AccountNameComparator;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType;

/**
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class TestAccountNameComparator extends TestCase {

    public void testOrder() {

        int numberAccounts = 121;

        AccountList accountList = new AccountList();

        accountList.generateNewAccounts(ClientType.Type.TEAM, numberAccounts, 1, PasswordType.JOE, 12, true);

        Account[] accounts = accountList.getList();

        String[] names = new String[accounts.length];

        for (int j = 0; j < accounts.length; j++) {
            names[j] = accounts[j].getDisplayName();
        }

        // Regular string sort
        Arrays.sort(names);

        // sort by display name
        Arrays.sort(names, new AccountNameComparator());

        for (int j = 0; j < accounts.length; j++) {
            assertTrue("Sort failed on display name " + names[j] + " expecting " + (j + 1), names[j].equalsIgnoreCase("team" + (j + 1)));
        }
    }
}
