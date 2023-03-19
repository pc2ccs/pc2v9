 // Copyright (C) 1989-2023 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.core.scoring;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.standings.ContestStandings;
import edu.csus.ecs.pc2.core.standings.ScoreboardUtilites;
import edu.csus.ecs.pc2.core.standings.TeamStanding;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;
import edu.csus.ecs.pc2.util.ScoreboardVariableReplacer;

/**
 * Unit tests. 
 * @author pc2@ecs.csus.edu
 */

public class NewScoringAlgorithmTest extends AbstractTestCase {

    private boolean debugMode = false;

    public void testBasic() throws Exception {

        SampleContest sampleContest = new SampleContest();

        IInternalContest contest = sampleContest.createContest(2, 2, 6, 12, true);
        setFirstTeamClient(contest);

        NewScoringAlgorithm scoringAlgorithm = new NewScoringAlgorithm();

        StandingsRecord[] standingsRecords = scoringAlgorithm.getStandingsRecords(contest, new Properties());

        int numberAccounts = contest.getAccounts(Type.TEAM).size();

        assertEquals("getStandingsRecords accounts and records unequal", numberAccounts, standingsRecords.length);

        createJudgedRun(contest, 0, true, 12);
        standingsRecords = scoringAlgorithm.getStandingsRecords(contest, new Properties());

        if (debugMode) {
            for (StandingsRecord standingsRecord : standingsRecords) {
                System.out.println(standingsRecord.getRankNumber() + " " + standingsRecord.getNumberSolved() + " " + standingsRecord.getPenaltyPoints() + " " + standingsRecord.getClientId());
            }
        }

    }
    
    public void testScoringAdjustments() throws Exception {
        SampleContest sampleContest = new SampleContest();

        IInternalContest contest = sampleContest.createContest(2, 2, 6, 12, true);

        NewScoringAlgorithm scoringAlgorithm = new NewScoringAlgorithm();

        createJudgedRun(contest, 0, true, 12);

        Account account = contest.getAccounts(ClientType.Type.TEAM).firstElement();
        account.setScoringAdjustment(-2);
        contest.updateAccount(account);
        
        StandingsRecord[] standingsRecords = scoringAlgorithm.getStandingsRecords(contest, new Properties());
        assertEquals("scoring adjustment -2", 10, standingsRecords[0].getPenaltyPoints());
        account.setScoringAdjustment(-15);
        contest.updateAccount(account);
        standingsRecords = scoringAlgorithm.getStandingsRecords(contest, new Properties());
        assertEquals("scoring adjustment -15", 0, standingsRecords[0].getPenaltyPoints());
    }

    public void testHonorScoreboardFreezeUnfreeze() throws Exception {

        SampleContest sampleContest = new SampleContest();

        IInternalContest contest = sampleContest.createContest(1, 1, 2, 12, true);
        String [] runsData = {
                "1,1,A,1,No",  // 20 (a No before first yes)
                "2,1,A,103,Yes",  // 3 (first yes counts Minute points but never Run Penalty points)
                "3,1,A,105,No",  // zero -- after Yes
                "4,1,A,107,Yes",  // zero -- after Yes
                "5,1,A,109,No",  // zero -- after Yes
                "6,1,B,111,No",  // zero -- not solved
                "7,1,B,113,No",  // zero -- not solved
                "8,2,A,241,Yes",  // 30 (minute points; no Run points on first Yes)
                "9,2,B,255,No",  // zero -- not solved
                "10,2,B,260,No",  // zero -- not solved
                "11,2,B,265,No",  // zero -- not solved
                "12,2,B,270,No",  // zero -- not solved
                "13,2,B,275,No",  // zero -- not solved
        };
        for (int i = 0; i < runsData.length; i++) {
            String runInfoLine = runsData[i];
            SampleContest.addRunFromInfo(contest, runInfoLine);
        }
        NewScoringAlgorithm scoringAlgorithm = new NewScoringAlgorithm();
        ObjectMapper mapper = new ObjectMapper();

        StandingsRecord[] standingsRecords = scoringAlgorithm.getStandingsRecords(contest, null, new Properties(), true, null);
        StandingsRecord standingsRecord = standingsRecords[1];
        JsonNode rootNode = mapper.readTree(standingsRecord.toString());
        for (Iterator<JsonNode> iterator = rootNode.findValue("listOfSummaryInfo").elements(); iterator.hasNext();) {
            JsonNode type = (JsonNode) iterator.next();
            long pendingRunCount = type.get("pendingRunCount").asLong();
            long numberSubmitted = type.get("numberSubmitted").asLong();
            assertEquals("for team 2 number submitted should equal pending run count", numberSubmitted, pendingRunCount);
        }
        ContestInformation ci = contest.getContestInformation();
        ci.setThawed(true);
        contest.updateContestInformation(ci);
        // once the contest is unfrozen the pendingRunCount should go to 0
        standingsRecords = scoringAlgorithm.getStandingsRecords(contest, null, new Properties(), true, null);
        // just look at 2nd team
        standingsRecord = standingsRecords[1];
        rootNode = mapper.readTree(standingsRecord.toString());

        for (Iterator<Entry<String, JsonNode>> iterator = rootNode.findValue("listOfSummaryInfo").fields(); iterator.hasNext();) {
            Entry<String, JsonNode> type = (Entry<String, JsonNode>) iterator.next();
            if (type.getKey().equals("2")) {
                long pendingRunCount = type.getValue().get("pendingRunCount").asLong();
                long numberSubmitted = type.getValue().get("numberSubmitted").asLong();
                assertEquals("once unfrozen for team 2 number submitted", 5, numberSubmitted);
                assertEquals("once unfrozen for team 2 number pendingRunCount", 0, pendingRunCount);
            }
        }
    }

    /**
     * Submit and judge a run.
     * 
     * @param contest
     * @param judgementIndex
     * @param solved
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public Run createJudgedRun(IInternalContest contest, int judgementIndex, boolean solved, int elapsedMinutes) throws IOException, ClassNotFoundException, FileSecurityException {
        Run run = getARun(contest, elapsedMinutes);
        RunFiles runFiles = new RunFiles(run, "samps/Sumit.java");

        int runId = contest.getRuns().length + 1;
        run.setNumber(runId);
        contest.addRun(run, runFiles, null);

        ClientId who = contest.getAccounts(ClientType.Type.JUDGE).firstElement().getClientId();
        assertFalse("Could not retrieve first judge ", who == null);

        checkOutRun(contest, run, who);

        Judgement judgement = contest.getJudgements()[judgementIndex];

        JudgementRecord judgementRecord = new JudgementRecord(judgement.getElementId(), who, solved, false);
        contest.addRunJudgement(run, judgementRecord, null, who);

        return run;

    }

    /**
     * Create a new run in the contest.
     * 
     * @param contest
     * @param elapsedMinutes
     * @return created run.
     */
    private Run getARun(IInternalContest contest, int elapsedMinutes) {
        Problem problem = contest.getProblems()[0];
        Language language = contest.getLanguages()[0];

        Account account = contest.getAccounts(ClientType.Type.TEAM).firstElement();

        ClientId id = account.getClientId();
        Run run = new Run(id, language, problem);
        run.setElapsedMins(elapsedMinutes);
        return run;
    }

    private void checkOutRun(IInternalContest contest, Run run, ClientId judgeId) {
        try {
            contest.checkoutRun(run, judgeId, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testGroupWinners() throws Exception {

        SampleContest sampleContest = new SampleContest();

        String[] groupNames = { "paragon", "north", "west", "south", "anderson" };

        int startr = 0;

        int teamsPerGroup = 4;

        IInternalContest contest = sampleContest.createContest(2, 2, teamsPerGroup * groupNames.length + 5, 12, true);

        for (String name : groupNames) {
            Group group = new Group(name);
            contest.addGroup(group);
            assignGroups(contest, group, startr, teamsPerGroup);
            startr += teamsPerGroup;
        }

        NewScoringAlgorithm scoringAlgorithm = new NewScoringAlgorithm();
        StandingsRecord[] standingsRecords = scoringAlgorithm.getRegionalWinners(contest, new Properties());

        if (debugMode){
            System.out.println("debug records " + standingsRecords.length);
        }
        
        assertEquals("Expecting one team per group", teamsPerGroup * groupNames.length, standingsRecords.length);

        for (Group group : contest.getGroups()) {

            StandingsRecord record = scoringAlgorithm.getRegionalWinner(contest, new Properties(), group);
            /**
             * Since there are ties for first place there is no clear winner so getRegionalWinner return null.
             */
            assertNull(record);
        }

        // TODO CCS figure out assertNull fails below
        
//        Run aRun = createJudgedRun(contest, 0, true, 12);
//        aRun.setSubmitter(getTeamAccounts(contest)[0].getClientId()); // assign to team 1
//
//        for (Group group : contest.getGroups()) {
//
//            StandingsRecord record = scoringAlgorithm.getRegionalWinner(contest, new Properties(), group);
//            /**
//             * Since there are ties for first place there is no clear winner so getRegionalWinner return null.
//             */
//            assertNull(record);
//        }

    }

    // old code
//    Account[] getTeamAccounts(IInternalContest contest) {
//        Vector<Account> vector = contest.getAccounts(Type.TEAM);
//        Account[] accounts = (Account[]) vector.toArray(new Account[vector.size()]);
//        Arrays.sort(accounts, new AccountComparator());
//        return accounts;
//    }

    private void assignGroups(IInternalContest contest, Group group, int startr, int count) {
        Account[] accounts = getTeamAccounts(contest);
        for (int i = startr; i < startr + count; i++) {
            accounts[i].setGroupId(group.getElementId());
            if (debugMode) {
                System.out.println("debug Account  " + accounts[i].getClientId() + " " + accounts[i].getGroupId());
            }
        }
    }
    
    
    protected Run [] addTc1Runs(IInternalContest contest) throws Exception {
        
        String [] runsDataList = { //
                "1,101,B,1,Yes", //
                "2,151,C,1,Yes", //
                "3,201,B,1,Yes", //
                "4,251,C,1,Yes", //
                "5,301,D,1,Yes", //
                "6,351,A,1,Yes", //
                "7,401,A,1,Yes", //
                "8,451,B,1,No", //
                "9,501,A,1,Yes", //
                "10,551,A,1,Yes", //
                "11,551,D,1,Yes", //
                "12,551,D,1,No", //
                "13,602,A,1,Yes", //
                "14,801,A,1,No", //
                "15,801,D,1,Yes", //
                "16,801,B,1,No", //
                "17,801,A,1,No", //
                "18,901,C,1,No", //
                "19,901,D,1,Yes", //
                "20,901,B,1,No", //
                "90,901,C,2,No" //
        };

        for (String runInfoLine : runsDataList) {
            SampleContest.addRunFromInfo(contest, runInfoLine);
        }

        return contest.getRuns();
    }
    
    private void initializeStaticLog(String name) {
        StaticLog.setLog(new Log("logs", name + ".log"));
    }
    
    public void testWithTestContest1() throws Exception {
        
        initializeStaticLog(getName());
        InternalContest contest = new InternalContest();
        String cdpDir = getTestSampleContestDirectory("tc1");
  
        IContestLoader loader = new ContestSnakeYAMLLoader();
        loader.initializeContest(contest, new File( cdpDir));
        setFirstTeamClient(contest);
        
        Group[] groups = contest.getGroups();
        
        for (Group group : groups) {
            String divName = ScoreboardUtilites.getDivision(group.getDisplayName());
            assertNotNull("No division found for "+group.getDisplayName(), divName);
        }
        
        Account[] accounts = getTeamAccounts(contest);
        Arrays.sort(accounts, new AccountComparator());
        for (Account account : accounts) {
            String name = ScoreboardUtilites.getDivision(contest, account.getClientId());
            assertNotNull("No division found for "+account, name);
        }

        Judgement[] judgements = contest.getJudgements();

        assertEquals("Expecting # jugements", 10, judgements.length);

        addTc1Runs(contest);
        
        Run[] runlist = contest.getRuns();
        for (Run run : runlist) {
            assertNotNull("Expecting account for "+run.getSubmitter(), contest.getAccount(run.getSubmitter()));
            String div = ScoreboardUtilites.getDivision(contest, run.getSubmitter());
            assertNotNull("Missing division for "+run.getSubmitter(), div);
        }
        
        assertEquals("Expecting # runs", 21, runlist.length);
        
        ClientId client1 = accounts[5].getClientId();
        Group group = contest.getGroup(contest.getAccount(client1).getGroupId());

        Run[] runs = ScoreboardUtilites.getRunsForUserDivision(client1, contest);
        assertEquals("Expecting runs matching group " + group, 7, runs.length);

        client1 = accounts[12].getClientId();
        runs = ScoreboardUtilites.getRunsForUserDivision(client1, contest);
        assertEquals("Expecting runs matching group " + group, 5, runs.length);

        NewScoringAlgorithm scoringAlgorithm = new NewScoringAlgorithm();
        
        Account acc = contest.getAccount(client1);
        assertNotNull(acc.getGroupId());
        Group group2 = contest.getGroup(acc.getGroupId());
        assertNotNull(group2);
        
        String divString = ScoreboardUtilites.getDivision(contest, client1);
        Integer division = new Integer(divString);
        ClientId id  = contest.getClientId();
        assertNotNull("No client Id for contest",id);
        StandingsRecord[] standingsRecords = scoringAlgorithm.getStandingsRecords(contest, division,DefaultScoringAlgorithm.getDefaultProperties(), false, runs);
        assertEquals("Expecting standing records for client "+client1, 18, standingsRecords.length);

        
        ClientId lastClient = accounts[accounts.length-1].getClientId();
        division = 3;
        standingsRecords = scoringAlgorithm.getStandingsRecords(contest, division, DefaultScoringAlgorithm.getDefaultProperties(), false, runs);
        assertEquals("Expecting standing records for client "+lastClient, 22, standingsRecords.length);

        division = 1;
        Run[] divRuns = ScoreboardUtilites.getRunsForDivision(contest, division.toString());
        assertEquals("Expecting run count for division "+division, 5, divRuns.length);
        
        division = 2;
        divRuns = ScoreboardUtilites.getRunsForDivision(contest, division.toString());
        assertEquals("Expecting run count for division "+division, 7, divRuns.length);
        
        division = 3;
        divRuns = ScoreboardUtilites.getRunsForDivision(contest, division.toString());
        assertEquals("Expecting run count for division "+division, 9, divRuns.length);
        
        
    }

    /**
     * Assign client for contest to first team.
     * 
     * @param contest
     */
    private void setFirstTeamClient(IInternalContest contest) {
        Account[] acc = getTeamAccounts(contest);
        Arrays.sort(acc, new AccountComparator());
        contest.setClientId(acc[0].getClientId());
        
    }
    
    /**
     * Test whether NSA team name matches Team Display Format name
     * @throws Exception
     */
    public void testTeamDisplayFormat() throws Exception {

        initializeStaticLog(getName());
        InternalContest contest = new InternalContest();
        String cdpDir = getTestSampleContestDirectory("tc1");

        IContestLoader loader = new ContestSnakeYAMLLoader();
        loader.initializeContest(contest, new File(cdpDir));
        setFirstTeamClient(contest);

        // String teamVarDisplayString = contestInformation.getTeamScoreboardDisplayFormat();
        // standingsRecordMemento.putString("teamName", ScoreboardVariableReplacer.substituteDisplayNameVariables(teamVarDisplayString, account, group));
        // team-scoreboard-display-format-string : 'Team {:clientnumber} {:teamname} and login: {:teamloginname}
        // {:groupid}:{:groupname} long: {:longschoolname} short: {:shortschoolname} cms id: {:externalid}'

        String teamScoreboardDisplayForamtString = "Team {:clientnumber} name: {:teamname} and login: {:teamloginname}";

        ContestInformation info = contest.getContestInformation();
        info.setTeamScoreboardDisplayFormat(teamScoreboardDisplayForamtString);
        contest.updateContestInformation(info);

        Group[] groups = contest.getGroups();

        for (Group group : groups) {
            String divName = ScoreboardUtilites.getDivision(group.getDisplayName());
            assertNotNull("No division found for " + group.getDisplayName(), divName);
        }

        Account[] accounts = getTeamAccounts(contest);
        Arrays.sort(accounts, new AccountComparator());
        for (Account account : accounts) {
            String name = ScoreboardUtilites.getDivision(contest, account.getClientId());
            assertNotNull("No division found for " + account, name);
        }

        addTc1Runs(contest);

        NewScoringAlgorithm scoringAlgorithm = new NewScoringAlgorithm();
        String xml = scoringAlgorithm.getStandings(contest, DefaultScoringAlgorithm.getDefaultProperties(), StaticLog.getLog());

        // view the xml file
//        FileUtilities.writeFileContents("tempfile.xml", new String[] {xml});
//        editFile("tempfile.xml");

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ContestStandings contestStandings = xmlMapper.readValue(xml, ContestStandings.class);

        int mismatches = 0;

        List<TeamStanding> teamStandings = contestStandings.getTeamStandings();
        for (TeamStanding teamStanding : teamStandings) {

            int clientNumber = Integer.parseInt(teamStanding.getTeamId());
            ClientId clientId = new ClientId(contest.getSiteNumber(), Type.TEAM, clientNumber);
            Account account = contest.getAccount(clientId);

            Group group = null;
            if (account.getGroupId() != null) {
                group = contest.getGroup(account.getGroupId());
            }

            String expectedDisplayName = ScoreboardVariableReplacer.substituteDisplayNameVariables(teamScoreboardDisplayForamtString, account, group);
            if (!expectedDisplayName.contentEquals(teamStanding.getTeamName())) {
                if (isDebugMode()) {
                    System.err.println(" Did not match team " + teamStanding.getTeamId() + " " + expectedDisplayName + " vs " + teamStanding.getTeamName());
                }
                mismatches++;
            }
//                    assertEquals("Expected team based on "+teamScoreboardDisplayForamtString, expectedDisplayName, teamStanding.getTeamName());
        }

        int expectedMismatches = 0;

        assertEquals("Expecting mis matched names ", expectedMismatches, mismatches);
        assertEquals("Expecting matching names ", 77, teamStandings.size() - expectedMismatches);
    }

}
