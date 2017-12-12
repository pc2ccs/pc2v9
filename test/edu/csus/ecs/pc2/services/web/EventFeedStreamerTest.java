package edu.csus.ecs.pc2.services.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.list.AccountComparator;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.services.core.EventFeedJSON;

/**
 * Unit Test
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class EventFeedStreamerTest extends AbstractTestCase {
    
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
    

    private static String pickRandom(String[] stringArray) {

        // return stringArray[0]; // return first name

        Random random = new Random();
        int nameIndex = random.nextInt(stringArray.length);
        return stringArray[nameIndex];
    }
    
    public void testCompleteStream() throws Exception {
        
        String outputDir = getOutputDataDirectory(this.getName());
        ensureDirectory(outputDir);
        
        IInternalContest contest = new UnitTestData().getContest();
        IInternalController controller = new SampleContest().createController(contest, outputDir, true, false);
 
        String json = EventFeedStreamer.createEventFeedJSON(contest, controller, null, null);
        
        assertNotNull(json);
        
        assertTrue("Expected long json ",  json.length() > 8000);
        
        assertCountEvent(100, EventFeedJSON.CLARIFICATIONS_KEY, json); 
        
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

}
