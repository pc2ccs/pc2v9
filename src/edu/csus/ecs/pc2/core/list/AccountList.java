package edu.csus.ecs.pc2.core.list;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import edu.csus.ecs.pc2.core.PermissionGroup;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IElementObject;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.security.PermissionList;

/**
 * Maintain a list of {@link edu.csus.ecs.pc2.core.model.Account}s.
 * 
 * Generate accounts as well as maintain list of accounts.
 * 
 * @version $Id$
 * @author pc2@ecs.csus.edu
 * 
 * 
 */

// $HeadURL$
public class AccountList extends BaseElementList {
    public static final String SVN_ID = "$Id$";

    /**
     * 
     */
    private static final long serialVersionUID = -9188551825072244360L;

    private PermissionGroup permissionGroup = new PermissionGroup();

    /**
     * All password generation types
     * 
     * @author pc2@ecs.csus.edu
     */
    public enum PasswordType {
        /**
         * Same as login (lowercase).
         * 
         * ie. team1 is team1, board1 is board1
         */
        JOE,
        /**
         * Random
         */
        RANDOM,
        /**
         * 
         */
        DICTIONARY_WORDS
    }

    /**
     * Generate accounts, start a client number 1.
     * 
     * @see #generateNewAccounts(edu.csus.ecs.pc2.core.model.ClientType.Type, int, int, PasswordType, int, boolean)
     * @param type
     * @param count
     * @param passwordType
     * @param siteNumber
     * @param isActive
     */
    public Vector <Account> generateNewAccounts(Type type, int count, PasswordType passwordType, int siteNumber, boolean isActive) {
        return generateNewAccounts(type, count, 1, passwordType, siteNumber, isActive);
    }

    /**
     * Generate accounts, start client number at startNumber.
     * 
     * Will not overwrite/recreate accounts, so will create
     * new accounts that are startNumber or larger.
     * <br>
     * If a startNumber were 100 and count 5, and there were
     * accounts number 100-105, this routine would create
     * accounts 106-110.
     * 
     * @param type
     * @param count
     * @param passwordType
     * @param siteNumber
     */
    public Vector <Account> generateNewAccounts(Type type, int count, int startNumber, PasswordType passwordType, int siteNumber, boolean isActive) {
        
        Vector<Account> newAccountList = new Vector<Account>();
        
        int offset = startNumber - 1;

        for (int i = 0; i < count; i++) {
            
            /**
             * Since the account can start at startNumber, we need to check
             * that the accounts do not exist before we add them.
             */
            
            ClientId clientId = new ClientId(siteNumber, type, i + offset + 1);
            while (getAccount(clientId) != null){
                offset ++;  // skip past existing accounts 
                clientId = new ClientId(siteNumber, type, i + offset + 1);
            }
            
            String newPassword = generatePassword(passwordType, clientId);
            Account account = new Account(clientId, newPassword, siteNumber);
            if (isActive){
               
                PermissionList permissionList = permissionGroup.getPermissionList (type);
                if (permissionList != null){
                    account.clearListAndLoadPermissions(permissionList);
                } else {
                    account.addPermission(Permission.Type.LOGIN);
                    account.addPermission(Permission.Type.DISPLAY_ON_SCOREBOARD);
                }
            }
            newAccountList.add(account);
            super.add(account);
        }
        return newAccountList;
    }

    /**
     * Return true if account defined.
     * 
     * @param clientId
     * @return true if defined, false if not defined.
     */
    public boolean isDefinedAccount(ClientId clientId) {
        return getAccount(clientId) != null;
    }

    /**
     * Get account by clientid.
     * 
     * @param clientId
     *            ClientId
     * @return the account
     */
    public Account getAccount(ClientId clientId) {
        Account account = new Account(clientId, "", clientId.getSiteNumber());
        return (Account) get(account);
    }

    /**
     * Get display name for user.
     * 
     * @param id
     *            client id for user
     * @return string of display name or short client id name {@link ClientId#getName()}
     */
    public String getTitle(ClientId id) {
        Enumeration<? extends IElementObject> enumeration = elements();
        while (enumeration.hasMoreElements()) {
            Account account = (Account) enumeration.nextElement();

            if (account.getClientId().equals(id)) {
                return account.getDisplayName();
            }
        }

        return id.getName();
    }

    /**
     * Generate password
     * 
     * @param passwordType
     *            PasswordType
     * @param clientId
     *            ClientId
     * @return a password baed on the PasswordType.
     */
    private String generatePassword(PasswordType passwordType, ClientId clientId) {
        if (passwordType == PasswordType.JOE) {
            return clientId.getClientType().toString().toLowerCase() + clientId.getClientNumber();
        } else {
            // TODO Random and dictionary words
            return clientId.getClientType().toString().toLowerCase() + clientId.getClientNumber();
        }
    }

    public Vector<Account> getAccounts(Type type) {
        Vector<Account> v = new Vector<Account>();
        
        if (type.equals(ClientType.Type.ALL)) {
            Enumeration<? extends IElementObject> enumeration = elements();
            while (enumeration.hasMoreElements()) {
                Account account = (Account) enumeration.nextElement();
                v.addElement(account);
            }
            return v;
        }

        Enumeration<? extends IElementObject> enumeration = elements();
        while (enumeration.hasMoreElements()) {
            Account account = (Account) enumeration.nextElement();

            if (account.getClientId().getClientType() == type) {
                v.addElement(account);
            }
        }

        return v;

    }

    public Vector<Account> getAccounts(Type type, int siteNumber) {
        Vector<Account> v = new Vector<Account>();

        Enumeration<? extends IElementObject> enumeration = elements();
        while (enumeration.hasMoreElements()) {
            Account account = (Account) enumeration.nextElement();

            if (account.getClientId().getClientType() == type) {
                if (account.getSiteNumber() == siteNumber) {
                    v.addElement(account);
                }

            }
        }

        return v;
    }

    public Vector<Account> getAccounts(int siteNumber) {
        Vector<Account> v = new Vector<Account>();

        Enumeration<? extends IElementObject> enumeration = elements();
        while (enumeration.hasMoreElements()) {
            Account account = (Account) enumeration.nextElement();

            if (account.getSiteNumber() == siteNumber) {
                v.addElement(account);
            }
        }

        return v;
    }

    /**
     * Returns site account/info for input password.
     * 
     * This assumes that no site password can be identical.
     * 
     * @param password
     * @return null if no matching account, otherwise returns account.
     */
    public Account getSiteAccountByPassword(String password) {
        for (Account siteAccount : getAccounts(ClientType.Type.SERVER)) {
            if (siteAccount.getPassword().equals(password)) {
                return siteAccount;
            }
        }
        return null;
    }

    
    /**
     * Is clientId and password valid ?.
     * 
     * Throws exception if account does not exist, password
     * does not match.  Exception message gives description of
     * problem.
     * 
     * @param clientId
     * @param password
     * @return  true if login and password match
     */
    public boolean isValidLoginAndPassword (ClientId clientId, String password) {
        Account account = getAccount(clientId);

        if (account == null || clientId.getClientType() == ClientType.Type.SERVER) {
            Account siteAccount = getSiteAccountByPassword(password);
            if (siteAccount != null) {
                return true;
            } else if (account == null) {
                throw new SecurityException("No such account");
            } else {
                throw new SecurityException("Invalid password");
            }
        }

        if (!account.isAllowed(Permission.Type.LOGIN)) {
            throw new SecurityException("Account inactive");
        }

        if (account.getClientId().equals(clientId)) {
            if (account.getPassword().equals(password)) {
                return true;
            } else {
                throw new SecurityException("Invalid password");
            }
        }

        throw new SecurityException("No such account.");
    }

    /**
     * Add account, set client number number.
     * 
     * The add method inserts without changes.
     * 
     * @param account
     */
    public void addNewAccount(Account account) {
        int siteNumber = account.getSiteNumber();
        Vector<Account> accounts = getAccounts(account.getClientId().getClientType(), siteNumber);
        int nextClientNumber = accounts.size() + 1;
        ClientId id = new ClientId(siteNumber, account.getClientId().getClientType(), nextClientNumber);
        account.setClientId(id);
        super.add(account);
    }

    /**
     * Get the account lookup key.
     */
    @Override
    public String getKey(IElementObject elementObject) {
        Account account = (Account) elementObject;
        return account.getClientId().getTripletKey();
    }
    
    public Account get(ClientId id){
        Account account = new Account(id, "", id.getSiteNumber());
        return (Account) get(account);
    }

    /**
     * Get all accounts
     * 
     * @return an array of accounts
     */
    public Account[] getList() {
        return (Account[]) values().toArray(new Account[size()]);
    }

    public Account assignNewTeam(int siteNumber, String teamName, String[] memberNames, String password) {
            Account newAccount = findOrCreateNewAccount(siteNumber, Type.TEAM, teamName);
            newAccount.setDisplayName(teamName);
            newAccount.setMemberNames(memberNames);
            if (password != null){
                newAccount.setPassword(password);
            }
            return newAccount;
    }
    
    private Account findOrCreateNewAccount(int siteNumber, Type type, String teamName) {

        /**
         * Synchronize to insure that only one team is assigned/registered, otherwise
         * could have problem with two teams assigned the same account.
         */
        synchronized (this) {
            Vector <Account> vector = getAccounts(type, siteNumber);
            Account[] accounts = (Account[]) vector.toArray(new Account[vector.size()]);
            Arrays.sort(accounts, new AccountComparator());
            for (Account account : accounts) {
                if (isNotAssigned(account)) {
                    return account;
                }
            }
            
            /**
             * Not found must generate a new account
             */
            
            Vector<Account> newAccounts = generateNewAccounts(type, 1, PasswordType.JOE, siteNumber, true);
            return newAccounts.firstElement();
        }
    }

    private boolean isNotAssigned(Account account) {
        return account.getDefaultDisplayName(account.getClientId()).equals(account.getDisplayName());
    }

}
