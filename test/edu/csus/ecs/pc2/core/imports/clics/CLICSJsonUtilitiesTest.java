// Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.imports.clics;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import edu.csus.ecs.pc2.core.exception.IllegalContestState;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.standings.ContestStandings;
import edu.csus.ecs.pc2.core.standings.ScoreboardUtilites;
import edu.csus.ecs.pc2.core.standings.TeamStanding;
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

    private SampleContest sampleContest;

    /**
     * Test no awards (no runs) in awards.json
     * 
     * @throws Exception
     */
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
    
    /**
     * Dump standings information.
     * 
     * @param message
     * @param contest
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws JAXBException
     * @throws IllegalContestState
     * @throws IOException
     */
    public void dumpStandings(String message, IInternalContest contest) throws JsonParseException, JsonMappingException, JAXBException, IllegalContestState, IOException {
        
        System.out.println("dumpStandings: "+message);
        ContestStandings contestStandings = ScoreboardUtilites.createContestStandings(contest);

        List<TeamStanding> teamStands = contestStandings.getTeamStandings();
        for (TeamStanding teamStanding : teamStands) {
            ClientId clientId = CLICSJsonUtilities.createClientId(teamStanding);
            Group teamGroup = CLICSJsonUtilities.getGroupForTeam(contest, clientId);
            if (teamGroup == null) {
                System.out.println(teamStanding.getRank()+" "+teamStanding.getSolved()+" "+teamStanding.getPoints() + " " + //
                        teamStanding.getTeamName() + " school:" + teamStanding.getShortSchoolName() + " TEAM HAS NO GROUP");
            } else {
                System.out.println(teamStanding.getRank()+" "+teamStanding.getSolved()+" "+teamStanding.getPoints() + " " + //
                        teamStanding.getTeamName() + " school=" + teamStanding.getShortSchoolName() + " " + //
                        teamGroup.getGroupId()+":"+teamGroup.getDisplayName());
            }
        }
    }

    public void testcreateAwardsListFor5awards() throws Exception {

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

        InternalContest contest = createContestWithJudgedRuns(12, runsData, 8);

        assertNotNull(contest);
        
        assertEquals("Expecting groups", 2, contest.getGroups().length);

        List<CLICSAward> awards = CLICSJsonUtilities.createAwardsList(contest);

//        dumpAwards ("debug DA ",System.out, awards);
//        dumpStandings("debug XX ", contest);
        
        assertAwardCount(1, awards, CLICSJsonUtilities.WINNER_S_OF_GROUP_TITLE);

        assertEquals("Awards expected ", 6, awards.size());

        String outdir = getOutputDataDirectory(getName());
        ensureDirectory(outdir);

        String awardsFile = outdir + File.separator + "awards.json";

        int rowsWritten = CLICSJsonUtilities.writeAwardsJSONFile(awardsFile, awards);
        assertEquals("Expecting awards elements ", 6, rowsWritten);

//      editFile(awardsFile, "debug C "+getName());

    }
    

    /**
     * If expectedCount found in citation matching searchForString. 
     * @param expectedCount
     * @param awards
     * @param searchForString
     */
    private void assertAwardCount(int expectedCount, List<CLICSAward> awards, String searchForString) {

        int count = 0;
        for (CLICSAward clicsAward : awards) {
            if (clicsAward.getCitation() != null && clicsAward.getCitation().contains(searchForString)) {
                count++;
            }
        }

        assertEquals("Expecting " + searchForString + " citation count ", expectedCount, count);
    }

    /**
     * Test with runs with only No judgement runs.   No awards output.
     * 
     * @throws Exception
     */
    public void testWithNoAwards() throws Exception {

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
                "1,1,A,1,No,No,4", //
                "2,1,A,1,No,No,2", //
                "3,1,A,1,No,No,1", //
                "4,1,A,3,No,No,3", //
                "5,1,A,5,No,No,1", //
                "6,1,A,7,No,No,3", //
                "7,1,A,9,No,No,1", //
                "8,1,B,11,No,No,1", //
                "9,2,A,48,No,No,4", //
                "10,2,A,50,No,No,1", //
                "11,2,C,35,No,No,2", //
                "12,2,D,40,No,No,3", //
        };


        InternalContest contest = createContestWithJudgedRuns(12, runsData, 8);

        assertNotNull(contest);
        
        assertEquals("Expecting groups", 2, contest.getGroups().length);

        List<CLICSAward> awards = CLICSJsonUtilities.createAwardsList(contest);

        assertEquals("Awards expected ", 0, awards.size());

        String outdir = getOutputDataDirectory(getName());
        ensureDirectory(outdir);
        String awardsFile = outdir + File.separator + "awards.json";
        
        int rowsWritten = CLICSJsonUtilities.writeAwardsJSONFile(awardsFile, awards);
        assertEquals("Expecting awards elements ", 0, rowsWritten);

    }

    /**
     * Test with two awards.
     * 
     * @throws Exception
     */
    public void testWithTwoAwards() throws Exception {

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
                "1,2,A,1,Yes,No,0", //
                "2,1,A,1,No,No,2", //
        };


        InternalContest contest = createContestWithJudgedRuns(12, runsData, 8);

        assertNotNull(contest);
        
        assertEquals("Expecting groups", 2, contest.getGroups().length);

        List<CLICSAward> awards = CLICSJsonUtilities.createAwardsList(contest);
        
        

        assertEquals("Awards expected ", 4, awards.size());

        String outdir = getOutputDataDirectory(getName());
        ensureDirectory(outdir);
        String awardsFile = outdir + File.separator + "awards.json";

        int rowsWritten = CLICSJsonUtilities.writeAwardsJSONFile(awardsFile, awards);
        assertEquals("Expecting no award rows ", 4, rowsWritten);
        
//        editFile(awardsFile, "debug A "+getName());
    }
    
    public void testAllAwards() throws Exception {
        
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
                "5,1,A,5,No,No,1",  //
                "6,1,A,7,Yes,No,0",  //
                "7,1,A,9,No,No,1",  //
                "8,11,B,11,Yes,No,1",  //
                "9,12,A,48,Yes,No,4",  //
                "10,2,A,50,Yes,No,0",  //
                "11,2,C,35,Yes,No,0",  //
                "12,2,D,40,Yes,No,0",  //
                "13,6,A,42,Yes,No,0",  //
                "14,13,B,46,Yes,No,0",  //
                "15,8,C,48,Yes,No,0",  //
                "16,13,D,50,Yes,No,0",  //
                "17,10,D,60,Yes,No,0",  //
                "18,10,A,6,Yes,No,0",  //
                "19,14,B,62,Yes,No,0",  //
                "20,14,C,66,Yes,No,0",  //
                "21,1,D,66,Yes,No,0",  //
                "22,14,E,66,Yes,No,0",  //
                "23,14,F,66,Yes,No,0",  //

                "24,16,F,166,Yes,No,0",  //
                "25,17,F,266,Yes,No,0",  //
                "26,18,F,366,Yes,No,0",  //
                "27,19,F,866,Yes,No,0",  //
                
                "28,12,F,1466,Yes,No,0",  //
                "20,11,A, 123,Yes,No,0",  //
                
                "21,22,F,1466,Yes,No,0",  //
                "22,22,A, 123,Yes,No,0",  //
                "28,23,F,1466,Yes,No,0",  //
                "24,24,A, 123,Yes,No,0",  //
                

        };

        int numProbs = 8;
        InternalContest contest = createContestWithJudgedRuns(30, runsData, numProbs);

        assertNotNull(contest);
        
        assertEquals("Expecting groups", 2, contest.getGroups().length);

        List<CLICSAward> awards = CLICSJsonUtilities.createAwardsList(contest);
        
//        dumpAwards("test All ", System.out,  awards);
        
        assertEquals("Awards expected ", 12, awards.size());
        
        
    }
    
    public void testAwardsTwo() throws Exception {

        
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
                "1,1,A,1,Yes,No,0", //
                "2,2,A,2,Yes,No,0", //
                "3,3,A,3,Yes,No,0", //
                "4,4,A,4,Yes,No,0", //
                
                "5,5,A,5,Yes,No,0", //
                "6,6,A,6,Yes,No,0", //
                "7,7,A,7,Yes,No,0", //
                "8,8,A,8,Yes,No,0", //
                
                
                "9,9,A,9,Yes,No,0", //
                "10,10,A,13,Yes,No,0", //
                "11,11,A,13,Yes,No,0", //
                "12,12,A,13,Yes,No,0", //
                "13,13,A,13,Yes,No,0", //
                "14,14,A,13,Yes,No,0", //
                "15,15,A,13,Yes,No,0", //
                "16,16,A,13,Yes,No,0", //
                "17,17,A,13,Yes,No,0", //
                "18,18,A,13,Yes,No,0", //
                "19,19,A,13,Yes,No,0", //
                
                "20,20,A,23,Yes,No,0", //
                "21,21,A,21,Yes,No,0", //
                "22,22,A,22,Yes,No,0", //
//                "23,23,A,23,Yes,No,0", //
//                "24,24,A,24,Yes,No,0", //
//                "25,25,A,25,Yes,No,0", //
//                "26,26,A,26,Yes,No,0", //
//                "27,27,A,27,Yes,No,0", //
//                "28,28,A,28,Yes,No,0", //
//                "20,29,A,29,Yes,No,0", //
//                "22,30,A,30,Yes,No,0", //
//                "28,31,A,31,Yes,No,0", //
//                "24,24,A,32,Yes,No,0", //

        };
        

        int numProbs = 8;
        InternalContest contest = createContestWithJudgedRuns(40, runsData, numProbs);

        assertNotNull(contest);

        assertEquals("Expecting groups", 2, contest.getGroups().length);

        List<CLICSAward> awards = CLICSJsonUtilities.createAwardsList(contest);
        
//        dumpStandings("debug Two", contest);

        assertTeamCount(awards, CLICSJsonUtilities.ID_WINNER, 1);

        assertTeamCount(awards, CLICSJsonUtilities.ID_GOLD_MEDAL, 4);
        assertTeamCount(awards, CLICSJsonUtilities.ID_SILVER_MEDAL, 4);
        assertTeamCount(awards, CLICSJsonUtilities.ID_BRONZE_MEDAL, 11);

//        dumpAwards (System.out, awards);
        
        assertEquals("Awards expected ", 7, awards.size());
        
        
        FinalizeData data = createFinalizeData(4, 4, 13);
        contest.setFinalizeData(data);
        awards = CLICSJsonUtilities.createAwardsList(contest);
        
        assertTeamCount(awards, CLICSJsonUtilities.ID_WINNER, 1);

        assertTeamCount(awards, CLICSJsonUtilities.ID_GOLD_MEDAL, 4);
        assertTeamCount(awards, CLICSJsonUtilities.ID_SILVER_MEDAL, 4);
        assertTeamCount(awards, CLICSJsonUtilities.ID_BRONZE_MEDAL, 11);

        assertEquals("Awards expected ", 7, awards.size());
    }
    
    protected void dumpAwards(String message, PrintStream out, List<CLICSAward> awards) throws JsonProcessingException {
        out.println("dumpAwards: "+message);
        for (CLICSAward clicsAward : awards) {
            out.println("debug dump award  = " + clicsAward.toJSON());
        }
    }

    protected FinalizeData createFinalizeData(int numberGolds, int numberSilvers, int numberBronzes) {
        FinalizeData data = new FinalizeData();
        data.setGoldRank(numberGolds);
        data.setSilverRank(numberSilvers);
        data.setBronzeRank(numberBronzes);
        data.setComment("Finalized by Director of Operations, no, really!");
        return data;
    }
    
    /**
     * Expect team count for awards id to be the same as expectedNumber
     * @param awards
     * @param id
     * @param expectedNumber
     */
    private void assertTeamCount(List<CLICSAward> awards, String id, int expectedNumber) {
         String[] teamIds = getTeamList (awards, id);
        assertEquals ("Expecting team count for award "+id, expectedNumber, teamIds.length);
    }

    private String[] getTeamList(List<CLICSAward> awards, String id) {
        for (CLICSAward clicsAward : awards) {
            if (id.equals( clicsAward.getId())) {
                return clicsAward.getTeam_ids();
            }
        }
        return new String [0];
    }

    /**
     * Test with 23 runs and 8 awards.
     * 
     * @throws Exception
     */
    public void testEightAwards() throws Exception {
        
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
                "5,1,A,5,No,No,1",  //
                "6,1,A,7,Yes,No,0",  //
                "7,1,A,9,No,No,1",  //
                "8,1,B,11,No,No,1",  //
                "9,2,A,48,No,No,4",  //
                "10,2,A,50,Yes,No,0",  //
                "11,2,C,35,Yes,No,0",  //
                "12,2,D,40,Yes,No,0",  //
                "13,3,A,42,Yes,No,0",  //
                "14,3,B,46,Yes,No,0",  //
                "15,3,C,48,Yes,No,0",  //
                "16,3,D,50,Yes,No,0",  //
                "17,3,D,60,Yes,No,0",  //
                "18,4,A,6,Yes,No,0",  //
                "19,4,B,62,Yes,No,0",  //
                "20,4,C,66,Yes,No,0",  //
                "21,4,D,66,Yes,No,0",  //
                "22,4,E,66,Yes,No,0",  //
                "23,4,F,66,Yes,No,0",  //
        };

        int numProbs = 8;
        InternalContest contest = createContestWithJudgedRuns(12, runsData, numProbs);

        assertNotNull(contest);
        
        assertEquals("Expecting groups", 2, contest.getGroups().length);

        List<CLICSAward> awards = CLICSJsonUtilities.createAwardsList(contest);
        
        List<CLICSAward> list = new ArrayList<CLICSAward>();
        CLICSJsonUtilities.addMedals(contest, list);
        
        assertEquals("Awards expected ", 9, awards.size());

        String outdir = getOutputDataDirectory(getName());
        ensureDirectory(outdir);
        String awardsFile = outdir + File.separator + "awards.json";

        int rowsWritten = CLICSJsonUtilities.writeAwardsJSONFile(awardsFile, awards);
        assertEquals("Expecting no award rows ", 9, rowsWritten);
        
//        editFile(awardsFile, "debug A "+getName());
        
        
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

//        editFile (awardsFile, "debug tr "+getName());

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

        String[] teams = award.getTeam_ids();
        int expectedTeamCount = 1;
        assertEquals("Expecting only " + expectedTeamCount + "team ", 1, teams.length);

        String teamid = teams[0];
        assertEquals("Expected team for " + id, team, teamid);
    }

    /**
     * Find award in awards list
     * 
     * @param awards
     * @param id the award id
     * @return
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
        
        sampleContest = new SampleContest();
        sampleContest.assignSampleGroups(contest, "Group Thing One", "Group Thing Two");
        
        // Add scoreboard account and set the scoreboard account for this client (in contest)
        contest.setClientId(createBoardAccount(contest));

        checkForJudgeAndTeam(contest);

        // Add Problem
        for (int i = 0; i < numProblems; i++) {
            char letter = 'A';
            letter += i;
            Problem problem = new Problem("Problem " + letter);
            problem.setShortName("short"+letter);
            problem.setLetter(""+letter);
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
    // TODO REFACTOR move to SampleContest
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
