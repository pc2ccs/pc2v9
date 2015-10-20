package edu.csus.ecs.pc2.core.model;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.list.ProblemList;

/**
 * Test for BalloonSettings class.
 * 
 * @author pc2@ecs.csus.edu
 * 
 * @version $Id$
 */

// $HeadURL$
public class BalloonSettingsTest extends TestCase {
    
    private ProblemList problemList = new ProblemList();
    private String[] problemNames = { "Sumit", "Quadrangles", "Routing" };
    private String[] colorNames  = { "Red", "White", "Blue" };

    protected void setUp() throws Exception {
        super.setUp();
        
        for (String probName : problemNames) {
            Problem problem = new Problem(probName);
            problemList.add(problem);
        }
    }

    /**
     * Get a new populated BalloonSettings
     * 
     * @return BalloonSettings
     */
    private BalloonSettings getNewBalloonSettings() {
        BalloonSettings b2 = new BalloonSettings("BalloonSettings One", 4);
        
        b2.setEmailBalloons(true);
        b2.setPrintBalloons(true);
        
        b2.setMailServer("smtp.google.com");
        b2.setEmailContact("admin@contest.org");

        b2.setSiteNumber(4);
        
        for (int i = 0 ; i < colorNames.length; i ++) {
            String colorName = colorNames[i];
            Problem problem = problemList.getList()[i];
            b2.addColor(problem, colorName);
        }
        
        return b2;
    }

    public void checkSetting(String title, String expected, String found, BalloonSettings balloonSettings1, BalloonSettings balloonSettings2) {
        assertFalse("Is same as, should not be: " + title + " expected " + expected + " found " + found, balloonSettings1.isSameAs(balloonSettings2));
        assertFalse("Is same as, should not be:  " + title + " expected " + found + " found " + expected, balloonSettings2.isSameAs(balloonSettings1));
    }

    // private void checkSetting(String string, boolean b, boolean c, BalloonSettings b1, BalloonSettings b2) {
    public void checkSetting(String title, boolean expected, boolean found, BalloonSettings balloonSettings1, BalloonSettings balloonSettings2) {
        assertFalse("Is same as, should not be:  " + title + " expected " + expected + " found " + found, balloonSettings1.isSameAs(balloonSettings2));
        assertFalse("Is same as, should not be:  " + title + " expected " + found + " found " + expected, balloonSettings2.isSameAs(balloonSettings1));
    }

    // private void checkSetting(String string, int siteNumber, int siteNumber2, BalloonSettings b1, BalloonSettings b2) {
    public void checkSetting(String title, int expected, int found, BalloonSettings balloonSettings1, BalloonSettings balloonSettings2) {
        assertFalse("Is same as, should not be:  " + title + " expected " + expected + " found " + found, balloonSettings1.isSameAs(balloonSettings2));
        assertFalse("Is same as, should not be:  " + title + " expected " + found + " found " + expected, balloonSettings2.isSameAs(balloonSettings1));
    }

    public void testIsSameIs() {

        BalloonSettings b1 = getNewBalloonSettings();
        BalloonSettings b2 = getNewBalloonSettings();

        assertTrue("Is same As", b1.isSameAs(b2));
        assertFalse("Is not same As, null parameter", b1.isSameAs(null));

        b2 = getNewBalloonSettings();
        b2.setEmailBalloons(false);
        checkSetting(" setEmailBalloons", b1.getEmailContact(), b2.getEmailContact(), b1, b2);

        b2 = getNewBalloonSettings();
        b2.setPrintDevice("/dev/null");
        checkSetting(" setPrintDevice ", b1.getPrintDevice(), b2.getPrintDevice(), b1, b2);

        b2 = getNewBalloonSettings();
        b2.setMailServer("mail");
        checkSetting(" setMailServer ", b1.getMailServer(), b2.getMailServer(), b1, b2);
        
        b2 = getNewBalloonSettings();
        b2.setEmailContact("foo@foo.com");
        checkSetting(" setEmailContact ", b1.getEmailContact(), b2.getEmailContact(), b1, b2);

        
        b2 = getNewBalloonSettings();
        b2.addColor(new Problem("Problem X"), "Orange");
        checkSetting(" addColor ", b1.getMailServer(), b2.getMailServer(), b1, b2);
        
        b2 = getNewBalloonSettings();
        Problem problem = problemList.getList()[2];
        b2.updateColor(problem, "Black");
        checkSetting(" updateColor prob 2 color black ", b1.getColor(problem), b2.getColor(problem), b1, b2);
        
        b2 = getNewBalloonSettings();
        b2.setSiteNumber(9);
        checkSetting(" setSiteNumber 9", b1.getSiteNumber(), b2.getSiteNumber(), b1, b2);

    }

}
