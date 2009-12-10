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
        Profile profile = new Profile(title);
        profile.setDescription(description);
        profile.setProfilePath("/tmp/foo2");

        ProfileManager manager = new ProfileManager();

        String filename = "test" + ProfileManager.PROFILE_INDEX_FILENAME;

        Profile[] profiles = { profile };

        manager.store(filename, profiles, profile);

        Profile profile2 = manager.defaultProfile(filename);

        assertEquals(profile.getName(), profile2.getName());

        assertTrue("Failed defaultProfile", profile.isSameAs(profile2));
    }

}
