package edu.csus.ecs.pc2.core.model;

import java.util.Properties;

import junit.framework.TestCase;

/**
 * Site JUnit Test.
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public class SiteTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

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
    public static Site createSite(int siteNumber, String siteName, String hostName, int portNumber) {
        Site site = new Site(siteName, siteNumber);

        Properties props = new Properties();
        if (hostName == null) {
            hostName = "localhost";
        }

        props.put(Site.IP_KEY, hostName);

        if (portNumber == 0) {
            portNumber = 50002 + (siteNumber - 1) * 1000;
        }
        props.put(Site.PORT_KEY, "" + portNumber);

        site.setConnectionInfo(props);
        site.setPassword("site" + siteNumber);

        return site;
    }

    public void testOneSite() {

        String name = "Site Four";
        Site site4 = createSite(4, name, null, 0);

        assertTrue("Name should be " + name, site4.getDisplayName().equals(name));

        name = "2nd Site";
        int port = 4343;
        String hostName = "155.23.44.56";
        Site site2 = createSite(2, name, hostName, port);

        assertTrue("Name should be " + name, site2.getDisplayName().equals(name));

        String site2host = site2.getConnectionInfo().getProperty(Site.IP_KEY);
        String site2portStr = site2.getConnectionInfo().getProperty(Site.PORT_KEY);
        int site2port = Integer.parseInt(site2portStr);

        assertTrue("Port should be " + port, port == site2port);
        assertTrue("Host should be " + hostName, hostName.equals(site2host));
    }

}
