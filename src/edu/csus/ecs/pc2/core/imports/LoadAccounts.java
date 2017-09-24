package edu.csus.ecs.pc2.core.imports;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.exception.IllegalTSVFormatException;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.TabSeparatedValueParser;

/**
 * 
 * @author Troy pc2@ecs.csus.edu
 */
public class LoadAccounts {
    


    private HashMap<ClientId, Account> accountMap = new HashMap<ClientId, Account>();
    private HashMap<String,Group> groups = new HashMap<String,Group>();

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
    
    private HashMap<ClientId, Account> existingAccountsMap = new HashMap<ClientId, Account>();
    
    /**
     * 
     */
    public LoadAccounts() {
        super();
        // TODO Auto-generated constructor stub
    }

    Account getAccount(String[] values) {
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
        // TODO would be nice if Account had a deep clone
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
        return account;
    }
   
    /**
     * Read a tab-separated values from a file.
     * 1st line should contain the column headers.
     * Supported column headers are:
     * account (required)
     * alias
     * displayname
     * group (name)
     * password
     * permdisplay
     * permlogin
     * site (required)
     * 
     * @param filename
     * @param existingAccounts
     * @param groupList
     * @return an array of accounts
     * @throws Exception
     */
    public Account[] fromTSVFile(String filename, Account[] existingAccounts, Group[] groupList) throws Exception {
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
            }
            if (accountColumn == -1 || siteColumn == -1) {
                String msg = "1st line should be the row headers (account and site are required)";
                in.close();
                throw new IllegalTSVFormatException(msg);
            }
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
                // skip lines with no account too
                if (!values[accountColumn].equals("")) {
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
                e.printStackTrace();
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
}
