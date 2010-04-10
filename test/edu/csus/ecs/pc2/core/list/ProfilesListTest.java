package edu.csus.ecs.pc2.core.list;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.InternalContest;
import edu.csus.ecs.pc2.core.model.Profile;
import edu.csus.ecs.pc2.profile.ProfileManager;

/**
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ProfilesListTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGet() throws Exception {

        Profile profile1 = ProfileManager.createNewProfile();
        Profile profile2 = ProfileManager.createNewProfile();
        Profile profile3 = ProfileManager.createNewProfile();
        Profile profile4 = ProfileManager.createNewProfile();
        Profile profile5 = ProfileManager.createNewProfile();

        Profile[] list1 = { profile1, profile2, profile3, profile4 };

        ProfilesList list = new ProfilesList();

        for (Profile profile : list1) {
            list.add(profile);
        }

        for (Profile profile : list1) {
            assertNotNull(list.get(profile));
        }

        assertNull(list.get(profile5));
        
        list.delete(profile1);
        assertNull(list.get(profile1));
        
        list.delete(profile2);
        assertNull(list.get(profile2));

    }
    
    public void testContestGet() throws Exception {
        
        IInternalContest contest = new InternalContest();
        
        Profile profile1 = ProfileManager.createNewProfile();
        Profile profile2 = ProfileManager.createNewProfile();
        Profile profile3 = ProfileManager.createNewProfile();
        Profile profile4 = ProfileManager.createNewProfile();
        Profile profile5 = ProfileManager.createNewProfile();

        Profile[] list1 = { profile1, profile2, profile3, profile4 };
        
        for (Profile profile : list1) {
            contest.addProfile(profile);
        }
        
        for (Profile profile : list1) {
            assertNotNull(contest.getProfile(profile.getElementId()));
        }

        
        
    }
}
