package edu.csus.ecs.pc2.core.list;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.AccountList.PasswordType;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.security.Permission;

/**
 * Test AccountList.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AccountListTest extends TestCase {

    private boolean debugMode = false;

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
    
    Account[] getOrderedAccounts(AccountList accountList, Type type, int siteNumber) {
        Vector<Account> vector = accountList.getAccounts(type, siteNumber);
        Account[] accounts = (Account[]) vector.toArray(new Account[vector.size()]);
        Arrays.sort(accounts, new AccountComparator());
        return accounts;
    }
    
    public void testReg() throws Exception {
        
        AccountList accountList = new AccountList();

        int siteNumber = 3;
        
        String teamName = "Foo Fighters";
        String[] memberNames = { "C", "BB", "AAA" };
        String pass = "letMeGo";
        Account newRegisterAccount = accountList.assignNewTeam(siteNumber, teamName, memberNames, pass);
        
        assertEquals(newRegisterAccount, teamName, memberNames, pass);
        
        Account[] list = getOrderedAccounts(accountList, Type.TEAM, siteNumber);
        
        Account account = list[0];

        assertEquals(account, teamName, memberNames, pass);
        
        
    }
    
    public void testRegWithExistingAccounts() throws Exception {
        
        AccountList accountList = new AccountList();

        int siteNumber = 3;
        
        accountList.generateNewAccounts(Type.TEAM, 12, PasswordType.JOE, siteNumber, true);
        assignAccountNames (accountList, siteNumber);
        
        String teamName = "Foo Fighters";
        String[] memberNames = { "C", "BB", "AAA" };
        String pass = "letMeGo";
        
        Account newRegisterAccount = accountList.assignNewTeam(siteNumber, teamName, memberNames, pass);
        
        assertEquals(newRegisterAccount, teamName, memberNames, pass);
        
        Account[] list = getOrderedAccounts(accountList, Type.TEAM, siteNumber);
        
        int len = list.length-1;
        Account account = list[len];

        assertEquals(account, teamName, memberNames, pass);
        
        if (debugMode) {
        printAccount(System.out, account);
        }
        
        accountList.generateNewAccounts(Type.TEAM, 12, PasswordType.JOE, siteNumber, true);
        
        teamName = "Foo Fighters 2";
        newRegisterAccount = accountList.assignNewTeam(siteNumber, teamName, memberNames, pass);
        list = getOrderedAccounts(accountList, Type.TEAM, siteNumber);
        
        account = list[len + 1];
        assertEquals(account, teamName, memberNames, pass);
        
    }

    private void assignAccountNames(AccountList accountList, int siteNumber) {
        Account[] list = getOrderedAccounts(accountList, Type.TEAM, siteNumber);
        for (Account account : list) {
            account.setDisplayName("New Name" + account.getClientId().getClientNumber());
        }
    }

    /**
     * Compares some of account fields.
     * 
     * @param account
     * @param teamName
     * @param memberNames
     * @param pass
     */
    private void assertEquals(Account account, String teamName, String[] memberNames, String pass) {
        assertEquals("Team name", teamName, account.getDisplayName());
        assertTrue("Member names", Arrays.equals(memberNames, account.getMemberNames()));
        assertEquals("Password", pass, account.getPassword());
    }

    private void printAccount(PrintStream out, Account account) {
        out.print("   Site " + account.getSiteNumber());
        out.format(" %-15s", account.getClientId().getName());
        out.println(" id=" + account.getElementId());

        out.format("%22s", " ");
        out.print("'" + account.getDisplayName() + "' ");
        
//        if (contest.isAllowed (edu.csus.ecs.pc2.core.security.Permission.Type.VIEW_PASSWORDS)) {
            out.print("password '" + account.getPassword() + "' ");
//        }
        
        Permission.Type type = Permission.Type.LOGIN;
        if (account.isAllowed(type)) {
            out.print(type + " ");
        }
        type = Permission.Type.DISPLAY_ON_SCOREBOARD;
        if (account.isAllowed(type)) {
            out.print(type + " ");
        }
        out.println();

        out.format("%22s", " ");
        out.print("alias '" + account.getAliasName() + "' ");
//        ElementId groupId = account.getGroupId();
//        if (groupId != null) {
//            Group group = contest.getGroup(groupId);
//            if (group != null) {
//                printWriter.print("group '"+group+"' ("+groupId+")");
//            } else {
//                printWriter.print("group invalid ("+groupId+")");
//            }
//        } else {
//            printWriter.print("group ''");
//        }
        out.println();

        out.print("       Member Names: ");

        String[] names = account.getMemberNames();

        if (names.length == 0) {
            out.print(" NO member names assigned ");
        } else {
            out.print(" " + join(", ", names));
        }
        out.println();
    }

    private String join(String delimit, String[] names) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < names.length; i++) {
            buffer.append(names[i]);
            if (i < names.length - 1) {
                buffer.append(delimit);
            }
        }
        return buffer.toString();
    }
    
}
