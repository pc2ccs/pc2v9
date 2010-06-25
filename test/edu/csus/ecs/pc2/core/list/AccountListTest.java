package edu.csus.ecs.pc2.core.list;

import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * Test AccountList.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AccountListTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCreate() throws Exception {

        AccountList accountList = new AccountList();

        int siteNumber = 3;
        int numberJudges = 12;

        accountList.generateNewAccounts(Type.JUDGE, numberJudges, PasswordType.JOE, siteNumber, true);

        Account[] list = accountList.getList();

        assertTrue("Should be " + numberJudges + " Judge accounts", list.length == numberJudges);

        Vector<Account> judges = accountList.getAccounts(Type.JUDGE);
        assertTrue("Should be " + numberJudges + " Judge accounts", judges.size() == numberJudges);

        Vector<Account> admins = accountList.getAccounts(Type.ADMINISTRATOR);
        assertFalse("Should not be 1 admin accounts", admins.size() == 1);

        Vector<Account> judgesAt3 = accountList.getAccounts(Type.JUDGE, siteNumber);
        assertTrue("Should be " + numberJudges + " Judge accounts", judgesAt3.size() == numberJudges);

        Vector<Account> judgesAt1 = accountList.getAccounts(Type.JUDGE, 1);
        assertTrue("Should be no Judge accounts site 1", judgesAt1.size() == 0);

    }

}
