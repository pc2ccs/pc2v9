package edu.csus.ecs.pc2.core.controller;

import java.util.Arrays;
import java.util.Date;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IJudgementListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementEvent;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunFiles;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.model.SiteTest;
import edu.csus.ecs.pc2.core.model.ClientType.Type;

/**
 * Single run through the system test.
 * 
 * Single site starts, judge logs in, team logs in.
 * Team submits run, judge judges run Yes, returns
 * judgement to team.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
public class RunFlowTest extends TestCase {

    @SuppressWarnings("unused")
    private static final String [] SERVER_COMMAND_LINE_OPTIONS = {"--server", "--port", "42000"};

    private static final String [] CLIENT_COMMAND_LINE_OPTIONS = {"--port", "42000"};

    private IInternalContest contestOne;

    @SuppressWarnings("unused")
    private InternalController controllerOne;

    private IInternalContest teamContest;

    private TeamController teamController;

    private IInternalContest judgeContest;

    private JudgeController judgeController;

    private int siteNumber = 1;

    protected void setUp() throws Exception {
        super.setUp();

        contestOne = new InternalContest();
        Site siteTwelve = SiteTest.createSite(siteNumber, "Site 1", null, 42000);
        contestOne.addSite(siteTwelve);

        // Start site 1
//        controllerOne = new InternalController(contestOne);
//        controllerOne.setContactingRemoteServer(false);
//        controllerOne.setUsingMainUI(false);
//        controllerOne.start(SERVER_COMMAND_LINE_OPTIONS);
//        controllerOne.login("site1", "site1");
        initializeModel(contestOne);
//        assertTrue("Site "+contestOne.getSiteNumber()+" logged in", contestOne.isLoggedIn());

        startContestTime();
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    public class JudgeController extends InternalController {

        private Judgement yesJudgement;

        private IInternalContest theContest;

        JudgeController(IInternalContest contest) {
            super(contest);
            contest.addRunListener(new RunListenerImpl());
            contest.addJudgementListener(new JudgementListenerImpl());
            theContest = contest;
        }

        /**
         * 
         * @author pc2@ecs.csus.edu
         * 
         */
        class JudgementListenerImpl implements IJudgementListener {

            public void judgementAdded(JudgementEvent event) {
                if (yesJudgement == null) {
                    yesJudgement = event.getJudgement();
                }
            }

            public void judgementChanged(JudgementEvent event) {
                // TODO Auto-generated method stub

            }

            public void judgementRemoved(JudgementEvent event) {
                // TODO Auto-generated method stub

            }

        }

        /**
         * 
         * @author pc2@ecs.csus.edu
         */
        class RunListenerImpl implements IRunListener {

            public void runAdded(RunEvent event) {
                System.err.println("Judge - " + event.getRun());
                
                // Request the run
                System.err.println("Judge - checking out run "+event.getRun());
                checkOutRun (event.getRun(), false);
            }

            public void runChanged(RunEvent event) {
                System.err.println("Judge - " + event.getRun());

                if (event.getAction().equals(RunEvent.Action.CHECKEDOUT_RUN)) {
                    // Every run gets a yes!
                    JudgementRecord judgementRecord = new JudgementRecord(yesJudgement.getElementId(), theContest.getClientId(), true, false);
                    judgementRecord.setSendToTeam(true);
                    System.err.println("Judge - sending judgement for run " + event.getRun());
                    submitRunJudgement(event.getRun(), judgementRecord, null);
                }
            }

            public void runRemoved(RunEvent event) {
                // TODO Auto-generated method stub

            }
        }
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    public class TeamController extends InternalController {
        TeamController(IInternalContest contest) {
            super(contest);
            contest.addRunListener(new RunListenerImpl());
        }

        /**
         * 
         * @author pc2@ecs.csus.edu
         */
        class RunListenerImpl implements IRunListener {

            public void runAdded(RunEvent event) {
                System.err.println("Team Run " + event.getAction() + " " + event.getRun());

            }

            public void runChanged(RunEvent event) {
                Run run = event.getRun();
                boolean isSolved = run.isSolved();
                System.err.println("Team Run " + event.getAction() + " " + event.getRun());
                assertTrue("Run was not solved ", isSolved);
            }

            public void runRemoved(RunEvent event) {
                // TODO Auto-generated method stub

            }
        }
    }

    public void initializeModel(IInternalContest contest) {

        String[] languages = { "Java", "C", "APL" };
        String[] problems = { "Sumit", "Quadrangles", "Routing" };
        String[] judgements = { "No no", "No no no", "No - judges are confused" };

        for (String langName : languages) {
            Language language = new Language(langName);
            contestOne.addLanguage(language);
        }

        for (String probName : problems) {
            Problem problem = new Problem(probName);
            contestOne.addProblem(problem);
        }

        Judgement judgementYes = new Judgement("Yes");
        contestOne.addJudgement(judgementYes);

        for (String judgementName : judgements) {
            contestOne.addJudgement(new Judgement(judgementName));
        }

        contest.setSiteNumber(siteNumber);
        contest.generateNewAccounts(ClientType.Type.TEAM.toString(), 10, true);
        contest.generateNewAccounts(ClientType.Type.JUDGE.toString(), 5, true);
        contest.setSiteNumber(1);

        assertTrue("10 teams at site " + siteNumber + " not generated", contestOne.getAccounts(Type.TEAM, siteNumber).size() == 10);
        assertTrue("5 teams at site " + siteNumber + " not generated", contestOne.getAccounts(Type.JUDGE, siteNumber).size() == 5);
    }

    private void startContestTime() {
        ContestTime contestTime = new ContestTime();
        contestTime.setSiteNumber(contestOne.getSiteNumber());
        contestTime.setElapsedMins(20);
        contestTime.startContestClock();
        contestOne.addContestTime(contestTime);
        
//        controllerOne.setContestTime(contestTime);
    }
    
    public void testOneRun() {
        Account account = contestOne.getAccounts(Type.TEAM).firstElement();
        ClientId teamId = account.getClientId();

        account = contestOne.getAccounts(Type.JUDGE).firstElement();
//        ClientId judgeId = account.getClientId();
        
        Run run = new Run(teamId, contestOne.getLanguages()[0],
                contestOne.getProblems()[0]);

        RunFiles runFiles = new RunFiles(run,"pc2v9.ini");
        
        contestOne.acceptRun(run, runFiles);
        
        assertTrue ("Should be "+contestOne.getRuns(), contestOne.getRuns().length == 1);

        
    }

    public void oneRun() {

        Account account = contestOne.getAccounts(Type.TEAM).firstElement();
        ClientId teamId = account.getClientId();

        account = contestOne.getAccounts(Type.JUDGE).firstElement();
        ClientId judgeId = account.getClientId();

        // Login a judge
        judgeContest = new InternalContest();
        judgeController = new JudgeController(judgeContest);
        judgeController.setUsingMainUI(false);
        judgeController.start(CLIENT_COMMAND_LINE_OPTIONS);
        judgeController.login(judgeId.getName(), judgeId.getName());
        sleep(12, "judge logging in");

        // Login a team

        teamContest = new InternalContest();
        teamController = new TeamController(teamContest);
        teamController.setUsingMainUI(false);
        teamController.start(CLIENT_COMMAND_LINE_OPTIONS);
        teamController.login(teamId.getName(), teamId.getName());
        sleep(12, "team logging in");

        Problem firstProblem = contestOne.getProblems()[0];
        Language firstLanguage = contestOne.getLanguages()[0];

        try {
            teamController.submitRun(firstProblem, firstLanguage, "pc2v9.ini", null);
            System.err.println(new Date() + " submitted run to server ");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        /**
         * This pause is done while:
         * - team send run to server
         * - server sends run (available) to judge
         * - judge receives new run and requests it
         * - server gives requested run to judge
         * - judge judges it yes
         * - team receives judgement.
         */
        sleep(8, "pausing while run goes through system");
        
        Run [] runs = teamContest.getRuns();
        Arrays.sort(runs,new RunComparator());
        System.err.println("There are "+runs.length+" runs submitted");
        
        for (Run run : runs){
            System.err.println(run);
            if (run.isJudged()) {
                System.err.println("   judgement is "+run.getJudgementRecord());
            }
            
            // All runs should be Yes and judged.
            assertTrue("Run is not Yes ", run.isSolved());
            assertTrue ("Run is not judged "+run, run.isJudged());
        }
        
        // Fails if no runs found.
        
        assertTrue ("No runs found, should be at least one run ", runs.length != 0);
        
        System.err.println(new Date()+" tests done.");
    }

    /**
     * Sleep for secs seconds, print comment before and after sleep.
     * 
     * @param secs
     * @param comment
     */
    private void sleep(int secs, String comment) {

        System.err.flush();
        System.out.println(new Date() + " Pausing " + secs + " seconds for " + comment);
        System.out.flush();
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(new Date() + " Paused  " + secs + " seconds (end) for " + comment);

    }
    
    public static void main(String[] args) {
        
        try {
            RunFlowTest runFlowTest = new RunFlowTest();
            runFlowTest.setUp();
            runFlowTest.testOneRun();
            
        } catch (Exception e) {
            // TODO: log handle exception
            StaticLog.log("Exception logged ", e);
        }
        System.out.println(new Date() + " main done.");
        System.exit(0);
        
    }

    @Override
    protected void tearDown() throws Exception {
        
        super.tearDown();
//        controllerOne.shutdownTransport();
//        sleep(10, "waiting for transport shutdown");
    
    }

    
}
