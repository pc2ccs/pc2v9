package edu.csus.ecs.pc2.core.controller;

import java.util.Arrays;
import java.util.Date;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.Controller;
import edu.csus.ecs.pc2.core.list.RunComparator;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.Contest;
import edu.csus.ecs.pc2.core.model.ContestTime;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.IJudgementListener;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementEvent;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
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

    private static final String [] SERVER_COMMAND_LINE_OPTIONS = {"--server"};

    private IContest modelOne;

    private Controller controllerOne;

    private IContest teamModel;

    private TeamController teamController;

    private IContest judgeModel;

    private JudgeController judgeController;

    protected void setUp() throws Exception {
        super.setUp();

        modelOne = new Contest();
        initializeModel(modelOne);

        Site siteOne = SiteTest.createSite(12, "Site ONE", null, 0);
        modelOne.addSite(siteOne);

        // Start site 1
        controllerOne = new Controller(modelOne);
        controllerOne.setContactingRemoteServer(false);
        controllerOne.setUsingMainUI(false);
        controllerOne.start(SERVER_COMMAND_LINE_OPTIONS);
        controllerOne.login("site1", "site1");
        assertTrue("Site 1 logged in", modelOne.isLoggedIn());

        startContestTime();
    }

    /**
     * 
     * @author pc2@ecs.csus.edu
     */
    public class JudgeController extends Controller {

        private Judgement yesJudgement;

        private IContest theModel;

        JudgeController(IContest contest) {
            super(contest);
            contest.addRunListener(new RunListenerImpl());
            contest.addJudgementListener(new JudgementListenerImpl());
            theModel = contest;
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
                    JudgementRecord judgementRecord = new JudgementRecord(yesJudgement.getElementId(), theModel.getClientId(), true, false);
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
    public class TeamController extends Controller {
        TeamController(IContest contest) {
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

    public void initializeModel(IContest contest) {

        String[] languages = { "Java", "C", "APL" };
        String[] problems = { "Sumit", "Quadrangles", "Routing" };
        String[] judgements = { "No no", "No no no", "No - judges are confused" };

        for (String langName : languages) {
            Language language = new Language(langName);
            modelOne.addLanguage(language);
        }

        for (String probName : problems) {
            Problem problem = new Problem(probName);
            modelOne.addProblem(problem);
        }

        Judgement judgementYes = new Judgement("Yes");
        modelOne.addJudgement(judgementYes);

        for (String judgementName : judgements) {
            modelOne.addJudgement(new Judgement(judgementName));
        }

        contest.generateNewAccounts(ClientType.Type.TEAM.toString(), 10, true);
        contest.generateNewAccounts(ClientType.Type.JUDGE.toString(), 5, true);

        assertTrue("Insure generate of 10 teams", modelOne.getAccounts(Type.TEAM).size() == 10);
        assertTrue("Insure generate of 5 teams", modelOne.getAccounts(Type.JUDGE).size() == 5);
    }

    private void startContestTime() {
        ContestTime contestTime = new ContestTime();
        contestTime.setSiteNumber(modelOne.getSiteNumber());
        contestTime.setElapsedMins(20);
        contestTime.startContestClock();
        controllerOne.setContestTime(contestTime);
    }

    public void testOneRun() {

        Account account = modelOne.getAccounts(Type.TEAM).firstElement();
        ClientId teamId = account.getClientId();

        account = modelOne.getAccounts(Type.JUDGE).firstElement();
        ClientId judgeId = account.getClientId();

        // Login a judge
        judgeModel = new Contest();
        judgeController = new JudgeController(judgeModel);
        judgeController.setUsingMainUI(false);
        judgeController.start(new String[0]);
        judgeController.login(judgeId.getName(), judgeId.getName());
        sleep(12, "judge logging in");

        // Login a team

        teamModel = new Contest();
        teamController = new TeamController(teamModel);
        teamController.setUsingMainUI(false);
        teamController.start(new String[0]);
        teamController.login(teamId.getName(), teamId.getName());
        sleep(12, "team logging in");

        Problem firstProblem = modelOne.getProblems()[0];
        Language firstLanguage = modelOne.getLanguages()[0];

        try {
            teamController.submitRun(firstProblem, firstLanguage, "pc2v9.ini");
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
        
        Run [] runs = teamModel.getRuns();
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
        controllerOne.shutdownTransport();
        sleep(10, "waiting for transport shutdown");
    
    }

    
}
