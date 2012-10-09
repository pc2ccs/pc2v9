package edu.csus.ecs.pc2.api.reports;

import edu.csus.ecs.pc2.api.IContest;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;

/**
 * getContest.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintGetContestSC extends APIAbstractTest {

    @Override
    public void printTest() {
        try {
            IContest serverConnContest = getServerConnection().getContest();
            if (serverConnContest != null) {
                println("getContest from ServerConnection, getContest() title=" + serverConnContest.getContestTitle());
            } else {
                println("getContest from ServerConnection returns null");
            }
        } catch (NotLoggedInException e) {
            println("Exception during report " + e.getLocalizedMessage() + " " + e.getStackTrace()[0].getClassName());
            e.printStackTrace();
        }
        println();
    }

    @Override
    public String getTitle() {
        return "getContest (ServerConnection)";
    }

    public static String trueFalseString(boolean value, String trueString, String falseString) {
        if (value) {
            return trueString;
        } else {
            return falseString;
        }
    }

}
