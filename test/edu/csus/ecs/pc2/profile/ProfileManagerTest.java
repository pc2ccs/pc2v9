package edu.csus.ecs.pc2.profile;

import java.io.File;

import junit.framework.TestCase;
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
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testWriteRead() throws Exception {

        String title = "Sample";
        String description = "description 11";
        Profile profile1 = new Profile(title);
        profile1.setDescription(description);
        profile1.setProfilePath("/tmp/foo2");

        ProfileManager manager = new ProfileManager();

        String filename = "test" + ProfileManager.PROFILE_INDEX_FILENAME;

        Profile profile4 = new Profile("Profile IV");
        profile4.setDescription(profile4.getName());
        profile4.setProfilePath("/tmp/fooIV");

        Profile profile3 = new Profile("Profile III");
        profile3.setDescription(profile3.getName());
        profile3.setProfilePath("/tmp/foo3");

        Profile[] profiles = { profile1, profile4, profile3 };

        manager.store(filename, profiles, profile1);

        Profile profile2 = manager.defaultProfile(filename);

        assertEquals(profile1.getName(), profile2.getName());

        assertTrue("Failed defaultProfile", profile2.isSameAs(profile2));

        assertFalse("Failed defaultProfile", profile1.isSameAs(profile3));

        Profile[] list = manager.load(filename);

        if (debugFlag) {
            for (Profile profile : list) {
                System.out.println("Profile " + profile.getName());
            }
        }
        
        // Test profiles which do not have files/dirs
        for (Profile profile : list) {
            
            assertFalse("Profile not available "+profile.toString(),
            manager.isProfileAvailable(profile, "contest".toCharArray()));
               
        }
    }
    
    public void testProfileAvailable() throws Exception {
        
        String title = "Sample";
        String description = "description 11";
        Profile profile1 = new Profile(title);
        profile1.setDescription(description);
        profile1.setProfilePath("/tmp/foo2");

        ProfileManager manager = new ProfileManager();

        String filename = "test" + ProfileManager.PROFILE_INDEX_FILENAME;

        Profile profile4 = new Profile("Profile IV");
        profile4.setDescription(profile4.getName());
        profile4.setProfilePath("/tmp/fooIV");

        Profile profile3 = new Profile("Profile III");
        profile3.setDescription(profile3.getName());
        profile3.setProfilePath("/tmp/foo3");

        Profile[] profiles = { profile1, profile4, profile3 };

        manager.store(filename, profiles, profile1);
        
        String password = "contest2";
        
        createEncryptedDataFiles(profile1.getProfilePath(), password.toCharArray());
        createEncryptedDataFiles(profile3.getProfilePath(), password.toCharArray());
        
        // no encrypted password files
        assertFalse(manager.isProfileAvailable(profile4, password.toCharArray()));
        
        // encrypted password files
        assertTrue(manager.isProfileAvailable(profile1, password.toCharArray()));
        assertTrue(manager.isProfileAvailable(profile3, password.toCharArray()));
        
    }

    /**
     * Create encrypted files in directory.
     * 
     * This creates the files which store the contest password.  Creating
     * these files and using the correct contest password is required
     * for {@link ProfileManager#isProfileAvailable(Profile, char[])}
     * to return true.
     * 
     * @param profilePath
     * @param password
     * @throws FileSecurityException
     */
    private void createEncryptedDataFiles(String profilePath, char[] password) throws FileSecurityException {
        
        FileSecurity fileSecurity = new FileSecurity(profilePath);
        fileSecurity.saveSecretKey(password);
        
    }
    
    public void testManagerStartup() throws Exception {

        String password = "contest22";
        
        // Create profiles directory
        
        String baseDir = "profiles";
        
        new File(baseDir).mkdirs();
        
        String title = "Profile One";
        Profile profile1 = new Profile(title);
        String description = "description 11";
        profile1.setDescription(description);

        createProfileFilesAndDirs (profile1, password);

        Profile profile4 = new Profile("Profile IV");
        profile4.setDescription(profile4.getName());
        
        createProfileFilesAndDirs (profile4, password);
        
        Profile profile3 = new Profile("Profile III");
        profile3.setDescription(profile3.getName());
        
        createProfileFilesAndDirs (profile3, password);
        
        Profile profile2 = new Profile("Profile Two");
        profile2.setDescription(profile3.getName());
        
        createProfileFilesAndDirs (profile2, password);
        
        ProfileManager manager = new ProfileManager();
        
        Profile[] profiles = { profile1, profile4, profile3, profile2 };

        manager.store(profiles, profile1);
        
        for (Profile profile : profiles){
            assertTrue(manager.isProfileAvailable(profile, password.toCharArray()));
        }
        
        
    }

    /**
     * Create profile directory and security/encryption information.
     * 
     * @param profile
     * @param password
     * @throws FileSecurityException 
     */
    private void createProfileFilesAndDirs(Profile profile, String password) throws FileSecurityException {
        new File(profile.getProfilePath()).mkdirs();
        createEncryptedDataFiles(profile.getProfilePath(), password.toCharArray());
    }
}
