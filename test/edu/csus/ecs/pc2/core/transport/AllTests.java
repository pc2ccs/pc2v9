package edu.csus.ecs.pc2.core.transport;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * All tests for transport package
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public final class AllTests {

    private AllTests() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for edu.csus.ecs.pc2.core.transport");
        //$JUnit-BEGIN$
        suite.addTestSuite(TransportManagerTest1.class);
        //$JUnit-END$
        return suite;
    }

}
