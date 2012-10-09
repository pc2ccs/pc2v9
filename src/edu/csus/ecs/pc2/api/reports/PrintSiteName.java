package edu.csus.ecs.pc2.api.reports;


/**
 * Site Name.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */
public class PrintSiteName extends APIAbstractTest {

    @Override
    public void printTest() {
        println("Site Name = " + getContest().getSiteName());
    }

    @Override
    public String getTitle() {
        return "getSiteName";
    }
}
