package edu.csus.ecs.pc2.api.apireports;

import edu.csus.ecs.pc2.api.APIAbstractTest;

/**
 * Contest Title.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintContestTitle extends APIAbstractTest {

    @Override
    public void printTest() {
        println("Contest title: '" + getContest().getContestTitle() + "'");
    }

    @Override
    public String getTitle() {
        return "getContestTitle";
    }
}
