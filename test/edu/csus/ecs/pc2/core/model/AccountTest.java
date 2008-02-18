package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.model.ClientType.Type;
import junit.framework.TestCase;

/**
 * JUnit test for Account class.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AccountTest extends TestCase {

    private Account account = null;
    
    private int siteNumber = 22;

    public static void main(String[] args) {
    }

    public AccountTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
        ClientId clientId = new ClientId(siteNumber, Type.JUDGE, 3);
        account = new Account(clientId, "", clientId.getSiteNumber());
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

}
