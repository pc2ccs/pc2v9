package edu.csus.ecs.pc2.profile;

import java.io.File;
import java.io.IOException;

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

        String title = "PMTest";
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

        Profile profile2 = manager.getDefaultProfile(filename);

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
    
    public void testDefaultProfile() throws Exception {

        String title = "PMTest";
        String description = "description 12";
        Profile profile1 = new Profile(title);
        profile1.setDescription(description);

        ProfileManager manager = new ProfileManager();

        String filename2 = ProfileManager.PROFILE_INDEX_FILENAME;
        if (new File(filename2).isFile()) {
            new File(filename2).delete();
        }

        String password = "foo";

        Profile[] profiles = createProfiles("PMTest sample", password, 6);

        manager.store(profiles, profile1);

        manager = new ProfileManager();
        Profile defProf = manager.getDefaultProfile();

        compareProfiles(profile1, defProf);
    }


    public void testDefaultProfile2() throws Exception {

        String title = "PMTest";
        String description = "description 11";
        Profile profile1 = new Profile(title);
        profile1.setDescription(description);

        ProfileManager manager = new ProfileManager();

        String filename = "testdp" + ProfileManager.PROFILE_INDEX_FILENAME;
        if (new File(filename).isFile()) {
            new File(filename).delete();
        }
        
        Profile [] profiles = { profile1 };
        
        manager.store(filename, profiles, profile1);
        
        manager = new ProfileManager();
        Profile defProf = manager.getDefaultProfile (filename);
        
        compareProfiles (profile1, defProf);
    }
    
    public void testProfileAvailable() throws Exception {

        String title = "PMTest";
        String description = "description 11";
        Profile profile1 = new Profile(title);
        profile1.setDescription(description);

        ProfileManager manager = new ProfileManager();

        String filename = "testpa" + ProfileManager.PROFILE_INDEX_FILENAME;
        if (new File(filename).isFile()) {
            new File(filename).delete();
        }

        Profile profile4 = new Profile("PMTest Profile IV");
        profile4.setDescription(profile4.getName());

        Profile profile3 = new Profile("PMTest Profile III");
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
    
    private Profile createProfile(String name, String password) throws FileSecurityException {
        Profile profile3 = new Profile(name);
        profile3.setDescription(profile3.getName());

        try {
            createProfileFilesAndDirs(profile3, password);
            return profile3;
        } catch (FileSecurityException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    private Profile[] createProfiles(String prefix, String password, int count) {

        Profile[] profiles = new Profile[count];

        for (int i = 0; i < count; i++) {
            try {
                String name = prefix + " " + (i + 1);
                Profile profile = createProfile(name, password);
                profile.setDescription("Desc: " + profile.getName());
                profiles[i] = profile;
            } catch (FileSecurityException e) {
                e.printStackTrace();
            }
        }
        return profiles;
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
        
        String profileDirectory = profile.getProfilePath() + File.separator + "db."+profile.getSiteNumber();
        
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

        String title = "PMTest";
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
    
    public static void main(String[] args) {
        
        ProfileManager manager = new ProfileManager();
        
        try {
            
            Profile profile = manager.getDefaultProfile();
            
            System.out.println();
            System.out.println("Profile name: " + profile.getName());
            System.out.println("  contest id = " + profile.getContestId());
            System.out.println("  element id = " + profile.getElementId());
            System.out.println("  descripton = " + profile.getDescription());
            System.out.println("  path       = " + profile.getProfilePath());
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ProfileLoadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}
