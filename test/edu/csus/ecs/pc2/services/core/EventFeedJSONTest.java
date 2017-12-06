package edu.csus.ecs.pc2.services.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.list.ClarificationComparator;
import edu.csus.ecs.pc2.core.list.GroupComparator;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClarificationAnswer;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.core.util.JSONTool;
import edu.csus.ecs.pc2.services.web.EventFeedFilter;

/**
 * Unit Test.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class EventFeedJSONTest extends AbstractTestCase {

    /**
     * Unit test data.
     * 
     * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
     */
    class UnitTestData {

        private IInternalContest contest = new SampleContest().createStandardContest();

        private SampleContest sampleContest = new SampleContest();

        /**
         * Construct contest with accounts, runs, clars, etc.
         * 
         * @throws Exception
         */
        public UnitTestData() throws Exception {
            String[] runsData = { "1,1,A,1,No,No,4", // 0 (a No before first yes Security Violation)
                    "2,1,A,1,No,No,2", // 0 (a No before first yes Compilation Error)
                    "3,1,A,1,No,No,1", // 20 (a No before first yes)
                    "4,1,A,3,Yes,No,0", // 3 (first yes counts Minute points but never Run Penalty points)
                    "5,1,A,5,No,No,1", // zero -- after Yes
                    "6,1,A,7,Yes,No,0", // zero -- after Yes
                    "7,1,A,9,No,No,1", // zero -- after Yes
                    "8,1,B,11,No,No,1", // zero -- not solved
                    "9,2,A,48,No,No,4", // 0 (a No before first yes Security Violation)
                    "10,2,A,50,Yes,No,0", // 50 (minute points; no Run points on first Yes)
                    "11,2,B,35,No,No,1", // zero -- not solved
                    "12,2,B,40,No,No,1", // zero -- not solved
            };

            // Assign half eams random team member names
            addTeamMembers(getContest(), getTeamAccounts(getContest()).length / 2, 5);

            assertEquals("Expectig team member names", 5, getFirstAccount(getContest(), Type.TEAM).getMemberNames().length);

            assertEquals("team count", 120, getContest().getAccounts(Type.TEAM).size());

            for (String runInfoLine : runsData) {
                SampleContest.addRunFromInfo(getContest(), runInfoLine);
            }

            Run[] runs = getContest().getRuns();
            assertEquals("Run count", 12, runs.length);

            Problem problem = getContest().getProblems()[0];
            Account judge = getFirstAccount(getContest(), Type.JUDGE);
            generateClarifications(getContest(), 20, problem, judge.getClientId(), false, false);
            generateClarifications(getContest(), 20, problem, judge.getClientId(), true, false);
            generateClarifications(getContest(), 20, problem, judge.getClientId(), true, true);

            sampleContest.assignSampleGroups(getContest(), "North Group", "South Group");

            assertEquals("Runs", 12, getContest().getRuns().length);

        }

        /**
         * Get contest populated with test data.
         * 
         * @return
         */
        public IInternalContest getContest() {
            return contest;
        }

    }

    public void simpleTest() throws Exception {

        IInternalContest contest = new SampleContest().createStandardContest();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(contest);

        String json = eventFeedJSON.createJSON(contest);

        assertNotNull(json);

    }

    /**
     * Test every name and value that event feed JSON can output.
     * 
     */
    public void testCompleteEventFeed() throws Exception {

        UnitTestData data = new UnitTestData();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(data.getContest());

        String json = eventFeedJSON.createJSON(data.getContest());

        assertNotNull(json);

        // write valid JSON to a file to test for syntax errors
        // String validJSON = json.replaceAll("\n", ",\n");
        // validJSON = "[\n" + validJSON.substring(0, validJSON.length()-2) + "\n]";
        // String filename = "/tmp/stuf.new.json";
        // writeFile(new File(filename), validJSON);

        validateJSON(json);

        String eventCounts[] = {
                //
                EventFeedJSON.CLARIFICATIONS_KEY + ": 100", //
                EventFeedJSON.CONTEST_KEY + ": 1", //
                EventFeedJSON.GROUPS_KEY + ": 2", //

                EventFeedJSON.JUDGEMENT_KEY + ":  12", //
                EventFeedJSON.JUDGEMENT_TYPE_KEY + ": 9", //
                EventFeedJSON.LANGUAGE_KEY + ":   6", //

                EventFeedJSON.PROBLEM_KEY + ":    6", //
                EventFeedJSON.RUN_KEY + ":    0", //
                EventFeedJSON.SUBMISSION_KEY + ": 12", //

                EventFeedJSON.TEAM_MEMBERS_KEY + ":  300", //

                // EventFeedJSON.ORGANIZATION_KEY + ": 12",
                // EventFeedJSON.AWARD_KEY + ": 1", //
        };

        for (String line : eventCounts) {
            String[] fields = line.split(":");
            String name = fields[0];
            int value = Integer.parseInt(fields[1].trim());
            assertCountEvent(value, name, json);
        }

        assertMatchCount(12, "\"judgement_type_id\"", json);
        assertMatchCount(422, "\"icpc_id\"", json);

    }

    public void testSubmissionJSON() throws Exception {

        UnitTestData data = new UnitTestData();
        IInternalContest contest = data.getContest();

        Run[] runs = contest.getRuns();
        assertEquals("Run count", 12, runs.length);

        Arrays.sort(runs, new RunComparator());

        Run run = runs[runs.length - 1];

        SubmissionJSON submissionJSON = new SubmissionJSON();

        // String json = eventFeedJSON.getJSONEvent(eventFeedJSON.SUBMISSION_KEY, 22, EventFeedOperation.CREATE, submissionJSON.createJSON(contest, run));
        String json = wrapBrackets(submissionJSON.createJSON(contest, run));

        // System.out.println("debug submission json = "+json);
        // debug submission json = {"id":"12","language_id":"1","problem_id":"quadrangles","team_id":"2","time":"2017-10-09T08:45:43.744-07","contest_time":"00:40:00.000"}

        assertEqualJSON(json, "id", "12");
        assertEqualJSON(json, "language_id", "1");
        assertEqualJSON(json, "problem_id", "quadrangles");
        assertJSONStringValue(json, "team_id", "2");
    }

    String writeFile(File file, String string) throws FileNotFoundException, IOException {

        String[] lines = { string };
        Utilities.writeLinesToFile(file.getCanonicalPath(), lines);
        System.out.println("debug - Wrote file to " + file.getCanonicalPath());
        return file.getCanonicalPath();
    }

    /**
     * Add team member names to account.
     * 
     * @param contest
     * @param count
     *            number of accounts to add team names to.
     * @param numberOnMtea
     *            number of team names per team
     */
    private void addTeamMembers(IInternalContest contest, int count, int numberOnMtea) {

        Account[] accounts = getTeamAccounts(contest);

        for (int tid = 0; tid < accounts.length && tid < count; tid++) {

            Account account = accounts[tid];
            if (account.getMemberNames() == null || account.getMemberNames().length == 0) {

                List<String> names = new ArrayList<>();

                for (int i = 0; i < numberOnMtea; i++) {
                    String name = pickRandom(SampleContest.GIRL_NAMES);
                    names.add(name);
                }

                String[] newNames = (String[]) names.toArray(new String[names.size()]);
                account.setMemberNames(newNames);
            }
            contest.updateAccount(account);
        }
    }

    public void testPickRandom() throws Exception {

        for (int i = 0; i < 3000; i++) {
            String name = pickRandom(SampleContest.GIRL_NAMES);
            assertNotNull("Found null at iteration " + i, name);
        }
    }

    private static String pickRandom(String[] stringArray) {

        // return stringArray[0]; // return first name

        Random random = new Random();
        int nameIndex = random.nextInt(stringArray.length);
        return stringArray[nameIndex];
    }

    private void assertMatchCount(int count, String regex, String json) {
        assertEquals("Expecting to find " + count + "matches for '" + regex + "'", count, matchCount(regex, json));
    }

    /**
     * Expect count of elementName in JSON.
     * 
     * @param exepectedCount
     * @param eleementName
     * @param json
     */
    private void assertCountEvent(int exepectedCount, String eleementName, String json) {

        // System.out.println("debug "+eleementName+" "+matchEventCount(eleementName, json));
        assertEquals("For event '" + eleementName + "' expecting count", exepectedCount, matchEventCount(eleementName, json));
    }

    /**
     * Return count of events in json.
     * 
     * @param eleementName
     *            element to match
     * @param json
     * @return
     */
    private int matchEventCount(String eleementName, String json) {

        String regex = "\"type\":\"" + eleementName + "\"";

        return matchCount(regex, json);
    }

    private int matchCount(String regex, String json) {

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(json);

        int count = 0;
        while (matcher.find()) {
            count++;
        }

        return count;
    }

    /**
     * Generate a clarification for every team accounts.
     * 
     * @param contest
     * @param problem
     * @param judgeId
     * @param answerAll
     *            add an answer for every generated clarification
     * @param sendToAll
     *            mark clarifications send to All.
     * @throws Exception
     */
    public static void generateClarifications(IInternalContest contest, int maxCountToCreate, Problem problem, ClientId judgeId, boolean answerAll, boolean sendToAll) throws Exception {
        // SOMEDAY move generateClarifications into SampleContest

        String judgesDefaultAnswer = "No response, read problem statement";

        Account[] accounts = getTeamAccounts(contest);

        int count = 0;

        for (Account account : accounts) {

            count++;
            if (count > maxCountToCreate) {
                break;
            }

            Clarification clarification = new Clarification(account.getClientId(), problem, "Why? from " + account);

            clarification = new Clarification(account.getClientId(), problem, "Why #2? from " + account);
            if (answerAll) {
                String answer = judgesDefaultAnswer;
                clarification.setAnswer(answer, judgeId, contest.getContestTime(), sendToAll);
            }
            try {
                contest.acceptClarification(clarification);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    /**
     * Get all team accounts (all sites).
     * 
     */
    public static Account[] getTeamAccounts(IInternalContest contest) {
        // SOMEDAY move getTeamAccounts into AccountsUtility class

        return getAllAccounts(contest, Type.TEAM);
    }

    /**
     * Get all first account
     */
    public static Account getFirstAccount(IInternalContest contest, ClientType.Type type) {
        // SOMEDAY move getFirstAccount into AccountsUtility class

        Account[] accounts = getAllAccounts(contest, type);
        return accounts[0];
    }

    /**
     * Get all accounts (all sites).
     */
    public static Account[] getAllAccounts(IInternalContest contest, ClientType.Type type) {
        // SOMEDAY move getAllAccounts into AccountsUtility class

        Vector<Account> accountVector = contest.getAccounts(type);
        Account[] accounts = (Account[]) accountVector.toArray(new Account[accountVector.size()]);
        Arrays.sort(accounts, new AccountComparator());

        return accounts;
    }

    /**
     * Test single teams JSON line.
     * 
     * @throws Exception
     */
    public void testTeamJSON() throws Exception {

        IInternalContest contest = new UnitTestData().getContest();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(contest);

        Account[] account = getAccounts(contest, Type.TEAM);

        String json = eventFeedJSON.getTeamJSON(contest, account[0]);

        // System.out.println("debug team json = "+json);

        // debug team json = {"id":"1", "icpc_id":"3001", "name":"team1", "organization_id": null, "group_id":"1024"}

        assertEqualJSON(json, "id", "1");
        assertEqualJSON(json, "name", "team1");
        assertEqualJSON(json, "group_id", "1024");

        assertJSONStringValue(json, "id", "1");
        assertJSONStringValue(json, "icpc_id", "3001");
        assertJSONStringValue(json, "group_id", "1024");
    }

    /**
     * TEst single teams JSON line.
     * 
     * @throws Exception
     */
    public void testGroupJSON() throws Exception {

        IInternalContest contest = new UnitTestData().getContest();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(contest);

        Group[] groups = contest.getGroups();
        Arrays.sort(groups, new GroupComparator());

        String json = eventFeedJSON.getGroupJSON(contest, groups[0]);

        // System.out.println("debug group json = "+json);

        // debug group json = {"id":1024, "icpc_id":1024, "name":"North Group"}

        // {"id":1, "icpc_id":"3001", "name":"team1", "organization_id": null}

        assertEqualJSON(json, "id", "1024");

        assertJSONStringValue(json, "id",  "1024");
        assertJSONStringValue(json, "icpc_id", "1024");
        assertJSONStringValue(json, "name", "North Group");
    }

    /**
     * Wrap with brackets
     * 
     * @param teamJSON
     * @return
     */
    private String wrapBrackets(String s) {
        return "{" + s + "}";
    }

    /**
     * Test single clarification JSON line.
     * 
     * @throws Exception
     */
    public void testClarificationJSON() throws Exception {

        IInternalContest contest = new UnitTestData().getContest();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(contest);
        JSONTool jsonTool = new JSONTool(contest, null);


        Clarification[] clarifications = contest.getClarifications();
        Arrays.sort(clarifications, new ClarificationComparator());

        Clarification clarification = clarifications[4];
        ClarificationAnswer clarAnswer = null;
        if (clarification.isAnswered()) {
            ClarificationAnswer[] clarificationAnswers = clarification.getClarificationAnswers();
            clarAnswer = clarificationAnswers[clarificationAnswers.length - 1];
        }
        String json = eventFeedJSON.getClarificationJSON(contest, clarification, clarAnswer);

        int problemNumber = EventFeedJSON.getProblemIndex(contest, clarification.getProblemId());
        assertEquals("Expected problem number ", 1, problemNumber);

//        System.out.println("debug clar json = " + json);

        // debug json = {"id":"5", "from_team_id":"5", "to_team_id":"5", "reply_to_id": null, "problem_id":"1", "text":"Why #2? from team5", "start_time": null, "start_contest_time":"0.000"}

        assertEqualJSON(json, "text", "Why #2? from team5");
        assertEqualJSON(json, "reply_to_id", "null");
        assertEqualJSON(json, "problem_id", jsonTool.getProblemId(contest.getProblem(clarification.getProblemId())));

        assertJSONStringValue(json, "problem_id", jsonTool.getProblemId(contest.getProblem(clarification.getProblemId())));
        assertJSONStringValue(json, "id", clarification.getElementId().toString());
        assertJSONStringValue(json, "from_team_id", "5");
        assertJSONNullValue(json, "to_team_id");
    }

    private void assertJSONNullValue(String json, String fieldname) {
        String regex = "\"" + fieldname + "\":null";
        assertEquals("Expected to find JSON string syntax, double quoted value, for field " + fieldname, 1, matchCount(regex, json));
    }

    /**
     * Tests whether value for field is a string and value matches exptectedFieldValue.
     * 
     * Surround field and value with double quotes, make into JSON.
     * 
     * @param matchCount
     *            number of expected match for field and value
     * @param fieldname
     * @param exptectedFieldValue
     * @param json
     */
    private void assertJSONStringValue(String json, String fieldname, String exptectedFieldValue) {

        String regex = "\"" + fieldname + "\":\"" + exptectedFieldValue + "\"";
        assertEquals("Expected to find JSON string syntax, double quoted value, for field " + fieldname, 1, matchCount(regex, json));
    }

    public void testContestJSON() throws Exception {
        IInternalContest contest = new UnitTestData().getContest();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(contest);
        String json = eventFeedJSON.getContestJSONFields(contest);

        // System.out.println("debug cont json = "+json);

        // {"id":"Pdf9051a6-c092-4d3b-abda-04e362a60a77", "name":"Programming Contest", "formal_name":"Programming Contest", "start_time": null, "duration":"5:00:00",
        // "scoreboard_freeze_duration":"01:00:00", "penalty_time":20, "state":{"state.running":false, "state.frozen":false, "state.final":false}}

        asertPresentJSON(json, "id");
        asertPresentJSON(json, "name");

        assertEqualJSON(json, "name", "Programming Contest");
        assertEqualJSON(json, "duration", "5:00:00");
        assertEqualJSON(json, "scoreboard_freeze_duration", "01:00:00");
        assertEqualJSON(json, "penalty_time", "20");

    }

    public void testProblemJSON() throws Exception {
        IInternalContest contest = new UnitTestData().getContest();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(contest);

        Problem problem = contest.getProblems()[0];
        String json = eventFeedJSON.getProblemJSON(contest, problem, 3);

        // System.out.println("debug prob json = "+json);

        // debug prob json = {"id":"3", "label":"A", "name":"Sumit", "ordinal":3, "test_data_coun":0}

        assertEqualJSON(json, "id", "sumit");
        assertJSONStringValue(json, "id", "sumit");

        assertEqualJSON(json, "label", "A");
        assertEqualJSON(json, "name", "Sumit");
        assertEqualJSON(json, "ordinal", "3");
    }

    public void testTeamMemberJSON() throws Exception {
        IInternalContest contest = new UnitTestData().getContest();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(contest);

        Account account = getAccounts(contest, Type.TEAM)[8];
        String[] names = account.getMemberNames();
        assertNotNull(names);
        assertNotNull(names[0]);

        String json = eventFeedJSON.getTeamMemberJSON(contest, account, names[0]);
        json = wrapBrackets(json);

        // System.out.println("debug team name "+json);

        // debug team name {"id": null, "team_id":"9", "icpc_id": null, "first_name": null, "last_name": null, "sex": null, "role": null}

        // assertEqualJSON(json, "id", "3");
        // assertJSONStringValue(json, "id", "3");

        assertEqualJSON(json, "team_id", "9");
        assertJSONStringValue(json, "team_id", "9");

        // assertJSONStringValue(json, "icpc_id", "9");
        // assertJSONStringValue(json, "first_name", "9");
        // assertJSONStringValue(json, "last_name", "9");
        // assertJSONStringValue(json, "sex", "9");

    }

    public void testLanguageJSON() throws Exception {
        IInternalContest contest = new UnitTestData().getContest();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(contest);

        Language language = contest.getLanguages()[5];
        int langNumber = JSONUtilities.getLanguageIndex(contest, language.getElementId());
        assertEquals(6, langNumber);
        String json = eventFeedJSON.getLanguageJSON(contest, language);

//        System.out.println("debug lang json = "+json);
        
//        editFile(writeFile(new File("/tmp/stuf." + System.currentTimeMillis() + ".json"), json));

        // {"id":3, "name":"Java"}
        JSONTool jsonTool= new JSONTool(contest, null);
        assertEqualJSON(json, "id", jsonTool.getLanguageId(language));
        assertJSONStringValue(json, "id", jsonTool.getLanguageId(language));
        assertEqualJSON(json, "name", "APL");
    }

    /**
     * Assert that JSON field has value.
     * 
     * Parses JSON, compares expected valu eot actual value.
     * 
     * @param json
     * @param fieldName
     *            - field name
     * @param expectedValue
     *            - expected value
     */
    private void assertEqualJSON(String json, String fieldName, String expectedValue) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        try {

            JsonNode rootNode = objectMapper.readTree(json);
            String value = rootNode.path(fieldName).asText();
            assertEquals("Expected for field <" + fieldName + "> value", expectedValue, value);

        } catch (JsonParseException e) {
            System.out.println("Trouble trying to check " + e.getMessage()); // TODO better message
            throw e;
        }

    }

    private void validateJSON(String json) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        // String filename = "/tmp/stuf.validateJSON.json";
        // writeFile(new File(filename), json);

        try {

            JsonNode rootNode = objectMapper.readTree(json);
            assertNotNull("Expecting parsed json root node, rootNode", rootNode);

        } catch (JsonParseException e) {
            System.out.println(json);
            System.out.println("Trouble trying to validate JSON " + e.getMessage()); // TODO better message
            throw e;
        }

    }

    /**
     * Assert that JSON field has a value/is preent
     * 
     * Parses JSON, compares expected valu eot actual value.
     * 
     * @param json
     * @param fieldName
     *            - field name
     */
    private void asertPresentJSON(String json, String fieldName) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        try {

            JsonNode rootNode = objectMapper.readTree(json);
            String value = rootNode.path(fieldName).asText();
            assertNotNull("Expected for field <" + fieldName + "> value", value);

        } catch (JsonParseException e) {
            System.out.println("Trouble trying to check " + e.getMessage()); // TODO better message
            throw e;
        }

    }

    public void testconvertToMs() throws Exception {

        long ms;
        String hhmmss;

        hhmmss = "00";
        ms = JSONUtilities.convertToMs(hhmmss);
        assertEquals("Expecting ms for " + hhmmss, 0, ms);

        hhmmss = "00:00:00";
        ms = JSONUtilities.convertToMs(hhmmss);
        assertEquals("Expecting ms for " + hhmmss, 0, ms);

        hhmmss = "00:00:01";
        ms = JSONUtilities.convertToMs(hhmmss);
        assertEquals("Expecting ms for " + hhmmss, 1000, ms);

        hhmmss = "34:00";
        ms = JSONUtilities.convertToMs(hhmmss);
        assertEquals("Expecting ms for " + hhmmss, 2040000, ms);

        hhmmss = "01:00:00";
        ms = JSONUtilities.convertToMs(hhmmss);
        assertEquals("Expecting ms for " + hhmmss, 3600000, ms);

        hhmmss = "04:12:00";
        ms = JSONUtilities.convertToMs(hhmmss);
        assertEquals("Expecting ms for " + hhmmss, 15120000, ms);

        hhmmss = "01:22:00";
        ms = JSONUtilities.convertToMs(hhmmss);
        assertEquals("Expecting ms for " + hhmmss, 4920000, ms);

        hhmmss = "45:00";
        ms = JSONUtilities.convertToMs(hhmmss);
        assertEquals("Expecting ms for " + hhmmss, 2700000, ms);

        hhmmss = "01:00:00";
        ms = JSONUtilities.convertToMs(hhmmss);
        assertEquals("Expecting ms for " + hhmmss, 3600000, ms);
    }

    public void testStartAtContestEvent() throws Exception {

        UnitTestData data = new UnitTestData();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(data.getContest());

        String jsonBefore = new EventFeedJSON(data.getContest()).createJSON(data.getContest());

        EventFeedFilter filter = new EventFeedFilter(EventFeedJSON.getEventId(0), null);
        eventFeedJSON.setEventIdSequence(0);
        String json = eventFeedJSON.createJSON(data.getContest(), filter);

        // eventFeedJSON.setEventIdSequence(0);
        // writeFile(new File("/tmp/stuf1"), eventFeedJSON.createJSON(data.getContest()));
        // writeFile(new File("/tmp/stuf2"), json);

        // System.out.println("debug  start at json "+json);

        assertNotNull(json);

        assertCountEvent(1, EventFeedJSON.CONTEST_KEY, json);

        assertEquals("Expected JSON length when started with event 1 (contest event) ", jsonBefore.length(), json.length());
    }

    public void testStartAtEvent40() throws Exception {

        UnitTestData data = new UnitTestData();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(data.getContest());

        EventFeedFilter filter = new EventFeedFilter(EventFeedJSON.getEventId(39), null);
        String json = eventFeedJSON.createJSON(data.getContest(), filter);

        // System.out.println("debug after event 40 json = "+json);

        assertNotNull(json);

        assertMatchCount(529, "\"type\"", json);

    }

    /**
     * Test bad event type names, ensure method throws an exception.
     * 
     * @throws Exception
     */
    public void testInvalidEventTypes() throws Exception {

        UnitTestData data = new UnitTestData();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(data.getContest());

        String[] badTypeNameLists = {
                //
                "a,b,c", //
                "contest,teams,bogus", //
                "unk,contest,teams", //
                "bad", //
                "23423499afsdfdf,34343,contest", //
        };

        for (String badbadlist : badTypeNameLists) {
            try {
                eventFeedJSON.setEventTypeList(badbadlist);
                eventFeedJSON.createJSON(data.getContest());
                fail("Expecting IllegalArgumentException for list '" + badbadlist + "'");
            } catch (IllegalArgumentException e) {

                ; // Expected results, this passes the test
            }
        }

    }

    /**
     * Test valid event types.
     * 
     * @throws Exception
     */
    public void testValidEventTypes() throws Exception {

        UnitTestData data = new UnitTestData();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(data.getContest());

        String elist = EventFeedJSON.CONTEST_KEY + "," + EventFeedJSON.TEAM_KEY;

        EventFeedFilter filter = new EventFeedFilter(null, elist);
        String json = eventFeedJSON.createJSON(data.getContest(), filter);
        assertNotNull(json);

        assertCountEvent(1, EventFeedJSON.CONTEST_KEY, json);
        assertCountEvent(120, EventFeedJSON.TEAM_KEY, json);
        assertMatchCount(120, "icpc_id", json);
    }

    /**
     * Test valid event types.
     * 
     * @throws Exception
     */
    public void testLotsOfValidTypes() throws Exception {

        UnitTestData data = new UnitTestData();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(data.getContest());

        String elist = JSONUtilities.AWARD_KEY + "," + //
                JSONUtilities.CLARIFICATIONS_KEY + "," + //
                JSONUtilities.CONTEST_KEY + "," + //
                JSONUtilities.GROUPS_KEY + "," + //
                JSONUtilities.JUDGEMENT_KEY + "," + //
                JSONUtilities.JUDGEMENT_TYPE_KEY + "," + //
                JSONUtilities.LANGUAGE_KEY + "," + //
                JSONUtilities.ORGANIZATION_KEY + "," + //
                JSONUtilities.TEAM_MEMBERS_KEY + "," + //
                JSONUtilities.PROBLEM_KEY + "," + //
                JSONUtilities.RUN_KEY + "," + //
                JSONUtilities.SUBMISSION_KEY + "," + //
                JSONUtilities.TEAM_KEY + "," + //
                JSONUtilities.TEAM_MEMBERS_KEY;

        // TODO RMeventFeedJSON.setEventTypeList(elist);

        EventFeedFilter filter = new EventFeedFilter(null, elist);
        String json = eventFeedJSON.createJSON(data.getContest(), filter);
        assertNotNull(json);

        assertCountEvent(1, JSONUtilities.CONTEST_KEY, json);
        assertCountEvent(100, JSONUtilities.CLARIFICATIONS_KEY, json);
        assertCountEvent(300, JSONUtilities.TEAM_MEMBERS_KEY, json);
        assertCountEvent(120, JSONUtilities.TEAM_KEY, json);
        assertCountEvent(6, JSONUtilities.LANGUAGE_KEY, json);
        assertCountEvent(12, JSONUtilities.JUDGEMENT_KEY, json);
        assertCountEvent(9, JSONUtilities.JUDGEMENT_TYPE_KEY, json);
    }

    public void testEventTypeNotFound() throws Exception {
        UnitTestData data = new UnitTestData();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(data.getContest());

        String elist = EventFeedJSON.AWARD_KEY + "," + //
                EventFeedJSON.CLARIFICATIONS_KEY;

        // TODO RMeventFeedJSON.setEventTypeList(elist);

        EventFeedFilter filter = new EventFeedFilter(null, elist);
        String json = eventFeedJSON.createJSON(data.getContest(), filter);

        assertNotNull(json);

        assertCountEvent(0, EventFeedJSON.CONTEST_KEY, json);
        assertCountEvent(100, EventFeedJSON.CLARIFICATIONS_KEY, json);
        assertCountEvent(0, EventFeedJSON.TEAM_MEMBERS_KEY, json);
        assertCountEvent(0, EventFeedJSON.TEAM_KEY, json);
        assertCountEvent(0, EventFeedJSON.LANGUAGE_KEY, json);
        assertCountEvent(0, EventFeedJSON.JUDGEMENT_KEY, json);
        assertCountEvent(0, EventFeedJSON.JUDGEMENT_TYPE_KEY, json);
    }


}
