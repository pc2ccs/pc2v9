package edu.csus.ecs.pc2.core.imports;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.model.Site;

/**
 * JUnit test for ContestXML.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestXMLTest extends TestCase {

    private final boolean debugMode = false;

    private ContestXML contestXML = new ContestXML();

    public void testEmptyContest() throws Exception {

        IInternalContest contest = new InternalContest();

        String xmlString = contestXML.toXML(contest);

        if (debugMode) {
            System.out.println("XML =" + xmlString);
            System.out.println();
        }

        assertNotNull("Contest XML should not be null ", xmlString);
    }

    public void testContest1() throws Exception {

        SampleContest sample = new SampleContest();

        IInternalContest contest = sample.createContest(3, 6, 22, 12, true);

        String xmlString = contestXML.toXML(contest);
        assertNotNull("Contest XML should not be null ", xmlString);

        if (debugMode) {
            System.out.println("XML =" + xmlString);
            System.out.println();
        }

        Site[] newSites = sample.createSites(contest, 6);
        for (Site site : newSites) {
            contest.addSite(site);
        }

        xmlString = contestXML.toXML(contest);
        assertNotNull("Contest XML should not be null ", xmlString);
        if (debugMode) {
            System.out.println("XML =" + xmlString);
        }

        Profile[] profiles = sample.createProfiles(contest, 4);
        for (Profile profile : profiles) {
            contest.addProfile(profile);
        }
        xmlString = contestXML.toXML(contest);
        assertNotNull("Contest XML should not be null ", xmlString);
        if (debugMode) {
            System.out.println("XML =" + xmlString);
        }
    }

    /**
     * Test getVersionTriplet and getVersionInteger.
     *  
     * @throws Exception
     */
    public void testgetVersionTriplet() throws Exception {

        String versionString;
        String triplet;

        versionString = "9.1.6 20100929 (Wednesday, September 29th 2010 03:53 UTC) build 2136";
        showVersion(versionString);
        triplet = contestXML.getVersionTriplet(versionString);
        assertEquals("9.1.6", triplet);

        // spaces on front
        versionString = "    9.2 20101016 (Saturday, October 16th 2010 01:57 UTC) build 2200";
        showVersion(versionString);
        triplet = contestXML.getVersionTriplet(versionString);
        assertEquals("9.2.0", triplet);

        versionString = "9.2 20101016 (Saturday, October 16th 2010 01:57 UTC) build 2200";
        showVersion(versionString);
        triplet = contestXML.getVersionTriplet(versionString);
        assertEquals("9.2.0", triplet);

        versionString = "9.2beta 20100312 (Friday, March 12th 2010 04:44 UTC) build 2043";
        showVersion(versionString);
        triplet = contestXML.getVersionTriplet(versionString);
        assertEquals("9.0.0", triplet);
        
    }
    
//    /**
//     * Get nodelist based on XPath expression.
//     * @param contest
//     * @param xPathExpression
//     * @return
//     */
//    private NodeList getXMLNodeList (IInternalContest contest, String xPathExpression) {
//        
//        try {
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            
//            String xmlString = contestXML.toXML(contest);
//            
//            Document doc = builder.parse(xmlString);
//            XPathFactory xPathfactory = XPathFactory.newInstance();
//            XPath xpath = xPathfactory.newXPath();
//            XPathExpression expr = xpath.compile(xPathExpression);
//            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
//            return nl;
//            
//        } catch (Exception e) {
//                e.printStackTrace();
//            return null;
//        }
//    }
//    
//    public void testXMLParts() throws IOException {
//        
//        SampleContest sample = new SampleContest();
//
//        IInternalContest contest = sample.createContest(3, 6, 22, 12, true);
//
//        String xmlString = contestXML.toXML(contest);
//        assertNotNull("Contest XML should not be null ", xmlString);
//        
//        String contestRoot = "/"+ContestXML.CONTEST_TAG ;
//        
//        // /howto/topic[@name='PowerBuilder']/url[2]/text()
//        
//        System.out.println("XML =" + xmlString);
//        System.out.println();
//        
//        String path = contestRoot + "/profiles[@contestid]";
//        path = contestRoot + "/profiles";
//        path = contestRoot + "/profiles[0]/name";
//        
//        NodeList nodeList = getXMLNodeList(contest, path);
//        System.out.println("Searching for "+path+ " found "+nodeList);
//        
//        // SOMEDAY fix java.net.MalformedURLException
//        /**
//         * java.net.MalformedURLException: no protocol: <?xml version="1.0" encoding="UTF-8"?>
//         */
//        
////        assertNotNull (nodeList);
//    }

    private void showVersion(String vernum) {
        if (debugMode) {
            System.out.println("Version : " + vernum);
            System.out.println("Parsed  : " + contestXML.getVersionTriplet(vernum));
        }
    }
}
