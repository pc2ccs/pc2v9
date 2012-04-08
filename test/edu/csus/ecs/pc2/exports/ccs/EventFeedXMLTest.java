package edu.csus.ecs.pc2.exports.ccs;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.BalloonDeliveryComparator;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.BalloonDeliveryInfo;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestInformation.TeamDisplayMask;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.NotificationUtilities;
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
    
    private SampleContest sample = new SampleContest();
    
    private NotificationUtilities notificationUtilities = new NotificationUtilities();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        int siteNumber = 1;
        contest = sample.createContest(siteNumber, 1, 22, 12, true);

        /**
         * Add random runs
         */

        Run[] runs = sample.createRandomRuns(contest, 12, true, true, true);
        
        addContestInfo (contest, "Contest Title");
        
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
            if (debugMode){
                System.out.println(xml);
            }
            testForValidXML (xml);
            idx++;
        }
    }

    public void testTeamElement() throws Exception {
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
            String xml = toContestXML(eventFeedXML.createElement(contest, run));
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
        testForValidXML (xml);
    }

    public void testStartupElement() throws Exception {

        EventFeedXML eventFeedXML = new EventFeedXML();
        String xml = eventFeedXML.createStartupXML(contest);

        if (debugMode){
            System.out.println(" -- testStartupElement ");
            System.out.println(xml);
        }
        testForValidXML (xml);

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
    
    /**
     * Test for well formed XML.
     * 
     * @param xml
     */
    private void testForValidXML(String xml) {
        
        assertFalse("Expected XML, found null", xml == null);
        assertFalse("Expected XML, found empty string", xml.length() == 0);
        
//        System.out.println("XML length is "+xml.length());
        
        // TODO CCS test for well formed XML
        
    }

    protected void startEventFeed(int port) throws IOException {

        ServerSocket server = new ServerSocket(port);

        /**
         * Check info tag, if there is no contest data in InternalContest.
         */
        EventFeedXML eventFeedXML = new EventFeedXML();
        String xml = eventFeedXML.toXML(contest);

        System.out.println("Opened socket on port " + port);

        while (true) {

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
            }
        }

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
}

