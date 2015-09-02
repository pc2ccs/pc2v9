package edu.csus.ecs.pc2.exports.ccs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.list.AccountComparator;
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
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.core.util.XMLMemento;

/**
 * Unit Test. 
 *
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EventFeedXML2013Test extends AbstractTestCase {

    private static final String CONTEST_END_TAG = "</contest>";

    private static final String CONTEST_START_TAG = "<contest>";

    private final boolean debugMode = false;

    private IInternalContest contest = null;
    
    private SampleContest sample = new SampleContest();
    
    public EventFeedXML2013Test(String testName) {
        super(testName);
    }

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

        Account[] teams = getTeamAccounts();

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
            
            inContest.updateRunFiles(run, runFiles);
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

        EventFeedXML2013 eventFeedXML = new EventFeedXML2013();

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

        assertXMLCounts(xml, EventFeedXML2013.CONTEST_TAG, 1);
        assertXMLCounts(xml, EventFeedXML2013.INFO_TAG, 1);
        
    }

    /**
     * Test <info> tag.
     * 
     * @throws Exception
     */
    public void testInfoElement() throws Exception {

        EventFeedXML2013 eventFeedXML = new EventFeedXML2013();
        
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

        EventFeedXML2013 eventFeedXML = new EventFeedXML2013();
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

        if (debugMode){
            System.out.println(" -- testRegionElement ");
        }

        Group[] groups = contest.getGroups();
        Arrays.sort(groups, new GroupComparator());

        EventFeedXML2013 eventFeedXML = new EventFeedXML2013();
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

        EventFeedXML2013 eventFeedXML = new EventFeedXML2013();
        int sequence = 1;
        for (Judgement judgement : contest.getJudgements()) {
            String xml = toContestXML(eventFeedXML.createElement(contest, judgement, sequence));
            if (debugMode){
                System.out.println(xml);
            }
            testForValidXML (xml);
            sequence ++;
        }
    }
    
    @SuppressWarnings("unused")
    private String toString(BalloonDeliveryInfo info) {
        return "Notification: "+info.getKey()+" "+info.getTimeSent();
    }

  

    public void testProblemElement() throws Exception {

        if (debugMode){
            System.out.println(" -- testProblemElement ");
        }

        EventFeedXML2013 eventFeedXML = new EventFeedXML2013();
        int idx = 1;
        for (Problem problem : contest.getProblems()) {
            String xml = toContestXML(eventFeedXML.createElement(contest, problem, idx));
            if (debugMode) {
                System.out.println(xml);
            }
            testForValidXML(xml);
            assertXMLCounts(xml, EventFeedXML2013.PROBLEM_TAG, 1);

            if (idx == 1) {
                assertXMLNodeValueEquals(xml, "name", "Sumit");
            }

            if (idx == 6) {
                assertXMLNodeValueEquals(xml, "name", "Finnigans Bluff");
            }

            idx++;
        }
    }

    public void testTeamElement() throws Exception {
        if (debugMode){
            System.out.println(" -- testTeamElement ");
        }

        EventFeedXML2013 eventFeedXML = new EventFeedXML2013();

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
        // TODO: ensure that EventFeedXMLTest includes this method.

        if (debugMode){
            System.out.println(" -- testClarElement ");
        }

        EventFeedXML2013 eventFeedXML = new EventFeedXML2013();
        Clarification[] clarifications = contest.getClarifications();
        Arrays.sort(clarifications, new ClarificationComparator());
        
        Problem problem = contest.getProblems()[0];
        ClientId who = getTeamAccounts()[0].getClientId();
        String question = "What is the meaning of pi ?";
        
        Clarification clar = new Clarification(who,problem,question);
        contest.addClarification(clar);
        
        clarifications = contest.getClarifications();
        assertEquals("Expecting number of clarifications ",1,clarifications.length);

        for (Clarification clarification : clarifications) {
            String xml = toContestXML(eventFeedXML.createElement(contest, clarification));
            if (debugMode){
                System.out.println(xml);
            }
            testForValidXML (xml);
            assertXMLCounts(xml, "clar>", 0); // not expecting <clar>
            assertXMLCounts(xml, EventFeedXML2013.CLARIFICATION_TAG, 1);
        }
        
    }

    public void testRunElement() throws Exception {

        if (debugMode){
            System.out.println(" -- testRunElement ");
        }

        EventFeedXML2013 eventFeedXML = new EventFeedXML2013();
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

    public void testFinalizedElement() throws Exception {

        EventFeedXML2013 eventFeedXML = new EventFeedXML2013();
        
        FinalizeData data = new FinalizeData();
        data.setGoldRank(8);
        data.setBronzeRank(20);
        data.setSilverRank(16);
        data.setComment("Finalized by the Ultimiate Finalizer role");
        
        String xml = CONTEST_START_TAG + eventFeedXML.createFinalizeXML(contest, data);
        if (debugMode){
            System.out.println(" -- testFinalizedElement ");
            System.out.println(xml);
        }

        testForValidXML (xml);
        
        assertXMLCounts(xml, "finalized", 1);
        
    }

    public void testStartupElement() throws Exception {

        EventFeedXML2013 eventFeedXML = new EventFeedXML2013();
        String xml = eventFeedXML.createStartupXML(contest);

        if (debugMode){
            System.out.println(" -- testStartupElement ");
            System.out.println(xml);
        }
        xml = xml + CONTEST_END_TAG;
        testForValidXML (xml);
                
        assertXMLCounts(xml, EventFeedXML2013.CONTEST_TAG, 1);
        assertXMLCounts(xml, EventFeedXML2013.INFO_TAG, 1);
        assertXMLCounts(xml, EventFeedXML2013.JUDGEMENT_TAG, 9);
        assertXMLCounts(xml, EventFeedXML2013.REGION_TAG, 24);

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
        EventFeedXML2013.CONTEST_TAG, EventFeedXML2013.INFO_TAG, EventFeedXML2013.REGION_TAG, EventFeedXML2013.PROBLEM_TAG, EventFeedXML2013.LANGUAGE_TAG, EventFeedXML2013.TEAM_TAG,
                EventFeedXML2013.CLARIFICATION_TAG, EventFeedXML2013.TESTCASE_TAG, EventFeedXML2013.RUN_TAG, EventFeedXML2013.JUDGEMENT_TAG, EventFeedXML2013.FINALIZE_TAG,
                EventFeedXML2013.JUDGEMENT_RECORD_TAG
        //
        };
        return tagnames;
    }

    public void testToXML() throws Exception {

        EventFeedXML2013 eventFeedXML = new EventFeedXML2013();
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
        EventFeedXML2013 eventFeedXML = new EventFeedXML2013();
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
                server.close();
            }
        }
    }

    public void testTestCase() throws Exception {
        
        EventFeedXML2013 eventFeedXML = new EventFeedXML2013();
        
        int siteNumber = 2;
        
        IInternalContest testCaseContest = sample.createContest(siteNumber, 1, 22, 12, true);
        
        /**
         * Add random runs
         */
        
        Run[] runs = sample.createRandomRuns(testCaseContest, 12, true, true, true);
        
        createDataFilesForContest (testCaseContest, 5);

        sample.assignSampleGroups(testCaseContest, "Group Thing One", "Group Thing Two");
        
        sample.assignTeamExternalIds(testCaseContest, 424242);
        
        /**
         * Add Run Judgements.
         */
        addRunJudgements(testCaseContest, runs, 5);
        runs = testCaseContest.getRuns();
        
        String xml = eventFeedXML.toXML(testCaseContest);
        
        System.out.println("debug 22 xml = "+xml);
        
        if (debugMode){
            System.out.println(" -- testTestCase ");
            System.out.println(xml);
        }

        
        testForValidXML(xml);

        assertEquals ("No empty external-id tags expected", 0, countString(xml, "external-id/"));
        
        validateUsingSchema (xml);
        assertEquals ("No empty OCS values expected", 0, countString(xml, "<result>OCS"));
        
        assertXMLCounts(xml, EventFeedXML2013.CONTEST_TAG, 1);
        assertXMLCounts(xml, EventFeedXML2013.INFO_TAG, 1);
        assertXMLCounts(xml, EventFeedXML2013.JUDGEMENT_TAG, 9);
        assertXMLCounts(xml, EventFeedXML2013.LANGUAGE_TAG, 18);
        assertXMLCounts(xml, EventFeedXML2013.PROBLEM_TAG, 18);
        assertXMLCounts(xml, EventFeedXML2013.REGION_TAG, 24);
        assertXMLCounts(xml, EventFeedXML2013.RUN_TAG, 12);
        assertXMLCounts(xml, EventFeedXML2013.TEAM_TAG, 34); // both teams and team tag in submissions
        assertXMLCounts(xml, EventFeedXML2013.TESTCASE_TAG, 12 * 5); 

        /**
         * Test FINALIZE
         */
        
        FinalizeData data = new FinalizeData();
        data.setCertified(true);
        data.setComment(getName()+" test");
        testCaseContest.setFinalizeData(data);
        
        xml = eventFeedXML.toXML(testCaseContest);
        
        testForValidXML (xml);
        
        assertXMLCounts(xml, EventFeedXML2013.FINALIZE_TAG, 1);
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
        
        assertXMLCounts(xml, EventFeedXML2013.RUN_TAG, numruns);
        assertXMLCounts(xml, EventFeedXML2013.TESTCASE_TAG, numruns * 5); 

    }
    
    private void validateUsingSchema(String xml) throws Exception {

        // TODO 623 TODO CCS get this schema validation  to work.
        
//        String prolog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
//        String newXML = prolog + xml;
//        String [] contents = {newXML};
//        String filename ="testing.ef.xml";
//        writeFileContents(filename, contents);
//        System.out.println("Wrote xml to file "+filename);
        
        String schemaFileName = getSchemaFilename("event-feed-2013.xsd");
        assertFileExists(schemaFileName);
        
//        testForValidXML (newXML, schemaFileName);
        
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
    private void createDataFilesForContest(IInternalContest inContest, int numberTestCases) throws FileNotFoundException {

        Problem[] problems = inContest.getProblems();
        for (Problem problem : problems) {

            int numProblemDataFiles = problem.getNumberTestCases();
            if (numProblemDataFiles == 0){
                numProblemDataFiles = numberTestCases;
            }

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

        EventFeedXML2013 feed = new EventFeedXML2013();

//        for (Run run : runs) {
//            System.out.println(feed.isYoungerThanFirstYes(testContest, run) + " " + getRunInfo(run));
//        }

        assertFalse("Should be younger", feed.isYoungerThanFirstYes(testContest, runs[13]));
        assertTrue("Should not be younger", feed.isYoungerThanFirstYes(testContest, runs[14]));

        Run laterRun = feed.getFirstSolvedRun(testContest, runs[15].getSubmitter(), runs[15].getProblemId());
        Run earliest = runs[7];
        assertEquals("Expecting first solved run to be " + earliest, earliest, laterRun);
        
    }
    
    // SOMEDAY: Ensure that teams that are not shown on scoreboard runs are not in feed.
    
    public void testDeletedRuns() throws Exception {
        // TODO: ensure that EventFeedXMLTest includes this method.

        EventFeedXML2013 eventFeedXML = new EventFeedXML2013();

        int siteNumber = 2;

        IInternalContest testCaseContest = sample.createContest(siteNumber, 1, 22, 12, true);

        /**
         * Add random runs
         */

        Run[] runs = sample.createRandomRuns(testCaseContest, 12, true, true, true);

        createDataFilesForContest(testCaseContest, 5);

        sample.assignSampleGroups(testCaseContest, "Group Thing One", "Group Thing Two");

        sample.assignTeamExternalIds(testCaseContest, 424242);

        /**
         * Add Run Judgements.
         */
        addRunJudgements(testCaseContest, runs, 5);

        Run[] runs2 = testCaseContest.getRuns();
        Arrays.sort(runs2, new RunComparator());
        Run run2 = runs2[1];
        run2.setDeleted(true);
        ClientId adminUser = getAdminAccount(testCaseContest).getClientId();
        testCaseContest.updateRun(run2, adminUser);

        runs2 = testCaseContest.getRuns();
        Arrays.sort(runs2, new RunComparator());
        run2 = runs2[1];
        assertTrue("Expecting run to be deleted ", run2.isDeleted());

        run2 = runs2[3];
        run2.setDeleted(true);
        testCaseContest.updateRun(run2, adminUser);

        int deletedCount = 0;
        runs2 = testCaseContest.getRuns();
        Arrays.sort(runs2, new RunComparator());
        for (Run run33 : runs2) {
            if (run33.isDeleted()) {
                deletedCount++;
            }
        }

        String xml = eventFeedXML.toXML(testCaseContest);

        if (debugMode) {
            System.out.println(" -- testDeletedRuns ");
            System.out.println(xml);
        }

        testForValidXML(xml);

        assertXMLCounts(xml, EventFeedXML2013.RUN_TAG, runs2.length - deletedCount);

    }

    private Account getAdminAccount(IInternalContest inContest) {
        return inContest.getAccounts(Type.ADMINISTRATOR).firstElement();
    }
    
    public void testExternalId() throws Exception {
        
        EventFeedXML2013 eventFeedXML = new EventFeedXML2013();

        int siteNumber = 2;

        IInternalContest testCaseContest = sample.createContest(siteNumber, 1, 22, 12, true);
        String xmlString = eventFeedXML.toXML(testCaseContest);
        
        testForValidXML(xmlString);

        Document document = getDocument(xmlString);
        
        NodeList nodes = document.getElementsByTagName("external-id");
        
        assertEquals("external id count ", 22,nodes.getLength());
        
        String baseValue = "836577";

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String value = node.getTextContent();
            // System.out.println("value = " + value);
            String expectedValue = baseValue + (i + 1);
            assertNotNull("Expect value for external-id", value);
            assertFalse("Expect value " + expectedValue + "for external-id", "".equals(value));
            assertEquals("Expecting same value", expectedValue, value);
        }

        assertXMLCounts(xmlString, EventFeedXML2013.TEAM_TAG, 22);
        
    }
    
    public void testUnjudgedRuns() throws Exception {
        
        EventFeedXML2013 eventFeedXML = new EventFeedXML2013();

        int siteNumber = 2;

        IInternalContest testCaseContest = sample.createContest(siteNumber, 1, 22, 12, true);
        

        Account acc = sample.getTeamAccounts(testCaseContest)[0];
        ClientId clientId = acc.getClientId();
        Problem problem = testCaseContest.getProblems()[0];
        Run run = sample.createRun(testCaseContest, clientId, problem);
        
        testCaseContest.addRun(run);
        String xmlString = eventFeedXML.toXML(testCaseContest);
        
        testForValidXML(xmlString);
        
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

//    /**
//     * Test Suite.
//     * 
//     * This only works under JUnit 3.
//     * 
//     * @return suite of tests.
//     */
//    public static TestSuite suite() {
//
//        TestSuite suite = new TestSuite("EventFeedXML2013Test");
//
//        String singletonTestName = "";
////        singletonTestName = "testExternalId";
//
//        if (!"".equals(singletonTestName)) {
//            suite.addTest(new EventFeedXML2013Test(singletonTestName));
//        } else {
//
//            suite.addTest(new EventFeedXML2013Test("testContestElement"));
//            suite.addTest(new EventFeedXML2013Test("testInfoElement"));
//            suite.addTest(new EventFeedXML2013Test("testLanguageElement"));
//            suite.addTest(new EventFeedXML2013Test("testRegionElement"));
//            suite.addTest(new EventFeedXML2013Test("testJudgementElement"));
//            suite.addTest(new EventFeedXML2013Test("testProblemElement"));
//            suite.addTest(new EventFeedXML2013Test("testTeamElement"));
//            suite.addTest(new EventFeedXML2013Test("testClarElement"));
//            suite.addTest(new EventFeedXML2013Test("testRunElement"));
//            suite.addTest(new EventFeedXML2013Test("testFinalizedElement"));
//            suite.addTest(new EventFeedXML2013Test("testStartupElement"));
//            suite.addTest(new EventFeedXML2013Test("testToXML"));
//            suite.addTest(new EventFeedXML2013Test("testTestCase"));
//            suite.addTest(new EventFeedXML2013Test("testIsYounger"));
//            suite.addTest(new EventFeedXML2013Test("testDeletedRuns"));
//            suite.addTest(new EventFeedXML2013Test("testExternalId"));
//
//        }
//        return suite;
//    }
}
