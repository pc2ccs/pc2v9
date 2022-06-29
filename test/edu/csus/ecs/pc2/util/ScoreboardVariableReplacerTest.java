// Copyright (C) 1989-2021 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.util;

import java.util.Arrays;

import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 *
 */
public class ScoreboardVariableReplacerTest extends AbstractTestCase {

    /**
     * Test substituteDisplayNameVariables with var string, account and group.
     * 
     * @throws Exception
     */
    public void testSubstituteDisplayNameVariablesStringAccountGroup() throws Exception {

        IInternalContest contest = loadFullSampleContest(null, "mini");
        assertNotNull(contest);

        assertEquals("group count ", 12, contest.getGroups().length);

        Account[] accounts = getTeamAccounts(contest);
        assertEquals("accounts count ", 151, accounts.length);
        Arrays.sort(accounts, new AccountComparator());

        Account account3 = accounts[2];
        Group group = contest.getGroup(account3.getGroupId());

        String inputString = "Team Name: " + ScoreboardVariableReplacer.TEAM_NAME + //
                ", number" + ScoreboardVariableReplacer.CLIENT_NUMBER + //
                " " + ScoreboardVariableReplacer.SITE_NUMBER + //
                " " + ScoreboardVariableReplacer.EXTERNAL_ID + //
                " login=" + ScoreboardVariableReplacer.TEAM_LOGIN_NAME + //
                " group=" + ScoreboardVariableReplacer.GROUP_NAME + //
                "";

        String actual = ScoreboardVariableReplacer.substituteDisplayNameVariables(inputString, account3, group);

        String expected = "Team Name: Whitworth Freebooters, number103 1 307562 login=team103 group=Northeast - EWU, Cheney/Spokane D1";

        assertEquals("Expecting sub string ", expected, actual);

    }

    /**
     * Print all display variables and their values
     * 
     * @param contest
     * @param account
     */
    public void printAllVariables(IInternalContest contest, Account account) {
        String[] names = ScoreboardVariableReplacer.VARIABLE_NAMES;
        Arrays.sort(names);
        ;
        for (String varName : names) {
            Object afterString = ScoreboardVariableReplacer.substituteDisplayNameVariables(varName, contest, account);
            System.out.println(varName + " = " + afterString);
        }
    }

    /**
     * Test substituteDisplayNameVariables with contest and account.
     * 
     * @throws Exception
     */
    public void testSubstituteDisplayNameVariablesStringIInternalContestAccount() throws Exception {

        IInternalContest contest = loadFullSampleContest(null, "mini");
        assertNotNull(contest);

        assertEquals("group count ", 12, contest.getGroups().length);

        Account[] accounts = getTeamAccounts(contest);
        assertEquals("accounts count ", 151, accounts.length);
        Arrays.sort(accounts, new AccountComparator());

        Account account120 = accounts[119];

//        printAllVariables(contest, account120);

        String inputString = "Team Name: " + ScoreboardVariableReplacer.TEAM_NAME + //
                ", number" + ScoreboardVariableReplacer.CLIENT_NUMBER + //
                " " + ScoreboardVariableReplacer.SITE_NUMBER + //
                " " + ScoreboardVariableReplacer.EXTERNAL_ID + //
                " login=" + ScoreboardVariableReplacer.TEAM_LOGIN_NAME + //
                " group=" + ScoreboardVariableReplacer.GROUP_ID + //
                " group nam=" + ScoreboardVariableReplacer.GROUP_NAME + //
                "";

        String actual = ScoreboardVariableReplacer.substituteDisplayNameVariables(inputString, contest, account120);

        String expected = "Team Name: UBC!, number514 1 309407 login=team514 group=12545 group nam=Canada - University of British Columbia D1";

        assertEquals("Expecting sub string ", expected, actual);

        /**
         * 
         * 
         * GROUP_ID, // SHORT_SCHOOL_NAME, // LONG_SCHOOL_NAME, // COUNTRY_CODE, //
         */

        inputString = "Team Name: " + ScoreboardVariableReplacer.TEAM_NAME + //
                ", number=" + ScoreboardVariableReplacer.CLIENT_NUMBER + //
                " gid=" + ScoreboardVariableReplacer.GROUP_ID + //
                " sschool=" + ScoreboardVariableReplacer.SHORT_SCHOOL_NAME + //
                " lschool=" + ScoreboardVariableReplacer.LONG_SCHOOL_NAME + //
                " cc=" + ScoreboardVariableReplacer.COUNTRY_CODE + //
                "";

        actual = ScoreboardVariableReplacer.substituteDisplayNameVariables(inputString, contest, account120);

        expected = "Team Name: UBC!, number=514 gid=12545 sschool=U British Columbia lschool=UBC! (U British Columbia) cc=CAN";

        assertEquals("Expecting sub string ", expected, actual);
    }

}
