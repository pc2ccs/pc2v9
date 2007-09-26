/**
 * 
 */
package edu.csus.ecs.pc2.core.imports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Vector;

import edu.csus.ecs.pc2.core.exception.IllegalTSVFormatException;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.Site;
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
    
    private ICPCImportData importData;
    private Site[] sites;
    
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

    boolean fromDirectory(String directory, Account[] existingAccounts, Site[] existingSites) throws Exception {
        this.sites = existingSites;
        boolean result = false;
        importData = new ICPCImportData();
        
        // TODO use or delete
        if (existingAccounts != null && existingAccounts.length > 0) {
            for (int i = 0; i < existingAccounts.length; i++) {
                existingAccountsMap.put(existingAccounts[i].getClientId(), existingAccounts[i]);
            }
        }
        
        String fs = File.separator;
        String path = directory + fs;
        String file = "PC2_Contest.tab";
        if (new File(path+file).exists()){
            readFile(path+file, CONTEST_TAB);
        } else {
            throw new FileNotFoundException(path+file);
        }

        file = "_PC2_Site.tab";
        if (!new File(path+file).exists()){
            file = "PC2_Site.tab";
            if (!new File(path+file).exists()){
                throw new FileNotFoundException(path+file);
            }
        }
        readFile(path+file, SITE_TAB);

        file = "_PC2_Team.tab";
        if (!new File(path+file).exists()){
            file = "PC2_Team.tab";
            if (!new File(path+file).exists()){
                throw new FileNotFoundException(path+file);
            }
        }
        readFile(path+file, TEAM_TAB);
        // we made it to the end successfully
        result = true;
        
        return result;
    }
    
    void readFile(String filename, int fileType) throws Exception {
        Vector<ICPCAccount> accounts = new Vector<ICPCAccount>();
        Vector<Group> groups = new Vector<Group>();
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
                        accounts.add(processTeam(values));                      
                        break;

                    case 22:
                        groups.add(processSite(values));
                        break;
                        
                    case 33:
                        importData.setContestTitle(processContest(values));
                        break;
                        
                    default:
                        new Exception("Unknown file type");
                        break;
                }
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
        if (fileType == TEAM_TAB) {
            importData.setAccounts(accounts.toArray(new ICPCAccount[accounts.size()]));                      
        } else if (fileType == SITE_TAB) {
            importData.setGroups(accounts.toArray(new Group[groups.size()]));                      
        }
    }
    
    private ICPCAccount processTeam(String[] values) {
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
//        String siteString = values[siteColumn];
        ICPCAccount account = new ICPCAccount();
        
        account.setExternalId(values[2+offset]);
        account.setGroupId(values[3+offset]);
        account.setExternalName(values[5+offset]);
        account.setLongSchoolName(values[6+offset]);
        account.setShortSchoolName(values[7+offset]);
        return account;
    }

    private Group processSite(String[] values) {
        Group group = new Group();
        int offset;
        
        if (values.length == 7) {
            // we have no pc2 site info
            offset = 1;
        } else {
            offset = 0;
            // we have the pc2 site info
        }
        // 0 is the pc2 site id
        // 1 is the contest Id
        group.setGroupId(Integer.parseInt(values[2-offset]));
        group.setGroupTitle(values[3-offset]);
        if (values.length == 9) {
            int siteNum = Integer.parseInt(values[0]);
            group.setSite(sites[siteNum-1].getElementId());
        }
        return group;
    }

    private String processContest(String[] values) {
        String title = "";
        if (values.length > 0) {
            title = values[1];
        }
        return title;
    }
}
