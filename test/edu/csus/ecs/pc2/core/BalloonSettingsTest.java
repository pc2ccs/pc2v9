package edu.csus.ecs.pc2.core;

import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.BalloonSettings;
import edu.csus.ecs.pc2.core.model.Contest;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Site;
import edu.csus.ecs.pc2.core.report.BalloonSettingsReport;

/**
 * Balloon Settings Test.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class BalloonSettingsTest extends TestCase {

    private IContest contest;

    private String[] colors = { "Orange", "TweedColor", "TreeColor", "Forest", "FBlack", "SeaFoam" };

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

    protected void setUp() throws Exception {
        super.setUp();

        // Create Site 1 with

        String[] languages = { "Java", "C", "APL" };
        String[] problems = { "Sumit", "Quadrangles", "Routing" };

        contest = new Contest();
        Site siteOne = createSite(1, "Site ONE", null, 0);
        contest.addSite(siteOne);

        for (String langName : languages) {
            Language language = new Language(langName);
            contest.addLanguage(language);
        }

        for (String probName : problems) {
            Problem problem = new Problem(probName);
            contest.addProblem(problem);
        }

        BalloonSettings balloonSettings = new BalloonSettings("BallonSite1", 1);
        balloonSettings.addColorList(contest.getProblems(), colors);
        balloonSettings.setEmailBalloons(true);
        balloonSettings.setPrintBalloons(true);
        balloonSettings.setEmailContact("contact@server.org");
        balloonSettings.setIncludeNos(true);
        balloonSettings.setLinesPerPage(50);
        balloonSettings.setPostscriptCapable(true);
        balloonSettings.setPrintDevice("/dev/balloonprint");
        balloonSettings.setMailServer("smtp.server.org");

        contest.addBalloonSettings(balloonSettings);
    }

    /**
     * View File
     * @param filename
     */
    public void viewFile(String filename) {
        String editorNameFullPath = "/windows/vi.bat";
        String command = editorNameFullPath + " " + filename;
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to run command " + command + " " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * View BalloonsSettingsReport.
     * @throws IOException
     */
    @SuppressWarnings("unused")
    private void viewReport() throws IOException {
        BalloonSettingsReport balloonSettingsReport = new BalloonSettingsReport();

        String filename = "/tmp/stuf324";
        Controller controller = new Controller(contest);
        balloonSettingsReport.setContestAndController(contest, controller);
        balloonSettingsReport.createReportFile(filename, null);
        viewFile(filename);
    }

    /**
     * Test settings for Balloon Settings in Contest.
     *
     */
    public void testOne() {

        BalloonSettings balloonSettings = contest.getBalloonSettings()[0];

        compareString("Mail Server", balloonSettings.getMailServer(), "smtp.server.org");
        compareString("Print Device", balloonSettings.getPrintDevice(), "/dev/balloonprint");
        compareString("Mail Contact", balloonSettings.getEmailContact(), "contact@server.org");

        compareInt("Lines Per Page", balloonSettings.getLinesPerPage(), 50);

        // balloonSettings.setIncludeNos(true);
        // balloonSettings.setPostscriptCapable(true);

        int counter = 0;
        for (Problem problem : contest.getProblems()) {
            String color = balloonSettings.getColor(problem);
            String colorName = colors[counter];
            assertTrue("Found " + color + " expected " + colorName, colors[counter].equals(color));
            counter++;
        }
    }

    private void compareString(String fieldName, String found, String expected) {
        // System.out.println("found " +found);
        // System.out.println("expected "+expected);

        assertTrue(fieldName + " found '" + found + "' expected '" + expected + "'", expected.equals(found));
    }

    private void compareInt(String fieldName, int found, int expected) {
        assertTrue(fieldName + " found " + found + "' expected '" + expected + "'", expected == found);
    }
}
