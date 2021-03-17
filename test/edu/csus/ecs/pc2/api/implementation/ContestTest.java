// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api.implementation;

import java.io.File;
import java.util.Vector;

import edu.csus.ecs.pc2.api.IClarification;
import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IClient.ClientType;
import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.ILanguage;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.IProblemDetails;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.IStanding;
import edu.csus.ecs.pc2.api.ITeam;
import edu.csus.ecs.pc2.api.RunStates;
import edu.csus.ecs.pc2.api.ServerConnection;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.Clarification;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType.Type;
import edu.csus.ecs.pc2.core.model.Group;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.LanguageAutoFill;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;
import edu.csus.ecs.pc2.imports.ccs.IContestLoader;

/**
 * API Unit test.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestTest extends AbstractTestCase {

    private SampleContest sampleContest = new SampleContest();

    protected IContest createInstance(String logPrefix) {
        if (logPrefix == null) {
            throw new IllegalArgumentException("log prefix must not be null");
        }
        IInternalContest contest = sampleContest.createContest(1, 3, 12, 12, true);

        ensureOutputDirectory();
        String storageDirectory = getOutputDataDirectory();

        IInternalController controller = sampleContest.createController(contest, storageDirectory, true, false);
        Log log = createLog(logPrefix + getName());

        Contest apiContestInst = new Contest(contest, controller, log);
        return apiContestInst;
    }

    public void testProblems() throws Exception {
        IInternalContest contest = sampleContest.createContest(1, 3, 12, 12, true);

        ensureOutputDirectory();
        String storageDirectory = getOutputDataDirectory();

        IInternalController controller = sampleContest.createController(contest, storageDirectory, true, false);
        Log log = createLog(getName());

        Contest apiContestInst = new Contest(contest, controller, log);
        IContest apiContest = apiContestInst;
        
        Problem[] problems = contest.getProblems();
        assertNotNull("Expecting problems ", problems);
        assertEquals("expected problems count", 6, problems.length);
        problems[0].setShortName("short_name");
        IProblem[] iproblems = apiContest.getProblems();
        for ( IProblem iProblem : iproblems) {
            if (iProblem.getName().equalsIgnoreCase("Sumit")) {
                assertEquals("name vs shortname", "short_name", iProblem.getShortName());
            } else {
                String expectedName = iProblem.getName().toLowerCase();
                int space = expectedName.indexOf(" ");
                if (space > 0) {
                    expectedName = expectedName.substring(0, space);
                }
                assertEquals("name vs shortname", expectedName, iProblem.getShortName());
            }
        }
    }
    
    public void testProblemDetails() throws Exception {

        IInternalContest contest = sampleContest.createContest(1, 3, 12, 12, true);

        int problemtoDelete = 3; // added for Bug 993

        ensureOutputDirectory();
        String storageDirectory = getOutputDataDirectory();

        IInternalController controller = sampleContest.createController(contest, storageDirectory, true, false);
        Log log = createLog(getName());

        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {
            Run run = sampleContest.createRandomJudgedRunSolved(contest);
            run.setProblemId(problem.getElementId());
        }

        assertEquals("Expected problem count ", 6, problems.length);
        problems[problemtoDelete].setActive(false);

        Contest apiContestInst = new Contest(contest, controller, log);
        IContest apiContest = apiContestInst;

        int expectedNumberOfRuns = problems.length;

        IRun[] runs = apiContest.getRuns();
        assertEquals("Expecting runs ", expectedNumberOfRuns, runs.length);

        ITeam team = runs[0].getTeam();
        IProblemDetails[] details = apiContest.getProblemDetails();

        // dumpDetails(apiContestInst, details);
        // System.out.println("number of runs  " + runs.length);

        ITeam[] teams = apiContestInst.getTeams();

        int actualDetailCount = problems.length * teams.length;

        actualDetailCount -= teams.length; // subtract # teams because each no longer has problem problemtoDelete

        assertEquals("Expecting run details  ", actualDetailCount, details.length);

        IStanding standing = apiContest.getStanding(team);
        assertNotNull("Expecting standing not null", standing);

        IProblemDetails[] teamDet = standing.getProblemDetails();
        expectedNumberOfRuns--; // subtract one because team has one fewer run because problem problemtoDelete not counted
        assertEquals("Expecting run detail for team " + team, expectedNumberOfRuns, teamDet.length);

        IProblem[] problemsApi = apiContest.getProblems();
        IProblem[] problemsApiAll = apiContest.getAllProblems();

        // Test for Bug 
        assertEquals("Same problems for API ", problems.length - 1, problemsApi.length);
        assertTrue("Expecting problem " + problemtoDelete + " to be deleted ", problemsApiAll[problemtoDelete].isDeleted());

    }

    protected void dumpDetails(IContest contest, IProblemDetails[] details) {

        IProblem[] problems = contest.getProblems();

        int detailCounter = 0;
        for (IProblemDetails det : details) {
            detailCounter++;
            boolean solved = det.getSolutionTime() != 0;
            println("  " + detailCounter + " " + problems[det.getProblemId() - 1].getName() + //
                    " solved=" + solved + " solutionTime=" + det.getSolutionTime() + //
                    " points=" + det.getPenaltyPoints() + " attempts=" + det.getAttempts()+" "+det.getClient().getLoginName());
            
        }
    }
    
    /**
     * Tests for Bug 766 - Add support for general problem/problem categories.
     * 
     * @throws Exception
     */
    public void testProblemClars() throws Exception {

        IContest apiContest = createInstance("tpc");
        
        IProblem[] cats = apiContest.getClarificationCategories();
        
        assertEquals("Expecting one category ", 1, cats.length);

        Problem prob = sampleContest.getGeneralProblem();
        assertEquals("Expecting general  ", prob.getDisplayName(), cats[0].getName());
        
    }

    private void println(String string) {
        System.out.println(string);
        
    }
    
    private int countClients(IClient [] list, int siteNumber, IClient.ClientType type){
        
        int count = 0;
        
        for (IClient iClient : list) {
            if (iClient.getSiteNumber() == siteNumber){
                if (iClient.getType().equals(type)){
                    count++;
                }
            }
        }
        
        return count;
    }
    
    public void testGetClients() throws Exception {
        
        IContest contest = createInstance("testGetClients");
        
        ITeam[] teams = contest.getTeams();
        assertEquals("Expecting teams ", 12, teams.length);

        IClient[] siteList = contest.getClients();

        assertEquals("Expecting this sites clients ", 26, siteList.length);

        IClient[] allClients = contest.getClientsAllSites();

        assertEquals("Expecting teams ", 26, allClients.length);

        int number;

        number = countClients(allClients, 1, ClientType.JUDGE_CLIENT);
        assertEquals("Judge clients ", 12, number);

        number = countClients(allClients, 1, ClientType.ADMIN_CLIENT);
        assertEquals("Admin clients ", 1, number);

        number = countClients(allClients, 1, ClientType.TEAM_CLIENT);
        assertEquals("Team clients ", 12, number);

        number = countClients(allClients, 1, ClientType.SCOREBOARD_CLIENT);
        assertEquals("Scoreboard clients ", 1, number);
    }
    
    public void testRunStatus() throws Exception {
        
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

                "14,2,A,30, ", // doesn't count, no after yes
                "15,2,A,25, ", // doesn't count, no after yes

                "16,2,A,330, ",  // doesn't count, yes after yes
        };
        
        IInternalContest contest = sampleContest.createContest(1, 3, 12, 12, true);

        ensureOutputDirectory();
        String storageDirectory = getOutputDataDirectory();

        IInternalController controller = sampleContest.createController(contest, storageDirectory, true, false);
        Log log = createLog("testRunStatus" + getName());

        Contest apiContestInst = new Contest(contest, controller, log);
        
        for (String runInfoLine : runsData) {
            sampleContest.addARun(contest, runInfoLine);      
        }
        
        IRun[] runs = apiContestInst.getRuns();
        
        assertEquals("Number of runs", 16, runs.length);
        
        assertEquals("Number of NEW runs", 3, countRunStatus(apiContestInst, runs, RunStates.NEW));
        assertEquals("Number of JUDGED runs", 13, countRunStatus(apiContestInst, runs, RunStates.JUDGED));
        
//        for (IRun iRun : runs) {
//            println("debug "+apiContestInst.getRunState(iRun));
//        }
    }

    private int countRunStatus(Contest contest, IRun[] runs, RunStates runStates) {
        int count = 0;
        for (IRun iRun : runs) {
            if (runStates.equals(contest.getRunState(iRun))){
                count ++;
            }
        }
        return count;
    }

    
    public void testgetVersionParts() throws Exception {

        IContest apiContest = createInstance("tgparts");
        
        String[] data = {
                // input,expected
                "9.3Beta,9+3+Beta", //
                "2.2,2+2+", //
                "2.,2.++", //
                
        };

        for (String line : data) {
            String[] fields = line.trim().split(",");
            String input = fields[0];
            String expected = fields[1];
            
            String [] results = ((Contest)apiContest).getVersionParts(input);
            String actual = join("+", results);
            
//            println("\""+input+","+actual+"\", //");
            
            assertEquals("Expected matching strings", expected, actual);
        }
        
//        String s = apiContest.getBuildNumber();
//        println("Build number: "+s);
//        s = apiContest.getMajorVersion();
//        println("Major : "+s);
//        s = apiContest.getMinorVersion();
//        println("Minor: "+s);
        
        
        
    }
    
    public void testGetRun() throws Exception {
        
        IInternalContest contest = sampleContest.createContest(1, 3, 12, 12, true);

        assertEquals("Site id", 1, contest.getSiteNumber());

        ensureOutputDirectory();
        String storageDirectory = getOutputDataDirectory();

        IInternalController controller = sampleContest.createController(contest, storageDirectory, true, false);
        Log log = createLog(getName());

        Contest apiContestInst = new Contest(contest, controller, log);
        IContest apiContest = apiContestInst;

        Problem[] problems = contest.getProblems();
        for (Problem problem : problems) {
            Run run = sampleContest.createRandomJudgedRunSolved(contest);
            run.setProblemId(problem.getElementId());
            assertEquals("Site id", contest.getSiteNumber(), run.getSiteNumber());

        }

        int expectedNumberOfRuns = problems.length;

        IRun[] runs = apiContest.getRuns();
        assertEquals("Expecting runs ", expectedNumberOfRuns, runs.length);

        // Edge Tests
        
        IRun runOne = apiContest.getRun(1);
        assertNotNull("Expecting run id 1 ", runOne);
        assertEquals("Run id", 1, runOne.getNumber());
        
        int lastRunNumber = runs.length;
        IRun lastRun = apiContest.getRun(lastRunNumber);
        assertNotNull("Expecting run id "+lastRun, lastRun);
        assertEquals("Run id", lastRunNumber, lastRun.getNumber());
        
        // out of upper range test
        
        IRun lastRunPlusOne = apiContest.getRun(lastRunNumber+1);
        assertNull("Expecting null for run id "+(lastRunNumber+1), lastRunPlusOne);
        
        // negative test, ahem
        
        int runId = -3;
        IRun run = apiContest.getRun(-3);
        assertNull("Expecting null for run id "+runId, run);
        
        // mid point test
        
        runId = runs.length / 2;
        run = apiContest.getRun(runId);
        assertNotNull("Expecting to find run id "+runId, run);
        assertEquals("Run id", runId, run.getNumber());
        
    }
    
    public void testLanguagesImpl() throws Exception {
        
        Language language = LanguageAutoFill.createAutoFilledLanguage(LanguageAutoFill.JAVATITLE);
        
        IInternalContest internal = sampleContest.createContest(1, 3, 12, 12, true);

        ensureOutputDirectory();
        String storageDirectory = getOutputDataDirectory();

        IInternalController controller = sampleContest.createController(internal, storageDirectory, true, false);
        Log log = createLog("loggy" + getName());
        
        internal.addLanguage(language);
        
        Contest contest = new Contest(internal, controller, log);
        
        int lastIndex = contest.getLanguages().length - 1;
        ILanguage lang = contest.getLanguages()[lastIndex];

        
        assertEquals("getName ", language.getDisplayName(), lang.getName());
        assertEquals("getTitle ", language.getDisplayName(), lang.getTitle());
        
        assertEquals("getCompileCommandLine ", language.getCompileCommandLine(), lang.getCompilerCommandLine());
        assertEquals("isInterpreted ", language.isInterpreted(), lang.isInterpreted());
        assertEquals("getExecutableMask ", language.getExecutableIdentifierMask(), lang.getExecutableMask());
        assertEquals("getExecutionCommandLine ", language.getProgramExecuteCommandLine(), lang.getExecutionCommandLine());
        
        assertFalse("isInterpreted ", lang.isInterpreted());

        
        language = LanguageAutoFill.createAutoFilledLanguage(LanguageAutoFill.PHPTITLE);
        
        internal.addLanguage(language);
        
        lastIndex = contest.getLanguages().length - 1;
        lang = contest.getLanguages()[lastIndex];
        
        assertEquals("getName ", language.getDisplayName(), lang.getName());
        assertEquals("getTitle ", language.getDisplayName(), lang.getTitle());
        
        assertEquals("getCompileCommandLine ", language.getCompileCommandLine(), lang.getCompilerCommandLine());
        assertEquals("isInterpreted ", language.isInterpreted(), lang.isInterpreted());
        
        assertEquals("getExecutableMask ", language.getExecutableIdentifierMask(), lang.getExecutableMask());
        assertEquals("getExecutionCommandLine ", language.getProgramExecuteCommandLine(), lang.getExecutionCommandLine());
        
        assertTrue("isInterpreted ", lang.isInterpreted());
        
    }
    
    // TODO REFACTOR move to AbstractTestcase
    private IInternalContest loadSampleContest(IInternalContest contest, String sampleName) throws Exception {

        IContestLoader loader = new ContestSnakeYAMLLoader();
        if (contest == null) {
            contest = new InternalContest();
        }

        try {
            String cdpDir = getTestSampleContestDirectory(sampleName);
            loader.initializeContest(contest, new File( cdpDir));
            return contest;

        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw e;
        }
    }

    public void testAPIBroadcastClars() throws Exception {

        IInternalContest internal = loadSampleContest(null, "valtest");
        assertNotNull(internal);

        ensureOutputDirectory();
        String storageDirectory = getOutputDataDirectory();

        IInternalController controller = sampleContest.createController(internal, storageDirectory, true, false);
        Log log = createLog("logfile." + getName());

        Account team151 = internal.getAccount(new ClientId(1, Type.TEAM, 151));
        assertNotNull(team151);
        // 151 309101 312543 D2 Eagle White D2 Eagle White (EWU) EWU USA

        assertEquals("Team 151 display name", "D2 Eagle White (EWU)", team151.getDisplayName());

        Account team = getTeamAccounts(internal)[0];

        assertNotNull("Team " + team.getClientId() + " not assigned a group", team.getGroupId());

        int addedClarDcount = addClarification(internal, team, internal.getProblems(), "Which team is? ");
        assertEquals("added clar count", 6, addedClarDcount);

        internal.setClientId(team.getClientId());

        Contest contest = new Contest(internal, controller, log);

        Vector<Account> teams = internal.getAccounts(Type.TEAM);
        assertEquals("Expecting team count ", 151, teams.size());

        Group[] groups = internal.getGroups();
        assertEquals("Expecting group count ", 12, groups.length);
        
        // Create Answered clar from Division 1
        
        Account div1team =  internal.getAccount(new ClientId(1, Type.TEAM, 301));
        assertNotNull(div1team);
        // 301  308430  12546   Lute Octothorpe Lute Octothorpe (PLU)   PLU USA
        
        assertEquals("Team 301 display name", "Lute Octothorpe (PLU)", div1team.getDisplayName());

        Account judgeAccount = internal.getAccounts(Type.JUDGE).firstElement();
        
        Problem problem = internal.getProblems()[0];
        
        Clarification answerClar = createBroadcastClar(internal, div1team, problem, "Broadcast clar", "Answer is here!", judgeAccount.getClientId());
      
        IClarification[] clars = contest.getClarifications();
        
        assertEquals("Expected clars from API", 7, clars.length);

        // Failes on getClarification if bug not fixed
        IClarification[] allClars = contest.getClarifications();
        assertNotNull(allClars);
    }
    
    /**
     * Created send to all clar.
     * 
     * @param internal
     * @param team
     * @param problem
     * @param quetion
     * @param answer
     * @param whoAnsweredIt
     * @return answered clarification
     */
    private Clarification createBroadcastClar(IInternalContest internal, Account team, Problem problem, String quetion, String answer, ClientId whoAnsweredIt) {
        Clarification clarification = new Clarification(team.getClientId(), problem, quetion + " from " + team.getClientId() + " group " + team.getGroupId());
        Clarification newClar = internal.acceptClarification(clarification);
        internal.answerClarification(newClar, answer, whoAnsweredIt, true);
        return internal.getClarification(newClar.getElementId());
    }

    public static void dumpTeamAccounts(String message, IInternalContest internal, int max) {
        
        Account[] teams = getTeamAccounts(internal);

        System.out.println("dumpTeamAccounts " + message);
        
        int cnt = 0;
        
        for (Account account : teams) {
            System.out.println(account.getClientId().getName()+" name="+ account.getDisplayName() + " " +//
                    account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD) + " " + //
                    account.getGroupId() + " " + //
                    account.getInstitutionCode() + " " + //
                    account.getExternalId() + " " + //
                    ""
                    );
            if ( cnt > max ) {
                break;
            }
            cnt ++;
            
        }
        System.out.println("dumpTeamAccounts " + message+ " end team count="+teams.length);

    }
    
   public static void dumpTeamAccounts(String message, Account[] teams , int max) {
        
        System.out.println("dumpTeamAccounts " + message);
        
        int cnt = 0;
        
        for (Account account : teams) {
            System.out.println(account.getClientId().getName()+" name="+ account.getDisplayName() + " " +//
                    account.isAllowed(Permission.Type.DISPLAY_ON_SCOREBOARD) + " " + //
                    account.getGroupId() + " " + //
                    account.getInstitutionCode() + " " + //
                    account.getExternalId() + " " + //
                    ""
                    );
            if ( cnt > max ) {
                break;
            }
            cnt ++;
            
        }
        System.out.println("dumpTeamAccounts " + message+ " end team count="+teams.length);

    }

    private int addClarification(IInternalContest internal, Account team, Problem[] problems, String content) {

        for (Problem problem : problems) {
            Clarification clarification = new Clarification(team.getClientId(), problem, content + " from " + team.getClientId() + " group " + team.getGroupId());
            internal.acceptClarification(clarification);
        }

        return problems.length;

    }
    
    /**
     * 
     * Unit test for: Issue Broadcast Clars "to all teams" don't show up in WTI
     * https://github.com/pc2ccs/pc2v9/issues/186
     * 
     * @throws Exception
     */
    public void NONtestAPIGEtBroadcastClarLive() throws Exception {

        //Test case Setup:
        //Start server with teams from different divisions, like sample mini contest
        //Submit clars from team 151 (D2)
        //judge answers clars check send to all
        //        
        // 301  308430  12546   Lute Octothorpe Lute Octothorpe (PLU)   PLU USA
        // 151 309101 312543 D2 Eagle White D2 Eagle White (EWU) EWU USA
        
        ServerConnection serverConnection = new ServerConnection();
        String testId = "team301"; // D1 team
//        testId = "team151"; // D2 team
        

        // If server not started will throw  LoginFailureException: Unable to contact server at localhost:50002 (server not started?)
        IContest contest = serverConnection.login(testId, testId);
        assertNotNull("Not logged in " + testId, contest);

        IProblem[] probs = contest.getProblems();
//        for (IProblem iProblem : probs) {
//            System.out.println("Prob " + iProblem.getName());
//        }
        
        assertEquals("number of problems D2",  4, probs.length); // D1
//        assertEquals("number of problems D1",  2, probs.length); // D2

        // Before fix Throws NPE in ProblemImplementation
//        java.lang.NullPointerException
//        at edu.csus.ecs.pc2.api.implementation.ProblemImplementation.<init>(ProblemImplementation.java:52)
//        at edu.csus.ecs.pc2.api.implementation.ProblemImplementation.<init>(ProblemImplementation.java:48)
        
        IClarification[] clars = contest.getClarifications();

        for (IClarification clar : clars) {
            System.out.println("debug   Clar " + clar.getNumber() + " " + //
                    clar.getTeam().getAccountNumber() + "  " + clar.getProblem().getName() + " " + clar.isSendToAll());
        }

    }
    
    /**
     * Test login to server.
     * @throws Exception
     */
    public void NOTtestLogin() throws Exception {
        
        ServerConnection serverConnection = new ServerConnection();
        String testId = "team92";
        testId = "team303";
        IContest contest = serverConnection.login(testId, testId);
        
        assertNotNull(contest);
        
    }
    
    public void NOTtestGetProblemName(){
        
        IInternalContest contest = sampleContest.createStandardContest();
        
        Problem[] problems = contest.getProblems();
        
        for (Problem problem : problems) {
            String stripped = ProblemImplementation. getProblemName(problem.getElementId());
            println("Problem name = " + problem.getDisplayName() + " ele = " + problem.getElementId()+" newstr '"+stripped+"'");
        }
        
    }
        
}
