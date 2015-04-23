package edu.csus.ecs.pc2.core.model;

import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;

/**
 * Site JUnit Test.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class AccountListTest extends TestCase {

    private int totAccounts = 0;

    private int numAccounts = 0;

    private AccountList accountList = new AccountList();

    protected void setUp() throws Exception {
        super.setUp();

    }

    public void testGenOneTeam() {

        numAccounts = 1;
        Vector<Account> accounts = accountList.generateNewAccounts(ClientType.Type.TEAM, numAccounts, PasswordType.JOE, 1, true);
        
        String password = accounts.firstElement().getClientId().getName();
        assertEquals(password,accounts.firstElement().getPassword());

        totAccounts += numAccounts;
        assertTrue("Did not create " + numAccounts + " team accounts", accountList.getAccounts(ClientType.Type.TEAM).size() == numAccounts);
        assertTrue("Did not create " + totAccounts + " accounts", accountList.getAccounts(ClientType.Type.ALL).size() == totAccounts);
        
        testExternalIdAccounts(accounts);
    }

    public void testAddJudgeAccounts() {

        numAccounts = 4;
        Vector<Account> accounts =accountList.generateNewAccounts(ClientType.Type.JUDGE, numAccounts, PasswordType.JOE, 1, true);

        String password = accounts.firstElement().getClientId().getName();
        assertEquals(password,accounts.firstElement().getPassword());

        totAccounts += numAccounts;
        assertTrue("Did not create " + numAccounts + " judge accounts", accountList.getAccounts(ClientType.Type.JUDGE).size() == numAccounts);
        assertTrue("Did not create " + totAccounts + " accounts", accountList.getAccounts(ClientType.Type.ALL).size() == totAccounts);
        
        testExternalIdAccounts(accounts);
    }

    public void testNoAdminAccount() {
        numAccounts = 0;
        assertTrue("Did not create " + numAccounts + " judge accounts", accountList.getAccounts(ClientType.Type.SCOREBOARD).size() == numAccounts);
        assertTrue("Did not create " + totAccounts + " accounts", accountList.getAccounts(ClientType.Type.ALL).size() == totAccounts);

    }

    public void testFiveScoreboards() {
        numAccounts = 4;
        Vector<Account> accounts =accountList.generateNewAccounts(ClientType.Type.SCOREBOARD, numAccounts, PasswordType.JOE, 1, true);

        String password = accounts.firstElement().getClientId().getName();
        assertEquals(password,accounts.firstElement().getPassword());

        
        totAccounts += numAccounts;
        assertTrue("Did not create " + numAccounts + " judge accounts", accountList.getAccounts(ClientType.Type.SCOREBOARD).size() == numAccounts);
        assertTrue("Did not create " + totAccounts + " accounts", accountList.getAccounts(ClientType.Type.ALL).size() == totAccounts);
        
        testExternalIdAccounts(accounts);
    }
    
    
    private void testExternalIdAccounts(Vector<Account> accounts) {
        for (Account account : accounts) {
            assertElementIdCorrect(account);
        }
    }

    private long generateExternalId(Account account) {
        
        return AccountList.generateExternalId(account);

    }
    
    private void assertElementIdCorrect (Account account){
        assertEquals("Expecting external id ", ""+ generateExternalId(account), account.getExternalId());
    }

}
