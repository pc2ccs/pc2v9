package edu.csus.ecs.pc2.core.model;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.csus.ecs.pc2.core.security.FileSecurity;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.util.AbstractTestCase;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfileTest extends AbstractTestCase {
    
//    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmssSSS");
//    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH_mm_ss.SSS-ddMMMyyyy");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
//    "++%Y%m%d-%H%M%S"


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
        
        // not pretty but this is static and abstractTestCase getOutputDirectory() is not
        String profileDirectory = AbstractTestCase.DEFAULT_PC2_OUTPUT_FOR_TESTING_DIRECTORY + File.separator + "ProfileTest" + File.separator + profile.getProfilePath() + File.separator + "db."
                + profile.getSiteNumber();
        
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
    
    private String createProfilePath(String basepath) {
        // profiles/P2ef182be-b8ed-42c0-88ca-19dfef3419c3/archive
        if (basepath == null || basepath.trim().length() == 0) {
            basepath = "";
        } else if (basepath.substring(basepath.length() - 1).equals(File.separator)) {
            basepath += File.separator;
        }
        String dateString = simpleDateFormat.format(new Date());
        return basepath + "profiles" + File.separator + "P" + dateString;
    }
    
    public static void main(String[] args) throws InterruptedException {
        
        Profile profile = new Profile("Default");
        
        System.out.println(profile.getProfilePath());
        
        ProfileTest profileTest = new ProfileTest();
        
        for (int i= 0; i < 12; i++){
        String s = profileTest.createProfilePath("");
        System.out.println(s);
           Thread.sleep(4000);
        }
       
        
    }
}
