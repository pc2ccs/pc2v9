package edu.csus.ecs.pc2.core.model;

import java.util.Properties;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.RomanNumeral;

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
    
    /**
     * test the .format method
     */
    public void testFormat (){
        
        String name = "Site Four";
        Site site4 = createSite(4, name, null, 0);

        assertTrue("Name should be " + name, site4.getDisplayName().equals(name));
         
        String pattern = Site.NUMBER_PATTERN;
        assertEquals("4", "" + site4.format(pattern));
        
        pattern = Site.NUMBER_PATTERN + " - " + Site.LONG_NAME_PATTERN;
        assertEquals( "4 - Site Four", "" + site4.format(pattern));
        
        pattern = Site.SHORT_NAME_PATTERN;
        assertEquals("Site 4", "" + site4.format(pattern));
        
        pattern = Site.LONG_NAME_PATTERN;
        assertEquals("Site Four", "" + site4.format(pattern));
    }
    
    
    
    /**
     * Test add proxy and is proxy.
     * @throws Exception
     */
    public void testAddProxy() throws Exception {

        int siteCount = 12;

        IInternalContest contest = new SampleContest().createStandardContest();

        for (int i = 4; i < siteCount + 1; i++) {
            if (i != contest.getSiteNumber()) {
                Site site = createSite(i, "Site " + getNumber(i), null, 0);
                contest.addSite(site);
            }
        }
        
        assertEquals(siteCount, contest.getSites().length);
        
        Site lastSite = contest.getSites()[siteCount-1];
        
        assertFalse(lastSite.isProxyFor(5));
        
        lastSite.addProxySite(5);
        assertTrue(lastSite.isProxyFor(5));
        
        lastSite.addProxySite(6);
        assertTrue(lastSite.isProxyFor(6));
        
        assertEquals("Expecting proxy sites ", 2, lastSite.getProxySites().length);
        
        assertFalse(lastSite.isProxyFor(2));
        
        assertTrue(lastSite.isProxyFor(5));
        assertTrue(lastSite.isProxyFor(6));
        
        lastSite.removeAllProxies();
        assertFalse(lastSite.isProxyFor(5));
        assertFalse(lastSite.isProxyFor(6));
        
        Site thisSite = contest.getSite(contest.getSiteNumber());
        assertNotNull(thisSite);
        
        // Proxy all other sites
        for (int i = 1; i < siteCount + 1; i++) {
            thisSite.addProxySite(i);
        }
        
        assertEquals("proxied sites ", siteCount-1, thisSite.getProxySites().length);
        
        assertTrue(thisSite.isProxyFor(5));
        assertTrue(thisSite.isProxyFor(6));
        assertTrue(thisSite.isProxyFor(lastSite.getSiteNumber()));
        
        // must not be proxy for ourselves
        assertFalse(thisSite.isProxyFor(contest.getSiteNumber()));
        
        // proxies for all sites for this site.
        assertEquals("Expecting proxy sites ", siteCount - 1, thisSite.getProxySites().length);
    }

    private String getNumber(int i) {
        RomanNumeral roman = new RomanNumeral(i);
        return roman.toString();
    }

}
