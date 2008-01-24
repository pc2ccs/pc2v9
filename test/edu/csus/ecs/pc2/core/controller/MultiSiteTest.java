package edu.csus.ecs.pc2.core.controller;

import java.util.Properties;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Site;

/**
 * Test multi site, runs through system.
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class MultiSiteTest extends TestCase {

    // Models for site 1, 2, 3
    private IInternalContest modelOne;


    // Controllers for site 1,2,3
//    private InternalController controllerOne;

    /**
     * Create a new Site class instance.
     * 
     * @param siteNumber
     *            site number
     * @param siteName
     *            title for site
     * @param hostName
     *            if null, assigned to localhost
     * @param portNumber
     *            if 0 assigned 50002 + (siteNumber-1)* 1000
     * @return
     */
    private Site createSite(int siteNumber, String siteName, String hostName, int portNumber) {
        Site site = new Site(siteName, siteNumber);

        Properties props = new Properties();
        if (hostName == null) {
            props.put(Site.IP_KEY, "localhost");
        }

        if (portNumber == 0) {
            portNumber = 50002 + (siteNumber - 1) * 1000;
        }
        props.put(Site.PORT_KEY, "" + portNumber);

        site.setConnectionInfo(props);
        site.setPassword("site" + siteNumber);

        return site;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp(); 
        
        // Create Site 1 with

        String[] languages = { "Java", "C", "APL" };
        String[] problems = { "Sumit", "Quadrangles", "Routing" };

        modelOne = new InternalContest();
        Site siteOne = createSite(1, "Site ONE", null, 0);
        modelOne.addSite(siteOne);

        for (String langName : languages) {
            Language language = new Language(langName);
            modelOne.addLanguage(language);
        }

        for (String probName : problems) {
            Problem problem = new Problem(probName);
            modelOne.addProblem(problem);
        }

        // Start site 1
//        controllerOne = new InternalController(modelOne);
//        controllerOne.setContactingRemoteServer(false);
//        controllerOne.setUsingMainUI(false);
//        String [] argsSiteOne = {"--server"};
//        controllerOne.start(argsSiteOne);

    }

    public void testMultiSite() {

        Site[] sites = modelOne.getSites();
        assertTrue("Add site one ", sites.length == 1);

    }

}
