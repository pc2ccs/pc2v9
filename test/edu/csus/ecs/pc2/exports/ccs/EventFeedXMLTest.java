package edu.csus.ecs.pc2.exports.ccs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.BalloonDeliveryComparator;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonDeliveryInfo;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformation.TeamDisplayMask;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.RunTestCase;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.core.util.NotificationUtilities;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * Test Event Feed XML.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeedXMLTest extends AbstractTestCase {

    private static final String CONTEST_END_TAG = "</contest>";

    private static final String CONTEST_START_TAG = "<contest>";

    private final boolean debugMode = false;

    private IInternalContest contest = null;
    
    private SampleContest sample = new SampleContest();
    
    private NotificationUtilities notificationUtilities = new NotificationUtilities();

    private boolean exitingServer = false;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        int siteNumber = 1;
        contest = sample.createContest(siteNumber, 1, 22, 12, true);
        
        addContestInfo (contest, "Contest Title");
        
        Group group1 = new Group("Mississippi");
        group1.setGroupId(1024);
        contest.addGroup(group1);

        Group group2 = new Group("Arkansas");
        group2.setGroupId(2048);
        contest.addGroup(group2);

        Account[] teams = getTeamAccounts(contest);

        assignTeamGroup(group1, 0, teams.length / 2);
        assignTeamGroup(group2, teams.length / 2, teams.length - 1);

        /**
         * Add random runs
         */
        
        Run[] runs = sample.createRandomRuns(contest, 12, true, true, true);
        
        /**
         * Add Run Judgements.
         */
        addRunJudgements(contest, runs);
     
    }
 
    private void addRunJudgements (IInternalContest inContest, Run[] runs) throws Exception {
        addRunJudgements(inContest, runs, 0);
    }
    
    private void addRunJudgements (IInternalContest inContest, Run[] runs, int numberOfTestCases) throws Exception {

        ClientId judgeId = inContest.getAccounts(Type.JUDGE).firstElement().getClientId();
        Judgement judgement;
        String sampleFileName = sample.getSampleFile();

        for (Run run : runs) {
            RunFiles runFiles = new RunFiles(run, sampleFileName);

            inContest.acceptRun(run, runFiles);

            run.setElapsedMins((run.getNumber() - 1) * 9);

            judgement = sample.getRandomJudgement(inContest, run.getNumber() % 2 == 0); // ever other run is judged Yes.
            sample.addJudgement(inContest, run, judgement, judgeId);
            sample.addTestCase (inContest,run, numberOfTestCases);
        }
    }

    private void addContestInfo(IInternalContest contest2, String title) {
        ContestInformation info = new ContestInformation();
        info.setContestTitle(title);
        info.setContestURL("http://pc2.ecs.csus.edu/pc2");
        info.setTeamDisplayMode(TeamDisplayMask.LOGIN_NAME_ONLY);

        contest.addContestInformation(info);
    }

    /**
     * Assign group to team startIdx to endIdx.
     * 
     * @param group
     * @param startIdx
     * @param endIdx
     */
    private void assignTeamGroup(Group group, int startIdx, int endIdx) {
        Account[] teams = getTeamAccounts(contest);
        for (int i = startIdx; i < endIdx; i++) {
            teams[i].setGroupId(group.getElementId());
        }
    }

    /**
     * Return list of accounts sorted by team id.
     * @return
     */
    private Account[] getTeamAccounts(IInternalContest inContest) {
        Vector<Account> teams = inContest.getAccounts(Type.TEAM);
        Account[] accounts = (Account[]) teams.toArray(new Account[teams.size()]);
        Arrays.sort(accounts, new AccountComparator());
        return accounts;
    }

    public void testContestElement() throws Exception {

        EventFeedXML eventFeedXML = new EventFeedXML();

        InternalContest internalContest = new InternalContest();
        
        ContestInformation info = new ContestInformation();
        info.setContestTitle("Title One");
        
        /**
         * Check info tag, if there is no contest data in InternalContest.
         */
        String xml = toContestXML(eventFeedXML.createInfoElement(internalContest, info));
        assertNotNull("Should create contest element", xml);
        testForValidXML (xml);

        if (debugMode){
            System.out.println(" -- testContestElement info tag ");
            System.out.println(xml);
            System.out.println();
        }
            
        /**
         * Check complete EventFeed XML,  if there is no contest data in InternalContest.
         */
        xml = eventFeedXML.toXML(internalContest);

        if (debugMode){
            System.out.println(" -- testContestElement info tag ");
            System.out.println(xml);
            System.out.println();
        }
        testForValidXML (xml);

        assertXMLCounts(xml, EventFeedXML.CONTEST_TAG, 1);
        assertXMLCounts(xml, EventFeedXML.INFO_TAG, 1);
        
    }

    /**
     * Test <info> tag.
     * 
     * @throws Exception
     */
    public void testInfoElement() throws Exception {

        EventFeedXML eventFeedXML = new EventFeedXML();
        
        ContestInformation info = new ContestInformation();
        info.setContestTitle("Title One");
        
        String xml = toContestXML(eventFeedXML.createInfoElement(contest, info));

        if (debugMode){
            System.out.println(" -- testInfoElement ");
            System.out.println(xml);

            System.out.println();
        }

        contest.startContest(1);
        xml = toContestXML(eventFeedXML.createInfoElement(contest, info));

        if (debugMode){
            System.out.println(xml);
        }
        
        testForValidXML (xml);
    }

    public void testLanguageElement() throws Exception {

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
            testForValidXML (xml);
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
            testForValidXML (xml);
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
            testForValidXML (xml);
        }
    }
    
    public void testCreateBalloonElement() throws Exception {

        if (debugMode){
            System.out.println(" -- testCreateBalloonElement ");
        }
        
        EventFeedXML eventFeedXML = new EventFeedXML();
        for (Problem problem : contest.getProblems()) {
            String xml = toContestXML(eventFeedXML.createBalloonElement(contest, problem));
            assertNotNull ("Failed to create balloon element for "+problem, xml);
            if (debugMode){
                System.out.println(xml);
            }
            testForValidXML (xml);
        }
    }
    
    private Run [] getSortedRuns() {
        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());
        return runs;
    }
    
    public void testNotification() throws Exception {

        // Create notifications

        addNotifications(contest);
        
        /**
         * Solved runs is used to only count one solved run per team and problem.
         */
        Hashtable<String, Run>solvedRuns = new Hashtable<String, Run>();

        int solved = 0;
        for (Run run : getSortedRuns()) {
            if (run.isSolved()) {
                assertTrue("Expecting Notification for " + run, notificationUtilities.alreadyHasNotification(contest, run));
                String key = run.getSubmitter().getTripletKey()+":"+run.getProblemId();
                solvedRuns.put(key, run);
            }
        }

        EventFeedXML evenFeedXML = new EventFeedXML();

        BalloonDeliveryInfo[] deliveries = notificationUtilities.getBalloonDeliveries(contest);
        Arrays.sort(deliveries, new BalloonDeliveryComparator(contest));
        int notificationSequenceNumber = 1;
        
        solved = solvedRuns.keySet().size();
        
        assertEquals("Expected notifications for all solved", solved, deliveries.length);
        
        for (BalloonDeliveryInfo balloonDeliveryInfo : deliveries) {

            String xml = toContestXML(evenFeedXML.createElement(contest, balloonDeliveryInfo, notificationSequenceNumber));
            
            if (debugMode){
                System.out.println(xml);
            }
            testForValidXML (xml);
            notificationSequenceNumber++;
        }
        
        assertEquals("Expected notifification for all solved.", notificationSequenceNumber-1, deliveries.length);
    }
    
    /**
     * For all solved runs insure that each has a notification.
     * 
     * @param contest2
     * @return
     */
    private int addNotifications(IInternalContest contest2) {
        
        int count = 0;
        
        for (Run run : getSortedRuns()) {
            if (run.isSolved()) {

                if (!notificationUtilities.alreadyHasNotification(contest2, run)) {
                    count++;
                    createNotification(contest2, run);
                }
            }
        }
        
        return count;
    }
    
    
    /**
     * Create a notification if needed.
     * 
     * Checks for existing notification, only creates
     * a notification if no notification exists.
     * 
     * @param contest2
     * @param run
     */
    private void createNotification(IInternalContest contest2, Run run) {

        BalloonDeliveryInfo info = notificationUtilities.getNotification(contest2, run);

        if (info == null) {
            /**
             * Only create notification if needed.
             */
            notificationUtilities.addNotification(contest2, run);
            assertNotNull("Expecting a delivery info",notificationUtilities.getNotification(contest2, run));
        }
    }

    @SuppressWarnings("unused")
    private String toString(BalloonDeliveryInfo info) {
        return "Notification: "+info.getKey()+" "+info.getTimeSent();
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
            if (debugMode) {
                System.out.println(xml);
            }
            testForValidXML(xml);
            assertXMLCounts(xml, EventFeedXML.PROBLEM_TAG, 1);

            if (idx == 1) {
                assertXMLNodeValueEquals(xml, "label", "A");
                assertXMLNodeValueEquals(xml, "name", "Sumit");
            }

            if (idx == 6) {
                assertXMLNodeValueEquals(xml, "label", "F");
                assertXMLNodeValueEquals(xml, "name", "Finnigans Bluff");
            }

            idx++;
        }
    }

    public void testTeamElement() throws Exception {
        if (debugMode){
            System.out.println(" -- testTeamElement ");
        }

        EventFeedXML eventFeedXML = new EventFeedXML();

        Account[] accounts = getTeamAccounts(contest);

        for (Account account : accounts) {
            String xml = toContestXML(eventFeedXML.createElement(contest, account));
            if (debugMode){
                System.out.println(xml);
            }
            testForValidXML (xml);
        }
    }

    public void testClarElement() throws Exception {

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
            testForValidXML (xml);
        }
    }

    public void testRunElement() throws Exception {

        if (debugMode){
            System.out.println(" -- testRunElement ");
        }

        EventFeedXML eventFeedXML = new EventFeedXML();
        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());

        for (Run run : runs) {
            String xml = toContestXML(eventFeedXML.createElement(contest, run, true));
            if (debugMode){
                System.out.println(xml);
            }
            testForValidXML (xml);
        }
    }

    public void testTestcaseElement() throws Exception {

        // TODO CCS tag: testcase run-id="1">
    }

    public void testFinalizedElement() throws Exception {

        EventFeedXML eventFeedXML = new EventFeedXML();
        
        FinalizeData data = new FinalizeData();
        data.setGoldRank(8);
        data.setBronzeRank(20);
        data.setSilverRank(16);
        data.setComment("Finalized by the Ultimiate Finalizer role");
        
        String xml = eventFeedXML.createFinalizeXML(contest, data);
        if (debugMode){
            System.out.println(" -- testFinalizedElement ");
            System.out.println(xml);
        }

        testForValidXML (CONTEST_START_TAG + xml);
    }

    public void testStartupElement() throws Exception {

        EventFeedXML eventFeedXML = new EventFeedXML();
        String xml = eventFeedXML.createStartupXML(contest);

        if (debugMode){
            System.out.println(" -- testStartupElement ");
            System.out.println(xml);
        }
        xml = xml + CONTEST_END_TAG;
        testForValidXML (xml);
                
        assertXMLCounts(xml, EventFeedXML.CONTEST_TAG, 1);
        assertXMLCounts(xml, EventFeedXML.INFO_TAG, 1);
        assertXMLCounts(xml, EventFeedXML.JUDGEMENT_TAG, 9);
        assertXMLCounts(xml, EventFeedXML.REGION_TAG, 24);

    }

    
    /**
     * Print counts in xml string for EventFeed elements.
     * 
     * @param comment
     * @param xmlString
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public void printElemntCounts(String comment, String xmlString) throws ParserConfigurationException, SAXException, IOException {

        // printElemntCounts(getName(), xml + "</contest>");

        System.out.println("printElemntCounts " + comment);

        String[] tagnames = getAllEventFeedTagNames();
        
        Arrays.sort(tagnames);
        for (String tagName : tagnames) {
            System.out.println(tagName + " count = " + getTagCount(xmlString, tagName));
        }
    }
    
    public String [] getAllEventFeedTagNames (){
        String[] tagnames = { //
        EventFeedXML.CONTEST_TAG, EventFeedXML.INFO_TAG, EventFeedXML.REGION_TAG, EventFeedXML.PROBLEM_TAG, EventFeedXML.LANGUAGE_TAG, EventFeedXML.TEAM_TAG, EventFeedXML.CLARIFICATION_TAG,
                EventFeedXML.TESTCASE_TAG, EventFeedXML.RUN_TAG, EventFeedXML.JUDGEMENT_TAG, EventFeedXML.FINALIZE_TAG, EventFeedXML.JUDGEMENT_RECORD_TAG, EventFeedXML.BALLOON_TAG,
                EventFeedXML.BALLOON_LIST_TAG, EventFeedXML.NOTIFICATION_TAG
        //
        };
        return tagnames;
    }

    public void testToXML() throws Exception {

        EventFeedXML eventFeedXML = new EventFeedXML();
        String xml = eventFeedXML.toXML(contest);
        if (debugMode){
            System.out.println(" -- testToXML ");
            System.out.println(xml);
        }
        testForValidXML (xml);
    }
    
    protected void startEventFeed(int port) throws IOException {

        ServerSocket server = new ServerSocket(port);

        /**
         * Check info tag, if there is no contest data in InternalContest.
         */
        EventFeedXML eventFeedXML = new EventFeedXML();
        String xml = eventFeedXML.toXML(contest);

        System.out.println("Opened socket on port " + port);

        while (! exitingServer) {

            try {
                Socket connection = server.accept();
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                out.write(xml);
                out.write("<!-- end of stream -->");
                out.flush();
                connection.close();
                System.out.println("Opened and output sample Event Feed");
            } catch (Exception e) {
                e.printStackTrace();
                server.close();
            }
        }
        
        server = null;

    }

    public void testTestCase() throws Exception {
        
        EventFeedXML eventFeedXML = new EventFeedXML();
        
        int siteNumber = 2;
        
         IInternalContest testCaseContest = sample.createContest(siteNumber, 1, 22, 12, true);
        
        /**
         * Add random runs
         */
        
        Run[] runs = sample.createRandomRuns(testCaseContest, 12, true, true, true);
        
        createDataFilesForContest (testCaseContest);
        
        /**
         * Add Run Judgements.
         */
        addRunJudgements(testCaseContest, runs, 5);
        
        int expectedTeamTags = getTeamAccounts(testCaseContest).length  + testCaseContest.getRuns().length;
        
        String xml = eventFeedXML.toXML(testCaseContest);
        if (debugMode){
            System.out.println(" -- testTestCase ");
            System.out.println(xml);
        }
        testForValidXML (xml);
        
        assertXMLCounts(xml, EventFeedXML.CONTEST_TAG, 1);
        assertXMLCounts(xml, EventFeedXML.INFO_TAG, 1);
        assertXMLCounts(xml, EventFeedXML.JUDGEMENT_TAG, 9);
        assertXMLCounts(xml, EventFeedXML.LANGUAGE_TAG, 18);
        assertXMLCounts(xml, EventFeedXML.NOTIFICATION_TAG, 0);
        assertXMLCounts(xml, EventFeedXML.PROBLEM_TAG, 18);
        assertXMLCounts(xml, EventFeedXML.REGION_TAG, 22);
        assertXMLCounts(xml, EventFeedXML.RUN_TAG, 12);
        assertXMLCounts(xml, EventFeedXML.TEAM_TAG, expectedTeamTags);
        assertXMLCounts(xml, EventFeedXML.TESTCASE_TAG, 12 * 5); 

        /**
         * Test FINALIZE
         */
        
        FinalizeData data = new FinalizeData();
        data.setCertified(true);
        data.setComment(getName()+" test");
        testCaseContest.setFinalizeData(data);
        
        xml = eventFeedXML.toXML(testCaseContest);
        
        testForValidXML (xml);
        
        assertXMLCounts(xml, EventFeedXML.FINALIZE_TAG, 1);
        assertXMLCounts(xml, "comment", 1);
        

        for (Run run : runs) {
            run.setElapsedMins(100); /// set all runs to elapsed time 100
        }
        
        int numruns = 5;
        
        for (int i = runs.length - numruns; i < runs.length; i++) {
            runs[i].setElapsedMins(300); /// set all runs to elapsed time 100
            RunTestCase[] testCases = runs[i].getRunTestCases();
            for (RunTestCase runTestCase : testCases) {
                runTestCase.setElapsedMS(300 * Constants.MS_PER_MINUTE);
            }
        }
        
         Filter filter = new Filter();
         filter.setStartElapsedTime(200);
        xml = eventFeedXML.toXML(testCaseContest, filter);
        
        assertXMLCounts(xml, EventFeedXML.RUN_TAG, numruns);
        assertXMLCounts(xml, EventFeedXML.TESTCASE_TAG, numruns * 5); 

    }

    /**
     * Test to ensure that DISPLAY_ON_SCOREBOARD teams are not present in the event feed.
     * 
     * @throws Exception
     */
    public void testTeamsNotOnScoreboard() throws Exception {
        
        EventFeedXML eventFeedXML = new EventFeedXML();
        
        int siteNumber = 2;
        
         IInternalContest testCaseContest = sample.createContest(siteNumber, 1, 22, 12, true);
        
        /**
         * Add random runs
         */
        
        Run[] runs = sample.createRandomRuns(testCaseContest, 12, true, true, true);
        
        createDataFilesForContest (testCaseContest);
        
        /**
         * Add Run Judgements.
         */
        addRunJudgements(testCaseContest, runs, 5);
        
        int expectedTeamTags = getTeamAccounts(testCaseContest).length  + testCaseContest.getRuns().length;

        String xml = eventFeedXML.toXML(testCaseContest);
        
        if (debugMode){
            System.out.println(" -- testTestCase ");
            System.out.println(xml);
        }
        testForValidXML (xml);
        
        assertXMLCounts(xml, EventFeedXML.CONTEST_TAG, 1);
        assertXMLCounts(xml, EventFeedXML.INFO_TAG, 1);
        assertXMLCounts(xml, EventFeedXML.JUDGEMENT_TAG, 9);
        assertXMLCounts(xml, EventFeedXML.LANGUAGE_TAG, 18);
        assertXMLCounts(xml, EventFeedXML.NOTIFICATION_TAG, 0);
        assertXMLCounts(xml, EventFeedXML.PROBLEM_TAG, 18);
        assertXMLCounts(xml, EventFeedXML.REGION_TAG, 22);
        assertXMLCounts(xml, EventFeedXML.RUN_TAG, 12);
        assertXMLCounts(xml, EventFeedXML.TEAM_TAG, expectedTeamTags);
        assertXMLCounts(xml, EventFeedXML.TESTCASE_TAG, 12 * 5);
        
        Vector<Account> vector = testCaseContest.getAccounts(Type.TEAM);
        Account[] accounts = (Account[]) vector.toArray(new Account[vector.size()]);
        Arrays.sort(accounts,new AccountComparator());
        
        int dontShowCount = 7;
        
        assertEquals("Expecting number of runs ", 12, testCaseContest.getRuns().length);
        
        /**
         * Team 16 - Team 22 should not show on event feed.
         */
        
        for (int i = 0; i < dontShowCount; i++) {
            Account account = accounts[accounts.length - 1 - i];
            account.removePermission(edu.csus.ecs.pc2.core.security.Permission.Type.DISPLAY_ON_SCOREBOARD);
            testCaseContest.updateAccount(account);
            runs = sample.createRandomRuns(testCaseContest, 1, account.getClientId(), false, false, false, account.getClientId().getSiteNumber());
            assertTrue("Expecting one run to be created", runs.length == 1);
            testCaseContest.addRun(runs[0]);
        }
        
        int numberTeamsToDisplay = getNumberOfTeamsTodisplay(testCaseContest);
        
        int numberOfRunsToDisplay = getNumberOfRunsToDisplay (testCaseContest);
        
        assertEquals ("Expeced teams to be displayed/included", getTeamAccounts(testCaseContest).length - dontShowCount, numberTeamsToDisplay);
        
        xml = eventFeedXML.toXML(testCaseContest);
        assertXMLCounts(xml, EventFeedXML.TEAM_TAG, numberTeamsToDisplay + numberOfRunsToDisplay);
        assertXMLCounts(xml, EventFeedXML.RUN_TAG, numberOfRunsToDisplay);
    }
    
    private int getNumberOfRunsToDisplay(IInternalContest testCaseContest) {
        Run[] runs = testCaseContest.getRuns();
        int count = 0;
        for (Run run : runs) {
            if (teamDisplayedOnScoreboard(testCaseContest, run.getSubmitter())){
                count ++;
            }
        }
        return count;
    }

    private boolean teamDisplayedOnScoreboard(IInternalContest inContest, ClientId clientId) {
        return inContest.isAllowed(clientId, Permission.Type.DISPLAY_ON_SCOREBOARD);
    }

    
    private int getNumberOfTeamsTodisplay(IInternalContest testCaseContest) {
        Account[] accounts = getTeamAccounts(testCaseContest);
        int count = 0;
        for (Account account : accounts) {
            if (teamDisplayedOnScoreboard(testCaseContest, account.getClientId())){
                count ++;
            }
        }
        return count;
    }

    public void viewString(String xml) throws IOException {
        String [] lines = xml.split("\n");
        File file = writeTempFile(lines);
        editFile(file.getAbsolutePath());
    }

    /**
     * A very simple
     * 
     * @param xml
     * @param string
     * @param i
     * @throws IOException
     * @throws Exception
     */
    private void assertXMLCounts(String xmlString, String string, int count) throws Exception {
        assertEquals("Expecting occurances (for" + string + ")", count, getTagCount(xmlString, string));
    }
    
    /**
     * Finds name in xml string, compares node/element values against expectedValue.
     * @param xmlString
     * @param name XML tag name
     * @param expectedValue 
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private void assertXMLNodeValueEquals (String xmlString, String name, String expectedValue) throws ParserConfigurationException, SAXException, IOException{
        Document document = getDocument(xmlString);
        NodeList nodes = document.getElementsByTagName(name);
        if (nodes.getLength() != 1){
            System.out.println("xml = "+xmlString);
            fail("Expecting to find nodes for name "+name);
        }
        
//        System.out.println("Nodes length = "+nodes.getLength());
//        for (int i = 0; i < nodes.getLength(); i++) {
//            Node node = nodes.item(i);
//            System.out.println("Looking for "+name+", found node "+node.getNodeName()+" "+node.getNodeValue()+" child is: "+node.getChildNodes().item(0).getNodeValue());
//        }
        
        String childValue = nodes.item(0).getChildNodes().item(0).getNodeValue();
        if (! expectedValue.equals(childValue)){
            System.out.println("xml = "+xmlString);
            assertEquals("Expecting value for "+name, expectedValue, childValue);
        }
    }
    
    private int getTagCount(String xmlString, String string) throws ParserConfigurationException, SAXException, IOException {

        Document document = getDocument(xmlString);
        NodeList nodes = document.getElementsByTagName(string);
        return nodes.getLength();
    }

    /**
     * Creates a single sample testcase (data and answer file) for each problem.
     * 
     * @param inContest
     * @throws FileNotFoundException
     */
    private void createDataFilesForContest(IInternalContest inContest) throws FileNotFoundException {

        Problem[] problems = inContest.getProblems();
        for (Problem problem : problems) {

            int numProblemDataFiles = problem.getNumberTestCases();

            if (numProblemDataFiles < 1) {

                String shortname = problem.getShortName();
                if (shortname == null) {
                    shortname = problem.getLetter();
                }
                String filename = getOutputTestFilename(shortname + ".dat");
                String answerName = getOutputTestFilename(shortname + ".ans");

                if (new File(filename).exists()) {
                    if (debugMode){
                        System.out.println("Data file exists: " + filename);
                    }
                } else {
                    ensureOutputDirectory();
                    createSampleDataFile(filename);
                    createSampleAnswerFile(answerName);
                }

                ProblemDataFiles dataFiles = new ProblemDataFiles(problem);
                dataFiles.setJudgesDataFile(new SerializedFile(filename));
                dataFiles.setJudgesAnswerFile(new SerializedFile(answerName));

                problem.setDataFileName(shortname + ".dat");
                problem.setAnswerFileName(shortname + ".ans");

                inContest.updateProblem(problem, dataFiles);

                assertEquals("Expecting test data set", 1, problem.getNumberTestCases());
            }
        }
    }
    
    
    public void testgetColorSettings() throws Exception {

        EventFeedXML eventFeedXML = new EventFeedXML();
        IInternalContest aContest = sample.createStandardContest();
        BalloonSettings settings = eventFeedXML.getColorSettings(aContest);

        Problem[] problems = aContest.getProblems();
        for (Problem problem : problems) {

            String color = settings.getColor(problem);
            String colorRGB = settings.getColorRGB(problem);

            assertNotNull("Expecting color for " + problem, color);
            assertNotNull("Expecting RGB for " + problem, colorRGB);
//            System.out.println(problem.getShortName()+"TAB"+color+" ATAB"+colorRGB);
        }
        
        eventFeedXML = new EventFeedXML();

        aContest = new InternalContest();
        
        for (Problem problem : problems) {
            aContest.addProblem(problem);
        }
        
        settings = eventFeedXML.getColorSettings(aContest);
        
        assertNull("Not expecting balloon settings ", settings);
        
        ensureDirectory(getDataDirectory());
        
        String colorFile = getTestFilename("colors.txt");
//        editFile(colorFile);
        assertFileExists(colorFile);
        
        eventFeedXML.setColorsFilename(colorFile);
        settings = eventFeedXML.getColorSettings(aContest);
        
        assertNotNull("Expecting color settings from "+colorFile, settings);
        
        String [] data = { //
                "sumit;Alice Blue A;F0F8FF", //
                "quadrangles;Antique White A;FAEBD7", //
                "finnigans;Beige A;F5F5DC", //
        };
        
        for (Problem problem : problems) {

            String color = settings.getColor(problem);
            String colorRGB = settings.getColorRGB(problem);

            assertNotNull("Expecting color for " + problem, color);
            assertNotNull("Expecting RGB for " + problem, colorRGB);

//            System.out.println("Problem : " + problem + " " + color + "," + colorRGB);
            for (String datum : data) {
                String[] fields = datum.split(";");
                if (fields[0].equals(problem.getShortName())) {
                    assertEquals("Expecting same color for " + problem, fields[1], color);
                    assertEquals("Expecting same RGB for " + problem, fields[2], colorRGB);
                }
            }
        }
        
//        File schemaFile = new File(getSchemaFilename(CCS_EVENT_FEED_SCHEMA_2013));
//        String xmlString = eventFeedXML.toXML(aContest);
        
//        xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xmlString;
//        xmlString = "<contest></contest>";
        
//        editTempFile(xmlString);
        
//        System.out.println("xml="+xmlString);
        
//        try {
//            testForValidXML(xmlString, schemaFile);
//        } catch (Exception e) {
//            System.err.println("Error "+e.getMessage());
//            throw e;
//        }
    }

    public void testIsYounger() throws Exception {
        
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

                "14,2,A,30,No", // doesn't count, no after yes
                "15,2,A,25,No", // doesn't count, no after yes

                "16,2,A,330,Yes",  // doesn't count, yes after yes

        };
        
        IInternalContest testContest = sample.createStandardContest();
        
        for (String runInfoLine : runsData) {
            sample.addARun(testContest, runInfoLine);      
        }

        Run[] runs = testContest.getRuns();
        Arrays.sort(runs, new RunComparator());

        EventFeedXML feed = new EventFeedXML();

//        for (Run run : runs) {
//            System.out.println(feed.isYoungerThanFirstYes(testContest, run) + " " + getRunInfo(run));
//        }

        assertFalse("Should be younger", feed.isYoungerThanFirstYes(testContest, runs[13]));
        assertTrue("Should not be younger", feed.isYoungerThanFirstYes(testContest, runs[14]));

        Run laterRun = feed.getFirstSolvedRun(testContest, runs[15].getSubmitter(), runs[15].getProblemId());
        Run earliest = runs[7];
        assertEquals("Expecting first solved run to be " + earliest, earliest, laterRun);
        
    }

    /**
     * Create socket server on port.
     * 
     * @param args
     */
    public static void main(String[] args) {

        try {
            EventFeedXMLTest eventFeedXMLTest = new EventFeedXMLTest();
            eventFeedXMLTest.setUp();
            eventFeedXMLTest.startEventFeed(5555);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public void exitServer(){
        this.exitingServer = true;
    }
}
