package edu.csus.ecs.pc2.api.apireports;

import java.util.Arrays;

import edu.csus.ecs.pc2.api.APIAbstractTest;
import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.IRun;
import edu.csus.ecs.pc2.api.IRunComparator;

/**
 * Prints Runs (IRun and Judgement)
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintRuns extends APIAbstractTest {

    @Override
    public void printTest() {

        if (getContest().getRuns().length == 0) {
            println("No runs in system");
            return;
        }

        IRun[] runs = getContest().getRuns();
        Arrays.sort(runs, new IRunComparator());
        println("There are " + runs.length + " runs.");

        for (IRun run : runs) {

            print("Run " + run.getNumber() + " Site " + run.getSiteNumber());

            print(" @ " + run.getSubmissionTime() + " by " + run.getTeam().getLoginName());
            print(" problem: " + run.getProblem().getName());
            print(" in " + run.getLanguage().getName());

            if (run.isJudged()) {
                println("  Judgement: " + run.getJudgementName());
            } else {
                println("  Judgement: not judged yet ");
            }

            println();
        }
    }

    @Override
    public String getTitle() {
        return "getRuns, IRun, etc.";
    }

    public void printClient(APIAbstractTest abstractTest, String title, IClient client) {
        abstractTest.print(title + " login=" + client.getLoginName());
        abstractTest.print(", name=" + client.getDisplayName());
        abstractTest.print(", type=" + client.getType());
        abstractTest.print(", account#=" + client.getAccountNumber());
        abstractTest.print(", site=" + client.getSiteNumber());
        abstractTest.println();
    }
}
