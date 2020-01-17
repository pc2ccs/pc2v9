package edu.csus.ecs.pc2.shadow;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import edu.csus.ecs.pc2.core.ClientUtility;
import edu.csus.ecs.pc2.core.FileUtilities;
import edu.csus.ecs.pc2.core.InternalController;
import edu.csus.ecs.pc2.core.Plugin;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;
import edu.csus.ecs.pc2.util.ContestLoadUtilities;

/**
 * Unit test.
 * 
 * @author Douglas A. Lane, PC^2 Team, pc2@ecs.csus.edu
 */
public class RemoteEventFeedMonitorTest extends AbstractTestCase {

    /**
     * Contact app server?
     * 
     * true = yes, contact server at {@link #LOCALHOST_CONTEST_EVENT_FEED}
     * false = no, skip test.
     */
//    boolean serverRunning = false;
            boolean serverRunning = true;

    /**
     * URL to contact for event feed, etc.
     */
    private static final String LOCALHOST_CONTEST_API = "https://localhost:50443/contest";

    /**
     * Tests RemoteEventFeedMonitor using running pc2 server with feeder login.
     * 
     * @throws Exception
     */
    public void testMontor() throws Exception {

        if (!serverRunning) {
            return;
        }

        String login = InternalController.loginShortcutExpansion(1, "a2").getName();

        assertEquals("Expecting login ", "administrator2", login);

        String password = login;

        Plugin plugin = ClientUtility.logInToContest(login, password);

        assertNotNull("Expeting " + login + " to be logged in, not null ", plugin);
        assertNotNull("Expeting " + login + " to be logged in ", plugin.getController());

        URL remoteURL = new URL(LOCALHOST_CONTEST_API);

        String weblogin = "admin";
        String webpassword = "admin";
        IRemoteContestAPIAdapter remoteContestAPIAdapter = new RemoteContestAPIAdapter(remoteURL, weblogin, webpassword);

        assertTrue("Expecting connection available for " + remoteURL, remoteContestAPIAdapter.testConnection());

        try {
            InputStream str = remoteContestAPIAdapter.getRemoteEventFeedInputStream();
            assertNotNull("Expecting non-null EF stream for " + remoteURL, str);
        } catch (Exception e) {
//            e.printStackTrace(System.err);
            fail("Unable to get stream for URL " + remoteURL + " " + e.getMessage());
        }

        assertNotNull("Expecting non-null RemoteContestAPIAdapter", remoteContestAPIAdapter);
        RemoteRunSubmitter submitter = new RemoteRunSubmitter(plugin.getController());

        RemoteEventFeedMonitor mon = new RemoteEventFeedMonitor(plugin.getController(), remoteContestAPIAdapter, remoteURL, weblogin, webpassword, submitter);

        System.out.println("debug 22 contacting " + LOCALHOST_CONTEST_API);
        Thread runnyBaby = new Thread(mon);
        runnyBaby.start();

        int secondsToSleep = 7;

        System.out.println("Sleeping for 7 seconds");
        Thread.sleep(secondsToSleep * 1000);

        runnyBaby.interrupt();

    }

    /**
     * Smoke test for tenproblems sample contest/CDP
     * @throws Exception
     */
    public void testTenProblemsLoad() throws Exception {

        String sampleContestName = "tenprobs";

        // load sample tenprobs contest

        File cdpConfigDir = FileUtilities.findCDPConfigDirectory(new File(sampleContestName));

        assertNotNull("Expecting sample contest " + sampleContestName, cdpConfigDir);

        assertDirectoryExists(cdpConfigDir.getAbsolutePath(), "Expected sample contest directory to exist ");

        IInternalContest contest = new InternalContest();

        assertTrue("Expecting to load TSV files ", ContestLoadUtilities.loadCCSTSVFiles(contest, cdpConfigDir));

        ContestSnakeYAMLLoader loader = new ContestSnakeYAMLLoader();
        contest = loader.fromYaml(contest, cdpConfigDir.getAbsolutePath());

        Problem[] problems = contest.getProblems();
        assertEquals("Expected problems from " + sampleContestName, 10, problems.length);
    }

    public static void main(String[] args) throws MalformedURLException {

        /**
         * Test getRemoteJSON
         */
        String addr = "Https://localhost:50443/submission_files?id=1";
        URL url = new URL(addr);
        RemoteContestAPIAdapter ad = new RemoteContestAPIAdapter(url, "admin", "admin");

        String s = ad.getRemoteJSON("");
        System.out.println("s = " + s);
    }
}
