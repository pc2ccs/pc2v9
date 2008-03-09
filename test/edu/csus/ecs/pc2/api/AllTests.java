package edu.csus.ecs.pc2.api;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test Suite for api package.
 *  
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class AllTests {
    
    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.api");
        // $JUnit-BEGIN$
        suite.addTestSuite(ClientImplementationTest.class);
        // $JUnit-END$
        return suite;
    }

}
