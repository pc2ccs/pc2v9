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
import edu.csus.ecs.pc2.core.util.TabSeparatedValueParser;

/**
 * @author pc2@ecs.csus.edu
 * 
 */
public class LoadICPCData {

    private static final int TEAM_TAB = 11;
    private static final int SITE_TAB = 22;
    private static final int CONTEST_TAB = 33;
    
    private ICPCImportData importData;
    private Site[] sites;

    private HashMap<ClientId, Account> existingAccountsMap = new HashMap<ClientId, Account>();
    
    /**
     * 
     */
    public LoadICPCData() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ICPCImportData fromDirectory(String directory, Account[] existingAccounts, Site[] existingSites) throws Exception {
        this.sites = existingSites;
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
        
        return importData;
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
                Exception sendException = new Exception(msg);
                sendException.setStackTrace(e.getStackTrace());
                throw sendException;
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
        int clientNumber = 0;
        
        switch(values.length) {
            case 10:
                if (values[0].length() > 0) {
                    clientNumber = Integer.parseInt(values[0]);
                }
                offset = 0;
                break;
            case 9:
//                clientNumber++;
                offset = -1;
                break;
            default:
                // TODO consider throwing exception here
                break;
        }
        // TODO convert icpc site to pc2 site
//        String siteString = values[siteColumn];
        ICPCAccount account = new ICPCAccount();
        
        if (clientNumber != 0) {
            account.setAccountNumber(clientNumber);
        }
        account.setExternalId(values[1+offset]);
        account.setGroupId(values[2+offset]);
        account.setExternalName(values[4+offset]);
        account.setLongSchoolName(values[5+offset]);
        account.setShortSchoolName(values[6+offset]);
        return account;
    }

    private Group processSite(String[] values) {
        int offset;
        
        if (values.length == 8) {
            // we have no pc2 site info
            offset = -1;
        } else {
            offset = 0;
            // we have the pc2 site info
        }
        Group group = new Group(values[3+offset]);
        // 0 is the pc2 site id
        if (values.length == 9 && sites != null) {
            int siteNum = Integer.parseInt(values[0]);
            if (sites.length <= siteNum) {
                group.setSite(sites[siteNum-1].getElementId());
            }
        }
        // 1 is the contest Id
        group.setGroupId(Integer.parseInt(values[2+offset]));
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
