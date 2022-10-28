// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

import java.io.File;
import java.util.List;
import java.util.Vector;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class CLICSJsonUtilitiesTest extends AbstractTestCase {

    public void testWithNoRuns() throws Exception {

        SampleContest sample = new SampleContest();

        IInternalContest contest = sample.createStandardContest();

        List<CLICSAward> awards = CLICSJsonUtilities.createAwardsList(contest);

        String outdir = getOutputDataDirectory(getName());
        ensureDirectory(outdir);

        String awardsFile = outdir + File.separator + "awards.json";

        int rowsWritten = CLICSJsonUtilities.writeAwardsJSONFile(awardsFile, awards);

        assertEquals("Expecting no award rows ", 0, rowsWritten);
    }

    public void testcreateAwardsList() throws Exception {

        /**
         * runsData columns.
         * 
         * 0 - run id, int
         * 1 - team id, int
         * 2 - problem letter, char
         * 3 - elapsed, int
         * 4 - solved, String &quot;Yes&quot; or No
         * 5 - send to teams, Yes or No
         * 6 - No Judgement index
         */
        String[] runsData = { //
                "1,1,A,1,No,No,4",// 
                "2,1,A,1,No,No,2", //
                "3,1,A,1,No,No,1", // 
                "4,1,A,3,Yes,No,0", // 
                "5,1,A,5,No,No,1", 
                "6,1,A,7,Yes,No,0", 
                "7,1,A,9,No,No,1", 
                "8,1,B,11,No,No,1", 
                "9,2,A,48,No,No,4", 
                "10,2,A,50,Yes,No,0", 
                "11,2,C,35,Yes,No,0", 
                "12,2,D,40,Yes,No,0", 
        };

        //        // Rank  TeamId Solved Penalty
        //        
        //        String [] rankData = {
        //                "1,team1,1,23",
        //                "2,team2,1,50"
        //        };
        //        
        //        String [] rankData5 = {
        //                "1,team1,1,33", // +7 for SV + 3 for CE
        //                "2,team2,1,57" // +7 for SV
        //        };

        InternalContest contest = createContestWithJudgedRuns(12, runsData, 8);

        assertNotNull(contest);

        List<CLICSAward> awards = CLICSJsonUtilities.createAwardsList(contest);

//        for (CLICSAward clicsAward : awards) {
//            System.out.println("debug award "+clicsAward.toJSON());
//        }

        assertEquals("Awards expected ", 4, awards.size());

        String outdir = getOutputDataDirectory(getName());
        ensureDirectory(outdir);

        String awardsFile = outdir + File.separator + "awards.json";

        CLICSJsonUtilities.writeAwardsJSONFile(awardsFile, awards);

//        editFile(awardsFile);

    }

    /**
     * Test load awards.json from file.
     * 
     * @throws Exception
     */
    public void testreadAwardsList() throws Exception {

        String dataDir = getDataDirectory(getName());

        //        ensureDirectory(dataDir);
        //        startExplorer(dataDir);

        String awardsFile = dataDir + File.separator + "bapc2020.awards.json";
        assertFileExists(awardsFile);

        //        editFile (awardsFile);

        List<CLICSAward> awards = CLICSJsonUtilities.readAwardsList(awardsFile);

        assertEquals("Expecting same number of awards", 26, awards.size());

        //        for (CLICSAward clicsAward : awards) {
        //            System.out.println("debug read award  "+clicsAward.toJSON());
        //        }

        // print assertCitationEquals based on data
        //        for (CLICSAward clicsAward : awards) {
        //            System.out.println("assertCitationEquals(awards, \""+
        //                    clicsAward.getId()+"\", \""+clicsAward.getTeamIds().get(0)+"\");");
        //        }

        assertCitationEquals(awards, "winner", "2");

        assertCitationEquals(awards, "group-winner-3", "2");
        assertCitationEquals(awards, "group-winner-4", "28");
        assertCitationEquals(awards, "group-winner-5", "54");
        assertCitationEquals(awards, "group-winner-6", "6");
        assertCitationEquals(awards, "group-winner-7", "7");
        assertCitationEquals(awards, "group-winner-8", "9");
        assertCitationEquals(awards, "group-winner-9", "10");
        assertCitationEquals(awards, "group-winner-10", "11");
        assertCitationEquals(awards, "group-winner-11", "52");
        assertCitationEquals(awards, "group-winner-13", "19");
        assertCitationEquals(awards, "group-winner-14", "56");
        assertCitationEquals(awards, "group-winner-15", "21");
        assertCitationEquals(awards, "group-winner-17", "42");

        assertCitationEquals(awards, "first-to-solve-crashingcompetitioncomputer", "2");
        assertCitationEquals(awards, "first-to-solve-grindinggravel", "2");
        assertCitationEquals(awards, "first-to-solve-housenumbering", "2");
        assertCitationEquals(awards, "first-to-solve-lowestlatency", "2");
        assertCitationEquals(awards, "first-to-solve-kioskconstruction", "31");
        assertCitationEquals(awards, "first-to-solve-adjustedaverage", "36");
        assertCitationEquals(awards, "first-to-solve-equalisingaudio", "6");
        assertCitationEquals(awards, "first-to-solve-imperfectimperialunits", "7");
        assertCitationEquals(awards, "first-to-solve-bellevue", "10");
        assertCitationEquals(awards, "first-to-solve-failingflagship", "11");
        assertCitationEquals(awards, "first-to-solve-jaggedskyline", "11");
        assertCitationEquals(awards, "first-to-solve-dividingdna", "38");
        assertCitationEquals(awards, "winner", "2");

    }

    /**
     * Compare award for id with team number
     * 
     * @param awards
     * @param id
     *            the name of the award
     * @param team
     * @throws JsonProcessingException
     */
    private void assertCitationEquals(List<CLICSAward> awards, String id, String team) throws JsonProcessingException {

        CLICSAward award = findAward(awards, id);
        assertNotNull("Missing award for citation " + id, award);

        List<String> teams = award.getTeamIds();
        int expectedTeamCount = 1;
        assertEquals("Expecting only " + expectedTeamCount + "team ", 1, teams.size());

        String teamid = teams.get(0);
        assertEquals("Expected team for " + id, team, teamid);
    }

    /**
     * 
     * @param awards
     * @param citation
     * @return null if not found, otherwise award for citation
     * @throws JsonProcessingException
     */
    private CLICSAward findAward(List<CLICSAward> awards, String id) throws JsonProcessingException {
        for (CLICSAward clicsAward : awards) {
            if (clicsAward.getId() != null && clicsAward.getId().equals(id)) {
                return clicsAward;
            }
        }
        return null;
    }

    /**
     * Create and return a new scoreboard client.
     * 
     * @param contest
     * @return a ClientId for newly created scoreboard account.
     */
    private ClientId createBoardAccount(IInternalContest contest) {
        Vector<Account> scoreboards = contest.generateNewAccounts(ClientType.Type.SCOREBOARD.toString(), 1, true);
        return scoreboards.firstElement().getClientId();
    }

    /**
     * Insure that there is one team and one judge in the contest model.
     * 
     * @param contest
     */
    private void checkForJudgeAndTeam(IInternalContest contest) {
        Account account = contest.getAccounts(ClientType.Type.TEAM).firstElement();
        assertFalse("Team account not generated", account == null);
        assertFalse("Team account not generated", account.getClientId().equals(Type.TEAM));

        account = contest.getAccounts(ClientType.Type.JUDGE).firstElement();
        assertFalse("Judge account not generated", account == null);
        assertFalse("Team account not generated", account.getClientId().equals(Type.TEAM));

    }

    /**
     * Initialize contest with teams, problems, languages, judgements.
     * 
     * @param contest
     * @param numTeams
     * @param numProblems
     */
    private void initData(IInternalContest contest, int numTeams, int numProblems) {

        // Add accounts
        contest.generateNewAccounts(ClientType.Type.TEAM.toString(), numTeams, true);
        contest.generateNewAccounts(ClientType.Type.JUDGE.toString(), 6, true);

        // Add scoreboard account and set the scoreboard account for this client (in contest)
        contest.setClientId(createBoardAccount(contest));

        checkForJudgeAndTeam(contest);

        // Add Problem
        for (int i = 0; i < numProblems; i++) {
            char letter = 'A';
            letter += i;
            Problem problem = new Problem("" + letter);
            contest.addProblem(problem);
        }

        // Add Language
        Language language = new Language("Java");
        contest.addLanguage(language);

        String[] judgementNames = { "Yes", "No - incorrect output", "No - compilation error", "Contact staff", "No - Security Violation" };

        String[] acronyms = { "AC", "WA", "CE", "WA", "SV" };

        for (int i = 0; i < judgementNames.length; i++) {
            Judgement judgement = new Judgement(judgementNames[i], acronyms[i]);
            contest.addJudgement(judgement);
        }
    }

    /**
     * Create contest with judged runs.
     * 
     * @param numTeams
     *            number of teams to create
     * @param runsDataList
     *            array of strings, see {@link SampleContest#addRunFromInfo(IInternalContest, String)}}
     * @return
     * @throws Exception
     */
    public InternalContest createContestWithJudgedRuns(int numTeams, String[] runsDataList, int numberProblems) throws Exception {
        return createContestWithJudgedRuns(numTeams, runsDataList, numberProblems, false);
    }

    /**
     * Create contest with judged runs.
     * 
     * @param numTeams
     *            number of teams to create
     * @param runsDataList
     *            array of strings, see {@link SampleContest#addRunFromInfo(IInternalContest, String)}}
     * @param runsDataList
     * @param respectSendTo
     *            add RESPECT_NOTIFY_TEAM_SETTING to each team account
     * @return
     * @throws Exception
     */
    public InternalContest createContestWithJudgedRuns(int numTeams, String[] runsDataList, int numberProblems, boolean respectSendTo) throws Exception {

        InternalContest contest = new InternalContest();

        initData(contest, numTeams, numberProblems);

        if (respectSendTo) {
            /**
             * Set permission that will respect the {@link JudgementRecord#isSendToTeam()}
             */
            Account account = contest.getAccount(contest.getClientId());
            account.addPermission(edu.csus.ecs.pc2.core.security.Permission.Type.RESPECT_NOTIFY_TEAM_SETTING);
        }

        for (String runInfoLine : runsDataList) {
            SampleContest.addRunFromInfo(contest, runInfoLine);
        }

        return contest;
    }

}
