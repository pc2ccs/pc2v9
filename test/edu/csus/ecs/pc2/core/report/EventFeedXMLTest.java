package edu.csus.ecs.pc2.core.report;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * Test Event Feed XML.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeedXMLTest extends TestCase {

    private final boolean debugMode = false;

    private IInternalContest contest = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        SampleContest sample = new SampleContest();
        contest = sample.createContest(1, 1, 22, 12, true);

        /**
         * Add random runs
         */

        Run[] runs = sample.createRandomRuns(contest, 12, true, true, true);

        Group group1 = new Group("Mississippi");
        group1.setGroupId(1024);
        contest.addGroup(group1);

        Group group2 = new Group("Arkansas");
        group2.setGroupId(2048);
        contest.addGroup(group2);

        Account[] teams = getTeamAccounts();

        assignTeamGroup(group1, 0, teams.length / 2);
        assignTeamGroup(group2, teams.length / 2, teams.length - 1);

        /**
         * Add Run Judgements.
         */
        ClientId judgeId = contest.getAccounts(Type.JUDGE).firstElement().getClientId();
        Judgement judgement;
        String sampleFileName = sample.getSampleFile();

        for (Run run : runs) {
            RunFiles runFiles = new RunFiles(run, sampleFileName);

            contest.acceptRun(run, runFiles);

            run.setElapsedMins((run.getNumber() - 1) * 9);

            judgement = sample.getRandomJudgement(contest, run.getNumber() % 2 == 0); // ever other run is judged Yes.
            sample.addJudgement(contest, run, judgement, judgeId);
        }
    }

    /**
     * Assign group to team startIdx to endIdx.
     * 
     * @param group
     * @param startIdx
     * @param endIdx
     */
    private void assignTeamGroup(Group group, int startIdx, int endIdx) {
        Account[] teams = getTeamAccounts();
        for (int i = startIdx; i < endIdx; i++) {
            teams[i].setGroupId(group.getElementId());
        }
    }

    /**
     * Return list of accounts sorted by team id.
     * @return
     */
    private Account[] getTeamAccounts() {
        Vector<Account> teams = contest.getAccounts(Type.TEAM);
        Account[] accounts = (Account[]) teams.toArray(new Account[teams.size()]);
        Arrays.sort(accounts, new AccountComparator());
        return accounts;
    }

    public void testContestElement() throws Exception {

        // TODO tag: contest>
        
        EventFeedXML eventFeedXML = new EventFeedXML();

        InternalContest internalContest = new InternalContest();
        
        /**
         * Check info tag, if there is no contest data in InternalContest.
         */
        String xml = toContestXML(eventFeedXML.createInfoElement(internalContest, null));

        if (debugMode){
            System.out.println(" -- testContestElement info tag ");
            System.out.println(xml);
            System.out.println();
        }
        
        System.out.println(" -- testContestElement info tag ");
        System.out.println(xml);
        System.out.println();
            
        /**
         * Check complete EventFeed XML,  if there is no contest data in InternalContest.
         */
        xml = eventFeedXML.toXML(internalContest);

        if (debugMode){
            System.out.println(" -- testContestElement info tag ");
            System.out.println(xml);
            System.out.println();
        }
        
        System.out.println(" -- testContestElement info tag ");
        System.out.println(xml);
        System.out.println();

    }

    /**
     * Test <info> tag.
     * 
     * @throws Exception
     */
    public void testInfoElement() throws Exception {

        EventFeedXML eventFeedXML = new EventFeedXML();

        String xml = toContestXML(eventFeedXML.createInfoElement(contest, null));

        if (debugMode){
            System.out.println(" -- testInfoElement ");
            System.out.println(xml);

            System.out.println();
        }

        contest.startContest(1);
        xml = toContestXML(eventFeedXML.createInfoElement(contest, null));

        if (debugMode){
            System.out.println(xml);
        }
    }

    public void testLanguageElement() throws Exception {

        // TODO tag: language>

        if (debugMode){
            System.out.println(" -- testLanguageElement ");
        }

        EventFeedXML eventFeedXML = new EventFeedXML();
        int idx = 1;
        for (Language language : contest.getLanguages()) {
            String xml = toContestXML(eventFeedXML.createElement(contest, language, idx));
            if (debugMode){
                System.out.println(xml);
            }
            idx++;
        }
    }

    /**
     * Create Contest XML.
     * 
     * @param memento
     * @return
     */
    private String toContestXML(XMLMemento mementoRoot) {
        try {
            return mementoRoot.saveToString(true);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
            return null;
        }
    }

    public void testRegionElement() throws Exception {

        // TODO tag: region

        if (debugMode){
            System.out.println(" -- testRegionElement ");
        }

        Group[] groups = contest.getGroups();
        Arrays.sort(groups, new GroupComparator());

        EventFeedXML eventFeedXML = new EventFeedXML();
        for (Group group : groups) {
            String xml = toContestXML(eventFeedXML.createElement(contest, group));
            if (debugMode){
                System.out.println(xml);
            }
        }
    }

    public void testJudgementElement() throws Exception {

        if (debugMode){
            System.out.println(" -- testJudgementElement ");
        }

        EventFeedXML eventFeedXML = new EventFeedXML();
        for (Judgement judgement : contest.getJudgements()) {
            String xml = toContestXML(eventFeedXML.createElement(contest, judgement));
            if (debugMode){
                System.out.println(xml);
            }
        }
    }

    public void testProblemElement() throws Exception {

        // TODO tag: problem id="1" state="enabled">

        if (debugMode){
            System.out.println(" -- testProblemElement ");
        }

        EventFeedXML eventFeedXML = new EventFeedXML();
        int idx = 1;
        for (Problem problem : contest.getProblems()) {
            String xml = toContestXML(eventFeedXML.createElement(contest, problem, idx));
            if (debugMode){
                System.out.println(xml);
            }
            idx++;
        }
    }

    public void testTeamElement() throws Exception {
        // TODO tag: team id="1" external-id="23412">

        if (debugMode){
            System.out.println(" -- testTeamElement ");
        }

        EventFeedXML eventFeedXML = new EventFeedXML();

        Account[] accounts = getTeamAccounts();

        for (Account account : accounts) {
            String xml = toContestXML(eventFeedXML.createElement(contest, account));
            if (debugMode){
                System.out.println(xml);
            }
        }
    }

    public void testClarElement() throws Exception {

        // TODO tag: clar id="1" team-id="0" problem-id="1">

        if (debugMode){
            System.out.println(" -- testClarElement ");
        }

        EventFeedXML eventFeedXML = new EventFeedXML();
        Clarification[] clarifications = contest.getClarifications();
        Arrays.sort(clarifications, new ClarificationComparator());

        for (Clarification clarification : clarifications) {
            String xml = toContestXML(eventFeedXML.createElement(contest, clarification));
            if (debugMode){
                System.out.println(xml);
            }
        }
    }

    public void testRunElement() throws Exception {

        if (debugMode){
            System.out.println(" -- testRunElement ");
        }

        // TODO tag: run id="1410" team-id="74" problem-id="4">
        EventFeedXML eventFeedXML = new EventFeedXML();
        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        for (Run run : runs) {
            String xml = toContestXML(eventFeedXML.createElement(contest, run));
            if (debugMode){
                System.out.println(xml);
            }
        }
    }

    public void testTestcaseElement() throws Exception {

        // TODO tag: testcase run-id="1">
        System.out.println(" -- testTestcaseElement TODO "); // TODO code testcase
    }

    public void testFinalizedElement() throws Exception {

        // TODO tag: finalized>
        EventFeedXML eventFeedXML = new EventFeedXML();
        String xml = eventFeedXML.createFinalizeXML(contest);
        if (debugMode){
            System.out.println(" -- testFinalizedElement ");
            System.out.println(xml);
        }
    }

    public void testStartupElement() throws Exception {


        EventFeedXML eventFeedXML = new EventFeedXML();
        String xml = eventFeedXML.createStartupXML(contest);

        if (debugMode){
            System.out.println(" -- testStartupElement ");
            System.out.println(xml);
        }
    }

    public void testToXML() throws Exception {

        EventFeedXML eventFeedXML = new EventFeedXML();
        String xml = eventFeedXML.toXML(contest);
        if (debugMode){
            System.out.println(" -- testToXML ");
            System.out.println(xml);
        }

    }
}

