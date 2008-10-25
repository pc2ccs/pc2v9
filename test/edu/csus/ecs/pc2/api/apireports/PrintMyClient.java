package edu.csus.ecs.pc2.api.apireports;

import edu.csus.ecs.pc2.api.APIAbstractTest;
import edu.csus.ecs.pc2.api.APIPrintReports;
import edu.csus.ecs.pc2.api.IClient;

/**
 * My Client.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintMyClient extends APIAbstractTest {

    @Override
    public void printTest() {

        IClient client = getContest().getMyClient();
        APIPrintReports.printClient(this, "This client", client);
    }

    @Override
    public String getTitle() {
        return "getMyClient";
    }
}
