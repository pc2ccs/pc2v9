package edu.csus.ecs.pc2.api.reports;

import edu.csus.ecs.pc2.api.IClient;
import edu.csus.ecs.pc2.api.exceptions.NotLoggedInException;

/**
 * My Client - ServerConnection
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintMyClientSC extends APIAbstractTest {
    @Override
    public void printTest() {

        IClient client;
        try {
            client = getServerConnection().getMyClient();
            APIPrintReports.printClient(this, "This client", client);
        } catch (NotLoggedInException e) {
            println("Exception during report " + e.getLocalizedMessage() + " " + e.getStackTrace()[0].getClassName());
            e.printStackTrace();
        }
        println();
    }

    @Override
    public String getTitle() {
        return "getMyClient (ServerConnection)";
    }
}
