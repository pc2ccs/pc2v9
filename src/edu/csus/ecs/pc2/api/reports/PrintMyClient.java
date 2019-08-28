// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api.reports;

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
