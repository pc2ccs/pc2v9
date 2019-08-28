// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api.reports;


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
