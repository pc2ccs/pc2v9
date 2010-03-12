package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.util.Random;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.security.FileStorage;
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;

/**
 * Test ConfigurationIO class.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ConfigurationIOTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Create testing directory and initialize storage.
     * 
     * @param dirname
     * @return
     */
    protected IStorage createStorage(String dirname) {
        if (!new File(dirname).isDirectory()) {
            new File(dirname).mkdirs();
        }
        return new FileStorage(dirname);
    }

    public void testProfileCloneSettings() throws Exception {

        int siteNumber = 5;

        IInternalContest contest = new InternalContest();
        contest.setSiteNumber(siteNumber);

        String configDirName = "configio" + File.separator + "testdir" + getRandNumber();

        IStorage storage = createStorage(configDirName);

        Log log = new Log(configDirName, "configio.log");

        ConfigurationIO configurationIO = new ConfigurationIO(storage);

        String contestPassword = "pass22for66";

        char[] pass = contestPassword.toCharArray();

        ProfileCloneSettings settings = new ProfileCloneSettings("name", "title", pass);
        settings.setCopyGroups(true);
        settings.setResetContestTimes(true);

        contest.setProfileCloneSettings(settings);

        boolean wroteConfig = configurationIO.store(contest, log);

        assertTrue("Failed to write configuration, check log in  " + configDirName, wroteConfig);

        contest = new InternalContest();

        configurationIO = null;

        configurationIO = new ConfigurationIO(storage);

        boolean readConfig = configurationIO.loadFromDisk(siteNumber, contest, log);

        assertTrue("Failed to read configuration, check log in  " + configDirName, readConfig);

        ProfileCloneSettings settings2 = contest.getProfileCloneSettings();

        assertNotNull("Settings should not be null", settings2);

        assertEquals(contest.getSiteNumber(), siteNumber);

        assertEquals(settings.getTitle(), settings2.getTitle());
        assertEquals(settings.getName(), settings2.getName());

        assertEquals(settings.isCopyGroups(), settings2.isCopyGroups());
        assertEquals(settings.isResetContestTimes(), settings2.isResetContestTimes());

        compareArrays(settings.getContestPassword(), settings2.getContestPassword());

        // Unset, comparing default values
        assertEquals(settings.isCopyAccounts(), settings2.isCopyAccounts());

    }

    private void compareArrays(char[] charArray, char[] charArray2) {

        assertEquals(charArray.length, charArray2.length);

        for (int i = 0; i < charArray.length; i++) {
            assertEquals(charArray[i], charArray2[i]);
        }
    }

    /**
     * 
     * @return randome number
     */
    private int getRandNumber() {
        Random r = new Random();
        r.setSeed(System.nanoTime());
        return r.nextInt(32760);
    }

}
