// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api.implementation;

import java.util.Properties;
import java.util.Vector;

import edu.csus.ecs.pc2.api.IStanding;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.standings.ScoreboardUtilites;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class GenerateStandingsTest extends AbstractTestCase {

    private SampleContest sampleContest = new SampleContest();

    /**
     * Test DSA/standings with default scoring properties.
     * 
     * @throws Exception
     */
    public void testgetStandingsDefaultProperties() throws Exception {

        IInternalContest contest = sampleContest.createStandardContest();
        IInternalController controller = sampleContest.createController(contest, true, false);

        /**
         * TODO Fix DSA so there is no reliance on current account/clientId
         * 
         * From DSA:
         * respectSendToTeam = isAllowed (theContest, theContest.getClientId(), Permission.Type.RESPECT_NOTIFY_TEAM_SETTING);
         * respectEOC = isAllowed (theContest, theContest.getClientId(), Permission.Type.RESPECT_EOC_SUPPRESSION);
         *  - both return false.
         * 
         * Bug - if there is no account for model clientId then DSA will not work (assign rank, etc.)
         */
        // Must add account for server (current contest.getClientId()) else DSA fails to rank teams.
        ensureAccount(contest);
        Account userAccount = contest.getAccount(contest.getClientId());
        assertNotNull(userAccount);
        assertFalse(userAccount.isAllowed(Permission.Type.RESPECT_NOTIFY_TEAM_SETTING));
        assertFalse(userAccount.isAllowed(Permission.Type.RESPECT_EOC_SUPPRESSION));

        //        contest.setClientId(getBoardClientId(contest));

        // Line is: runId,teamId,problemLetter,elapsed,solved[,sendToTeamsYN]
        String[] runsData = { //
                "1,1,A,1,No,No,4", // 0 (a No before first yes Security Violation)
                "2,1,A,1,No,No,2", // 0 (a No before first yes Compilation Error)
                "3,1,A,1,No,No,1", // 20 (a No before first yes)
                "4,1,A,3,Yes,No,0", // 3 (first yes counts Minute points but never Run Penalty points)
                "5,1,A,5,No,No,1", // zero -- after Yes
                "6,1,A,7,Yes,No,0", // zero -- after Yes
                "7,1,A,9,No,No,1", // zero -- after Yes
                "8,1,B,11,No,No,1", // zero -- not solved
                "9,2,A,48,No,No,4", // 0 (a No before first yes Security Violation)
                "10,2,A,50,Yes,No,0", // 50 (minute points; no Run points on first Yes)
                "11,2,B,35,No,No,1", // zero -- not solved
                "12,2,B,40,No,No,1", // zero -- not solved
        };

        assertEquals("team count", 120, contest.getAccounts(Type.TEAM).size());

        for (String runInfoLine : runsData) {
            SampleContest.addRunFromInfo(contest, runInfoLine);
        }

        //        Account[] accounts = getTeamAccounts(contest);
        //        for (Account account : accounts) {
        //            account.addPermission(edu.csus.ecs.pc2.core.security.Permission.Type.RESPECT_NOTIFY_TEAM_SETTING);
        //        }

        //        Judgement[] judgements = contest.getJudgements();
        //        for (Judgement judgement : judgements) {
        //            System.out.println(judgement.getAcronym() + " " + judgement.getDisplayName());
        //        }

        //        Run[] runs = contest.getRuns();
        //        for (Run run : runs) {
        //            JudgementRecord jr = run.getJudgementRecord();
        //            assertNotNull(jr.getJudgementId());
        //            Judgement judgement = contest.getJudgement(jr.getJudgementId());
        //            assertNotNull(judgement);
        //            assertNotNull(judgement.getAcronym());
        //            
        //        }

        GenerateStandings generateStandings = new GenerateStandings();

        /**
         * Standings rows.
         */
        IStanding[] rows = generateStandings.getStandings(contest, controller.getLog());

        assertNotNull(rows);
        assertEquals("Expecting number of standing rows ", 120, rows.length);

        for (IStanding iStanding : rows) {
            if (iStanding.getRank() == 1) {
                // ensure that DSA is working, the first row should have penalty points 
                assertFalse("Expecting penalty points not zero for team " + iStanding.getClient().getLoginName(), iStanding.getPenaltyPoints() == 0);
            }
        }

        // check ranks for teams
        assertEqualsStandings(rows[0], "team1,1,1,43");
        assertEqualsStandings(rows[1], "team2,2,1,70");
        assertEqualsStandings(rows[2], "team3,3,0,0");
    }



    /**
     * Test DSA/standings with points per no of -5.
     * 
     * @throws Exception
     */
    public void testTestwithScoringProperties() throws Exception {

        IInternalContest contest = sampleContest.createStandardContest();
        IInternalController controller = sampleContest.createController(contest, true, false);
        
        Properties scoreProps = ScoreboardUtilites.getScoringProperties(contest);
        scoreProps.setProperty(DefaultScoringAlgorithm.POINTS_PER_NO, "-5");
        contest.getContestInformation().setScoringProperties(scoreProps);
        
        assertEquals ("-5", ScoreboardUtilites.getScoringProperties(contest).getProperty(DefaultScoringAlgorithm.POINTS_PER_NO));

        // Must add account for server (current contest.getClientId()) else DSA fails to rank teams.
        ensureAccount(contest);
        //    contest.setClientId(getBoardClientId(contest));

        // Line is: runId,teamId,problemLetter,elapsed,solved[,sendToTeamsYN]
        String[] runsData = { //
                "1,1,A,1,No,No,4", // 0 (a No before first yes Security Violation)
                "2,1,A,1,No,No,2", // 0 (a No before first yes Compilation Error)
                "3,1,A,1,No,No,1", // 20 (a No before first yes)
                "4,1,A,3,Yes,No,0", // 3 (first yes counts Minute points but never Run Penalty points)
                "5,1,A,5,No,No,1", // zero -- after Yes
                "6,1,A,7,Yes,No,0", // zero -- after Yes
                "7,1,A,9,No,No,1", // zero -- after Yes
                "8,1,B,11,No,No,1", // zero -- not solved
                "9,2,A,48,No,No,4", // 0 (a No before first yes Security Violation)
                "10,2,A,50,Yes,No,0", // 50 (minute points; no Run points on first Yes)
                "11,2,B,35,No,No,1", // zero -- not solved
                "12,2,B,40,No,No,1", // zero -- not solved
        };

        assertEquals("team count", 120, contest.getAccounts(Type.TEAM).size());

        for (String runInfoLine : runsData) {
            SampleContest.addRunFromInfo(contest, runInfoLine);
        }


        GenerateStandings generateStandings = new GenerateStandings();

        /**
         * Standings rows.
         */
        IStanding[] rows = generateStandings.getStandings(contest, controller.getLog());

        assertNotNull(rows);
        assertEquals("Expecting number of standing rows ", 120, rows.length);

        for (IStanding iStanding : rows) {
            if (iStanding.getRank() == 1) {
                // ensure that DSA is working, the first row should have penalty points 
                assertFalse("Expecting penalty points not zero for team " + iStanding.getClient().getLoginName(), iStanding.getPenaltyPoints() == 0);
            }
        }

        // check ranks for teams
        assertEqualsStandings(rows[0], "team1,1,1,-7");
        assertEqualsStandings(rows[1], "team2,2,1,45");
        assertEqualsStandings(rows[2], "team3,3,0,0");

    }    

    /**
     * Check whether standings rank, etc. are equal.
     * 
     * @param iStanding
     * @param loginName
     * @param rank
     * @param numProblemsSolved
     * @param penaltyPoints
     */
    private void assertEqualsStandings(IStanding standing, String loginName, int rank, int numProblemsSolved, int penaltyPoints) {

        //        System.out.println("\""+standing.getClient().getLoginName()+ "\"," + standing.getRank() + "," + standing.getNumProblemsSolved() + "," + standing.getPenaltyPoints());       

        assertEquals("For " + loginName + " expected rank ", rank, standing.getRank());
        assertEquals("For " + loginName + " expected numProblemsSolved ", numProblemsSolved, standing.getNumProblemsSolved());
        assertEquals("For " + loginName + " expected penaltyPoints ", penaltyPoints, standing.getPenaltyPoints());
        assertEquals("For " + loginName + " expected loginName ", loginName, standing.getClient().getLoginName());
    }

    /**
     *  Check whether standings rank, etc. are equal.
     *  
     *  Uses rankString as input, ex. "team3,3,0,0" 
     *  
     * @param standing
     * @param rankString a string of fields.
     */
    private void assertEqualsStandings(IStanding standing, String rankString) {

        //        System.out.println("\""+standing.getClient().getLoginName()+ "," + standing.getRank() + "," + standing.getNumProblemsSolved() + "," + standing.getPenaltyPoints()+"\"");

        String[] fields = rankString.split(",");
        assertTrue(fields.length == 4);

        int idx = 0;
        String teamName = fields[idx++];
        int rank = Integer.parseInt(fields[idx++]);
        int numProblemsSolved = Integer.parseInt(fields[idx++]);
        int penaltyPoints = Integer.parseInt(fields[idx++]);
        assertEqualsStandings(standing, teamName, rank, numProblemsSolved, penaltyPoints);
    }

    /**
     * Ensure account exists, add if necessary.
     * 
     * @param contest
     * @return the account
     */
    private Account ensureAccount(IInternalContest contest) {
        Account userAccount = contest.getAccount(contest.getClientId());
        if (userAccount == null) {
            // create account if missing, ex. if is server client id, servers do not have an Account
            // they have a Site entry.
            userAccount = new Account(contest.getClientId(), contest.getClientId().getName(), contest.getClientId().getSiteNumber());
            contest.addAccount(userAccount);
        }
        return userAccount;
    }

    /**
     * Return a scoreboard clientId
     * 
     * @param contest
     * @return a random scoreboard clientId or null if no scoreboard accounts are present in model
     */
    protected ClientId getBoardClientId(IInternalContest contest) {

        Vector<Account> accounts = contest.getAccounts(Type.SCOREBOARD);
        if (accounts.isEmpty()) {
            return null;
        }
        return accounts.firstElement().getClientId();
    }

}
