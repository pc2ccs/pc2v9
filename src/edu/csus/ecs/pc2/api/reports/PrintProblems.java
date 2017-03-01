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

        println("There are " + getContest().getAllProblems().length + " problems ");
        printProblems(getContest().getAllProblems());
        println();
        println();

        println("There are " + getContest().getProblems().length + " non-hidden problems ");
        printProblems(getContest().getProblems());
        println();
    }

    private void printProblems(IProblem[] problems) {

        for (IProblem problem : problems) {
            print("Problem name = " + problem.getName());

            print(" short name = " + problem.getShortName());
            
            if (problem.isDeleted()){
                print(" [DELETED] ");
            }

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

    }

    @Override
    public String getTitle() {
        return "getProblems";
    }
}
