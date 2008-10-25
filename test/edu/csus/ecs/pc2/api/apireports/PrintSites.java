package edu.csus.ecs.pc2.api.apireports;

import edu.csus.ecs.pc2.api.APIAbstractTest;
import edu.csus.ecs.pc2.api.ISite;

/**
 * Sites.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintSites extends APIAbstractTest {

    @Override
    public void printTest() {
        ISite[] sites = getContest().getSites();
        println("There are " + sites.length + " sites.");
        for (ISite site : sites) {
            println(" Site " + site.getNumber() + " name=" + site.getName());
        }
    }

    @Override
    public String getTitle() {
        return "getSites";
    }
}
