// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;
import edu.csus.ecs.pc2.imports.ccs.ICPCTSVLoader;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;

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
        assertEquals("Number of accounts", 3, accounts.length);

    }

    public void testLoad() throws Exception {
        LoadICPCTSVData load = new LoadICPCTSVData();

        String groupsFilename = getTestFilename(LoadICPCTSVData.GROUPS_FILENAME);

        assertFileExists(groupsFilename);
        load.checkFiles(groupsFilename);

        Group[] groups = ICPCTSVLoader.loadGroups(load.getGroupsFilename());
        Account[] accounts = ICPCTSVLoader.loadAccounts(load.getTeamsFilename());

        assertEquals("Number of groups", 6, groups.length);
        assertEquals("Number of accounts", 3, accounts.length);

        IInternalContest contest = new SampleContest().createContest(1, 1, 12, 12, true);

        assertNotNull("contest", contest);

        List<Group> groupList = Arrays.asList(groups);
        List<Account> accountList = Arrays.asList(accounts);
        checkIfGroupsAssigned(contest, accountList);

        load.updateGroupsAndAccounts(contest, groupList, accountList);

        checkIfGroupsAssigned(contest, accountList);

        checkIfTeamAssignments(accountList);

        Account chile = accountList.get(2);

        assertEquals("External name ", "University of Chile", chile.getExternalName());
        assertEquals("Display Name", "University of Chile", chile.getDisplayName());
        assertEquals("Team Name", "Natural Log", chile.getTeamName());
        assertEquals("Country Code", "CL", chile.getCountryCode());

        edu.csus.ecs.pc2.core.security.Permission.Type[] permList = getPermList(Type.TEAM);

        assertEquals("Expecting same permissions ", 7,  permList.length);

        ElementId id = chile.getPrimaryGroupId();
        Group groupId = lookupGroup(groupList, id);
        assertEquals("Group Id ", 206, groupId.getGroupId());

    }

    public void testLoadMultipleGroups() throws Exception {
        LoadICPCTSVData load = new LoadICPCTSVData();

        String groupsFilename = getTestFilename(LoadICPCTSVData.GROUPS_FILENAME);

        assertFileExists(groupsFilename);
        load.checkFiles(groupsFilename);

        Group[] groups = ICPCTSVLoader.loadGroups(load.getGroupsFilename());
        Account[] accounts = ICPCTSVLoader.loadAccounts(load.getTeamsFilename());

        assertEquals("Number of groups", 6, groups.length);
        assertEquals("Number of accounts", 3, accounts.length);

        IInternalContest contest = new SampleContest().createContest(1, 1, 12, 12, true);

        assertNotNull("contest", contest);

        List<Group> groupList = Arrays.asList(groups);
        List<Account> accountList = Arrays.asList(accounts);
        checkIfGroupsAssigned(contest, accountList);

        load.updateGroupsAndAccounts(contest, groupList, accountList);

        checkIfGroupsAssigned(contest, accountList);

        checkIfTeamAssignments(accountList);

        Account co = accountList.get(1);

        assertEquals("External name ", "University of Colorado", co.getExternalName());
        assertEquals("Display Name", "University of Colorado", co.getDisplayName());
        assertEquals("Team Name", "Rams", co.getTeamName());
        assertEquals("Country Code", "USA", co.getCountryCode());

        edu.csus.ecs.pc2.core.security.Permission.Type[] permList = getPermList(Type.TEAM);

        assertEquals("Expecting same permissions ", 7,  permList.length);

        // We know at least one group is assigned, or we'd assert above (checkIfGroupsAssigned)
        for(ElementId id : co.getGroupIds()) {
            Group groupId = lookupGroup(groupList, id);
            assertNotNull("Group Id " + id.toString(), groupId);
            int gid = groupId.getGroupId();
            assertTrue("Wrong group id", gid == 202 || gid == 204 || gid == 503);
        }
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

    private void checkIfGroupsAssigned(IInternalContest contest, List<Account> accountList) {

        int num = 1;
        for (Account account : accountList) {
            if (account.getGroupIds() == null) {
                fail("Expecting group assigned to account " + account);
            }
            int n = account.getGroupIds().size();
            if(num == 1) {
                // account index 1 has 3 groups assigned, the "A" groups (any group starting with A).
                assertEquals("Expecting 3 groups for account", 3, n);
                for(ElementId groupElementId : account.getGroupIds()) {
                    assertTrue("Group for account does not start with A", contest.getGroup(groupElementId).getDisplayName().startsWith("A"));
                }
            } else if(n != 1) {
                assertEquals("Expecting 1 group for account", 1, n);
            }
        }
    }

    private IInternalContest loadSampleContest(IInternalContest contest, String sampleName) throws Exception {
        IContestLoader loader = new ContestSnakeYAMLLoader();
        String configDir = getTestSampleContestConfigDirectory(sampleName);

        try {
            return loader.fromYaml(contest, configDir);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw e;
        }
    }

    private String getTestSampleContestConfigDirectory(String contestName) {
        return getTestSampleContestDirectory(contestName) + File.separator + IContestLoader.CONFIG_DIRNAME;
    }

    private String getTestSampleContestConfigFile(String contestName, String filename) {
        return getTestSampleContestDirectory(contestName) + File.separator + IContestLoader.CONFIG_DIRNAME + File.separator + filename;
    }

    /**
     * Test loading a teams.tsv twice.
     *
     * The 2nd load of teams and groups, in particular groups, caused all accounts' groups to be "empty"
     * on the Accounts tab.  See https://github.com/pc2ccs/pc2v9/issues/318 for details.
     *
     * @throws Exception
     */
    public void testReLoadTSV() throws Exception {

        String contestName = "mini";
        String groupsFilename = getTestSampleContestConfigFile(contestName, LoadICPCTSVData.GROUPS_FILENAME);
        assertFileExists(groupsFilename);

        IInternalContest contest = loadSampleContest(null, contestName);
        assertNotNull(contest);
        IInternalController controller = new SampleContest().createController(contest, true, false);

        LoadICPCTSVData loader = new LoadICPCTSVData();
        loader.setContestAndController(contest, controller);

        boolean loaded = loader.loadFiles(groupsFilename, false, false);
        assertTrue("Expecting "+contestName+" contest loaded", loaded);

        assertEquals("Number of groups", 12, contest.getGroups().length);
        assertEquals("Number of accounts", 167, contest.getAccounts().length);

        loader.checkFiles(groupsFilename);

        Account[] accounts = ICPCTSVLoader.loadAccounts(loader.getTeamsFilename());

        /**
         * Groups from contest/model, the authoritative groups from model.
         */
        Group[] modelGroups = contest.getGroups();

        assertEquals("Number of groups", 12, modelGroups.length);
        assertEquals("Number of accounts", 151, accounts.length);

        // Load groups from file, merge with modelGroups list.
        Group[] mergedGroups = ICPCTSVLoader.loadGroups(loader.getGroupsFilename(), modelGroups);

        Account[] accounts2 = ICPCTSVLoader.loadAccounts(loader.getTeamsFilename());

        assertEquals("Number of groups", 12, mergedGroups.length);
        assertEquals("Number of accounts", 151, accounts2.length);

        for (int i = 0; i < modelGroups.length; i++) {
            assertEquals("(" + i + ") Expecting same group element id for group " + modelGroups[i], modelGroups[i].getElementId(), mergedGroups[i].getElementId());
        }

        // check that all accounts assigned groups in the model/contest
        for (Account account : accounts2) {
            Group group = contest.getGroup(account.getPrimaryGroupId());
            assertNotNull("Expecting group to exist in contest/model " + account.getPrimaryGroupId(), group);
        }
    }
}
