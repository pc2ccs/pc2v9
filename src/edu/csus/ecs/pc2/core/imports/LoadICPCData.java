/**
 * 
 */
package edu.csus.ecs.pc2.core.imports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import edu.csus.ecs.pc2.core.exception.IllegalTSVFormatException;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.TabSeparatedValueParser;

/**
 * @author pc2@ecs.csus.edu
 * 
 */
public class LoadICPCData {
    private HashMap<ClientId, Account> accountMap = new HashMap<ClientId, Account>();

    private static final int TEAM_TAB = 11;
    private static final int SITE_TAB = 22;
    private static final int CONTEST_TAB = 33;
    
    private int clientNumber = 0;
    
    private int siteColumn = -1;

    private int accountColumn = -1;

    private int displayNameColumn = -1;

    private int passwordColumn = -1;

    private int groupColumn = -1;

    private int permDisplayColumn = -1;

    private int permLoginColumn = -1;
    
    private int aliasColumn = -1;
    
    private int externalIdColumn = -1;

    private HashMap<ClientId, Account> existingAccountsMap = new HashMap<ClientId, Account>();
    
    /**
     * 
     */
    public LoadICPCData() {
        super();
        // TODO Auto-generated constructor stub
    }

    Account getAccount(String[] values) {
        String accountString = values[accountColumn];
        String[] accountSplit = accountString.split("[0-9]+$");
        String accountName = accountString.substring(0, accountSplit[0].length());
        Type type = Type.valueOf(accountName.toUpperCase());
        clientNumber = Integer.parseInt(accountString.substring(accountSplit[0].length()));
        String siteString = values[siteColumn];
        String password = values[passwordColumn];
        ClientId clientId = new ClientId(Integer.parseInt(siteString), type, clientNumber);
        Account accountClean = existingAccountsMap.get(clientId);
        if (accountClean == null) {
            // would be nice if we could create the account... not now though...
            // accountClean = new Account(clientId, password, clientId.getSiteNumber());
            return null;
        }
        // TODO would be nice if Account had a deep clone
        Account account = new Account(clientId, password, clientId.getSiteNumber());
        account.clearListAndLoadPermissions(accountClean.getPermissionList());
        account.setGroupId(accountClean.getGroupId());
        account.setPassword(password);
        if (displayNameColumn != -1 && values.length > displayNameColumn) {
            account.setDisplayName(values[displayNameColumn]);
        }
        if (aliasColumn != -1 && values.length > aliasColumn) {
            account.setAliasName(values[aliasColumn]);
        }
        if (externalIdColumn != -1 && values.length > externalIdColumn) {
            account.setAliasName(values[externalIdColumn]);
        }
        if (groupColumn != -1 && values.length >= groupColumn && values.length > 0) {
            // TODO in the future, may need to convert this id to an elementId
            account.setGroupId(values[groupColumn]);
        }
        if (permDisplayColumn != -1 && values.length > permDisplayColumn && values[permDisplayColumn].length() > 0) {
            if (Boolean.parseBoolean(values[permDisplayColumn])) {
                account.addPermission(Permission.Type.DISPLAY_ON_SCOREBOARD);
            } else {
                account.removePermission(Permission.Type.DISPLAY_ON_SCOREBOARD);
            }
        }
        if (permLoginColumn != -1 && values.length > permLoginColumn && values[permLoginColumn].length() > 0) {
            if (Boolean.parseBoolean(values[permLoginColumn])) {
                account.addPermission(Permission.Type.LOGIN);
            } else {
                account.removePermission(Permission.Type.LOGIN);
            }
        }
        return account;
    }

    boolean fromDirectory(String directory, Account[] existingAccounts) throws Exception {
        boolean result = false;
        if (existingAccounts != null && existingAccounts.length > 0) {
            for (int i = 0; i < existingAccounts.length; i++) {
                existingAccountsMap.put(existingAccounts[i].getClientId(), existingAccounts[i]);
            }
        }
        // TODO add file exists checks before attempting read
        readFile(directory+File.separator+"PC2_Contest.tab", CONTEST_TAB);
        // if
        readFile(directory+File.separator+"_PC2_Site.tab", SITE_TAB);
        // else
        readFile(directory+File.separator+"PC2_Site.tab", SITE_TAB);
        // if
        readFile(directory+File.separator+"_PC2_Team.tab", TEAM_TAB);
        // else
        readFile(directory+File.separator+"PC2_Team.tab", TEAM_TAB);
        
        return result;
    }
    
    void readFile(String filename, int fileType) throws Exception {
        String line;
        int lineCount = 0;
        FileReader fileReader = new FileReader(filename);
        BufferedReader in = new BufferedReader(fileReader);
        line = in.readLine();
        lineCount++;
        while (line != null) {
            try {
                if (line.startsWith("#")) {
                    line = in.readLine();
                    lineCount++;
                    continue;
                }
                String[] values = TabSeparatedValueParser.parseLine(line);
                switch (fileType) {
                    case 11:
                        processTeam(values);                      
                        break;

                    case 22:
                        processSite(values);
                        break;
                        
                    case 33:
                        processContest(values);
                        break;
                        
                    default:
                        new Exception("Unknown file type");
                        break;
                }
                Account account = getAccount(values);
                if (account == null) {
                    String msg = filename + ":" + lineCount + ": " + " please create the account first (" + values[accountColumn] + ")";
                    throw new IllegalTSVFormatException(msg);
                }
                accountMap.put(account.getClientId(), account);
            } catch (IllegalTSVFormatException e2) {
                // already a properly formatted exception
                throw e2;
            } catch (Exception e) {
                String msg = "Error " + filename + ":" + lineCount + ": " + e.getMessage();
                throw new Exception(msg);
            }
            line = in.readLine();
            lineCount++;
        }
        in.close();
        fileReader.close();
        in = null;
        fileReader = null;
    }
    
    private Account processTeam(String[] values) {
        int offset = 0;
        switch(values.length) {
            case 10:
                clientNumber = Integer.parseInt(values[0]);
                offset = 0;
                break;
            case 9:
                clientNumber++;
                offset = -1;
                break;
            default:
                break;
        }
        // TODO convert icpc site to pc2 site
        String siteString = values[siteColumn];
        ClientId clientId = new ClientId(Integer.parseInt(siteString), Type.TEAM, clientNumber);
        Account accountClean = existingAccountsMap.get(clientId);
        if (accountClean == null) {
            // would be nice if we could create the account... not now though...
            // accountClean = new Account(clientId, password, clientId.getSiteNumber());
            return null;
        }
        // TODO would be nice if Account had a deep clone
        Account account = new Account(clientId, accountClean.getPassword(), clientId.getSiteNumber());
        account.clearListAndLoadPermissions(accountClean.getPermissionList());
        // TODO consider blanking these out
        account.setGroupId(accountClean.getGroupId());
        account.setExternalId(accountClean.getExternalId());
        account.setExternalName(accountClean.getExternalName());
        account.setLongSchoolName(accountClean.getLongSchoolName());
        account.setShortSchoolName(accountClean.getShortSchoolName());
        
        account.setExternalId(values[2+offset]);
        account.setGroupId(values[3+offset]);
        account.setExternalName(values[5+offset]);
        account.setLongSchoolName(values[6+offset]);
        account.setShortSchoolName(values[7+offset]);
        return account;
    }

    private void processSite(String[] values) {
        // TODO Auto-generated method stub
        
    }

    private void processContest(String[] values) {
        // TODO Auto-generated method stub
        
    }

    /**
     * Read a tab-separated values from a file.
     * 1st line should contain the column headers.
     * Supported column headers are:
     * account (required)
     * alias
     * displayname
     * group
     * password
     * permdisplay
     * permlogin
     * site
     * 
     * @param filename
     * @param existingAccounts
     * @return an array of accounts
     * @throws Exception
     */
    public Account[] fromTSVFile(String filename, Account[] existingAccounts) throws Exception {
        if (existingAccounts != null && existingAccounts.length > 0) {
            for (int i = 0; i < existingAccounts.length; i++) {
                existingAccountsMap.put(existingAccounts[i].getClientId(), existingAccounts[i]);
            }
        }
        int lineCount = 0;
        String[] columns;
        FileReader fileReader = new FileReader(filename);
        BufferedReader in = new BufferedReader(fileReader);
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
            for (int i = 0; i < columns.length; i++) {
                if (columns[i].equalsIgnoreCase("site")) {
                    siteColumn = i;
                }
                if (columns[i].equalsIgnoreCase("account")) {
                    accountColumn = i;
                }
                if (columns[i].equalsIgnoreCase("displayname")) {
                    displayNameColumn = i;
                }
                if (columns[i].equalsIgnoreCase("password")) {
                    passwordColumn = i;
                }
                if (columns[i].equalsIgnoreCase("group")) {
                    groupColumn = i;
                }
                if (columns[i].equalsIgnoreCase("permdisplay")) {
                    permDisplayColumn = i;
                }
                if (columns[i].equalsIgnoreCase("permlogin")) {
                    permLoginColumn = i;
                }
                if (columns[i].equalsIgnoreCase("externalid")) {
                    groupColumn = i;
                }
                if (columns[i].equalsIgnoreCase("alias")) {
                    groupColumn = i;
                }
            }
            if (accountColumn == -1 || siteColumn == -1 || passwordColumn == -1) {
                String msg = "1st line should be the row headers (account, password, and site are required)";
                throw new IllegalTSVFormatException(msg);
            }
        }
        line = in.readLine();
        lineCount++;
        while (line != null) {
            try {
                if (line.startsWith("#")) {
                    line = in.readLine();
                    lineCount++;
                    continue;
                }
                String[] values = TabSeparatedValueParser.parseLine(line);
                Account account = getAccount(values);
                if (account == null) {
                    String msg = filename + ":" + lineCount + ": " + " please create the account first (" + values[accountColumn] + ")";
                    throw new IllegalTSVFormatException(msg);
                }
                accountMap.put(account.getClientId(), account);
            } catch (IllegalTSVFormatException e2) {
                // already a properly formatted exception
                throw e2;
            } catch (Exception e) {
                String msg = "Error " + filename + ":" + lineCount + ": " + e.getMessage();
                throw new Exception(msg);
            }
            line = in.readLine();
            lineCount++;
        }
        in.close();
        fileReader.close();
        in = null;
        fileReader = null;
        return accountMap.values().toArray(new Account[accountMap.size()]);
    }
}
