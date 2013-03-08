package edu.csus.ecs.pc2.core.imports;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.imports.ccs.ICPCTSVLoader;
import edu.csus.ecs.pc2.ui.FrameUtilities;
import edu.csus.ecs.pc2.ui.UIPlugin;

/**
 * Load ICPC TSV files into contest.
 * 
 * Read input .tsv files, validate then if valid load into contest.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class LoadICPCTSVData implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 1611218320856176033L;

    public static final String TEAMS_FILENAME = "teams.tsv";

    public static final String GROUPS_FILENAME = "groups.tsv";

    private String teamsFilename = "";

    private String groupsFilename = "";

    private IInternalContest contest;

    private IInternalController controller;

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        contest = inContest;
        controller = inController;
    }

    public boolean loadFiles(String filename) throws Exception {

        if (checkFiles(filename)) {

            Group[] groups = ICPCTSVLoader.loadGroups(groupsFilename);
            Account[] accounts = ICPCTSVLoader.loadAccounts(teamsFilename);

            String nl = System.getProperty("line.separator");

            String message = "Add " + nl + accounts.length + " accounts and " + nl + groups.length + " groups?";

            int result = FrameUtilities.yesNoCancelDialog(null, message, "Load TSV files");
            
            if (result == JOptionPane.YES_OPTION) {
                
                List<Group> groupList = Arrays.asList(groups);
                List<Account> accountList = Arrays.asList(accounts);

                /**
                 * Merge/update groups and account into existing groups and accounts, create if necessary
                 */
                updateGroupsAndAccounts (contest, groupList, accountList);
                
                /**
                 * Update Groups
                 */
                Group [] updatedGroups = (Group[]) groupList.toArray(new Group[groupList.size()]);
                for (Group group : updatedGroups) {
                    getController().updateGroup(group);
                }
                
                /**
                 * UpdateAccounts
                 */
                Account [] updatedAccounts = (Account[]) accountList.toArray(new Account[accountList.size()]);

                getController().updateAccounts(updatedAccounts);

                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }



    /**
     * Merge/update groups and account into existing groups and accounts, create if necessary
     * @param contest2
     * @param groupList
     * @param accountList
     */
    protected void updateGroupsAndAccounts(IInternalContest inContest, List<Group> groupList, List<Account> accountList) {

        int i = 0;
        
        for (Group group : groupList) {
            
            Group existingGroup = lookupGroup (inContest, group.getGroupId());
            if (existingGroup != null){

                /**
                 * Update certain fields in group
                 */
                existingGroup.updateFrom(group);
                
            } else {
                existingGroup = group;
            }
            
            groupList.set(i, existingGroup);
            
            i++;
        }
        
        i = 0;
        
        for (Account account : accountList) {
            
            Account existingAccount = inContest.getAccount(account.getClientId());
            
            if (existingAccount != null){
                existingAccount.updateFrom(account);
            } else {
                existingAccount = account;
            }
            
            accountList.set(i, existingAccount);
            i ++;
        }
        
    }

    /**
     * Lookup group by externalId
     * @param contest2
     * @param externalId
     * @return
     */
    private Group lookupGroup(IInternalContest contest2, int externalId) {
        
        Group [] groups = contest2.getGroups();
        for (Group group : groups) {
            if (group.getGroupId() == externalId){
                return group;
            }
        }
        return null;
    }

    protected boolean checkFiles(String filename) throws Exception {

        File file = new File(filename);
        if (!file.isFile()) {
            throw new FileNotFoundException("File not found: " + filename);
        }

        if (filename.endsWith(TEAMS_FILENAME)) {
            teamsFilename = filename;
            groupsFilename = filename;
            groupsFilename = groupsFilename.replaceFirst(TEAMS_FILENAME, GROUPS_FILENAME);
        } else if (filename.endsWith(GROUPS_FILENAME)) {
            teamsFilename = filename;
            groupsFilename = filename;
            teamsFilename = teamsFilename.replaceFirst(GROUPS_FILENAME, TEAMS_FILENAME);
        } else {
            throw new Exception("Must select either " + TEAMS_FILENAME + " or " + GROUPS_FILENAME);
        }

        file = new File(teamsFilename);
        if (!file.isFile()) {
            throw new FileNotFoundException("File not found: " + teamsFilename);
        }

        file = new File(groupsFilename);
        if (!file.isFile()) {
            throw new FileNotFoundException("File not found: " + groupsFilename);
        }

        return true;

    }

    public String getPluginTitle() {
        return "Load TSV Files";
    }

    public IInternalController getController() {
        return controller;
    }

    
    public final String getGroupsFilename() {
        return groupsFilename;
    }
    
    public final String getTeamsFilename() {
        return teamsFilename;
    }
}
