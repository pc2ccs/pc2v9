package edu.csus.ecs.pc2.profile;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.Profile;

/**
 * Tests for ProfileManager.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfileManagerTest extends TestCase {

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

        for (Profile profile : list) {
            System.out.println("Profile " + profile.getName());
        }

    }

}
