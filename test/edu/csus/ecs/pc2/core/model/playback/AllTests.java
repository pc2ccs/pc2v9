package edu.csus.ecs.pc2.core.model.playback;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * All JUnit tests for playback package.
 * 
 * @author pc2@ecs.csus.edu
 * $version $Id$
 */

// $HeadURL$
public final class AllTests {

    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.core.model");
        //$JUnit-BEGIN$
        suite.addTestSuite(PlaybackManagerTest.class);
        //$JUnit-END$
        return suite;
    }

}
