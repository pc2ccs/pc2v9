package edu.csus.ecs.pc2.shadow;

import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/***
 * Unit tests
 * 
 * @author Douglas A. Lane <pc2@ecs.csus.edu>
 */
public class ShadowControllerTest extends AbstractTestCase {

    private SampleContest sampleContest;

    /**
     * Test getJudgementsMap - that that if validtor judgement string not in Big 5 then will use existing acronym.
     * 
     * @throws Exception
     */
    public void testgetJudgementsMap() throws Exception {

//        String outdir = getOutputDataDirectory(getName());
//        ensureDirectory(outdir);

        String remoteURL = "empty";
        String remoteCCSLogin = "joe";
        String remoteCCSPassword = "joe";

        /**
         * runsData columns.
         * 
         * 0 - run id, int 1 - team id, int 2 - problem letter, char 3 - elapsed, int 4 - solved, String &quot;Yes&quot; or No 5 - send to teams, Yes or No 6 - No Judgement index 7 - Validator
         * judgement string
         */
        String[] runsData = { //
                "1,1,A,1,No,No,4", //
                "2,1,A,1,No,No,2", //
                "3,1,A,1,No,No,1,Undetermined", //
                "4,1,A,3,No,No,3", //
                "5,1,A,5,No,No,1", //
                "6,1,A,7,No,No,3", //
                "7,1,A,9,No,No,1", //
                "8,1,B,11,No,No,3,teetoutput", //
                "9,2,A,48,No,No,4", //
                "10,2,A,50,No,No,1,No - Wrong Answer", //
                "11,2,C,35,No,No,2", //
                "12,2,D,40,No,No,3", //
        };

        InternalContest contest = createContestWithJudgedRuns(12, runsData, 8);

        String[] judgementsData = { //
                "testoutput,WA", //
                "Undetermined,UDT" };

        addJudgements(contest, judgementsData);

        SampleContest samleContest = new SampleContest();
        IInternalController controller = samleContest.createController(contest, true, false);
        assertNotNull(controller.getLog());
        StaticLog.setLog(controller.getLog());

//        controller.getLog().startConsoleLogger();  // add console logging output

        ShadowController shad = new ShadowController(contest, controller, remoteURL, remoteCCSLogin, remoteCCSPassword);

        Run[] runs = contest.getRuns();
        Map<String, ShadowJudgementInfo> map = shad.getJudgementsMap(runs);

        Set<String> keyset = map.keySet();
        int num = keyset.size();
        assertEquals("Expected mapped judgement infos ", runs.length, num);

//        dumpShadowJudgementInfoMap(map);

        // Compare with expected judgements

        assertSameJudgement(map, "1", "SV");
        assertSameJudgement(map, "2", "CE");
        assertSameJudgement(map, "3", "WA");
        assertSameJudgement(map, "4", "CS");
        assertSameJudgement(map, "5", "WA");
        assertSameJudgement(map, "6", "CS");
        assertSameJudgement(map, "7", "WA");
        assertSameJudgement(map, "8", "CS");
        assertSameJudgement(map, "9", "SV");
        assertSameJudgement(map, "10", "WA");
        assertSameJudgement(map, "11", "CE");
        assertSameJudgement(map, "12", "CS");

    }

    /**
     * Compare judgement in map to expcted judgement.
     * 
     * @param map
     */
    void dumpShadowJudgementInfoMap(Map<String, ShadowJudgementInfo> map) {
        Set<String> keyset = map.keySet();
        for (String key : keyset) {
            ShadowJudgementInfo info = map.get(key);
            ShadowJudgementPair pair = info.getShadowJudgementPair();
            System.out.println("debug key = " + key + " " + info.getJudgerID() + " " + pair.getSubmissionID() + " " + pair.getPc2Judgement());
        }
    }

    private void assertSameJudgement(Map<String, ShadowJudgementInfo> map, String key, String expectedJudgement) {
        ShadowJudgementInfo info = map.get(key);
        assertNotNull("Expecting ShadowJudgementPair for run" + key, info);
        ShadowJudgementPair pair = info.getShadowJudgementPair();

        assertEquals("Expecing judgement acronymn for run " + key, expectedJudgement, pair.getPc2Judgement());

    }

    private void addJudgements(InternalContest contest, String[] judgementsData) {

        for (String data : judgementsData) {
            String[] fields = data.split(",");

            String name = fields[0];
            String acronymn = fields[1];
            Judgement judgement = new Judgement(name, acronymn);
            contest.addJudgement(judgement);

        }
    }

    /**
     * Create and return a new scoreboard client.
     * 
     * @param contest
     * @return a ClientId for newly created scoreboard account.
     */
    private ClientId createBoardAccount(IInternalContest contest) {
        Vector<Account> scoreboards = contest.generateNewAccounts(ClientType.Type.SCOREBOARD.toString(), 1, true);
        return scoreboards.firstElement().getClientId();
    }

    /**
     * Initialize contest with teams, problems, languages, judgements.
     * 
     * @param contest
     * @param numTeams
     * @param numProblems
     */
    private void initData(IInternalContest contest, int numTeams, int numProblems) {

        // Add accounts
        contest.generateNewAccounts(ClientType.Type.TEAM.toString(), numTeams, true);
        contest.generateNewAccounts(ClientType.Type.JUDGE.toString(), 6, true);

        sampleContest = new SampleContest();
        sampleContest.assignSampleGroups(contest, "Group Thing One", "Group Thing Two");

        // Add scoreboard account and set the scoreboard account for this client (in contest)
        contest.setClientId(createBoardAccount(contest));

        // Add Problem
        for (int i = 0; i < numProblems; i++) {
            char letter = 'A';
            letter += i;
            Problem problem = new Problem("Problem " + letter);
            problem.setShortName("short" + letter);
            problem.setLetter("" + letter);
            contest.addProblem(problem);
        }

        // Add Language
        Language language = new Language("Java");
        contest.addLanguage(language);

        String[] judgementNames = { "Yes", "No - incorrect output", "No - compilation error", "Contact staff", "No - Security Violation" };

        String[] acronyms = { "AC", "WA", "CE", "WA", "SV" };

        for (int i = 0; i < judgementNames.length; i++) {
            Judgement judgement = new Judgement(judgementNames[i], acronyms[i]);
            contest.addJudgement(judgement);
        }
    }

    /**
     * Create contest with judged runs.
     * 
     * @param numTeams
     *            number of teams to create
     * @param runsDataList
     *            array of strings, see {@link SampleContest#addRunFromInfo(IInternalContest, String)}}
     * @return
     * @throws Exception
     */
    public InternalContest createContestWithJudgedRuns(int numTeams, String[] runsDataList, int numberProblems) throws Exception {
        return createContestWithJudgedRuns(numTeams, runsDataList, numberProblems, false);
    }

    /**
     * Create contest with judged runs.
     * 
     * @param numTeams
     *            number of teams to create
     * @param runsDataList
     *            array of strings, see {@link SampleContest#addRunFromInfo(IInternalContest, String)}}
     * @param runsDataList
     * @param respectSendTo
     *            add RESPECT_NOTIFY_TEAM_SETTING to each team account
     * @return
     * @throws Exception
     */
    // TODO REFACTOR move to SampleContest
    public InternalContest createContestWithJudgedRuns(int numTeams, String[] runsDataList, int numberProblems, boolean respectSendTo) throws Exception {

        InternalContest contest = new InternalContest();

        initData(contest, numTeams, numberProblems);

        if (respectSendTo) {
            /**
             * Set permission that will respect the {@link JudgementRecord#isSendToTeam()}
             */
            Account account = contest.getAccount(contest.getClientId());
            account.addPermission(edu.csus.ecs.pc2.core.security.Permission.Type.RESPECT_NOTIFY_TEAM_SETTING);
        }

        for (String runInfoLine : runsDataList) {
            SampleContest.addRunFromInfo(contest, runInfoLine);
        }

        return contest;
    }
}
