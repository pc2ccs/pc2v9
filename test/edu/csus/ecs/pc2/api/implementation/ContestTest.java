package edu.csus.ecs.pc2.api.implementation;

import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IClient.ClientType;
import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.IProblem;
import edu.csus.ecs.pc2.api.IProblemDetails;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.IStanding;
import edu.csus.ecs.pc2.api.ITeam;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

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
        }

        int expectedNumberOfRuns = problems.length;

        IRun[] runs = apiContest.getRuns();
        assertEquals("Expecting runs ", expectedNumberOfRuns, runs.length);

        ITeam team = runs[0].getTeam();

        IProblemDetails[] details = apiContest.getProblemDetails();

//        dumpDetails(apiContestInst, details);
//        System.out.println("number of runs  " + runs.length);

        ITeam [] teams = apiContestInst.getTeams();
        assertEquals("Expecting run details  ", problems.length *teams.length, details.length);

        IStanding standing = apiContest.getStanding(team);
        assertNotNull("Expecting standing ", standing);

        IProblemDetails[] teamDet = standing.getProblemDetails();
        assertEquals("Expecting run detail for team " + team,  expectedNumberOfRuns, teamDet.length);
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
        assertEquals("Admin clients ", 0, number);

        number = countClients(allClients, 1, ClientType.TEAM_CLIENT);
        assertEquals("Team clients ", 12, number);

        number = countClients(allClients, 1, ClientType.SCOREBOARD_CLIENT);
        assertEquals("Scoreboard clients ", 2, number);
    }
}
