// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.exception.IllegalTSVFormatException;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.TabSeparatedValueParser;
import edu.csus.ecs.pc2.imports.ccs.ICPCTSVLoader;

/**
 * Methods that provide updated accounts for an input model and load account TSV file.
 *  
 * @author Troy pc2@ecs.csus.edu
 */
public class LoadAccounts {
    
    /**
     * List of existing groups
     */
    private Map<String,Group> groups = new HashMap<String,Group>();
    
    /**
     * List of existing Accounts
     */
    private Map<ClientId, Account> existingAccountsMap = new HashMap<ClientId, Account>();

    private int siteColumn = -1;

    private int accountColumn = -1;

    private int displayNameColumn = -1;

    private int passwordColumn = -1;

    private int groupColumn = -1;

    private int permDisplayColumn = -1;

    private int permLoginColumn = -1;
    
    private int aliasColumn = -1;
    
    private int externalIdColumn = -1;
    
    private int permPasswordColumn = -1;

    /*
     * These correspond to columns found in the icpc data
     */
    private int longSchoolNameColumnn = -1;
    private int shortSchoolNameColumn = -1;
    private int countryCodeColumn = -1;
    private int teamNameColumn = -1;
    private int scoreAdjustmentColumn = -1;
    private int institutionCodeColumn = -1;
    
    /**
     * 
     */
    public LoadAccounts() {
        super();
    }

    protected Account getAccount(String[] values) {
        String accountString = values[accountColumn];
        String[] accountSplit = accountString.split("[0-9]+$");
        String accountName = accountString.substring(0, accountSplit[0].length());
        Type type = Type.valueOf(accountName.toUpperCase());
        int clientNumber = Integer.parseInt(accountString.substring(accountSplit[0].length()));
        String siteString = values[siteColumn];
        ClientId clientId = new ClientId(Integer.parseInt(siteString), type, clientNumber);
        Account accountClean = existingAccountsMap.get(clientId);
        if (accountClean == null) {
            // would be nice if we could create the account... not now though...
            // accountClean = new Account(clientId, password, clientId.getSiteNumber());
            return null;
        }
        // TODO SOMEDAY would be nice if Account had a deep clone
        Account account = new Account(accountClean.getClientId(), accountClean.getPassword(), accountClean.getClientId().getSiteNumber());
        account.clearListAndLoadPermissions(accountClean.getPermissionList());
        account.setGroupId(accountClean.getGroupId());
        account.setDisplayName(new String(accountClean.getDisplayName()));
        account.setAliasName(new String(accountClean.getAliasName()));
        account.setExternalId(new String(accountClean.getExternalId()));
        account.setExternalName(new String(accountClean.getExternalName()));
        account.setGroupId(accountClean.getGroupId());
        account.setLongSchoolName(new String(accountClean.getLongSchoolName()));
        account.setShortSchoolName(new String(accountClean.getShortSchoolName()));
        account.setInstitutionCode(new String(accountClean.getInstitutionCode()));
        account.setInstitutionName(new String(accountClean.getInstitutionName()));
        account.setInstitutionShortName(new String(accountClean.getInstitutionShortName()));
        
        // now start changing
        if (passwordColumn != -1 && values.length > passwordColumn) {
            account.setPassword(values[passwordColumn]);
        }
        if (displayNameColumn != -1 && values.length > displayNameColumn) {
            account.setDisplayName(values[displayNameColumn]);
        }
        if (aliasColumn != -1 && values.length > aliasColumn) {
            account.setAliasName(values[aliasColumn]);
        }
        if (externalIdColumn != -1 && values.length > externalIdColumn) {
            account.setExternalId(values[externalIdColumn]);
        }
        if (longSchoolNameColumnn != -1 && values.length > longSchoolNameColumnn) {
            account.setLongSchoolName(values[longSchoolNameColumnn]);
        }
        if (shortSchoolNameColumn != -1 && values.length > shortSchoolNameColumn) {
            account.setShortSchoolName(values[shortSchoolNameColumn]);
        }
        if (countryCodeColumn != -1 && values.length > countryCodeColumn) {
            account.setCountryCode(values[countryCodeColumn]);
        }
        if (teamNameColumn != -1 && values.length > teamNameColumn) {
            account.setExternalName(values[teamNameColumn]);
        }
        
        if (groups.size() > 0) {
            if (groupColumn != -1 && values.length > groupColumn && values[groupColumn].length() > 0) {
                if (groups.containsKey(values[groupColumn])) {
                    account.setGroupId(groups.get(values[groupColumn]).getElementId());
                }
            }
        }
        // do not allow permission changes for root
        if (permDisplayColumn != -1 && values.length > permDisplayColumn && values[permDisplayColumn].length() > 0) {
            boolean newValue = Boolean.parseBoolean(values[permDisplayColumn]);
            if (clientId.getClientType().equals(ClientType.Type.ADMINISTRATOR) && clientId.getClientNumber() == 1) {
                if (account.getPermissionList().isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD) != newValue) {
                    String message = "Attempt to change root permission DISPLAY_ON_SCOREBOARD denied.";
                    StaticLog.warning(message);
                    System.out.println("WARNING: "+message);
                }
            } else {
                if (newValue) {
                    account.addPermission(Permission.Type.DISPLAY_ON_SCOREBOARD);
                } else {
                    account.removePermission(Permission.Type.DISPLAY_ON_SCOREBOARD);
                }
            }
        }
        if (permLoginColumn != -1 && values.length > permLoginColumn && values[permLoginColumn].length() > 0) {
            boolean newValue = Boolean.parseBoolean(values[permLoginColumn]);
            if (clientId.getClientType().equals(ClientType.Type.ADMINISTRATOR) && clientId.getClientNumber() == 1) {
                if (account.getPermissionList().isAllowed(Permission.Type.LOGIN) != newValue) {
                    String message = "Attempt to change root permission LOGIN denied.";
                    StaticLog.warning(message);
                    System.out.println("WARNING: "+message);
                }
            } else {
                if (Boolean.parseBoolean(values[permLoginColumn])) {
                    account.addPermission(Permission.Type.LOGIN);
                } else {
                    account.removePermission(Permission.Type.LOGIN);
                }
            }
        }
        if (permPasswordColumn != -1 && values.length > permPasswordColumn && values[permPasswordColumn].length() > 0) {
            Permission.Type perm = Permission.Type.CHANGE_PASSWORD;
            boolean newValue = Boolean.parseBoolean(values[permPasswordColumn]);
            if (clientId.getClientType().equals(ClientType.Type.ADMINISTRATOR) && clientId.getClientNumber() == 1) {
                if (account.getPermissionList().isAllowed(perm) != newValue) {
                    String message = "Attempt to change root permission "+perm+" denied.";
                    StaticLog.warning(message);
                    System.out.println("WARNING: "+message);
                }
            } else {
                if (Boolean.parseBoolean(values[permPasswordColumn])) {
                    account.addPermission(perm);
                } else {
                    account.removePermission(perm);
                }
            }
        }
        if (scoreAdjustmentColumn != -1 && values.length > scoreAdjustmentColumn && values[scoreAdjustmentColumn].length() > 0) {
            try {
                int newValue = Integer.parseInt(values[scoreAdjustmentColumn]);
                account.setScoringAdjustment(newValue);
            } catch (NumberFormatException e) {
                String message = e.getMessage();
                StaticLog.warning(message);
                System.out.println("WARNING: " + message);
            }
        }
        if (institutionCodeColumn != -1 && values.length > institutionCodeColumn && values[institutionCodeColumn].length() > 0) {
            setInstitutionInformation(account, values[institutionCodeColumn]);
        }
        return account;
    }
    
    
    /**
     * Create a new account or update an existing account given input values from load accounts file.
     * 
     * @param values
     * @return
     * @throws IllegalTSVFormatException
     */
    protected Account getAccountFromFields(String[] values) throws IllegalTSVFormatException {
        String accountString = values[accountColumn];
        String[] accountSplit = accountString.split("[0-9]+$");
        String accountName = accountString.substring(0, accountSplit[0].length());
        Type type = Type.valueOf(accountName.toUpperCase());
        int clientNumber = Integer.parseInt(accountString.substring(accountSplit[0].length()));
        String siteString = values[siteColumn];
        ClientId clientId = new ClientId(Integer.parseInt(siteString), type, clientNumber);
        Account existingAccount = existingAccountsMap.get(clientId);
        if (existingAccount == null) {

            // "Create" new account
            int siteNumber = Integer.parseInt(siteString);

            if (passwordColumn != -1 && values.length > passwordColumn) {
                existingAccount = new Account(clientId, values[passwordColumn], siteNumber);
            } else {
                throw new IllegalTSVFormatException("Password required for new account for " + clientId);
            }
        }
        
        Account account = new Account(existingAccount.getClientId(), existingAccount.getPassword(), existingAccount.getClientId().getSiteNumber());
        
        account.clearListAndLoadPermissions(existingAccount.getPermissionList());
        account.setGroupId(existingAccount.getGroupId());
        account.setDisplayName(new String(existingAccount.getDisplayName()));
        account.setAliasName(new String(existingAccount.getAliasName()));
        account.setExternalId(new String(existingAccount.getExternalId()));
        account.setExternalName(new String(existingAccount.getExternalName()));
        account.setGroupId(existingAccount.getGroupId());
        account.setLongSchoolName(new String(existingAccount.getLongSchoolName()));
        account.setShortSchoolName(new String(existingAccount.getShortSchoolName()));
        account.setInstitutionCode(existingAccount.getInstitutionCode());
        account.setInstitutionName(existingAccount.getInstitutionName());
        account.setInstitutionShortName(existingAccount.getInstitutionShortName());
        String [] existingMembers = existingAccount.getMemberNames();
        if(existingMembers != null) {
            account.setMemberNames(StringUtilities.cloneStringArray(existingMembers));
        }
        // now start updating fields
        
        if (passwordColumn != -1 && values.length > passwordColumn) {
            account.setPassword(values[passwordColumn]);
        }
        if (displayNameColumn != -1 && values.length > displayNameColumn) {
            account.setDisplayName(values[displayNameColumn]);
        }
        if (aliasColumn != -1 && values.length > aliasColumn) {
            account.setAliasName(values[aliasColumn]);
        }
        if (externalIdColumn != -1 && values.length > externalIdColumn) {
            account.setExternalId(values[externalIdColumn]);
        }
        if (longSchoolNameColumnn != -1 && values.length > longSchoolNameColumnn) {
            account.setLongSchoolName(values[longSchoolNameColumnn]);
        }
        if (shortSchoolNameColumn != -1 && values.length > shortSchoolNameColumn) {
            account.setShortSchoolName(values[shortSchoolNameColumn]);
        }
        if (countryCodeColumn != -1 && values.length > countryCodeColumn) {
            account.setCountryCode(values[countryCodeColumn]);
        }
        if (teamNameColumn != -1 && values.length > teamNameColumn) {
            account.setExternalName(values[teamNameColumn]);
        }
        
        if (groups.size() > 0) {
            if (groupColumn != -1 && values.length > groupColumn && values[groupColumn].length() > 0) {
                if (groups.containsKey(values[groupColumn])) {
                    account.setGroupId(groups.get(values[groupColumn]).getElementId());
                }
            }
        }
        // do not allow permission changes for root
        if (permDisplayColumn != -1 && values.length > permDisplayColumn && values[permDisplayColumn].length() > 0) {
            boolean newValue = Boolean.parseBoolean(values[permDisplayColumn]);
            if (clientId.getClientType().equals(ClientType.Type.ADMINISTRATOR) && clientId.getClientNumber() == 1) {
                if (account.getPermissionList().isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD) != newValue) {
                    String message = "Attempt to change root permission DISPLAY_ON_SCOREBOARD denied.";
                    StaticLog.warning(message);
                    System.out.println("WARNING: "+message);
                }
            } else {
                if (newValue) {
                    account.addPermission(Permission.Type.DISPLAY_ON_SCOREBOARD);
                } else {
                    account.removePermission(Permission.Type.DISPLAY_ON_SCOREBOARD);
                }
            }
        }
        if (permLoginColumn != -1 && values.length > permLoginColumn && values[permLoginColumn].length() > 0) {
            boolean newValue = Boolean.parseBoolean(values[permLoginColumn]);
            if (clientId.getClientType().equals(ClientType.Type.ADMINISTRATOR) && clientId.getClientNumber() == 1) {
                if (account.getPermissionList().isAllowed(Permission.Type.LOGIN) != newValue) {
                    String message = "Attempt to change root permission LOGIN denied.";
                    StaticLog.warning(message);
                    System.out.println("WARNING: "+message);
                }
            } else {
                if (Boolean.parseBoolean(values[permLoginColumn])) {
                    account.addPermission(Permission.Type.LOGIN);
                } else {
                    account.removePermission(Permission.Type.LOGIN);
                }
            }
        }
        if (permPasswordColumn != -1 && values.length > permPasswordColumn && values[permPasswordColumn].length() > 0) {
            Permission.Type perm = Permission.Type.CHANGE_PASSWORD;
            boolean newValue = Boolean.parseBoolean(values[permPasswordColumn]);
            if (clientId.getClientType().equals(ClientType.Type.ADMINISTRATOR) && clientId.getClientNumber() == 1) {
                if (account.getPermissionList().isAllowed(perm) != newValue) {
                    String message = "Attempt to change root permission "+perm+" denied.";
                    StaticLog.warning(message);
                    System.out.println("WARNING: "+message);
                }
            } else {
                if (Boolean.parseBoolean(values[permPasswordColumn])) {
                    account.addPermission(perm);
                } else {
                    account.removePermission(perm);
                }
            }
        }
        if (scoreAdjustmentColumn != -1 && values.length > scoreAdjustmentColumn && values[scoreAdjustmentColumn].length() > 0) {
            try {
                int newValue = Integer.parseInt(values[scoreAdjustmentColumn]);
                account.setScoringAdjustment(newValue);
            } catch (NumberFormatException e) {
                String message = e.getMessage();
                StaticLog.warning(message);
                System.out.println("WARNING: " + message);
            }
        }
        if (institutionCodeColumn != -1 && values.length > institutionCodeColumn && values[institutionCodeColumn].length() > 0) {
            setInstitutionInformation(account, siteString);
        }
        return account;
    }
    /**
     * Returns a list of accounts updated from the input load accounts file.
     * 
     * @see #fromTSVFile(String, Account[], Group[])
     * 
     * @param contest
     * @param filename updates model accounts from file 
     * @return a list of accounts to update.
     * @throws Exception
     */
    public static Account[] updateAccountsFromFile(IInternalContest contest, String filename) throws Exception {

        Account[] curAccounts = contest.getAccounts();
        Group[] curGroups = contest.getGroups();

        // we may need the institutions later if the accounts file wants to update/add an accounts institution code.
        // we need to get them now since we have the contest object and these are all static methods that deal
        // with institutions
        loadInstitutions(contest);
        
        Account[] updatedAccounts = new LoadAccounts().fromTSVFile(contest, filename, curAccounts, curGroups);
        return updatedAccounts;
    }
   
    /**
     * Read a tab-separated values from a file.
     * 
     * File must have a header of field names.  The fields can appear in any order.
     * 
     * Reads TSV file, updates existing accounts in model, returns only a list
     * of accounts that should be updated.
     * 
     * <P>
     * 
     * All accounts that are specified in the tsv file must exist, if an account
     * does not exist then a IllegalTSVFormatException will be thrown.
     * 
     * <P>
     * 
     * 
     * 1st line should contain the column headers.   Columns can appear in any order.
     * Supported column headers are:
     * <pre>
     * account (required)
     * alias
     * displayname
     * group (name)
     * password
     * permdisplay
     * permlogin
     * site (required)

     * </pre>
     * 
     * @param filename
     * @param existingAccounts 
     * @param groupList
     * @return an array of accounts
     * @throws Exception
     */
    public Account[] fromTSVFile(IInternalContest contest, String filename, Account[] existingAccounts, Group[] groupList) throws Exception  {
        
        /**
         * Output accounts
         */
        Map<ClientId, Account> accountMap = new HashMap<ClientId, Account>();
        
        if (existingAccounts != null && existingAccounts.length > 0) {
            for (int i = 0; i < existingAccounts.length; i++) {
                existingAccountsMap.put(existingAccounts[i].getClientId(), existingAccounts[i]);
            }
        }
        groups.clear();
        for (Group group : groupList) {
            groups.put(group.toString(),group);
        }
        int lineCount = 0;
        String[] columns;
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF8"));
        String line = in.readLine();
        while (line != null && (line.startsWith("#") || line.trim().length() == 0)) {
            line = in.readLine();
            lineCount++;
        }
        lineCount++;
        if (line != null) {
            columns = TabSeparatedValueParser.parseLine(line);
            siteColumn = -1;
            accountColumn = -1;
            displayNameColumn = -1;
            passwordColumn = -1;
            groupColumn = -1;
            aliasColumn = -1;
            externalIdColumn = -1;
            permDisplayColumn = -1;
            permLoginColumn = -1;
            permPasswordColumn = -1;
            scoreAdjustmentColumn = -1;
            /*
             * These correspond to columns found in the icpc data
             */
            longSchoolNameColumnn = -1;
            shortSchoolNameColumn = -1;
            countryCodeColumn = -1;
            teamNameColumn = -1;
            institutionCodeColumn = -1;
            
            for (int i = 0; i < columns.length; i++) {
                
                if (Constants.SITE_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    siteColumn = i;
                }
                if (Constants.ACCOUNT_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    accountColumn = i;
                }
                if (Constants.DISPLAYNAME_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    displayNameColumn = i;
                }
                if (Constants.PASSWORD_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    passwordColumn = i;
                }
                if (Constants.GROUP_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    groupColumn = i;
                }
                if (Constants.PERMDISPLAY_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    permDisplayColumn = i;
                }
                if (Constants.PERMLOGIN_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    permLoginColumn = i;
                }
                if (Constants.EXTERNALID_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    externalIdColumn = i;
                }
                if (Constants.ALIAS_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    aliasColumn = i;
                }
                if (Constants.PERMPASSWORD_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    permPasswordColumn = i;
                }
                if (Constants.LONGSCHOOLNAME_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    longSchoolNameColumnn = i;
                }
                if (Constants.SHORTSCHOOLNAME_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    shortSchoolNameColumn = i;
                }
                if (Constants.COUNTRY_CODE_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    countryCodeColumn = i;
                }
                if (Constants.TEAMNAME_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    teamNameColumn = i;
                }
                if (Constants.SCORING_ADJUSTMENT_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    scoreAdjustmentColumn = i;
                }
                if (Constants.INST_CODE_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    institutionCodeColumn = i;
                }
            }
            if (accountColumn == -1 || siteColumn == -1) {
                String msg = "1st line should be the row headers (account and site are required)";
                in.close();
                throw new IllegalTSVFormatException(msg);
            }
        }
        
        // only need to load institutions if the column is specified
        if(institutionCodeColumn != -1) {
            loadInstitutions(contest);
        }
        
        line = in.readLine();
        lineCount++;
        while (line != null) {
            try {
                // skip comments & line blanks
                if (line.startsWith("#") || line.trim().length() == 0) {
                    line = in.readLine();
                    lineCount++;
                    continue;
                }
                String[] values = TabSeparatedValueParser.parseLine(line);
                
                if (!values[accountColumn].equals("")) {
                    // No such account in contest model
                    Account account = getAccount(values);
                    if (account == null) {
                        String msg = filename + ":" + lineCount + ": " + " please create the account first (" + values[accountColumn] + ")";
                        in.close();
                        throw new IllegalTSVFormatException(msg);
                    }
                    accountMap.put(account.getClientId(), account);
                }
            } catch (IllegalTSVFormatException e2) {
                // already a properly formatted exception
                in.close();
                throw e2;
            } catch (Exception e) {
                String msg = "Error " + filename + ":" + lineCount + ": " + e.getMessage();
                Exception sendException = new Exception(msg);
                sendException.setStackTrace(e.getStackTrace());
                in.close();
                throw sendException;
            }
            line = in.readLine();
            lineCount++;
        }
        in.close();
        in = null;
        return accountMap.values().toArray(new Account[accountMap.size()]);
    }

    /**
     * Update accounts from accounts load file.
     * 
     * @param loadFilename - accounts load filename.
     * @throws Exception 
     */
    public static void updateAccountsFromLoadAccountsFile(IInternalContest contest, String loadAccountFilename) throws Exception {
        if (Utilities.fileExists(loadAccountFilename)) {

            // we may need the institutions later if the accounts file wants to update/add an accounts institution code.
            // we need to get them now since we have the contest object and these are all static methods that deal
            // with institutions
            loadInstitutions(contest);
            
            Account[] updateAccounts = LoadAccounts.updateAccountsFromFile(contest, loadAccountFilename);
            contest.updateAccounts(updateAccounts);
            contest.storeConfiguration(StaticLog.getLog());
        }
    }
    
    
    
    /**
     * Create a list of updated accounts, if account in TSV file does not exist will create a new Account.
     * 
     * Intention is a list of all accounts in the TSV files that need to be updated or added.
     * 
     * File must have a header of field names.  The fields can appear in any order.
     * 
     * Reads TSV file, updates existing accounts in model, returns only a list
     * of accounts that should be updated.
     * 
     * <P>
     * 
     * All accounts that are specified in the tsv file must exist, if an account
     * does not exist then a IllegalTSVFormatException will be thrown.
     * 
     * <P>
     * 
     * 
     * 1st line should contain the column headers.   Columns can appear in any order.
     * Supported column headers are:
     * <pre>
     * account (required)
     * alias
     * displayname
     * group (name)
     * password
     * permdisplay
     * permlogin
     * site (required)

     * </pre>
     * 
     * @param filename
     * @param existingAccounts 
     * @param groupList
     * @return an array of accounts
     * @throws Exception
     */
    public Account[] fromTSVFileWithNewAccounts(IInternalContest contest, String filename, Account[] existingAccounts, Group[] groupList) throws Exception  {
        
        /**
         * Output accounts
         */
        Map<ClientId, Account> accountMap = new HashMap<ClientId, Account>();
        
        if (existingAccounts != null && existingAccounts.length > 0) {
            for (int i = 0; i < existingAccounts.length; i++) {
                existingAccountsMap.put(existingAccounts[i].getClientId(), existingAccounts[i]);
            }
        }
        groups.clear();
        for (Group group : groupList) {
            groups.put(group.toString(),group);
        }
        int lineCount = 0;
        String[] columns;
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF8"));
        String line = in.readLine();
        while (line != null && line.startsWith("#")) {
            line = in.readLine();
            lineCount++;
        }
        lineCount++;
        if (line != null) {
            columns = TabSeparatedValueParser.parseLine(line);
            siteColumn = -1;
            accountColumn = -1;
            displayNameColumn = -1;
            passwordColumn = -1;
            groupColumn = -1;
            aliasColumn = -1;
            externalIdColumn = -1;
            permDisplayColumn = -1;
            permLoginColumn = -1;
            permPasswordColumn = -1;
            scoreAdjustmentColumn = -1;
            
            /*
             * These correspond to columns found in the icpc data
             */
            longSchoolNameColumnn = -1;
            shortSchoolNameColumn = -1;
            countryCodeColumn = -1;
            teamNameColumn = -1;
            institutionCodeColumn = -1;
            
            for (int i = 0; i < columns.length; i++) {
                
                if (Constants.SITE_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    siteColumn = i;
                }
                if (Constants.ACCOUNT_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    accountColumn = i;
                }
                if (Constants.DISPLAYNAME_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    displayNameColumn = i;
                }
                if (Constants.PASSWORD_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    passwordColumn = i;
                }
                if (Constants.GROUP_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    groupColumn = i;
                }
                if (Constants.PERMDISPLAY_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    permDisplayColumn = i;
                }
                if (Constants.PERMLOGIN_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    permLoginColumn = i;
                }
                if (Constants.EXTERNALID_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    externalIdColumn = i;
                }
                if (Constants.ALIAS_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    aliasColumn = i;
                }
                if (Constants.PERMPASSWORD_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    permPasswordColumn = i;
                }
                if (Constants.LONGSCHOOLNAME_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    longSchoolNameColumnn = i;
                }
                if (Constants.SHORTSCHOOLNAME_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    shortSchoolNameColumn = i;
                }
                if (Constants.COUNTRY_CODE_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    countryCodeColumn = i;
                }
                if (Constants.TEAMNAME_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    teamNameColumn = i;
                }
                if (Constants.SCORING_ADJUSTMENT_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    scoreAdjustmentColumn = i;
                }
                if (Constants.INST_CODE_COLUMN_NAME.equalsIgnoreCase(columns[i])) {
                    institutionCodeColumn = i;
                }
            }
            if (accountColumn == -1 || siteColumn == -1) {
                String msg = "1st line should be the row headers (account and site are required)";
                in.close();
                throw new IllegalTSVFormatException(msg);
            }
        }
        
        // only load institutions if column is specified in file
        if(institutionCodeColumn != -1) {
            loadInstitutions(contest);
        }
        line = in.readLine();
        lineCount++;
        while (line != null) {
            try {
                // skip comments & line blanks
                if (line.startsWith("#") || line.equals("")) {
                    line = in.readLine();
                    lineCount++;
                    continue;
                }
                String[] values = TabSeparatedValueParser.parseLine(line);

                Account account = getAccountFromFields(values);
                
                
                accountMap.put(account.getClientId(), account);
                
            } catch (IllegalTSVFormatException e2) {
                // already a properly formatted exception
                in.close();
                throw e2;
            } catch (Exception e) {
                String msg = "Error " + filename + ":" + lineCount + ": " + e.getMessage();
                Exception sendException = new Exception(msg);
                sendException.setStackTrace(e.getStackTrace());
                in.close();
                throw sendException;
            }
            line = in.readLine();
            lineCount++;
        }
        in.close();
        in = null;
        return accountMap.values().toArray(new Account[accountMap.size()]);
    }
    
    /**
     * Attempt to load the institutions from the supplied file.
     * 
     * @param file probable location of the institutions.tsv file
     * @return true if loaded, false if error (file not found, etc)
     */
    public static boolean loadInstitutions(String file) {
        boolean found = false;
        
        try {
            // have to check existance of file since loadInstitutions() doesn't care if it exists or not
            if(new File(file).exists()) {
                ICPCTSVLoader.loadInstitutions(file);
                found = true;
            }
        } catch(Exception e) {
            // completely uninterested in the exception, other than that it happened, meaning, we didn't load the file
        }
        return(found);
    }
    
    public static void loadInstitutions(IInternalContest contest) {
        if(!loadInstitutions(contest.getContestInformation().getAdminCDPBasePath() + File.separator + LoadICPCTSVData.INSTITUTIONS_FILENAME)) {
            if(!loadInstitutions(contest.getContestInformation().getJudgeCDPBasePath() + File.separator + LoadICPCTSVData.INSTITUTIONS_FILENAME)) {
                StaticLog.warning("Can not load " + LoadICPCTSVData.INSTITUTIONS_FILENAME + " from "
                    + contest.getContestInformation().getAdminCDPBasePath() + "or "
                    + contest.getContestInformation().getJudgeCDPBasePath());
            }
        }
    }

    private static void setInstitutionInformation(Account account, String instCode) {
        try {
            String [] institutionInfo = ICPCTSVLoader.getInstitutionNames(instCode);
            if (institutionInfo != null) {
                account.setInstitutionCode(instCode);
                String institutionFormalName = institutionInfo[1];
                String institutionName = institutionInfo[2];
                if (!institutionName.equals("")) {
                    account.setInstitutionName(institutionFormalName);
                }
                if (!institutionFormalName.equals("")) {
                    account.setInstitutionShortName(institutionName);
                }
            }
        } catch (Exception e) {
            String message = e.getMessage();
            StaticLog.warning(message);
            System.out.println("WARNING: " + message);
        }
        
    }
}
