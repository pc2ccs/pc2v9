package edu.csus.ecs.pc2.core.model;

import java.io.File;

import edu.csus.ecs.pc2.core.security.FileSecurity;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import junit.framework.TestCase;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfileTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    /**
     * Create profile DB directory.
     * 
     * @param profile
     * @return the directory where the profile was saved
     * @throws FileSecurityException
     */
    public static String  createProfileFilesAndDirs(Profile profile) throws FileSecurityException {
        
        String profileDirectory = profile.getProfilePath() + File.separator + "db."+profile.getSiteNumber();
        
        if (new File(profileDirectory).isDirectory()){
            new Exception("Directory already exists: "+profileDirectory);
        }
        
        new File(profileDirectory).mkdirs();
        
        return profileDirectory;
    }
    
    /**
     * Create profile DB directory and initializes security (writes contest password).
     * 
     * @param profile
     * @param password
     * @return the directory where the profile was saved
     * @throws FileSecurityException
     */
    public static String createProfileFilesAndDirs(Profile profile, String password) throws FileSecurityException {
        
        String profileDirectory = createProfileFilesAndDirs(profile);
        
        FileSecurity fileSecurity = new FileSecurity(profileDirectory);
        fileSecurity.saveSecretKey(password.toCharArray());
        
        return profileDirectory;
    }
    

    public void testEquals() {

        Profile profile1 = new Profile("One");
        Profile profile2 = new Profile("One");

        assertEquals(profile1, profile1);
        assertNotSame(profile1, profile2);
        assertNotSame(profile2, profile1);

        assertEquals(profile1.getName(), profile2.getName());

        assertNotSame(profile1.getElementId(), profile2.getElementId());
    }

    public void testNullContructor() {
        try {
            @SuppressWarnings("unused")
            Profile profile3 = new Profile(null);
            assertFalse("Profile should fail with a null parameter constructor", true);
        } catch (IllegalArgumentException e) {
            // expected exception - valid
            assertTrue("expected exception", true);
        } catch (Exception e) {
            assertFalse("Profile should fail with a IllegalArgumentException exception", true);
        }
    }
}
