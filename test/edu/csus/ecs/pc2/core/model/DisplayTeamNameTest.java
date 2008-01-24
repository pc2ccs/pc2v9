package edu.csus.ecs.pc2.core.model;

import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.ContestInformation.TeamDisplayMask;

/**
 * Display Name JUnit test.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class DisplayTeamNameTest extends TestCase {

    public DisplayTeamNameTest(String arg0) {
        super(arg0);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void checkString(String title, String expected, String found) {
        assertTrue(title + " expected '" + expected + "' found '" + found + "'", expected.equals(found));
//        System.out.println("passed for "+title+" "+ " expected '" + expected + "' found '" + found + "'");
    }

    public void testOne() {

        InternalContest contest = new InternalContest();

        contest.setSiteNumber(22);
        Vector<Account> accounts = contest.generateNewAccounts(ClientType.Type.TEAM.toString(), 4, true);

        DisplayTeamName displayTeamName = new DisplayTeamName();
        displayTeamName.setContestAndController(contest, null);

        String displayName = "Last Team";

        int lastAccountIdx = accounts.size() - 1;
        Account teamAccount = accounts.elementAt(lastAccountIdx);
        teamAccount.setDisplayName(displayName);
        ClientId id = teamAccount.getClientId();

        for (TeamDisplayMask teamDisplayMask : TeamDisplayMask.values()) {
            displayTeamName.setTeamDisplayMask(teamDisplayMask);
            String outName = displayTeamName.getDisplayName(id);

            // TEAM4 @ site 22 is NONE ***
            // TEAM4 @ site 22 is LOGIN_NAME_ONLY team4
            // TEAM4 @ site 22 is DISPLAY_NAME_ONLY team4
            // TEAM4 @ site 22 is NUMBERS_AND_NAME 4 team4
            // TEAM4 @ site 22 is ALIAS team4 (not aliased)

            switch (teamDisplayMask) {
                case NONE:
                    checkString("Using " + teamDisplayMask, "***", outName);
                    break;
                case LOGIN_NAME_ONLY:
                    checkString("Using " + teamDisplayMask, id.getName(), outName);
                    break;
                case DISPLAY_NAME_ONLY:
                    checkString("Using " + teamDisplayMask, displayName, outName);
                    break;
                case NUMBERS_AND_NAME:
                    checkString("Using " + teamDisplayMask, id.getClientNumber()+" "+displayName, outName);
                    break;
                case ALIAS:
                    checkString("Using " + teamDisplayMask, "team4 (not aliased)", outName);
                    String alias = "Too legit to Quit";
                    teamAccount.setAliasName(alias);
                    outName = displayTeamName.getDisplayName(id);
                    checkString("Using " + teamDisplayMask, alias, outName);
                    break;
                default:
                    break;
            }
        }

    }
}
