package edu.csus.ecs.pc2.core.imports;

import java.util.Arrays;
import java.util.List;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.ICPCTSVLoader;

/**
 * Unit Tests for LoadICPCTSVData.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class LoadICPCTSVDataTest extends AbstractTestCase {

   
    public void testCheckFiles() throws Exception {
        LoadICPCTSVData load = new LoadICPCTSVData();

        String groupsFilename = getTestFilename(LoadICPCTSVData.GROUPS_FILENAME);

        assertFileExists(groupsFilename);
        load.checkFiles(groupsFilename);

        Group[] groups = ICPCTSVLoader.loadGroups(load.getGroupsFilename());
        Account[] accounts = ICPCTSVLoader.loadAccounts(load.getTeamsFilename());

        assertEquals("Number of groups", 6, groups.length);
        assertEquals("Number of acounts", 3, accounts.length);

    }
    
    public void testLoad() throws Exception {
        LoadICPCTSVData load = new LoadICPCTSVData();

        String groupsFilename = getTestFilename(LoadICPCTSVData.GROUPS_FILENAME);

        assertFileExists(groupsFilename);
        load.checkFiles(groupsFilename);

        Group[] groups = ICPCTSVLoader.loadGroups(load.getGroupsFilename());
        Account[] accounts = ICPCTSVLoader.loadAccounts(load.getTeamsFilename());

        assertEquals("Number of groups", 6, groups.length);
        assertEquals("Number of acounts", 3, accounts.length);

        IInternalContest contest = new SampleContest().createContest(1, 1, 12, 12, true);

        assertNotNull("contest", contest);

        List<Group> groupList = Arrays.asList(groups);
        List<Account> accountList = Arrays.asList(accounts);
        checkIfGroupsAssigned(accountList);
        
        load.updateGroupsAndAccounts(contest, groupList, accountList);

        checkIfGroupsAssigned(accountList);

        checkIfTeamAssignments(accountList);

        Account chile = accountList.get(2);

        assertEquals("External name ", "University of Chile", chile.getExternalName());
        assertEquals("Display Name", "University of Chile", chile.getDisplayName());
        assertEquals("Team Name", "Natural Log", chile.getTeamName());
        assertEquals("Country Code", "CL", chile.getCountryCode());
        
        edu.csus.ecs.pc2.core.security.Permission.Type[] permList = getPermList(Type.TEAM);
        
        assertEquals("Expecting same permissions ", 7,  permList.length);

        ElementId id = chile.getGroupId();
        Group groupId = lookupGroup(groupList, id);
        assertEquals("Group Id ", 206, groupId.getGroupId());

    }

    private Group lookupGroup(List<Group> groupList, ElementId id) {
        for (Group group : groupList) {
            if (group.getElementId().equals(id)) {
                return group;
            }
        }
        return null;
    }

    private void checkIfTeamAssignments(List<Account> accountList) {
        int num = 1;
        for (Account account : accountList) {
            assertEquals("Expecting same account number ", num, account.getClientId().getClientNumber());
            num++;
        }

    }

    private void checkIfGroupsAssigned(List<Account> accountList) {

        for (Account account : accountList) {
            if (account.getGroupId() == null) {
                fail("Expecting group assigned to account " + account);
            }
        }
    }

}
