package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.util.Random;

import edu.csus.ecs.pc2.core.IStorage;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.security.FileStorage;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;
import edu.csus.ecs.pc2.profile.ProfileCloneSettings;

/**
 * Test ConfigurationIO class.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ConfigurationIOTest extends AbstractTestCase {


    protected String getTestDirectoryName(){
        String testDir = "testing";
        
        if (!new File(testDir).isDirectory()) {
            new File(testDir).mkdirs();
        }

        return testDir;
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

        String configDirName = getTestDirectoryName() + File.separator + "configio" + File.separator + "testdir" + getRandNumber();

        IStorage storage = createStorage(configDirName);

        Log log = new Log(configDirName, "configio.log");

        ConfigurationIO configurationIO = new ConfigurationIO(storage);

        String contestPassword = "pass22for66";

        char[] pass = contestPassword.toCharArray();

        Profile origProfile = new Profile("Orig profile");
        ProfileCloneSettings settings = new ProfileCloneSettings("name", "title", pass, origProfile);
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

        assertEquals(settings.getName(), settings2.getName());
        assertEquals(settings.getDescription(), settings2.getDescription());
        assertEquals(settings.getContestTitle(), settings2.getContestTitle());

        assertEquals(settings.isCopyGroups(), settings2.isCopyGroups());
        assertEquals(settings.isResetContestTimes(), settings2.isResetContestTimes());

        compareArrays(settings.getContestPassword(), settings2.getContestPassword());

        // Unset, comparing default values
        assertEquals(settings.isCopyAccounts(), settings2.isCopyAccounts());

    }
    
    public void testSaveFinalizeData() throws Exception {
        
        int siteNumber = 5;

        IInternalContest contest = new InternalContest();
        contest.setSiteNumber(siteNumber);

        String configDirName = getTestDirectoryName() + File.separator + "configio" + File.separator + "testdir" + getRandNumber();

        IStorage storage = createStorage(configDirName);

        Log log = new Log(configDirName, "configioSaveFinalData.log");

        ConfigurationIO configurationIO = new ConfigurationIO(storage);
        
        FinalizeData finalizeData = null;
        
        contest.setFinalizeData(finalizeData); // null
        
        /**
         * Write configuration.
         */

        boolean wroteConfig = configurationIO.store(contest, log);    
        
        assertTrue("Failed to write configuration, check log in  " + configDirName, wroteConfig);
        
        /**
         * Read configuration.
         */
        
        configurationIO = null;

        configurationIO = new ConfigurationIO(storage);

        contest = new InternalContest();
        
        boolean readConfig = configurationIO.loadFromDisk(siteNumber, contest, log);

        assertTrue("Failed to read configuration, check log in  " + configDirName, readConfig);
        
        assertNull("Failed, FinalData should be null", contest.getFinalizeData());
        
        finalizeData = createSampFinalData();
        contest.setFinalizeData(finalizeData);
       
        wroteConfig = configurationIO.store(contest, log);    
        
        assertTrue("Failed to write configuration, check log in  " + configDirName, wroteConfig);
        
        configurationIO = null;

        configurationIO = new ConfigurationIO(storage);

        contest = new InternalContest();
        
        readConfig = configurationIO.loadFromDisk(siteNumber, contest, log);

        assertTrue("Failed to read configuration, check log in  " + configDirName, readConfig);
        
        assertNotNull("Failed, FinalData should NOT be null", contest.getFinalizeData());
        
        FinalizeData data2 = contest.getFinalizeData();

        compareData(finalizeData, data2);
        
    }
    
    /**
     * Compare Finalize Data's.
     * 
     * @param data1
     * @param data2
     */
    private void compareData(FinalizeData data1, FinalizeData data2) {
        
        assertEquals("gold rank should be equal ", data1.getGoldRank(), data2.getGoldRank());
        assertEquals("silver rank should be equal ", data1.getSilverRank(), data2.getSilverRank());
        assertEquals("bronze rank should be equal ", data1.getBronzeRank(), data2.getBronzeRank());
        assertTrue("finalize comment should be same ", data1.getComment().equals(data2.getComment()));
        
    }

    private FinalizeData createSampFinalData() {
        FinalizeData data = new FinalizeData();
        int rank = 1;
        rank += 8;
        data.setGoldRank(rank);
        rank += 5;
        data.setSilverRank(rank);
        rank += 10;
        data.setBronzeRank(rank);
        data.setComment("Finalized by Director of Operations");
        return data;
    }

    private void compareArrays(char[] charArray, char[] charArray2) {

        assertEquals(charArray.length, charArray2.length);

        for (int i = 0; i < charArray.length; i++) {
            assertEquals(charArray[i], charArray2[i]);
        }
    }

    /**
     * 
     * @return random number
     */
    private int getRandNumber() {
        Random r = new Random();
        r.setSeed(System.nanoTime());
        return r.nextInt(32760);
    }
    
    /**
     * Test that backup file created when settings written to disk.
     * 
     * Unit test Bug 876.
     * @throws Exception
     */
    public void testBackupFiles() throws Exception {
        
        String configDirName = getOutputDataDirectory("testBackupFiles");
        
        removeDirectory(configDirName); // remove previous files
        
        int siteNumber = 5;

        IInternalContest contest = new InternalContest();
        contest.setSiteNumber(siteNumber);

        IStorage storage = createStorage(configDirName);

        Log log = new Log(configDirName, "configioSaveFinalData.log");

        ConfigurationIO configurationIO = new ConfigurationIO(storage);
        
        boolean wroteConfig = configurationIO.store(contest, log);
        
        assertTrue("Expecting to write config ", wroteConfig);
        
        configurationIO.store(contest, log);
        
//        startExplorer(new File (configDirName));
        
        assertExpectedFileCount("Expected file count ", new File(configDirName), 5);
        
    }

}
