package edu.csus.ecs.pc2.core.model;

import edu.csus.ecs.pc2.core.list.AccountList;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * JUnit tests.
 * 
 * @author pc2@ecs.csus.edu
 */
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
    
    /**
     * Test .updateFrom.
     * 
     * @throws Exception
     */
    public void testupdateFrom() throws Exception {

        ClientId clientId = new ClientId(siteNumber, clientType, 3);
        Account account2 = new Account(clientId, testPassword, clientId.getSiteNumber());
        
        setIvalues(account2,"XX", "UCLA", "University of California Los Angeles");
        
        Account account3 = new Account(clientId, testPassword, clientId.getSiteNumber());
        account3.updateFrom(account2);

        assertEquals("Inst Code", account2.getInstitutionCode(), account3.getInstitutionCode());
        assertEquals("Inst Name", account2.getInstitutionName(), account3.getInstitutionName());
        assertEquals("Inst Short Name", account2.getInstitutionShortName(), account3.getInstitutionShortName());
    }
    
    private void setIvalues(Account inAccount, String code, String shortName, String name) {
        
        inAccount.setInstitutionCode(code);
        inAccount.setInstitutionShortName(shortName);
        inAccount.setInstitutionName(name);
        
    }

    public void testSameinstitutionCode() throws Exception {
        
        ClientId clientId = new ClientId(siteNumber, clientType, 3);
        Account account2 = new Account(clientId, testPassword, clientId.getSiteNumber());
        
        setIvalues(account2,"XX", "", "");
        
        Account account3 = new Account(clientId, testPassword, clientId.getSiteNumber());
        
        assertFalse(account2.isSameAs(account3));
        account3.updateFrom(account2);
        assertTrue(account2.isSameAs(account3));

    }
    
    
   public void testSameinstitutionShortName() throws Exception {

       
       ClientId clientId = new ClientId(siteNumber, clientType, 3);
       Account account2 = new Account(clientId, testPassword, clientId.getSiteNumber());
       
       setIvalues(account2,"", "UCLA", "");
       
       Account account3 = new Account(clientId, testPassword, clientId.getSiteNumber());
       assertFalse(account2.isSameAs(account3));
       
       account3.updateFrom(account2);
       assertTrue(account2.isSameAs(account3));


    }
   
   public void testSameinstitutionName() throws Exception {

       ClientId clientId = new ClientId(siteNumber, clientType, 3);
       Account account2 = new Account(clientId, testPassword, clientId.getSiteNumber());
       
       setIvalues(account2,"", "", "University of California Los Angeles");
       
       Account account3 = new Account(clientId, testPassword, clientId.getSiteNumber());
       assertFalse(account2.isSameAs(account3));
       
       account3.updateFrom(account2);
       assertTrue(account2.isSameAs(account3));

   }
//  
//  if (!StringUtilities.stringSame(institutionCode, account.getInstitutionCode())) {
//      return false;
//  }
//
//  if (!StringUtilities.stringSame(institutionShortName, account.getInstitutionName())) {
//      return false;
//  }
//
//  if (!StringUtilities.stringSame(institutionName, account.getInstitutionName())) {
//      return false;
//  }

}
