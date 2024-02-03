// Copyright (C) 1989-2022 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.services.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.csus.ecs.pc2.clics.CLICSJudgementType;
import edu.csus.ecs.pc2.core.IInternalController;
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
import edu.csus.ecs.pc2.core.model.ContestInformation;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.FinalizeData;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
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
public class JSONToolTest extends AbstractTestCase {

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
            String[] runsData = {
                    "1,1,A,1,No,No,4", // 0 (a No before first yes Security Violation)
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
            addTeamMembers(contest, getTeamAccounts(contest).length / 2, 5);

            assertEquals("Expectig team member names", 5, getFirstAccount(contest, Type.TEAM).getMemberNames().length);

            assertEquals("team count", 120, contest.getAccounts(Type.TEAM).size());

            for (String runInfoLine : runsData) {
                SampleContest.addRunFromInfo(contest, runInfoLine);
            }

            Problem problem = contest.getProblems()[0];
            Account judge = getFirstAccount(contest, Type.JUDGE);
            generateClarifications(contest, 20, problem, judge.getClientId(), false, false);
            generateClarifications(contest, 20, problem, judge.getClientId(), true, false);
            generateClarifications(contest, 20, problem, judge.getClientId(), true, true);

            sampleContest.assignSampleGroups(contest, "North Group", "South Group");

            assertEquals("Runs", 12, contest.getRuns().length);

        }

        /**
         * Get contest populated with test data.
         * @return
         */
        public IInternalContest getContest() {
            return contest;
        }

    }

    public void simpleTest() throws Exception {

        EventFeedJSON eventFeedJSON = new EventFeedJSON(null);
        IInternalContest contest = new SampleContest().createStandardContest();

        String json = eventFeedJSON.createJSON(contest, null, null, null);

        assertNotNull(json);

    }

    /**
     * Test every name and value that event feed JSON can output.
     *
     */
    public void testCompleteEventFeed() throws Exception {

        UnitTestData data = new UnitTestData();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(data.getContest());

        String json = eventFeedJSON.createJSON(data.getContest(), null, null);

//        editFile(writeFile(new File("/tmp/stuf." + System.currentTimeMillis() + ".json"), json));

        assertNotNull(json);
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
//                EventFeedJSON.RUN_KEY + ":    12", //  SOMEDAY
                EventFeedJSON.SUBMISSION_KEY + ": 12", //

                EventFeedJSON.TEAM_MEMBERS_KEY + ":  300", //

//                EventFeedJSON.ORGANIZATION_KEY + ":  12",
//                EventFeedJSON.AWARD_KEY + ":  1", //
        };

        for (String line : eventCounts) {
            String[] fields = line.split(":");
            String name = fields[0];
            int value = Integer.parseInt(fields[1].trim());
            assertCountEvent(value, name, json);
        }

        assertMatchCount(12, "\"judgement_type_id\"", json);
        assertMatchCount(422, "\"icpc_id\"", json);

        Run[] runs = data.getContest().getRuns();
        assertMatchCount(runs.length, "\"entry_point\"", json);

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
     * @param count number of accounts to add team names to.
     * @param numberOnMtea number of team names per team
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

                String[] newNames = names.toArray(new String[names.size()]);
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

        //      return stringArray[0]; // return first name

        Random random = new Random();
        int nameIndex = random.nextInt(stringArray.length);
        return stringArray[nameIndex];
    }

    private void assertMatchCount(int count, String regex, String json) {
        assertEquals("Expecting to find "+count+ " matches for '" + regex+"'", count, matchCount(regex, json));
    }

    /**
     * Expect count of elementName in JSON.
     *
     * @param exepectedCount
     * @param eleementName
     * @param json
     */
    private void assertCountEvent(int exepectedCount, String eleementName, String json) {

//      System.out.println("debug "+eleementName+" "+matchEventCount(eleementName, json));
        assertEquals("For event '" + eleementName + "' expecting count", exepectedCount, matchEventCount(eleementName, json));
    }

    /**
     * Return count of events in json.
     *
     * @param eleementName element to match
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
     * @param answerAll add an answer for every generated clarification
     * @param sendToAll mark clarifications send to All.
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
     * Get all  accounts (all sites).
     */
    public static Account[] getAllAccounts(IInternalContest contest, ClientType.Type type) {
        // SOMEDAY move getAllAccounts into AccountsUtility class

        Vector<Account> accountVector = contest.getAccounts(type);
        Account[] accounts = accountVector.toArray(new Account[accountVector.size()]);
        Arrays.sort(accounts, new AccountComparator());

        return accounts;
    }

    /**
     * Tests Run JSON.
     *
     * @throws Exception
     */
    public void testSubmissionJSON() throws Exception {

        IInternalContest contest = new UnitTestData().getContest();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(contest);

        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparator());
        for (Run run : runs) {
            String json = eventFeedJSON.getSubmissionJSON(contest,run, null, null);
            String expecteInt = Integer.toString(run.getNumber());
            asertEqualJSON(json, "id", expecteInt);
        }
    }

    /**
     * Test team and group JSON.
     *
     * @throws Exception
     */
    public void testTeamAndGroupJSON() throws Exception {

        IInternalContest contest = new UnitTestData().getContest();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(contest);

        Account[] accounts = getAccounts(contest, Type.TEAM);

        Account account = accounts[0];

        String json = eventFeedJSON.getTeamJSON(contest, account);

//        System.out.println("debug team json = "+json);

        //  debug team json = {"id":"1", "icpc_id":"3001", "name":"team1", "organization_id": null, "group_id":"1024"}

        asertEqualJSON(json, "id", "1");
        asertEqualJSON(json, "name", "team1");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode readTree = mapper.readTree(json);
        List<JsonNode> findValues = readTree.findValues("group_ids");
        assertEquals("matching group ids", "1024", findValues.get(0).elements().next().asText());

        assertJSONStringValue(json, "id", "1");
        assertJSONStringValue(json, "icpc_id", "3001");
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


        Group group = groups[0];
        String json = eventFeedJSON.getGroupJSON(contest, group);

//         System.out.println("debug group json = "+json);

        // debug group json = {"id":1024, "icpc_id":1024, "name":"North Group"}

        //  {"id":1, "icpc_id":"3001", "name":"team1", "organization_id": null}

        asertEqualJSON(json, "id",  "1024");

        assertJSONStringValue(json,  "id",  "1024");
        assertJSONStringValue(json,  "icpc_id", "1024");
        assertJSONStringValue(json,  "name", "North Group");
    }

    /**
     * Wrap with brackets
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
        JSONTool jsonTool= new JSONTool(contest, null);

        Clarification[] clarifications = contest.getClarifications();
        Arrays.sort(clarifications, new ClarificationComparator());

        Clarification clarification = clarifications[4];
        ClarificationAnswer answer = null;
        String json = jsonTool.convertToJSON(clarification, answer ).toString();

        int problemNumber = JSONUtilities.getProblemIndex(contest, clarification.getProblemId());
        assertEquals("Expected problem number ", 1, problemNumber);

//        editFile(writeFile(new File("/tmp/stuf." + System.currentTimeMillis() + ".json"), json));

        //        System.out.println("debug clar json = "+json);

        // debug json = {"id":"5", "from_team_id":"5", "to_team_id":"5", "reply_to_id": null, "problem_id":"1", "text":"Why #2? from team5", "start_time": null, "start_contest_time":"0.000"}

        asertEqualJSON(json, "text", "Why #2? from team5");
        asertEqualJSON(json, "reply_to_id", "null");
        asertEqualJSON(json, "problem_id", JSONTool.getProblemId(contest.getProblem(clarification.getProblemId())));

//        assertJSONStringValue(json, "problem_id", "1");  SOMEDAY
        assertJSONStringValue(json, "problem_id", JSONTool.getProblemId(contest.getProblem(clarification.getProblemId())));
//        assertJSONStringValue(json, "id", "5"); SOMEDAY
        assertJSONStringValue(json, "id", clarification.getElementId().toString());
        assertJSONStringValue(json,  "from_team_id", "5" );
        assertJSONNullValue(json,  "to_team_id");
    }

    private void assertJSONNullValue(String json, String fieldname) {
        String regex = "\"" + fieldname + "\":null";
        assertEquals("Expected to find JSON string syntax, double quoted value, for field "+fieldname, 1, matchCount(regex, json));
    }

    /**
     * Tests whether value for field is a string and value matches exptectedFieldValue.
     *
     * Surround field and value with double quotes, make into JSON.
     *
     * @param matchCount number of expected match for field and value
     * @param fieldname
     * @param exptectedFieldValue
     * @param json
     */
    private void assertJSONStringValue (String json, String fieldname, String exptectedFieldValue) {

        String regex = "\"" + fieldname + "\":\"" + exptectedFieldValue + "\"";
//        throw new ComparisonFailure("Expected double quoted value \""+exptectedFieldValue+"\" value in json ", 1, matchCount(regex, json));
        assertTrue("Expected to find JSON string syntax, double quoted value \""+exptectedFieldValue+"\", for field \""+fieldname+"\"", 1 == matchCount(regex, json));
    }
    /**
     * Tests whether value for field is a int and value matches exptectedFieldValue.
     *
     * @param matchCount number of expected match for field and value
     * @param fieldname
     * @param exptectedFieldValue
     * @param json
     */
    private void assertJSONIntValue (String json, String fieldname, int exptectedFieldValue) {

        String regex = "\"" + fieldname + "\":" + exptectedFieldValue;
//        throw new ComparisonFailure("Expected double quoted value \""+exptectedFieldValue+"\" value in json ", 1, matchCount(regex, json));
        assertTrue("Expected to find JSON int syntax, value \""+exptectedFieldValue+"\", for field \""+fieldname+"\"", 1 == matchCount(regex, json));
    }

    public void testContestJSON() throws Exception {
        IInternalContest contest = new UnitTestData().getContest();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(contest);

        String json = eventFeedJSON.getContestJSONFields(contest);
        //        System.out.println("debug cont json = "+json);

        //         {"id":"Pdf9051a6-c092-4d3b-abda-04e362a60a77", "name":"Programming Contest", "formal_name":"Programming Contest", "start_time": null, "duration":"5:00:00",
        // "scoreboard_freeze_duration":"01:00:00", "penalty_time":20, "state":{"state.running":false, "state.frozen":false, "state.final":false}}

        asertPresentJSON(json, "id");
        asertPresentJSON(json, "name");

        asertEqualJSON(json, "name", "Programming Contest");
        asertEqualJSON(json, "duration", "5:00:00");
        asertEqualJSON(json, "scoreboard_freeze_duration", "01:00:00");
        asertEqualJSON(json, "penalty_time", "20");

    }

    public void testProblemJSON() throws Exception {
        IInternalContest contest = new UnitTestData().getContest();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(contest);


        Problem problem = contest.getProblems()[0];
        String json = eventFeedJSON.getProblemJSON(contest, problem, 3);

        // System.out.println("debug prob json = "+json);

        // debug prob json = {"id":"3", "label":"A", "name":"Sumit", "ordinal":3, "test_data_coun":0}

        asertEqualJSON(json, "id", "sumit");
        assertJSONStringValue(json,  "id", "sumit");

        asertEqualJSON(json, "label", "A");
        asertEqualJSON(json, "name", "Sumit");
        asertEqualJSON(json, "ordinal", "3");
    }

    public void testTeamMemberJSON() throws Exception{
        IInternalContest contest = new UnitTestData().getContest();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(contest);

        Account account = getAccounts(contest, Type.TEAM)[8];
        String[] names = account.getMemberNames();
        assertNotNull(names);
        assertNotNull(names[0]);

        String json = eventFeedJSON.getTeamMemberJSON(contest, account, names[0]);
        json = wrapBrackets(json);

//        editFile(writeFile(new File("/tmp/stuf." + System.currentTimeMillis() + ".json"), json));
//         System.out.println("debug team member "+json);

        // debug team name {"id": null, "team_id":"9", "icpc_id": null, "first_name": null, "last_name": null, "sex": null, "role": null}

//        asertEqualJSON(json, "id", "3");
//        assertJSONStringValue(json,  "id", "3");

      asertEqualJSON(json, "team_id", "9");
      assertJSONStringValue(json,  "team_id", "9");

//      assertJSONStringValue(json,  "icpc_id", "9");
//      assertJSONStringValue(json,  "first_name", "9");
//      assertJSONStringValue(json,  "last_name", "9");
//      assertJSONStringValue(json,  "sex", "9");

    }

    public void testLanguageJSON() throws Exception {
        IInternalContest contest = new UnitTestData().getContest();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(contest);

        Language language = contest.getLanguages()[2];
        String json = eventFeedJSON.getLanguageJSON(contest, language);

//         System.out.println("debug lang json = "+json);

        //  {"id": "java", "name":"Java"}
        JSONTool jsonTool= new JSONTool(contest, null);

        asertEqualJSON(json, "id", JSONTool.getLanguageId(language));
        assertJSONStringValue(json,  "id", JSONTool.getLanguageId(language));
        asertEqualJSON(json, "name", "GNU C++ (Unix / Windows)");
    }

    /**
     * Assert that JSON field has value.
     *
     * Parses JSON, compares expected valu eot actual value.
     *
     * @param json
     * @param fieldName - field name
     * @param expectedValue - expected value
     */
    private void asertEqualJSON(String json, String fieldName, String expectedValue) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        try {

            JsonNode rootNode = objectMapper.readTree(json);
            String value = rootNode.path(fieldName).asText();
            assertEquals("Expected for field <" + fieldName + "> value", expectedValue, value);

        } catch (JsonParseException e) {
            System.err.println("Unable to compare field '"+fieldName+"' for expected value '"+expectedValue+"' " + e.getMessage());
            throw e;
        }

    }

    private void validateJSON(String json) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

//        String filename = "/tmp/stuf.validateJSON.json";
//        writeFile(new File(filename), json);

        try {

            JsonNode rootNode = objectMapper.readTree(json);
            assertNotNull("Expecting parsed json root node, rootNode", rootNode);

        } catch (JsonParseException e) {
            System.out.println(json);
            System.out.println("Trouble trying to validate JSON " + e.getMessage()); // TODO  better message
            throw e;
        }

    }

    /**
     * Assert that JSON field has a value/is preent
     *
     * Parses JSON, compares expected valu eot actual value.
     *
     * @param json
     * @param fieldName - field name
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
        String jsonBefore = eventFeedJSON.createJSON(data.getContest(), null, null);

        eventFeedJSON.setEventIdSequence(0);
        EventFeedFilter filter = new EventFeedFilter(EventFeedJSON.getEventId(0), null);
        String json = eventFeedJSON.createJSON(data.getContest(), filter, null, null);

//        System.out.println("debug start json "+json);

        assertNotNull(json);

        assertCountEvent(1, EventFeedJSON.CONTEST_KEY, json);

        assertEquals("Expected JSON length when started with event 1 (contest event) ", jsonBefore.length(), json.length());
    }

    public void testStartAfterEvent39() throws Exception {

        UnitTestData data = new UnitTestData();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(data.getContest());

        EventFeedFilter filter = new EventFeedFilter(EventFeedJSON.getEventId(39), null);
        String json = eventFeedJSON.createJSON(data.getContest(), filter, null, null);

//        System.out.println("debug after event 39  json = "+json);

//        editFile(writeFile(new File("/tmp/stuf.ev40." + System.currentTimeMillis() + ".json"), json));

        assertNotNull(json);

        assertMatchCount(530, "\"type\"", json);

    }

    /**
     * Test bad event type names, ensure method throws an exception.
     *
     * @throws Exception
     */
    public void testInvalidEventTypes() throws Exception {


        UnitTestData data = new UnitTestData();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(data.getContest());

        String [] badTypeNameLists = {
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
                eventFeedJSON.createJSON(data.getContest(), null, null);
                fail("Expecting IllegalArgumentException for list '" + badbadlist + "'");
            } catch (IllegalArgumentException e) {

                ; // Expected results, this passes the test
            }
        }


    }

    /**
     * Test valid event types.
     * @throws Exception
     */
    public void testValidEventTypes() throws Exception {

        UnitTestData data = new UnitTestData();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(data.getContest());

        String elist = EventFeedJSON.CONTEST_KEY + "," + EventFeedJSON.TEAM_KEY;
        EventFeedFilter filter = new EventFeedFilter(null, elist);
        String json = eventFeedJSON.createJSON(data.getContest(), filter, null, null);
        assertNotNull(json);

//        System.out.println("debug valid event json "+json);

        assertCountEvent(1, EventFeedJSON.CONTEST_KEY, json);
        assertCountEvent(120, EventFeedJSON.TEAM_KEY, json);
        assertMatchCount(120, "icpc_id", json);
    }

    /**
     * Test valid event types.
     * @throws Exception
     */
    public void testLotsOfValidTypes() throws Exception {

        UnitTestData data = new UnitTestData();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(data.getContest());

        String elist = EventFeedJSON.AWARD_KEY + "," + //
                EventFeedJSON.CLARIFICATIONS_KEY + "," + //
                EventFeedJSON.CONTEST_KEY + "," + //
                EventFeedJSON.GROUPS_KEY + "," + //
                EventFeedJSON.JUDGEMENT_KEY + "," + //
                EventFeedJSON.JUDGEMENT_KEY + "," + //
                EventFeedJSON.JUDGEMENT_TYPE_KEY + "," + //
                EventFeedJSON.LANGUAGE_KEY + "," + //
                EventFeedJSON.CONTEST_KEY + "," + //
                EventFeedJSON.ORGANIZATION_KEY + "," + //
                EventFeedJSON.TEAM_MEMBERS_KEY + "," + //
                EventFeedJSON.PROBLEM_KEY + "," + //
                EventFeedJSON.RUN_KEY + "," + //
                EventFeedJSON.RUN_KEY + "," + //
                EventFeedJSON.SUBMISSION_KEY + "," + //
                EventFeedJSON.CLARIFICATIONS_KEY + "," + //
                EventFeedJSON.TEAM_KEY + "," + //
                EventFeedJSON.TEAM_MEMBERS_KEY;

        eventFeedJSON.setEventTypeList(elist);
        String json = eventFeedJSON.createJSON(data.getContest(), null, null);
        assertNotNull(json);

        // editFile(writeFile(new File("/tmp/stuf." + System.currentTimeMillis() + ".json"), json));

        assertCountEvent(2, EventFeedJSON.CONTEST_KEY, json);
        assertCountEvent(200, EventFeedJSON.CLARIFICATIONS_KEY, json);
        assertCountEvent(600, EventFeedJSON.TEAM_MEMBERS_KEY, json);
        assertCountEvent(120, EventFeedJSON.TEAM_KEY, json);
        assertCountEvent(6, EventFeedJSON.LANGUAGE_KEY, json);
        assertCountEvent(24, EventFeedJSON.JUDGEMENT_KEY, json);
        assertCountEvent(9, EventFeedJSON.JUDGEMENT_TYPE_KEY, json);
    }


    /**
     * Test team and submission event types.
     *
     * @throws Exception
     */
    public void testTeamEventType() throws Exception {

        UnitTestData data = new UnitTestData();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(data.getContest());

        String elist = EventFeedJSON.SUBMISSION_KEY + "," + //
                EventFeedJSON.TEAM_KEY;

        eventFeedJSON.setEventTypeList(elist);
        String json = eventFeedJSON.createJSON(data.getContest(), null, null);
        assertNotNull(json);

        // editFile(writeFile(new File("/tmp/stuf." + System.currentTimeMillis() + ".json"), json));

        assertCountEvent(0, EventFeedJSON.CONTEST_KEY, json);
        assertCountEvent(0, EventFeedJSON.CLARIFICATIONS_KEY, json);
        assertCountEvent(0, EventFeedJSON.TEAM_MEMBERS_KEY, json);
        assertCountEvent(120, EventFeedJSON.TEAM_KEY, json);
        assertCountEvent(0, EventFeedJSON.JUDGEMENT_TYPE_KEY, json);

        /**
         * Run test of filter a second time.
         */

        json = eventFeedJSON.createJSON(data.getContest(), null, null);
        assertNotNull(json);

        assertCountEvent(0, EventFeedJSON.CONTEST_KEY, json);
        assertCountEvent(0, EventFeedJSON.CLARIFICATIONS_KEY, json);
        assertCountEvent(0, EventFeedJSON.TEAM_MEMBERS_KEY, json);
        assertCountEvent(120, EventFeedJSON.TEAM_KEY, json);
        assertCountEvent(0, EventFeedJSON.JUDGEMENT_TYPE_KEY, json);

    }

    public void testEventTypeNotFound() throws Exception {
        UnitTestData data = new UnitTestData();
        EventFeedJSON eventFeedJSON = new EventFeedJSON(data.getContest());

        String elist = EventFeedJSON.AWARD_KEY + "," + //
                EventFeedJSON.CLARIFICATIONS_KEY;

        EventFeedFilter filter = new EventFeedFilter(null, elist);
        String json = eventFeedJSON.createJSON(data.getContest(), filter, null, null);
        assertNotNull(json);

        // editFile(writeFile(new File("/tmp/stuf." + System.currentTimeMillis() + ".json"), json));

        assertCountEvent(0, EventFeedJSON.CONTEST_KEY, json);
        assertCountEvent(100, EventFeedJSON.CLARIFICATIONS_KEY, json);
        assertCountEvent(0, EventFeedJSON.TEAM_MEMBERS_KEY, json);
        assertCountEvent(0, EventFeedJSON.TEAM_KEY, json);
        assertCountEvent(0, EventFeedJSON.LANGUAGE_KEY, json);
        assertCountEvent(0, EventFeedJSON.JUDGEMENT_KEY, json);
        assertCountEvent(0, EventFeedJSON.JUDGEMENT_TYPE_KEY, json);
    }

    public void testConvertToJSONGroup() throws Exception {

        IInternalContest contest = new UnitTestData().getContest();JSONTool jsonTool= new JSONTool(contest, null);

        Group group = new Group("Group T1");
        group.setGroupId(2323);
        group.setSite(contest.getSite(contest.getSiteNumber()).getElementId());
        String json = jsonTool.convertToJSON(group).toString();
//        System.out.println("debug group json "+json);
        // debug json group {"id":"Group_T1-1621487835425380678","icpc_id":"2323","name":"Group T1"}

        assertJSONStringValue(json, "id",  "2323");
        assertJSONStringValue(json, "name", "Group T1");
        assertJSONStringValue(json, "icpc_id", "2323");
    }

    public void testConvertToJSONClarification() throws Exception {

        IInternalContest contest = new UnitTestData().getContest();
        JSONTool jsonTool = new JSONTool(contest, null);

        Clarification[] clarifications = contest.getClarifications();
        Arrays.sort(clarifications, new ClarificationComparator());

        int clarNum = 4;

        Clarification clarification = clarifications[clarNum - 1];
        String json = jsonTool.convertToJSON(clarification, null).toString();
        //        System.out.println("debug clar 4 "+json);

        // debug clar 4 {"id":"Clarification--1638909146661829105","from_team_id":"4","to_team_id":null,"reply_to_id":null,"problem_id":"Sumit-6203214424750787208",
        // "text":"Why #2? from team4","time":"2017-10-25T10:58:08.527-07","contest_time":"00:00:00.000"}

        assertJSONStringValue(json, "id", clarification.getElementId().toString()); // SOMEDAY
        assertJSONStringValue(json, "from_team_id", "4");
        assertJSONStringValue(json, "problem_id", JSONTool.getProblemId(contest.getProblem(clarification.getProblemId())));
        assertJSONStringValue(json, "text", "Why #2. from team4");

    }

    public void testConvertToJSONContestInformation() throws Exception {
        IInternalContest contest = new UnitTestData().getContest();
        JSONTool jsonTool = new JSONTool(contest, null);
        ContestInformation contestInformation = contest.getContestInformation();
          String json = jsonTool.convertToJSON(contestInformation).toString();
//          System.out.println("debug contest info json "+json);
          // debug contest info json {"id":"Default.-3848727515497618657","name":"Programming Contest",
          // "formal_name":"Programming Contest","start_time":null,"duration":"5:00:00","scoreboard_freeze_duration":"01:00:00","penalty_time":"20","state":{"running":false,"frozen":false,"final":false}}

          assertJSONStringValue(json, "id", contest.getContestIdentifier());
          assertJSONStringValue(json, "formal_name", "Programming Contest");
          assertJSONStringValue(json, "name", "Programming Contest");

          assertJSONIntValue(json, "penalty_time", 20);


    }

    public void testConvertToJSONJudgement() throws Exception {
        IInternalContest contest = new UnitTestData().getContest();
        JSONTool jsonTool = new JSONTool(contest, null);
        Judgement judgement = contest.getJudgements()[4];

        String json = jsonTool.convertToJSON(judgement).toString();
        //        System.out.println("debug judgement "+json);
        // debug judgement {"id":"WA2","name":"ave no clue","penalty":true,"solved":false}

        assertJSONStringValue(json, "id", "WA2");
        assertJSONStringValue(json, "name", "ave no clue");
        assertJSONBooleanValue(json, "penalty", true);
        assertJSONBooleanValue(json, "solved", false);

    }

    private void assertJSONBooleanValue(String json, String fieldname, boolean exptectedFieldValue) {
        String regex = "\"" + fieldname + "\":" + exptectedFieldValue;
        assertTrue("Expected to find JSON boolean value " + exptectedFieldValue + " for field \"" + fieldname + "\"", 1 == matchCount(regex, json));
    }

    public void testConvertToJSONAccount() throws Exception {
        IInternalContest contest = new UnitTestData().getContest();
        JSONTool jsonTool = new JSONTool(contest, null);
        Account[] accounts = getTeamAccounts(contest);
        Arrays.sort(accounts, new AccountComparator());

        int teamNum = 5;

        Account account = accounts[teamNum - 1];

        String json = jsonTool.convertToJSON(account).toString();

        //        System.out.println("debug team account 5"+json);
        // debug team account 5{"id":"5","icpc_id":"3005","name":"team5","group_id":"North_Group--7306185641154577475"}

        assertNotNull("Expecting json for account ", json);

        assertJSONStringValue(json, "id", "5");
        assertJSONStringValue(json, "icpc_id", "3005");
        assertJSONStringValue(json, "name", "team5");
        //        assertJSONStringValue(json,  "group_id", "5");  SOMEDAY REFACTOR test find and compare group_id

    }

    public void testConvertToJSONRunTestCase() throws Exception {

        // TODO testConvertToJSONRunTestCase
//        IInternalContest contest = new UnitTestData().getContest();
//        JSONTool jsonTool = new JSONTool(contest, null);
//        String json =  jsonTool.convertToJSON(RunTestCase[] runTestCases, int ordinal) {
//        fail();
    }


    protected FinalizeData createFinalizeData(int numberGolds, int numberSilvers, int numberBronzes) {
        FinalizeData data = new FinalizeData();
        data.setGoldRank(numberGolds);
        data.setSilverRank(numberSilvers);
        data.setBronzeRank(numberBronzes);
        data.setComment("Finalized by Director of Operations, no, really!");
        return data;
    }

    /**
     * Test "ended" calculation.
     *
     * Tests: https://github.com/pc2ccs/pc2v9/issues/325
     *
     * @throws Exception
     */

    public void testtoStateJSONEnded() throws Exception {

        SampleContest sampleContest = new SampleContest();
        IInternalContest contest = sampleContest.createStandardContest();
        IInternalController controller = sampleContest.createController(contest, true, false);

        contest.getContestTime().startContestClock();
        contest.getContestTime().stopContestClock();
        contest.getContestTime().setRemainingSecs(0);

        FinalizeData data = createFinalizeData(4, 4, 5);
        data.setCertified(true);
        contest.setFinalizeData(data);

        JSONTool tool = new JSONTool(contest, controller);

        ObjectNode rootNode = tool.toStateJSON(contest.getContestInformation());

        assertNotNull(rootNode);

        String json = rootNode.toString();

        JsonNode endNode = rootNode.get("ended");
        assertNotNull("Did not find ended element in json: " + json, endNode);

        // check ended value
        ContestTime contestTime = contest.getContestTime();
        Calendar contestStart = contestTime.getContestStartTime();

        long contestEndMS = contestStart.getTimeInMillis() + contestTime.getContestLengthMS();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(contestEndMS);
        Date date = calendar.getTime();

        SimpleDateFormat iso8601formatterWithMS = new SimpleDateFormat(Utilities.ISO_8601_TIMEDATE_FORMAT_WITH_MS);
        String iso8601DateString = iso8601formatterWithMS.format(date);

        String endValue = endNode.textValue();

        /**
         * Compare dates but not the times.
         */
        String actual = iso8601DateString.substring(0, 10); // only YYYY-MM-DD
        String expected = endValue.substring(0, 10); // only YYYY-MM-DD

        // compare YYYY-MM-DD
        assertEquals("Expected contest end date value", expected, actual);

        // compare full date string, ex. 2022-04-17T00:03:29.280-07
        assertEquals("Expected contest end date value", iso8601DateString, endValue);

    }


    /**
     * List of override judgements from CLICSJudgementType.
     *
     * @return list of override judgements from {@link CLICSJudgementType} judgementStringMappings
     */
    String [] getJudgementMappingList() {

        String[] JudgementMappingList = {
                "Accepted", //
                "Yes", //
                "Correct", //
                "Rejected", //
                "Incorrect", //
                "No", //
                "Wrong Answer", //
                "No - Wrong Answer", //
                "Time Limit Exceeded", //
                "Time-Limit Exceeded", //
                "No - Time Limit Exceeded", //
                "No - Time-Limit Exceeded", //
                "Run Time Error", //
                "Runtime Error", //
                "Run-Time Error", //
                "No - Run Time Error", //
                "No - Runtime Error", //
                "No - Run-Time Error", //
                "Compile Error", //
                "Compiler Error", //
                "Compilation Error", //
                "No - Compile Error", //
                "No - Compiler Error", //
                "No - Compilation Error", //
                "Accepted - Presentation Error", //
                "Output Limit Exceeded", //
                "No - Output Limit Exceeded", //
                "Presentation Error", //
                "Output Format Error", //
                "Incorrect Output Format", //
                "No - Presentation Error", //
                "No - Output Format Error", //
                "No - Incorrect Output Format", //
                "Excessive Output", //
                "Incomplete Output", //
                "No Output", //
                "Presentation Error", //
                "No - Excessive Output", //
                "No - Incomplete Output", //
                "No - No Output", //
                "No - Presentation Error", //
                "Wallclock Time Limit Exceeded", //
                "Wall-clock Time Limit Exceeded", //
                "Wall Clock Time Limit Exceeded", //
                "No - Wallclock Time Limit Exceeded", //
                "No - Wall-clock Time Limit Exceeded", //
                "No - Wall Clock Time Limit Exceeded", //
                "Idleness Limit Exceeded", //
                "Idle Limit Exceeded", //
                "No - Idleness Limit Exceeded", //
                "No - Idle Limit Exceeded", //
                "Time Limit Exceeded - Correct Output", //
                "Time-Limit Exceeded - Correct Output", //
                "Time Limit Exceeded - Wrong Answer", //
                "Time-Limit Exceeded - Wrong Answer", //
                "Time Limit Exceeded - Presentation Error", //
                "Time-Limit Exceeded - Presentation Error", //
                "Time Limit Exceeded - Excessive Output", //
                "Time-Limit Exceeded - Excessive Output", //
                "Time Limit Exceeded - Incomplete Output", //
                "Time-Limit Exceeded - Incomplete Output", //
                "Time Limit Exceeded - No Output", //
                "Time-Limit Exceeded - No Output", //
                "Memory Limit Exceeded", //
                "Memory-Limit Exceeded", //
                "No - Memory Limit Exceeded", //
                "No - Memory-Limit Exceeded", //
                "Runtime Error - Correct Output", //
                "Run-time Error - Correct Output", //
                "Run Time Error - Correct Output", //
                "Runtime Error - Wrong Answer", //
                "Run-time Error - Wrong Answer", //
                "Run Time Error - Wrong Answer", //
                "Runtime Error - Presentation Error", //
                "Run-time Error - Presentation Error", //
                "Run Time Error - Presentation Error", //
                "Runtime Error - Excessive Output", //
                "Run-time Error - Excessive Output", //
                "Run Time Error - Excessive Output", //
                "Runtime Error - Incomplete Output", //
                "Run-time Error - Incomplete Output", //
                "Run Time Error - Incomplete Output", //
                "Runtime Error - No Output", //
                "Run-time Error - No Output", //
                "Run Time Error - No Output", //
                "Compile Time Limit Exceeded", //
                "Compile Time-Limit Exceeded", //
                "No - Compile Time Limit Exceeded", //
                "No - Compile Time-Limit Exceeded", //
                "Security Violation", //
                "Illegal Function", //
                "Judging Error", //
                "Submission Error", //
                "Contact Staff", //
                "Other Contact Staff", //
                "Other - Contact Staff", //
        };

        return JudgementMappingList;
    }

    /**
     * A single test where the judgement acronym from JSON does not match run from model judgement acronym.
     *
     * @throws Exception
     */
    public void testForInvalidAcronym() throws Exception {

        SampleContest sampleContest = new SampleContest();

        IInternalContest contest = sampleContest.createStandardContest();

        String runInfoLine = "1,1,A,1,No,No,4"; // 0 (a No before first yes Security Violation)
        SampleContest.addRunFromInfo(contest, runInfoLine, true);

        IInternalController conroller = null;
        JSONTool jsonTool = new JSONTool(contest,conroller);

        Run firstRun = contest.getRuns()[0];
        assertTrue(firstRun.isJudged());
        Judgement judgement = contest.getJudgement(firstRun.getJudgementRecord().getJudgementId());
        String expectedJudgementAcroynym = judgement.getAcronym();

        //        add(new JudgementMapping("Incomplete Output",CLICS_JUDGEMENT_ACRONYM.IO));
        firstRun.getJudgementRecord().setValidatorResultString("Incomplete Output");

        ObjectNode node = jsonTool.convertJudgementToJSON(firstRun);

        String judgementAcronym = node.get("judgement_type_id").toString();

        judgementAcronym = judgementAcronym.replaceAll("\"", "");   // strip off " around judgement acronym, "RTE" to RTE

        assertEquals("Expected run judgement acronym ", expectedJudgementAcroynym, judgementAcronym);

    }

    /**
     * Test very JudgementMapping udgement acronym from JSON does not match run from model judgement acronym.
     *
     * @throws Exception
     */
    public void testForInvalidAcronymFromMappgingList() throws Exception {

        SampleContest sampleContest = new SampleContest();

        IInternalContest contest = sampleContest.createStandardContest();

        String runInfoLine = "1,1,A,1,No,No,4"; // 0 (a No before first yes Security Violation)
        SampleContest.addRunFromInfo(contest, runInfoLine, true);

        IInternalController conroller = null;
        JSONTool jsonTool = new JSONTool(contest,conroller);

        Run firstRun = contest.getRuns()[0];
        assertTrue(firstRun.isJudged());
        Judgement judgement = contest.getJudgement(firstRun.getJudgementRecord().getJudgementId());
        String expectedJudgementAcroynym = judgement.getAcronym();

        String [] overRideJudgements =getJudgementMappingList();
        for (String overrideString : overRideJudgements) {

            firstRun.getJudgementRecord().setValidatorResultString(overrideString);
            ObjectNode node = jsonTool.convertJudgementToJSON(firstRun);

            String judgementAcronym = node.get("judgement_type_id").toString();

            judgementAcronym = judgementAcronym.replaceAll("\"", "");   // strip off " around judgement acronym, "RTE" to RTE

            assertEquals("Expected run judgement acronym ", expectedJudgementAcroynym, judgementAcronym);
        }
    }
}
