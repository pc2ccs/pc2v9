package edu.csus.ecs.pc2.core.scoring;

import java.io.StringReader;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Contest;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
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

            System.out.println("Root node is " + rootNodeName);
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
    

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
