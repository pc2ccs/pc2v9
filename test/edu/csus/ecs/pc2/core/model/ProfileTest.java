package edu.csus.ecs.pc2.core.model;

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
