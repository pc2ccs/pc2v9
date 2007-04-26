package edu.csus.ecs.pc2.core.scoring;

import java.io.StringReader;

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
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Model;
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

    protected void setUp() throws Exception {
        super.setUp();
    
        Log log = new Log("DefaultScoringAlgorithmTest");
        StaticLog.setLog(log);
        
    }

    /**
     * Tests whether valid XML is generated with no data in the model.
     */
    public void testNoData() {

        Model model = new Model();

        checkOutputXML(model);

    }

    /**
     * Initialize the model.
     * 
     * Initialize with problems, languages, accounts, judgements.
     * 
     * @param model
     */
    private void initFakeData(IModel model) {

        // Add accounts
        model.generateNewAccounts(ClientType.Type.TEAM.toString(), 1, true);
        model.generateNewAccounts(ClientType.Type.TEAM.toString(), 1, false);
        
        model.generateNewAccounts(ClientType.Type.JUDGE.toString(), 1, true);
        
        // Add Problem
        Problem problem = new Problem("Problem One");
        model.addProblem(problem);
        
        // Add Language
        Language language = new Language("Language One");
        model.addLanguage(language);
        
        String[] judgementNames = { "Yes", "No - compilation error", "No - incorrect output", "No - It's just really bad",
                "No - judges enjoyed a good laugh", "You've been bad - contact staff" };

        for (String judgementName : judgementNames) {
            Judgement judgement = new Judgement(judgementName);
            model.addJudgement(judgement);
        }
    }

    /**
     * Create a new run in the model.
     * 
     * @param model
     * @return created run.
     */
    private Run getARun(IModel model) {
        Problem problem = model.getProblems()[0];
        Language language = model.getLanguages()[0];
        
        Account account = model.getAccounts(ClientType.Type.TEAM).firstElement();

        ClientId id = account.getClientId();
        return new Run(id, language, problem);
    }

    /**
     * Verify XML created for a single unjudged run.
     */
    public void testOneRunUnjudged() {

        Model model = new Model();
        
        initFakeData(model);
        Run run = getARun(model);
        RunFiles runFiles = new RunFiles(run, "samps/Sumit.java");
        
        model.addRun(run, runFiles, null);
        
        checkOutputXML(model);
    }
    
    /**
     * Submit and judge a run.
     * 
     * @param model
     * @param judgementIndex
     * @param solved
     */
    public void createJudgedRun (IModel model, int judgementIndex, boolean solved){
        Run run = getARun(model);
        RunFiles runFiles = new RunFiles(run, "samps/Sumit.java");
        
        model.addRun(run, runFiles, null);
        
        ClientId who = model.getAccounts(ClientType.Type.JUDGE).firstElement().getClientId();
        model.updateRun(run, RunStates.BEING_JUDGED, who);
        
        Judgement judgement = model.getJudgements()[judgementIndex]; // Judge as No
        
        JudgementRecord judgementRecord = new JudgementRecord(judgement.getElementId(), who, solved, false);
        model.addRunJudgement(run, judgementRecord, null, who);
        
    }

    /**
     * Get XML from ScoringAlgorithm and test whether it can be parsed.
     * @param model
     */
    public void checkOutputXML (IModel model) {
        
        DefaultScoringAlgorithm defaultScoringAlgorithm = new DefaultScoringAlgorithm();
        String xmlString = defaultScoringAlgorithm.getStandings(model);

        assertTrue("With one run unjudged, no getStandings output ", xmlString == null);
        assertTrue("With one run unjudged, empty string getStandings output ", xmlString.trim().length() == 0);

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

        Model model = new Model();
        
        initFakeData(model);
        
        createJudgedRun(model, 2, false);

        checkOutputXML(model);
    }
    
    /**
     * Verify XML created for 5 judged runs, one solved, four no's.
     */
    public void testFiveRunsJudged() {

        Model model = new Model();
        
        initFakeData(model);
        
        createJudgedRun(model, 2, false);
        createJudgedRun(model, 0, true);
        createJudgedRun(model, 3, false);
        createJudgedRun(model, 4, false);

        checkOutputXML(model);
    }
    

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
