// Copyright (C) 1989-2019 PC2 Development Team: John Clevenger, Douglas Lane, Samir Ashoo, and Troy Boudreau.
package edu.csus.ecs.pc2.api.reports;

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
