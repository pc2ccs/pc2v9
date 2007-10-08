package edu.csus.ecs.pc2.core.security;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * All tests for security package
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class AllTests {

    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.core.security");
        //$JUnit-BEGIN$
        suite.addTestSuite(PermissionTest.class);
        suite.addTestSuite(PermissionListTest.class);
        //$JUnit-END$
        return suite;
    }

}
