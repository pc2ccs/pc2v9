package edu.csus.ecs.pc2.api.reports;


/**
 * Contest Running.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintContestRunning extends APIAbstractTest {

    @Override
    public void printTest() {
        println("Contest running ? " + getContest().isContestClockRunning());
    }

    @Override
    public String getTitle() {
        return "isContestClockRunning";
    }
}
