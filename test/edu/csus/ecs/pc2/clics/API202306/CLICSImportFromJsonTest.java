// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, Troy Boudreau and John Buck.
package edu.csus.ecs.pc2.clics.API202306;

import java.io.File;
import java.util.HashMap;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Test creating an array of Account objects from a CLICS compliant teams.json file
 *
 * @author John Buck
 *
 */
public class CLICSImportFromJsonTest extends AbstractTestCase {

    public static final String GROUPS_JSON_FILE = "groups.json";
    public static final String TEAMS_JSON_FILE = "teams.json";
    public static final String ORGANIZATIONS_JSON_FILE = "organizations.json";
    public static final String ACCOUNTS_JSON_FILE = "accounts.json";
    public static final String ACCOUNTS_YAML_FILE = "accounts.yaml";

    public static final int NUM_EXPECTED_TEAMS = 140;
    public static final int NUM_EXPECTED_GROUPS = 9;
    // Note the file actually has 140 institutions, but there are 2 duplicates
    public static final int NUM_EXPECTED_ORGANIZATIONS = 138;

    // for accounts.json and accounts.yaml reads
    public static final int NUM_EXPECTED_ACCOUNTS = 99;

    public void testGroupsFromGroupsJson() throws Exception {

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 1, NUM_EXPECTED_TEAMS, 1, true);
        String groupsJson = AbstractTestCase.DEFAULT_PC2_TEST_DIRECTORY + File.separator + this.getClass().getSimpleName() + File.separator + GROUPS_JSON_FILE;
        sample.createController(contest, true, false); // creates StaticLog instance

        // check creating groups from a groups.json type file
        Group [] groups = CLICSGroup.fromJSON(contest, new File(groupsJson), 1);
        assertNotNull("No groups were created from file " + groupsJson, groups);
        assertEquals("Expected new groups", NUM_EXPECTED_GROUPS, groups.length);

        // test also converting from string read from file
        groups = CLICSGroup.fromJSON(contest, String.join("", Utilities.loadFile(groupsJson)), NUM_EXPECTED_GROUPS);
        assertNotNull("No groups were created from json string", groups);
        assertEquals("Expected new groups", NUM_EXPECTED_GROUPS, groups.length);

    }

    public void testAccountsFromTeamsJson() throws Exception {

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 1, NUM_EXPECTED_TEAMS, 1, true);
        String teamsJson = AbstractTestCase.DEFAULT_PC2_TEST_DIRECTORY + File.separator + this.getClass().getSimpleName() + File.separator + TEAMS_JSON_FILE;
        String groupsJson = AbstractTestCase.DEFAULT_PC2_TEST_DIRECTORY + File.separator + this.getClass().getSimpleName() + File.separator + GROUPS_JSON_FILE;
        String orgsJson =  AbstractTestCase.DEFAULT_PC2_TEST_DIRECTORY + File.separator + this.getClass().getSimpleName() + File.separator + ORGANIZATIONS_JSON_FILE;
        sample.createController(contest, true, false); // creates StaticLog instance

        Group [] groups = CLICSGroup.fromJSON(contest, new File(groupsJson), 1);
        assertNotNull("No groups were created", groups);
        contest.addGroups(groups);

        // get organizations hashmap from a organizations.json type file
        HashMap<String, String[]> orgsMap = CLICSOrganization.fromJSON(new File(orgsJson));
        assertNotNull("No organizations were created from file " + orgsJson, orgsMap);
        assertEquals("Expected new organizations", NUM_EXPECTED_ORGANIZATIONS, orgsMap.size());

        // check creating accounts from a teams.json type file
        Account [] accounts = CLICSTeam.fromJSON(contest, new File(teamsJson), 1, orgsMap);
        assertNotNull("No accounts were created from " + teamsJson, accounts);
        assertEquals("Expected new accounts", NUM_EXPECTED_TEAMS, accounts.length);

        // now try reading teams from a string
        accounts = CLICSTeam.fromJSON(contest, String.join("",  Utilities.loadFile(teamsJson)), 1, orgsMap);
        assertNotNull("No accounts were created from JSON string", accounts);
        assertEquals("Expected new accounts", NUM_EXPECTED_TEAMS, accounts.length);


    }


    public void testOrganizationsFromOrganizationsJson() throws Exception {

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 1, NUM_EXPECTED_TEAMS, 1, true);
        String orgsJson = AbstractTestCase.DEFAULT_PC2_TEST_DIRECTORY + File.separator + this.getClass().getSimpleName() + File.separator + ORGANIZATIONS_JSON_FILE;
        sample.createController(contest, true, false); // creates StaticLog instance

        // check creating organizations hashmap from a organizations.json type file
        HashMap<String, String[]> orgsMap = CLICSOrganization.fromJSON(new File(orgsJson));
        assertNotNull("No organizations were created from file " + orgsJson, orgsMap);
        assertEquals("Expected new organizations", NUM_EXPECTED_ORGANIZATIONS, orgsMap.size());

        // now check loading from a string
        HashMap<String, String[]> orgsMap2 = CLICSOrganization.fromJSON(String.join("", Utilities.loadFile(orgsJson)));
        assertNotNull("No organizations were created from String", orgsMap2);
        assertEquals("Expected new organizations", NUM_EXPECTED_ORGANIZATIONS, orgsMap2.size());

    }

    public void testAccountsFromAccountsJson() throws Exception {

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 1, NUM_EXPECTED_ACCOUNTS, 1, true);
        String acctsJson = AbstractTestCase.DEFAULT_PC2_TEST_DIRECTORY + File.separator + this.getClass().getSimpleName() + File.separator + ACCOUNTS_JSON_FILE;
        sample.createController(contest, true, false); // creates StaticLog instance

        // check creating accounts from a accounts.json type file
        Account [] accounts = CLICSAccount.fromJSON(contest, new File(acctsJson), contest.getSiteNumber());
        assertNotNull("No accounts were created from " + acctsJson, accounts);
        assertEquals("Expected new accounts", NUM_EXPECTED_ACCOUNTS, accounts.length);

        // now try reading accts from a string
        accounts = CLICSAccount.fromJSON(contest, String.join("",  Utilities.loadFile(acctsJson)), contest.getSiteNumber());
        assertNotNull("No accounts were created from JSON string", accounts);
        assertEquals("Expected new accounts", NUM_EXPECTED_ACCOUNTS, accounts.length);


    }

    public void testAccountsFromAccountsYaml() throws Exception {

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 1, NUM_EXPECTED_ACCOUNTS, 1, true);
        String acctsYaml = AbstractTestCase.DEFAULT_PC2_TEST_DIRECTORY + File.separator + this.getClass().getSimpleName() + File.separator + ACCOUNTS_YAML_FILE;
        sample.createController(contest, true, false); // creates StaticLog instance

        // check creating accounts from a accounts.json type file
        Account [] accounts = CLICSAccount.fromYAML(contest, new File(acctsYaml), contest.getSiteNumber());
        assertNotNull("No accounts were created from " + acctsYaml, accounts);
        assertEquals("Expected new accounts", NUM_EXPECTED_ACCOUNTS, accounts.length);

        // now try reading accts from a string
        accounts = CLICSAccount.fromYAML(contest, String.join("\n",  Utilities.loadFile(acctsYaml)), contest.getSiteNumber());
        assertNotNull("No accounts were created from YAML string", accounts);
        assertEquals("Expected new accounts", NUM_EXPECTED_ACCOUNTS, accounts.length);


    }
}
