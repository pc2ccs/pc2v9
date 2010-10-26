package edu.csus.ecs.pc2.profile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.core.model.ProfileComparatorByName;
import edu.csus.ecs.pc2.core.model.SampleContest;
import edu.csus.ecs.pc2.core.security.FileSecurity;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import edu.csus.ecs.pc2.core.security.FileStorage;

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

        String filename = getFullName ("testwr" + ProfileManager.PROFILE_INDEX_FILENAME);
        removeFile (filename);

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

        String filename = getFullName ("testdp" + ProfileManager.PROFILE_INDEX_FILENAME);
        removeFile(filename);
        
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
        
        String filename = getFullName ("testpa" + ProfileManager.PROFILE_INDEX_FILENAME);
        removeFile(filename);

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

        String profilePropertiesFileName = getFullName ("testPM.properties");
        removeFile(profilePropertiesFileName);

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
        
        String profilePropertiesFileName = getFullName ("storeOne.properties");
        removeFile(profilePropertiesFileName);

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
        
        assertEquals(profile1.getContestId(), profile2.getContestId());
        assertEquals(profile1.getDescription(), profile2.getDescription());
        assertEquals(profile1.getName(), profile2.getName());
        assertEquals(profile1.getSiteNumber(), profile2.getSiteNumber());
        assertEquals(profile1.getProfilePath(), profile2.getProfilePath());
        assertEquals(profile1.isActive(), profile2.isActive());
        
        assertTrue("Profiles should be same ", profile1.isSameAs(profile2));
        
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

        String profilePropertiesFileName = getFullName ("mergeTwo.properties");
        removeFile(profilePropertiesFileName);

        ProfileManager profileManager = new ProfileManager(profilePropertiesFileName);

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
    
    public void testMergeThree() throws Exception {
        
        String filename = getFullName( "testm3.properties");
        removeFile (filename);

        String testdirName = getTestDirectoryName();
        
        int numProfilesGenerated = 8;
        
        SampleContest sample = new SampleContest();

        IInternalContest contest = sample.createContest(3, 3, 3, 3, true);
        contest.setStorage(new FileStorage(testdirName));
        Profile[] addedProfiles = sample.createProfiles(contest, numProfilesGenerated - 1);
        for (Profile profile3 : addedProfiles) {
            contest.updateProfile(profile3);
        }
        
        Profile [] theList = contest.getProfiles();

        ProfileManager manager1 = new ProfileManager(filename);
        manager1.store(theList, contest.getProfile());

        Profile savedProfile = contest.getProfile();

        InternalContest contest2 = new InternalContest();
        contest2.setSiteNumber(savedProfile.getSiteNumber());
        contest2.setProfile(savedProfile);
        
        manager1.mergeProfiles(contest2);
        
        for (Profile profileFive : contest2.getProfiles()){
            Profile [] matches = findMatches(profileFive, theList);
            if (matches.length != 1){
                fail ("Could not find profile in list "+profileFive.getName());
            } 
            
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
    
    protected IInternalContest createConstest(Profile [] profiles, Profile profile){
        
        InternalContest contest = new InternalContest();
        
        for (Profile p : profiles){
            contest.addProfile(p);
        }
        
        contest.setProfile(profile);
        return contest;
    }
    
    protected String getTestDirectoryName(){
        String testDir = "testing";
        
        if (!new File(testDir).isDirectory()) {
            new File(testDir).mkdirs();
        }

        return testDir;
    }
    
    protected String getFullName (String filename){
        return getTestDirectoryName() + File.separator + filename;
    }
    
    protected void dumpProfiles(String name, Profile[] profiles) {
        System.out.println("dumpProfile - " + name);
        Arrays.sort(profiles, new ProfileComparatorByName());
        for (Profile profile : profiles) {
            dumpProfile(null, profile);
        }
    }
    
    protected void dumpProfile(String name, Profile profile) {

        if (name != null) {
            System.out.println("dumpProfile - " + name);
        }
        System.out.printf("%-15s %6s %02d %-30s %-20s", profile.getName(), Boolean.toString(profile.isActive()), profile.getSiteNumber(), profile.getContestId(), profile.getDescription());
        System.out.println(" Path=" + profile.getProfilePath());

        // Longer/Prettier version.
        // System.out.println("profile name  : " + profile.getName());
        // System.out.println("  description : " + profile.getDescription());
        // System.out.println("  create date : " + profile.getCreateDate().toString());
        // System.out.println("  site number : " + profile.getSiteNumber());
        // System.out.println("   element id : " + profile.getElementId());
        // System.out.println("       active : " + profile.isActive());
        // System.out.println("   contest id : " + profile.getContestId());
        // System.out.println("         path : " + profile.getProfilePath());

    } 
    
    /**
     * Tests whether if the default profile is changed.
     * @throws Exception
     */
    public void testMergeFour() throws Exception {
        
        String filename = getFullName( "testm4.properties");
        removeFile (filename);

        String testdirName = getTestDirectoryName();
        
        int numProfilesGenerated = 8;
        
        SampleContest sample = new SampleContest();
        
        IInternalContest contest = sample.createContest(3, 3, 3, 3, true);
        contest.setStorage(new FileStorage(testdirName));
        Profile [] addedProfiles = sample.createProfiles(contest, numProfilesGenerated-1);
        for (Profile profile3 : addedProfiles){
            contest.updateProfile(profile3);
        }
        
        ProfileManager manager1 = new ProfileManager(filename);
        manager1.store(contest.getProfiles(), contest.getProfile());
        
        String name = "DE FOOT PROFILE";
        contest.getProfile().setName(name);
        contest.getProfile().setDescription(name + " Describet!");
        
        ProfileManager manager2 = new ProfileManager(filename);
        manager2.mergeProfiles(contest);
        
        assertEquals("Profile name should be ", name, contest.getProfile().getName());
        
        
    }
    
    public void testSave() throws IOException, ProfileLoadException {
        
        String filename = getFullName( "testm.properties");
        String filename2 = getFullName( "testM2.properties");

        removeFile (filename);
        removeFile (filename2);

        String testdirName = getTestDirectoryName();
        
        int numProfilesGenerated = 8;
        
        SampleContest sample = new SampleContest();
        
        IInternalContest contest = sample.createContest(3, 3, 3, 3, true);
        contest.setStorage(new FileStorage(testdirName));
        Profile [] addedProfiles = sample.createProfiles(contest, numProfilesGenerated-1);
        for (Profile profile3 : addedProfiles){
            contest.updateProfile(profile3);
        }
        
        ProfileManager manager1 = new ProfileManager(filename);

        Profile[] profiles1 = contest.getProfiles();
        assertEquals("Should have "+numProfilesGenerated+" profiles ", numProfilesGenerated, profiles1.length);
        
        // Set profile #2 as inactive
        profiles1[1].setActive(false);
        profiles1[2].setActive(false);
        
        manager1.store(profiles1, profiles1[0]);
        
        manager1 = null;
        manager1 = new ProfileManager(filename);
        
        Profile [] profiles = manager1.load();
        
        assertEquals("Should be "+numProfilesGenerated, profiles1.length, profiles.length);
        
        if (debugFlag){
            dumpProfiles("Contest profiles", profiles1);
            dumpProfiles("Loaded profiles", profiles);
        }
        
        for (Profile profile : profiles){
            Profile [] matches = findMatches(profile, profiles1);
            if (matches.length != 1){
                fail ("Could not find profile in list "+profile.getName());
            }
        }
    }
    
    private Profile[] findMatches(Profile inProfile, Profile[] profiles) {

        int matches = 0;
        Profile aMatch = null;

        for (Profile profile : profiles) {
            if (profile.isSameAs(inProfile)) {
                matches++;
                aMatch = profile;
            }
        }

        if (matches == 0) {
            return new Profile[0];
        }

        Profile[] foundProfiles = new Profile[matches];
        if (matches == 1) {
            foundProfiles[0] = aMatch;
        } else {
            int idx = 0;
            for (Profile profile : profiles) {
                if (profile.isSameAs(inProfile)) {
                    idx++;
                    foundProfiles[idx] = profile;
                }
            }
        }

        return foundProfiles;
    }

    public void testMerge() throws Exception {
        
        String filename = getFullName( "testm.properties");
        String filename2 = getFullName( "testM2.properties");

        removeFile (filename);
        removeFile (filename2);
     
        ProfileManager manager1 = new ProfileManager(filename);

        Profile[] profiles1 = createProfiles("PMTtm", "foo", 4);

        Profile[] profiles2 = createProfiles("PMTtm", "foo", 3);
        
        manager1.store(profiles1, profiles1[0]);

        IInternalContest contest1 = createConstest(profiles2, profiles2[0]);
        
        /**
         * Merge profiles2 into contest.
         */
        manager1.mergeProfiles(contest1);
        
        assertEquals(profiles1.length + profiles2.length, contest1.getProfiles().length);

        ProfileManager manager2 = new ProfileManager(filename2);
        manager2.store(profiles2, profiles2[0]);

        IInternalContest contest2 = createConstest(profiles2, profiles2[0]);

        /**
         * Merget profiles2 into contest2
         */
        manager2.mergeProfiles(contest2);

        assertEquals(profiles2.length, contest2.getProfiles().length);

//        dumpProfiles(profiles1, "1"); System.out.println();
//        dumpProfiles(contest2.getProfiles(),"contest try 1 ");
        
        /**
         * Merge profiles1 into contest2
         */
        manager1.mergeProfiles(contest2);
        
//        dumpProfiles(profiles2, "2");
//        dumpProfiles(contest2.getProfiles(),"contest try 2 ");
        
        assertEquals(profiles1.length + profiles2.length, contest2.getProfiles().length);
        
    }
    
//    private void dumpProfiles(Profile[] profiles, String string) {
//        
//        Arrays.sort(profiles, new ProfileComparatorByName());
//        for (Profile p : profiles){
//            System.out.println("  "+string+" "+p.getProfilePath()+" "+p.getName());
//        }
//        
//    }

    private void removeFile(String filename) {
        if (new File(filename).isFile()){
            new File(filename).delete();
        }
    }

    /**
     * Print default profile properties. 
     * 
     * @param args
     */
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
