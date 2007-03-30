package edu.csus.ecs.pc2.core.list;

import java.util.Enumeration;
import java.util.Vector;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IElementObject;
import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * List of accounts.
 * 
 * Generate accounts as well as maintain list of accounts.
 * 
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

    public static final String VALID_LOGIN = "Valid Login";

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
     * Generate/add new accounts.
     * 
     * @param type
     * @param count
     * @param passwordType
     * @param siteNumber
     */
    public void generateNewAccounts(Type type, int count, PasswordType passwordType, int siteNumber, boolean isActive) {
        Vector accounts = getAccounts(type, siteNumber);
        int total = accounts.size();

        for (int i = 0; i < count; i++) {
            ClientId clientId = new ClientId(siteNumber, type, i + total + 1);
            String newPassword = generatePassword(passwordType, clientId);
            Account account = new Account(clientId, newPassword, siteNumber);
            account.setActive(isActive);
            super.add(account);
        }
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
        Enumeration enumeration = elements();
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
     * @return
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

        Enumeration enumeration = elements();
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

        Enumeration enumeration = elements();
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

        Enumeration enumeration = elements();
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
     * Return result for login.
     */
    public String loginResult(ClientId clientId, String password) {
        Account account = getAccount(clientId);

        if (account == null || clientId.getClientType() == ClientType.Type.SERVER) {
            Account siteAccount = getSiteAccountByPassword(password);
            if (siteAccount != null) {
                return VALID_LOGIN;
            } else if (account == null) {
                return "No such account";
            } else {
                return "Invalid password";
            }
        }

        if (account == null) {
            return "No such account";
        }

        if (!account.isActive()) {
            return "Account inactive";
        }

        if (account.getClientId().equals(clientId)) {
            if (account.getPassword().equals(password)) {
                return VALID_LOGIN;
            } else {
                return "Invalid password";
            }
        }

        return "No such account.";
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
        Vector accounts = getAccounts(account.getClientId().getClientType(), siteNumber);
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

    /**
     * Get all accounts
     * 
     * @return an array of acounts
     */
    @SuppressWarnings("unchecked")
    public Account[] getList() {
        return (Account[]) values().toArray(new Account[size()]);
    }

}
