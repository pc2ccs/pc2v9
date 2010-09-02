package edu.csus.ecs.pc2.api.implementation;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.api.IProblemDetails;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.IStanding;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithmTest;

/**
 * Test class for StandingsImplementation (IStanding).
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class StandingImplementationTest extends TestCase {
    
    private final boolean debugMode = false;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOneDetails() {
        Log log = new Log("test.GSD-2.log");

        // RunID TeamID Prob Time Result

        String[] runsData = { // 
        "2,8,C,1,No", //
                "15,8,D,1,Yes", //
                "23,8,D,1,No", //
                "29,8,D,1,No", //
                "43,8,C,1,No", //
                "44,6,A,21,No", //
                "45,6,A,31,Yes", //
                "52,8,C,1,Yes", //
                "65,4,B,2,Yes", //
        };

        SampleContest sample = new SampleContest();
        IInternalContest contest = sample.createContest(1, 5, 40, 12, true);

        DefaultScoringAlgorithmTest dsaTest = new DefaultScoringAlgorithmTest();

        for (String runLine : runsData) {
            dsaTest.addTheRun(contest, runLine);
        }

        Contest apiContest = new Contest(contest, sample.createController(contest, true, true), log);

        IStanding[] standings = apiContest.getStandings();
        
        if (debugMode){
            System.out.println("Standings count = " + standings.length);
        }

        for (IStanding standing : standings) {
            IProblemDetails[] detailList = standing.getProblemDetails();
            if (debugMode && standing.getNumProblemsSolved() > 0) {
                System.out.println("  Standings, rank =" + standing.getRank() + " " + standing.getNumProblemsSolved() + " " + " s" + standing.getClient().getSiteNumber() + " "
                        + standing.getClient().getLoginName() + " " + detailList.length + " detail rows");
            }
            for (IProblemDetails detail : detailList) {
                
                String loginName = detail.getClient().getLoginName() ;
                int problemNumber = detail.getProblemId();
                
                if (debugMode && (detail.getAttempts() > 0 || standing.getNumProblemsSolved() > 0)) {
                    System.out.println(detail.getClient().getLoginName() + ", prob=" + detail.getProblemId() + ", att=" + //
                            detail.getAttempts() + ", time=" + detail.getSolutionTime() + ", pts=" + detail.getPenaltyPoints());
                }
                
                if (loginName.equals("team8") && problemNumber == 3) {
                    // team8, prob=4, att=1, time=1, pts=1

                    String detailInfo = "For " + loginName + " problem " + problemNumber;
                    assertTrue(detailInfo + " Points should be 41 found " + detail.getPenaltyPoints(), detail.getPenaltyPoints() == 41);
                    assertTrue(detailInfo + " time should be 1 found " + detail.getSolutionTime(), detail.getSolutionTime() == 1);
                }

                if (loginName.equals("team8") && problemNumber == 4) {
                    // team8, prob=4, att=1, time=1, pts=1

                    String detailInfo = "For " + loginName + " problem " + problemNumber;
                    assertTrue(detailInfo + " Points should be 1 found " + detail.getPenaltyPoints(), detail.getPenaltyPoints() == 1);
                    assertTrue(detailInfo + " time should be 1 found " + detail.getSolutionTime(), detail.getSolutionTime() == 1);
                }
                
                if (loginName.equals("team6") && problemNumber == 1) {
                    // team8, prob=4, att=1, time=1, pts=1

                    String detailInfo = "For " + loginName + " problem " + problemNumber;
                    assertTrue(detailInfo + " Points should be 51 found " + detail.getPenaltyPoints(), detail.getPenaltyPoints() == 51);
                    assertTrue(detailInfo + " time should be 31 found " + detail.getSolutionTime(), detail.getSolutionTime() == 31);
                }
                
            }
        }
        
        if (debugMode){
            System.out.println();

            for (IRun run : apiContest.getRuns()) {
                System.out.println("Run " + run.getNumber() + " " + run.getTeam().getLoginName() + " " + run.getJudgementName());
            }
            
        }
    }

}
