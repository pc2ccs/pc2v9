package edu.csus.ecs.pc2.core.model;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @author pc2@ecs.csus.edu
 * 
 */

// $HeadURL$
public final class AllTests {

    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.core.model");
        // $JUnit-BEGIN$
        suite.addTestSuite(SiteTest.class);
        // $JUnit-END$
        return suite;
    }

}
