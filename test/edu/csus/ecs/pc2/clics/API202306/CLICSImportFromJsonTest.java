// Copyright (C) 1989-2024 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, Troy Boudreau and John Buck.
package edu.csus.ecs.pc2.clics.API202306;

import java.io.File;

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
    
    public static final int NUM_EXPECTED_TEAMS = 140;
    public static final int NUM_EXPECTED_GROUPS = 9;
    
    public void testGroupsFromGroupsJson() throws Exception {
        
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 1, NUM_EXPECTED_TEAMS, 1, true);
        String groupsJson = AbstractTestCase.DEFAULT_PC2_TEST_DIRECTORY + File.separator + this.getClass().getSimpleName() + File.separator + GROUPS_JSON_FILE;
        sample.createController(contest, true, false); // creates StaticLog instance
        
        Group [] groups = CLICSGroup.fromJSON(contest, groupsJson, 1);
        assertNotNull("No groups were created", groups);
        assertEquals("Expected new accounts", NUM_EXPECTED_GROUPS, groups.length);
        
    }
    
    public void testAccountsFromTeamsJson() throws Exception {
        
        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 1, NUM_EXPECTED_TEAMS, 1, true);
        String teamsJson = AbstractTestCase.DEFAULT_PC2_TEST_DIRECTORY + File.separator + this.getClass().getSimpleName() + File.separator + TEAMS_JSON_FILE;
        String groupsJson = AbstractTestCase.DEFAULT_PC2_TEST_DIRECTORY + File.separator + this.getClass().getSimpleName() + File.separator + GROUPS_JSON_FILE;
        sample.createController(contest, true, false); // creates StaticLog instance
        
        Group [] groups = CLICSGroup.fromJSON(contest, groupsJson, 1);
        assertNotNull("No groups were created", groups);
        contest.addGroups(groups);
        Account [] accounts = CLICSTeam.fromJSON(contest, teamsJson, 1);
        assertNotNull("No accounts were created", accounts);
        assertEquals("Expected new accounts", NUM_EXPECTED_TEAMS, accounts.length);
        
    }

}
