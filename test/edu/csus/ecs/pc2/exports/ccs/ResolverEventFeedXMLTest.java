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

import junit.framework.TestSuite;

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
public class ResolverEventFeedXMLTest extends AbstractTestCase {

    private static final String CONTEST_END_TAG = "</contest>";

    private static final String CONTEST_START_TAG = "<contest>";

    private IInternalContest contest = null;

    private SampleContest sample = new SampleContest();

    public ResolverEventFeedXMLTest(String testName) {
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

        ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();

        InternalContest internalContest = new InternalContest();

        ContestInformation info = new ContestInformation();
        info.setContestTitle("Title One");

        /**
         * Check info tag, if there is no contest data in InternalContest.
         */
        String xml = toContestXML(eventFeedXML.createInfoElement(internalContest, info));
        assertNotNull("Should create contest element", xml);
        testForValidXML (xml);

        debugPrintln(" -- testContestElement info tag ");
        debugPrintln(xml);
        debugPrintln();

        /**
         * Check complete EventFeed XML,  if there is no contest data in InternalContest.
         */
        xml = eventFeedXML.toXML(internalContest);

        debugPrintln(" -- testContestElement info tag ");
        debugPrintln(xml);
        debugPrintln();
        testForValidXML (xml);

        assertXMLCounts(xml, ResolverEventFeedXML.CONTEST_TAG, 1);
        assertXMLCounts(xml, ResolverEventFeedXML.INFO_TAG, 1);

    }

    /**
     * Test <info> tag.
     * 
     * @throws Exception
     */
    public void testInfoElement() throws Exception {

        ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();

        ContestInformation info = new ContestInformation();
        info.setContestTitle("Title One");

        String xml = toContestXML(eventFeedXML.createInfoElement(contest, info));

        debugPrintln(" -- testInfoElement ");
        debugPrintln(xml);

        debugPrintln();

        contest.startContest(1);
        xml = toContestXML(eventFeedXML.createInfoElement(contest, info));

        debugPrintln(xml);

        testForValidXML (xml);
    }

    public void testLanguageElement() throws Exception {

        debugPrintln(" -- testLanguageElement ");

        ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();
        int idx = 1;
        for (Language language : contest.getLanguages()) {
            String xml = toContestXML(eventFeedXML.createElement(contest, language, idx));
            debugPrintln(xml);
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

        debugPrintln(" -- testRegionElement ");

        Group[] groups = contest.getGroups();
        Arrays.sort(groups, new GroupComparator());

        ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();
        for (Group group : groups) {
            String xml = toContestXML(eventFeedXML.createElement(contest, group));
            debugPrintln(xml);
            testForValidXML (xml);
        }
    }

    public void testJudgementElement() throws Exception {

        debugPrintln(" -- testJudgementElement ");

        ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();
        int sequence = 1;
        for (Judgement judgement : contest.getJudgements()) {
            String xml = toContestXML(eventFeedXML.createElement(contest, judgement, sequence));
            debugPrintln(xml);
            testForValidXML (xml);
            sequence ++;
        }
    }

    @SuppressWarnings("unused")
    private String toString(BalloonDeliveryInfo info) {
        return "Notification: "+info.getKey()+" "+info.getTimeSent();
    }



    public void testProblemElement() throws Exception {

        debugPrintln(" -- testProblemElement ");

        ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();
        int idx = 1;
        for (Problem problem : contest.getProblems()) {
            String xml = toContestXML(eventFeedXML.createElement(contest, problem, idx));
            debugPrintln(xml);
            testForValidXML(xml);
            assertXMLCounts(xml, ResolverEventFeedXML.PROBLEM_TAG, 1);

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
        debugPrintln(" -- testTeamElement ");

        ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();

        Account[] accounts = getTeamAccounts();

        for (Account account : accounts) {
            String xml = toContestXML(eventFeedXML.createElement(contest, account));
            debugPrintln(xml);
            testForValidXML (xml);
        }
    }

public void testClarElement() throws Exception {

    debugPrintln(" -- testClarElement ");

    ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();
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
        debugPrintln(xml);
        testForValidXML (xml);
        assertXMLCounts(xml, "clar>", 0); // not expecting <clar>
        assertXMLCounts(xml, ResolverEventFeedXML.CLARIFICATION_TAG, 1);
    }

}

public void testRunElement() throws Exception {

    debugPrintln(" -- testRunElement ");

    ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();
    Run[] runs = contest.getRuns();
    Arrays.sort(runs, new RunComparator());

    for (Run run : runs) {
        String xml = toContestXML(eventFeedXML.createElement(contest, run));
        debugPrintln(xml);
        testForValidXML (xml);
    }
}

public void testFinalizedElement() throws Exception {

    ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();

    FinalizeData data = new FinalizeData();
    data.setGoldRank(8);
    data.setBronzeRank(20);
    data.setSilverRank(16);
    data.setComment("Finalized by the Ultimiate Finalizer role");

    String xml = CONTEST_START_TAG + eventFeedXML.createFinalizeXML(contest, data);
    debugPrintln(" -- testFinalizedElement ");
    debugPrintln(xml);

    testForValidXML (xml);

    assertXMLCounts(xml, "finalized", 1);

}

public void testStartupElement() throws Exception {

    ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();
    String xml = eventFeedXML.createStartupXML(contest);

    debugPrintln(" -- testStartupElement ");
    debugPrintln(xml);
    xml = xml + CONTEST_END_TAG;
    testForValidXML (xml);

    assertXMLCounts(xml, ResolverEventFeedXML.CONTEST_TAG, 1);
    assertXMLCounts(xml, ResolverEventFeedXML.INFO_TAG, 1);
    assertXMLCounts(xml, ResolverEventFeedXML.JUDGEMENT_TAG, 9);
    assertXMLCounts(xml, ResolverEventFeedXML.REGION_TAG, 24);

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

    debugPrintln("printElemntCounts " + comment);

    String[] tagnames = getAllEventFeedTagNames();

    Arrays.sort(tagnames);
    for (String tagName : tagnames) {
        debugPrintln(tagName + " count = " + getTagCount(xmlString, tagName));
    }
}

public String [] getAllEventFeedTagNames (){
    String[] tagnames = { //
            ResolverEventFeedXML.CONTEST_TAG, ResolverEventFeedXML.INFO_TAG, ResolverEventFeedXML.REGION_TAG, ResolverEventFeedXML.PROBLEM_TAG, ResolverEventFeedXML.LANGUAGE_TAG, ResolverEventFeedXML.TEAM_TAG,
            ResolverEventFeedXML.CLARIFICATION_TAG, ResolverEventFeedXML.TESTCASE_TAG, ResolverEventFeedXML.RUN_TAG, ResolverEventFeedXML.JUDGEMENT_TAG, ResolverEventFeedXML.FINALIZE_TAG,
            ResolverEventFeedXML.JUDGEMENT_RECORD_TAG
            //
    };
    return tagnames;
}

public void testToXML() throws Exception {

    ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();
    String xml = eventFeedXML.toXML(contest);
    debugPrintln(" -- testToXML ");
    debugPrintln(xml);
    testForValidXML (xml);
}

protected void startEventFeed(int port) throws IOException {

    ServerSocket server = new ServerSocket(port);

    /**
     * Check info tag, if there is no contest data in InternalContest.
     */
    ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();
    String xml = eventFeedXML.toXML(contest);

    debugPrintln("Opened socket on port " + port);

    while (true) {

        try {
            Socket connection = server.accept();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(xml);
            out.write("<!-- end of stream -->");
            out.flush();
            connection.close();
            debugPrintln("Opened and output sample Event Feed");
        } catch (Exception e) {
            e.printStackTrace();
            server.close();
        }
    }
}

public void testTestCase() throws Exception {

    ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();

    int siteNumber = 2;

    IInternalContest testCaseContest = sample.createContest(siteNumber, 1, 22, 12, true);

    /**
     * Add random runs
     */

    Run[] runs = sample.createRandomRuns(testCaseContest, 12, true, true, true);

    createDataFilesForContest (testCaseContest);

    sample.assignSampleGroups(testCaseContest, "Group Thing One", "Group Thing Two");

    sample.assignTeamExternalIds(testCaseContest, 424242);

    /**
     * Add Run Judgements.
     */
    addRunJudgements(testCaseContest, runs, 5);

    String xml = eventFeedXML.toXML(testCaseContest);

    debugPrintln(" -- testTestCase ");
    debugPrintln(xml);

    testForValidXML(xml);

    assertEquals ("No empty external-id tags expected", 0, countString(xml, "external-id/"));

    validateUsingSchema (xml);
    assertEquals ("No empty OCS values expected", 0, countString(xml, "<result>OCS"));

    assertXMLCounts(xml, ResolverEventFeedXML.CONTEST_TAG, 1);
    assertXMLCounts(xml, ResolverEventFeedXML.INFO_TAG, 1);
    assertXMLCounts(xml, ResolverEventFeedXML.JUDGEMENT_TAG, 9);
    assertXMLCounts(xml, ResolverEventFeedXML.LANGUAGE_TAG, 18);
    assertXMLCounts(xml, ResolverEventFeedXML.PROBLEM_TAG, 18);
    assertXMLCounts(xml, ResolverEventFeedXML.REGION_TAG, 24);
    assertXMLCounts(xml, ResolverEventFeedXML.RUN_TAG, 12);
    assertXMLCounts(xml, ResolverEventFeedXML.TEAM_TAG, 34); // both teams and team tag in submissions
    assertXMLCounts(xml, ResolverEventFeedXML.TESTCASE_TAG, 12 * 5); 

    /**
     * Test FINALIZE
     */

    FinalizeData data = new FinalizeData();
    data.setCertified(true);
    data.setComment(getName()+" test");
    testCaseContest.setFinalizeData(data);

    xml = eventFeedXML.toXML(testCaseContest);

    testForValidXML (xml);

    assertXMLCounts(xml, ResolverEventFeedXML.FINALIZE_TAG, 1);
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

    // TODO fix this test
    //        assertXMLCounts(xml, ResolverEventFeedXML.RUN_TAG, numruns);

    assertXMLCounts(xml, ResolverEventFeedXML.TESTCASE_TAG, numruns * 5); 

}

private void validateUsingSchema(String xml) throws Exception {

    
//    startExplorer(getSchemaDirectory());
    
    String schemaFileName = getSchemaFilename("resolver-event-feed.xsd");
    assertFileExists(schemaFileName);

    String prolog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    
    String newXML = prolog + xml;
    String [] contents = {newXML};
    String filename ="testing.ef.xml";
    writeFileContents(filename, contents);
    
//    debugPrintln("Wrote xml to file "+filename);
    
//    editFile(filename);
    
    // TODO 623 TODO CCS validation failing with
//    org.xml.sax.SAXParseException; lineNumber: 1; columnNumber: 1; Content is not allowed in prolog.

//    testForValidXML (newXML, schemaFileName);

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
        debugPrintln("xml = "+xmlString);
        fail("Expecting to find nodes for name "+name);
    }

    //        debugPrintln("Nodes length = "+nodes.getLength());
    //        for (int i = 0; i < nodes.getLength(); i++) {
    //            Node node = nodes.item(i);
    //            debugPrintln("Looking for "+name+", found node "+node.getNodeName()+" "+node.getNodeValue()+" child is: "+node.getChildNodes().item(0).getNodeValue());
    //        }

    String childValue = nodes.item(0).getChildNodes().item(0).getNodeValue();
    if (! expectedValue.equals(childValue)){
        debugPrintln("xml = "+xmlString);
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
                debugPrintln("Data file exists: " + filename);
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

    ResolverEventFeedXML feed = new ResolverEventFeedXML();

    //        for (Run run : runs) {
    //            debugPrintln(feed.isYoungerThanFirstYes(testContest, run) + " " + getRunInfo(run));
    //        }

    assertFalse("Should be younger", feed.isYoungerThanFirstYes(testContest, runs[13]));
    assertTrue("Should not be younger", feed.isYoungerThanFirstYes(testContest, runs[14]));

    Run laterRun = feed.getFirstSolvedRun(testContest, runs[15].getSubmitter(), runs[15].getProblemId());
    Run earliest = runs[7];
    assertEquals("Expecting first solved run to be " + earliest, earliest, laterRun);

}

// SOMEDAY: Ensure that teams that are not shown on scoreboard runs are not in feed.

public void testDeletedRuns() throws Exception {
    ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();

    int siteNumber = 2;

    IInternalContest testCaseContest = sample.createContest(siteNumber, 1, 22, 12, true);

    /**
     * Add random runs
     */

    Run[] runs = sample.createRandomRuns(testCaseContest, 12, true, true, true);

    createDataFilesForContest(testCaseContest);

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

    debugPrintln(" -- testDeletedRuns ");
    debugPrintln(xml);

    testForValidXML(xml);

    assertXMLCounts(xml, ResolverEventFeedXML.RUN_TAG, runs2.length - deletedCount);

}

private Account getAdminAccount(IInternalContest inContest) {
    return inContest.getAccounts(Type.ADMINISTRATOR).firstElement();
}

public void testExternalId() throws Exception {

    ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();

    int siteNumber = 2;

    IInternalContest testCaseContest = sample.createContest(siteNumber, 1, 22, 12, true);
    String xmlString = eventFeedXML.toXML(testCaseContest);

    testForValidXML(xmlString);

    Document document = getDocument(xmlString);

    NodeList nodes = document.getElementsByTagName("external-id");

    assertEquals("external id count ", 22,nodes.getLength());

    String baseValue = "4242";

    for (int i = 0; i < nodes.getLength(); i++) {
        Node node = nodes.item(i);
        String value = node.getTextContent();
        // debugPrintln("value = " + value);
        String expectedValue = baseValue + (i + 1);
        assertNotNull("Expect value for external-id", value);
        assertFalse("Expect value " + expectedValue + "for external-id", "".equals(value));
        assertEquals("Expecting same value", expectedValue, value);
    }

    assertXMLCounts(xmlString, ResolverEventFeedXML.TEAM_TAG, 22);

}

public void testUnjudgedRuns() throws Exception {

    ResolverEventFeedXML eventFeedXML = new ResolverEventFeedXML();

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

/**
 * Test Suite.
 * 
 * This only works under JUnit 3.
 * 
 * @return suite of tests.
 */
public static TestSuite suiteA() {

    TestSuite suite = new TestSuite("ResolverEventFeedXMLTest");

    String singletonTestName = "";
    //        singletonTestName = "testExternalId";

    if (!"".equals(singletonTestName)) {
        suite.addTest(new ResolverEventFeedXMLTest(singletonTestName));
    } else {

        suite.addTest(new ResolverEventFeedXMLTest("testContestElement"));
        suite.addTest(new ResolverEventFeedXMLTest("testInfoElement"));
        suite.addTest(new ResolverEventFeedXMLTest("testLanguageElement"));
        suite.addTest(new ResolverEventFeedXMLTest("testRegionElement"));
        suite.addTest(new ResolverEventFeedXMLTest("testJudgementElement"));
        suite.addTest(new ResolverEventFeedXMLTest("testProblemElement"));
        suite.addTest(new ResolverEventFeedXMLTest("testTeamElement"));
        suite.addTest(new ResolverEventFeedXMLTest("testClarElement"));
        suite.addTest(new ResolverEventFeedXMLTest("testRunElement"));
        suite.addTest(new ResolverEventFeedXMLTest("testFinalizedElement"));
        suite.addTest(new ResolverEventFeedXMLTest("testStartupElement"));
        suite.addTest(new ResolverEventFeedXMLTest("testToXML"));
        suite.addTest(new ResolverEventFeedXMLTest("testTestCase"));
        suite.addTest(new ResolverEventFeedXMLTest("testIsYounger"));
        suite.addTest(new ResolverEventFeedXMLTest("testDeletedRuns"));
        suite.addTest(new ResolverEventFeedXMLTest("testExternalId"));

    }
    return suite;
}
}
