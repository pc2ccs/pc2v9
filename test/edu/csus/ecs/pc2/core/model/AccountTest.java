package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * JUnit test for Account class.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AccountTest extends AbstractTestCase {

    private Account account = null;

    private int siteNumber = 22;

    private Type clientType = Type.JUDGE;

    private String testPassword = "passwordFiftyFive";

    public AccountTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
        ClientId clientId = new ClientId(siteNumber, clientType, 3);
        account = new Account(clientId, testPassword, clientId.getSiteNumber());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'edu.csus.ecs.pc2.core.model.Account.setPassword(String)'
     */
    public void testSetPassword() {

        String password = "foo";
        account.setPassword(password);

        assertEquals(password, account.getPassword());
        assertNotSame(password + "A", account.getPassword());
    }
    
    public void testExternalId() throws Exception {

//        System.out.println("debug " + account.getExternalId());
        assertElementIdCorrect(account);
    }

    public void testPassword() {

        String password = "admin904";

        ClientId clientId = new ClientId(siteNumber, Type.ADMINISTRATOR, 3);
        Account account2 = new Account(clientId, password, clientId.getSiteNumber());

        String newPasswprd = password;

        assertEquals(newPasswprd, account2.getPassword());
        assertNotSame(newPasswprd + "A", account2.getPassword());

    }
    
    private void assertElementIdCorrect (Account anAccount){
        assertEquals("Expecting external id ", ""+ AccountList.generateExternalId(anAccount), anAccount.getExternalId());
    }

    public void testSiteNumber() {

        assertEquals(siteNumber, account.getSiteNumber());
        assertEquals(siteNumber, account.getClientId().getSiteNumber());
    }

    public void testClientType() {

        Type clientTypeTest = Type.JUDGE;
        assertEquals(clientTypeTest, account.getClientId().getClientType());
    }

    public void testIsSameAs() {
        ClientId clientId = new ClientId(siteNumber, clientType, 3);
        Account account2 = new Account(clientId, testPassword, clientId.getSiteNumber());

        assertTrue("Account isSameAs ", account.isSameAs(account2));

    }

}
