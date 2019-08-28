// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api.reports;


/**
 * Print Local Host name and port.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintLocalPortNumber extends APIAbstractTest {

    @Override
    public void printTest() {
        println("Contacted: host=" + getContest().getServerHostName() + " port=" + getContest().getServerPort());
    }

    @Override
    public String getTitle() {
        return "getLocalContactedPortNumber";
    }
}
