package edu.csus.ecs.pc2.api.apireports;

import edu.csus.ecs.pc2.api.APIAbstractTest;

/**
 * Print Local Host name and port.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintLocalHostName extends APIAbstractTest {

    @Override
    public void printTest() {
        println("Contacted: host=" + getContest().getServerHostName() + " port=" + getContest().getServerPort());
    }

    @Override
    public String getTitle() {
        return "getLocalContactedHostName";
    }
}
