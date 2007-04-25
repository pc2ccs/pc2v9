package edu.csus.ecs.pc2.core.controller;

import java.util.Properties;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.Controller;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.model.IModel;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Model;
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

    private static final int SECS_TO_PAUSE_ON_LOGIN = 8;
    
    private static final String [] SERVER_COMMAND_LINE_OPTIONS = {"--server"};

    // Models for site 1, 2, 3
    private IModel modelOne;

    private IModel modelTwo;

    private IModel modelThree;

    // Controllers for site 1,2,3
    private Controller controllerOne;

    private Controller controllerTwo;

    private Controller controllerThree;

    private ClientId clientIdOne = new ClientId(1, ClientType.Type.SERVER, 0);

    private ClientId clientIdTwo = new ClientId(2, ClientType.Type.SERVER, 0);

    private ClientId clientIdThree = new ClientId(3, ClientType.Type.SERVER, 0);

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

        modelOne = new Model();
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
        controllerOne = new Controller(modelOne);
        controllerOne.setContactingRemoteServer(false);
        controllerOne.setUsingMainUI(false);
        controllerOne.start(SERVER_COMMAND_LINE_OPTIONS);


    }

    public void testMultiSite() {

        Site[] sites = modelOne.getSites();
        assertTrue("Add site one ", sites.length == 1);

        controllerOne.login("site1", "site1");
        assertTrue("Site 1 logged in", modelOne.isLoggedIn());

        Site siteTwo = createSite(2, "Site TWO", null, 0);
        modelOne.addSite(siteTwo);

        sites = modelOne.getSites();
        assertTrue("Add site two  ", sites.length == 2);

        // Add Site 2
        modelTwo = new Model();
        controllerTwo = new Controller(modelTwo);
        controllerTwo.setContactingRemoteServer(true);
        controllerTwo.setUsingMainUI(false);
        controllerTwo.start(SERVER_COMMAND_LINE_OPTIONS);

        // Site 2 Login
        controllerTwo.login("site2", "site2");
        sleep(SECS_TO_PAUSE_ON_LOGIN, "site 2 login");
        assertTrue("Site 1 logged in", modelTwo.isLoggedIn());

        // Add site 3 to site 2
        Site siteThree = createSite(3, "Site THREE", null, 0);
        controllerTwo.addNewSite(siteThree);

        sleep(SECS_TO_PAUSE_ON_LOGIN, "add site 3 def to site 1");
        // Check site 1 for site 3
        sites = modelOne.getSites();
        assertTrue("Add site three on site 1 ", sites.length == 3);

        // Site 3 Login
        modelThree = new Model();
        controllerThree = new Controller(modelThree);
        controllerThree.setContactingRemoteServer(true);
        controllerThree.setUsingMainUI(false);
        controllerThree.start(SERVER_COMMAND_LINE_OPTIONS);

        controllerThree.login("site3", "site3");
        sleep(SECS_TO_PAUSE_ON_LOGIN, "site 3 login");

        checkAllSitesPresent(3);

        checkSiteLogins();

        addTeamsAndRuns();
    }

    /**
     * Sleep for secs seconds, print comment before and after sleep.
     * 
     * @param secs
     * @param comment
     */
    private void sleep(int secs, String comment) {

        System.out.println(" Pausing "+secs+" seconds for "+comment);
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(" Paused  "+secs+" seconds (end) for "+comment);

    }

    private void addTeamsAndRuns() {

    }

    /**
     * Check that all sites are defined on all sites.
     * 
     * @param numSites
     */
    private void checkAllSitesPresent(int numSites) {
        // test all sites insure they all have 3 sites.

        Site[] sites = modelOne.getSites();
        assertTrue("Add site three on site 1, sites="+sites.length, sites.length == numSites);

        sites = modelTwo.getSites();
        assertTrue("Add site three on site 2, sites="+sites.length, sites.length == numSites);

        sites = modelThree.getSites();
        assertTrue("Add site three on site 3, sites="+sites.length, sites.length == numSites);

    }

    /**
     * Check that all sites are logged into all other sites.
     *
     */
    private void checkSiteLogins() {
        /**
         * Test whether the sites are all logged into other sites.
         */

        // For site 1 expect 2,3
        assertTrue("Site 2 not logged into 1", modelOne.isLoggedIn(clientIdTwo));
        assertTrue("Site 3 not logged into 1", modelOne.isLoggedIn(clientIdThree));

        // For site 2 expect 1,3

        assertTrue("Site 1 not logged into 2", modelTwo.isLoggedIn(clientIdOne));
        assertTrue("Site 3 not logged into 2", modelTwo.isLoggedIn(clientIdThree));

        // For site 3 expect 1,2

        assertTrue("Site 1 not logged into 3", modelThree.isLoggedIn(clientIdOne));
        assertTrue("Site 2 not logged into 3", modelThree.isLoggedIn(clientIdTwo));
    }

    @Override
    protected void tearDown() throws Exception {
        
        super.tearDown();
        controllerThree.shutdownTransport();
        controllerTwo.shutdownTransport();
        controllerOne.shutdownTransport();
    }

}
