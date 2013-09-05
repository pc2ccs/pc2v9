package edu.csus.ecs.pc2.api.implementation;

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
}
