package edu.csus.ecs.pc2.core.scoring;

import java.io.File;
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
 * Test Scoring Algorithm.
 * 
 * The inital tests were to insure that proper XML
 * is created on startup.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class DefaultScoringAlgorithmTest extends AbstractTestCase {
    
    private Log log = null;
    
    private boolean debugMode = false;
    // alt1: 0 0 200 0 0
    private Properties alt1 = populateProperties(0, 0, 200, 0, 0);
    // alt2: 30 5 0 0 0
    private Properties alt2 = populateProperties(30, 5, 0, 0, 0);
    // alt3: 0 10 0 0 0
    private Properties alt3 = populateProperties(0, 10, 0, 0, 0);
    // alt4: 5 0 20 0 0
    private Properties alt4 = populateProperties(5, 0, 20, 0, 0);
    // alt5: 5 0 20 3 7
    private Properties alt5 = populateProperties(20, 1, 0, 3, 7);

    private File loadData;

    protected void setUp() throws Exception {
        super.setUp();
    
        log = createLog("DefaultScoringAlgorithmTest");
        
        StaticLog.setLog(log);

//        String loadFile = projectPath + File.separator+ testDir + File.separator + "Sumit.java";
        String loadFile = getSamplesSourceFilename(SUMIT_SOURCE_FILENAME);
        loadData = new File(loadFile);
        if (!loadData.exists()) {
            System.err.println("could not find " + loadFile);
            throw new Exception("Unable to locate "+loadFile);
        }
        
    }

    private Properties populateProperties(int perNo, int perMin, int baseYes, int perCE, int perSV) {
        Properties props=DefaultScoringAlgorithm.getDefaultProperties();
        Enumeration<Object> keys= props.keys();
        while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            switch (key) {
                case DefaultScoringAlgorithm.BASE_POINTS_PER_YES:
                    props.put(key, Integer.toString(baseYes));
                    break;
                case DefaultScoringAlgorithm.POINTS_PER_NO:
                    props.put(key, Integer.toString(perNo));
                    break;
                case DefaultScoringAlgorithm.POINTS_PER_NO_COMPILATION_ERROR:
                    props.put(key, Integer.toString(perCE));
                    break;
                case DefaultScoringAlgorithm.POINTS_PER_NO_SECURITY_VIOLATION:
                    props.put(key, Integer.toString(perSV));
                    break;
                case DefaultScoringAlgorithm.POINTS_PER_YES_MINUTE:
                    props.put(key, Integer.toString(perMin));
                    break;
                default:
                    assertTrue("Unknown property: "+key,true);
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
        contest.setClientId(createBoardAccount (contest));

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
        contest.setClientId(createBoardAccount (contest));
        
        // Add Problem
        Problem problem = new Problem("Problem One");
        contest.addProblem(problem);
        
        // Add Language
        Language language = new Language("Language One");
        contest.addLanguage(language);
        
        String[] judgementNames = { "Yes", "No - compilation error", "No - incorrect output", "No - It's just really bad",
                "No - judges enjoyed a good laugh", "You've been bad - contact staff", "No - Illegal Function" };
        
        String[] acronyms = { "AC", "CE", "WA", "WA", "WA", "WA", "SV" };
        
        for (int i = 0; i < judgementNames.length; i++) {
            Judgement judgement = new Judgement(judgementNames[i], acronyms[i]);
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
     * @param contest
     * @param numTeams
     * @param numProblems
     */
    private void initData(IInternalContest contest, int numTeams, int numProblems) {

        // Add accounts
        contest.generateNewAccounts(ClientType.Type.TEAM.toString(), numTeams, true);
        contest.generateNewAccounts(ClientType.Type.JUDGE.toString(), 6, true);
        
        // Add scoreboard account and set the scoreboard account for this client (in contest)
        contest.setClientId(createBoardAccount (contest));
        
        checkForJudgeAndTeam(contest);
        
        // Add Problem
        for (int i = 0; i < numProblems; i ++){
            char letter = 'A';
            letter += i;
            Problem problem = new Problem(""+letter);
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
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void testOneRunUnjudged() throws IOException, ClassNotFoundException, FileSecurityException {

        InternalContest contest = new InternalContest();
        
        initContestData(contest);
        Run run = getARun(contest);
        // Directory where test data is
//        String testDir = "testdata";
//        String projectPath=JUnitUtilities.locate(testDir);
//        if (projectPath == null) {
//            throw new IOException("Unable to locate "+testDir);
//        }

        RunFiles runFiles = new RunFiles(run, loadData.getAbsolutePath());
        
        contest.addRun(run, runFiles, null);
        
        checkOutputXML(contest);
    }

    /**
     * Verify XML created for a single unjudged run.
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void testMixedjudged() throws IOException, ClassNotFoundException, FileSecurityException {

        InternalContest contest = new InternalContest();
        
        initContestData(contest);
        Run run = getARun(contest, 5);
        RunFiles runFiles = new RunFiles(run, loadData.getAbsolutePath());
        
        contest.addRun(run, runFiles, null);

        createJudgedRun(contest, 0, false, 7);
        
        run = getARun(contest, 10);
        contest.addRun(run, runFiles, null);

        createJudgedRun(contest, 0, true, 15);
       
        checkOutputXML(contest);
    }
    
    public void testCESVNoJudgements() throws IOException, ClassNotFoundException, FileSecurityException {

        String [] runsData = {
                "1,1,A,1,No,No,4",  // 0 (a No before first yes Security Violation)
                "2,1,A,1,No,No,2",  // 0 (a No before first yes Compilation Error)
                "3,1,A,1,No,No,1",  // 20 (a No before first yes)
                "4,1,A,3,Yes,No,0",  // 3 (first yes counts Minute points but never Run Penalty points)
                "5,1,A,5,No,No,1",  // zero -- after Yes
                "6,1,A,7,Yes,No,0",  // zero -- after Yes
                "7,1,A,9,No,No,1",  // zero -- after Yes
                "8,1,B,11,No,No,1",  // zero -- not solved
                "9,2,A,48,No,No,4",  // 0 (a No before first yes Security Violation)
                "10,2,A,50,Yes,No,0",  // 50 (minute points; no Run points on first Yes)
                "11,2,B,35,No,No,1",  // zero -- not solved
                "12,2,B,40,No,No,1",  // zero -- not solved
        };
        
        // Rank  TeamId Solved Penalty
        
        String [] rankData = {
                "1,team1,1,23",
                "2,team2,1,50"
        };
        
        String [] rankData5 = {
                "1,team1,1,33", // +7 for SV + 3 for CE
                "2,team2,1,57" // +7 for SV
        };


        scoreboardTest(2, runsData, rankData);
        scoreboardTest(2, runsData, rankData5, alt5);

    } 
    
    /**
     * Create a judged run
     * 
     * @param contest
     * @param judgementIndex - the judgement list index
     * @param solved - was this run solved/Yes judgement
     * @throws FileSecurityException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     */
    public void createJudgedRun (IInternalContest contest, int judgementIndex, boolean solved) throws IOException, ClassNotFoundException, FileSecurityException {
        Run run = getARun(contest);
        RunFiles runFiles = new RunFiles(run, loadData.getAbsolutePath());
        
        contest.addRun(run, runFiles, null);
        
        ClientId who = contest.getAccounts(ClientType.Type.JUDGE).firstElement().getClientId();
        
        checkOutRun (contest, run, who);
        
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
    public void createJudgedRun (IInternalContest contest, int judgementIndex, boolean solved, int elapsedMinutes) throws IOException, ClassNotFoundException, FileSecurityException{
        Run run = getARun(contest, elapsedMinutes);
        RunFiles runFiles = new RunFiles(run, loadData.getAbsolutePath());
        
        contest.addRun(run, runFiles, null);
        
        ClientId who = contest.getAccounts(ClientType.Type.JUDGE).firstElement().getClientId();
        assertFalse ("Could not retrieve first judge ", who == null);
        
        checkOutRun (contest,run,who);
        
        Judgement judgement = contest.getJudgements()[judgementIndex]; // Judge as No
        
        JudgementRecord judgementRecord = new JudgementRecord(judgement.getElementId(), who, solved, false);
        contest.addRunJudgement(run, judgementRecord, null, who);
        
    }
    /**
     * Get XML from ScoringAlgorithm and test whether it can be parsed.
     * @param contest
     */
    public void checkOutputXML (IInternalContest contest) {
       
        try {
            DefaultScoringAlgorithm defaultScoringAlgorithm = new DefaultScoringAlgorithm();
            String xmlString = defaultScoringAlgorithm.getStandings(contest, new Properties(), log);
            
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
    
    private void checkOutRun (IInternalContest contest, Run run, ClientId judgeId){
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
    public void testScoreboardCaseOne () {
        // RunID    TeamID  Prob    Time    Result
        
        String [] runsData = {
                "1,1,A,1,No",  // 20 (a No before first yes)
                "2,1,A,3,Yes",  // 3 (first yes counts Minute points but never Run Penalty points)
                "3,1,A,5,No",  // zero -- after Yes
                "4,1,A,7,Yes",  // zero -- after Yes
                "5,1,A,9,No",  // zero -- after Yes
                "6,1,B,11,No",  // zero -- not solved
                "7,1,B,13,No",  // zero -- not solved
                "8,2,A,30,Yes",  // 30 (minute points; no Run points on first Yes)
                "9,2,B,35,No",  // zero -- not solved
                "10,2,B,40,No",  // zero -- not solved
                "11,2,B,45,No",  // zero -- not solved
                "12,2,B,50,No",  // zero -- not solved
                "13,2,B,55,No",  // zero -- not solved
        };
        
        // Rank  TeamId Solved Penalty
        
        String [] rankData = {
                "1,team1,1,23",
                "2,team2,1,30"
        };
        
        scoreboardTest (2, runsData, rankData);
    }
    
    public void testScoreboardCaseOneA(){
        
        // RunID    TeamID  Prob    Time    Result
        
        String [] runsData = {
                "2,8,C,1,No",
                "15,8,D,1,Yes",
                "23,8,D,1,No",
                "29,8,D,1,No",
                "43,8,C,1,No",
                "44,8,A,1,Yes",
                "52,8,C,1,Yes",
                "65,8,B,2,Yes",
        };
        
        // Rank  TeamId Solved Penalty
        
        String [] rankData = {
                "1,team8,4,45",
                "2,team1,0,0",
                "2,team2,0,0",
                "2,team3,0,0",
                "2,team4,0,0",
                "2,team5,0,0",
                "2,team6,0,0",
                "2,team7,0,0",
        };
        
        scoreboardTest (8, runsData, rankData);
    }
    
    

    /**
     * Tests for cases where Yes is before No, and multiple yes at same elapsed time.
     * 
     * Both runs have same elapsed time, the tie breaker is runId.
     * Also tests when one or more Yes are after first yes 
     */
    public void testNoBeforeYesSameElapsed(){
        
        // RunID    TeamID  Prob    Time    Result
        
        String [] runsData = {
                "15,8,D,12,Yes",
                "16,8,D,12,No",
                
                "24,4,B,15,Yes",
                "25,4,B,15,No",
                "26,4,B,15,No",
                
                "28,2,C,22,Yes",
                "29,2,C,22,No",
                "30,2,C,22,Yes",
                "30,2,C,22,Yes",
        };
        
        /**
         * 
         * 
         * 
         */
        
        // Rank  TeamId Solved Penalty
        
        String [] rankData = {
                "1,team8,1,12",
                "2,team4,1,15",
                "3,team2,1,22",
                "4,team1,0,0",
                "4,team3,0,0",
                "4,team5,0,0",
                "4,team6,0,0",
                "4,team7,0,0",
        };
        
        scoreboardTest (8, runsData, rankData);
    }
    
    /**
     * CASE (2): "When Solved, all No Runs".
     *
     * Created from testing/boardtest.html
     */
    public void testScoreboardCaseTwo (){
        
        // RunID    TeamID  Prob    Time    Result
        
        String [] runsData = {
                "1,1,A,1,No", // 20
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
        

        // Rank  TeamId Solved Penalty
        
        String [] rankData = {
                "1,team1,1,23",
                "2,team2,1,30",
        };
        
        // TODO when SA supports count all runs, replace rankData
        
        /**
         * Case 2 tests when all no runs are counted, the current SA
         * does not support this scoring method.  The commented
         * rankData is the proper results
         */

//      Team 2 -- 30     <-- Team 2 is now winning with a lower score 
//      Team 1 -- 63           (same database; different scoring method)
        
//        String [] rankData = {
//                "1,team2,1,30",
//                "2,team1,1,63",
//        };
        
        
        scoreboardTest (2, runsData, rankData);
        
    }
    
    /**
     * CASE (3):  "When Solved, All Runs"
     * 
     * Created from testing/boardtest.html
     */
    public void testScoreboardCaseThree () {
        
        // RunID    TeamID  Prob    Time    Result
        
        String [] runsData = {

                "1,1,A,1,No",   // 20
                "2,1,A,3,Yes",   // 3 (first Yes counts only Min.Pts)
                "3,1,A,5,No",   // 20
                "4,1,A,7,Yes",   // 20 (all runs count!)
                "5,1,A,9,No",   // 20

                "6,1,B,11,No",   // zero (not solved)
                "7,1,B,13,No",   // zero (not solved)

                "8,2,A,30,Yes",   // 30

                "9,2,B,35,No",   // zero -- not solved
                "10,2,B,40,No",   // zero -- not solved
                "11,2,B,45,No",   // zero -- not solved
                "12,2,B,50,No",   // zero -- not solved
                "13,2,B,55,No",   // zero -- not solved
        };
        
        // Rank  TeamId Solved Penalty
        
        String [] rankData = {
                "1,team1,1,23",
                "2,team2,1,30"
        };
        
        // TODO when SA supports count all runs, replace rankData
        
        /**
         * Case 3 tests when all runs are counted, the current SA
         * does not support this scoring method.  The commented
         * rankData is the proper results
         */
        
//        String [] rankData = {
//                "1,team2,1,30"
//                "2,team1,1,83",
//        };
        
        scoreboardTest (2, runsData, rankData);
    }
    
    /**
     * CASE (4): "All Runs"
     * 
     * Created from testing/boardtest.html
     */
    public void testScoreboardCaseFour () {
        
        // RunID    TeamID  Prob    Time    Result
        
        String [] runsData = {

                "1,1,A,1,No",  //20
                "2,1,A,3,Yes",  //3 (first yes counts Minutes only)
                "3,1,A,5,No",  //20
                "4,1,A,7,Yes",  //20  
                "5,1,A,9,No",  //20
                
                "6,1,B,11,No",  //20  (all runs count)
                "7,1,B,13,No",  //20  (all runs count)
                
                "8,2,A,30,Yes",  //30
                
                "9,2,B,35,No",  //20 (all runs count)
                "10,2,B,40,No",  //20 (all runs count)
                "11,2,B,45,No",  //20 (all runs count)
                "12,2,B,50,No",  //20 (all runs count)
                "13,2,B,55,No",  //20 (all runs count)

        };
        
        // Rank  TeamId Solved Penalty
        
        String [] rankData = {
                "1,team1,1,23",
                "2,team2,1,30"
        };
        
        // TODO when SA supports count all runs, replace rankData
        
        /**
         * Case 4 tests when all runs are counted, the current SA
         * does not support this scoring method.  The commented
         * rankData is the proper results
         */
        
//        Team 1 -- 123   <-- Team 1 is winning again
//        Team 2 -- 130
        
//        String [] rankData = {
//                "1,team1,1,123",
//                "2,team2,1,130"
//        };
        
        scoreboardTest (2, runsData, rankData);
    }


    /**
     * 
     */
    public void testScoreboard55 (){

        String [] runsData = {
                "1,16,B,1,No",
                "2,8,C,1,No",
                "3,5,B,1,No",
                "4,4,C,1,No",
                "5,4,D,1,No",
                "6,3,A,1,No",
                "7,1,A,1,No",
                "8,6,B,1,New",
                "9,18,A,1,No",
                "10,6,A,1,No",
                "11,21,D,1,Yes",
                "12,6,D,1,No",
                "13,12,A,1,Yes",
                "14,13,A,1,No",
                "15,8,D,1,Yes",
                "16,3,B,1,No",
                "17,3,A,1,No",
                "18,16,C,1,No",
                "19,20,D,1,Yes",
                "20,12,B,1,No",
                "21,14,A,1,No",
                "22,15,C,1,No",
                "23,8,D,1,No",
                "24,13,D,1,No",
                "25,21,A,1,No",
                "26,18,D,1,Yes",
                "27,6,C,1,No",
                "28,20,B,1,Yes",
                "29,8,D,1,No",
                "30,19,B,1,No",
                "31,22,C,1,No",
                "32,7,A,1,No",
                "33,7,A,1,No",
                "34,4,D,1,New",
                "35,18,B,1,No",
                "36,4,D,1,Yes",
                "37,19,C,1,No",
                "38,2,B,1,No",
                "39,15,C,1,No",
                "40,12,B,1,No",
                "41,10,D,1,Yes",
                "42,22,A,1,No",
                "43,8,C,1,No",
                "44,8,A,1,Yes",
                "45,18,D,1,No",
                "46,13,C,1,No",
                "47,7,D,1,No",
                "48,7,C,1,New",
                "49,5,C,1,No",
                "50,7,B,1,New",
                "51,21,B,1,No",
                "52,8,D,1,Yes",
                "53,16,A,1,No",
                "54,10,A,1,No",
                "55,22,B,1,No",
                "56,18,C,1,No",
                "57,5,D,2,Yes",
                "58,10,C,2,No",
                "59,9,C,2,Yes",
                "60,5,D,2,Yes",
                "61,12,D,2,No",
                "62,10,C,2,No",
                "63,3,B,2,Yes",
                "64,21,C,2,No",
                "65,8,B,2,Yes",
                "66,19,B,2,Yes",
                "67,18,A,2,No",
                "68,12,D,2,Yes",
                "69,5,B,2,Yes",
                "70,2,A,2,No",
                "71,21,D,2,No",
                "72,12,D,2,No",
                "73,18,C,2,No",
                "74,14,D,2,Yes",
                "75,2,A,2,No",
                "76,20,D,2,No",
                "77,7,C,2,No",
                "78,14,D,2,No",
                "79,15,A,2,No",
                "80,16,B,2,No",
                "81,2,C,2,No",
                "82,2,C,2,No",
                "83,22,A,2,No",
                "84,21,D,2,Yes",
                "85,2,C,2,No",
                "86,10,C,2,No",
                "87,17,C,2,No",
                "88,7,A,2,New",
                "89,20,B,2,No",
                "90,12,C,2,No" 
        };
        
        // Rank  TeamId Solved Penalty
        
        String [] rankData = {
                "1,team8,3,4",
                "2,team20,2,2",
                "3,team12,2,23",
                "4,team5,2,24",
                "5,team10,1,1",
                "5,team18,1,1",
                "5,team21,1,1",
                "8,team9,1,2",
                "8,team14,1,2",
                "10,team3,1,22",
                "10,team19,1,22",
                "12,team4,1,41",
                "13,team1,0,0",
                "13,team2,0,0",
                "13,team6,0,0",
                "13,team7,0,0",
                "13,team11,0,0",
                "13,team13,0,0",
                "13,team15,0,0",
                "13,team16,0,0",
                "13,team17,0,0",
                "13,team22,0,0",
        };
        
        scoreboardTest (22, runsData, rankData);
    }
  
    /**
     * Test a No before a Yes, both runs same elapsed time.
     */
    public void testNoYes (){

        // RunID    TeamID  Prob    Time    Result
        
        String [] runsData = {
                "5,2,A,12,No",
                "6,2,A,12,Yes",
        };
        
        // Rank  TeamId Solved Penalty
        
        String [] rankData = {
                "1,team2,1,32",
                "2,team1,0,0",
        };
        
        scoreboardTest (2, runsData, rankData);
    }
    
    
    /**
     * Have run that has a BEING_JUDGED state and should show same standing
     * as the state were JUDGED.
     * 
     * Test for Bug 407 - The SA fails to reflect prelim judgements.
     * 
     * based on CASE (1): "When Solved, all runs before Yes".
     * 
     * Created from testing/boardtest.html
     * 
     */
    public void testScoreboardForBeingJudgedState () {
        // RunID    TeamID  Prob    Time    Result
        
        String [] runsData = {
                "1,1,A,1,No",  // 20 (a No before first yes)
                "2,1,A,3,Yes",  // 3 (first yes counts Minute points but never Run Penalty points)
                "3,1,A,5,No",  // zero -- after Yes
                "4,1,A,7,Yes",  // zero -- after Yes
                "5,1,A,9,No",  // zero -- after Yes
                "6,1,B,11,No",  // zero -- not solved
                "7,1,B,13,No",  // zero -- not solved
                "8,2,A,30,Yes",  // 30 (minute points; no Run points on first Yes)
                "9,2,B,35,No",  // zero -- not solved
                "10,2,B,40,No",  // zero -- not solved
                "11,2,B,45,No",  // zero -- not solved
                "12,2,B,50,No",  // zero -- not solved
                "13,2,B,55,No",  // zero -- not solved
        };
        
        // Rank  TeamId Solved Penalty
        
        String [] rankData = {
                "1,team1,1,23",
                "2,team2,1,30"
        };
        
        InternalContest contest = new InternalContest();
        
        int numTeams = 2;

        initData(contest, numTeams, 5);
        
        for (String runInfoLine : runsData) {
            addTheRun(contest, runInfoLine);
        }

        Run [] runs = contest.getRuns();
        
        for (Run run : runs){
            run.setStatus(RunStates.BEING_JUDGED);
        }

        checkOutputXML(contest);

        confirmRanks(contest, rankData);
    }

    /**
     * Test tie breaker down to last yes submission time. 
     * 
     */
    public void testTieBreakerSubmissionTime(){

        // Sort order:
        // Primary Sort = number of solved problems (high to low)
        // Secondary Sort = score (low to high)
        // Tertiary Sort = earliest submittal of last submission (low to high)
        // Forth Sort = teamName (low to high)
        // Fifth Sort = clientId (low to high)
        
        // RunID    TeamID  Prob    Time    Result
        
        String [] runsData = {
                "5,5,A,12,No",
                "6,5,A,12,Yes", 
                
                "7,6,A,12,No",
                "8,6,A,12,Yes",
                
                // Both solve 1 score 32  (no for 20, 12 min)

                "15,5,B,21,No",
                "16,5,B,22,Yes",  
                
                "25,6,B,21,No",
                "26,6,B,22,Yes",  
                
                // Both solve 2 score 42  (no for 20 and 22 min)
                // total 74 each
        };
        
        // Rank  TeamId Solved Penalty
        
        String [] rankData = {
                // must identical score, sort by team display name, identical ranks.
                "1,team5,2,74",
                "1,team6,2,74",
                "3,team1,0,0",
                "3,team2,0,0",
                "3,team3,0,0",
                "3,team4,0,0",
        };
        
        scoreboardTest (6, runsData, rankData);
    }

    /**
     * Test whether SA respects send to team 
     */
    public void testSendToTeams(){

        // Sort order:
        // Primary Sort = number of solved problems (high to low)
        // Secondary Sort = score (low to high)
        // Tertiary Sort = earliest submittal of last submission (low to high)
        // Forth Sort = teamName (low to high)
        // Fifth Sort = clientId (low to high)
        
        // RunID    TeamID  Prob    Time    Result
        
        String [] runsData = {
                "5,5,A,12,No",
                "6,5,A,12,Yes", 

                // t5 solves A, 32 pts = 20 + 12
                
                "7,6,A,12,No,Yes",
                "8,6,A,12,Yes,No",
                
                // t6 does not solve, 0 solve, 0 pts

                "15,5,B,21,No",
                "16,5,B,22,Yes,No",  
                
                // t5 does solve B, but not sent to team/used 0 pts 0 solved
                
                "25,6,B,21,No,No",
                "26,6,B,22,Yes,Yes",
                
                // t6 solves B, 22 pts because No at 21 is NOT counted.
        };
        
        // Rank  TeamId Solved Penalty
        
        String [] rankData = {
                // rank, team, solved, pts
                "1,team6,1,22",
                "2,team5,1,32",
                "3,team1,0,0",
                "3,team2,0,0",
                "3,team3,0,0",
                "3,team4,0,0",
        };
        
        scoreboardTest (6, runsData, rankData, true);
    }

    public void testAltScoring() {
        // RunID    TeamID  Prob    Time    Result
        
        String [] runsData = {
                "1,1,A,1,No", // 20
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
        

        // Rank  TeamId Solved Penalty
        
        // alt1: 0 0 200
        
        String[] alt1rankData = {
                "1,team1,1,200",
                "2,team2,1,200", // tie-breaker causes rank 2
        };
        
        scoreboardTest (2, runsData, alt1rankData, alt1);
        // alt2: 30 5 0
        String[] alt2rankData = {
                "1,team1,1,45",  // 1 no@30 each + 3 min * 5
                "2,team2,1,150", // 5*30
        };
        
        scoreboardTest (2, runsData, alt2rankData, alt2);
        
        // alt3: 0 10 0
        String[] alt3rankData = {
                "1,team1,1,30", // 3 min * 10
                "2,team2,1,300", // 30 min * 10
        };
        
        scoreboardTest (2, runsData, alt3rankData, alt3);
        
        // alt4: 5 0 20
        String[] alt4rankData = {
                "1,team2,1,20", // base yes
                "2,team1,1,25", // base yes + 1 no
        };
        
        scoreboardTest (2, runsData, alt4rankData, alt4);

    }
    /**
     * This is a test for bug 691
     */
    public void testDeletedProblem() throws IOException, ClassNotFoundException, FileSecurityException {
 
        InternalContest contest = new InternalContest();

        int numTeams = 2;
        initData(contest, numTeams , 5);
        String [] runsData = {

                "1,1,A,1,No",  //20
                "2,1,A,3,Yes",  //3 (first yes counts Minutes only)
                "3,1,A,5,No",  //20
                "4,1,A,7,Yes",  //20  
                "5,1,A,9,No",  //20
                
                "6,1,B,11,No",  //20  (all runs count)
                "7,1,B,13,No",  //20  (all runs count)
                
                "8,2,A,30,Yes",  //30
                
                "9,2,B,35,No",  //20 (all runs count)
                "10,2,B,40,No",  //20 (all runs count)
                "11,2,B,45,No",  //20 (all runs count)
                "12,2,B,50,No",  //20 (all runs count)
                "13,2,B,55,No",  //20 (all runs count)

        };
        
        // Rank  TeamId Solved Penalty
        
        String [] rankData = {
                "1,team1,0,0",
                "1,team2,0,0"
        };

        for (String runInfoLine : runsData) {
            addTheRun(contest, runInfoLine);
        }

        Problem probA = contest.getProblems()[0];
        probA.setActive(false);
        contest.updateProblem(probA);
        Problem probA1 = contest.getProblem(probA.getElementId());
        assertEquals("probA1 setup", false, probA1.isActive());
        Problem probA2 = contest.getProblems()[0];
        assertEquals("probA2 setup", false, probA2.isActive());
        confirmRanks(contest, rankData);
        Document document = null;

        try {
            DefaultScoringAlgorithm defaultScoringAlgorithm = new DefaultScoringAlgorithm();
            String xmlString = defaultScoringAlgorithm.getStandings(contest, null, log);
            if (debugMode) {
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
        
        // skip past nodes to find teamStanding node
        NodeList list = document.getDocumentElement().getChildNodes();
        
        int rankIndex = 0;
        
        for(int i=0; i<list.getLength(); i++) {
            Node node = (Node)list.item(i);
            String name = node.getNodeName();
            if (name.equals("teamStanding")){
                String [] standingsRow = fetchStanding (node);
//              Object[] cols = { "Rank", "Name", "Solved", "Points" };
                String [] cols = rankData[rankIndex].split(",");

                if (debugMode) {
                    System.out.println("SA rank="+standingsRow[0]+" solved="+standingsRow[2]+" points="+standingsRow[3]+" name="+standingsRow[1]);
                    System.out.println("   rank="+cols[0]+" solved="+cols[2]+" points="+cols[3]+" name="+cols[1]);
                    System.out.println();
                    System.out.flush();
                }
                
                compareRanking (rankIndex+1, standingsRow, cols);
                rankIndex++;
            } else if(name.equals("standingsHeader")) {
                String problemCount = node.getAttributes().getNamedItem("problemCount").getNodeValue();
                int problemCountInt = Integer.valueOf(problemCount);
                NodeList list2 =  node.getChildNodes();
                int foundProblemCount = -1;
                for(int j=0; j<list2.getLength(); j++) {
                    Node node2 = (Node)list2.item(j);
                    String name2 = node2.getNodeName();
                    if (name2.equals("problem")){
                        int id = Integer.valueOf(node2.getAttributes().getNamedItem("id").getNodeValue());
                        if (id > foundProblemCount) {
                            foundProblemCount = id;
                        }
                    }
                }
                assertEquals("problem list max id",problemCountInt, foundProblemCount);
                assertEquals("problemCount","4", problemCount);
            }
        }

    }

    private void scoreboardTest(int numTeams, String[] runsData, String[] rankData, Properties scoreProps) {
        scoreboardTest (numTeams, runsData, rankData, false, scoreProps);
         
    }
    
    private void scoreboardTest(int numTeams, String[] runsDataList, String[] rankDataList, boolean respectSendTo, Properties scoreProps) {
        
        InternalContest contest = new InternalContest();

        initData(contest, numTeams, 5);
        
        if (respectSendTo){
            /**
             * Set permission that will respect the {@link JudgementRecord#isSendToTeam()}
             */
            Account account = contest.getAccount(contest.getClientId());
            account.addPermission(edu.csus.ecs.pc2.core.security.Permission.Type.RESPECT_NOTIFY_TEAM_SETTING);
        }
        
        for (String runInfoLine : runsDataList) {
            addTheRun(contest, runInfoLine);
        }

        confirmRanks(contest, rankDataList, scoreProps);
        
    }

    private void confirmRanks(InternalContest contest, String[] rankData, Properties scoreProps) {
        Document document = null;

        if (debugMode) {

            Run[] runs = contest.getRuns();
            Arrays.sort(runs, new RunComparator());
            for (Run run : runs) {
                System.out.println("Run " + run.getNumber() + " time=" + run.getElapsedMins() + " " + run.getSubmitter().getName() + " solved=" + run.isSolved());
            }
            System.out.flush();

        }
        
        // Rank  Solved Penalty TeamId
        
        try {
            DefaultScoringAlgorithm defaultScoringAlgorithm = new DefaultScoringAlgorithm();
            String xmlString = defaultScoringAlgorithm.getStandings(contest, scoreProps, log);
            
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
        
        // skip past nodes to find teamStanding node
        NodeList list = document.getDocumentElement().getChildNodes();
        
        int rankIndex = 0;
        
        for(int i=0; i<list.getLength(); i++) {
            Node node = (Node)list.item(i);
            String name = node.getNodeName();
            if (name.equals("teamStanding")){
                String [] standingsRow = fetchStanding (node);
//              Object[] cols = { "Rank", "Name", "Solved", "Points" };
                String [] cols = rankData[rankIndex].split(",");

                if (debugMode) {
                    System.out.println("SA rank="+standingsRow[0]+" solved="+standingsRow[2]+" points="+standingsRow[3]+" name="+standingsRow[1]);
                    System.out.println("   rank="+cols[0]+" solved="+cols[2]+" points="+cols[3]+" name="+cols[1]);
                    System.out.println();
                    System.out.flush();
                }
                
                compareRanking (rankIndex+1, standingsRow, cols);
                rankIndex++;
            }
        }
    }

    /**
     * Test whether SA respects send to team 
     */
    public void testZZZZEOCSettings(){

        // Sort order:
        // Primary Sort = number of solved problems (high to low)
        // Secondary Sort = score (low to high)
        // Tertiary Sort = earliest submittal of last submission (low to high)
        // Forth Sort = teamName (low to high)
        // Fifth Sort = clientId (low to high)
        
        // RunID    TeamID  Prob    Time    Result
        
        String [] runsData = {
                "1,1,A,250,No",
                "2,1,A,290,Yes", 

                // t5 solves A, 310 pts = 20 + 290
                // but with ECO settings, yes is not seen, so 0
                
        };
        
        // Rank  TeamId Solved Penalty
        
        String [] rankData = {
                // rank, team, solved, pts
                "1,team1,0,0",
        };
        // without EOC permission
        String [] rankData2 = {
                // rank, team, solved, pts
                "1,team1,1,310",
        };
        
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
     * rankDataList array is array of string, thus: Rank  TeamDisplayName Solved Penalty,
     * for example: "1,team5,2,74",
     * 
     * @param numTeams
     * @param runsDataList
     * @param rankDataList
     */
    public void scoreboardTest(int numTeams, String[] runsDataList, String[] rankDataList) {
        scoreboardTest(numTeams, runsDataList, rankDataList, false);
    }
    
    public void scoreboardTest(int numTeams, String[] runsDataList, String[] rankDataList, boolean respectSendTo)  {

        InternalContest contest = new InternalContest();

        initData(contest, numTeams, 5);
        
        if (respectSendTo){
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
     * 6 - No Judgement index
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
    public void addTheRun(IInternalContest contest, String runInfoLine) {

        // get 5th judge
        ClientId judgeId = contest.getAccounts(Type.JUDGE).elementAt(4).getClientId();
        
        Problem[] problemList = contest.getProblems();
        Language languageId = contest.getLanguages()[0];

        Judgement yesJudgement = contest.getJudgements()[0];
        Judgement[] judgement = contest.getJudgements();
        Judgement noJudgement = null;
        for (int i = 0; i < judgement.length; i++) {
            if (judgement[i].getAcronym().equals("WA")) {
                noJudgement = judgement[i];
                break;
            }
        }
        
        String[] data = runInfoLine.split(",");
        
        // Line is: runId,teamId,problemLetter,elapsed,solved[,sendToTeamsYN]

        int runId = getIntegerValue(data[0]);
        int teamId = getIntegerValue(data[1]);
        String probLet = data[2];
        int elapsed = getIntegerValue(data[3]);
        boolean solved = data[4].equals("Yes");
        
        boolean sendToTeams = true;
        if (data.length > 5){
            sendToTeams = data[5].equals("Yes");
        }
        if (data.length > 6) {
            noJudgement = contest.getJudgements()[getIntegerValue(data[6])];
        }

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

            checkOutRun(contest, run, judgeId);

            contest.addRunJudgement(run, judgementRecord, null, judgeId);
            
        } catch (IOException e) {
            e.printStackTrace();
            assertFalse("Unable to add run from run: "+run, false);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            assertFalse("Unable to add run from run: "+run, false);
        } catch (FileSecurityException e) {
            e.printStackTrace();
            assertFalse("Unable to add run from run: "+run, false);
        }

        if (debugMode){
            System.out.print("Send to teams "+run.getJudgementRecord().isSendToTeam()+" ");
            System.out.println("Added run "+run);
        }
        
    }
    
    

    

    /**
     * Fetch string from nodes.
     * 
     * @param node
     * @return
     */
    private String[] fetchStanding(Node node) {
        
//        Object[] cols = { "Rank", "Name", "Solved", "Points" };

        String[] outArray = new String[4];

        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node standingNode = attributes.item(i);
            String value = standingNode.getNodeValue();
            String name = standingNode.getNodeName();
            if (name.equals("rank")) {
                outArray[0] = value;
            } else if (name.equals("teamName")) {
                outArray[1] = value;
            } else if (name.equals("solved")) {
                outArray[2] = value;
            } else if (name.equals("points")) {
                outArray[3] = value;
            }
        }

        return outArray;
    }

    /**
     * Confirms ranks between runs in contest and rankData.
     * 
     * rankdata is a array of string, each string contains
     * comma delimited fields: rank,teamid,solved,points
     * <p>
     * rankdata is compared with XML from DefaultScoringAlgorithm
     * and if all ranks/fields match, passes the test.
     * 
     * @param contest
     * @param rankData
     */
    private void confirmRanks(InternalContest contest, String[] rankData) {
        confirmRanks(contest, rankData, new Properties());
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
    private void compareRanking(int rankIndex, String[] standingsRow, String [] expectedRow) {
        
//        Object[] cols = { "Rank", "Name", "Solved", "Points" };
     
        int idx = 0;
        assertEquals("Standings row "+rankIndex+" rank incorrect, ", expectedRow[idx], standingsRow[idx]);
//        assertTrue ("Standings row "+rankIndex+" rank wrong expected "+expectedRow[idx]+" found "+standingsRow[idx], standingsRow[idx].equals(expectedRow[idx]));
        idx++;
        assertEquals("Standings row "+rankIndex+" team name incorrect, ", expectedRow[idx], standingsRow[idx]);
//        assertTrue ("Standings row "+rankIndex+" name wrong expected "+expectedRow[idx]+" found "+standingsRow[idx], standingsRow[idx].equals(expectedRow[idx]));
        idx++;
        assertEquals("Standings row "+rankIndex+" number solved incorrect, ", expectedRow[idx], standingsRow[idx]);
//        assertTrue ("Standings row "+rankIndex+" number solved wrong expected "+expectedRow[idx]+" found "+standingsRow[idx], standingsRow[idx].equals(expectedRow[idx]));
        idx++;
        assertEquals("Standings row "+rankIndex+" penalty points incorrect ", expectedRow[idx], standingsRow[idx]);
//        assertTrue ("Standings row "+rankIndex+" points wrong expected "+expectedRow[idx]+" found "+standingsRow[idx], standingsRow[idx].equals(expectedRow[idx]));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
