package edu.csus.ecs.pc2.profile;

import java.io.File;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.security.FileSecurity;
import edu.csus.ecs.pc2.core.security.FileSecurityException;

/**
 * Tests for ProfileManager.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfileManagerTest extends TestCase {

    private boolean debugFlag = false;

    protected void setUp() throws Exception {
        super.setUp();
        StaticLog.setLog(new Log("PMT.log"));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testWriteRead() throws Exception {

        String title = "Sample";
        String description = "description 11";
        Profile profile1 = new Profile(title);
        profile1.setDescription(description);

        ProfileManager manager = new ProfileManager();

        String filename = "testwr" + ProfileManager.PROFILE_INDEX_FILENAME;
        if (new File(filename).isFile()){
            new File(filename).delete();
        }

        Profile profile4 = new Profile("Profile IV");
        profile4.setDescription(profile4.getName());

        Profile profile3 = new Profile("Profile III");
        profile3.setDescription(profile3.getName());

        Profile[] profiles = { profile1, profile4, profile3 };
        
        String password = "contest";
        
        createProfileFilesAndDirs(profile1, password);
        createProfileFilesAndDirs(profile3, password);
        createProfileFilesAndDirs(profile4, password);

        manager.store(filename, profiles, profile1);

        Profile profile2 = manager.defaultProfile(filename);

        assertEquals(profile1.getName(), profile2.getName());

        assertTrue("Failed defaultProfile", profile2.isSameAs(profile2));

        assertFalse("Failed defaultProfile", profile1.isSameAs(profile3));

        Profile[] list = manager.load(filename);

        if (debugFlag) {
            for (Profile profile : list) {
                System.out.println("Profile: " + profile.getName() + " " + profile.getProfilePath());
            }
        }

        // Test profiles which do not have files/dirs
        for (Profile profile : list) {

            if (debugFlag) {
                System.out.println("Profile> " + profile.getName() + " " + profile.getProfilePath());
            }
            
            boolean available = manager.isProfileAvailable(profile, password.toCharArray());
            assertTrue("Profile not available " + profile.getName()+" at "+profile.getProfilePath(),available);
        }
    }

    public void testProfileAvailable() throws Exception {

        String title = "Sample";
        String description = "description 11";
        Profile profile1 = new Profile(title);
        profile1.setDescription(description);

        ProfileManager manager = new ProfileManager();

        String filename = "testpa" + ProfileManager.PROFILE_INDEX_FILENAME;
        if (new File(filename).isFile()) {
            new File(filename).delete();
        }

        Profile profile4 = new Profile("Profile IV");
        profile4.setDescription(profile4.getName());

        Profile profile3 = new Profile("Profile III");
        profile3.setDescription(profile3.getName());

        Profile[] profiles = { profile1, profile4, profile3 };

        String password = "contest2";

        createProfileFilesAndDirs(profile1, password);
        createProfileFilesAndDirs(profile3, password);
        createProfileFilesAndDirs(profile4, password);

        manager.store(filename, profiles, profile1);

        // encrypted password files
        assertTrue(manager.isProfileAvailable(profile1, password.toCharArray()));
        assertTrue(manager.isProfileAvailable(profile3, password.toCharArray()));
        assertTrue(manager.isProfileAvailable(profile4, password.toCharArray()));

    }

    /**
     * A bunch of tests creating multiple profiles, storing, loading.
     * 
     * @throws Exception
     */
    public void testProfileManager() throws Exception {

        String profilePropertiesFileName = "storeOne.properties";

        String password = "contest";

        // Create profiles directory

        String baseDir = "profiles";
        new File(baseDir).mkdirs();

        String title = "Profile One";
        Profile profile1 = new Profile(title);
        String description = "description 11";
        profile1.setDescription(description);

        createProfileFilesAndDirs(profile1, password);

        Profile profile4 = new Profile("Profile IV");
        profile4.setDescription(profile4.getName());

        createProfileFilesAndDirs(profile4, password);

        Profile profile3 = new Profile("Profile III");
        profile3.setDescription(profile3.getName());

        createProfileFilesAndDirs(profile3, password);

        Profile profile2 = new Profile("Profile Two");
        profile2.setDescription(profile3.getName());

        createProfileFilesAndDirs(profile2, password);

        ProfileManager manager = new ProfileManager();

        Profile[] profiles = { profile1, profile4, profile3, profile2 };

        manager.store(profilePropertiesFileName, profiles, profile1);

        for (Profile profile : profiles) {
            assertTrue(manager.isProfileAvailable(profile, password.toCharArray()));
        }
        
        Profile[] loadedProfiles = manager.load(profilePropertiesFileName);
        
        // Compare saved profiles with loaded profiles
        
        for (Profile profile : profiles){
            Profile foundProfile = findProfile(loadedProfiles, profile);
            compareProfiles (profile, foundProfile);
        }
        
        
    }

    public void testStoreOne() throws Exception {

        ProfileManager manager = new ProfileManager();
        
        String profilePropertiesFileName = "storeOne.properties";

        String title = "Profile One";
        Profile profile1 = new Profile(title);
        String description = "description 11";
        profile1.setDescription(description);

        Profile[] profiles = { profile1 };

        manager.store(profilePropertiesFileName, profiles, profile1);

        manager = new ProfileManager();

        Profile[] loadedProfiles = manager.load(profilePropertiesFileName);
        
        compareProfiles (profile1, loadedProfiles[0]);

    }

    private void compareProfiles(Profile profile1, Profile profile2) {
        
//        assertEquals(profile1.getContestId(), profile2.getContestId());
//        assertEquals(profile1.getDescription(), profile2.getDescription());
        assertEquals(profile1.getName(), profile2.getName());
//        assertEquals(profile1.getSiteNumber(), profile2.getSiteNumber());
        assertEquals(profile1.getProfilePath(), profile2.getProfilePath());
        
    }

    /**
     * Create profile directory and security/encryption information.
     * 
     * @param profile
     * @param password
     * @throws FileSecurityException
     */
    private void createProfileFilesAndDirs(Profile profile, String password) throws FileSecurityException {
        
        String profileDirectory = profile.getProfilePath();
        
        if (new File(profileDirectory).isDirectory()){
            new Exception("Directory already exists: "+profileDirectory);
        }
        
        new File(profileDirectory).mkdirs();
        
        FileSecurity fileSecurity = new FileSecurity(profileDirectory);
        fileSecurity.saveSecretKey(password.toCharArray());
    }

    /**
     * Test merge with two null profile lists.
     * 
     * @throws Exception
     */
    public void testMergeOne() throws Exception {

        ProfileManager profileManager = new ProfileManager();

        Profile[] profiles = profileManager.mergeProfiles(null, null);
        assertNotNull(profiles);
    }

    public void testMergeTwo() throws Exception {

        ProfileManager profileManager = new ProfileManager();

        String title = "Sample";
        String description = "description 11";
        Profile profile1 = new Profile(title);
        profile1.setDescription(description);

        Profile profile3 = new Profile("Profile III");
        profile3.setDescription(profile3.getName());

        Profile profile4 = new Profile("Profile IV");
        profile4.setDescription(profile4.getName());

        Profile[] threeProfiles = { profile1, profile4, profile3 };

        Profile[] profiles = profileManager.mergeProfiles(null, threeProfiles);

        assertNotNull(profiles);

        assertEquals("Merge has same number profiles", profiles.length, threeProfiles.length);

        profiles = profileManager.mergeProfiles(threeProfiles, threeProfiles);

        assertNotNull(profiles);

        assertEquals("Merge has same number profiles", profiles.length, threeProfiles.length);

        Profile profile2 = new Profile("Profile 2");
        profile2.setDescription(profile2.getName());

        Profile[] list2 = { profile2, profile1 };

        profiles = profileManager.mergeProfiles(list2, threeProfiles);

        assertNotNull(profiles);

        assertEquals("Merge has same number profiles", 4, profiles.length);

        Profile[] allProfiles = { profile1, profile2, profile3, profile4 };

        for (Profile profile : allProfiles) {
            assertNotNull("Profile present", findProfile(profiles, profile));
        }

    }

    /**
     * Returns true if inProfile is in list of profiles.
     * 
     * @param profiles
     * @param inProfile
     * @return
     */
    private Profile findProfile(Profile[] profiles, Profile inProfile) {

        for (Profile profile : profiles) {
            if (profile.getProfilePath().equals(inProfile.getProfilePath())) {
                return profile;
            }
        }
        return null;
    }
}
