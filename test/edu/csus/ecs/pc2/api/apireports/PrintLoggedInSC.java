package edu.csus.ecs.pc2.api.apireports;

import edu.csus.ecs.pc2.api.APIAbstractTest;

/**
 * isLoggedIn - ServerConnection
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintLoggedInSC extends APIAbstractTest {
    @Override
    public void printTest() {
        print("This client, logged in = " + getServerConnection().isLoggedIn());
        println();
    }

    @Override
    public String getTitle() {
        return "isLoggedIn (ServerConnection)";
    }
}
