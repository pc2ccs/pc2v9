package edu.csus.ecs.pc2.api.apireports;

import edu.csus.ecs.pc2.api.APIAbstractTest;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.IRunJudgement;

/**
 * Print a single IRun.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class PrintRun extends APIAbstractTest {

    @Override
    public void printTest() {

        int runNumber = getNumber();
        if (runNumber < 1) {
            println("getRun() Select a run number");
        } else {
            int siteNum = getSiteNumber();
            if (siteNum == 0) {
                siteNum = getContest().getMyClient().getSiteNumber();
            }
            boolean foundRun = false;

            for (IRun run : getContest().getRuns()) {
                if (run.getNumber() == runNumber && run.getSiteNumber() == siteNum) {
                    foundRun = true;
                    print("   Site " + run.getSiteNumber());
                    print(" Run " + run.getNumber());
                    print(", " + run.getProblem().getName());
                    print(", " + run.getLanguage().getName());
                    print(", del=" + run.isDeleted());
                    print(", finalJudged=" + run.isFinalJudged());
                    print(", preliminaryJudged=" + run.isPreliminaryJudged());
                    print(", solved=" + run.isSolved());
                    println();
                    if (run.isFinalJudged() || run.isPreliminaryJudged()) {
                        for (IRunJudgement runJudgement : run.getRunJudgements()) {
                            println("     " + run.getJudgementName());
                            print("     ");
                            if (runJudgement.isActive()) {
                                print("active");
                            } else {
                                print("      ");
                            }
                            print(" " + runJudgement.getJudgement().getName());
                            print(", solved=" + runJudgement.isSolved());
                            print(", sendToTeam=" + runJudgement.isSendToTeam());
                            print(", computerJudged=" + runJudgement.isComputerJudgement());
                            print(", preliminary=" + runJudgement.isPreliminaryJudgement());
                            println();
                        }

                    } else {
                        print("     ");
                        println("Run not judged.");
                    }
                    break;
                }
            } // for IRun

            if (!foundRun) {
                println("No such run " + runNumber + " eists at site "+siteNum);
            }
        }
    }

    @Override
    public String getTitle() {
        return "getRun";
    }
}
