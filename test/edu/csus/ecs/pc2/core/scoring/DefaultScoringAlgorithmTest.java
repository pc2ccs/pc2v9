package edu.csus.ecs.pc2.core.scoring;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Contest;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Run.RunStates;

/**
 * Test Scoring Alogorithm.
 * 
 * The inital tests were to insure that proper XML
 * is created on startup.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class DefaultScoringAlgorithmTest extends TestCase {
    
    private Log log = null;

    protected void setUp() throws Exception {
        super.setUp();
    
        log = new Log("DefaultScoringAlgorithmTest");
        StaticLog.setLog(log);
        
    }

    /**
     * Tests whether valid XML is generated with no data in the contest.
     */
    public void testNoData() {

        Contest contest = new Contest();

        checkOutputXML(contest);

    }

    /**
     * Initialize the contest.
     * 
     * Initialize with problems, languages, accounts, judgements.
     * 
     * @param contest
     */
    private void initFakeData(IContest contest) {

        // Add accounts
        contest.generateNewAccounts(ClientType.Type.TEAM.toString(), 1, true);
        contest.generateNewAccounts(ClientType.Type.TEAM.toString(), 1, true);
        
        contest.generateNewAccounts(ClientType.Type.JUDGE.toString(), 1, true);
        
        // Add Problem
        Problem problem = new Problem("Problem One");
        contest.addProblem(problem);
        
        // Add Language
        Language language = new Language("Language One");
        contest.addLanguage(language);
        
        String[] judgementNames = { "Yes", "No - compilation error", "No - incorrect output", "No - It's just really bad",
                "No - judges enjoyed a good laugh", "You've been bad - contact staff" };

        for (String judgementName : judgementNames) {
            Judgement judgement = new Judgement(judgementName);
            contest.addJudgement(judgement);
        }
    }
    
    private void initData(IContest contest, int numTeams, int numProblems) {

        // Add accounts
        contest.generateNewAccounts(ClientType.Type.TEAM.toString(), numTeams, true);
        contest.generateNewAccounts(ClientType.Type.JUDGE.toString(), 6, true);
        
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
    private Run getARun(IContest contest) {
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
    private Run getARun(IContest contest, int elapsedMinutes) {
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
     */
    public void testOneRunUnjudged() {

        Contest contest = new Contest();
        
        initFakeData(contest);
        Run run = getARun(contest);
        RunFiles runFiles = new RunFiles(run, "samps/Sumit.java");
        
        contest.addRun(run, runFiles, null);
        
        checkOutputXML(contest);
    }

    /**
     * Verify XML created for a single unjudged run.
     */
    public void testMixedjudged() {

        Contest contest = new Contest();
        
        initFakeData(contest);
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
     * Submit and judge a run.
     * 
     * @param contest
     * @param judgementIndex
     * @param solved
     */
    public void createJudgedRun (IContest contest, int judgementIndex, boolean solved) {
        Run run = getARun(contest);
        RunFiles runFiles = new RunFiles(run, "samps/Sumit.java");
        
        contest.addRun(run, runFiles, null);
        
        ClientId who = contest.getAccounts(ClientType.Type.JUDGE).firstElement().getClientId();
        run.setStatus(RunStates.BEING_JUDGED);
        contest.updateRun(run, who);
        
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
     */
    public void createJudgedRun (IContest contest, int judgementIndex, boolean solved, int elapsedMinutes){
        Run run = getARun(contest, elapsedMinutes);
        RunFiles runFiles = new RunFiles(run, "samps/Sumit.java");
        
        contest.addRun(run, runFiles, null);
        
        ClientId who = contest.getAccounts(ClientType.Type.JUDGE).firstElement().getClientId();
        run.setStatus(RunStates.BEING_JUDGED);
        contest.updateRun(run, who);
        
        Judgement judgement = contest.getJudgements()[judgementIndex]; // Judge as No
        
        JudgementRecord judgementRecord = new JudgementRecord(judgement.getElementId(), who, solved, false);
        contest.addRunJudgement(run, judgementRecord, null, who);
        
    }
    /**
     * Get XML from ScoringAlgorithm and test whether it can be parsed.
     * @param contest
     */
    public void checkOutputXML (IContest contest) {
        
        DefaultScoringAlgorithm defaultScoringAlgorithm = new DefaultScoringAlgorithm();
        String xmlString = defaultScoringAlgorithm.getStandings(contest, new Properties(), log);

        // getStandings should always return a well-formed xml
        assertFalse("getStandings returned null ", xmlString == null);
        assertFalse("getStandings returned empty string ", xmlString.trim().length() == 0);

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(xmlString)));

            String rootNodeName = document.getDocumentElement().getNodeName();

//            System.out.println("Root node is " + rootNodeName);
        } catch (Exception e) {
            assertTrue("Error in XML output " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    /**
     * Verify XML created for a single judged run.
     */
    public void testOneRunJudged() {

        Contest contest = new Contest();
        
        initFakeData(contest);
        
        createJudgedRun(contest, 2, false);

        checkOutputXML(contest);
    }
    
    /**
     * Verify XML created for 5 judged runs, one solved, four no's.
     */
    public void testFiveRunsJudged() {

        Contest contest = new Contest();
        
        initFakeData(contest);
        
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

    
    /**
     * Test based on data from boardtest.html.
     */
    public void testScoreboardCaseOne () {
        
        // RunID    TeamID  Prob    Time    Result
        
        String [] runsData = {
                "1,1,A,1,No",   
                "2,1,A,3,Yes", // With No on run 1, team 1 solved 1 23 penalty points.
                "3,1,A,5,No",
                "4,1,A,7,Yes",
                "5,1,A,9,No",
                "6,1,B,11,No",
                "7,1,B,13,No",
                "8,2,A,30,Yes",
                "9,2,B,35,No",
                "10,2,B,40,No",
                "11,2,B,45,No",
                "12,2,B,50,No",
                "13,2,B,55,No"
                };
        
        // Rank  TeamId Solved Penalty
        
        String [] rankData = {
                "1,team1,1,23",
                "2,team2,1,30"
        };

        Contest contest = new Contest();

        initData(contest, 2, 2);

        Problem[] problemList = contest.getProblems();
        Language languageId = contest.getLanguages()[0];

        Judgement yesJudgement = contest.getJudgements()[0];
        Judgement noJudgement = contest.getJudgements()[1];

        // get 5th judge
        ClientId judgeId = contest.getAccounts(Type.JUDGE).elementAt(4).getClientId();

        for (String runInfoLine : runsData) {

            String[] data = runInfoLine.split(",");

            int runId = getIntegerValue(data[0]);
            int teamId = getIntegerValue(data[1]);
            String probLet = data[2];
            int elapsed = getIntegerValue(data[3]);
            boolean solved = data[4].equals("Yes");

            int problemIndex = probLet.charAt(0) - 'A';
            Problem problem = problemList[problemIndex];
            ClientId clientId = new ClientId(contest.getSiteNumber(), Type.TEAM, teamId);

            // System.out.println(runInfoLine);
            // System.out.println(runId + " " + teamId + " " + probLet + " " + elapsed + " " + (solved ? "Yes" : "No"));

            Run run = new Run(clientId, languageId, problem);
            run.setNumber(runId);
            run.setElapsedMins(elapsed);
            ElementId judgementId = noJudgement.getElementId();
            if (solved) {
                judgementId = yesJudgement.getElementId();
            }
            JudgementRecord judgementRecord = new JudgementRecord(judgementId, judgeId, solved, false);
            contest.addRun(run);
            contest.addRunJudgement(run, judgementRecord, null, judgeId);
        }

        System.out.println();
        System.out.println("Contest Runs");

        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());
        for (Run run : runs) {
            System.out.println("debug2 run " + run.getNumber() + "," + run.getSubmitter().getClientNumber() + "," + contest.getProblem(run.getProblemId()) + "," + run.getElapsedMins() + ","
                    + run.isSolved()+" judged "+run.isJudged());
        }

        checkOutputXML(contest);

        confirmRanks(contest, rankData);
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
    private void confirmRanks(Contest contest, String[] rankData) {
        
        // Rank  Solved Penalty TeamId
        
        DefaultScoringAlgorithm defaultScoringAlgorithm = new DefaultScoringAlgorithm();
        String xmlString = defaultScoringAlgorithm.getStandings(contest, new Properties(), log);
        
//        System.out.println("debug XML is "+xmlString);

        // getStandings should always return a well-formed xml
        assertFalse("getStandings returned null ", xmlString == null);
        assertFalse("getStandings returned empty string ", xmlString.trim().length() == 0);
        
        Document document = null;

        try {
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
                compareRanking (rankIndex+1, standingsRow, cols);
                rankIndex++;
            }
        }
        
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
        assertTrue ("Standings row "+rankIndex+" rank wrong expected "+expectedRow[idx]+" found "+standingsRow[idx], standingsRow[idx].equals(expectedRow[idx]));
        idx++;
        assertTrue ("Standings row "+rankIndex+" name wrong expected "+expectedRow[idx]+" found "+standingsRow[idx], standingsRow[idx].equals(expectedRow[idx]));
        idx++;
        assertTrue ("Standings row "+rankIndex+" number solved wrong expected "+expectedRow[idx]+" found "+standingsRow[idx], standingsRow[idx].equals(expectedRow[idx]));
        idx++;
        assertTrue ("Standings row "+rankIndex+" points wrong expected "+expectedRow[idx]+" found "+standingsRow[idx], standingsRow[idx].equals(expectedRow[idx]));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
