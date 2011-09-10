package edu.csus.ecs.pc2.core.execute;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Executable class tests.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class AllTests {

    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.core.execute");
        //$JUnit-BEGIN$
        suite.addTestSuite(ExecutableTest.class);
        //$JUnit-END$
        return suite;
    }
}
