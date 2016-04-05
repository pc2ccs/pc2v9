package edu.csus.ecs.pc2.core.scoring;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.JudgementNotificationsList;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementNotification;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.NotificationSetting;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.Run.RunStates;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * Unit tests.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id: NewScoringAlgorithmTestLong.java 161 2010-03-14 06:37:02Z laned $
 */

public class NewScoringAlgorithmTestLong extends AbstractTestCase {

    /**
     * output sample data rather than do the tests.
     */
    private static final boolean OUTPUT_SAMPLE_DATA_FLAG = false;

    private Log log = null;

    private boolean debugMode = false;

    // alt1: 0 0 200
    private Properties alt1 = populateProperties(0, 0, 200);

    // alt2: 30 5 0
    private Properties alt2 = populateProperties(30, 5, 0);

    // alt3: 0 10 0
    private Properties alt3 = populateProperties(0, 10, 0);

    // alt4: 5 0 20
    private Properties alt4 = populateProperties(5, 0, 20);

    protected void setUp() throws Exception {
        super.setUp();

        log = createLog(getName());
        StaticLog.setLog(log);

    }

    private Properties populateProperties(int perNo, int perMin, int baseYes) {
        Properties props = DefaultScoringAlgorithm.getDefaultProperties();
        Enumeration<Object> keys = props.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = props.getProperty(key);
            switch (Integer.parseInt(value)) {
                case 0:
                    props.put(key, Integer.toString(baseYes));
                    break;
                case 20:
                    props.put(key, Integer.toString(perNo));
                    break;
                case 1:
                    props.put(key, Integer.toString(perMin));
                    break;
                default:
                    assertTrue("Unknown property: " + key, true);
                    break;
            }
        }
        return props;
    }

    /**
     * Tests whether valid XML is generated with no data in the contest.
     */
    public void testNoData() {

        InternalContest contest = new InternalContest();

        // Add scoreboard account and set the scoreboard account for this client (in contest)
        contest.setClientId(createBoardAccount(contest));

        checkOutputXML(contest);
    }

    /**
     * Initialize the contest.
     * 
     * Initialize with problems, languages, accounts, judgements.
     * 
     * @param contest
     */
    private void initContestData(IInternalContest contest) {

        // Add accounts
        contest.generateNewAccounts(ClientType.Type.TEAM.toString(), 1, true);
        contest.generateNewAccounts(ClientType.Type.TEAM.toString(), 1, true);

        contest.generateNewAccounts(ClientType.Type.JUDGE.toString(), 1, true);

        // Add scoreboard account and set the scoreboard account for this client (in contest)
        contest.setClientId(createBoardAccount(contest));

        // Add Problem
        Problem problem = new Problem("Problem One");
        contest.addProblem(problem);

        // Add Language
        Language language = new Language("Language One");
        contest.addLanguage(language);

        String[] judgementNames = { "Yes", "No - compilation error", "No - incorrect output", "No - It's just really bad", "No - judges enjoyed a good laugh", "You've been bad - contact staff" };

        for (String judgementName : judgementNames) {
            Judgement judgement = new Judgement(judgementName);
            contest.addJudgement(judgement);
        }

        checkForJudgeAndTeam(contest);
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
            problem.setLetter("" + letter);
            contest.addProblem(problem);
        }

        // Add Language
        Language language = new Language("Java");
        contest.addLanguage(language);

        String[] judgementNames = { "Yes", "No - compilation error", "No - incorrect output", "Contact staff" };

        for (String judgementName : judgementNames) {
            Judgement judgement = new Judgement(judgementName);
            contest.addJudgement(judgement);
        }
    }

    /**
     * Create a new run in the contest.
     * 
     * @param contest
     * @return created run.
     */
    private Run getARun(IInternalContest contest) {
        Problem problem = contest.getProblems()[0];
        Language language = contest.getLanguages()[0];

        Account account = contest.getAccounts(ClientType.Type.TEAM).firstElement();

        ClientId id = account.getClientId();
        Run run = new Run(id, language, problem);
        run.setElapsedMins(5);
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

    /**
     * Verify XML created for a single unjudged run.
     * 
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void testOneRunUnjudged() throws IOException, ClassNotFoundException, FileSecurityException {

        InternalContest contest = new InternalContest();

        initContestData(contest);
        Run run = getARun(contest);
        RunFiles runFiles = new RunFiles(run, "samps/Sumit.java");

        contest.addRun(run, runFiles, null);

        checkOutputXML(contest);
    }

    /**
     * Verify XML created for a single unjudged run.
     * 
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void testMixedjudged() throws IOException, ClassNotFoundException, FileSecurityException {

        InternalContest contest = new InternalContest();

        initContestData(contest);
        Run run = getARun(contest, 5);
        RunFiles runFiles = new RunFiles(run, "samps/Sumit.java");

        contest.addRun(run, runFiles, null);

        createJudgedRun(contest, 0, false, 7);

        run = getARun(contest, 10);
        contest.addRun(run, runFiles, null);

        createJudgedRun(contest, 0, true, 15);

        checkOutputXML(contest);
    }

    /**
     * Create a judged run
     * 
     * @param contest
     * @param judgementIndex
     *            - the judgement list index
     * @param solved
     *            - was this run solved/Yes judgement
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void createJudgedRun(IInternalContest contest, int judgementIndex, boolean solved) throws IOException, ClassNotFoundException, FileSecurityException {
        Run run = getARun(contest);
        RunFiles runFiles = new RunFiles(run, "samps/Sumit.java");

        contest.addRun(run, runFiles, null);

        ClientId who = contest.getAccounts(ClientType.Type.JUDGE).firstElement().getClientId();

        checkOutRun(contest, run, who);

        Judgement judgement = contest.getJudgements()[judgementIndex]; // Judge as No

        JudgementRecord judgementRecord = new JudgementRecord(judgement.getElementId(), who, solved, false);
        contest.addRunJudgement(run, judgementRecord, null, who);

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
    public void createJudgedRun(IInternalContest contest, int judgementIndex, boolean solved, int elapsedMinutes) throws IOException, ClassNotFoundException, FileSecurityException {
        Run run = getARun(contest, elapsedMinutes);
        RunFiles runFiles = new RunFiles(run, "samps/Sumit.java");

        contest.addRun(run, runFiles, null);

        ClientId who = contest.getAccounts(ClientType.Type.JUDGE).firstElement().getClientId();
        assertFalse("Could not retrieve first judge ", who == null);

        checkOutRun(contest, run, who);

        Judgement judgement = contest.getJudgements()[judgementIndex]; // Judge as No

        JudgementRecord judgementRecord = new JudgementRecord(judgement.getElementId(), who, solved, false);
        contest.addRunJudgement(run, judgementRecord, null, who);

    }

    /**
     * Get XML from ScoringAlgorithm and test whether it can be parsed.
     * 
     * @param contest
     */
    public void checkOutputXML(IInternalContest contest) {

        try {
            IScoringAlgorithm algorithm = null;
            // algorithm = new DefaultScoringAlgorithm();
            algorithm = new NewScoringAlgorithm();

            String xmlString = algorithm.getStandings(contest, new Properties(), log);

            // getStandings should always return a well-formed xml
            assertFalse("getStandings returned null ", xmlString == null);
            assertFalse("getStandings returned empty string ", xmlString.trim().length() == 0);

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e) {
            assertTrue("Error in XML output " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    /**
     * Verify XML created for a single judged run.
     * 
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void testOneRunJudged() throws IOException, ClassNotFoundException, FileSecurityException {

        InternalContest contest = new InternalContest();

        initContestData(contest);

        createJudgedRun(contest, 2, false);

        checkOutputXML(contest);
    }

    /**
     * Verify XML created for 5 judged runs, one solved, four no's.
     * 
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public void testFiveRunsJudged() throws IOException, ClassNotFoundException, FileSecurityException {

        InternalContest contest = new InternalContest();

        initContestData(contest);

        createJudgedRun(contest, 2, false);
        createJudgedRun(contest, 0, true);
        createJudgedRun(contest, 3, false);
        createJudgedRun(contest, 4, false);

        checkOutputXML(contest);
    }

    private int getIntegerValue(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private void checkOutRun(IInternalContest contest, Run run, ClientId judgeId) {
        try {
            contest.checkoutRun(run, judgeId, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * CASE (1): "When Solved, all runs before Yes".
     * 
     * Created from testing/boardtest.html
     * 
     */
    public void testScoreboardCaseOne() {
        // RunID TeamID Prob Time Result

        String[] runsData = { "1,1,A,1,No", // 20 (a No before first yes)
                "2,1,A,3,Yes", // 3 (first yes counts Minute points but never Run Penalty points)
                "3,1,A,5,No", // zero -- after Yes
                "4,1,A,7,Yes", // zero -- after Yes
                "5,1,A,9,No", // zero -- after Yes
                "6,1,B,11,No", // zero -- not solved
                "7,1,B,13,No", // zero -- not solved
                "8,2,A,30,Yes", // 30 (minute points; no Run points on first Yes)
                "9,2,B,35,No", // zero -- not solved
                "10,2,B,40,No", // zero -- not solved
                "11,2,B,45,No", // zero -- not solved
                "12,2,B,50,No", // zero -- not solved
                "13,2,B,55,No", // zero -- not solved
        };

        // Rank TeamId Solved Penalty

        String[] rankData = { "1,team1,1,23", "2,team2,1,30" };

        scoreboardTest(2, runsData, rankData);
    }

    /**
     * CASE (1): "When Solved, all runs before Yes" - test group ranks.
     * 
     * Created from testing/boardtest.html
     * 
     */
    public void testScoreboardCaseOneGroupRanks() {
        // RunID TeamID Prob Time Result

        String[] runsData = { "1,1,A,1,No", // 20 (a No before first yes)
                "2,1,A,3,Yes", // 3 (first yes counts Minute points but never Run Penalty points)
                "3,1,A,5,No", // zero -- after Yes
                "4,1,A,7,Yes", // zero -- after Yes
                "5,1,A,9,No", // zero -- after Yes
                "6,1,B,11,No", // zero -- not solved
                "7,1,B,13,No", // zero -- not solved
                "8,2,A,30,Yes", // 30 (minute points; no Run points on first Yes)
                "9,2,B,35,No", // zero -- not solved
                "10,2,B,40,No", // zero -- not solved
                "11,2,B,45,No", // zero -- not solved
                "12,2,B,50,No", // zero -- not solved
                "13,2,B,55,No", // zero -- not solved
        };

        // Rank TeamId Solved Penalty

        String[] groupRankData = { "1,team1,1,23", "1,team2,1,30" };

        String[] groupAssignments = { "1,3", "2,2" };

        scoreboardTest(2, runsData, groupRankData, false, null, groupAssignments, true);
    }

    public void testScoreboardCaseOneA() {

        // RunID TeamID Prob Time Result

        String[] runsData = { "2,8,C,1,No", "15,8,D,1,Yes", "23,8,D,1,No", "29,8,D,1,No", "43,8,C,1,No", "44,8,A,1,Yes", "52,8,C,1,Yes", "65,8,B,2,Yes", };

        // Rank TeamId Solved Penalty

        String[] rankData = { "1,team8,4,45", "2,team1,0,0", "2,team2,0,0", "2,team3,0,0", "2,team4,0,0", "2,team5,0,0", "2,team6,0,0", "2,team7,0,0", };

        scoreboardTest(8, runsData, rankData);
    }

    /**
     * Tests for cases where Yes is before No, and multiple yes at same elapsed time.
     * 
     * Both runs have same elapsed time, the tie breaker is runId. Also tests when one or more Yes are after first yes
     */
    public void testNoBeforeYesSameElapsed() {

        // RunID TeamID Prob Time Result

        String[] runsData = { "15,8,D,12,Yes", //
                "16,8,D,12,No", //
                "24,4,B,15,Yes", //
                "25,4,B,15,No", //
                "26,4,B,15,No", //
                "28,2,C,22,Yes", //
                "29,2,C,22,No", //
                "30,2,C,22,Yes", //
                "30,2,C,22,Yes", };

        /**
         * 
         * 
         * 
         */

        // Rank TeamId Solved Penalty
        String[] rankData = { "1,team8,1,12", "2,team4,1,15", "3,team2,1,22", "4,team1,0,0", "4,team3,0,0", "4,team5,0,0", "4,team6,0,0", "4,team7,0,0", };

        scoreboardTest(8, runsData, rankData);
    }

    /**
     * CASE (2): "When Solved, all No Runs".
     * 
     * Created from testing/boardtest.html
     */
    public void testScoreboardCaseTwo() {

        // RunID TeamID Prob Time Result

        String[] runsData = { "1,1,A,1,No", // 20
                "2,1,A,3,Yes", // 3 (Minute points for 1st Yes count)
                "3,1,A,5,No", // 20 (a No on a solved problem)
                "4,1,A,7,Yes", // zero (only "No's" count)
                "5,1,A,9,No", // 20 (another No on the solved problem)

                "6,1,B,11,No", // zero (problem has not been solved)
                "7,1,B,13,No", // zero (problem has not been solved)

                "8,2,A,30,Yes", // 30 (Minute points for 1st Yes)

                "9,2,B,35,No", // zero -- not solved
                "10,2,B,40,No", // zero -- not solved
                "11,2,B,45,No", // zero -- not solved
                "12,2,B,50,No", // zero -- not solved
                "13,2,B,55,No", // zero -- not solved
        };

        // Rank TeamId Solved Penalty

        String[] rankData = { "1,team1,1,23", "2,team2,1,30", };

        // SOMEDAY when SA supports count all runs, replace rankData

        /**
         * Case 2 tests when all no runs are counted, the current SA does not support this scoring method. The commented rankData is the proper results
         */

        // Team 2 -- 30 <-- Team 2 is now winning with a lower score
        // Team 1 -- 63 (same database; different scoring method)
        // String [] rankData = {
        // "1,team2,1,30",
        // "2,team1,1,63",
        // };
        scoreboardTest(2, runsData, rankData);

    }

    /**
     * CASE (3): "When Solved, All Runs"
     * 
     * Created from testing/boardtest.html
     */
    public void testScoreboardCaseThree() {

        // RunID TeamID Prob Time Result

        String[] runsData = {

        "1,1,A,1,No", // 20
                "2,1,A,3,Yes", // 3 (first Yes counts only Min.Pts)
                "3,1,A,5,No", // 20
                "4,1,A,7,Yes", // 20 (all runs count!)
                "5,1,A,9,No", // 20

                "6,1,B,11,No", // zero (not solved)
                "7,1,B,13,No", // zero (not solved)

                "8,2,A,30,Yes", // 30

                "9,2,B,35,No", // zero -- not solved
                "10,2,B,40,No", // zero -- not solved
                "11,2,B,45,No", // zero -- not solved
                "12,2,B,50,No", // zero -- not solved
                "13,2,B,55,No", // zero -- not solved
        };

        // Rank TeamId Solved Penalty

        String[] rankData = { "1,team1,1,23", "2,team2,1,30" };

        // SOMEDAY when SA supports count all runs, replace rankData

        /**
         * Case 3 tests when all runs are counted, the current SA does not support this scoring method. The commented rankData is the proper results
         */

        // String [] rankData = {
        // "1,team2,1,30"
        // "2,team1,1,83",
        // };
        scoreboardTest(2, runsData, rankData);
    }

    /**
     * CASE (4): "All Runs"
     * 
     * Created from testing/boardtest.html
     */
    public void testScoreboardCaseFour() {

        // RunID TeamID Prob Time Result

        String[] runsData = {

        "1,1,A,1,No", // 20
                "2,1,A,3,Yes", // 3 (first yes counts Minutes only)
                "3,1,A,5,No", // 20
                "4,1,A,7,Yes", // 20
                "5,1,A,9,No", // 20

                "6,1,B,11,No", // 20 (all runs count)
                "7,1,B,13,No", // 20 (all runs count)

                "8,2,A,30,Yes", // 30

                "9,2,B,35,No", // 20 (all runs count)
                "10,2,B,40,No", // 20 (all runs count)
                "11,2,B,45,No", // 20 (all runs count)
                "12,2,B,50,No", // 20 (all runs count)
                "13,2,B,55,No", // 20 (all runs count)

        };

        // Rank TeamId Solved Penalty

        String[] rankData = { "1,team1,1,23", "2,team2,1,30" };

        // SOMEDAY when SA supports count all runs, replace rankData

        /**
         * Case 4 tests when all runs are counted, the current SA does not support this scoring method. The commented rankData is the proper results
         */

        // Team 1 -- 123 <-- Team 1 is winning again
        // Team 2 -- 130
        // String [] rankData = {
        // "1,team1,1,123",
        // "2,team2,1,130"
        // };
        scoreboardTest(2, runsData, rankData);
    }

    /**
     * 
     */
    public void testScoreboard55() {

        String[] runsData = { "1,16,B,1,No", "2,8,C,1,No", "3,5,B,1,No", "4,4,C,1,No", "5,4,D,1,No", "6,3,A,1,No", "7,1,A,1,No", "8,6,B,1,New", "9,18,A,1,No", "10,6,A,1,No", "11,21,D,1,Yes",
                "12,6,D,1,No", "13,12,A,1,Yes", "14,13,A,1,No", "15,8,D,1,Yes", "16,3,B,1,No", "17,3,A,1,No", "18,16,C,1,No", "19,20,D,1,Yes", "20,12,B,1,No", "21,14,A,1,No", "22,15,C,1,No",
                "23,8,D,1,No", "24,13,D,1,No", "25,21,A,1,No", "26,18,D,1,Yes", "27,6,C,1,No", "28,20,B,1,Yes", "29,8,D,1,No", "30,19,B,1,No", "31,22,C,1,No", "32,7,A,1,No", "33,7,A,1,No",
                "34,4,D,1,New", "35,18,B,1,No", "36,4,D,1,Yes", "37,19,C,1,No", "38,2,B,1,No", "39,15,C,1,No", "40,12,B,1,No", "41,10,D,1,Yes", "42,22,A,1,No", "43,8,C,1,No", "44,8,A,1,Yes",
                "45,18,D,1,No", "46,13,C,1,No", "47,7,D,1,No", "48,7,C,1,New", "49,5,C,1,No", "50,7,B,1,New", "51,21,B,1,No", "52,8,D,1,Yes", "53,16,A,1,No", "54,10,A,1,No", "55,22,B,1,No",
                "56,18,C,1,No", "57,5,D,2,Yes", "58,10,C,2,No", "59,9,C,2,Yes", "60,5,D,2,Yes", "61,12,D,2,No", "62,10,C,2,No", "63,3,B,2,Yes", "64,21,C,2,No", "65,8,B,2,Yes", "66,19,B,2,Yes",
                "67,18,A,2,No", "68,12,D,2,Yes", "69,5,B,2,Yes", "70,2,A,2,No", "71,21,D,2,No", "72,12,D,2,No", "73,18,C,2,No", "74,14,D,2,Yes", "75,2,A,2,No", "76,20,D,2,No", "77,7,C,2,No",
                "78,14,D,2,No", "79,15,A,2,No", "80,16,B,2,No", "81,2,C,2,No", "82,2,C,2,No", "83,22,A,2,No", "84,21,D,2,Yes", "85,2,C,2,No", "86,10,C,2,No", "87,17,C,2,No", "88,7,A,2,New",
                "89,20,B,2,No", "90,12,C,2,No" };

        // Rank TeamId Solved Penalty

        String[] rankData = { 
                
                "1,team8,3,4", // 
                "2,team20,2,2", // 
                "3,team12,2,23", // 
                "4,team5,2,24", // 
                "5,team10,1,1", // 
                "5,team18,1,1", // 
                "5,team21,1,1", // 
                "8,team9,1,2", // 
                "8,team14,1,2", // 
                "10,team4,1,21", // 
                "11,team3,1,22", // 
                "11,team19,1,22", // 
                "13,team1,0,0", // 
                "13,team2,0,0", // 
                "13,team6,0,0", // 
                "13,team7,0,0", // 
                "13,team11,0,0", // 
                "13,team13,0,0", // 
                "13,team15,0,0", // 
                "13,team16,0,0", // 
                "13,team17,0,0", // 
                "13,team22,0,0", // 
        };

        scoreboardTest(22, runsData, rankData);
    }

    /**
     * 
     */
    public void testScoreboard55Groups() {

        String[] runsData = { "1,16,B,1,No", "2,8,C,1,No", "3,5,B,1,No", "4,4,C,1,No", "5,4,D,1,No", "6,3,A,1,No", "7,1,A,1,No", "8,6,B,1,New", "9,18,A,1,No", "10,6,A,1,No", "11,21,D,1,Yes",
                "12,6,D,1,No", "13,12,A,1,Yes", "14,13,A,1,No", "15,8,D,1,Yes", "16,3,B,1,No", "17,3,A,1,No", "18,16,C,1,No", "19,20,D,1,Yes", "20,12,B,1,No", "21,14,A,1,No", "22,15,C,1,No",
                "23,8,D,1,No", "24,13,D,1,No", "25,21,A,1,No", "26,18,D,1,Yes", "27,6,C,1,No", "28,20,B,1,Yes", "29,8,D,1,No", "30,19,B,1,No", "31,22,C,1,No", "32,7,A,1,No", "33,7,A,1,No",
                "34,4,D,1,New", "35,18,B,1,No", "36,4,D,1,Yes", "37,19,C,1,No", "38,2,B,1,No", "39,15,C,1,No", "40,12,B,1,No", "41,10,D,1,Yes", "42,22,A,1,No", "43,8,C,1,No", "44,8,A,1,Yes",
                "45,18,D,1,No", "46,13,C,1,No", "47,7,D,1,No", "48,7,C,1,New", "49,5,C,1,No", "50,7,B,1,New", "51,21,B,1,No", "52,8,D,1,Yes", "53,16,A,1,No", "54,10,A,1,No", "55,22,B,1,No",
                "56,18,C,1,No", "57,5,D,2,Yes", "58,10,C,2,No", "59,9,C,2,Yes", "60,5,D,2,Yes", "61,12,D,2,No", "62,10,C,2,No", "63,3,B,2,Yes", "64,21,C,2,No", "65,8,B,2,Yes", "66,19,B,2,Yes",
                "67,18,A,2,No", "68,12,D,2,Yes", "69,5,B,2,Yes", "70,2,A,2,No", "71,21,D,2,No", "72,12,D,2,No", "73,18,C,2,No", "74,14,D,2,Yes", "75,2,A,2,No", "76,20,D,2,No", "77,7,C,2,No",
                "78,14,D,2,No", "79,15,A,2,No", "80,16,B,2,No", "81,2,C,2,No", "82,2,C,2,No", "83,22,A,2,No", "84,21,D,2,Yes", "85,2,C,2,No", "86,10,C,2,No", "87,17,C,2,No", "88,7,A,2,New",
                "89,20,B,2,No", "90,12,C,2,No" };

        // Rank TeamId Solved Penalty

        String[] groupRankData = { 
                "1,team8,3,4", // 
                "1,team20,2,2", // 
                "1,team12,2,23", // 
                "1,team5,2,24", // 
                "2,team10,1,1", // 
                "1,team18,1,1", // 
                "1,team21,1,1", // 
                "2,team9,1,2", // 
                "1,team14,1,2", // 
                "2,team4,1,21", // 
                "1,team3,1,22", // 
                "3,team19,1,22", // 
                "4,team1,0,0", // 
                "2,team2,0,0", // 
                "2,team6,0,0", // 
                "2,team7,0,0", // 
                "2,team11,0,0", // 
                "2,team13,0,0", // 
                "2,team15,0,0", // 
                "2,team16,0,0", // 
                "2,team17,0,0", // 
                "4,team22,0,0", // 
        };


        String[] groupAssignments = {
                "8,1", //
                "20,5", //
                "12,4", //
                "5,10", //
                "10,10", //
                "18,2", //
                "21,9", //
                "9,5", //
                "14,3", //
                "3,6", //
                "19,9", //
                "4,9", //
                "1,9", //
                "2,2", //
                "6,2", //
                "7,2", //
                "11,2", //
                "13,2", //
                "15,2", //
                "16,2", //
                "17,2", //
                "22,9", //
        };

        scoreboardTest(22, runsData, groupRankData, false, null, groupAssignments, true);

    }

    /**
     * Test a No before a Yes, both runs same elapsed time.
     */
    public void testNoYes() {

        // RunID TeamID Prob Time Result

        String[] runsData = { "5,2,A,12,No", "6,2,A,12,Yes", };

        // Rank TeamId Solved Penalty

        String[] rankData = { "1,team2,1,32", "2,team1,0,0", };

        scoreboardTest(2, runsData, rankData);
    }

    /**
     * Have run that has a BEING_JUDGED state and should show same standing as the state were JUDGED.
     * 
     * Test for Bug 407 - The SA fails to reflect prelim judgements.
     * 
     * based on CASE (1): "When Solved, all runs before Yes".
     * 
     * Created from testing/boardtest.html
     * 
     */
    public void testScoreboardForBeingJudgedState() {
        // RunID TeamID Prob Time Result

        String[] runsData = { //
                "1,1,A,1,No", // 20 (a No before first yes)
                "2,1,A,3,Yes", // 3 (first yes counts Minute points but never Run Penalty points)
                "3,1,A,5,No", // zero -- after Yes
                "4,1,A,7,Yes", // zero -- after Yes
                "5,1,A,9,No", // zero -- after Yes
                "6,1,B,11,No", // zero -- not solved
                "7,1,B,13,No", // zero -- not solved
                "8,2,A,30,Yes", // 30 (minute points; no Run points on first Yes)
                "9,2,B,35,No", // zero -- not solved
                "10,2,B,40,No", // zero -- not solved
                "11,2,B,45,No", // zero -- not solved
                "12,2,B,50,No", // zero -- not solved
                "13,2,B,55,No", // zero -- not solved
        };

        // Rank TeamId Solved Penalty

        String[] rankData = { "1,team1,1,23", "2,team2,1,30" };

        InternalContest contest = new InternalContest();

        int numTeams = 2;

        initData(contest, numTeams, 5);

        for (String runInfoLine : runsData) {
            addTheRun(contest, runInfoLine);
        }

        if (debugMode) {
            Run[] runs = contest.getRuns();

            for (Run run : runs) {
                run.setStatus(RunStates.BEING_JUDGED);
                System.out.println("Run " + run.getNumber() + " " + run + " " + run.getStatus());
            }
        }

        checkOutputXML(contest);

        confirmRanks(contest, rankData);

    }

    /**
     * Test tie breaker down to last yes submission time.
     * 
     */
    public void testTieBreakerSubmissionTime() {

        // Sort order:
        // Primary Sort = number of solved problems (high to low)
        // Secondary Sort = score (low to high)
        // Tertiary Sort = earliest submittal of last submission (low to high)
        // Forth Sort = teamName (low to high)
        // Fifth Sort = clientId (low to high)

        // RunID TeamID Prob Time Result

        String[] runsData = { "5,5,A,12,No", //
                "6,5,A,12,Yes", //

                "7,6,A,12,No", //
                "8,6,A,12,Yes", //

                // Both solve 1 score 32 (no for 20, 12 min)

                "15,5,B,21,No", //
                "16,5,B,22,Yes", //

                "25,6,B,21,No", //
                "26,6,B,22,Yes", //

        // Both solve 2 score 42 (no for 20 and 22 min)
        // total 74 each
        };

        // Rank TeamId Solved Penalty

        String[] rankData = {
                // must identical score, sort by team display name, identical ranks.
                "1,team5,2,74", "1,team6,2,74", "3,team1,0,0", "3,team2,0,0", "3,team3,0,0", "3,team4,0,0", };

        scoreboardTest(6, runsData, rankData);
    }

    /**
     * Test whether SA respects send to team, it should _not_!
     */
    public void testSendToTeams() {

        // Sort order:
        // Primary Sort = number of solved problems (high to low)
        // Secondary Sort = score (low to high)
        // Tertiary Sort = earliest submittal of last submission (low to high)
        // Forth Sort = teamName (low to high)
        // Fifth Sort = clientId (low to high)

        // RunID TeamID Prob Time Result
        String[] runsData = { "5,5,A,12,No", "6,5,A,12,Yes", // t5 solves A, 32 pts = 20 + 12

                "7,6,A,12,No,Yes", "8,6,A,13,Yes,No", // t6 33 pts

                "15,5,B,21,No", "16,5,B,22,Yes,No", // t5 42 pts
                "25,6,B,21,No,No", "26,6,B,23,Yes,Yes", // t6 solves B, 43 pts
        };

        // Rank TeamId Solved Penalty

        String[] rankData = {
                // rank, team, solved, pts
                "1,team5,2,74", "2,team6,2,76", "3,team1,0,0", "3,team2,0,0", "3,team3,0,0", "3,team4,0,0", };

        scoreboardTest(6, runsData, rankData, true);
    }

    public void testAltScoring() {
        // RunID TeamID Prob Time Result

        String[] runsData = { "1,1,A,1,No", // 20
                "2,1,A,3,Yes", // 3 (Minute points for 1st Yes count)
                "3,1,A,5,No", // 20 (a No on a solved problem)
                "4,1,A,7,Yes", // zero (only "No's" count)
                "5,1,A,9,No", // 20 (another No on the solved problem)

                "6,1,B,11,No", // zero (problem has not been solved)
                "7,1,B,13,No", // zero (problem has not been solved)

                "8,2,A,30,Yes", // 30 (Minute points for 1st Yes)

                "9,2,B,35,No", // zero -- not solved
                "10,2,B,40,No", // zero -- not solved
                "11,2,B,45,No", // zero -- not solved
                "12,2,B,50,No", // zero -- not solved
                "13,2,B,55,No", // zero -- not solved
        };

        // Rank TeamId Solved Penalty

        // alt1: 0 0 200

        String[] alt1rankData = { "1,team1,1,200", "2,team2,1,200", // tie-breaker causes rank 2
        };

        scoreboardTest(2, runsData, alt1rankData, alt1);
        // alt2: 30 5 0
        String[] alt2rankData = { "1,team1,1,45", // 1 no@30 each + 3 min * 5
                "2,team2,1,150", // 5*30
        };

        scoreboardTest(2, runsData, alt2rankData, alt2);

        // alt3: 0 10 0
        String[] alt3rankData = { "1,team1,1,30", // 3 min * 10
                "2,team2,1,300", // 30 min * 10
        };

        scoreboardTest(2, runsData, alt3rankData, alt3);

        // alt4: 5 0 20
        String[] alt4rankData = { "1,team2,1,20", // base yes
                "2,team1,1,25", // base yes + 1 no
        };

        scoreboardTest(2, runsData, alt4rankData, alt4);

    }

    private void scoreboardTest(int numTeams, String[] runsData, String[] rankData, Properties scoreProps) {
        scoreboardTest(numTeams, runsData, rankData, false, scoreProps, null, false);

    }

    private void scoreboardTest(int numTeams, String[] runsDataList, String[] rankDataList, boolean respectSendTo, Properties scoreProps, String[] groupAssignments, boolean testGroupRanks) {

        InternalContest contest = new InternalContest();

        initData(contest, numTeams, 5);

        if (testGroupRanks) {
            for (String groupLine : groupAssignments) {
                // Line "TeamNumber,GroupNumber"
                // "4,3" team 4 group 3, group numbers start with 1
                String[] fields = groupLine.split(",");
                int teamNumber = Integer.parseInt(fields[0]);
                int groupNumber = Integer.parseInt(fields[1]);
                Group group = insureGroup(contest, groupNumber);

                ClientId clientId = new ClientId(contest.getSiteNumber(), Type.TEAM, teamNumber);
                Account account = contest.getAccount(clientId);
                account.setGroupId(group.getElementId());
            }
        }

        if (respectSendTo) {
            /**
             * Set permission that will respect the {@link JudgementRecord#isSendToTeam()}
             */
            Account account = contest.getAccount(contest.getClientId());
            account.addPermission(edu.csus.ecs.pc2.core.security.Permission.Type.RESPECT_NOTIFY_TEAM_SETTING);
        }

        for (String runInfoLine : runsDataList) {
            addTheRun(contest, runInfoLine);
        }

        if (testGroupRanks) {
            confirmGroupRanks(contest, rankDataList);
        } else {
            confirmRanks(contest, rankDataList, scoreProps, respectSendTo);
        }

    }

    private Group insureGroup(InternalContest contest, int groupNumber) {

        Group newGroup = null;
        Group[] groups = contest.getGroups();

        while (groupNumber > groups.length) {
            newGroup = new Group("Group " + (groups.length + 1));
            newGroup.setGroupId(groups.length);
            contest.addGroup(newGroup);
            groups = contest.getGroups();
        }

        newGroup = groups[groupNumber - 1];
        return newGroup;
    }

    private void confirmRanks(InternalContest contest, String[] rankData, Properties scoreProps, boolean compareGroupRanks) {
        Document document = null;

        if (debugMode) {

            Run[] runs = contest.getRuns();
            Arrays.sort(runs, new RunComparator());
            for (Run run : runs) {
                System.out.println("Run " + run.getNumber() + " time=" + run.getElapsedMins() + " " + run.getSubmitter().getName() + " solved=" + run.isSolved());
            }
            System.out.flush();

        }

        // Rank Solved Penalty TeamId

        try {
            NewScoringAlgorithm newScoringAlgorithm = new NewScoringAlgorithm();
            String xmlString = newScoringAlgorithm.getStandings(contest, scoreProps, log);

            if (compareGroupRanks && debugMode) {
                System.out.println(xmlString);
            }

            // getStandings should always return a well-formed xml
            assertFalse("getStandings returned null ", xmlString == null);
            assertFalse("getStandings returned empty string ", xmlString.trim().length() == 0);

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(new InputSource(new StringReader(xmlString)));

        } catch (Exception e) {
            assertTrue("Error in XML output " + e.getMessage(), true);
            e.printStackTrace();
        }
        
        
        if (debugMode){
            dumpSAInfo (contest, scoreProps, true);
        }

        // skip past nodes to find teamStanding node
        NodeList list = document.getDocumentElement().getChildNodes();

        int rankIndex = 0;

        for (int i = 0; i < list.getLength(); i++) {
            Node node = (Node) list.item(i);
            String name = node.getNodeName();
            if (name.equals("teamStanding")) {
                String[] standingsRow = fetchStanding(node, compareGroupRanks);
                // Object[] cols = { "Rank", "Name", "Solved", "Points" };
                String[] cols = rankData[rankIndex].split(",");

                if (debugMode) {
                    System.out.println("SA rank=" + standingsRow[0] + " solved=" + standingsRow[2] + " points=" + standingsRow[3] + " name=" + standingsRow[1]);
                    System.out.println("   rank=" + cols[0] + " solved=" + cols[2] + " points=" + cols[3] + " name=" + cols[1]);
                    System.out.println();
                    System.out.flush();
                }

                compareRanking(rankIndex + 1, standingsRow, cols);
                rankIndex++;
            }
        }
    }

    private void dumpSAInfo(InternalContest contest, Properties properties, boolean showProblemDetails) {
        
        try {
            NewScoringAlgorithm algo = new NewScoringAlgorithm();
            StandingsRecord[] standingsRecords = algo.getStandingsRecords(contest, properties);
            
            
            for (StandingsRecord standingsRecord : standingsRecords) {

                System.out.println( //
                        standingsRecord.getClientId().getName() + " rank=" + //
                                standingsRecord.getRankNumber() + " solved= " + //
                                standingsRecord.getNumberSolved() + " points=" + //
                                standingsRecord.getPenaltyPoints() + " last=" + //
                                standingsRecord.getLastSolved() + "." //
                        );

                SummaryRow row = standingsRecord.getSummaryRow();

                if (showProblemDetails) {
                    System.out.print("   " + standingsRecord.getClientId().getName() + " problems: ");

                    Integer[] problemNumbers = row.getSortedKeys();
                    for (Integer integer : problemNumbers) {
                        ProblemSummaryInfo problemSummaryInfo = row.get(integer);
                        String rowInfo = Utilities.getProblemLetter(integer) + ":" + //
                                integer + " " + //
                                problemSummaryInfo.getNumberSubmitted() + "," + //
                                problemSummaryInfo.getPenaltyPoints() + "," + //
                                problemSummaryInfo.getJudgedRunCount() + "," + //
                                problemSummaryInfo.getPendingRunCount() + "; " //
                        ;
                        System.out.print(rowInfo.replaceAll("0[,;]", "-,"));
                    }
                }

                System.out.println();
            }
            
        } catch (Exception e) {
            System.out.println("Error dumping StandingsRecords");
            e.printStackTrace(System.err);
        }
    }

    /**
     * Test whether SA respects send to team
     */
    public void testZZZZEOCSettings() {

        // Sort order:
        // Primary Sort = number of solved problems (high to low)
        // Secondary Sort = score (low to high)
        // Tertiary Sort = earliest submittal of last submission (low to high)
        // Forth Sort = teamName (low to high)
        // Fifth Sort = clientId (low to high)

        // RunID TeamID Prob Time Result

        String[] runsData = { "1,1,A,250,No", "2,1,A,290,Yes",

        // t5 solves A, 310 pts = 20 + 290
        // but with ECO settings, yes is not seen, so 0

        };

        // Rank TeamId Solved Penalty

        String[] rankData = {
        // rank, team, solved, pts
        "1,team1,0,0", };
        // without EOC permission
        String[] rankData2 = {
        // rank, team, solved, pts
        "1,team1,1,310", };

        InternalContest contest = new InternalContest();

        initData(contest, 1, 5);
        ContestTime contestTime = new ContestTime(1);
        contestTime.setElapsedMins(300);
        contest.updateContestTime(contestTime);
        JudgementNotificationsList judgementNotificationsList = new JudgementNotificationsList();
        for (String runInfoLine : runsData) {
            addTheRun(contest, runInfoLine);
        }

        Run[] runs = contest.getRuns();
        NotificationSetting notificationSetting = new NotificationSetting(runs[0].getProblemId());
        JudgementNotification judgementNotification = new JudgementNotification(true, 30);
        notificationSetting.setFinalNotificationYes(judgementNotification);
        JudgementNotification judgementNotificationNo = new JudgementNotification(false, 30);
        notificationSetting.setFinalNotificationNo(judgementNotificationNo);
        judgementNotificationsList.add(notificationSetting);
        contest.getContestInformation().updateJudgementNotification(notificationSetting);

        checkOutputXML(contest);

        confirmRanks(contest, rankData2);

        Account account = contest.getAccount(contest.getClientId());
        account.addPermission(edu.csus.ecs.pc2.core.security.Permission.Type.RESPECT_EOC_SUPPRESSION);
        contest.updateAccount(account);

        checkOutputXML(contest);

        confirmRanks(contest, rankData);
    }

    /**
     * Test the SA given a list of runs and outcomes.
     * 
     * rankDataList array is array of string, thus: Rank TeamDisplayName Solved Penalty, for example: "1,team5,2,74",
     * 
     * @param numTeams
     * @param runsDataList
     * @param rankDataList
     */
    public void scoreboardTest(int numTeams, String[] runsDataList, String[] rankDataList) {
        scoreboardTest(numTeams, runsDataList, rankDataList, false);
    }

    public void scoreboardTest(int numTeams, String[] runsDataList, String[] rankDataList, boolean respectSendTo) {

        InternalContest contest = new InternalContest();

        initData(contest, numTeams, 5);

        if (respectSendTo) {
            /**
             * Set permission that will respect the {@link JudgementRecord#isSendToTeam()}
             */
            Account account = contest.getAccount(contest.getClientId());
            account.addPermission(edu.csus.ecs.pc2.core.security.Permission.Type.RESPECT_NOTIFY_TEAM_SETTING);
        }

        for (String runInfoLine : runsDataList) {
            addTheRun(contest, runInfoLine);
        }

        checkOutputXML(contest);

        confirmRanks(contest, rankDataList);
    }

    /**
     * add run to list of runs in a contest.
     * 
     * Files found in runInfoLine, comma delmited
     * 
     * <pre>
     * 0 - run id, int
     * 1 - team id, int
     * 2 - problem letter, char
     * 3 - elapsed, int
     * 4 - solved, String &quot;Yes&quot; or No
     * 5 - send to teams, Yes or No
     * 
     * Example:
     * &quot;6,5,A,12,Yes&quot;
     * &quot;6,5,A,12,Yes,Yes&quot;
     * 
     * </pre>
     * 
     * @param contest
     * @param runInfoLine
     * @throws FileSecurityException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void addTheRun(InternalContest contest, String runInfoLine) {

        // get 5th judge
        ClientId judgeId = contest.getAccounts(Type.JUDGE).elementAt(4).getClientId();

        Problem[] problemList = contest.getProblems();
        Language languageId = contest.getLanguages()[0];

        Judgement yesJudgement = contest.getJudgements()[0];
        Judgement noJudgement = contest.getJudgements()[1];

        String[] data = runInfoLine.split(",");

        // Line is: runId,teamId,problemLetter,elapsed,solved[,sendToTeamsYN]

        int runId = getIntegerValue(data[0]);
        int teamId = getIntegerValue(data[1]);
        String probLet = data[2];
        int elapsed = getIntegerValue(data[3]);
        boolean solved = "Yes".equals(data[4]);


        boolean sendToTeams = true;
        if (data.length > 5) {
            sendToTeams = "Yes".equals(data[5]);
        }

        String judgement = data[4];
        assertTrue("Expecting valid judgement, found " + judgement, "Yes".equals(judgement) || //
                "New".equals(judgement) || "No".equals(judgement));

        /**
         * Pending, aka not judged.
         */
        boolean pending = "New".equals(data[4]);

        int problemIndex = probLet.charAt(0) - 'A';
        Problem problem = problemList[problemIndex];
        ClientId clientId = new ClientId(contest.getSiteNumber(), Type.TEAM, teamId);

        Run run = new Run(clientId, languageId, problem);
        run.setNumber(runId);
        run.setElapsedMins(elapsed);

        ElementId judgementId = noJudgement.getElementId();
        if (solved) {
            judgementId = yesJudgement.getElementId();
        }
        JudgementRecord judgementRecord = new JudgementRecord(judgementId, judgeId, solved, false);
        judgementRecord.setSendToTeam(sendToTeams);

        try {
            contest.addRun(run);

            if (!pending) {
                checkOutRun(contest, run, judgeId);
                contest.addRunJudgement(run, judgementRecord, null, judgeId);
            }

        } catch (IOException e) {
            e.printStackTrace();
            assertFalse("Unable to add run from run: " + run, false);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            assertFalse("Unable to add run from run: " + run, false);
        } catch (FileSecurityException e) {
            e.printStackTrace();
            assertFalse("Unable to add run from run: " + run, false);
        }

        if (debugMode) {
            if (run.getJudgementRecord() != null) {
                System.out.print("Send to teams " + run.getJudgementRecord().isSendToTeam() + " ");
            } else {
                System.out.print("Send to teams false(not judged) ");
                System.out.println("Added run " + run);
            }
        }

    }

    /**
     * Fetch string from nodes.
     * 
     * @param node
     * @param fetchGroupRank
     *            if true, fetch the group rank
     * @return array of String "Rank", "Name", "Solved", "Points"
     */
    private String[] fetchStanding(Node node, boolean fetchGroupRank) {

        // Object[] cols = { "Rank", "Name", "Solved", "Points" };

        String[] outArray = new String[4];

        String rank = "";
        String groupRank = "";

        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node standingNode = attributes.item(i);
            String value = standingNode.getNodeValue();
            String name = standingNode.getNodeName();
            if (name.equals("rank")) {
                rank = value;
            } else if (name.equals("groupRank")) {
                groupRank = value;
            } else if (name.equals("teamName")) {
                outArray[1] = value;
            } else if (name.equals("solved")) {
                outArray[2] = value;
            } else if (name.equals("points")) {
                outArray[3] = value;
            }
        }

        if (fetchGroupRank) {
            outArray[0] = groupRank;
        } else {
            outArray[0] = rank;
        }

        return outArray;
    }

    /**
     * Confirms ranks between runs in contest and rankData.
     * 
     * rankdata is a array of string, each string contains comma delimited fields: rank,teamid,solved,points
     * <p>
     * rankdata is compared with XML from DefaultScoringAlgorithm and if all ranks/fields match, passes the test.
     * 
     * @param contest
     * @param rankData
     * @param compareGroupRanks
     */
    private void confirmRanks(InternalContest contest, String[] rankData) {
        confirmRanks(contest, rankData, new Properties(), false);
    }

    /**
     * Confirms ranks between runs in contest and rankData.
     * 
     * @see #confirmGroupRanks(InternalContest, String[])
     * 
     * @param contest
     * @param rankData
     */
    private void confirmGroupRanks(InternalContest contest, String[] rankData) {
        confirmRanks(contest, rankData, new Properties(), true);
    }

    /**
     * Compares a standings Row with an expected row.
     * 
     * Each row contains: rank, name, number solved, points.
     * 
     * @param rankIndex
     * @param standingsRow
     * @param expectedRow
     */
    private void compareRanking(int rankIndex, String[] standingsRow, String[] expectedRow) {

        // Object[] cols = { "Rank", "Name", "Solved", "Points" };
        
        if (OUTPUT_SAMPLE_DATA_FLAG) {
            System.out.print("\"");
            for (int i = 0; i < standingsRow.length; i++) {
                System.out.print(standingsRow[i]);
                if (i < standingsRow.length - 1) {
                    System.out.print(",");
                }
            }
            System.out.println("\", // ");
        }
        else 
        {
            int idx = 0;
            assertEquals("Standings row " + rankIndex + " rank incorrect, ", expectedRow[idx], standingsRow[idx]);
            // assertTrue ("Standings row "+rankIndex+" rank wrong expected "+expectedRow[idx]+" found "+standingsRow[idx],
            // standingsRow[idx].equals(expectedRow[idx]));
            idx++;
            assertEquals("Standings row " + rankIndex + " team name incorrect, ", expectedRow[idx], standingsRow[idx]);
            // assertTrue ("Standings row "+rankIndex+" name wrong expected "+expectedRow[idx]+" found "+standingsRow[idx],
            // standingsRow[idx].equals(expectedRow[idx]));
            idx++;
            assertEquals("Standings row " + rankIndex + " number solved incorrect, ", expectedRow[idx], standingsRow[idx]);
            // assertTrue ("Standings row "+rankIndex+" number solved wrong expected "+expectedRow[idx]+" found "+standingsRow[idx],
            // standingsRow[idx].equals(expectedRow[idx]));
            idx++;
            assertEquals("Standings row " + rankIndex + " penalty points incorrect ", expectedRow[idx], standingsRow[idx]);
            // assertTrue ("Standings row "+rankIndex+" points wrong expected "+expectedRow[idx]+" found "+standingsRow[idx],
            // standingsRow[idx].equals(expectedRow[idx]));
        }

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test getRuns method.
     * 
     * Test whether runs from getRuns(runs,clientId,problem) method returns proper number of runs.
     * 
     * @throws Exception
     */
    public void testgetRuns() throws Exception {

        // RunID TeamID Prob Time Result

        String[] runsDataList = { "1,1,A,1,No", // 20
                "2,1,A,3,Yes", // 3 (Minute points for 1st Yes count)
                "3,1,A,5,No", // 20 (a No on a solved problem)
                "4,1,A,7,Yes", // zero (only "No's" count)
                "5,1,A,9,No", // 20 (another No on the solved problem)

                "6,1,B,11,No", // zero (problem has not been solved)
                "7,1,B,13,No", // zero (problem has not been solved)

                "8,2,A,30,Yes", // 30 (Minute points for 1st Yes)

                "9,2,B,35,No", //
                "10,3,B,40,New", //
                "11,4,B,145,Yes", //
                "12,3,B,50,Yes", //
                "13,4,B,155,Yes", //
                "14,4,C,155,New", //
                "15,4,C,155,New", //
                "16,2,B,155,New", //

                "17,5,B,155,Yes", //
                "18,5,D,15,New", //
                "18,5,D,15,No", //
                "19,5,D,152,Yes", //
        };

        InternalContest contest = new InternalContest();
        int numTeams = 5;

        initData(contest, numTeams, 5);

        for (String runInfoLine : runsDataList) {
            addTheRun(contest, runInfoLine);
        }

        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        Problem[] problems = contest.getProblems();

        // Properties properties = DefaultScoringAlgorithm.getDefaultProperties();
        NewScoringAlgorithm algorithm = new NewScoringAlgorithm();

        Vector<Account> acc = contest.getAccounts(Type.TEAM);

        String[] expectedData = { //
                "TEAM5 @ site 1 A=0 B=1 C=0 D=3 E=0 ", //
                "TEAM4 @ site 1 A=0 B=2 C=2 D=0 E=0 ", //
                "TEAM3 @ site 1 A=0 B=2 C=0 D=0 E=0 ", //
                "TEAM2 @ site 1 A=1 B=2 C=0 D=0 E=0 ", //
                "TEAM1 @ site 1 A=5 B=2 C=0 D=0 E=0 ", //
        };
        
        int expectedIndex = 0;
        for (Account account : acc) {
            String actual = "";
            actual += account.getClientId() + " ";
            
            for (Problem problem : problems) {
                Run[] theruns = algorithm.getRuns(runs, account.getClientId(), problem);
                actual += problem.getLetter() + "=" + theruns.length + " ";
            }

            String expected = expectedData[expectedIndex];
            if (OUTPUT_SAMPLE_DATA_FLAG) {
                System.out.println("\"" + actual + "\", //");
            } else {
                assertEquals("Expecting data ", expected, actual);
            }
            expectedIndex++;
        }
    }

    /**
     * Test get methods in ProblemSummary.
     * 
     * Bug 1028.
     * 
     * @throws Exception
     */
    public void testRankings1028() throws Exception {

        // RunID TeamID Prob Time Result

        String[] runsDataList = { "1,1,A,1,No", // 20
                "2,1,A,3,Yes", // 3 (Minute points for 1st Yes count)
                "3,1,A,5,No", // 20 (a No on a solved problem)
                "4,1,A,7,Yes", // zero (only "No's" count)
                "5,1,A,9,No", // 20 (another No on the solved problem)

                "6,1,B,11,No", // zero (problem has not been solved)
                "7,1,B,13,No", // zero (problem has not been solved)

                "8,2,A,30,Yes", // 30 (Minute points for 1st Yes)

                "9,2,B,35,No", //
                "10,3,B,40,New", //
                "11,4,B,145,Yes", //
                "12,3,B,50,Yes", //
                "13,4,B,155,Yes", //
                "14,4,C,155,New", //
                "15,4,C,155,New", //
                "16,2,B,155,New", //

                "17,5,B,155,Yes", //
                "18,5,D,15,New", //
                "18,5,D,15,No", //
                "19,5,D,152,Yes", //
        };

        InternalContest contest = new InternalContest();
        int numTeams = 5;

        initData(contest, numTeams, 5);

        for (String runInfoLine : runsDataList) {
            addTheRun(contest, runInfoLine);
        }

        INewScoringAlgorithm algorithm = new NewScoringAlgorithm();

        Properties properties = DefaultScoringAlgorithm.getDefaultProperties();

        /**
         * Representation of a StandingRecord
         * 
         * name, rank, solved, lastsolved, points; then for each problem: problem#, numRuns, points, judgedCount, pendingCount ;
         */
        String[] rep = { //
                "team5, 1,2,155,327; 1,0,0,0,0; 2,1,155,1,0; 3,0,0,0,0; 4,3,172,2,1; 5,0,0,0,0; ", // 
                "team1, 2,1,3,23; 1,5,23,5,0; 2,2,0,2,0; 3,0,0,0,0; 4,0,0,0,0; 5,0,0,0,0; ", // 
                "team2, 3,1,30,30; 1,1,30,1,0; 2,2,0,1,1; 3,0,0,0,0; 4,0,0,0,0; 5,0,0,0,0; ", // 
                "team3, 4,1,50,50; 1,0,0,0,0; 2,2,50,1,1; 3,0,0,0,0; 4,0,0,0,0; 5,0,0,0,0; ", // 
                "team4, 5,1,145,145; 1,0,0,0,0; 2,2,145,2,0; 3,2,0,0,2; 4,0,0,0,0; 5,0,0,0,0; ", // 
        };

        int i = 0;

        // Run[] runs = contest.getRuns();
        // Arrays.sort(runs, new RunComparator());
        // for (Run run : runs) {
        // System.out.println(run);
        // }

        StandingsRecord[] recs = algorithm.getStandingsRecords(contest, properties);

        for (StandingsRecord standingsRecord : recs) {
            // System.out.println("rec = " + standingsRecord);
            if ( OUTPUT_SAMPLE_DATA_FLAG ){
                dumpStandingsRecordSampleData(standingsRecord);
            } else {
                String expected = rep[i];
                String actual = getStndingsRecordRepresenation(standingsRecord);
                
//                System.out.println(expected); 
//                System.out.println(actual);
//                System.out.println("");
                
                assertEquals("Expecting same rep ", expected, actual);
            }

            i++;
        }
    }

    /**
     * Prints test data representation for standingsRecord.
     * 
     * @param standingsRecord
     */
    protected void dumpStandingsRecordSampleData(StandingsRecord standingsRecord) {

        System.out.print("\"");
        System.out.print(getStndingsRecordRepresenation(standingsRecord));
        System.out.println("\", // ");

    }

    /**
     * Returns sample/test data.
     * 
     * name,  rank, solved, lastsolvedtime, penalty then <br>
     *     probbNum, points, judgecount, pendingcount
     * 
     * @param standingsRecord
     * @return
     */
    private String getStndingsRecordRepresenation(StandingsRecord standingsRecord) {

        StringBuffer buf = new StringBuffer();

        buf.append( //
        standingsRecord.getClientId().getName() + ", " + //
                standingsRecord.getRankNumber() + "," + //
                standingsRecord.getNumberSolved() + "," + //
                standingsRecord.getLastSolved() + "," + //
                standingsRecord.getPenaltyPoints() + "; " //
        );

        SummaryRow row = standingsRecord.getSummaryRow();

        Integer[] problemNumbers = row.getSortedKeys();
        for (Integer integer : problemNumbers) {
            ProblemSummaryInfo problemSummaryInfo = row.get(integer);
            buf.append(integer + "," + //
                    problemSummaryInfo.getNumberSubmitted() + "," + //
                    problemSummaryInfo.getPenaltyPoints() + "," + //
                    problemSummaryInfo.getJudgedRunCount() + "," + //
                    problemSummaryInfo.getPendingRunCount() + "; " //
            );
        }

        return buf.toString();

    }
}
