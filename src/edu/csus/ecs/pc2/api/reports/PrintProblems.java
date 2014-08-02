package edu.csus.ecs.pc2.api.reports;

import edu.csus.ecs.pc2.api.IProblem;

/**
 * Problems.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintProblems extends APIAbstractTest {

    @Override
    public void printTest() {
        println("There are " + getContest().getProblems().length + " team ");
        for (IProblem problem : getContest().getProblems()) {
            print("Problem name = " + problem.getName());

            print(" short name = " + problem.getShortName());

            print(" data file = ");
            if (problem.hasDataFile()) {
                print(problem.getJudgesDataFileName());
            } else {
                print("<none>");
            }

            print(" answer file = ");
            if (problem.hasAnswerFile()) {
                print(problem.getJudgesAnswerFileName());
            } else {
                print("<none>");
            }

            print(" validator = ");
            if (problem.hasExternalValidator()) {
                print(problem.getValidatorFileName());
            } else {
                print("<none>");
            }

            if (problem.readsInputFromFile()) {
                print(" reads from FILE");
            }
            if (problem.readsInputFromStdIn()) {
                print(" reads from stdin");
            }
            println();
        }
        println();
    }

    @Override
    public String getTitle() {
        return "getProblems";
    }
}
